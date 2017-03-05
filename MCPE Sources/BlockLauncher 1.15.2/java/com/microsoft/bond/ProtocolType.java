package com.microsoft.bond;

import org.mozilla.javascript.regexp.NativeRegExp;

public enum ProtocolType {
    MARSHALED_PROTOCOL(0),
    MAFIA_PROTOCOL(17997),
    COMPACT_PROTOCOL(16963),
    JSON_PROTOCOL(21322),
    PRETTY_JSON_PROTOCOL(20554),
    SIMPLE_PROTOCOL(20563),
    __INVALID_ENUM_VALUE(21323);
    
    private final int value;

    private ProtocolType(int i) {
        this.value = i;
    }

    public static ProtocolType fromValue(int i) {
        switch (i) {
            case NativeRegExp.TEST /*0*/:
                return MARSHALED_PROTOCOL;
            case 16963:
                return COMPACT_PROTOCOL;
            case 17997:
                return MAFIA_PROTOCOL;
            case 20554:
                return PRETTY_JSON_PROTOCOL;
            case 20563:
                return SIMPLE_PROTOCOL;
            case 21322:
                return JSON_PROTOCOL;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }

    public int getValue() {
        return this.value;
    }
}
