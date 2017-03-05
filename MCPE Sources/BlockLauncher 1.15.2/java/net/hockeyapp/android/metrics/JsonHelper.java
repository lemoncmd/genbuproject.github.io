package net.hockeyapp.android.metrics;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.metrics.model.IJsonSerializable;

public final class JsonHelper {
    private static final String[] CONTROL_CHARACTERS = new String[CONTROL_CHARACTER_RANGE];
    private static final int CONTROL_CHARACTER_RANGE = 128;

    static {
        for (int i = 0; i <= 31; i++) {
            CONTROL_CHARACTERS[i] = String.format("\\u%04X", new Object[]{Integer.valueOf(i)});
        }
        CONTROL_CHARACTERS[34] = "\\\"";
        CONTROL_CHARACTERS[92] = "\\\\";
        CONTROL_CHARACTERS[8] = "\\b";
        CONTROL_CHARACTERS[12] = "\\f";
        CONTROL_CHARACTERS[10] = "\\n";
        CONTROL_CHARACTERS[13] = "\\r";
        CONTROL_CHARACTERS[9] = "\\t";
    }

    private JsonHelper() {
    }

    public static String convert(char c) {
        return Character.toString(c);
    }

    public static String convert(Double d) {
        return Double.toString(d.doubleValue());
    }

    public static String convert(Float f) {
        return Float.toString(f.floatValue());
    }

    public static String convert(Integer num) {
        return Integer.toString(num.intValue());
    }

    public static String convert(Long l) {
        return Long.toString(l.longValue());
    }

    public static String convert(String str) {
        return str == null ? "null" : str.length() == 0 ? "\"\"" : escapeJSON(str);
    }

    public static String convert(boolean z) {
        return Boolean.toString(z);
    }

    private static String escapeJSON(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt < '\u0080') {
                String str2 = CONTROL_CHARACTERS[charAt];
                if (str2 == null) {
                    stringBuilder.append(charAt);
                } else {
                    stringBuilder.append(str2);
                }
            } else if (charAt == '\u2028') {
                stringBuilder.append("\\u2028");
            } else if (charAt == '\u2029') {
                stringBuilder.append("\\u2029");
            } else {
                stringBuilder.append(charAt);
            }
        }
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    public static <T> void writeDictionary(Writer writer, Map<String, T> map) throws IOException {
        if (map == null || map.isEmpty()) {
            writer.write("null");
            return;
        }
        Iterator it = map.keySet().iterator();
        if (it.hasNext()) {
            writer.write("{");
            String str = (String) it.next();
            Object obj = map.get(str);
            writer.write("\"" + str + "\"");
            writer.write(":");
            writeItem(writer, obj);
            while (it.hasNext()) {
                str = (String) it.next();
                writer.write(",");
                writer.write("\"" + str + "\"");
                writer.write(":");
                writeItem(writer, map.get(str));
            }
            writer.write("}");
        }
    }

    private static <T> void writeItem(Writer writer, T t) throws IOException {
        if (t == null) {
            writer.write("null");
        } else if (t instanceof String) {
            writer.write(convert((String) t));
        } else if (t instanceof Double) {
            writer.write(convert((Double) t));
        } else if (t instanceof Integer) {
            writer.write(convert((Integer) t));
        } else if (t instanceof Long) {
            writer.write(convert((Long) t));
        } else if (t instanceof IJsonSerializable) {
            ((IJsonSerializable) t).serialize(writer);
        } else {
            throw new IOException("Cannot serialize: " + t.toString());
        }
    }

    public static void writeJsonSerializable(Writer writer, IJsonSerializable iJsonSerializable) throws IOException {
        if (iJsonSerializable != null) {
            iJsonSerializable.serialize(writer);
        }
    }

    public static <T extends IJsonSerializable> void writeList(Writer writer, List<T> list) throws IOException {
        if (list == null || list.isEmpty()) {
            writer.write("null");
            return;
        }
        Iterator it = list.iterator();
        if (it.hasNext()) {
            writer.write("[");
            ((IJsonSerializable) it.next()).serialize(writer);
            while (it.hasNext()) {
                IJsonSerializable iJsonSerializable = (IJsonSerializable) it.next();
                writer.write(",");
                iJsonSerializable.serialize(writer);
            }
            writer.write("]");
        }
    }
}
