package net.zhuoweizhang.mcpelauncher.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import eu.chainfire.libsuperuser.Shell.SU;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.Utils;

public class ManageTexturepacksActivity extends ListActivity {
    public static final File REQUEST_DEMO = new File("/demo/textures");
    public static final File REQUEST_DISABLE = new File("/just/disable/textures");
    public static final File REQUEST_ENABLE = new File("/just/enable/textures");
    protected TexturesAdapter adapter;
    OnCheckedChangeListener ls = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                ManageTexturepacksActivity.this.setTexturepack(ManageTexturepacksActivity.REQUEST_ENABLE);
            } else {
                ManageTexturepacksActivity.this.setTexturepack(ManageTexturepacksActivity.REQUEST_DISABLE);
            }
            ManageTexturepacksActivity.this.loadHistory();
        }
    };
    protected CompoundButton master = null;

    private class ExtractTextureTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private boolean hasSu;
        private String mcpeApkLoc;
        private File outFile;

        private ExtractTextureTask() {
            this.hasSu = true;
        }

        protected void onPreExecute() {
            this.dialog = new ProgressDialog(ManageTexturepacksActivity.this);
            this.dialog.setMessage(ManageTexturepacksActivity.this.getResources().getString(R.string.extracting_textures));
            this.dialog.setIndeterminate(true);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Void doInBackground(Void... params) {
            try {
                this.mcpeApkLoc = ManageTexturepacksActivity.this.getPackageManager().getApplicationInfo("com.mojang.minecraftpe", 0).sourceDir;
            } catch (NameNotFoundException e) {
            }
            this.outFile = new File(ManageTexturepacksActivity.this.getExternalFilesDir(null), "minecraft.apk");
            if (SU.run("cat \"" + this.mcpeApkLoc + "\" >\"" + this.outFile.getAbsolutePath() + "\"") == null) {
                this.hasSu = false;
            }
            ScriptManager.clearTextureOverrides();
            return null;
        }

        protected void onPostExecute(Void result) {
            this.dialog.dismiss();
            if (this.outFile.exists()) {
                ManageTexturepacksActivity.this.adapter.add(this.outFile);
                ManageTexturepacksActivity.this.adapter.notifyDataSetChanged();
                ManageTexturepacksActivity.this.saveHistory();
                ManageTexturepacksActivity.this.setTexturepack(this.outFile);
                Toast.makeText(ManageTexturepacksActivity.this, R.string.extract_textures_success, 0).show();
                return;
            }
            new Builder(ManageTexturepacksActivity.this).setMessage(this.hasSu ? R.string.extract_textures_error : R.string.extract_textures_no_root).setPositiveButton(17039370, null).show();
        }
    }

    protected class TexturesAdapter extends ArrayAdapter<File> {
        private LayoutInflater inflater;

        public TexturesAdapter(Context context) {
            super(context, 17367043, new ArrayList());
            this.inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = this.inflater.inflate(17367043, parent, false);
            }
            TextView text = (TextView) v.findViewById(16908308);
            File f = (File) getItem(position);
            if (f.getAbsolutePath().equalsIgnoreCase(ManageTexturepacksActivity.REQUEST_DEMO.getAbsolutePath())) {
                text.setText(R.string.textures_demo);
            } else {
                text.setText(f.getName());
            }
            return v;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_textures);
        this.adapter = new TexturesAdapter(this);
        setListAdapter(this.adapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ManageTexturepacksActivity.this.setTexturepack((File) ManageTexturepacksActivity.this.adapter.getItem(position));
                ManageTexturepacksActivity.this.finish();
            }
        });
        ((Button) findViewById(R.id.manage_textures_select)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent target = FileUtils.createGetContentIntent();
                target.setType("application/zip");
                target.setClass(ManageTexturepacksActivity.this, FileChooserActivity.class);
                ManageTexturepacksActivity.this.startActivityForResult(target, 5);
            }
        });
        ((Button) findViewById(R.id.manage_textures_extract)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new ExtractTextureTask().execute(new Void[0]);
            }
        });
        setResult(0);
        loadHistory();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == -1) {
            File file = FileUtils.getFile(data.getData());
            this.adapter.add(file);
            this.adapter.notifyDataSetChanged();
            setTexturepack(file);
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
            if (canAccessMCPE()) {
                this.adapter.add(REQUEST_DEMO);
            }
            for (String s : Utils.getPrefs(0).getString("textures_history", BuildConfig.FLAVOR).split(";")) {
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
            Editor sh = Utils.getPrefs(0).edit();
            sh.putString("textures_history", out);
            sh.apply();
        }
    }

    protected boolean isEnabled() {
        return Utils.getPrefs(0).getBoolean("zz_texture_pack_enable", false);
    }

    public static void setTexturepack(File f, ManageTexturepacksActivity activity) {
        Editor p1 = Utils.getPrefs(0).edit();
        Editor p2 = Utils.getPrefs(1).edit();
        if (f.getAbsolutePath().equalsIgnoreCase(REQUEST_DISABLE.getAbsolutePath())) {
            p1.putBoolean("zz_texture_pack_enable", false);
            p1.putBoolean("zz_texture_pack_demo", false);
        } else if (f.getAbsolutePath().equalsIgnoreCase(REQUEST_ENABLE.getAbsolutePath())) {
            p1.putBoolean("zz_texture_pack_enable", true);
            if (Utils.getPrefs(1).getString("texturePack", "no_pack").equals("no_pack")) {
                p1.putBoolean("zz_texture_pack_demo", true);
            }
        } else if (f.getAbsolutePath().equalsIgnoreCase(REQUEST_DEMO.getAbsolutePath())) {
            p1.putBoolean("zz_texture_pack_enable", true);
            p1.putBoolean("zz_texture_pack_demo", true);
            p2.putString("texturePack", null);
        } else {
            p1.putBoolean("zz_texture_pack_enable", true);
            p2.putString("texturePack", f.getAbsolutePath());
            p1.putBoolean("zz_texture_pack_demo", false);
        }
        p1.apply();
        p2.apply();
        if (activity != null) {
            activity.refreshABToggle();
            activity.setResult(-1);
        }
    }

    protected void setTexturepack(File f) {
        setTexturepack(f, this);
    }

    @TargetApi(14)
    public boolean onCreateOptionsMenu(Menu menu) {
        if (VERSION.SDK_INT >= 11) {
            getMenuInflater().inflate(R.menu.ab_master, menu);
            this.master = (CompoundButton) menu.findItem(R.id.ab_switch_container).getActionView().findViewById(R.id.ab_switch);
            if (this.master != null) {
                this.master.setOnCheckedChangeListener(this.ls);
                refreshABToggle();
            } else {
                System.err.println("WTF?");
            }
        }
        menu.add(getResources().getString(R.string.textures_clear_script_texture_overrides));
        return true;
    }

    protected void refreshABToggle() {
        if (VERSION.SDK_INT >= 11 && this.master != null) {
            this.master.setOnCheckedChangeListener(null);
            this.master.setChecked(isEnabled());
            this.master.setOnCheckedChangeListener(this.ls);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (!item.getTitle().equals(getResources().getString(R.string.textures_clear_script_texture_overrides))) {
            return super.onOptionsItemSelected(item);
        }
        ScriptManager.clearTextureOverrides();
        return true;
    }

    protected boolean canAccessMCPE() {
        try {
            ApplicationInfo mcAppInfo = getPackageManager().getApplicationInfo("com.mojang.minecraftpe", 0);
            return mcAppInfo.sourceDir.equalsIgnoreCase(mcAppInfo.publicSourceDir);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
