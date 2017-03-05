package com.microsoft.onlineid.internal.sso;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.sts.Cryptography;
import com.microsoft.onlineid.sts.ServerConfig;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SignatureVerifier {
    private final Context _applicationContext;
    private final ServerConfig _config;
    private final PackageManager _packageManager;

    @Deprecated
    public SignatureVerifier() {
        this._applicationContext = null;
        this._packageManager = null;
        this._config = null;
    }

    public SignatureVerifier(Context context) {
        this._applicationContext = context;
        this._packageManager = context.getPackageManager();
        this._config = new ServerConfig(context);
    }

    public boolean isPackageInUid(int i, String str) {
        String[] packagesForUid = this._packageManager.getPackagesForUid(i);
        return (packagesForUid == null || packagesForUid.length == 0) ? false : Arrays.asList(packagesForUid).contains(str);
    }

    public boolean isTrusted(String str) {
        if (this._applicationContext.getPackageName().equalsIgnoreCase(str)) {
            return true;
        }
        Settings instance = Settings.getInstance(this._applicationContext);
        if (Settings.isDebugBuild() && !instance.isSettingEnabled(Settings.ShouldCheckSsoCertificatesInDebug)) {
            return true;
        }
        try {
            PackageInfo packageInfo = this._packageManager.getPackageInfo(str, 64);
            Set stringSet = this._config.getStringSet(ServerConfig.AndroidSsoCertificates);
            List arrayList = new ArrayList();
            MessageDigest sha256Digester = Cryptography.getSha256Digester();
            for (Signature toByteArray : packageInfo.signatures) {
                String encodeToString = Base64.encodeToString(sha256Digester.digest(toByteArray.toByteArray()), 2);
                if (stringSet.contains(encodeToString)) {
                    return true;
                }
                arrayList.add(encodeToString);
            }
            Logger.warning("Not trusting " + str + " because no matching hash was found in the whitelist.");
            Logger.warning("Hashes for " + str + " are: " + Arrays.toString(arrayList.toArray()));
            Logger.warning("Whitelist is: " + Arrays.toString(stringSet.toArray()));
            return false;
        } catch (Throwable e) {
            String str2 = "Cannot check trust state of missing package: " + str;
            Logger.error(str2, e);
            Assertion.check(false, str2);
            return false;
        }
    }
}
