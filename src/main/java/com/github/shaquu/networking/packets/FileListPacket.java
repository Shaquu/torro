package com.github.shaquu.networking.packets;

public class FileListPacket extends Packet {
    public FileListPacket(long id, int part, int maxPart, Byte[] data) {
        super(id, part, maxPart, data);
    }
}
