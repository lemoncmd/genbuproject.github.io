package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.hockeyapp.android.adapters.MessagesAdapter;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.objects.FeedbackUserDataElement;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.tasks.ParseFeedbackTask;
import net.hockeyapp.android.tasks.SendFeedbackTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.PrefsUtil;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.views.AttachmentListView;
import net.hockeyapp.android.views.AttachmentView;

public class FeedbackActivity extends Activity implements OnClickListener {
    private static final int ATTACH_FILE = 2;
    private static final int ATTACH_PICTURE = 1;
    private static final int DIALOG_ERROR_ID = 0;
    public static final String EXTRA_INITIAL_ATTACHMENTS = "initialAttachments";
    public static final String EXTRA_INITIAL_USER_EMAIL = "initialUserEmail";
    public static final String EXTRA_INITIAL_USER_NAME = "initialUserName";
    public static final String EXTRA_URL = "url";
    private static final int MAX_ATTACHMENTS_PER_MSG = 3;
    private static final int PAINT_IMAGE = 3;
    private String initialUserEmail;
    private String initialUserName;
    private Button mAddAttachmentButton;
    private Button mAddResponseButton;
    private Context mContext;
    private EditText mEmailInput;
    private ErrorObject mError;
    private Handler mFeedbackHandler;
    private ArrayList<FeedbackMessage> mFeedbackMessages;
    private ScrollView mFeedbackScrollview;
    private boolean mFeedbackViewInitialized;
    private boolean mInSendFeedback;
    private List<Uri> mInitialAttachments;
    private TextView mLastUpdatedTextView;
    private MessagesAdapter mMessagesAdapter;
    private ListView mMessagesListView;
    private EditText mNameInput;
    private Handler mParseFeedbackHandler;
    private ParseFeedbackTask mParseFeedbackTask;
    private Button mRefreshButton;
    private Button mSendFeedbackButton;
    private SendFeedbackTask mSendFeedbackTask;
    private EditText mSubjectInput;
    private EditText mTextInput;
    private String mToken;
    private String mUrl;
    private LinearLayout mWrapperLayoutFeedbackAndMessages;

    private static class FeedbackHandler extends Handler {
        private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;

        public FeedbackHandler(FeedbackActivity feedbackActivity) {
            this.mWeakFeedbackActivity = new WeakReference(feedbackActivity);
        }

