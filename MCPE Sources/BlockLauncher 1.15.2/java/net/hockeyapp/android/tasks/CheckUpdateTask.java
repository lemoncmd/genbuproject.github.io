package net.hockeyapp.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.VersionCache;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUpdateTask extends AsyncTask<Void, String, JSONArray> {
    protected static final String APK = "apk";
    private static final int MAX_NUMBER_OF_VERSIONS = 25;
    protected String appIdentifier;
    private Context context;
    protected UpdateManagerListener listener;
    protected Boolean mandatory;
    protected String urlString;
    private long usageTime;

    public CheckUpdateTask(WeakReference<? extends Context> weakReference, String str) {
        this(weakReference, str, null);
    }

    public CheckUpdateTask(WeakReference<? extends Context> weakReference, String str, String str2) {
        this(weakReference, str, str2, null);
    }

    public CheckUpdateTask(WeakReference<? extends Context> weakReference, String str, String str2, UpdateManagerListener updateManagerListener) {
        Context context = null;
        this.urlString = null;
        this.appIdentifier = null;
        this.context = null;
        this.mandatory = Boolean.valueOf(false);
        this.usageTime = 0;
        this.appIdentifier = str2;
        this.urlString = str;
        this.listener = updateManagerListener;
        if (weakReference != null) {
            context = (Context) weakReference.get();
        }
        if (context != null) {
            this.context = context.getApplicationContext();
            this.usageTime = Tracking.getUsageTime(context);
            Constants.loadFromContext(context);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String convertStreamToString(java.io.InputStream r4) {
        /*
        r0 = new java.io.BufferedReader;
        r1 = new java.io.InputStreamReader;
        r1.<init>(r4);
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0.<init>(r1, r2);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
    L_0x0011:
        r2 = r0.readLine();	 Catch:{ IOException -> 0x002e }
        if (r2 == 0) goto L_0x003a;
    L_0x0017:
        r3 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x002e }
        r3.<init>();	 Catch:{ IOException -> 0x002e }
        r2 = r3.append(r2);	 Catch:{ IOException -> 0x002e }
        r3 = "\n";
        r2 = r2.append(r3);	 Catch:{ IOException -> 0x002e }
        r2 = r2.toString();	 Catch:{ IOException -> 0x002e }
        r1.append(r2);	 Catch:{ IOException -> 0x002e }
        goto L_0x0011;
    L_0x002e:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x0048 }
        r4.close();	 Catch:{ IOException -> 0x0043 }
    L_0x0035:
        r0 = r1.toString();
        return r0;
    L_0x003a:
        r4.close();	 Catch:{ IOException -> 0x003e }
        goto L_0x0035;
    L_0x003e:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0035;
    L_0x0043:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0035;
    L_0x0048:
        r0 = move-exception;
        r4.close();	 Catch:{ IOException -> 0x004d }
    L_0x004c:
        throw r0;
    L_0x004d:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.tasks.CheckUpdateTask.convertStreamToString(java.io.InputStream):java.lang.String");
    }

    private String encodeParam(String str) {
        try {
            return URLEncoder.encode(str, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return BuildConfig.FLAVOR;
        }
    }

    private boolean findNewVersion(JSONArray jSONArray, int i) {
        int i2 = 0;
        boolean z = false;
        while (i2 < jSONArray.length()) {
            try {
                JSONObject jSONObject = jSONArray.getJSONObject(i2);
                Object obj = jSONObject.getInt("version") > i ? 1 : null;
                Object obj2 = (jSONObject.getInt("version") == i && VersionHelper.isNewerThanLastUpdateTime(this.context, jSONObject.getLong("timestamp"))) ? 1 : null;
                Object obj3 = VersionHelper.compareVersionStrings(jSONObject.getString("minimum_os_version"), VersionHelper.mapGoogleVersion(VERSION.RELEASE)) <= 0 ? 1 : null;
                if (!((obj == null && obj2 == null) || obj3 == null)) {
                    if (jSONObject.has("mandatory")) {
                        this.mandatory = Boolean.valueOf(this.mandatory.booleanValue() | jSONObject.getBoolean("mandatory"));
                    }
                    z = true;
                }
                i2++;
            } catch (JSONException e) {
                return false;
            }
        }
        return z;
    }

    private JSONArray limitResponseSize(JSONArray jSONArray) {
        JSONArray jSONArray2 = new JSONArray();
        for (int i = 0; i < Math.min(jSONArray.length(), MAX_NUMBER_OF_VERSIONS); i++) {
            try {
                jSONArray2.put(jSONArray.get(i));
            } catch (JSONException e) {
            }
        }
        return jSONArray2;
    }

    public void attach(WeakReference<? extends Context> weakReference) {
        Context context = null;
        if (weakReference != null) {
            context = (Context) weakReference.get();
        }
        if (context != null) {
            this.context = context.getApplicationContext();
            Constants.loadFromContext(context);
        }
    }

    protected void cleanUp() {
        this.urlString = null;
        this.appIdentifier = null;
    }

    protected URLConnection createConnection(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        openConnection.addRequestProperty("User-Agent", "HockeySDK/Android");
        if (VERSION.SDK_INT <= 9) {
            openConnection.setRequestProperty("connection", "close");
        }
        return openConnection;
    }

    public void detach() {
        this.context = null;
    }

    protected JSONArray doInBackground(Void... voidArr) {
        Exception e;
        try {
            int versionCode = getVersionCode();
            JSONArray jSONArray = new JSONArray(VersionCache.getVersionInfo(this.context));
            if (getCachingEnabled() && findNewVersion(jSONArray, versionCode)) {
                HockeyLog.verbose("HockeyUpdate", "Returning cached JSON");
                return jSONArray;
            }
            URLConnection createConnection = createConnection(new URL(getURLString(UpdateActivity.EXTRA_JSON)));
            createConnection.connect();
            InputStream bufferedInputStream = new BufferedInputStream(createConnection.getInputStream());
            String convertStreamToString = convertStreamToString(bufferedInputStream);
            bufferedInputStream.close();
            JSONArray jSONArray2 = new JSONArray(convertStreamToString);
            if (findNewVersion(jSONArray2, versionCode)) {
                return limitResponseSize(jSONArray2);
            }
            return null;
        } catch (IOException e2) {
            e = e2;
            e.printStackTrace();
            return null;
        } catch (JSONException e3) {
            e = e3;
            e.printStackTrace();
            return null;
        }
    }

    protected boolean getCachingEnabled() {
        return true;
    }

    protected String getURLString(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.urlString);
        stringBuilder.append("api/2/apps/");
        stringBuilder.append(this.appIdentifier != null ? this.appIdentifier : this.context.getPackageName());
        stringBuilder.append("?format=" + str);
        if (!TextUtils.isEmpty(Secure.getString(this.context.getContentResolver(), "android_id"))) {
            stringBuilder.append("&udid=" + encodeParam(Secure.getString(this.context.getContentResolver(), "android_id")));
        }
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("net.hockeyapp.android.login", 0);
        Object string = sharedPreferences.getString("auid", null);
        if (!TextUtils.isEmpty(string)) {
            stringBuilder.append("&auid=" + encodeParam(string));
        }
        Object string2 = sharedPreferences.getString("iuid", null);
        if (!TextUtils.isEmpty(string2)) {
            stringBuilder.append("&iuid=" + encodeParam(string2));
        }
        stringBuilder.append("&os=Android");
        stringBuilder.append("&os_version=" + encodeParam(Constants.ANDROID_VERSION));
        stringBuilder.append("&device=" + encodeParam(Constants.PHONE_MODEL));
        stringBuilder.append("&oem=" + encodeParam(Constants.PHONE_MANUFACTURER));
        stringBuilder.append("&app_version=" + encodeParam(Constants.APP_VERSION));
        stringBuilder.append("&sdk=" + encodeParam(Constants.SDK_NAME));
        stringBuilder.append("&sdk_version=" + encodeParam(BuildConfig.VERSION_NAME));
        stringBuilder.append("&lang=" + encodeParam(Locale.getDefault().getLanguage()));
        stringBuilder.append("&usage_time=" + this.usageTime);
        return stringBuilder.toString();
    }

    protected int getVersionCode() {
        return Integer.parseInt(Constants.APP_VERSION);
    }

    protected void onPostExecute(JSONArray jSONArray) {
        if (jSONArray != null) {
            HockeyLog.verbose("HockeyUpdate", "Received Update Info");
            if (this.listener != null) {
                this.listener.onUpdateAvailable(jSONArray, getURLString(APK));
                return;
            }
            return;
        }
        HockeyLog.verbose("HockeyUpdate", "No Update Info available");
        if (this.listener != null) {
            this.listener.onNoUpdateAvailable();
        }
    }
}
