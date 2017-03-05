package com.microsoft.xbox.toolkit;

public enum AsyncActionStatus {
    SUCCESS,
    FAIL,
    NO_CHANGE,
    NO_OP_SUCCESS,
    NO_OP_FAIL;
    
    private static final AsyncActionStatus[][] MERGE_MATRIX = null;

    static {
        r0 = new AsyncActionStatus[5][];
        r0[0] = new AsyncActionStatus[]{SUCCESS, FAIL, SUCCESS, SUCCESS, FAIL};
        r0[1] = new AsyncActionStatus[]{FAIL, FAIL, FAIL, FAIL, FAIL};
        r0[2] = new AsyncActionStatus[]{SUCCESS, FAIL, NO_CHANGE, NO_OP_SUCCESS, NO_OP_FAIL};
        r0[3] = new AsyncActionStatus[]{SUCCESS, FAIL, NO_OP_SUCCESS, NO_OP_SUCCESS, NO_OP_FAIL};
        r0[4] = new AsyncActionStatus[]{FAIL, FAIL, NO_OP_FAIL, NO_OP_FAIL, NO_OP_FAIL};
        MERGE_MATRIX = r0;
    }

    public static boolean getIsFail(AsyncActionStatus asyncActionStatus) {
        return asyncActionStatus == FAIL || asyncActionStatus == NO_OP_FAIL;
    }

    public static AsyncActionStatus merge(AsyncActionStatus asyncActionStatus, AsyncActionStatus... asyncActionStatusArr) {
        for (AsyncActionStatus ordinal : asyncActionStatusArr) {
            asyncActionStatus = MERGE_MATRIX[asyncActionStatus.ordinal()][ordinal.ordinal()];
        }
        return asyncActionStatus;
    }
}
