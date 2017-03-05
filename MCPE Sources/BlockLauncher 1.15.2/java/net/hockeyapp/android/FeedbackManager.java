package net.hockeyapp.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import net.hockeyapp.android.objects.FeedbackUserDataElement;
import net.hockeyapp.android.tasks.ParseFeedbackTask;
import net.hockeyapp.android.tasks.SendFeedbackTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.PrefsUtil;
import net.hockeyapp.android.utils.Util;

public class FeedbackManager {
    private static final String BROADCAST_ACTION = "net.hockeyapp.android.SCREENSHOT";
    private static final int BROADCAST_REQUEST_CODE = 1;
    private static final int SCREENSHOT_NOTIFICATION_ID = 1;
    private static Activity currentActivity;
    private static String identifier = null;
    private static FeedbackManagerListener lastListener = null;
    private static boolean notificationActive = false;
    private static BroadcastReceiver receiver = null;
    private static FeedbackUserDataElement requireUserEmail;
    private static FeedbackUserDataElement requireUserName;
    private static String urlString = null;
    private static String userEmail;
    private static String userName;

    private static class MediaScannerClient implements MediaScannerConnectionClient {
        private MediaScannerConnection connection;
        private String path;

        private MediaScannerClient(String str) {
            this.connection = null;
            this.path = str;
        }

        public void onMediaScannerConnected() {
            if (this.connection != null) {
                this.connection.scanFile(this.path, null);
            }
        }

        public void onScanCompleted(String str, Uri uri) {
            HockeyLog.verbose(String.format("Scanned path %s -> URI = %s", new Object[]{str, uri.toString()}));
            this.connection.disconnect();
        }

        public void setConnection(MediaScannerConnection mediaScannerConnection) {
            this.connection = mediaScannerConnection;
        }
    }

    public static void checkForAnswersAndNotify(final Context context) {
        String feedbackTokenFromPrefs = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(context);
        if (feedbackTokenFromPrefs != null) {
            int i = context.getSharedPreferences(ParseFeedbackTask.PREFERENCES_NAME, 0).getInt(ParseFeedbackTask.ID_LAST_MESSAGE_SEND, -1);
            AsyncTask sendFeedbackTask = new SendFeedbackTask(context, getURLString(context), null, null, null, null, null, feedbackTokenFromPrefs, new Handler() {
                public void handleMessage(Message message) {
                    String string = message.getData().getString(SendFeedbackTask.BUNDLE_FEEDBACK_RESPONSE);
                    if (string != null) {
                        AsyncTask parseFeedbackTask = new ParseFeedbackTask(context, string, null, "fetch");
                        parseFeedbackTask.setUrlString(FeedbackManager.getURLString(context));
                        AsyncTaskUtils.execute(parseFeedbackTask);
                    }
                }
            }, true);
            sendFeedbackTask.setShowProgressDialog(false);
            sendFeedbackTask.setLastMessageId(i);
            AsyncTaskUtils.execute(sendFeedbackTask);
        }
    }

    private static void endNotification() {
        notificationActive = false;
        currentActivity.unregisterReceiver(receiver);
        ((NotificationManager) currentActivity.getSystemService("notification")).cancel(SCREENSHOT_NOTIFICATION_ID);
    }

    public static FeedbackManagerListener getLastListener() {
        return lastListener;
    }

    public static FeedbackUserDataElement getRequireUserEmail() {
        return requireUserEmail;
    }

    public static FeedbackUserDataElement getRequireUserName() {
        return requireUserName;
    }

    private static String getURLString(Context context) {
        return urlString + "api/2/apps/" + identifier + "/feedback/";
    }

