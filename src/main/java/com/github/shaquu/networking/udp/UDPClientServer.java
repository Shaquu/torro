package com.github.shaquu.networking.udp;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.IpPortPacket;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.utils.ArrayChunker;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class UDPClientServer extends NetworkNode {

    private final static int MESSAGE_SIZE = 1024;
    private final static int WAIT_TIME = 1000;

    private final DatagramSocket serverSocket;

    private HashMap<IpPort, List<TorroFile>> clientFileMap = new HashMap<>();

    public UDPClientServer(int port, String folderPath) throws SocketException {
        super(port, folderPath);
        serverSocket = new DatagramSocket(this.port);
    }

    @Override
    protected void receiver() throws Exception {
        byte[] receiveData = new byte[MESSAGE_SIZE * 10];

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
            Iterator<IpPortPacket> iterator = packetQueue.iterator();

            while (iterator.hasNext()) {
                IpPortPacket ipPortPacket = iterator.next();
                IpPort ipPort = ipPortPacket.getIpPort();
                Packet packet = ipPortPacket.getPacket();

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
            Byte[][] chunked = ArrayChunker.forBytes(packet.getData(), UDPClientServer.MESSAGE_SIZE - Packet.BASE_SIZE);
            int part = 1;

            Packet chunkPacket;

            for (Byte[] data : chunked) {
                chunkPacket = createPacketChunk(packet, part++, chunked.length, data);
                packetQueue.add(new IpPortPacket(ipPort, chunkPacket));
                logger.debug("Added packet to queue " + Objects.requireNonNull(chunkPacket).getClass().getTypeName() + " " + chunkPacket.toString() + " " + chunkPacket.getPacketSize());
            }
        } else {
            packetQueue.add(new IpPortPacket(ipPort, packet));
            logger.debug("Added packet to queue " + packet.getClass().getTypeName() + " " + packet.toString() + " " + packetSize);
        }
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

    public void addClienFileList(IpPort ipPort, List<TorroFile> fileList) {
        clientFileMap.put(ipPort, fileList);
    }

    public HashMap<IpPort, List<TorroFile>> getClientFileMap() {
        return clientFileMap;
    }
}
