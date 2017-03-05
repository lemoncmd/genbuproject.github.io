package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.XLEAllocationTracker;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.module.ScreenModuleLayout;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class AdapterBase {
    public static String ALLOCATION_TAG = "ADAPTERBASE";
    private static HashMap<String, Integer> adapterCounter = new HashMap();
    protected boolean isActive;
    private boolean isStarted;
    private ArrayList<ScreenModuleLayout> screenModules;
    private final ViewModelBase viewModel;

    public AdapterBase() {
        this(null);
    }

    public AdapterBase(ViewModelBase viewModelBase) {
        this.isActive = false;
        this.isStarted = false;
        this.screenModules = new ArrayList();
        this.viewModel = viewModelBase;
        XLEAllocationTracker.getInstance().debugIncrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void finalize() {
        XLEAllocationTracker.getInstance().debugDecrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    protected void findAndInitializeModuleById(int i, ViewModelBase viewModelBase) {
        View findViewById = findViewById(i);
        if (findViewById != null && (findViewById instanceof ScreenModuleLayout)) {
            ScreenModuleLayout screenModuleLayout = (ScreenModuleLayout) findViewById(i);
            screenModuleLayout.setViewModel(viewModelBase);
            this.screenModules.add(screenModuleLayout);
        }
    }

    public View findViewById(int i) {
        View view = null;
        if (this.viewModel != null) {
            view = this.viewModel.findViewById(i);
        }
        return view != null ? view : XboxTcuiSdk.getActivity().findViewById(i);
    }

    public void forceUpdateViewImmediately() {
        XLEAssert.assertIsUIThread();
        updateViewOverride();
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).updateView();
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean z) {
        return null;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean z) {
        return null;
    }

    protected boolean getIsStarted() {
        return this.isStarted;
    }

    public void invalidateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            invalidateViewOverride();
            Iterator it = this.screenModules.iterator();
            while (it.hasNext()) {
                ((ScreenModuleLayout) it.next()).invalidateView();
            }
        }
    }

    protected void invalidateViewOverride() {
    }

    public void onAnimateInCompleted() {
    }

    protected void onAppBarButtonsAdded() {
    }

    @Deprecated
    protected void onAppBarUpdated() {
    }

    public void onApplicationPause() {
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onApplicationResume();
        }
    }

    public void onDestroy() {
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onDestroy();
        }
        this.screenModules.clear();
    }

    public void onPause() {
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onPause();
        }
    }

    public void onResume() {
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onResume();
        }
    }

    public void onSetActive() {
        this.isActive = true;
        if (XboxTcuiSdk.getActivity() != null && this.isStarted) {
            updateView();
        }
    }

    public void onSetInactive() {
        this.isActive = false;
    }

    public void onStart() {
        this.isStarted = true;
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onStart();
        }
    }

    public void onStop() {
        this.isStarted = false;
        Iterator it = this.screenModules.iterator();
        while (it.hasNext()) {
            ((ScreenModuleLayout) it.next()).onStop();
        }
    }

    protected void setBlocking(boolean z, String str) {
        DialogManager.getInstance().setBlocking(z, str);
    }

    protected void setCancelableBlocking(boolean z, String str, Runnable runnable) {
        DialogManager.getInstance().setCancelableBlocking(z, str, runnable);
    }

    public void setScreenState(int i) {
    }

    protected void showKeyboard(View view, int i) {
        XLEUtil.showKeyboard(view, i);
    }

    public void updateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            updateViewOverride();
            Iterator it = this.screenModules.iterator();
            while (it.hasNext()) {
                ((ScreenModuleLayout) it.next()).updateView();
            }
        }
    }

    protected abstract void updateViewOverride();
}
