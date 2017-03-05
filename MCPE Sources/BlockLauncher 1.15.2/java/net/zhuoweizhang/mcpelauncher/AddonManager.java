package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;

public class AddonManager {
    public static AddonManager addonMgr;
    private Context context;
    private Set<String> disabledAddons;

    public static AddonManager getAddonManager(Context context) {
        if (addonMgr == null) {
            addonMgr = new AddonManager(context.getApplicationContext());
        }
        return addonMgr;
    }

    public AddonManager(Context context) {
        this.context = context;
        loadDisabledAddons();
    }

    public Set<String> getDisabledAddons() {
        return this.disabledAddons;
    }

    public void setEnabled(String name, boolean state) {
        if (state) {
            this.disabledAddons.remove(name);
        } else {
            this.disabledAddons.add(name);
        }
        saveDisabledAddons();
    }

    public boolean isEnabled(String name) {
        return !this.disabledAddons.contains(name);
    }

    public void removeDeadEntries(Collection<String> allPossibleFiles) {
        this.disabledAddons.retainAll(allPossibleFiles);
        saveDisabledAddons();
    }

    protected void loadDisabledAddons() {
        this.disabledAddons = new HashSet(Arrays.asList(Utils.getPrefs(1).getString("disabledAddons", BuildConfig.FLAVOR).split(";")));
    }

    protected void saveDisabledAddons() {
        Editor edit = Utils.getPrefs(1).edit();
        edit.putString("disabledAddons", PatchManager.join((String[]) this.disabledAddons.toArray(PatchManager.blankArray), ";"));
        edit.putInt("addonManagerVersion", 1);
        edit.apply();
    }
}
