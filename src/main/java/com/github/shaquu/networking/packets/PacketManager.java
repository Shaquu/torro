package com.github.shaquu.networking.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PacketManager {

    private HashMap<Long, Integer> packetParts = new HashMap<>();
    private HashMap<Long, Packet[]> receivedParts = new HashMap<>();

    public boolean add(Packet packet) {
        int maxPart = packet.getMaxPart();
        long id = packet.getId();

        if (!packetParts.containsKey(id)) {
            packetParts.put(id, maxPart);
        }

        if (!receivedParts.containsKey(id)) {
            receivedParts.put(id, new Packet[maxPart]);
        }

        receivedParts.get(id)[packet.getPart() - 1] = packet;


        for (Packet p : receivedParts.get(id)) {
            if (p == null)
                return false;
        }

        return true;
    }

    public Packet getPacket(Class clazz, long id) {
        if (receivedParts.containsKey(id)) {

            List<Byte> byteList = new ArrayList<>();

            for (Packet partPacket : receivedParts.get(id)) {
                byteList.addAll(Arrays.asList(partPacket.getData()));
            }

            Byte[] data = byteList.toArray(new Byte[0]);

            if (clazz == RequestFileListPacket.class) {
                return new RequestFileListPacket(id);
            } else if (clazz == FileListPacket.class) {
                return new FileListPacket(id, 1, 1, data);
            } else if (clazz == PullFilePacket.class) {
                return new PullFilePacket(id, 1, 1, data);
            } else if (clazz == PushFilePacket.class) {
                return new PushFilePacket(id, 1, 1, data);
            } else {
                return new Packet(id, -1, -1, data);
            }
        } else return null;
    }

}
