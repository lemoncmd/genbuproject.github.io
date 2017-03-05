package com.microsoft.xbox.toolkit.network;

import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpClientFactory {
    private static final int CONNECTION_PER_ROUTE = 16;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 40;
    private static final int MAX_TOTAL_CONNECTIONS = 32;
    public static HttpClientFactory networkOperationsFactory = new HttpClientFactory();
    public static HttpClientFactory noRedirectNetworkOperationsFactory = new HttpClientFactory(false);
    public static HttpClientFactory textureFactory = new HttpClientFactory(true);
    private AbstractXLEHttpClient client;
    private AbstractXLEHttpClient clientWithTimeoutOverride;
    private ClientConnectionManager connectionManager;
    private Object httpSyncObject;
    private HttpParams params;

    public HttpClientFactory() {
        this(false);
    }

    public HttpClientFactory(boolean z) {
        this.connectionManager = null;
        this.httpSyncObject = new Object();
        this.client = null;
        this.clientWithTimeoutOverride = null;
        this.params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        HttpProtocolParams.setVersion(this.params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(this.params, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(this.params, false);
        HttpClientParams.setRedirecting(this.params, z);
        if (XboxLiveEnvironment.Instance().getProxyEnabled()) {
            this.params.setParameter("http.route.default-proxy", new HttpHost("itgproxy.redmond.corp.microsoft.com", 80));
        }
        HttpConnectionParams.setConnectionTimeout(this.params, 40000);
        HttpConnectionParams.setSoTimeout(this.params, 40000);
        HttpConnectionParams.setSocketBufferSize(this.params, 8192);
        ConnManagerParams.setMaxConnectionsPerRoute(this.params, new ConnPerRouteBean(CONNECTION_PER_ROUTE));
        ConnManagerParams.setMaxTotalConnections(this.params, MAX_TOTAL_CONNECTIONS);
        this.connectionManager = new ThreadSafeClientConnManager(this.params, schemeRegistry);
    }

    public ClientConnectionManager getClientConnectionManager() {
        return this.connectionManager;
    }

    public AbstractXLEHttpClient getHttpClient(int i) {
        AbstractXLEHttpClient abstractXLEHttpClient;
        synchronized (this.httpSyncObject) {
            if (i <= 0) {
                if (this.client == null) {
                    this.client = new XLEHttpClient(this.connectionManager, this.params);
                }
                abstractXLEHttpClient = this.client;
            } else if (this.clientWithTimeoutOverride == null) {
                HttpParams copy = this.params.copy();
                HttpConnectionParams.setConnectionTimeout(copy, i * 1000);
                HttpConnectionParams.setSoTimeout(copy, i * 1000);
                abstractXLEHttpClient = new XLEHttpClient(this.connectionManager, copy);
            } else {
                abstractXLEHttpClient = this.clientWithTimeoutOverride;
            }
        }
        return abstractXLEHttpClient;
    }

    public HttpParams getHttpParams() {
        return this.params;
    }

    public void setHttpClient(AbstractXLEHttpClient abstractXLEHttpClient) {
    }
}
