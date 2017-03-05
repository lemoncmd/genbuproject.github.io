package net.zhuoweizhang.mcpelauncher.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ManageSkinsActivity extends ListActivity {
    public static final File REQUEST_DISABLE = new File("/just/disable/skins");
    public static final File REQUEST_ENABLE = new File("/just/enable/skins");
    protected SkinsAdapter adapter;
    protected CompoundButton master = null;

    protected class SkinsAdapter extends ArrayAdapter<File> {
        private LayoutInflater inflater;

        public SkinsAdapter(Context context) {
            super(context, 17367043, new ArrayList());
            this.inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = this.inflater.inflate(17367043, parent, false);
            }
            ((TextView) v.findViewById(16908308)).setText(((File) getItem(position)).getName());
            return v;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_skins);
        this.adapter = new SkinsAdapter(this);
        setListAdapter(this.adapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ManageSkinsActivity.this.setSkin((File) ManageSkinsActivity.this.adapter.getItem(position));
                ManageSkinsActivity.this.finish();
            }
        });
        ((Button) findViewById(R.id.manage_skins_select)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent target = FileUtils.createGetContentIntent();
                target.setType("image/png");
                target.setClass(ManageSkinsActivity.this, FileChooserActivity.class);
                ManageSkinsActivity.this.startActivityForResult(target, 7);
            }
        });
        ((Button) findViewById(R.id.manage_skins_players)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Builder b = new Builder(ManageSkinsActivity.this);
                b.setTitle(R.string.pref_zz_skin_download_source);
                b.setSingleChoiceItems(new String[]{ManageSkinsActivity.this.getString(R.string.skin_download_do_not_download), ManageSkinsActivity.this.getString(R.string.skin_download_download_pc)}, getCurrentMode(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Editor prefs = Utils.getPrefs(0).edit();
                        switch (which) {
                            case NativeRegExp.MATCH /*1*/:
                                prefs.putString("zz_skin_download_source", "mojang_pc");
                                break;
                            default:
                                prefs.putString("zz_skin_download_source", "none");
                                break;
                        }
                        prefs.apply();
                    }
                });
                b.show();
            }

            protected int getCurrentMode() {
                String mode = Utils.getPrefs(0).getString("zz_skin_download_source", "none");
                if (!mode.equals("none") && mode.equals("mojang_pc")) {
                    return 1;
                }
                return 0;
            }
        });
        setResult(0);
        loadHistory();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == -1) {
            File file = FileUtils.getFile(data.getData());
            this.adapter.add(file);
            this.adapter.notifyDataSetChanged();
            setSkin(file);
            finish();
        }
    }

    protected void onResume() {
        super.onResume();
        refreshABToggle();
    }

    protected void onPause() {
        super.onPause();
        saveHistory();
        refreshABToggle();
    }

    public void loadHistory() {
        this.adapter.clear();
        if (isEnabled()) {
            for (String s : Utils.getPrefs(1).getString("skins_history", BuildConfig.FLAVOR).split(";")) {
                File f = new File(s);
                if (f.exists() && f.canRead()) {
                    this.adapter.add(f);
                }
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public void saveHistory() {
        if (isEnabled()) {
            String out = BuildConfig.FLAVOR;
            List<String> res = new ArrayList();
            for (int l = 0; l < this.adapter.getCount(); l++) {
                File f = (File) this.adapter.getItem(l);
                if (f.exists() && f.canRead()) {
                    res.add(f.getAbsolutePath());
                }
            }
            out = Utils.join(res, ";");
            Editor sh = Utils.getPrefs(1).edit();
            sh.putString("skins_history", out);
            sh.apply();
        }
    }

    protected boolean isEnabled() {
        return Utils.getPrefs(0).getBoolean("zz_skin_enable", false);
    }

    public static void setSkin(File f, ManageSkinsActivity activity) {
        Editor p1 = Utils.getPrefs(0).edit();
        Editor p2 = Utils.getPrefs(1).edit();
        if (f.getAbsolutePath().equalsIgnoreCase(REQUEST_DISABLE.getAbsolutePath())) {
            p1.putBoolean("zz_skin_enable", false);
        } else if (f.getAbsolutePath().equalsIgnoreCase(REQUEST_ENABLE.getAbsolutePath())) {
            p1.putBoolean("zz_skin_enable", true);
        } else {
            p1.putBoolean("zz_skin_enable", true);
            p2.putString("player_skin", f.getAbsolutePath());
        }
        p1.apply();
        p2.apply();
        if (activity != null) {
            activity.refreshABToggle();
            activity.setResult(-1);
        }
    }

    protected void setSkin(File f) {
        setSkin(f, this);
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
                        ManageSkinsActivity.this.setSkin(ManageSkinsActivity.REQUEST_ENABLE);
                    } else {
                        ManageSkinsActivity.this.setSkin(ManageSkinsActivity.REQUEST_DISABLE);
                    }
                    ManageSkinsActivity.this.loadHistory();
                }
            });
            refreshABToggle();
        } else {
            System.err.println("WTF?");
        }
        return true;
    }

    protected void refreshABToggle() {
        if (VERSION.SDK_INT >= 11 && this.master != null) {
            this.master.setChecked(isEnabled());
        }
    }
}