    public static void register(Context context) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (appIdentifier == null || appIdentifier.length() == 0) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier);
    }

    public static void register(Context context, String str) {
        register(context, str, null);
    }

    public static void register(Context context, String str, String str2, FeedbackManagerListener feedbackManagerListener) {
        if (context != null) {
            identifier = Util.sanitizeAppIdentifier(str2);
            urlString = str;
            lastListener = feedbackManagerListener;
            Constants.loadFromContext(context);
        }
    }

    public static void register(Context context, String str, FeedbackManagerListener feedbackManagerListener) {
        register(context, Constants.BASE_URL, str, feedbackManagerListener);
    }

    public static void setActivityForScreenshot(Activity activity) {
        currentActivity = activity;
        if (!notificationActive) {
            startNotification();
        }
    }

    public static void setRequireUserEmail(FeedbackUserDataElement feedbackUserDataElement) {
        requireUserEmail = feedbackUserDataElement;
    }

    public static void setRequireUserName(FeedbackUserDataElement feedbackUserDataElement) {
        requireUserName = feedbackUserDataElement;
    }

    public static void setUserEmail(String str) {
        userEmail = str;
    }

    public static void setUserName(String str) {
        userName = str;
    }

    public static void showFeedbackActivity(Context context, Bundle bundle, Uri... uriArr) {
        if (context != null) {
            Class cls = null;
            if (lastListener != null) {
                cls = lastListener.getFeedbackActivityClass();
            }
            if (cls == null) {
                cls = FeedbackActivity.class;
            }
            Intent intent = new Intent();
            if (!(bundle == null || bundle.isEmpty())) {
                intent.putExtras(bundle);
            }
            intent.setFlags(268435456);
            intent.setClass(context, cls);
            intent.putExtra(UpdateFragment.FRAGMENT_URL, getURLString(context));
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_USER_NAME, userName);
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_USER_EMAIL, userEmail);
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_ATTACHMENTS, uriArr);
            context.startActivity(intent);
        }
    }

    public static void showFeedbackActivity(Context context, Uri... uriArr) {
        showFeedbackActivity(context, null, uriArr);
    }

    private static void startNotification() {
        notificationActive = true;
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService("notification");
        int identifier = currentActivity.getResources().getIdentifier("ic_menu_camera", "drawable", AddAccountActivity.PlatformName);
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        notificationManager.notify(SCREENSHOT_NOTIFICATION_ID, Util.createNotification(currentActivity, PendingIntent.getBroadcast(currentActivity, SCREENSHOT_NOTIFICATION_ID, intent, 1073741824), "HockeyApp Feedback", "Take a screenshot for your feedback.", identifier));
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    FeedbackManager.takeScreenshot(context);
                }
            };
        }
        currentActivity.registerReceiver(receiver, new IntentFilter(BROADCAST_ACTION));
    }

    public static void takeScreenshot(final Context context) {
        View decorView = currentActivity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        final Bitmap drawingCache = decorView.getDrawingCache();
        String localClassName = currentActivity.getLocalClassName();
        File hockeyAppStorageDir = Constants.getHockeyAppStorageDir();
        File file = new File(hockeyAppStorageDir, localClassName + ".jpg");
        int i = SCREENSHOT_NOTIFICATION_ID;
        while (file.exists()) {
            file = new File(hockeyAppStorageDir, localClassName + "_" + i + ".jpg");
            i += SCREENSHOT_NOTIFICATION_ID;
        }
        AnonymousClass2 anonymousClass2 = new AsyncTask<File, Void, Boolean>() {
            protected Boolean doInBackground(File... fileArr) {
                try {
                    OutputStream fileOutputStream = new FileOutputStream(fileArr[0]);
                    drawingCache.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    return Boolean.valueOf(true);
                } catch (Throwable e) {
                    HockeyLog.error("Could not save screenshot.", e);
                    return Boolean.valueOf(false);
                }
            }

            protected void onPostExecute(Boolean bool) {
                if (!bool.booleanValue()) {
                    Toast.makeText(context, "Screenshot could not be created. Sorry.", FeedbackManager.SCREENSHOT_NOTIFICATION_ID).show();
                }
            }
        };
        File[] fileArr = new File[SCREENSHOT_NOTIFICATION_ID];
        fileArr[0] = file;
        anonymousClass2.execute(fileArr);
        Object mediaScannerClient = new MediaScannerClient(file.getAbsolutePath());
        MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(currentActivity, mediaScannerClient);
        mediaScannerClient.setConnection(mediaScannerConnection);
        mediaScannerConnection.connect();
        Toast.makeText(context, "Screenshot '" + file.getName() + "' is available in gallery.", SCREENSHOT_NOTIFICATION_ID).show();
    }

    public static void unregister() {
        lastListener = null;
    }

    public static void unsetCurrentActivityForScreenshot(Activity activity) {
        if (currentActivity != null && currentActivity == activity) {
            endNotification();
            currentActivity = null;
        }
    }
}
