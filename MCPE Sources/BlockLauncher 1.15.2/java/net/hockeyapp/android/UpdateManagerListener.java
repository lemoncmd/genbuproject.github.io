package net.hockeyapp.android;

import java.util.Date;
import org.json.JSONArray;

public abstract class UpdateManagerListener {
    public boolean canUpdateInMarket() {
        return false;
    }

    public Date getExpiryDate() {
        return null;
    }

    public Class<? extends UpdateActivity> getUpdateActivityClass() {
        return UpdateActivity.class;
    }

    public Class<? extends UpdateFragment> getUpdateFragmentClass() {
        return UpdateFragment.class;
    }

    public boolean onBuildExpired() {
        return true;
    }

    public void onCancel() {
    }

    public void onNoUpdateAvailable() {
    }

    public void onUpdateAvailable() {
    }

    public void onUpdateAvailable(JSONArray jSONArray, String str) {
        onUpdateAvailable();
    }

    public void onUpdatePermissionsNotGranted() {
    }
}
