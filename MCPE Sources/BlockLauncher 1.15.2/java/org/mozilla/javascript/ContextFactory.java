package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.xml.XMLLib.Factory;

public class ContextFactory {
    private static ContextFactory global = new ContextFactory();
    private static volatile boolean hasCustomGlobal;
    private ClassLoader applicationClassLoader;
    private boolean disabledListening;
    private volatile Object listeners;
    private final Object listenersLock = new Object();
    private volatile boolean sealed;

    public interface GlobalSetter {
        ContextFactory getContextFactoryGlobal();

        void setContextFactoryGlobal(ContextFactory contextFactory);
    }

    public interface Listener {
        void contextCreated(Context context);

        void contextReleased(Context context);
    }

    public static ContextFactory getGlobal() {
        return global;
    }

    public static synchronized GlobalSetter getGlobalSetter() {
        GlobalSetter anonymousClass1GlobalSetterImpl;
        synchronized (ContextFactory.class) {
            if (hasCustomGlobal) {
                throw new IllegalStateException();
            }
            hasCustomGlobal = true;
            anonymousClass1GlobalSetterImpl = new GlobalSetter() {
                public ContextFactory getContextFactoryGlobal() {
                    return ContextFactory.global;
                }

                public void setContextFactoryGlobal(ContextFactory contextFactory) {
                    if (contextFactory == null) {
                        contextFactory = new ContextFactory();
                    }
                    ContextFactory.global = contextFactory;
                }
            };
        }
        return anonymousClass1GlobalSetterImpl;
    }

    public static boolean hasExplicitGlobal() {
        return hasCustomGlobal;
    }

    public static synchronized void initGlobal(ContextFactory contextFactory) {
        synchronized (ContextFactory.class) {
            if (contextFactory == null) {
                throw new IllegalArgumentException();
            } else if (hasCustomGlobal) {
                throw new IllegalStateException();
            } else {
                hasCustomGlobal = true;
                global = contextFactory;
            }
        }
    }

    private boolean isDom3Present() {
        Class classOrNull = Kit.classOrNull("org.w3c.dom.Node");
        if (classOrNull == null) {
            return false;
        }
        try {
            classOrNull.getMethod("getUserData", new Class[]{String.class});
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public final void addListener(Listener listener) {
        checkNotSealed();
        synchronized (this.listenersLock) {
            if (this.disabledListening) {
                throw new IllegalStateException();
            }
            this.listeners = Kit.addListener(this.listeners, listener);
        }
    }

    public final Object call(ContextAction contextAction) {
        return Context.call(this, contextAction);
    }

    protected final void checkNotSealed() {
        if (this.sealed) {
            throw new IllegalStateException();
        }
    }

    protected GeneratedClassLoader createClassLoader(final ClassLoader classLoader) {
        return (GeneratedClassLoader) AccessController.doPrivileged(new PrivilegedAction<DefiningClassLoader>() {
            public DefiningClassLoader run() {
                return new DefiningClassLoader(classLoader);
            }
        });
    }

    final void disableContextListening() {
        checkNotSealed();
        synchronized (this.listenersLock) {
            this.disabledListening = true;
            this.listeners = null;
        }
    }

    protected Object doTopCall(Callable callable, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        Object call = callable.call(context, scriptable, scriptable2, objArr);
        return call instanceof ConsString ? call.toString() : call;
    }

    @Deprecated
    public final Context enter() {
        return enterContext(null);
    }

    public Context enterContext() {
        return enterContext(null);
    }

    public final Context enterContext(Context context) {
        return Context.enter(context, this);
    }

    @Deprecated
    public final void exit() {
        Context.exit();
    }

    public final ClassLoader getApplicationClassLoader() {
        return this.applicationClassLoader;
    }

    protected Factory getE4xImplementationFactory() {
        return isDom3Present() ? Factory.create("org.mozilla.javascript.xmlimpl.XMLLibImpl") : null;
    }

    protected boolean hasFeature(Context context, int i) {
        boolean z = true;
        int languageVersion;
        switch (i) {
            case NativeRegExp.MATCH /*1*/:
                languageVersion = context.getLanguageVersion();
                return languageVersion == 100 || languageVersion == Token.FUNCTION || languageVersion == Token.FOR;
            case NativeRegExp.PREFIX /*2*/:
            case Token.IFNE /*7*/:
            case Token.SETNAME /*8*/:
            case Token.BITOR /*9*/:
            case Token.BITXOR /*10*/:
            case Token.BITAND /*11*/:
            case Token.EQ /*12*/:
            case Token.NE /*13*/:
                return false;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return true;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                if (context.getLanguageVersion() != Token.FOR) {
                    z = false;
                }
                return z;
            case Token.GOTO /*5*/:
                return true;
            case Token.IFEQ /*6*/:
                languageVersion = context.getLanguageVersion();
                return languageVersion == 0 || languageVersion >= Token.WITHEXPR;
            case Token.LT /*14*/:
                return true;
            case Token.LE /*15*/:
                if (context.getLanguageVersion() > Context.VERSION_1_7) {
                    z = false;
                }
                return z;
            case Token.GT /*16*/:
                if (context.getLanguageVersion() < Context.VERSION_ES6) {
                    z = false;
                }
                return z;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
    }

    public final void initApplicationClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException("loader is null");
        } else if (!Kit.testIfCanLoadRhinoClasses(classLoader)) {
            throw new IllegalArgumentException("Loader can not resolve Rhino classes");
        } else if (this.applicationClassLoader != null) {
            throw new IllegalStateException("applicationClassLoader can only be set once");
        } else {
            checkNotSealed();
            this.applicationClassLoader = classLoader;
        }
    }

    public final boolean isSealed() {
        return this.sealed;
    }

    protected Context makeContext() {
        return new Context(this);
    }

    protected void observeInstructionCount(Context context, int i) {
    }

    protected void onContextCreated(Context context) {
        Object obj = this.listeners;
        int i = 0;
        while (true) {
            Listener listener = (Listener) Kit.getListener(obj, i);
            if (listener != null) {
                listener.contextCreated(context);
                i++;
            } else {
                return;
            }
        }
    }

    protected void onContextReleased(Context context) {
        Object obj = this.listeners;
        int i = 0;
        while (true) {
            Listener listener = (Listener) Kit.getListener(obj, i);
            if (listener != null) {
                listener.contextReleased(context);
                i++;
            } else {
                return;
            }
        }
    }

    public final void removeListener(Listener listener) {
        checkNotSealed();
        synchronized (this.listenersLock) {
            if (this.disabledListening) {
                throw new IllegalStateException();
            }
            this.listeners = Kit.removeListener(this.listeners, listener);
        }
    }

    public final void seal() {
        checkNotSealed();
        this.sealed = true;
    }
}
