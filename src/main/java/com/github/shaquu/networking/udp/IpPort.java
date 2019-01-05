package com.github.shaquu.networking.udp;

import java.net.InetAddress;

public class IpPort {
    private InetAddress ip;
    private int port;

    public IpPort(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    InetAddress getIp() {
        return ip;
    }

    int getPort() {
        return port;
    }


    @Override
    public String toString() {
        return "IpPort{" +
                "ip=" + ip.getHostName() +
                ", port=" + port +
                '}';
    }
}
