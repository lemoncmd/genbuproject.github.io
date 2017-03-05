package net.zhuoweizhang.mcpelauncher.texture.tga;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LEDataInputStream extends FilterInputStream implements DataInput {
    DataInputStream dataIn;

    public LEDataInputStream(InputStream in) {
        super(in);
        this.dataIn = new DataInputStream(in);
    }

    public void close() throws IOException {
        this.dataIn.close();
    }

    public final synchronized int read(byte[] b) throws IOException {
        return this.dataIn.read(b, 0, b.length);
    }

    public final synchronized int read(byte[] b, int off, int len) throws IOException {
        return this.dataIn.read(b, off, len);
    }

    public final void readFully(byte[] b) throws IOException {
        this.dataIn.readFully(b, 0, b.length);
    }

    public final void readFully(byte[] b, int off, int len) throws IOException {
        this.dataIn.readFully(b, off, len);
    }

    public final int skipBytes(int n) throws IOException {
        return this.dataIn.skipBytes(n);
    }

    public final boolean readBoolean() throws IOException {
        int ch = this.dataIn.read();
        if (ch >= 0) {
            return ch != 0;
        } else {
            throw new EOFException();
        }
    }

    public final byte readByte() throws IOException {
        int ch = this.dataIn.read();
        if (ch >= 0) {
            return (byte) ch;
        }
        throw new EOFException();
    }

    public final int readUnsignedByte() throws IOException {
        int ch = this.dataIn.read();
        if (ch >= 0) {
            return ch;
        }
        throw new EOFException();
    }

    public final short readShort() throws IOException {
        int ch1 = this.dataIn.read();
        int ch2 = this.dataIn.read();
        if ((ch1 | ch2) >= 0) {
            return (short) ((ch1 << 0) + (ch2 << 8));
        }
        throw new EOFException();
    }

    public final int readUnsignedShort() throws IOException {
        int ch1 = this.dataIn.read();
        int ch2 = this.dataIn.read();
        if ((ch1 | ch2) >= 0) {
            return (ch1 << 0) + (ch2 << 8);
        }
        throw new EOFException();
    }

    public final char readChar() throws IOException {
        int ch1 = this.dataIn.read();
        int ch2 = this.dataIn.read();
        if ((ch1 | ch2) >= 0) {
            return (char) ((ch1 << 0) + (ch2 << 8));
        }
        throw new EOFException();
    }

    public final int readInt() throws IOException {
        int ch1 = this.dataIn.read();
        int ch2 = this.dataIn.read();
        int ch3 = this.dataIn.read();
        int ch4 = this.dataIn.read();
        if ((((ch1 | ch2) | ch3) | ch4) >= 0) {
            return (((ch1 << 0) + (ch2 << 8)) + (ch3 << 16)) + (ch4 << 24);
        }
        throw new EOFException();
    }

    public final long readLong() throws IOException {
        return (((long) readInt()) & 4294967295L) + ((long) (readInt() << 32));
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final String readLine() throws IOException {
        return new String();
    }

    public final String readUTF() throws IOException {
        return new String();
    }

    public static final String readUTF(DataInput in) throws IOException {
        return new String();
    }
}
