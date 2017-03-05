package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ScreenLayout extends FrameLayout {
    private static ArrayList<View> badList = new ArrayList();
    private boolean allEventsEnabled;
    private boolean drawerEnabled;
    private boolean isActive;
    private boolean isEditable;
    private boolean isReady;
    private boolean isStarted;
    protected boolean isTombstoned;
    private Runnable onLayoutChangedListener;
    private int orientation;
    private int screenPercent;

    public ScreenLayout() {
        this(XboxTcuiSdk.getApplicationContext());
    }

    public ScreenLayout(Context context) {
        this(context, 0);
    }

    public ScreenLayout(Context context, int i) {
        super(context);
        this.onLayoutChangedListener = null;
        this.isEditable = false;
        this.screenPercent = 100;
        this.drawerEnabled = true;
        this.allEventsEnabled = true;
        Initialize(i);
    }

    public ScreenLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.onLayoutChangedListener = null;
        this.isEditable = false;
        this.screenPercent = 100;
        this.drawerEnabled = true;
        this.allEventsEnabled = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("ScreenLayout"));
        if (obtainStyledAttributes.hasValue(XLERValueHelper.getStyleableRValue("ScreenLayout_screenDIPs"))) {
            this.screenPercent = (int) (100.0f * (((float) obtainStyledAttributes.getDimensionPixelSize(XLERValueHelper.getStyleableRValue("ScreenLayout_screenDIPs"), SystemUtil.getScreenWidth())) / ((float) SystemUtil.getScreenWidth())));
        } else {
            this.screenPercent = obtainStyledAttributes.getInt(XLERValueHelper.getStyleableRValue("ScreenLayout_screenPercent"), -2);
        }
        obtainStyledAttributes.recycle();
        Initialize(7);
    }

    public static void addViewThatCausesAndroidLeaks(View view) {
        badList.add(view);
    }

    private void removeAllViewsAndWorkaroundAndroidLeaks() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        removeAllViews();
        Iterator it = badList.iterator();
        while (it.hasNext()) {
            removeViewAndWorkaroundAndroidLeaks((View) it.next());
        }
        badList.clear();
    }

    public static void removeViewAndWorkaroundAndroidLeaks(View view) {
        boolean z = false;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeAllViews();
                XLEAssert.assertTrue(view.getParent() == null);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                viewGroup.removeAllViews();
                viewGroup.destroyDrawingCache();
                if (viewGroup.getChildCount() == 0) {
                    z = true;
                }
                XLEAssert.assertTrue(z);
            }
        }
    }

    protected void Initialize(int i) {
        this.isReady = false;
        this.isActive = false;
        this.isStarted = false;
        this.orientation = i;
    }

    public void adjustBottomMargin(int i) {
    }

    public abstract void forceRefresh();

    public abstract void forceUpdateViewImmediately();

    public XLEAnimationPackage getAnimateIn(boolean z) {
        return null;
    }

    public XLEAnimationPackage getAnimateOut(boolean z) {
        return null;
    }

    public boolean getCanAutoLaunch() {
        return !this.isEditable;
    }

    public String getContent() {
        return null;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public boolean getIsEditable() {
        return this.isEditable;
    }

    public boolean getIsReady() {
        return this.isReady;
    }

    public boolean getIsStarted() {
        return this.isStarted;
    }

    public boolean getIsTombstoned() {
        return this.isTombstoned;
    }

    public String getLocalClassName() {
        return getClass().getName();
    }

    public abstract String getName();

    public String getRelativeId() {
        return null;
    }

    public int getScreenPercent() {
        return this.screenPercent;
    }

    public boolean getShouldShowAppbar() {
        return !this.isEditable;
    }

    public Boolean getTrackPage() {
        return Boolean.valueOf(true);
    }

    public boolean isAllEventsEnabled() {
        return this.allEventsEnabled;
    }

    public boolean isAnimateOnPop() {
        return true;
    }

    public boolean isAnimateOnPush() {
        return true;
    }

    public boolean isDrawerEnabled() {
        return this.drawerEnabled;
    }

    public boolean isKeepPreviousScreen() {
        return false;
    }

    public void leaveScreen(Runnable runnable) {
        runnable.run();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
    }

    public abstract void onAnimateInCompleted();

    public abstract void onAnimateInStarted();

    public void onApplicationPause() {
    }

    public void onApplicationResume() {
    }

    public abstract boolean onBackButtonPressed();

    public boolean onContextItemSelected(MenuItem menuItem) {
        return false;
    }

    public void onCreate() {
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
    }

    public void onDestroy() {
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        return this.allEventsEnabled ? super.onHoverEvent(motionEvent) : true;
    }

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        return this.allEventsEnabled ? super.onInterceptHoverEvent(motionEvent) : true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.allEventsEnabled ? super.onInterceptTouchEvent(motionEvent) : true;
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.onLayoutChangedListener != null) {
            this.onLayoutChangedListener.run();
        }
    }

    public void onPause() {
        this.isReady = false;
    }

    public void onRehydrate() {
        this.isTombstoned = false;
        onRehydrateOverride();
    }

    public abstract void onRehydrateOverride();

    public void onRestart() {
    }

    public void onRestoreInstanceState(Bundle bundle) {
    }

    public void onResume() {
        this.isReady = true;
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    public void onSetActive() {
        this.isActive = true;
    }

    public void onSetInactive() {
        this.isActive = false;
    }

    public void onStart() {
        this.isStarted = true;
    }

    public void onStop() {
        this.isStarted = false;
    }

    public void onTombstone() {
        this.isTombstoned = true;
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.allEventsEnabled ? super.onTouchEvent(motionEvent) : true;
    }

    public void removeBottomMargin() {
    }

    public void resetBottomMargin() {
    }

    public void setAllEventsEnabled(boolean z) {
        this.allEventsEnabled = z;
    }

    public void setContentView(int i) {
        LayoutInflater.from(getContext()).inflate(i, this, true);
    }

    public void setDrawerEnabled(boolean z) {
        this.drawerEnabled = z;
    }

    public void setIsEditable(boolean z) {
        this.isEditable = z;
    }

    public void setOnLayoutChangedListener(Runnable runnable) {
        this.onLayoutChangedListener = runnable;
    }

    public ScreenLayout setScreenPercent(int i) {
        this.screenPercent = i;
        return this;
    }

    public void setScreenState(int i) {
    }

    public View xleFindViewId(int i) {
        return findViewById(i);
    }
}
