package com.github.shaquu;

import com.github.shaquu.logger.Logger;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    //for udp with debug
    //java -jar target/torro-1.1-SNAPSHOT.jar true true 10001 10001 10002 10003

    public static void main(String[] args) throws Exception {

        Logger.DEBUG = Boolean.parseBoolean(args[0]);

        boolean udp = Boolean.parseBoolean(args[1]);

        int myPort = Integer.parseInt(args[2]);

        if (!available(myPort)) {
            System.out.println("Port not available");
            System.exit(0);
        }

        String folder = args[3];
        List<Integer> theirPorts = new ArrayList<>();

        for (int i = 4; i < args.length; i++) {
            theirPorts.add(Integer.parseInt(args[i]));
        }

        try {
            if (udp) {
                UDPClientServer udpClientServer = new UDPClientServer(myPort, folder);

                for (int port : theirPorts) {
                    udpClientServer.addClient(new IpPort(InetAddress.getByName("localhost"), port));
                }

                udpClientServer.start();
                udpClientServer.stop();
            } else {
                TCPServer tcpServer = new TCPServer(myPort, folder);

                new Thread(() -> {
                    List<Integer> toJoin = new ArrayList<>(theirPorts);

                    Iterator<Integer> iterator = toJoin.iterator();

                    while (true) {
                        boolean connected = false;

                        int port = iterator.next();

                        try {
                            connected = tcpServer.connect(port);
                        } catch (IOException e) {
                            //System.out.println("Cannot connect " + e.getLocalizedMessage());
                        }

                        if (connected) {
                            iterator.remove();
                        }

                        try {
                            Thread.sleep(TCPServer.WAIT_TIME * 50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (toJoin.size() == 0 || !iterator.hasNext()) {
                            toJoin = new ArrayList<>(theirPorts);
                            iterator = toJoin.iterator();
                        }
                    }
                }).start();

                tcpServer.start();
                tcpServer.stop();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

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
