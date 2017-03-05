package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequest.Extras;
import com.microsoft.onlineid.internal.ApiRequestResultReceiver;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Intents.DataBuilder;
import com.microsoft.onlineid.internal.MsaService;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class SignOutActivity extends Activity {
    private boolean _isSignedOutOfThisAppOnly;
    private ActivityResultSender _resultSender;
    private String _userCid;

    private class SignOutResultReceiver extends ApiRequestResultReceiver {
        public SignOutResultReceiver() {
            super(new Handler());
        }

        protected void onFailure(Exception exception) {
            SignOutActivity.this._resultSender.putException(exception).set();
            SignOutActivity.this.finish();
        }

        protected void onSuccess(ApiResult apiResult) {
            SignOutActivity.this._resultSender.putSignedOutCid(SignOutActivity.this._userCid, SignOutActivity.this._isSignedOutOfThisAppOnly).set();
            SignOutActivity.this.finish();
        }

        protected void onUINeeded(PendingIntent pendingIntent) {
            onFailure(new UnsupportedOperationException("onUINeeded not expected for sign out request."));
        }

        protected void onUserCancel() {
            SignOutActivity.this.finish();
        }
    }

    private AlertDialog buildDialog() {
        final Context applicationContext = getApplicationContext();
        final ApiRequest apiRequest = new ApiRequest(applicationContext, getIntent());
        Resources resources = new Resources(applicationContext);
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(resources.getLayout("sign_out_custom_content_view"), null);
        final CheckBox checkBox = (CheckBox) relativeLayout.findViewById(resources.getId("signOutCheckBox"));
        checkBox.setText(String.format(resources.getString("sign_out_dialog_checkbox"), new Object[]{apiRequest.getAccountName()}));
        OnClickListener anonymousClass1 = new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String accountPuid = apiRequest.getAccountPuid();
                String str = checkBox.isChecked() ? MsaService.ActionSignOutAllApps : MsaService.ActionSignOut;
                SignOutActivity.this._isSignedOutOfThisAppOnly = !checkBox.isChecked();
                new ApiRequest(applicationContext, new Intent(applicationContext, MsaService.class).setAction(str)).setAccountPuid(accountPuid).setResultReceiver(new SignOutResultReceiver()).executeAsync();
                dialogInterface.dismiss();
            }
        };
        OnClickListener anonymousClass2 = new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        };
        OnCancelListener anonymousClass3 = new OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                SignOutActivity.this.finish();
            }
        };
        Builder builder = new Builder(this);
        builder.setView(relativeLayout).setTitle(resources.getString("sign_out_dialog_title")).setPositiveButton(resources.getString("sign_out_dialog_button_sign_out"), anonymousClass1).setNegativeButton(resources.getString("sign_out_dialog_button_cancel"), anonymousClass2).setOnCancelListener(anonymousClass3);
        return builder.create();
    }

    public static Intent getSignOutIntent(Context context, String str, String str2, String str3, Bundle bundle) {
        return new Intent(context, SignOutActivity.class).putExtra(Extras.AccountPuid.getKey(), str).putExtra(Extras.AccountName.getKey(), str3).putExtra(BundleMarshaller.UserCidKey, str2).putExtra(BundleMarshaller.ClientStateBundleKey, bundle).setData(new DataBuilder().add(str).add(str3).build());
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this._resultSender = new ActivityResultSender(this, ResultType.Account);
        this._userCid = getIntent().getStringExtra(BundleMarshaller.UserCidKey);
        buildDialog().show();
    }
}
