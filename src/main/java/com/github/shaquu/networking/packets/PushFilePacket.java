package com.github.shaquu.networking.packets;

public class PushFilePacket extends Packet {
    public PushFilePacket(long id, int part, int maxPart, Byte[] data) {
        super(id, part, maxPart, data);
    }
}
