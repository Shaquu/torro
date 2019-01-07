package com.github.shaquu.utils;

import java.util.Set;

public class Utils {
    public static Object getFromSetByIndex(Set<?> set, int index) {
        int result = 0;
        for (Object entry : set) {
            if (result == index)
                return entry;
            result++;
        }
        return -1;
    }
}
