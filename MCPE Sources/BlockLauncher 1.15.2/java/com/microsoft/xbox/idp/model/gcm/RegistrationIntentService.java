package com.microsoft.xbox.idp.model.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.xbox.idp.interop.Interop;
import java.io.IOException;
import net.hockeyapp.android.BuildConfig;

public class RegistrationIntentService extends IntentService {
    private static final String REGISTRATION_MODE = "com.microsoft.xbox.idp.model.gcm";
    private static final String REGISTRATION_TOKEN_FIELD = "registrationToken";
    private static final String SENDER_ID = "86584527366";
    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    public void onHandleIntent(Intent intent) {
        String str = null;
        boolean z = false;
        SharedPreferences sharedPreferences = getSharedPreferences(REGISTRATION_MODE, 0);
        if (sharedPreferences != null) {
            str = sharedPreferences.getString(REGISTRATION_TOKEN_FIELD, BuildConfig.FLAVOR);
        }
        Bundle extras = intent.getExtras();
        boolean z2 = extras != null ? extras.getBoolean(NotificationInstanceIDListenerService.REFRESH_FLAG, false) : false;
        if (str.isEmpty() || z2) {
            try {
                str = InstanceID.getInstance(this).getToken(SENDER_ID, "GCM", null);
            } catch (IOException e) {
                Log.d("XSAPI.Android", e.getMessage());
            } catch (SecurityException e2) {
                Log.d("XSAPI.Android", e2.getMessage());
            }
            sharedPreferences.edit().putString(REGISTRATION_TOKEN_FIELD, str).commit();
        } else {
            z = true;
        }
        Interop.NotificationRegisterCallback(str, z);
    }
}
