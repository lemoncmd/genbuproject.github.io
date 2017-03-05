package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtil {
    private SharedPreferences mFeedbackTokenPrefs;
    private Editor mFeedbackTokenPrefsEditor;
    private SharedPreferences mNameEmailSubjectPrefs;
    private Editor mNameEmailSubjectPrefsEditor;

    private static class PrefsUtilHolder {
        public static final PrefsUtil INSTANCE = new PrefsUtil();

        private PrefsUtilHolder() {
        }
    }

    private PrefsUtil() {
    }

    public static PrefsUtil getInstance() {
        return PrefsUtilHolder.INSTANCE;
    }

    public String getFeedbackTokenFromPrefs(Context context) {
        if (context == null) {
            return null;
        }
        this.mFeedbackTokenPrefs = context.getSharedPreferences(Util.PREFS_FEEDBACK_TOKEN, 0);
        return this.mFeedbackTokenPrefs != null ? this.mFeedbackTokenPrefs.getString(Util.PREFS_KEY_FEEDBACK_TOKEN, null) : null;
    }

    public String getNameEmailFromPrefs(Context context) {
        if (context == null) {
            return null;
        }
        this.mNameEmailSubjectPrefs = context.getSharedPreferences(Util.PREFS_NAME_EMAIL_SUBJECT, 0);
        return this.mNameEmailSubjectPrefs != null ? this.mNameEmailSubjectPrefs.getString(Util.PREFS_KEY_NAME_EMAIL_SUBJECT, null) : null;
    }

    public void saveFeedbackTokenToPrefs(Context context, String str) {
        if (context != null) {
            this.mFeedbackTokenPrefs = context.getSharedPreferences(Util.PREFS_FEEDBACK_TOKEN, 0);
            if (this.mFeedbackTokenPrefs != null) {
                this.mFeedbackTokenPrefsEditor = this.mFeedbackTokenPrefs.edit();
                this.mFeedbackTokenPrefsEditor.putString(Util.PREFS_KEY_FEEDBACK_TOKEN, str);
                this.mFeedbackTokenPrefsEditor.apply();
            }
        }
    }

    public void saveNameEmailSubjectToPrefs(Context context, String str, String str2, String str3) {
        if (context != null) {
            this.mNameEmailSubjectPrefs = context.getSharedPreferences(Util.PREFS_NAME_EMAIL_SUBJECT, 0);
            if (this.mNameEmailSubjectPrefs != null) {
                this.mNameEmailSubjectPrefsEditor = this.mNameEmailSubjectPrefs.edit();
                if (str == null || str2 == null || str3 == null) {
                    this.mNameEmailSubjectPrefsEditor.putString(Util.PREFS_KEY_NAME_EMAIL_SUBJECT, null);
                } else {
                    this.mNameEmailSubjectPrefsEditor.putString(Util.PREFS_KEY_NAME_EMAIL_SUBJECT, String.format("%s|%s|%s", new Object[]{str, str2, str3}));
                }
                this.mNameEmailSubjectPrefsEditor.apply();
            }
        }
    }
}
