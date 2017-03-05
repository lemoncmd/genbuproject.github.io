package com.microsoft.xbox.idp.telemetry.helpers;

import Microsoft.Telemetry.Base;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.ClientError;
import com.microsoft.xbox.idp.telemetry.utc.ServiceError;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction;
import com.microsoft.xbox.idp.toolkit.HttpError;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Errors;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Context;

public class UTCError {
    private static final int CLIENTERRORVERSION = 1;
    private static final String FAILURE = "Client Error Type - Failure";
    private static final String MSACANCEL = "Client Error Type - MSA canceled";
    private static final int SERVICEERRORVERSION = 1;
    private static final String SIGNEDOUT = "Client Error Type - Signed out";
    private static final String UINEEDEDERROR = "Client Error Type - UI Needed";
    private static final String USERCANCEL = "Client Error Type - User canceled";

    public static void trackClose(ErrorScreen errorScreen, CharSequence charSequence) {
        try {
            UTCPageAction.track(Errors.Close, charSequence);
        } catch (Exception e) {
            trackException(e, "UTCError.trackClose");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackException(Exception exception, String str) {
        Base clientError = new ClientError();
        if (exception != null && str != null) {
            UTCLog.log(String.format("%s:%s", new Object[]{str, exception.getMessage()}), new Object[0]);
            clientError.setErrorName(exception.getClass().getSimpleName());
            clientError.setErrorText(exception.getMessage());
            StackTraceElement[] stackTrace = exception.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                int i = 0;
                while (i < stackTrace.length && i < 10) {
                    StackTraceElement stackTraceElement = stackTrace[i];
                    if (stackTraceElement != null) {
                        String stackTraceElement2 = stackTraceElement.toString();
                        str = String.format("%s;%s", new Object[]{str, stackTraceElement2});
                    }
                    if (str.length() > Context.VERSION_ES6) {
                        break;
                    }
                    i += SERVICEERRORVERSION;
                }
            }
            clientError.setCallStack(str);
            clientError.setPageName(UTCPageView.getCurrentPage());
            UTCTelemetry.LogEvent(clientError);
        }
    }

    public static void trackFailure(String str, boolean z, CallBackSources callBackSources, long j) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setErrorName(FAILURE);
            Object[] objArr = new Object[SERVICEERRORVERSION];
            objArr[0] = Long.valueOf(j);
            clientError.setErrorCode(String.format("%s", objArr));
            clientError.setPageName(UTCPageView.getCurrentPage());
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, errorCode:%s, additionalInfo:%s", FAILURE, Long.valueOf(j), uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackFailure(String str, boolean z, CallBackSources callBackSources, Exception exception) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setErrorName(FAILURE);
            clientError.setPageName(UTCPageView.getCurrentPage());
            String str2 = BuildConfig.FLAVOR;
            if (exception != null) {
                str2 = exception.getClass().getSimpleName();
                String message = exception.getMessage();
                clientError.setErrorName(str2);
                clientError.setErrorText(message);
            }
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, exception:%s, additionalInfo:%s", FAILURE, str2, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackGoToEnforcement(ErrorScreen errorScreen, CharSequence charSequence) {
        try {
            UTCPageAction.track(Errors.GoToBanned, charSequence);
        } catch (Exception e) {
            trackException(e, "UTCError.trackGoToEnforcement");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSACancel(String str, boolean z, CallBackSources callBackSources) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setPageName(UTCPageView.getCurrentPage());
            clientError.setErrorName(MSACANCEL);
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, additionalInfo:%s", MSACANCEL, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUserCancel");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackPageView(ErrorScreen errorScreen, CharSequence charSequence) {
        try {
            UTCPageView.track(UTCTelemetry.getErrorScreen(errorScreen), charSequence);
        } catch (Exception e) {
            trackException(e, "UTCError.trackPageView");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackRightButton(ErrorScreen errorScreen, CharSequence charSequence) {
        try {
            UTCPageAction.track(PageAction.Errors.RightButton, charSequence);
        } catch (Exception e) {
            trackException(e, "UTCError.trackRightButton");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackServiceFailure(String str, String str2, HttpError httpError) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("pageName", str2);
            String str3 = "UNKNOWN";
            String str4 = MigrationManager.InitialSdkVersion;
            if (httpError != null) {
                str3 = httpError.getErrorMessage();
                Object[] objArr = new Object[SERVICEERRORVERSION];
                objArr[0] = Integer.valueOf(httpError.getErrorCode());
                str4 = String.format("%s", objArr);
            }
            Base serviceError = new ServiceError();
            serviceError.setErrorName(str);
            serviceError.setErrorText(str3);
            if (str2 == null) {
                str2 = UTCPageView.getCurrentPage();
            }
            serviceError.setPageName(str2);
            Object[] objArr2 = new Object[SERVICEERRORVERSION];
            objArr2[0] = str4;
            serviceError.setErrorCode(String.format("%s", objArr2));
            serviceError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Service Error:%s, errorCode:%s, additionalInfo:%s", FAILURE, str4, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(serviceError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackServiceFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSignedOut(String str, boolean z, CallBackSources callBackSources) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setPageName(UTCPageView.getCurrentPage());
            clientError.setErrorName(SIGNEDOUT);
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, additionalInfo:%s", SIGNEDOUT, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackSignedOut");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackTryAgain(ErrorScreen errorScreen, CharSequence charSequence) {
        try {
            UTCPageAction.track(Errors.Retry, charSequence);
        } catch (Exception e) {
            trackException(e, "UTCError.trackTryAgain");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackUINeeded(String str, boolean z, CallBackSources callBackSources) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setPageName(UTCPageView.getCurrentPage());
            clientError.setErrorName(UINEEDEDERROR);
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, additionalInfo:%s", UINEEDEDERROR, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUINeeded");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackUserCancel(String str, boolean z, CallBackSources callBackSources) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            Base clientError = new ClientError();
            clientError.setPageName(UTCPageView.getCurrentPage());
            clientError.setErrorName(USERCANCEL);
            clientError.setBaseData(UTCCommonDataModel.getCommonData(SERVICEERRORVERSION, uTCAdditionalInfoModel));
            UTCLog.log("Error:%s, additionalInfo:%s", USERCANCEL, uTCAdditionalInfoModel);
            UTCTelemetry.LogEvent(clientError);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUserCancel");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
