package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import java.io.IOException;

public class MemoryBondOutputStream extends BondOutputStream {
    private static final int DEFAULT_CAPACITY_BYTES = 1024;
    private byte[] buffer;
    private int position;

    public MemoryBondOutputStream() {
        this(DEFAULT_CAPACITY_BYTES);
    }

    public MemoryBondOutputStream(int i) {
        this.buffer = new byte[i];
        this.position = 0;
    }

    private void ensureBufferSizeForExtraBytes(int i) {
        if (this.buffer.length < this.position + i) {
            int length = this.buffer.length + (this.buffer.length >> 1);
            if (length < this.position + i) {
                length = this.position + i;
            }
            Object obj = new byte[length];
            System.arraycopy(this.buffer, 0, obj, 0, this.position);
            this.buffer = obj;
        }
    }

    public void close() throws IOException {
        this.buffer = null;
        this.position = -1;
    }

    public int getPosition() throws IOException {
        return this.position;
    }

    public boolean isSeekable() {
        return true;
    }

    public int setPosition(int i) throws IOException {
        if (i < 0 || i >= this.buffer.length) {
            throw new IllegalArgumentException(String.format("Cannot jump to position [%d]. Valid positions are from [%d] to [%d] inclusive.", new Object[]{Integer.valueOf(i), Integer.valueOf(0), Integer.valueOf(this.buffer.length - 1)}));
        }
        this.position = i;
        return this.position;
    }

    public int setPositionRelative(int i) throws IOException {
        return setPosition(this.position + i);
    }

    public BondBlob toBondBlod() {
        return new BondBlob(this.buffer, 0, this.position);
    }

    public byte[] toByteArray() {
        Object obj = new byte[this.position];
        System.arraycopy(this.buffer, 0, obj, 0, obj.length);
        return obj;
    }

    public void write(byte b) {
        ensureBufferSizeForExtraBytes(1);
        this.buffer[this.position] = b;
        this.position++;
    }

    public void write(byte[] bArr) {
        write(bArr, 0, bArr.length);
    }

    public void write(byte[] bArr, int i, int i2) {
        ensureBufferSizeForExtraBytes(i2);
        System.arraycopy(bArr, i, this.buffer, this.position, i2);
        this.position += i2;
    }
}
