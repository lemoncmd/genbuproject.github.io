package net.hockeyapp.android.metrics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.onlineid.sts.request.AbstractStsRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.metrics.model.Application;
import net.hockeyapp.android.metrics.model.Device;
import net.hockeyapp.android.metrics.model.Internal;
import net.hockeyapp.android.metrics.model.Session;
import net.hockeyapp.android.metrics.model.User;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

class TelemetryContext {
    private static final String SESSION_IS_FIRST_KEY = "SESSION_IS_FIRST";
    private static final String SHARED_PREFERENCES_KEY = "HOCKEY_APP_TELEMETRY_CONTEXT";
    private static final String TAG = "HockeyApp-Metrics";
    private final Object IKEY_LOCK;
    protected final Application mApplication;
    protected Context mContext;
    protected final Device mDevice;
    private String mInstrumentationKey;
    protected final Internal mInternal;
    private String mPackageName;
    protected final Session mSession;
    private SharedPreferences mSettings;
    protected final User mUser;

    private TelemetryContext() {
        this.IKEY_LOCK = new Object();
        this.mDevice = new Device();
        this.mSession = new Session();
        this.mUser = new User();
        this.mApplication = new Application();
        this.mInternal = new Internal();
    }

    protected TelemetryContext(Context context, String str) {
        this();
        this.mSettings = context.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        this.mContext = context;
        this.mInstrumentationKey = Util.convertAppIdentifierToGuid(str);
        configDeviceContext();
        configUserId();
        configInternalContext();
        configApplicationContext();
    }

