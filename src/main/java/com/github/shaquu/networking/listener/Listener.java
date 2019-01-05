package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;

public interface Listener {
    void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception;
    void call(NetworkNode networkNode, Packet packet) throws Exception;
}
