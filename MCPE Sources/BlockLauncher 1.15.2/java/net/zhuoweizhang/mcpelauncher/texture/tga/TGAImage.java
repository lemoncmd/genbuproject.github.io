package net.zhuoweizhang.mcpelauncher.texture.tga;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class TGAImage {
    private int bpp;
    private ByteBuffer data;
    private int format;
    private Header header;
    private boolean topToBottom = false;

    public static class Header {
        public static final int BLACKWHITE = 11;
        public static final int COLORMAPPED = 9;
        public static final int ID_ATTRIBPERPIXEL = 15;
        public static final int ID_INTERLEAVE = 192;
        public static final int ID_RIGHTTOLEFT = 16;
        public static final int ID_TOPTOBOTTOM = 32;
        public static final int I_FOURWAY = 2;
        public static final int I_NOTINTERLEAVED = 0;
        public static final int I_TWOWAY = 1;
        public static final int NO_IMAGE = 0;
        public static final int TRUECOLOR = 10;
        public static final int TYPE_NEW = 0;
        public static final int TYPE_OLD = 1;
        public static final int TYPE_UNK = 2;
        public static final int UBLACKWHITE = 3;
        public static final int UCOLORMAPPED = 1;
        public static final int UTRUECOLOR = 2;
        private byte colorMapEntrySize;
        private int colorMapLength;
        private int colorMapType;
        private int firstEntryIndex;
        private int height;
        private int idLength;
        private byte imageDescriptor;
        private String imageID;
        private byte[] imageIDbuf;
        private int imageType;
        private byte pixelDepth;
        private int tgaType = UCOLORMAPPED;
        private int width;
        private int xOrigin;
        private int yOrigin;

        Header() {
        }

        Header(LEDataInputStream in) throws IOException {
            this.idLength = in.readUnsignedByte();
            this.colorMapType = in.readUnsignedByte();
            this.imageType = in.readUnsignedByte();
            this.firstEntryIndex = in.readUnsignedShort();
            this.colorMapLength = in.readUnsignedShort();
            this.colorMapEntrySize = in.readByte();
            this.xOrigin = in.readUnsignedShort();
            this.yOrigin = in.readUnsignedShort();
            this.width = in.readUnsignedShort();
            this.height = in.readUnsignedShort();
            this.pixelDepth = in.readByte();
            this.imageDescriptor = in.readByte();
            if (this.idLength > 0) {
                this.imageIDbuf = new byte[this.idLength];
                in.read(this.imageIDbuf, TYPE_NEW, this.idLength);
                this.imageID = new String(this.imageIDbuf, "US-ASCII");
            }
        }

        public int tgaType() {
            return this.tgaType;
        }

        public int idLength() {
            return this.idLength;
        }

        public int colorMapType() {
            return this.colorMapType;
        }

        public int imageType() {
            return this.imageType;
        }

        public int firstEntryIndex() {
            return this.firstEntryIndex;
        }

        public int colorMapLength() {
            return this.colorMapLength;
        }

        public byte colorMapEntrySize() {
            return this.colorMapEntrySize;
        }

        public int xOrigin() {
            return this.xOrigin;
        }

        public int yOrigin() {
            return this.yOrigin;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public byte pixelDepth() {
            return this.pixelDepth;
        }

        public byte imageDescriptor() {
            return this.imageDescriptor;
        }

        public byte attribPerPixel() {
            return (byte) (this.imageDescriptor & ID_ATTRIBPERPIXEL);
        }

        public boolean rightToLeft() {
            return (this.imageDescriptor & ID_RIGHTTOLEFT) != 0;
        }

        public boolean topToBottom() {
            return (this.imageDescriptor & ID_TOPTOBOTTOM) != 0;
        }

        public byte interleave() {
            return (byte) ((this.imageDescriptor & ID_INTERLEAVE) >> 6);
        }

        public byte[] imageIDbuf() {
            return this.imageIDbuf;
        }

        public String imageID() {
            return this.imageID;
        }

        public String toString() {
            return "TGA Header  id length: " + this.idLength + " color map type: " + this.colorMapType + " image type: " + this.imageType + " first entry index: " + this.firstEntryIndex + " color map length: " + this.colorMapLength + " color map entry size: " + this.colorMapEntrySize + " x Origin: " + this.xOrigin + " y Origin: " + this.yOrigin + " width: " + this.width + " height: " + this.height + " pixel depth: " + this.pixelDepth + " image descriptor: " + this.imageDescriptor + (this.imageIDbuf == null ? BuildConfig.FLAVOR : " ID String: " + this.imageID);
        }

        public int size() {
            return this.idLength + 18;
        }

        private void write(ByteBuffer buf) {
            buf.put((byte) this.idLength);
            buf.put((byte) this.colorMapType);
            buf.put((byte) this.imageType);
            buf.putShort((short) this.firstEntryIndex);
            buf.putShort((short) this.colorMapLength);
            buf.put(this.colorMapEntrySize);
            buf.putShort((short) this.xOrigin);
            buf.putShort((short) this.yOrigin);
            buf.putShort((short) this.width);
            buf.putShort((short) this.height);
            buf.put(this.pixelDepth);
            buf.put(this.imageDescriptor);
            if (this.idLength > 0) {
                try {
                    buf.put(this.imageID.getBytes("US-ASCII"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private TGAImage(Header header) {
        this.header = header;
    }

    private void decodeImage(LEDataInputStream dIn) throws IOException {
        switch (this.header.imageType()) {
            case NativeRegExp.MATCH /*1*/:
                throw new IOException("TGADecoder Uncompressed Colormapped images not supported");
            case NativeRegExp.PREFIX /*2*/:
                switch (this.header.pixelDepth) {
                    case Token.GT /*16*/:
                        throw new IOException("TGADecoder Compressed 16-bit True Color images not supported");
                    case Token.DIV /*24*/:
                    case Token.TYPEOF /*32*/:
                        decodeRGBImageU24_32(dIn);
                        return;
                    default:
                        return;
                }
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                throw new IOException("TGADecoder Uncompressed Grayscale images not supported");
            case Token.BITOR /*9*/:
                throw new IOException("TGADecoder Compressed Colormapped images not supported");
            case Token.BITXOR /*10*/:
                throw new IOException("TGADecoder Compressed True Color images not supported");
            case Token.BITAND /*11*/:
                throw new IOException("TGADecoder Compressed Grayscale images not supported");
            default:
                return;
        }
    }

    private void decodeRGBImageU24_32(LEDataInputStream dIn) throws IOException {
        int rawWidth = this.header.width() * (this.header.pixelDepth() / 8);
        byte[] rawBuf = new byte[rawWidth];
        byte[] tmpData = new byte[(this.header.height() * rawWidth)];
        for (int i = 0; i < this.header.height(); i++) {
            int y;
            dIn.readFully(rawBuf, 0, rawWidth);
            if (this.header.topToBottom() == this.topToBottom) {
                y = (this.header.height - i) - 1;
            } else {
                y = i;
            }
            System.arraycopy(rawBuf, 0, tmpData, y * rawWidth, rawBuf.length);
        }
        this.data = ByteBuffer.wrap(tmpData);
    }

    public static void swapBGR(byte[] data, int bWidth, int height, int bpp) {
        for (int i = 0; i < height; i++) {
            int j = 0;
            while (j < bWidth) {
                int k = (i * bWidth) + j;
                byte b = data[k + 0];
                data[k + 0] = data[k + 2];
                data[k + 2] = b;
                j += bpp;
            }
        }
    }

    public int getWidth() {
        return this.header.width();
    }

    public int getHeight() {
        return this.header.height();
    }

    public int getGLFormat() {
        return this.format;
    }

    public int getBytesPerPixel() {
        return this.bpp;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public static TGAImage read(String filename) throws IOException {
        return read(new FileInputStream(filename));
    }

    public static TGAImage read(InputStream in) throws IOException {
        LEDataInputStream dIn = new LEDataInputStream(new BufferedInputStream(in));
        TGAImage res = new TGAImage(new Header(dIn));
        res.decodeImage(dIn);
        return res;
    }

    public void write(String filename) throws IOException {
        write(new File(filename));
    }

    public void write(File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        WritableByteChannel chan = stream.getChannel();
        write(chan);
        chan.force(true);
        chan.close();
        stream.close();
    }

    public void write(WritableByteChannel chan) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(this.header.size());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        this.header.write(buf);
        buf.rewind();
        chan.write(buf);
        chan.write(this.data);
        this.data.rewind();
    }

    public static TGAImage createFromData(int width, int height, boolean hasAlpha, boolean topToBottom, ByteBuffer data) {
        int i = 32;
        Header header = new Header();
        header.imageType = 2;
        header.width = width;
        header.height = height;
        header.pixelDepth = (byte) (hasAlpha ? 32 : 24);
        if (!topToBottom) {
            i = 0;
        }
        header.imageDescriptor = (byte) i;
        TGAImage ret = new TGAImage(header);
        ret.data = data;
        return ret;
    }
}
