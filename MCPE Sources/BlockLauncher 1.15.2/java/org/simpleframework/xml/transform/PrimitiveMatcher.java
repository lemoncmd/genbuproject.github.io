package org.simpleframework.xml.transform;

class PrimitiveMatcher implements Matcher {
    public Transform match(Class cls) throws Exception {
        return cls == Integer.TYPE ? new IntegerTransform() : cls == Boolean.TYPE ? new BooleanTransform() : cls == Long.TYPE ? new LongTransform() : cls == Double.TYPE ? new DoubleTransform() : cls == Float.TYPE ? new FloatTransform() : cls == Short.TYPE ? new ShortTransform() : cls == Byte.TYPE ? new ByteTransform() : cls == Character.TYPE ? new CharacterTransform() : null;
    }
}
