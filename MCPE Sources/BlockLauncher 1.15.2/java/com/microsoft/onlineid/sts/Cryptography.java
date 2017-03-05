package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Assertion;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {
    public static final String AesAlgorithm = "AES";
    public static final int AesCbcPkcs5PaddingInitializationVectorByteCount = 16;
    public static final String AesCbcPkcs5PaddingTransformation = "AES/CBC/PKCS5Padding";
    private static final String ByteToBase32Lookup = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    public static final String HmacSha1Algorithm = "HmacSHA1";
    public static final String HmacSha256Algorithm = "HmacSHA256";
    public static final String Sha256Algorithm = "SHA256";

    public static byte[] decryptWithAesCbcPcs5PaddingCipher(byte[] bArr, byte[] bArr2) throws IllegalBlockSizeException, BadPaddingException {
        return getInitializedDecryptionCipher(AesCbcPkcs5PaddingTransformation, new SecretKeySpec(bArr2, AesAlgorithm), new IvParameterSpec(bArr, 0, AesCbcPkcs5PaddingInitializationVectorByteCount)).doFinal(bArr, AesCbcPkcs5PaddingInitializationVectorByteCount, bArr.length - 16);
    }

    public static String encodeBase32(byte[] bArr) {
        int i = 0;
        Assertion.check(bArr != null);
        StringBuilder stringBuilder = new StringBuilder(((bArr.length * 8) / 5) + 1);
        while (i < bArr.length) {
            Object obj = new byte[8];
            int min = Math.min(bArr.length - i, 5);
            System.arraycopy(bArr, i, obj, (obj.length - min) - 1, min);
            long j = ByteBuffer.wrap(obj).getLong();
            for (int i2 = ((min + 1) * 8) - 5; i2 > 3; i2 -= 5) {
                stringBuilder.append(ByteToBase32Lookup.charAt((int) (31 & (j >>> i2))));
            }
            i += 5;
        }
        return stringBuilder.toString();
    }

    private static Cipher getInitializedDecryptionCipher(String str, Key key, IvParameterSpec ivParameterSpec) {
        try {
            Cipher instance = Cipher.getInstance(str);
            instance.init(2, key, ivParameterSpec);
            return instance;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        } catch (Throwable e22) {
            throw new RuntimeException(e22);
        } catch (Throwable e222) {
            throw new RuntimeException(e222);
        }
    }

    private static Mac getInitializedHmacDigester(Key key, String str) {
        try {
            Mac instance = Mac.getInstance(str);
            instance.init(key);
            return instance;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public static Mac getInitializedHmacSha1Digester(Key key) {
        return getInitializedHmacDigester(key, HmacSha1Algorithm);
    }

    public static Mac getInitializedHmacSha256Digester(Key key) {
        return getInitializedHmacDigester(key, HmacSha256Algorithm);
    }

    public static MessageDigest getSha256Digester() {
        try {
            return MessageDigest.getInstance(Sha256Algorithm);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageDigest getShaDigester() {
        try {
            return MessageDigest.getInstance("SHA");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
