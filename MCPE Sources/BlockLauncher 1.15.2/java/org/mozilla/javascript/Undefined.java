package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Undefined implements Serializable {
    public static final Scriptable SCRIPTABLE_UNDEFINED = ((Scriptable) Proxy.newProxyInstance(Undefined.class.getClassLoader(), new Class[]{Scriptable.class}, new InvocationHandler() {
        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            boolean z = false;
            if (method.getName().equals("toString")) {
                return "undefined";
            }
            if (method.getName().equals("equals")) {
                if (objArr.length > 0 && Undefined.isUndefined(objArr[0])) {
                    z = true;
                }
                return Boolean.valueOf(z);
            }
            throw new UnsupportedOperationException("undefined doesn't support " + method.getName());
        }
    }));
    public static final Object instance = new Undefined();
    static final long serialVersionUID = 9195680630202616767L;

    private Undefined() {
    }

    public static boolean isUndefined(Object obj) {
        return instance == obj || SCRIPTABLE_UNDEFINED == obj;
    }

    public boolean equals(Object obj) {
        return isUndefined(obj) || super.equals(obj);
    }

    public Object readResolve() {
        return instance;
    }
}
