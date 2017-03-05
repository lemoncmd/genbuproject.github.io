package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public abstract class AuthActivity extends BaseActivity {
    public static final int RESULT_PROVIDER_ERROR = 2;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$interop$Interop$AuthFlowScreenStatus = new int[AuthFlowScreenStatus.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$interop$Interop$AuthFlowScreenStatus[AuthFlowScreenStatus.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$interop$Interop$AuthFlowScreenStatus[AuthFlowScreenStatus.ERROR_USER_CANCEL.ordinal()] = AuthActivity.RESULT_PROVIDER_ERROR;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$interop$Interop$AuthFlowScreenStatus[AuthFlowScreenStatus.PROVIDER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static AuthFlowScreenStatus fromActivityResult(int i) {
        switch (i) {
            case Token.ERROR /*-1*/:
                return AuthFlowScreenStatus.NO_ERROR;
            case NativeRegExp.TEST /*0*/:
                return AuthFlowScreenStatus.ERROR_USER_CANCEL;
            default:
                return AuthFlowScreenStatus.PROVIDER_ERROR;
        }
    }

    public static int toActivityResult(AuthFlowScreenStatus authFlowScreenStatus) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$idp$interop$Interop$AuthFlowScreenStatus[authFlowScreenStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return -1;
            case RESULT_PROVIDER_ERROR /*2*/:
                return 0;
            default:
                return RESULT_PROVIDER_ERROR;
        }
    }

    protected void finishCompat() {
        finish();
    }

    protected void showBodyFragment(Fragment fragment, Bundle bundle, boolean z) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        Fragment findFragmentById;
        if (!z) {
            findFragmentById = fragmentManager.findFragmentById(R.id.xbid_header_fragment);
            if (findFragmentById != null) {
                beginTransaction.remove(findFragmentById);
            }
        } else if (fragmentManager.findFragmentById(R.id.xbid_header_fragment) == null) {
            findFragmentById = new HeaderFragment();
            findFragmentById.setArguments(bundle);
            beginTransaction.add(R.id.xbid_header_fragment, findFragmentById);
        }
        if (fragment != null) {
            fragment.setArguments(bundle);
        }
        beginTransaction.replace(R.id.xbid_body_fragment, fragment);
        beginTransaction.commit();
    }
}
