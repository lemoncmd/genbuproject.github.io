package net.zhuoweizhang.mcpelauncher.ui;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import com.mojang.minecraftpe.MainActivity;
import de.ankri.views.Switch;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.ui.SwitchPreference.OnCheckedChangeListener;

public class MainMenuOptionsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnCheckedChangeListener {
    public static final String GOOGLE_PLAY_URL = "market://details?id=";
    public static final String PREFERENCES_NAME = "mcpelauncherprefs";
    public static final String PRO_APP_ID = "net.zhuoweizhang.mcpelauncher.pro";
    public static final int REQUEST_MANAGE_ADDONS = 8;
    public static final int REQUEST_MANAGE_PATCHES = 6;
    public static final int REQUEST_MANAGE_SCRIPTS = 10;
    public static final int REQUEST_MANAGE_SKINS = 7;
    public static final int REQUEST_MANAGE_TEXTURES = 5;
    public static final int REQUEST_SERVER_LIST = 9;
    public static boolean isManagingAddons = false;
    private Preference aboutPreference;
    private SwitchPreference addonsPreference;
    private SwitchPreference desktopGuiPreference;
    private SwitchPreference enableKamcordPreference;
    private Preference getProPreference;
    private Preference goToForumsPreference;
    private Set<Switch> hasInflatedSwitches = new HashSet();
    private ListPreference languagePreference;
    private Preference legacyLivePatchPreference;
    private boolean needsRestart = false;
    private Preference paranoidPreference;
    private SwitchPreference patchesPreference;
    private Preference recorderReshareLastPreference;
    private Preference recorderWatchPreference;
    private SwitchPreference reimportScriptsPreference;
    private SwitchPreference safeModePreference;
    private SwitchPreference scriptsPreference;
    private SwitchPreference skinPreference;
    private SwitchPreference texturepackPreference;
    private SwitchPreference themeDarkPreference;
    protected Thread ui = new Thread(new Runnable() {
        protected WeakReference<MainMenuOptionsActivity> activity = null;

        public void run() {
            this.activity = new WeakReference(MainMenuOptionsActivity.this);
            while (this.activity.get() != null) {
                updateStates();
                updateTP();
                updateSkin();
                updatePatches();
                updateScripts();
                synchronized (MainMenuOptionsActivity.this.ui) {
                    try {
                        MainMenuOptionsActivity.this.ui.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.gc();
        }

        protected void updateTP() {
        }

        protected void updateSkin() {
        }

        protected void updatePatches() {
            MainMenuOptionsActivity a = (MainMenuOptionsActivity) this.activity.get();
            final SwitchPreference p = a.patchesPreference;
            String sum = null;
            if (p.content != null && p.content.isChecked()) {
                int count = MainMenuOptionsActivity.this.getDir(MainActivity.PT_PATCHES_DIR, 0).listFiles().length;
                if (!(Utils.isPro() || Utils.getMaxPatches() == -1)) {
                    count = Math.min(Utils.getMaxPatches(), count);
                }
                String descr = MainMenuOptionsActivity.this.getString(R.string.plurals_patches_more);
                if (count == 1) {
                    descr = MainMenuOptionsActivity.this.getString(R.string.plurals_patches_one);
                }
                sum = count == 0 ? MainMenuOptionsActivity.this.getString(R.string.plurals_patches_no) : Utils.getEnabledPatches().size() + "/" + count + " " + descr;
            }
            final String sm = sum;
            a.runOnUiThread(new Runnable() {
                public void run() {
                    p.setSummary(sm);
                }
            });
        }

        protected void updateScripts() {
            MainMenuOptionsActivity a = (MainMenuOptionsActivity) this.activity.get();
            final SwitchPreference p = a.scriptsPreference;
            String sum = null;
            if (p.content != null && p.content.isChecked()) {
                int count = MainMenuOptionsActivity.this.getDir(ScriptManager.SCRIPTS_DIR, 0).listFiles().length;
                if (!(Utils.isPro() || Utils.getMaxScripts() == -1)) {
                    count = Math.min(Utils.getMaxScripts(), count);
                }
                String descr = MainMenuOptionsActivity.this.getString(R.string.plurals_scripts_more);
                if (count == 1) {
                    descr = MainMenuOptionsActivity.this.getString(R.string.plurals_scripts_one);
                }
                sum = count == 0 ? MainMenuOptionsActivity.this.getString(R.string.plurals_scripts_no) : Utils.getEnabledScripts().size() + "/" + count + " " + descr;
            }
            final String sm = sum;
            a.runOnUiThread(new Runnable() {
                public void run() {
                    p.setSummary(sm);
                }
            });
        }

        protected void updateStates() {
            ((MainMenuOptionsActivity) this.activity.get()).runOnUiThread(new Runnable() {
                public void run() {
                    MainMenuOptionsActivity.this.togglePrefs();
                }
            });
        }
    });
    private SwitchPreference useControllerPreference;

    public void onCreate(Bundle savedInstanceState) {
        Utils.setLanguageOverride();
        super.onCreate(savedInstanceState);
        setUp();
        this.ui.start();
    }

    public void onBackPressed() {
        if (this.needsRestart) {
            forceRestart();
        } else {
            super.onBackPressed();
        }
    }

    protected void onRestart() {
        super.onRestart();
        setPreferenceScreen(null);
        setUp();
        if (!this.ui.isAlive()) {
            this.ui.start();
        }
    }

    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().post(new Runnable() {
            public void run() {
                synchronized (MainMenuOptionsActivity.this.ui) {
                    MainMenuOptionsActivity.this.ui.notifyAll();
                }
            }
        });
    }

    protected void setUp() {
        addPreferencesFromResource(R.xml.preferences);
        this.texturepackPreference = (SwitchPreference) findPreference("zz_texture_pack_enable");
        if (this.texturepackPreference != null) {
            this.texturepackPreference.setListener(this);
            this.texturepackPreference.setOnPreferenceClickListener(this);
        }
        this.patchesPreference = (SwitchPreference) findPreference("zz_manage_patches");
        if (this.patchesPreference != null) {
            this.patchesPreference.setListener(this);
            this.patchesPreference.setOnPreferenceClickListener(this);
        }
        this.safeModePreference = (SwitchPreference) findPreference("zz_safe_mode");
        if (this.safeModePreference != null) {
            this.safeModePreference.setListener(this);
            this.safeModePreference.setOnPreferenceClickListener(this);
        }
        this.addonsPreference = (SwitchPreference) findPreference("zz_load_native_addons");
        if (this.addonsPreference != null) {
            this.addonsPreference.setOnPreferenceClickListener(this);
        }
        this.skinPreference = (SwitchPreference) findPreference("zz_skin_enable");
        if (this.skinPreference != null) {
            this.skinPreference.setListener(this);
            this.skinPreference.setOnPreferenceClickListener(this);
        }
        this.scriptsPreference = (SwitchPreference) findPreference("zz_script_enable");
        if (this.scriptsPreference != null) {
            this.scriptsPreference.setListener(this);
            this.scriptsPreference.setOnPreferenceClickListener(this);
            if (ScriptManager.isRemote) {
                this.scriptsPreference.setEnabled(false);
            }
        }
        this.languagePreference = (ListPreference) findPreference("zz_language_override");
        if (this.languagePreference != null) {
            initLanguagePreference();
            this.languagePreference.setOnPreferenceClickListener(this);
        }
        this.paranoidPreference = findPreference("zz_script_paranoid_mode");
        if (this.paranoidPreference != null) {
            this.paranoidPreference.setOnPreferenceClickListener(this);
        }
        this.legacyLivePatchPreference = findPreference("zz_legacy_live_patch");
        if (this.legacyLivePatchPreference != null) {
            this.legacyLivePatchPreference.setOnPreferenceClickListener(this);
        }
        this.aboutPreference = findPreference("zz_about");
        if (this.aboutPreference != null) {
            this.aboutPreference.setOnPreferenceClickListener(this);
        }
        this.getProPreference = findPreference("zz_get_pro");
        if (this.getProPreference != null) {
            this.getProPreference.setOnPreferenceClickListener(this);
        }
        this.goToForumsPreference = findPreference("zz_go_to_forums");
        if (this.goToForumsPreference != null) {
            this.goToForumsPreference.setOnPreferenceClickListener(this);
        }
        Preference immersiveModePreference = findPreference("zz_immersive_mode");
        if (immersiveModePreference != null && VERSION.SDK_INT < 19) {
            getPreferenceScreen().removePreference(immersiveModePreference);
            immersiveModePreference.setEnabled(false);
        }
        boolean hasRecorder = hasRecorder();
        System.out.println("Has recorder: " + hasRecorder);
        this.recorderWatchPreference = findPreference("zz_watch_recording");
        if (this.recorderWatchPreference != null) {
            if (hasRecorder) {
                this.recorderWatchPreference.setOnPreferenceClickListener(this);
            } else {
                this.recorderWatchPreference.setEnabled(false);
            }
        }
        this.recorderReshareLastPreference = findPreference("zz_reshare_last_recording");
        if (this.recorderReshareLastPreference != null) {
            if (hasRecorder) {
                this.recorderReshareLastPreference.setOnPreferenceClickListener(this);
            } else {
                this.recorderReshareLastPreference.setEnabled(false);
            }
        }
        this.useControllerPreference = (SwitchPreference) findPreference("zz_use_controller");
        if (this.useControllerPreference != null) {
            if (VERSION.SDK_INT < 12) {
                getPreferenceScreen().removePreference(this.useControllerPreference);
            } else {
                this.useControllerPreference.setListener(this);
            }
        }
        this.enableKamcordPreference = (SwitchPreference) findPreference("zz_enable_kamcord");
        if (this.enableKamcordPreference != null) {
            if (VERSION.SDK_INT < 16 || VERSION.SDK_INT >= 23) {
                getPreferenceScreen().removePreference(this.enableKamcordPreference);
            } else {
                this.enableKamcordPreference.setListener(this);
            }
        }
        this.themeDarkPreference = (SwitchPreference) findPreference("zz_theme_dark");
        if (this.themeDarkPreference != null) {
            this.themeDarkPreference.setListener(this);
        }
        this.reimportScriptsPreference = (SwitchPreference) findPreference("zz_reimport_scripts");
        if (this.reimportScriptsPreference != null) {
            this.reimportScriptsPreference.setListener(this);
        }
        this.desktopGuiPreference = (SwitchPreference) findPreference("zz_desktop_gui");
        if (this.desktopGuiPreference != null) {
            this.desktopGuiPreference.setListener(this);
        }
    }

    public boolean onPreferenceClick(Preference pref) {
        synchronized (this.ui) {
            this.ui.notifyAll();
        }
        if (pref == this.patchesPreference) {
            managePatches();
            return true;
        } else if (pref == this.texturepackPreference) {
            manageTexturepacks();
            return true;
        } else if (pref == this.getProPreference) {
            startGetPro();
            return true;
        } else if (pref == this.aboutPreference) {
            startAbout();
            return true;
        } else if (pref == this.addonsPreference) {
            manageAddons();
            return true;
        } else if (pref == this.scriptsPreference) {
            manageScripts();
            return true;
        } else if (pref == this.skinPreference) {
            manageSkins();
            return true;
        } else if (pref == this.goToForumsPreference) {
            goToForums();
            return true;
        } else if (pref == this.languagePreference || pref == this.paranoidPreference || pref == this.legacyLivePatchPreference) {
            this.needsRestart = true;
            return false;
        } else if (pref == this.recorderWatchPreference) {
            try {
                Class.forName("com.kamcord.android.Kamcord").getDeclaredMethod("showWatchView", (Class[]) null).invoke(null, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            return false;
        } else if (pref != this.recorderReshareLastPreference) {
            return false;
        } else {
            reshareLast();
            return false;
        }
    }

    public void onCheckedChange(Switch data) {
        this.needsRestart = true;
        if (this.useControllerPreference != null && data == this.useControllerPreference.content) {
            controllerChange(data);
        } else if (this.reimportScriptsPreference == null || data != this.reimportScriptsPreference.content) {
            synchronized (this.ui) {
                this.ui.notifyAll();
            }
            if (data == this.texturepackPreference.content) {
                File f = ManageTexturepacksActivity.REQUEST_ENABLE;
                if (!data.isChecked()) {
                    f = ManageTexturepacksActivity.REQUEST_DISABLE;
                }
                ManageTexturepacksActivity.setTexturepack(f, null);
            }
        } else if (data.isChecked()) {
            new Builder(this).setMessage(R.string.manage_scripts_reimport_enable_dialog).setPositiveButton(17039370, null).show();
        }
    }

    private void controllerChange(Switch sw) {
        if (sw.isChecked() && !Utils.hasExtrasPackage(this)) {
            sw.setChecked(false);
            Utils.getPrefs(0).edit().putBoolean("zz_use_controller", false).apply();
            new Builder(this).setMessage(R.string.purchase_extras_package).setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialogI, int button) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://gist.github.com/zhuowei/4538923d1963524b71fc#file-getextras-md"));
                    MainMenuOptionsActivity.this.startActivity(intent);
                }
            }).setNegativeButton(17039360, null).show();
        }
    }

