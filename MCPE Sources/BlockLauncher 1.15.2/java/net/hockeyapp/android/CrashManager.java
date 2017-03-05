package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;
import org.mozilla.javascript.ast.FunctionNode;

public class CrashManager {
    private static final String ALWAYS_SEND_KEY = "always_send_crash_reports";
    private static final int STACK_TRACES_FOUND_CONFIRMED = 2;
    private static final int STACK_TRACES_FOUND_NEW = 1;
    private static final int STACK_TRACES_FOUND_NONE = 0;
    private static boolean didCrashInLastSession = false;
    private static String identifier = null;
    private static long initializeTimestamp;
    private static boolean submitting = false;
    private static String urlString = null;

    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput = new int[CrashManagerUserInput.values().length];

        static {
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputDontSend.ordinal()] = CrashManager.STACK_TRACES_FOUND_NEW;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputAlwaysSend.ordinal()] = CrashManager.STACK_TRACES_FOUND_CONFIRMED;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputSend.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static String contentsOfFile(WeakReference<Context> weakReference, String str) {
        BufferedReader bufferedReader;
        IOException e;
        Throwable th;
        BufferedReader bufferedReader2 = null;
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(str)));
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            stringBuilder.append(readLine);
                            stringBuilder.append(System.getProperty("line.separator"));
                        } catch (FileNotFoundException e2) {
                            bufferedReader2 = bufferedReader;
                        } catch (IOException e3) {
                            e = e3;
                            bufferedReader2 = bufferedReader;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e4) {
                        }
                    }
                } catch (FileNotFoundException e5) {
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e6) {
                        }
                    }
                    return stringBuilder.toString();
                } catch (IOException e7) {
                    e = e7;
                    try {
                        e.printStackTrace();
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (IOException e8) {
                            }
                        }
                        return stringBuilder.toString();
                    } catch (Throwable th3) {
                        th = th3;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e9) {
                            }
                        }
                        throw th;
                    }
                }
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private static void deleteRetryCounter(WeakReference<Context> weakReference, String str, int i) {
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                Editor edit = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
                edit.remove("RETRY_COUNT: " + str);
                edit.apply();
            }
        }
    }

    private static void deleteStackTrace(WeakReference<Context> weakReference, String str) {
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                context.deleteFile(str);
                context.deleteFile(str.replace(".stacktrace", ".user"));
                context.deleteFile(str.replace(".stacktrace", ".contact"));
                context.deleteFile(str.replace(".stacktrace", ".description"));
            }
        }
    }

    public static void deleteStackTraces(WeakReference<Context> weakReference) {
        String[] searchForStackTraces = searchForStackTraces();
        if (searchForStackTraces != null && searchForStackTraces.length > 0) {
            HockeyLog.debug("Found " + searchForStackTraces.length + " stacktrace(s).");
            for (int i = 0; i < searchForStackTraces.length; i += STACK_TRACES_FOUND_NEW) {
                if (weakReference != null) {
                    try {
                        HockeyLog.debug("Delete stacktrace " + searchForStackTraces[i] + ".");
                        deleteStackTrace(weakReference, searchForStackTraces[i]);
                        Context context = (Context) weakReference.get();
                        if (context != null) {
                            context.deleteFile(searchForStackTraces[i]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean didCrashInLastSession() {
        return didCrashInLastSession;
    }

    public static void execute(Context context, CrashManagerListener crashManagerListener) {
        boolean z = true;
        boolean z2 = crashManagerListener != null && crashManagerListener.ignoreDefaultHandler();
        Boolean valueOf = Boolean.valueOf(z2);
        WeakReference weakReference = new WeakReference(context);
        int hasStackTraces = hasStackTraces(weakReference);
        if (hasStackTraces == STACK_TRACES_FOUND_NEW) {
            didCrashInLastSession = true;
            if (context instanceof Activity) {
                z = false;
            }
            Boolean valueOf2 = Boolean.valueOf(Boolean.valueOf(z).booleanValue() | PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ALWAYS_SEND_KEY, false));
            if (crashManagerListener != null) {
                valueOf2 = Boolean.valueOf(Boolean.valueOf(valueOf2.booleanValue() | crashManagerListener.shouldAutoUploadCrashes()).booleanValue() | crashManagerListener.onCrashesFound());
                crashManagerListener.onNewCrashesFound();
            }
            if (valueOf2.booleanValue()) {
                sendCrashes(weakReference, crashManagerListener, valueOf.booleanValue());
            } else {
                showDialog(weakReference, crashManagerListener, valueOf.booleanValue());
            }
        } else if (hasStackTraces == STACK_TRACES_FOUND_CONFIRMED) {
            if (crashManagerListener != null) {
                crashManagerListener.onConfirmedCrashesFound();
            }
            sendCrashes(weakReference, crashManagerListener, valueOf.booleanValue());
        } else {
            registerHandler(weakReference, crashManagerListener, valueOf.booleanValue());
        }
    }

    private static String getAlertTitle(Context context) {
        String appName = Util.getAppName(context);
        String string = context.getString(R.string.hockeyapp_crash_dialog_title);
        Object[] objArr = new Object[STACK_TRACES_FOUND_NEW];
        objArr[0] = appName;
        return String.format(string, objArr);
    }

    private static List<String> getConfirmedFilenames(WeakReference<Context> weakReference) {
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                return Arrays.asList(context.getSharedPreferences(Constants.SDK_NAME, 0).getString("ConfirmedFilenames", BuildConfig.FLAVOR).split("\\|"));
            }
        }
        return null;
    }

    public static long getInitializeTimestamp() {
        return initializeTimestamp;
    }

    public static CrashDetails getLastCrashDetails() {
        if (Constants.FILES_PATH == null || !didCrashInLastSession()) {
            return null;
        }
        File[] listFiles = new File(Constants.FILES_PATH + "/").listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.endsWith(".stacktrace");
            }
        });
        long j = 0;
        int length = listFiles.length;
        int i = 0;
        File file = null;
        while (i < length) {
            File file2 = listFiles[i];
            if (file2.lastModified() > j) {
                j = file2.lastModified();
            } else {
                file2 = file;
            }
            i += STACK_TRACES_FOUND_NEW;
            file = file2;
        }
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return CrashDetails.fromFile(file);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static String getURLString() {
        return urlString + "api/2/apps/" + identifier + "/crashes/";
    }

    public static boolean handleUserInput(CrashManagerUserInput crashManagerUserInput, CrashMetaData crashMetaData, CrashManagerListener crashManagerListener, WeakReference<Context> weakReference, boolean z) {
        switch (AnonymousClass7.$SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[crashManagerUserInput.ordinal()]) {
            case STACK_TRACES_FOUND_NEW /*1*/:
                if (crashManagerListener != null) {
                    crashManagerListener.onUserDeniedCrashes();
                }
                deleteStackTraces(weakReference);
                registerHandler(weakReference, crashManagerListener, z);
                return true;
            case STACK_TRACES_FOUND_CONFIRMED /*2*/:
                Context context = null;
                if (weakReference != null) {
                    context = (Context) weakReference.get();
                }
                if (context == null) {
                    return false;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ALWAYS_SEND_KEY, true).apply();
                sendCrashes(weakReference, crashManagerListener, z, crashMetaData);
                return true;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                sendCrashes(weakReference, crashManagerListener, z, crashMetaData);
                return true;
            default:
                return false;
        }
    }

    public static int hasStackTraces(WeakReference<Context> weakReference) {
        int i = 0;
        String[] searchForStackTraces = searchForStackTraces();
        List list = null;
        if (searchForStackTraces == null || searchForStackTraces.length <= 0) {
            return 0;
        }
        List confirmedFilenames;
        try {
            confirmedFilenames = getConfirmedFilenames(weakReference);
        } catch (Exception e) {
            confirmedFilenames = list;
        }
        if (confirmedFilenames == null) {
            return STACK_TRACES_FOUND_NEW;
        }
        int length = searchForStackTraces.length;
        while (i < length) {
            if (!confirmedFilenames.contains(searchForStackTraces[i])) {
                return STACK_TRACES_FOUND_NEW;
            }
            i += STACK_TRACES_FOUND_NEW;
        }
        return STACK_TRACES_FOUND_CONFIRMED;
    }

    public static void initialize(Context context, String str, String str2, CrashManagerListener crashManagerListener) {
        initialize(context, str, str2, crashManagerListener, true);
    }

    private static void initialize(Context context, String str, String str2, CrashManagerListener crashManagerListener, boolean z) {
        boolean z2 = false;
        if (context != null) {
            if (initializeTimestamp == 0) {
                initializeTimestamp = System.currentTimeMillis();
            }
            urlString = str;
            identifier = Util.sanitizeAppIdentifier(str2);
            didCrashInLastSession = false;
            Constants.loadFromContext(context);
            if (identifier == null) {
                identifier = Constants.APP_PACKAGE;
            }
            if (z) {
                if (crashManagerListener != null && crashManagerListener.ignoreDefaultHandler()) {
                    z2 = true;
                }
                registerHandler(new WeakReference(context), crashManagerListener, Boolean.valueOf(z2).booleanValue());
            }
        }
    }

    public static void initialize(Context context, String str, CrashManagerListener crashManagerListener) {
        initialize(context, Constants.BASE_URL, str, crashManagerListener, true);
    }

    private static String joinArray(String[] strArr, String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < strArr.length; i += STACK_TRACES_FOUND_NEW) {
            stringBuffer.append(strArr[i]);
            if (i < strArr.length - 1) {
                stringBuffer.append(str);
            }
        }
        return stringBuffer.toString();
    }

    public static void register(Context context) {
        Object appIdentifier = Util.getAppIdentifier(context);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier);
    }

    public static void register(Context context, String str) {
        register(context, Constants.BASE_URL, str, null);
    }

    public static void register(Context context, String str, String str2, CrashManagerListener crashManagerListener) {
        initialize(context, str, str2, crashManagerListener, false);
        execute(context, crashManagerListener);
    }

    public static void register(Context context, String str, CrashManagerListener crashManagerListener) {
        register(context, Constants.BASE_URL, str, crashManagerListener);
    }

    private static void registerHandler(WeakReference<Context> weakReference, CrashManagerListener crashManagerListener, boolean z) {
        if (TextUtils.isEmpty(Constants.APP_VERSION) || TextUtils.isEmpty(Constants.APP_PACKAGE)) {
            HockeyLog.debug("Exception handler not set because version or package is null.");
            return;
        }
        UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler != null) {
            HockeyLog.debug("Current handler class = " + defaultUncaughtExceptionHandler.getClass().getName());
        }
        if (defaultUncaughtExceptionHandler instanceof ExceptionHandler) {
            ((ExceptionHandler) defaultUncaughtExceptionHandler).setListener(crashManagerListener);
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(defaultUncaughtExceptionHandler, crashManagerListener, z));
        }
    }

    public static void resetAlwaysSend(WeakReference<Context> weakReference) {
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(ALWAYS_SEND_KEY).apply();
            }
        }
    }

    private static void saveConfirmedStackTraces(WeakReference<Context> weakReference) {
        if (weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                try {
                    String[] searchForStackTraces = searchForStackTraces();
                    Editor edit = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
                    edit.putString("ConfirmedFilenames", joinArray(searchForStackTraces, "|"));
                    edit.apply();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String[] searchForStackTraces() {
        if (Constants.FILES_PATH != null) {
            HockeyLog.debug("Looking for exceptions in: " + Constants.FILES_PATH);
            File file = new File(Constants.FILES_PATH + "/");
            return (file.mkdir() || file.exists()) ? file.list(new FilenameFilter() {
                public boolean accept(File file, String str) {
                    return str.endsWith(".stacktrace");
                }
            }) : new String[0];
        } else {
            HockeyLog.debug("Can't search for exception as file path is null.");
            return null;
        }
    }

    private static void sendCrashes(WeakReference<Context> weakReference, CrashManagerListener crashManagerListener, boolean z) {
        sendCrashes(weakReference, crashManagerListener, z, null);
    }

    private static void sendCrashes(final WeakReference<Context> weakReference, final CrashManagerListener crashManagerListener, boolean z, final CrashMetaData crashMetaData) {
        saveConfirmedStackTraces(weakReference);
        registerHandler(weakReference, crashManagerListener, z);
        Context context = (Context) weakReference.get();
        if ((context == null || Util.isConnectedToNetwork(context)) && !submitting) {
            submitting = true;
            new Thread() {
                public void run() {
                    CrashManager.submitStackTraces(weakReference, crashManagerListener, crashMetaData);
                    CrashManager.submitting = false;
                }
            }.start();
        }
    }

    private static void showDialog(final WeakReference<Context> weakReference, final CrashManagerListener crashManagerListener, final boolean z) {
        Context context = null;
        if (weakReference != null) {
            context = (Context) weakReference.get();
        }
        if (context != null) {
            if (crashManagerListener == null || !crashManagerListener.onHandleAlertView()) {
                Builder builder = new Builder(context);
                builder.setTitle(getAlertTitle(context));
                builder.setMessage(R.string.hockeyapp_crash_dialog_message);
                builder.setNegativeButton(R.string.hockeyapp_crash_dialog_negative_button, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputDontSend, null, crashManagerListener, weakReference, z);
                    }
                });
                builder.setNeutralButton(R.string.hockeyapp_crash_dialog_neutral_button, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputAlwaysSend, null, crashManagerListener, weakReference, z);
                    }
                });
                builder.setPositiveButton(R.string.hockeyapp_crash_dialog_positive_button, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputSend, null, crashManagerListener, weakReference, z);
                    }
                });
                builder.create().show();
            }
        }
    }

    public static void submitStackTraces(WeakReference<Context> weakReference, CrashManagerListener crashManagerListener) {
        submitStackTraces(weakReference, crashManagerListener, null);
    }

    public static void submitStackTraces(WeakReference<Context> weakReference, CrashManagerListener crashManagerListener, CrashMetaData crashMetaData) {
        String[] searchForStackTraces = searchForStackTraces();
        Boolean valueOf = Boolean.valueOf(false);
        if (searchForStackTraces != null && searchForStackTraces.length > 0) {
            HockeyLog.debug("Found " + searchForStackTraces.length + " stacktrace(s).");
            Boolean bool = valueOf;
            for (int i = 0; i < searchForStackTraces.length; i += STACK_TRACES_FOUND_NEW) {
                HttpURLConnection httpURLConnection = null;
                try {
                    String str = searchForStackTraces[i];
                    String contentsOfFile = contentsOfFile(weakReference, str);
                    if (contentsOfFile.length() > 0) {
                        Object userID;
                        HockeyLog.debug("Transmitting crash data: \n" + contentsOfFile);
                        String contentsOfFile2 = contentsOfFile(weakReference, str.replace(".stacktrace", ".user"));
                        Object contentsOfFile3 = contentsOfFile(weakReference, str.replace(".stacktrace", ".contact"));
                        String str2;
                        if (crashMetaData != null) {
                            userID = crashMetaData.getUserID();
                            if (TextUtils.isEmpty(userID)) {
                                str2 = contentsOfFile2;
                            }
                            CharSequence userEmail = crashMetaData.getUserEmail();
                            if (!TextUtils.isEmpty(userEmail)) {
                                contentsOfFile3 = userEmail;
                            }
                        } else {
                            str2 = contentsOfFile2;
                        }
                        CharSequence contentsOfFile4 = contentsOfFile(weakReference, str.replace(".stacktrace", ".description"));
                        Object userDescription = crashMetaData != null ? crashMetaData.getUserDescription() : BuildConfig.FLAVOR;
                        if (!TextUtils.isEmpty(contentsOfFile4)) {
                            if (TextUtils.isEmpty(userDescription)) {
                                Object[] objArr = new Object[STACK_TRACES_FOUND_NEW];
                                objArr[0] = contentsOfFile4;
                                userDescription = String.format("Log:\n%s", objArr);
                            } else {
                                Object[] objArr2 = new Object[STACK_TRACES_FOUND_CONFIRMED];
                                objArr2[0] = userDescription;
                                objArr2[STACK_TRACES_FOUND_NEW] = contentsOfFile4;
                                userDescription = String.format("%s\n\nLog:\n%s", objArr2);
                            }
                        }
                        Map hashMap = new HashMap();
                        hashMap.put("raw", contentsOfFile);
                        hashMap.put("userID", userID);
                        hashMap.put("contact", contentsOfFile3);
                        hashMap.put("description", userDescription);
                        hashMap.put("sdk", Constants.SDK_NAME);
                        hashMap.put("sdk_version", BuildConfig.VERSION_NAME);
                        httpURLConnection = new HttpURLConnectionBuilder(getURLString()).setRequestMethod(HttpEngine.POST).writeFormFields(hashMap).build();
                        int responseCode = httpURLConnection.getResponseCode();
                        boolean z = responseCode == 202 || responseCode == 201;
                        bool = Boolean.valueOf(z);
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    if (bool.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakReference, searchForStackTraces[i]);
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesSent();
                            deleteRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesNotSent();
                            updateRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    if (bool.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakReference, searchForStackTraces[i]);
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesSent();
                            deleteRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesNotSent();
                            updateRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    }
                } catch (Throwable th) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    if (bool.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakReference, searchForStackTraces[i]);
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesSent();
                            deleteRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (crashManagerListener != null) {
                            crashManagerListener.onCrashesNotSent();
                            updateRetryCounter(weakReference, searchForStackTraces[i], crashManagerListener.getMaxRetryAttempts());
                        }
                    }
                }
            }
        }
    }

    private static void updateRetryCounter(WeakReference<Context> weakReference, String str, int i) {
        if (i != -1 && weakReference != null) {
            Context context = (Context) weakReference.get();
            if (context != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SDK_NAME, 0);
                Editor edit = sharedPreferences.edit();
                int i2 = sharedPreferences.getInt("RETRY_COUNT: " + str, 0);
                if (i2 >= i) {
                    deleteStackTrace(weakReference, str);
                    deleteRetryCounter(weakReference, str, i);
                    return;
                }
                edit.putInt("RETRY_COUNT: " + str, i2 + STACK_TRACES_FOUND_NEW);
                edit.apply();
            }
        }
    }
}
