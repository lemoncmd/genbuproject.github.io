package com.microsoft.bond;

import org.mozilla.javascript.regexp.NativeRegExp;

public enum ProtocolVersion {
    ONE(1),
    TWO(2);
    
    private short value;

    private ProtocolVersion(int i) {
        this.value = (short) i;
    }

    public static ProtocolVersion fromValue(short s) {
        switch (s) {
            case NativeRegExp.MATCH /*1*/:
                return ONE;
            case NativeRegExp.PREFIX /*2*/:
                return TWO;
            default:
                return null;
        }
    }

    public short getValue() {
        return this.value;
    }
}
