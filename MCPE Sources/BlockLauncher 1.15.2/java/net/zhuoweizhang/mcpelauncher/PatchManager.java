package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import java.io.File;
import java.util.Collection;
import java.util.Set;

public class PatchManager {
    public static final String[] blankArray = new String[0];
    public static PatchManager patchMgr;
    private Context context;
    private Set<String> enabledPatches;

    public static PatchManager getPatchManager(Context context) {
        if (patchMgr == null) {
            patchMgr = new PatchManager(context);
        }
        return patchMgr;
    }

    public PatchManager(Context context) {
        this.context = context;
        loadEnabledPatches();
    }

    public Set<String> getEnabledPatches() {
        return this.enabledPatches;
    }

    private void setEnabled(String name, boolean state) {
        if (state) {
            this.enabledPatches.add(name);
        } else {
            this.enabledPatches.remove(name);
        }
        saveEnabledPatches();
    }

    public void setEnabled(File[] files, boolean state) {
        for (File file : files) {
            String name = file.getAbsolutePath();
            if (name != null && name.length() > 0) {
                if (state) {
                    this.enabledPatches.add(name);
                } else {
                    this.enabledPatches.remove(name);
                }
            }
        }
        saveEnabledPatches();
    }

    public void setEnabled(File file, boolean state) {
        setEnabled(file.getAbsolutePath(), state);
    }

    private boolean isEnabled(String name) {
        return this.enabledPatches.contains(name);
    }

    public boolean isEnabled(File file) {
        return isEnabled(file.getAbsolutePath());
    }

    public void removeDeadEntries(Collection<String> allPossibleFiles) {
        this.enabledPatches.retainAll(allPossibleFiles);
        saveEnabledPatches();
    }

    protected void loadEnabledPatches() {
        this.enabledPatches = Utils.getEnabledPatches();
    }

    protected void saveEnabledPatches() {
        Editor edit = Utils.getPrefs(1).edit();
        edit.putString("enabledPatches", join((String[]) this.enabledPatches.toArray(blankArray), ";"));
        edit.putBoolean("force_prepatch", true);
        edit.putInt("patchManagerVersion", 1);
        edit.apply();
    }

    public static String join(String[] list, String token) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            builder.append(list[i]);
            if (i < list.length - 1) {
                builder.append(token);
            }
        }
        return builder.toString();
    }

    public void disableAllPatches() {
        this.enabledPatches.clear();
        saveEnabledPatches();
    }
}
