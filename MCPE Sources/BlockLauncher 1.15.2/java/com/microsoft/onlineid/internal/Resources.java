package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import java.util.Locale;

public class Resources {
    private final Context _appContext;

    public Resources(Context context) {
        this._appContext = context;
    }

    private int getIdentifierByType(String str, String str2) {
        int i = 0;
        try {
            i = this._appContext.getResources().getIdentifier(str, str2, this._appContext.getPackageName());
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "%s resource with name %s not found", new Object[]{str2, str}));
        }
        return i;
    }

    public static String getSdkVersion(Context context) {
        return new Resources(context).getSdkVersion();
    }

    public static String getString(Context context, String str) {
        return new Resources(context).getString(str);
    }

    public int getDimensionPixelSize(String str) {
        int i = 0;
        try {
            i = this._appContext.getResources().getDimensionPixelSize(getIdentifierByType(str, "dimen"));
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Dimen resource with name %s not found", new Object[]{str}));
        }
        return i;
    }

    public int getId(String str) {
        int i = 0;
        try {
            i = getIdentifierByType(str, Name.MARK);
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Id resource with name %s not found", new Object[]{str}));
        }
        return i;
    }

    public int getLayout(String str) {
        int i = 0;
        try {
            i = getIdentifierByType(str, "layout");
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Layout resource with name %s not found", new Object[]{str}));
        }
        return i;
    }

    public int getMenu(String str) {
        int i = 0;
        try {
            i = getIdentifierByType(str, "menu");
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Menu resource with name %s not found", new Object[]{str}));
        }
        return i;
    }

    public String getSdkVersion() {
        return getString("sdk_version_name");
    }

    public String getString(String str) {
        try {
            return this._appContext.getString(getIdentifierByType(str, "string"));
        } catch (NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "String resource with name %s not found", new Object[]{str}));
            return null;
        }
    }
}
