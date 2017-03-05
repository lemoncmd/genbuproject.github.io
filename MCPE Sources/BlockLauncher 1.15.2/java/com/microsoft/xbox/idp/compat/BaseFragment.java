package com.microsoft.xbox.idp.compat;

import android.app.Activity;
import android.app.Fragment;

public abstract class BaseFragment extends Fragment {
    public static final String ARG_ALT_BUTTON_TEXT = "ARG_ALT_BUTTON_TEXT";
    public static final String ARG_USER_PTR = "ARG_USER_PTR";

    protected CharSequence getActivityTitle() {
        Activity activity = getActivity();
        return activity == null ? null : activity.getTitle();
    }
}
