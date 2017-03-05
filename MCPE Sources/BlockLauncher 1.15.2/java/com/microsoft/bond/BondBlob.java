package com.microsoft.bond;

import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;

public class BondBlob {
    private final byte[] buffer;
    private final int length;
    private final int offset;

    public BondBlob() {
        this.buffer = null;
        this.length = 0;
        this.offset = 0;
    }

    public BondBlob(BondBlob bondBlob) {
        this.buffer = bondBlob.buffer;
        this.offset = bondBlob.offset;
        this.length = bondBlob.length;
    }

    public BondBlob(BondInputStream bondInputStream, int i) throws IOException {
        this.buffer = new byte[i];
        this.length = i;
        this.offset = 0;
        bondInputStream.read(this.buffer, this.offset, this.length);
    }

    public BondBlob(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.offset = i;
        this.length = i2;
    }

    public boolean equals(BondBlob bondBlob) {
        if (this.length != bondBlob.length) {
            return false;
        }
        for (int i = 0; i < this.length; i++) {
            if (this.buffer[this.offset + i] != bondBlob.buffer[bondBlob.offset + i]) {
                return false;
            }
        }
        return true;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getOffset() {
        return this.offset;
    }

    public int size() {
        return this.length;
    }
}
