package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderSuggestionModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.sls.FavoriteListRequest;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPeopleSummary;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPersonSummary;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.Iterator;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderSuggestionsScreenViewModel extends ViewModelBase {
    private AddSuggestionsAsyncTask addSuggestionsAsyncTask;
    private ArrayList<PeopleHubPersonSummary> foundPeople;
    private FriendFinderType friendFinderType;
    private GetPeopleHubRecommendationsAsyncTask getPeopleHubRecommendationsAsyncTask;
    private boolean isAddingSuggestions;
    private boolean isLoadingRecommendations;
    private ProfileModel meProfileModel;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType = new int[FriendFinderType.values().length];
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
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.PHONE.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.FACEBOOK.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private class AddSuggestionsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> xuids;

        public AddSuggestionsAsyncTask(ArrayList<String> arrayList) {
            this.xuids = arrayList;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.xuids.size() > 0;
        }

        protected AsyncActionStatus loadDataInBackground() {
            try {
                return ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(this.xuids))).getAddFollowingRequestStatus() ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            FriendFinderSuggestionsScreenViewModel.this.onAddSuggestionsCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderSuggestionsScreenViewModel.this.isAddingSuggestions = false;
            FriendFinderSuggestionsScreenViewModel.this.onAddSuggestionsCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            FriendFinderSuggestionsScreenViewModel.this.isAddingSuggestions = true;
            FriendFinderSuggestionsScreenViewModel.this.updateAdapter();
        }
    }

    private class GetPeopleHubRecommendationsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private GetPeopleHubRecommendationsAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            return false;
        }

        protected AsyncActionStatus loadDataInBackground() {
            FriendFinderSuggestionsScreenViewModel.this.meProfileModel.loadSync(true);
            return FriendFinderSuggestionsScreenViewModel.this.meProfileModel.loadPeopleHubRecommendations(true).getStatus();
        }

        protected AsyncActionStatus onError() {
            return null;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderSuggestionsScreenViewModel.this.onGetPeopleHubRecommendationsAsyncTaskCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderSuggestionsScreenViewModel.this.isLoadingRecommendations = true;
        }
    }

    public FriendFinderSuggestionsScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.foundPeople = new ArrayList(0);
        this.adapter = new FriendFinderSuggestionsScreenAdapter(this);
    }

    private void cancelActiveTasks() {
        if (this.getPeopleHubRecommendationsAsyncTask != null) {
            this.getPeopleHubRecommendationsAsyncTask.cancel();
        }
        if (this.addSuggestionsAsyncTask != null) {
            this.addSuggestionsAsyncTask.cancel();
        }
    }

    private String getNameOrGamertagAtIndex(int i) {
        String str = BuildConfig.FLAVOR;
        if (i >= this.foundPeople.size()) {
            return str;
        }
        PeopleHubPersonSummary peopleHubPersonSummary = (PeopleHubPersonSummary) this.foundPeople.get(i);
        if (!(peopleHubPersonSummary.recommendation == null || peopleHubPersonSummary.recommendation.Reasons == null || peopleHubPersonSummary.recommendation.Reasons.size() <= 0)) {
            str = (String) peopleHubPersonSummary.recommendation.Reasons.get(0);
        }
        return JavaUtil.isNullOrEmpty(str) ? peopleHubPersonSummary.gamertag : str;
    }

    private void navigateToFacebookInvite() {
        ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putFriendFinderType(FriendFinderType.FACEBOOK);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderInviteScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    private void navigateToInvite() {
        if (this.friendFinderType == FriendFinderType.FACEBOOK) {
            navigateToFacebookInvite();
        } else {
            navigateToPhoneInvite();
        }
    }

    private void navigateToPhoneInvite() {
        ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putFriendFinderDone(true);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderHomeScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    private void onAddSuggestionsCompleted(AsyncActionStatus asyncActionStatus) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                navigateToInvite();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                return;
            default:
                return;
        }
    }

    private void onGetPeopleHubRecommendationsAsyncTaskCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingRecommendations = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                updateFoundPeople();
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                break;
        }
        updateAdapter();
    }

    private void updateFoundPeople() {
        RecommendationType recommendationType = this.friendFinderType == FriendFinderType.FACEBOOK ? RecommendationType.FacebookFriend : RecommendationType.PhoneContact;
        PeopleHubPeopleSummary peopleHubRecommendationsRawData = this.meProfileModel.getPeopleHubRecommendationsRawData();
        this.foundPeople = new ArrayList();
        if (peopleHubRecommendationsRawData != null) {
            Iterator it = peopleHubRecommendationsRawData.people.iterator();
            while (it.hasNext()) {
                PeopleHubPersonSummary peopleHubPersonSummary = (PeopleHubPersonSummary) it.next();
                if (peopleHubPersonSummary.recommendation.getRecommendationType() == recommendationType) {
                    this.foundPeople.add(peopleHubPersonSummary);
                }
            }
        }
    }

    public void addSuggestions(ArrayList<Integer> arrayList) {
        cancelActiveTasks();
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            XLEAssert.assertTrue(num.intValue() < this.foundPeople.size());
            if (num.intValue() < this.foundPeople.size()) {
                arrayList2.add(((PeopleHubPersonSummary) this.foundPeople.get(num.intValue())).xuid);
            }
        }
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[this.friendFinderType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                UTCFriendFinder.trackPhoneContactsAddFriends(getScreen().getName(), (String[]) arrayList2.toArray(new String[arrayList2.size()]));
                break;
            case NativeRegExp.PREFIX /*2*/:
                UTCFriendFinder.trackAddFacebookFriend(getScreen().getName(), (String[]) arrayList2.toArray(new String[arrayList2.size()]));
                break;
        }
        this.addSuggestionsAsyncTask = new AddSuggestionsAsyncTask(arrayList2);
        this.addSuggestionsAsyncTask.load(true);
    }

    public String getSubtitle() {
        if (this.foundPeople.size() != 0) {
            return XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Found_Subtitle);
        }
        String string = XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Upsell_Description_NoFriends_LineOne);
        return this.friendFinderType == FriendFinderType.FACEBOOK ? string + "\n\n" + XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Upsell_Description_Default_LineTwo) : string;
    }

    public ArrayList<FriendFinderSuggestionModel> getSuggestions() {
        ArrayList<FriendFinderSuggestionModel> arrayList = new ArrayList(this.foundPeople.size());
        Iterator it = this.foundPeople.iterator();
        while (it.hasNext()) {
            arrayList.add(FriendFinderSuggestionModel.fromPeopleHubSummary((PeopleHubPersonSummary) it.next()));
        }
        return arrayList;
    }

    public String getTitle() {
        switch (this.foundPeople.size()) {
            case NativeRegExp.TEST /*0*/:
                return XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_NoFriends).toString();
            case NativeRegExp.MATCH /*1*/:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_OneFriend_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0)});
            case NativeRegExp.PREFIX /*2*/:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_TwoFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1)});
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_ThreeFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1), getNameOrGamertagAtIndex(2)});
            default:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_ManyFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1), Integer.valueOf(this.foundPeople.size() - 2)});
        }
    }

    public boolean isBusy() {
        return this.isLoadingRecommendations || this.isAddingSuggestions;
    }

    public void load(boolean z) {
        cancelActiveTasks();
        this.getPeopleHubRecommendationsAsyncTask = new GetPeopleHubRecommendationsAsyncTask();
        this.getPeopleHubRecommendationsAsyncTask.load(true);
    }

    public void navigateToSkip() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[this.friendFinderType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                UTCFriendFinder.trackPhoneContactsSkipAddFriends(getScreen().getName());
                break;
            case NativeRegExp.PREFIX /*2*/:
                UTCFriendFinder.trackAddFacebookFriendCancel(getScreen().getName());
                break;
        }
        navigateToInvite();
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), this.friendFinderType);
        return super.onBackButtonPressed();
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderSuggestionsScreenAdapter(this);
    }

    protected void onStartOverride() {
        this.friendFinderType = NavigationManager.getInstance().getActivityParameters().getFriendFinderType();
        XLEAssert.assertTrue(this.friendFinderType != FriendFinderType.UNKNOWN);
        this.meProfileModel = ProfileModel.getMeProfileModel();
        XLEAssert.assertNotNull(this.meProfileModel);
    }

    protected void onStopOverride() {
        cancelActiveTasks();
    }
}
