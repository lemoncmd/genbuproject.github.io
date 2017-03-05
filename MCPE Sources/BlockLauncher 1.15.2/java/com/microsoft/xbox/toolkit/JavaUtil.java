package com.microsoft.xbox.toolkit;

import android.graphics.Rect;
import android.text.format.DateUtils;
import android.view.View;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.mozilla.javascript.regexp.NativeRegExp;

public class JavaUtil {
    private static final String HEX_PREFIX = "0x";
    private static final NumberFormat INTEGER_FORMATTER = NumberFormat.getIntegerInstance(Locale.getDefault());
    private static final Date MIN_DATE = new Date(100, 1, 1);
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance(Locale.getDefault());

    public static <T> boolean DeepCompareArrayList(ArrayList<T> arrayList, ArrayList<T> arrayList2) {
        if (arrayList != arrayList2) {
            if (arrayList == null) {
                if (arrayList2 != null) {
                    return false;
                }
            } else if (arrayList2 == null || arrayList.size() != arrayList2.size()) {
                return false;
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!arrayList.get(i).equals(arrayList2.get(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static String EnsureEncode(String str) {
        if (!(str == null || str.length() == 0)) {
            try {
                str = URLEncoder.encode(URLDecoder.decode(str, HttpURLConnectionBuilder.DEFAULT_CHARSET), HttpURLConnectionBuilder.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return str;
    }

    public static Date JSONDateToJavaDate(String str) {
        if (isNullOrEmpty(str)) {
            return null;
        }
        XLEAssert.assertTrue(str.substring(0, 6).equals("/Date("));
        int length = str.length();
        if (str.substring(str.length() - 7, str.length()).equals("+0000)/")) {
            length = str.length() - 7;
        } else if (str.substring(str.length() - 2, str.length()).equals(")/")) {
            length = str.length() - 2;
        } else {
            XLEAssert.assertTrue(false);
        }
        return new Date(Long.parseLong(str.substring(6, length)));
    }

    public static String JavaDateToJSONDate(Date date) {
        new GregorianCalendar(TimeZone.getTimeZone("GMT")).setTime(date);
        return String.format("/Date(%d)/", new Object[]{Long.valueOf(r0.getTimeInMillis())});
    }

    public static int[] concatIntArrays(int[]... iArr) {
        if (iArr == null) {
            return null;
        }
        int i = 0;
        for (int[] length : iArr) {
            i += length.length;
        }
        Object obj = new int[i];
        int i2 = 0;
        for (Object obj2 : iArr) {
            System.arraycopy(obj2, 0, obj, i2, obj2.length);
            i2 += obj2.length;
        }
        return obj;
    }

    public static String concatenateStringsWithDelimiter(String str, String str2, String str3, String str4) {
        return concatenateStringsWithDelimiter(str, str2, str3, str4, true);
    }

    public static String concatenateStringsWithDelimiter(String str, String str2, String str3, String str4, boolean z) {
        String str5 = (z ? " " : BuildConfig.FLAVOR) + str4 + " ";
        StringBuilder stringBuilder = new StringBuilder();
        if (!isNullOrEmpty(str)) {
            stringBuilder.append(str);
        }
        if (!isNullOrEmpty(str2)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(str5);
            }
            stringBuilder.append(str2);
        }
        if (!isNullOrEmpty(str3)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(str5);
            }
            stringBuilder.append(str3);
        }
        return stringBuilder.toString();
    }

    public static String concatenateStringsWithDelimiter(String str, boolean z, String... strArr) {
        String str2 = (z ? " " : BuildConfig.FLAVOR) + str + " ";
        StringBuilder stringBuilder = new StringBuilder();
        if (strArr.length == 0) {
            return BuildConfig.FLAVOR;
        }
        for (int i = 0; i < strArr.length; i++) {
            if (!isNullOrEmpty(strArr[i])) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(str2);
                }
                stringBuilder.append(strArr[i]);
            }
        }
        return stringBuilder.toString();
    }

    public static String concatenateUrlWithLinkAndParam(String str, String str2, String str3) {
        StringBuffer stringBuffer = new StringBuffer();
        if (!isNullOrEmpty(str)) {
            stringBuffer.append(str);
        }
        if (!isNullOrEmpty(str2)) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(str3);
            }
            stringBuffer.append(str2);
        }
        return stringBuffer.toString();
    }

    public static boolean containsFlag(int i, int i2) {
        return (i & i2) == i2;
    }

    public static Date convertToUTC(Date date) {
        if (date == null) {
            return null;
        }
        TimeZone timeZone = TimeZone.getDefault();
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(14, -timeZone.getOffset(date.getTime()));
        return instance.getTime();
    }

    public static String formatInteger(int i) {
        return INTEGER_FORMATTER.format((long) i);
    }

    public static String formatPercent(float f) {
        String str = f + " is not between 0 and 1";
        boolean z = f >= 0.0f && f <= 1.0f;
        XLEAssert.assertTrue(str, z);
        return PERCENT_FORMATTER.format((double) f);
    }

    public static String getCurrentStackTraceAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                stringBuilder.append("\n\n \t " + stackTraceElement.toString());
            }
        }
        return stringBuilder.toString();
    }

    public static String getLocalizedDateString(Date date) {
        try {
            return DateUtils.formatDateTime(XboxTcuiSdk.getApplicationContext(), date.getTime(), 131088);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getShortClassName(Class cls) {
        String[] split = cls.getName().split("\\.");
        return split[split.length - 1];
    }

    public static String getTimeStringMMSS(long j) {
        return DateUtils.formatElapsedTime(j);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static <T> boolean isPositionInRange(ArrayList<T> arrayList, int i) {
        return i >= 0 && i < arrayList.size();
    }

    public static boolean isTouchPointInsideView(float f, float f2, View view) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight()).contains((int) f, (int) f2);
    }

    public static <T> List<T> listIteratorToList(ListIterator<T> listIterator) {
        List arrayList = new ArrayList();
        while (listIterator != null && listIterator.hasNext()) {
            arrayList.add(listIterator.next());
        }
        return arrayList;
    }

    public static <T> boolean move(ArrayList<T> arrayList, int i, int i2) {
        if (arrayList == null || !isPositionInRange(arrayList, i) || !isPositionInRange(arrayList, i2)) {
            return false;
        }
        Object obj = arrayList.get(i);
        if (i < i2) {
            while (i < i2) {
                arrayList.set(i, arrayList.get(i + 1));
                i++;
            }
        } else {
            while (i > i2) {
                arrayList.set(i, arrayList.get(i - 1));
                i--;
            }
        }
        arrayList.set(i2, obj);
        return true;
    }

    private static boolean parseBoolean(String str) {
        boolean z = false;
        try {
            z = Boolean.parseBoolean(str);
        } catch (Exception e) {
        }
        return z;
    }

    public static long parseHexLong(String str) {
        long j = 0;
        if (str == null) {
            return j;
        }
        if (str.startsWith(HEX_PREFIX)) {
            return parseHexLongExpectHex(str);
        }
        try {
            return Long.parseLong(str, 16);
        } catch (Exception e) {
            return j;
        }
    }

    private static long parseHexLongExpectHex(String str) {
        XLEAssert.assertTrue(str.startsWith(HEX_PREFIX));
        long j = 0;
        try {
            j = Long.parseLong(str.substring(HEX_PREFIX.length()), 16);
        } catch (Exception e) {
        }
        return j;
    }

    public static int parseInteger(String str) {
        int i = 0;
        try {
            i = Integer.parseInt(str, 10);
        } catch (Exception e) {
        }
        return i;
    }

    public static String pluralize(int i, String str, String str2, String str3) {
        switch (i) {
            case NativeRegExp.TEST /*0*/:
                return str;
            case NativeRegExp.MATCH /*1*/:
                return str2;
            default:
                return String.format(str3, new Object[]{Integer.valueOf(i)});
        }
    }

    public static int randInRange(Random random, int i, int i2) {
        XLEAssert.assertTrue(i2 >= i);
        return random.nextInt(i2 - i) + i;
    }

    public static boolean setFieldValue(Object obj, String str, Object obj2) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            declaredField.set(obj, obj2);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalAccessException e2) {
            return false;
        }
    }

    public static void sleepDebug(long j) {
    }

    public static String stringToLower(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public static String stringToUpper(String str) {
        return str == null ? null : str.toUpperCase();
    }

    public static boolean stringsEqual(String str, String str2) {
        return ((str == null && str2 == null) || str == str2) ? true : stringsEqualNonNull(str, str2);
    }

    public static boolean stringsEqualCaseInsensitive(String str, String str2) {
        boolean z = true;
        if (str == str2) {
            return true;
        }
        if (str == null || str2 == null) {
            return false;
        }
        if (str == null || str2 == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return str.equalsIgnoreCase(str2);
    }

    public static boolean stringsEqualNonNull(String str, String str2) {
        boolean z = false;
        if (str == null || str2 == null) {
            return false;
        }
        if (!(str == null || str2 == null)) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        return str.equals(str2);
    }

    public static boolean stringsEqualNonNullCaseInsensitive(String str, String str2) {
        boolean z = true;
        if (str == null || str2 == null) {
            return false;
        }
        if (str == str2) {
            return true;
        }
        if (str == null || str2 == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return str.equalsIgnoreCase(str2);
    }

    public static <T> ArrayList<T> sublistShuffle(ArrayList<T> arrayList, int i) {
        boolean z = false;
        Random random = new Random();
        ArrayList<T> arrayList2 = new ArrayList(i);
        if (!(arrayList == null || arrayList.size() == 0)) {
            int i2;
            if (arrayList.size() >= i) {
                for (i2 = 0; i2 < i; i2++) {
                    int randInRange = randInRange(random, i2, arrayList.size());
                    Object obj = arrayList.get(i2);
                    arrayList.set(i2, arrayList.get(randInRange));
                    arrayList.set(randInRange, obj);
                    arrayList2.add(arrayList.get(i2));
                }
            } else {
                boolean z2 = arrayList.size() > 0 && arrayList.size() < i;
                XLEAssert.assertTrue(z2);
                for (i2 = 0; i2 < i; i2++) {
                    arrayList2.add(arrayList.get(random.nextInt(arrayList.size())));
                }
            }
            if (arrayList2.size() == i) {
                z = true;
            }
            XLEAssert.assertTrue(z);
        }
        return arrayList2;
    }

    public static String surroundInQuotes(String str) {
        return "\"" + str + "\"";
    }

    public static boolean tryParseBoolean(String str, boolean z) {
        try {
            z = Boolean.parseBoolean(str);
        } catch (Exception e) {
        }
        return z;
    }

    public static double tryParseDouble(String str, double d) {
        try {
            d = Double.parseDouble(str);
        } catch (Exception e) {
        }
        return d;
    }

    public static int tryParseInteger(String str, int i) {
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return i;
    }

    public static long tryParseLong(String str, long j) {
        try {
            j = Long.parseLong(str);
        } catch (Exception e) {
        }
        return j;
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
