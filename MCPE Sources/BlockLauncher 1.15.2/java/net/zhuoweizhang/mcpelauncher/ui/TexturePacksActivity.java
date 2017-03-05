package net.zhuoweizhang.mcpelauncher.ui;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import eu.chainfire.libsuperuser.Shell.SU;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.texture.TexturePackDescription;
import net.zhuoweizhang.mcpelauncher.texture.TexturePackLoader;

public class TexturePacksActivity extends ListActivity implements OnClickListener {
    private static final int REQUEST_ADD_TEXTURE = 522;
    private ArrayAdapter<TexturePackDescription> adapter;
    private ImageButton addButton;
    private List<TexturePackDescription> list;

    private class ExtractTextureTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private boolean hasSu;
        private String mcpeApkLoc;
        private File outFile;

        private ExtractTextureTask() {
            this.hasSu = true;
        }

        protected void onPreExecute() {
            this.dialog = new ProgressDialog(TexturePacksActivity.this);
            this.dialog.setMessage(TexturePacksActivity.this.getResources().getString(R.string.extracting_textures));
            this.dialog.setIndeterminate(true);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Void doInBackground(Void... params) {
            try {
                this.mcpeApkLoc = TexturePacksActivity.this.getPackageManager().getApplicationInfo("com.mojang.minecraftpe", 0).sourceDir;
            } catch (NameNotFoundException e) {
            }
            this.outFile = new File(TexturePacksActivity.this.getExternalFilesDir(null), "minecraft.apk");
            this.outFile.delete();
            String outPath = this.outFile.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
            System.out.println(outPath);
            if (SU.run("cat \"" + this.mcpeApkLoc + "\" >\"" + outPath + "\"") == null) {
                this.hasSu = false;
            }
            ScriptManager.clearTextureOverrides();
            return null;
        }

        protected void onPostExecute(Void result) {
            this.dialog.dismiss();
            if (this.outFile.exists()) {
                TexturePacksActivity.this.addTexturePack(TexturePacksActivity.this.list.size(), this.outFile);
                Toast.makeText(TexturePacksActivity.this, R.string.extract_textures_success, 0).show();
                return;
            }
            new Builder(TexturePacksActivity.this).setMessage(this.hasSu ? R.string.extract_textures_error : R.string.extract_textures_no_root).setPositiveButton(17039370, null).show();
        }
    }

    private class TexturesAdapter extends ArrayAdapter<TexturePackDescription> {
        private LayoutInflater inflater;

        public TexturesAdapter(Context context) {
            super(context, R.layout.texture_pack_entry, R.id.texture_entry_name, TexturePacksActivity.this.list);
            this.inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View v, ViewGroup parent) {
            int i;
            boolean z;
            boolean z2 = true;
            if (v == null) {
                v = this.inflater.inflate(R.layout.texture_pack_entry, parent, false);
            }
            TexturePackDescription item = (TexturePackDescription) getItem(position);
            v.findViewById(R.id.texture_entry_container).setTag(Integer.valueOf(position));
            ((TextView) v.findViewById(R.id.texture_entry_name)).setText(TexturePackLoader.describeTexturePack(TexturePacksActivity.this, item));
            TextView desc = (TextView) v.findViewById(R.id.texture_entry_desc);
            desc.setText(item.description);
            if (item.description.length() == 0) {
                i = 8;
            } else {
                i = 0;
            }
            desc.setVisibility(i);
            View up = v.findViewById(R.id.texture_entry_up);
            if (position != 0) {
                z = true;
            } else {
                z = false;
            }
            up.setEnabled(z);
            View down = v.findViewById(R.id.texture_entry_down);
            if (position == getCount() - 1) {
                z2 = false;
            }
            down.setEnabled(z2);
            ((ImageView) v.findViewById(R.id.texture_entry_img)).setImageDrawable(item.img != null ? new BitmapDrawable(item.img) : null);
            return v;
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            this.list = TexturePackLoader.loadDescriptionsWithIcons(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.list = new ArrayList();
        }
        if (new File("/sdcard/bl_clearTextures").exists()) {
            this.list = new ArrayList();
        }
        setContentView(R.layout.manage_textures);
        this.adapter = new TexturesAdapter(this);
        setListAdapter(this.adapter);
        this.addButton = (ImageButton) findViewById(R.id.manage_textures_select);
        this.addButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == this.addButton) {
            Intent target = FileUtils.createGetContentIntent();
            target.setType("application/zip");
            target.putExtra(FileUtils.EXTRA_MIME_TYPES, new String[]{"application/zip", "application/x-appx", "application/vnd.android.package-archive"});
            target.setClass(this, FileChooserActivity.class);
            startActivityForResult(target, REQUEST_ADD_TEXTURE);
        }
    }

    private void saveState() {
        try {
            TexturePackLoader.saveDescriptions(this, this.list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTexturePack(int index, File f) {
        addTexturePack(index, new TexturePackDescription(TexturePackLoader.TYPE_ZIP, f.getAbsolutePath()));
    }

    private void addTexturePack(int index, TexturePackDescription desc) {
        Utils.getPrefs(0).edit().putBoolean("zz_texture_pack_enable", true).apply();
        for (TexturePackDescription d : this.list) {
            if (d.path.equals(desc.path)) {
                return;
            }
        }
        if (desc.img == null) {
            try {
                TexturePackLoader.loadIconForDescription(desc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.list.add(index, desc);
        updateContents();
    }

    private void updateContents() {
        this.adapter.notifyDataSetChanged();
        saveState();
        setResult(-1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TEXTURE && resultCode == -1) {
            addTexturePack(0, FileUtils.getFile(data.getData()));
        }
    }

    public void onTextureUpClick(View v) {
        int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
        this.list.add(index - 1, (TexturePackDescription) this.list.remove(index));
        updateContents();
    }

    public void onTextureDownClick(View v) {
        int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
        this.list.add(index + 1, (TexturePackDescription) this.list.remove(index));
        updateContents();
    }

    public void onTextureRemoveClick(View v) {
        TexturePackDescription desc = (TexturePackDescription) this.list.remove(((Integer) ((View) v.getParent()).getTag()).intValue());
        updateContents();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_textures_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.manage_textures_extract) {
            new ExtractTextureTask().execute(new Void[0]);
            return true;
        } else if (item.getItemId() != R.id.manage_textures_clear_script_texture_overrides) {
            return super.onOptionsItemSelected(item);
        } else {
            ScriptManager.clearTextureOverrides();
            Toast.makeText(this, R.string.textures_clear_script_texture_overrides, 0).show();
            return true;
        }
    }
}
