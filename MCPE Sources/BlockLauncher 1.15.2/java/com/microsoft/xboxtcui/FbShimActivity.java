package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.FacebookSdk;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.XLEAssert;

public class FbShimActivity extends Activity {
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        FacebookManager.getInstance().onShimActivityResult(i, i2, intent);
        finish();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
        XLEAssert.assertTrue(FacebookSdk.isInitialized());
        XLEAssert.assertNotNull(ProfileModel.getMeProfileModel());
    }
}
