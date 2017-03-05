package org.simpleframework.xml.transform;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class PackageMatcher implements Matcher {
    private Transform matchEnum(Class cls) {
        Class superclass = cls.getSuperclass();
        if (superclass != null) {
            if (superclass.isEnum()) {
                return new EnumTransform(cls);
            }
            if (cls.isEnum()) {
                return new EnumTransform(cls);
            }
        }
        return null;
    }

    private Transform matchFile(Class cls) throws Exception {
        return cls == File.class ? new FileTransform() : null;
    }

    private Transform matchLanguage(Class cls) throws Exception {
        return cls == Boolean.class ? new BooleanTransform() : cls == Integer.class ? new IntegerTransform() : cls == Long.class ? new LongTransform() : cls == Double.class ? new DoubleTransform() : cls == Float.class ? new FloatTransform() : cls == Short.class ? new ShortTransform() : cls == Byte.class ? new ByteTransform() : cls == Character.class ? new CharacterTransform() : cls == String.class ? new StringTransform() : cls == Class.class ? new ClassTransform() : null;
    }

    private Transform matchMath(Class cls) throws Exception {
        return cls == BigDecimal.class ? new BigDecimalTransform() : cls == BigInteger.class ? new BigIntegerTransform() : null;
    }

    private Transform matchSQL(Class cls) throws Exception {
        return cls == Time.class ? new DateTransform(cls) : cls == Date.class ? new DateTransform(cls) : cls == Timestamp.class ? new DateTransform(cls) : null;
    }

    private Transform matchURL(Class cls) throws Exception {
        return cls == URL.class ? new URLTransform() : null;
    }

    private Transform matchUtility(Class cls) throws Exception {
        return cls == java.util.Date.class ? new DateTransform(cls) : cls == Locale.class ? new LocaleTransform() : cls == Currency.class ? new CurrencyTransform() : cls == GregorianCalendar.class ? new GregorianCalendarTransform() : cls == TimeZone.class ? new TimeZoneTransform() : cls == AtomicInteger.class ? new AtomicIntegerTransform() : cls == AtomicLong.class ? new AtomicLongTransform() : null;
    }

    public Transform match(Class cls) throws Exception {
        String name = cls.getName();
        return name.startsWith("java.lang") ? matchLanguage(cls) : name.startsWith("java.util") ? matchUtility(cls) : name.startsWith("java.net") ? matchURL(cls) : name.startsWith("java.io") ? matchFile(cls) : name.startsWith("java.sql") ? matchSQL(cls) : name.startsWith("java.math") ? matchMath(cls) : matchEnum(cls);
    }
}
