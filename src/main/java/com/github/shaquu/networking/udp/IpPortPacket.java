package com.github.shaquu.networking.udp;

import com.github.shaquu.networking.packets.Packet;

public class IpPortPacket {

    private IpPort ipPort;
    private Packet packet;

    IpPortPacket(IpPort ipPort, Packet packet) {
        this.ipPort = ipPort;
        this.packet = packet;
    }

    IpPort getIpPort() {
        return ipPort;
    }

    public Packet getPacket() {
        return packet;
    }
}
