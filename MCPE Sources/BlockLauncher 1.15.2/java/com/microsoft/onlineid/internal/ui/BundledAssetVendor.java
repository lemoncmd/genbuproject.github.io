package com.microsoft.onlineid.internal.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.ui.PropertyBag.Key;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class BundledAssetVendor implements IWebPropertyProvider {
    private static final String AccessControlAllowOriginAllValue = "*";
    private static final String AccessControlAllowOriginKey = "Access-Control-Allow-Origin";
    private static final Map<String, String> AccessControlAllowOriginMap = Collections.singletonMap(AccessControlAllowOriginKey, AccessControlAllowOriginAllValue);
    private static final String HttpsScheme = "https://";
    private static BundledAssetVendor Instance = null;
    public static final String ManifestAssetPath = "com.microsoft.onlineid.serverAssetBundle.path";
    public static final String ManifestAssetVersion = "com.microsoft.onlineid.serverAssetBundle.version";
    private final Context _applicationContext;
    private AssetManager _assetManager;
    private Object _countLock = new Object();
    private volatile int _hitCount;
    private volatile int _missCount;
    private String _pathToAssetBundle = null;
    private String _version;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key = new int[Key.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleVersion.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleHits.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleMisses.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private BundledAssetVendor(Context context) {
        this._applicationContext = getApplicationContextFromContext(context);
    }

    private Context getApplicationContext() {
        return this._applicationContext;
    }

    private static Context getApplicationContextFromContext(Context context) {
        return context.getApplicationContext() != null ? context.getApplicationContext() : context;
    }

    public static BundledAssetVendor getInstance(Context context) throws IllegalArgumentException {
        Class cls;
        if (Instance == null) {
            synchronized (BundledAssetVendor.class) {
                try {
                    if (Instance == null) {
                        Instance = new BundledAssetVendor(context);
                        Instance.initialize();
                    }
                } catch (Throwable th) {
                    while (true) {
                        cls = BundledAssetVendor.class;
                    }
                }
            }
        } else if (Instance.getApplicationContext() != getApplicationContextFromContext(context)) {
            Assertion.check(false, "Replacing previous instance with new instance for provided different context.");
            synchronized (BundledAssetVendor.class) {
                try {
                    Instance = new BundledAssetVendor(context);
                    Instance.initialize();
                } catch (Throwable th2) {
                    cls = BundledAssetVendor.class;
                }
            }
        }
        return Instance;
    }

    private void initialize() {
        this._assetManager = this._applicationContext.getAssets();
        PackageManager packageManager = this._applicationContext.getPackageManager();
        this._missCount = 0;
        this._hitCount = 0;
        try {
            Bundle bundle = packageManager.getApplicationInfo(this._applicationContext.getPackageName(), Token.RESERVED).metaData;
            if (bundle == null) {
                this._pathToAssetBundle = null;
                this._version = null;
                return;
            }
            this._pathToAssetBundle = bundle.getString(ManifestAssetPath);
            this._version = bundle.getString(ManifestAssetVersion);
        } catch (Throwable e) {
            Logger.error("Package name not found", e);
        }
    }

    protected String buildLocalAssetPath(String str) {
        return new StringBuilder(this._pathToAssetBundle).append('/').append(str.substring(HttpsScheme.length())).toString();
    }

    @TargetApi(17)
    public WebResourceResponse getAsset(String str) {
        if (TextUtils.isEmpty(this._pathToAssetBundle)) {
            return null;
        }
        String buildLocalAssetPath = buildLocalAssetPath(str);
        if (TextUtils.isEmpty(buildLocalAssetPath)) {
            return null;
        }
        Mimetype findFromFilename = Mimetype.findFromFilename(buildLocalAssetPath);
        if (findFromFilename == null) {
            return null;
        }
        try {
            WebResourceResponse webResourceResponse = new WebResourceResponse(findFromFilename.toString(), HttpURLConnectionBuilder.DEFAULT_CHARSET, this._assetManager.open(buildLocalAssetPath));
            if (VERSION.SDK_INT >= 21 && findFromFilename == Mimetype.FONT) {
                webResourceResponse.setResponseHeaders(AccessControlAllowOriginMap);
            }
            if (Settings.isDebugBuild()) {
                Logger.info("BundledAssetVendor: Proxied " + str + " with " + buildLocalAssetPath);
            }
            incrementHitCount();
            return webResourceResponse;
        } catch (IOException e) {
            if (Settings.isDebugBuild()) {
                Logger.info("BundledAssetVendor: MISS: No proxied asset found for " + str);
            }
            incrementMissCount();
            return null;
        }
    }

    public String getProperty(Key key) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return this._version;
            case NativeRegExp.PREFIX /*2*/:
                return Integer.toString(this._hitCount);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return Integer.toString(this._missCount);
            default:
                return null;
        }
    }

    public boolean handlesProperty(Key key) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return true;
            default:
                return false;
        }
    }

    protected void incrementHitCount() {
        synchronized (this._countLock) {
            this._hitCount++;
        }
    }

    protected void incrementMissCount() {
        synchronized (this._countLock) {
            this._missCount++;
        }
    }

    protected void setHitCount(int i) {
        synchronized (this._countLock) {
            this._hitCount = i;
        }
    }

    protected void setMissCount(int i) {
        synchronized (this._countLock) {
            this._missCount = i;
        }
    }

    public void setProperty(Key key, String str) {
        try {
            switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
                case NativeRegExp.PREFIX /*2*/:
                    setHitCount(Integer.parseInt(str));
                    return;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    setMissCount(Integer.parseInt(str));
                    return;
                default:
                    return;
            }
        } catch (NumberFormatException e) {
            Logger.error("Could not convert string to integer: '" + str + "'");
        }
        Logger.error("Could not convert string to integer: '" + str + "'");
    }
}