    protected void managePatches() {
        startActivityForResult(new Intent(this, ManagePatchesActivity.class), REQUEST_MANAGE_PATCHES);
    }

    protected void manageAddons() {
        isManagingAddons = true;
        startActivityForResult(new Intent(this, ManageAddonsActivity.class), REQUEST_MANAGE_ADDONS);
    }

    protected void manageScripts() {
        startActivityForResult(new Intent(this, ManageScriptsActivity.class), REQUEST_MANAGE_SCRIPTS);
    }

    protected void manageTexturepacks() {
        startActivityForResult(new Intent(this, TexturePacksActivity.class), REQUEST_MANAGE_TEXTURES);
    }

    protected void manageSkins() {
        startActivityForResult(new Intent(this, ManageSkinsActivity.class), REQUEST_MANAGE_SKINS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        synchronized (this.ui) {
            this.ui.notifyAll();
        }
        switch (requestCode) {
            case REQUEST_MANAGE_TEXTURES /*5*/:
            case REQUEST_MANAGE_SKINS /*7*/:
            case REQUEST_MANAGE_SCRIPTS /*10*/:
                if (resultCode == -1) {
                    this.needsRestart = true;
                    return;
                }
                return;
            case REQUEST_MANAGE_PATCHES /*6*/:
                if (resultCode == -1) {
                    this.needsRestart = true;
                    return;
                }
                return;
            case REQUEST_MANAGE_ADDONS /*8*/:
                isManagingAddons = false;
                if (resultCode == -1) {
                    this.needsRestart = true;
                    return;
                }
                return;
            case REQUEST_SERVER_LIST /*9*/:
                if (resultCode == -1) {
                    onBackPressed();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void startAbout() {
        startActivity(new Intent(this, AboutAppActivity.class));
    }

    private void startGetPro() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(AboutAppActivity.FORUMS_PAGE_URL));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToForums() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(AboutAppActivity.FORUMS_PAGE_URL));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reshareLast() {
        File file = findLastKamcordVideo();
        if (file == null) {
            new Builder(this).setMessage(R.string.recorder_no_recording).setPositiveButton(17039370, null).show();
            return;
        }
        Uri theUri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("video/mp4");
        intent.putExtra("android.intent.extra.STREAM", theUri);
        startActivity(Intent.createChooser(intent, "Share video"));
    }

    private File findLastKamcordVideo() {
        File myDir = new File(new File(Environment.getExternalStorageDirectory(), "Kamcord"), "PP3JLc1YQlxEBNbiuYewGLsn5tBs1J5DGv6BWko2ePi-" + getPackageName());
        if (!myDir.exists()) {
            return null;
        }
        for (File theDir : myDir.listFiles()) {
            if (theDir.isDirectory() && new File(theDir, "thumbnail.jpg").exists() && new File(theDir, "video.mp4").exists()) {
                return new File(theDir, "video.mp4");
            }
        }
        return null;
    }

    private void forceRestart() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initLanguagePreference() {
        String[] langList = getResources().getString(R.string.languages_supported).split(",");
        List<String> languageNames = new ArrayList();
        languageNames.add(getResources().getString(R.string.pref_zz_language_override_default));
        Locale currentLocale = getResources().getConfiguration().locale;
        for (String override : langList) {
            if (override.length() != 0) {
                String[] overrideSplit = override.split("_");
                languageNames.add(new Locale(overrideSplit[0], overrideSplit.length > 1 ? overrideSplit[1] : BuildConfig.FLAVOR).getDisplayName(currentLocale));
            }
        }
        this.languagePreference.setEntries((CharSequence[]) languageNames.toArray(new String[0]));
        this.languagePreference.setEntryValues(langList);
    }

    protected void togglePrefs() {
    }

    protected boolean hasRecorder() {
        return getPackageName().equals(PRO_APP_ID);
    }
}
