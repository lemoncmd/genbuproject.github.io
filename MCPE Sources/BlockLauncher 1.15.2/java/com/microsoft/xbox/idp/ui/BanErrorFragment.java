package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import net.hockeyapp.android.BuildConfig;

public class BanErrorFragment extends BaseFragment {
    public static final String ARG_GAMER_TAG = "ARG_GAMER_TAG";
    private static final String TAG = BanErrorFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_error_ban, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        Object string;
        super.onViewCreated(view, bundle);
        Bundle arguments = getArguments();
        String str;
        String str2;
        if (arguments == null) {
            str = BuildConfig.FLAVOR;
            Log.e(TAG, "No arguments provided");
            str2 = str;
        } else if (arguments.containsKey(ARG_GAMER_TAG)) {
            string = arguments.getString(ARG_GAMER_TAG);
        } else {
            str = BuildConfig.FLAVOR;
            Log.e(TAG, "No ARG_GAMER_TAG provided");
            str2 = str;
        }
        ((TextView) view.findViewById(R.id.xbid_greeting_text)).setText(getString(R.string.xbid_ban_error_header_android, new Object[]{string}));
    }
}
