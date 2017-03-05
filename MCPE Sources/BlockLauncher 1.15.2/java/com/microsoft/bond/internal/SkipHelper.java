package com.microsoft.bond.internal;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondException;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.ProtocolReader.MapTag;
import com.microsoft.bond.Void;
import java.io.IOException;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class SkipHelper {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$BondDataType = new int[BondDataType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_UINT8.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_UINT16.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_UINT32.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_UINT64.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_FLOAT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_DOUBLE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_STRING.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_STRUCT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_LIST.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_SET.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_MAP.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_INT8.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_INT16.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_INT32.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_INT64.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_WSTRING.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
        }
    }

    public static void skip(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        int i = 0;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$BondDataType[bondDataType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                protocolReader.readBool();
                return;
            case NativeRegExp.PREFIX /*2*/:
                protocolReader.readUInt8();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                protocolReader.readUInt16();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                protocolReader.readUInt32();
                return;
            case Token.GOTO /*5*/:
                protocolReader.readUInt64();
                return;
            case Token.IFEQ /*6*/:
                protocolReader.readFloat();
                return;
            case Token.IFNE /*7*/:
                protocolReader.readDouble();
                return;
            case Token.SETNAME /*8*/:
                protocolReader.readString();
                return;
            case Token.BITOR /*9*/:
                new Void().readNested(protocolReader);
                return;
            case Token.BITXOR /*10*/:
            case Token.BITAND /*11*/:
                ListTag readContainerBegin = protocolReader.readContainerBegin();
                while (i < readContainerBegin.size) {
                    protocolReader.skip(readContainerBegin.type);
                    i++;
                }
                protocolReader.readContainerEnd();
                return;
            case Token.EQ /*12*/:
                MapTag readMapContainerBegin = protocolReader.readMapContainerBegin();
                while (i < readMapContainerBegin.size) {
                    protocolReader.skip(readMapContainerBegin.keyType);
                    protocolReader.skip(readMapContainerBegin.valueType);
                    i++;
                }
                protocolReader.readContainerEnd();
                return;
            case Token.NE /*13*/:
                protocolReader.readInt8();
                return;
            case Token.LT /*14*/:
                protocolReader.readInt16();
                return;
            case Token.LE /*15*/:
                protocolReader.readInt32();
                return;
            case Token.GT /*16*/:
                protocolReader.readInt64();
                return;
            case Token.GE /*17*/:
                protocolReader.readWString();
                return;
            default:
                throw new BondException("Unknown type to skip: " + bondDataType.toString());
        }
    }
}
