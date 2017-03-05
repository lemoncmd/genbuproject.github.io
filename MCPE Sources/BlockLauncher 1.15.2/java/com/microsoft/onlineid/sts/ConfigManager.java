package com.microsoft.onlineid.sts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.internal.configuration.Environment;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.ServerConfig.Editor;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.ConfigParser;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConfigManager {
    private final Context _applicationContext;
    private ServerConfig _config;
    private TypedStorage _storage;

    public ConfigManager(Context context) {
        this._applicationContext = context;
    }

    static long compareVersions(String str, String str2) {
        String[] split = TextUtils.isEmpty(str) ? new String[0] : str.split("\\.");
        String[] split2 = TextUtils.isEmpty(str2) ? new String[0] : str2.split("\\.");
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i >= split.length && i >= split2.length) {
                break;
            }
            i2 = (i < split.length ? Integer.parseInt(split[i]) : 0) - (i < split2.length ? Integer.parseInt(split2[i]) : 0);
            if (i2 != 0) {
                break;
            }
            i++;
        }
        return (long) i2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean downloadConfiguration(com.microsoft.onlineid.internal.configuration.Environment r6) {
        /*
        r5 = this;
        r0 = 0;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Downloading new PPCRL config file (";
        r1 = r1.append(r2);
        r1 = r1.append(r6);
        r2 = ").";
        r1 = r1.append(r2);
        r1 = r1.toString();
        com.microsoft.onlineid.internal.log.Logger.info(r1);
        r1 = r5.getTransportFactory();
        r2 = r1.createTransport();
        r1 = r6.getConfigUrl();	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r2.openGetRequest(r1);	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r1 = r2.getResponseCode();	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r3 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r1 != r3) goto L_0x0063;
    L_0x0034:
        r1 = r2.getResponseStream();	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r0 = r5.parseConfig(r1, r6);	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
    L_0x003c:
        r2.closeConnection();
    L_0x003f:
        if (r0 == 0) goto L_0x00c3;
    L_0x0041:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Successfully updated ppcrlconfig to version: ";
        r1 = r1.append(r2);
        r2 = r5.getCurrentConfigVersion();
        r1 = r1.append(r2);
        r1 = r1.toString();
        com.microsoft.onlineid.internal.log.Logger.info(r1);
        r1 = r5.getStorage();
        r1.writeConfigLastDownloadedTime();
    L_0x0062:
        return r0;
    L_0x0063:
        r3 = new java.lang.StringBuilder;	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r3.<init>();	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r4 = "Failed to update ppcrlconfig due to HTTP response code ";
        r3 = r3.append(r4);	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r1 = r3.append(r1);	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        r1 = r1.toString();	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        com.microsoft.onlineid.internal.log.Logger.error(r1);	 Catch:{ NetworkException -> 0x007a, IOException -> 0x008b, XmlPullParserException -> 0x009c, StsParseException -> 0x00ad }
        goto L_0x003c;
    L_0x007a:
        r1 = move-exception;
        r3 = "Failed to update ppcrlconfig.";
        com.microsoft.onlineid.internal.log.Logger.error(r3, r1);	 Catch:{ all -> 0x00be }
        r3 = com.microsoft.onlineid.analytics.ClientAnalytics.get();	 Catch:{ all -> 0x00be }
        r3.logException(r1);	 Catch:{ all -> 0x00be }
        r2.closeConnection();
        goto L_0x003f;
    L_0x008b:
        r1 = move-exception;
        r3 = "Failed to update ppcrlconfig.";
        com.microsoft.onlineid.internal.log.Logger.error(r3, r1);	 Catch:{ all -> 0x00be }
        r3 = com.microsoft.onlineid.analytics.ClientAnalytics.get();	 Catch:{ all -> 0x00be }
        r3.logException(r1);	 Catch:{ all -> 0x00be }
        r2.closeConnection();
        goto L_0x003f;
    L_0x009c:
        r1 = move-exception;
        r3 = "Failed to update ppcrlconfig.";
        com.microsoft.onlineid.internal.log.Logger.error(r3, r1);	 Catch:{ all -> 0x00be }
        r3 = com.microsoft.onlineid.analytics.ClientAnalytics.get();	 Catch:{ all -> 0x00be }
        r3.logException(r1);	 Catch:{ all -> 0x00be }
        r2.closeConnection();
        goto L_0x003f;
    L_0x00ad:
        r1 = move-exception;
        r3 = "Failed to update ppcrlconfig.";
        com.microsoft.onlineid.internal.log.Logger.error(r3, r1);	 Catch:{ all -> 0x00be }
        r3 = com.microsoft.onlineid.analytics.ClientAnalytics.get();	 Catch:{ all -> 0x00be }
        r3.logException(r1);	 Catch:{ all -> 0x00be }
        r2.closeConnection();
        goto L_0x003f;
    L_0x00be:
        r0 = move-exception;
        r2.closeConnection();
        throw r0;
    L_0x00c3:
        r1 = "Failed to update ppcrlconfig (parseConfig() returned false).";
        com.microsoft.onlineid.internal.log.Logger.error(r1);
        goto L_0x0062;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.onlineid.sts.ConfigManager.downloadConfiguration(com.microsoft.onlineid.internal.configuration.Environment):boolean");
    }

    protected ServerConfig getConfig() {
        if (this._config == null) {
            this._config = new ServerConfig(this._applicationContext);
        }
        return this._config;
    }

    public String getCurrentConfigVersion() {
        return getConfig().getString(ServerConfig.Version);
    }

    protected TypedStorage getStorage() {
        if (this._storage == null) {
            this._storage = new TypedStorage(this._applicationContext);
        }
        return this._storage;
    }

    protected TransportFactory getTransportFactory() {
        return new TransportFactory(this._applicationContext);
    }

    public boolean hasConfigBeenUpdatedRecently(long j) {
        return (System.currentTimeMillis() - j) / 1000 < ((long) getConfig().getInt(Int.MinSecondsBetweenConfigDownloads));
    }

    public boolean isClientConfigVersionOlder(String str) {
        try {
            return compareVersions(str, getCurrentConfigVersion()) < 0;
        } catch (Throwable e) {
            Logger.warning("Invalid client version: " + str, e);
            return false;
        }
    }

    protected boolean parseConfig(InputStream inputStream, Environment environment) throws IOException, XmlPullParserException, StsParseException {
        try {
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(inputStream, null);
            Integer ngcCloudPinLength = getConfig().getNgcCloudPinLength();
            Editor edit = getConfig().edit();
            edit.clear();
            edit.setString(ServerConfig.EnvironmentName, environment.getEnvironmentName());
            edit.setUrl(Endpoint.Configuration, environment.getConfigUrl());
            edit.setInt(ServerConfig.NgcCloudPinLength, ngcCloudPinLength.intValue());
            new ConfigParser(newPullParser, edit).parse();
            boolean commit = edit.commit();
            return commit;
        } finally {
            inputStream.close();
        }
    }

    public boolean switchEnvironment(Environment environment) {
        return environment.equals(getConfig().getEnvironment()) ? true : downloadConfiguration(environment);
    }

    public boolean update() {
        return downloadConfiguration(getConfig().getEnvironment());
    }

    public boolean updateIfFirstDownloadNeeded() {
        return compareVersions(getCurrentConfigVersion(), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION) == 0 ? update() : true;
    }

    public boolean updateIfNeeded(String str) {
        if (hasConfigBeenUpdatedRecently(getStorage().readConfigLastDownloadedTime())) {
            return true;
        }
        Logger.info(String.format(Locale.US, "Checking for PPCRL config update from version \"%s\" to version \"%s\"", new Object[]{getCurrentConfigVersion(), str}));
        try {
            return compareVersions(str, getCurrentConfigVersion()) > 0 ? downloadConfiguration(getConfig().getEnvironment()) : true;
        } catch (Throwable e) {
            Logger.warning("Invalid server configuration requested: " + str, e);
            return false;
        }
    }
}
