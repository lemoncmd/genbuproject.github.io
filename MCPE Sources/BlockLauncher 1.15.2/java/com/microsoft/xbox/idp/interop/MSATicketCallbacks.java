package com.microsoft.xbox.idp.interop;

import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.idp.jobs.MSAJob.Callbacks;
import net.hockeyapp.android.BuildConfig;

public class MSATicketCallbacks implements Callbacks {
    private String m_ticket = new String(BuildConfig.FLAVOR);

    public String getTicket() {
        return this.m_ticket;
    }

    public void onAccountAcquired(MSAJob mSAJob, UserAccount userAccount) {
    }

    public void onFailure(MSAJob mSAJob, Exception exception) {
        synchronized (mSAJob) {
            mSAJob.notifyAll();
        }
    }

    public void onSignedOut(MSAJob mSAJob) {
        synchronized (mSAJob) {
            mSAJob.notifyAll();
        }
    }

    public void onTicketAcquired(MSAJob mSAJob, Ticket ticket) {
        synchronized (mSAJob) {
            this.m_ticket = ticket.getValue();
            mSAJob.notifyAll();
        }
    }

    public void onUiNeeded(MSAJob mSAJob) {
        synchronized (mSAJob) {
            mSAJob.notifyAll();
        }
    }

    public void onUserCancel(MSAJob mSAJob) {
        synchronized (mSAJob) {
            mSAJob.notifyAll();
        }
    }
}
