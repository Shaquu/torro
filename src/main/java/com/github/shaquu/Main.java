package com.github.shaquu;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.RequestFileListPacket;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main {

    private static int[] ports = new int[]{10004, 10005};

    public static void main(String[] args) throws Exception {

        int myPort;
        int hisPort;

        if (available(ports[0])) {
            myPort = ports[0];
            hisPort = ports[1];
        } else {
            myPort = ports[1];
            hisPort = ports[0];
        }

        System.out.println(myPort);
        System.out.println(hisPort);

        IpPort[] ipPorts = new IpPort[]{new IpPort(InetAddress.getByName("localhost"), hisPort)};

        UDPClientServer udpClientServer;

        try {
            udpClientServer = new UDPClientServer(myPort);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        udpClientServer.start();

        if (myPort == ports[0]) {
            Packet packet = new RequestFileListPacket(System.currentTimeMillis());

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            udpClientServer.addPacketToQueue(ipPorts[0], packet);
        } else {
            udpClientServer.getFileManager().add(new TorroFile("dupa", 1, "asdkljhui1j2"));
        }
    }

    private static boolean available(int port) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.close();
        } catch (SocketException e) {
            return false;
        }

        return true;
    }

}
