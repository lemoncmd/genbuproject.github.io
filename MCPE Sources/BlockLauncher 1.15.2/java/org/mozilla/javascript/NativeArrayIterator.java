package org.mozilla.javascript;

public final class NativeArrayIterator extends ES6Iterator {
    private static final String ITERATOR_TAG = "ArrayIterator";
    private static final long serialVersionUID = 1;
    private Scriptable arrayLike;
    private int index;

    private NativeArrayIterator() {
    }

    public NativeArrayIterator(Scriptable scriptable, Scriptable scriptable2) {
        super(scriptable);
        this.index = 0;
        this.arrayLike = scriptable2;
    }

    static void init(ScriptableObject scriptableObject, boolean z) {
        ES6Iterator.init(scriptableObject, z, new NativeArrayIterator(), ITERATOR_TAG);
    }

    public String getClassName() {
        return "Array Iterator";
    }

    protected String getTag() {
        return ITERATOR_TAG;
    }

    protected boolean isDone(Context context, Scriptable scriptable) {
        return ((long) this.index) >= NativeArray.getLengthProperty(context, this.arrayLike);
    }

    protected Object nextValue(Context context, Scriptable scriptable) {
        Scriptable scriptable2 = this.arrayLike;
        int i = this.index;
        this.index = i + 1;
        Object obj = scriptable2.get(i, this.arrayLike);
        return obj == ScriptableObject.NOT_FOUND ? Undefined.instance : obj;
    }
}
