package com.github.shaquu.networking.packets;

import com.github.shaquu.file.TorroFile;

import java.util.List;

public class PushFilePartsPacket extends PushFilePacket {
    private List<Integer> parts;

    public PushFilePartsPacket(long id, int part, int maxPart, Byte[] data, TorroFile torroFile, List<Integer> parts) {
        super(id, part, maxPart, data, torroFile);
        this.parts = parts;
    }

    public List<Integer> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "PushFilePartsPacket{" +
                "id=" + getId() +
                ", part=" + getPart() +
                ", maxPart=" + getMaxPart() +
                ", data=" + getData().length +
                ", parts=" + parts.size() +
                '}';
    }
}
