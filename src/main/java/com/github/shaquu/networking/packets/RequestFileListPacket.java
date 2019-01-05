package com.github.shaquu.networking.packets;

public class RequestFileListPacket extends Packet {
    public RequestFileListPacket(long id) {
        super(id, 1, 1, new Byte[]{});
    }
}
