package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.lang.ref.WeakReference;

public abstract class ActivityBase extends ScreenLayout {
    private boolean showRightPane;
    private boolean showUtilityBar;
    protected ViewModelBase viewModel;

    public ActivityBase() {
        this(0);
    }

    public ActivityBase(int i) {
        super(XboxTcuiSdk.getApplicationContext(), i);
        this.showUtilityBar = true;
        this.showRightPane = true;
    }

    public ActivityBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.showUtilityBar = true;
        this.showRightPane = true;
    }

    private XLERootView getXLERootView() {
        return getChildAt(0) instanceof XLERootView ? (XLERootView) getChildAt(0) : null;
    }

    public void adjustBottomMargin(int i) {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(i);
        }
    }

    protected int computeBottomMargin() {
        return 0;
    }

    protected boolean delayAppbarAnimation() {
        return false;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() != 8 || getXLERootView() == null || getXLERootView().getContentDescription() == null) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        accessibilityEvent.getText().clear();
        accessibilityEvent.getText().add(getXLERootView().getContentDescription());
        return true;
    }

    public void forceRefresh() {
        if (this.viewModel != null) {
            this.viewModel.forceRefresh();
        }
    }

    public void forceUpdateViewImmediately() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    protected abstract String getActivityName();

    public XLEAnimationPackage getAnimateIn(boolean z) {
        View childAt = getChildAt(0);
        if (childAt != null) {
            MAASAnimation animation = MAAS.getInstance().getAnimation("Screen");
            if (animation != null) {
                XLEAnimation compile = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAASAnimationType.ANIMATE_IN, z, childAt);
                if (compile != null) {
                    XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
                    xLEAnimationPackage.add(compile);
                    return xLEAnimationPackage;
                }
            }
        }
        return null;
    }

    public XLEAnimationPackage getAnimateOut(boolean z) {
        View childAt = getChildAt(0);
        if (childAt != null) {
            MAASAnimation animation = MAAS.getInstance().getAnimation("Screen");
            if (animation != null) {
                XLEAnimation compile = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAASAnimationType.ANIMATE_OUT, z, childAt);
                if (compile != null) {
                    XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
                    xLEAnimationPackage.add(compile);
                    return xLEAnimationPackage;
                }
            }
        }
        return null;
    }

    public String getName() {
        return getActivityName();
    }

    public String getRelativeId() {
        return null;
    }

    public boolean getShouldShowAppbar() {
        return false;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.viewModel != null) {
            this.viewModel.onActivityResult(i, i2, intent);
        }
    }

    public void onAnimateInCompleted() {
        if (this.viewModel != null) {
            final WeakReference weakReference = new WeakReference(this.viewModel);
            BackgroundThreadWaitor.getInstance().postRunnableAfterReady(new Runnable() {
                public void run() {
                    ViewModelBase viewModelBase = (ViewModelBase) weakReference.get();
                    if (viewModelBase != null) {
                        viewModelBase.forceUpdateViewImmediately();
                    }
                }
            });
        }
        if (this.viewModel != null) {
            this.viewModel.onAnimateInCompleted();
        }
    }

    public void onAnimateInStarted() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        if (this.viewModel != null) {
            this.viewModel.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        super.onApplicationResume();
        if (this.viewModel != null) {
            this.viewModel.onApplicationResume();
        }
    }

    public boolean onBackButtonPressed() {
        return this.viewModel != null ? this.viewModel.onBackButtonPressed() : false;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.viewModel != null) {
            this.viewModel.onConfigurationChanged(configuration);
        }
    }

    public abstract void onCreateContentView();

    public void onDestroy() {
        if (this.viewModel != null) {
            this.viewModel.onDestroy();
        }
        this.viewModel = null;
        super.onDestroy();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearDisappearingChildren();
    }

    public void onPause() {
        super.onPause();
        if (this.viewModel != null) {
            this.viewModel.onPause();
        }
    }

    public void onRehydrate() {
        super.onRehydrate();
        if (this.viewModel != null) {
            this.viewModel.onRehydrate();
        }
    }

    public void onRehydrateOverride() {
        onCreateContentView();
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (this.viewModel != null) {
            this.viewModel.onRestoreInstanceState(bundle);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.viewModel != null) {
            this.viewModel.onResume();
        }
    }

    public void onSetActive() {
        super.onSetActive();
        if (this.viewModel != null) {
            this.viewModel.onSetActive();
        }
    }

    public void onSetInactive() {
        super.onSetInactive();
        if (this.viewModel != null) {
            this.viewModel.onSetInactive();
        }
    }

    public void onStart() {
        if (!getIsStarted()) {
            super.onStart();
            if (this.viewModel != null) {
                this.viewModel.onStart();
            }
            if (this.viewModel != null) {
                this.viewModel.load();
            }
        }
        if (!delayAppbarAnimation()) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    public void onStop() {
        if (getIsStarted()) {
            super.onStop();
            if (this.viewModel != null) {
                this.viewModel.onSetInactive();
            }
            if (this.viewModel != null) {
                this.viewModel.onStop();
            }
        }
    }

    public void onTombstone() {
        if (this.viewModel != null) {
            this.viewModel.onTombstone();
        }
        super.onTombstone();
    }

    public void removeBottomMargin() {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(0);
        }
    }

    public void resetBottomMargin() {
        if (getXLERootView() != null) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    public void setHeaderName(String str) {
    }

    public void setScreenState(int i) {
        if (this.viewModel != null) {
            this.viewModel.setScreenState(i);
        }
    }
}
