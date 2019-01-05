package com.github.shaquu.networking.udp;

import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.listener.FileListPacketListener;
import com.github.shaquu.networking.listener.PushFilePacketListener;
import com.github.shaquu.networking.listener.RequestFileListPacketListener;
import com.github.shaquu.networking.packets.FileListPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PushFilePacket;
import com.github.shaquu.networking.packets.RequestFileListPacket;
import com.github.shaquu.utils.ArrayChunker;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class UDPClientServer extends NetworkNode {

    private final static int MESSAGE_SIZE = 9192;
    private final static int WAIT_TIME = 1000;

    private final DatagramSocket serverSocket;

    private final HashMap<IpPort, Packet> packetQueue = new HashMap<>();
    private List<IpPort> ipPortList = new ArrayList<>();

    public UDPClientServer(int port, String folderPath) throws SocketException {
        super(port, folderPath);
        serverSocket = new DatagramSocket(this.port);

        registerListener(new RequestFileListPacketListener());
        registerListener(new FileListPacketListener());
        registerListener(new PushFilePacketListener());
    }

    @Override
    protected void receiver() throws Exception {
        byte[] receiveData = new byte[MESSAGE_SIZE];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        serverSocket.receive(receivePacket);

        byte[] bytes = receivePacket.getData();
        IpPort ipPort = new IpPort(receivePacket.getAddress(), receivePacket.getPort());
        logger.debug("Received packet");

        super.notifyListeners(this, ipPort, bytes);
    }

    @Override
    protected void sender() throws Exception {
        if (packetQueue.size() > 0) {
            Iterator<IpPort> iterator = packetQueue.keySet().iterator();

            while (iterator.hasNext()) {
                IpPort ipPort = iterator.next();
                Packet packet = packetQueue.get(ipPort);

                iterator.remove();

                byte[] sendData = Packet.toBytes(packet);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipPort.getIp(), ipPort.getPort());

                serverSocket.send(sendPacket);

                logger.debug(new Date() + "|Send bytes length: " + sendData.length);
            }
        }
        Thread.sleep(WAIT_TIME);
    }

    @Override
    public void addPacketToQueue(IpPort ipPort, Packet packet) throws Exception {
        int packetSize = packet.getPacketSize();

        if (packetSize > UDPClientServer.MESSAGE_SIZE) {
            //TODO chunker not working. Sending big files dont chunk :(
            Byte[][] chunked = ArrayChunker.forBytes(packet.getData(), UDPClientServer.MESSAGE_SIZE - Packet.BASE_SIZE);
            int part = 1;

            Packet chunkPacket;

            for (Byte[] data : chunked) {
                chunkPacket = createPacketChunk(packet, part++, data);
                packetQueue.put(ipPort, chunkPacket);
                logger.debug("Added packet to queue " + Objects.requireNonNull(chunkPacket).getClass().getTypeName() + " " + chunkPacket.toString() + " " + chunkPacket.getPacketSize());
            }
        } else {
            packetQueue.put(ipPort, packet);
            logger.debug("Added packet to queue " + packet.getClass().getTypeName() + " " + packet.toString() + " " + packetSize);
        }
    }

    private Packet createPacketChunk(Packet packet, int part, Byte[] data) {
        if (packet instanceof RequestFileListPacket) {
            return new RequestFileListPacket(packet.getId());
        } else if (packet instanceof FileListPacket) {
            return new FileListPacket(packet.getId(), part, packet.getMaxPart(), data);
        } else if (packet instanceof PushFilePacket) {
            return new PushFilePacket(packet.getId(), part, packet.getMaxPart(), data);
        }
        return null;
    }

    public void addClient(IpPort ipPort) {
        ipPortList.add(ipPort);
    }

    @Override
    public void addPacketToQueue(Packet packet) throws Exception {
        for (IpPort ipPort : ipPortList) {
            addPacketToQueue(ipPort, packet);
        }
    }

    public List<IpPort> getIpPortList() {
        return ipPortList;
    }
}
