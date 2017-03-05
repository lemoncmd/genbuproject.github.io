package com.microsoft.onlineid.internal.storage;

import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectStreamSerializer<ObjectType> implements ISerializer<ObjectType> {
    public ObjectType deserialize(String str) throws IOException {
        ObjectType objectType = null;
        if (str != null) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(str, 2)));
                try {
                    objectType = objectInputStream.readObject();
                    objectInputStream.close();
                } catch (Throwable e) {
                    throw new IOException(e);
                } catch (Throwable e2) {
                    throw new IOException(e2);
                } catch (Throwable th) {
                    objectInputStream.close();
                }
            } catch (Throwable e22) {
                throw new IOException(e22);
            }
        }
        return objectType;
    }

    public Set<ObjectType> deserializeAll(Map<String, String> map) throws IOException {
        if (map.isEmpty()) {
            return Collections.emptySet();
        }
        Set<ObjectType> hashSet = new HashSet();
        for (String deserialize : map.values()) {
            hashSet.add(deserialize(deserialize));
        }
        return hashSet;
    }

    public String serialize(ObjectType objectType) throws IOException {
        if (objectType == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        try {
            objectOutputStream.writeObject(objectType);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
        } finally {
            objectOutputStream.close();
        }
    }

    public Map<String, String> serializeAll(Map<String, ObjectType> map) throws IOException {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> hashMap = new HashMap();
        for (Entry entry : map.entrySet()) {
            hashMap.put(entry.getKey(), serialize(entry.getValue()));
        }
        return hashMap;
    }
}
