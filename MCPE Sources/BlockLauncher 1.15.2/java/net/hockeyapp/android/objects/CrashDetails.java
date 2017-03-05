package net.hockeyapp.android.objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.HockeyLog;

public class CrashDetails {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    private static final String FIELD_APP_CRASH_DATE = "Date";
    private static final String FIELD_APP_PACKAGE = "Package";
    private static final String FIELD_APP_START_DATE = "Start Date";
    private static final String FIELD_APP_VERSION_CODE = "Version Code";
    private static final String FIELD_APP_VERSION_NAME = "Version Name";
    private static final String FIELD_CRASH_REPORTER_KEY = "CrashReporter Key";
    private static final String FIELD_DEVICE_MANUFACTURER = "Manufacturer";
    private static final String FIELD_DEVICE_MODEL = "Model";
    private static final String FIELD_OS_BUILD = "Android Build";
    private static final String FIELD_OS_VERSION = "Android";
    private static final String FIELD_THREAD_NAME = "Thread";
    private Date appCrashDate;
    private String appPackage;
    private Date appStartDate;
    private String appVersionCode;
    private String appVersionName;
    private final String crashIdentifier;
    private String deviceManufacturer;
    private String deviceModel;
    private String osBuild;
    private String osVersion;
    private String reporterKey;
    private String threadName;
    private String throwableStackTrace;

    public CrashDetails(String str) {
        this.crashIdentifier = str;
    }

    public CrashDetails(String str, Throwable th) {
        this(str);
        Writer stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        this.throwableStackTrace = stringWriter.toString();
    }

    public static CrashDetails fromFile(File file) throws IOException {
        return fromReader(file.getName().substring(0, file.getName().indexOf(".stacktrace")), new FileReader(file));
    }

