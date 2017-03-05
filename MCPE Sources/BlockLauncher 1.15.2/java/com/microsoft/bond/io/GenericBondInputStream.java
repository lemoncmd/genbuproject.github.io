package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.IOException;
import java.io.InputStream;

public class GenericBondInputStream extends BondInputStream {
    private final InputStream stream;

    public GenericBondInputStream(InputStream inputStream) {
        this.stream = inputStream;
    }

    public BondInputStream clone(boolean z) {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public int getPosition() {
        throw new UnsupportedOperationException();
    }

    public boolean isCloneable() {
        return false;
    }

    public boolean isSeekable() {
        return false;
    }

    public byte read() {
        try {
            return (byte) this.stream.read();
        } catch (IOException e) {
            throw new BondException(e);
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        try {
            return this.stream.read(bArr, i, i2);
        } catch (IOException e) {
            throw new BondException(e);
        }
    }

    public BondBlob readBlob(int i) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int setPosition(int i) {
        throw new UnsupportedOperationException();
    }

    public int setPositionRelative(int i) {
        throw new UnsupportedOperationException();
    }
}
