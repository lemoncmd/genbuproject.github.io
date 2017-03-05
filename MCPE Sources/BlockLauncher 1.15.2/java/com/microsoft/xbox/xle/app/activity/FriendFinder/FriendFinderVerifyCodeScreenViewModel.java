package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers.LinkedAccountType;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.MsgType;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileResponse;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySetting;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingId;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingValue;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask.UploadContactsCompleted;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderVerifyCodeScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import java.util.ArrayList;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderVerifyCodeScreenViewModel extends ViewModelBase {
    private static final String TAG = "FriendFinder";
    private AddShortCircuitProfileAsyncTask addShortCircuitProfileAsyncTask;
    private boolean isSendingCode;
    private boolean isUpdatingProfile;
    private UpdateShortCircuitProfileAsyncTask updateShortCircuitProfileAsyncTask;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    static /* synthetic */ class AnonymousClass2 {
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
        private boolean useVoice;

        public AddShortCircuitProfileAsyncTask(boolean z) {
            this.useVoice = z;
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
                ShortCircuitProfileResponse sendShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(MsgType.Delete, r2, regionWithCode));
                if (sendShortCircuitProfile != null && sendShortCircuitProfile.error != null && sendShortCircuitProfile.error.message != null && !sendShortCircuitProfile.error.message.contains("Cannot edit or delete a phone that does not exist")) {
                    return AsyncActionStatus.FAIL;
                }
                sendShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(MsgType.Add, r2, regionWithCode, this.useVoice));
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
                        ShortCircuitProfileResponse sendShortCircuitProfile2 = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(MsgType.Add, "+1" + r2.replace("+", BuildConfig.FLAVOR), regionWithCode));
                        return (sendShortCircuitProfile2 == null || sendShortCircuitProfile2.error == null) ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
                    }
                }
                return AsyncActionStatus.FAIL;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        protected AsyncActionStatus onError() {
            return null;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderVerifyCodeScreenViewModel.this.onAddShortCircuitProfileCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
        }
    }

    private class UpdateShortCircuitProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String verificationToken;

        public UpdateShortCircuitProfileAsyncTask(String str) {
            this.verificationToken = str;
        }

        private boolean updateOptInSucceeded() throws XLEException {
            PrivacySetting privacySetting = ServiceManagerFactory.getInstance().getSLSServiceManager().getPrivacySetting(PrivacySettingId.ShareIdentity);
            PrivacySettingValue privacySettingValue = privacySetting != null ? privacySetting.getPrivacySettingValue() : PrivacySettingValue.NotSet;
            if (privacySettingValue == PrivacySettingValue.NotSet || privacySettingValue == PrivacySettingValue.Blocked) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(new PrivacySetting(PrivacySettingId.ShareIdentity, PrivacySettingValue.FriendCategoryShareIdentity));
                if (!ServiceManagerFactory.getInstance().getSLSServiceManager().setPrivacySettings(new PrivacySettingsResult(arrayList))) {
                    return false;
                }
            }
            return ServiceManagerFactory.getInstance().getSLSServiceManager().setFriendFinderOptInStatus(LinkedAccountType.Phone, OptInStatus.OptedIn);
        }

        private boolean verifyPhoneNumberSucceeded() throws XLEException {
            ShortCircuitProfileResponse shortCircuitProfileResponse;
            boolean z;
            String regionWithCode = PhoneContactInfo.getInstance().getRegionWithCode();
            String userEnteredNumber = PhoneContactInfo.getInstance().getUserEnteredNumber();
            if (!JavaUtil.isNullOrEmpty(regionWithCode)) {
                userEnteredNumber = userEnteredNumber.replace("+", BuildConfig.FLAVOR);
            } else if (!userEnteredNumber.startsWith("+")) {
                userEnteredNumber = "+" + userEnteredNumber;
            }
            ShortCircuitProfileResponse sendShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(MsgType.PhoneVerification, userEnteredNumber, regionWithCode, this.verificationToken));
            boolean z2 = sendShortCircuitProfile.error == null || (sendShortCircuitProfile.error.code != null && sendShortCircuitProfile.error.code.equalsIgnoreCase("PhoneAlreadyVerified:PhoneToVerifyTokenAlreadyVerified"));
            if (!z2 && JavaUtil.isNullOrEmpty(regionWithCode)) {
                int i = 0;
                for (int i2 = 0; i2 < userEnteredNumber.length(); i2++) {
                    if (Character.isDigit(userEnteredNumber.charAt(i2))) {
                        i++;
                    }
                }
                if (i == 10) {
                    ShortCircuitProfileResponse sendShortCircuitProfile2 = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileRequest(MsgType.PhoneVerification, "+1" + userEnteredNumber.replace("+", BuildConfig.FLAVOR), regionWithCode, this.verificationToken));
                    if (sendShortCircuitProfile2.error == null || (sendShortCircuitProfile2.error.code != null && sendShortCircuitProfile2.error.code.equalsIgnoreCase("PhoneAlreadyVerified:PhoneToVerifyTokenAlreadyVerified"))) {
                        shortCircuitProfileResponse = sendShortCircuitProfile2;
                        z = true;
                    } else {
                        shortCircuitProfileResponse = sendShortCircuitProfile2;
                        z = false;
                    }
                    return shortCircuitProfileResponse != null && z;
                }
            }
            z = z2;
            shortCircuitProfileResponse = sendShortCircuitProfile;
            if (shortCircuitProfileResponse != null) {
                return false;
            }
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            try {
                if (verifyPhoneNumberSucceeded() && updateOptInSucceeded()) {
                    UTCFriendFinder.trackPhoneContactsLinkSuccess(FriendFinderVerifyCodeScreenViewModel.this.getScreen().getName());
                    return AsyncActionStatus.SUCCESS;
                }
            } catch (XLEException e) {
            }
            return AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderVerifyCodeScreenViewModel.this.onUpdateShortCircuitProfileCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderVerifyCodeScreenViewModel.this.isUpdatingProfile = true;
        }
    }

    public FriendFinderVerifyCodeScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.adapter = new FriendFinderVerifyCodeScreenAdapter(this);
    }

    private void addProfile(boolean z) {
        if (this.addShortCircuitProfileAsyncTask != null) {
            this.addShortCircuitProfileAsyncTask.cancel();
        }
        this.addShortCircuitProfileAsyncTask = new AddShortCircuitProfileAsyncTask(z);
        this.addShortCircuitProfileAsyncTask.load(true);
        this.isSendingCode = true;
        updateAdapter();
    }

    private void cancelActiveTasks() {
        if (this.addShortCircuitProfileAsyncTask != null) {
            this.addShortCircuitProfileAsyncTask.cancel();
            this.addShortCircuitProfileAsyncTask = null;
        }
        if (this.updateShortCircuitProfileAsyncTask != null) {
            this.updateShortCircuitProfileAsyncTask.cancel();
            this.updateShortCircuitProfileAsyncTask = null;
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
    }

    private void onAddShortCircuitProfileCompleted(AsyncActionStatus asyncActionStatus) {
        this.isSendingCode = false;
        switch (AnonymousClass2.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                break;
        }
        updateAdapter();
    }

    private void onUpdateShortCircuitProfileCompleted(AsyncActionStatus asyncActionStatus) {
        switch (AnonymousClass2.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (this.uploadContactsAsyncTask != null) {
                    this.uploadContactsAsyncTask.cancel();
                    this.uploadContactsAsyncTask = null;
                }
                this.uploadContactsAsyncTask = new UploadContactsAsyncTask(new UploadContactsCompleted() {
                    public void onResult(AsyncActionStatus asyncActionStatus) {
                        FriendFinderVerifyCodeScreenViewModel.this.onUploadContactsCompleted(asyncActionStatus);
                    }
                });
                this.uploadContactsAsyncTask.load(true);
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                this.isUpdatingProfile = false;
                showError(R.string.Service_ErrorText);
                updateAdapter();
                return;
            default:
                return;
        }
    }

    private void onUploadContactsCompleted(AsyncActionStatus asyncActionStatus) {
        this.isUpdatingProfile = false;
        updateAdapter();
        ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putFriendFinderType(FriendFinderType.PHONE);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderSuggestionsScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    public void callMe() {
        addProfile(true);
    }

    public boolean isBusy() {
        return this.isUpdatingProfile;
    }

    public boolean isSendingCode() {
        return this.isSendingCode;
    }

    public void load(boolean z) {
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.PHONE);
        return super.onBackButtonPressed();
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderVerifyCodeScreenAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
        cancelActiveTasks();
    }

    public void resendCode() {
        addProfile(false);
    }

    public void verifyCode(String str) {
        if (this.updateShortCircuitProfileAsyncTask != null) {
            this.updateShortCircuitProfileAsyncTask.cancel();
        }
        this.updateShortCircuitProfileAsyncTask = new UpdateShortCircuitProfileAsyncTask(str);
        this.updateShortCircuitProfileAsyncTask.load(true);
        updateAdapter();
    }
}
