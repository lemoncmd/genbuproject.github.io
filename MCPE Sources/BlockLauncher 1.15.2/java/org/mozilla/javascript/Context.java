package org.mozilla.javascript;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.mozilla.javascript.TopLevel.Builtins;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.xml.XMLLib;
import org.mozilla.javascript.xml.XMLLib.Factory;

public class Context {
    public static final int FEATURE_DYNAMIC_SCOPE = 7;
    public static final int FEATURE_E4X = 6;
    public static final int FEATURE_ENHANCED_JAVA_ACCESS = 13;
    public static final int FEATURE_ENUMERATE_IDS_FIRST = 16;
    public static final int FEATURE_LOCATION_INFORMATION_IN_ERROR = 10;
    public static final int FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME = 2;
    public static final int FEATURE_NON_ECMA_GET_YEAR = 1;
    public static final int FEATURE_OLD_UNDEF_NULL_THIS = 15;
    public static final int FEATURE_PARENT_PROTO_PROPERTIES = 5;
    @Deprecated
    public static final int FEATURE_PARENT_PROTO_PROPRTIES = 5;
    public static final int FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER = 3;
    public static final int FEATURE_STRICT_EVAL = 9;
    public static final int FEATURE_STRICT_MODE = 11;
    public static final int FEATURE_STRICT_VARS = 8;
    public static final int FEATURE_TO_STRING_AS_SOURCE = 4;
    public static final int FEATURE_V8_EXTENSIONS = 14;
    public static final int FEATURE_WARNING_AS_ERROR = 12;
    public static final int VERSION_1_0 = 100;
    public static final int VERSION_1_1 = 110;
    public static final int VERSION_1_2 = 120;
    public static final int VERSION_1_3 = 130;
    public static final int VERSION_1_4 = 140;
    public static final int VERSION_1_5 = 150;
    public static final int VERSION_1_6 = 160;
    public static final int VERSION_1_7 = 170;
    public static final int VERSION_1_8 = 180;
    public static final int VERSION_DEFAULT = 0;
    public static final int VERSION_ES6 = 200;
    public static final int VERSION_UNKNOWN = -1;
    private static Class<?> codegenClass = Kit.classOrNull("org.mozilla.javascript.optimizer.Codegen");
    public static final Object[] emptyArgs = ScriptRuntime.emptyArgs;
    public static final String errorReporterProperty = "error reporter";
    private static String implementationVersion = null;
    private static Class<?> interpreterClass = Kit.classOrNull("org.mozilla.javascript.Interpreter");
    public static final String languageVersionProperty = "language version";
    Set<String> activationNames;
    private ClassLoader applicationClassLoader;
    XMLLib cachedXMLLib;
    private ClassShutter classShutter;
    NativeCall currentActivationCall;
    Debugger debugger;
    private Object debuggerData;
    private int enterCount;
    private ErrorReporter errorReporter;
    private final ContextFactory factory;
    public boolean generateObserverCount;
    private boolean generatingDebug;
    private boolean generatingDebugChanged;
    private boolean generatingSource;
    private boolean hasClassShutter;
    int instructionCount;
    int instructionThreshold;
    Object interpreterSecurityDomain;
    boolean isContinuationsTopCall;
    boolean isTopLevelStrict;
    ObjToIntMap iterating;
    Object lastInterpreterFrame;
    private Locale locale;
    private int maximumInterpreterStackDepth;
    private int optimizationLevel;
    ObjArray previousInterpreterInvocations;
    private Object propertyListeners;
    RegExpProxy regExpProxy;
    int scratchIndex;
    Scriptable scratchScriptable;
    long scratchUint32;
    private Object sealKey;
    private boolean sealed;
    private SecurityController securityController;
    private Map<Object, Object> threadLocalMap;
    Scriptable topCallScope;
    BaseFunction typeErrorThrower;
    boolean useDynamicScope;
    int version;
    private WrapFactory wrapFactory;

    public interface ClassShutterSetter {
        ClassShutter getClassShutter();

        void setClassShutter(ClassShutter classShutter);
    }

    @Deprecated
    public Context() {
        this(ContextFactory.getGlobal());
    }

    protected Context(ContextFactory contextFactory) {
        int i = VERSION_DEFAULT;
        this.generatingSource = true;
        this.generateObserverCount = false;
        if (contextFactory == null) {
            throw new IllegalArgumentException("factory == null");
        }
        this.factory = contextFactory;
        this.version = VERSION_DEFAULT;
        if (codegenClass == null) {
            i = VERSION_UNKNOWN;
        }
        this.optimizationLevel = i;
        this.maximumInterpreterStackDepth = Integer.MAX_VALUE;
    }