    protected void configApplicationContext() {
        HockeyLog.debug(TAG, "Configuring application context");
        this.mPackageName = BuildConfig.FLAVOR;
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            if (packageInfo.packageName != null) {
                this.mPackageName = packageInfo.packageName;
            }
            String num = Integer.toString(packageInfo.versionCode);
            setAppVersion(String.format("%s (%S)", new Object[]{packageInfo.versionName, num}));
        } catch (NameNotFoundException e) {
            HockeyLog.debug(TAG, "Could not get application context");
        } finally {
            setAppVersion("unknown");
        }
        setSdkVersion("android:" + BuildConfig.VERSION_NAME);
    }

    protected void configDeviceContext() {
        HockeyLog.debug(TAG, "Configuring device context");
        setOsVersion(VERSION.RELEASE);
        setOsName(AbstractStsRequest.DeviceType);
        setDeviceModel(Build.MODEL);
        setDeviceOemName(Build.MANUFACTURER);
        setOsLocale(Locale.getDefault().toString());
        setOsLanguage(Locale.getDefault().getLanguage());
        updateScreenResolution();
        setDeviceId(Constants.DEVICE_IDENTIFIER);
        if (((TelephonyManager) this.mContext.getSystemService("phone")).getPhoneType() != 0) {
            setDeviceType("Phone");
        } else {
            setDeviceType("Tablet");
        }
        if (Util.isEmulator()) {
            setDeviceModel("[Emulator]" + this.mDevice.getModel());
        }
    }

    protected void configInternalContext() {
        setSdkVersion("android:" + BuildConfig.VERSION_NAME);
    }

    protected void configSessionContext(String str) {
        HockeyLog.debug(TAG, "Configuring session context");
        setSessionId(str);
        HockeyLog.debug(TAG, "Setting the isNew-flag to true, as we only count new sessions");
        setIsNewSession("true");
        Editor edit = this.mSettings.edit();
        if (this.mSettings.getBoolean(SESSION_IS_FIRST_KEY, false)) {
            setIsFirstSession("false");
            HockeyLog.debug(TAG, "It's not their first session, writing false to SharedPreferences.");
            return;
        }
        edit.putBoolean(SESSION_IS_FIRST_KEY, true);
        edit.apply();
        setIsFirstSession("true");
        HockeyLog.debug(TAG, "It's our first session, writing true to SharedPreferences.");
    }

    protected void configUserId() {
        HockeyLog.debug(TAG, "Configuring user context");
        HockeyLog.debug("Using pre-supplied anonymous device identifier.");
        setAnonymousUserId(Constants.CRASH_IDENTIFIER);
    }

    public String getAnonymousUserId() {
        String id;
        synchronized (this.mUser) {
            id = this.mUser.getId();
        }
        return id;
    }

    public String getAppVersion() {
        String ver;
        synchronized (this.mApplication) {
            ver = this.mApplication.getVer();
        }
        return ver;
    }

    protected Map<String, String> getContextTags() {
        Map<String, String> linkedHashMap = new LinkedHashMap();
        synchronized (this.mApplication) {
            this.mApplication.addToHashMap(linkedHashMap);
        }
        synchronized (this.mDevice) {
            this.mDevice.addToHashMap(linkedHashMap);
        }
        synchronized (this.mSession) {
            this.mSession.addToHashMap(linkedHashMap);
        }
        synchronized (this.mUser) {
            this.mUser.addToHashMap(linkedHashMap);
        }
        synchronized (this.mInternal) {
            this.mInternal.addToHashMap(linkedHashMap);
        }
        return linkedHashMap;
    }

    public String getDeviceId() {
        return this.mDevice.getId();
    }

    public String getDeviceModel() {
        String model;
        synchronized (this.mDevice) {
            model = this.mDevice.getModel();
        }
        return model;
    }

    public String getDeviceOemName() {
        String oemName;
        synchronized (this.mDevice) {
            oemName = this.mDevice.getOemName();
        }
        return oemName;
    }

    public String getDeviceType() {
        return this.mDevice.getType();
    }

    public String getInstrumentationKey() {
        String str;
        synchronized (this.IKEY_LOCK) {
            str = this.mInstrumentationKey;
        }
        return str;
    }

    public String getIsFirstSession() {
        String isFirst;
        synchronized (this.mSession) {
            isFirst = this.mSession.getIsFirst();
        }
        return isFirst;
    }

    public String getIsNewSession() {
        String isNew;
        synchronized (this.mSession) {
            isNew = this.mSession.getIsNew();
        }
        return isNew;
    }

    public String getOSLanguage() {
        String language;
        synchronized (this.mDevice) {
            language = this.mDevice.getLanguage();
        }
        return language;
    }

    public String getOsLocale() {
        String locale;
        synchronized (this.mDevice) {
            locale = this.mDevice.getLocale();
        }
        return locale;
    }

    public String getOsName() {
        String os;
        synchronized (this.mDevice) {
            os = this.mDevice.getOs();
        }
        return os;
    }

    public String getOsVersion() {
        String osVersion;
        synchronized (this.mDevice) {
            osVersion = this.mDevice.getOsVersion();
        }
        return osVersion;
    }

    protected String getPackageName() {
        return this.mPackageName;
    }

    public String getScreenResolution() {
        String screenResolution;
        synchronized (this.mDevice) {
            screenResolution = this.mDevice.getScreenResolution();
        }
        return screenResolution;
    }

    public String getSdkVersion() {
        String sdkVersion;
        synchronized (this.mInternal) {
            sdkVersion = this.mInternal.getSdkVersion();
        }
        return sdkVersion;
    }

    public String getSessionId() {
        String id;
        synchronized (this.mSession) {
            id = this.mSession.getId();
        }
        return id;
    }

    protected void renewSessionContext(String str) {
        configSessionContext(str);
    }

    public void setAnonymousUserId(String str) {
        synchronized (this.mUser) {
            this.mUser.setId(str);
        }
    }

    public void setAppVersion(String str) {
        synchronized (this.mApplication) {
            this.mApplication.setVer(str);
        }
    }

    public void setDeviceId(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setId(str);
        }
    }

    public void setDeviceModel(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setModel(str);
        }
    }

    public void setDeviceOemName(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setOemName(str);
        }
    }

    public void setDeviceType(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setType(str);
        }
    }

    public void setInstrumentationKey(String str) {
        synchronized (this) {
            synchronized (this.IKEY_LOCK) {
                this.mInstrumentationKey = str;
            }
        }
    }

    public void setIsFirstSession(String str) {
        synchronized (this.mSession) {
            this.mSession.setIsFirst(str);
        }
    }

    public void setIsNewSession(String str) {
        synchronized (this.mSession) {
            this.mSession.setIsNew(str);
        }
    }

    public void setOsLanguage(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setLanguage(str);
        }
    }

    public void setOsLocale(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setLocale(str);
        }
    }

    public void setOsName(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setOs(str);
        }
    }

    public void setOsVersion(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setOsVersion(str);
        }
    }

    public void setScreenResolution(String str) {
        synchronized (this.mDevice) {
            this.mDevice.setScreenResolution(str);
        }
    }

    public void setSdkVersion(String str) {
        synchronized (this.mInternal) {
            this.mInternal.setSdkVersion(str);
        }
    }

    public void setSessionId(String str) {
        synchronized (this.mSession) {
            this.mSession.setId(str);
        }
    }

    @SuppressLint({"NewApi", "Deprecation"})
    protected void updateScreenResolution() {
        if (this.mContext != null) {
            int i;
            int i2;
            WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
            if (VERSION.SDK_INT >= 17) {
                Point point = new Point();
                windowManager.getDefaultDisplay().getRealSize(point);
                i = point.x;
                i2 = point.y;
            } else if (VERSION.SDK_INT >= 13) {
                try {
                    Method method = Display.class.getMethod("getRawWidth", new Class[0]);
                    Method method2 = Display.class.getMethod("getRawHeight", new Class[0]);
                    Display defaultDisplay = windowManager.getDefaultDisplay();
                    int intValue = ((Integer) method.invoke(defaultDisplay, new Object[0])).intValue();
                    i2 = ((Integer) method2.invoke(defaultDisplay, new Object[0])).intValue();
                    i = intValue;
                } catch (Exception e) {
                    Exception exception = e;
                    Point point2 = new Point();
                    windowManager.getDefaultDisplay().getSize(point2);
                    i = point2.x;
                    i2 = point2.y;
                    HockeyLog.debug(TAG, "Couldn't determine screen resolution: " + exception.toString());
                }
            } else {
                Display defaultDisplay2 = windowManager.getDefaultDisplay();
                i = defaultDisplay2.getWidth();
                i2 = defaultDisplay2.getHeight();
            }
            setScreenResolution(String.valueOf(i2) + "x" + String.valueOf(i));
        }
    }
}
