package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Intent;
import android.net.Uri;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.Contact;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderPhoneInviteScreenAdapater;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class FriendFinderPhoneInviteScreenViewModel extends ViewModelBase {
    private Comparator<Contact> contactComparator = new Comparator<Contact>() {
        public int compare(Contact contact, Contact contact2) {
            return contact.displayName.compareTo(contact2.displayName);
        }
    };
    private ArrayList<Contact> contactsList = new ArrayList();
    private boolean isUploadingContacts;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    private class UploadContactsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private UploadContactsAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            return false;
        }

        protected AsyncActionStatus loadDataInBackground() {
            return AsyncActionStatus.SUCCESS;
        }

        protected AsyncActionStatus onError() {
            return null;
        }

        protected void onNoAction() {
            FriendFinderPhoneInviteScreenViewModel.this.onUploadContactsTaskCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderPhoneInviteScreenViewModel.this.onUploadContactsTaskCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderPhoneInviteScreenViewModel.this.isUploadingContacts = true;
        }
    }

    public FriendFinderPhoneInviteScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        XLEAssert.fail("This isn't supported yet.");
        this.adapter = new FriendFinderPhoneInviteScreenAdapater(this);
    }

    private void cancelActiveTasks() {
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
    }

    private void onUploadContactsTaskCompleted(AsyncActionStatus asyncActionStatus) {
        this.isUploadingContacts = false;
        this.contactsList = PhoneContactInfo.getInstance().getContacts();
        Collections.sort(this.contactsList, this.contactComparator);
        updateAdapter();
    }

    public void addContacts(ArrayList<Integer> arrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            if (stringBuffer.length() > 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append((String) ((Contact) this.contactsList.get(num.intValue())).phoneNumbers.get(0));
        }
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setData(Uri.parse("smsto:" + Uri.encode(stringBuffer.toString())));
        intent.putExtra("sms_body", XboxTcuiSdk.getResources().getString(R.string.FriendFinder_PhoneInviteFriends_Message));
        intent.putExtra("address", stringBuffer.toString());
        if (!XboxTcuiSdk.getActivity().getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            XboxTcuiSdk.getActivity().startActivity(intent);
        }
        ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putFriendFinderDone(true);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderHomeScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    public ArrayList<Contact> getContacts() {
        return this.contactsList;
    }

    public boolean isBusy() {
        return this.isUploadingContacts;
    }

    public void load(boolean z) {
        cancelActiveTasks();
        if (PhoneContactInfo.getInstance().isXboxContactsUpdated()) {
            this.contactsList = PhoneContactInfo.getInstance().getContacts();
            Collections.sort(this.contactsList, this.contactComparator);
            return;
        }
        this.uploadContactsAsyncTask = new UploadContactsAsyncTask();
        this.uploadContactsAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.PHONE);
        return super.onBackButtonPressed();
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderPhoneInviteScreenAdapater(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
        cancelActiveTasks();
    }
}
