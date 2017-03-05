package com.microsoft.onlineid.analytics;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsException;
import java.util.ArrayList;
import java.util.Collection;

public class MsaExceptionParser extends StandardExceptionParser {
    public MsaExceptionParser(Context context, Collection<String> collection) {
        super(context, collection);
    }

    private String getStackLocationDescription(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        int lastIndexOf = className.lastIndexOf(46);
        if (lastIndexOf >= 0) {
            className = className.substring(lastIndexOf + 1);
        }
        className = className + ":" + stackTraceElement.getMethodName();
        if (stackTraceElement.getLineNumber() > 0) {
            className = className + ":" + stackTraceElement.getLineNumber();
        }
        return "(@" + className + ")";
    }

    protected String getDescription(Throwable th, StackTraceElement stackTraceElement, String str) {
        Iterable arrayList = new ArrayList();
        arrayList.add(th.getClass().getSimpleName());
        if (th instanceof StsException) {
            StsError error = ((StsException) th).getError();
            if (error != null) {
                arrayList.add("[" + error.getOriginalErrorMessage() + "]");
            }
        }
        arrayList.add(getStackLocationDescription(stackTraceElement));
        arrayList.add("{" + str + "}");
        return TextUtils.join(" ", arrayList);
    }
}
