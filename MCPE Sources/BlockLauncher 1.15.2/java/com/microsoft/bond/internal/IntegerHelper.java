package com.microsoft.bond.internal;

import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;
import org.mozilla.javascript.Token;

public final class IntegerHelper {
    public static final int MAX_BYTES_VARINT16 = 3;
    public static final int MAX_BYTES_VARINT32 = 5;
    public static final int MAX_BYTES_VARINT64 = 10;
    public static final int MAX_VARINT_SIZE_BYTES = 10;
    public static final int SIZEOF_BYTE = 1;
    public static final int SIZEOF_INT = 4;
    public static final int SIZEOF_LONG = 8;
    public static final int SIZEOF_SHORT = 2;

    private IntegerHelper() {
    }

    public static short decodeVarInt16(BondInputStream bondInputStream) throws IOException {
        return (short) ((int) decodeVarInt64(bondInputStream));
    }

    public static int decodeVarInt32(BondInputStream bondInputStream) throws IOException {
        return (int) decodeVarInt64(bondInputStream);
    }

    public static long decodeVarInt64(BondInputStream bondInputStream) throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte read = bondInputStream.read();
            j |= ((long) (read & Token.VOID)) << i;
            if ((read & Token.RESERVED) == 0) {
                break;
            }
        }
        return j;
    }

    public static short decodeZigzag16(short s) {
        return ((65535 & s) >>> SIZEOF_BYTE) ^ (-(s & SIZEOF_BYTE));
    }

    public static int decodeZigzag32(int i) {
        return (i >>> SIZEOF_BYTE) ^ (-(i & SIZEOF_BYTE));
    }

    public static long decodeZigzag64(long j) {
        return (j >>> SIZEOF_BYTE) ^ (-(1 & j));
    }

    public static int encodeVarUInt16(short s, byte[] bArr, int i) {
        int i2;
        int i3;
        if ((65408 & s) != 0) {
            i2 = i + SIZEOF_BYTE;
            bArr[i] = (byte) ((s & Token.VOID) | Token.RESERVED);
            i3 = s >>> 7;
            if ((65408 & i3) != 0) {
                i = i2 + SIZEOF_BYTE;
                bArr[i2] = (byte) ((i3 & Token.VOID) | Token.RESERVED);
                i3 >>>= 7;
            } else {
                i = i2;
            }
        }
        i2 = i + SIZEOF_BYTE;
        bArr[i] = (byte) (i3 & Token.VOID);
        return i2;
    }

    public static int encodeVarUInt16(byte[] bArr, short s) {
        return encodeVarUInt16(s, bArr, 0);
    }

    public static int encodeVarUInt32(int i, byte[] bArr) {
        return encodeVarUInt32(i, bArr, 0);
    }

    public static int encodeVarUInt32(int i, byte[] bArr, int i2) {
        int i3;
        if ((i & -128) != 0) {
            i3 = i2 + SIZEOF_BYTE;
            bArr[i2] = (byte) ((i & Token.VOID) | Token.RESERVED);
            i >>>= 7;
            if ((i & -128) != 0) {
                i2 = i3 + SIZEOF_BYTE;
                bArr[i3] = (byte) ((i & Token.VOID) | Token.RESERVED);
                i >>>= 7;
                if ((i & -128) != 0) {
                    i3 = i2 + SIZEOF_BYTE;
                    bArr[i2] = (byte) ((i & Token.VOID) | Token.RESERVED);
                    i >>>= 7;
                    if ((i & -128) != 0) {
                        i2 = i3 + SIZEOF_BYTE;
                        bArr[i3] = (byte) ((i & Token.VOID) | Token.RESERVED);
                        i >>>= 7;
                    }
                }
            }
            i2 = i3;
        }
        i3 = i2 + SIZEOF_BYTE;
        bArr[i2] = (byte) (i & Token.VOID);
        return i3;
    }

    public static int encodeVarUInt64(long j, byte[] bArr) {
        return encodeVarUInt64(j, bArr, 0);
    }

    public static int encodeVarUInt64(long j, byte[] bArr, int i) {
        int i2;
        if ((-128 & j) != 0) {
            i2 = i + SIZEOF_BYTE;
            bArr[i] = (byte) ((int) (128 | (127 & j)));
            j >>>= 7;
            if ((-128 & j) != 0) {
                i = i2 + SIZEOF_BYTE;
                bArr[i2] = (byte) ((int) (128 | (127 & j)));
                j >>>= 7;
                if ((-128 & j) != 0) {
                    i2 = i + SIZEOF_BYTE;
                    bArr[i] = (byte) ((int) (128 | (127 & j)));
                    j >>>= 7;
                    if ((-128 & j) != 0) {
                        i = i2 + SIZEOF_BYTE;
                        bArr[i2] = (byte) ((int) (128 | (127 & j)));
                        j >>>= 7;
                        if ((-128 & j) != 0) {
                            i2 = i + SIZEOF_BYTE;
                            bArr[i] = (byte) ((int) (128 | (127 & j)));
                            j >>>= 7;
                            if ((-128 & j) != 0) {
                                i = i2 + SIZEOF_BYTE;
                                bArr[i2] = (byte) ((int) (128 | (127 & j)));
                                j >>>= 7;
                                if ((-128 & j) != 0) {
                                    int i3 = i + SIZEOF_BYTE;
                                    bArr[i] = (byte) ((int) (128 | (127 & j)));
                                    j >>>= 7;
                                    if ((-128 & j) != 0) {
                                        i2 = i3 + SIZEOF_BYTE;
                                        bArr[i3] = (byte) ((int) (128 | (127 & j)));
                                        j >>>= 7;
                                        if ((-128 & j) != 0) {
                                            i = i2 + SIZEOF_BYTE;
                                            bArr[i2] = (byte) ((int) (128 | (127 & j)));
                                            j >>>= 7;
                                        }
                                    } else {
                                        i = i3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            i = i2;
        }
        i2 = i + SIZEOF_BYTE;
        bArr[i] = (byte) ((int) (127 & j));
        return i2;
    }

    public static short encodeZigzag16(short s) {
        return (s << SIZEOF_BYTE) ^ (s >> 15);
    }

    public static int encodeZigzag32(int i) {
        return (i << SIZEOF_BYTE) ^ (i >> 31);
    }

    public static long encodeZigzag64(long j) {
        return (j << SIZEOF_BYTE) ^ (j >> 63);
    }

    public static int getVarUInt16Size(short s) {
        return (65408 & s) != 0 ? (49152 & s) != 0 ? MAX_BYTES_VARINT16 : SIZEOF_SHORT : SIZEOF_BYTE;
    }

    public static int getVarUInt32Size(int i) {
        return (-2097152 & i) != 0 ? (-268435456 & i) != 0 ? MAX_BYTES_VARINT32 : SIZEOF_INT : (i & -128) != 0 ? (i & -16384) != 0 ? MAX_BYTES_VARINT16 : SIZEOF_SHORT : SIZEOF_BYTE;
    }

    public static int getVarUInt64Size(long j) {
        return 0 != (-34359738368L & j) ? 0 != (-562949953421312L & j) ? 0 != (-72057594037927936L & j) ? 0 != (Long.MIN_VALUE & j) ? MAX_VARINT_SIZE_BYTES : 9 : SIZEOF_LONG : 0 != (-4398046511104L & j) ? 7 : 6 : 0 != (-2097152 & j) ? 0 != (-268435456 & j) ? MAX_BYTES_VARINT32 : SIZEOF_INT : 0 != (-128 & j) ? 0 != (-16384 & j) ? MAX_BYTES_VARINT16 : SIZEOF_SHORT : SIZEOF_BYTE;
    }

    public static int readBigEndianInt32(byte[] bArr) {
        return (((bArr[MAX_BYTES_VARINT16] & 255) | ((bArr[SIZEOF_SHORT] & 255) << SIZEOF_LONG)) | ((bArr[SIZEOF_BYTE] & 255) << 16)) | ((bArr[0] & 255) << 24);
    }

    public static void writeBigEndianInt32(int i, byte[] bArr) {
        bArr[MAX_BYTES_VARINT16] = (byte) i;
        bArr[SIZEOF_SHORT] = (byte) (i >> SIZEOF_LONG);
        bArr[SIZEOF_BYTE] = (byte) (i >> 16);
        bArr[0] = (byte) (i >> 24);
    }
}
