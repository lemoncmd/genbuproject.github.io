package com.microsoft.onlineid.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class Strings {
    public static final Charset Utf8Charset = Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET);

    public static boolean equalsIgnoreCase(String str, String str2) {
        return str == str2 ? true : (str == null || str2 == null) ? false : str.equalsIgnoreCase(str2);
    }

    public static String fromStream(InputStream inputStream, Charset charset) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        StringBuilder stringBuilder = new StringBuilder();
        char[] cArr = new char[EnchantType.pickaxe];
        while (true) {
            try {
                int read = inputStreamReader.read(cArr);
                if (read < 0) {
                    break;
                }
                stringBuilder.append(cArr, 0, read);
            } finally {
                inputStreamReader.close();
            }
        }
        return stringBuilder.toString();
    }

    public static String pluralize(long j, String str, String str2) {
        StringBuilder append = new StringBuilder().append(j).append(" ");
        if (j == 1) {
            str2 = str;
        }
        return append.append(str2).toString();
    }

    public static void verifyArgumentNotNullOrEmpty(String str, String str2) {
        Objects.verifyArgumentNotNull(str, str2);
        if (str.isEmpty()) {
            throw new IllegalArgumentException(str2 + " must not be empty.");
        }
    }
}