    @Deprecated
    public static void addContextListener(ContextListener contextListener) {
        if ("org.mozilla.javascript.tools.debugger.Main".equals(contextListener.getClass().getName())) {
            Class cls = contextListener.getClass();
            Class[] clsArr = new Class[FEATURE_NON_ECMA_GET_YEAR];
            clsArr[VERSION_DEFAULT] = Kit.classOrNull("org.mozilla.javascript.ContextFactory");
            Object[] objArr = new Object[FEATURE_NON_ECMA_GET_YEAR];
            objArr[VERSION_DEFAULT] = ContextFactory.getGlobal();
            try {
                cls.getMethod("attachTo", clsArr).invoke(contextListener, objArr);
                return;
            } catch (Throwable e) {
                RuntimeException runtimeException = new RuntimeException();
                Kit.initCause(runtimeException, e);
                throw runtimeException;
            }
        }
        ContextFactory.getGlobal().addListener(contextListener);
    }

    @Deprecated
    public static Object call(ContextAction contextAction) {
        return call(ContextFactory.getGlobal(), contextAction);
    }

    public static Object call(ContextFactory contextFactory, final Callable callable, final Scriptable scriptable, final Scriptable scriptable2, final Object[] objArr) {
        if (contextFactory == null) {
            contextFactory = ContextFactory.getGlobal();
        }
        return call(contextFactory, new ContextAction() {
            public Object run(Context context) {
                return callable.call(context, scriptable, scriptable2, objArr);
            }
        });
    }

    static Object call(ContextFactory contextFactory, ContextAction contextAction) {
        try {
            Object run = contextAction.run(enter(null, contextFactory));
            return run;
        } finally {
            exit();
        }
    }

    public static void checkLanguageVersion(int i) {
        if (!isValidLanguageVersion(i)) {
            throw new IllegalArgumentException("Bad language version: " + i);
        }
    }

    public static void checkOptimizationLevel(int i) {
        if (!isValidOptimizationLevel(i)) {
            throw new IllegalArgumentException("Optimization level outside [-1..9]: " + i);
        }
    }

    private Object compileImpl(Scriptable scriptable, Reader reader, String str, String str2, int i, Object obj, boolean z, Evaluator evaluator, ErrorReporter errorReporter) throws IOException {
        int i2 = VERSION_DEFAULT;
        if (str2 == null) {
            str2 = "unnamed script";
        }
        if (obj == null || getSecurityController() != null) {
            if (((str == null ? FEATURE_NON_ECMA_GET_YEAR : VERSION_DEFAULT) ^ (reader == null ? FEATURE_NON_ECMA_GET_YEAR : VERSION_DEFAULT)) == 0) {
                Kit.codeBug();
            }
            if (scriptable == null) {
                i2 = FEATURE_NON_ECMA_GET_YEAR;
            }
            if ((i2 ^ z) == 0) {
                Kit.codeBug();
            }
            CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
            compilerEnvirons.initFromContext(this);
            if (errorReporter == null) {
                errorReporter = compilerEnvirons.getErrorReporter();
            }
            if (!(this.debugger == null || reader == null)) {
                str = Kit.readReader(reader);
                reader = null;
            }
            Parser parser = new Parser(compilerEnvirons, errorReporter);
            if (z) {
                parser.calledByCompileFunction = true;
            }
            if (isStrictMode()) {
                parser.setDefaultUseStrictDirective(true);
            }
            AstRoot parse = str != null ? parser.parse(str, str2, i) : parser.parse(reader, str2, i);
            if (!z || (parse.getFirstChild() != null && parse.getFirstChild().getType() == VERSION_1_1)) {
                ScriptNode transformTree = new IRFactory(compilerEnvirons, errorReporter).transformTree(parse);
                if (evaluator == null) {
                    evaluator = createCompiler();
                }
                Object compile = evaluator.compile(compilerEnvirons, transformTree, transformTree.getEncodedSource(), z);
                if (this.debugger != null) {
                    if (str == null) {
                        Kit.codeBug();
                    }
                    if (compile instanceof DebuggableScript) {
                        notifyDebugger_r(this, (DebuggableScript) compile, str);
                    } else {
                        throw new RuntimeException("NOT SUPPORTED");
                    }
                }
                return z ? evaluator.createFunctionObject(this, scriptable, compile, obj) : evaluator.createScriptObject(compile, obj);
            } else {
                throw new IllegalArgumentException("compileFunction only accepts source with single JS function: " + str);
            }
        }
        throw new IllegalArgumentException("securityDomain should be null if setSecurityController() was never called");
    }

    private Evaluator createCompiler() {
        Evaluator evaluator = null;
        if (this.optimizationLevel >= 0 && codegenClass != null) {
            evaluator = (Evaluator) Kit.newInstanceOrNull(codegenClass);
        }
        return evaluator == null ? createInterpreter() : evaluator;
    }

