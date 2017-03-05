package com.microsoft.xbox.toolkit.network;

import java.io.InputStream;
import org.apache.http.Header;

public class XLEHttpStatusAndStream {
    public Header[] headers = new Header[0];
    public String redirectUrl = null;
    public int statusCode = -1;
    public String statusLine = null;
    public InputStream stream = null;

    public void close() {
        if (this.stream != null) {
            try {
                this.stream.close();
                this.stream = null;
            } catch (Exception e) {
            }
        }
    }
}
