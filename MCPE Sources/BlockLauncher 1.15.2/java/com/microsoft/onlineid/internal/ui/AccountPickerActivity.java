package com.microsoft.onlineid.internal.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequestResultReceiver;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Intents.DataBuilder;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AccountPickerActivity extends Activity {
    public static final String ActionPickAccount = "com.microsoft.onlineid.internal.PICK_ACCOUNT";
    public static final int AddAccountRequest = 1;
    private static final float BackgroundDimValue = 0.5f;
    private AccountListAdapter _accountList;
    private AuthenticatorAccountManager _accountManager;
    private Set<String> _cidExclusionList;
    private Resources _resources;
    private ActivityResultSender _resultSender;

    private class AddAccountFlowReceiver extends ApiRequestResultReceiver {
        public AddAccountFlowReceiver(Handler handler) {
            super(handler);
        }

        protected void onFailure(Exception exception) {
            Assertion.check(exception != null, "Request failed without Exception.");
            AccountPickerActivity.this.onException(exception);
        }

        protected void onSuccess(ApiResult apiResult) {
            ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.AddAccount, ClientAnalytics.ViaAccountPicker);
            AuthenticatorUserAccount accountByPuid = AccountPickerActivity.this._accountManager.getAccountByPuid(apiResult.getAccountPuid());
            if (accountByPuid == null) {
                AccountPickerActivity.this.onException(new InternalException("Picker could not find newly added account."));
            } else {
                AccountPickerActivity.this.onAccountPicked(accountByPuid);
            }
        }

        protected void onUINeeded(PendingIntent pendingIntent) {
            try {
                AccountPickerActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(), 0, null, 0, 0, 0);
            } catch (Exception e) {
                AccountPickerActivity.this.onException(e);
            }
        }

        protected void onUserCancel() {
            if (AccountPickerActivity.this._accountManager.getFilteredAccounts(AccountPickerActivity.this._cidExclusionList).isEmpty()) {
                AccountPickerActivity.this.finish();
            }
        }
    }

    private enum Extras {
        CidsToExclude;

        public String getKey() {
            return "com.microsoft.msa.authenticator." + name();
        }
    }

    public static Intent getAccountPickerIntent(Context context, ArrayList<String> arrayList, String str, String str2, String str3, Bundle bundle) {
        return new Intent().setClass(context, AccountPickerActivity.class).setAction(ActionPickAccount).putStringArrayListExtra(Extras.CidsToExclude.getKey(), arrayList).putExtra(BundleMarshaller.PreferredMembernameTypeKey, str).putExtra(BundleMarshaller.CobrandingIdKey, str2).putExtra(BundleMarshaller.ClientPackageNameKey, str3).putExtra(BundleMarshaller.ClientStateBundleKey, bundle).setData(new DataBuilder().add((List) arrayList).add(str).add(str2).add(str3).build());
    }

    private int getStatusBarHeight() {
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", AddAccountActivity.PlatformName);
        return identifier != 0 ? getResources().getDimensionPixelSize(identifier) : 0;
    }

    private void launchAddAccountFlow() {
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.InitiateAccountAdd, ClientAnalytics.ViaAccountPicker);
        startActivityForResult(new ApiRequest(getApplicationContext(), AddAccountActivity.getSignInIntent(getApplicationContext(), new SignInOptions(getIntent().getExtras()), getIntent().getStringExtra(BundleMarshaller.PreferredMembernameTypeKey), getIntent().getStringExtra(BundleMarshaller.CobrandingIdKey), false, getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey), getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey))).setResultReceiver(new AddAccountFlowReceiver(new Handler())).asIntent(), AddAccountRequest);
    }

    private void onAccountPicked(AuthenticatorUserAccount authenticatorUserAccount) {
        this._resultSender.putLimitedUserAccount(authenticatorUserAccount).set();
        finish();
    }

    private void onException(Exception exception) {
        this._resultSender.putException(exception).set();
        finish();
    }

    private void setupWindow() {
        requestWindowFeature(8);
        Window window = getWindow();
        LayoutParams attributes = window.getAttributes();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        window.addFlags(2);
        int statusBarHeight = displayMetrics.heightPixels - getStatusBarHeight();
        int dimensionPixelSize = displayMetrics.widthPixels - this._resources.getDimensionPixelSize("accountPickerMargin");
        int dimensionPixelSize2 = this._resources.getDimensionPixelSize("maxAccountPickerHeight");
        int dimensionPixelSize3 = this._resources.getDimensionPixelSize("maxAccountPickerWidth");
        attributes.height = statusBarHeight > dimensionPixelSize2 ? dimensionPixelSize2 : statusBarHeight;
        attributes.width = dimensionPixelSize > dimensionPixelSize3 ? dimensionPixelSize3 : dimensionPixelSize;
        attributes.gravity = statusBarHeight > dimensionPixelSize2 ? 17 : 80;
        attributes.dimAmount = BackgroundDimValue;
        window.setAttributes(attributes);
    }

    protected void onCreate(Bundle bundle) {
        this._resources = new Resources(getApplicationContext());
        setupWindow();
        AccountHeaderView.applyStyle(this, this._resources.getString("webflow_header"));
        super.onCreate(bundle);
        this._resultSender = new ActivityResultSender(this, ResultType.Account);
        setContentView(this._resources.getLayout("account_picker"));
        Bundle bundleExtra = getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey);
        String str = null;
        String stringExtra = getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey);
        if (stringExtra != null && stringExtra.equals(PackageInfoHelper.AuthenticatorPackageName)) {
            str = bundleExtra.getString(BundleMarshaller.AccountPickerBodyKey);
        }
        if (str == null) {
            str = this._resources.getString("account_picker_list_body");
        }
        getFragmentManager().beginTransaction().add(this._resources.getId("accountPickerBase"), BaseScreenFragment.buildWithBaseScreen(this._resources.getString("account_picker_list_header"), str, BaseScreenFragment.class)).commit();
        this._accountList = new AccountListAdapter(this);
        this._accountManager = new AuthenticatorAccountManager(getApplicationContext());
        Collection stringArrayListExtra = getIntent().getStringArrayListExtra(Extras.CidsToExclude.getKey());
        this._cidExclusionList = new HashSet();
        if (stringArrayListExtra != null) {
            this._cidExclusionList.addAll(stringArrayListExtra);
        }
        ListView listView = (ListView) findViewById(this._resources.getId("listAccounts"));
        listView.addFooterView(getLayoutInflater().inflate(this._resources.getLayout("add_account_tile"), listView, false));
        listView.setAdapter(this._accountList);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.PickAccount, ClientAnalytics.ViaAccountPicker);
                if (i == AccountPickerActivity.this._accountList.getCount()) {
                    AccountPickerActivity.this.launchAddAccountFlow();
                    return;
                }
                AccountPickerActivity.this.onAccountPicked((AuthenticatorUserAccount) AccountPickerActivity.this._accountList.getItem(i));
                AccountPickerActivity.this.finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(this._resources.getMenu("action_dismiss_account_picker"), menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != this._resources.getId("action_dismiss")) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    protected void onResume() {
        super.onResume();
        Collection filteredAccounts = this._accountManager.getFilteredAccounts(this._cidExclusionList);
        Locale locale = Locale.US;
        Object[] objArr = new Object[AddAccountRequest];
        objArr[0] = Integer.valueOf(filteredAccounts.size());
        Logger.info(String.format(locale, "%d active account(s)", objArr));
        if (filteredAccounts.isEmpty()) {
            launchAddAccountFlow();
        } else {
            this._accountList.setContent(filteredAccounts);
        }
    }

    protected void onStart() {
        super.onStart();
        ClientAnalytics.get().logScreenView(ClientAnalytics.AccountPickerScreen);
    }
}
