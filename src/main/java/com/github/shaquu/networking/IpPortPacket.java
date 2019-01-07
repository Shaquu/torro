package com.github.shaquu.networking;

import com.github.shaquu.networking.packets.Packet;

public class IpPortPacket {

    private IpPort ipPort;
    private Packet packet;

    public IpPortPacket(IpPort ipPort, Packet packet) {
        this.ipPort = ipPort;
        this.packet = packet;
    }

    public IpPort getIpPort() {
        return ipPort;
    }

    public Packet getPacket() {
        return packet;
    }
}
