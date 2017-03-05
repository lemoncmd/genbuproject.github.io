package com.microsoft.xbox.toolkit;

import android.util.Pair;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpCall.Callback;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class TcuiHttpUtil {
    public static String getResponseBodySync(HttpCall httpCall) throws XLEException {
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(Boolean.valueOf(false), null));
        httpCall.getResponseAsync(new Callback() {
            public void processHttpError(int i, int i2, String str) {
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(Boolean.valueOf(true), null));
                    atomicReference.notify();
                }
            }

            public void processResponse(InputStream inputStream) throws Exception {
                Object obj = null;
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, HttpURLConnectionBuilder.DEFAULT_CHARSET), EnchantType.fishingRod);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        stringBuilder.append(readLine + "\n");
                    }
                    obj = stringBuilder.toString();
                } catch (IOException e) {
                    XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + e.getMessage(), false);
                }
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(Boolean.valueOf(true), obj));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return (String) ((Pair) atomicReference.get()).second;
    }

    public static <T> T getResponseSync(HttpCall httpCall, final Class<T> cls) throws XLEException {
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(Boolean.valueOf(false), null));
        httpCall.getResponseAsync(new Callback() {
            public void processHttpError(int i, int i2, String str) {
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(Boolean.valueOf(true), null));
                    atomicReference.notify();
                }
            }

            public void processResponse(InputStream inputStream) throws Exception {
                Object deserializeJson = GsonUtil.deserializeJson(inputStream, cls);
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(Boolean.valueOf(true), deserializeJson));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return ((Pair) atomicReference.get()).second;
    }

    public static boolean getResponseSyncSucceeded(HttpCall httpCall, final List<Integer> list) {
        final AtomicReference atomicReference = new AtomicReference();
        httpCall.getResponseAsync(new Callback() {
            public void processHttpError(int i, int i2, String str) {
                boolean contains = list.contains(Integer.valueOf(i2));
                synchronized (atomicReference) {
                    atomicReference.set(Boolean.valueOf(contains));
                    atomicReference.notify();
                }
            }

            public void processResponse(InputStream inputStream) throws Exception {
                synchronized (atomicReference) {
                    atomicReference.set(Boolean.valueOf(true));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (atomicReference.get() == null) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return ((Boolean) atomicReference.get()).booleanValue();
    }

    public static <T> void throwIfNullOrFalse(T t) throws XLEException {
        if (t == null && !Boolean.getBoolean(t.toString())) {
            throw new XLEException(2);
        }
    }
}
