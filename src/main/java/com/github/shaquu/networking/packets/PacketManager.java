package com.github.shaquu.networking.packets;

import com.github.shaquu.file.TorroFile;

import java.util.*;

public class PacketManager {

    private HashMap<Long, Integer> packetParts = new HashMap<>();
    private HashMap<Long, Packet[]> receivedParts = new HashMap<>();


    private HashMap<TorroFile, Integer> packetFileParts = new HashMap<>();
    private HashMap<TorroFile, Packet[]> receiverFileParts = new HashMap<>();

    public HashMap<TorroFile, Packet[]> getReceiverFileParts() {
        return receiverFileParts;
    }

    public HashMap<Long, Packet[]> getReceivedParts() {
        return receivedParts;
    }

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

    public boolean add(PushFilePacket packet) {
        int maxPart = packet.getMaxPart();
        TorroFile torroFile = packet.getTorroFile();

        if (!packetFileParts.containsKey(torroFile)) {
            packetFileParts.put(torroFile, maxPart);
        }

        if (!receiverFileParts.containsKey(torroFile)) {
            receiverFileParts.put(torroFile, new Packet[maxPart]);
        }

        receiverFileParts.get(torroFile)[packet.getPart() - 1] = packet;


        for (Packet p : receiverFileParts.get(torroFile)) {
            if (p == null)
                return false;
        }

        return true;
    }

    public Packet getPacket(Class clazz, long id) {
        if (receivedParts.containsKey(id)) {

            List<Byte> byteList = new ArrayList<>();

            Arrays.stream(receivedParts.get(id)).forEach(e -> Collections.addAll(byteList, e.getData()));

            Byte[] data = byteList.toArray(new Byte[0]);

            if (clazz == RequestFileListPacket.class) {
                return new RequestFileListPacket(id);
            } else if (clazz == FileListPacket.class) {
                return new FileListPacket(id, 1, 1, data);
            } else if (clazz == PullFilePacket.class) {
                return new PullFilePacket(id, 1, 1, data, ((PullFilePacket) receivedParts.get(id)[0]).getTorroFile());
            } else if (clazz == PushFilePacket.class) {
                return new PushFilePacket(id, 1, 1, data, ((PushFilePacket) receivedParts.get(id)[0]).getTorroFile());
            } else if (clazz == LogOnPacket.class) {
                return new LogOnPacket(id);
            } else if (clazz == PullFilePartsPacket.class) {
                return new PullFilePartsPacket(id, 1, 1, data, ((PullFilePartsPacket) receivedParts.get(id)[0]).getParts());
            } else {
                return new Packet(id, -1, -1, data);
            }
        } else return null;
    }

    public PushFilePacket getPushFilePacket(TorroFile torroFile) {
        if (receiverFileParts.containsKey(torroFile)) {

            List<Byte> byteList = new ArrayList<>();

            Arrays.stream(receiverFileParts.get(torroFile)).forEach(e -> Collections.addAll(byteList, e.getData()));

            Byte[] data = byteList.toArray(new Byte[0]);

            return new PushFilePacket(receiverFileParts.get(torroFile)[0].getId(), 1, 1, data, torroFile);
        } else return null;
    }

}
