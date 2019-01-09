package com.github.shaquu.networking.packets;

import com.github.shaquu.file.TorroFile;

public class PullFilePacket extends Packet {

    private TorroFile torroFile;

    public PullFilePacket(long id, int part, int maxPart, Byte[] data, TorroFile torroFile) {
        super(id, part, maxPart, data);
        this.torroFile = torroFile;
    }

    public TorroFile getTorroFile() {
        return torroFile;
    }
}
