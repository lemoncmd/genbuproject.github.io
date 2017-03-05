package com.microsoft.xbox.service.model.friendfinder;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;

public class FriendFinderModel extends ModelBase<FriendsFinderStateResult> {
    private static FriendFinderModel instance = new FriendFinderModel();
    private LoadFailedCallback callback;
    private FriendsFinderStateResult result;

    private class GetPeopleHubFriendFinderStateResultRunner extends IDataLoaderRunnable<FriendsFinderStateResult> {
        public FriendsFinderStateResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getPeopleHubFriendFinderState();
        }

        public long getDefaultErrorCode() {
            return 11;
        }

        public void onPostExcute(AsyncResult<FriendsFinderStateResult> asyncResult) {
            FriendFinderModel.this.updateWithNewData(asyncResult);
        }

        public void onPreExecute() {
            FriendFinderModel.this.isLoading = true;
        }
    }

    public interface LoadFailedCallback {
        void onFriendFinderLoadFailed();
    }

    public static FriendFinderModel getInstance() {
        return instance;
    }

    public FriendsFinderStateResult getResult() {
        return this.result;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void loadAsync(boolean z) {
        loadInternal(z, UpdateType.FriendFinderFacebook, new GetPeopleHubFriendFinderStateResultRunner());
    }

    public void loadAsync(boolean z, LoadFailedCallback loadFailedCallback) {
        this.callback = loadFailedCallback;
        loadAsync(z);
    }

    public boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    public void updateWithNewData(AsyncResult<FriendsFinderStateResult> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        super.updateWithNewData(asyncResult);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult() != null) {
            this.result = (FriendsFinderStateResult) asyncResult.getResult();
            FacebookManager.getInstance().setFacebookFriendFinderState(this.result);
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.FriendFinderFacebook, true), this, asyncResult.getException()));
        } else if (this.callback != null) {
            this.callback.onFriendFinderLoadFailed();
            this.callback = null;
        }
    }
}
