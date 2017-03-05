package com.microsoft.xbox.idp.jobs;

import android.content.Context;
import android.content.Intent;
import com.microsoft.onlineid.AccountManager;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.OnlineIdConfiguration.PreferredSignUpMemberNameType;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;

public abstract class MSAJob {
    protected final AccountManager accountManager;
    protected final Callbacks callbacks;

    public interface Callbacks {
        void onAccountAcquired(MSAJob mSAJob, UserAccount userAccount);

        void onFailure(MSAJob mSAJob, Exception exception);

        void onSignedOut(MSAJob mSAJob);

        void onTicketAcquired(MSAJob mSAJob, Ticket ticket);

        void onUiNeeded(MSAJob mSAJob);

        void onUserCancel(MSAJob mSAJob);
    }

    public enum Type {
        SILENT_SIGN_IN,
        SIGN_IN;

        public static Type fromOrdinal(int i) {
            Type[] values = values();
            return (i < 0 || values.length <= i) ? null : values[i];
        }
    }

    public MSAJob(Context context, Callbacks callbacks) {
        this.callbacks = callbacks;
        OnlineIdConfiguration onlineIdConfiguration = new OnlineIdConfiguration(PreferredSignUpMemberNameType.Email);
        onlineIdConfiguration.setCobrandingId("90011");
        this.accountManager = new AccountManager(context, onlineIdConfiguration);
    }

    public abstract Type getType();

    public boolean onActivityResult(int i, int i2, Intent intent) {
        return this.accountManager.onActivityResult(i, i2, intent);
    }

    public abstract MSAJob start();
}
