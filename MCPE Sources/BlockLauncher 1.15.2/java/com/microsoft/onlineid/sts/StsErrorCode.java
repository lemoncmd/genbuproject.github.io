package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;

public enum StsErrorCode {
    PP_E_FORCESIGNIN((String) -2147217396),
    PPCRL_REQUEST_E_FORCE_SIGNIN((String) -2147186459),
    PP_E_INVALIDREQUEST((String) -2147217380),
    PP_E_SA_INVALID_REGISTRATION_ID((String) -2147180536),
    PP_E_SA_INVALID_DEVICE_ID((String) -2147180537),
    PP_E_INTERFACE_INVALIDPUID((String) -2147208105),
    PP_E_SA_DEVICE_NOT_FOUND((String) -2147180538),
    PP_E_TOTP_AUTHENTICATOR_ID_INVALID((String) -2147181515),
    PP_E_FLOWDISABLED((String) -2147208158),
    PP_E_NOT_OVER_SSL((String) -2147217386),
    PP_E_INTERFACE_NOT_POST((String) -2147208119),
    PP_E_INTERFACE_INVALIDREQUESTFORMAT((String) -2147208112),
    PP_E_SA_CANT_APPROVE_DENIED_SESSION((String) -2147180533),
    PP_E_SA_CANT_DENY_APPROVED_SESSION((String) -2147180531),
    PP_E_SA_SID_ALREADY_APPROVED((String) -2147180530),
    PP_E_SA_INVALID_STATE_TRANSITION((String) -2147180543),
    PP_E_SA_INVALID_OPERATION((String) -2147180540),
    PP_E_BAD_PASSWORD((String) -2147217390),
    PP_E_INTERFACE_INVALID_PASSWORD((String) -2147208107),
    PP_E_MISSING_CERT((String) -2147197912),
    PPCRL_REQUEST_E_PARTNER_NOT_FOUND((String) -2147186646),
    PPCRL_REQUEST_E_INVALID_POLICY((String) -2147186644),
    PP_E_STS_NONCE_REQUIRED((String) -2147197895),
    PPCRL_REQUEST_E_PARTNER_HAS_NO_ASYMMETRIC_KEY((String) -2147186645),
    PPCRL_REQUEST_E_PARTNER_NEED_PIN((String) -2147186457),
    PPCRL_REQUEST_E_DEVICE_DA_INVALID((String) -2147186627),
    PPCRL_E_DEVICE_DA_TOKEN_EXPIRED((String) -2147188631),
    PP_E_K_ERROR_DB_MEMBER_DOES_NOT_EXIST((String) -805307371),
    PP_E_K_ERROR_DB_MEMBER_EXISTS((String) -805307370),
    PPCRL_REQUEST_E_BAD_MEMBER_NAME_OR_PASSWORD((String) -2147186655),
    PP_E_NGC_INVALID_CLOUD_PIN((String) -2147180401),
    PP_E_NGC_ACCOUNT_LOCKED((String) -2147180400),
    PP_E_NGC_LOGIN_KEY_NOT_FOUND((String) -2147180408),
    Unrecognized;
    
    private final Integer _code;
    private final String _dcClass;

    private StsErrorCode(int i) {
        this._code = Integer.valueOf(i);
        this._dcClass = null;
    }

    private StsErrorCode(String str) {
        this._code = null;
        this._dcClass = str;
    }

    private static StsErrorCode convertDCCode(String str) {
        Strings.verifyArgumentNotNullOrEmpty(str, "subCode");
        for (StsErrorCode stsErrorCode : values()) {
            if (stsErrorCode._dcClass != null && stsErrorCode._dcClass.equals(str)) {
                return stsErrorCode;
            }
        }
        return null;
    }

    private static StsErrorCode convertHR(int i) {
        for (StsErrorCode stsErrorCode : values()) {
            if (stsErrorCode._code != null && stsErrorCode._code.equals(Integer.valueOf(i))) {
                return stsErrorCode;
            }
        }
        return null;
    }

    public static StsErrorCode convertServerError(IntegerCodeServerError integerCodeServerError) {
        Objects.verifyArgumentNotNull(integerCodeServerError, "error");
        StsErrorCode convertHR = convertHR(integerCodeServerError.getSubError());
        if (convertHR == null) {
            convertHR = convertHR(integerCodeServerError.getError());
        }
        return convertHR == null ? Unrecognized : convertHR;
    }

    public static StsErrorCode convertServerError(StringCodeServerError stringCodeServerError) {
        Objects.verifyArgumentNotNull(stringCodeServerError, "error");
        StsErrorCode convertHR = convertHR(stringCodeServerError.getSubError());
        if (convertHR == null) {
            convertHR = convertDCCode(stringCodeServerError.getError());
        }
        return convertHR == null ? Unrecognized : convertHR;
    }

    public static String getFriendlyHRDescription(int i) {
        StsErrorCode convertHR = convertHR(i);
        return convertHR != null ? convertHR.name() + " (0x" + Integer.toHexString(i) + ")" : "0x" + Integer.toHexString(i);
    }

    @Deprecated
    Integer getCode() {
        return this._code;
    }

    @Deprecated
    String getDCClass() {
        return this._dcClass;
    }
}