    static Evaluator createInterpreter() {
        return (Evaluator) Kit.newInstanceOrNull(interpreterClass);
    }

    public static Context enter() {
        return enter(null);
    }

    @Deprecated
    public static Context enter(Context context) {
        return enter(context, ContextFactory.getGlobal());
    }

    static final Context enter(Context context, ContextFactory contextFactory) {
        Object threadContextHelper = VMBridge.instance.getThreadContextHelper();
        Context context2 = VMBridge.instance.getContext(threadContextHelper);
        if (context2 != null) {
            context = context2;
        } else {
            if (context == null) {
                context = contextFactory.makeContext();
                if (context.enterCount != 0) {
                    throw new IllegalStateException("factory.makeContext() returned Context instance already associated with some thread");
                }
                contextFactory.onContextCreated(context);
                if (contextFactory.isSealed() && !context.isSealed()) {
                    context.seal(null);
                }
            } else if (context.enterCount != 0) {
                throw new IllegalStateException("can not use Context instance already associated with some thread");
            }
            VMBridge.instance.setContext(threadContextHelper, context);
        }
        context.enterCount += FEATURE_NON_ECMA_GET_YEAR;
        return context;
    }

    public static void exit() {
        Object threadContextHelper = VMBridge.instance.getThreadContextHelper();
        Context context = VMBridge.instance.getContext(threadContextHelper);
        if (context == null) {
            throw new IllegalStateException("Calling Context.exit without previous Context.enter");
        }
        if (context.enterCount < FEATURE_NON_ECMA_GET_YEAR) {
            Kit.codeBug();
        }
        int i = context.enterCount + VERSION_UNKNOWN;
        context.enterCount = i;
        if (i == 0) {
            VMBridge.instance.setContext(threadContextHelper, null);
            context.factory.onContextReleased(context);
        }
    }

    private void firePropertyChangeImpl(Object obj, String str, Object obj2, Object obj3) {
        int i = VERSION_DEFAULT;
        while (true) {
            Object listener = Kit.getListener(obj, i);
            if (listener != null) {
                if (listener instanceof PropertyChangeListener) {
                    ((PropertyChangeListener) listener).propertyChange(new PropertyChangeEvent(this, str, obj2, obj3));
                }
                i += FEATURE_NON_ECMA_GET_YEAR;
            } else {
                return;
            }
        }
    }

    static Context getContext() {
        Context currentContext = getCurrentContext();
        if (currentContext != null) {
            return currentContext;
        }
        throw new RuntimeException("No Context associated with current Thread");
    }

    public static Context getCurrentContext() {
        return VMBridge.instance.getContext(VMBridge.instance.getThreadContextHelper());
    }

    public static DebuggableScript getDebuggableView(Script script) {
        return script instanceof NativeFunction ? ((NativeFunction) script).getDebuggableView() : null;
    }

    static String getSourcePositionFromStack(int[] iArr) {
        Context currentContext = getCurrentContext();
        if (currentContext == null) {
            return null;
        }
        if (currentContext.lastInterpreterFrame != null) {
            Evaluator createInterpreter = createInterpreter();
            if (createInterpreter != null) {
                return createInterpreter.getSourcePositionFromStack(currentContext, iArr);
            }
        }
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int length = stackTrace.length;
        for (int i = VERSION_DEFAULT; i < length; i += FEATURE_NON_ECMA_GET_YEAR) {
            StackTraceElement stackTraceElement = stackTrace[i];
            String fileName = stackTraceElement.getFileName();
            if (!(fileName == null || fileName.endsWith(".java"))) {
                int lineNumber = stackTraceElement.getLineNumber();
                if (lineNumber >= 0) {
                    iArr[VERSION_DEFAULT] = lineNumber;
                    return fileName;
                }
            }
        }
        return null;
    }

    public static Object getUndefinedValue() {
        return Undefined.instance;
    }

