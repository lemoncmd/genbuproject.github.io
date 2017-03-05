package com.microsoft.xbox.service.network.managers;

import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import java.net.URI;
import org.apache.http.client.methods.HttpPost;

public class HttpDeleteWithRequestBody extends HttpPost {
    public HttpDeleteWithRequestBody(URI uri) {
        super(uri);
    }

    public String getMethod() {
        return HttpEngine.DELETE;
    }
}
