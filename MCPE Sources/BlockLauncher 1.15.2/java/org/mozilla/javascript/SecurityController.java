package org.mozilla.javascript;

public abstract class SecurityController {
    private static SecurityController global;

    public static GeneratedClassLoader createLoader(ClassLoader classLoader, Object obj) {
        Context context = Context.getContext();
        if (classLoader == null) {
            classLoader = context.getApplicationClassLoader();
        }
        SecurityController securityController = context.getSecurityController();
        return securityController == null ? context.createClassLoader(classLoader) : securityController.createClassLoader(classLoader, securityController.getDynamicSecurityDomain(obj));
    }

    public static Class<?> getStaticSecurityDomainClass() {
        SecurityController securityController = Context.getContext().getSecurityController();
        return securityController == null ? null : securityController.getStaticSecurityDomainClassInternal();
    }

    static SecurityController global() {
        return global;
    }

    public static boolean hasGlobal() {
        return global != null;
    }

    public static void initGlobal(SecurityController securityController) {
        if (securityController == null) {
            throw new IllegalArgumentException();
        } else if (global != null) {
            throw new SecurityException("Cannot overwrite already installed global SecurityController");
        } else {
            global = securityController;
        }
    }

    public Object callWithDomain(Object obj, Context context, final Callable callable, Scriptable scriptable, final Scriptable scriptable2, final Object[] objArr) {
        return execWithDomain(context, scriptable, new Script() {
            public Object exec(Context context, Scriptable scriptable) {
                return callable.call(context, scriptable, scriptable2, objArr);
            }
        }, obj);
    }

    public abstract GeneratedClassLoader createClassLoader(ClassLoader classLoader, Object obj);

    @Deprecated
    public Object execWithDomain(Context context, Scriptable scriptable, Script script, Object obj) {
        throw new IllegalStateException("callWithDomain should be overridden");
    }

    public abstract Object getDynamicSecurityDomain(Object obj);

    public Class<?> getStaticSecurityDomainClassInternal() {
        return null;
    }
}
