package com.microsoft.onlineid.internal.transport;

import android.content.Context;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;

public class TransportFactory {
    private final Context _applicationContext;

    public TransportFactory(Context context) {
        this._applicationContext = context;
    }

    protected void configureTransport(Transport transport) {
        ServerConfig serverConfig = getServerConfig();
        transport.setConnectionTimeoutMilliseconds(serverConfig.getInt(Int.ConnectTimeout));
        transport.setReadTimeoutMilliseconds(serverConfig.getInt(Int.ReceiveTimeout));
        transport.appendCustomUserAgentString(Transport.buildUserAgentString(this._applicationContext));
    }

    public Transport createTransport() {
        Transport transport = new Transport();
        configureTransport(transport);
        return transport;
    }

    protected ServerConfig getServerConfig() {
        return new ServerConfig(this._applicationContext);
    }
}
