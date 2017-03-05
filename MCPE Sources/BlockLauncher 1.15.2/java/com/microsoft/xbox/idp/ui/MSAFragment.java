package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.jobs.JobSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;

public class MSAFragment extends BaseFragment implements com.microsoft.xbox.idp.jobs.MSAJob.Callbacks {
    static final /* synthetic */ boolean $assertionsDisabled = (!MSAFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    public static final String ARG_SECURITY_POLICY = "ARG_SECURITY_POLICY";
    public static final String ARG_SECURITY_SCOPE = "ARG_SECURITY_SCOPE";
    private static final String KEY_STATE = "KEY_STATE";
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status, String str, Ticket ticket) {
        }
    };
    private static final String TAG = MSAFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private JobSignIn currentJob;
    private State state;

    public interface Callbacks {
        void onComplete(Status status, String str, Ticket ticket);
    }

    private static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel parcel) {
                return new State(parcel);
            }

            public State[] newArray(int i) {
                return new State[i];
            }
        };
        public String cid;

        protected State(Parcel parcel) {
            this.cid = parcel.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.cid);
        }
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PROVIDER_ERROR
    }

    public void onAccountAcquired(MSAJob mSAJob, UserAccount userAccount) {
        this.state.cid = userAccount.getCid();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.d(TAG, "requestCode: " + i + ", resultCode: " + i2 + ", extras: " + (intent == null ? null : intent.getExtras()));
        if (this.currentJob != null) {
            this.currentJob.onActivityResult(i, i2, intent);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (bundle != null) {
            this.state = (State) bundle.getParcelable(KEY_STATE);
            this.currentJob = new JobSignIn(getActivity(), this, arguments.getString(ARG_SECURITY_SCOPE), arguments.getString(ARG_SECURITY_POLICY));
        } else if (arguments == null) {
            Log.e(TAG, "Intent has no extras");
            this.callbacks.onComplete(Status.ERROR, null, null);
        } else {
            String string = arguments.getString(ARG_SECURITY_SCOPE);
            if (string == null) {
                Log.e(TAG, "No security scope");
                this.callbacks.onComplete(Status.ERROR, null, null);
                return;
            }
            String string2 = arguments.getString(ARG_SECURITY_POLICY);
            if (string2 == null) {
                Log.e(TAG, "No security policy");
                this.callbacks.onComplete(Status.ERROR, null, null);
                return;
            }
            this.state = new State();
            this.currentJob = new JobSignIn(getActivity(), this, string, string2).start();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_busy, viewGroup, $assertionsDisabled);
    }

    public void onDetach() {
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onFailure(MSAJob mSAJob, Exception exception) {
        Log.d(TAG, "There was a problem acquiring an account: " + exception);
        this.callbacks.onComplete(exception instanceof NetworkException ? Status.PROVIDER_ERROR : Status.ERROR, null, null);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }

    public void onSignedOut(MSAJob mSAJob) {
        Log.d(TAG, "Signed out during sing in - should not be here.");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }

    public void onTicketAcquired(MSAJob mSAJob, Ticket ticket) {
        this.callbacks.onComplete(Status.SUCCESS, this.state.cid, ticket);
    }

    public void onUiNeeded(MSAJob mSAJob) {
        Log.e(TAG, "Must show UI to acquire an account. Should not be here");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }

    public void onUserCancel(MSAJob mSAJob) {
        Log.d(TAG, "The user cancelled the UI to acquire a ticket.");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }
}
