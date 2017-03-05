package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Strings;
import java.nio.ByteBuffer;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SharedKeyGenerator {
    static final int NonceLengthBytes = 32;
    private final byte[] _sessionKey;

    public enum KeyPurpose {
        CredentialSignature(SharedKeyGenerator.NonceLengthBytes, "WS-SecureConversation"),
        STSDigest(SharedKeyGenerator.NonceLengthBytes, "WS-SecureConversationWS-SecureConversation");
        
        private final int _keyLengthBytes;
        private final String _label;

        private KeyPurpose(int i, String str) {
            this._keyLengthBytes = i;
            this._label = str;
        }

        int getKeyLengthBytes() {
            return this._keyLengthBytes;
        }

        String getLabel() {
            return this._label;
        }
    }

    public SharedKeyGenerator(byte[] bArr) {
        this._sessionKey = bArr;
    }

    static byte[] deriveSP800108HmacSHA256Key(int i, byte[] bArr, String str, byte[] bArr2) {
        ByteBuffer allocate = ByteBuffer.allocate(i);
        ByteBuffer allocate2 = ByteBuffer.allocate(4);
        Mac initializedHmacSha256Digester = Cryptography.getInitializedHmacSha256Digester(new SecretKeySpec(bArr, Cryptography.HmacSha256Algorithm));
        int i2 = 1;
        while (allocate.position() < i) {
            initializedHmacSha256Digester.reset();
            allocate2.clear();
            allocate2.putInt(i2);
            allocate2.rewind();
            initializedHmacSha256Digester.update(allocate2);
            initializedHmacSha256Digester.update(str.getBytes(Strings.Utf8Charset));
            initializedHmacSha256Digester.update((byte) 0);
            initializedHmacSha256Digester.update(bArr2);
            allocate2.clear();
            allocate2.putInt(i * 8);
            allocate2.rewind();
            initializedHmacSha256Digester.update(allocate2);
            byte[] doFinal = initializedHmacSha256Digester.doFinal();
            int length = doFinal.length;
            if (length > allocate.remaining()) {
                length = allocate.remaining();
            }
            allocate.put(doFinal, 0, length);
            i2++;
        }
        return allocate.array();
    }

    public byte[] generateKey(KeyPurpose keyPurpose, byte[] bArr) {
        return deriveSP800108HmacSHA256Key(keyPurpose.getKeyLengthBytes(), this._sessionKey, keyPurpose.getLabel(), bArr);
    }
}
