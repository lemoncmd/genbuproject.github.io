package net.hockeyapp.android.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class UiThreadUtil {

    private static class WbUtilHolder {
        public static final UiThreadUtil INSTANCE = new UiThreadUtil();

        private WbUtilHolder() {
        }
    }

    private UiThreadUtil() {
    }

    public static UiThreadUtil getInstance() {
        return WbUtilHolder.INSTANCE;
    }

    public void dismissLoading(WeakReference<Activity> weakReference, final ProgressDialog progressDialog) {
        if (weakReference != null) {
            Activity activity = (Activity) weakReference.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    public void dismissLoadingDialogAndDisplayError(WeakReference<Activity> weakReference, final ProgressDialog progressDialog, final int i) {
        if (weakReference != null) {
            final Activity activity = (Activity) weakReference.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        activity.showDialog(i);
                    }
                });
            }
        }
    }

    public void displayToastMessage(WeakReference<Activity> weakReference, final String str, final int i) {
        if (weakReference != null) {
            final Activity activity = (Activity) weakReference.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, str, i).show();
                    }
                });
            }
        }
    }
}
