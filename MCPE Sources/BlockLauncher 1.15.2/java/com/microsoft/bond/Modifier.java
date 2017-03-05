package com.microsoft.bond;

import org.mozilla.javascript.regexp.NativeRegExp;

public enum Modifier {
    Optional(0),
    Required(1),
    RequiredOptional(2),
    __INVALID_ENUM_VALUE(3);
    
    private final int value;

    private Modifier(int i) {
        this.value = i;
    }

    public static Modifier fromValue(int i) {
        switch (i) {
            case NativeRegExp.TEST /*0*/:
                return Optional;
            case NativeRegExp.MATCH /*1*/:
                return Required;
            case NativeRegExp.PREFIX /*2*/:
                return RequiredOptional;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }

    public int getValue() {
        return this.value;
    }
}
