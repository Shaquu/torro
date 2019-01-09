package com.github.shaquu.networking.packets;

import java.util.List;

public class PullFilePartsPacket extends Packet {
    private List<Integer> parts;

    public PullFilePartsPacket(long id, int part, int maxPart, Byte[] data, List<Integer> parts) {
        super(id, part, maxPart, data);
        this.parts = parts;
    }

    public List<Integer> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "PullFilePartsPacket{" +
                "id=" + getId() +
                ", part=" + getPart() +
                ", maxPart=" + getMaxPart() +
                ", data=" + getData().length +
                ", parts=" + parts.size() +
                '}';
    }
}
