package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Intent;
import com.microsoft.onlineid.exception.InternalException;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public abstract class ActivityResultHandler {
    public void onActivityResult(int i, Intent intent) {
        ApiResult apiResult = new ApiResult(intent != null ? intent.getExtras() : null);
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

    protected abstract void onFailure(Exception exception);

    protected abstract void onSuccess(ApiResult apiResult);

    protected abstract void onUINeeded(PendingIntent pendingIntent);

    protected void onUnknownResult(ApiResult apiResult, int i) {
        Assertion.check(false, "Unknown result code: " + i);
        onFailure(new InternalException("Unknown result code: " + i));
    }

    protected abstract void onUserCancel();
}
