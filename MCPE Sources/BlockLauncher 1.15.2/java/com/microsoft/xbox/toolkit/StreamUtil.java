package com.microsoft.xbox.toolkit;

import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamUtil {
    public static void CopyStream(OutputStream outputStream, InputStream inputStream) throws IOException {
        byte[] bArr = new byte[16384];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                outputStream.write(bArr, 0, read);
            } else {
                outputStream.flush();
                return;
            }
        }
    }

    public static byte[] CreateByteArray(InputStream inputStream) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            CopyStream(byteArrayOutputStream, inputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] HexStringToByteArray(String str) {
        int i = 0;
        if (str == null) {
            throw new IllegalArgumentException("hexString invalid");
        }
        if (str.length() % 2 != 0) {
            str = MigrationManager.InitialSdkVersion + str;
        }
        XLEAssert.assertTrue(str.length() % 2 == 0);
        byte[] bArr = new byte[(str.length() / 2)];
        while (i < str.length()) {
            bArr[i / 2] = Byte.parseByte(str.substring(i, i + 2), 16);
            i += 2;
        }
        return bArr;
    }

    public static String ReadAsString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return stringBuilder.toString();
                }
                stringBuilder.append(readLine);
                stringBuilder.append('\n');
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static void consumeAndClose(InputStream inputStream) throws IOException {
        InputStream bufferedInputStream = new BufferedInputStream(inputStream);
        while (true) {
            try {
                if (bufferedInputStream.read() == -1) {
                    break;
                }
            } finally {
                bufferedInputStream.close();
            }
        }
    }
}
