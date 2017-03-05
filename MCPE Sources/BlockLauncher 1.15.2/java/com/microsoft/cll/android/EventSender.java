package com.microsoft.cll.android;

import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;
import net.hockeyapp.android.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class EventSender {
    private final String NO_HTTPS_CONN = "URL didn't return HttpsUrlConnection instance.";
    private final String TAG = "AndroidCll-EventSender";
    private final ClientTelemetry clientTelemetry;
    private final URL endpoint;
    private final ILogger logger;

    public EventSender(URL url, ClientTelemetry clientTelemetry, ILogger iLogger) {
        this.endpoint = url;
        this.clientTelemetry = clientTelemetry;
        this.logger = iLogger;
    }

    private long getTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTimeInMillis();
    }

    protected HttpURLConnection openConnection(int i, boolean z, TicketHeaders ticketHeaders) throws IOException {
        String str = BuildConfig.FLAVOR;
        if (!(ticketHeaders == null || ticketHeaders.xtokens.isEmpty())) {
            boolean z2 = true;
            for (Entry entry : ticketHeaders.xtokens.entrySet()) {
                str = (!z2 ? str + ";" : str) + "\"" + ((String) entry.getKey()) + "\"=\"" + ((String) entry.getValue()) + "\"";
                z2 = false;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        URLConnection openConnection = this.endpoint.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setConnectTimeout(SettingsStore.getCllSettingsAsInt(Settings.HTTPTIMEOUTINTERVAL));
            httpURLConnection.setReadTimeout(SettingsStore.getCllSettingsAsInt(Settings.HTTPTIMEOUTINTERVAL));
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(HttpEngine.POST);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-json-stream; charset=utf-8");
            httpURLConnection.setRequestProperty("X-UploadTime", simpleDateFormat.format(new Date()).toString());
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(i));
            if (z) {
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                httpURLConnection.setRequestProperty("Content-Encoding", "deflate");
            }
            if (str != BuildConfig.FLAVOR) {
                httpURLConnection.setRequestProperty("X-Tickets", str);
                httpURLConnection.setRequestProperty("X-AuthXToken", ticketHeaders.authXToken);
                if (ticketHeaders.msaDeviceTicket != null) {
                    httpURLConnection.setRequestProperty("X-AuthMsaDeviceTicket", ticketHeaders.msaDeviceTicket);
                }
            }
            return httpURLConnection;
        }
        this.clientTelemetry.IncrementVortexHttpFailures(-1);
        throw new IOException("URL didn't return HttpsUrlConnection instance.");
    }

    protected String processResponseBody(BufferedReader bufferedReader) {
        return processResponseBodyConditionally(bufferedReader, true);
    }

    protected String processResponseBodyConditionally(BufferedReader bufferedReader, boolean z) {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine);
            } catch (IOException e) {
                this.logger.error("AndroidCll-EventSender", "Couldn't read response body");
            }
        }
        if (z) {
            try {
                this.clientTelemetry.IncrementRejectDropCount(new JSONObject(stringBuilder.toString()).getInt("rej"));
            } catch (JSONException e2) {
                this.logger.info("AndroidCll-EventSender", e2.getMessage());
            } catch (RuntimeException e3) {
                this.logger.info("AndroidCll-EventSender", e3.getMessage());
            }
        }
        this.logger.info("AndroidCll-EventSender", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendEvent(byte[] r10, boolean r11, com.microsoft.cll.android.TicketHeaders r12) throws java.io.IOException {
        /*
        r9 = this;
        r3 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0 = 0;
        r2 = 0;
        r1 = r9.clientTelemetry;
        r1.IncrementVortexHttpAttempts();
        r1 = r10.length;
        r4 = r9.openConnection(r1, r11, r12);
        r4.connect();	 Catch:{ all -> 0x0086 }
        r1 = r9.logger;
        r5 = "AndroidCll-EventSender";
        r6 = "Error connecting.";
        r1.error(r5, r6);
        r1 = r4.getOutputStream();	 Catch:{ all -> 0x0091 }
        r1.write(r10);	 Catch:{ all -> 0x0091 }
        r1.flush();	 Catch:{ all -> 0x0091 }
        r1.close();	 Catch:{ all -> 0x0091 }
        r1 = r9.logger;
        r5 = "AndroidCll-EventSender";
        r6 = "Error writing data";
        r1.error(r5, r6);
        r6 = r9.getTime();
        r3 = r4.getResponseCode();	 Catch:{ IOException -> 0x00f5, all -> 0x00f8 }
    L_0x0038:
        r0 = r4.getInputStream();	 Catch:{ IOException -> 0x009e, all -> 0x00f8 }
        if (r0 == 0) goto L_0x0050;
    L_0x003e:
        r5 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x009e }
        r1 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x009e }
        r1.<init>(r0);	 Catch:{ IOException -> 0x009e }
        r5.<init>(r1);	 Catch:{ IOException -> 0x009e }
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 != r1) goto L_0x009c;
    L_0x004c:
        r1 = 1;
    L_0x004d:
        r9.processResponseBodyConditionally(r5, r1);	 Catch:{ IOException -> 0x009e }
    L_0x0050:
        if (r0 == 0) goto L_0x0055;
    L_0x0052:
        r0.close();
    L_0x0055:
        if (r2 == 0) goto L_0x005a;
    L_0x0057:
        r2.close();
    L_0x005a:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r3 < r0) goto L_0x0074;
    L_0x005e:
        r0 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        if (r3 >= r0) goto L_0x0074;
    L_0x0062:
        r0 = r9.logger;
        r1 = "AndroidCll-EventSender";
        r2 = "Bad Response Code";
        r0.error(r1, r2);
        r0 = r9.clientTelemetry;
        r1 = r4.getResponseCode();
        r0.IncrementVortexHttpFailures(r1);
    L_0x0074:
        r0 = r9.getTime();
        r0 = r0 - r6;
        r2 = r9.clientTelemetry;
        r4 = (int) r0;
        r2.SetAvgVortexLatencyMs(r4);
        r2 = r9.clientTelemetry;
        r0 = (int) r0;
        r2.SetMaxVortexLatencyMs(r0);
        return r3;
    L_0x0086:
        r0 = move-exception;
        r1 = r9.logger;
        r2 = "AndroidCll-EventSender";
        r3 = "Error connecting.";
        r1.error(r2, r3);
        throw r0;
    L_0x0091:
        r0 = move-exception;
        r1 = r9.logger;
        r2 = "AndroidCll-EventSender";
        r3 = "Error writing data";
        r1.error(r2, r3);
        throw r0;
    L_0x009c:
        r1 = 0;
        goto L_0x004d;
    L_0x009e:
        r1 = move-exception;
        r2 = r4.getErrorStream();	 Catch:{ all -> 0x00fe }
        if (r2 == 0) goto L_0x0050;
    L_0x00a5:
        r5 = new java.io.BufferedReader;	 Catch:{ all -> 0x00b8 }
        r1 = new java.io.InputStreamReader;	 Catch:{ all -> 0x00b8 }
        r1.<init>(r2);	 Catch:{ all -> 0x00b8 }
        r5.<init>(r1);	 Catch:{ all -> 0x00b8 }
        r1 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r3 != r1) goto L_0x00f3;
    L_0x00b3:
        r1 = 1;
    L_0x00b4:
        r9.processResponseBodyConditionally(r5, r1);	 Catch:{ all -> 0x00b8 }
        goto L_0x0050;
    L_0x00b8:
        r1 = move-exception;
        r8 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r8;
    L_0x00bd:
        if (r2 == 0) goto L_0x00c2;
    L_0x00bf:
        r2.close();
    L_0x00c2:
        if (r1 == 0) goto L_0x00c7;
    L_0x00c4:
        r1.close();
    L_0x00c7:
        r1 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r3 < r1) goto L_0x00e1;
    L_0x00cb:
        r1 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        if (r3 >= r1) goto L_0x00e1;
    L_0x00cf:
        r1 = r9.logger;
        r2 = "AndroidCll-EventSender";
        r3 = "Bad Response Code";
        r1.error(r2, r3);
        r1 = r9.clientTelemetry;
        r2 = r4.getResponseCode();
        r1.IncrementVortexHttpFailures(r2);
    L_0x00e1:
        r2 = r9.getTime();
        r2 = r2 - r6;
        r1 = r9.clientTelemetry;
        r4 = (int) r2;
        r1.SetAvgVortexLatencyMs(r4);
        r1 = r9.clientTelemetry;
        r2 = (int) r2;
        r1.SetMaxVortexLatencyMs(r2);
        throw r0;
    L_0x00f3:
        r1 = 0;
        goto L_0x00b4;
    L_0x00f5:
        r1 = move-exception;
        goto L_0x0038;
    L_0x00f8:
        r1 = move-exception;
        r8 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r8;
        goto L_0x00bd;
    L_0x00fe:
        r1 = move-exception;
        r8 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r8;
        goto L_0x00bd;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.cll.android.EventSender.sendEvent(byte[], boolean, com.microsoft.cll.android.TicketHeaders):int");
    }
}
