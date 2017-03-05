package com.microsoft.xbox.idp.compat;

import android.app.Activity;

public abstract class BaseActivity extends Activity {
    public void addFragment(int i, BaseFragment baseFragment) {
        getFragmentManager().beginTransaction().add(i, baseFragment).commit();
    }

    public boolean hasFragment(int i) {
        return getFragmentManager().findFragmentById(i) != null;
    }
}
