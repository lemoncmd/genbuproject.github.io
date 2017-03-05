package org.mozilla.javascript;

import com.microsoft.cll.android.EventEnums;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.mozilla.javascript.regexp.NativeRegExp;

final class NativeDate extends IdScriptableObject {
    static final /* synthetic */ boolean $assertionsDisabled = (!NativeDate.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final int ConstructorId_UTC = -1;
    private static final int ConstructorId_now = -3;
    private static final int ConstructorId_parse = -2;
    private static final Object DATE_TAG = "Date";
    private static final double HalfTimeDomain = 8.64E15d;
    private static final double HoursPerDay = 24.0d;
    private static final int Id_constructor = 1;
    private static final int Id_getDate = 17;
    private static final int Id_getDay = 19;
    private static final int Id_getFullYear = 13;
    private static final int Id_getHours = 21;
    private static final int Id_getMilliseconds = 27;
    private static final int Id_getMinutes = 23;
    private static final int Id_getMonth = 15;
    private static final int Id_getSeconds = 25;
    private static final int Id_getTime = 11;
    private static final int Id_getTimezoneOffset = 29;
    private static final int Id_getUTCDate = 18;
    private static final int Id_getUTCDay = 20;
    private static final int Id_getUTCFullYear = 14;
    private static final int Id_getUTCHours = 22;
    private static final int Id_getUTCMilliseconds = 28;
    private static final int Id_getUTCMinutes = 24;
    private static final int Id_getUTCMonth = 16;
    private static final int Id_getUTCSeconds = 26;
    private static final int Id_getYear = 12;
    private static final int Id_setDate = 39;
    private static final int Id_setFullYear = 43;
    private static final int Id_setHours = 37;
    private static final int Id_setMilliseconds = 31;
    private static final int Id_setMinutes = 35;
    private static final int Id_setMonth = 41;
    private static final int Id_setSeconds = 33;
    private static final int Id_setTime = 30;
    private static final int Id_setUTCDate = 40;
    private static final int Id_setUTCFullYear = 44;
    private static final int Id_setUTCHours = 38;
    private static final int Id_setUTCMilliseconds = 32;
    private static final int Id_setUTCMinutes = 36;
    private static final int Id_setUTCMonth = 42;
    private static final int Id_setUTCSeconds = 34;
    private static final int Id_setYear = 45;
    private static final int Id_toDateString = 4;
    private static final int Id_toGMTString = 8;
    private static final int Id_toISOString = 46;
    private static final int Id_toJSON = 47;
    private static final int Id_toLocaleDateString = 7;
    private static final int Id_toLocaleString = 5;
    private static final int Id_toLocaleTimeString = 6;
    private static final int Id_toSource = 9;
    private static final int Id_toString = 2;
    private static final int Id_toTimeString = 3;
    private static final int Id_toUTCString = 8;
    private static final int Id_valueOf = 10;
    private static double LocalTZA = 0.0d;
    private static final int MAXARGS = 7;
    private static final int MAX_PROTOTYPE_ID = 47;
    private static final double MinutesPerDay = 1440.0d;
    private static final double MinutesPerHour = 60.0d;
    private static final double SecondsPerDay = 86400.0d;
    private static final double SecondsPerHour = 3600.0d;
    private static final double SecondsPerMinute = 60.0d;
    private static final String js_NaN_date_str = "Invalid Date";
    private static DateFormat localeDateFormatter = null;
    private static DateFormat localeDateTimeFormatter = null;
    private static DateFormat localeTimeFormatter = null;
    private static final double msPerDay = 8.64E7d;
    private static final double msPerHour = 3600000.0d;
    private static final double msPerMinute = 60000.0d;
    private static final double msPerSecond = 1000.0d;
    static final long serialVersionUID = -8307438915861678966L;
    private static TimeZone thisTimeZone;
    private static DateFormat timeZoneFormatter;
    private double date;

    private NativeDate() {
        if (thisTimeZone == null) {
            thisTimeZone = TimeZone.getDefault();
            LocalTZA = (double) thisTimeZone.getRawOffset();
        }
    }

    private static int DateFromTime(double d) {
        int i = Id_setTime;
        int i2 = Id_setMilliseconds;
        int YearFromTime = YearFromTime(d);
        int Day = ((int) (Day(d) - DayFromYear((double) YearFromTime))) - 59;
        if (Day < 0) {
            return Day < -28 ? ((Day + Id_setMilliseconds) + Id_getUTCMilliseconds) + Id_constructor : (Day + Id_getUTCMilliseconds) + Id_constructor;
        } else {
            if (IsLeapYear(YearFromTime)) {
                if (Day == 0) {
                    return Id_getTimezoneOffset;
                }
                Day += ConstructorId_UTC;
            }
            switch (Day / Id_setTime) {
                case NativeRegExp.TEST /*0*/:
                    return Day + Id_constructor;
                case Id_constructor /*1*/:
                    i = Id_setMilliseconds;
                    break;
                case Id_toString /*2*/:
                    i2 = 61;
                    break;
                case Id_toTimeString /*3*/:
                    i = Id_setMilliseconds;
                    i2 = 92;
                    break;
                case Id_toDateString /*4*/:
                    i2 = Token.CONTINUE;
                    break;
                case Id_toLocaleString /*5*/:
                    i = Id_setMilliseconds;
                    i2 = 153;
                    break;
                case Id_toLocaleTimeString /*6*/:
                    i = Id_setMilliseconds;
                    i2 = 184;
                    break;
                case MAXARGS /*7*/:
                    i2 = 214;
                    break;
                case Id_toUTCString /*8*/:
                    i = Id_setMilliseconds;
                    i2 = 245;
                    break;
                case Id_toSource /*9*/:
                    i2 = 275;
                    break;
                case Id_valueOf /*10*/:
                    return (Day - 275) + Id_constructor;
                default:
                    throw Kit.codeBug();
            }
            Day -= i2;
            if (Day < 0) {
                Day += i;
            }
            return Day + Id_constructor;
        }
    }

    private static double Day(double d) {
        return Math.floor(d / msPerDay);
    }

    private static double DayFromMonth(int i, int i2) {
        int i3 = i * Id_setTime;
        i3 = i >= MAXARGS ? i3 + ((i / Id_toString) + ConstructorId_UTC) : i >= Id_toString ? i3 + (((i + ConstructorId_UTC) / Id_toString) + ConstructorId_UTC) : i3 + i;
        if (i >= Id_toString && IsLeapYear(i2)) {
            i3 += Id_constructor;
        }
        return (double) i3;
    }

    private static double DayFromYear(double d) {
        return (((365.0d * (d - 1970.0d)) + Math.floor((d - 1969.0d) / 4.0d)) - Math.floor((d - 1901.0d) / EventEnums.SampleRate_NoSampling)) + Math.floor((d - 1601.0d) / 400.0d);
    }

    private static double DaylightSavingTA(double d) {
        if (d < 0.0d) {
            d = MakeDate(MakeDay((double) EquivalentYear(YearFromTime(d)), (double) MonthFromTime(d), (double) DateFromTime(d)), TimeWithinDay(d));
        }
        return thisTimeZone.inDaylightTime(new Date((long) d)) ? msPerHour : 0.0d;
    }

    private static int DaysInMonth(int i, int i2) {
        return i2 == Id_toString ? IsLeapYear(i) ? Id_getTimezoneOffset : Id_getUTCMilliseconds : i2 >= Id_toUTCString ? 31 - (i2 & Id_constructor) : (i2 & Id_constructor) + Id_setTime;
    }

    private static double DaysInYear(double d) {
        return (Double.isInfinite(d) || Double.isNaN(d)) ? ScriptRuntime.NaN : IsLeapYear((int) d) ? 366.0d : 365.0d;
    }

    private static int EquivalentYear(int i) {
        int DayFromYear = (((int) DayFromYear((double) i)) + Id_toDateString) % MAXARGS;
        if (DayFromYear < 0) {
            DayFromYear += MAXARGS;
        }
        if (!IsLeapYear(i)) {
            switch (DayFromYear) {
                case NativeRegExp.TEST /*0*/:
                    return 1978;
                case Id_constructor /*1*/:
                    return 1973;
                case Id_toString /*2*/:
                    return 1985;
                case Id_toTimeString /*3*/:
                    return 1986;
                case Id_toDateString /*4*/:
                    return 1981;
                case Id_toLocaleString /*5*/:
                    return 1971;
                case Id_toLocaleTimeString /*6*/:
                    return 1977;
                default:
                    break;
            }
        }
        switch (DayFromYear) {
            case NativeRegExp.TEST /*0*/:
                return 1984;
            case Id_constructor /*1*/:
                return 1996;
            case Id_toString /*2*/:
                return 1980;
            case Id_toTimeString /*3*/:
                return 1992;
            case Id_toDateString /*4*/:
                return 1976;
            case Id_toLocaleString /*5*/:
                return 1988;
            case Id_toLocaleTimeString /*6*/:
                return 1972;
        }
        throw Kit.codeBug();
    }

    private static int HourFromTime(double d) {
        double floor = Math.floor(d / msPerHour) % HoursPerDay;
        if (floor < 0.0d) {
            floor += HoursPerDay;
        }
        return (int) floor;
    }

    private static boolean IsLeapYear(int i) {
        return (i % Id_toDateString != 0 || (i % 100 == 0 && i % 400 != 0)) ? $assertionsDisabled : true;
    }

    private static double LocalTime(double d) {
        return (LocalTZA + d) + DaylightSavingTA(d);
    }

    private static double MakeDate(double d, double d2) {
        return (msPerDay * d) + d2;
    }

    private static double MakeDay(double d, double d2, double d3) {
        double floor = d + Math.floor(d2 / 12.0d);
        double d4 = d2 % 12.0d;
        if (d4 < 0.0d) {
            d4 += 12.0d;
        }
        return ((DayFromMonth((int) d4, (int) floor) + Math.floor(TimeFromYear(floor) / msPerDay)) + d3) - 1.0d;
    }

    private static double MakeTime(double d, double d2, double d3, double d4) {
        return (((((d * SecondsPerMinute) + d2) * SecondsPerMinute) + d3) * msPerSecond) + d4;
    }

    private static int MinFromTime(double d) {
        double floor = Math.floor(d / msPerMinute) % SecondsPerMinute;
        if (floor < 0.0d) {
            floor += SecondsPerMinute;
        }
        return (int) floor;
    }

    private static int MonthFromTime(double d) {
        int YearFromTime = YearFromTime(d);
        int Day = ((int) (Day(d) - DayFromYear((double) YearFromTime))) - 59;
        if (Day < 0) {
            return Day < -28 ? 0 : Id_constructor;
        } else {
            int i;
            if (!IsLeapYear(YearFromTime)) {
                i = Day;
            } else if (Day == 0) {
                return Id_constructor;
            } else {
                i = Day + ConstructorId_UTC;
            }
            YearFromTime = i / Id_setTime;
            switch (YearFromTime) {
                case NativeRegExp.TEST /*0*/:
                    return Id_toString;
                case Id_constructor /*1*/:
                    Day = Id_setMilliseconds;
                    break;
                case Id_toString /*2*/:
                    Day = 61;
                    break;
                case Id_toTimeString /*3*/:
                    Day = 92;
                    break;
                case Id_toDateString /*4*/:
                    Day = Token.CONTINUE;
                    break;
                case Id_toLocaleString /*5*/:
                    Day = Token.SET;
                    break;
                case Id_toLocaleTimeString /*6*/:
                    Day = 184;
                    break;
                case MAXARGS /*7*/:
                    Day = 214;
                    break;
                case Id_toUTCString /*8*/:
                    Day = 245;
                    break;
                case Id_toSource /*9*/:
                    Day = 275;
                    break;
                case Id_valueOf /*10*/:
                    return Id_getTime;
                default:
                    throw Kit.codeBug();
            }
            return i >= Day ? YearFromTime + Id_toString : YearFromTime + Id_constructor;
        }
    }

    private static int SecFromTime(double d) {
        double floor = Math.floor(d / msPerSecond) % SecondsPerMinute;
        if (floor < 0.0d) {
            floor += SecondsPerMinute;
        }
        return (int) floor;
    }

    private static double TimeClip(double d) {
        return (d != d || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY || Math.abs(d) > HalfTimeDomain) ? ScriptRuntime.NaN : d > 0.0d ? Math.floor(d + 0.0d) : Math.ceil(d + 0.0d);
    }

    private static double TimeFromYear(double d) {
        return DayFromYear(d) * msPerDay;
    }

    private static double TimeWithinDay(double d) {
        double d2 = d % msPerDay;
        return d2 < 0.0d ? d2 + msPerDay : d2;
    }

    private static int WeekDay(double d) {
        double Day = (Day(d) + 4.0d) % 7.0d;
        if (Day < 0.0d) {
            Day += 7.0d;
        }
        return (int) Day;
    }

    private static int YearFromTime(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return 0;
        }
        double floor = Math.floor(d / 3.1556952E10d) + 1970.0d;
        double TimeFromYear = TimeFromYear(floor);
        if (TimeFromYear > d) {
            floor -= 1.0d;
        } else if (TimeFromYear + (msPerDay * DaysInYear(floor)) <= d) {
            floor += 1.0d;
        }
        return (int) floor;
    }

