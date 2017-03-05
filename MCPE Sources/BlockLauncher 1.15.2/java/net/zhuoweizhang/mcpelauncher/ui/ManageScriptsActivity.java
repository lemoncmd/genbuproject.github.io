package net.zhuoweizhang.mcpelauncher.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.CoffeeScriptCompiler;
import net.zhuoweizhang.mcpelauncher.KamcordConstants;
import net.zhuoweizhang.mcpelauncher.MinecraftConstants;
import net.zhuoweizhang.mcpelauncher.MissingTextureException;
import net.zhuoweizhang.mcpelauncher.MpepInfo;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;
import net.zhuoweizhang.mcpelauncher.ui.RefreshContentListThread.OnRefreshContentList;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.WrappedException;

public class ManageScriptsActivity extends ListActivity implements OnClickListener, OnRefreshContentList {
    private static final String[] ALL_SCRIPT_MIMETYPES;
    private static final int DIALOG_IMPORT_FROM_CFGY = 6;
    private static final int DIALOG_IMPORT_FROM_CLIPBOARD = 9;
    private static final int DIALOG_IMPORT_FROM_CLIPBOARD_CODE = 10;
    private static final int DIALOG_IMPORT_FROM_INTENT = 11;
    private static final int DIALOG_IMPORT_FROM_URL = 7;
    private static final int DIALOG_IMPORT_SOURCES = 5;
    private static final int DIALOG_MANAGE_PATCH = 1;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED = 2;
    private static final int DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED = 3;
    private static final int DIALOG_PATCH_INFO = 4;
    private static final int DIALOG_VERSION_INCOMPATIBLE = 8;
    private static final int REQUEST_IMPORT_PATCH = 212;
    private static char[] cfgyMappings = new char[]{'f', 't', 'a', 'm', 'b', 'q', 'g', 'r', 'z', 'o'};
    protected ArrayAdapter<ContentListItem> adapter;
    private ImageButton importButton;
    private String importClipboardName = BuildConfig.FLAVOR;
    protected CompoundButton master;
    private List<ContentListItem> patches;
    private Thread refreshThread;
    private ContentListItem selectedPatchItem;

    private abstract class ImportScriptTask<I> extends AsyncTask<I, Void, File> {
        private ImportScriptTask() {
        }

