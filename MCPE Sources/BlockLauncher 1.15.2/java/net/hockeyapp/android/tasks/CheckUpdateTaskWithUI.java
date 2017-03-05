package net.hockeyapp.android.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import com.mojang.minecraftpe.MainActivity;
import java.lang.ref.WeakReference;
import net.hockeyapp.android.R;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionCache;
import org.json.JSONArray;

public class CheckUpdateTaskWithUI extends CheckUpdateTask {
    private Activity mActivity = null;
    private AlertDialog mDialog = null;
    protected boolean mIsDialogRequired = false;

    public CheckUpdateTaskWithUI(WeakReference<Activity> weakReference, String str, String str2, UpdateManagerListener updateManagerListener, boolean z) {
        super(weakReference, str, str2, updateManagerListener);
        if (weakReference != null) {
            this.mActivity = (Activity) weakReference.get();
        }
        this.mIsDialogRequired = z;
    }

    @TargetApi(11)
    private void showDialog(final JSONArray jSONArray) {
        if (getCachingEnabled()) {
            HockeyLog.verbose("HockeyUpdate", "Caching is enabled. Setting version to cached one.");
            VersionCache.setVersionInfo(this.mActivity, jSONArray.toString());
        }
        if (this.mActivity != null && !this.mActivity.isFinishing()) {
            Builder builder = new Builder(this.mActivity);
            builder.setTitle(R.string.hockeyapp_update_dialog_title);
            if (this.mandatory.booleanValue()) {
                String appName = Util.getAppName(this.mActivity);
                Toast.makeText(this.mActivity, String.format(this.mActivity.getString(R.string.hockeyapp_update_mandatory_toast), new Object[]{appName}), 1).show();
                startUpdateIntent(jSONArray, Boolean.valueOf(true));
                return;
            }
            builder.setMessage(R.string.hockeyapp_update_dialog_message);
            builder.setNegativeButton(R.string.hockeyapp_update_dialog_negative_button, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    CheckUpdateTaskWithUI.this.cleanUp();
                    if (CheckUpdateTaskWithUI.this.listener != null) {
                        CheckUpdateTaskWithUI.this.listener.onCancel();
                    }
                }
            });
            builder.setPositiveButton(R.string.hockeyapp_update_dialog_positive_button, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (CheckUpdateTaskWithUI.this.getCachingEnabled()) {
                        VersionCache.setVersionInfo(CheckUpdateTaskWithUI.this.mActivity, "[]");
                    }
                    WeakReference weakReference = new WeakReference(CheckUpdateTaskWithUI.this.mActivity);
                    if (Util.fragmentsSupported().booleanValue() && Util.runsOnTablet(weakReference).booleanValue()) {
                        CheckUpdateTaskWithUI.this.showUpdateFragment(jSONArray);
                    } else {
                        CheckUpdateTaskWithUI.this.startUpdateIntent(jSONArray, Boolean.valueOf(false));
                    }
                }
            });
            this.mDialog = builder.create();
            this.mDialog.show();
        }
    }

    @TargetApi(11)
    private void showUpdateFragment(JSONArray jSONArray) {
        if (this.mActivity != null) {
            FragmentTransaction beginTransaction = this.mActivity.getFragmentManager().beginTransaction();
            beginTransaction.setTransition(MainActivity.DIALOG_RUNTIME_OPTIONS);
            Fragment findFragmentByTag = this.mActivity.getFragmentManager().findFragmentByTag("hockey_update_dialog");
            if (findFragmentByTag != null) {
                beginTransaction.remove(findFragmentByTag);
            }
            beginTransaction.addToBackStack(null);
            Class cls = UpdateFragment.class;
            if (this.listener != null) {
                cls = this.listener.getUpdateFragmentClass();
            }
            try {
                ((DialogFragment) cls.getMethod("newInstance", new Class[]{JSONArray.class, String.class}).invoke(null, new Object[]{jSONArray, getURLString("apk")})).show(beginTransaction, "hockey_update_dialog");
            } catch (Exception e) {
                HockeyLog.error("An exception happened while showing the update fragment:");
                e.printStackTrace();
                HockeyLog.error("Showing update activity instead.");
                startUpdateIntent(jSONArray, Boolean.valueOf(false));
            }
        }
    }

    private void startUpdateIntent(JSONArray jSONArray, Boolean bool) {
        Class cls = null;
        if (this.listener != null) {
            cls = this.listener.getUpdateActivityClass();
        }
        if (cls == null) {
            cls = UpdateActivity.class;
        }
        if (this.mActivity != null) {
            Intent intent = new Intent();
            intent.setClass(this.mActivity, cls);
            intent.putExtra(UpdateActivity.EXTRA_JSON, jSONArray.toString());
            intent.putExtra(UpdateFragment.FRAGMENT_URL, getURLString("apk"));
            this.mActivity.startActivity(intent);
            if (bool.booleanValue()) {
                this.mActivity.finish();
            }
        }
        cleanUp();
    }

    protected void cleanUp() {
        super.cleanUp();
        this.mActivity = null;
        this.mDialog = null;
    }

    public void detach() {
        super.detach();
        this.mActivity = null;
        if (this.mDialog != null) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    protected void onPostExecute(JSONArray jSONArray) {
        super.onPostExecute(jSONArray);
        if (jSONArray != null && this.mIsDialogRequired) {
            showDialog(jSONArray);
        }
    }
}
