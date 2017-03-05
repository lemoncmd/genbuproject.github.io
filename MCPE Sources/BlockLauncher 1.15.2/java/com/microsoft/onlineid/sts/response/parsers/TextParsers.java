package com.microsoft.onlineid.sts.response.parsers;

import android.util.Base64;
import com.microsoft.onlineid.internal.Integers;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.net.URL;

class TextParsers {
    TextParsers() {
    }

    static byte[] parseBase64(String str) throws StsParseException {
        try {
            return Base64.decode(str, 2);
        } catch (Throwable e) {
            throw new StsParseException(e);
        } catch (Throwable e2) {
            throw new StsParseException(e2);
        }
    }

    static int parseInt(String str, String str2) throws StsParseException {
        try {
            return Integer.parseInt(str);
        } catch (Throwable e) {
            throw new StsParseException(str2, e, new Object[0]);
        }
    }

    static int parseIntHex(String str) throws StsParseException {
        try {
            return Integers.parseIntHex(str);
        } catch (Throwable e) {
            throw new StsParseException(e);
        }
    }

    static URL parseUrl(String str, String str2) throws StsParseException {
        try {
            return new URL(str);
        } catch (Throwable e) {
            throw new StsParseException(str2, e, new Object[0]);
        }
    }
}