        protected void onPostExecute(File file) {
            if (file == null) {
                Toast.makeText(ManageScriptsActivity.this, R.string.manage_patches_import_error, 0).show();
                return;
            }
            try {
                ScriptManager.setEnabled(file, false);
                int maxPatchCount = ManageScriptsActivity.this.getMaxPatchCount();
                if (Utils.hasTooManyScripts()) {
                    Toast.makeText(ManageScriptsActivity.this, R.string.script_import_too_many, 0).show();
                } else {
                    ScriptManager.setEnabled(file, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ManageScriptsActivity.this.reportError(e);
                Toast.makeText(ManageScriptsActivity.this, R.string.manage_patches_import_error, 0).show();
            }
            ManageScriptsActivity.this.findScripts();
        }
    }

    private class ImportScriptFromCfgyTask extends ImportScriptTask<String> {
        private ImportScriptFromCfgyTask() {
            super();
        }

        protected File doInBackground(String... ids) {
            Exception e;
            Throwable th;
            String id = ids[0];
            InputStream is = null;
            byte[] content = null;
            int response = 0;
            FileOutputStream fos = null;
            File file = new File(ManageScriptsActivity.this.getDir(ScriptManager.SCRIPTS_DIR, 0), id + ".js");
            try {
                HttpURLConnection conn = (HttpURLConnection) ManageScriptsActivity.getCfgyUrl(id).openConnection();
                conn.setRequestProperty("User-Agent", KamcordConstants.GAME_NAME);
                conn.setDoInput(true);
                conn.connect();
                try {
                    response = conn.getResponseCode();
                    is = conn.getInputStream();
                } catch (Exception e2) {
                    is = conn.getErrorStream();
                }
                if (response >= 400) {
                    file = null;
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e3) {
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e4) {
                        }
                    }
                } else {
                    if (is != null) {
                        content = ManageScriptsActivity.bytesFromInputStream(is, conn.getContentLength() > 0 ? conn.getContentLength() : EnchantType.pickaxe);
                    }
                    byte[] decoded = Base64.decode(new String(content).replaceAll(" ", "+"), 0);
                    FileOutputStream fos2 = new FileOutputStream(file);
                    try {
                        fos2.write(decoded);
                        fos2.flush();
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e5) {
                            }
                        }
                        if (fos2 != null) {
                            try {
                                fos2.close();
                            } catch (Exception e6) {
                            }
                        }
                        fos = fos2;
                    } catch (Exception e7) {
                        e = e7;
                        fos = fos2;
                        try {
                            e.printStackTrace();
                            ManageScriptsActivity.this.reportError(e);
                            file = null;
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception e8) {
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (Exception e9) {
                                }
                            }
                            return file;
                        } catch (Throwable th2) {
                            th = th2;
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception e10) {
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (Exception e11) {
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fos = fos2;
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                        throw th;
                    }
                }
            } catch (Exception e12) {
                e = e12;
                e.printStackTrace();
                ManageScriptsActivity.this.reportError(e);
                file = null;
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                return file;
            }
            return file;
        }
    }

    private class ImportScriptFromIntentTask extends ImportScriptTask<Uri> {
        private ImportScriptFromIntentTask() {
            super();
        }

        protected File doInBackground(Uri... ids) {
            Exception e;
            Throwable th;
            Uri data = ids[0];
            InputStream is = null;
            FileOutputStream fos = null;
            File file = new File(ManageScriptsActivity.this.getDir(ScriptManager.SCRIPTS_DIR, 0), data.getLastPathSegment());
            try {
                is = ManageScriptsActivity.this.getContentResolver().openInputStream(data);
                byte[] content = ManageScriptsActivity.bytesFromInputStream(is, EnchantType.pickaxe);
                FileOutputStream fos2 = new FileOutputStream(file);
                try {
                    fos2.write(content);
                    fos2.flush();
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e2) {
                        }
                    }
                    if (fos2 != null) {
                        try {
                            fos2.close();
                        } catch (Exception e3) {
                        }
                    }
                    fos = fos2;
                } catch (Exception e4) {
                    e = e4;
                    fos = fos2;
                    try {
                        e.printStackTrace();
                        file = null;
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e5) {
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (Exception e6) {
                            }
                        }
                        return file;
                    } catch (Throwable th2) {
                        th = th2;
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e7) {
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (Exception e8) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fos = fos2;
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            } catch (Exception e9) {
                e = e9;
                e.printStackTrace();
                file = null;
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                return file;
            }
            return file;
        }
    }

    private class ImportScriptFromUrlTask extends ImportScriptTask<String> {
        private ImportScriptFromUrlTask() {
            super();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected java.io.File doInBackground(java.lang.String... r16) {
            /*
            r15 = this;
            r8 = 0;
            r1 = 0;
            r9 = 0;
            r6 = 0;
            r3 = 0;
            r10 = new java.net.URL;	 Catch:{ Exception -> 0x00a6 }
            r12 = 0;
            r12 = r16[r12];	 Catch:{ Exception -> 0x00a6 }
            r10.<init>(r12);	 Catch:{ Exception -> 0x00a6 }
            r12 = java.lang.System.out;	 Catch:{ Exception -> 0x00a6 }
            r12.println(r10);	 Catch:{ Exception -> 0x00a6 }
            r11 = r10.getPath();	 Catch:{ Exception -> 0x00a6 }
            r12 = "/";
            r12 = r11.lastIndexOf(r12);	 Catch:{ Exception -> 0x00a6 }
            r12 = r12 + 1;
            r5 = r11.substring(r12);	 Catch:{ Exception -> 0x00a6 }
            r12 = r5.length();	 Catch:{ Exception -> 0x00a6 }
            r13 = 1;
            if (r12 >= r13) goto L_0x002b;
        L_0x0029:
            r5 = "nameless_script.js";
        L_0x002b:
            r4 = new java.io.File;	 Catch:{ Exception -> 0x00a6 }
            r12 = net.zhuoweizhang.mcpelauncher.ui.ManageScriptsActivity.this;	 Catch:{ Exception -> 0x00a6 }
            r13 = "modscripts";
            r14 = 0;
            r12 = r12.getDir(r13, r14);	 Catch:{ Exception -> 0x00a6 }
            r4.<init>(r12, r5);	 Catch:{ Exception -> 0x00a6 }
            r0 = r10.openConnection();	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r0 = (java.net.HttpURLConnection) r0;	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r12 = "User-Agent";
            r13 = "BlockLauncher";
            r0.setRequestProperty(r12, r13);	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r12 = 1;
            r0.setDoInput(r12);	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r0.connect();	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r9 = r0.getResponseCode();	 Catch:{ Exception -> 0x0067, all -> 0x00db }
            r8 = r0.getInputStream();	 Catch:{ Exception -> 0x0067, all -> 0x00db }
        L_0x0055:
            r12 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
            if (r9 < r12) goto L_0x006d;
        L_0x0059:
            r12 = 0;
            if (r8 == 0) goto L_0x005f;
        L_0x005c:
            r8.close();	 Catch:{ Exception -> 0x00c9 }
        L_0x005f:
            if (r6 == 0) goto L_0x0064;
        L_0x0061:
            r6.close();	 Catch:{ Exception -> 0x00cb }
        L_0x0064:
            r3 = r4;
            r4 = r12;
        L_0x0066:
            return r4;
        L_0x0067:
            r2 = move-exception;
            r8 = r0.getErrorStream();	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            goto L_0x0055;
        L_0x006d:
            if (r8 != 0) goto L_0x007d;
        L_0x006f:
            r12 = 0;
            if (r8 == 0) goto L_0x0075;
        L_0x0072:
            r8.close();	 Catch:{ Exception -> 0x00cd }
        L_0x0075:
            if (r6 == 0) goto L_0x007a;
        L_0x0077:
            r6.close();	 Catch:{ Exception -> 0x00cf }
        L_0x007a:
            r3 = r4;
            r4 = r12;
            goto L_0x0066;
        L_0x007d:
            r12 = r0.getContentLength();	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            if (r12 <= 0) goto L_0x00a3;
        L_0x0083:
            r12 = r0.getContentLength();	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
        L_0x0087:
            r1 = net.zhuoweizhang.mcpelauncher.ui.ManageScriptsActivity.bytesFromInputStream(r8, r12);	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r7 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r7.<init>(r4);	 Catch:{ Exception -> 0x00e2, all -> 0x00db }
            r7.write(r1);	 Catch:{ Exception -> 0x00e5, all -> 0x00de }
            r7.flush();	 Catch:{ Exception -> 0x00e5, all -> 0x00de }
            if (r8 == 0) goto L_0x009b;
        L_0x0098:
            r8.close();	 Catch:{ Exception -> 0x00d1 }
        L_0x009b:
            if (r7 == 0) goto L_0x00a0;
        L_0x009d:
            r7.close();	 Catch:{ Exception -> 0x00d3 }
        L_0x00a0:
            r3 = r4;
            r6 = r7;
            goto L_0x0066;
        L_0x00a3:
            r12 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            goto L_0x0087;
        L_0x00a6:
            r2 = move-exception;
        L_0x00a7:
            r12 = net.zhuoweizhang.mcpelauncher.ui.ManageScriptsActivity.this;	 Catch:{ all -> 0x00bd }
            r12.reportError(r2);	 Catch:{ all -> 0x00bd }
            r2.printStackTrace();	 Catch:{ all -> 0x00bd }
            r4 = 0;
            if (r8 == 0) goto L_0x00b5;
        L_0x00b2:
            r8.close();	 Catch:{ Exception -> 0x00d5 }
        L_0x00b5:
            if (r6 == 0) goto L_0x0066;
        L_0x00b7:
            r6.close();	 Catch:{ Exception -> 0x00bb }
            goto L_0x0066;
        L_0x00bb:
            r12 = move-exception;
            goto L_0x0066;
        L_0x00bd:
            r12 = move-exception;
        L_0x00be:
            if (r8 == 0) goto L_0x00c3;
        L_0x00c0:
            r8.close();	 Catch:{ Exception -> 0x00d7 }
        L_0x00c3:
            if (r6 == 0) goto L_0x00c8;
        L_0x00c5:
            r6.close();	 Catch:{ Exception -> 0x00d9 }
        L_0x00c8:
            throw r12;
        L_0x00c9:
            r13 = move-exception;
            goto L_0x005f;
        L_0x00cb:
            r13 = move-exception;
            goto L_0x0064;
        L_0x00cd:
            r13 = move-exception;
            goto L_0x0075;
        L_0x00cf:
            r13 = move-exception;
            goto L_0x007a;
        L_0x00d1:
            r12 = move-exception;
            goto L_0x009b;
        L_0x00d3:
            r12 = move-exception;
            goto L_0x00a0;
        L_0x00d5:
            r12 = move-exception;
            goto L_0x00b5;
        L_0x00d7:
            r13 = move-exception;
            goto L_0x00c3;
        L_0x00d9:
            r13 = move-exception;
            goto L_0x00c8;
        L_0x00db:
            r12 = move-exception;
            r3 = r4;
            goto L_0x00be;
        L_0x00de:
            r12 = move-exception;
            r3 = r4;
            r6 = r7;
            goto L_0x00be;
        L_0x00e2:
            r2 = move-exception;
            r3 = r4;
            goto L_0x00a7;
        L_0x00e5:
            r2 = move-exception;
            r3 = r4;
            r6 = r7;
            goto L_0x00a7;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.zhuoweizhang.mcpelauncher.ui.ManageScriptsActivity.ImportScriptFromUrlTask.doInBackground(java.lang.String[]):java.io.File");
        }
    }

    static {
        String[] strArr = new String[DIALOG_PATCH_INFO];
        strArr[0] = "application/javascript";
        strArr[DIALOG_MANAGE_PATCH] = "text/coffeescript";
        strArr[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED] = "text/literate-coffeescript";
        strArr[DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED] = "application/x-mpep";
        ALL_SCRIPT_MIMETYPES = strArr;
    }

    public void onCreate(Bundle savedInstanceState) {
        Utils.setLanguageOverride();
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.manage_patches);
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ManageScriptsActivity.this.openManagePatchWindow((ContentListItem) ManageScriptsActivity.this.patches.get(position));
            }
        });
        this.importButton = (ImageButton) findViewById(R.id.manage_patches_import_button);
        this.importButton.setOnClickListener(this);
        ScriptManager.androidContext = getApplicationContext();
        this.patches = new ArrayList();
        this.adapter = new ContentListAdapter(this, R.layout.patch_list_item, this.patches);
        setListAdapter(this.adapter);
    }

    @TargetApi(14)
    public boolean onCreateOptionsMenu(Menu menu) {
        if (VERSION.SDK_INT < DIALOG_IMPORT_FROM_INTENT) {
            return false;
        }
        getMenuInflater().inflate(R.menu.ab_master, menu);
        this.master = (CompoundButton) menu.findItem(R.id.ab_switch_container).getActionView().findViewById(R.id.ab_switch);
        if (this.master != null) {
            this.master.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ManageScriptsActivity.this.findScripts();
                    } else {
                        ((ArrayAdapter) ManageScriptsActivity.this.getListAdapter()).clear();
                    }
                    Editor sh = PreferenceManager.getDefaultSharedPreferences(ManageScriptsActivity.this).edit();
                    sh.putBoolean("zz_script_enable", isChecked);
                    sh.apply();
                    ManageScriptsActivity.this.refreshABToggle();
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
        findScripts();
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
        if (VERSION.SDK_INT >= DIALOG_IMPORT_FROM_INTENT && this.master != null) {
            this.master.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("zz_script_enable", true));
        }
    }

    public void onClick(View v) {
        if (v == this.importButton) {
            importPatch();
        }
    }

    public void importPatch() {
        showDialog(DIALOG_IMPORT_SOURCES);
    }

    public void importPatchFromFile() {
        Intent target = FileUtils.createGetContentIntent();
        target.setType("application/javascript");
        target.setClass(this, FileChooserActivity.class);
        target.putExtra(FileUtils.EXTRA_MIME_TYPES, ALL_SCRIPT_MIMETYPES);
        target.putExtra(FileUtils.EXTRA_SORT_METHOD, FileUtils.SORT_LAST_MODIFIED);
        startActivityForResult(target, REQUEST_IMPORT_PATCH);
    }

    protected int getMaxPatchCount() {
        return getResources().getInteger(R.integer.max_num_scripts);
    }

    protected void setPatchListModified() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Exception e;
        switch (requestCode) {
            case REQUEST_IMPORT_PATCH /*212*/:
                if (resultCode == -1) {
                    File file = FileUtils.getFile(data.getData());
                    File file2 = null;
                    try {
                        File to;
                        if (CoffeeScriptCompiler.isCoffeeScript(file)) {
                            to = new File(getDir(ScriptManager.SCRIPTS_DIR, 0), CoffeeScriptCompiler.outputName(file.getName()));
                            try {
                                CoffeeScriptCompiler.compileFile(file, to);
                                file2 = to;
                            } catch (Exception e2) {
                                e = e2;
                                file2 = to;
                                e.printStackTrace();
                                if (!checkModPkgTextureError(e, file2)) {
                                    reportError(e);
                                    Toast.makeText(this, R.string.manage_patches_import_error, DIALOG_MANAGE_PATCH).show();
                                    return;
                                }
                                return;
                            }
                        }
                        to = new File(getDir(ScriptManager.SCRIPTS_DIR, 0), file.getName());
                        PatchUtils.copy(file, to);
                        ScriptManager.setOriginalLocation(file, to);
                        file2 = to;
                        ScriptManager.setEnabled(file2, false);
                        int maxPatchCount = getMaxPatchCount();
                        if (Utils.hasTooManyScripts()) {
                            Toast.makeText(this, R.string.script_import_too_many, 0).show();
                        } else {
                            ScriptManager.setEnabled(file2, true);
                            afterPatchToggle(new ContentListItem(file2, true));
                        }
                        setPatchListModified();
                        findScripts();
                        return;
                    } catch (Exception e3) {
                        e = e3;
                        e.printStackTrace();
                        if (!checkModPkgTextureError(e, file2)) {
                            reportError(e);
                            Toast.makeText(this, R.string.manage_patches_import_error, DIALOG_MANAGE_PATCH).show();
                            return;
                        }
                        return;
                    }
                }
                return;
            default:
                return;
        }
    }

    private boolean checkModPkgTextureError(Exception e, File to) {
        boolean isModpkg;
        Throwable unwrapped = e;
        if (e instanceof WrappedException) {
            unwrapped = ((WrappedException) e).getWrappedException();
        }
        if (to == null || !to.getName().toLowerCase().endsWith(".modpkg")) {
            isModpkg = false;
        } else {
            isModpkg = true;
        }
        if (!isModpkg || !(unwrapped instanceof MissingTextureException)) {
            return false;
        }
        try {
            ScriptManager.setEnabledWithoutLoad(to, true);
            afterPatchToggle(new ContentListItem(to, true));
            setResult(-1);
            return true;
        } catch (Exception ee) {
            ee.printStackTrace();
            reportError(ee);
            return true;
        }
    }

    private void findScripts() {
        this.refreshThread = new Thread(new RefreshContentListThread(this, this));
        this.refreshThread.start();
    }

    private void openManagePatchWindow(ContentListItem item) {
        this.selectedPatchItem = item;
        showDialog(item.enabled ? DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED : DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED);
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
            case DIALOG_IMPORT_SOURCES /*5*/:
                return createImportSourcesDialog();
            case DIALOG_IMPORT_FROM_CFGY /*6*/:
                return createImportFromCfgyDialog();
            case DIALOG_IMPORT_FROM_URL /*7*/:
                return createImportFromUrlDialog();
            case DIALOG_VERSION_INCOMPATIBLE /*8*/:
                return createVersionIncompatibleDialog();
            case DIALOG_IMPORT_FROM_CLIPBOARD /*9*/:
                return createImportFromClipboardDialog();
            case DIALOG_IMPORT_FROM_CLIPBOARD_CODE /*10*/:
                return createImportFromClipboardCodeDialog();
            case DIALOG_IMPORT_FROM_INTENT /*11*/:
                return createImportFromIntentDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_MANAGE_PATCH /*1*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED /*2*/:
            case DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED /*3*/:
                if (this.selectedPatchItem != null) {
                    ((AlertDialog) dialog).setTitle(this.selectedPatchItem.toString(getResources()));
                    return;
                }
                return;
            case DIALOG_PATCH_INFO /*4*/:
                if (this.selectedPatchItem != null) {
                    preparePatchInfo((AlertDialog) dialog, this.selectedPatchItem);
                    return;
                }
                return;
            case DIALOG_IMPORT_FROM_CLIPBOARD_CODE /*10*/:
                AlertDialog bDialog = (AlertDialog) dialog;
                ((EditText) bDialog.findViewById(20130805)).setText(((ClipboardManager) getSystemService("clipboard")).getText());
                return;
            case DIALOG_IMPORT_FROM_INTENT /*11*/:
                ((AlertDialog) dialog).setTitle(getIntent().getData().toString());
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    public void togglePatch(ContentListItem patch) {
        boolean z = true;
        int maxPatchCount = getMaxPatchCount();
        if (patch.enabled || !Utils.hasTooManyScripts()) {
            try {
                ScriptManager.setEnabled(patch.file, !patch.enabled);
            } catch (Exception e) {
                e.printStackTrace();
                if (!checkModPkgTextureError(e, patch.file)) {
                    reportScriptLoadError(e);
                }
            }
            if (patch.enabled) {
                z = false;
            }
            patch.enabled = z;
            afterPatchToggle(patch);
            return;
        }
        Toast.makeText(this, R.string.script_import_too_many, 0).show();
    }

    private void reportScriptLoadError(Exception e) {
        reportError(e);
    }

    private void afterPatchToggle(ContentListItem patch) {
    }

    public void deletePatch(ContentListItem patch) throws Exception {
        patch.enabled = false;
        try {
            ScriptManager.setEnabled(patch.file, false);
        } catch (Exception e) {
            e.printStackTrace();
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
        return builder.toString();
    }

    protected AlertDialog createManagePatchDialog(int enableStatus) {
        CharSequence[] options;
        CharSequence patchInfoStr = getResources().getText(R.string.manage_patches_info);
        CharSequence viewSourceStr = getResources().getText(R.string.script_view_source);
        if (enableStatus == -1) {
            options = new CharSequence[DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED];
            options[0] = getResources().getText(R.string.manage_patches_delete);
            options[DIALOG_MANAGE_PATCH] = patchInfoStr;
            options[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED] = viewSourceStr;
        } else {
            options = new CharSequence[DIALOG_PATCH_INFO];
            options[0] = getResources().getText(R.string.manage_patches_delete);
            options[DIALOG_MANAGE_PATCH] = patchInfoStr;
            options[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED] = viewSourceStr;
            options[DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED] = enableStatus == 0 ? getResources().getText(R.string.manage_patches_enable) : getResources().getText(R.string.manage_patches_disable);
        }
        return new Builder(this).setTitle("Patch name goes here").setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                if (button == 0) {
                    try {
                        ManageScriptsActivity.this.deletePatch(ManageScriptsActivity.this.selectedPatchItem);
                        ManageScriptsActivity.this.findScripts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH) {
                    ManageScriptsActivity.this.showDialog(ManageScriptsActivity.DIALOG_PATCH_INFO);
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED) {
                    ManageScriptsActivity.this.viewSource(ManageScriptsActivity.this.selectedPatchItem);
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED) {
                    ManageScriptsActivity.this.togglePatch(ManageScriptsActivity.this.selectedPatchItem);
                    ManageScriptsActivity.this.findScripts();
                }
            }
        }).create();
    }

    private AlertDialog createPatchInfoDialog() {
        return new Builder(this).setTitle("Whoops! info fail").setMessage("Whoops - try again, this is a tiny fail").setPositiveButton(17039370, null).create();
    }

    private AlertDialog createImportSourcesDialog() {
        Resources res = getResources();
        CharSequence[] options = new CharSequence[DIALOG_PATCH_INFO];
        options[0] = res.getString(R.string.script_import_from_local);
        options[DIALOG_MANAGE_PATCH] = res.getString(R.string.script_import_from_cfgy);
        options[DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED] = res.getString(R.string.script_import_from_url);
        options[DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED] = res.getString(R.string.script_import_from_clipboard);
        return new Builder(this).setTitle(R.string.script_import_from).setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                if (button == 0) {
                    ManageScriptsActivity.this.importPatchFromFile();
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH) {
                    ManageScriptsActivity.this.showDialog(ManageScriptsActivity.DIALOG_IMPORT_FROM_CFGY);
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH_CURRENTLY_DISABLED) {
                    ManageScriptsActivity.this.showDialog(ManageScriptsActivity.DIALOG_IMPORT_FROM_URL);
                } else if (button == ManageScriptsActivity.DIALOG_MANAGE_PATCH_CURRENTLY_ENABLED) {
                    ManageScriptsActivity.this.showDialog(ManageScriptsActivity.DIALOG_IMPORT_FROM_CLIPBOARD);
                }
            }
        }).create();
    }

    private AlertDialog createImportFromCfgyDialog() {
        final EditText view = new EditText(this);
        return new Builder(this).setTitle(R.string.script_import_from_cfgy_id).setView(view).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                ManageScriptsActivity.this.importFromCfgy(view.getText().toString());
            }
        }).setNegativeButton(17039360, null).create();
    }

    private AlertDialog createImportFromUrlDialog() {
        final EditText view = new EditText(this);
        return new Builder(this).setTitle(R.string.script_import_from_url_enter).setView(view).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                ManageScriptsActivity.this.importFromUrl(view.getText().toString());
            }
        }).setNegativeButton(17039360, null).create();
    }

    private AlertDialog createVersionIncompatibleDialog() {
        return new Builder(this).setMessage(R.string.script_minecraft_version_incompatible).setPositiveButton(17039370, null).create();
    }

    private AlertDialog createImportFromClipboardDialog() {
        final EditText view = new EditText(this);
        return new Builder(this).setTitle(R.string.script_import_from_clipboard_name).setView(view).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                ManageScriptsActivity.this.importClipboardName = view.getText().toString();
                ManageScriptsActivity.this.showDialog(ManageScriptsActivity.DIALOG_IMPORT_FROM_CLIPBOARD_CODE);
            }
        }).setNegativeButton(17039360, null).create();
    }

    private AlertDialog createImportFromClipboardCodeDialog() {
        final EditText view = new EditText(this);
        view.setId(20130805);
        return new Builder(this).setTitle(R.string.script_import_from_clipboard_code).setView(view).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                ManageScriptsActivity.this.importFromClipboard(view.getText().toString());
            }
        }).setNegativeButton(17039360, null).create();
    }

    private AlertDialog createImportFromIntentDialog() {
        return new Builder(this).setTitle("Unable to get script location").setMessage(R.string.script_import_from_intent_warning).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                ManageScriptsActivity.this.importFromIntent();
            }
        }).setNegativeButton(17039360, null).create();
    }

    private boolean isValidPatch(ContentListItem patch) {
        if (patch.file.length() < 1) {
            return false;
        }
        return true;
    }

    private void importFromCfgy(String id) {
        ImportScriptFromCfgyTask task = new ImportScriptFromCfgyTask();
        String[] strArr = new String[DIALOG_MANAGE_PATCH];
        strArr[0] = id;
        task.execute(strArr);
    }

    private static String cfgyIdToFilename(String str) {
        char[] cfgyFilename = Integer.toString(Integer.parseInt(str, 36)).toCharArray();
        for (int i = 0; i < cfgyFilename.length; i += DIALOG_MANAGE_PATCH) {
            cfgyFilename[i] = cfgyMappings[(char) (cfgyFilename[i] - 48)];
        }
        String retval = new String(cfgyFilename);
        System.out.println(retval);
        return retval;
    }

    private void importFromUrl(String url) {
        ImportScriptFromUrlTask task = new ImportScriptFromUrlTask();
        String[] strArr = new String[DIALOG_MANAGE_PATCH];
        strArr[0] = url;
        task.execute(strArr);
    }

    private void reportError(final Throwable t) {
        runOnUiThread(new Runnable() {
            public void run() {
                final StringWriter strWriter = new StringWriter();
                PrintWriter pWriter = new PrintWriter(strWriter);
                if (t instanceof RhinoException) {
                    String lineSource = ((RhinoException) t).lineSource();
                    if (lineSource != null) {
                        pWriter.println(lineSource);
                    }
                }
                t.printStackTrace(pWriter);
                new Builder(ManageScriptsActivity.this).setTitle(R.string.manage_patches_import_error).setMessage(strWriter.toString()).setPositiveButton(17039370, null).setNeutralButton(17039361, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface aDialog, int button) {
                        ((ClipboardManager) ManageScriptsActivity.this.getSystemService("clipboard")).setText(strWriter.toString());
                    }
                }).show();
            }
        });
    }

    private boolean versionIsSupported() {
        try {
            return getPackageManager().getPackageInfo("com.mojang.minecraftpe", 0).versionCode == MinecraftConstants.MINECRAFT_VERSION_CODE;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private static URL getCfgyUrl(String id) throws MalformedURLException {
        id = id.trim();
        boolean legacyScript = id.length() >= DIALOG_IMPORT_FROM_CFGY || id.matches("[a-zA-Z]");
        if (legacyScript) {
            return new URL("http://modpe.cf.gy/mods/" + cfgyIdToFilename(id) + ".js");
        }
        return new URL("http://betamodpe2.cf.gy/user/getScr.php?scrid=" + id);
    }

    private void importFromClipboard(String code) {
        try {
            File scriptFile = new File(getDir(ScriptManager.SCRIPTS_DIR, 0), this.importClipboardName + ".js");
            PrintWriter printWriter = new PrintWriter(scriptFile);
            printWriter.write(code);
            printWriter.flush();
            printWriter.close();
            ScriptManager.setEnabled(scriptFile, false);
            int maxPatchCount = getMaxPatchCount();
            if (Utils.hasTooManyScripts()) {
                Toast.makeText(this, R.string.script_import_too_many, 0).show();
            } else {
                ScriptManager.setEnabled(scriptFile, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reportError(e);
        }
        findScripts();
    }

    private void importFromIntent() {
        Uri uri = getIntent().getData();
        ImportScriptFromIntentTask task = new ImportScriptFromIntentTask();
        Uri[] uriArr = new Uri[DIALOG_MANAGE_PATCH];
        uriArr[0] = uri;
        task.execute(uriArr);
    }

    private void viewSource(ContentListItem item) {
        try {
            File outDir = new File(getExternalFilesDir(null), "scripts");
            outDir.mkdirs();
            File outFile = new File(outDir, item.file.getName());
            PatchUtils.copy(item.file, outFile);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(outFile), "text/plain");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] bytesFromInputStream(InputStream in, int startingLength) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(startingLength);
        try {
            byte[] buffer = new byte[EnchantType.pickaxe];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                bytes.write(buffer, 0, count);
            }
            byte[] toByteArray = bytes.toByteArray();
            return toByteArray;
        } finally {
            bytes.close();
        }
    }

    public void onRefreshComplete(final List<ContentListItem> items) {
        for (ContentListItem item : items) {
            try {
                if (item.file.getName().toLowerCase().endsWith(".modpkg")) {
                    ZipFile zip = new ZipFile(item.file);
                    MpepInfo info = MpepInfo.fromZip(zip);
                    zip.close();
                    if (info != null) {
                        item.extraData = "by " + info.authorName;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            public void run() {
                ContentListItem.sort(items);
                ManageScriptsActivity.this.patches.clear();
                ManageScriptsActivity.this.patches.addAll(items);
                ManageScriptsActivity.this.adapter.notifyDataSetChanged();
                List<String> allPaths = new ArrayList(ManageScriptsActivity.this.patches.size());
                for (ContentListItem i : ManageScriptsActivity.this.patches) {
                    allPaths.add(i.file.getName());
                }
                ScriptManager.removeDeadEntries(allPaths);
            }
        });
    }

    public List<File> getFolders() {
        List<File> folders = new ArrayList();
        folders.add(getDir(ScriptManager.SCRIPTS_DIR, 0));
        return folders;
    }

    public boolean isEnabled(File f) {
        return ScriptManager.isEnabled(f);
    }
}
