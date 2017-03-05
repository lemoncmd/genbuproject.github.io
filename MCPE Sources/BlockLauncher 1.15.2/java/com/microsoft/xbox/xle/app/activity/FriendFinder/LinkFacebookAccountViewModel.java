package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers.LinkedAccountType;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySetting;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingId;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingValue;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import java.util.ArrayList;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class LinkFacebookAccountViewModel extends ViewModelBase {
    private LinkFacebookAccountAsyncTask linkAccountAsyncTask;

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

    protected class LinkFacebookAccountAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected LinkFacebookAccountAsyncTask() {
        }

        private boolean needUpdatePrivacy(PrivacySetting privacySetting) {
            PrivacySettingValue privacySettingValue = privacySetting.getPrivacySettingValue();
            return privacySettingValue == PrivacySettingValue.NotSet || privacySettingValue == PrivacySettingValue.Blocked;
        }

        protected boolean checkShouldExecute() {
            return false;
        }

        protected AsyncActionStatus loadDataInBackground() {
            try {
                PrivacySetting privacySetting = ServiceManagerFactory.getInstance().getSLSServiceManager().getPrivacySetting(PrivacySettingId.ShareIdentity);
                if (privacySetting != null && needUpdatePrivacy(privacySetting)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new PrivacySetting(PrivacySettingId.ShareIdentity, PrivacySettingValue.FriendCategoryShareIdentity));
                    if (!ServiceManagerFactory.getInstance().getSLSServiceManager().setPrivacySettings(new PrivacySettingsResult(arrayList))) {
                        return AsyncActionStatus.FAIL;
                    }
                }
                return !ServiceManagerFactory.getInstance().getSLSServiceManager().updateThirdPartyToken(LinkedAccountType.Facebook, FacebookManager.getInstance().getTokenString()) ? AsyncActionStatus.FAIL : !ServiceManagerFactory.getInstance().getSLSServiceManager().setFriendFinderOptInStatus(LinkedAccountType.Facebook, OptInStatus.OptedIn) ? AsyncActionStatus.FAIL : AsyncActionStatus.SUCCESS;
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
            LinkFacebookAccountViewModel.this.onLinkAccountAsyncTaskCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
        }
    }

    public LinkFacebookAccountViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
    }

    private void cancelActiveTasks() {
        if (this.linkAccountAsyncTask != null) {
            this.linkAccountAsyncTask.cancel();
        }
    }

    private void onLinkAccountAsyncTaskCompleted(AsyncActionStatus asyncActionStatus) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                FriendFinderModel.getInstance().loadAsync(true);
                try {
                    ActivityParameters activityParameters = new ActivityParameters();
                    activityParameters.putFriendFinderType(FriendFinderType.FACEBOOK);
                    UTCFriendFinder.trackFacebookLoginSuccessful(((FriendFinderLinkScreen) getScreen()).getActivityName());
                    NavigationManager.getInstance().PopScreensAndReplace(1, FriendFinderSuggestionsScreen.class, false, true, false, activityParameters);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                FacebookManager.getInstance().resetFacebookToken(true);
                showError(R.string.Service_ErrorText);
                NavigationManager.getInstance().OnBackButtonPressed();
                return;
            default:
                return;
        }
    }

    public boolean isBusy() {
        return true;
    }

    public void load(boolean z) {
        cancelActiveTasks();
        this.linkAccountAsyncTask = new LinkFacebookAccountAsyncTask();
        this.linkAccountAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.FACEBOOK);
        return super.onBackButtonPressed();
    }

    public void onRehydrate() {
    }

    public void onStart() {
        super.onStart();
        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
        cancelActiveTasks();
    }
}
