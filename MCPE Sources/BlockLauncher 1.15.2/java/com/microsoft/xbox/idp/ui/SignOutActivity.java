package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.ui.HeaderFragment.Callbacks;
import com.microsoft.xbox.idp.ui.SignOutFragment.Status;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class SignOutActivity extends AuthActivity implements Callbacks, SignOutFragment.Callbacks {
    private static final String KEY_STATE = "KEY_STATE";
    private static final String TAG = SignOutActivity.class.getSimpleName();
    private State state;
    private AuthFlowScreenStatus status = AuthFlowScreenStatus.NO_ERROR;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$SignOutFragment$Status = new int[Status.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignOutFragment$Status[Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignOutFragment$Status[Status.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignOutFragment$Status[Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel parcel) {
                return new State(parcel);
            }

            public State[] newArray(int i) {
                return new State[i];
            }
        };
        public Task currentTask;

        protected State(Parcel parcel) {
            int readInt = parcel.readInt();
            if (readInt != -1) {
                this.currentTask = Task.values()[readInt];
            }
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.currentTask == null ? -1 : this.currentTask.ordinal());
        }
    }

    private enum Task {
        SIGN_OUT
    }

    private void finishWithResult() {
        setResult(AuthActivity.toActivityResult(this.status));
        finishCompat();
    }

    private void showBodyFragment(Task task, Fragment fragment, Bundle bundle, boolean z) {
        this.state.currentTask = task;
        showBodyFragment(fragment, bundle, z);
    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onComplete(Status status) {
        Log.d(TAG, "onComplete: StartSignInFragment.Status." + status);
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$idp$ui$SignOutFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.status = AuthFlowScreenStatus.NO_ERROR;
                UTCUser.trackSignout(getTitle());
                finishWithResult();
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.status = AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.xbid_activity_auth_flow);
        if (bundle == null) {
            this.state = new State();
            showBodyFragment(Task.SIGN_OUT, new SignOutFragment(), new Bundle(), true);
            return;
        }
        this.state = (State) bundle.getParcelable(KEY_STATE);
    }

    protected void onDestroy() {
        UTCPageView.removePage();
        super.onDestroy();
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }
}
