package com.github.shaquu.networking.listener;

import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.*;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private List<Listener> listeners = new ArrayList<>();

    protected void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(NetworkNode networkNode, byte[] bytes) throws Exception {
        Packet packet = (Packet) Packet.fromBytes(bytes);
        networkNode.getLogger().debug("Received packet " + packet.getClass().getTypeName() + " " + packet.toString());

        for (Listener listener : listeners) {
            if (listener instanceof RequestFileListPacketListener) {
                if (packet instanceof RequestFileListPacket) {
                    listener.call(networkNode, packet);
                }
            } else if (listener instanceof FileListPacketListener) {
                if (packet instanceof FileListPacket) {
                    listener.call(networkNode, packet);
                }
            } else if (listener instanceof PushFilePacketListener) {
                if (packet instanceof PushFilePacket) {
                    listener.call(networkNode, packet);
                }
            } else if (listener instanceof PullFilePacketListener) {
                if (packet instanceof PullFilePacket) {
                    listener.call(networkNode, packet);
                }
            } else {
                networkNode.getLogger().debug("Listener type not supported in notifyListeners");
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
