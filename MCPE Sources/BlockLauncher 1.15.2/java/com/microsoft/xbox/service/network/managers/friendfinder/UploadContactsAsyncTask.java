package com.microsoft.xbox.service.network.managers.friendfinder;

import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileResponse;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsResponse;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.Contact;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import java.util.ArrayList;

public class UploadContactsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
    private static final int MAX_UPLOAD_NUM_PER_REQUEST = 100;
    private UploadContactsCompleted callback;
    private String phoneNumber;

    public interface UploadContactsCompleted {
        void onResult(AsyncActionStatus asyncActionStatus);
    }

    public UploadContactsAsyncTask(UploadContactsCompleted uploadContactsCompleted) {
        this.callback = uploadContactsCompleted;
        if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getProfileNumber())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getProfileNumber();
        } else if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getUserEnteredNumber())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getUserEnteredNumber();
        } else if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getPhoneNumberFromSim())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getPhoneNumberFromSim();
        }
    }

    private boolean batchUploadContacts(ArrayList<Contact> arrayList) throws XLEException {
        XLEAssert.assertNotNull(arrayList);
        XLEAssert.assertTrue(arrayList.size() > MAX_UPLOAD_NUM_PER_REQUEST);
        boolean z = true;
        int i = 0;
        while (z) {
            int i2 = i + MAX_UPLOAD_NUM_PER_REQUEST;
            if (i2 >= arrayList.size()) {
                i2 = arrayList.size();
                z = false;
            }
            if (!uploadContacts(new ArrayList(arrayList.subList(i, i2)))) {
                return false;
            }
            i = i2;
        }
        return true;
    }

    private boolean uploadContacts(ArrayList<Contact> arrayList) throws XLEException {
        XLEAssert.assertNotNull(arrayList);
        boolean z = arrayList.size() > 0 && arrayList.size() <= MAX_UPLOAD_NUM_PER_REQUEST;
        XLEAssert.assertTrue(z);
        UploadPhoneContactsResponse updatePhoneContacts = ServiceManagerFactory.getInstance().getSLSServiceManager().updatePhoneContacts(new UploadPhoneContactsRequest(arrayList, this.phoneNumber));
        if (updatePhoneContacts == null || updatePhoneContacts.isErrorResponse) {
            return false;
        }
        PhoneContactInfo.getInstance().updateXboxContacts(updatePhoneContacts.getXboxPhoneContacts());
        return true;
    }

    private boolean uploadContactsSucceeded() throws XLEException {
        ArrayList contacts = PhoneContactInfo.getInstance().getContacts();
        return contacts != null ? contacts.size() == 0 ? true : contacts.size() > MAX_UPLOAD_NUM_PER_REQUEST ? batchUploadContacts(contacts) : uploadContacts(contacts) : false;
    }

    protected boolean checkShouldExecute() {
        return true;
    }

    protected AsyncActionStatus loadDataInBackground() {
        try {
            if (JavaUtil.isNullOrEmpty(this.phoneNumber)) {
                ShortCircuitProfileResponse myShortCircuitProfile = ServiceManagerFactory.getInstance().getSLSServiceManager().getMyShortCircuitProfile();
                if (myShortCircuitProfile == null) {
                    return AsyncActionStatus.FAIL;
                }
                String xboxNumber = myShortCircuitProfile.getXboxNumber();
                if (JavaUtil.isNullOrEmpty(xboxNumber)) {
                    return AsyncActionStatus.FAIL;
                }
                PhoneContactInfo.getInstance().setProfileNumber(xboxNumber);
                this.phoneNumber = xboxNumber;
            }
            if (uploadContactsSucceeded()) {
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
        if (this.callback != null) {
            this.callback.onResult(asyncActionStatus);
        }
    }

    protected void onPreExecute() {
    }
}
