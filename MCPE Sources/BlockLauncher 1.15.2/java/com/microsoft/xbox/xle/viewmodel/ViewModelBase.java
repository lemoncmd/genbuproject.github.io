package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

public abstract class ViewModelBase implements XLEObserver<UpdateData> {
    protected static int LAUNCH_TIME_OUT = 5000;
    public static final String TAG_PAGE_LOADING_TIME = "performance_measure_page_loadingtime";
    protected int LifetimeInMinutes;
    protected AdapterBase adapter;
    protected boolean isActive;
    protected boolean isForeground;
    protected boolean isLaunching;
    protected Runnable launchTimeoutHandler;
    protected int listIndex;
    private NavigationData nextScreenData;
    protected int offset;
    private boolean onlyProcessExceptionsAndShowToastsWhenActive;
    private ViewModelBase parent;
    private final ScreenLayout screen;
    private boolean shouldHideScreen;
    private boolean showNoNetworkPopup;
    private HashMap<UpdateType, XLEException> updateExceptions;
    private EnumSet<UpdateType> updateTypesToCheck;
    private boolean updating;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType = new int[NavigationType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType[NavigationType.Push.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType[NavigationType.PopReplace.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType[NavigationType.PopAll.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class NavigationData {
        private NavigationType navigationType;
        private Class<? extends ScreenLayout> screenClass;

        protected NavigationData(Class<? extends ScreenLayout> cls, NavigationType navigationType) {
            this.screenClass = cls;
            this.navigationType = navigationType;
        }

        protected NavigationType getNavigationType() {
            return this.navigationType;
        }

        protected Class<? extends ScreenLayout> getScreenClass() {
            return this.screenClass;
        }
    }

    private enum NavigationType {
        Push,
        PopReplace,
        PopAll
    }

    public ViewModelBase() {
        this(null, true, false);
    }

    public ViewModelBase(ScreenLayout screenLayout) {
        this(screenLayout, true, false);
    }

    public ViewModelBase(ScreenLayout screenLayout, boolean z, boolean z2) {
        this.LifetimeInMinutes = 60;
        this.updateExceptions = new HashMap();
        this.showNoNetworkPopup = true;
        this.onlyProcessExceptionsAndShowToastsWhenActive = false;
        this.nextScreenData = null;
        this.updating = false;
        this.isLaunching = false;
        this.screen = screenLayout;
        this.showNoNetworkPopup = z;
        this.onlyProcessExceptionsAndShowToastsWhenActive = z2;
    }

    public ViewModelBase(boolean z, boolean z2) {
        this(null, z, z2);
    }

    private boolean shouldProcessErrors() {
        return this.onlyProcessExceptionsAndShowToastsWhenActive ? this.isActive : true;
    }

    protected void NavigateTo(Class<? extends ScreenLayout> cls) {
        NavigateTo((Class) cls, null);
    }

    protected void NavigateTo(Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) {
        NavigateTo(cls, true, activityParameters);
    }

    protected void NavigateTo(Class<? extends ScreenLayout> cls, boolean z) {
        NavigateTo(cls, z, null);
    }

    protected void NavigateTo(Class<? extends ScreenLayout> cls, boolean z, ActivityParameters activityParameters) {
        cancelLaunchTimeout();
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        if (this.updating) {
            this.nextScreenData = new NavigationData(cls, z ? NavigationType.Push : NavigationType.PopReplace);
            return;
        }
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        NavigationManager.getInstance().NavigateTo(cls, z, activityParameters);
    }

    public void TEST_induceGoBack() {
    }

    protected void adapterUpdateView() {
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    public void cancelLaunch() {
        this.isLaunching = false;
    }

    protected void cancelLaunchTimeout() {
        this.isLaunching = false;
        if (this.launchTimeoutHandler != null) {
            ThreadManager.Handler.removeCallbacks(this.launchTimeoutHandler);
        }
    }

    protected boolean checkErrorCode(UpdateType updateType, long j) {
        return (this.updateExceptions.containsKey(updateType) && ((XLEException) this.updateExceptions.get(updateType)).getErrorCode() == j) ? !((XLEException) this.updateExceptions.get(updateType)).getIsHandled() : false;
    }

    public View findViewById(int i) {
        return this.screen != null ? this.screen.xleFindViewId(i) : null;
    }

    public void forceRefresh() {
        load(true);
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    public void forceUpdateViewImmediately() {
        if (this.adapter != null) {
            this.adapter.forceUpdateViewImmediately();
        }
    }

    public AdapterBase getAdapter() {
        return this.adapter;
    }

    public int getAndResetListOffset() {
        int i = this.offset;
        this.offset = 0;
        return i;
    }

    public int getAndResetListPosition() {
        int i = this.listIndex;
        this.listIndex = 0;
        return i;
    }

    public XLEAnimationPackage getAnimateIn(boolean z) {
        ArrayList animateIn = this.adapter.getAnimateIn(z);
        if (animateIn == null || animateIn.size() <= 0) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        Iterator it = animateIn.iterator();
        while (it.hasNext()) {
            xLEAnimationPackage.add((XLEAnimation) it.next());
        }
        return xLEAnimationPackage;
    }

    public XLEAnimationPackage getAnimateOut(boolean z) {
        ArrayList animateOut = this.adapter.getAnimateOut(z);
        if (animateOut == null || animateOut.size() <= 0) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        Iterator it = animateOut.iterator();
        while (it.hasNext()) {
            xLEAnimationPackage.add((XLEAnimation) it.next());
        }
        return xLEAnimationPackage;
    }

    public String getBlockingStatusText() {
        return null;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    protected ViewModelBase getParent() {
        return this.parent;
    }

    public ScreenLayout getScreen() {
        return this.screen;
    }

    public boolean getShouldHideScreen() {
        return this.shouldHideScreen;
    }

    public boolean getShowNoNetworkPopup() {
        return this.showNoNetworkPopup;
    }

    public boolean isBlockingBusy() {
        return false;
    }

    public abstract boolean isBusy();

    public void leaveViewModel(Runnable runnable) {
        runnable.run();
    }

    public void load() {
        load(XLEGlobalData.getInstance().CheckDrainShouldRefresh(getClass()));
    }

    public abstract void load(boolean z);

    protected void logOut(boolean z) {
    }

    public void onActivityResult(int i, int i2, Intent intent) {
    }

    public void onAnimateInCompleted() {
        if (this.adapter != null) {
            this.adapter.onAnimateInCompleted();
        }
    }

    public void onApplicationPause() {
        if (this.adapter != null) {
            this.adapter.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (this.adapter != null) {
            this.adapter.onApplicationResume();
        }
    }

    public boolean onBackButtonPressed() {
        return false;
    }

    protected void onChildViewModelChanged(ViewModelBase viewModelBase) {
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onDestroy() {
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    public void onPause() {
        cancelLaunchTimeout();
        if (this.adapter != null) {
            this.adapter.onPause();
        }
    }

    public abstract void onRehydrate();

    public void onRestoreInstanceState(Bundle bundle) {
    }

    public void onResume() {
        if (this.adapter != null) {
            this.adapter.onResume();
            this.adapter.updateView();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    public void onSetActive() {
        this.isActive = true;
        if (this.adapter != null) {
            this.adapter.onSetActive();
        }
    }

    public void onSetInactive() {
        DialogManager.getInstance().dismissToast();
        this.isActive = false;
        if (this.adapter != null) {
            this.adapter.onSetInactive();
        }
    }

    public void onStart() {
        this.isForeground = true;
        onStartOverride();
        if (this.adapter != null) {
            this.adapter.onStart();
        }
    }

    protected abstract void onStartOverride();

    public void onStop() {
        this.isForeground = false;
        if (this.adapter != null) {
            this.adapter.onStop();
        }
        DialogManager.getInstance().dismissBlocking();
        if (shouldDismissTopNoFatalAlert()) {
            DialogManager.getInstance().dismissTopNonFatalAlert();
        }
        DialogManager.getInstance().dismissToast();
        onStopOverride();
    }

    protected abstract void onStopOverride();

    public void onTombstone() {
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    protected void onUpdateFinished() {
        this.updateTypesToCheck = null;
        this.updateExceptions.clear();
    }

    public void setAsPivotPane() {
        this.showNoNetworkPopup = true;
        this.onlyProcessExceptionsAndShowToastsWhenActive = true;
    }

    public void setListPosition(int i, int i2) {
        this.listIndex = i;
        this.offset = i2;
    }

    protected void setParent(ViewModelBase viewModelBase) {
        this.parent = viewModelBase;
    }

    public void setScreenState(int i) {
        if (this.adapter != null) {
            this.adapter.setScreenState(i);
        }
    }

    public void setShouldHideScreen(boolean z) {
        this.shouldHideScreen = z;
    }

    protected void setUpdateTypesToCheck(EnumSet<UpdateType> enumSet) {
        this.updateTypesToCheck = enumSet;
        this.updateExceptions.clear();
    }

    protected boolean shouldDismissTopNoFatalAlert() {
        return true;
    }

    public boolean shouldRefreshAsPivotHeader() {
        return false;
    }

    protected void showError(int i) {
        DialogManager.getInstance().showToast(i);
    }

    protected void showMustActDialog(String str, String str2, String str3, Runnable runnable, boolean z) {
    }

    protected void showOkCancelDialog(String str, String str2, Runnable runnable, String str3, Runnable runnable2) {
        showOkCancelDialog(null, str, str2, runnable, str3, runnable2);
    }

    protected void showOkCancelDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        if (shouldProcessErrors()) {
            XLEUtil.showOkCancelDialog(str, str2, str3, runnable, str4, runnable2);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void update(com.microsoft.xbox.toolkit.AsyncResult<com.microsoft.xbox.service.model.UpdateData> r9) {
        /*
        r8 = this;
        r3 = 0;
        r2 = 0;
        r1 = 1;
        r8.updating = r1;
        r0 = r8.nextScreenData;
        if (r0 != 0) goto L_0x005f;
    L_0x0009:
        r0 = r1;
    L_0x000a:
        com.microsoft.xbox.toolkit.XLEAssert.assertTrue(r0);
        r8.nextScreenData = r3;
        r0 = r9.getException();
        if (r0 == 0) goto L_0x0034;
    L_0x0015:
        r0 = r9.getException();
        r4 = r0.getErrorCode();
        r0 = r9.getException();
        r0 = r0.getIsHandled();
        if (r0 != 0) goto L_0x0034;
    L_0x0027:
        r6 = 1005; // 0x3ed float:1.408E-42 double:4.965E-321;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x0034;
    L_0x002d:
        r0 = r9.getException();
        r0.setIsHandled(r1);
    L_0x0034:
        r0 = r8.nextScreenData;
        if (r0 != 0) goto L_0x0045;
    L_0x0038:
        r0 = r8.adapter;
        if (r0 != 0) goto L_0x0042;
    L_0x003c:
        r0 = r8.updateWithoutAdapter();
        if (r0 == 0) goto L_0x0045;
    L_0x0042:
        r8.updateOverride(r9);
    L_0x0045:
        r8.updating = r2;
        r0 = r8.nextScreenData;
        if (r0 == 0) goto L_0x008f;
    L_0x004b:
        r0 = com.microsoft.xbox.xle.viewmodel.ViewModelBase.AnonymousClass1.$SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType;	 Catch:{ XLEException -> 0x0070 }
        r1 = r8.nextScreenData;	 Catch:{ XLEException -> 0x0070 }
        r1 = r1.getNavigationType();	 Catch:{ XLEException -> 0x0070 }
        r1 = r1.ordinal();	 Catch:{ XLEException -> 0x0070 }
        r0 = r0[r1];	 Catch:{ XLEException -> 0x0070 }
        switch(r0) {
            case 1: goto L_0x0061;
            case 2: goto L_0x0072;
            case 3: goto L_0x0081;
            default: goto L_0x005c;
        };
    L_0x005c:
        r8.nextScreenData = r3;
        return;
    L_0x005f:
        r0 = r2;
        goto L_0x000a;
    L_0x0061:
        r0 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0070 }
        r1 = r8.nextScreenData;	 Catch:{ XLEException -> 0x0070 }
        r1 = r1.getScreenClass();	 Catch:{ XLEException -> 0x0070 }
        r2 = 1;
        r0.NavigateTo(r1, r2);	 Catch:{ XLEException -> 0x0070 }
        goto L_0x005c;
    L_0x0070:
        r0 = move-exception;
        goto L_0x005c;
    L_0x0072:
        r0 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0070 }
        r1 = r8.nextScreenData;	 Catch:{ XLEException -> 0x0070 }
        r1 = r1.getScreenClass();	 Catch:{ XLEException -> 0x0070 }
        r2 = 0;
        r0.NavigateTo(r1, r2);	 Catch:{ XLEException -> 0x0070 }
        goto L_0x005c;
    L_0x0081:
        r0 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x0070 }
        r1 = r8.nextScreenData;	 Catch:{ XLEException -> 0x0070 }
        r1 = r1.getScreenClass();	 Catch:{ XLEException -> 0x0070 }
        r0.GotoScreenWithPop(r1);	 Catch:{ XLEException -> 0x0070 }
        goto L_0x005c;
    L_0x008f:
        r0 = r8.shouldProcessErrors();
        if (r0 == 0) goto L_0x005c;
    L_0x0095:
        r0 = r9.getException();
        if (r0 == 0) goto L_0x00ce;
    L_0x009b:
        r0 = r9.getException();
        r0 = r0.getIsHandled();
        if (r0 != 0) goto L_0x00ce;
    L_0x00a5:
        r0 = r8.updateTypesToCheck;
        if (r0 == 0) goto L_0x00ce;
    L_0x00a9:
        r1 = r8.updateTypesToCheck;
        r0 = r9.getResult();
        r0 = (com.microsoft.xbox.service.model.UpdateData) r0;
        r0 = r0.getUpdateType();
        r0 = r1.contains(r0);
        if (r0 == 0) goto L_0x00ce;
    L_0x00bb:
        r1 = r8.updateExceptions;
        r0 = r9.getResult();
        r0 = (com.microsoft.xbox.service.model.UpdateData) r0;
        r0 = r0.getUpdateType();
        r2 = r9.getException();
        r1.put(r0, r2);
    L_0x00ce:
        r0 = r9.getResult();
        r0 = (com.microsoft.xbox.service.model.UpdateData) r0;
        r0 = r0.getIsFinal();
        if (r0 == 0) goto L_0x005c;
    L_0x00da:
        r0 = r8.updateTypesToCheck;
        if (r0 == 0) goto L_0x00ed;
    L_0x00de:
        r1 = r8.updateTypesToCheck;
        r0 = r9.getResult();
        r0 = (com.microsoft.xbox.service.model.UpdateData) r0;
        r0 = r0.getUpdateType();
        r1.remove(r0);
    L_0x00ed:
        r0 = r8.updateTypesToCheck;
        if (r0 == 0) goto L_0x00f9;
    L_0x00f1:
        r0 = r8.updateTypesToCheck;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x005c;
    L_0x00f9:
        r8.onUpdateFinished();
        r8.updateTypesToCheck = r3;
        goto L_0x005c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.viewmodel.ViewModelBase.update(com.microsoft.xbox.toolkit.AsyncResult):void");
    }

    protected void updateAdapter() {
        updateAdapter(true);
    }

    protected void updateAdapter(boolean z) {
        if (this.adapter != null) {
            this.adapter.updateView();
        }
        if (this.parent != null && z) {
            this.parent.onChildViewModelChanged(this);
        }
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
    }

    protected boolean updateTypesToCheckHadAnyErrors() {
        return !this.updateExceptions.isEmpty();
    }

    protected boolean updateTypesToCheckIsEmpty() {
        return this.updateTypesToCheck == null || this.updateTypesToCheck.isEmpty();
    }

    protected boolean updateWithoutAdapter() {
        return false;
    }
}
