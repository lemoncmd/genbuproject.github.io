package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.EOFException;
import java.io.IOException;

public class MemoryBondInputStream extends BondInputStream {
    private final byte[] buffer;
    private final int bufferLength;
    private final int bufferOffset;
    private int readPosition;

    public MemoryBondInputStream(byte[] bArr) {
        this(bArr, 0, bArr.length);
    }

    public MemoryBondInputStream(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.bufferOffset = i;
        this.bufferLength = i2;
        this.readPosition = 0;
    }

    private void validateNewPosition(int i) {
        if (i < 0) {
            throw new BondException(String.format("Invalid stream position [%s].", new Object[]{Integer.valueOf(i)}));
        } else if (i > this.bufferLength) {
            throw new BondException(String.format("Position [%s] is past the end of the buffer.", new Object[]{Integer.valueOf(i)}));
        }
    }

    private void validateRead(int i) throws EOFException {
        if (this.readPosition + i > this.bufferLength) {
            throw new EOFException(String.format("EOF reached. Trying to read [%d] bytes", new Object[]{Integer.valueOf(i)}));
        }
    }

    public BondInputStream clone(boolean z) {
        BondInputStream memoryBondInputStream = new MemoryBondInputStream(this.buffer, this.bufferOffset, this.bufferLength);
        memoryBondInputStream.readPosition = this.readPosition;
        return memoryBondInputStream;
    }

    public void close() throws IOException {
    }

    public int getPosition() {
        return this.readPosition;
    }

    public boolean isCloneable() {
        return true;
    }

    public boolean isSeekable() {
        return true;
    }

    public byte read() throws EOFException {
        validateRead(1);
        this.readPosition++;
        return this.buffer[(this.bufferOffset + this.readPosition) - 1];
    }

    public int read(byte[] bArr, int i, int i2) throws EOFException {
        validateRead(i2);
        System.arraycopy(this.buffer, this.bufferOffset + this.readPosition, bArr, i, i2);
        this.readPosition += i2;
        return i2;
    }

    public BondBlob readBlob(int i) throws IOException {
        return new BondBlob(this, i);
    }

    public int setPosition(int i) {
        validateNewPosition(i);
        this.readPosition = i;
        return this.readPosition;
    }

    public int setPositionRelative(int i) {
        int i2 = this.readPosition + i;
        validateNewPosition(i2);
        this.readPosition = i2;
        return this.readPosition;
    }
}