    public static boolean isValidLanguageVersion(int i) {
        switch (i) {
            case VERSION_DEFAULT /*0*/:
            case VERSION_1_0 /*100*/:
            case VERSION_1_1 /*110*/:
            case VERSION_1_2 /*120*/:
            case VERSION_1_3 /*130*/:
            case VERSION_1_4 /*140*/:
            case VERSION_1_5 /*150*/:
            case VERSION_1_6 /*160*/:
            case VERSION_1_7 /*170*/:
            case VERSION_1_8 /*180*/:
            case VERSION_ES6 /*200*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isValidOptimizationLevel(int i) {
        return VERSION_UNKNOWN <= i && i <= FEATURE_STRICT_EVAL;
    }

    public static Object javaToJS(Object obj, Scriptable scriptable) {
        if ((obj instanceof String) || (obj instanceof Number) || (obj instanceof Boolean) || (obj instanceof Scriptable)) {
            return obj;
        }
        if (obj instanceof Character) {
            return String.valueOf(((Character) obj).charValue());
        }
        Context context = getContext();
        return context.getWrapFactory().wrap(context, scriptable, obj, null);
    }

    public static Object jsToJava(Object obj, Class<?> cls) throws EvaluatorException {
        return NativeJavaObject.coerceTypeImpl(cls, obj);
    }

    private static void notifyDebugger_r(Context context, DebuggableScript debuggableScript, String str) {
        context.debugger.handleCompilationDone(context, debuggableScript, str);
        for (int i = VERSION_DEFAULT; i != debuggableScript.getFunctionCount(); i += FEATURE_NON_ECMA_GET_YEAR) {
            notifyDebugger_r(context, debuggableScript.getFunction(i), str);
        }
    }

    static void onSealedMutation() {
        throw new IllegalStateException();
    }

    @Deprecated
    public static void removeContextListener(ContextListener contextListener) {
        ContextFactory.getGlobal().addListener(contextListener);
    }

    public static void reportError(String str) {
        int[] iArr = new int[FEATURE_NON_ECMA_GET_YEAR];
        iArr[VERSION_DEFAULT] = VERSION_DEFAULT;
        reportError(str, getSourcePositionFromStack(iArr), iArr[VERSION_DEFAULT], null, VERSION_DEFAULT);
    }

    public static void reportError(String str, String str2, int i, String str3, int i2) {
        Context currentContext = getCurrentContext();
        if (currentContext != null) {
            currentContext.getErrorReporter().error(str, str2, i, str3, i2);
            return;
        }
        throw new EvaluatorException(str, str2, i, str3, i2);
    }

    public static EvaluatorException reportRuntimeError(String str) {
        int[] iArr = new int[FEATURE_NON_ECMA_GET_YEAR];
        iArr[VERSION_DEFAULT] = VERSION_DEFAULT;
        return reportRuntimeError(str, getSourcePositionFromStack(iArr), iArr[VERSION_DEFAULT], null, VERSION_DEFAULT);
    }

    public static EvaluatorException reportRuntimeError(String str, String str2, int i, String str3, int i2) {
        Context currentContext = getCurrentContext();
        if (currentContext != null) {
            return currentContext.getErrorReporter().runtimeError(str, str2, i, str3, i2);
        }
        throw new EvaluatorException(str, str2, i, str3, i2);
    }

    static EvaluatorException reportRuntimeError0(String str) {
        return reportRuntimeError(ScriptRuntime.getMessage0(str));
    }

    static EvaluatorException reportRuntimeError1(String str, Object obj) {
        return reportRuntimeError(ScriptRuntime.getMessage1(str, obj));
    }

    static EvaluatorException reportRuntimeError2(String str, Object obj, Object obj2) {
        return reportRuntimeError(ScriptRuntime.getMessage2(str, obj, obj2));
    }

    static EvaluatorException reportRuntimeError3(String str, Object obj, Object obj2, Object obj3) {
        return reportRuntimeError(ScriptRuntime.getMessage3(str, obj, obj2, obj3));
    }

    static EvaluatorException reportRuntimeError4(String str, Object obj, Object obj2, Object obj3, Object obj4) {
        return reportRuntimeError(ScriptRuntime.getMessage4(str, obj, obj2, obj3, obj4));
    }

    public static void reportWarning(String str) {
        int[] iArr = new int[FEATURE_NON_ECMA_GET_YEAR];
        iArr[VERSION_DEFAULT] = VERSION_DEFAULT;
        reportWarning(str, getSourcePositionFromStack(iArr), iArr[VERSION_DEFAULT], null, VERSION_DEFAULT);
    }

    public static void reportWarning(String str, String str2, int i, String str3, int i2) {
        Context context = getContext();
        if (context.hasFeature(FEATURE_WARNING_AS_ERROR)) {
            reportError(str, str2, i, str3, i2);
        } else {
            context.getErrorReporter().warning(str, str2, i, str3, i2);
        }
    }

    public static void reportWarning(String str, Throwable th) {
        int[] iArr = new int[FEATURE_NON_ECMA_GET_YEAR];
        iArr[VERSION_DEFAULT] = VERSION_DEFAULT;
        String sourcePositionFromStack = getSourcePositionFromStack(iArr);
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(str);
        th.printStackTrace(printWriter);
        printWriter.flush();
        reportWarning(stringWriter.toString(), sourcePositionFromStack, iArr[VERSION_DEFAULT], null, VERSION_DEFAULT);
    }

    @Deprecated
    public static void setCachingEnabled(boolean z) {
    }

    public static RuntimeException throwAsScriptRuntimeEx(Throwable th) {
        Throwable th2 = th;
        while (th2 instanceof InvocationTargetException) {
            th2 = ((InvocationTargetException) th2).getTargetException();
        }
        if (th2 instanceof Error) {
            Context context = getContext();
            if (context == null || !context.hasFeature(FEATURE_ENHANCED_JAVA_ACCESS)) {
                throw ((Error) th2);
            }
        }
        if (th2 instanceof RhinoException) {
            throw ((RhinoException) th2);
        }
        throw new WrappedException(th2);
    }

    public static boolean toBoolean(Object obj) {
        return ScriptRuntime.toBoolean(obj);
    }

    public static double toNumber(Object obj) {
        return ScriptRuntime.toNumber(obj);
    }

    public static Scriptable toObject(Object obj, Scriptable scriptable) {
        return ScriptRuntime.toObject(scriptable, obj);
    }

    @Deprecated
    public static Scriptable toObject(Object obj, Scriptable scriptable, Class<?> cls) {
        return ScriptRuntime.toObject(scriptable, obj);
    }

    public static String toString(Object obj) {
        return ScriptRuntime.toString(obj);
    }

    @Deprecated
    public static Object toType(Object obj, Class<?> cls) throws IllegalArgumentException {
        try {
            return jsToJava(obj, cls);
        } catch (Throwable e) {
            RuntimeException illegalArgumentException = new IllegalArgumentException(e.getMessage());
            Kit.initCause(illegalArgumentException, e);
            throw illegalArgumentException;
        }
    }

    public void addActivationName(String str) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (this.activationNames == null) {
            this.activationNames = new HashSet();
        }
        this.activationNames.add(str);
    }

    public final void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.propertyListeners = Kit.addListener(this.propertyListeners, propertyChangeListener);
    }

    public Object callFunctionWithContinuations(Callable callable, Scriptable scriptable, Object[] objArr) throws ContinuationPending {
        if (!(callable instanceof InterpretedFunction)) {
            throw new IllegalArgumentException("Function argument was not created by interpreted mode ");
        } else if (ScriptRuntime.hasTopCall(this)) {
            throw new IllegalStateException("Cannot have any pending top calls when executing a script with continuations");
        } else {
            this.isContinuationsTopCall = true;
            return ScriptRuntime.doTopCall(callable, this, scriptable, scriptable, objArr, this.isTopLevelStrict);
        }
    }

    public ContinuationPending captureContinuation() {
        return new ContinuationPending(Interpreter.captureContinuation(this));
    }

    public final Function compileFunction(Scriptable scriptable, String str, String str2, int i, Object obj) {
        return compileFunction(scriptable, str, null, null, str2, i, obj);
    }

    final Function compileFunction(Scriptable scriptable, String str, Evaluator evaluator, ErrorReporter errorReporter, String str2, int i, Object obj) {
        try {
            return (Function) compileImpl(scriptable, null, str, str2, i, obj, true, evaluator, errorReporter);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public final Script compileReader(Reader reader, String str, int i, Object obj) throws IOException {
        return (Script) compileImpl(null, reader, null, str, i < 0 ? VERSION_DEFAULT : i, obj, false, null, null);
    }

    @Deprecated
    public final Script compileReader(Scriptable scriptable, Reader reader, String str, int i, Object obj) throws IOException {
        return compileReader(reader, str, i, obj);
    }

    public final Script compileString(String str, String str2, int i, Object obj) {
        return compileString(str, null, null, str2, i < 0 ? VERSION_DEFAULT : i, obj);
    }

    final Script compileString(String str, Evaluator evaluator, ErrorReporter errorReporter, String str2, int i, Object obj) {
        try {
            return (Script) compileImpl(null, null, str, str2, i, obj, false, evaluator, errorReporter);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public GeneratedClassLoader createClassLoader(ClassLoader classLoader) {
        return getFactory().createClassLoader(classLoader);
    }

    public final String decompileFunction(Function function, int i) {
        return function instanceof BaseFunction ? ((BaseFunction) function).decompile(i, VERSION_DEFAULT) : "function " + function.getClassName() + "() {\n\t[native code]\n}\n";
    }

    public final String decompileFunctionBody(Function function, int i) {
        return function instanceof BaseFunction ? ((BaseFunction) function).decompile(i, FEATURE_NON_ECMA_GET_YEAR) : "[native code]\n";
    }

    public final String decompileScript(Script script, int i) {
        return ((NativeFunction) script).decompile(i, VERSION_DEFAULT);
    }

    public final Object evaluateReader(Scriptable scriptable, Reader reader, String str, int i, Object obj) throws IOException {
        Script compileReader = compileReader(scriptable, reader, str, i, obj);
        return compileReader != null ? compileReader.exec(this, scriptable) : null;
    }

    public final Object evaluateString(Scriptable scriptable, String str, String str2, int i, Object obj) {
        Script compileString = compileString(str, str2, i, obj);
        return compileString != null ? compileString.exec(this, scriptable) : null;
    }

    public Object executeScriptWithContinuations(Script script, Scriptable scriptable) throws ContinuationPending {
        if ((script instanceof InterpretedFunction) && ((InterpretedFunction) script).isScript()) {
            return callFunctionWithContinuations((InterpretedFunction) script, scriptable, ScriptRuntime.emptyArgs);
        }
        throw new IllegalArgumentException("Script argument was not a script or was not created by interpreted mode ");
    }

    final void firePropertyChange(String str, Object obj, Object obj2) {
        Object obj3 = this.propertyListeners;
        if (obj3 != null) {
            firePropertyChangeImpl(obj3, str, obj, obj2);
        }
    }

    public final ClassLoader getApplicationClassLoader() {
        if (this.applicationClassLoader == null) {
            ContextFactory factory = getFactory();
            ClassLoader applicationClassLoader = factory.getApplicationClassLoader();
            if (applicationClassLoader == null) {
                applicationClassLoader = VMBridge.instance.getCurrentThreadClassLoader();
                if (applicationClassLoader != null && Kit.testIfCanLoadRhinoClasses(applicationClassLoader)) {
                    return applicationClassLoader;
                }
                Class cls = factory.getClass();
                applicationClassLoader = cls != ScriptRuntime.ContextFactoryClass ? cls.getClassLoader() : getClass().getClassLoader();
            }
            this.applicationClassLoader = applicationClassLoader;
        }
        return this.applicationClassLoader;
    }

    final synchronized ClassShutter getClassShutter() {
        return this.classShutter;
    }

    public final synchronized ClassShutterSetter getClassShutterSetter() {
        ClassShutterSetter classShutterSetter;
        if (this.hasClassShutter) {
            classShutterSetter = null;
        } else {
            this.hasClassShutter = true;
            classShutterSetter = new ClassShutterSetter() {
                public ClassShutter getClassShutter() {
                    return Context.this.classShutter;
                }

                public void setClassShutter(ClassShutter classShutter) {
                    Context.this.classShutter = classShutter;
                }
            };
        }
        return classShutterSetter;
    }

    public final Debugger getDebugger() {
        return this.debugger;
    }

    public final Object getDebuggerContextData() {
        return this.debuggerData;
    }

    public Factory getE4xImplementationFactory() {
        return getFactory().getE4xImplementationFactory();
    }

    public final Object[] getElements(Scriptable scriptable) {
        return ScriptRuntime.getArrayElements(scriptable);
    }

    public final ErrorReporter getErrorReporter() {
        return this.errorReporter == null ? DefaultErrorReporter.instance : this.errorReporter;
    }

    public final ContextFactory getFactory() {
        return this.factory;
    }

    public final String getImplementationVersion() {
        InputStream openStream;
        InputStream inputStream;
        Throwable th;
        if (implementationVersion == null) {
            try {
                Enumeration resources = Context.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
                while (resources.hasMoreElements()) {
                    try {
                        openStream = ((URL) resources.nextElement()).openStream();
                        try {
                            Attributes mainAttributes = new Manifest(openStream).getMainAttributes();
                            if ("Mozilla Rhino".equals(mainAttributes.getValue("Implementation-Title"))) {
                                implementationVersion = "Rhino " + mainAttributes.getValue("Implementation-Version") + " " + mainAttributes.getValue("Built-Date").replaceAll("-", " ");
                                String str = implementationVersion;
                                if (openStream == null) {
                                    return str;
                                }
                                try {
                                    openStream.close();
                                    return str;
                                } catch (IOException e) {
                                    return str;
                                }
                            } else if (openStream != null) {
                                try {
                                    openStream.close();
                                } catch (IOException e2) {
                                }
                            }
                        } catch (IOException e3) {
                            inputStream = openStream;
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e4) {
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (IOException e5) {
                        inputStream = null;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        openStream = null;
                    }
                }
            } catch (IOException e6) {
                return null;
            }
        }
        return implementationVersion;
        throw th;
        if (openStream != null) {
            try {
                openStream.close();
            } catch (IOException e7) {
            }
        }
        throw th;
    }

    public final int getInstructionObserverThreshold() {
        return this.instructionThreshold;
    }

    public final int getLanguageVersion() {
        return this.version;
    }

    public final Locale getLocale() {
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }
        return this.locale;
    }

    public final int getMaximumInterpreterStackDepth() {
        return this.maximumInterpreterStackDepth;
    }

    public final int getOptimizationLevel() {
        return this.optimizationLevel;
    }

    RegExpProxy getRegExpProxy() {
        if (this.regExpProxy == null) {
            Class classOrNull = Kit.classOrNull("org.mozilla.javascript.regexp.RegExpImpl");
            if (classOrNull != null) {
                this.regExpProxy = (RegExpProxy) Kit.newInstanceOrNull(classOrNull);
            }
        }
        return this.regExpProxy;
    }

    SecurityController getSecurityController() {
        SecurityController global = SecurityController.global();
        return global != null ? global : this.securityController;
    }

    public final Object getThreadLocal(Object obj) {
        return this.threadLocalMap == null ? null : this.threadLocalMap.get(obj);
    }

    public final WrapFactory getWrapFactory() {
        if (this.wrapFactory == null) {
            this.wrapFactory = new WrapFactory();
        }
        return this.wrapFactory;
    }

    public boolean hasFeature(int i) {
        return getFactory().hasFeature(this, i);
    }

    public final Scriptable initSafeStandardObjects(ScriptableObject scriptableObject) {
        return initSafeStandardObjects(scriptableObject, false);
    }

    public final ScriptableObject initSafeStandardObjects() {
        return initSafeStandardObjects(null, false);
    }

    public ScriptableObject initSafeStandardObjects(ScriptableObject scriptableObject, boolean z) {
        return ScriptRuntime.initSafeStandardObjects(this, scriptableObject, z);
    }

    public final Scriptable initStandardObjects(ScriptableObject scriptableObject) {
        return initStandardObjects(scriptableObject, false);
    }

    public final ScriptableObject initStandardObjects() {
        return initStandardObjects(null, false);
    }

    public ScriptableObject initStandardObjects(ScriptableObject scriptableObject, boolean z) {
        return ScriptRuntime.initStandardObjects(this, scriptableObject, z);
    }

    public final boolean isActivationNeeded(String str) {
        return this.activationNames != null && this.activationNames.contains(str);
    }

    public final boolean isGeneratingDebug() {
        return this.generatingDebug;
    }

    public final boolean isGeneratingDebugChanged() {
        return this.generatingDebugChanged;
    }

    public final boolean isGeneratingSource() {
        return this.generatingSource;
    }

    public final boolean isSealed() {
        return this.sealed;
    }

    public final boolean isStrictMode() {
        return this.isTopLevelStrict || (this.currentActivationCall != null && this.currentActivationCall.isStrict);
    }

    final boolean isVersionECMA1() {
        return this.version == 0 || this.version >= VERSION_1_3;
    }

    public Scriptable newArray(Scriptable scriptable, int i) {
        Scriptable nativeArray = new NativeArray((long) i);
        ScriptRuntime.setBuiltinProtoAndParent(nativeArray, scriptable, Builtins.Array);
        return nativeArray;
    }

    public Scriptable newArray(Scriptable scriptable, Object[] objArr) {
        if (objArr.getClass().getComponentType() != ScriptRuntime.ObjectClass) {
            throw new IllegalArgumentException();
        }
        Scriptable nativeArray = new NativeArray(objArr);
        ScriptRuntime.setBuiltinProtoAndParent(nativeArray, scriptable, Builtins.Array);
        return nativeArray;
    }

    public Scriptable newObject(Scriptable scriptable) {
        Scriptable nativeObject = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(nativeObject, scriptable, Builtins.Object);
        return nativeObject;
    }

    public Scriptable newObject(Scriptable scriptable, String str) {
        return newObject(scriptable, str, ScriptRuntime.emptyArgs);
    }

    public Scriptable newObject(Scriptable scriptable, String str, Object[] objArr) {
        return ScriptRuntime.newObject(this, scriptable, str, objArr);
    }

    protected void observeInstructionCount(int i) {
        getFactory().observeInstructionCount(this, i);
    }

    public final synchronized void putThreadLocal(Object obj, Object obj2) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (this.threadLocalMap == null) {
            this.threadLocalMap = new HashMap();
        }
        this.threadLocalMap.put(obj, obj2);
    }

    public void removeActivationName(String str) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (this.activationNames != null) {
            this.activationNames.remove(str);
        }
    }

    public final void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.propertyListeners = Kit.removeListener(this.propertyListeners, propertyChangeListener);
    }

    public final void removeThreadLocal(Object obj) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (this.threadLocalMap != null) {
            this.threadLocalMap.remove(obj);
        }
    }

    public Object resumeContinuation(Object obj, Scriptable scriptable, Object obj2) throws ContinuationPending {
        Object[] objArr = new Object[FEATURE_NON_ECMA_GET_YEAR];
        objArr[VERSION_DEFAULT] = obj2;
        return Interpreter.restartContinuation((NativeContinuation) obj, this, scriptable, objArr);
    }

    public final void seal(Object obj) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.sealed = true;
        this.sealKey = obj;
    }

    public final void setApplicationClassLoader(ClassLoader classLoader) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (classLoader == null) {
            this.applicationClassLoader = null;
        } else if (Kit.testIfCanLoadRhinoClasses(classLoader)) {
            this.applicationClassLoader = classLoader;
        } else {
            throw new IllegalArgumentException("Loader can not resolve Rhino classes");
        }
    }

    public final synchronized void setClassShutter(ClassShutter classShutter) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (classShutter == null) {
            throw new IllegalArgumentException();
        } else if (this.hasClassShutter) {
            throw new SecurityException("Cannot overwrite existing ClassShutter object");
        } else {
            this.classShutter = classShutter;
            this.hasClassShutter = true;
        }
    }

    public final void setDebugger(Debugger debugger, Object obj) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.debugger = debugger;
        this.debuggerData = obj;
    }

    public final ErrorReporter setErrorReporter(ErrorReporter errorReporter) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (errorReporter == null) {
            throw new IllegalArgumentException();
        }
        ErrorReporter errorReporter2 = getErrorReporter();
        if (errorReporter != errorReporter2) {
            Object obj = this.propertyListeners;
            if (obj != null) {
                firePropertyChangeImpl(obj, errorReporterProperty, errorReporter2, errorReporter);
            }
            this.errorReporter = errorReporter;
        }
        return errorReporter2;
    }

    public void setGenerateObserverCount(boolean z) {
        this.generateObserverCount = z;
    }

    public final void setGeneratingDebug(boolean z) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.generatingDebugChanged = true;
        if (z && getOptimizationLevel() > 0) {
            setOptimizationLevel(VERSION_DEFAULT);
        }
        this.generatingDebug = z;
    }

    public final void setGeneratingSource(boolean z) {
        if (this.sealed) {
            onSealedMutation();
        }
        this.generatingSource = z;
    }

    public final void setInstructionObserverThreshold(int i) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        this.instructionThreshold = i;
        setGenerateObserverCount(i > 0);
    }

    public void setLanguageVersion(int i) {
        if (this.sealed) {
            onSealedMutation();
        }
        checkLanguageVersion(i);
        Object obj = this.propertyListeners;
        if (!(obj == null || i == this.version)) {
            firePropertyChangeImpl(obj, languageVersionProperty, Integer.valueOf(this.version), Integer.valueOf(i));
        }
        this.version = i;
    }

    public final Locale setLocale(Locale locale) {
        if (this.sealed) {
            onSealedMutation();
        }
        Locale locale2 = this.locale;
        this.locale = locale;
        return locale2;
    }

    public final void setMaximumInterpreterStackDepth(int i) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (this.optimizationLevel != VERSION_UNKNOWN) {
            throw new IllegalStateException("Cannot set maximumInterpreterStackDepth when optimizationLevel != -1");
        } else if (i < FEATURE_NON_ECMA_GET_YEAR) {
            throw new IllegalArgumentException("Cannot set maximumInterpreterStackDepth to less than 1");
        } else {
            this.maximumInterpreterStackDepth = i;
        }
    }

    public final void setOptimizationLevel(int i) {
        int i2 = VERSION_UNKNOWN;
        if (this.sealed) {
            onSealedMutation();
        }
        if (i == -2) {
            i = VERSION_UNKNOWN;
        }
        checkOptimizationLevel(i);
        if (codegenClass != null) {
            i2 = i;
        }
        this.optimizationLevel = i2;
    }

    public final void setSecurityController(SecurityController securityController) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (securityController == null) {
            throw new IllegalArgumentException();
        } else if (this.securityController != null) {
            throw new SecurityException("Can not overwrite existing SecurityController object");
        } else if (SecurityController.hasGlobal()) {
            throw new SecurityException("Can not overwrite existing global SecurityController object");
        } else {
            this.securityController = securityController;
        }
    }

    public final void setWrapFactory(WrapFactory wrapFactory) {
        if (this.sealed) {
            onSealedMutation();
        }
        if (wrapFactory == null) {
            throw new IllegalArgumentException();
        }
        this.wrapFactory = wrapFactory;
    }

    public final boolean stringIsCompilableUnit(String str) {
        boolean z;
        CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.initFromContext(this);
        compilerEnvirons.setGeneratingSource(false);
        Parser parser = new Parser(compilerEnvirons, DefaultErrorReporter.instance);
        try {
            parser.parse(str, null, (int) FEATURE_NON_ECMA_GET_YEAR);
            z = false;
        } catch (EvaluatorException e) {
            z = FEATURE_NON_ECMA_GET_YEAR;
        }
        return (z && parser.eof()) ? false : true;
    }

    public final void unseal(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        } else if (this.sealKey != obj) {
            throw new IllegalArgumentException();
        } else if (this.sealed) {
            this.sealed = false;
            this.sealKey = null;
        } else {
            throw new IllegalStateException();
        }
    }
}
