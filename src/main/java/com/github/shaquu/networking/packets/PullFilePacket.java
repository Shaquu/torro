package com.github.shaquu.networking.packets;

public class PullFilePacket extends Packet {
    public PullFilePacket(long id, int part, int maxPart, Byte[] data) {
        super(id, part, maxPart, data);
    }
}
