package net.zhuoweizhang.mcpelauncher.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.AddonManager;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;
import org.mozilla.javascript.Token;

public class ManageAddonsActivity extends ListActivity {
    private static final int DIALOG_MANAGE_PATCH = 1;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED = 2;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED = 3;
    private static String disabledString = " (disabled)";
    private static String enabledString = BuildConfig.FLAVOR;
    private List<AddonListItem> addons;
    protected CompoundButton master = null;
    private AddonListItem selectedAddonItem;

    @SuppressLint({"DefaultLocale"})
    private final class AddonListComparator implements Comparator<AddonListItem> {
        private AddonListComparator() {
        }

        public int compare(AddonListItem a, AddonListItem b) {
            return a.displayName.toLowerCase().compareTo(b.displayName.toLowerCase());
        }

        public boolean equals(AddonListItem a, AddonListItem b) {
            return a.displayName.toLowerCase().equals(b.displayName.toLowerCase());
        }
    }

    private final class AddonListItem {
        public final ApplicationInfo appInfo;
        public String displayName;
        public boolean enabled = true;

        public AddonListItem(ApplicationInfo appInfo, boolean enabled) {
            this.appInfo = appInfo;
            this.displayName = appInfo.packageName;
            this.enabled = enabled;
        }

        public String toString() {
            return this.displayName + (this.enabled ? ManageAddonsActivity.enabledString : ManageAddonsActivity.disabledString);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Utils.setLanguageOverride();
        super.onCreate(savedInstanceState);
        setResult(0);
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ManageAddonsActivity.this.openManageAddonWindow((AddonListItem) ManageAddonsActivity.this.addons.get(position));
            }
        });
    }

    @TargetApi(14)
    public boolean onCreateOptionsMenu(Menu menu) {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        getMenuInflater().inflate(R.menu.ab_master, menu);
        this.master = (CompoundButton) menu.findItem(R.id.ab_switch_container).getActionView().findViewById(R.id.ab_switch);
        if (this.master != null) {
            this.master.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ManageAddonsActivity.this.findAddons();
                    } else {
                        ((ArrayAdapter) ManageAddonsActivity.this.getListAdapter()).clear();
                    }
                    Editor sh = Utils.getPrefs(0).edit();
                    sh.putBoolean("zz_load_native_addons", isChecked);
                    sh.apply();
                    ManageAddonsActivity.this.refreshABToggle();
                }
            });
            refreshABToggle();
        } else {
            System.err.println("WTF?");
        }
        return true;
    }

    protected void onStart() {
        super.onStart();
        findAddons();
        refreshABToggle();
    }

    protected void onPause() {
        super.onPause();
        refreshABToggle();
    }

    protected void onResume() {
        super.onResume();
        refreshABToggle();
    }

    protected void refreshABToggle() {
        if (VERSION.SDK_INT >= 11 && this.master != null) {
            this.master.setChecked(Utils.getPrefs(0).getBoolean("zz_load_native_addons", false));
        }
    }

    protected void setAddonListModified() {
        setResult(-1);
    }

    private void findAddons() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(Token.RESERVED);
        List<AddonListItem> addonListItems = new ArrayList();
        AddonManager manager = AddonManager.getAddonManager(this);
        for (ApplicationInfo app : apps) {
            if (!(app.metaData == null || app.metaData.getString("net.zhuoweizhang.mcpelauncher.api.targetmcpeversion") == null)) {
                AddonListItem itm = new AddonListItem(app, manager.isEnabled(app.packageName));
                itm.displayName = pm.getApplicationLabel(app).toString() + " " + itm.displayName;
                addonListItems.add(itm);
            }
        }
        receiveAddons(addonListItems);
    }

    private void receiveAddons(List<AddonListItem> addons) {
        this.addons = addons;
        List<String> allPaths = new ArrayList(addons.size());
        for (AddonListItem i : addons) {
            allPaths.add(i.appInfo.packageName);
        }
        AddonManager.getAddonManager(this).removeDeadEntries(allPaths);
        ArrayAdapter<AddonListItem> adapter = new ArrayAdapter(this, R.layout.patch_list_item, addons);
        adapter.sort(new AddonListComparator());
        setListAdapter(adapter);
    }

    private void openManageAddonWindow(AddonListItem item) {
        this.selectedAddonItem = item;
        showDialog(item.enabled ? DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED : DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED);
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_MANAGE_PATCH /*1*/:
                return createManageAddonDialog(-1);
            case DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED /*2*/:
                return createManageAddonDialog(0);
            case DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED /*3*/:
                return createManageAddonDialog(DIALOG_MANAGE_PATCH);
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_MANAGE_PATCH /*1*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED /*2*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED /*3*/:
                ((AlertDialog) dialog).setTitle(this.selectedAddonItem.toString());
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    public void toggleAddon(AddonListItem addon) {
        boolean z;
        boolean z2 = true;
        AddonManager addonManager = AddonManager.getAddonManager(this);
        String str = addon.appInfo.packageName;
        if (addon.enabled) {
            z = false;
        } else {
            z = true;
        }
        addonManager.setEnabled(str, z);
        if (addon.enabled) {
            z2 = false;
        }
        addon.enabled = z2;
        afterAddonToggle(addon);
    }

    private void afterAddonToggle(AddonListItem patch) {
        setAddonListModified();
    }

    public void deleteAddon(AddonListItem addon) throws Exception {
        startActivityForResult(new Intent("android.intent.action.DELETE", Uri.parse("package:" + addon.appInfo.packageName)), Token.VAR);
        setAddonListModified();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Token.VAR) {
            findAddons();
        }
    }

    protected AlertDialog createManageAddonDialog(int enableStatus) {
        CharSequence[] options;
        if (enableStatus == -1) {
            options = new CharSequence[DIALOG_MANAGE_PATCH];
            options[0] = getResources().getText(R.string.manage_patches_delete);
        } else {
            options = new CharSequence[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED];
            options[0] = getResources().getText(R.string.manage_patches_delete);
            options[DIALOG_MANAGE_PATCH] = enableStatus == 0 ? getResources().getText(R.string.manage_patches_enable) : getResources().getText(R.string.manage_patches_disable);
        }
        return new Builder(this).setTitle("Addon name goes here").setItems(options, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                if (button == 0) {
                    try {
                        ManageAddonsActivity.this.deleteAddon(ManageAddonsActivity.this.selectedAddonItem);
                        ManageAddonsActivity.this.findAddons();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (button == ManageAddonsActivity.DIALOG_MANAGE_PATCH) {
                    ManageAddonsActivity.this.toggleAddon(ManageAddonsActivity.this.selectedAddonItem);
                    ManageAddonsActivity.this.findAddons();
                }
            }
        }).create();
    }
}
