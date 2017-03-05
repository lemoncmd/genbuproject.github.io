package com.microsoft.bond.io;

import java.io.IOException;
import java.io.OutputStream;

public class GenericBondOutputStream extends BondOutputStream {
    private final OutputStream stream;

    public GenericBondOutputStream(OutputStream outputStream) {
        this.stream = outputStream;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public int getPosition() throws IOException {
        throw new UnsupportedOperationException();
    }

    public boolean isSeekable() {
        return false;
    }

    public int setPosition(int i) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int setPositionRelative(int i) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void write(byte b) throws IOException {
        this.stream.write(b);
    }

    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    public void write(byte[] bArr, int i, int i2) throws IOException {
        this.stream.write(bArr, i, i2);
    }
}
