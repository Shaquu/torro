package com.github.shaquu.networking.udp;

import java.net.InetAddress;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpPort)) return false;
        IpPort ipPort = (IpPort) o;
        return getPort() == ipPort.getPort() &&
                getIp().equals(ipPort.getIp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIp(), getPort());
    }

    @Override
    public String toString() {
        return "IpPort{" +
                "ip=" + ip.getHostName() +
                ", port=" + port +
                '}';
    }
}