    private static void append0PaddedUint(StringBuilder stringBuilder, int i, int i2) {
        int i3 = 1000000000;
        if (i < 0) {
            Kit.codeBug();
        }
        int i4 = i2 + ConstructorId_UTC;
        if (i < Id_valueOf) {
            i3 = Id_constructor;
        } else if (i < 1000000000) {
            i3 = Id_constructor;
            while (true) {
                int i5 = i3 * Id_valueOf;
                if (i < i5) {
                    break;
                }
                i4 += ConstructorId_UTC;
                i3 = i5;
            }
        } else {
            i4 -= 9;
        }
        while (i4 > 0) {
            stringBuilder.append('0');
            i4 += ConstructorId_UTC;
        }
        while (i3 != Id_constructor) {
            stringBuilder.append((char) ((i / i3) + 48));
            i %= i3;
            i3 /= Id_valueOf;
        }
        stringBuilder.append((char) (i + 48));
    }

    private static void appendMonthName(StringBuilder stringBuilder, int i) {
        String str = "JanFebMarAprMayJunJulAugSepOctNovDec";
        int i2 = i * Id_toTimeString;
        for (int i3 = 0; i3 != Id_toTimeString; i3 += Id_constructor) {
            stringBuilder.append(str.charAt(i2 + i3));
        }
    }

    private static void appendWeekDayName(StringBuilder stringBuilder, int i) {
        String str = "SunMonTueWedThuFriSat";
        int i2 = i * Id_toTimeString;
        for (int i3 = 0; i3 != Id_toTimeString; i3 += Id_constructor) {
            stringBuilder.append(str.charAt(i2 + i3));
        }
    }

    private static String date_format(double d, int i) {
        int YearFromTime;
        StringBuilder stringBuilder = new StringBuilder(60);
        double LocalTime = LocalTime(d);
        if (i != Id_toTimeString) {
            appendWeekDayName(stringBuilder, WeekDay(LocalTime));
            stringBuilder.append(' ');
            appendMonthName(stringBuilder, MonthFromTime(LocalTime));
            stringBuilder.append(' ');
            append0PaddedUint(stringBuilder, DateFromTime(LocalTime), Id_toString);
            stringBuilder.append(' ');
            YearFromTime = YearFromTime(LocalTime);
            if (YearFromTime < 0) {
                stringBuilder.append('-');
                YearFromTime = -YearFromTime;
            }
            append0PaddedUint(stringBuilder, YearFromTime, Id_toDateString);
            if (i != Id_toDateString) {
                stringBuilder.append(' ');
            }
        }
        if (i != Id_toDateString) {
            append0PaddedUint(stringBuilder, HourFromTime(LocalTime), Id_toString);
            stringBuilder.append(':');
            append0PaddedUint(stringBuilder, MinFromTime(LocalTime), Id_toString);
            stringBuilder.append(':');
            append0PaddedUint(stringBuilder, SecFromTime(LocalTime), Id_toString);
            YearFromTime = (int) Math.floor((LocalTZA + DaylightSavingTA(d)) / msPerMinute);
            YearFromTime = (YearFromTime % 60) + ((YearFromTime / 60) * 100);
            if (YearFromTime > 0) {
                stringBuilder.append(" GMT+");
            } else {
                stringBuilder.append(" GMT-");
                YearFromTime = -YearFromTime;
            }
            append0PaddedUint(stringBuilder, YearFromTime, Id_toDateString);
            if (timeZoneFormatter == null) {
                timeZoneFormatter = new SimpleDateFormat("zzz");
            }
            if (d < 0.0d) {
                d = MakeDate(MakeDay((double) EquivalentYear(YearFromTime(LocalTime)), (double) MonthFromTime(d), (double) DateFromTime(d)), TimeWithinDay(d));
            }
            stringBuilder.append(" (");
            Date date = new Date((long) d);
            synchronized (timeZoneFormatter) {
                stringBuilder.append(timeZoneFormatter.format(date));
            }
            stringBuilder.append(')');
        }
        return stringBuilder.toString();
    }

    private static double date_msecFromArgs(Object[] objArr) {
        double[] dArr = new double[MAXARGS];
        for (int i = 0; i < MAXARGS; i += Id_constructor) {
            if (i < objArr.length) {
                double toNumber = ScriptRuntime.toNumber(objArr[i]);
                if (toNumber != toNumber || Double.isInfinite(toNumber)) {
                    return ScriptRuntime.NaN;
                }
                dArr[i] = ScriptRuntime.toInteger(objArr[i]);
            } else if (i == Id_toString) {
                dArr[i] = 1.0d;
            } else {
                dArr[i] = 0.0d;
            }
        }
        if (dArr[0] >= 0.0d && dArr[0] <= 99.0d) {
            dArr[0] = dArr[0] + 1900.0d;
        }
        return date_msecFromDate(dArr[0], dArr[Id_constructor], dArr[Id_toString], dArr[Id_toTimeString], dArr[Id_toDateString], dArr[Id_toLocaleString], dArr[Id_toLocaleTimeString]);
    }

    private static double date_msecFromDate(double d, double d2, double d3, double d4, double d5, double d6, double d7) {
        return MakeDate(MakeDay(d, d2, d3), MakeTime(d4, d5, d6, d7));
    }

