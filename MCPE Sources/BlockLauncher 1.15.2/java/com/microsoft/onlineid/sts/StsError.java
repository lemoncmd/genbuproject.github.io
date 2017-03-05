package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.util.Locale;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class StsError {
    private final StsErrorCode _code;
    private final String _logMessage;
    private final String _originalErrorMessage;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode = new int[StsErrorCode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_DEVICE_DA_INVALID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_E_DEVICE_DA_TOKEN_EXPIRED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_FORCE_SIGNIN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_FORCESIGNIN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_CANT_DENY_APPROVED_SESSION.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_CANT_APPROVE_DENIED_SESSION.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_INVALID_STATE_TRANSITION.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_INVALID_OPERATION.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_TOTP_AUTHENTICATOR_ID_INVALID.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public StsError(IntegerCodeServerError integerCodeServerError) {
        Objects.verifyArgumentNotNull(integerCodeServerError, "error");
        this._code = StsErrorCode.convertServerError(integerCodeServerError);
        this._originalErrorMessage = integerCodeServerError.toString();
        this._logMessage = String.format(Locale.US, "%s error caused by server error:\n%s", new Object[]{this._code.name(), this._originalErrorMessage});
    }

    public StsError(StringCodeServerError stringCodeServerError) {
        Objects.verifyArgumentNotNull(stringCodeServerError, "error");
        this._code = StsErrorCode.convertServerError(stringCodeServerError);
        this._originalErrorMessage = stringCodeServerError.toString();
        this._logMessage = String.format(Locale.US, "%s error caused by server error:\n%s", new Object[]{this._code.name(), this._originalErrorMessage});
    }

    public StsError(StsErrorCode stsErrorCode) {
        Objects.verifyArgumentNotNull(stsErrorCode, "code");
        this._code = stsErrorCode;
        this._originalErrorMessage = stsErrorCode.name();
        this._logMessage = String.format(Locale.US, "%s error.", new Object[]{this._originalErrorMessage});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof StsError) && obj != null) {
            return Objects.equals(this._code, ((StsError) obj)._code);
        } else if (!(obj instanceof StsErrorCode) || obj == null) {
            return false;
        } else {
            return Objects.equals(this._code, (StsErrorCode) obj);
        }
    }

    public StsErrorCode getCode() {
        return this._code;
    }

    public String getMessage() {
        return this._logMessage;
    }

    public String getOriginalErrorMessage() {
        return this._originalErrorMessage;
    }

    public int hashCode() {
        return Objects.hashCode(this._code);
    }

    public boolean isInvalidSessionError() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case Token.GOTO /*5*/:
            case Token.IFEQ /*6*/:
            case Token.IFNE /*7*/:
            case Token.SETNAME /*8*/:
            case Token.BITOR /*9*/:
                return true;
            default:
                return false;
        }
    }

    public boolean isNgcKeyNotFoundError() {
        return this._code == StsErrorCode.PP_E_NGC_LOGIN_KEY_NOT_FOUND;
    }

    public boolean isRetryableDeviceDAErrorForDeviceAuth() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return true;
            default:
                return false;
        }
    }

    public boolean isRetryableDeviceDAErrorForUserAuth() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return true;
            default:
                return false;
        }
    }
}
