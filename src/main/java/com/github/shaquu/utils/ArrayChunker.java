package com.github.shaquu.utils;

public class ArrayChunker {

    //based on https://gist.github.com/lesleh/7724554#file-chunkarray-java
    public static Byte[][] forBytes(Byte[] bytes, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) bytes.length / chunkSize);
        Byte[][] output = new Byte[numOfChunks][];

        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(bytes.length - start, chunkSize);

            Byte[] temp = new Byte[length];
            System.arraycopy(bytes, start, temp, 0, length);
            output[i] = temp;
        }

        return output;
    }
}
