package com.microsoft.xboxtcui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEUnhandledExceptionHandler;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.NavigationManager.NavigationCallbacks;
import com.microsoft.xbox.toolkit.ui.NavigationManager.OnNavigatedListener;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.XleProjectSpecificDataProvider;
import java.util.Stack;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public class XboxTcuiWindow extends FrameLayout implements NavigationCallbacks, OnNavigatedListener {
    private static final int NAVIGATION_BLOCK_TIMEOUT_MS = 5000;
    private static final String TAG = XboxTcuiWindow.class.getSimpleName();
    private Activity activity;
    private boolean animationBlocking;
    private final ActivityParameters launchParams;
    private final Class<? extends ScreenLayout> launchScreenClass;
    private final Stack<ScreenLayout> screens = new Stack();
    private boolean wasRestarted;

    public XboxTcuiWindow(Activity activity, Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) {
        super(activity);
        XLEAssert.assertNotNull(activityParameters.getMeXuid());
        this.activity = activity;
        this.launchScreenClass = cls;
        this.launchParams = activityParameters;
        setBackgroundResource(R.color.backgroundColor);
    }

    private void setupNavigationManager() {
        NavigationManager.getInstance().setNavigationCallbacks(this);
        NavigationManager.getInstance().setOnNavigatedListener(this);
        try {
            NavigationManager.getInstance().PopAllScreens();
        } catch (Throwable e) {
            Log.e(TAG, "setupNavigationManager: " + Log.getStackTraceString(e));
        }
    }

    private void setupThreadManager() {
        ThreadManager.UIThread = Thread.currentThread();
        ThreadManager.Handler = new Handler();
        Thread thread = ThreadManager.UIThread;
        Thread.setDefaultUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
    }

    public void addContentViewXLE(ScreenLayout screenLayout) {
        if (!this.screens.isEmpty()) {
            if (screenLayout == this.screens.peek()) {
                screenLayout.setAllEventsEnabled(true);
                return;
            } else if (screenLayout.isKeepPreviousScreen()) {
                ((ScreenLayout) this.screens.peek()).setAllEventsEnabled(false);
            } else {
                removeView((View) this.screens.pop());
            }
        }
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        layoutParams.addRule(10);
        layoutParams.addRule(12);
        addView(screenLayout, layoutParams);
        this.screens.push(screenLayout);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return !NavigationManager.getInstance().onKey(this, keyEvent.getKeyCode(), keyEvent) ? super.dispatchKeyEvent(keyEvent) : true;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if (view != this) {
            return false;
        }
        View findNextFocus;
        switch (i) {
            case NativeRegExp.MATCH /*1*/:
            case Token.GETPROP /*33*/:
                findNextFocus = FocusFinder.getInstance().findNextFocus(this, getFocusedChild(), 33);
                if (findNextFocus != null) {
                    findNextFocus.requestFocus();
                    break;
                }
                break;
            case NativeRegExp.PREFIX /*2*/:
            case Token.BLOCK /*130*/:
                findNextFocus = FocusFinder.getInstance().findNextFocus(this, getFocusedChild(), Token.BLOCK);
                if (findNextFocus != null) {
                    findNextFocus.requestFocus();
                    break;
                }
                break;
        }
        return true;
    }

    public void onBeforeNavigatingIn() {
    }

    public void onCreate(Bundle bundle) {
        this.wasRestarted = bundle != null;
        setupThreadManager();
        ProjectSpecificDataProvider.getInstance().setProvider(XleProjectSpecificDataProvider.getInstance());
        String xuidString = ProjectSpecificDataProvider.getInstance().getXuidString();
        if (!(JavaUtil.isNullOrEmpty(xuidString) || xuidString.equalsIgnoreCase(this.launchParams.getMeXuid()))) {
            ProfileModel.getMeProfileModel();
            ProfileModel.reset();
        }
        ProjectSpecificDataProvider.getInstance().setXuidString(this.launchParams.getMeXuid());
        ProjectSpecificDataProvider.getInstance().setPrivileges(this.launchParams.getPrivileges());
        DialogManager.getInstance().setManager(SGProjectSpecificDialogManager.getInstance());
        setFocusableInTouchMode(true);
        requestFocus();
        setupNavigationManager();
    }

    public void onPageNavigated(ScreenLayout screenLayout, ScreenLayout screenLayout2) {
    }

    public void onPageRestarted(ScreenLayout screenLayout) {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStart() {
        /*
        r5 = this;
        r4 = 0;
        r0 = r5.activity;
        com.microsoft.xboxtcui.XboxTcuiSdk.sdkInitialize(r0);
        r0 = com.microsoft.xbox.toolkit.DialogManager.getInstance();
        r1 = 1;
        r0.setEnabled(r1);
        r0 = r5.wasRestarted;	 Catch:{ XLEException -> 0x0046 }
        if (r0 == 0) goto L_0x003a;
    L_0x0012:
        r0 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0046 }
        r0 = r0.getCurrentActivity();	 Catch:{ XLEException -> 0x0046 }
        if (r0 == 0) goto L_0x0037;
    L_0x001c:
        r1 = new android.os.Bundle;	 Catch:{ XLEException -> 0x0046 }
        r1.<init>();	 Catch:{ XLEException -> 0x0046 }
        r2 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0046 }
        r2 = r2.getCurrentActivity();	 Catch:{ XLEException -> 0x0046 }
        r2.onSaveInstanceState(r1);	 Catch:{ XLEException -> 0x0046 }
        r2 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0046 }
        r3 = 0;
        r2.RestartCurrentScreen(r3);	 Catch:{ XLEException -> 0x0046 }
        r0.onRestoreInstanceState(r1);	 Catch:{ XLEException -> 0x0046 }
    L_0x0037:
        r5.wasRestarted = r4;
    L_0x0039:
        return;
    L_0x003a:
        r0 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0046 }
        r1 = r5.launchScreenClass;	 Catch:{ XLEException -> 0x0046 }
        r2 = r5.launchParams;	 Catch:{ XLEException -> 0x0046 }
        r0.PushScreen(r1, r2);	 Catch:{ XLEException -> 0x0046 }
        goto L_0x0037;
    L_0x0046:
        r0 = move-exception;
        r1 = TAG;	 Catch:{ all -> 0x0066 }
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0066 }
        r2.<init>();	 Catch:{ all -> 0x0066 }
        r3 = "onStart: ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x0066 }
        r0 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x0066 }
        r0 = r2.append(r0);	 Catch:{ all -> 0x0066 }
        r0 = r0.toString();	 Catch:{ all -> 0x0066 }
        android.util.Log.e(r1, r0);	 Catch:{ all -> 0x0066 }
        r5.wasRestarted = r4;
        goto L_0x0039;
    L_0x0066:
        r0 = move-exception;
        r5.wasRestarted = r4;
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xboxtcui.XboxTcuiWindow.onStart():void");
    }

    public void onStop() {
        DialogManager.getInstance().setEnabled(false);
        try {
            NavigationManager.getInstance().PopAllScreens();
        } catch (Throwable e) {
            Log.e(TAG, "onStop: " + Log.getStackTraceString(e));
        }
    }

    public void removeContentViewXLE(ScreenLayout screenLayout) {
        int indexOf = this.screens.indexOf(screenLayout);
        if (indexOf >= 0) {
            while (this.screens.size() > indexOf) {
                removeView((View) this.screens.pop());
            }
        }
    }

    public void setAnimationBlocking(boolean z) {
        if (this.animationBlocking != z) {
            this.animationBlocking = z;
            if (this.animationBlocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.Navigation, NAVIGATION_BLOCK_TIMEOUT_MS);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.Navigation);
            }
        }
    }
}
