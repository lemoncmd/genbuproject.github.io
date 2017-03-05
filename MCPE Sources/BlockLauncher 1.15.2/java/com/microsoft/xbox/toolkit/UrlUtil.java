package com.microsoft.xbox.toolkit;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtil {
    public static boolean UrisEqualCaseInsensitive(URI uri, URI uri2) {
        return uri == uri2 ? true : (uri == null || uri2 == null) ? false : JavaUtil.stringsEqualCaseInsensitive(uri.toString(), uri2.toString());
    }

    public static String encodeUrl(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        URI encodedUri = getEncodedUri(str);
        return encodedUri != null ? encodedUri.toString() : null;
    }

    public static URI getEncodedUri(String str) {
        return (str == null || str.length() == 0) ? null : getEncodedUriNonNull(str);
    }

    public static URI getEncodedUriNonNull(String str) {
        try {
            URL url = new URL(str);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException e) {
            return null;
        } catch (MalformedURLException e2) {
            return null;
        }
    }

    public static URI getUri(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            return null;
        }
        try {
            return new URI(str);
        } catch (Exception e) {
            return null;
        }
    }
}
