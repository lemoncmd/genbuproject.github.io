package com.microsoft.bond;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public enum BondDataType {
    BT_STOP(0),
    BT_STOP_BASE(1),
    BT_BOOL(2),
    BT_UINT8(3),
    BT_UINT16(4),
    BT_UINT32(5),
    BT_UINT64(6),
    BT_FLOAT(7),
    BT_DOUBLE(8),
    BT_STRING(9),
    BT_STRUCT(10),
    BT_LIST(11),
    BT_SET(12),
    BT_MAP(13),
    BT_INT8(14),
    BT_INT16(15),
    BT_INT32(16),
    BT_INT64(17),
    BT_WSTRING(18),
    BT_UNAVAILABLE(Token.VOID),
    __INVALID_ENUM_VALUE(Token.RESERVED);
    
    private final int value;

    private BondDataType(int i) {
        this.value = i;
    }

    public static BondDataType fromValue(int i) {
        switch (i) {
            case NativeRegExp.TEST /*0*/:
                return BT_STOP;
            case NativeRegExp.MATCH /*1*/:
                return BT_STOP_BASE;
            case NativeRegExp.PREFIX /*2*/:
                return BT_BOOL;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return BT_UINT8;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return BT_UINT16;
            case Token.GOTO /*5*/:
                return BT_UINT32;
            case Token.IFEQ /*6*/:
                return BT_UINT64;
            case Token.IFNE /*7*/:
                return BT_FLOAT;
            case Token.SETNAME /*8*/:
                return BT_DOUBLE;
            case Token.BITOR /*9*/:
                return BT_STRING;
            case Token.BITXOR /*10*/:
                return BT_STRUCT;
            case Token.BITAND /*11*/:
                return BT_LIST;
            case Token.EQ /*12*/:
                return BT_SET;
            case Token.NE /*13*/:
                return BT_MAP;
            case Token.LT /*14*/:
                return BT_INT8;
            case Token.LE /*15*/:
                return BT_INT16;
            case Token.GT /*16*/:
                return BT_INT32;
            case Token.GE /*17*/:
                return BT_INT64;
            case Token.LSH /*18*/:
                return BT_WSTRING;
            case Token.VOID /*127*/:
                return BT_UNAVAILABLE;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }

    public int getValue() {
        return this.value;
    }
}
