package com.jakewharton;

import java.lang.reflect.Array;

class Arrays {
    Arrays() {
    }

    static <T> T[] copyOfRange(T[] original, int start, int end) {
        int originalLength = original.length;
        if (start > end) {
            throw new IllegalArgumentException();
        } else if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            int resultLength = end - start;
            Object[] result = (Object[]) ((Object[]) Array.newInstance(original.getClass().getComponentType(), resultLength));
            System.arraycopy(original, start, result, 0, Math.min(resultLength, originalLength - start));
            return result;
        }
    }
}
