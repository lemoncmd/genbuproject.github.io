package net.hockeyapp.android.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.util.ArrayList;
import net.hockeyapp.android.FeedbackActivity;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.FeedbackManagerListener;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.utils.FeedbackParser;
import net.hockeyapp.android.utils.Util;

public class ParseFeedbackTask extends AsyncTask<Void, Void, FeedbackResponse> {
    public static final String BUNDLE_PARSE_FEEDBACK_RESPONSE = "parse_feedback_response";
    public static final String ID_LAST_MESSAGE_PROCESSED = "idLastMessageProcessed";
    public static final String ID_LAST_MESSAGE_SEND = "idLastMessageSend";
    public static final int NEW_ANSWER_NOTIFICATION_ID = 2;
    public static final String PREFERENCES_NAME = "net.hockeyapp.android.feedback";
    private Context mContext;
    private String mFeedbackResponse;
    private Handler mHandler;
    private String mRequestType;
    private String mUrlString = null;

    public ParseFeedbackTask(Context context, String str, Handler handler, String str2) {
        this.mContext = context;
        this.mFeedbackResponse = str;
        this.mHandler = handler;
        this.mRequestType = str2;
    }

    private void checkForNewAnswers(ArrayList<FeedbackMessage> arrayList) {
        FeedbackMessage feedbackMessage = (FeedbackMessage) arrayList.get(arrayList.size() - 1);
        int id = feedbackMessage.getId();
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(PREFERENCES_NAME, 0);
        if (this.mRequestType.equals("send")) {
            sharedPreferences.edit().putInt(ID_LAST_MESSAGE_SEND, id).putInt(ID_LAST_MESSAGE_PROCESSED, id).apply();
        } else if (this.mRequestType.equals("fetch")) {
            int i = sharedPreferences.getInt(ID_LAST_MESSAGE_SEND, -1);
            int i2 = sharedPreferences.getInt(ID_LAST_MESSAGE_PROCESSED, -1);
            if (id != i && id != i2) {
                sharedPreferences.edit().putInt(ID_LAST_MESSAGE_PROCESSED, id).apply();
                FeedbackManagerListener lastListener = FeedbackManager.getLastListener();
                if (!(lastListener != null ? lastListener.feedbackAnswered(feedbackMessage) : false)) {
                    startNotification(this.mContext);
                }
            }
        }
    }

    private void startNotification(Context context) {
        if (this.mUrlString != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            int identifier = context.getResources().getIdentifier("ic_menu_refresh", "drawable", AddAccountActivity.PlatformName);
            Class cls = null;
            if (FeedbackManager.getLastListener() != null) {
                cls = FeedbackManager.getLastListener().getFeedbackActivityClass();
            }
            if (cls == null) {
                cls = FeedbackActivity.class;
            }
            Intent intent = new Intent();
            intent.setFlags(805306368);
            intent.setClass(context, cls);
            intent.putExtra(UpdateFragment.FRAGMENT_URL, this.mUrlString);
            Notification createNotification = Util.createNotification(context, PendingIntent.getActivity(context, 0, intent, 1073741824), "HockeyApp Feedback", "A new answer to your feedback is available.", identifier);
            if (createNotification != null) {
                notificationManager.notify(NEW_ANSWER_NOTIFICATION_ID, createNotification);
            }
        }
    }

    protected FeedbackResponse doInBackground(Void... voidArr) {
        if (this.mContext == null || this.mFeedbackResponse == null) {
            return null;
        }
        FeedbackResponse parseFeedbackResponse = FeedbackParser.getInstance().parseFeedbackResponse(this.mFeedbackResponse);
        if (parseFeedbackResponse == null || parseFeedbackResponse.getFeedback() == null) {
            return parseFeedbackResponse;
        }
        ArrayList messages = parseFeedbackResponse.getFeedback().getMessages();
        if (messages == null || messages.isEmpty()) {
            return parseFeedbackResponse;
        }
        checkForNewAnswers(messages);
        return parseFeedbackResponse;
    }

    protected void onPostExecute(FeedbackResponse feedbackResponse) {
        if (feedbackResponse != null && this.mHandler != null) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_PARSE_FEEDBACK_RESPONSE, feedbackResponse);
            message.setData(bundle);
            this.mHandler.sendMessage(message);
        }
    }

    public void setUrlString(String str) {
        this.mUrlString = str;
    }
}
