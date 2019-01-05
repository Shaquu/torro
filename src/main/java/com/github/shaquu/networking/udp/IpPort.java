package com.github.shaquu.networking.udp;

import java.net.InetAddress;

public class IpPort {
    private InetAddress ip;
    private int port;

    public IpPort(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
