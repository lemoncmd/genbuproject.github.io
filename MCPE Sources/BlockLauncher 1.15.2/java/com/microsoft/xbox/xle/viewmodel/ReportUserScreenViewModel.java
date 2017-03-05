package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ReportUserScreenViewModel extends ViewModelBase {
    private FeedbackType[] feedbackReasons;
    private boolean isSubmittingReport;
    private ProfileModel model;
    private FeedbackType selectedReason;
    private SubmitReportAsyncTask submitReportAsyncTask;

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

    private class SubmitReportAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private FeedbackType feedbackType;
        private ProfileModel model;
        private String textReason;

        private SubmitReportAsyncTask(ProfileModel profileModel, FeedbackType feedbackType, String str) {
            this.model = profileModel;
            this.feedbackType = feedbackType;
            this.textReason = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            return this.model.submitFeedbackForUser(this.forceLoad, this.feedbackType, this.textReason).getStatus();
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ReportUserScreenViewModel.this.onSubmitReportCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ReportUserScreenViewModel.this.onSubmitReportCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ReportUserScreenViewModel.this.isSubmittingReport = true;
            ReportUserScreenViewModel.this.updateAdapter();
        }
    }

    public ReportUserScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        String selectedProfile = NavigationManager.getInstance().getActivityParameters().getSelectedProfile();
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(selectedProfile));
        if (JavaUtil.isNullOrEmpty(selectedProfile)) {
            popScreenWithXuidError();
        }
        this.model = ProfileModel.getProfileModel(selectedProfile);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(this.model.getGamerTag()));
        this.adapter = new ReportUserScreenAdapter(this);
        FeedbackType[] feedbackTypeArr = new FeedbackType[7];
        feedbackTypeArr[0] = FeedbackType.UserContentPersonalInfo;
        feedbackTypeArr[1] = FeedbackType.FairPlayCheater;
        feedbackTypeArr[2] = JavaUtil.isNullOrEmpty(this.model.getRealName()) ? FeedbackType.UserContentGamertag : FeedbackType.UserContentRealName;
        feedbackTypeArr[3] = FeedbackType.UserContentGamerpic;
        feedbackTypeArr[4] = FeedbackType.FairPlayQuitter;
        feedbackTypeArr[5] = FeedbackType.FairplayUnsporting;
        feedbackTypeArr[6] = FeedbackType.CommsAbusiveVoice;
        this.feedbackReasons = feedbackTypeArr;
    }

    private void onSubmitReportCompleted(AsyncActionStatus asyncActionStatus) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                DialogManager.getInstance().showToast(R.string.ProfileCard_Report_SuccessSubtext);
                onBackButtonPressed();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.ProfileCard_Report_Error);
                return;
            default:
                return;
        }
    }

    private void popScreenWithXuidError() {
        try {
            showError(R.string.Service_ErrorText);
            NavigationManager.getInstance().PopScreen();
        } catch (XLEException e) {
        }
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public FeedbackType getReason() {
        return this.selectedReason;
    }

    public ArrayList<String> getReasonTitles() {
        ArrayList<String> arrayList = new ArrayList(this.feedbackReasons.length);
        arrayList.add(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_SelectReason));
        for (FeedbackType title : this.feedbackReasons) {
            arrayList.add(title.getTitle());
        }
        return arrayList;
    }

    public String getTitle() {
        return String.format(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_InfoString_Android), new Object[]{this.model.getGamerTag()});
    }

    public String getXUID() {
        return this.model.getXuid();
    }

    public boolean isBusy() {
        return this.isSubmittingReport;
    }

    public void load(boolean z) {
    }

    public boolean onBackButtonPressed() {
        UTCPageView.removePage();
        try {
            NavigationManager.getInstance().PopScreensAndReplace(1, null, false, false, false, NavigationManager.getInstance().getActivityParameters());
            return true;
        } catch (XLEException e) {
            return false;
        }
    }

    public void onRehydrate() {
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
        if (this.submitReportAsyncTask != null) {
            this.submitReportAsyncTask.cancel();
        }
    }

    public void setReason(int i) {
        Object obj = (i == 0 || i - 1 >= this.feedbackReasons.length) ? null : 1;
        this.selectedReason = obj != null ? this.feedbackReasons[i - 1] : null;
        updateAdapter();
    }

    public void submitReport(String str) {
        if (this.submitReportAsyncTask != null) {
            this.submitReportAsyncTask.cancel();
        }
        if (this.selectedReason != null) {
            this.submitReportAsyncTask = new SubmitReportAsyncTask(this.model, this.selectedReason, str);
            this.submitReportAsyncTask.load(true);
        }
    }

    public boolean validReasonSelected() {
        return this.selectedReason != null;
    }
}
