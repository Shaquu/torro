package com.github.shaquu;

import com.github.shaquu.logger.Logger;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main {

    private static int[] ports = new int[]{10004, 10005};

    public static void main(String[] args) throws Exception {

        Logger.DEBUG = true;

        int myPort;
        int hisPort;

        if (available(ports[0])) {
            myPort = ports[0];
            hisPort = ports[1];
        } else {
            myPort = ports[1];
            hisPort = ports[0];
        }

        UDPClientServer udpClientServer;

        try {
            udpClientServer = new UDPClientServer(myPort, "" + myPort);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        udpClientServer.addClient(new IpPort(InetAddress.getByName("localhost"), hisPort));

        udpClientServer.start();

        System.exit(0);
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
