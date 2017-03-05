package net.hockeyapp.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.ipaulpro.afilechooser.utils.MimeTypeParser;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.R;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class SendFeedbackTask extends ConnectionTask<Void, Void, HashMap<String, String>> {
    public static final String BUNDLE_FEEDBACK_RESPONSE = "feedback_response";
    public static final String BUNDLE_FEEDBACK_STATUS = "feedback_status";
    public static final String BUNDLE_REQUEST_TYPE = "request_type";
    private static final String FILE_TAG = "HockeyApp";
    private static final String TAG = "SendFeedbackTask";
    private List<Uri> mAttachmentUris;
    private Context mContext;
    private String mEmail;
    private Handler mHandler;
    private boolean mIsFetchMessages;
    private int mLastMessageId = -1;
    private String mName;
    private ProgressDialog mProgressDialog;
    private boolean mShowProgressDialog = true;
    private String mSubject;
    private String mText;
    private String mToken;
    private String mUrlString;

    public SendFeedbackTask(Context context, String str, String str2, String str3, String str4, String str5, List<Uri> list, String str6, Handler handler, boolean z) {
        this.mContext = context;
        this.mUrlString = str;
        this.mName = str2;
        this.mEmail = str3;
        this.mSubject = str4;
        this.mText = str5;
        this.mAttachmentUris = list;
        this.mToken = str6;
        this.mHandler = handler;
        this.mIsFetchMessages = z;
        if (context != null) {
            Constants.loadFromContext(context);
        }
    }

    private void clearTemporaryFolder(HashMap<String, String> hashMap) {
        String str = (String) hashMap.get("status");
        if (str != null && str.startsWith(XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION) && this.mContext != null) {
            File file = new File(this.mContext.getCacheDir(), FILE_TAG);
            if (file != null && file.exists()) {
                for (File file2 : file.listFiles()) {
                    if (!(file2 == null || Boolean.valueOf(file2.delete()).booleanValue())) {
                        HockeyLog.debug(TAG, "Error deleting file from temporary folder");
                    }
                }
            }
        }
    }

    private HashMap<String, String> doGet() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mUrlString + Util.encodeParam(this.mToken));
        if (this.mLastMessageId != -1) {
            stringBuilder.append("?last_message_id=" + this.mLastMessageId);
        }
        HashMap<String, String> hashMap = new HashMap();
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = new HttpURLConnectionBuilder(stringBuilder.toString()).build();
            hashMap.put(MimeTypeParser.TAG_TYPE, "fetch");
            httpURLConnection.connect();
            hashMap.put("status", String.valueOf(httpURLConnection.getResponseCode()));
            hashMap.put("response", ConnectionTask.getStringFromConnection(httpURLConnection));
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (Throwable th) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return hashMap;
    }

    private HashMap<String, String> doPostPut() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(MimeTypeParser.TAG_TYPE, "send");
        HttpURLConnection httpURLConnection = null;
        try {
            Map hashMap2 = new HashMap();
            hashMap2.put("name", this.mName);
            hashMap2.put("email", this.mEmail);
            hashMap2.put("subject", this.mSubject);
            hashMap2.put("text", this.mText);
            hashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
            hashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
            hashMap2.put("bundle_version", Constants.APP_VERSION);
            hashMap2.put("os_version", Constants.ANDROID_VERSION);
            hashMap2.put("oem", Constants.PHONE_MANUFACTURER);
            hashMap2.put("model", Constants.PHONE_MODEL);
            if (this.mToken != null) {
                this.mUrlString += this.mToken + "/";
            }
            httpURLConnection = new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod(this.mToken != null ? HttpEngine.PUT : HttpEngine.POST).writeFormFields(hashMap2).build();
            httpURLConnection.connect();
            hashMap.put("status", String.valueOf(httpURLConnection.getResponseCode()));
            hashMap.put("response", ConnectionTask.getStringFromConnection(httpURLConnection));
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (Throwable th) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return hashMap;
    }

    private HashMap<String, String> doPostPutWithAttachments() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(MimeTypeParser.TAG_TYPE, "send");
        HttpURLConnection httpURLConnection = null;
        try {
            Map hashMap2 = new HashMap();
            hashMap2.put("name", this.mName);
            hashMap2.put("email", this.mEmail);
            hashMap2.put("subject", this.mSubject);
            hashMap2.put("text", this.mText);
            hashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
            hashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
            hashMap2.put("bundle_version", Constants.APP_VERSION);
            hashMap2.put("os_version", Constants.ANDROID_VERSION);
            hashMap2.put("oem", Constants.PHONE_MANUFACTURER);
            hashMap2.put("model", Constants.PHONE_MODEL);
            if (this.mToken != null) {
                this.mUrlString += this.mToken + "/";
            }
            httpURLConnection = new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod(this.mToken != null ? HttpEngine.PUT : HttpEngine.POST).writeMultipartData(hashMap2, this.mContext, this.mAttachmentUris).build();
            httpURLConnection.connect();
            hashMap.put("status", String.valueOf(httpURLConnection.getResponseCode()));
            hashMap.put("response", ConnectionTask.getStringFromConnection(httpURLConnection));
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (Throwable th) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return hashMap;
    }

    public void attach(Context context) {
        this.mContext = context;
    }

    public void detach() {
        this.mContext = null;
        this.mProgressDialog = null;
    }

    protected HashMap<String, String> doInBackground(Void... voidArr) {
        if (this.mIsFetchMessages && this.mToken != null) {
            return doGet();
        }
        if (this.mIsFetchMessages) {
            return null;
        }
        if (this.mAttachmentUris.isEmpty()) {
            return doPostPut();
        }
        HashMap<String, String> doPostPutWithAttachments = doPostPutWithAttachments();
        if (doPostPutWithAttachments == null) {
            return doPostPutWithAttachments;
        }
        clearTemporaryFolder(doPostPutWithAttachments);
        return doPostPutWithAttachments;
    }

    protected void onPostExecute(HashMap<String, String> hashMap) {
        if (this.mProgressDialog != null) {
            try {
                this.mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.mHandler != null) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            if (hashMap != null) {
                bundle.putString(BUNDLE_REQUEST_TYPE, (String) hashMap.get(MimeTypeParser.TAG_TYPE));
                bundle.putString(BUNDLE_FEEDBACK_RESPONSE, (String) hashMap.get("response"));
                bundle.putString(BUNDLE_FEEDBACK_STATUS, (String) hashMap.get("status"));
            } else {
                bundle.putString(BUNDLE_REQUEST_TYPE, "unknown");
            }
            message.setData(bundle);
            this.mHandler.sendMessage(message);
        }
    }

    protected void onPreExecute() {
        CharSequence string = this.mContext.getString(R.string.hockeyapp_feedback_sending_feedback_text);
        if (this.mIsFetchMessages) {
            string = this.mContext.getString(R.string.hockeyapp_feedback_fetching_feedback_text);
        }
        if ((this.mProgressDialog == null || !this.mProgressDialog.isShowing()) && this.mShowProgressDialog) {
            this.mProgressDialog = ProgressDialog.show(this.mContext, BuildConfig.FLAVOR, string, true, false);
        }
    }

    public void setLastMessageId(int i) {
        this.mLastMessageId = i;
    }

    public void setShowProgressDialog(boolean z) {
        this.mShowProgressDialog = z;
    }
}
