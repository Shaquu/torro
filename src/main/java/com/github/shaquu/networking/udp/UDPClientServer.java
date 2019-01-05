package com.github.shaquu.networking.udp;

import com.github.shaquu.logger.Logger;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.listener.*;
import com.github.shaquu.networking.packets.FileListPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.RequestFileListPacket;
import com.github.shaquu.utils.ArrayChunker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class UDPClientServer extends NetworkNode {

    private final static int MESSAGE_SIZE = 2048;
    private final static int WAIT_TIME = 1000;

    private final DatagramSocket serverSocket;

    private final HashMap<IpPort, Packet> packetQueue = new HashMap<>();

    public UDPClientServer(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);

        registerListener(new RequestFileListPacketListener());
        registerListener(new FileListPacketListener());
    }

    @Override
    protected void receiver() throws Exception {
        byte[] receiveData = new byte[MESSAGE_SIZE];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        serverSocket.receive(receivePacket);

        byte[] bytes = receivePacket.getData();
        IpPort ipPort = new IpPort(receivePacket.getAddress(), receivePacket.getPort());
        logger.log("Received packet");

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

                logger.log(new Date() + "|Send bytes length: " + sendData.length);
            }
        }
        Thread.sleep(WAIT_TIME);
    }

    @Override
    public void addPacketToQueue(IpPort ipPort, Packet packet) throws Exception {
        int packetSize = packet.getPacketSize();

        if (packetSize > UDPClientServer.MESSAGE_SIZE) {
            Byte[][] chunked = ArrayChunker.forBytes(packet.getData(), UDPClientServer.MESSAGE_SIZE - Packet.BASE_SIZE);
            int part = 1;

            Packet chunkPacket;

            for (Byte[] data : chunked) {
                chunkPacket = createPacketChunk(packet, part++, data);
                packetQueue.put(ipPort, chunkPacket);
                logger.log("Added packet to queue " + chunkPacket.getClass().getTypeName() + " " + chunkPacket.toString() + " " + chunkPacket.getPacketSize());
            }
        } else {
            packetQueue.put(ipPort, packet);
            logger.log("Added packet to queue " + packet.getClass().getTypeName() + " " + packet.toString() + " " + packetSize);
        }
    }

    private Packet createPacketChunk(Packet packet, int part, Byte[] data) {
        if (packet instanceof RequestFileListPacket) {
            return new RequestFileListPacket(packet.getId());
        } else if (packet instanceof FileListPacket) {
            return new FileListPacket(packet.getId(), part, packet.getMaxPart(), data);
        }
        return null;
    }

    @Override
    public void addPacketToQueue(Packet packet) {}
}
