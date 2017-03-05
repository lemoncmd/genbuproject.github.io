package com.microsoft.onlineid.internal;

import java.util.Locale;

public class Integers {
    public static int parseIntHex(String str) {
        Strings.verifyArgumentNotNullOrEmpty(str, "hexHr");
        long longValue = Long.decode(str).longValue();
        if (longValue >= 0 && longValue <= 4294967295L) {
            return (int) longValue;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "Hex string does not fit in integer: %s", new Object[]{str}));
    }
}
