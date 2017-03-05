package org.simpleframework.xml.core;

import java.util.Arrays;

class KeyBuilder {
    private final Label label;

    private static class Key {
        private final KeyType type;
        private final String value;

        public Key(KeyType keyType, String str) throws Exception {
            this.value = str;
            this.type = keyType;
        }

        public boolean equals(Object obj) {
            return obj instanceof Key ? equals((Key) obj) : false;
        }

        public boolean equals(Key key) {
            return this.type == key.type ? key.value.equals(this.value) : false;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value;
        }
    }

    private enum KeyType {
        TEXT,
        ATTRIBUTE,
        ELEMENT
    }

    public KeyBuilder(Label label) {
        this.label = label;
    }

    private Object getKey(KeyType keyType) throws Exception {
        String key = getKey(this.label.getPaths());
        return keyType == null ? key : new Key(keyType, key);
    }

    private String getKey(String[] strArr) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if (strArr.length > 0) {
            Arrays.sort(strArr);
            for (String append : strArr) {
                stringBuilder.append(append);
                stringBuilder.append('>');
            }
        }
        return stringBuilder.toString();
    }

    public Object getKey() throws Exception {
        return this.label.isAttribute() ? getKey(KeyType.ATTRIBUTE) : getKey(KeyType.ELEMENT);
    }
}
