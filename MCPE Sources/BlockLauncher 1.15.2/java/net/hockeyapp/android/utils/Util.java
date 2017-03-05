package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.R;
import org.mozilla.javascript.Token;

public class Util {
    public static final String APP_IDENTIFIER_KEY = "net.hockeyapp.android.appIdentifier";
    public static final int APP_IDENTIFIER_LENGTH = 32;
    public static final String APP_IDENTIFIER_PATTERN = "[0-9a-f]+";
    private static final String APP_SECRET_KEY = "net.hockeyapp.android.appSecret";
    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat;
        }
    };
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static final String LOG_IDENTIFIER = "HockeyApp";
    public static final String PREFS_FEEDBACK_TOKEN = "net.hockeyapp.android.prefs_feedback_token";
    public static final String PREFS_KEY_FEEDBACK_TOKEN = "net.hockeyapp.android.prefs_key_feedback_token";
    public static final String PREFS_KEY_NAME_EMAIL_SUBJECT = "net.hockeyapp.android.prefs_key_name_email";
    public static final String PREFS_NAME_EMAIL_SUBJECT = "net.hockeyapp.android.prefs_name_email";
    private static final String SDK_VERSION_KEY = "net.hockeyapp.android.sdkVersion";
    private static final Pattern appIdentifierPattern = Pattern.compile(APP_IDENTIFIER_PATTERN, 2);

    private static Notification buildNotificationPreHoneycomb(Context context, PendingIntent pendingIntent, String str, String str2, int i) {
        Notification notification = new Notification(i, BuildConfig.FLAVOR, System.currentTimeMillis());
        try {
            notification.getClass().getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}).invoke(notification, new Object[]{context, str, str2, pendingIntent});
        } catch (Exception e) {
        }
        return notification;
    }

    @TargetApi(11)
    private static Notification buildNotificationWithBuilder(Context context, PendingIntent pendingIntent, String str, String str2, int i) {
        Builder smallIcon = new Builder(context).setContentTitle(str).setContentText(str2).setContentIntent(pendingIntent).setSmallIcon(i);
        return VERSION.SDK_INT < 16 ? smallIcon.getNotification() : smallIcon.build();
    }

    public static boolean classExists(String str) {
        try {
            return Class.forName(str) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String convertAppIdentifierToGuid(String str) throws IllegalArgumentException {
        try {
            String sanitizeAppIdentifier = sanitizeAppIdentifier(str);
            if (sanitizeAppIdentifier == null) {
                return null;
            }
            StringBuffer stringBuffer = new StringBuffer(sanitizeAppIdentifier);
            stringBuffer.insert(20, '-');
            stringBuffer.insert(16, '-');
            stringBuffer.insert(12, '-');
            stringBuffer.insert(8, '-');
            return stringBuffer.toString();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public static Notification createNotification(Context context, PendingIntent pendingIntent, String str, String str2, int i) {
        return isNotificationBuilderSupported() ? buildNotificationWithBuilder(context, pendingIntent, str, str2, i) : buildNotificationPreHoneycomb(context, pendingIntent, str, str2, i);
    }

    public static String dateToISO8601(Date date) {
        if (date == null) {
            date = new Date();
        }
        return ((DateFormat) DATE_FORMAT_THREAD_LOCAL.get()).format(date);
    }

    public static String encodeParam(String str) {
        try {
            return URLEncoder.encode(str, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    @SuppressLint({"NewApi"})
    public static Boolean fragmentsSupported() {
        try {
            boolean z = VERSION.SDK_INT >= 11 && classExists("android.app.Fragment");
            return Boolean.valueOf(z);
        } catch (NoClassDefFoundError e) {
            return Boolean.valueOf(false);
        }
    }

    public static String getAppIdentifier(Context context) {
        return getManifestString(context, APP_IDENTIFIER_KEY);
    }

    public static String getAppName(Context context) {
        if (context == null) {
            return BuildConfig.FLAVOR;
        }
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (NameNotFoundException e) {
        }
        return applicationInfo != null ? (String) packageManager.getApplicationLabel(applicationInfo) : context.getString(R.string.hockeyapp_crash_dialog_app_name_fallback);
    }

    public static String getAppSecret(Context context) {
        return getManifestString(context, APP_SECRET_KEY);
    }

    private static Bundle getBundle(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), Token.RESERVED).metaData;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFormString(Map<String, String> map) throws UnsupportedEncodingException {
        Iterable arrayList = new ArrayList();
        for (String str : map.keySet()) {
            String str2 = (String) map.get(str);
            String str3 = URLEncoder.encode(str3, HttpURLConnectionBuilder.DEFAULT_CHARSET);
            arrayList.add(str3 + "=" + URLEncoder.encode(str2, HttpURLConnectionBuilder.DEFAULT_CHARSET));
        }
        return TextUtils.join("&", arrayList);
    }

    public static String getManifestString(Context context, String str) {
        return getBundle(context).getString(str);
    }

    public static String getSdkVersionFromManifest(Context context) {
        return getManifestString(context, SDK_VERSION_KEY);
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmulator() {
        return Build.BRAND.equalsIgnoreCase("generic");
    }

    public static boolean isNotificationBuilderSupported() {
        return VERSION.SDK_INT >= 11 && classExists("android.app.Notification.Builder");
    }

    public static final boolean isValidEmail(String str) {
        return !TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public static Boolean runsOnTablet(WeakReference<Activity> weakReference) {
        if (weakReference != null) {
            Activity activity = (Activity) weakReference.get();
            if (activity != null) {
                Configuration configuration = activity.getResources().getConfiguration();
                boolean z = (configuration.screenLayout & 15) == 3 || (configuration.screenLayout & 15) == 4;
                return Boolean.valueOf(z);
            }
        }
        return Boolean.valueOf(false);
    }

    public static String sanitizeAppIdentifier(String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException("App ID must not be null.");
        }
        String trim = str.trim();
        Matcher matcher = appIdentifierPattern.matcher(trim);
        if (trim.length() != APP_IDENTIFIER_LENGTH) {
            throw new IllegalArgumentException("App ID length must be 32 characters.");
        } else if (matcher.matches()) {
            return trim;
        } else {
            throw new IllegalArgumentException("App ID must match regex pattern /[0-9a-f]+/i");
        }
    }

    public static boolean sessionTrackingSupported() {
        return VERSION.SDK_INT >= 14;
    }
}
