package net.zhuoweizhang.mcpelauncher.texture.tga;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LEDataOutputStream extends FilterOutputStream implements DataOutput {
    DataOutputStream dataOut;

    public LEDataOutputStream(OutputStream out) {
        super(out);
        this.dataOut = new DataOutputStream(out);
    }

    public void close() throws IOException {
        this.dataOut.close();
    }

    public final synchronized void write(byte[] b) throws IOException {
        this.dataOut.write(b, 0, b.length);
    }

    public final synchronized void write(byte[] b, int off, int len) throws IOException {
        this.dataOut.write(b, off, len);
    }

    public final void write(int b) throws IOException {
        this.dataOut.write(b);
    }

    public final void writeBoolean(boolean v) throws IOException {
        this.dataOut.writeBoolean(v);
    }

    public final void writeByte(int v) throws IOException {
        this.dataOut.writeByte(v);
    }

    public final void writeBytes(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final void writeChar(int v) throws IOException {
        this.dataOut.writeChar(((v >> 8) & 255) | ((v & 255) << 8));
    }

    public final void writeChars(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToRawLongBits(v));
    }

    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToRawIntBits(v));
    }

    public final void writeInt(int v) throws IOException {
        this.dataOut.writeInt((((v >>> 24) | ((v >>> 8) & 65280)) | ((v << 8) & 65280)) | (v << 24));
    }

    public final void writeLong(long v) throws IOException {
        writeInt((int) v);
        writeInt((int) (v >>> 32));
    }

    public final void writeShort(int v) throws IOException {
        this.dataOut.writeShort(((v >> 8) & 255) | ((v & 255) << 8));
    }

    public final void writeUTF(String s) throws IOException {
        throw new UnsupportedOperationException();
    }
}
