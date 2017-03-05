package net.zhuoweizhang.mcpelauncher.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.joshuahuelsman.patchtool.PTPatch;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.zhuoweizhang.mcpelauncher.MinecraftVersion;
import net.zhuoweizhang.mcpelauncher.PatchManager;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;
import net.zhuoweizhang.mcpelauncher.ui.RefreshContentListThread.OnRefreshContentList;

public class ManagePatchesActivity extends ListActivity implements OnClickListener, OnRefreshContentList {
    private static final int DIALOG_MANAGE_PATCH = 1;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED = 2;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED = 3;
    private static final int DIALOG_PATCH_INFO = 4;
    private static final int REQUEST_IMPORT_PATCH = 212;
    private ImageButton importButton;
    private byte[] libBytes = null;
    protected CompoundButton master = null;
    private List<ContentListItem> patches;
    private boolean prePatchConfigure = true;
    private Thread refreshThread;
    private ContentListItem selectedPatchItem;

    public void onCreate(Bundle savedInstanceState) {
        Utils.setLanguageOverride();
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.manage_patches);
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ManagePatchesActivity.this.openManagePatchWindow((ContentListItem) ManagePatchesActivity.this.patches.get(position));
            }
        });
        this.importButton = (ImageButton) findViewById(R.id.manage_patches_import_button);
        this.importButton.setOnClickListener(this);
        this.prePatchConfigure = getIntent().getBooleanExtra("prePatchConfigure", true);
        PatchUtils.minecraftVersion = MinecraftVersion.get((Context) this);
    }

    protected void onStart() {
        super.onStart();
        findPatches();
    }

    protected void onPause() {
        super.onPause();
        this.libBytes = null;
    }

    public void onClick(View v) {
        if (v.equals(this.importButton)) {
            importPatch();
        }
    }

    public void importPatch() {
        Intent target = FileUtils.createGetContentIntent();
        target.setType("application/x-ptpatch");
        target.setClass(this, FileChooserActivity.class);
        target.putExtra(FileUtils.EXTRA_SORT_METHOD, FileUtils.SORT_LAST_MODIFIED);
        startActivityForResult(target, REQUEST_IMPORT_PATCH);
    }

    protected void setPatchListModified() {
        setResult(-1);
        Utils.getPrefs(DIALOG_MANAGE_PATCH).edit().putBoolean("force_prepatch", true).apply();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMPORT_PATCH /*212*/:
                if (resultCode == -1) {
                    File file = FileUtils.getFile(data.getData());
                    try {
                        File to = new File(getDir(MainActivity.PT_PATCHES_DIR, 0), file.getName());
                        PatchUtils.copy(file, to);
                        PatchManager.getPatchManager(this).setEnabled(to, false);
                        if (Utils.hasTooManyPatches()) {
                            Toast.makeText(this, R.string.manage_patches_too_many, 0).show();
                        } else {
                            PatchManager.getPatchManager(this).setEnabled(to, true);
                            afterPatchToggle(new ContentListItem(to, true));
                        }
                        setPatchListModified();
                        findPatches();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.manage_patches_import_error, DIALOG_MANAGE_PATCH).show();
                        return;
                    }
                }
                return;
            default:
                return;
        }
    }

    private void findPatches() {
        this.refreshThread = new Thread(new RefreshContentListThread(this, this));
        this.refreshThread.start();
    }

    private void openManagePatchWindow(ContentListItem item) {
        this.selectedPatchItem = item;
        if (this.prePatchConfigure || canLivePatch(item)) {
            showDialog(item.enabled ? DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED : DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED);
        } else {
            Toast.makeText(this, "This patch cannot be disabled in game.", 0).show();
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_MANAGE_PATCH /*1*/:
                return createManagePatchDialog(-1);
            case DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED /*2*/:
                return createManagePatchDialog(0);
            case DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED /*3*/:
                return createManagePatchDialog(DIALOG_MANAGE_PATCH);
            case DIALOG_PATCH_INFO /*4*/:
                return createPatchInfoDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_MANAGE_PATCH /*1*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED /*2*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED /*3*/:
                ((AlertDialog) dialog).setTitle(this.selectedPatchItem.toString(getResources()));
                return;
            case DIALOG_PATCH_INFO /*4*/:
                preparePatchInfo((AlertDialog) dialog, this.selectedPatchItem);
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    public void togglePatch(ContentListItem patch) {
        boolean z = true;
        if (patch.enabled || !Utils.hasTooManyPatches()) {
            boolean z2;
            PatchManager patchManager = PatchManager.getPatchManager(this);
            File file = patch.file;
            if (patch.enabled) {
                z2 = false;
            } else {
                z2 = true;
            }
            patchManager.setEnabled(file, z2);
            if (patch.enabled) {
                z = false;
            }
            patch.enabled = z;
            afterPatchToggle(patch);
            return;
        }
        Toast.makeText(this, R.string.manage_patches_too_many, 0).show();
    }

    private void afterPatchToggle(ContentListItem patch) {
        if (!isValidPatch(patch)) {
            PatchManager.getPatchManager(this).setEnabled(patch.file, false);
            new Builder(this).setMessage(getResources().getString(R.string.manage_patches_invalid_patches) + " " + patch.displayName).setPositiveButton(17039370, null).show();
        } else if (this.prePatchConfigure) {
            setPatchListModified();
        } else if (canLivePatch(patch)) {
            try {
                livePatch(patch);
                Utils.getPrefs(DIALOG_MANAGE_PATCH).edit().putBoolean("force_prepatch", true).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setPatchListModified();
        }
    }

    public void livePatch(ContentListItem patchItem) throws Exception {
        ApplicationInfo mcAppInfo = getPackageManager().getApplicationInfo("com.mojang.minecraftpe", 0);
        File patched = getDir("patched", 0);
        File originalLibminecraft = new File(mcAppInfo.nativeLibraryDir + "/libminecraftpe.so");
        File file = new File(patched, "libminecraftpe.so");
        PTPatch patch = new PTPatch();
        patch.loadPatch(patchItem.file);
        if (patchItem.enabled) {
            PatchUtils.patch(MainActivity.minecraftLibBuffer, patch);
            return;
        }
        if (this.libBytes == null) {
            this.libBytes = new byte[((int) originalLibminecraft.length())];
            InputStream is = new FileInputStream(originalLibminecraft);
            is.read(this.libBytes);
            is.close();
        }
        PatchUtils.unpatch(MainActivity.minecraftLibBuffer, this.libBytes, patch);
    }

    public boolean canLivePatch(ContentListItem patch) {
        try {
            return PatchUtils.canLivePatch(patch.file);
        } catch (Exception e) {
            return false;
        }
    }

    public void deletePatch(ContentListItem patch) throws Exception {
        patch.enabled = false;
        if (!this.prePatchConfigure) {
            livePatch(patch);
            Utils.getPrefs(DIALOG_MANAGE_PATCH).edit().putBoolean("force_prepatch", true).apply();
        }
        setPatchListModified();
        patch.file.delete();
    }

    public void preparePatchInfo(AlertDialog dialog, ContentListItem patch) {
        String patchInfo;
        dialog.setTitle(patch.toString(getResources()));
        try {
            patchInfo = getPatchInfo(patch);
        } catch (Exception e) {
            patchInfo = "Cannot show info: " + e.getStackTrace();
        }
        dialog.setMessage(patchInfo);
    }

    private String getPatchInfo(ContentListItem patchItem) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(getResources().getString(R.string.manage_patches_path));
        builder.append(": ");
        builder.append(patchItem.file.getAbsolutePath());
        builder.append('\n');
        PTPatch patch = new PTPatch();
        patch.loadPatch(patchItem.file);
        String desc = patch.getDescription();
        if (desc.length() > 0) {
            builder.append(getResources().getString(R.string.manage_patches_metadata));
            builder.append(": ");
            builder.append(desc);
        } else {
            builder.append(getResources().getString(R.string.manage_patches_no_metadata));
        }
        return builder.toString();
    }

    protected AlertDialog createManagePatchDialog(int enableStatus) {
        CharSequence[] options;
        CharSequence patchInfoStr = getResources().getText(R.string.manage_patches_info);
        if (enableStatus == -1) {
            options = new CharSequence[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED];
            options[0] = getResources().getText(R.string.manage_patches_delete);
            options[DIALOG_MANAGE_PATCH] = patchInfoStr;
        } else {
            options = new CharSequence[DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED];
            options[0] = getResources().getText(R.string.manage_patches_delete);
            options[DIALOG_MANAGE_PATCH] = patchInfoStr;
            options[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED] = enableStatus == 0 ? getResources().getText(R.string.manage_patches_enable) : getResources().getText(R.string.manage_patches_disable);
        }
        return new Builder(this).setTitle("Patch name goes here").setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                if (button == 0) {
                    try {
                        ManagePatchesActivity.this.deletePatch(ManagePatchesActivity.this.selectedPatchItem);
                        ManagePatchesActivity.this.findPatches();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (button == ManagePatchesActivity.DIALOG_MANAGE_PATCH) {
                    ManagePatchesActivity.this.showDialog(ManagePatchesActivity.DIALOG_PATCH_INFO);
                } else if (button == ManagePatchesActivity.DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED) {
                    ManagePatchesActivity.this.togglePatch(ManagePatchesActivity.this.selectedPatchItem);
                    ManagePatchesActivity.this.findPatches();
                }
            }
        }).create();
    }

    private AlertDialog createPatchInfoDialog() {
        return new Builder(this).setTitle("Whoops! info fail").setMessage("Whoops - try again, this is a tiny fail").setPositiveButton(17039370, null).create();
    }

    private boolean isValidPatch(ContentListItem patch) {
        if (patch.file.length() < 6) {
            return false;
        }
        return true;
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
                        ManagePatchesActivity.this.findPatches();
                    } else {
                        ((ArrayAdapter) ManagePatchesActivity.this.getListAdapter()).clear();
                    }
                    Editor sh = Utils.getPrefs(0).edit();
                    sh.putBoolean("zz_manage_patches", isChecked);
                    sh.apply();
                    ManagePatchesActivity.this.refreshABToggle();
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
            this.master.setChecked(Utils.getPrefs(0).getBoolean("zz_manage_patches", true));
        }
    }

    public void onRefreshComplete(final List<ContentListItem> items) {
        runOnUiThread(new Runnable() {
            public void run() {
                ContentListItem.sort(items);
                ManagePatchesActivity.this.patches = items;
                ManagePatchesActivity.this.setListAdapter(new ContentListAdapter(ManagePatchesActivity.this, R.layout.patch_list_item, ManagePatchesActivity.this.patches));
                List<String> allPaths = new ArrayList(ManagePatchesActivity.this.patches.size());
                for (ContentListItem i : ManagePatchesActivity.this.patches) {
                    allPaths.add(i.file.getAbsolutePath());
                }
                PatchManager.getPatchManager(ManagePatchesActivity.this).removeDeadEntries(allPaths);
            }
        });
    }

    public List<File> getFolders() {
        List<File> folders = new ArrayList();
        folders.add(getDir(MainActivity.PT_PATCHES_DIR, 0));
        folders.add(new File(Environment.getExternalStorageDirectory(), "Android/data/com.snowbound.pockettool.free/Patches"));
        folders.add(new File(Environment.getExternalStorageDirectory(), "Android/data/com.joshuahuelsman.pockettool/Patches"));
        return folders;
    }

    public boolean isEnabled(File f) {
        return PatchManager.getPatchManager(this).isEnabled(f);
    }
}
