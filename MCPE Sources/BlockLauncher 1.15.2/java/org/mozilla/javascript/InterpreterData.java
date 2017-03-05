package org.mozilla.javascript;

import java.io.Serializable;
import org.mozilla.javascript.debug.DebuggableScript;

final class InterpreterData implements Serializable, DebuggableScript {
    static final int INITIAL_MAX_ICODE_LENGTH = 1024;
    static final int INITIAL_NUMBERTABLE_SIZE = 64;
    static final int INITIAL_STRINGTABLE_SIZE = 64;
    static final long serialVersionUID = 5067677351589230234L;
    int argCount;
    boolean[] argIsConst;
    String[] argNames;
    String encodedSource;
    int encodedSourceEnd;
    int encodedSourceStart;
    boolean evalScriptFlag;
    int firstLinePC = -1;
    boolean isStrict;
    double[] itsDoubleTable;
    int[] itsExceptionTable;
    int itsFunctionType;
    byte[] itsICode;
    int itsMaxCalleeArgs;
    int itsMaxFrameArray;
    int itsMaxLocals;
    int itsMaxStack;
    int itsMaxVars;
    String itsName;
    boolean itsNeedsActivation;
    InterpreterData[] itsNestedFunctions;
    Object[] itsRegExpLiterals;
    String itsSourceFile;
    String[] itsStringTable;
    int languageVersion;
    Object[] literalIds;
    UintMap longJumps;
    InterpreterData parentData;
    boolean topLevel;

    InterpreterData(int i, String str, String str2, boolean z) {
        this.languageVersion = i;
        this.itsSourceFile = str;
        this.encodedSource = str2;
        this.isStrict = z;
        init();
    }

    InterpreterData(InterpreterData interpreterData) {
        this.parentData = interpreterData;
        this.languageVersion = interpreterData.languageVersion;
        this.itsSourceFile = interpreterData.itsSourceFile;
        this.encodedSource = interpreterData.encodedSource;
        this.isStrict = interpreterData.isStrict;
        init();
    }

    private void init() {
        this.itsICode = new byte[INITIAL_MAX_ICODE_LENGTH];
        this.itsStringTable = new String[INITIAL_STRINGTABLE_SIZE];
    }

    public DebuggableScript getFunction(int i) {
        return this.itsNestedFunctions[i];
    }

    public int getFunctionCount() {
        return this.itsNestedFunctions == null ? 0 : this.itsNestedFunctions.length;
    }

    public String getFunctionName() {
        return this.itsName;
    }

    public int[] getLineNumbers() {
        return Interpreter.getLineNumbers(this);
    }

    public int getParamAndVarCount() {
        return this.argNames.length;
    }

    public int getParamCount() {
        return this.argCount;
    }

    public boolean getParamOrVarConst(int i) {
        return this.argIsConst[i];
    }

    public String getParamOrVarName(int i) {
        return this.argNames[i];
    }

    public DebuggableScript getParent() {
        return this.parentData;
    }

    public String getSourceName() {
        return this.itsSourceFile;
    }

    public boolean isFunction() {
        return this.itsFunctionType != 0;
    }

    public boolean isGeneratedScript() {
        return ScriptRuntime.isGeneratedScript(this.itsSourceFile);
    }

    public boolean isTopLevel() {
        return this.topLevel;
    }
}
