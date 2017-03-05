package com.microsoft.bond.internal;

import java.nio.charset.Charset;

public final class StringHelper {
    public static final Charset UTF16 = Charset.forName("utf-16le");
    public static final Charset UTF8 = Charset.forName("utf-8");

    private StringHelper() {
    }

    public static String decodeFromUtf16(byte[] bArr, int i, int i2) {
        return new String(bArr, i, i2, UTF16);
    }

    public static String decodeFromUtf8(byte[] bArr, int i, int i2) {
        return new String(bArr, i, i2, UTF8);
    }

    public static byte[] encodeToUtf16(String str) {
        return str.getBytes(UTF16);
    }

    public static byte[] encodeToUtf8(String str) {
        return str.getBytes(UTF8);
    }
}
