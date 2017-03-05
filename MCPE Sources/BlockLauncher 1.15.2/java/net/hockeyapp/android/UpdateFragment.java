package net.hockeyapp.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Token;

@TargetApi(11)
public class UpdateFragment extends DialogFragment implements OnClickListener, UpdateInfoListener {
    public static final String FRAGMENT_URL = "url";
    public static final String FRAGMENT_VERSION_INFO = "versionInfo";
    private DownloadFileTask mDownloadTask;
    private String mUrlString;
    private VersionHelper mVersionHelper;
    private JSONArray mVersionInfo;

    public static UpdateFragment newInstance(JSONArray jSONArray, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_URL, str);
        bundle.putString(FRAGMENT_VERSION_INFO, jSONArray.toString());
        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setArguments(bundle);
        return updateFragment;
    }

    private void startDownloadTask(final Activity activity) {
        this.mDownloadTask = new DownloadFileTask(activity, this.mUrlString, new DownloadFileListener() {
            public void downloadFailed(DownloadFileTask downloadFileTask, Boolean bool) {
                if (bool.booleanValue()) {
                    UpdateFragment.this.startDownloadTask(activity);
                }
            }

            public void downloadSuccessful(DownloadFileTask downloadFileTask) {
            }
        });
        AsyncTaskUtils.execute(this.mDownloadTask);
    }

    public String getAppName() {
        Activity activity = getActivity();
        try {
            PackageManager packageManager = activity.getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(activity.getPackageName(), 0)).toString();
        } catch (NameNotFoundException e) {
            return BuildConfig.FLAVOR;
        }
    }

    public int getCurrentVersionCode() {
        int i = -1;
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), Token.RESERVED).versionCode;
        } catch (NameNotFoundException e) {
            return i;
        } catch (NullPointerException e2) {
            return i;
        }
    }

    public View getLayoutView() {
        View linearLayout = new LinearLayout(getActivity());
        LayoutInflater.from(getActivity()).inflate(R.layout.hockeyapp_fragment_update, linearLayout);
        return linearLayout;
    }

    public void onClick(View view) {
        prepareDownload();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.mUrlString = getArguments().getString(FRAGMENT_URL);
            this.mVersionInfo = new JSONArray(getArguments().getString(FRAGMENT_VERSION_INFO));
            setStyle(1, 16973939);
        } catch (JSONException e) {
            dismiss();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View layoutView = getLayoutView();
        this.mVersionHelper = new VersionHelper(getActivity(), this.mVersionInfo.toString(), this);
        ((TextView) layoutView.findViewById(R.id.label_title)).setText(getAppName());
        final TextView textView = (TextView) layoutView.findViewById(R.id.label_version);
        final String str = "Version " + this.mVersionHelper.getVersionString();
        final String fileDateString = this.mVersionHelper.getFileDateString();
        String str2 = "Unknown size";
        if (this.mVersionHelper.getFileSizeBytes() >= 0) {
            str2 = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) r4) / 1048576.0f)}) + " MB";
        } else {
            AsyncTaskUtils.execute(new GetFileSizeTask(getActivity(), this.mUrlString, new DownloadFileListener() {
                public void downloadSuccessful(DownloadFileTask downloadFileTask) {
                    if (downloadFileTask instanceof GetFileSizeTask) {
                        long size = ((GetFileSizeTask) downloadFileTask).getSize();
                        String str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) size) / 1048576.0f)}) + " MB";
                        textView.setText(UpdateFragment.this.getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDateString, str}));
                    }
                }
            }));
        }
        textView.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDateString, str2}));
        ((Button) layoutView.findViewById(R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) layoutView.findViewById(R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL(Constants.BASE_URL, this.mVersionHelper.getReleaseNotes(false), "text/html", "utf-8", null);
        return layoutView;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (strArr.length != 0 && iArr.length != 0 && i == 1) {
            if (iArr[0] == 0) {
                startDownloadTask(getActivity());
                return;
            }
            HockeyLog.warn("User denied write permission, can't continue with updater task.");
            UpdateManagerListener lastListener = UpdateManager.getLastListener();
            if (lastListener != null) {
                lastListener.onUpdatePermissionsNotGranted();
            } else {
                new Builder(getActivity()).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        this.prepareDownload();
                    }
                }).create().show();
            }
        }
    }

    public void prepareDownload() {
        if (VERSION.SDK_INT < 23 || getActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            startDownloadTask(getActivity());
            dismiss();
            return;
        }
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    }
}
