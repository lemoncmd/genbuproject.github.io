package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.microsoft.onlineid.exception.InternalException;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public abstract class ApiRequestResultReceiver extends ResultReceiver {
    public ApiRequestResultReceiver(Handler handler) {
        super(handler);
    }

    protected abstract void onFailure(Exception exception);

    protected void onReceiveResult(int i, Bundle bundle) {
        ApiResult apiResult = new ApiResult(bundle);
        switch (i) {
            case Token.ERROR /*-1*/:
                onSuccess(apiResult);
                return;
            case NativeRegExp.TEST /*0*/:
                onUserCancel();
                return;
            case NativeRegExp.MATCH /*1*/:
                onFailure(apiResult.getException());
                return;
            case NativeRegExp.PREFIX /*2*/:
                onUINeeded(apiResult.getUINeededIntent());
                return;
            default:
                onUnknownResult(apiResult, i);
                return;
        }
    }

    protected abstract void onSuccess(ApiResult apiResult);

    protected abstract void onUINeeded(PendingIntent pendingIntent);

    protected void onUnknownResult(ApiResult apiResult, int i) {
        Assertion.check(false, "Unknown result code: " + i);
        onFailure(new InternalException("Unknown result code: " + i));
    }

    protected abstract void onUserCancel();
}
