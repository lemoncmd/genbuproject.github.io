package com.ipaulpro.afilechooser;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class FileChooserActivity extends ListActivity {
    private static final String BREADCRUMB = "breadcrumb";
    private static final boolean DEBUG = true;
    private static final String HIDDEN_PREFIX = ".";
    public static final String MIME_TYPE_ALL = "*/*";
    private static final String PATH = "path";
    private static final String POSTIION = "position";
    public static final int REQUEST_CODE = 6384;
    private static final String TAG = "ChooserActivity";
    private Set<String> extendedMimeTypes = new HashSet();
    private ArrayList<String> mBreadcrumb = new ArrayList();
    private Comparator<File> mComparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
        }
    };
    private FileFilter mDirFilter = new FileFilter() {
        public boolean accept(File file) {
            return (!file.isDirectory() || file.getName().startsWith(FileChooserActivity.HIDDEN_PREFIX)) ? false : FileChooserActivity.DEBUG;
        }
    };
    private File mExternalDir;
    private boolean mExternalStorageAvailable = false;
    private BroadcastReceiver mExternalStorageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(FileChooserActivity.TAG, "External storage broadcast recieved: " + intent.getData());
            FileChooserActivity.this.updateExternalStorageState();
        }
    };
    private boolean mExternalStorageWriteable = false;
    private FileFilter mFileFilter = new FileFilter() {
        public boolean accept(File file) {
            String fileName = file.getName();
            String mimeType = FileUtils.getMimeType(FileChooserActivity.this, file);
            return (file.isFile() && !fileName.startsWith(FileChooserActivity.HIDDEN_PREFIX) && (mimeType.equals(FileChooserActivity.this.getIntent().getType()) || FileChooserActivity.this.extendedMimeTypes.contains(mimeType))) ? FileChooserActivity.DEBUG : false;
        }
    };
    private Comparator<File> mLastModifiedComparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            long a = f1.lastModified();
            long b = f2.lastModified();
            if (a == b) {
                return 0;
            }
            return a < b ? 1 : -1;
        }
    };
    private ArrayList<File> mList = new ArrayList();
    private String mPath;

    protected boolean isIntentGetContent() {
        String action = getIntent().getAction();
        Log.d(TAG, "Intent Action: " + action);
        return "android.intent.action.GET_CONTENT".equals(action);
    }

    protected void showFileChooser(String title, String type) {
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.select_file);
        }
        if (TextUtils.isEmpty(type)) {
            type = MIME_TYPE_ALL;
        }
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType(type.toLowerCase());
        intent.addCategory("android.intent.category.OPENABLE");
        try {
            startActivityForResult(Intent.createChooser(intent, title), REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            onFileError(e);
        }
    }

    protected void showFileChooser() {
        showFileChooser(null, null);
    }

    private void fillList(int position) {
        Log.d(TAG, "Current path: " + this.mPath);
        setTitle(this.mPath);
        ((FileListAdapter) getListAdapter()).clear();
        File pathDir = new File(this.mPath);
        File[] dirs = pathDir.listFiles(this.mDirFilter);
        if (dirs != null) {
            Arrays.sort(dirs, this.mComparator);
            for (File dir : dirs) {
                this.mList.add(dir);
            }
        }
        File[] files = pathDir.listFiles(this.mFileFilter);
        if (files != null) {
            Arrays.sort(files, this.mComparator);
            for (File file : files) {
                this.mList.add(file);
            }
        }
        if (dirs == null && files == null) {
            Log.d(TAG, "Directory is empty");
        }
        ((FileListAdapter) getListAdapter()).setListItems(this.mList);
        ((FileListAdapter) getListAdapter()).notifyDataSetChanged();
        getListView().setSelection(position);
    }

    private void updateBreadcrumb(boolean add) {
        if (add) {
            this.mBreadcrumb.add(this.mPath);
        } else if (this.mExternalDir.getAbsolutePath().equals(this.mPath)) {
            onFileSelectCancel();
            finish();
        } else {
            int size = this.mBreadcrumb.size();
            if (size > 1) {
                this.mBreadcrumb.remove(size - 1);
                this.mPath = (String) this.mBreadcrumb.get(size - 2);
                fillList(0);
            }
        }
    }

    private void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            this.mExternalStorageWriteable = DEBUG;
            this.mExternalStorageAvailable = DEBUG;
        } else if ("mounted_ro".equals(state)) {
            this.mExternalStorageAvailable = DEBUG;
            this.mExternalStorageWriteable = false;
        } else {
            this.mExternalStorageWriteable = false;
            this.mExternalStorageAvailable = false;
        }
        handleExternalStorageState(this.mExternalStorageAvailable, this.mExternalStorageWriteable);
    }

    private void startWatchingExternalStorage() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        registerReceiver(this.mExternalStorageReceiver, filter);
        if (isIntentGetContent()) {
            updateExternalStorageState();
        }
    }

    private void stopWatchingExternalStorage() {
        unregisterReceiver(this.mExternalStorageReceiver);
    }

    private void handleExternalStorageState(boolean available, boolean writeable) {
        if (!available && isIntentGetContent()) {
            Log.d(TAG, "External Storage was disconnected");
            onFileDisconnect();
            finish();
        }
    }

    protected void onFileSelect(File file) {
        Log.d(TAG, "File selected: " + file.getAbsolutePath());
    }

    protected void onFileError(Exception e) {
        Log.e(TAG, "Error selecting file", e);
    }

    protected void onFileSelectCancel() {
        Log.d(TAG, "File selection canceled");
    }

    protected void onFileDisconnect() {
        Log.d(TAG, "External storage disconnected");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] extraMimeTypes = getIntent().getStringArrayExtra(FileUtils.EXTRA_MIME_TYPES);
        this.extendedMimeTypes.clear();
        if (extraMimeTypes != null) {
            for (String s : extraMimeTypes) {
                this.extendedMimeTypes.add(s);
            }
        }
        this.mExternalDir = Environment.getExternalStorageDirectory();
        String sortMethod = getIntent().getStringExtra(FileUtils.EXTRA_SORT_METHOD);
        if (sortMethod != null && sortMethod.equals(FileUtils.SORT_LAST_MODIFIED)) {
            this.mComparator = this.mLastModifiedComparator;
        }
        if (getListAdapter() == null) {
            setListAdapter(new FileListAdapter(this));
        }
        if (savedInstanceState != null) {
            restoreMe(savedInstanceState);
            return;
        }
        this.mPath = this.mExternalDir.getAbsolutePath();
        String startPath = getIntent().getStringExtra("startPath");
        if (startPath != null) {
            this.mPath = startPath;
        }
        updateBreadcrumb(DEBUG);
        if (isIntentGetContent()) {
            setContentView(R.layout.explorer);
            fillList(0);
        }
    }

    protected void onResume() {
        super.onResume();
        startWatchingExternalStorage();
    }

    protected void onPause() {
        super.onPause();
        stopWatchingExternalStorage();
    }

    public void onBackPressed() {
        updateBreadcrumb(false);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        File file = (File) this.mList.get(position);
        this.mPath = file.getAbsolutePath();
        Log.d(TAG, "Selected file: " + this.mPath);
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            updateBreadcrumb(DEBUG);
            fillList(0);
            return;
        }
        Intent data = new Intent();
        data.setData(Uri.fromFile(file));
        setResult(-1, data);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE /*6384*/:
                if (resultCode != -1) {
                    if (resultCode == 0) {
                        onFileSelectCancel();
                        break;
                    }
                }
                try {
                    onFileSelect(new File(FileUtils.getPath(this, data.getData())));
                    break;
                } catch (Exception e) {
                    onFileError(e);
                    break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PATH, this.mPath);
        outState.putStringArrayList(BREADCRUMB, this.mBreadcrumb);
        outState.putInt(POSTIION, getListView().getFirstVisiblePosition());
    }

    private void restoreMe(Bundle state) {
        this.mPath = state.containsKey(PATH) ? state.getString(PATH) : this.mExternalDir.getAbsolutePath();
        this.mBreadcrumb = state.getStringArrayList(BREADCRUMB);
        fillList(state.getInt(POSTIION));
    }
}
