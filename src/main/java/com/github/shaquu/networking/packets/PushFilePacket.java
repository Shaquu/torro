package com.github.shaquu.networking.packets;

import com.github.shaquu.file.TorroFile;

public class PushFilePacket extends Packet {

    private TorroFile torroFile;

    public PushFilePacket(long id, int part, int maxPart, Byte[] data, TorroFile torroFile) {
        super(id, part, maxPart, data);
        this.torroFile = torroFile;
    }

    public TorroFile getTorroFile() {
        return torroFile;
    }
}
