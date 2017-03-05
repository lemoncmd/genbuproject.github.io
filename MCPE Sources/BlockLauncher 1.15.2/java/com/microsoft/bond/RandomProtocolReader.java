package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.ProtocolReader.MapTag;
import java.io.IOException;
import java.util.Random;

public class RandomProtocolReader extends ProtocolReader {
    private static final int DEFAULT_MAX_CONTAINER_SIZE = 10;
    private static final int DEFAULT_MAX_STRING_LENGTH = 20;
    private final int maxContainerSize;
    private final int maxStringLength;
    private final Random random;

    public RandomProtocolReader() {
        this.maxStringLength = DEFAULT_MAX_STRING_LENGTH;
        this.maxContainerSize = DEFAULT_MAX_CONTAINER_SIZE;
        this.random = new Random();
    }

    public RandomProtocolReader(long j) {
        this(j, DEFAULT_MAX_STRING_LENGTH, DEFAULT_MAX_CONTAINER_SIZE);
    }

    public RandomProtocolReader(long j, int i, int i2) {
        this.maxStringLength = i;
        this.maxContainerSize = i2;
        this.random = new Random(j);
    }

    public ProtocolReader clone() {
        return null;
    }

    public int getPosition() throws IOException {
        throw new IOException();
    }

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        return false;
    }

    public boolean isProtocolSame(ProtocolWriter protocolWriter) {
        return false;
    }

    public BondBlob readBlob(int i) {
        return null;
    }

    public boolean readBool() {
        return this.random.nextBoolean();
    }

    public ListTag readContainerBegin() {
        return new ListTag(this.random.nextInt(this.maxContainerSize) + 1, BondDataType.BT_UNAVAILABLE);
    }

    public void readContainerEnd() {
    }

    public double readDouble() {
        return ((double) this.random.nextLong()) * this.random.nextDouble();
    }

    public float readFloat() {
        return ((float) this.random.nextLong()) * this.random.nextFloat();
    }

    public short readInt16() {
        return (short) (this.random.nextInt(65535) - 32767);
    }

    public int readInt32() {
        return this.random.nextInt();
    }

    public long readInt64() {
        return this.random.nextLong();
    }

    public byte readInt8() {
        return (byte) (this.random.nextInt(255) - 127);
    }

    public MapTag readMapContainerBegin() {
        return new MapTag(this.random.nextInt(this.maxContainerSize) + 1, BondDataType.BT_UNAVAILABLE, BondDataType.BT_UNAVAILABLE);
    }

    public String readString() {
        int nextInt = this.random.nextInt(this.maxStringLength) + 1;
        StringBuilder stringBuilder = new StringBuilder(nextInt);
        for (int i = 0; i < nextInt; i++) {
            stringBuilder.append((char) (this.random.nextInt(94) + 32));
        }
        return stringBuilder.toString();
    }

    public short readUInt16() {
        return (short) (65535 & this.random.nextInt());
    }

    public int readUInt32() {
        return this.random.nextInt();
    }

    public long readUInt64() {
        return this.random.nextLong();
    }

    public byte readUInt8() {
        return (byte) this.random.nextInt(255);
    }

    public String readWString() {
        return readString();
    }

    public void setPosition(int i) throws IOException {
        throw new IOException();
    }

    public void skip(BondDataType bondDataType) {
    }
}
