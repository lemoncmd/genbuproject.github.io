package net.hockeyapp.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.ipaulpro.afilechooser.utils.MimeTypeParser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask extends ConnectionTask<Void, Void, Boolean> {
    public static final String BUNDLE_SUCCESS = "success";
    private Context mContext;
    private Handler mHandler;
    private final int mMode;
    private final Map<String, String> mParams;
    private ProgressDialog mProgressDialog;
    private boolean mShowProgressDialog = true;
    private final String mUrlString;

    public LoginTask(Context context, Handler handler, String str, int i, Map<String, String> map) {
        this.mContext = context;
        this.mHandler = handler;
        this.mUrlString = str;
        this.mMode = i;
        this.mParams = map;
        if (context != null) {
            Constants.loadFromContext(context);
        }
    }

    private boolean handleResponse(String str) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("net.hockeyapp.android.login", 0);
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("status");
            if (TextUtils.isEmpty(string)) {
                return false;
            }
            Object string2;
            if (this.mMode == 1) {
                if (!string.equals("identified")) {
                    return false;
                }
                string2 = jSONObject.getString("iuid");
                if (TextUtils.isEmpty(string2)) {
                    return false;
                }
                sharedPreferences.edit().putString("iuid", string2).apply();
                return true;
            } else if (this.mMode == 2) {
                if (!string.equals("authorized")) {
                    return false;
                }
                string2 = jSONObject.getString("auid");
                if (TextUtils.isEmpty(string2)) {
                    return false;
                }
                sharedPreferences.edit().putString("auid", string2).apply();
                return true;
            } else if (this.mMode != 3) {
                throw new IllegalArgumentException("Login mode " + this.mMode + " not supported.");
            } else if (string.equals("validated")) {
                return true;
            } else {
                sharedPreferences.edit().remove("iuid").remove("auid").apply();
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HttpURLConnection makeRequest(int i, Map<String, String> map) throws IOException {
        if (i == 1) {
            return new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod(HttpEngine.POST).writeFormFields(map).build();
        }
        if (i == 2) {
            return new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod(HttpEngine.POST).setBasicAuthorization((String) map.get("email"), (String) map.get("password")).build();
        }
        if (i == 3) {
            return new HttpURLConnectionBuilder(this.mUrlString + "?" + ((String) map.get(MimeTypeParser.TAG_TYPE)) + "=" + ((String) map.get(Name.MARK))).build();
        }
        throw new IllegalArgumentException("Login mode " + i + " not supported.");
    }

    public void attach(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void detach() {
        this.mContext = null;
        this.mHandler = null;
        this.mProgressDialog = null;
    }

    protected Boolean doInBackground(Void... voidArr) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = makeRequest(this.mMode, this.mParams);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == org.mozilla.javascript.Context.VERSION_ES6) {
                Object stringFromConnection = ConnectionTask.getStringFromConnection(httpURLConnection);
                if (!TextUtils.isEmpty(stringFromConnection)) {
                    Boolean valueOf = Boolean.valueOf(handleResponse(stringFromConnection));
                    if (httpURLConnection == null) {
                        return valueOf;
                    }
                    httpURLConnection.disconnect();
                    return valueOf;
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (Throwable th) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return Boolean.valueOf(false);
    }

    protected void onPostExecute(Boolean bool) {
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
            bundle.putBoolean(BUNDLE_SUCCESS, bool.booleanValue());
            message.setData(bundle);
            this.mHandler.sendMessage(message);
        }
    }

    protected void onPreExecute() {
        if ((this.mProgressDialog == null || !this.mProgressDialog.isShowing()) && this.mShowProgressDialog) {
            this.mProgressDialog = ProgressDialog.show(this.mContext, BuildConfig.FLAVOR, "Please wait...", true, false);
        }
    }

    public void setShowProgressDialog(boolean z) {
        this.mShowProgressDialog = z;
    }
}
