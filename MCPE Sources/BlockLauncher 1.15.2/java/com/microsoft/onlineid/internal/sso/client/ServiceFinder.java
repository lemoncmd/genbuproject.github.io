package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SignatureVerifier;
import com.microsoft.onlineid.internal.sso.SsoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class ServiceFinder {
    public static final Comparator<SsoService> MasterPrecedenceComparator = new Comparator<SsoService>() {
        public int compare(SsoService ssoService, SsoService ssoService2) {
            if (ssoService.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return -1;
            }
            if (ssoService2.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return 1;
            }
            int ssoVersion = ssoService2.getSsoVersion() - ssoService.getSsoVersion();
            return ssoVersion == 0 ? (int) (ssoService.getFirstInstallTime() - ssoService2.getFirstInstallTime()) : ssoVersion;
        }
    };
    public static final String SdkVersionMetaDataName = "com.microsoft.msa.service.sdk_version";
    public static final String SsoVersionMetaDataName = "com.microsoft.msa.service.sso_version";
    private final Context _applicationContext;
    private final SignatureVerifier _signatureVerifier;

    public ServiceFinder(Context context) {
        this._applicationContext = context;
        this._signatureVerifier = new SignatureVerifier(context);
    }

    protected long getFirstInstallTime(String str) throws NameNotFoundException {
        return this._applicationContext.getPackageManager().getPackageInfo(str, 0).firstInstallTime;
    }

    public List<SsoService> getOrderedSsoServices() {
        List<SsoService> trustedSsoServices = getTrustedSsoServices();
        Collections.sort(trustedSsoServices, MasterPrecedenceComparator);
        Logger.info("Available trusted/ordered SSO services: " + Arrays.toString(trustedSsoServices.toArray()));
        return trustedSsoServices;
    }

    protected SsoService getSelfSsoService() {
        return new SsoService(this._applicationContext.getPackageName(), 0, BuildConfig.FLAVOR, 0);
    }

    public SsoService getSsoService(String str) {
        if (str != null) {
            for (SsoService ssoService : getOrderedSsoServices()) {
                if (ssoService.getPackageName().equalsIgnoreCase(str)) {
                    return ssoService;
                }
            }
        }
        return null;
    }

    protected List<SsoService> getTrustedSsoServices() {
        List<ResolveInfo> queryIntentServices = this._applicationContext.getPackageManager().queryIntentServices(new Intent(SsoService.SsoServiceIntent), Token.RESERVED);
        List<SsoService> arrayList = new ArrayList();
        for (ResolveInfo resolveInfo : queryIntentServices) {
            String str = resolveInfo.serviceInfo.applicationInfo.packageName;
            Bundle bundle = resolveInfo.serviceInfo.metaData;
            int i = bundle.getInt(SsoVersionMetaDataName);
            if (!this._signatureVerifier.isTrusted(str)) {
                Logger.warning("Disallowing SSO with " + str + " because it is not trusted.");
            } else if (i <= 1) {
                Logger.warning("Disallowing  SSO with " + str + " because its SSO version is " + i + ".");
            } else {
                try {
                    arrayList.add(new SsoService(str, i, bundle.getString(SdkVersionMetaDataName), getFirstInstallTime(str)));
                } catch (Throwable e) {
                    Logger.error("Could not find package when querying for first install time: " + str, e);
                }
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.TotalTrustedSsoServices, String.valueOf(arrayList.size()));
        return arrayList;
    }
}
