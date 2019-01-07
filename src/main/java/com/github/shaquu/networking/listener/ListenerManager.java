package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.packets.*;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private List<Listener> listeners = new ArrayList<>();

    protected void registerListener(Listener listener) {
        listeners.add(listener);
    }

    protected void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(TCPServer tcpServer, TCPCLient tcpcLient, byte[] bytes) throws Exception {
        Packet packet = (Packet) Packet.fromBytes(bytes);
        tcpServer.getLogger().debug("Received packet " + packet.getClass().getTypeName() + " " + packet.toString());

        for (Listener listener : listeners) {
            if (listener instanceof RequestFileListPacketListener) {
                if (packet instanceof RequestFileListPacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else if (listener instanceof FileListPacketListener) {
                if (packet instanceof FileListPacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else if (listener instanceof PushFilePacketListener) {
                if (packet instanceof PushFilePacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else if (listener instanceof PullFilePacketListener) {
                if (packet instanceof PullFilePacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else if (listener instanceof LogOnPacketListener) {
                if (packet instanceof LogOnPacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else {
                tcpServer.getLogger().debug("Listener type not supported in notifyListeners");
            }
        }
    }

    protected void notifyListeners(UDPClientServer udpClientServer, IpPort ipPort, byte[] bytes) throws Exception {
        Packet packet = (Packet) Packet.fromBytes(bytes);
        udpClientServer.getLogger().debug("Received packet " + packet.getClass().getTypeName() + " " + packet.toString());

        for (Listener listener : listeners) {
            if (listener instanceof RequestFileListPacketListener) {
                if (packet instanceof RequestFileListPacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else if (listener instanceof FileListPacketListener) {
                if (packet instanceof FileListPacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else if (listener instanceof PushFilePacketListener) {
                if (packet instanceof PushFilePacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else if (listener instanceof PullFilePacketListener) {
                if (packet instanceof PullFilePacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else {
                udpClientServer.getLogger().debug("Listener type not supported in notifyListeners");
            }
        }
    }
}
