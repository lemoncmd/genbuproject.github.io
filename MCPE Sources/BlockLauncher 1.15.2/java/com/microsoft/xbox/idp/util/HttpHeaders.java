package com.microsoft.xbox.idp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpHeaders {
    private final List<Header> headers = new ArrayList();

    public static class Header {
        private final String key;
        private final String value;

        public Header(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ ").append("\"").append(this.key).append("\": ").append("\"").append(this.value).append("\"").append(" }");
            return stringBuilder.toString();
        }
    }

    public void add(String str, String str2) {
        this.headers.add(new Header(str, str2));
    }

    public Collection<Header> getAllHeaders() {
        return this.headers;
    }

    public Header getFirstHeader(String str) {
        if (str != null) {
            for (Header header : this.headers) {
                if (str.equals(header.key)) {
                    return header;
                }
            }
        }
        return null;
    }

    public Header getLastHeader(String str) {
        if (str != null) {
            for (int size = this.headers.size() - 1; size >= 0; size--) {
                Header header = (Header) this.headers.get(size);
                if (str.equals(header.key)) {
                    return header;
                }
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ ");
        for (Header append : this.headers) {
            stringBuilder.append(append);
        }
        stringBuilder.append(" ]");
        return stringBuilder.toString();
    }
}
