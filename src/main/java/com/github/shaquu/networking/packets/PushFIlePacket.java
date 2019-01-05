package com.github.shaquu.networking.packets;

public class PushFIlePacket extends Packet {
    public PushFIlePacket(long id, int part, int maxPart, Byte[] data) {
        super(id, part, maxPart, data);
    }
}