    private static double date_parseString(String str) {
        double parseISOString = parseISOString(str);
        if (parseISOString == parseISOString) {
            return parseISOString;
        }
        int i = ConstructorId_UTC;
        int i2 = ConstructorId_UTC;
        int i3 = ConstructorId_UTC;
        int i4 = ConstructorId_UTC;
        int i5 = ConstructorId_UTC;
        int i6 = ConstructorId_UTC;
        int i7 = 0;
        double d = EventEnums.SampleRate_Unspecified;
        Object obj = null;
        int length = str.length();
        char c = '\u0000';
        while (i7 < length) {
            int i8;
            char charAt = str.charAt(i7);
            i7 += Id_constructor;
            char charAt2;
            if (charAt <= ' ' || charAt == ',' || charAt == '-') {
                if (i7 < length) {
                    charAt2 = str.charAt(i7);
                    if (charAt == '-' && '0' <= charAt2 && charAt2 <= '9') {
                        c = charAt;
                    }
                }
            } else if (charAt == '(') {
                r3 = i7;
                i7 = Id_constructor;
                while (r3 < length) {
                    charAt = str.charAt(r3);
                    r3 += Id_constructor;
                    if (charAt == '(') {
                        i7 += Id_constructor;
                    } else if (charAt == ')') {
                        i7 += ConstructorId_UTC;
                        if (i7 <= 0) {
                            i7 = r3;
                        }
                    } else {
                        continue;
                    }
                }
                i7 = r3;
            } else if ('0' <= charAt && charAt <= '9') {
                double d2;
                Object obj2;
                int i9;
                i8 = i7;
                i7 = charAt - 48;
                charAt2 = charAt;
                while (i8 < length) {
                    charAt2 = str.charAt(i8);
                    if ('0' <= charAt2 && charAt2 <= '9') {
                        i7 = ((i7 * Id_valueOf) + charAt2) - 48;
                        i8 += Id_constructor;
                    }
                    if (c != Id_setFullYear || c == Id_setYear) {
                        i7 = i7 >= Id_getUTCMinutes ? i7 * 60 : ((i7 / 100) * 60) + (i7 % 100);
                        if (c == Id_setFullYear) {
                            i7 = -i7;
                        }
                        if (d == 0.0d && d != EventEnums.SampleRate_Unspecified) {
                            return ScriptRuntime.NaN;
                        }
                        d2 = (double) i7;
                        obj2 = Id_constructor;
                        r6 = i5;
                        r7 = i4;
                        i9 = i3;
                        r3 = i6;
                        i5 = i;
                        i6 = i2;
                    } else if (i7 >= 70 || (c == MAX_PROTOTYPE_ID && i2 >= 0 && i3 >= 0 && i < 0)) {
                        if (i >= 0) {
                            return ScriptRuntime.NaN;
                        }
                        if (charAt2 > ' ' && charAt2 != ',' && charAt2 != '/' && i8 < length) {
                            return ScriptRuntime.NaN;
                        }
                        if (i7 < 100) {
                            i7 += 1900;
                        }
                        d2 = d;
                        r3 = i6;
                        r6 = i5;
                        r7 = i4;
                        i6 = i2;
                        i5 = i7;
                        obj2 = obj;
                        i9 = i3;
                    } else if (charAt2 == ':') {
                        if (i4 < 0) {
                            d2 = d;
                            r3 = i6;
                            r6 = i5;
                            r7 = i7;
                            obj2 = obj;
                            i6 = i2;
                            i5 = i;
                            i9 = i3;
                        } else if (i5 >= 0) {
                            return ScriptRuntime.NaN;
                        } else {
                            d2 = d;
                            r3 = i6;
                            r6 = i7;
                            r7 = i4;
                            i5 = i;
                            i6 = i2;
                            obj2 = obj;
                            i9 = i3;
                        }
                    } else if (charAt2 == '/') {
                        if (i2 < 0) {
                            d2 = d;
                            r3 = i6;
                            r6 = i5;
                            r7 = i4;
                            i6 = i7 + ConstructorId_UTC;
                            i5 = i;
                            obj2 = obj;
                            i9 = i3;
                        } else if (i3 >= 0) {
                            return ScriptRuntime.NaN;
                        } else {
                            d2 = d;
                            r3 = i6;
                            r6 = i5;
                            r7 = i4;
                            i6 = i2;
                            i5 = i;
                            r22 = obj;
                            i9 = i7;
                            obj2 = r22;
                        }
                    } else if (i8 < length && charAt2 != ',' && charAt2 > ' ' && charAt2 != '-') {
                        return ScriptRuntime.NaN;
                    } else {
                        if (obj == null || i7 >= 60) {
                            if (i4 >= 0 && i5 < 0) {
                                d2 = d;
                                r3 = i6;
                                r6 = i7;
                                r7 = i4;
                                i5 = i;
                                i6 = i2;
                                obj2 = obj;
                                i9 = i3;
                            } else if (i5 >= 0 && i6 < 0) {
                                d2 = d;
                                r3 = i7;
                                r6 = i5;
                                r7 = i4;
                                i6 = i2;
                                obj2 = obj;
                                i5 = i;
                                i9 = i3;
                            } else if (i3 >= 0) {
                                return ScriptRuntime.NaN;
                            } else {
                                d2 = d;
                                r3 = i6;
                                r6 = i5;
                                r7 = i4;
                                i6 = i2;
                                i5 = i;
                                r22 = obj;
                                i9 = i7;
                                obj2 = r22;
                            }
                        } else if (d < 0.0d) {
                            d2 = d - ((double) i7);
                            r6 = i5;
                            r7 = i4;
                            obj2 = obj;
                            r3 = i6;
                            i5 = i;
                            i6 = i2;
                            i9 = i3;
                        } else {
                            d2 = ((double) i7) + d;
                            r6 = i5;
                            r7 = i4;
                            obj2 = obj;
                            r3 = i6;
                            i5 = i;
                            i6 = i2;
                            i9 = i3;
                        }
                    }
                    c = '\u0000';
                    d = d2;
                    i3 = i9;
                    i2 = i6;
                    i = i5;
                    i5 = r6;
                    i4 = r7;
                    obj = obj2;
                    i6 = r3;
                    i7 = i8;
                }
                if (c != Id_setFullYear) {
                }
                if (i7 >= Id_getUTCMinutes) {
                }
                if (c == Id_setFullYear) {
                    i7 = -i7;
                }
                if (d == 0.0d) {
                }
                d2 = (double) i7;
                obj2 = Id_constructor;
                r6 = i5;
                r7 = i4;
                i9 = i3;
                r3 = i6;
                i5 = i;
                i6 = i2;
                c = '\u0000';
                d = d2;
                i3 = i9;
                i2 = i6;
                i = i5;
                i5 = r6;
                i4 = r7;
                obj = obj2;
                i6 = r3;
                i7 = i8;
            } else if (charAt == '/' || charAt == ':' || charAt == '+' || charAt == '-') {
                c = charAt;
            } else {
                String str2;
                int i10;
                int indexOf;
                r6 = i7 + ConstructorId_UTC;
                int i11 = i7;
                while (i11 < length) {
                    char charAt3 = str.charAt(i11);
                    if (('A' <= charAt3 && charAt3 <= 'Z') || ('a' <= charAt3 && charAt3 <= 'z')) {
                        i11 += Id_constructor;
                    }
                    r7 = i11 - r6;
                    if (r7 < Id_toString) {
                        return ScriptRuntime.NaN;
                    }
                    str2 = "am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;";
                    i10 = 0;
                    i8 = 0;
                    while (true) {
                        indexOf = str2.indexOf(59, i10);
                        if (indexOf < 0) {
                            return ScriptRuntime.NaN;
                        }
                        if (str2.regionMatches(true, i10, str, r6, r7)) {
                            i10 = indexOf + Id_constructor;
                            i8 += Id_constructor;
                        } else {
                            if (i8 < Id_toString) {
                                i7 = i8 + ConstructorId_parse;
                                if (i7 >= MAXARGS) {
                                    i7 -= 7;
                                    if (i7 < Id_getYear) {
                                        switch (i7 - 12) {
                                            case NativeRegExp.TEST /*0*/:
                                                d = 0.0d;
                                                break;
                                            case Id_constructor /*1*/:
                                                d = 0.0d;
                                                break;
                                            case Id_toString /*2*/:
                                                d = 0.0d;
                                                break;
                                            case Id_toTimeString /*3*/:
                                                d = 300.0d;
                                                break;
                                            case Id_toDateString /*4*/:
                                                d = 240.0d;
                                                break;
                                            case Id_toLocaleString /*5*/:
                                                d = 360.0d;
                                                break;
                                            case Id_toLocaleTimeString /*6*/:
                                                d = 300.0d;
                                                break;
                                            case MAXARGS /*7*/:
                                                d = 420.0d;
                                                break;
                                            case Id_toUTCString /*8*/:
                                                d = 360.0d;
                                                break;
                                            case Id_toSource /*9*/:
                                                d = 480.0d;
                                                break;
                                            case Id_valueOf /*10*/:
                                                d = 420.0d;
                                                break;
                                            default:
                                                Kit.codeBug();
                                                break;
                                        }
                                    } else if (i2 < 0) {
                                        return ScriptRuntime.NaN;
                                    } else {
                                        i2 = i7;
                                    }
                                }
                            } else if (i4 <= Id_getYear || i4 < 0) {
                                return ScriptRuntime.NaN;
                            } else {
                                if (i8 == 0) {
                                    if (i4 == Id_getYear) {
                                        i4 = 0;
                                    }
                                } else if (i4 != Id_getYear) {
                                    i4 += Id_getYear;
                                }
                            }
                            i7 = i11;
                        }
                    }
                }
                r7 = i11 - r6;
                if (r7 < Id_toString) {
                    return ScriptRuntime.NaN;
                }
                str2 = "am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;";
                i10 = 0;
                i8 = 0;
                while (true) {
                    indexOf = str2.indexOf(59, i10);
                    if (indexOf < 0) {
                        return ScriptRuntime.NaN;
                    }
                    if (str2.regionMatches(true, i10, str, r6, r7)) {
                        i10 = indexOf + Id_constructor;
                        i8 += Id_constructor;
                    } else {
                        if (i8 < Id_toString) {
                            i7 = i8 + ConstructorId_parse;
                            if (i7 >= MAXARGS) {
                                i7 -= 7;
                                if (i7 < Id_getYear) {
                                    switch (i7 - 12) {
                                        case NativeRegExp.TEST /*0*/:
                                            d = 0.0d;
                                            break;
                                        case Id_constructor /*1*/:
                                            d = 0.0d;
                                            break;
                                        case Id_toString /*2*/:
                                            d = 0.0d;
                                            break;
                                        case Id_toTimeString /*3*/:
                                            d = 300.0d;
                                            break;
                                        case Id_toDateString /*4*/:
                                            d = 240.0d;
                                            break;
                                        case Id_toLocaleString /*5*/:
                                            d = 360.0d;
                                            break;
                                        case Id_toLocaleTimeString /*6*/:
                                            d = 300.0d;
                                            break;
                                        case MAXARGS /*7*/:
                                            d = 420.0d;
                                            break;
                                        case Id_toUTCString /*8*/:
                                            d = 360.0d;
                                            break;
                                        case Id_toSource /*9*/:
                                            d = 480.0d;
                                            break;
                                        case Id_valueOf /*10*/:
                                            d = 420.0d;
                                            break;
                                        default:
                                            Kit.codeBug();
                                            break;
                                    }
                                } else if (i2 < 0) {
                                    return ScriptRuntime.NaN;
                                } else {
                                    i2 = i7;
                                }
                            }
                        } else {
                            if (i4 <= Id_getYear) {
                            }
                            return ScriptRuntime.NaN;
                        }
                        i7 = i11;
                    }
                }
            }
        }
        if (i < 0 || i2 < 0 || i3 < 0) {
            return ScriptRuntime.NaN;
        }
        i8 = i6 < 0 ? 0 : i6;
        if (i5 < 0) {
            i5 = 0;
        }
        if (i4 < 0) {
            i4 = 0;
        }
        parseISOString = date_msecFromDate((double) i, (double) i2, (double) i3, (double) i4, (double) i5, (double) i8, 0.0d);
        return d == EventEnums.SampleRate_Unspecified ? internalUTC(parseISOString) : parseISOString + (msPerMinute * d);
    }