        public void handleMessage(Message message) {
            boolean z = false;
            ErrorObject errorObject = new ErrorObject();
            final FeedbackActivity feedbackActivity = (FeedbackActivity) this.mWeakFeedbackActivity.get();
            if (feedbackActivity != null) {
                if (message == null || message.getData() == null) {
                    errorObject.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
                } else {
                    Bundle data = message.getData();
                    String string = data.getString(SendFeedbackTask.BUNDLE_FEEDBACK_RESPONSE);
                    String string2 = data.getString(SendFeedbackTask.BUNDLE_FEEDBACK_STATUS);
                    String string3 = data.getString(SendFeedbackTask.BUNDLE_REQUEST_TYPE);
                    if (string3.equals("send") && (string == null || Integer.parseInt(string2) != 201)) {
                        errorObject.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_generic_error));
                    } else if (string3.equals("fetch") && string2 != null && (Integer.parseInt(string2) == 404 || Integer.parseInt(string2) == 422)) {
                        feedbackActivity.resetFeedbackView();
                        z = true;
                    } else if (string != null) {
                        feedbackActivity.startParseFeedbackTask(string, string3);
                        z = true;
                    } else {
                        errorObject.setMessage(feedbackActivity.getString(R.string.hockeyapp_feedback_send_network_error));
                    }
                }
                feedbackActivity.mError = errorObject;
                if (!z) {
                    feedbackActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            feedbackActivity.enableDisableSendFeedbackButton(true);
                            feedbackActivity.showDialog(FeedbackActivity.DIALOG_ERROR_ID);
                        }
                    });
                }
                feedbackActivity.onSendFeedbackResult(z);
            }
        }
    }

    private static class ParseFeedbackHandler extends Handler {
        private final WeakReference<FeedbackActivity> mWeakFeedbackActivity;

        public ParseFeedbackHandler(FeedbackActivity feedbackActivity) {
            this.mWeakFeedbackActivity = new WeakReference(feedbackActivity);
        }

        public void handleMessage(Message message) {
            final FeedbackActivity feedbackActivity = (FeedbackActivity) this.mWeakFeedbackActivity.get();
            if (feedbackActivity != null) {
                boolean z;
                feedbackActivity.mError = new ErrorObject();
                if (!(message == null || message.getData() == null)) {
                    FeedbackResponse feedbackResponse = (FeedbackResponse) message.getData().getSerializable(ParseFeedbackTask.BUNDLE_PARSE_FEEDBACK_RESPONSE);
                    if (feedbackResponse != null) {
                        if (feedbackResponse.getStatus().equalsIgnoreCase(LoginTask.BUNDLE_SUCCESS)) {
                            if (feedbackResponse.getToken() != null) {
                                PrefsUtil.getInstance().saveFeedbackTokenToPrefs(feedbackActivity, feedbackResponse.getToken());
                                feedbackActivity.loadFeedbackMessages(feedbackResponse);
                                feedbackActivity.mInSendFeedback = false;
                            }
                            z = true;
                        } else {
                            z = false;
                        }
                        if (!z) {
                            feedbackActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    feedbackActivity.showDialog(FeedbackActivity.DIALOG_ERROR_ID);
                                }
                            });
                        }
                        feedbackActivity.enableDisableSendFeedbackButton(true);
                    }
                }
                z = false;
                if (z) {
                    feedbackActivity.runOnUiThread(/* anonymous class already generated */);
                }
                feedbackActivity.enableDisableSendFeedbackButton(true);
            }
        }
    }

    private boolean addAttachment(int i) {
        Intent intent;
        if (i == ATTACH_FILE) {
            intent = new Intent();
            intent.setType(FileChooserActivity.MIME_TYPE_ALL);
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hockeyapp_feedback_select_file)), ATTACH_FILE);
            return true;
        } else if (i != ATTACH_PICTURE) {
            return false;
        } else {
            intent = new Intent();
            intent.setType(FileUtils.MIME_TYPE_IMAGE);
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hockeyapp_feedback_select_picture)), ATTACH_PICTURE);
            return true;
        }
    }

    private void configureAppropriateView() {
        this.mToken = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this);
        if (this.mToken == null || this.mInSendFeedback) {
            configureFeedbackView(false);
            return;
        }
        configureFeedbackView(true);
        sendFetchFeedback(this.mUrl, null, null, null, null, null, this.mToken, this.mFeedbackHandler, true);
    }

    private void createParseFeedbackTask(String str, String str2) {
        this.mParseFeedbackTask = new ParseFeedbackTask(this, str, this.mParseFeedbackHandler, str2);
    }

    private void hideKeyboard() {
        if (this.mTextInput != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.mTextInput.getWindowToken(), DIALOG_ERROR_ID);
        }
    }

    private void initFeedbackHandler() {
        this.mFeedbackHandler = new FeedbackHandler(this);
    }

    private void initParseFeedbackHandler() {
        this.mParseFeedbackHandler = new ParseFeedbackHandler(this);
    }

    @SuppressLint({"SimpleDateFormat"})
    private void loadFeedbackMessages(final FeedbackResponse feedbackResponse) {
        runOnUiThread(new Runnable() {
            public void run() {
                FeedbackActivity.this.configureFeedbackView(true);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("d MMM h:mm a");
                if (feedbackResponse != null && feedbackResponse.getFeedback() != null && feedbackResponse.getFeedback().getMessages() != null && feedbackResponse.getFeedback().getMessages().size() > 0) {
                    FeedbackActivity.this.mFeedbackMessages = feedbackResponse.getFeedback().getMessages();
                    Collections.reverse(FeedbackActivity.this.mFeedbackMessages);
                    try {
                        Date parse = simpleDateFormat.parse(((FeedbackMessage) FeedbackActivity.this.mFeedbackMessages.get(FeedbackActivity.DIALOG_ERROR_ID)).getCreatedAt());
                        TextView access$200 = FeedbackActivity.this.mLastUpdatedTextView;
                        FeedbackActivity feedbackActivity = FeedbackActivity.this;
                        int i = R.string.hockeyapp_feedback_last_updated_text;
                        Object[] objArr = new Object[FeedbackActivity.ATTACH_PICTURE];
                        objArr[FeedbackActivity.DIALOG_ERROR_ID] = simpleDateFormat2.format(parse);
                        access$200.setText(feedbackActivity.getString(i, objArr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (FeedbackActivity.this.mMessagesAdapter == null) {
                        FeedbackActivity.this.mMessagesAdapter = new MessagesAdapter(FeedbackActivity.this.mContext, FeedbackActivity.this.mFeedbackMessages);
                    } else {
                        FeedbackActivity.this.mMessagesAdapter.clear();
                        Iterator it = FeedbackActivity.this.mFeedbackMessages.iterator();
                        while (it.hasNext()) {
                            FeedbackActivity.this.mMessagesAdapter.add((FeedbackMessage) it.next());
                        }
                        FeedbackActivity.this.mMessagesAdapter.notifyDataSetChanged();
                    }
                    FeedbackActivity.this.mMessagesListView.setAdapter(FeedbackActivity.this.mMessagesAdapter);
                }
            }
        });
    }

    private void resetFeedbackView() {
        runOnUiThread(new Runnable() {
            public void run() {
                PrefsUtil.getInstance().saveFeedbackTokenToPrefs(FeedbackActivity.this, null);
                FeedbackActivity.this.getSharedPreferences(ParseFeedbackTask.PREFERENCES_NAME, FeedbackActivity.DIALOG_ERROR_ID).edit().remove(ParseFeedbackTask.ID_LAST_MESSAGE_SEND).remove(ParseFeedbackTask.ID_LAST_MESSAGE_PROCESSED).apply();
                FeedbackActivity.this.configureFeedbackView(false);
            }
        });
    }

    private void sendFeedback() {
        if (Util.isConnectedToNetwork(this)) {
            enableDisableSendFeedbackButton(false);
            hideKeyboard();
            String feedbackTokenFromPrefs = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext);
            String trim = this.mNameInput.getText().toString().trim();
            String trim2 = this.mEmailInput.getText().toString().trim();
            String trim3 = this.mSubjectInput.getText().toString().trim();
            Object trim4 = this.mTextInput.getText().toString().trim();
            if (TextUtils.isEmpty(trim3)) {
                this.mSubjectInput.setVisibility(DIALOG_ERROR_ID);
                setError(this.mSubjectInput, R.string.hockeyapp_feedback_validate_subject_error);
                return;
            } else if (FeedbackManager.getRequireUserName() == FeedbackUserDataElement.REQUIRED && TextUtils.isEmpty(trim)) {
                setError(this.mNameInput, R.string.hockeyapp_feedback_validate_name_error);
                return;
            } else if (FeedbackManager.getRequireUserEmail() == FeedbackUserDataElement.REQUIRED && TextUtils.isEmpty(trim2)) {
                setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_empty);
                return;
            } else if (TextUtils.isEmpty(trim4)) {
                setError(this.mTextInput, R.string.hockeyapp_feedback_validate_text_error);
                return;
            } else if (FeedbackManager.getRequireUserEmail() != FeedbackUserDataElement.REQUIRED || Util.isValidEmail(trim2)) {
                PrefsUtil.getInstance().saveNameEmailSubjectToPrefs(this.mContext, trim, trim2, trim3);
                sendFetchFeedback(this.mUrl, trim, trim2, trim3, trim4, ((AttachmentListView) findViewById(R.id.wrapper_attachments)).getAttachments(), feedbackTokenFromPrefs, this.mFeedbackHandler, false);
                return;
            } else {
                setError(this.mEmailInput, R.string.hockeyapp_feedback_validate_email_error);
                return;
            }
        }
        Toast.makeText(this, R.string.hockeyapp_error_no_network_message, ATTACH_PICTURE).show();
    }

    private void sendFetchFeedback(String str, String str2, String str3, String str4, String str5, List<Uri> list, String str6, Handler handler, boolean z) {
        this.mSendFeedbackTask = new SendFeedbackTask(this.mContext, str, str2, str3, str4, str5, list, str6, handler, z);
        AsyncTaskUtils.execute(this.mSendFeedbackTask);
    }

    private void setError(EditText editText, int i) {
        editText.setError(getString(i));
        enableDisableSendFeedbackButton(true);
    }

    private void startParseFeedbackTask(String str, String str2) {
        createParseFeedbackTask(str, str2);
        AsyncTaskUtils.execute(this.mParseFeedbackTask);
    }

    protected void configureFeedbackView(boolean z) {
        this.mFeedbackScrollview = (ScrollView) findViewById(R.id.wrapper_feedback_scroll);
        this.mWrapperLayoutFeedbackAndMessages = (LinearLayout) findViewById(R.id.wrapper_messages);
        this.mMessagesListView = (ListView) findViewById(R.id.list_feedback_messages);
        if (z) {
            this.mWrapperLayoutFeedbackAndMessages.setVisibility(DIALOG_ERROR_ID);
            this.mFeedbackScrollview.setVisibility(8);
            this.mLastUpdatedTextView = (TextView) findViewById(R.id.label_last_updated);
            this.mAddResponseButton = (Button) findViewById(R.id.button_add_response);
            this.mAddResponseButton.setOnClickListener(this);
            this.mRefreshButton = (Button) findViewById(R.id.button_refresh);
            this.mRefreshButton.setOnClickListener(this);
            return;
        }
        this.mWrapperLayoutFeedbackAndMessages.setVisibility(8);
        this.mFeedbackScrollview.setVisibility(DIALOG_ERROR_ID);
        this.mNameInput = (EditText) findViewById(R.id.input_name);
        this.mEmailInput = (EditText) findViewById(R.id.input_email);
        this.mSubjectInput = (EditText) findViewById(R.id.input_subject);
        this.mTextInput = (EditText) findViewById(R.id.input_message);
        if (!this.mFeedbackViewInitialized) {
            String nameEmailFromPrefs = PrefsUtil.getInstance().getNameEmailFromPrefs(this.mContext);
            if (nameEmailFromPrefs != null) {
                String[] split = nameEmailFromPrefs.split("\\|");
                if (split != null && split.length >= ATTACH_FILE) {
                    this.mNameInput.setText(split[DIALOG_ERROR_ID]);
                    this.mEmailInput.setText(split[ATTACH_PICTURE]);
                    if (split.length >= PAINT_IMAGE) {
                        this.mSubjectInput.setText(split[ATTACH_FILE]);
                        this.mTextInput.requestFocus();
                    } else {
                        this.mSubjectInput.requestFocus();
                    }
                }
            } else {
                this.mNameInput.setText(this.initialUserName);
                this.mEmailInput.setText(this.initialUserEmail);
                this.mSubjectInput.setText(BuildConfig.FLAVOR);
                if (TextUtils.isEmpty(this.initialUserName)) {
                    this.mNameInput.requestFocus();
                } else if (TextUtils.isEmpty(this.initialUserEmail)) {
                    this.mEmailInput.requestFocus();
                } else {
                    this.mSubjectInput.requestFocus();
                }
            }
            this.mFeedbackViewInitialized = true;
        }
        this.mTextInput.setText(BuildConfig.FLAVOR);
        if (PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext) != null) {
            this.mSubjectInput.setVisibility(8);
        } else {
            this.mSubjectInput.setVisibility(DIALOG_ERROR_ID);
        }
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.wrapper_attachments);
        viewGroup.removeAllViews();
        if (this.mInitialAttachments != null) {
            for (Uri attachmentView : this.mInitialAttachments) {
                viewGroup.addView(new AttachmentView((Context) this, viewGroup, attachmentView, true));
            }
        }
        this.mAddAttachmentButton = (Button) findViewById(R.id.button_attachment);
        this.mAddAttachmentButton.setOnClickListener(this);
        registerForContextMenu(this.mAddAttachmentButton);
        this.mSendFeedbackButton = (Button) findViewById(R.id.button_send);
        this.mSendFeedbackButton.setOnClickListener(this);
    }

    public void enableDisableSendFeedbackButton(boolean z) {
        if (this.mSendFeedbackButton != null) {
            this.mSendFeedbackButton.setEnabled(z);
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(R.layout.hockeyapp_activity_feedback, null);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (i == ATTACH_FILE) {
                Uri data = intent.getData();
                if (data != null) {
                    ViewGroup viewGroup = (ViewGroup) findViewById(R.id.wrapper_attachments);
                    viewGroup.addView(new AttachmentView((Context) this, viewGroup, data, true));
                }
            } else if (i == ATTACH_PICTURE) {
                Parcelable data2 = intent.getData();
                if (data2 != null) {
                    try {
                        Intent intent2 = new Intent(this, PaintActivity.class);
                        intent2.putExtra(PaintActivity.EXTRA_IMAGE_URI, data2);
                        startActivityForResult(intent2, PAINT_IMAGE);
                    } catch (Throwable e) {
                        HockeyLog.error(Util.LOG_IDENTIFIER, "Paint activity not declared!", e);
                    }
                }
            } else if (i == PAINT_IMAGE) {
                Uri uri = (Uri) intent.getParcelableExtra(PaintActivity.EXTRA_IMAGE_URI);
                if (uri != null) {
                    ViewGroup viewGroup2 = (ViewGroup) findViewById(R.id.wrapper_attachments);
                    viewGroup2.addView(new AttachmentView((Context) this, viewGroup2, uri, true));
                }
            }
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_send) {
            sendFeedback();
        } else if (id == R.id.button_attachment) {
            if (((ViewGroup) findViewById(R.id.wrapper_attachments)).getChildCount() >= PAINT_IMAGE) {
                Toast.makeText(this, String.valueOf(PAINT_IMAGE), DIALOG_ERROR_ID).show();
            } else {
                openContextMenu(view);
            }
        } else if (id == R.id.button_add_response) {
            configureFeedbackView(false);
            this.mInSendFeedback = true;
        } else if (id == R.id.button_refresh) {
            sendFetchFeedback(this.mUrl, null, null, null, null, null, PrefsUtil.getInstance().getFeedbackTokenFromPrefs(this.mContext), this.mFeedbackHandler, true);
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case ATTACH_PICTURE /*1*/:
            case ATTACH_FILE /*2*/:
                return addAttachment(menuItem.getItemId());
            default:
                return super.onContextItemSelected(menuItem);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getLayoutView());
        setTitle(getString(R.string.hockeyapp_feedback_title));
        this.mContext = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mUrl = extras.getString(EXTRA_URL);
            this.initialUserName = extras.getString(EXTRA_INITIAL_USER_NAME);
            this.initialUserEmail = extras.getString(EXTRA_INITIAL_USER_EMAIL);
            Parcelable[] parcelableArray = extras.getParcelableArray(EXTRA_INITIAL_ATTACHMENTS);
            if (parcelableArray != null) {
                this.mInitialAttachments = new ArrayList();
                int length = parcelableArray.length;
                for (int i = DIALOG_ERROR_ID; i < length; i += ATTACH_PICTURE) {
                    this.mInitialAttachments.add((Uri) parcelableArray[i]);
                }
            }
        }
        if (bundle != null) {
            this.mFeedbackViewInitialized = bundle.getBoolean("feedbackViewInitialized");
            this.mInSendFeedback = bundle.getBoolean("inSendFeedback");
        } else {
            this.mInSendFeedback = false;
            this.mFeedbackViewInitialized = false;
        }
        ((NotificationManager) getSystemService("notification")).cancel(ATTACH_FILE);
        initFeedbackHandler();
        initParseFeedbackHandler();
        configureAppropriateView();
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(DIALOG_ERROR_ID, ATTACH_FILE, DIALOG_ERROR_ID, getString(R.string.hockeyapp_feedback_attach_file));
        contextMenu.add(DIALOG_ERROR_ID, ATTACH_PICTURE, DIALOG_ERROR_ID, getString(R.string.hockeyapp_feedback_attach_picture));
    }

    protected Dialog onCreateDialog(int i) {
        switch (i) {
            case DIALOG_ERROR_ID /*0*/:
                return new Builder(this).setMessage(getString(R.string.hockeyapp_dialog_error_message)).setCancelable(false).setTitle(getString(R.string.hockeyapp_dialog_error_title)).setIcon(17301543).setPositiveButton(getString(R.string.hockeyapp_dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FeedbackActivity.this.mError = null;
                        dialogInterface.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (this.mInSendFeedback) {
            this.mInSendFeedback = false;
            configureAppropriateView();
        } else {
            finish();
        }
        return true;
    }

    protected void onPrepareDialog(int i, Dialog dialog) {
        switch (i) {
            case DIALOG_ERROR_ID /*0*/:
                AlertDialog alertDialog = (AlertDialog) dialog;
                if (this.mError != null) {
                    alertDialog.setMessage(this.mError.getMessage());
                    return;
                } else {
                    alertDialog.setMessage(getString(R.string.hockeyapp_feedback_generic_error));
                    return;
                }
            default:
                return;
        }
    }

    protected void onRestoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.wrapper_attachments);
            Iterator it = bundle.getParcelableArrayList("attachments").iterator();
            while (it.hasNext()) {
                Uri uri = (Uri) it.next();
                if (!this.mInitialAttachments.contains(uri)) {
                    viewGroup.addView(new AttachmentView((Context) this, viewGroup, uri, true));
                }
            }
            this.mFeedbackViewInitialized = bundle.getBoolean("feedbackViewInitialized");
        }
        super.onRestoreInstanceState(bundle);
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mSendFeedbackTask != null) {
            this.mSendFeedbackTask.detach();
        }
        return this.mSendFeedbackTask;
    }

    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("attachments", ((AttachmentListView) findViewById(R.id.wrapper_attachments)).getAttachments());
        bundle.putBoolean("feedbackViewInitialized", this.mFeedbackViewInitialized);
        bundle.putBoolean("inSendFeedback", this.mInSendFeedback);
        super.onSaveInstanceState(bundle);
    }

    protected void onSendFeedbackResult(boolean z) {
    }

    protected void onStop() {
        super.onStop();
        if (this.mSendFeedbackTask != null) {
            this.mSendFeedbackTask.detach();
        }
    }
}