    public static CrashDetails fromReader(String str, Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        CrashDetails crashDetails = new CrashDetails(str);
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                crashDetails.setThrowableStackTrace(stringBuilder.toString());
                return crashDetails;
            } else if (i != 0) {
                stringBuilder.append(readLine).append("\n");
            } else if (readLine.isEmpty()) {
                i = 1;
            } else {
                int indexOf = readLine.indexOf(":");
                if (indexOf < 0) {
                    HockeyLog.error("Malformed header line when parsing crash details: \"" + readLine + "\"");
                }
                String trim = readLine.substring(0, indexOf).trim();
                readLine = readLine.substring(indexOf + 1, readLine.length()).trim();
                if (trim.equals(FIELD_CRASH_REPORTER_KEY)) {
                    crashDetails.setReporterKey(readLine);
                } else if (trim.equals(FIELD_APP_START_DATE)) {
                    try {
                        crashDetails.setAppStartDate(DATE_FORMAT.parse(readLine));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                } else if (trim.equals(FIELD_APP_CRASH_DATE)) {
                    try {
                        crashDetails.setAppCrashDate(DATE_FORMAT.parse(readLine));
                    } catch (Throwable e2) {
                        throw new RuntimeException(e2);
                    }
                } else if (trim.equals(FIELD_OS_VERSION)) {
                    crashDetails.setOsVersion(readLine);
                } else if (trim.equals(FIELD_OS_BUILD)) {
                    crashDetails.setOsBuild(readLine);
                } else if (trim.equals(FIELD_DEVICE_MANUFACTURER)) {
                    crashDetails.setDeviceManufacturer(readLine);
                } else if (trim.equals(FIELD_DEVICE_MODEL)) {
                    crashDetails.setDeviceModel(readLine);
                } else if (trim.equals(FIELD_APP_PACKAGE)) {
                    crashDetails.setAppPackage(readLine);
                } else if (trim.equals(FIELD_APP_VERSION_NAME)) {
                    crashDetails.setAppVersionName(readLine);
                } else if (trim.equals(FIELD_APP_VERSION_CODE)) {
                    crashDetails.setAppVersionCode(readLine);
                } else if (trim.equals(FIELD_THREAD_NAME)) {
                    crashDetails.setThreadName(readLine);
                }
            }
        }
    }

    private void writeHeader(Writer writer, String str, String str2) throws IOException {
        writer.write(str + ": " + str2 + "\n");
    }

    public Date getAppCrashDate() {
        return this.appCrashDate;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public Date getAppStartDate() {
        return this.appStartDate;
    }

    public String getAppVersionCode() {
        return this.appVersionCode;
    }

    public String getAppVersionName() {
        return this.appVersionName;
    }

    public String getCrashIdentifier() {
        return this.crashIdentifier;
    }

    public String getDeviceManufacturer() {
        return this.deviceManufacturer;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public String getOsBuild() {
        return this.osBuild;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public String getReporterKey() {
        return this.reporterKey;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public String getThrowableStackTrace() {
        return this.throwableStackTrace;
    }

    public void setAppCrashDate(Date date) {
        this.appCrashDate = date;
    }

    public void setAppPackage(String str) {
        this.appPackage = str;
    }

    public void setAppStartDate(Date date) {
        this.appStartDate = date;
    }

    public void setAppVersionCode(String str) {
        this.appVersionCode = str;
    }

    public void setAppVersionName(String str) {
        this.appVersionName = str;
    }

    public void setDeviceManufacturer(String str) {
        this.deviceManufacturer = str;
    }

    public void setDeviceModel(String str) {
        this.deviceModel = str;
    }

    public void setOsBuild(String str) {
        this.osBuild = str;
    }

    public void setOsVersion(String str) {
        this.osVersion = str;
    }

    public void setReporterKey(String str) {
        this.reporterKey = str;
    }

    public void setThreadName(String str) {
        this.threadName = str;
    }

    public void setThrowableStackTrace(String str) {
        this.throwableStackTrace = str;
    }

    public void writeCrashReport() {
        Throwable e;
        String str = Constants.FILES_PATH + "/" + this.crashIdentifier + ".stacktrace";
        HockeyLog.debug("Writing unhandled exception to: " + str);
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(str));
            try {
                writeHeader(bufferedWriter2, FIELD_APP_PACKAGE, this.appPackage);
                writeHeader(bufferedWriter2, FIELD_APP_VERSION_CODE, this.appVersionCode);
                writeHeader(bufferedWriter2, FIELD_APP_VERSION_NAME, this.appVersionName);
                writeHeader(bufferedWriter2, FIELD_OS_VERSION, this.osVersion);
                writeHeader(bufferedWriter2, FIELD_OS_BUILD, this.osBuild);
                writeHeader(bufferedWriter2, FIELD_DEVICE_MANUFACTURER, this.deviceManufacturer);
                writeHeader(bufferedWriter2, FIELD_DEVICE_MODEL, this.deviceModel);
                writeHeader(bufferedWriter2, FIELD_THREAD_NAME, this.threadName);
                writeHeader(bufferedWriter2, FIELD_CRASH_REPORTER_KEY, this.reporterKey);
                writeHeader(bufferedWriter2, FIELD_APP_START_DATE, DATE_FORMAT.format(this.appStartDate));
                writeHeader(bufferedWriter2, FIELD_APP_CRASH_DATE, DATE_FORMAT.format(this.appCrashDate));
                bufferedWriter2.write("\n");
                bufferedWriter2.write(this.throwableStackTrace);
                bufferedWriter2.flush();
                if (bufferedWriter2 != null) {
                    try {
                        bufferedWriter2.close();
                    } catch (Throwable e2) {
                        HockeyLog.error("Error saving crash report!", e2);
                    }
                }
            } catch (IOException e3) {
                e2 = e3;
                bufferedWriter = bufferedWriter2;
                try {
                    HockeyLog.error("Error saving crash report!", e2);
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (Throwable e22) {
                            HockeyLog.error("Error saving crash report!", e22);
                        }
                    }
                } catch (Throwable th) {
                    e22 = th;
                    bufferedWriter2 = bufferedWriter;
                    if (bufferedWriter2 != null) {
                        try {
                            bufferedWriter2.close();
                        } catch (Throwable e4) {
                            HockeyLog.error("Error saving crash report!", e4);
                        }
                    }
                    throw e22;
                }
            } catch (Throwable th2) {
                e22 = th2;
                if (bufferedWriter2 != null) {
                    bufferedWriter2.close();
                }
                throw e22;
            }
        } catch (IOException e5) {
            e22 = e5;
            HockeyLog.error("Error saving crash report!", e22);
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }
}
