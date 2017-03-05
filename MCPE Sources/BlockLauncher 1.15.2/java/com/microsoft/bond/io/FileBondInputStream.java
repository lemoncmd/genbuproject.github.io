package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileBondInputStream extends BondInputStream {
    private final File file;
    private int fileLength;
    private int position;
    private FileInputStream stream;

    public FileBondInputStream(File file) throws FileNotFoundException {
        this.file = file;
        resetStream();
    }

    private void resetStream() throws FileNotFoundException {
        this.fileLength = (int) this.file.length();
        this.position = 0;
        this.stream = new FileInputStream(this.file);
    }

    public BondInputStream clone(boolean z) throws IOException {
        BondInputStream fileBondInputStream = new FileBondInputStream(this.file);
        fileBondInputStream.setPosition(this.position);
        return fileBondInputStream;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isCloneable() {
        return true;
    }

    public boolean isSeekable() {
        return true;
    }

    public byte read() throws IOException {
        this.position++;
        return (byte) this.stream.read();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        while (i3 < i2) {
            try {
                i3 = this.stream.read(bArr, i + i3, i2 - i3) + i3;
            } catch (Throwable th) {
                this.position = i3 + this.position;
            }
        }
        this.position += i3;
        return i3;
    }

    public BondBlob readBlob(int i) throws IOException {
        return new BondBlob(this, i);
    }

    public int setPosition(int i) throws IOException {
        if (i < 0 || i > this.fileLength) {
            throw new BondException("Invalid position: " + i);
        }
        int i2;
        if (i >= this.position) {
            i2 = i - this.position;
        } else {
            resetStream();
            i2 = i;
        }
        this.position = i;
        this.stream.skip((long) i2);
        return this.position;
    }

    public int setPositionRelative(int i) throws IOException {
        setPosition(this.position + i);
        return this.position;
    }
}
