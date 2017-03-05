package org.mozilla.javascript;

public final class NativeStringIterator extends ES6Iterator {
    private static final String ITERATOR_TAG = "StringIterator";
    private static final long serialVersionUID = 1;
    private int index;
    private String string;

    private NativeStringIterator() {
    }

    NativeStringIterator(Scriptable scriptable, Scriptable scriptable2) {
        super(scriptable);
        this.index = 0;
        this.string = ScriptRuntime.toString((Object) scriptable2);
    }

    static void init(ScriptableObject scriptableObject, boolean z) {
        ES6Iterator.init(scriptableObject, z, new NativeStringIterator(), ITERATOR_TAG);
    }

    public String getClassName() {
        return "String Iterator";
    }

    protected String getTag() {
        return ITERATOR_TAG;
    }

    protected boolean isDone(Context context, Scriptable scriptable) {
        return this.index >= this.string.length();
    }

    protected Object nextValue(Context context, Scriptable scriptable) {
        int offsetByCodePoints = this.string.offsetByCodePoints(this.index, 1);
        String substring = this.string.substring(this.index, offsetByCodePoints);
        this.index = offsetByCodePoints;
        return substring;
    }
}
