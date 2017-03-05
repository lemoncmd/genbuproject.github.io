package com.microsoft.xbox.idp.telemetry.helpers;

import Microsoft.Telemetry.Base;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Errors;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class UTCTelemetry {
    public static final String UNKNOWNPAGE = "Unknown";

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen = new int[ErrorScreen.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorScreen.BAN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorScreen.CATCHALL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorScreen.CREATION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorScreen.OFFLINE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum CallBackSources {
        Account,
        Ticket
    }

    public static void LogEvent(Base base) {
        try {
            Interop.getCll().log(base);
        } catch (NullPointerException e) {
            UTCLog.log("CLL not initialized.  Is null", new Object[0]);
        }
    }

    public static String getErrorScreen(ErrorScreen errorScreen) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[errorScreen.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return Errors.Banned;
            case NativeRegExp.PREFIX /*2*/:
                return Errors.Generic;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return Errors.Create;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return Errors.Offline;
            default:
                return String.format("%sErrorScreen", new Object[]{UNKNOWNPAGE});
        }
    }
}
