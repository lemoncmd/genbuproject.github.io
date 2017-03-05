package com.microsoft.xbox.toolkit.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Iterator;
import java.util.Stack;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class NavigationManager implements OnKeyListener {
    private static final String TAG = "NavigationManager";
    private NavigationManagerAnimationState animationState;
    final Runnable callAfterAnimation;
    private boolean cannotNavigateTripwire;
    private XLEAnimationPackage currentAnimation;
    private boolean goingBack;
    private NavigationCallbacks navigationCallbacks;
    private OnNavigatedListener navigationListener;
    private final Stack<ActivityParameters> navigationParameters;
    private final Stack<ScreenLayout> navigationStack;
    private boolean transitionAnimate;
    private Runnable transitionLambda;

    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState = new int[NavigationManagerAnimationState.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState[NavigationManagerAnimationState.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState[NavigationManagerAnimationState.ANIMATING_IN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState[NavigationManagerAnimationState.ANIMATING_OUT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public interface NavigationCallbacks {
        void addContentViewXLE(ScreenLayout screenLayout);

        void onBeforeNavigatingIn();

        void removeContentViewXLE(ScreenLayout screenLayout);

        void setAnimationBlocking(boolean z);
    }

    private enum NavigationManagerAnimationState {
        NONE,
        ANIMATING_IN,
        ANIMATING_OUT,
        COUNT
    }

    private static class NavigationManagerHolder {
        public static final NavigationManager instance = new NavigationManager();

        private NavigationManagerHolder() {
        }
    }

    public interface OnNavigatedListener {
        void onPageNavigated(ScreenLayout screenLayout, ScreenLayout screenLayout2);

        void onPageRestarted(ScreenLayout screenLayout);
    }

    private class RestartRunner implements Runnable {
        private final ActivityParameters params;

        public RestartRunner(ActivityParameters activityParameters) {
            this.params = activityParameters;
        }

        public void run() {
            boolean z = true;
            NavigationManager.this.cannotNavigateTripwire = true;
            ScreenLayout currentActivity = NavigationManager.this.getCurrentActivity();
            XLEAssert.assertNotNull(currentActivity);
            NavigationManager.this.getCurrentActivity().onSetInactive();
            NavigationManager.this.getCurrentActivity().onPause();
            NavigationManager.this.getCurrentActivity().onStop();
            if (NavigationManager.this.navigationParameters.isEmpty()) {
                z = false;
            }
            XLEAssert.assertTrue("navigationParameters cannot be empty!", z);
            NavigationManager.this.navigationParameters.pop();
            NavigationManager.this.navigationParameters.push(this.params);
            NavigationManager.this.getCurrentActivity().onStart();
            NavigationManager.this.getCurrentActivity().onResume();
            NavigationManager.this.getCurrentActivity().onSetActive();
            NavigationManager.this.getCurrentActivity().onAnimateInStarted();
            XboxTcuiSdk.getActivity().invalidateOptionsMenu();
            if (NavigationManager.this.navigationListener != null) {
                NavigationManager.this.navigationListener.onPageRestarted(currentActivity);
            }
            NavigationManager.this.cannotNavigateTripwire = false;
        }
    }

    private NavigationManager() {
        boolean z = false;
        this.navigationParameters = new Stack();
        this.navigationStack = new Stack();
        this.currentAnimation = null;
        this.animationState = NavigationManagerAnimationState.NONE;
        this.transitionLambda = null;
        this.goingBack = false;
        this.transitionAnimate = true;
        this.cannotNavigateTripwire = false;
        this.callAfterAnimation = new Runnable() {
            public void run() {
                NavigationManager.this.OnAnimationEnd();
            }
        };
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        }
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", z);
    }

    private void OnAnimationEnd() {
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState[this.animationState.ordinal()]) {
            case NativeRegExp.PREFIX /*2*/:
                if (this.navigationCallbacks != null) {
                    this.navigationCallbacks.setAnimationBlocking(false);
                }
                this.animationState = NavigationManagerAnimationState.NONE;
                if (getCurrentActivity() != null) {
                    getCurrentActivity().onAnimateInCompleted();
                    return;
                }
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.transitionLambda.run();
                XLEAnimationPackage xLEAnimationPackage = null;
                if (getCurrentActivity() != null) {
                    xLEAnimationPackage = getCurrentActivity().getAnimateIn(this.goingBack);
                }
                if (this.navigationCallbacks != null) {
                    this.navigationCallbacks.onBeforeNavigatingIn();
                }
                startAnimation(xLEAnimationPackage, NavigationManagerAnimationState.ANIMATING_IN);
                return;
            default:
                return;
        }
    }

    private void ReplaceOnAnimationEnd(boolean z, Runnable runnable, boolean z2) {
        boolean z3 = this.animationState == NavigationManagerAnimationState.ANIMATING_OUT || this.animationState == NavigationManagerAnimationState.ANIMATING_IN;
        XLEAssert.assertTrue(z3);
        this.animationState = NavigationManagerAnimationState.ANIMATING_OUT;
        this.transitionLambda = runnable;
        this.transitionAnimate = z2;
        this.goingBack = z;
    }

    private int Size() {
        return this.navigationStack.size();
    }

    private void Transition(boolean z, Runnable runnable, boolean z2) {
        this.transitionLambda = runnable;
        this.transitionAnimate = z2;
        this.goingBack = z;
        this.currentAnimation = getCurrentActivity() == null ? null : getCurrentActivity().getAnimateOut(z);
        startAnimation(this.currentAnimation, NavigationManagerAnimationState.ANIMATING_OUT);
    }

    public static NavigationManager getInstance() {
        return NavigationManagerHolder.instance;
    }

    private void startAnimation(XLEAnimationPackage xLEAnimationPackage, NavigationManagerAnimationState navigationManagerAnimationState) {
        this.animationState = navigationManagerAnimationState;
        this.currentAnimation = xLEAnimationPackage;
        if (this.navigationCallbacks != null) {
            this.navigationCallbacks.setAnimationBlocking(true);
        }
        if (!this.transitionAnimate || xLEAnimationPackage == null) {
            this.callAfterAnimation.run();
            return;
        }
        xLEAnimationPackage.setOnAnimationEndRunnable(this.callAfterAnimation);
        xLEAnimationPackage.startAnimation();
    }

    public int CountPopsToScreen(Class<? extends ScreenLayout> cls) {
        int size = this.navigationStack.size() - 1;
        for (int i = size; i >= 0; i--) {
            if (((ScreenLayout) this.navigationStack.get(i)).getClass().equals(cls)) {
                return size - i;
            }
        }
        return -1;
    }

    public void GotoScreenWithPop(ActivityParameters activityParameters, Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout>... clsArr) throws XLEException {
        int size = this.navigationStack.size() - 1;
        int i = size;
        loop0:
        while (i >= 0) {
            Class<? extends ScreenLayout> cls2 = ((ScreenLayout) this.navigationStack.get(i)).getClass();
            for (Class<? extends ScreenLayout> cls3 : clsArr) {
                if (cls3 == cls2) {
                    break loop0;
                }
            }
            i--;
        }
        Class<? extends ScreenLayout> cls32 = null;
        if (cls32 == null) {
            PopScreensAndReplace(Size(), cls, true, true, false, activityParameters);
        } else if (cls32 != cls) {
            PopScreensAndReplace(size - i, cls, true, true, false, activityParameters);
        } else if (i == size) {
            RestartCurrentScreen(activityParameters, false);
        } else {
            PopScreensAndReplace(size - i, null, true, true, false, activityParameters);
        }
    }

    public void GotoScreenWithPop(Class<? extends ScreenLayout> cls) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(Size(), cls, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> cls) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false, activityParameters);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls, true, false, false, activityParameters);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public boolean IsScreenOnStack(Class<? extends ScreenLayout> cls) {
        Iterator it = this.navigationStack.iterator();
        while (it.hasNext()) {
            if (((ScreenLayout) it.next()).getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public void NavigateTo(Class<? extends ScreenLayout> cls, boolean z) {
        NavigateTo(cls, z, null);
    }

    public void NavigateTo(Class<? extends ScreenLayout> cls, boolean z, ActivityParameters activityParameters) {
        if (z) {
            try {
                PushScreen(cls, activityParameters);
                return;
            } catch (XLEException e) {
                return;
            }
        }
        PopScreensAndReplace(1, cls, activityParameters);
    }

    public boolean OnBackButtonPressed() {
        boolean ShouldBackCloseApp = ShouldBackCloseApp();
        if (!(getCurrentActivity() == null || getCurrentActivity().onBackButtonPressed())) {
            if (ShouldBackCloseApp) {
                try {
                    PopScreensAndReplace(1, null, false, false, false);
                } catch (XLEException e) {
                }
            } else {
                PopScreen();
            }
        }
        return ShouldBackCloseApp;
    }

    public void PopAllScreens() throws XLEException {
        if (Size() > 0) {
            PopScreensAndReplace(Size(), null, false, false, false);
        }
    }

    public void PopScreen() throws XLEException {
        PopScreens(1);
    }

    public void PopScreens(int i) throws XLEException {
        PopScreensAndReplace(i, null);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls) throws XLEException {
        PopScreensAndReplace(i, cls, null);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(i, cls, true, true, false, activityParameters);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, boolean z, boolean z2) throws XLEException {
        PopScreensAndReplace(i, cls, z, true, z2);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, boolean z, boolean z2, boolean z3) throws XLEException {
        PopScreensAndReplace(i, cls, z, z2, z3, null);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, boolean z, boolean z2, boolean z3, ActivityParameters activityParameters) throws XLEException {
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        if (this.cannotNavigateTripwire) {
            throw new UnsupportedOperationException("NavigationManager: attempted to execute a recursive navigation in the OnStop/OnStart method.  This is forbidden.");
        }
        ScreenLayout screenLayout;
        Runnable restartRunner;
        if (cls == null || z3) {
            screenLayout = null;
        } else {
            try {
                ScreenLayout screenLayout2 = (ScreenLayout) cls.getConstructor(new Class[0]).newInstance(new Object[0]);
                z = z && screenLayout2.isAnimateOnPush();
                screenLayout = screenLayout2;
            } catch (Throwable e) {
                throw new XLEException(19, "FIXME: Failed to create a screen of type " + cls.getName(), e);
            }
        }
        if (getCurrentActivity() != null) {
            z = z && getCurrentActivity().isAnimateOnPop();
        }
        final ActivityParameters activityParameters2 = activityParameters == null ? new ActivityParameters() : activityParameters;
        final NavigationCallbacks navigationCallbacks = this.navigationCallbacks;
        XLEAssert.assertNotNull(navigationCallbacks);
        if (z3) {
            restartRunner = new RestartRunner(activityParameters2);
        } else {
            final int i2 = i;
            restartRunner = new Runnable() {
                public void run() {
                    ScreenLayout currentActivity;
                    NavigationManager.this.cannotNavigateTripwire = true;
                    ScreenLayout currentActivity2 = NavigationManager.this.getCurrentActivity();
                    activityParameters2.putFromScreen(currentActivity2);
                    activityParameters2.putSourcePage(NavigationManager.this.getCurrentActivityName());
                    if (NavigationManager.this.getCurrentActivity() != null) {
                        NavigationManager.this.getCurrentActivity().onSetInactive();
                        NavigationManager.this.getCurrentActivity().onPause();
                        NavigationManager.this.getCurrentActivity().onStop();
                    }
                    for (int i = 0; i < i2; i++) {
                        NavigationManager.this.getCurrentActivity().onDestroy();
                        navigationCallbacks.removeContentViewXLE((ScreenLayout) NavigationManager.this.navigationStack.pop());
                        NavigationManager.this.navigationParameters.pop();
                    }
                    TextureManager.Instance().purgeResourceBitmapCache();
                    if (screenLayout != null) {
                        if (!(NavigationManager.this.getCurrentActivity() == null || screenLayout.isKeepPreviousScreen())) {
                            NavigationManager.this.getCurrentActivity().onTombstone();
                        }
                        navigationCallbacks.addContentViewXLE((ScreenLayout) NavigationManager.this.navigationStack.push(screenLayout));
                        NavigationManager.this.navigationParameters.push(activityParameters2);
                        NavigationManager.this.getCurrentActivity().onCreate();
                    } else if (NavigationManager.this.getCurrentActivity() != null) {
                        navigationCallbacks.addContentViewXLE(NavigationManager.this.getCurrentActivity());
                        if (NavigationManager.this.getCurrentActivity().getIsTombstoned()) {
                            NavigationManager.this.getCurrentActivity().onRehydrate();
                        }
                    }
                    if (NavigationManager.this.getCurrentActivity() != null) {
                        NavigationManager.this.getCurrentActivity().onStart();
                        NavigationManager.this.getCurrentActivity().onResume();
                        NavigationManager.this.getCurrentActivity().onSetActive();
                        NavigationManager.this.getCurrentActivity().onAnimateInStarted();
                        XboxTcuiSdk.getActivity().invalidateOptionsMenu();
                        currentActivity = NavigationManager.this.getCurrentActivity();
                    } else {
                        currentActivity = null;
                    }
                    if (NavigationManager.this.navigationListener != null) {
                        NavigationManager.this.navigationListener.onPageNavigated(currentActivity2, currentActivity);
                    }
                    NavigationManager.this.cannotNavigateTripwire = false;
                }
            };
        }
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$toolkit$ui$NavigationManager$NavigationManagerAnimationState[this.animationState.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                Transition(z2, restartRunner, z);
                return;
            default:
                ReplaceOnAnimationEnd(z2, restartRunner, z);
                return;
        }
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout> cls2) throws XLEException {
        PopTillScreenThenPush(cls, cls2, null);
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout> cls2, ActivityParameters activityParameters) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, cls2, true, true, false, activityParameters);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls2, true, false, false, activityParameters);
        } else {
            PopScreensAndReplace(0, cls2, true, false, false, activityParameters);
        }
    }

    public void PushScreen(Class<? extends ScreenLayout> cls) throws XLEException {
        PushScreen(cls, null);
    }

    public void PushScreen(Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(0, cls, true, false, false, activityParameters);
    }

    public void RestartCurrentScreen(ActivityParameters activityParameters, boolean z) throws XLEException {
        if (this.animationState == NavigationManagerAnimationState.ANIMATING_OUT) {
            OnAnimationEnd();
        } else if (this.animationState == NavigationManagerAnimationState.ANIMATING_IN) {
            OnAnimationEnd();
            PopScreensAndReplace(1, getCurrentActivity().getClass(), z, true, true, activityParameters);
        } else {
            PopScreensAndReplace(1, getCurrentActivity().getClass(), z, true, true, activityParameters);
        }
    }

    public void RestartCurrentScreen(boolean z) throws XLEException {
        RestartCurrentScreen(null, z);
    }

    public boolean ShouldBackCloseApp() {
        return Size() <= 1 && this.animationState == NavigationManagerAnimationState.NONE;
    }

    public boolean TEST_isAnimatingIn() {
        return false;
    }

    public boolean TEST_isAnimatingOut() {
        return false;
    }

    public ActivityParameters getActivityParameters() {
        return getActivityParameters(0);
    }

    public ActivityParameters getActivityParameters(int i) {
        boolean z = i >= 0 && i < this.navigationParameters.size();
        XLEAssert.assertTrue(z);
        return (ActivityParameters) this.navigationParameters.get((this.navigationParameters.size() - i) - 1);
    }

    public ScreenLayout getCurrentActivity() {
        return this.navigationStack.empty() ? null : (ScreenLayout) this.navigationStack.peek();
    }

    public String getCurrentActivityName() {
        ScreenLayout currentActivity = getCurrentActivity();
        return currentActivity != null ? currentActivity.getName() : null;
    }

    public ScreenLayout getPreviousActivity() {
        return (this.navigationStack.empty() || this.navigationStack.size() <= 1) ? null : (ScreenLayout) this.navigationStack.get(this.navigationStack.size() - 2);
    }

    public boolean isAnimating() {
        return this.animationState != NavigationManagerAnimationState.NONE;
    }

    public void onApplicationPause() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            ((ScreenLayout) this.navigationStack.get(i)).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            ((ScreenLayout) this.navigationStack.get(i)).onApplicationResume();
        }
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getAction() != 1) {
            return false;
        }
        if (!OnBackButtonPressed()) {
            return true;
        }
        removeNavigationCallbacks();
        removeNaviationListener();
        return false;
    }

    public void removeNaviationListener() {
        this.navigationListener = null;
    }

    public void removeNavigationCallbacks() {
        this.navigationCallbacks = null;
    }

    public void setAnimationBlocking(boolean z) {
        if (this.navigationCallbacks != null) {
            this.navigationCallbacks.setAnimationBlocking(z);
        }
    }

    public void setNavigationCallbacks(NavigationCallbacks navigationCallbacks) {
        this.navigationCallbacks = navigationCallbacks;
    }

    public void setOnNavigatedListener(OnNavigatedListener onNavigatedListener) {
        this.navigationListener = onNavigatedListener;
    }
}
