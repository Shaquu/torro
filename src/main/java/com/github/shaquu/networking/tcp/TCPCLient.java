package com.github.shaquu.networking.tcp;

import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.utils.ArrayChunker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TCPCLient extends NetworkNode {
    private final ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    private Socket socket;
    private TCPServer tcpServer;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private long id;

    public TCPCLient(long id, Socket clientSocket, TCPServer tcpServer) throws IOException {
        super();

        this.id = id;

        this.socket = clientSocket;
        this.tcpServer = tcpServer;

        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    void close() throws IOException {
        //tcpServer.getLogger().debug("Closing connection with " + socket.getInetAddress().getHostName() + " " + socket.getPort());
        inputStream.close();
        outputStream.close();
        socket.close();
        stop();
    }

    @Override
    protected void receiver() throws Exception {
        byte[] receiveData = new byte[TCPServer.MESSAGE_SIZE * 10];

        int read = inputStream.read(receiveData);

        if (read == -1) {
            tcpServer.disconnect(id, socket.getInetAddress(), socket.getPort());
            close();
            Thread.interrupted();
            return;
        }

        tcpServer.getLogger().debug("Received packet from " + socket.getInetAddress().getHostName() + " " + socket.getPort());
        tcpServer.notifyListeners(tcpServer, this, receiveData);
    }

    @Override
    protected void sender() throws Exception {
        if (packetQueue.size() > 0) {
            Iterator<Packet> iterator = packetQueue.iterator();

            while (iterator.hasNext()) {
                Packet packet = iterator.next();

                iterator.remove();

                byte[] sendData = Packet.toBytes(packet);

                outputStream.write(sendData);
                outputStream.flush();

                logger.debug("Send bytes length: " + sendData.length);
                Thread.sleep(TCPServer.WAIT_TIME);
            }
        }
    }

    @Override
    public void addPacketToQueue(IpPort ipPort, Packet packet) {
    }

    @Override
    public String toString() {
        return "TCPCLient{" +
                "socket=" + socket +
                ", id=" + id +
                '}';
    }

    @Override
    public void addPacketToQueue(Packet packet) throws Exception {
        int packetSize = packet.getPacketSize();

        if (packetSize > TCPServer.MESSAGE_SIZE) {
            Byte[][] chunked = ArrayChunker.forBytes(packet.getData(), TCPServer.MESSAGE_SIZE - Packet.BASE_SIZE);
            int part = 1;

            Packet chunkPacket;

            for (Byte[] data : chunked) {
                chunkPacket = createPacketChunk(packet, part++, chunked.length, data);
                packetQueue.add(chunkPacket);
                logger.debug("Added packet to queue " + Objects.requireNonNull(chunkPacket).getClass().getTypeName() + " " + chunkPacket.toString() + " " + chunkPacket.getPacketSize());
            }
        } else {
            packetQueue.add(packet);
            logger.debug("Added packet to queue " + packet.getClass().getTypeName() + " " + packet.toString() + " " + packetSize);
        }
    }

    public long getId() {
        return id;
    }
}
