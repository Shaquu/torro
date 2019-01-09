package com.github.shaquu.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayChunker<T> {

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

    public List<List<T>> forList(List<T> list, int chunkSize) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += chunkSize) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + chunkSize)))
            );
        }
        return parts;
    }
}
