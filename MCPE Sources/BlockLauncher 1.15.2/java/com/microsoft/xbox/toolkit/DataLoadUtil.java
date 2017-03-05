package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;

public class DataLoadUtil {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> com.microsoft.xbox.toolkit.AsyncResult<T> Load(boolean r9, long r10, java.util.Date r12, com.microsoft.xbox.toolkit.SingleEntryLoadingStatus r13, final com.microsoft.xbox.toolkit.network.IDataLoaderRunnable<T> r14) {
        /*
        r2 = 0;
        com.microsoft.xbox.toolkit.XLEAssert.assertNotNull(r13);
        com.microsoft.xbox.toolkit.XLEAssert.assertNotNull(r14);
        com.microsoft.xbox.toolkit.XLEAssert.assertIsNotUIThread();
        r0 = r13.waitForNotLoading();
        r1 = r0.waited;
        if (r1 != 0) goto L_0x0079;
    L_0x0012:
        r0 = com.microsoft.xbox.xle.app.XLEUtil.shouldRefresh(r12, r10);
        if (r0 != 0) goto L_0x001a;
    L_0x0018:
        if (r9 == 0) goto L_0x006f;
    L_0x001a:
        r0 = new com.microsoft.xbox.toolkit.DataLoadUtil$2;
        r0.<init>(r14);
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadSend(r0);
        r3 = r14.getShouldRetryCountOnTokenError();
        r0 = 0;
        r1 = r0;
        r0 = r2;
    L_0x0029:
        if (r1 > r3) goto L_0x0059;
    L_0x002b:
        r4 = r14.buildData();	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        r0 = 0;
        r5 = com.microsoft.xbox.toolkit.AsyncActionStatus.SUCCESS;	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        postExecute(r4, r14, r0, r5);	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        r13.setSuccess();	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        r0 = new com.microsoft.xbox.toolkit.AsyncResult;	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        r5 = 0;
        r6 = com.microsoft.xbox.toolkit.AsyncActionStatus.SUCCESS;	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
        r0.<init>(r4, r14, r5, r6);	 Catch:{ XLEException -> 0x0041, Exception -> 0x0063 }
    L_0x0040:
        return r0;
    L_0x0041:
        r0 = move-exception;
        r4 = r0.getErrorCode();
        r6 = 1020; // 0x3fc float:1.43E-42 double:5.04E-321;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x004f;
    L_0x004c:
        r1 = r1 + 1;
        goto L_0x0029;
    L_0x004f:
        r4 = r0.getErrorCode();
        r6 = 1005; // 0x3ed float:1.408E-42 double:4.965E-321;
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r1 != 0) goto L_0x0059;
    L_0x0059:
        r13.setFailed(r0);
        r1 = com.microsoft.xbox.toolkit.AsyncActionStatus.FAIL;
        r0 = safeReturnResult(r2, r14, r0, r1);
        goto L_0x0040;
    L_0x0063:
        r0 = move-exception;
        r1 = r0;
        r0 = new com.microsoft.xbox.toolkit.XLEException;
        r4 = r14.getDefaultErrorCode();
        r0.<init>(r4, r1);
        goto L_0x0059;
    L_0x006f:
        r13.setSuccess();
        r0 = com.microsoft.xbox.toolkit.AsyncActionStatus.NO_CHANGE;
        r0 = safeReturnResult(r2, r14, r2, r0);
        goto L_0x0040;
    L_0x0079:
        r0 = r0.error;
        if (r0 != 0) goto L_0x0084;
    L_0x007d:
        r0 = com.microsoft.xbox.toolkit.AsyncActionStatus.NO_OP_SUCCESS;
        r0 = safeReturnResult(r2, r14, r2, r0);
        goto L_0x0040;
    L_0x0084:
        r1 = com.microsoft.xbox.toolkit.AsyncActionStatus.NO_OP_FAIL;
        r0 = safeReturnResult(r2, r14, r0, r1);
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.DataLoadUtil.Load(boolean, long, java.util.Date, com.microsoft.xbox.toolkit.SingleEntryLoadingStatus, com.microsoft.xbox.toolkit.network.IDataLoaderRunnable):com.microsoft.xbox.toolkit.AsyncResult<T>");
    }

    public static <T> NetworkAsyncTask StartLoadFromUI(boolean z, long j, Date date, SingleEntryLoadingStatus singleEntryLoadingStatus, IDataLoaderRunnable<T> iDataLoaderRunnable) {
        final long j2 = j;
        final Date date2 = date;
        final SingleEntryLoadingStatus singleEntryLoadingStatus2 = singleEntryLoadingStatus;
        final IDataLoaderRunnable<T> iDataLoaderRunnable2 = iDataLoaderRunnable;
        NetworkAsyncTask anonymousClass1 = new NetworkAsyncTask<T>() {
            protected boolean checkShouldExecute() {
                return this.forceLoad;
            }

            protected T loadDataInBackground() {
                return DataLoadUtil.Load(this.forceLoad, j2, date2, singleEntryLoadingStatus2, iDataLoaderRunnable2).getResult();
            }

            protected T onError() {
                return null;
            }

            protected void onNoAction() {
            }

            protected void onPostExecute(T t) {
            }

            protected void onPreExecute() {
            }
        };
        anonymousClass1.execute();
        return anonymousClass1;
    }

    private static <T> void postExecute(final T t, final IDataLoaderRunnable<T> iDataLoaderRunnable, final XLEException xLEException, final AsyncActionStatus asyncActionStatus) {
        ThreadManager.UIThreadSend(new Runnable() {
            public void run() {
                iDataLoaderRunnable.onPostExcute(new AsyncResult(t, iDataLoaderRunnable, xLEException, asyncActionStatus));
            }
        });
    }

    private static <T> AsyncResult<T> safeReturnResult(T t, IDataLoaderRunnable<T> iDataLoaderRunnable, XLEException xLEException, AsyncActionStatus asyncActionStatus) {
        postExecute(t, iDataLoaderRunnable, xLEException, asyncActionStatus);
        return new AsyncResult(t, iDataLoaderRunnable, xLEException, asyncActionStatus);
    }
}
