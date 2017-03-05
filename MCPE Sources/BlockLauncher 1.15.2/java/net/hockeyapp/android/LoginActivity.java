package net.hockeyapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.Util;

public class LoginActivity extends Activity {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_SECRET = "secret";
    public static final String EXTRA_URL = "url";
    private Button mButtonLogin;
    private Handler mLoginHandler;
    private LoginTask mLoginTask;
    private int mMode;
    private String mSecret;
    private String mUrl;

    private static class LoginHandler extends Handler {
        private final WeakReference<Activity> mWeakActivity;

        public LoginHandler(Activity activity) {
            this.mWeakActivity = new WeakReference(activity);
        }

        public void handleMessage(Message message) {
            Activity activity = (Activity) this.mWeakActivity.get();
            if (activity != null) {
                if (message.getData().getBoolean(LoginTask.BUNDLE_SUCCESS)) {
                    activity.finish();
                    if (LoginManager.listener != null) {
                        LoginManager.listener.onSuccess();
                        return;
                    }
                    return;
                }
                Toast.makeText(activity, "Login failed. Check your credentials.", 1).show();
            }
        }
    }

    private void configureView() {
        if (this.mMode == 1) {
            ((EditText) findViewById(R.id.input_password)).setVisibility(4);
        }
        ((TextView) findViewById(R.id.text_headline)).setText(this.mMode == 1 ? R.string.hockeyapp_login_headline_text_email_only : R.string.hockeyapp_login_headline_text);
        this.mButtonLogin = (Button) findViewById(R.id.button_login);
        this.mButtonLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.performAuthentication();
            }
        });
    }

    private void initLoginHandler() {
        this.mLoginHandler = new LoginHandler(this);
    }

    private void performAuthentication() {
        int i = 0;
        if (Util.isConnectedToNetwork(this)) {
            CharSequence obj = ((EditText) findViewById(R.id.input_email)).getText().toString();
            CharSequence obj2 = ((EditText) findViewById(R.id.input_password)).getText().toString();
            Map hashMap = new HashMap();
            if (this.mMode == 1) {
                int i2 = !TextUtils.isEmpty(obj) ? 1 : 0;
                hashMap.put("email", obj);
                hashMap.put("authcode", md5(this.mSecret + obj));
                i = i2;
            } else if (this.mMode == 2) {
                if (!(TextUtils.isEmpty(obj) || TextUtils.isEmpty(obj2))) {
                    i = 1;
                }
                hashMap.put("email", obj);
                hashMap.put("password", obj2);
            }
            if (i != 0) {
                this.mLoginTask = new LoginTask(this, this.mLoginHandler, this.mUrl, this.mMode, hashMap);
                AsyncTaskUtils.execute(this.mLoginTask);
                return;
            }
            Toast.makeText(this, getString(R.string.hockeyapp_login_missing_credentials_toast), 1).show();
            return;
        }
        Toast.makeText(this, R.string.hockeyapp_error_no_network_message, 1).show();
    }

    public String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                String toHexString = Integer.toHexString(b & 255);
                while (toHexString.length() < 2) {
                    toHexString = MigrationManager.InitialSdkVersion + toHexString;
                }
                stringBuilder.append(toHexString);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.hockeyapp_activity_login);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mUrl = extras.getString(EXTRA_URL);
            this.mSecret = extras.getString(EXTRA_SECRET);
            this.mMode = extras.getInt(EXTRA_MODE);
        }
        configureView();
        initLoginHandler();
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            this.mLoginTask = (LoginTask) lastNonConfigurationInstance;
            this.mLoginTask.attach(this, this.mLoginHandler);
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (LoginManager.listener != null) {
                LoginManager.listener.onBack();
            } else {
                Intent intent = new Intent(this, LoginManager.mainActivity);
                intent.setFlags(67108864);
                intent.putExtra("net.hockeyapp.android.EXIT", true);
                startActivity(intent);
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mLoginTask != null) {
            this.mLoginTask.detach();
        }
        return this.mLoginTask;
    }
}
