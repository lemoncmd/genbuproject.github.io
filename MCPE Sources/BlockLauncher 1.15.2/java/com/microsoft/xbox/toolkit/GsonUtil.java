package com.microsoft.xbox.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import org.mozilla.javascript.Token;

public class GsonUtil {

    public interface JsonBodyBuilder {
        void buildBody(JsonWriter jsonWriter) throws IOException;
    }

    public static String buildJsonBody(JsonBodyBuilder jsonBodyBuilder) throws IOException {
        Writer stringWriter = new StringWriter();
        JsonWriter jsonWriter;
        try {
            jsonWriter = new JsonWriter(stringWriter);
            jsonBodyBuilder.buildBody(jsonWriter);
            String stringWriter2 = stringWriter.toString();
            jsonWriter.close();
            stringWriter.close();
            return stringWriter2;
        } catch (Throwable th) {
            stringWriter.close();
        }
    }

    public static GsonBuilder createMinimumGsonBuilder() {
        return new GsonBuilder().excludeFieldsWithModifiers(new int[]{Token.RESERVED});
    }

    public static <T> T deserializeJson(Gson gson, InputStream inputStream, Class<T> cls) {
        BufferedReader bufferedReader;
        T fromJson;
        Throwable th;
        BufferedReader bufferedReader2 = null;
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (Exception e) {
                bufferedReader = bufferedReader2;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e2) {
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception e3) {
                    }
                }
                return fromJson;
            } catch (Throwable th2) {
                th = th2;
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (Exception e4) {
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception e5) {
                    }
                }
                throw th;
            }
            try {
                fromJson = gson.fromJson(bufferedReader, cls);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e6) {
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception e7) {
                    }
                }
            } catch (Exception e8) {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                return fromJson;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                bufferedReader2 = bufferedReader;
                th = th4;
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                throw th;
            }
        } catch (Exception e9) {
            bufferedReader = bufferedReader2;
            inputStreamReader = bufferedReader2;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            return fromJson;
        } catch (Throwable th5) {
            th = th5;
            inputStreamReader = bufferedReader2;
            if (bufferedReader2 != null) {
                bufferedReader2.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            throw th;
        }
        return fromJson;
    }

    public static <T> T deserializeJson(Gson gson, String str, Class<T> cls) {
        T t = null;
        try {
            t = gson.fromJson(str, cls);
        } catch (Exception e) {
        }
        return t;
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls) {
        return deserializeJson(createMinimumGsonBuilder().create(), inputStream, (Class) cls);
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls, Type type, Object obj) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(type, obj).create(), inputStream, (Class) cls);
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls, Map<Type, Object> map) {
        GsonBuilder createMinimumGsonBuilder = createMinimumGsonBuilder();
        for (Entry entry : map.entrySet()) {
            createMinimumGsonBuilder.registerTypeAdapter((Type) entry.getKey(), entry.getValue());
        }
        return deserializeJson(createMinimumGsonBuilder.create(), inputStream, (Class) cls);
    }

    public static <T> T deserializeJson(String str, Class<T> cls) {
        return deserializeJson(createMinimumGsonBuilder().create(), str, (Class) cls);
    }

    public static <T> T deserializeJson(String str, Class<T> cls, Type type, Object obj) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(type, obj).create(), str, (Class) cls);
    }

    public static String toJsonString(Object obj) {
        return new Gson().toJson(obj);
    }
}
