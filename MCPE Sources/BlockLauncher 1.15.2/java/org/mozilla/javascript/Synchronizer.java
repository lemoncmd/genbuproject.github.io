package org.mozilla.javascript;

public class Synchronizer extends Delegator {
    private Object syncObject;

    public Synchronizer(Scriptable scriptable) {
        super(scriptable);
    }

    public Synchronizer(Scriptable scriptable, Object obj) {
        super(scriptable);
        this.syncObject = obj;
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        Object obj;
        if (this.syncObject != null) {
            obj = this.syncObject;
        } else {
            Scriptable scriptable3 = scriptable2;
        }
        synchronized ((r0 instanceof Wrapper ? ((Wrapper) r0).unwrap() : r0)) {
            obj = ((Function) this.obj).call(context, scriptable, scriptable2, objArr);
        }
        return obj;
    }
}
