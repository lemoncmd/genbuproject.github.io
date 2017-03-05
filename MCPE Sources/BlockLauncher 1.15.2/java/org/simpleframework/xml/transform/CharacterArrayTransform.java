package org.simpleframework.xml.transform;

import java.lang.reflect.Array;

class CharacterArrayTransform implements Transform {
    private final Class entry;

    public CharacterArrayTransform(Class cls) {
        this.entry = cls;
    }

    private Object read(char[] cArr, int i) throws Exception {
        Object newInstance = Array.newInstance(this.entry, i);
        for (int i2 = 0; i2 < i; i2++) {
            Array.set(newInstance, i2, Character.valueOf(cArr[i2]));
        }
        return newInstance;
    }

    private String write(Object obj, int i) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(i);
        for (int i2 = 0; i2 < i; i2++) {
            Object obj2 = Array.get(obj, i2);
            if (obj2 != null) {
                stringBuilder.append(obj2);
            }
        }
        return stringBuilder.toString();
    }

    public Object read(String str) throws Exception {
        Object toCharArray = str.toCharArray();
        return this.entry == Character.TYPE ? toCharArray : read(toCharArray, toCharArray.length);
    }

    public String write(Object obj) throws Exception {
        return this.entry == Character.TYPE ? new String((char[]) obj) : write(obj, Array.getLength(obj));
    }
}
