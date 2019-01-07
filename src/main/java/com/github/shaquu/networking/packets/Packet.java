package com.github.shaquu.networking.packets;

import java.io.*;

public class Packet implements Serializable {

    public final static int BASE_SIZE = 300;

    private final long id;
    private final int part;
    private final int maxPart;

    private Byte[] data;

    public Packet(long id, int part, int maxPart, Byte[] data) {
        this.id = id;
        this.part = part;
        this.maxPart = maxPart;
        this.data = data;
    }

    public static byte[] toBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);

        out.writeObject(object);
        out.close();

        return bos.toByteArray();
    }

    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = new ObjectInputStream(bis)
        ) {
            return in.readObject();
        }
    }

    public long getId() {
        return id;
    }

    int getPart() {
        return part;
    }

    int getMaxPart() {
        return maxPart;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", part=" + part +
                ", maxPart=" + maxPart +
                ", data=" + data.length +
                '}';
    }

    public Byte[] getData() {
        return data;
    }

    public int getPacketSize() throws IOException {
        return Packet.toBytes(this).length;
    }
}
