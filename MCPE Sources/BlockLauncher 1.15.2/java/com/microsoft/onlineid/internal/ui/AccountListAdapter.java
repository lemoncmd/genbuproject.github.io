package com.microsoft.onlineid.internal.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.ui.AbstractListAdapter;
import java.util.HashSet;
import java.util.Set;

public class AccountListAdapter extends AbstractListAdapter<AuthenticatorUserAccount> {
    protected final Context _applicationContext;
    private final int _imageUserTileResId;
    protected final Resources _resources;
    private final int _textEmailResId;
    private final int _textFirstLastResId;
    protected final Set<TextView> _visibleAccounts;

    public AccountListAdapter() {
        this._visibleAccounts = new HashSet();
        this._applicationContext = null;
        this._resources = null;
        this._imageUserTileResId = 0;
        this._textFirstLastResId = 0;
        this._textEmailResId = 0;
    }

    public AccountListAdapter(Activity activity) {
        this._visibleAccounts = new HashSet();
        this._applicationContext = activity.getApplicationContext();
        this._resources = new Resources(this._applicationContext);
        this._imageUserTileResId = this._resources.getId("imageUserTile");
        this._textFirstLastResId = this._resources.getId("textFirstLast");
        this._textEmailResId = this._resources.getId("textEmail");
    }

    public long getItemId(int i) {
        return (long) ((AuthenticatorUserAccount) this._items.get(i)).hashCode();
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        AuthenticatorUserAccount authenticatorUserAccount = (AuthenticatorUserAccount) this._items.get(i);
        if (view == null) {
            view = ((LayoutInflater) viewGroup.getContext().getSystemService("layout_inflater")).inflate(this._resources.getLayout("account_picker_tile"), null);
        }
        setupBasicAccountViews(view, authenticatorUserAccount);
        return view;
    }

    public int getViewTypeCount() {
        return 1;
    }

    protected void onChanged() {
        super.onChanged();
        this._visibleAccounts.clear();
    }

    protected void setupBasicAccountViews(View view, AuthenticatorUserAccount authenticatorUserAccount) {
        ImageView imageView = (ImageView) view.findViewById(this._imageUserTileResId);
        TextView textView = (TextView) view.findViewById(this._textFirstLastResId);
        TextView textView2 = (TextView) view.findViewById(this._textEmailResId);
        authenticatorUserAccount.getDisplayName();
        textView2.setText(authenticatorUserAccount.getUsername());
        if (textView != null) {
            textView.setText(authenticatorUserAccount.getDisplayName());
        }
        new DownloadProfileImageTask(this._applicationContext, authenticatorUserAccount, imageView).execute(new Void[0]);
    }
}
