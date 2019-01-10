package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.packets.*;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListenerManager {
    private List<Listener> listeners = new ArrayList<>();

    protected void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(TCPServer tcpServer, TCPCLient tcpcLient, byte[] bytes) throws Exception {
        Packet packet = (Packet) Packet.fromBytes(bytes);
        tcpServer.getLogger().debug("Received packet " + Objects.requireNonNull(packet).getClass().getTypeName() + " " + packet.toString());

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
            } else if (listener instanceof PullFilePartsPacketListener) {
                if (packet instanceof PullFilePartsPacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else if (listener instanceof PushFilePartsPacketListener) {
                if (packet instanceof PushFilePartsPacket) {
                    listener.call(tcpServer, tcpcLient, packet);
                }
            } else {
                tcpServer.getLogger().debug("TCP Listener type not supported in notifyListeners " + listener.getClass().getTypeName());
            }
        }
    }

    protected void notifyListeners(UDPClientServer udpClientServer, IpPort ipPort, byte[] bytes) throws Exception {
        Packet packet = (Packet) Packet.fromBytes(bytes);
        udpClientServer.getLogger().debug("Received packet " + Objects.requireNonNull(packet).getClass().getTypeName() + " " + packet.toString());

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
            } else if (listener instanceof PullFilePartsPacketListener) {
                if (packet instanceof PullFilePartsPacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else if (listener instanceof PushFilePartsPacketListener) {
                if (packet instanceof PushFilePartsPacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else if (listener instanceof LogOnPacketListener) {
                if (packet instanceof LogOnPacket) {
                    listener.call(udpClientServer, ipPort, packet);
                }
            } else {
                udpClientServer.getLogger().debug("UDP Listener type not supported in notifyListeners");
            }
        }
    }
}
