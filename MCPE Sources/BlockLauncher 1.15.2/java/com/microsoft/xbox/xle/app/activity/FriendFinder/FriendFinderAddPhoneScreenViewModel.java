package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.os.AsyncTask;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers.LinkedAccountType;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.MsgType;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.PhoneState;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileResponse;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderAddPhoneScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderAddPhoneScreenViewModel extends ViewModelBase {
    private AddShortCircuitProfileAsyncTask addShortCircuitProfileAsyncTask;
    private String currentCountryCode;
    private boolean isAddingProfile;
    private boolean isLoadingInfo;
    private boolean isLoadingMyProfileTask;
    private boolean isUploadingContactsAndOptingIn;
    private LoadInfoAsyncTask loadInfoAsyncTask;
    private LoadMyProfileAsyncTask loadMyProfileAsyncTask;
    private PhoneState myPhoneState;
    private ShortCircuitProfileResponse myProfile;
    private OptInAsyncTask optInAsyncTask;
    private String simPhoneNumber;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus = new int[AsyncActionStatus.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_CHANGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_SUCCESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.FAIL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_FAIL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private class AddShortCircuitProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private AddShortCircuitProfileAsyncTask() {
        }

        private MsgType getAddType() {
            if (FriendFinderAddPhoneScreenViewModel.this.myPhoneState == null) {
                return MsgType.Add;
            }
            boolean z = FriendFinderAddPhoneScreenViewModel.this.myPhoneState.isVerified && FriendFinderAddPhoneScreenViewModel.this.myPhoneState.hasXboxApplication;
            XLEAssert.assertFalse("Check for these before invoking this task", z);
            return FriendFinderAddPhoneScreenViewModel.this.myPhoneState.isVerified ? MsgType.AddXbox : MsgType.Edit;
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            int i = 0;
            try {
                String regionWithCode = PhoneContactInfo.getInstance().getRegionWithCode();
                String userEnteredNumber = PhoneContactInfo.getInstance().getUserEnteredNumber();
                r2 = JavaUtil.isNullOrEmpty(regionWithCode) ? !userEnteredNumber.startsWith("+") ? "+" + userEnteredNumber : userEnteredNumber : userEnteredNumber.replace("+", BuildConfig.FLAVOR);
                ShortCircuitProfileResponse sendShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(getAddType(), r2, regionWithCode));
                if (sendShortCircuitProfile == null || sendShortCircuitProfile.error == null) {
                    return AsyncActionStatus.SUCCESS;
                }
                if (JavaUtil.isNullOrEmpty(regionWithCode)) {
                    for (int i2 = 0; i2 < r2.length(); i2++) {
                        if (Character.isDigit(r2.charAt(i2))) {
                            i++;
                        }
                    }
                    if (i == 10) {
                        ShortCircuitProfileResponse sendShortCircuitProfile2 = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(getAddType(), "+1" + r2.replace("+", BuildConfig.FLAVOR), regionWithCode));
                        return (sendShortCircuitProfile2 == null || sendShortCircuitProfile2.error == null) ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
                    }
                }
                return (sendShortCircuitProfile.error.code == null || !sendShortCircuitProfile.error.code.equalsIgnoreCase("PhoneAlreadyVerified")) ? AsyncActionStatus.FAIL : AsyncActionStatus.SUCCESS;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderAddPhoneScreenViewModel.this.onAddShortCircuitProfileCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderAddPhoneScreenViewModel.this.isAddingProfile = true;
        }
    }

    private class LoadInfoAsyncTask extends AsyncTask<Void, Void, Void> {
        private LoadInfoAsyncTask() {
        }

        protected Void doInBackground(Void... voidArr) {
            PhoneContactInfo.getInstance().getCountryNameFromRegion(PhoneContactInfo.getInstance().getRegion());
            String countryCode = PhoneContactInfo.getInstance().getCountryCode();
            if (!JavaUtil.isNullOrEmpty(countryCode)) {
                FriendFinderAddPhoneScreenViewModel.this.currentCountryCode = countryCode;
            }
            FriendFinderAddPhoneScreenViewModel.this.simPhoneNumber = PhoneContactInfo.getInstance().getPhoneNumberFromSim();
            return null;
        }

        protected void onPostExecute(Void voidR) {
            FriendFinderAddPhoneScreenViewModel.this.isLoadingInfo = false;
            FriendFinderAddPhoneScreenViewModel.this.updateAdapter();
        }

        protected void onPreExecute() {
            FriendFinderAddPhoneScreenViewModel.this.isLoadingInfo = true;
        }
    }

    private class LoadMyProfileAsyncTask extends NetworkAsyncTask<AsyncResult<ShortCircuitProfileResponse>> {
        private LoadMyProfileAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            return FriendFinderAddPhoneScreenViewModel.this.myProfile == null;
        }

        protected AsyncResult<ShortCircuitProfileResponse> loadDataInBackground() {
            Object myShortCircuitProfile;
            try {
                myShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().getMyShortCircuitProfile();
            } catch (XLEException e) {
                myShortCircuitProfile = null;
            }
            return new AsyncResult(myShortCircuitProfile, this, null);
        }

        protected AsyncResult<ShortCircuitProfileResponse> onError() {
            return null;
        }

        protected void onNoAction() {
            FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(AsyncActionStatus.SUCCESS, FriendFinderAddPhoneScreenViewModel.this.myProfile);
        }

        protected void onPostExecute(AsyncResult<ShortCircuitProfileResponse> asyncResult) {
            if (asyncResult != null) {
                FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(asyncResult.getStatus(), (ShortCircuitProfileResponse) asyncResult.getResult());
            } else {
                FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(AsyncActionStatus.FAIL, null);
            }
        }

        protected void onPreExecute() {
            FriendFinderAddPhoneScreenViewModel.this.isLoadingMyProfileTask = true;
        }
    }

    private class OptInAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private OptInAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            try {
                ServiceManagerFactory.getInstance().getSLSServiceManager().setFriendFinderOptInStatus(LinkedAccountType.Phone, OptInStatus.OptedIn);
                return AsyncActionStatus.SUCCESS;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderAddPhoneScreenViewModel.this.onOptInCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderAddPhoneScreenViewModel.this.isUploadingContactsAndOptingIn = true;
        }
    }

    public FriendFinderAddPhoneScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.adapter = new FriendFinderAddPhoneScreenAdapter(this);
    }

    private void cancelActiveTasks() {
        if (this.addShortCircuitProfileAsyncTask != null) {
            this.addShortCircuitProfileAsyncTask.cancel();
            this.addShortCircuitProfileAsyncTask = null;
        }
        if (this.loadInfoAsyncTask != null) {
            this.loadInfoAsyncTask.cancel(true);
            this.loadInfoAsyncTask = null;
        }
        if (this.loadMyProfileAsyncTask != null) {
            this.loadMyProfileAsyncTask.cancel();
            this.loadMyProfileAsyncTask = null;
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
        if (this.optInAsyncTask != null) {
            this.optInAsyncTask.cancel();
            this.optInAsyncTask = null;
        }
    }

    private boolean needToAddPhoneNumber(String str) {
        if (this.myProfile == null) {
            return true;
        }
        this.myPhoneState = this.myProfile.isVerified(str);
        return (this.myPhoneState != null && this.myPhoneState.isVerified && this.myPhoneState.hasXboxApplication) ? false : true;
    }

    private void onAddShortCircuitProfileCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingProfile = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                try {
                    NavigationManager.getInstance().PushScreen(FriendFinderVerifyCodeScreen.class);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                updateAdapter();
                return;
            default:
                return;
        }
    }

    private void onLoadMyProfileCompleted(AsyncActionStatus asyncActionStatus, ShortCircuitProfileResponse shortCircuitProfileResponse) {
        this.isLoadingMyProfileTask = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.myProfile = shortCircuitProfileResponse;
                break;
        }
        updateAdapter();
    }

    private void onOptInCompleted(AsyncActionStatus asyncActionStatus) {
        this.isUploadingContactsAndOptingIn = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                try {
                    ActivityParameters activityParameters = new ActivityParameters();
                    activityParameters.putFriendFinderType(FriendFinderType.PHONE);
                    NavigationManager.getInstance().PushScreen(FriendFinderSuggestionsScreen.class, activityParameters);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                updateAdapter();
                return;
            default:
                return;
        }
    }

    public void addPhoneNumber(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            showError(R.string.FriendFinder_PhoneNumberHint);
            return;
        }
        PhoneContactInfo.getInstance().setUserEnteredNumber(str);
        if (needToAddPhoneNumber(str)) {
            if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.normalizePhoneNumber(str))) {
                cancelActiveTasks();
                this.addShortCircuitProfileAsyncTask = new AddShortCircuitProfileAsyncTask();
                this.addShortCircuitProfileAsyncTask.load(true);
            } else {
                showError(R.string.FriendFinder_PhoneVerifyEnterRegionAndPhoneNubmer);
                return;
            }
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
        this.uploadContactsAsyncTask = new UploadContactsAsyncTask(null);
        this.uploadContactsAsyncTask.load(true);
        if (this.optInAsyncTask != null) {
            this.optInAsyncTask.cancel();
            this.optInAsyncTask = null;
        }
        this.optInAsyncTask = new OptInAsyncTask();
        this.optInAsyncTask.load(true);
        updateAdapter();
    }

    public String getCurrentCountryCode() {
        return this.currentCountryCode;
    }

    public String getSimPhoneNumber() {
        return this.simPhoneNumber;
    }

    public boolean isBusy() {
        return this.isAddingProfile || this.isLoadingInfo || this.isLoadingMyProfileTask || this.isUploadingContactsAndOptingIn;
    }

    public void load(boolean z) {
        cancelActiveTasks();
        this.loadInfoAsyncTask = new LoadInfoAsyncTask();
        this.loadInfoAsyncTask.execute(new Void[0]);
        this.loadMyProfileAsyncTask = new LoadMyProfileAsyncTask();
        this.loadMyProfileAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.PHONE);
        return super.onBackButtonPressed();
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderAddPhoneScreenAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
        cancelActiveTasks();
    }
}
