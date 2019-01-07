package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;

public interface Listener {
    void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception;

    void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception;
}
