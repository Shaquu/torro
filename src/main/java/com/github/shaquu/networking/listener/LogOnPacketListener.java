package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;

public class LogOnPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) {
        handler(udpClientServer, packet);
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) {
        handler(tcpServer, packet);
    }

    private void handler(NetworkNode networkNode, Packet packet) {
        networkNode.getPacketManager().add(packet);
    }
}