    static void init(Scriptable scriptable, boolean z) {
        NativeDate nativeDate = new NativeDate();
        nativeDate.date = ScriptRuntime.NaN;
        nativeDate.exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private static double internalUTC(double d) {
        return (d - LocalTZA) - DaylightSavingTA(d - LocalTZA);
    }

    private static Object jsConstructor(Object[] objArr) {
        NativeDate nativeDate = new NativeDate();
        if (objArr.length == 0) {
            nativeDate.date = now();
            return nativeDate;
        } else if (objArr.length == Id_constructor) {
            Object obj = objArr[0];
            if (obj instanceof Scriptable) {
                obj = ((Scriptable) obj).getDefaultValue(null);
            }
            nativeDate.date = TimeClip(obj instanceof CharSequence ? date_parseString(obj.toString()) : ScriptRuntime.toNumber(obj));
            return nativeDate;
        } else {
            double date_msecFromArgs = date_msecFromArgs(objArr);
            if (!(Double.isNaN(date_msecFromArgs) || Double.isInfinite(date_msecFromArgs))) {
                date_msecFromArgs = TimeClip(internalUTC(date_msecFromArgs));
            }
            nativeDate.date = date_msecFromArgs;
            return nativeDate;
        }
    }

    private static double jsStaticFunction_UTC(Object[] objArr) {
        return TimeClip(date_msecFromArgs(objArr));
    }

    private static String js_toISOString(double d) {
        StringBuilder stringBuilder = new StringBuilder(Id_getMilliseconds);
        int YearFromTime = YearFromTime(d);
        if (YearFromTime < 0) {
            stringBuilder.append('-');
            append0PaddedUint(stringBuilder, -YearFromTime, Id_toLocaleTimeString);
        } else if (YearFromTime > 9999) {
            append0PaddedUint(stringBuilder, YearFromTime, Id_toLocaleTimeString);
        } else {
            append0PaddedUint(stringBuilder, YearFromTime, Id_toDateString);
        }
        stringBuilder.append('-');
        append0PaddedUint(stringBuilder, MonthFromTime(d) + Id_constructor, Id_toString);
        stringBuilder.append('-');
        append0PaddedUint(stringBuilder, DateFromTime(d), Id_toString);
        stringBuilder.append('T');
        append0PaddedUint(stringBuilder, HourFromTime(d), Id_toString);
        stringBuilder.append(':');
        append0PaddedUint(stringBuilder, MinFromTime(d), Id_toString);
        stringBuilder.append(':');
        append0PaddedUint(stringBuilder, SecFromTime(d), Id_toString);
        stringBuilder.append('.');
        append0PaddedUint(stringBuilder, msFromTime(d), Id_toTimeString);
        stringBuilder.append('Z');
        return stringBuilder.toString();
    }

    private static String js_toUTCString(double d) {
        StringBuilder stringBuilder = new StringBuilder(60);
        appendWeekDayName(stringBuilder, WeekDay(d));
        stringBuilder.append(", ");
        append0PaddedUint(stringBuilder, DateFromTime(d), Id_toString);
        stringBuilder.append(' ');
        appendMonthName(stringBuilder, MonthFromTime(d));
        stringBuilder.append(' ');
        int YearFromTime = YearFromTime(d);
        if (YearFromTime < 0) {
            stringBuilder.append('-');
            YearFromTime = -YearFromTime;
        }
        append0PaddedUint(stringBuilder, YearFromTime, Id_toDateString);
        stringBuilder.append(' ');
        append0PaddedUint(stringBuilder, HourFromTime(d), Id_toString);
        stringBuilder.append(':');
        append0PaddedUint(stringBuilder, MinFromTime(d), Id_toString);
        stringBuilder.append(':');
        append0PaddedUint(stringBuilder, SecFromTime(d), Id_toString);
        stringBuilder.append(" GMT");
        return stringBuilder.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static double makeDate(double r12, java.lang.Object[] r14, int r15) {
        /*
        r0 = r14.length;
        if (r0 != 0) goto L_0x0006;
    L_0x0003:
        r0 = org.mozilla.javascript.ScriptRuntime.NaN;
    L_0x0005:
        return r0;
    L_0x0006:
        r0 = 1;
        switch(r15) {
            case 39: goto L_0x0010;
            case 40: goto L_0x000f;
            case 41: goto L_0x002a;
            case 42: goto L_0x0029;
            case 43: goto L_0x002f;
            case 44: goto L_0x002e;
            default: goto L_0x000a;
        };
    L_0x000a:
        r0 = org.mozilla.javascript.Kit.codeBug();
        throw r0;
    L_0x000f:
        r0 = 0;
    L_0x0010:
        r1 = 1;
        r6 = r0;
        r4 = r1;
    L_0x0013:
        r1 = 0;
        r0 = r14.length;
        if (r0 >= r4) goto L_0x0033;
    L_0x0017:
        r0 = r14.length;
        r5 = r0;
    L_0x0019:
        r0 = $assertionsDisabled;
        if (r0 != 0) goto L_0x0035;
    L_0x001d:
        r0 = 1;
        if (r0 > r5) goto L_0x0023;
    L_0x0020:
        r0 = 3;
        if (r5 <= r0) goto L_0x0035;
    L_0x0023:
        r0 = new java.lang.AssertionError;
        r0.<init>();
        throw r0;
    L_0x0029:
        r0 = 0;
    L_0x002a:
        r1 = 2;
        r6 = r0;
        r4 = r1;
        goto L_0x0013;
    L_0x002e:
        r0 = 0;
    L_0x002f:
        r1 = 3;
        r6 = r0;
        r4 = r1;
        goto L_0x0013;
    L_0x0033:
        r5 = r4;
        goto L_0x0019;
    L_0x0035:
        r0 = 3;
        r9 = new double[r0];
        r0 = 0;
        r10 = r0;
        r0 = r1;
        r1 = r10;
    L_0x003c:
        if (r1 >= r5) goto L_0x0059;
    L_0x003e:
        r2 = r14[r1];
        r2 = org.mozilla.javascript.ScriptRuntime.toNumber(r2);
        r7 = (r2 > r2 ? 1 : (r2 == r2 ? 0 : -1));
        if (r7 != 0) goto L_0x004e;
    L_0x0048:
        r7 = java.lang.Double.isInfinite(r2);
        if (r7 == 0) goto L_0x0052;
    L_0x004e:
        r0 = 1;
    L_0x004f:
        r1 = r1 + 1;
        goto L_0x003c;
    L_0x0052:
        r2 = org.mozilla.javascript.ScriptRuntime.toInteger(r2);
        r9[r1] = r2;
        goto L_0x004f;
    L_0x0059:
        if (r0 == 0) goto L_0x005e;
    L_0x005b:
        r0 = org.mozilla.javascript.ScriptRuntime.NaN;
        goto L_0x0005;
    L_0x005e:
        r2 = 0;
        r0 = (r12 > r12 ? 1 : (r12 == r12 ? 0 : -1));
        if (r0 == 0) goto L_0x009d;
    L_0x0063:
        r0 = 3;
        if (r4 >= r0) goto L_0x0069;
    L_0x0066:
        r0 = org.mozilla.javascript.ScriptRuntime.NaN;
        goto L_0x0005;
    L_0x0069:
        r12 = 0;
    L_0x006b:
        r0 = 3;
        if (r4 < r0) goto L_0x00a4;
    L_0x006e:
        if (r2 >= r5) goto L_0x00a4;
    L_0x0070:
        r8 = 1;
        r0 = r9[r2];
    L_0x0073:
        r2 = 2;
        if (r4 < r2) goto L_0x00ab;
    L_0x0076:
        if (r8 >= r5) goto L_0x00ab;
    L_0x0078:
        r7 = r8 + 1;
        r2 = r9[r8];
    L_0x007c:
        r8 = 1;
        if (r4 < r8) goto L_0x00b2;
    L_0x007f:
        if (r7 >= r5) goto L_0x00b2;
    L_0x0081:
        r4 = r7 + 1;
        r4 = r9[r7];
    L_0x0085:
        r0 = MakeDay(r0, r2, r4);
        r2 = TimeWithinDay(r12);
        r0 = MakeDate(r0, r2);
        if (r6 == 0) goto L_0x0097;
    L_0x0093:
        r0 = internalUTC(r0);
    L_0x0097:
        r0 = TimeClip(r0);
        goto L_0x0005;
    L_0x009d:
        if (r6 == 0) goto L_0x006b;
    L_0x009f:
        r12 = LocalTime(r12);
        goto L_0x006b;
    L_0x00a4:
        r0 = YearFromTime(r12);
        r0 = (double) r0;
        r8 = r2;
        goto L_0x0073;
    L_0x00ab:
        r2 = MonthFromTime(r12);
        r2 = (double) r2;
        r7 = r8;
        goto L_0x007c;
    L_0x00b2:
        r4 = DateFromTime(r12);
        r4 = (double) r4;
        goto L_0x0085;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeDate.makeDate(double, java.lang.Object[], int):double");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static double makeTime(double r16, java.lang.Object[] r18, int r19) {
        /*
        r0 = r18;
        r2 = r0.length;
        if (r2 != 0) goto L_0x0008;
    L_0x0005:
        r2 = org.mozilla.javascript.ScriptRuntime.NaN;
    L_0x0007:
        return r2;
    L_0x0008:
        r2 = 1;
        switch(r19) {
            case 31: goto L_0x0012;
            case 32: goto L_0x0011;
            case 33: goto L_0x002d;
            case 34: goto L_0x002c;
            case 35: goto L_0x0032;
            case 36: goto L_0x0031;
            case 37: goto L_0x0037;
            case 38: goto L_0x0036;
            default: goto L_0x000c;
        };
    L_0x000c:
        r2 = org.mozilla.javascript.Kit.codeBug();
        throw r2;
    L_0x0011:
        r2 = 0;
    L_0x0012:
        r3 = 1;
        r10 = r2;
        r8 = r3;
    L_0x0015:
        r3 = 0;
        r0 = r18;
        r2 = r0.length;
        if (r2 >= r8) goto L_0x003b;
    L_0x001b:
        r0 = r18;
        r2 = r0.length;
        r9 = r2;
    L_0x001f:
        r2 = $assertionsDisabled;
        if (r2 != 0) goto L_0x003d;
    L_0x0023:
        r2 = 4;
        if (r9 <= r2) goto L_0x003d;
    L_0x0026:
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x002c:
        r2 = 0;
    L_0x002d:
        r3 = 2;
        r10 = r2;
        r8 = r3;
        goto L_0x0015;
    L_0x0031:
        r2 = 0;
    L_0x0032:
        r3 = 3;
        r10 = r2;
        r8 = r3;
        goto L_0x0015;
    L_0x0036:
        r2 = 0;
    L_0x0037:
        r3 = 4;
        r10 = r2;
        r8 = r3;
        goto L_0x0015;
    L_0x003b:
        r9 = r8;
        goto L_0x001f;
    L_0x003d:
        r2 = 4;
        r13 = new double[r2];
        r2 = 0;
        r14 = r2;
        r2 = r3;
        r3 = r14;
    L_0x0044:
        if (r3 >= r9) goto L_0x0061;
    L_0x0046:
        r4 = r18[r3];
        r4 = org.mozilla.javascript.ScriptRuntime.toNumber(r4);
        r6 = (r4 > r4 ? 1 : (r4 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x0056;
    L_0x0050:
        r6 = java.lang.Double.isInfinite(r4);
        if (r6 == 0) goto L_0x005a;
    L_0x0056:
        r2 = 1;
    L_0x0057:
        r3 = r3 + 1;
        goto L_0x0044;
    L_0x005a:
        r4 = org.mozilla.javascript.ScriptRuntime.toInteger(r4);
        r13[r3] = r4;
        goto L_0x0057;
    L_0x0061:
        if (r2 != 0) goto L_0x0067;
    L_0x0063:
        r2 = (r16 > r16 ? 1 : (r16 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x006a;
    L_0x0067:
        r2 = org.mozilla.javascript.ScriptRuntime.NaN;
        goto L_0x0007;
    L_0x006a:
        r4 = 0;
        if (r10 == 0) goto L_0x0071;
    L_0x006d:
        r16 = LocalTime(r16);
    L_0x0071:
        r2 = 4;
        if (r8 < r2) goto L_0x00ac;
    L_0x0074:
        if (r4 >= r9) goto L_0x00ac;
    L_0x0076:
        r6 = 1;
        r2 = r13[r4];
    L_0x0079:
        r4 = 3;
        if (r8 < r4) goto L_0x00b3;
    L_0x007c:
        if (r6 >= r9) goto L_0x00b3;
    L_0x007e:
        r12 = r6 + 1;
        r4 = r13[r6];
    L_0x0082:
        r6 = 2;
        if (r8 < r6) goto L_0x00ba;
    L_0x0085:
        if (r12 >= r9) goto L_0x00ba;
    L_0x0087:
        r11 = r12 + 1;
        r6 = r13[r12];
    L_0x008b:
        r12 = 1;
        if (r8 < r12) goto L_0x00c1;
    L_0x008e:
        if (r11 >= r9) goto L_0x00c1;
    L_0x0090:
        r8 = r11 + 1;
        r8 = r13[r11];
    L_0x0094:
        r2 = MakeTime(r2, r4, r6, r8);
        r4 = Day(r16);
        r2 = MakeDate(r4, r2);
        if (r10 == 0) goto L_0x00a6;
    L_0x00a2:
        r2 = internalUTC(r2);
    L_0x00a6:
        r2 = TimeClip(r2);
        goto L_0x0007;
    L_0x00ac:
        r2 = HourFromTime(r16);
        r2 = (double) r2;
        r6 = r4;
        goto L_0x0079;
    L_0x00b3:
        r4 = MinFromTime(r16);
        r4 = (double) r4;
        r12 = r6;
        goto L_0x0082;
    L_0x00ba:
        r6 = SecFromTime(r16);
        r6 = (double) r6;
        r11 = r12;
        goto L_0x008b;
    L_0x00c1:
        r8 = msFromTime(r16);
        r8 = (double) r8;
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeDate.makeTime(double, java.lang.Object[], int):double");
    }

    private static int msFromTime(double d) {
        double d2 = d % msPerSecond;
        if (d2 < 0.0d) {
            d2 += msPerSecond;
        }
        return (int) d2;
    }

    private static double now() {
        return (double) System.currentTimeMillis();
    }

    private static double parseISOString(String str) {
        int i;
        int i2;
        int i3;
        int i4;
        char charAt;
        char charAt2;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        double date_msecFromDate;
        int[] iArr = new int[]{1970, Id_constructor, Id_constructor, 0, 0, 0, 0, ConstructorId_UTC, ConstructorId_UTC};
        int i10 = Id_toDateString;
        int i11 = Id_constructor;
        int i12 = 0;
        int length = str.length();
        if (length != 0) {
            char charAt3 = str.charAt(0);
            if (charAt3 == '+' || charAt3 == '-') {
                i12 = Id_constructor;
                i10 = Id_toLocaleTimeString;
                i = charAt3 == '-' ? ConstructorId_UTC : Id_constructor;
                i2 = 0;
                while (i2 != ConstructorId_UTC) {
                    i3 = i2 == 0 ? i10 : i2 == Id_toLocaleTimeString ? Id_toTimeString : Id_toString;
                    i4 = i12 + i3;
                    if (i4 > length) {
                        i2 = ConstructorId_UTC;
                    } else {
                        i3 = i12;
                        i12 = 0;
                        while (i3 < i4) {
                            charAt = str.charAt(i3);
                            if (charAt < '0' || charAt > '9') {
                                i2 = ConstructorId_UTC;
                                i12 = i3;
                            } else {
                                i12 = (i12 * Id_valueOf) + (charAt - 48);
                                i3 += Id_constructor;
                            }
                        }
                        iArr[i2] = i12;
                        if (i3 == length) {
                            switch (i2) {
                                case Id_toTimeString /*3*/:
                                case MAXARGS /*7*/:
                                    i2 = ConstructorId_UTC;
                                    break;
                            }
                            i12 = i3;
                        } else {
                            i12 = i3 + Id_constructor;
                            charAt2 = str.charAt(i3);
                            if (charAt2 != 'Z') {
                                iArr[MAXARGS] = 0;
                                iArr[Id_toUTCString] = 0;
                                switch (i2) {
                                    case Id_toDateString /*4*/:
                                    case Id_toLocaleString /*5*/:
                                    case Id_toLocaleTimeString /*6*/:
                                        break;
                                    default:
                                        i2 = ConstructorId_UTC;
                                        break;
                                }
                            }
                            switch (i2) {
                                case NativeRegExp.TEST /*0*/:
                                case Id_constructor /*1*/:
                                    i2 = charAt2 == '-' ? i2 + Id_constructor : charAt2 == 'T' ? Id_toTimeString : ConstructorId_UTC;
                                    i3 = i2;
                                    break;
                                case Id_toString /*2*/:
                                    i3 = charAt2 == 'T' ? Id_toTimeString : ConstructorId_UTC;
                                    break;
                                case Id_toTimeString /*3*/:
                                    i3 = charAt2 == ':' ? Id_toDateString : ConstructorId_UTC;
                                    break;
                                case Id_toDateString /*4*/:
                                    i2 = charAt2 != ':' ? Id_toLocaleString : (charAt2 != '+' || charAt2 == '-') ? MAXARGS : ConstructorId_UTC;
                                    i3 = i2;
                                    break;
                                case Id_toLocaleString /*5*/:
                                    i2 = charAt2 != '.' ? Id_toLocaleTimeString : (charAt2 != '+' || charAt2 == '-') ? MAXARGS : ConstructorId_UTC;
                                    i3 = i2;
                                    break;
                                case Id_toLocaleTimeString /*6*/:
                                    i2 = (charAt2 != '+' || charAt2 == '-') ? MAXARGS : ConstructorId_UTC;
                                    i3 = i2;
                                    break;
                                case MAXARGS /*7*/:
                                    i2 = charAt2 != ':' ? i12 + ConstructorId_UTC : i12;
                                    i3 = Id_toUTCString;
                                    i12 = i2;
                                    break;
                                case Id_toUTCString /*8*/:
                                    i3 = ConstructorId_UTC;
                                    break;
                                default:
                                    i3 = i2;
                                    break;
                            }
                            i2 = i3 == MAXARGS ? charAt2 == '-' ? ConstructorId_UTC : Id_constructor : i11;
                            i11 = i2;
                            i2 = i3;
                        }
                    }
                    if (i2 != ConstructorId_UTC && r3 == length) {
                        i2 = iArr[0];
                        i3 = iArr[Id_constructor];
                        length = iArr[Id_toString];
                        i4 = iArr[Id_toTimeString];
                        i5 = iArr[Id_toDateString];
                        i6 = iArr[Id_toLocaleString];
                        i7 = iArr[Id_toLocaleTimeString];
                        i8 = iArr[MAXARGS];
                        i9 = iArr[Id_toUTCString];
                        if (i2 <= 275943 && i3 >= Id_constructor && i3 <= Id_getYear && length >= Id_constructor && length <= DaysInMonth(i2, i3) && i4 <= Id_getUTCMinutes && ((i4 != Id_getUTCMinutes || (i5 <= 0 && i6 <= 0 && i7 <= 0)) && i5 <= 59 && i6 <= 59 && i8 <= Id_getMinutes && i9 <= 59)) {
                            date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
                            if (i8 != ConstructorId_UTC) {
                                date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
                            }
                            if (date_msecFromDate >= -8.64E15d) {
                                if (date_msecFromDate > HalfTimeDomain) {
                                    return date_msecFromDate;
                                }
                            }
                        }
                    }
                    return ScriptRuntime.NaN;
                }
                i2 = iArr[0];
                i3 = iArr[Id_constructor];
                length = iArr[Id_toString];
                i4 = iArr[Id_toTimeString];
                i5 = iArr[Id_toDateString];
                i6 = iArr[Id_toLocaleString];
                i7 = iArr[Id_toLocaleTimeString];
                i8 = iArr[MAXARGS];
                i9 = iArr[Id_toUTCString];
                date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
                if (i8 != ConstructorId_UTC) {
                    date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
                }
                if (date_msecFromDate >= -8.64E15d) {
                    if (date_msecFromDate > HalfTimeDomain) {
                        return date_msecFromDate;
                    }
                }
                return ScriptRuntime.NaN;
            } else if (charAt3 == 'T') {
                i12 = Id_constructor;
                i = Id_constructor;
                i2 = Id_toTimeString;
                while (i2 != ConstructorId_UTC) {
                    if (i2 == 0) {
                        if (i2 == Id_toLocaleTimeString) {
                        }
                    }
                    i4 = i12 + i3;
                    if (i4 > length) {
                        i3 = i12;
                        i12 = 0;
                        while (i3 < i4) {
                            charAt = str.charAt(i3);
                            if (charAt < '0') {
                            }
                            i2 = ConstructorId_UTC;
                            i12 = i3;
                        }
                        iArr[i2] = i12;
                        if (i3 == length) {
                            i12 = i3 + Id_constructor;
                            charAt2 = str.charAt(i3);
                            if (charAt2 != 'Z') {
                                iArr[MAXARGS] = 0;
                                iArr[Id_toUTCString] = 0;
                                switch (i2) {
                                    case Id_toDateString /*4*/:
                                    case Id_toLocaleString /*5*/:
                                    case Id_toLocaleTimeString /*6*/:
                                        break;
                                    default:
                                        i2 = ConstructorId_UTC;
                                        break;
                                }
                            }
                            switch (i2) {
                                case NativeRegExp.TEST /*0*/:
                                case Id_constructor /*1*/:
                                    if (charAt2 == '-') {
                                        if (charAt2 == 'T') {
                                        }
                                    }
                                    i3 = i2;
                                    break;
                                case Id_toString /*2*/:
                                    if (charAt2 == 'T') {
                                    }
                                    i3 = charAt2 == 'T' ? Id_toTimeString : ConstructorId_UTC;
                                    break;
                                case Id_toTimeString /*3*/:
                                    if (charAt2 == ':') {
                                    }
                                    i3 = charAt2 == ':' ? Id_toDateString : ConstructorId_UTC;
                                    break;
                                case Id_toDateString /*4*/:
                                    if (charAt2 != ':') {
                                        if (charAt2 != '+') {
                                            break;
                                        }
                                    }
                                    i3 = i2;
                                    break;
                                case Id_toLocaleString /*5*/:
                                    if (charAt2 != '.') {
                                        if (charAt2 != '+') {
                                            break;
                                        }
                                    }
                                    i3 = i2;
                                    break;
                                case Id_toLocaleTimeString /*6*/:
                                    if (charAt2 != '+') {
                                        break;
                                    }
                                    i3 = i2;
                                    break;
                                case MAXARGS /*7*/:
                                    if (charAt2 != ':') {
                                    }
                                    i3 = Id_toUTCString;
                                    i12 = i2;
                                    break;
                                case Id_toUTCString /*8*/:
                                    i3 = ConstructorId_UTC;
                                    break;
                                default:
                                    i3 = i2;
                                    break;
                            }
                            if (i3 == MAXARGS) {
                            }
                            i11 = i2;
                            i2 = i3;
                        } else {
                            switch (i2) {
                                case Id_toTimeString /*3*/:
                                case MAXARGS /*7*/:
                                    i2 = ConstructorId_UTC;
                                    break;
                            }
                            i12 = i3;
                        }
                    } else {
                        i2 = ConstructorId_UTC;
                    }
                    i2 = iArr[0];
                    i3 = iArr[Id_constructor];
                    length = iArr[Id_toString];
                    i4 = iArr[Id_toTimeString];
                    i5 = iArr[Id_toDateString];
                    i6 = iArr[Id_toLocaleString];
                    i7 = iArr[Id_toLocaleTimeString];
                    i8 = iArr[MAXARGS];
                    i9 = iArr[Id_toUTCString];
                    date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
                    if (i8 != ConstructorId_UTC) {
                        date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
                    }
                    if (date_msecFromDate >= -8.64E15d) {
                        if (date_msecFromDate > HalfTimeDomain) {
                            return date_msecFromDate;
                        }
                    }
                    return ScriptRuntime.NaN;
                }
                i2 = iArr[0];
                i3 = iArr[Id_constructor];
                length = iArr[Id_toString];
                i4 = iArr[Id_toTimeString];
                i5 = iArr[Id_toDateString];
                i6 = iArr[Id_toLocaleString];
                i7 = iArr[Id_toLocaleTimeString];
                i8 = iArr[MAXARGS];
                i9 = iArr[Id_toUTCString];
                date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
                if (i8 != ConstructorId_UTC) {
                    date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
                }
                if (date_msecFromDate >= -8.64E15d) {
                    if (date_msecFromDate > HalfTimeDomain) {
                        return date_msecFromDate;
                    }
                }
                return ScriptRuntime.NaN;
            }
        }
        i = Id_constructor;
        i2 = 0;
        while (i2 != ConstructorId_UTC) {
            if (i2 == 0) {
            }
            i4 = i12 + i3;
            if (i4 > length) {
                i2 = ConstructorId_UTC;
            } else {
                i3 = i12;
                i12 = 0;
                while (i3 < i4) {
                    charAt = str.charAt(i3);
                    if (charAt < '0') {
                    }
                    i2 = ConstructorId_UTC;
                    i12 = i3;
                }
                iArr[i2] = i12;
                if (i3 == length) {
                    switch (i2) {
                        case Id_toTimeString /*3*/:
                        case MAXARGS /*7*/:
                            i2 = ConstructorId_UTC;
                            break;
                    }
                    i12 = i3;
                } else {
                    i12 = i3 + Id_constructor;
                    charAt2 = str.charAt(i3);
                    if (charAt2 != 'Z') {
                        iArr[MAXARGS] = 0;
                        iArr[Id_toUTCString] = 0;
                        switch (i2) {
                            case Id_toDateString /*4*/:
                            case Id_toLocaleString /*5*/:
                            case Id_toLocaleTimeString /*6*/:
                                break;
                            default:
                                i2 = ConstructorId_UTC;
                                break;
                        }
                    }
                    switch (i2) {
                        case NativeRegExp.TEST /*0*/:
                        case Id_constructor /*1*/:
                            if (charAt2 == '-') {
                            }
                            i3 = i2;
                            break;
                        case Id_toString /*2*/:
                            if (charAt2 == 'T') {
                            }
                            i3 = charAt2 == 'T' ? Id_toTimeString : ConstructorId_UTC;
                            break;
                        case Id_toTimeString /*3*/:
                            if (charAt2 == ':') {
                            }
                            i3 = charAt2 == ':' ? Id_toDateString : ConstructorId_UTC;
                            break;
                        case Id_toDateString /*4*/:
                            if (charAt2 != ':') {
                                if (charAt2 != '+') {
                                    break;
                                }
                            }
                            i3 = i2;
                            break;
                        case Id_toLocaleString /*5*/:
                            if (charAt2 != '.') {
                                if (charAt2 != '+') {
                                    break;
                                }
                            }
                            i3 = i2;
                            break;
                        case Id_toLocaleTimeString /*6*/:
                            if (charAt2 != '+') {
                                break;
                            }
                            i3 = i2;
                            break;
                        case MAXARGS /*7*/:
                            if (charAt2 != ':') {
                            }
                            i3 = Id_toUTCString;
                            i12 = i2;
                            break;
                        case Id_toUTCString /*8*/:
                            i3 = ConstructorId_UTC;
                            break;
                        default:
                            i3 = i2;
                            break;
                    }
                    if (i3 == MAXARGS) {
                        if (charAt2 == '-') {
                        }
                    }
                    i11 = i2;
                    i2 = i3;
                }
            }
            i2 = iArr[0];
            i3 = iArr[Id_constructor];
            length = iArr[Id_toString];
            i4 = iArr[Id_toTimeString];
            i5 = iArr[Id_toDateString];
            i6 = iArr[Id_toLocaleString];
            i7 = iArr[Id_toLocaleTimeString];
            i8 = iArr[MAXARGS];
            i9 = iArr[Id_toUTCString];
            date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
            if (i8 != ConstructorId_UTC) {
                date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
            }
            if (date_msecFromDate >= -8.64E15d) {
                if (date_msecFromDate > HalfTimeDomain) {
                    return date_msecFromDate;
                }
            }
            return ScriptRuntime.NaN;
        }
        i2 = iArr[0];
        i3 = iArr[Id_constructor];
        length = iArr[Id_toString];
        i4 = iArr[Id_toTimeString];
        i5 = iArr[Id_toDateString];
        i6 = iArr[Id_toLocaleString];
        i7 = iArr[Id_toLocaleTimeString];
        i8 = iArr[MAXARGS];
        i9 = iArr[Id_toUTCString];
        date_msecFromDate = date_msecFromDate((double) (i2 * i), (double) (i3 + ConstructorId_UTC), (double) length, (double) i4, (double) i5, (double) i6, (double) i7);
        if (i8 != ConstructorId_UTC) {
            date_msecFromDate -= (((double) ((i8 * 60) + i9)) * msPerMinute) * ((double) i11);
        }
        if (date_msecFromDate >= -8.64E15d) {
            if (date_msecFromDate > HalfTimeDomain) {
                return date_msecFromDate;
            }
        }
        return ScriptRuntime.NaN;
    }

    private static String toLocale_helper(double d, int i) {
        DateFormat dateFormat;
        String format;
        switch (i) {
            case Id_toLocaleString /*5*/:
                if (localeDateTimeFormatter == null) {
                    localeDateTimeFormatter = DateFormat.getDateTimeInstance(Id_constructor, Id_constructor);
                }
                dateFormat = localeDateTimeFormatter;
                break;
            case Id_toLocaleTimeString /*6*/:
                if (localeTimeFormatter == null) {
                    localeTimeFormatter = DateFormat.getTimeInstance(Id_constructor);
                }
                dateFormat = localeTimeFormatter;
                break;
            case MAXARGS /*7*/:
                if (localeDateFormatter == null) {
                    localeDateFormatter = DateFormat.getDateInstance(Id_constructor);
                }
                dateFormat = localeDateFormatter;
                break;
            default:
                throw new AssertionError();
        }
        synchronized (dateFormat) {
            format = dateFormat.format(new Date((long) d));
        }
        return format;
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(DATE_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        double doubleValue;
        switch (methodId) {
            case ConstructorId_now /*-3*/:
                return ScriptRuntime.wrapNumber(now());
            case ConstructorId_parse /*-2*/:
                return ScriptRuntime.wrapNumber(date_parseString(ScriptRuntime.toString(objArr, 0)));
            case ConstructorId_UTC /*-1*/:
                return ScriptRuntime.wrapNumber(jsStaticFunction_UTC(objArr));
            case Id_constructor /*1*/:
                return scriptable2 != null ? date_format(now(), Id_toString) : jsConstructor(objArr);
            case MAX_PROTOTYPE_ID /*47*/:
                Object toObject = ScriptRuntime.toObject(context, scriptable, (Object) scriptable2);
                Object toPrimitive = ScriptRuntime.toPrimitive(toObject, ScriptRuntime.NumberClass);
                if (toPrimitive instanceof Number) {
                    doubleValue = ((Number) toPrimitive).doubleValue();
                    if (doubleValue != doubleValue || Double.isInfinite(doubleValue)) {
                        return null;
                    }
                }
                toPrimitive = ScriptableObject.getProperty((Scriptable) toObject, "toISOString");
                if (toPrimitive == NOT_FOUND) {
                    throw ScriptRuntime.typeError2("msg.function.not.found.in", "toISOString", ScriptRuntime.toString(toObject));
                } else if (toPrimitive instanceof Callable) {
                    toPrimitive = ((Callable) toPrimitive).call(context, scriptable, toObject, ScriptRuntime.emptyArgs);
                    if (ScriptRuntime.isPrimitive(toPrimitive)) {
                        return toPrimitive;
                    }
                    throw ScriptRuntime.typeError1("msg.toisostring.must.return.primitive", ScriptRuntime.toString(toPrimitive));
                } else {
                    throw ScriptRuntime.typeError3("msg.isnt.function.in", "toISOString", ScriptRuntime.toString(toObject), ScriptRuntime.toString(toPrimitive));
                }
            default:
                if (scriptable2 instanceof NativeDate) {
                    NativeDate nativeDate = (NativeDate) scriptable2;
                    double d = nativeDate.date;
                    switch (methodId) {
                        case Id_toString /*2*/:
                        case Id_toTimeString /*3*/:
                        case Id_toDateString /*4*/:
                            return d == d ? date_format(d, methodId) : js_NaN_date_str;
                        case Id_toLocaleString /*5*/:
                        case Id_toLocaleTimeString /*6*/:
                        case MAXARGS /*7*/:
                            return d == d ? toLocale_helper(d, methodId) : js_NaN_date_str;
                        case Id_toUTCString /*8*/:
                            return d == d ? js_toUTCString(d) : js_NaN_date_str;
                        case Id_toSource /*9*/:
                            return "(new Date(" + ScriptRuntime.toString(d) + "))";
                        case Id_valueOf /*10*/:
                        case Id_getTime /*11*/:
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getYear /*12*/:
                        case Id_getFullYear /*13*/:
                        case Id_getUTCFullYear /*14*/:
                            if (d == d) {
                                if (methodId != Id_getUTCFullYear) {
                                    d = LocalTime(d);
                                }
                                d = (double) YearFromTime(d);
                                if (methodId == Id_getYear) {
                                    if (!context.hasFeature(Id_constructor)) {
                                        d -= 1900.0d;
                                    } else if (1900.0d <= d && d < 2000.0d) {
                                        d -= 1900.0d;
                                    }
                                }
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getMonth /*15*/:
                        case Id_getUTCMonth /*16*/:
                            if (d == d) {
                                if (methodId == Id_getMonth) {
                                    d = LocalTime(d);
                                }
                                d = (double) MonthFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getDate /*17*/:
                        case Id_getUTCDate /*18*/:
                            if (d == d) {
                                if (methodId == Id_getDate) {
                                    d = LocalTime(d);
                                }
                                d = (double) DateFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getDay /*19*/:
                        case Id_getUTCDay /*20*/:
                            if (d == d) {
                                if (methodId == Id_getDay) {
                                    d = LocalTime(d);
                                }
                                d = (double) WeekDay(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getHours /*21*/:
                        case Id_getUTCHours /*22*/:
                            if (d == d) {
                                if (methodId == Id_getHours) {
                                    d = LocalTime(d);
                                }
                                d = (double) HourFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getMinutes /*23*/:
                        case Id_getUTCMinutes /*24*/:
                            if (d == d) {
                                if (methodId == Id_getMinutes) {
                                    d = LocalTime(d);
                                }
                                d = (double) MinFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getSeconds /*25*/:
                        case Id_getUTCSeconds /*26*/:
                            if (d == d) {
                                if (methodId == Id_getSeconds) {
                                    d = LocalTime(d);
                                }
                                d = (double) SecFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getMilliseconds /*27*/:
                        case Id_getUTCMilliseconds /*28*/:
                            if (d == d) {
                                if (methodId == Id_getMilliseconds) {
                                    d = LocalTime(d);
                                }
                                d = (double) msFromTime(d);
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_getTimezoneOffset /*29*/:
                            if (d == d) {
                                d = (d - LocalTime(d)) / msPerMinute;
                            }
                            return ScriptRuntime.wrapNumber(d);
                        case Id_setTime /*30*/:
                            d = TimeClip(ScriptRuntime.toNumber(objArr, 0));
                            nativeDate.date = d;
                            return ScriptRuntime.wrapNumber(d);
                        case Id_setMilliseconds /*31*/:
                        case Id_setUTCMilliseconds /*32*/:
                        case Id_setSeconds /*33*/:
                        case Id_setUTCSeconds /*34*/:
                        case Id_setMinutes /*35*/:
                        case Id_setUTCMinutes /*36*/:
                        case Id_setHours /*37*/:
                        case Id_setUTCHours /*38*/:
                            d = makeTime(d, objArr, methodId);
                            nativeDate.date = d;
                            return ScriptRuntime.wrapNumber(d);
                        case Id_setDate /*39*/:
                        case Id_setUTCDate /*40*/:
                        case Id_setMonth /*41*/:
                        case Id_setUTCMonth /*42*/:
                        case Id_setFullYear /*43*/:
                        case Id_setUTCFullYear /*44*/:
                            d = makeDate(d, objArr, methodId);
                            nativeDate.date = d;
                            return ScriptRuntime.wrapNumber(d);
                        case Id_setYear /*45*/:
                            doubleValue = ScriptRuntime.toNumber(objArr, 0);
                            if (doubleValue != doubleValue || Double.isInfinite(doubleValue)) {
                                d = ScriptRuntime.NaN;
                            } else {
                                double LocalTime = d != d ? 0.0d : LocalTime(d);
                                d = (doubleValue < 0.0d || doubleValue > 99.0d) ? doubleValue : doubleValue + 1900.0d;
                                d = TimeClip(internalUTC(MakeDate(MakeDay(d, (double) MonthFromTime(LocalTime), (double) DateFromTime(LocalTime)), TimeWithinDay(LocalTime))));
                            }
                            nativeDate.date = d;
                            return ScriptRuntime.wrapNumber(d);
                        case Id_toISOString /*46*/:
                            if (d == d) {
                                return js_toISOString(d);
                            }
                            throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage0("msg.invalid.date"));
                        default:
                            throw new IllegalArgumentException(String.valueOf(methodId));
                    }
                }
                throw IdScriptableObject.incompatibleCallError(idFunctionObject);
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        addIdFunctionProperty(idFunctionObject, DATE_TAG, ConstructorId_now, "now", 0);
        addIdFunctionProperty(idFunctionObject, DATE_TAG, ConstructorId_parse, "parse", Id_constructor);
        addIdFunctionProperty(idFunctionObject, DATE_TAG, ConstructorId_UTC, "UTC", MAXARGS);
        super.fillConstructorProperties(idFunctionObject);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r9) {
        /*
        r8 = this;
        r6 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        r2 = 3;
        r5 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        r4 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r0 = 0;
        r1 = 0;
        r3 = r9.length();
        switch(r3) {
            case 6: goto L_0x001d;
            case 7: goto L_0x0035;
            case 8: goto L_0x008f;
            case 9: goto L_0x00df;
            case 10: goto L_0x00e8;
            case 11: goto L_0x0146;
            case 12: goto L_0x01d2;
            case 13: goto L_0x01ee;
            case 14: goto L_0x0234;
            case 15: goto L_0x0258;
            case 16: goto L_0x0010;
            case 17: goto L_0x0272;
            case 18: goto L_0x027b;
            default: goto L_0x0010;
        };
    L_0x0010:
        r2 = r1;
        r1 = r0;
    L_0x0012:
        if (r2 == 0) goto L_0x02b5;
    L_0x0014:
        if (r2 == r9) goto L_0x02b5;
    L_0x0016:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x02b5;
    L_0x001c:
        return r0;
    L_0x001d:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x002b;
    L_0x0023:
        r1 = "getDay";
        r2 = 19;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x002b:
        if (r2 != r6) goto L_0x0010;
    L_0x002d:
        r1 = "toJSON";
        r2 = 47;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0035:
        r2 = r9.charAt(r2);
        switch(r2) {
            case 68: goto L_0x003f;
            case 84: goto L_0x0057;
            case 89: goto L_0x006f;
            case 117: goto L_0x0087;
            default: goto L_0x003c;
        };
    L_0x003c:
        r2 = r1;
        r1 = r0;
        goto L_0x0012;
    L_0x003f:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x004d;
    L_0x0045:
        r1 = "getDate";
        r2 = 17;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x004d:
        if (r2 != r5) goto L_0x0010;
    L_0x004f:
        r1 = "setDate";
        r2 = 39;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0057:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x0065;
    L_0x005d:
        r1 = "getTime";
        r2 = 11;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0065:
        if (r2 != r5) goto L_0x0010;
    L_0x0067:
        r1 = "setTime";
        r2 = 30;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x006f:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x007d;
    L_0x0075:
        r1 = "getYear";
        r2 = 12;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x007d:
        if (r2 != r5) goto L_0x0010;
    L_0x007f:
        r1 = "setYear";
        r2 = 45;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0087:
        r1 = "valueOf";
        r2 = 10;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x008f:
        r2 = r9.charAt(r2);
        switch(r2) {
            case 72: goto L_0x009a;
            case 77: goto L_0x00b4;
            case 111: goto L_0x00ce;
            case 116: goto L_0x00d7;
            default: goto L_0x0096;
        };
    L_0x0096:
        r2 = r1;
        r1 = r0;
        goto L_0x0012;
    L_0x009a:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x00a9;
    L_0x00a0:
        r1 = "getHours";
        r2 = 21;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00a9:
        if (r2 != r5) goto L_0x0010;
    L_0x00ab:
        r1 = "setHours";
        r2 = 37;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00b4:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x00c3;
    L_0x00ba:
        r1 = "getMonth";
        r2 = 15;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00c3:
        if (r2 != r5) goto L_0x0010;
    L_0x00c5:
        r1 = "setMonth";
        r2 = 41;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00ce:
        r1 = "toSource";
        r2 = 9;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00d7:
        r1 = "toString";
        r2 = 2;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00df:
        r1 = "getUTCDay";
        r2 = 20;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00e8:
        r2 = r9.charAt(r2);
        r3 = 77;
        if (r2 != r3) goto L_0x010a;
    L_0x00f0:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x00ff;
    L_0x00f6:
        r1 = "getMinutes";
        r2 = 23;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x00ff:
        if (r2 != r5) goto L_0x0010;
    L_0x0101:
        r1 = "setMinutes";
        r2 = 35;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x010a:
        r3 = 83;
        if (r2 != r3) goto L_0x0128;
    L_0x010e:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x011d;
    L_0x0114:
        r1 = "getSeconds";
        r2 = 25;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x011d:
        if (r2 != r5) goto L_0x0010;
    L_0x011f:
        r1 = "setSeconds";
        r2 = 33;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0128:
        r3 = 85;
        if (r2 != r3) goto L_0x0010;
    L_0x012c:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x013b;
    L_0x0132:
        r1 = "getUTCDate";
        r2 = 18;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x013b:
        if (r2 != r5) goto L_0x0010;
    L_0x013d:
        r1 = "setUTCDate";
        r2 = 40;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0146:
        r2 = r9.charAt(r2);
        switch(r2) {
            case 70: goto L_0x0151;
            case 77: goto L_0x016b;
            case 83: goto L_0x0174;
            case 84: goto L_0x017d;
            case 85: goto L_0x0186;
            case 115: goto L_0x01ca;
            default: goto L_0x014d;
        };
    L_0x014d:
        r2 = r1;
        r1 = r0;
        goto L_0x0012;
    L_0x0151:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x0160;
    L_0x0157:
        r1 = "getFullYear";
        r2 = 13;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0160:
        if (r2 != r5) goto L_0x0010;
    L_0x0162:
        r1 = "setFullYear";
        r2 = 43;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x016b:
        r1 = "toGMTString";
        r2 = 8;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0174:
        r1 = "toISOString";
        r2 = 46;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x017d:
        r1 = "toUTCString";
        r2 = 8;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0186:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x01aa;
    L_0x018c:
        r2 = 9;
        r2 = r9.charAt(r2);
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r2 != r3) goto L_0x019f;
    L_0x0196:
        r1 = "getUTCHours";
        r2 = 22;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x019f:
        if (r2 != r6) goto L_0x0010;
    L_0x01a1:
        r1 = "getUTCMonth";
        r2 = 16;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01aa:
        if (r2 != r5) goto L_0x0010;
    L_0x01ac:
        r2 = 9;
        r2 = r9.charAt(r2);
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r2 != r3) goto L_0x01bf;
    L_0x01b6:
        r1 = "setUTCHours";
        r2 = 38;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01bf:
        if (r2 != r6) goto L_0x0010;
    L_0x01c1:
        r1 = "setUTCMonth";
        r2 = 42;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01ca:
        r1 = "constructor";
        r2 = 1;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01d2:
        r3 = 2;
        r3 = r9.charAt(r3);
        r4 = 68;
        if (r3 != r4) goto L_0x01e3;
    L_0x01db:
        r1 = "toDateString";
        r2 = 4;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01e3:
        r4 = 84;
        if (r3 != r4) goto L_0x0010;
    L_0x01e7:
        r1 = "toTimeString";
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x01ee:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x0213;
    L_0x01f4:
        r2 = 6;
        r2 = r9.charAt(r2);
        r3 = 77;
        if (r2 != r3) goto L_0x0206;
    L_0x01fd:
        r1 = "getUTCMinutes";
        r2 = 24;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0206:
        r3 = 83;
        if (r2 != r3) goto L_0x0010;
    L_0x020a:
        r1 = "getUTCSeconds";
        r2 = 26;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0213:
        if (r2 != r5) goto L_0x0010;
    L_0x0215:
        r2 = 6;
        r2 = r9.charAt(r2);
        r3 = 77;
        if (r2 != r3) goto L_0x0227;
    L_0x021e:
        r1 = "setUTCMinutes";
        r2 = 36;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0227:
        r3 = 83;
        if (r2 != r3) goto L_0x0010;
    L_0x022b:
        r1 = "setUTCSeconds";
        r2 = 34;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0234:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x0243;
    L_0x023a:
        r1 = "getUTCFullYear";
        r2 = 14;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0243:
        if (r2 != r5) goto L_0x024e;
    L_0x0245:
        r1 = "setUTCFullYear";
        r2 = 44;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x024e:
        if (r2 != r6) goto L_0x0010;
    L_0x0250:
        r1 = "toLocaleString";
        r2 = 5;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0258:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x0267;
    L_0x025e:
        r1 = "getMilliseconds";
        r2 = 27;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0267:
        if (r2 != r5) goto L_0x0010;
    L_0x0269:
        r1 = "setMilliseconds";
        r2 = 31;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0272:
        r1 = "getTimezoneOffset";
        r2 = 29;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x027b:
        r2 = r9.charAt(r0);
        if (r2 != r4) goto L_0x028a;
    L_0x0281:
        r1 = "getUTCMilliseconds";
        r2 = 28;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x028a:
        if (r2 != r5) goto L_0x0295;
    L_0x028c:
        r1 = "setUTCMilliseconds";
        r2 = 32;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x0295:
        if (r2 != r6) goto L_0x0010;
    L_0x0297:
        r2 = 8;
        r2 = r9.charAt(r2);
        r3 = 68;
        if (r2 != r3) goto L_0x02a9;
    L_0x02a1:
        r1 = "toLocaleDateString";
        r2 = 7;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x02a9:
        r3 = 84;
        if (r2 != r3) goto L_0x0010;
    L_0x02ad:
        r1 = "toLocaleTimeString";
        r2 = 6;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0012;
    L_0x02b5:
        r0 = r1;
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeDate.findPrototypeId(java.lang.String):int");
    }

    public String getClassName() {
        return "Date";
    }

    public Object getDefaultValue(Class<?> cls) {
        Class cls2;
        if (cls == null) {
            cls2 = ScriptRuntime.StringClass;
        }
        return super.getDefaultValue(cls2);
    }

    double getJSTimeValue() {
        return this.date;
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = 0;
        switch (i) {
            case Id_constructor /*1*/:
                i2 = MAXARGS;
                str = "constructor";
                break;
            case Id_toString /*2*/:
                str = "toString";
                break;
            case Id_toTimeString /*3*/:
                str = "toTimeString";
                break;
            case Id_toDateString /*4*/:
                str = "toDateString";
                break;
            case Id_toLocaleString /*5*/:
                str = "toLocaleString";
                break;
            case Id_toLocaleTimeString /*6*/:
                str = "toLocaleTimeString";
                break;
            case MAXARGS /*7*/:
                str = "toLocaleDateString";
                break;
            case Id_toUTCString /*8*/:
                str = "toUTCString";
                break;
            case Id_toSource /*9*/:
                str = "toSource";
                break;
            case Id_valueOf /*10*/:
                str = "valueOf";
                break;
            case Id_getTime /*11*/:
                str = "getTime";
                break;
            case Id_getYear /*12*/:
                str = "getYear";
                break;
            case Id_getFullYear /*13*/:
                str = "getFullYear";
                break;
            case Id_getUTCFullYear /*14*/:
                str = "getUTCFullYear";
                break;
            case Id_getMonth /*15*/:
                str = "getMonth";
                break;
            case Id_getUTCMonth /*16*/:
                str = "getUTCMonth";
                break;
            case Id_getDate /*17*/:
                str = "getDate";
                break;
            case Id_getUTCDate /*18*/:
                str = "getUTCDate";
                break;
            case Id_getDay /*19*/:
                str = "getDay";
                break;
            case Id_getUTCDay /*20*/:
                str = "getUTCDay";
                break;
            case Id_getHours /*21*/:
                str = "getHours";
                break;
            case Id_getUTCHours /*22*/:
                str = "getUTCHours";
                break;
            case Id_getMinutes /*23*/:
                str = "getMinutes";
                break;
            case Id_getUTCMinutes /*24*/:
                str = "getUTCMinutes";
                break;
            case Id_getSeconds /*25*/:
                str = "getSeconds";
                break;
            case Id_getUTCSeconds /*26*/:
                str = "getUTCSeconds";
                break;
            case Id_getMilliseconds /*27*/:
                str = "getMilliseconds";
                break;
            case Id_getUTCMilliseconds /*28*/:
                str = "getUTCMilliseconds";
                break;
            case Id_getTimezoneOffset /*29*/:
                str = "getTimezoneOffset";
                break;
            case Id_setTime /*30*/:
                str = "setTime";
                i2 = Id_constructor;
                break;
            case Id_setMilliseconds /*31*/:
                str = "setMilliseconds";
                i2 = Id_constructor;
                break;
            case Id_setUTCMilliseconds /*32*/:
                str = "setUTCMilliseconds";
                i2 = Id_constructor;
                break;
            case Id_setSeconds /*33*/:
                str = "setSeconds";
                i2 = Id_toString;
                break;
            case Id_setUTCSeconds /*34*/:
                str = "setUTCSeconds";
                i2 = Id_toString;
                break;
            case Id_setMinutes /*35*/:
                str = "setMinutes";
                i2 = Id_toTimeString;
                break;
            case Id_setUTCMinutes /*36*/:
                str = "setUTCMinutes";
                i2 = Id_toTimeString;
                break;
            case Id_setHours /*37*/:
                str = "setHours";
                i2 = Id_toDateString;
                break;
            case Id_setUTCHours /*38*/:
                str = "setUTCHours";
                i2 = Id_toDateString;
                break;
            case Id_setDate /*39*/:
                str = "setDate";
                i2 = Id_constructor;
                break;
            case Id_setUTCDate /*40*/:
                str = "setUTCDate";
                i2 = Id_constructor;
                break;
            case Id_setMonth /*41*/:
                str = "setMonth";
                i2 = Id_toString;
                break;
            case Id_setUTCMonth /*42*/:
                str = "setUTCMonth";
                i2 = Id_toString;
                break;
            case Id_setFullYear /*43*/:
                str = "setFullYear";
                i2 = Id_toTimeString;
                break;
            case Id_setUTCFullYear /*44*/:
                str = "setUTCFullYear";
                i2 = Id_toTimeString;
                break;
            case Id_setYear /*45*/:
                str = "setYear";
                i2 = Id_constructor;
                break;
            case Id_toISOString /*46*/:
                str = "toISOString";
                break;
            case MAX_PROTOTYPE_ID /*47*/:
                str = "toJSON";
                i2 = Id_constructor;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(DATE_TAG, i, str, i2);
    }
}
