package com.microsoft.xbox.idp.interop;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.interop.Interop.ErrorCallback;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import org.mozilla.javascript.regexp.NativeRegExp;

public final class XsapiUser {
    private static final String TAG = XsapiUser.class.getSimpleName();
    private static XsapiUser instance;
    private static final Object instanceLock = new Object();
    private final long id = create();
    private final UserImpl userImpl = new UserImpl(getUserImpl(this.id));

    private interface SignInSilentlyCallbackInternal extends ErrorCallback {
        void onSuccess(int i);
    }

    private interface LongCallback extends ErrorCallback {
        void onSuccess(long j);
    }

    public interface TokenAndSignatureCallback extends ErrorCallback {
        void onSuccess(TokenAndSignature tokenAndSignature);
    }

    private static class TokenAndSignatureCallbackWithResult implements TokenAndSignatureCallback {
        private int errorCode;
        private String errorMessage;
        private int httpStatusCode;
        private TokenAndSignature tokenAndSignature;

        private TokenAndSignatureCallbackWithResult() {
        }

        public int getErrorCode() {
            return this.errorCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public int getHttpStatusCode() {
            return this.httpStatusCode;
        }

        public TokenAndSignature getTokenAndSignature() {
            return this.tokenAndSignature;
        }

        public void onError(int i, int i2, String str) {
            this.httpStatusCode = i;
            this.errorCode = i2;
            this.errorMessage = str;
        }

        public void onSuccess(TokenAndSignature tokenAndSignature) {
            this.tokenAndSignature = tokenAndSignature;
        }
    }

    public interface VoidCallback extends ErrorCallback {
        void onSuccess();
    }

    public interface FinishSignInCallback extends VoidCallback {
    }

    public interface SignInSilentlyCallback extends ErrorCallback {
        void onSuccess(SignInStatus signInStatus);
    }

    public enum SignInStatus {
        SUCCESS(0),
        USER_INTERACTION_REQUIRED(1),
        USER_CANCEL(3);
        
        public final int id;

        private SignInStatus(int i) {
            this.id = i;
        }

        public static SignInStatus fromId(int i) {
            switch (i) {
                case NativeRegExp.TEST /*0*/:
                    return SUCCESS;
                case NativeRegExp.MATCH /*1*/:
                    return USER_INTERACTION_REQUIRED;
                default:
                    return USER_CANCEL;
            }
        }
    }

    public interface SignOutCallback extends VoidCallback {
    }

    public interface StartSignInCallback extends VoidCallback {
    }

    public static class UserImpl implements Parcelable {
        public static final Creator<UserImpl> CREATOR = new Creator<UserImpl>() {
            public UserImpl createFromParcel(Parcel parcel) {
                return new UserImpl(parcel);
            }

            public UserImpl[] newArray(int i) {
                return new UserImpl[i];
            }
        };
        private final long id;

        public UserImpl(long j) {
            this.id = j;
        }

        protected UserImpl(Parcel parcel) {
            this.id = parcel.readLong();
        }

        private long getId() {
            return this.id;
        }

        public int describeContents() {
            return 0;
        }

        public long getUserImplPtr() {
            return this.id;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.id);
        }
    }

    private XsapiUser() {
    }

    public static int[] convertPrivileges(String str) {
        LinkedList linkedList = new LinkedList();
        for (String str2 : str.split(" ")) {
            try {
                linkedList.add(Integer.valueOf(Integer.parseInt(str2)));
            } catch (NumberFormatException e) {
                Log.d(TAG, "Cannot convert " + str2 + " to integer");
            }
        }
        int[] iArr = new int[linkedList.size()];
        Iterator it = linkedList.iterator();
        int i = -1;
        while (it.hasNext()) {
            i++;
            iArr[i] = ((Integer) it.next()).intValue();
        }
        return iArr;
    }

    private static native long create();

    private static native void delete(long j);

    private static native void finishSignIn(long j, FinishSignInCallback finishSignInCallback, int i, String str);

    public static XsapiUser getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new XsapiUser();
                }
            }
        }
        return instance;
    }

    private static native String getPrivileges(long j);

    private static native void getTokenAndSignature(long j, String str, String str2, String str3, String str4, LongCallback longCallback);

    private static native long getUserImpl(long j);

    private static native String getXuid(long j);

    private static native boolean isProd(long j);

    private static native boolean isSignedIn(long j);

    private static native void signInSilently(long j, SignInSilentlyCallbackInternal signInSilentlyCallbackInternal);

    private static native void signOut(long j, SignOutCallback signOutCallback);

    private static native void startSignIn(long j, StartSignInCallback startSignInCallback);

    protected void finalize() throws Throwable {
        delete(this.id);
        super.finalize();
    }

    public void finishSignIn(FinishSignInCallback finishSignInCallback, AuthFlowScreenStatus authFlowScreenStatus, String str) {
        finishSignIn(this.id, finishSignInCallback, authFlowScreenStatus.getId(), str);
    }

    public int[] getPrivileges() {
        return convertPrivileges(getPrivileges(this.id));
    }

    public void getTokenAndSignature(String str, String str2, String str3, TokenAndSignatureCallback tokenAndSignatureCallback) {
        getTokenAndSignature(str, str2, str3, null, tokenAndSignatureCallback);
    }

    public void getTokenAndSignature(String str, String str2, String str3, String str4, final TokenAndSignatureCallback tokenAndSignatureCallback) {
        getTokenAndSignature(this.id, str, str2, str3, str4, new LongCallback() {
            public void onError(int i, int i2, String str) {
                tokenAndSignatureCallback.onError(i, i2, str);
            }

            public void onSuccess(long j) {
                tokenAndSignatureCallback.onSuccess(new TokenAndSignature(j));
            }
        });
    }

    public TokenAndSignature getTokenAndSignatureSync(String str, String str2, String str3) {
        return getTokenAndSignatureSync(str, str2, str3, null);
    }

    public TokenAndSignature getTokenAndSignatureSync(String str, String str2, String str3, String str4) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Object anonymousClass3 = new TokenAndSignatureCallbackWithResult() {
            public void onError(int i, int i2, String str) {
                super.onError(i, i2, str);
                countDownLatch.countDown();
            }

            public void onSuccess(TokenAndSignature tokenAndSignature) {
                super.onSuccess(tokenAndSignature);
                countDownLatch.countDown();
            }
        };
        getTokenAndSignature(str, str2, str3, str4, anonymousClass3);
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return anonymousClass3.getTokenAndSignature();
    }

    public UserImpl getUserImpl() {
        return this.userImpl;
    }

    public String getXuid() {
        return getXuid(this.id);
    }

    public boolean isProd() {
        return isProd(this.id);
    }

    public boolean isSignedIn() {
        return isSignedIn(this.id);
    }

    public void signInSilently(final SignInSilentlyCallback signInSilentlyCallback) {
        signInSilently(this.id, new SignInSilentlyCallbackInternal() {
            public void onError(int i, int i2, String str) {
                signInSilentlyCallback.onError(i, i2, str);
            }

            public void onSuccess(int i) {
                signInSilentlyCallback.onSuccess(SignInStatus.fromId(i));
            }
        });
    }

    public void signOut(SignOutCallback signOutCallback) {
        signOut(this.id, signOutCallback);
    }

    public void startSignIn(StartSignInCallback startSignInCallback) {
        startSignIn(this.id, startSignInCallback);
    }
}
