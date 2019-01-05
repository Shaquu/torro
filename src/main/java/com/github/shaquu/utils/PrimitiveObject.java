package com.github.shaquu.utils;

public class PrimitiveObject {

    public static byte[] toByteArrPrimitive(Byte[] bytesObject) {
        byte[] bytesPrimitive = new byte[bytesObject.length];

        for (int i = 0; i < bytesObject.length; i++) {
            bytesPrimitive[i] = bytesObject[i];
        }

        return bytesPrimitive;
    }

    public static Byte[] toByteArrObject(byte[] bytesPrimitive) {
        Byte[] bytesObject = new Byte[bytesPrimitive.length];

        for (int i = 0; i < bytesPrimitive.length; i++) {
            bytesObject[i] = bytesPrimitive[i];
        }

        return bytesObject;
    }

}
