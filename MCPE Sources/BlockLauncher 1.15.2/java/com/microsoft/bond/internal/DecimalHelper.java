package com.microsoft.bond.internal;

public final class DecimalHelper {
    public static final int SIZEOF_DOUBLE = 8;
    public static final int SIZEOF_FLOAT = 4;

    private DecimalHelper() {
    }

    public static double decodeDouble(byte[] bArr) {
        return Double.longBitsToDouble((((((((((long) bArr[0]) & 255) | ((((long) bArr[1]) & 255) << SIZEOF_DOUBLE)) | ((((long) bArr[2]) & 255) << 16)) | ((((long) bArr[3]) & 255) << 24)) | ((((long) bArr[SIZEOF_FLOAT]) & 255) << 32)) | ((((long) bArr[5]) & 255) << 40)) | ((((long) bArr[6]) & 255) << 48)) | ((((long) bArr[7]) & 255) << 56));
    }

    public static float decodeFloat(byte[] bArr) {
        return Float.intBitsToFloat((((bArr[0] & 255) | ((bArr[1] & 255) << SIZEOF_DOUBLE)) | ((bArr[2] & 255) << 16)) | ((bArr[3] & 255) << 24));
    }

    public static void encodeDouble(double d, byte[] bArr) {
        long doubleToRawLongBits = Double.doubleToRawLongBits(d);
        bArr[0] = (byte) ((int) doubleToRawLongBits);
        bArr[1] = (byte) ((int) (doubleToRawLongBits >> SIZEOF_DOUBLE));
        bArr[2] = (byte) ((int) (doubleToRawLongBits >> 16));
        bArr[3] = (byte) ((int) (doubleToRawLongBits >> 24));
        bArr[SIZEOF_FLOAT] = (byte) ((int) (doubleToRawLongBits >> 32));
        bArr[5] = (byte) ((int) (doubleToRawLongBits >> 40));
        bArr[6] = (byte) ((int) (doubleToRawLongBits >> 48));
        bArr[7] = (byte) ((int) (doubleToRawLongBits >> 56));
    }

    public static void encodeFloat(float f, byte[] bArr) {
        int floatToRawIntBits = Float.floatToRawIntBits(f);
        bArr[0] = (byte) floatToRawIntBits;
        bArr[1] = (byte) (floatToRawIntBits >> SIZEOF_DOUBLE);
        bArr[2] = (byte) (floatToRawIntBits >> 16);
        bArr[3] = (byte) (floatToRawIntBits >> 24);
    }
}
