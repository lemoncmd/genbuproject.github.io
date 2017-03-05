package org.mozilla.javascript;

public class IdFunctionObjectES6 extends IdFunctionObject {
    private static final int Id_length = 1;
    private static final int Id_name = 3;
    private boolean myLength = true;
    private boolean myName = true;

    public IdFunctionObjectES6(IdFunctionCall idFunctionCall, Object obj, int i, String str, int i2, Scriptable scriptable) {
        super(idFunctionCall, obj, i, str, i2, scriptable);
    }

    protected int findInstanceIdInfo(String str) {
        return str.equals(Name.LENGTH) ? IdScriptableObject.instanceIdInfo(Id_name, Id_length) : str.equals("name") ? IdScriptableObject.instanceIdInfo(Id_name, Id_name) : super.findInstanceIdInfo(str);
    }

    protected Object getInstanceIdValue(int i) {
        return (i != Id_length || this.myLength) ? (i != Id_name || this.myName) ? super.getInstanceIdValue(i) : NOT_FOUND : NOT_FOUND;
    }

    protected void setInstanceIdValue(int i, Object obj) {
        if (i == Id_length && obj == NOT_FOUND) {
            this.myLength = false;
        } else if (i == Id_name && obj == NOT_FOUND) {
            this.myName = false;
        } else {
            super.setInstanceIdValue(i, obj);
        }
    }
}
