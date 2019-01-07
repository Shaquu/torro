package com.github.shaquu.networking.packets;

public class LogOnPacket extends Packet {
    public LogOnPacket(long id) {
        super(id, 1, 1, new Byte[]{});
    }
}
