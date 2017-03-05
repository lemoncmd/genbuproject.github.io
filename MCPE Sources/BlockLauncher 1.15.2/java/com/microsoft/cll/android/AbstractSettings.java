package com.microsoft.cll.android;

import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;

public abstract class AbstractSettings {
    protected Settings ETagSettingName;
    protected String TAG = "AndroidCll-AbstractSettings";
    protected final ClientTelemetry clientTelemetry;
    protected boolean disableUploadOn404 = false;
    protected String endpoint;
    protected final ILogger logger;
    private final PartA partA;
    protected String queryParam;

    protected AbstractSettings(ClientTelemetry clientTelemetry, ILogger iLogger, PartA partA) {
        this.clientTelemetry = clientTelemetry;
        this.logger = iLogger;
        this.partA = partA;
    }

    public abstract void ParseSettings(JSONObject jSONObject);

    public JSONObject getSettings() {
        URLConnection openConnection;
        IOException e;
        Throwable th;
        JSONException e2;
        this.logger.info(this.TAG, "Get Settings");
        try {
            try {
                openConnection = new URL(this.endpoint + this.queryParam).openConnection();
                try {
                    if (openConnection instanceof HttpsURLConnection) {
                        this.clientTelemetry.IncrementSettingsHttpAttempts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) openConnection;
                        httpsURLConnection.getConnectTimeout();
                        httpsURLConnection.getReadTimeout();
                        httpsURLConnection.setConnectTimeout(SettingsStore.getCllSettingsAsInt(Settings.HTTPTIMEOUTINTERVAL));
                        httpsURLConnection.setReadTimeout(SettingsStore.getCllSettingsAsInt(Settings.HTTPTIMEOUTINTERVAL));
                        httpsURLConnection.setRequestMethod(HttpEngine.GET);
                        httpsURLConnection.setRequestProperty("Accept", "application/json");
                        httpsURLConnection.setRequestProperty("If-None-Match", SettingsStore.getCllSettingsAsString(this.ETagSettingName));
                        long timeInMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTimeInMillis();
                        httpsURLConnection.connect();
                        timeInMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTimeInMillis() - timeInMillis;
                        this.clientTelemetry.SetAvgSettingsLatencyMs((int) timeInMillis);
                        this.clientTelemetry.SetMaxSettingsLatencyMs((int) timeInMillis);
                        if (httpsURLConnection.getResponseCode() == 404 && this.disableUploadOn404) {
                            this.logger.info(this.TAG, "Your iKey is invalid. Your events will not be sent!");
                            SettingsStore.updateCllSetting(Settings.UPLOADENABLED, "false");
                        } else if (httpsURLConnection.getResponseCode() != 404 && this.disableUploadOn404) {
                            this.logger.info(this.TAG, "Your iKey is valid.");
                            SettingsStore.updateCllSetting(Settings.UPLOADENABLED, "true");
                        }
                        if (httpsURLConnection.getResponseCode() == Context.VERSION_ES6 || httpsURLConnection.getResponseCode() == 304) {
                            String headerField = httpsURLConnection.getHeaderField("ETAG");
                            if (!(headerField == null || headerField.isEmpty())) {
                                SettingsStore.updateCllSetting(this.ETagSettingName, headerField);
                            }
                        } else {
                            this.clientTelemetry.IncrementSettingsHttpFailures(httpsURLConnection.getResponseCode());
                        }
                        if (httpsURLConnection.getResponseCode() != Context.VERSION_ES6) {
                            httpsURLConnection.disconnect();
                            return null;
                        }
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        while (true) {
                            String readLine = bufferedReader.readLine();
                            if (readLine != null) {
                                stringBuilder.append(readLine);
                            } else {
                                bufferedReader.close();
                                httpsURLConnection.disconnect();
                                return new JSONObject(stringBuilder.toString());
                            }
                        }
                    }
                    if (openConnection != null) {
                        try {
                            openConnection.getInputStream().close();
                        } catch (Exception e3) {
                            this.logger.error(this.TAG, e3.getMessage());
                        }
                    }
                    return null;
                } catch (IOException e4) {
                    e = e4;
                    try {
                        this.logger.error(this.TAG, e.getMessage());
                        this.clientTelemetry.IncrementSettingsHttpFailures(-1);
                        if (openConnection != null) {
                            try {
                                openConnection.getInputStream().close();
                            } catch (Exception e32) {
                                this.logger.error(this.TAG, e32.getMessage());
                            }
                        }
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        if (openConnection != null) {
                            try {
                                openConnection.getInputStream().close();
                            } catch (Exception e5) {
                                this.logger.error(this.TAG, e5.getMessage());
                            }
                        }
                        throw th;
                    }
                } catch (JSONException e6) {
                    e2 = e6;
                    this.logger.error(this.TAG, e2.getMessage());
                    if (openConnection != null) {
                        try {
                            openConnection.getInputStream().close();
                        } catch (Exception e322) {
                            this.logger.error(this.TAG, e322.getMessage());
                        }
                    }
                    return null;
                }
            } catch (IOException e7) {
                e = e7;
                openConnection = null;
                this.logger.error(this.TAG, e.getMessage());
                this.clientTelemetry.IncrementSettingsHttpFailures(-1);
                if (openConnection != null) {
                    openConnection.getInputStream().close();
                }
                return null;
            } catch (JSONException e8) {
                e2 = e8;
                openConnection = null;
                this.logger.error(this.TAG, e2.getMessage());
                if (openConnection != null) {
                    openConnection.getInputStream().close();
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                openConnection = null;
                if (openConnection != null) {
                    openConnection.getInputStream().close();
                }
                throw th;
            }
        } catch (MalformedURLException e9) {
            this.logger.error(this.TAG, "Settings URL is invalid");
            return null;
        }
    }
}
