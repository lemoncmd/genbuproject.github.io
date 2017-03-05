package net.zhuoweizhang.mcpelauncher;

import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustModifier {
    private static final TrustingHostnameVerifier TRUSTING_HOSTNAME_VERIFIER = new TrustingHostnameVerifier();
    private static SSLSocketFactory factory;

    private static class AlwaysTrustManager implements X509TrustManager {
        private AlwaysTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static final class TrustingHostnameVerifier implements HostnameVerifier {
        private TrustingHostnameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static void relaxHostChecking(HttpURLConnection conn) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) conn;
            httpsConnection.setSSLSocketFactory(prepFactory(httpsConnection));
            httpsConnection.setHostnameVerifier(TRUSTING_HOSTNAME_VERIFIER);
        }
    }

    static synchronized SSLSocketFactory prepFactory(HttpsURLConnection httpsConnection) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLSocketFactory sSLSocketFactory;
        synchronized (TrustModifier.class) {
            if (factory == null) {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[]{new AlwaysTrustManager()}, null);
                factory = ctx.getSocketFactory();
            }
            sSLSocketFactory = factory;
        }
        return sSLSocketFactory;
    }
}
