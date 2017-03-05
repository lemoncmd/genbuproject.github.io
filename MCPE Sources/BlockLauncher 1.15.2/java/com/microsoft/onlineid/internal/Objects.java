package com.microsoft.onlineid.internal;

public class Objects {
    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static int hashCode(Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }

    public static void verifyArgumentNotNull(Object obj, String str) {
        if (obj == null) {
            throw new IllegalArgumentException(str + " must not be null.");
        }
    }
}
