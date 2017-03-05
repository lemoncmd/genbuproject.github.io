package net.zhuoweizhang.mcpelauncher.texture.tga;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.mozilla.javascript.Token;

public final class TGALoader {
    public static final int RGBA8 = 4;
    public static final int TYPE_BLACKANDWHITE = 3;
    public static final int TYPE_BLACKANDWHITE_RLE = 11;
    public static final int TYPE_COLORMAPPED = 1;
    public static final int TYPE_COLORMAPPED_RLE = 9;
    public static final int TYPE_NO_IMAGE = 0;
    public static final int TYPE_TRUECOLOR = 2;
    public static final int TYPE_TRUECOLOR_RLE = 10;

    static class ColorMapEntry {
        byte alpha;
        byte blue;
        byte green;
        byte red;

        ColorMapEntry() {
        }

        public String toString() {
            return "entry: " + this.red + "," + this.green + "," + this.blue + "," + this.alpha;
        }
    }

    public static Bitmap load(InputStream in, boolean flip) throws IOException {
        ColorMapEntry entry;
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(in));
        int idLength = dataInputStream.readUnsignedByte();
        int colorMapType = dataInputStream.readUnsignedByte();
        int imageType = dataInputStream.readUnsignedByte();
        dataInputStream.readShort();
        short cMapLength = flipEndian(dataInputStream.readShort());
        int cMapDepth = dataInputStream.readUnsignedByte();
        dataInputStream.readShort();
        dataInputStream.readShort();
        int width = flipEndian(dataInputStream.readShort());
        int height = flipEndian(dataInputStream.readShort());
        int pixelDepth = dataInputStream.readUnsignedByte();
        int imageDescriptor = dataInputStream.readUnsignedByte();
        if ((imageDescriptor & 32) != 0) {
            flip = !flip;
        }
        if ((imageDescriptor & 16) != 0) {
            if (false) {
            }
        }
        if (idLength > 0) {
            dataInputStream.skip((long) idLength);
        }
        ColorMapEntry[] cMapEntries = null;
        if (colorMapType != 0) {
            int bytesInColorMap = (cMapDepth * cMapLength) >> TYPE_BLACKANDWHITE;
            int bitsPerColor = Math.min(cMapDepth / TYPE_BLACKANDWHITE, 8);
            byte[] cMapData = new byte[bytesInColorMap];
            dataInputStream.read(cMapData);
            if (imageType == TYPE_COLORMAPPED || imageType == TYPE_COLORMAPPED_RLE) {
                cMapEntries = new ColorMapEntry[cMapLength];
                int alphaSize = cMapDepth - (bitsPerColor * TYPE_BLACKANDWHITE);
                float scalar = 255.0f / ((float) ((TYPE_COLORMAPPED << bitsPerColor) - 1));
                float alphaScalar = 255.0f / ((float) ((TYPE_COLORMAPPED << alphaSize) - 1));
                for (short i = (short) 0; i < cMapLength; i += TYPE_COLORMAPPED) {
                    entry = new ColorMapEntry();
                    int offset = cMapDepth * i;
                    entry.red = (byte) ((int) (((float) getBitsAsByte(cMapData, offset, bitsPerColor)) * scalar));
                    entry.green = (byte) ((int) (((float) getBitsAsByte(cMapData, offset + bitsPerColor, bitsPerColor)) * scalar));
                    entry.blue = (byte) ((int) (((float) getBitsAsByte(cMapData, (bitsPerColor * TYPE_TRUECOLOR) + offset, bitsPerColor)) * scalar));
                    if (alphaSize <= 0) {
                        entry.alpha = (byte) -1;
                    } else {
                        entry.alpha = (byte) ((int) (((float) getBitsAsByte(cMapData, (bitsPerColor * TYPE_BLACKANDWHITE) + offset, alphaSize)) * alphaScalar));
                    }
                    cMapEntries[i] = entry;
                }
            }
        }
        if (pixelDepth == 32) {
            int[] rawData = new int[(width * height)];
            int rawDataIndex = TYPE_NO_IMAGE;
            int i2;
            if (imageType == TYPE_TRUECOLOR) {
                if (pixelDepth == 32) {
                    ByteBuffer buf = ByteBuffer.allocate(width * RGBA8).order(ByteOrder.LITTLE_ENDIAN);
                    IntBuffer intb = buf.asIntBuffer();
                    byte[] bufBytes = buf.array();
                    for (i2 = TYPE_NO_IMAGE; i2 <= height - 1; i2 += TYPE_COLORMAPPED) {
                        if (!flip) {
                            rawDataIndex = (((height - 1) - i2) * width) * TYPE_COLORMAPPED;
                        }
                        dataInputStream.read(bufBytes, TYPE_NO_IMAGE, bufBytes.length);
                        intb.position(TYPE_NO_IMAGE);
                        intb.get(rawData, rawDataIndex, width);
                        rawDataIndex += width;
                    }
                } else {
                    throw new IOException("Unsupported TGA true color depth: " + pixelDepth);
                }
            } else if (imageType == TYPE_TRUECOLOR_RLE) {
                if (pixelDepth == 32) {
                    for (i2 = TYPE_NO_IMAGE; i2 <= height - 1; i2 += TYPE_COLORMAPPED) {
                        if (!flip) {
                            rawDataIndex = (((height - 1) - i2) * width) * TYPE_COLORMAPPED;
                        }
                        j = TYPE_NO_IMAGE;
                        while (j < width) {
                            int count = dataInputStream.readByte();
                            int count2;
                            byte blue;
                            if ((count & Token.RESERVED) == 0) {
                                j += count;
                                count2 = count;
                                rawDataIndex = rawDataIndex;
                                while (true) {
                                    count = count2 - 1;
                                    if (count2 < 0) {
                                        break;
                                    }
                                    blue = dataInputStream.readByte();
                                    rawDataIndex = rawDataIndex + TYPE_COLORMAPPED;
                                    rawData[rawDataIndex] = ((((dataInputStream.readByte() & 255) << 24) | ((dataInputStream.readByte() & 255) << 16)) | ((dataInputStream.readByte() & 255) << 8)) | (blue & 255);
                                    count2 = count;
                                    rawDataIndex = rawDataIndex;
                                }
                            } else {
                                count &= Token.VOID;
                                j += count;
                                blue = dataInputStream.readByte();
                                byte green = dataInputStream.readByte();
                                byte red = dataInputStream.readByte();
                                byte alpha = dataInputStream.readByte();
                                count2 = count;
                                rawDataIndex = rawDataIndex;
                                while (true) {
                                    count = count2 - 1;
                                    if (count2 < 0) {
                                        break;
                                    }
                                    rawDataIndex = rawDataIndex + TYPE_COLORMAPPED;
                                    rawData[rawDataIndex] = ((((alpha & 255) << 24) | ((red & 255) << 16)) | ((green & 255) << 8)) | (blue & 255);
                                    count2 = count;
                                    rawDataIndex = rawDataIndex;
                                }
                            }
                            rawDataIndex = rawDataIndex;
                            j += TYPE_COLORMAPPED;
                        }
                    }
                } else {
                    throw new IOException("Unsupported TGA true color depth: " + pixelDepth);
                }
            } else if (imageType == TYPE_COLORMAPPED) {
                int bytesPerIndex = pixelDepth / 8;
                int index;
                if (bytesPerIndex == TYPE_COLORMAPPED) {
                    i2 = TYPE_NO_IMAGE;
                    while (i2 <= height - 1) {
                        if (!flip) {
                            rawDataIndex = (((height - 1) - i2) * width) * TYPE_COLORMAPPED;
                        }
                        j = TYPE_NO_IMAGE;
                        rawDataIndex = rawDataIndex;
                        while (j < width) {
                            index = dataInputStream.readUnsignedByte();
                            if (index >= cMapEntries.length || index < 0) {
                                throw new IOException("TGA: Invalid color map entry referenced: " + index);
                            }
                            entry = cMapEntries[index];
                            rawDataIndex = rawDataIndex + TYPE_COLORMAPPED;
                            rawData[rawDataIndex] = ((((entry.alpha & 255) << 24) | ((entry.red & 255) << 16)) | ((entry.green & 255) << 8)) | (entry.blue & 255);
                            j += TYPE_COLORMAPPED;
                            rawDataIndex = rawDataIndex;
                        }
                        i2 += TYPE_COLORMAPPED;
                        rawDataIndex = rawDataIndex;
                    }
                } else if (bytesPerIndex == TYPE_TRUECOLOR) {
                    i2 = TYPE_NO_IMAGE;
                    while (i2 <= height - 1) {
                        if (!flip) {
                            rawDataIndex = (((height - 1) - i2) * width) * TYPE_COLORMAPPED;
                        }
                        j = TYPE_NO_IMAGE;
                        rawDataIndex = rawDataIndex;
                        while (j < width) {
                            index = flipEndian(dataInputStream.readShort());
                            if (index >= cMapEntries.length || index < 0) {
                                throw new IOException("TGA: Invalid color map entry referenced: " + index);
                            }
                            entry = cMapEntries[index];
                            rawDataIndex = rawDataIndex + TYPE_COLORMAPPED;
                            rawData[rawDataIndex] = ((((entry.alpha & 255) << 24) | ((entry.red & 255) << 16)) | ((entry.green & 255) << 8)) | (entry.blue & 255);
                            j += TYPE_COLORMAPPED;
                            rawDataIndex = rawDataIndex;
                        }
                        i2 += TYPE_COLORMAPPED;
                        rawDataIndex = rawDataIndex;
                    }
                } else {
                    throw new IOException("TGA: unknown colormap indexing size used: " + bytesPerIndex);
                }
            } else {
                throw new IOException("Monochrome and RLE colormapped images are not supported");
            }
            return Bitmap.createBitmap(rawData, width, height, Config.ARGB_8888);
        }
        throw new RuntimeException("Only 32-bit color TGAs are supported");
    }

    private static byte getBitsAsByte(byte[] data, int offset, int length) {
        int offsetBytes = offset / 8;
        int indexBits = offset % 8;
        int rVal = TYPE_NO_IMAGE;
        int i = length;
        while (true) {
            i--;
            if (i < 0) {
                return (byte) rVal;
            }
            if ((data[offsetBytes] & (indexBits == 7 ? TYPE_COLORMAPPED : TYPE_TRUECOLOR << (6 - indexBits))) != 0) {
                if (i == 0) {
                    rVal += TYPE_COLORMAPPED;
                } else {
                    rVal += TYPE_TRUECOLOR << (i - 1);
                }
            }
            indexBits += TYPE_COLORMAPPED;
            if (indexBits == 8) {
                indexBits = TYPE_NO_IMAGE;
                offsetBytes += TYPE_COLORMAPPED;
            }
        }
    }

    private static short flipEndian(short signedShort) {
        int input = signedShort & 65535;
        return (short) ((input << 8) | ((65280 & input) >>> 8));
    }
}
