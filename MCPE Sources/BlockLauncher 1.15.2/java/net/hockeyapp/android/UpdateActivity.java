package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;
import org.mozilla.javascript.Token;

public class UpdateActivity extends Activity implements OnClickListener, UpdateActivityInterface, UpdateInfoListener {
    private static final int DIALOG_ERROR_ID = 0;
    public static final String EXTRA_JSON = "json";
    public static final String EXTRA_URL = "url";
    private Context mContext;
    protected DownloadFileTask mDownloadTask;
    private ErrorObject mError;
    protected VersionHelper mVersionHelper;

    @SuppressLint({"InlinedApi"})
    private boolean isUnknownSourcesChecked() {
        try {
            return (VERSION.SDK_INT < 17 || VERSION.SDK_INT >= 21) ? Secure.getInt(getContentResolver(), "install_non_market_apps") == 1 : Global.getInt(getContentResolver(), "install_non_market_apps") == 1;
        } catch (SettingNotFoundException e) {
            return true;
        }
    }

    private boolean isWriteExternalStorageSet(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    protected void configureView() {
        ((TextView) findViewById(R.id.label_title)).setText(getAppName());
        final TextView textView = (TextView) findViewById(R.id.label_version);
        final String str = "Version " + this.mVersionHelper.getVersionString();
        final String fileDateString = this.mVersionHelper.getFileDateString();
        String str2 = "Unknown size";
        if (this.mVersionHelper.getFileSizeBytes() >= 0) {
            str2 = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) r4) / 1048576.0f)}) + " MB";
        } else {
            AsyncTaskUtils.execute(new GetFileSizeTask(this, getIntent().getStringExtra(EXTRA_URL), new DownloadFileListener() {
                public void downloadSuccessful(DownloadFileTask downloadFileTask) {
                    if (downloadFileTask instanceof GetFileSizeTask) {
                        long size = ((GetFileSizeTask) downloadFileTask).getSize();
                        String str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) size) / 1048576.0f)}) + " MB";
                        textView.setText(UpdateActivity.this.getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDateString, str}));
                    }
                }
            }));
        }
        textView.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDateString, str2}));
        ((Button) findViewById(R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) findViewById(R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL(Constants.BASE_URL, getReleaseNotes(), "text/html", "utf-8", null);
    }

    protected void createDownloadTask(String str, DownloadFileListener downloadFileListener) {
        this.mDownloadTask = new DownloadFileTask(this, str, downloadFileListener);
    }

    public void enableUpdateButton() {
        findViewById(R.id.button_update).setEnabled(true);
    }

    public String getAppName() {
        try {
            PackageManager packageManager = getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(getPackageName(), DIALOG_ERROR_ID)).toString();
        } catch (NameNotFoundException e) {
            return BuildConfig.FLAVOR;
        }
    }

    public int getCurrentVersionCode() {
        int i = -1;
        try {
            return getPackageManager().getPackageInfo(getPackageName(), Token.RESERVED).versionCode;
        } catch (NameNotFoundException e) {
            return i;
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(R.layout.hockeyapp_activity_update, null);
    }

    protected String getReleaseNotes() {
        return this.mVersionHelper.getReleaseNotes(false);
    }

    public void onClick(View view) {
        prepareDownload();
        view.setEnabled(false);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle("App Update");
        setContentView(getLayoutView());
        this.mContext = this;
        this.mVersionHelper = new VersionHelper(this, getIntent().getStringExtra(EXTRA_JSON), this);
        configureView();
        this.mDownloadTask = (DownloadFileTask) getLastNonConfigurationInstance();
        if (this.mDownloadTask != null) {
            this.mDownloadTask.attach(this);
        }
    }

    protected Dialog onCreateDialog(int i) {
        return onCreateDialog(i, null);
    }

    protected Dialog onCreateDialog(int i, Bundle bundle) {
        switch (i) {
            case DIALOG_ERROR_ID /*0*/:
                return new Builder(this).setMessage("An error has occured").setCancelable(false).setTitle("Error").setIcon(17301543).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpdateActivity.this.mError = null;
                        dialogInterface.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    protected void onPrepareDialog(int i, Dialog dialog) {
        switch (i) {
            case DIALOG_ERROR_ID /*0*/:
                AlertDialog alertDialog = (AlertDialog) dialog;
                if (this.mError != null) {
                    alertDialog.setMessage(this.mError.getMessage());
                    return;
                } else {
                    alertDialog.setMessage("An unknown error has occured.");
                    return;
                }
            default:
                return;
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        enableUpdateButton();
        if (strArr.length != 0 && iArr.length != 0 && i == 1) {
            if (iArr[DIALOG_ERROR_ID] == 0) {
                prepareDownload();
                return;
            }
            HockeyLog.warn("User denied write permission, can't continue with updater task.");
            UpdateManagerListener lastListener = UpdateManager.getLastListener();
            if (lastListener != null) {
                lastListener.onUpdatePermissionsNotGranted();
            } else {
                new Builder(this.mContext).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        this.prepareDownload();
                    }
                }).create().show();
            }
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mDownloadTask != null) {
            this.mDownloadTask.detach();
        }
        return this.mDownloadTask;
    }

    protected void prepareDownload() {
        if (!Util.isConnectedToNetwork(this.mContext)) {
            this.mError = new ErrorObject();
            this.mError.setMessage(getString(R.string.hockeyapp_error_no_network_message));
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
                }
            });
        } else if (isWriteExternalStorageSet(this.mContext)) {
            if (isUnknownSourcesChecked()) {
                startDownloadTask();
                return;
            }
            this.mError = new ErrorObject();
            this.mError.setMessage("The installation from unknown sources is not enabled. Please check the device settings.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
                }
            });
        } else if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        } else {
            this.mError = new ErrorObject();
            this.mError.setMessage("The permission to access the external storage permission is not set. Please contact the developer.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
                }
            });
        }
    }

    protected void startDownloadTask() {
        startDownloadTask(getIntent().getStringExtra(EXTRA_URL));
    }

    protected void startDownloadTask(String str) {
        createDownloadTask(str, new DownloadFileListener() {
            public void downloadFailed(DownloadFileTask downloadFileTask, Boolean bool) {
                if (bool.booleanValue()) {
                    UpdateActivity.this.startDownloadTask();
                } else {
                    UpdateActivity.this.enableUpdateButton();
                }
            }

            public void downloadSuccessful(DownloadFileTask downloadFileTask) {
                UpdateActivity.this.enableUpdateButton();
            }
        });
        AsyncTaskUtils.execute(this.mDownloadTask);
    }
}
