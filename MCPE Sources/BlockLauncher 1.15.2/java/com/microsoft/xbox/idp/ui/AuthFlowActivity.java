package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.microsoft.onlineid.Ticket;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.interop.XsapiUser;
import com.microsoft.xbox.idp.interop.XsapiUser.UserImpl;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCSignin;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.ui.AccountProvisioningFragment.Callbacks;
import com.microsoft.xbox.idp.ui.AccountProvisioningFragment.Status;
import com.microsoft.xbox.idp.util.AuthFlowResult;
import com.microsoft.xbox.idp.util.CacheUtil;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class AuthFlowActivity extends AuthActivity implements Callbacks, EventInitializationFragment.Callbacks, FinishSignInFragment.Callbacks, HeaderFragment.Callbacks, IntroducingFragment.Callbacks, MSAFragment.Callbacks, SignUpFragment.Callbacks, StartSignInFragment.Callbacks, WelcomeFragment.Callbacks, XBLoginFragment.Callbacks, XBLogoutFragment.Callbacks {
    public static final String ARG_ALT_BUTTON_TEXT = "ARG_ALT_BUTTON_TEXT";
    public static final String ARG_SECURITY_POLICY = "ARG_SECURITY_POLICY";
    public static final String ARG_SECURITY_SCOPE = "ARG_SECURITY_SCOPE";
    public static final String ARG_USER_PTR = "ARG_USER_PTR";
    public static final String EXTRA_CID = "EXTRA_CID";
    private static final String KEY_STATE = "KEY_STATE";
    public static final int RESULT_PROVIDER_ERROR = 2;
    private static final String TAG = AuthFlowActivity.class.getSimpleName();
    private static StaticCallbacks staticCallbacks;
    private final Handler handler = new Handler();
    private State state;
    private boolean stateSaved;
    private AuthFlowScreenStatus status = AuthFlowScreenStatus.NO_ERROR;

    public interface StaticCallbacks {
        void onAuthFlowFinished(long j, AuthFlowScreenStatus authFlowScreenStatus, String str);
    }

    static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$AccountProvisioningFragment$Status = new int[Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task = new int[Task.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$EventInitializationFragment$Status = new int[EventInitializationFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$IntroducingFragment$Status = new int[IntroducingFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$MSAFragment$Status = new int[MSAFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status = new int[SignUpFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$StartSignInFragment$Status = new int[StartSignInFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status = new int[WelcomeFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$XBLoginFragment$Status = new int[XBLoginFragment.Status.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$XBLogoutFragment$Status = new int[XBLogoutFragment.Status.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$XBLogoutFragment$Status[XBLogoutFragment.Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$XBLogoutFragment$Status[XBLogoutFragment.Status.ERROR.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status[WelcomeFragment.Status.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status[WelcomeFragment.Status.ERROR_USER_CANCEL.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status[WelcomeFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status[WelcomeFragment.Status.ERROR_SWITCH_USER.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$IntroducingFragment$Status[IntroducingFragment.Status.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$IntroducingFragment$Status[IntroducingFragment.Status.ERROR_USER_CANCEL.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$IntroducingFragment$Status[IntroducingFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status[SignUpFragment.Status.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status[SignUpFragment.Status.ERROR_USER_CANCEL.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status[SignUpFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status[SignUpFragment.Status.ERROR_SWITCH_USER.ordinal()] = 4;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$EventInitializationFragment$Status[EventInitializationFragment.Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$EventInitializationFragment$Status[EventInitializationFragment.Status.ERROR.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$EventInitializationFragment$Status[EventInitializationFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AccountProvisioningFragment$Status[Status.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AccountProvisioningFragment$Status[Status.ERROR_USER_CANCEL.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AccountProvisioningFragment$Status[Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$XBLoginFragment$Status[XBLoginFragment.Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$XBLoginFragment$Status[XBLoginFragment.Status.ERROR.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$XBLoginFragment$Status[XBLoginFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$MSAFragment$Status[MSAFragment.Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$MSAFragment$Status[MSAFragment.Status.ERROR.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$MSAFragment$Status[MSAFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$StartSignInFragment$Status[StartSignInFragment.Status.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$StartSignInFragment$Status[StartSignInFragment.Status.ERROR.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$StartSignInFragment$Status[StartSignInFragment.Status.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[Task.SIGN_UP.ordinal()] = 1;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[Task.INTRODUCING.ordinal()] = AuthFlowActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[Task.WELCOME.ordinal()] = 3;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[Task.XB_LOGOUT.ordinal()] = 4;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[Task.FINISH_SIGN_IN.ordinal()] = 5;
            } catch (NoSuchFieldError e33) {
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
        public AccountProvisioningResult accountProvisioningResult;
        public String cid;
        public boolean createAccount;
        public Task currentTask;
        public AuthFlowScreenStatus lastStatus;
        public boolean nativeActivity;
        public String ticket;
        public UserImpl userImpl;

        protected State(Parcel parcel) {
            boolean z = false;
            this.userImpl = (UserImpl) parcel.readParcelable(UserImpl.class.getClassLoader());
            int readInt = parcel.readInt();
            if (readInt != -1) {
                this.currentTask = Task.values()[readInt];
            }
            this.cid = parcel.readString();
            this.ticket = parcel.readString();
            this.createAccount = parcel.readInt() != 0;
            if (parcel.readInt() != 0) {
                z = true;
            }
            this.nativeActivity = z;
            readInt = parcel.readInt();
            if (readInt != -1) {
                this.lastStatus = AuthFlowScreenStatus.values()[readInt];
            }
            this.accountProvisioningResult = (AccountProvisioningResult) parcel.readParcelable(AccountProvisioningResult.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            int i2 = 0;
            int i3 = -1;
            parcel.writeParcelable(this.userImpl, i);
            parcel.writeInt(this.currentTask == null ? -1 : this.currentTask.ordinal());
            parcel.writeString(this.cid);
            parcel.writeString(this.ticket);
            parcel.writeInt(this.createAccount ? 1 : 0);
            if (this.nativeActivity) {
                i2 = 1;
            }
            parcel.writeInt(i2);
            if (this.lastStatus != null) {
                i3 = this.lastStatus.ordinal();
            }
            parcel.writeInt(i3);
            parcel.writeParcelable(this.accountProvisioningResult, i);
        }
    }

    private enum Task {
        START_SIGN_IN,
        MSA,
        XB_LOGIN,
        ACCOUNT_PROVISIONING,
        SIGN_UP,
        EVENT_INITIALIZATION,
        INTRODUCING,
        WELCOME,
        FINISH_SIGN_IN,
        XB_LOGOUT
    }

    private void finishWithResult() {
        if (this.state.nativeActivity || this.state.currentTask == Task.FINISH_SIGN_IN) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CID, this.state.cid);
            setResult(AuthActivity.toActivityResult(this.status), intent);
            finishCompat();
            return;
        }
        this.state.lastStatus = this.status;
        this.handler.post(new Runnable() {
            public void run() {
                Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                if (!AuthFlowActivity.this.stateSaved) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FinishSignInFragment.ARG_AUTH_STATUS, AuthFlowActivity.this.status.toString());
                    bundle.putString(FinishSignInFragment.ARG_CID, AuthFlowActivity.this.state.cid);
                    AuthFlowActivity.this.showBodyFragment(Task.FINISH_SIGN_IN, new FinishSignInFragment(), bundle, true);
                }
            }
        });
    }

    public static void setStaticCallbacks(StaticCallbacks staticCallbacks) {
        staticCallbacks = staticCallbacks;
    }

    private void showBodyFragment(Task task, Fragment fragment, Bundle bundle, boolean z) {
        this.state.currentTask = task;
        showBodyFragment(fragment, bundle, z);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.state.currentTask == Task.MSA) {
            getFragmentManager().findFragmentById(R.id.xbid_body_fragment).onActivityResult(i, i2, intent);
        }
    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        UTCUser.trackCancel(getTitle());
        this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$AuthFlowActivity$Task[this.state.currentTask.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case RESULT_PROVIDER_ERROR /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                UTCUser.trackCancel(getTitle());
                this.status = AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                return;
            default:
                UTCUser.trackCancel(getTitle());
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
        }
    }

    public void onCloseWithStatus(Status status, final AccountProvisioningResult accountProvisioningResult) {
        Log.d(TAG, "onComplete: AccountProvisioningFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$AccountProvisioningFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            AuthFlowActivity.this.state.accountProvisioningResult = accountProvisioningResult;
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            bundle.putString(XBLoginFragment.ARG_RPS_TICKET, AuthFlowActivity.this.state.ticket);
                            AuthFlowActivity.this.showBodyFragment(Task.EVENT_INITIALIZATION, new EventInitializationFragment(), bundle, false);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onCloseWithStatus(IntroducingFragment.Status status) {
        Log.d(TAG, "onCloseWithStatus: IntroducingFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$IntroducingFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.status = AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onCloseWithStatus(SignUpFragment.Status status) {
        Log.d(TAG, "onCloseWithStatus: SignUpFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$SignUpFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            Bundle extras = AuthFlowActivity.this.getIntent().getExtras();
                            if (extras != null && extras.containsKey(AuthFlowActivity.ARG_ALT_BUTTON_TEXT)) {
                                bundle.putString(AuthFlowActivity.ARG_ALT_BUTTON_TEXT, extras.getString(AuthFlowActivity.ARG_ALT_BUTTON_TEXT));
                            }
                            AuthFlowActivity.this.showBodyFragment(Task.INTRODUCING, new IntroducingFragment(), bundle, true);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.status = AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.XB_LOGOUT, new XBLogoutFragment(), bundle, true);
                        }
                    }
                });
                return;
            default:
                return;
        }
    }

    public void onCloseWithStatus(WelcomeFragment.Status status) {
        Log.d(TAG, "onCloseWithStatus: WelcomeFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$WelcomeFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.status = AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.status = AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.XB_LOGOUT, new XBLogoutFragment(), bundle, true);
                        }
                    }
                });
                return;
            default:
                return;
        }
    }

    public void onComplete(EventInitializationFragment.Status status) {
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$EventInitializationFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                final CharSequence title = getTitle();
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!(AuthFlowActivity.this.state == null || AuthFlowActivity.this.state.accountProvisioningResult == null)) {
                            UTCCommonDataModel.setUserId(AuthFlowActivity.this.state.accountProvisioningResult.getXuid());
                        }
                        UTCSignin.trackXBLSigninSuccess(AuthFlowActivity.this.state.cid, title, AuthFlowActivity.this.state.createAccount);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            if (AuthFlowActivity.this.state.createAccount) {
                                bundle.putParcelable(SignUpFragment.ARG_ACCOUNT_PROVISIONING_RESULT, AuthFlowActivity.this.state.accountProvisioningResult);
                                AuthFlowActivity.this.showBodyFragment(Task.SIGN_UP, new SignUpFragment(), bundle, true);
                                return;
                            }
                            Bundle extras = AuthFlowActivity.this.getIntent().getExtras();
                            if (extras != null && extras.containsKey(AuthFlowActivity.ARG_ALT_BUTTON_TEXT)) {
                                bundle.putString(AuthFlowActivity.ARG_ALT_BUTTON_TEXT, extras.getString(AuthFlowActivity.ARG_ALT_BUTTON_TEXT));
                            }
                            AuthFlowActivity.this.showBodyFragment(Task.WELCOME, new WelcomeFragment(), bundle, true);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onComplete(FinishSignInFragment.Status status) {
        Log.d(TAG, "onComplete: FinishSignInFragment.Status." + status);
        this.status = this.state.lastStatus;
        finishWithResult();
    }

    public void onComplete(MSAFragment.Status status, String str, Ticket ticket) {
        Log.d(TAG, "onComplete: MSAFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$MSAFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.state.cid = str;
                this.state.ticket = ticket.getValue();
                Bundle bundle = new Bundle();
                bundle.putString(XBLoginFragment.ARG_RPS_TICKET, this.state.ticket);
                bundle.putLong(ARG_USER_PTR, this.state.userImpl.getUserImplPtr());
                UTCSignin.trackXBLSigninStart(str, getTitle());
                showBodyFragment(Task.XB_LOGIN, new XBLoginFragment(), bundle, false);
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onComplete(StartSignInFragment.Status status) {
        Log.d(TAG, "onComplete: StartSignInFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$StartSignInFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            AuthFlowActivity.this.state.userImpl = XsapiUser.getInstance().getUserImpl();
                            Bundle bundle = new Bundle(AuthFlowActivity.this.getIntent().getExtras());
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.MSA, new MSAFragment(), bundle, false);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onComplete(XBLoginFragment.Status status, AuthFlowResult authFlowResult, final boolean z) {
        Log.d(TAG, "onComplete: XBLoginFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$XBLoginFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.state.createAccount = z;
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(AuthFlowActivity.ARG_USER_PTR, AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            if (z) {
                                AuthFlowActivity.this.showBodyFragment(Task.ACCOUNT_PROVISIONING, new AccountProvisioningFragment(), bundle, false);
                                return;
                            }
                            bundle.putString(XBLoginFragment.ARG_RPS_TICKET, AuthFlowActivity.this.state.ticket);
                            AuthFlowActivity.this.showBodyFragment(Task.EVENT_INITIALIZATION, new EventInitializationFragment(), bundle, false);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
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

    public void onComplete(XBLogoutFragment.Status status) {
        Log.d(TAG, "onComplete: XBLogoutFragment.Status." + status);
        switch (AnonymousClass10.$SwitchMap$com$microsoft$xbox$idp$ui$XBLogoutFragment$Status[status.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            CacheUtil.clearCaches();
                            AuthFlowActivity.this.state.createAccount = false;
                            AuthFlowActivity.this.showBodyFragment(Task.MSA, new MSAFragment(), AuthFlowActivity.this.getIntent().getExtras(), false);
                        }
                    }
                });
                return;
            case RESULT_PROVIDER_ERROR /*2*/:
                Log.e(TAG, "Should not be here! Cancelling auth flow.");
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
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
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Log.e(TAG, "Intent has no extras");
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            }
            Bundle bundle2 = new Bundle(extras);
            if (bundle2.containsKey(ARG_USER_PTR)) {
                Log.e(TAG, "User pointer present, native activity mode");
                this.state.nativeActivity = true;
                this.state.userImpl = new UserImpl(bundle2.getLong(ARG_USER_PTR));
                showBodyFragment(Task.MSA, new MSAFragment(), bundle2, false);
                return;
            }
            Log.e(TAG, "No user pointer, non-native activity mode");
            this.state.nativeActivity = false;
            CacheUtil.clearCaches();
            showBodyFragment(Task.START_SIGN_IN, new StartSignInFragment(), bundle2, false);
            return;
        }
        this.state = (State) bundle.getParcelable(KEY_STATE);
    }

    protected void onDestroy() {
        UTCPageView.removePage();
        super.onDestroy();
        if (isFinishing() && this.state.nativeActivity && staticCallbacks != null) {
            staticCallbacks.onAuthFlowFinished(this.state.userImpl.getUserImplPtr(), this.status, this.state.cid);
        }
    }

    protected void onResume() {
        super.onResume();
        this.stateSaved = false;
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
        this.stateSaved = true;
    }
}
