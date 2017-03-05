package com.microsoft.xbox.idp.model.serialization;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import net.hockeyapp.android.BuildConfig;

public class UTCDateConverter {
    private static final int NO_MS_STRING_LENGTH = 19;
    private static final String TAG = UTCDateConverter.class.getSimpleName();
    private static SimpleDateFormat defaultFormatMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    private static SimpleDateFormat defaultFormatNoMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat shortDateAlternateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);

    public static class UTCDateConverterJSONDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return UTCDateConverter.convert(jsonElement.getAsJsonPrimitive().getAsString());
        }

        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(UTCDateConverter.defaultFormatNoMs.format(date));
        }
    }

    public static class UTCDateConverterShortDateAlternateFormatJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            Date date = null;
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            UTCDateConverter.shortDateFormat.setTimeZone(timeZone);
            try {
                date = UTCDateConverter.shortDateFormat.parse(asString);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse short date " + asString);
            }
            if (date != null && date.getYear() + 1900 < 2000) {
                UTCDateConverter.shortDateAlternateFormat.setTimeZone(timeZone);
                try {
                    date = UTCDateConverter.shortDateAlternateFormat.parse(asString);
                } catch (ParseException e2) {
                    Log.d(UTCDateConverter.TAG, "failed to parse alternate short date " + asString);
                }
            }
            return date;
        }
    }

    public static class UTCDateConverterShortDateFormatJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            UTCDateConverter.shortDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverter.shortDateFormat.parse(asString);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + asString);
                return null;
            }
        }
    }

    public static class UTCRoundtripDateConverterJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            Date date = null;
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            String replace = asString.endsWith("Z") ? asString.replace("Z", BuildConfig.FLAVOR) : asString;
            UTCDateConverter.defaultFormatNoMs.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                date = UTCDateConverter.defaultFormatNoMs.parse(replace);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + replace);
            }
            return date;
        }
    }

    public static Date convert(String str) {
        Date date = null;
        synchronized (UTCDateConverter.class) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    TimeZone timeZone;
                    String replace;
                    String replace2 = str.endsWith("Z") ? str.replace("Z", BuildConfig.FLAVOR) : str;
                    if (replace2.endsWith("+00:00")) {
                        timeZone = date;
                        replace = replace2.replace("+00:00", BuildConfig.FLAVOR);
                    } else if (replace2.endsWith("+01:00")) {
                        replace = replace2.replace("+01:00", BuildConfig.FLAVOR);
                        timeZone = TimeZone.getTimeZone("GMT+01:00");
                    } else if (replace2.contains(".")) {
                        r2 = date;
                        replace = replace2.replaceAll("([.][0-9]{3})[0-9]*$", "$1");
                    } else {
                        r2 = date;
                        replace = replace2;
                    }
                    Object obj = replace.length() == NO_MS_STRING_LENGTH ? 1 : null;
                    if (timeZone == null) {
                        timeZone = TimeZone.getTimeZone("GMT");
                    }
                    if (obj != null) {
                        defaultFormatNoMs.setTimeZone(timeZone);
                        date = defaultFormatNoMs.parse(replace);
                    } else {
                        defaultFormatMs.setTimeZone(timeZone);
                        date = defaultFormatMs.parse(replace);
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
            } catch (Throwable th) {
                Class cls = UTCDateConverter.class;
            }
        }
        return date;
    }
}
