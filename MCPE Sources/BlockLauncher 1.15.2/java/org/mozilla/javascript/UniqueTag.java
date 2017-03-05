package org.mozilla.javascript;

import java.io.Serializable;

public final class UniqueTag implements Serializable {
    public static final UniqueTag DOUBLE_MARK = new UniqueTag(ID_DOUBLE_MARK);
    private static final int ID_DOUBLE_MARK = 3;
    private static final int ID_NOT_FOUND = 1;
    private static final int ID_NULL_VALUE = 2;
    public static final UniqueTag NOT_FOUND = new UniqueTag(ID_NOT_FOUND);
    public static final UniqueTag NULL_VALUE = new UniqueTag(ID_NULL_VALUE);
    static final long serialVersionUID = -4320556826714577259L;
    private final int tagId;

    private UniqueTag(int i) {
        this.tagId = i;
    }

    public Object readResolve() {
        switch (this.tagId) {
            case ID_NOT_FOUND /*1*/:
                return NOT_FOUND;
            case ID_NULL_VALUE /*2*/:
                return NULL_VALUE;
            case ID_DOUBLE_MARK /*3*/:
                return DOUBLE_MARK;
            default:
                throw new IllegalStateException(String.valueOf(this.tagId));
        }
    }

    public String toString() {
        String str;
        switch (this.tagId) {
            case ID_NOT_FOUND /*1*/:
                str = "NOT_FOUND";
                break;
            case ID_NULL_VALUE /*2*/:
                str = "NULL_VALUE";
                break;
            case ID_DOUBLE_MARK /*3*/:
                str = "DOUBLE_MARK";
                break;
            default:
                throw Kit.codeBug();
        }
        return super.toString() + ": " + str;
    }
}
