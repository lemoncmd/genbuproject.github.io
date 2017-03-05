package com.microsoft.bond.internal;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.ListTag;
import java.io.IOException;

public final class ReadHelper {
    private ReadHelper() {
    }

    public static void invalideType(BondDataType bondDataType, BondDataType bondDataType2) {
    }

    public static BondBlob readBlob(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        validateType(readContainerBegin.type, BondDataType.BT_LIST);
        BondBlob bondBlob = readContainerBegin.size == 0 ? new BondBlob() : new BondBlob(protocolReader.readBlob(readContainerBegin.size));
        protocolReader.readContainerEnd();
        return bondBlob;
    }

    public static boolean readBool(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_BOOL);
        return protocolReader.readBool();
    }

    public static double readDouble(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_DOUBLE || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readDouble();
        }
        if (bondDataType == BondDataType.BT_FLOAT) {
            return (double) protocolReader.readFloat();
        }
        invalideType(bondDataType, BondDataType.BT_DOUBLE);
        return 0.0d;
    }

    public static float readFloat(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_FLOAT);
        return protocolReader.readFloat();
    }

    public static short readInt16(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_INT16 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readInt16();
        }
        if (bondDataType == BondDataType.BT_INT8) {
            return (short) protocolReader.readInt8();
        }
        invalideType(bondDataType, BondDataType.BT_INT16);
        return (short) 0;
    }

    public static int readInt32(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_INT32 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readInt32();
        }
        if (bondDataType == BondDataType.BT_INT16) {
            return protocolReader.readInt16();
        }
        if (bondDataType == BondDataType.BT_INT8) {
            return protocolReader.readInt8();
        }
        invalideType(bondDataType, BondDataType.BT_INT32);
        return 0;
    }

    public static long readInt64(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_INT64 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readInt64();
        }
        if (bondDataType == BondDataType.BT_INT32) {
            return (long) protocolReader.readInt32();
        }
        if (bondDataType == BondDataType.BT_INT16) {
            return (long) protocolReader.readInt16();
        }
        if (bondDataType == BondDataType.BT_INT8) {
            return (long) protocolReader.readInt8();
        }
        invalideType(bondDataType, BondDataType.BT_INT64);
        return 0;
    }

    public static byte readInt8(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_INT8);
        return protocolReader.readInt8();
    }

    public static String readString(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_STRING);
        return protocolReader.readString();
    }

    public static short readUInt16(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_UINT16 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readUInt16();
        }
        if (bondDataType == BondDataType.BT_UINT8) {
            return (short) protocolReader.readUInt8();
        }
        invalideType(bondDataType, BondDataType.BT_UINT16);
        return (short) 0;
    }

    public static int readUInt32(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_UINT32 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readUInt32();
        }
        if (bondDataType == BondDataType.BT_UINT16) {
            return protocolReader.readUInt16();
        }
        if (bondDataType == BondDataType.BT_UINT8) {
            return protocolReader.readUInt8();
        }
        invalideType(bondDataType, BondDataType.BT_UINT32);
        return 0;
    }

    public static long readUInt64(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        if (bondDataType == BondDataType.BT_UINT64 || bondDataType == BondDataType.BT_UNAVAILABLE) {
            return protocolReader.readUInt64();
        }
        if (bondDataType == BondDataType.BT_UINT32) {
            return (long) protocolReader.readUInt32();
        }
        if (bondDataType == BondDataType.BT_UINT16) {
            return (long) protocolReader.readUInt16();
        }
        if (bondDataType == BondDataType.BT_UINT8) {
            return (long) protocolReader.readUInt8();
        }
        invalideType(bondDataType, BondDataType.BT_UINT64);
        return 0;
    }

    public static byte readUInt8(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_UINT8);
        return protocolReader.readUInt8();
    }

    public static String readWString(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        validateType(bondDataType, BondDataType.BT_WSTRING);
        return protocolReader.readWString();
    }

    public static void skipPartialStruct(ProtocolReader protocolReader) throws IOException {
        FieldTag readFieldBegin;
        do {
            protocolReader.readStructBegin(true);
            readFieldBegin = protocolReader.readFieldBegin();
            while (readFieldBegin.type != BondDataType.BT_STOP && readFieldBegin.type != BondDataType.BT_STOP_BASE) {
                protocolReader.skip(readFieldBegin.type);
                protocolReader.readFieldEnd();
                readFieldBegin = protocolReader.readFieldBegin();
            }
            protocolReader.readStructEnd();
        } while (BondDataType.BT_STOP != readFieldBegin.type);
    }

    public static void validateType(BondDataType bondDataType, BondDataType bondDataType2) {
        if (bondDataType != bondDataType2 && bondDataType != BondDataType.BT_UNAVAILABLE) {
            invalideType(bondDataType, bondDataType2);
        }
    }
}
