package net.hockeyapp.android;

import android.os.Process;
import android.text.TextUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.utils.HockeyLog;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private CrashManagerListener mCrashManagerListener;
    private UncaughtExceptionHandler mDefaultExceptionHandler;
    private boolean mIgnoreDefaultHandler = false;

    public ExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler, CrashManagerListener crashManagerListener, boolean z) {
        this.mDefaultExceptionHandler = uncaughtExceptionHandler;
        this.mIgnoreDefaultHandler = z;
        this.mCrashManagerListener = crashManagerListener;
    }

    private static String limitedString(String str) {
        return (TextUtils.isEmpty(str) || str.length() <= 255) ? str : str.substring(0, 255);
    }

    public static void saveException(Throwable th, Thread thread, CrashManagerListener crashManagerListener) {
        Date date = new Date();
        Date date2 = new Date(CrashManager.getInitializeTimestamp());
        th.printStackTrace(new PrintWriter(new StringWriter()));
        String uuid = UUID.randomUUID().toString();
        CrashDetails crashDetails = new CrashDetails(uuid, th);
        crashDetails.setAppPackage(Constants.APP_PACKAGE);
        crashDetails.setAppVersionCode(Constants.APP_VERSION);
        crashDetails.setAppVersionName(Constants.APP_VERSION_NAME);
        crashDetails.setAppStartDate(date2);
        crashDetails.setAppCrashDate(date);
        if (crashManagerListener == null || crashManagerListener.includeDeviceData()) {
            crashDetails.setOsVersion(Constants.ANDROID_VERSION);
            crashDetails.setOsBuild(Constants.ANDROID_BUILD);
            crashDetails.setDeviceManufacturer(Constants.PHONE_MANUFACTURER);
            crashDetails.setDeviceModel(Constants.PHONE_MODEL);
        }
        if (thread != null && (crashManagerListener == null || crashManagerListener.includeThreadDetails())) {
            crashDetails.setThreadName(thread.getName() + "-" + thread.getId());
        }
        if (Constants.CRASH_IDENTIFIER != null && (crashManagerListener == null || crashManagerListener.includeDeviceIdentifier())) {
            crashDetails.setReporterKey(Constants.CRASH_IDENTIFIER);
        }
        crashDetails.writeCrashReport();
        if (crashManagerListener != null) {
            try {
                writeValueToFile(limitedString(crashManagerListener.getUserID()), uuid + ".user");
                writeValueToFile(limitedString(crashManagerListener.getContact()), uuid + ".contact");
                writeValueToFile(crashManagerListener.getDescription(), uuid + ".description");
            } catch (Throwable e) {
                HockeyLog.error("Error saving crash meta data!", e);
            }
        }
    }

    @Deprecated
    public static void saveException(Throwable th, CrashManagerListener crashManagerListener) {
        saveException(th, null, crashManagerListener);
    }

    private static void writeValueToFile(String str, String str2) throws IOException {
        BufferedWriter bufferedWriter;
        BufferedWriter bufferedWriter2;
        Throwable th;
        if (!TextUtils.isEmpty(str)) {
            BufferedWriter bufferedWriter3 = null;
            try {
                String str3 = Constants.FILES_PATH + "/" + str2;
                if (!TextUtils.isEmpty(str) && TextUtils.getTrimmedLength(str) > 0) {
                    bufferedWriter = new BufferedWriter(new FileWriter(str3));
                    try {
                        bufferedWriter.write(str);
                        bufferedWriter.flush();
                        bufferedWriter3 = bufferedWriter;
                    } catch (IOException e) {
                        bufferedWriter2 = bufferedWriter;
                        if (bufferedWriter2 != null) {
                            bufferedWriter2.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (bufferedWriter != null) {
                            bufferedWriter.close();
                        }
                        throw th;
                    }
                }
                if (bufferedWriter3 != null) {
                    bufferedWriter3.close();
                }
            } catch (IOException e2) {
                bufferedWriter2 = null;
                if (bufferedWriter2 != null) {
                    bufferedWriter2.close();
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                throw th;
            }
        }
    }

    public void setListener(CrashManagerListener crashManagerListener) {
        this.mCrashManagerListener = crashManagerListener;
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (Constants.FILES_PATH == null) {
            this.mDefaultExceptionHandler.uncaughtException(thread, th);
            return;
        }
        saveException(th, thread, this.mCrashManagerListener);
        if (this.mIgnoreDefaultHandler) {
            Process.killProcess(Process.myPid());
            System.exit(10);
            return;
        }
        this.mDefaultExceptionHandler.uncaughtException(thread, th);
    }
}
