package com.microsoft.onlineid.internal.sso;

import android.app.PendingIntent;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.OnlineIdConfiguration.PreferredSignUpMemberNameType;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.client.ClientConfigUpdateNeededException;
import com.microsoft.onlineid.internal.sso.exception.ClientNotAuthorizedException;
import com.microsoft.onlineid.internal.sso.exception.UnsupportedClientVersionException;
import com.microsoft.onlineid.internal.storage.StorageException;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.DeviceIdentity;
import com.microsoft.onlineid.sts.IntegerCodeServerError;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;
import java.util.Date;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class BundleMarshaller {
    public static final String AccountPickerBodyKey = "com.microsoft.onlineid.account_picker_body";
    public static final String ActivityResultTypeKey = "com.microsoft.onlineid.result_type";
    public static final String AllUsersKey = "com.microsoft.onlineid.all_users";
    public static final String BackupDeviceKey = "com.microsoft.onlineid.backup_device";
    public static final String BackupUsersKey = "com.microsoft.onlineid.backup_users";
    public static final String CidExclusionListKey = "com.microsoft.onlineid.cid_exclusion_list";
    public static final String ClientAppVersionNameKey = "com.microsoft.onlineid.client_app_version_name";
    public static final String ClientConfigLastDownloadedTimeKey = "com.microsoft.onlineid.client_config_last_downloaded_time";
    public static final String ClientConfigVersionKey = "com.microsoft.onlineid.client_config_version";
    public static final String ClientFlightsKey = "com.microsoft.onlineid.client_flights";
    public static final String ClientPackageNameKey = "com.microsoft.onlineid.client_package_name";
    public static final String ClientSdkVersionKey = "com.microsoft.onlineid.client_sdk_version";
    public static final String ClientSsoVersionKey = "com.microsoft.onlineid.client_sso_version";
    public static final String ClientStateBundleKey = "com.microsoft.onlineid.client_state";
    public static final String CobrandingIdKey = "com.microsoft.onlineid.cobranding_id";
    public static final String DeviceDATokenKey = "com.microsoft.onlineid.device_datoken";
    public static final String DeviceDATokenObtainedTime = "com.microsoft.onlineid.device_datoken_obtained_time";
    public static final String DevicePasswordKey = "com.microsoft.onlineid.device_password";
    public static final String DeviceProvisionTimeKey = "com.microsoft.onlineid.device_provision_time";
    public static final String DevicePuidKey = "com.microsoft.onlineid.device_puid";
    public static final String DeviceSessionKeyBase64Key = "com.microsoft.onlineid.device_session_key_base64";
    public static final String DeviceUsernameKey = "com.microsoft.onlineid.device_username";
    public static final String ErrorCodeKey = "com.microsoft.onlineid.error_code";
    public static final String ErrorMessageKey = "com.microsoft.onlineid.error_message";
    public static final String IsRegisteredForNgc = "com.microsoft.onlineid.registered_for_ngc";
    public static final String IsSignedOutOfThisAppOnlyKey = "com.microsoft.onlineid.signed_out_this_app_only";
    public static final String KeyPrefix = "com.microsoft.onlineid.";
    public static final String PreferredMembernameTypeKey = "com.microsoft.onlineid.preferred_membername_type";
    public static final String PrefillUsernameKey = "com.microsoft.onlineid.prefill_username";
    public static final String RedirectRequestToKey = "com.microsoft.onlineid.redirect_request_to";
    public static final String RemoteConnectCodeKey = "com.microsoft.onlineid.remote_connect_code";
    public static final String TicketExpirationTimeKey = "com.microsoft.onlineid.ticket_expiration_time";
    public static final String TicketPolicyKey = "com.microsoft.onlineid.ticket_scope_policy";
    public static final String TicketTargetKey = "com.microsoft.onlineid.ticket_scope_target";
    public static final String TicketValueKey = "com.microsoft.onlineid.ticket_value";
    public static final String UiResolutionIntentKey = "com.microsoft.onlineid.ui_resolution_intent";
    public static final String UnauthenticatedSessionIdKey = "com.microsoft.onlineid.unauth_session_id";
    public static final String UserCidKey = "com.microsoft.onlineid.user_cid";
    public static final String UserDATokenKey = "com.microsoft.onlineid.user_datoken";
    public static final String UserDATokenObtainedTimeKey = "com.microsoft.onlineid.user_datoken_obtained_time";
    public static final String UserDisplayNameKey = "com.microsoft.onlineid.user_display_name";
    public static final String UserJustAddedKey = "com.microsoft.onlineid.user_just_added";
    public static final String UserPuidKey = "com.microsoft.onlineid.user_puid";
    public static final String UserSessionKeyBase64Key = "com.microsoft.onlineid.user_session_key_base64";
    public static final String UserUsernameKey = "com.microsoft.onlineid.user_username";
    public static final String WebFlowTelemetryAllEventsCapturedKey = "com.microsoft.onlineid.web_telemetry_all_events_captured";
    public static final String WebFlowTelemetryEventsKey = "com.microsoft.onlineid.web_telemetry_events";
    public static final String WebFlowTelemetryPrecachingEnabledKey = "com.microsoft.onlineid.web_telemetry_precaching_enabled";
    public static final String WebFlowTelemetryRequestedKey = "com.microsoft.onlineid.web_telemetry_requested";

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError = new int[SsoServiceError.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.ClientNotAuthorized.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.UnsupportedClientVersion.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.StorageException.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.IllegalArgumentException.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.AccountNotFound.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.NetworkException.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.StsException.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.InvalidResponseException.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.MasterRedirectException.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.ClientConfigUpdateNeededException.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.Unknown.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public static DeviceIdentity deviceAccountFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            return new DeviceIdentity(new DeviceCredentials(bundle.getString(DeviceUsernameKey), bundle.getString(DevicePasswordKey)), bundle.getString(DevicePuidKey), new DAToken(bundle.getString(DeviceDATokenKey), bundle.getByteArray(DeviceSessionKeyBase64Key)));
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create DeviceIdentity from Bundle.", e);
        }
    }

    public static Bundle deviceAccountToBundle(DeviceIdentity deviceIdentity) {
        Bundle bundle = new Bundle();
        bundle.putString(DevicePuidKey, deviceIdentity.getPuid());
        DeviceCredentials credentials = deviceIdentity.getCredentials();
        bundle.putString(DeviceUsernameKey, credentials.getUsername());
        bundle.putString(DevicePasswordKey, credentials.getPassword());
        DAToken dAToken = deviceIdentity.getDAToken();
        bundle.putString(DeviceDATokenKey, dAToken.getToken());
        bundle.putByteArray(DeviceSessionKeyBase64Key, dAToken.getSessionKey());
        return bundle;
    }

    public static Bundle errorToBundle(SsoServiceError ssoServiceError, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt(ErrorCodeKey, ssoServiceError.getCode());
        bundle.putString(ErrorMessageKey, str);
        return bundle;
    }

    public static AuthenticationException exceptionFromBundle(Bundle bundle) throws BundleMarshallerException {
        int i = bundle.getInt(ErrorCodeKey);
        String string = bundle.getString(ErrorMessageKey);
        if (i != 0) {
            switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$sso$SsoServiceError[SsoServiceError.get(i).ordinal()]) {
                case NativeRegExp.MATCH /*1*/:
                    return new ClientNotAuthorizedException(string);
                case NativeRegExp.PREFIX /*2*/:
                    return new UnsupportedClientVersionException(string);
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    return new InternalException(new StorageException(string));
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    return new InternalException(new IllegalArgumentException(string));
                case Token.GOTO /*5*/:
                    return new AccountNotFoundException(string);
                case Token.IFEQ /*6*/:
                    return new NetworkException(string);
                case Token.IFNE /*7*/:
                    return new StsException(string, new StsError(new IntegerCodeServerError(0)));
                case Token.SETNAME /*8*/:
                    return new InvalidResponseException(string);
                case Token.BITOR /*9*/:
                    return new MasterRedirectException(string, bundle.getString(RedirectRequestToKey));
                case Token.BITXOR /*10*/:
                    return new ClientConfigUpdateNeededException(string);
                default:
                    return new InternalException(string);
            }
        } else if (string != null) {
            return new InternalException(string);
        } else {
            throw new BundleMarshallerException("Neither error_code nor error_message was found in the given Bundle.");
        }
    }

    public static Bundle exceptionToBundle(Exception exception) {
        try {
            throw exception;
        } catch (ClientNotAuthorizedException e) {
            return errorToBundle(SsoServiceError.ClientNotAuthorized, e.getMessage());
        } catch (UnsupportedClientVersionException e2) {
            return errorToBundle(SsoServiceError.UnsupportedClientVersion, e2.getMessage());
        } catch (StorageException e3) {
            return errorToBundle(SsoServiceError.StorageException, e3.getMessage());
        } catch (IllegalArgumentException e4) {
            return errorToBundle(SsoServiceError.IllegalArgumentException, e4.getMessage());
        } catch (AccountNotFoundException e5) {
            return errorToBundle(SsoServiceError.AccountNotFound, e5.getMessage());
        } catch (NetworkException e6) {
            return errorToBundle(SsoServiceError.NetworkException, e6.getMessage());
        } catch (StsException e7) {
            return errorToBundle(SsoServiceError.StsException, e7.getMessage());
        } catch (InvalidResponseException e8) {
            return errorToBundle(SsoServiceError.InvalidResponseException, e8.getMessage());
        } catch (ClientConfigUpdateNeededException e9) {
            return errorToBundle(SsoServiceError.ClientConfigUpdateNeededException, e9.getMessage());
        } catch (MasterRedirectException e10) {
            Bundle errorToBundle = errorToBundle(SsoServiceError.MasterRedirectException, e10.getMessage());
            errorToBundle.putString(RedirectRequestToKey, e10.getRedirectRequestTo());
            return errorToBundle;
        } catch (Exception e11) {
            return errorToBundle(SsoServiceError.Unknown, e11.getClass().getName() + ": " + e11.getMessage());
        }
    }

    public static boolean hasError(Bundle bundle) {
        return (bundle.getInt(ErrorCodeKey) == 0 && bundle.getString(ErrorMessageKey) == null) ? false : true;
    }

    public static boolean hasLimitedUserAccount(Bundle bundle) {
        return (bundle.getString(UserPuidKey) == null || bundle.getString(UserCidKey) == null || bundle.getString(UserUsernameKey) == null) ? false : true;
    }

    public static boolean hasPendingIntent(Bundle bundle) {
        return bundle.containsKey(UiResolutionIntentKey);
    }

    public static boolean hasScope(Bundle bundle) {
        return (bundle.getString(TicketTargetKey, null) == null || bundle.getString(TicketPolicyKey, null) == null) ? false : true;
    }

    public static boolean hasTicket(Bundle bundle) {
        return (bundle.getString(TicketTargetKey) == null || bundle.getString(TicketPolicyKey) == null || bundle.getLong(TicketExpirationTimeKey) == 0 || bundle.getString(TicketValueKey) == null) ? false : true;
    }

    public static AuthenticatorUserAccount limitedUserAccountFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            AuthenticatorUserAccount authenticatorUserAccount = new AuthenticatorUserAccount(bundle.getString(UserPuidKey), bundle.getString(UserCidKey), bundle.getString(UserUsernameKey), null);
            authenticatorUserAccount.setDisplayName(bundle.getString(UserDisplayNameKey));
            return authenticatorUserAccount;
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create limited AuthenticatorUserAccount from Bundle.", e);
        }
    }

    public static Bundle limitedUserAccountToBundle(AuthenticatorUserAccount authenticatorUserAccount) {
        Bundle bundle = new Bundle();
        bundle.putString(UserCidKey, authenticatorUserAccount.getCid());
        bundle.putString(UserPuidKey, authenticatorUserAccount.getPuid());
        bundle.putString(UserUsernameKey, authenticatorUserAccount.getUsername());
        bundle.putString(UserDisplayNameKey, authenticatorUserAccount.getDisplayName());
        return bundle;
    }

    public static Bundle onlineIdConfigurationToBundle(OnlineIdConfiguration onlineIdConfiguration) {
        Bundle bundle = new Bundle();
        PreferredSignUpMemberNameType preferredSignUpMemberNameType = onlineIdConfiguration.getPreferredSignUpMemberNameType();
        if (preferredSignUpMemberNameType != PreferredSignUpMemberNameType.None) {
            bundle.putString(PreferredMembernameTypeKey, preferredSignUpMemberNameType.toString());
        }
        String cobrandingId = onlineIdConfiguration.getCobrandingId();
        if (!(cobrandingId == null || cobrandingId.isEmpty())) {
            bundle.putString(CobrandingIdKey, cobrandingId);
        }
        if (onlineIdConfiguration.getShouldGatherWebTelemetry()) {
            bundle.putBoolean(WebFlowTelemetryRequestedKey, true);
        }
        return bundle;
    }

    public static PendingIntent pendingIntentFromBundle(Bundle bundle) throws BundleMarshallerException {
        PendingIntent pendingIntent = (PendingIntent) bundle.getParcelable(UiResolutionIntentKey);
        if (pendingIntent != null) {
            return pendingIntent;
        }
        throw new BundleMarshallerException("PendingIntent not found in Bundle.");
    }

    public static Bundle pendingIntentToBundle(PendingIntent pendingIntent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(UiResolutionIntentKey, pendingIntent);
        return bundle;
    }

    public static ISecurityScope scopeFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            return new SecurityScope(bundle.getString(TicketTargetKey), bundle.getString(TicketPolicyKey));
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create SecurityScope from Bundle.", e);
        }
    }

    public static Bundle scopeToBundle(ISecurityScope iSecurityScope) {
        Bundle bundle = new Bundle();
        bundle.putString(TicketTargetKey, iSecurityScope.getTarget());
        bundle.putString(TicketPolicyKey, iSecurityScope.getPolicy());
        return bundle;
    }

    public static Ticket ticketFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            return new Ticket(scopeFromBundle(bundle), new Date(bundle.getLong(TicketExpirationTimeKey)), bundle.getString(TicketValueKey));
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create Ticket from Bundle.", e);
        }
    }

    public static Bundle ticketToBundle(Ticket ticket) {
        Bundle scopeToBundle = scopeToBundle(ticket.getScope());
        scopeToBundle.putString(TicketValueKey, ticket.getValue());
        scopeToBundle.putLong(TicketExpirationTimeKey, ticket.getExpiry().getTime());
        return scopeToBundle;
    }

    public static AuthenticatorUserAccount userAccountFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            DAToken userDATokenFromBundle = userDATokenFromBundle(bundle);
            AuthenticatorUserAccount limitedUserAccountFromBundle = limitedUserAccountFromBundle(bundle);
            limitedUserAccountFromBundle.setDAToken(userDATokenFromBundle);
            return limitedUserAccountFromBundle;
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create AuthenticatorUserAccount from Bundle.", e);
        }
    }

    public static Bundle userAccountToBundle(AuthenticatorUserAccount authenticatorUserAccount) {
        Bundle limitedUserAccountToBundle = limitedUserAccountToBundle(authenticatorUserAccount);
        limitedUserAccountToBundle.putAll(userDATokenToBundle(authenticatorUserAccount.getDAToken()));
        return limitedUserAccountToBundle;
    }

    public static DAToken userDATokenFromBundle(Bundle bundle) throws BundleMarshallerException {
        try {
            return new DAToken(bundle.getString(UserDATokenKey), bundle.getByteArray(UserSessionKeyBase64Key));
        } catch (Throwable e) {
            throw new BundleMarshallerException("Could not create DAToken from Bundle.", e);
        }
    }

    public static Bundle userDATokenToBundle(DAToken dAToken) {
        Bundle bundle = new Bundle();
        bundle.putString(UserDATokenKey, dAToken.getToken());
        bundle.putByteArray(UserSessionKeyBase64Key, dAToken.getSessionKey());
        return bundle;
    }
}
