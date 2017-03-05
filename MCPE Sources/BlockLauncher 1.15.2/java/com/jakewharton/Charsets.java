package com.jakewharton;

import java.nio.charset.Charset;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

class Charsets {
    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET);

    Charsets() {
    }
}
