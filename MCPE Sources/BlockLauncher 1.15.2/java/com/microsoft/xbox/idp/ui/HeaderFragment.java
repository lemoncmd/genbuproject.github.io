package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.UserAccount;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.BitmapLoader.Result;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class HeaderFragment extends BaseFragment implements OnClickListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!HeaderFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final int LOADER_GET_PROFILE = 1;
    private static final int LOADER_USER_IMAGE_URL = 2;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onClickCloseHeader() {
        }
    };
    private static final String TAG = HeaderFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private final LoaderCallbacks<Result> imageCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_USER_IMAGE_URL");
            Uri parse = Uri.parse(HeaderFragment.this.userAccount.imageUrl);
            Log.d(HeaderFragment.TAG, "uri: " + parse);
            return new BitmapLoader(HeaderFragment.this.getActivity(), CacheUtil.getBitmapCache(), HeaderFragment.this.userAccount.imageUrl, new HttpCall(HttpEngine.GET, HttpUtil.getEndpoint(parse), HttpUtil.getPathAndQuery(parse)));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(HeaderFragment.TAG, "LOADER_USER_IMAGE_URL finished");
            if (result.hasData()) {
                HeaderFragment.this.userImageView.setVisibility(0);
                HeaderFragment.this.userImageView.setImageBitmap((Bitmap) result.getData());
                return;
            }
            HeaderFragment.this.userImageView.setVisibility(8);
            Log.w(HeaderFragment.TAG, "LOADER_USER_IMAGE_URL: " + result.getError());
        }

        public void onLoaderReset(Loader<Result> loader) {
            HeaderFragment.this.userImageView.setImageBitmap(null);
        }
    };
    private UserAccount userAccount;
    LoaderCallbacks<ObjectLoader.Result<UserAccount>> userAccountCallbacks = new LoaderCallbacks<ObjectLoader.Result<UserAccount>>() {
        public Loader<ObjectLoader.Result<UserAccount>> onCreateLoader(int i, Bundle bundle) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_GET_PROFILE");
            return new ObjectLoader(HeaderFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), new FragmentLoaderKey(HeaderFragment.class, HeaderFragment.LOADER_GET_PROFILE), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, EndpointsFactory.get().accounts(), "/users/current/profile"), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION));
        }

        public void onLoadFinished(Loader<ObjectLoader.Result<UserAccount>> loader, ObjectLoader.Result<UserAccount> result) {
            Log.d(HeaderFragment.TAG, "LOADER_GET_PROFILE finished");
            if (result.hasData()) {
                HeaderFragment.this.userAccount = (UserAccount) result.getData();
                HeaderFragment.this.userEmail.setText(HeaderFragment.this.userAccount.email);
                if (TextUtils.isEmpty(HeaderFragment.this.userAccount.firstName) && TextUtils.isEmpty(HeaderFragment.this.userAccount.lastName)) {
                    HeaderFragment.this.userName.setVisibility(8);
                } else {
                    HeaderFragment.this.userName.setVisibility(0);
                    TextView access$300 = HeaderFragment.this.userName;
                    HeaderFragment headerFragment = HeaderFragment.this;
                    int i = R.string.xbid_first_and_last_name_android;
                    Object[] objArr = new Object[HeaderFragment.LOADER_USER_IMAGE_URL];
                    objArr[0] = HeaderFragment.this.userAccount.firstName;
                    objArr[HeaderFragment.LOADER_GET_PROFILE] = HeaderFragment.this.userAccount.lastName;
                    access$300.setText(headerFragment.getString(i, objArr));
                }
                HeaderFragment.this.getLoaderManager().initLoader(HeaderFragment.LOADER_USER_IMAGE_URL, null, HeaderFragment.this.imageCallbacks);
                return;
            }
            Log.e(HeaderFragment.TAG, "Error getting UserAccount");
        }

        public void onLoaderReset(Loader<ObjectLoader.Result<UserAccount>> loader) {
        }
    };
    private TextView userEmail;
    private ImageView userImageView;
    private TextView userName;

    public interface Callbacks {
        void onClickCloseHeader();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.xbid_close) {
            this.callbacks.onClickCloseHeader();
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_header, viewGroup, $assertionsDisabled);
    }

    public void onDetach() {
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null) {
            getLoaderManager().initLoader(LOADER_GET_PROFILE, arguments, this.userAccountCallbacks);
        } else {
            Log.e(TAG, "No arguments provided");
        }
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.findViewById(R.id.xbid_close).setOnClickListener(this);
        this.userImageView = (ImageView) view.findViewById(R.id.xbid_user_image);
        this.userName = (TextView) view.findViewById(R.id.xbid_user_name);
        this.userEmail = (TextView) view.findViewById(R.id.xbid_user_email);
    }
}
