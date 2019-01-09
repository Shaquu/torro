package com.github.shaquu.networking.tcp;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.IpPortPacket;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.LogOnPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PushFilePartsPacket;
import com.github.shaquu.utils.ArrayChunker;
import com.github.shaquu.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class TCPServer extends NetworkNode {

    public final static int WAIT_TIME = 1000;
    final static int MESSAGE_SIZE = 2048;
    private ServerSocket server;

    private HashMap<Long, TCPCLient> clientList = new HashMap<>();
    private HashMap<IpPort, TCPCLient> serverList = new HashMap<>();

    private HashMap<Long, List<TorroFile>> clientFileMap = new HashMap<>();

    public TCPServer(int port, String folderPath) throws IOException {
        super(port, folderPath);

        this.server = new ServerSocket(port, 1, InetAddress.getLocalHost());
    }

    public HashMap<Long, TCPCLient> getClientList() {
        return clientList;
    }

    public void addClienFileList(long id, List<TorroFile> fileList) {
        clientFileMap.put(id, fileList);
    }

    public HashMap<Long, List<TorroFile>> getClientFileMap() {
        return clientFileMap;
    }

    void disconnect(long id, InetAddress inetAddress, int port) throws IOException {
        clientList.remove(id);

        IpPort ipPort = new IpPort(inetAddress, port);

        if (serverList.containsKey(ipPort)) {
            if (serverList.get(ipPort) != null) {
                TCPCLient tcpcLient = serverList.get(ipPort);
                tcpcLient.close();
            }
            serverList.remove(ipPort);
        }
    }

    public boolean connect(int port) throws IOException {
        IpPort ipPort;
        try {
            ipPort = new IpPort(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            //System.out.println(e.getLocalizedMessage());
            return false;
        }

        Socket server;
        try {
            server = new Socket(InetAddress.getLocalHost(), port);
        } catch (IOException e) {
            //System.out.println(e.getLocalizedMessage());
            return false;
        }

        if (server.isConnected()) {
            System.out.println("Connecting to " + port);
            TCPCLient tcpcLient;
            try {
                tcpcLient = new TCPCLient(-1, server, this);
            } catch (IOException e) {
                //System.out.println(e.getLocalizedMessage());
                server.close();
                return false;
            }
            tcpcLient.start();

            serverList.put(ipPort, tcpcLient);
            addClient(ipPort);

            return true;
        } else {
            server.close();
            return false;
        }
    }

    @Override
    protected void receiver() throws Exception {
        Socket client = server.accept();
        System.out.println("Client connected " + client);

        long id = System.currentTimeMillis();
        TCPCLient tcpcLient = new TCPCLient(id, client, this);
        tcpcLient.start();

        clientList.put(id, tcpcLient);

        Packet logOn = new LogOnPacket(id);

        tcpcLient.addPacketToQueue(logOn);
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

                TCPCLient tcpcLient = serverList.get(ipPort);

                if (tcpcLient != null) {
                    tcpcLient.addPacketToQueue(packet);
                    logger.debug(new Date() + "|Send bytes length: " + sendData.length);
                }

            }
        }
        Thread.sleep(WAIT_TIME);
    }

    @Override
    public void addPacketToQueue(IpPort ipPort, Packet packet) throws Exception {
        int packetSize = packet.getPacketSize();

        if (packetSize > TCPServer.MESSAGE_SIZE) {
            Byte[][] chunked = ArrayChunker.forBytes(packet.getData(), TCPServer.MESSAGE_SIZE - Packet.BASE_SIZE);

            boolean random = Utils.getRandomBoolean();

            if (random) {
                for (int part = 1; part - 1 < chunked.length; part++) {
                    directionHandler(ipPort, packet, chunked, part);
                }
            } else {
                for (int part = chunked.length; part + 1 > chunked.length; part--) {
                    directionHandler(ipPort, packet, chunked, part);
                }
            }

        } else {
            packetQueue.add(new IpPortPacket(ipPort, packet));
            logger.debug("Added packet to queue " + packet.getClass().getTypeName() + " " + packet.toString() + " " + packetSize);
        }
    }

    private void directionHandler(IpPort ipPort, Packet packet, Byte[][] chunked, int part) throws IOException {
        Packet chunkPacket;
        if (packet instanceof PushFilePartsPacket) {
            if (!((PushFilePartsPacket) packet).getParts().contains(part)) {
                return;
            }
        }

        chunkPacket = createPacketChunk(packet, part, chunked.length, chunked[part - 1]);

        packetQueue.add(new IpPortPacket(ipPort, chunkPacket));
        logger.debug("Added packet to queue " + Objects.requireNonNull(chunkPacket).getClass().getTypeName() + " " + chunkPacket.toString() + " " + chunkPacket.getPacketSize());
    }

    @Override
    public void addPacketToQueue(Packet packet) throws Exception {
        for (IpPort ipPort : ipPortList) {
            addPacketToQueue(ipPort, packet);
        }
    }
}
