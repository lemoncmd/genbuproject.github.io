package com.microsoft.bond;

import java.io.Closeable;
import java.io.IOException;

public abstract class ProtocolReader implements Closeable {

    public static class FieldTag {
        public final int id;
        public final BondDataType type;

        FieldTag(BondDataType bondDataType, int i) {
            this.type = bondDataType;
            this.id = i;
        }
    }

    public static class ListTag {
        public final int size;
        public final BondDataType type;

        public ListTag(int i, BondDataType bondDataType) {
            this.size = i;
            this.type = bondDataType;
        }
    }

    public static class MapTag {
        public final BondDataType keyType;
        public final int size;
        public final BondDataType valueType;

        public MapTag(int i, BondDataType bondDataType, BondDataType bondDataType2) {
            this.size = i;
            this.keyType = bondDataType;
            this.valueType = bondDataType2;
        }
    }

    public ProtocolReader cloneReader() throws IOException {
        return null;
    }

    public void close() throws IOException {
    }

    public abstract int getPosition() throws IOException;

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        return false;
    }

    public abstract boolean isProtocolSame(ProtocolWriter protocolWriter);

    public void readBegin() {
    }

    public abstract BondBlob readBlob(int i) throws IOException;

    public abstract boolean readBool() throws IOException;

    public abstract ListTag readContainerBegin() throws IOException;

    public abstract void readContainerEnd() throws IOException;

    public abstract double readDouble() throws IOException;

    public void readEnd() {
    }

    public FieldTag readFieldBegin() throws IOException {
        return new FieldTag(BondDataType.BT_UNAVAILABLE, 32767);
    }

    public void readFieldEnd() throws IOException {
    }

    public boolean readFieldOmitted() throws IOException {
        return false;
    }

    public abstract float readFloat() throws IOException;

    public abstract short readInt16() throws IOException;

    public abstract int readInt32() throws IOException;

    public abstract long readInt64() throws IOException;

    public abstract byte readInt8() throws IOException;

    public abstract MapTag readMapContainerBegin() throws IOException;

    public abstract String readString() throws IOException;

    public void readStructBegin(boolean z) throws IOException {
    }

    public void readStructEnd() throws IOException {
    }

    public abstract short readUInt16() throws IOException;

    public abstract int readUInt32() throws IOException;

    public abstract long readUInt64() throws IOException;

    public abstract byte readUInt8() throws IOException;

    public abstract String readWString() throws IOException;

    public abstract void setPosition(int i) throws IOException;

    public abstract void skip(BondDataType bondDataType) throws IOException;
}
