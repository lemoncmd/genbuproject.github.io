package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.UpdateInfoListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionHelper {
    public static final String VERSION_MAX = "99.0";
    private int mCurrentVersionCode;
    private UpdateInfoListener mListener;
    private JSONObject mNewest;
    private ArrayList<JSONObject> mSortedVersions;

    public VersionHelper(Context context, String str, UpdateInfoListener updateInfoListener) {
        this.mListener = updateInfoListener;
        loadVersions(context, str);
        sortVersions();
    }

    public static int compareVersionStrings(String str, String str2) {
        if (str == null || str2 == null) {
            return 0;
        }
        try {
            Scanner scanner = new Scanner(str.replaceAll("\\-.*", BuildConfig.FLAVOR));
            Scanner scanner2 = new Scanner(str2.replaceAll("\\-.*", BuildConfig.FLAVOR));
            scanner.useDelimiter("\\.");
            scanner2.useDelimiter("\\.");
            while (scanner.hasNextInt() && scanner2.hasNextInt()) {
                int nextInt = scanner.nextInt();
                int nextInt2 = scanner2.nextInt();
                if (nextInt < nextInt2) {
                    break;
                } else if (nextInt > nextInt2) {
                    return 1;
                }
            }
            if (scanner.hasNextInt()) {
                return 1;
            }
            if (!scanner2.hasNextInt()) {
                return 0;
            }
            return -1;
        } catch (Exception e) {
            return 0;
        }
    }

    private static long failSafeGetLongFromJSON(JSONObject jSONObject, String str, long j) {
        try {
            j = jSONObject.getLong(str);
        } catch (JSONException e) {
        }
        return j;
    }

    private static String failSafeGetStringFromJSON(JSONObject jSONObject, String str, String str2) {
        try {
            str2 = jSONObject.getString(str);
        } catch (JSONException e) {
        }
        return str2;
    }

    private String getRestoreButton(int i, JSONObject jSONObject) {
        StringBuilder stringBuilder = new StringBuilder();
        Object versionID = getVersionID(jSONObject);
        if (!TextUtils.isEmpty(versionID)) {
            stringBuilder.append("<a href='restore:" + versionID + "'  style='background: #c8c8c8; color: #000; display: block; float: right; padding: 7px; margin: 0px 10px 10px; text-decoration: none;'>Restore</a>");
        }
        return stringBuilder.toString();
    }

    private Object getSeparator() {
        return "<hr style='border-top: 1px solid #c8c8c8; border-bottom: 0px; margin: 40px 10px 0px 10px;' />";
    }

    private int getVersionCode(JSONObject jSONObject) {
        int i = 0;
        try {
            i = jSONObject.getInt("version");
        } catch (JSONException e) {
        }
        return i;
    }

    private String getVersionID(JSONObject jSONObject) {
        String str = BuildConfig.FLAVOR;
        try {
            str = jSONObject.getString(Name.MARK);
        } catch (JSONException e) {
        }
        return str;
    }

    private String getVersionLine(int i, JSONObject jSONObject) {
        StringBuilder stringBuilder = new StringBuilder();
        int versionCode = getVersionCode(this.mNewest);
        int versionCode2 = getVersionCode(jSONObject);
        String versionName = getVersionName(jSONObject);
        stringBuilder.append("<div style='padding: 20px 10px 10px;'><strong>");
        if (i == 0) {
            stringBuilder.append("Newest version:");
        } else {
            stringBuilder.append("Version " + versionName + " (" + versionCode2 + "): ");
            if (versionCode2 != versionCode && versionCode2 == this.mCurrentVersionCode) {
                this.mCurrentVersionCode = -1;
                stringBuilder.append("[INSTALLED]");
            }
        }
        stringBuilder.append("</strong></div>");
        return stringBuilder.toString();
    }

    private String getVersionName(JSONObject jSONObject) {
        String str = BuildConfig.FLAVOR;
        try {
            str = jSONObject.getString("shortversion");
        } catch (JSONException e) {
        }
        return str;
    }

    private String getVersionNotes(int i, JSONObject jSONObject) {
        StringBuilder stringBuilder = new StringBuilder();
        String failSafeGetStringFromJSON = failSafeGetStringFromJSON(jSONObject, "notes", BuildConfig.FLAVOR);
        stringBuilder.append("<div style='padding: 0px 10px;'>");
        if (failSafeGetStringFromJSON.trim().length() == 0) {
            stringBuilder.append("<em>No information.</em>");
        } else {
            stringBuilder.append(failSafeGetStringFromJSON);
        }
        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    public static boolean isNewerThanLastUpdateTime(Context context, long j) {
        if (context == null) {
            return false;
        }
        try {
            return j > (new File(context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir).lastModified() / 1000) + 1800;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadVersions(Context context, String str) {
        this.mNewest = new JSONObject();
        this.mSortedVersions = new ArrayList();
        this.mCurrentVersionCode = this.mListener.getCurrentVersionCode();
        try {
            JSONArray jSONArray = new JSONArray(str);
            int currentVersionCode = this.mListener.getCurrentVersionCode();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                Object obj = jSONObject.getInt("version") > currentVersionCode ? 1 : null;
                Object obj2 = (jSONObject.getInt("version") == currentVersionCode && isNewerThanLastUpdateTime(context, jSONObject.getLong("timestamp"))) ? 1 : null;
                if (!(obj == null && obj2 == null)) {
                    this.mNewest = jSONObject;
                    currentVersionCode = jSONObject.getInt("version");
                }
                this.mSortedVersions.add(jSONObject);
            }
        } catch (JSONException e) {
        } catch (NullPointerException e2) {
        }
    }

    public static String mapGoogleVersion(String str) {
        return (str == null || str.equalsIgnoreCase("L")) ? "5.0" : str.equalsIgnoreCase("M") ? "6.0" : str.equalsIgnoreCase("N") ? "7.0" : Pattern.matches("^[a-zA-Z]+", str) ? VERSION_MAX : str;
    }

    private void sortVersions() {
        Collections.sort(this.mSortedVersions, new Comparator<JSONObject>() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int compare(org.json.JSONObject r3, org.json.JSONObject r4) {
                /*
                r2 = this;
                r0 = "version";
                r0 = r3.getInt(r0);	 Catch:{ JSONException -> 0x0010, NullPointerException -> 0x0012 }
                r1 = "version";
                r1 = r4.getInt(r1);	 Catch:{ JSONException -> 0x0010, NullPointerException -> 0x0012 }
                if (r0 <= r1) goto L_0x000e;
            L_0x000e:
                r0 = 0;
                return r0;
            L_0x0010:
                r0 = move-exception;
                goto L_0x000e;
            L_0x0012:
                r0 = move-exception;
                goto L_0x000e;
                */
                throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.VersionHelper.1.compare(org.json.JSONObject, org.json.JSONObject):int");
            }
        });
    }

    @SuppressLint({"SimpleDateFormat"})
    public String getFileDateString() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date(1000 * failSafeGetLongFromJSON(this.mNewest, "timestamp", 0)));
    }

    public long getFileSizeBytes() {
        boolean booleanValue = Boolean.valueOf(failSafeGetStringFromJSON(this.mNewest, "external", "false")).booleanValue();
        long failSafeGetLongFromJSON = failSafeGetLongFromJSON(this.mNewest, "appsize", 0);
        return (booleanValue && failSafeGetLongFromJSON == 0) ? -1 : failSafeGetLongFromJSON;
    }

    public String getReleaseNotes(boolean z) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("<body style='padding: 0px 0px 20px 0px'>");
        Iterator it = this.mSortedVersions.iterator();
        int i = 0;
        while (it.hasNext()) {
            JSONObject jSONObject = (JSONObject) it.next();
            if (i > 0) {
                stringBuilder.append(getSeparator());
                if (z) {
                    stringBuilder.append(getRestoreButton(i, jSONObject));
                }
            }
            stringBuilder.append(getVersionLine(i, jSONObject));
            stringBuilder.append(getVersionNotes(i, jSONObject));
            i++;
        }
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
        return stringBuilder.toString();
    }

    public String getVersionString() {
        return failSafeGetStringFromJSON(this.mNewest, "shortversion", BuildConfig.FLAVOR) + " (" + failSafeGetStringFromJSON(this.mNewest, "version", BuildConfig.FLAVOR) + ")";
    }
}
