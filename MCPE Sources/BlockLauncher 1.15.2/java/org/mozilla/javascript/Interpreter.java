package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebugFrame;

public final class Interpreter extends Icode implements Evaluator {
    static final int EXCEPTION_HANDLER_SLOT = 2;
    static final int EXCEPTION_LOCAL_SLOT = 4;
    static final int EXCEPTION_SCOPE_SLOT = 5;
    static final int EXCEPTION_SLOT_SIZE = 6;
    static final int EXCEPTION_TRY_END_SLOT = 1;
    static final int EXCEPTION_TRY_START_SLOT = 0;
    static final int EXCEPTION_TYPE_SLOT = 3;
    InterpreterData itsData;

    private static class CallFrame implements Serializable, Cloneable {
        static final long serialVersionUID = -2843792508994958978L;
        DebugFrame debuggerFrame;
        int emptyStackTop;
        InterpretedFunction fnOrScript;
        int frameIndex;
        boolean frozen;
        InterpreterData idata;
        boolean isContinuationsTopFrame;
        int localShift;
        CallFrame parentFrame;
        int pc;
        int pcPrevBranch;
        int pcSourceLineStart;
        Object result;
        double resultDbl;
        double[] sDbl;
        int savedCallOp;
        int savedStackTop;
        Scriptable scope;
        Object[] stack;
        int[] stackAttributes;
        Scriptable thisObj;
        Object throwable;
        boolean useActivation;
        CallFrame varSource;

        private CallFrame() {
        }

        CallFrame cloneFrozen() {
            if (!this.frozen) {
                Kit.codeBug();
            }
            try {
                CallFrame callFrame = (CallFrame) clone();
                callFrame.stack = (Object[]) this.stack.clone();
                callFrame.stackAttributes = (int[]) this.stackAttributes.clone();
                callFrame.sDbl = (double[]) this.sDbl.clone();
                callFrame.frozen = false;
                return callFrame;
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException();
            }
        }
    }

    private static final class ContinuationJump implements Serializable {
        static final long serialVersionUID = 7687739156004308247L;
        CallFrame branchFrame;
        CallFrame capturedFrame;
        Object result;
        double resultDbl;

        ContinuationJump(NativeContinuation nativeContinuation, CallFrame callFrame) {
            this.capturedFrame = (CallFrame) nativeContinuation.getImplementation();
            if (this.capturedFrame == null || callFrame == null) {
                this.branchFrame = null;
                return;
            }
            CallFrame callFrame2 = this.capturedFrame;
            int i = callFrame2.frameIndex - callFrame.frameIndex;
            if (i != 0) {
                if (i < 0) {
                    i = -i;
                    callFrame2 = callFrame;
                    callFrame = this.capturedFrame;
                }
                do {
                    callFrame2 = callFrame2.parentFrame;
                    i--;
                } while (i != 0);
                if (callFrame2.frameIndex != callFrame.frameIndex) {
                    Kit.codeBug();
                }
            }
            while (callFrame2 != callFrame && callFrame2 != null) {
                callFrame2 = callFrame2.parentFrame;
                callFrame = callFrame.parentFrame;
            }
            this.branchFrame = callFrame2;
            if (this.branchFrame != null && !this.branchFrame.frozen) {
                Kit.codeBug();
            }
        }
    }

    static class GeneratorState {
        int operation;
        RuntimeException returnedException;
        Object value;

        GeneratorState(int i, Object obj) {
            this.operation = i;
            this.value = obj;
        }
    }

    private static void addInstructionCount(Context context, CallFrame callFrame, int i) {
        context.instructionCount += (callFrame.pc - callFrame.pcPrevBranch) + i;
        if (context.instructionCount > context.instructionThreshold) {
            context.observeInstructionCount(context.instructionCount);
            context.instructionCount = EXCEPTION_TRY_START_SLOT;
        }
    }

    private static int bytecodeSpan(int i) {
        switch (i) {
            case -63:
            case -62:
            case -54:
            case -46:
            case -39:
            case -27:
            case -26:
            case -23:
            case -6:
            case EXCEPTION_SCOPE_SLOT /*5*/:
            case EXCEPTION_SLOT_SIZE /*6*/:
            case Token.IFNE /*7*/:
            case Token.THROW /*50*/:
            case Token.YIELD /*73*/:
                return EXCEPTION_TYPE_SLOT;
            case -61:
            case -49:
            case -48:
                return EXCEPTION_HANDLER_SLOT;
            case -47:
                return EXCEPTION_SCOPE_SLOT;
            case -45:
                return EXCEPTION_HANDLER_SLOT;
            case -40:
                return EXCEPTION_SCOPE_SLOT;
            case -38:
                return EXCEPTION_HANDLER_SLOT;
            case -28:
                return EXCEPTION_SCOPE_SLOT;
            case -21:
                return EXCEPTION_SCOPE_SLOT;
            case -11:
            case -10:
            case -9:
            case -8:
            case -7:
                return EXCEPTION_HANDLER_SLOT;
            case Token.CATCH_SCOPE /*57*/:
                return EXCEPTION_HANDLER_SLOT;
            default:
                if (Icode.validBytecode(i)) {
                    return EXCEPTION_TRY_END_SLOT;
                }
                throw Kit.codeBug();
        }
    }

    public static NativeContinuation captureContinuation(Context context) {
        if (context.lastInterpreterFrame != null && (context.lastInterpreterFrame instanceof CallFrame)) {
            return captureContinuation(context, (CallFrame) context.lastInterpreterFrame, true);
        }
        throw new IllegalStateException("Interpreter frames not found");
    }

    private static NativeContinuation captureContinuation(Context context, CallFrame callFrame, boolean z) {
        ScriptableObject nativeContinuation = new NativeContinuation();
        ScriptRuntime.setObjectProtoAndParent(nativeContinuation, ScriptRuntime.getTopCallScope(context));
        CallFrame callFrame2 = callFrame;
        CallFrame callFrame3 = callFrame;
        while (callFrame3 != null && !callFrame3.frozen) {
            callFrame3.frozen = true;
            for (int i = callFrame3.savedStackTop + EXCEPTION_TRY_END_SLOT; i != callFrame3.stack.length; i += EXCEPTION_TRY_END_SLOT) {
                callFrame3.stack[i] = null;
                callFrame3.stackAttributes[i] = EXCEPTION_TRY_START_SLOT;
            }
            if (callFrame3.savedCallOp == 38) {
                callFrame3.stack[callFrame3.savedStackTop] = null;
            } else if (callFrame3.savedCallOp != 30) {
                Kit.codeBug();
            }
            CallFrame callFrame4 = callFrame3;
            callFrame3 = callFrame3.parentFrame;
            callFrame2 = callFrame4;
        }
        if (z) {
            while (callFrame2.parentFrame != null) {
                callFrame2 = callFrame2.parentFrame;
            }
            if (!callFrame2.isContinuationsTopFrame) {
                throw new IllegalStateException("Cannot capture continuation from JavaScript code not called directly by executeScriptWithContinuations or callFunctionWithContinuations");
            }
        }
        nativeContinuation.initImplementation(callFrame);
        return nativeContinuation;
    }

    private static CallFrame captureFrameForGenerator(CallFrame callFrame) {
        callFrame.frozen = true;
        CallFrame cloneFrozen = callFrame.cloneFrozen();
        callFrame.frozen = false;
        cloneFrozen.parentFrame = null;
        cloneFrozen.frameIndex = EXCEPTION_TRY_START_SLOT;
        return cloneFrozen;
    }

    private static void doAdd(Object[] objArr, double[] dArr, int i, Context context) {
        double d;
        Object obj = objArr[i + EXCEPTION_TRY_END_SLOT];
        Object obj2 = objArr[i];
        if (obj == UniqueTag.DOUBLE_MARK) {
            d = dArr[i + EXCEPTION_TRY_END_SLOT];
            if (obj2 == UniqueTag.DOUBLE_MARK) {
                dArr[i] = dArr[i] + d;
                return;
            }
            obj = EXCEPTION_TRY_END_SLOT;
        } else if (obj2 == UniqueTag.DOUBLE_MARK) {
            d = dArr[i];
            UniqueTag uniqueTag = obj;
            obj = EXCEPTION_TRY_START_SLOT;
        } else if ((obj2 instanceof Scriptable) || (obj instanceof Scriptable)) {
            objArr[i] = ScriptRuntime.add(obj2, obj, context);
            return;
        } else if ((obj2 instanceof CharSequence) || (obj instanceof CharSequence)) {
            objArr[i] = new ConsString(ScriptRuntime.toCharSequence(obj2), ScriptRuntime.toCharSequence(obj));
            return;
        } else {
            d = obj2 instanceof Number ? ((Number) obj2).doubleValue() : ScriptRuntime.toNumber(obj2);
            double doubleValue = obj instanceof Number ? ((Number) obj).doubleValue() : ScriptRuntime.toNumber(obj);
            objArr[i] = UniqueTag.DOUBLE_MARK;
            dArr[i] = doubleValue + d;
            return;
        }
        if (obj2 instanceof Scriptable) {
            Object wrapNumber = ScriptRuntime.wrapNumber(d);
            if (obj == null) {
                Object obj3 = wrapNumber;
                wrapNumber = obj2;
                obj2 = obj3;
            }
            objArr[i] = ScriptRuntime.add(obj2, wrapNumber, context);
        } else if (obj2 instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj2;
            CharSequence toCharSequence = ScriptRuntime.toCharSequence(Double.valueOf(d));
            if (obj != null) {
                objArr[i] = new ConsString(charSequence, toCharSequence);
            } else {
                objArr[i] = new ConsString(toCharSequence, charSequence);
            }
        } else {
            doubleValue = obj2 instanceof Number ? ((Number) obj2).doubleValue() : ScriptRuntime.toNumber(obj2);
            objArr[i] = UniqueTag.DOUBLE_MARK;
            dArr[i] = doubleValue + d;
        }
    }

    private static int doArithmetic(CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2) {
        double stack_double = stack_double(callFrame, i2);
        int i3 = i2 - 1;
        double stack_double2 = stack_double(callFrame, i3);
        objArr[i3] = UniqueTag.DOUBLE_MARK;
        switch (i) {
            case Token.SUB /*22*/:
                stack_double2 -= stack_double;
                break;
            case Token.MUL /*23*/:
                stack_double2 *= stack_double;
                break;
            case Token.DIV /*24*/:
                stack_double2 /= stack_double;
                break;
            case Token.MOD /*25*/:
                stack_double2 %= stack_double;
                break;
        }
        dArr[i3] = stack_double2;
        return i3;
    }

    private static int doBitOp(CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2) {
        int stack_int32 = stack_int32(callFrame, i2 - 1);
        int stack_int322 = stack_int32(callFrame, i2);
        int i3 = i2 - 1;
        objArr[i3] = UniqueTag.DOUBLE_MARK;
        switch (i) {
            case Token.BITOR /*9*/:
                stack_int32 |= stack_int322;
                break;
            case Token.BITXOR /*10*/:
                stack_int32 ^= stack_int322;
                break;
            case Token.BITAND /*11*/:
                stack_int32 &= stack_int322;
                break;
            case Token.LSH /*18*/:
                stack_int32 <<= stack_int322;
                break;
            case Token.RSH /*19*/:
                stack_int32 >>= stack_int322;
                break;
        }
        dArr[i3] = (double) stack_int32;
        return i3;
    }

    private static int doCallSpecial(Context context, CallFrame callFrame, Object[] objArr, double[] dArr, int i, byte[] bArr, int i2) {
        int i3;
        int i4 = bArr[callFrame.pc] & 255;
        Object obj = bArr[callFrame.pc + EXCEPTION_TRY_END_SLOT] != (byte) 0 ? EXCEPTION_TRY_END_SLOT : null;
        int index = getIndex(bArr, callFrame.pc + EXCEPTION_HANDLER_SLOT);
        if (obj != null) {
            int i5 = i - i2;
            obj = objArr[i5];
            if (obj == UniqueTag.DOUBLE_MARK) {
                obj = ScriptRuntime.wrapNumber(dArr[i5]);
            }
            objArr[i5] = ScriptRuntime.newSpecial(context, obj, getArgsArray(objArr, dArr, i5 + EXCEPTION_TRY_END_SLOT, i2), callFrame.scope, i4);
            i3 = i5;
        } else {
            int i6 = i - (i2 + EXCEPTION_TRY_END_SLOT);
            objArr[i6] = ScriptRuntime.callSpecial(context, (Callable) objArr[i6], (Scriptable) objArr[i6 + EXCEPTION_TRY_END_SLOT], getArgsArray(objArr, dArr, i6 + EXCEPTION_HANDLER_SLOT, i2), callFrame.scope, callFrame.thisObj, i4, callFrame.idata.itsSourceFile, index);
            i3 = i6;
        }
        callFrame.pc += EXCEPTION_LOCAL_SLOT;
        return i3;
    }

    private static int doCompare(CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2) {
        double d;
        double stack_double;
        boolean z = true;
        int i3 = i2 - 1;
        Object obj = objArr[i3 + EXCEPTION_TRY_END_SLOT];
        UniqueTag uniqueTag = objArr[i3];
        if (obj == UniqueTag.DOUBLE_MARK) {
            d = dArr[i3 + EXCEPTION_TRY_END_SLOT];
            stack_double = stack_double(callFrame, i3);
        } else if (uniqueTag == UniqueTag.DOUBLE_MARK) {
            d = ScriptRuntime.toNumber(obj);
            stack_double = dArr[i3];
        } else {
            switch (i) {
                case Token.LT /*14*/:
                    z = ScriptRuntime.cmp_LT(uniqueTag, obj);
                    break;
                case Token.LE /*15*/:
                    z = ScriptRuntime.cmp_LE(uniqueTag, obj);
                    break;
                case Token.GT /*16*/:
                    z = ScriptRuntime.cmp_LT(obj, uniqueTag);
                    break;
                case Token.GE /*17*/:
                    z = ScriptRuntime.cmp_LE(obj, uniqueTag);
                    break;
                default:
                    throw Kit.codeBug();
            }
            objArr[i3] = ScriptRuntime.wrapBoolean(z);
            return i3;
        }
        switch (i) {
            case Token.LT /*14*/:
                if (stack_double >= d) {
                    z = false;
                    break;
                }
                break;
            case Token.LE /*15*/:
                if (stack_double > d) {
                    z = false;
                    break;
                }
                break;
            case Token.GT /*16*/:
                if (stack_double <= d) {
                    z = false;
                    break;
                }
                break;
            case Token.GE /*17*/:
                if (stack_double < d) {
                    z = false;
                    break;
                }
                break;
            default:
                throw Kit.codeBug();
        }
        objArr[i3] = ScriptRuntime.wrapBoolean(z);
        return i3;
    }

    private static int doDelName(Context context, CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2) {
        Object obj = objArr[i2];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i2]);
        }
        int i3 = i2 - 1;
        Object obj2 = objArr[i3];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i3]);
        }
        objArr[i3] = ScriptRuntime.delete(obj2, obj, context, callFrame.scope, i == 0);
        return i3;
    }

    private static int doElemIncDec(Context context, CallFrame callFrame, byte[] bArr, Object[] objArr, double[] dArr, int i) {
        Object obj = objArr[i];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i]);
        }
        int i2 = i - 1;
        Object obj2 = objArr[i2];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i2]);
        }
        objArr[i2] = ScriptRuntime.elemIncrDecr(obj2, obj, context, callFrame.scope, bArr[callFrame.pc]);
        callFrame.pc += EXCEPTION_TRY_END_SLOT;
        return i2;
    }

    private static boolean doEquals(Object[] objArr, double[] dArr, int i) {
        UniqueTag uniqueTag = objArr[i + EXCEPTION_TRY_END_SLOT];
        UniqueTag uniqueTag2 = objArr[i];
        return uniqueTag == UniqueTag.DOUBLE_MARK ? uniqueTag2 == UniqueTag.DOUBLE_MARK ? dArr[i] == dArr[i + EXCEPTION_TRY_END_SLOT] : ScriptRuntime.eqNumber(dArr[i + EXCEPTION_TRY_END_SLOT], uniqueTag2) : uniqueTag2 == UniqueTag.DOUBLE_MARK ? ScriptRuntime.eqNumber(dArr[i], uniqueTag) : ScriptRuntime.eq(uniqueTag2, uniqueTag);
    }

    private static int doGetElem(Context context, CallFrame callFrame, Object[] objArr, double[] dArr, int i) {
        int i2 = i - 1;
        Object obj = objArr[i2];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i2]);
        }
        UniqueTag uniqueTag = objArr[i2 + EXCEPTION_TRY_END_SLOT];
        objArr[i2] = uniqueTag != UniqueTag.DOUBLE_MARK ? ScriptRuntime.getObjectElem(obj, uniqueTag, context, callFrame.scope) : ScriptRuntime.getObjectIndex(obj, dArr[i2 + EXCEPTION_TRY_END_SLOT], context, callFrame.scope);
        return i2;
    }

    private static int doGetVar(CallFrame callFrame, Object[] objArr, double[] dArr, int i, Object[] objArr2, double[] dArr2, int i2) {
        int i3 = i + EXCEPTION_TRY_END_SLOT;
        if (callFrame.useActivation) {
            objArr[i3] = callFrame.scope.get(callFrame.idata.argNames[i2], callFrame.scope);
        } else {
            objArr[i3] = objArr2[i2];
            dArr[i3] = dArr2[i2];
        }
        return i3;
    }

    private static int doInOrInstanceof(Context context, int i, Object[] objArr, double[] dArr, int i2) {
        Object obj = objArr[i2];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i2]);
        }
        int i3 = i2 - 1;
        Object obj2 = objArr[i3];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i3]);
        }
        objArr[i3] = ScriptRuntime.wrapBoolean(i == 52 ? ScriptRuntime.in(obj2, obj, context) : ScriptRuntime.instanceOf(obj2, obj, context));
        return i3;
    }

    private static int doRefMember(Context context, Object[] objArr, double[] dArr, int i, int i2) {
        Object obj = objArr[i];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i]);
        }
        int i3 = i - 1;
        Object obj2 = objArr[i3];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i3]);
        }
        objArr[i3] = ScriptRuntime.memberRef(obj2, obj, context, i2);
        return i3;
    }

    private static int doRefNsMember(Context context, Object[] objArr, double[] dArr, int i, int i2) {
        Object obj = objArr[i];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i]);
        }
        int i3 = i - 1;
        Object obj2 = objArr[i3];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i3]);
        }
        int i4 = i3 - 1;
        Object obj3 = objArr[i4];
        if (obj3 == UniqueTag.DOUBLE_MARK) {
            obj3 = ScriptRuntime.wrapNumber(dArr[i4]);
        }
        objArr[i4] = ScriptRuntime.memberRef(obj3, obj2, obj, context, i2);
        return i4;
    }

    private static int doRefNsName(Context context, CallFrame callFrame, Object[] objArr, double[] dArr, int i, int i2) {
        Object obj = objArr[i];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i]);
        }
        int i3 = i - 1;
        Object obj2 = objArr[i3];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i3]);
        }
        objArr[i3] = ScriptRuntime.nameRef(obj2, obj, context, callFrame.scope, i2);
        return i3;
    }

    private static int doSetConstVar(CallFrame callFrame, Object[] objArr, double[] dArr, int i, Object[] objArr2, double[] dArr2, int[] iArr, int i2) {
        if (callFrame.useActivation) {
            Object wrapNumber;
            UniqueTag uniqueTag = objArr[i];
            if (uniqueTag == UniqueTag.DOUBLE_MARK) {
                wrapNumber = ScriptRuntime.wrapNumber(dArr[i]);
            } else {
                UniqueTag uniqueTag2 = uniqueTag;
            }
            String str = callFrame.idata.argNames[i2];
            if (callFrame.scope instanceof ConstProperties) {
                ((ConstProperties) callFrame.scope).putConst(str, callFrame.scope, wrapNumber);
            } else {
                throw Kit.codeBug();
            }
        } else if ((iArr[i2] & EXCEPTION_TRY_END_SLOT) == 0) {
            throw Context.reportRuntimeError1("msg.var.redecl", callFrame.idata.argNames[i2]);
        } else if ((iArr[i2] & 8) != 0) {
            objArr2[i2] = objArr[i];
            iArr[i2] = iArr[i2] & -9;
            dArr2[i2] = dArr[i];
        }
        return i;
    }

    private static int doSetElem(Context context, CallFrame callFrame, Object[] objArr, double[] dArr, int i) {
        Object objectElem;
        int i2 = i - 2;
        Object obj = objArr[i2 + EXCEPTION_HANDLER_SLOT];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(dArr[i2 + EXCEPTION_HANDLER_SLOT]);
        }
        Object obj2 = objArr[i2];
        if (obj2 == UniqueTag.DOUBLE_MARK) {
            obj2 = ScriptRuntime.wrapNumber(dArr[i2]);
        }
        UniqueTag uniqueTag = objArr[i2 + EXCEPTION_TRY_END_SLOT];
        if (uniqueTag != UniqueTag.DOUBLE_MARK) {
            objectElem = ScriptRuntime.setObjectElem(obj2, uniqueTag, obj, context, callFrame.scope);
        } else {
            objectElem = ScriptRuntime.setObjectIndex(obj2, dArr[i2 + EXCEPTION_TRY_END_SLOT], obj, context, callFrame.scope);
        }
        objArr[i2] = objectElem;
        return i2;
    }

    private static int doSetVar(CallFrame callFrame, Object[] objArr, double[] dArr, int i, Object[] objArr2, double[] dArr2, int[] iArr, int i2) {
        if (callFrame.useActivation) {
            Object obj = objArr[i];
            if (obj == UniqueTag.DOUBLE_MARK) {
                obj = ScriptRuntime.wrapNumber(dArr[i]);
            }
            callFrame.scope.put(callFrame.idata.argNames[i2], callFrame.scope, obj);
        } else if ((iArr[i2] & EXCEPTION_TRY_END_SLOT) == 0) {
            objArr2[i2] = objArr[i];
            dArr2[i2] = dArr[i];
        }
        return i;
    }

    private static boolean doShallowEquals(Object[] objArr, double[] dArr, int i) {
        double d;
        double d2;
        UniqueTag uniqueTag = objArr[i + EXCEPTION_TRY_END_SLOT];
        UniqueTag uniqueTag2 = objArr[i];
        UniqueTag uniqueTag3 = UniqueTag.DOUBLE_MARK;
        if (uniqueTag == uniqueTag3) {
            d = dArr[i + EXCEPTION_TRY_END_SLOT];
            if (uniqueTag2 == uniqueTag3) {
                d2 = dArr[i];
            } else if (!(uniqueTag2 instanceof Number)) {
                return false;
            } else {
                d2 = ((Number) uniqueTag2).doubleValue();
            }
        } else if (uniqueTag2 != uniqueTag3) {
            return ScriptRuntime.shallowEq(uniqueTag2, uniqueTag);
        } else {
            d = dArr[i];
            if (!(uniqueTag instanceof Number)) {
                return false;
            }
            double d3 = d;
            d = ((Number) uniqueTag).doubleValue();
            d2 = d3;
        }
        return d2 == d;
    }

    private static int doVarIncDec(Context context, CallFrame callFrame, Object[] objArr, double[] dArr, int i, Object[] objArr2, double[] dArr2, int[] iArr, int i2) {
        int i3 = i + EXCEPTION_TRY_END_SLOT;
        byte b = callFrame.idata.itsICode[callFrame.pc];
        if (callFrame.useActivation) {
            objArr[i3] = ScriptRuntime.nameIncrDecr(callFrame.scope, callFrame.idata.argNames[i2], context, b);
        } else {
            UniqueTag uniqueTag = objArr2[i2];
            double toNumber = uniqueTag == UniqueTag.DOUBLE_MARK ? dArr2[i2] : ScriptRuntime.toNumber((Object) uniqueTag);
            double d = (b & EXCEPTION_TRY_END_SLOT) == 0 ? 1.0d + toNumber : toNumber - 1.0d;
            Object obj = (b & EXCEPTION_HANDLER_SLOT) != 0 ? EXCEPTION_TRY_END_SLOT : null;
            if ((iArr[i2] & EXCEPTION_TRY_END_SLOT) == 0) {
                if (uniqueTag != UniqueTag.DOUBLE_MARK) {
                    objArr2[i2] = UniqueTag.DOUBLE_MARK;
                }
                dArr2[i2] = d;
                objArr[i3] = UniqueTag.DOUBLE_MARK;
                dArr[i3] = obj != null ? toNumber : d;
            } else if (obj == null || uniqueTag == UniqueTag.DOUBLE_MARK) {
                objArr[i3] = UniqueTag.DOUBLE_MARK;
                if (obj == null) {
                    toNumber = d;
                }
                dArr[i3] = toNumber;
            } else {
                objArr[i3] = uniqueTag;
            }
        }
        callFrame.pc += EXCEPTION_TRY_END_SLOT;
        return i3;
    }

    static void dumpICode(InterpreterData interpreterData) {
    }

    private static void enterFrame(Context context, CallFrame callFrame, Object[] objArr, boolean z) {
        boolean z2 = callFrame.idata.itsNeedsActivation;
        Object obj = callFrame.debuggerFrame != null ? EXCEPTION_TRY_END_SLOT : EXCEPTION_TRY_START_SLOT;
        if (z2 || obj != null) {
            Scriptable scriptable = callFrame.scope;
            if (scriptable == null) {
                Kit.codeBug();
            } else if (z) {
                while (scriptable instanceof NativeWith) {
                    scriptable = scriptable.getParentScope();
                    if (scriptable == null || (callFrame.parentFrame != null && callFrame.parentFrame.scope == scriptable)) {
                        Kit.codeBug();
                        break;
                    }
                }
            }
            if (obj != null) {
                callFrame.debuggerFrame.onEnter(context, scriptable, callFrame.thisObj, objArr);
            }
            if (z2) {
                ScriptRuntime.enterActivationFunction(context, scriptable);
            }
        }
    }

    private static void exitFrame(Context context, CallFrame callFrame, Object obj) {
        if (callFrame.idata.itsNeedsActivation) {
            ScriptRuntime.exitActivationFunction(context);
        }
        if (callFrame.debuggerFrame != null) {
            try {
                if (obj instanceof Throwable) {
                    callFrame.debuggerFrame.onExit(context, true, obj);
                    return;
                }
                ContinuationJump continuationJump = (ContinuationJump) obj;
                Object obj2 = continuationJump == null ? callFrame.result : continuationJump.result;
                if (obj2 == UniqueTag.DOUBLE_MARK) {
                    obj2 = ScriptRuntime.wrapNumber(continuationJump == null ? callFrame.resultDbl : continuationJump.resultDbl);
                }
                callFrame.debuggerFrame.onExit(context, false, obj2);
            } catch (Throwable th) {
                System.err.println("RHINO USAGE WARNING: onExit terminated with exception");
                th.printStackTrace(System.err);
            }
        }
    }

    private static Object freezeGenerator(Context context, CallFrame callFrame, int i, GeneratorState generatorState) {
        if (generatorState.operation == EXCEPTION_HANDLER_SLOT) {
            throw ScriptRuntime.typeError0("msg.yield.closing");
        }
        callFrame.frozen = true;
        callFrame.result = callFrame.stack[i];
        callFrame.resultDbl = callFrame.sDbl[i];
        callFrame.savedStackTop = i;
        callFrame.pc--;
        ScriptRuntime.exitActivationFunction(context);
        return callFrame.result != UniqueTag.DOUBLE_MARK ? callFrame.result : ScriptRuntime.wrapNumber(callFrame.resultDbl);
    }

    private static Object[] getArgsArray(Object[] objArr, double[] dArr, int i, int i2) {
        if (i2 == 0) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] objArr2 = new Object[i2];
        for (int i3 = EXCEPTION_TRY_START_SLOT; i3 != i2; i3 += EXCEPTION_TRY_END_SLOT) {
            Number number = objArr[i];
            if (number == UniqueTag.DOUBLE_MARK) {
                number = ScriptRuntime.wrapNumber(dArr[i]);
            }
            objArr2[i3] = number;
            i += EXCEPTION_TRY_END_SLOT;
        }
        return objArr2;
    }

    static String getEncodedSource(InterpreterData interpreterData) {
        return interpreterData.encodedSource == null ? null : interpreterData.encodedSource.substring(interpreterData.encodedSourceStart, interpreterData.encodedSourceEnd);
    }

    private static int getExceptionHandler(CallFrame callFrame, boolean z) {
        int i = EXCEPTION_TRY_START_SLOT;
        int[] iArr = callFrame.idata.itsExceptionTable;
        if (iArr == null) {
            return -1;
        }
        int i2 = callFrame.pc - 1;
        int i3 = EXCEPTION_TRY_START_SLOT;
        int i4 = -1;
        int i5 = EXCEPTION_TRY_START_SLOT;
        while (i != iArr.length) {
            int i6 = iArr[i + EXCEPTION_TRY_START_SLOT];
            int i7 = iArr[i + EXCEPTION_TRY_END_SLOT];
            if (i6 <= i2 && i2 < i7 && (!z || iArr[i + EXCEPTION_TYPE_SLOT] == EXCEPTION_TRY_END_SLOT)) {
                if (i4 >= 0) {
                    if (i5 >= i7) {
                        if (i3 > i6) {
                            Kit.codeBug();
                        }
                        if (i5 == i7) {
                            Kit.codeBug();
                        }
                    }
                }
                i5 = i7;
                i3 = i6;
                i4 = i;
            }
            i += EXCEPTION_SLOT_SIZE;
        }
        return i4;
    }

    private static int getIndex(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 8) | (bArr[i + EXCEPTION_TRY_END_SLOT] & 255);
    }

    private static int getInt(byte[] bArr, int i) {
        return (((bArr[i] << 24) | ((bArr[i + EXCEPTION_TRY_END_SLOT] & 255) << 16)) | ((bArr[i + EXCEPTION_HANDLER_SLOT] & 255) << 8)) | (bArr[i + EXCEPTION_TYPE_SLOT] & 255);
    }

    static int[] getLineNumbers(InterpreterData interpreterData) {
        UintMap uintMap = new UintMap();
        byte[] bArr = interpreterData.itsICode;
        int length = bArr.length;
        int i = EXCEPTION_TRY_START_SLOT;
        while (i != length) {
            byte b = bArr[i];
            int bytecodeSpan = bytecodeSpan(b);
            if (b == (byte) -26) {
                if (bytecodeSpan != EXCEPTION_TYPE_SLOT) {
                    Kit.codeBug();
                }
                uintMap.put(getIndex(bArr, i + EXCEPTION_TRY_END_SLOT), (int) EXCEPTION_TRY_START_SLOT);
            }
            i += bytecodeSpan;
        }
        return uintMap.getKeys();
    }

    private static int getShort(byte[] bArr, int i) {
        return (bArr[i] << 8) | (bArr[i + EXCEPTION_TRY_END_SLOT] & 255);
    }

    private static void initFrame(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr, double[] dArr, int i, int i2, InterpretedFunction interpretedFunction, CallFrame callFrame, CallFrame callFrame2) {
        DebugFrame debugFrame;
        boolean z;
        int i3;
        Object obj;
        Object obj2;
        int[] iArr;
        Object obj3;
        int i4;
        InterpreterData interpreterData = interpretedFunction.idata;
        boolean z2 = interpreterData.itsNeedsActivation;
        if (context.debugger != null) {
            DebugFrame frame = context.debugger.getFrame(context, interpreterData);
            if (frame != null) {
                debugFrame = frame;
                z = true;
            } else {
                debugFrame = frame;
                z = z2;
            }
        } else {
            debugFrame = null;
            z = z2;
        }
        if (z) {
            if (dArr != null) {
                Object argsArray = getArgsArray(objArr, dArr, i, i2);
            }
            i = EXCEPTION_TRY_START_SLOT;
            Object obj4 = null;
        }
        if (interpreterData.itsFunctionType != 0) {
            scriptable = interpretedFunction.getParentScope();
            if (z) {
                scriptable = interpreterData.itsFunctionType == EXCEPTION_LOCAL_SLOT ? ScriptRuntime.createArrowFunctionActivation(interpretedFunction, scriptable, argsArray, interpreterData.isStrict) : ScriptRuntime.createFunctionActivation(interpretedFunction, scriptable, argsArray, interpreterData.isStrict);
            }
        } else {
            ScriptRuntime.initScript(interpretedFunction, scriptable2, context, scriptable, interpretedFunction.idata.evalScriptFlag);
        }
        if (interpreterData.itsNestedFunctions != null) {
            if (!(interpreterData.itsFunctionType == 0 || interpreterData.itsNeedsActivation)) {
                Kit.codeBug();
            }
            for (i3 = EXCEPTION_TRY_START_SLOT; i3 < interpreterData.itsNestedFunctions.length; i3 += EXCEPTION_TRY_END_SLOT) {
                if (interpreterData.itsNestedFunctions[i3].itsFunctionType == EXCEPTION_TRY_END_SLOT) {
                    initFunction(context, scriptable, interpretedFunction, i3);
                }
            }
        }
        int i5 = (interpreterData.itsMaxVars + interpreterData.itsMaxLocals) - 1;
        int i6 = interpreterData.itsMaxFrameArray;
        if (i6 != (interpreterData.itsMaxStack + i5) + EXCEPTION_TRY_END_SLOT) {
            Kit.codeBug();
        }
        if (callFrame2.stack == null || i6 > callFrame2.stack.length) {
            obj = null;
            obj2 = new Object[i6];
            iArr = new int[i6];
            obj3 = new double[i6];
        } else {
            obj = EXCEPTION_TRY_END_SLOT;
            obj2 = callFrame2.stack;
            iArr = callFrame2.stackAttributes;
            obj3 = callFrame2.sDbl;
        }
        int paramAndVarCount = interpreterData.getParamAndVarCount();
        for (i4 = EXCEPTION_TRY_START_SLOT; i4 < paramAndVarCount; i4 += EXCEPTION_TRY_END_SLOT) {
            if (interpreterData.getParamOrVarConst(i4)) {
                iArr[i4] = 13;
            }
        }
        i4 = interpreterData.argCount;
        if (i4 <= i2) {
            i2 = i4;
        }
        callFrame2.parentFrame = callFrame;
        callFrame2.frameIndex = callFrame == null ? EXCEPTION_TRY_START_SLOT : callFrame.frameIndex + EXCEPTION_TRY_END_SLOT;
        if (callFrame2.frameIndex > context.getMaximumInterpreterStackDepth()) {
            throw Context.reportRuntimeError("Exceeded maximum stack depth");
        }
        callFrame2.frozen = false;
        callFrame2.fnOrScript = interpretedFunction;
        callFrame2.idata = interpreterData;
        callFrame2.stack = obj2;
        callFrame2.stackAttributes = iArr;
        callFrame2.sDbl = obj3;
        callFrame2.varSource = callFrame2;
        callFrame2.localShift = interpreterData.itsMaxVars;
        callFrame2.emptyStackTop = i5;
        callFrame2.debuggerFrame = debugFrame;
        callFrame2.useActivation = z;
        callFrame2.thisObj = scriptable2;
        callFrame2.result = Undefined.instance;
        callFrame2.pc = EXCEPTION_TRY_START_SLOT;
        callFrame2.pcPrevBranch = EXCEPTION_TRY_START_SLOT;
        callFrame2.pcSourceLineStart = interpreterData.firstLinePC;
        callFrame2.scope = scriptable;
        callFrame2.savedStackTop = i5;
        callFrame2.savedCallOp = EXCEPTION_TRY_START_SLOT;
        System.arraycopy(argsArray, i, obj2, EXCEPTION_TRY_START_SLOT, i2);
        if (obj4 != null) {
            System.arraycopy(obj4, i, obj3, EXCEPTION_TRY_START_SLOT, i2);
        }
        while (i2 != interpreterData.itsMaxVars) {
            obj2[i2] = Undefined.instance;
            i2 += EXCEPTION_TRY_END_SLOT;
        }
        if (obj != null) {
            for (i3 = i5 + EXCEPTION_TRY_END_SLOT; i3 != obj2.length; i3 += EXCEPTION_TRY_END_SLOT) {
                obj2[i3] = null;
            }
        }
        enterFrame(context, callFrame2, argsArray, false);
    }

    private static CallFrame initFrameForApplyOrCall(Context context, CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2, int i3, Scriptable scriptable, IdFunctionObject idFunctionObject, InterpretedFunction interpretedFunction) {
        Scriptable toObjectOrNull;
        CallFrame callFrame2;
        if (i != 0) {
            Object obj = objArr[i2 + EXCEPTION_HANDLER_SLOT];
            if (obj == UniqueTag.DOUBLE_MARK) {
                obj = ScriptRuntime.wrapNumber(dArr[i2 + EXCEPTION_HANDLER_SLOT]);
            }
            toObjectOrNull = ScriptRuntime.toObjectOrNull(context, obj, callFrame.scope);
        } else {
            toObjectOrNull = null;
        }
        if (toObjectOrNull == null) {
            toObjectOrNull = ScriptRuntime.getTopCallScope(context);
        }
        if (i3 == -55) {
            exitFrame(context, callFrame, null);
            callFrame2 = callFrame.parentFrame;
        } else {
            callFrame.savedStackTop = i2;
            callFrame.savedCallOp = i3;
            callFrame2 = callFrame;
        }
        CallFrame callFrame3 = new CallFrame();
        if (BaseFunction.isApply(idFunctionObject)) {
            Object[] applyArguments = i < EXCEPTION_HANDLER_SLOT ? ScriptRuntime.emptyArgs : ScriptRuntime.getApplyArguments(context, objArr[i2 + EXCEPTION_TYPE_SLOT]);
            initFrame(context, scriptable, toObjectOrNull, applyArguments, null, EXCEPTION_TRY_START_SLOT, applyArguments.length, interpretedFunction, callFrame2, callFrame3);
        } else {
            for (int i4 = EXCEPTION_TRY_END_SLOT; i4 < i; i4 += EXCEPTION_TRY_END_SLOT) {
                objArr[(i2 + EXCEPTION_TRY_END_SLOT) + i4] = objArr[(i2 + EXCEPTION_HANDLER_SLOT) + i4];
                dArr[(i2 + EXCEPTION_TRY_END_SLOT) + i4] = dArr[(i2 + EXCEPTION_HANDLER_SLOT) + i4];
            }
            initFrame(context, scriptable, toObjectOrNull, objArr, dArr, i2 + EXCEPTION_HANDLER_SLOT, i < EXCEPTION_HANDLER_SLOT ? EXCEPTION_TRY_START_SLOT : i - 1, interpretedFunction, callFrame2, callFrame3);
        }
        return callFrame3;
    }

    private static CallFrame initFrameForNoSuchMethod(Context context, CallFrame callFrame, int i, Object[] objArr, double[] dArr, int i2, int i3, Scriptable scriptable, Scriptable scriptable2, NoSuchMethodShim noSuchMethodShim, InterpretedFunction interpretedFunction) {
        CallFrame callFrame2;
        Object[] objArr2 = new Object[i];
        int i4 = i2 + EXCEPTION_HANDLER_SLOT;
        for (int i5 = EXCEPTION_TRY_START_SLOT; i5 < i; i5 += EXCEPTION_TRY_END_SLOT) {
            Number number = objArr[i4];
            if (number == UniqueTag.DOUBLE_MARK) {
                number = ScriptRuntime.wrapNumber(dArr[i4]);
            }
            objArr2[i5] = number;
            i4 += EXCEPTION_TRY_END_SLOT;
        }
        Object[] objArr3 = new Object[EXCEPTION_HANDLER_SLOT];
        objArr3[EXCEPTION_TRY_START_SLOT] = noSuchMethodShim.methodName;
        objArr3[EXCEPTION_TRY_END_SLOT] = context.newArray(scriptable2, objArr2);
        CallFrame callFrame3 = new CallFrame();
        if (i3 == -55) {
            callFrame2 = callFrame.parentFrame;
            exitFrame(context, callFrame, null);
        } else {
            callFrame2 = callFrame;
        }
        initFrame(context, scriptable2, scriptable, objArr3, null, EXCEPTION_TRY_START_SLOT, EXCEPTION_HANDLER_SLOT, interpretedFunction, callFrame2, callFrame3);
        if (i3 != -55) {
            callFrame.savedStackTop = i2;
            callFrame.savedCallOp = i3;
        }
        return callFrame3;
    }

    private static void initFunction(Context context, Scriptable scriptable, InterpretedFunction interpretedFunction, int i) {
        NativeFunction createFunction = InterpretedFunction.createFunction(context, scriptable, interpretedFunction, i);
        ScriptRuntime.initFunction(context, scriptable, createFunction, createFunction.idata.itsFunctionType, interpretedFunction.idata.evalScriptFlag);
    }

    static Object interpret(InterpretedFunction interpretedFunction, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!ScriptRuntime.hasTopCall(context)) {
            Kit.codeBug();
        }
        if (context.interpreterSecurityDomain != interpretedFunction.securityDomain) {
            Object obj = context.interpreterSecurityDomain;
            context.interpreterSecurityDomain = interpretedFunction.securityDomain;
            try {
                Object callWithDomain = interpretedFunction.securityController.callWithDomain(interpretedFunction.securityDomain, context, interpretedFunction, scriptable, scriptable2, objArr);
                return callWithDomain;
            } finally {
                context.interpreterSecurityDomain = obj;
            }
        } else {
            CallFrame callFrame = new CallFrame();
            initFrame(context, scriptable, scriptable2, objArr, null, EXCEPTION_TRY_START_SLOT, objArr.length, interpretedFunction, null, callFrame);
            callFrame.isContinuationsTopFrame = context.isContinuationsTopCall;
            context.isContinuationsTopCall = false;
            return interpretLoop(context, callFrame, null);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object interpretLoop(org.mozilla.javascript.Context r40, org.mozilla.javascript.Interpreter.CallFrame r41, java.lang.Object r42) {
        /*
        r32 = org.mozilla.javascript.UniqueTag.DOUBLE_MARK;
        r33 = org.mozilla.javascript.Undefined.instance;
        r0 = r40;
        r4 = r0.instructionThreshold;
        if (r4 == 0) goto L_0x00da;
    L_0x000a:
        r4 = 1;
        r26 = r4;
    L_0x000d:
        r31 = 0;
        r16 = -1;
        r0 = r40;
        r4 = r0.lastInterpreterFrame;
        if (r4 == 0) goto L_0x0031;
    L_0x0017:
        r0 = r40;
        r4 = r0.previousInterpreterInvocations;
        if (r4 != 0) goto L_0x0026;
    L_0x001d:
        r4 = new org.mozilla.javascript.ObjArray;
        r4.<init>();
        r0 = r40;
        r0.previousInterpreterInvocations = r4;
    L_0x0026:
        r0 = r40;
        r4 = r0.previousInterpreterInvocations;
        r0 = r40;
        r5 = r0.lastInterpreterFrame;
        r4.push(r5);
    L_0x0031:
        r4 = 0;
        if (r42 == 0) goto L_0x00e8;
    L_0x0034:
        r0 = r42;
        r5 = r0 instanceof org.mozilla.javascript.Interpreter.GeneratorState;
        if (r5 == 0) goto L_0x00df;
    L_0x003a:
        r42 = (org.mozilla.javascript.Interpreter.GeneratorState) r42;
        r4 = org.mozilla.javascript.ScriptRuntime.emptyArgs;
        r5 = 1;
        r0 = r40;
        r1 = r41;
        enterFrame(r0, r1, r4, r5);
        r4 = 0;
    L_0x0047:
        r30 = 0;
        r28 = 0;
        r6 = r31;
        r27 = r4;
        r5 = r41;
    L_0x0051:
        if (r27 == 0) goto L_0x00f0;
    L_0x0053:
        r0 = r40;
        r1 = r27;
        r2 = r16;
        r3 = r26;
        r5 = processThrowable(r0, r1, r5, r2, r3);	 Catch:{ Throwable -> 0x00fb }
        r0 = r5.throwable;	 Catch:{ Throwable -> 0x0d6c }
        r27 = r0;
        r4 = 0;
        r5.throwable = r4;	 Catch:{ Throwable -> 0x0d6c }
    L_0x0066:
        r7 = r5.stack;	 Catch:{ Throwable -> 0x00fb }
        r8 = r5.sDbl;	 Catch:{ Throwable -> 0x00fb }
        r4 = r5.varSource;	 Catch:{ Throwable -> 0x00fb }
        r0 = r4.stack;	 Catch:{ Throwable -> 0x00fb }
        r34 = r0;
        r4 = r5.varSource;	 Catch:{ Throwable -> 0x00fb }
        r0 = r4.sDbl;	 Catch:{ Throwable -> 0x00fb }
        r35 = r0;
        r4 = r5.varSource;	 Catch:{ Throwable -> 0x00fb }
        r0 = r4.stackAttributes;	 Catch:{ Throwable -> 0x00fb }
        r36 = r0;
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00fb }
        r15 = r4.itsICode;	 Catch:{ Throwable -> 0x00fb }
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00fb }
        r0 = r4.itsStringTable;	 Catch:{ Throwable -> 0x00fb }
        r37 = r0;
        r9 = r5.savedStackTop;	 Catch:{ Throwable -> 0x00fb }
        r0 = r40;
        r0.lastInterpreterFrame = r5;	 Catch:{ Throwable -> 0x00fb }
        r31 = r6;
    L_0x008e:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4 + 1;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
        switch(r6) {
            case -64: goto L_0x0b61;
            case -63: goto L_0x01be;
            case -62: goto L_0x0103;
            case -61: goto L_0x0838;
            case -60: goto L_0x0099;
            case -59: goto L_0x0403;
            case -58: goto L_0x0a8f;
            case -57: goto L_0x0a6e;
            case -56: goto L_0x0529;
            case -55: goto L_0x05d3;
            case -54: goto L_0x0b09;
            case -53: goto L_0x0af1;
            case -52: goto L_0x089d;
            case -51: goto L_0x0893;
            case -50: goto L_0x08ce;
            case -49: goto L_0x084f;
            case -48: goto L_0x0866;
            case -47: goto L_0x0c07;
            case -46: goto L_0x0bf5;
            case -45: goto L_0x0be3;
            case -44: goto L_0x0bde;
            case -43: goto L_0x0bd9;
            case -42: goto L_0x0bd4;
            case -41: goto L_0x0bcf;
            case -40: goto L_0x0bc1;
            case -39: goto L_0x0bb3;
            case -38: goto L_0x0ba3;
            case -37: goto L_0x0b9f;
            case -36: goto L_0x0b9b;
            case -35: goto L_0x0b97;
            case -34: goto L_0x0b93;
            case -33: goto L_0x0b8f;
            case -32: goto L_0x0b8b;
            case -31: goto L_0x0ab0;
            case -30: goto L_0x0a4d;
            case -29: goto L_0x0a37;
            case -28: goto L_0x07eb;
            case -27: goto L_0x07d6;
            case -26: goto L_0x0b6e;
            case -25: goto L_0x02cd;
            case -24: goto L_0x02ae;
            case -23: goto L_0x02a2;
            case -22: goto L_0x0361;
            case -21: goto L_0x05bb;
            case -20: goto L_0x0a16;
            case -19: goto L_0x09ec;
            case -18: goto L_0x059d;
            case -17: goto L_0x056c;
            case -16: goto L_0x054a;
            case -15: goto L_0x0532;
            case -14: goto L_0x07c2;
            case -13: goto L_0x09e2;
            case -12: goto L_0x09d6;
            case -11: goto L_0x04ff;
            case -10: goto L_0x04bb;
            case -9: goto L_0x0487;
            case -8: goto L_0x081e;
            case -7: goto L_0x087b;
            case -6: goto L_0x026d;
            case -5: goto L_0x02fc;
            case -4: goto L_0x02f5;
            case -3: goto L_0x033b;
            case -2: goto L_0x031b;
            case -1: goto L_0x030b;
            case 0: goto L_0x0422;
            case 1: goto L_0x0099;
            case 2: goto L_0x08d4;
            case 3: goto L_0x08ee;
            case 4: goto L_0x0355;
            case 5: goto L_0x0283;
            case 6: goto L_0x025c;
            case 7: goto L_0x024b;
            case 8: goto L_0x03d3;
            case 9: goto L_0x0374;
            case 10: goto L_0x0374;
            case 11: goto L_0x0374;
            case 12: goto L_0x021f;
            case 13: goto L_0x021f;
            case 14: goto L_0x0211;
            case 15: goto L_0x0211;
            case 16: goto L_0x0211;
            case 17: goto L_0x0211;
            case 18: goto L_0x0374;
            case 19: goto L_0x0374;
            case 20: goto L_0x037a;
            case 21: goto L_0x03a3;
            case 22: goto L_0x03ac;
            case 23: goto L_0x03ac;
            case 24: goto L_0x03ac;
            case 25: goto L_0x03ac;
            case 26: goto L_0x03b2;
            case 27: goto L_0x0367;
            case 28: goto L_0x0394;
            case 29: goto L_0x0394;
            case 30: goto L_0x071a;
            case 31: goto L_0x0422;
            case 32: goto L_0x07ae;
            case 33: goto L_0x0444;
            case 34: goto L_0x042a;
            case 35: goto L_0x045e;
            case 36: goto L_0x04ab;
            case 37: goto L_0x04b3;
            case 38: goto L_0x05d3;
            case 39: goto L_0x080e;
            case 40: goto L_0x0800;
            case 41: goto L_0x07d0;
            case 42: goto L_0x08a7;
            case 43: goto L_0x08ae;
            case 44: goto L_0x08be;
            case 45: goto L_0x08c6;
            case 46: goto L_0x0235;
            case 47: goto L_0x0235;
            case 48: goto L_0x0a23;
            case 49: goto L_0x03c3;
            case 50: goto L_0x01dc;
            case 51: goto L_0x0202;
            case 52: goto L_0x0217;
            case 53: goto L_0x0217;
            case 54: goto L_0x0519;
            case 55: goto L_0x0da3;
            case 56: goto L_0x0da7;
            case 57: goto L_0x08f8;
            case 58: goto L_0x092f;
            case 59: goto L_0x092f;
            case 60: goto L_0x092f;
            case 61: goto L_0x092f;
            case 62: goto L_0x0961;
            case 63: goto L_0x0961;
            case 64: goto L_0x08b6;
            case 65: goto L_0x011f;
            case 66: goto L_0x0ab0;
            case 67: goto L_0x0ab0;
            case 68: goto L_0x04c4;
            case 69: goto L_0x04d2;
            case 70: goto L_0x04f1;
            case 71: goto L_0x05d3;
            case 72: goto L_0x097c;
            case 73: goto L_0x0143;
            case 74: goto L_0x03d3;
            case 75: goto L_0x0b2b;
            case 76: goto L_0x0b41;
            case 77: goto L_0x0b51;
            case 78: goto L_0x0996;
            case 79: goto L_0x09a0;
            case 80: goto L_0x09aa;
            case 81: goto L_0x09c4;
            case 82: goto L_0x0099;
            case 83: goto L_0x0099;
            case 84: goto L_0x0099;
            case 85: goto L_0x0099;
            case 86: goto L_0x0099;
            case 87: goto L_0x0099;
            case 88: goto L_0x0099;
            case 89: goto L_0x0099;
            case 90: goto L_0x0099;
            case 91: goto L_0x0099;
            case 92: goto L_0x0099;
            case 93: goto L_0x0099;
            case 94: goto L_0x0099;
            case 95: goto L_0x0099;
            case 96: goto L_0x0099;
            case 97: goto L_0x0099;
            case 98: goto L_0x0099;
            case 99: goto L_0x0099;
            case 100: goto L_0x0099;
            case 101: goto L_0x0099;
            case 102: goto L_0x0099;
            case 103: goto L_0x0099;
            case 104: goto L_0x0099;
            case 105: goto L_0x0099;
            case 106: goto L_0x0099;
            case 107: goto L_0x0099;
            case 108: goto L_0x0099;
            case 109: goto L_0x0099;
            case 110: goto L_0x0099;
            case 111: goto L_0x0099;
            case 112: goto L_0x0099;
            case 113: goto L_0x0099;
            case 114: goto L_0x0099;
            case 115: goto L_0x0099;
            case 116: goto L_0x0099;
            case 117: goto L_0x0099;
            case 118: goto L_0x0099;
            case 119: goto L_0x0099;
            case 120: goto L_0x0099;
            case 121: goto L_0x0099;
            case 122: goto L_0x0099;
            case 123: goto L_0x0099;
            case 124: goto L_0x0099;
            case 125: goto L_0x0099;
            case 126: goto L_0x0099;
            case 127: goto L_0x0099;
            case 128: goto L_0x0099;
            case 129: goto L_0x0099;
            case 130: goto L_0x0099;
            case 131: goto L_0x0099;
            case 132: goto L_0x0099;
            case 133: goto L_0x0099;
            case 134: goto L_0x0099;
            case 135: goto L_0x0099;
            case 136: goto L_0x0099;
            case 137: goto L_0x0099;
            case 138: goto L_0x0099;
            case 139: goto L_0x0099;
            case 140: goto L_0x0099;
            case 141: goto L_0x0099;
            case 142: goto L_0x0099;
            case 143: goto L_0x0099;
            case 144: goto L_0x0099;
            case 145: goto L_0x0099;
            case 146: goto L_0x0099;
            case 147: goto L_0x0099;
            case 148: goto L_0x0099;
            case 149: goto L_0x0099;
            case 150: goto L_0x0099;
            case 151: goto L_0x0099;
            case 152: goto L_0x0099;
            case 153: goto L_0x0099;
            case 154: goto L_0x0099;
            case 155: goto L_0x0099;
            case 156: goto L_0x0099;
            case 157: goto L_0x0dab;
            default: goto L_0x0099;
        };	 Catch:{ Throwable -> 0x00c5 }
    L_0x0099:
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        dumpICode(r4);	 Catch:{ Throwable -> 0x00c5 }
        r4 = new java.lang.RuntimeException;	 Catch:{ Throwable -> 0x00c5 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00c5 }
        r7.<init>();	 Catch:{ Throwable -> 0x00c5 }
        r8 = "Unknown icode : ";
        r7 = r7.append(r8);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r7.append(r6);	 Catch:{ Throwable -> 0x00c5 }
        r7 = " @ pc : ";
        r6 = r6.append(r7);	 Catch:{ Throwable -> 0x00c5 }
        r7 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r7 = r7 + -1;
        r6 = r6.append(r7);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6.toString();	 Catch:{ Throwable -> 0x00c5 }
        r4.<init>(r6);	 Catch:{ Throwable -> 0x00c5 }
        throw r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x00c5:
        r4 = move-exception;
        r6 = r28;
        r8 = r31;
        r9 = r5;
        r5 = r30;
    L_0x00cd:
        if (r27 == 0) goto L_0x0c4f;
    L_0x00cf:
        r5 = java.lang.System.err;
        r4.printStackTrace(r5);
        r4 = new java.lang.IllegalStateException;
        r4.<init>();
        throw r4;
    L_0x00da:
        r4 = 0;
        r26 = r4;
        goto L_0x000d;
    L_0x00df:
        r0 = r42;
        r5 = r0 instanceof org.mozilla.javascript.Interpreter.ContinuationJump;
        if (r5 != 0) goto L_0x00e8;
    L_0x00e5:
        org.mozilla.javascript.Kit.codeBug();
    L_0x00e8:
        r38 = r4;
        r4 = r42;
        r42 = r38;
        goto L_0x0047;
    L_0x00f0:
        if (r42 != 0) goto L_0x0066;
    L_0x00f2:
        r4 = r5.frozen;	 Catch:{ Throwable -> 0x00fb }
        if (r4 == 0) goto L_0x0066;
    L_0x00f6:
        org.mozilla.javascript.Kit.codeBug();	 Catch:{ Throwable -> 0x00fb }
        goto L_0x0066;
    L_0x00fb:
        r4 = move-exception;
        r8 = r6;
        r9 = r5;
        r6 = r28;
        r5 = r30;
        goto L_0x00cd;
    L_0x0103:
        r4 = r5.frozen;	 Catch:{ Throwable -> 0x00c5 }
        if (r4 != 0) goto L_0x0143;
    L_0x0107:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + -1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = captureFrameForGenerator(r5);	 Catch:{ Throwable -> 0x00c5 }
        r6 = 1;
        r4.frozen = r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = new org.mozilla.javascript.NativeGenerator;	 Catch:{ Throwable -> 0x00c5 }
        r7 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r8 = r4.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r6.<init>(r7, r8, r4);	 Catch:{ Throwable -> 0x00c5 }
        r5.result = r6;	 Catch:{ Throwable -> 0x00c5 }
    L_0x011f:
        r4 = 0;
        r0 = r40;
        exitFrame(r0, r5, r4);	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.result;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.resultDbl;	 Catch:{ Throwable -> 0x0d75 }
        r8 = r5.parentFrame;	 Catch:{ Throwable -> 0x0d59 }
        if (r8 == 0) goto L_0x0c27;
    L_0x012d:
        r8 = r5.parentFrame;	 Catch:{ Throwable -> 0x0d59 }
        r5 = r8.frozen;	 Catch:{ Throwable -> 0x0d61 }
        if (r5 == 0) goto L_0x0137;
    L_0x0133:
        r8 = r8.cloneFrozen();	 Catch:{ Throwable -> 0x0d61 }
    L_0x0137:
        setCallResult(r8, r4, r6);	 Catch:{ Throwable -> 0x0d61 }
        r30 = 0;
        r28 = r6;
        r5 = r8;
        r6 = r31;
        goto L_0x0051;
    L_0x0143:
        r4 = r5.frozen;	 Catch:{ Throwable -> 0x00c5 }
        if (r4 != 0) goto L_0x0150;
    L_0x0147:
        r0 = r40;
        r1 = r42;
        r5 = freezeGenerator(r0, r5, r9, r1);	 Catch:{ Throwable -> 0x00c5 }
    L_0x014f:
        return r5;
    L_0x0150:
        r0 = r42;
        r4 = thawGenerator(r5, r9, r0, r6);	 Catch:{ Throwable -> 0x00c5 }
        r6 = org.mozilla.javascript.Scriptable.NOT_FOUND;	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == r6) goto L_0x008e;
    L_0x015a:
        r8 = r28;
        r10 = r30;
        r19 = r5;
        r5 = r4;
    L_0x0161:
        if (r5 != 0) goto L_0x0166;
    L_0x0163:
        org.mozilla.javascript.Kit.codeBug();
    L_0x0166:
        r6 = 0;
        if (r42 == 0) goto L_0x0c58;
    L_0x0169:
        r0 = r42;
        r4 = r0.operation;
        r7 = 2;
        if (r4 != r7) goto L_0x0c58;
    L_0x0170:
        r0 = r42;
        r4 = r0.value;
        if (r5 != r4) goto L_0x0c58;
    L_0x0176:
        r4 = 1;
        r38 = r6;
        r6 = r4;
        r4 = r38;
    L_0x017c:
        if (r26 == 0) goto L_0x0db6;
    L_0x017e:
        r7 = 100;
        r0 = r40;
        r1 = r19;
        addInstructionCount(r0, r1, r7);	 Catch:{ RuntimeException -> 0x0cd6, Error -> 0x0cdd }
        r7 = r6;
        r6 = r4;
    L_0x0189:
        r0 = r19;
        r4 = r0.debuggerFrame;
        if (r4 == 0) goto L_0x0daf;
    L_0x018f:
        r4 = r5 instanceof java.lang.RuntimeException;
        if (r4 == 0) goto L_0x0daf;
    L_0x0193:
        r4 = r5;
        r4 = (java.lang.RuntimeException) r4;
        r0 = r19;
        r11 = r0.debuggerFrame;	 Catch:{ Throwable -> 0x0ce5 }
        r0 = r40;
        r11.onExceptionThrown(r0, r4);	 Catch:{ Throwable -> 0x0ce5 }
        r38 = r6;
        r6 = r7;
        r7 = r38;
    L_0x01a4:
        if (r6 == 0) goto L_0x0cf3;
    L_0x01a6:
        r4 = 2;
        if (r6 == r4) goto L_0x0cf0;
    L_0x01a9:
        r4 = 1;
    L_0x01aa:
        r0 = r19;
        r16 = getExceptionHandler(r0, r4);
        if (r16 < 0) goto L_0x0cf3;
    L_0x01b2:
        r28 = r8;
        r30 = r10;
        r6 = r31;
        r27 = r5;
        r5 = r19;
        goto L_0x0051;
    L_0x01be:
        r4 = 1;
        r5.frozen = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getIndex(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r6 = new org.mozilla.javascript.JavaScriptException;	 Catch:{ Throwable -> 0x00c5 }
        r7 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r7 = org.mozilla.javascript.NativeIterator.getStopIterationObject(r7);	 Catch:{ Throwable -> 0x00c5 }
        r8 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r8 = r8.itsSourceFile;	 Catch:{ Throwable -> 0x00c5 }
        r6.<init>(r7, r8, r4);	 Catch:{ Throwable -> 0x00c5 }
        r0 = r42;
        r0.returnedException = r6;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x011f;
    L_0x01dc:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x01e8;
    L_0x01e2:
        r6 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r6);	 Catch:{ Throwable -> 0x00c5 }
    L_0x01e8:
        r6 = r9 + -1;
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r7 = getIndex(r15, r6);	 Catch:{ Throwable -> 0x00c5 }
        r6 = new org.mozilla.javascript.JavaScriptException;	 Catch:{ Throwable -> 0x00c5 }
        r8 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r8 = r8.itsSourceFile;	 Catch:{ Throwable -> 0x00c5 }
        r6.<init>(r4, r8, r7);	 Catch:{ Throwable -> 0x00c5 }
        r8 = r28;
        r10 = r30;
        r19 = r5;
        r5 = r6;
        goto L_0x0161;
    L_0x0202:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + r16;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r8 = r28;
        r10 = r30;
        r19 = r5;
        r5 = r4;
        goto L_0x0161;
    L_0x0211:
        r9 = doCompare(r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0217:
        r0 = r40;
        r9 = doInOrInstanceof(r0, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x021f:
        r9 = r9 + -1;
        r10 = doEquals(r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        r4 = 13;
        if (r6 != r4) goto L_0x0233;
    L_0x0229:
        r4 = 1;
    L_0x022a:
        r4 = r4 ^ r10;
        r4 = org.mozilla.javascript.ScriptRuntime.wrapBoolean(r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0233:
        r4 = 0;
        goto L_0x022a;
    L_0x0235:
        r9 = r9 + -1;
        r10 = doShallowEquals(r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        r4 = 47;
        if (r6 != r4) goto L_0x0249;
    L_0x023f:
        r4 = 1;
    L_0x0240:
        r4 = r4 ^ r10;
        r4 = org.mozilla.javascript.ScriptRuntime.wrapBoolean(r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0249:
        r4 = 0;
        goto L_0x0240;
    L_0x024b:
        r4 = r9 + -1;
        r6 = stack_boolean(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        if (r6 == 0) goto L_0x0da0;
    L_0x0253:
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6 + 2;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r4;
        goto L_0x008e;
    L_0x025c:
        r4 = r9 + -1;
        r6 = stack_boolean(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        if (r6 != 0) goto L_0x0da0;
    L_0x0264:
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6 + 2;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r4;
        goto L_0x008e;
    L_0x026d:
        r4 = r9 + -1;
        r6 = stack_boolean(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        if (r6 != 0) goto L_0x027e;
    L_0x0275:
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6 + 2;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r4;
        goto L_0x008e;
    L_0x027e:
        r9 = r4 + -1;
        r6 = 0;
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
    L_0x0283:
        if (r26 == 0) goto L_0x028b;
    L_0x0285:
        r4 = 2;
        r0 = r40;
        addInstructionCount(r0, r5, r4);	 Catch:{ Throwable -> 0x00c5 }
    L_0x028b:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getShort(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == 0) goto L_0x0c19;
    L_0x0293:
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + -1;
        r4 = r4 + r6;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x029a:
        if (r26 == 0) goto L_0x008e;
    L_0x029c:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r5.pcPrevBranch = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x02a2:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 2;
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x0283;
    L_0x02ae:
        r4 = r5.emptyStackTop;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        if (r9 != r4) goto L_0x02c4;
    L_0x02b4:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r7[r16] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r8[r16] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + -1;
        goto L_0x008e;
    L_0x02c4:
        r4 = r5.emptyStackTop;	 Catch:{ Throwable -> 0x00c5 }
        if (r9 == r4) goto L_0x008e;
    L_0x02c8:
        org.mozilla.javascript.Kit.codeBug();	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x02cd:
        if (r26 == 0) goto L_0x02d5;
    L_0x02cf:
        r4 = 0;
        r0 = r40;
        addInstructionCount(r0, r5, r4);	 Catch:{ Throwable -> 0x00c5 }
    L_0x02d5:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r7[r16];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 == r0) goto L_0x02e8;
    L_0x02df:
        r8 = r28;
        r10 = r30;
        r19 = r5;
        r5 = r4;
        goto L_0x0161;
    L_0x02e8:
        r10 = r8[r16];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (int) r10;	 Catch:{ Throwable -> 0x00c5 }
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        if (r26 == 0) goto L_0x008e;
    L_0x02ef:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r5.pcPrevBranch = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x02f5:
        r4 = 0;
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + -1;
        goto L_0x008e;
    L_0x02fc:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r5.result = r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r5.resultDbl = r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = 0;
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + -1;
        goto L_0x008e;
    L_0x030b:
        r4 = r9 + 1;
        r6 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + 1;
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r8[r4] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 1;
        goto L_0x008e;
    L_0x031b:
        r4 = r9 + 1;
        r6 = r9 + -1;
        r6 = r7[r6];	 Catch:{ Throwable -> 0x00c5 }
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + 1;
        r6 = r9 + -1;
        r10 = r8[r6];	 Catch:{ Throwable -> 0x00c5 }
        r8[r4] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + 2;
        r6 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + 2;
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r8[r4] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 2;
        goto L_0x008e;
    L_0x033b:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r6 = r9 + -1;
        r6 = r7[r6];	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r9 + -1;
        r7[r6] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + -1;
        r12 = r8[r4];	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r12;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + -1;
        r8[r4] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0355:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r5.result = r4;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r5.resultDbl = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + -1;
        goto L_0x011f;
    L_0x0361:
        r0 = r33;
        r5.result = r0;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x011f;
    L_0x0367:
        r4 = stack_int32(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 ^ -1;
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0374:
        r9 = doBitOp(r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x037a:
        r4 = r9 + -1;
        r10 = stack_double(r5, r4);	 Catch:{ Throwable -> 0x00c5 }
        r4 = stack_int32(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 & 31;
        r9 = r9 + -1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r10 = org.mozilla.javascript.ScriptRuntime.toUint32(r10);	 Catch:{ Throwable -> 0x00c5 }
        r10 = r10 >>> r4;
        r10 = (double) r10;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0394:
        r10 = stack_double(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = 29;
        if (r6 != r4) goto L_0x039f;
    L_0x039e:
        r10 = -r10;
    L_0x039f:
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03a3:
        r9 = r9 + -1;
        r0 = r40;
        doAdd(r7, r8, r9, r0);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03ac:
        r9 = doArithmetic(r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03b2:
        r4 = stack_boolean(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        if (r4 != 0) goto L_0x03c1;
    L_0x03b8:
        r4 = 1;
    L_0x03b9:
        r4 = org.mozilla.javascript.ScriptRuntime.wrapBoolean(r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03c1:
        r4 = 0;
        goto L_0x03b9;
    L_0x03c3:
        r9 = r9 + 1;
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.bind(r0, r4, r1);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03d3:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d9d;
    L_0x03d9:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r10 = r4;
    L_0x03e0:
        r9 = r9 + -1;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Scriptable) r4;	 Catch:{ Throwable -> 0x00c5 }
        r11 = 8;
        if (r6 != r11) goto L_0x03f8;
    L_0x03ea:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.setName(r4, r10, r0, r6, r1);	 Catch:{ Throwable -> 0x00c5 }
    L_0x03f4:
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x03f8:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.strictSetName(r4, r10, r0, r6, r1);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x03f4;
    L_0x0403:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d9a;
    L_0x0409:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4;
    L_0x0410:
        r9 = r9 + -1;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Scriptable) r4;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.setConst(r4, r6, r0, r1);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0422:
        r4 = r40;
        r9 = doDelName(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x042a:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0436;
    L_0x0430:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0436:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.getObjectPropNoWarn(r4, r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0444:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0450;
    L_0x044a:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0450:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.getObjectProp(r4, r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x045e:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d97;
    L_0x0464:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4;
    L_0x046b:
        r9 = r9 + -1;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0479;
    L_0x0473:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0479:
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.setObjectProp(r4, r0, r6, r1, r10);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0487:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0493;
    L_0x048d:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0493:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r15[r10];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.propIncrDecr(r4, r0, r1, r6, r10);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04ab:
        r0 = r40;
        r9 = doGetElem(r0, r5, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04b3:
        r0 = r40;
        r9 = doSetElem(r0, r5, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04bb:
        r4 = r40;
        r6 = r15;
        r9 = doElemIncDec(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04c4:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Ref) r4;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.refGet(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04d2:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d94;
    L_0x04d8:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4;
    L_0x04df:
        r9 = r9 + -1;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Ref) r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.refSet(r4, r6, r0, r10);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04f1:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Ref) r4;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.refDel(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x04ff:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Ref) r4;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r15[r10];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.refIncrDecr(r4, r0, r6, r10);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0519:
        r9 = r9 + 1;
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r7[r16];	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r8[r16];	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0529:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = 0;
        r7[r16] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0532:
        r4 = r9 + 1;
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r6 = org.mozilla.javascript.ScriptRuntime.getNameFunctionAndThis(r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r4 + 1;
        r4 = org.mozilla.javascript.ScriptRuntime.lastStoredScriptable(r40);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x054a:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0556;
    L_0x0550:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0556:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.getPropFunctionAndThis(r4, r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 1;
        r4 = org.mozilla.javascript.ScriptRuntime.lastStoredScriptable(r40);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x056c:
        r4 = r9 + -1;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d91;
    L_0x0574:
        r4 = r9 + -1;
        r10 = r8[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4;
    L_0x057d:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0589;
    L_0x0583:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0589:
        r10 = r9 + -1;
        r11 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.getElemFunctionAndThis(r6, r4, r0, r11);	 Catch:{ Throwable -> 0x00c5 }
        r7[r10] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.lastStoredScriptable(r40);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x059d:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x05a9;
    L_0x05a3:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x05a9:
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.getValueFunctionAndThis(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 1;
        r4 = org.mozilla.javascript.ScriptRuntime.lastStoredScriptable(r40);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x05bb:
        if (r26 == 0) goto L_0x05c7;
    L_0x05bd:
        r0 = r40;
        r4 = r0.instructionCount;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 100;
        r0 = r40;
        r0.instructionCount = r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x05c7:
        r10 = r40;
        r11 = r5;
        r12 = r7;
        r13 = r8;
        r14 = r9;
        r9 = doCallSpecial(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x05d3:
        if (r26 == 0) goto L_0x05df;
    L_0x05d5:
        r0 = r40;
        r4 = r0.instructionCount;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 100;
        r0 = r40;
        r0.instructionCount = r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x05df:
        r4 = r16 + 1;
        r9 = r9 - r4;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Callable) r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r9 + 1;
        r12 = r7[r10];	 Catch:{ Throwable -> 0x00c5 }
        r12 = (org.mozilla.javascript.Scriptable) r12;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 71;
        if (r6 != r10) goto L_0x0602;
    L_0x05f0:
        r6 = r9 + 2;
        r0 = r16;
        r6 = getArgsArray(r7, r8, r6, r0);	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.callRef(r4, r12, r6, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0602:
        r11 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.useActivation;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x060e;
    L_0x0608:
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r11 = org.mozilla.javascript.ScriptableObject.getTopLevelScope(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x060e:
        r10 = r4 instanceof org.mozilla.javascript.InterpretedFunction;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x064e;
    L_0x0612:
        r0 = r4;
        r0 = (org.mozilla.javascript.InterpretedFunction) r0;	 Catch:{ Throwable -> 0x00c5 }
        r17 = r0;
        r10 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r10.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r17;
        r13 = r0.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 != r13) goto L_0x064e;
    L_0x0621:
        r19 = new org.mozilla.javascript.Interpreter$CallFrame;	 Catch:{ Throwable -> 0x00c5 }
        r4 = 0;
        r0 = r19;
        r0.<init>();	 Catch:{ Throwable -> 0x00c5 }
        r4 = -55;
        if (r6 != r4) goto L_0x0d8d;
    L_0x062d:
        r0 = r5.parentFrame;	 Catch:{ Throwable -> 0x00c5 }
        r18 = r0;
        r4 = 0;
        r0 = r40;
        exitFrame(r0, r5, r4);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0637:
        r15 = r9 + 2;
        r10 = r40;
        r13 = r7;
        r14 = r8;
        initFrame(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);	 Catch:{ Throwable -> 0x00c5 }
        r4 = -55;
        if (r6 == r4) goto L_0x0648;
    L_0x0644:
        r5.savedStackTop = r9;	 Catch:{ Throwable -> 0x00c5 }
        r5.savedCallOp = r6;	 Catch:{ Throwable -> 0x00c5 }
    L_0x0648:
        r6 = r31;
        r5 = r19;
        goto L_0x0051;
    L_0x064e:
        r10 = r4 instanceof org.mozilla.javascript.NativeContinuation;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x0675;
    L_0x0652:
        r6 = new org.mozilla.javascript.Interpreter$ContinuationJump;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.NativeContinuation) r4;	 Catch:{ Throwable -> 0x00c5 }
        r6.<init>(r4, r5);	 Catch:{ Throwable -> 0x00c5 }
        if (r16 != 0) goto L_0x0668;
    L_0x065b:
        r0 = r33;
        r6.result = r0;	 Catch:{ Throwable -> 0x00c5 }
    L_0x065f:
        r8 = r28;
        r10 = r30;
        r19 = r5;
        r5 = r6;
        goto L_0x0161;
    L_0x0668:
        r4 = r9 + 2;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r6.result = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + 2;
        r8 = r8[r4];	 Catch:{ Throwable -> 0x00c5 }
        r6.resultDbl = r8;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x065f;
    L_0x0675:
        r10 = r4 instanceof org.mozilla.javascript.IdFunctionObject;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x06c6;
    L_0x0679:
        r0 = r4;
        r0 = (org.mozilla.javascript.IdFunctionObject) r0;	 Catch:{ Throwable -> 0x00c5 }
        r22 = r0;
        r10 = org.mozilla.javascript.NativeContinuation.isContinuationConstructor(r22);	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x0693;
    L_0x0684:
        r4 = r5.stack;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.parentFrame;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 0;
        r0 = r40;
        r6 = captureContinuation(r0, r6, r10);	 Catch:{ Throwable -> 0x00c5 }
        r4[r9] = r6;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0693:
        r10 = org.mozilla.javascript.BaseFunction.isApplyOrCall(r22);	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x06c6;
    L_0x0699:
        r23 = org.mozilla.javascript.ScriptRuntime.getCallable(r12);	 Catch:{ Throwable -> 0x00c5 }
        r0 = r23;
        r10 = r0 instanceof org.mozilla.javascript.InterpretedFunction;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x06c6;
    L_0x06a3:
        r23 = (org.mozilla.javascript.InterpretedFunction) r23;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r10.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r23;
        r13 = r0.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 != r13) goto L_0x06c6;
    L_0x06af:
        r14 = r40;
        r15 = r5;
        r17 = r7;
        r18 = r8;
        r19 = r9;
        r20 = r6;
        r21 = r11;
        r19 = initFrameForApplyOrCall(r14, r15, r16, r17, r18, r19, r20, r21, r22, r23);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r31;
        r5 = r19;
        goto L_0x0051;
    L_0x06c6:
        r10 = r4 instanceof org.mozilla.javascript.ScriptRuntime.NoSuchMethodShim;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x0700;
    L_0x06ca:
        r0 = r4;
        r0 = (org.mozilla.javascript.ScriptRuntime.NoSuchMethodShim) r0;	 Catch:{ Throwable -> 0x00c5 }
        r23 = r0;
        r0 = r23;
        r0 = r0.noSuchMethodMethod;	 Catch:{ Throwable -> 0x00c5 }
        r24 = r0;
        r0 = r24;
        r10 = r0 instanceof org.mozilla.javascript.InterpretedFunction;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x0700;
    L_0x06db:
        r24 = (org.mozilla.javascript.InterpretedFunction) r24;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r10.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r24;
        r13 = r0.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 != r13) goto L_0x0700;
    L_0x06e7:
        r14 = r40;
        r15 = r5;
        r17 = r7;
        r18 = r8;
        r19 = r9;
        r20 = r6;
        r21 = r12;
        r22 = r11;
        r19 = initFrameForNoSuchMethod(r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r31;
        r5 = r19;
        goto L_0x0051;
    L_0x0700:
        r0 = r40;
        r0.lastInterpreterFrame = r5;	 Catch:{ Throwable -> 0x00c5 }
        r5.savedCallOp = r6;	 Catch:{ Throwable -> 0x00c5 }
        r5.savedStackTop = r9;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r9 + 2;
        r0 = r16;
        r6 = getArgsArray(r7, r8, r6, r0);	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = r4.call(r0, r11, r12, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x071a:
        if (r26 == 0) goto L_0x0726;
    L_0x071c:
        r0 = r40;
        r4 = r0.instructionCount;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 100;
        r0 = r40;
        r0.instructionCount = r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x0726:
        r9 = r9 - r16;
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r10 = r4 instanceof org.mozilla.javascript.InterpretedFunction;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x0768;
    L_0x072e:
        r0 = r4;
        r0 = (org.mozilla.javascript.InterpretedFunction) r0;	 Catch:{ Throwable -> 0x00c5 }
        r17 = r0;
        r10 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r10.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r17;
        r11 = r0.securityDomain;	 Catch:{ Throwable -> 0x00c5 }
        if (r10 != r11) goto L_0x0768;
    L_0x073d:
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r17;
        r1 = r40;
        r12 = r0.createObject(r1, r4);	 Catch:{ Throwable -> 0x00c5 }
        r19 = new org.mozilla.javascript.Interpreter$CallFrame;	 Catch:{ Throwable -> 0x00c5 }
        r4 = 0;
        r0 = r19;
        r0.<init>();	 Catch:{ Throwable -> 0x00c5 }
        r11 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r15 = r9 + 1;
        r10 = r40;
        r13 = r7;
        r14 = r8;
        r18 = r5;
        initFrame(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r12;	 Catch:{ Throwable -> 0x00c5 }
        r5.savedStackTop = r9;	 Catch:{ Throwable -> 0x00c5 }
        r5.savedCallOp = r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r31;
        r5 = r19;
        goto L_0x0051;
    L_0x0768:
        r6 = r4 instanceof org.mozilla.javascript.Function;	 Catch:{ Throwable -> 0x00c5 }
        if (r6 != 0) goto L_0x077b;
    L_0x076c:
        r0 = r32;
        if (r4 != r0) goto L_0x0776;
    L_0x0770:
        r6 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r6);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0776:
        r4 = org.mozilla.javascript.ScriptRuntime.notFunctionError(r4);	 Catch:{ Throwable -> 0x00c5 }
        throw r4;	 Catch:{ Throwable -> 0x00c5 }
    L_0x077b:
        r4 = (org.mozilla.javascript.Function) r4;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4 instanceof org.mozilla.javascript.IdFunctionObject;	 Catch:{ Throwable -> 0x00c5 }
        if (r6 == 0) goto L_0x079a;
    L_0x0781:
        r0 = r4;
        r0 = (org.mozilla.javascript.IdFunctionObject) r0;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r0;
        r6 = org.mozilla.javascript.NativeContinuation.isContinuationConstructor(r6);	 Catch:{ Throwable -> 0x00c5 }
        if (r6 == 0) goto L_0x079a;
    L_0x078b:
        r4 = r5.stack;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.parentFrame;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 0;
        r0 = r40;
        r6 = captureContinuation(r0, r6, r10);	 Catch:{ Throwable -> 0x00c5 }
        r4[r9] = r6;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x079a:
        r6 = r9 + 1;
        r0 = r16;
        r6 = getArgsArray(r7, r8, r6, r0);	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = r4.construct(r0, r10, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x07ae:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x07ba;
    L_0x07b4:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x07ba:
        r4 = org.mozilla.javascript.ScriptRuntime.typeof(r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x07c2:
        r9 = r9 + 1;
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.typeofName(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x07d0:
        r9 = r9 + 1;
        r7[r9] = r31;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x07d6:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getShort(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 2;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x07eb:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getInt(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 4;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0800:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4.itsDoubleTable;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r4[r16];	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x080e:
        r9 = r9 + 1;
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r31;
        r4 = org.mozilla.javascript.ScriptRuntime.name(r0, r4, r1);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x081e:
        r9 = r9 + 1;
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r15[r6];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.nameIncrDecr(r4, r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0838:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4 + 1;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r13 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
    L_0x0840:
        r6 = r5;
        r10 = r34;
        r11 = r35;
        r12 = r36;
        r9 = doSetConstVar(r6, r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Throwable -> 0x00c5 }
        r16 = r13;
        goto L_0x008e;
    L_0x084f:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4 + 1;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r13 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
    L_0x0857:
        r6 = r5;
        r10 = r34;
        r11 = r35;
        r12 = r36;
        r9 = doSetVar(r6, r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Throwable -> 0x00c5 }
        r16 = r13;
        goto L_0x008e;
    L_0x0866:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4 + 1;
        r5.pc = r6;	 Catch:{ Throwable -> 0x00c5 }
        r12 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
    L_0x086e:
        r6 = r5;
        r10 = r34;
        r11 = r35;
        r9 = doGetVar(r6, r7, r8, r9, r10, r11, r12);	 Catch:{ Throwable -> 0x00c5 }
        r16 = r12;
        goto L_0x008e;
    L_0x087b:
        r17 = r40;
        r18 = r5;
        r19 = r7;
        r20 = r8;
        r21 = r9;
        r22 = r34;
        r23 = r35;
        r24 = r36;
        r25 = r16;
        r9 = doVarIncDec(r17, r18, r19, r20, r21, r22, r23, r24, r25);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0893:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 0;
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x089d:
        r9 = r9 + 1;
        r7[r9] = r32;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08a7:
        r9 = r9 + 1;
        r4 = 0;
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08ae:
        r9 = r9 + 1;
        r4 = r5.thisObj;	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08b6:
        r9 = r9 + 1;
        r4 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08be:
        r9 = r9 + 1;
        r4 = java.lang.Boolean.FALSE;	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08c6:
        r9 = r9 + 1;
        r4 = java.lang.Boolean.TRUE;	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08ce:
        r9 = r9 + 1;
        r7[r9] = r33;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08d4:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x08e0;
    L_0x08da:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x08e0:
        r9 = r9 + -1;
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.enterWith(r4, r0, r6);	 Catch:{ Throwable -> 0x00c5 }
        r5.scope = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08ee:
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.leaveWith(r4);	 Catch:{ Throwable -> 0x00c5 }
        r5.scope = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x08f8:
        r9 = r9 + -1;
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4.itsICode;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4[r6];	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == 0) goto L_0x0927;
    L_0x0908:
        r4 = 1;
        r6 = r4;
    L_0x090a:
        r4 = r9 + 1;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Throwable) r4;	 Catch:{ Throwable -> 0x00c5 }
        if (r6 != 0) goto L_0x092a;
    L_0x0912:
        r6 = 0;
    L_0x0913:
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.newCatchScope(r4, r6, r0, r1, r10);	 Catch:{ Throwable -> 0x00c5 }
        r7[r16] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0927:
        r4 = 0;
        r6 = r4;
        goto L_0x090a;
    L_0x092a:
        r6 = r7[r16];	 Catch:{ Throwable -> 0x00c5 }
        r6 = (org.mozilla.javascript.Scriptable) r6;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x0913;
    L_0x092f:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d8a;
    L_0x0935:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r10 = r4;
    L_0x093c:
        r9 = r9 + -1;
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = 58;
        if (r6 != r4) goto L_0x0953;
    L_0x0946:
        r4 = 0;
    L_0x0947:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.enumInit(r10, r0, r6, r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r16] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0953:
        r4 = 59;
        if (r6 != r4) goto L_0x0959;
    L_0x0957:
        r4 = 1;
        goto L_0x0947;
    L_0x0959:
        r4 = 61;
        if (r6 != r4) goto L_0x095f;
    L_0x095d:
        r4 = 6;
        goto L_0x0947;
    L_0x095f:
        r4 = 2;
        goto L_0x0947;
    L_0x0961:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r7[r16];	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 1;
        r10 = 62;
        if (r6 != r10) goto L_0x0975;
    L_0x096d:
        r4 = org.mozilla.javascript.ScriptRuntime.enumNext(r4);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0971:
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0975:
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.enumId(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x0971;
    L_0x097c:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0988;
    L_0x0982:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0988:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r31;
        r1 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.specialRef(r4, r0, r1, r6);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0996:
        r0 = r40;
        r1 = r16;
        r9 = doRefMember(r0, r7, r8, r9, r1);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09a0:
        r0 = r40;
        r1 = r16;
        r9 = doRefNsMember(r0, r7, r8, r9, r1);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09aa:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x09b6;
    L_0x09b0:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x09b6:
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r16;
        r4 = org.mozilla.javascript.ScriptRuntime.nameRef(r4, r0, r6, r1);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09c4:
        r17 = r40;
        r18 = r5;
        r19 = r7;
        r20 = r8;
        r21 = r9;
        r22 = r16;
        r9 = doRefNsName(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09d6:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r7[r16];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (org.mozilla.javascript.Scriptable) r4;	 Catch:{ Throwable -> 0x00c5 }
        r5.scope = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09e2:
        r4 = r5.localShift;	 Catch:{ Throwable -> 0x00c5 }
        r16 = r16 + r4;
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r7[r16] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x09ec:
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r16;
        r4 = org.mozilla.javascript.InterpretedFunction.createFunction(r0, r4, r6, r1);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4.idata;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6.itsFunctionType;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 4;
        if (r6 != r10) goto L_0x0a10;
    L_0x09ff:
        r9 = r9 + 1;
        r6 = new org.mozilla.javascript.ArrowFunction;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r11 = r5.thisObj;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r6.<init>(r0, r10, r4, r11);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r6;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a10:
        r9 = r9 + 1;
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a16:
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.fnOrScript;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r1 = r16;
        initFunction(r0, r4, r6, r1);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a23:
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4.itsRegExpLiterals;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4[r16];	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + 1;
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.wrapRegExp(r0, r6, r4);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a37:
        r4 = r9 + 1;
        r0 = r16;
        r6 = new int[r0];	 Catch:{ Throwable -> 0x00c5 }
        r7[r4] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r4 + 1;
        r0 = r16;
        r4 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = 0;
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a4d:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0d87;
    L_0x0a53:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r4;
    L_0x0a5a:
        r9 = r9 + -1;
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r10 = (int) r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4[r10] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r10 + 1;
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a6e:
        r6 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + -1;
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r10 = (int) r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4[r10] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + -1;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (int[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (int[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r6 = -1;
        r4[r10] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r10 + 1;
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0a8f:
        r6 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r9 = r9 + -1;
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r10 = (int) r10;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4[r10] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r9 + -1;
        r4 = r7[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (int[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (int[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r6 = 1;
        r4[r10] = r6;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r10 + 1;
        r10 = (double) r4;	 Catch:{ Throwable -> 0x00c5 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0ab0:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Throwable -> 0x00c5 }
        r10 = r9 + -1;
        r9 = r7[r10];	 Catch:{ Throwable -> 0x00c5 }
        r9 = (int[]) r9;	 Catch:{ Throwable -> 0x00c5 }
        r9 = (int[]) r9;	 Catch:{ Throwable -> 0x00c5 }
        r11 = 67;
        if (r6 != r11) goto L_0x0ad9;
    L_0x0ac2:
        r6 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6.literalIds;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6[r16];	 Catch:{ Throwable -> 0x00c5 }
        r6 = (java.lang.Object[]) r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = (java.lang.Object[]) r6;	 Catch:{ Throwable -> 0x00c5 }
        r11 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.newObjectLiteral(r6, r4, r9, r0, r11);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0ad4:
        r7[r10] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r9 = r10;
        goto L_0x008e;
    L_0x0ad9:
        r9 = 0;
        r11 = -31;
        if (r6 != r11) goto L_0x0d84;
    L_0x0ade:
        r6 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6.literalIds;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r6[r16];	 Catch:{ Throwable -> 0x00c5 }
        r6 = (int[]) r6;	 Catch:{ Throwable -> 0x00c5 }
        r6 = (int[]) r6;	 Catch:{ Throwable -> 0x00c5 }
    L_0x0ae8:
        r9 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.newArrayLiteral(r4, r6, r0, r9);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x0ad4;
    L_0x0af1:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0afd;
    L_0x0af7:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0afd:
        r9 = r9 + -1;
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.enterDotQuery(r4, r6);	 Catch:{ Throwable -> 0x00c5 }
        r5.scope = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b09:
        r4 = stack_boolean(r5, r9);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.updateDotQuery(r4, r6);	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == 0) goto L_0x0b27;
    L_0x0b15:
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.scope;	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.leaveDotQuery(r4);	 Catch:{ Throwable -> 0x00c5 }
        r5.scope = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 2;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b27:
        r9 = r9 + -1;
        goto L_0x0283;
    L_0x0b2b:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 != r0) goto L_0x0b37;
    L_0x0b31:
        r10 = r8[r9];	 Catch:{ Throwable -> 0x00c5 }
        r4 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r10);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0b37:
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.setDefaultNamespace(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b41:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 == r0) goto L_0x008e;
    L_0x0b47:
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.escapeAttributeValue(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b51:
        r4 = r7[r9];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r32;
        if (r4 == r0) goto L_0x008e;
    L_0x0b57:
        r0 = r40;
        r4 = org.mozilla.javascript.ScriptRuntime.escapeTextValue(r4, r0);	 Catch:{ Throwable -> 0x00c5 }
        r7[r9] = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b61:
        r4 = r5.debuggerFrame;	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == 0) goto L_0x008e;
    L_0x0b65:
        r4 = r5.debuggerFrame;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r4.onDebuggerStatement(r0);	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b6e:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r5.pcSourceLineStart = r4;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.debuggerFrame;	 Catch:{ Throwable -> 0x00c5 }
        if (r4 == 0) goto L_0x0b83;
    L_0x0b76:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getIndex(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.debuggerFrame;	 Catch:{ Throwable -> 0x00c5 }
        r0 = r40;
        r6.onLineChange(r0, r4);	 Catch:{ Throwable -> 0x00c5 }
    L_0x0b83:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 2;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0b8b:
        r16 = 0;
        goto L_0x008e;
    L_0x0b8f:
        r16 = 1;
        goto L_0x008e;
    L_0x0b93:
        r16 = 2;
        goto L_0x008e;
    L_0x0b97:
        r16 = 3;
        goto L_0x008e;
    L_0x0b9b:
        r16 = 4;
        goto L_0x008e;
    L_0x0b9f:
        r16 = 5;
        goto L_0x008e;
    L_0x0ba3:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
        r0 = r4 & 255;
        r16 = r0;
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bb3:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r16 = getIndex(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 2;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bc1:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r16 = getInt(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 + 4;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bcf:
        r4 = 0;
        r31 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bd4:
        r4 = 1;
        r31 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bd9:
        r4 = 2;
        r31 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0bde:
        r4 = 3;
        r31 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x008e;
    L_0x0be3:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r15[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4 & 255;
        r6 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00fb }
        r4 = r4 + 1;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00fb }
        r31 = r6;
        goto L_0x008e;
    L_0x0bf5:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getIndex(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00fb }
        r4 = r4 + 2;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00fb }
        r31 = r6;
        goto L_0x008e;
    L_0x0c07:
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = getInt(r15, r4);	 Catch:{ Throwable -> 0x00c5 }
        r6 = r37[r4];	 Catch:{ Throwable -> 0x00c5 }
        r4 = r5.pc;	 Catch:{ Throwable -> 0x00fb }
        r4 = r4 + 4;
        r5.pc = r4;	 Catch:{ Throwable -> 0x00fb }
        r31 = r6;
        goto L_0x008e;
    L_0x0c19:
        r4 = r5.idata;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4.longJumps;	 Catch:{ Throwable -> 0x00c5 }
        r6 = r5.pc;	 Catch:{ Throwable -> 0x00c5 }
        r4 = r4.getExistingInt(r6);	 Catch:{ Throwable -> 0x00c5 }
        r5.pc = r4;	 Catch:{ Throwable -> 0x00c5 }
        goto L_0x029a;
    L_0x0c27:
        r5 = r4;
        r4 = r27;
    L_0x0c2a:
        r0 = r40;
        r8 = r0.previousInterpreterInvocations;
        if (r8 == 0) goto L_0x0d40;
    L_0x0c30:
        r0 = r40;
        r8 = r0.previousInterpreterInvocations;
        r8 = r8.size();
        if (r8 == 0) goto L_0x0d40;
    L_0x0c3a:
        r0 = r40;
        r8 = r0.previousInterpreterInvocations;
        r8 = r8.pop();
        r0 = r40;
        r0.lastInterpreterFrame = r8;
    L_0x0c46:
        if (r4 == 0) goto L_0x0d4f;
    L_0x0c48:
        r5 = r4 instanceof java.lang.RuntimeException;
        if (r5 == 0) goto L_0x0d4c;
    L_0x0c4c:
        r4 = (java.lang.RuntimeException) r4;
        throw r4;
    L_0x0c4f:
        r10 = r5;
        r31 = r8;
        r19 = r9;
        r5 = r4;
        r8 = r6;
        goto L_0x0161;
    L_0x0c58:
        r4 = r5 instanceof org.mozilla.javascript.JavaScriptException;
        if (r4 == 0) goto L_0x0c64;
    L_0x0c5c:
        r4 = 2;
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0c64:
        r4 = r5 instanceof org.mozilla.javascript.EcmaError;
        if (r4 == 0) goto L_0x0c70;
    L_0x0c68:
        r4 = 2;
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0c70:
        r4 = r5 instanceof org.mozilla.javascript.EvaluatorException;
        if (r4 == 0) goto L_0x0c7c;
    L_0x0c74:
        r4 = 2;
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0c7c:
        r4 = r5 instanceof org.mozilla.javascript.ContinuationPending;
        if (r4 == 0) goto L_0x0c88;
    L_0x0c80:
        r4 = 0;
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0c88:
        r4 = r5 instanceof java.lang.RuntimeException;
        if (r4 == 0) goto L_0x0ca0;
    L_0x0c8c:
        r4 = 13;
        r0 = r40;
        r4 = r0.hasFeature(r4);
        if (r4 == 0) goto L_0x0c9e;
    L_0x0c96:
        r4 = 2;
    L_0x0c97:
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0c9e:
        r4 = 1;
        goto L_0x0c97;
    L_0x0ca0:
        r4 = r5 instanceof java.lang.Error;
        if (r4 == 0) goto L_0x0cb8;
    L_0x0ca4:
        r4 = 13;
        r0 = r40;
        r4 = r0.hasFeature(r4);
        if (r4 == 0) goto L_0x0cb6;
    L_0x0cae:
        r4 = 2;
    L_0x0caf:
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0cb6:
        r4 = 0;
        goto L_0x0caf;
    L_0x0cb8:
        r4 = r5 instanceof org.mozilla.javascript.Interpreter.ContinuationJump;
        if (r4 == 0) goto L_0x0cc2;
    L_0x0cbc:
        r6 = 1;
        r4 = r5;
        r4 = (org.mozilla.javascript.Interpreter.ContinuationJump) r4;
        goto L_0x017c;
    L_0x0cc2:
        r4 = 13;
        r0 = r40;
        r4 = r0.hasFeature(r4);
        if (r4 == 0) goto L_0x0cd4;
    L_0x0ccc:
        r4 = 2;
    L_0x0ccd:
        r38 = r6;
        r6 = r4;
        r4 = r38;
        goto L_0x017c;
    L_0x0cd4:
        r4 = 1;
        goto L_0x0ccd;
    L_0x0cd6:
        r6 = move-exception;
        r5 = 1;
        r7 = r5;
        r5 = r6;
        r6 = r4;
        goto L_0x0189;
    L_0x0cdd:
        r6 = move-exception;
        r4 = 0;
        r5 = 0;
        r7 = r5;
        r5 = r6;
        r6 = r4;
        goto L_0x0189;
    L_0x0ce5:
        r4 = move-exception;
        r6 = 0;
        r7 = 0;
        r5 = r4;
        r38 = r7;
        r7 = r6;
        r6 = r38;
        goto L_0x01a4;
    L_0x0cf0:
        r4 = 0;
        goto L_0x01aa;
    L_0x0cf3:
        r0 = r40;
        r1 = r19;
        exitFrame(r0, r1, r5);
        r0 = r19;
        r0 = r0.parentFrame;
        r19 = r0;
        if (r19 != 0) goto L_0x0d1d;
    L_0x0d02:
        if (r7 == 0) goto L_0x0d7f;
    L_0x0d04:
        r4 = r7.branchFrame;
        if (r4 == 0) goto L_0x0d0b;
    L_0x0d08:
        org.mozilla.javascript.Kit.codeBug();
    L_0x0d0b:
        r4 = r7.capturedFrame;
        if (r4 == 0) goto L_0x0d33;
    L_0x0d0f:
        r16 = -1;
        r28 = r8;
        r30 = r10;
        r6 = r31;
        r27 = r5;
        r5 = r19;
        goto L_0x0051;
    L_0x0d1d:
        if (r7 == 0) goto L_0x01a4;
    L_0x0d1f:
        r4 = r7.branchFrame;
        r0 = r19;
        if (r4 != r0) goto L_0x01a4;
    L_0x0d25:
        r16 = -1;
        r28 = r8;
        r30 = r10;
        r6 = r31;
        r27 = r5;
        r5 = r19;
        goto L_0x0051;
    L_0x0d33:
        r6 = r7.result;
        r4 = r7.resultDbl;
        r7 = 0;
        r38 = r4;
        r5 = r6;
        r4 = r7;
        r6 = r38;
        goto L_0x0c2a;
    L_0x0d40:
        r8 = 0;
        r0 = r40;
        r0.lastInterpreterFrame = r8;
        r8 = 0;
        r0 = r40;
        r0.previousInterpreterInvocations = r8;
        goto L_0x0c46;
    L_0x0d4c:
        r4 = (java.lang.Error) r4;
        throw r4;
    L_0x0d4f:
        r0 = r32;
        if (r5 != r0) goto L_0x014f;
    L_0x0d53:
        r5 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r6);
        goto L_0x014f;
    L_0x0d59:
        r8 = move-exception;
        r9 = r5;
        r5 = r4;
        r4 = r8;
        r8 = r31;
        goto L_0x00cd;
    L_0x0d61:
        r5 = move-exception;
        r9 = r8;
        r8 = r31;
        r38 = r4;
        r4 = r5;
        r5 = r38;
        goto L_0x00cd;
    L_0x0d6c:
        r4 = move-exception;
        r8 = r6;
        r9 = r5;
        r6 = r28;
        r5 = r30;
        goto L_0x00cd;
    L_0x0d75:
        r6 = move-exception;
        r8 = r31;
        r9 = r5;
        r5 = r4;
        r4 = r6;
        r6 = r28;
        goto L_0x00cd;
    L_0x0d7f:
        r6 = r8;
        r4 = r5;
        r5 = r10;
        goto L_0x0c2a;
    L_0x0d84:
        r6 = r9;
        goto L_0x0ae8;
    L_0x0d87:
        r6 = r4;
        goto L_0x0a5a;
    L_0x0d8a:
        r10 = r4;
        goto L_0x093c;
    L_0x0d8d:
        r18 = r5;
        goto L_0x0637;
    L_0x0d91:
        r6 = r4;
        goto L_0x057d;
    L_0x0d94:
        r6 = r4;
        goto L_0x04df;
    L_0x0d97:
        r6 = r4;
        goto L_0x046b;
    L_0x0d9a:
        r6 = r4;
        goto L_0x0410;
    L_0x0d9d:
        r10 = r4;
        goto L_0x03e0;
    L_0x0da0:
        r9 = r4;
        goto L_0x0283;
    L_0x0da3:
        r12 = r16;
        goto L_0x086e;
    L_0x0da7:
        r13 = r16;
        goto L_0x0857;
    L_0x0dab:
        r13 = r16;
        goto L_0x0840;
    L_0x0daf:
        r38 = r6;
        r6 = r7;
        r7 = r38;
        goto L_0x01a4;
    L_0x0db6:
        r7 = r6;
        r6 = r4;
        goto L_0x0189;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.Interpreter.interpretLoop(org.mozilla.javascript.Context, org.mozilla.javascript.Interpreter$CallFrame, java.lang.Object):java.lang.Object");
    }

    private static boolean isFrameEnterExitRequired(CallFrame callFrame) {
        return callFrame.debuggerFrame != null || callFrame.idata.itsNeedsActivation;
    }

    private static CallFrame processThrowable(Context context, Object obj, CallFrame callFrame, int i, boolean z) {
        int i2;
        if (i >= 0) {
            if (callFrame.frozen) {
                callFrame = callFrame.cloneFrozen();
            }
            int[] iArr = callFrame.idata.itsExceptionTable;
            callFrame.pc = iArr[i + EXCEPTION_HANDLER_SLOT];
            if (z) {
                callFrame.pcPrevBranch = callFrame.pc;
            }
            callFrame.savedStackTop = callFrame.emptyStackTop;
            i2 = callFrame.localShift + iArr[i + EXCEPTION_LOCAL_SLOT];
            callFrame.scope = (Scriptable) callFrame.stack[callFrame.localShift + iArr[i + EXCEPTION_SCOPE_SLOT]];
            callFrame.stack[i2] = obj;
        } else {
            ContinuationJump continuationJump = (ContinuationJump) obj;
            if (continuationJump.branchFrame != callFrame) {
                Kit.codeBug();
            }
            if (continuationJump.capturedFrame == null) {
                Kit.codeBug();
            }
            int i3 = continuationJump.capturedFrame.frameIndex + EXCEPTION_TRY_END_SLOT;
            if (continuationJump.branchFrame != null) {
                i3 -= continuationJump.branchFrame.frameIndex;
            }
            CallFrame callFrame2 = continuationJump.capturedFrame;
            i2 = EXCEPTION_TRY_START_SLOT;
            CallFrame[] callFrameArr = null;
            for (int i4 = EXCEPTION_TRY_START_SLOT; i4 != i3; i4 += EXCEPTION_TRY_END_SLOT) {
                if (!callFrame2.frozen) {
                    Kit.codeBug();
                }
                if (isFrameEnterExitRequired(callFrame2)) {
                    if (callFrameArr == null) {
                        callFrameArr = new CallFrame[(i3 - i4)];
                    }
                    callFrameArr[i2] = callFrame2;
                    i2 += EXCEPTION_TRY_END_SLOT;
                }
                callFrame2 = callFrame2.parentFrame;
            }
            while (i2 != 0) {
                i2--;
                enterFrame(context, callFrameArr[i2], ScriptRuntime.emptyArgs, true);
            }
            callFrame = continuationJump.capturedFrame.cloneFrozen();
            setCallResult(callFrame, continuationJump.result, continuationJump.resultDbl);
        }
        callFrame.throwable = null;
        return callFrame;
    }

    public static Object restartContinuation(NativeContinuation nativeContinuation, Context context, Scriptable scriptable, Object[] objArr) {
        if (ScriptRuntime.hasTopCall(context)) {
            Object obj = objArr.length == 0 ? Undefined.instance : objArr[EXCEPTION_TRY_START_SLOT];
            if (((CallFrame) nativeContinuation.getImplementation()) == null) {
                return obj;
            }
            ContinuationJump continuationJump = new ContinuationJump(nativeContinuation, null);
            continuationJump.result = obj;
            return interpretLoop(context, null, continuationJump);
        }
        return ScriptRuntime.doTopCall(nativeContinuation, context, scriptable, null, objArr, context.isTopLevelStrict);
    }

    public static Object resumeGenerator(Context context, Scriptable scriptable, int i, Object obj, Object obj2) {
        CallFrame callFrame = (CallFrame) obj;
        GeneratorState generatorState = new GeneratorState(i, obj2);
        if (i == EXCEPTION_HANDLER_SLOT) {
            try {
                return interpretLoop(context, callFrame, generatorState);
            } catch (RuntimeException e) {
                if (e == obj2) {
                    return Undefined.instance;
                }
                throw e;
            }
        }
        Object interpretLoop = interpretLoop(context, callFrame, generatorState);
        if (generatorState.returnedException == null) {
            return interpretLoop;
        }
        throw generatorState.returnedException;
    }

    private static void setCallResult(CallFrame callFrame, Object obj, double d) {
        if (callFrame.savedCallOp == 38) {
            callFrame.stack[callFrame.savedStackTop] = obj;
            callFrame.sDbl[callFrame.savedStackTop] = d;
        } else if (callFrame.savedCallOp != 30) {
            Kit.codeBug();
        } else if (obj instanceof Scriptable) {
            callFrame.stack[callFrame.savedStackTop] = obj;
        }
        callFrame.savedCallOp = EXCEPTION_TRY_START_SLOT;
    }

    private static boolean stack_boolean(CallFrame callFrame, int i) {
        Boolean bool = callFrame.stack[i];
        if (bool == Boolean.TRUE) {
            return true;
        }
        if (bool == Boolean.FALSE) {
            return false;
        }
        double d;
        if (bool == UniqueTag.DOUBLE_MARK) {
            d = callFrame.sDbl[i];
            boolean z = (d != d || d == 0.0d) ? EXCEPTION_TRY_START_SLOT : true;
            return z;
        } else if (bool == null || bool == Undefined.instance) {
            return false;
        } else {
            if (!(bool instanceof Number)) {
                return bool instanceof Boolean ? bool.booleanValue() : ScriptRuntime.toBoolean(bool);
            } else {
                d = ((Number) bool).doubleValue();
                return d == d && d != 0.0d;
            }
        }
    }

    private static double stack_double(CallFrame callFrame, int i) {
        Object obj = callFrame.stack[i];
        return obj != UniqueTag.DOUBLE_MARK ? ScriptRuntime.toNumber(obj) : callFrame.sDbl[i];
    }

    private static int stack_int32(CallFrame callFrame, int i) {
        Object obj = callFrame.stack[i];
        return obj == UniqueTag.DOUBLE_MARK ? ScriptRuntime.toInt32(callFrame.sDbl[i]) : ScriptRuntime.toInt32(obj);
    }

    private static Object thawGenerator(CallFrame callFrame, int i, GeneratorState generatorState, int i2) {
        callFrame.frozen = false;
        int index = getIndex(callFrame.idata.itsICode, callFrame.pc);
        callFrame.pc += EXCEPTION_HANDLER_SLOT;
        if (generatorState.operation == EXCEPTION_TRY_END_SLOT) {
            return new JavaScriptException(generatorState.value, callFrame.idata.itsSourceFile, index);
        }
        if (generatorState.operation == EXCEPTION_HANDLER_SLOT) {
            return generatorState.value;
        }
        if (generatorState.operation != 0) {
            throw Kit.codeBug();
        }
        if (i2 == 73) {
            callFrame.stack[i] = generatorState.value;
        }
        return Scriptable.NOT_FOUND;
    }

    public void captureStackInfo(RhinoException rhinoException) {
        int i = EXCEPTION_TRY_START_SLOT;
        Context currentContext = Context.getCurrentContext();
        if (currentContext == null || currentContext.lastInterpreterFrame == null) {
            rhinoException.interpreterStackInfo = null;
            rhinoException.interpreterLineData = null;
            return;
        }
        Object obj;
        int size;
        if (currentContext.previousInterpreterInvocations == null || currentContext.previousInterpreterInvocations.size() == 0) {
            obj = new CallFrame[EXCEPTION_TRY_END_SLOT];
        } else {
            size = currentContext.previousInterpreterInvocations.size();
            if (currentContext.previousInterpreterInvocations.peek() == currentContext.lastInterpreterFrame) {
                size--;
            }
            Object obj2 = new CallFrame[(size + EXCEPTION_TRY_END_SLOT)];
            currentContext.previousInterpreterInvocations.toArray(obj2);
            obj = obj2;
        }
        obj[obj.length - 1] = (CallFrame) currentContext.lastInterpreterFrame;
        for (size = EXCEPTION_TRY_START_SLOT; size != obj.length; size += EXCEPTION_TRY_END_SLOT) {
            i += obj[size].frameIndex + EXCEPTION_TRY_END_SLOT;
        }
        int[] iArr = new int[i];
        size = obj.length;
        while (size != 0) {
            int i2 = size - 1;
            for (CallFrame callFrame = obj[i2]; callFrame != null; callFrame = callFrame.parentFrame) {
                i--;
                iArr[i] = callFrame.pcSourceLineStart;
            }
            size = i2;
        }
        if (i != 0) {
            Kit.codeBug();
        }
        rhinoException.interpreterStackInfo = obj;
        rhinoException.interpreterLineData = iArr;
    }

    public Object compile(CompilerEnvirons compilerEnvirons, ScriptNode scriptNode, String str, boolean z) {
        this.itsData = new CodeGenerator().compile(compilerEnvirons, scriptNode, str, z);
        return this.itsData;
    }

    public Function createFunctionObject(Context context, Scriptable scriptable, Object obj, Object obj2) {
        if (obj != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createFunction(context, scriptable, this.itsData, obj2);
    }

    public Script createScriptObject(Object obj, Object obj2) {
        if (obj != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createScript(this.itsData, obj2);
    }

    public String getPatchedStack(RhinoException rhinoException, String str) {
        String str2 = "org.mozilla.javascript.Interpreter.interpretLoop";
        StringBuilder stringBuilder = new StringBuilder(str.length() + 1000);
        String systemProperty = SecurityUtilities.getSystemProperty("line.separator");
        CallFrame[] callFrameArr = (CallFrame[]) rhinoException.interpreterStackInfo;
        int[] iArr = rhinoException.interpreterLineData;
        int length = callFrameArr.length;
        int length2 = iArr.length;
        int i = length;
        length = EXCEPTION_TRY_START_SLOT;
        while (i != 0) {
            int i2 = i - 1;
            i = str.indexOf(str2, length);
            if (i < 0) {
                break;
            }
            i += str2.length();
            while (i != str.length()) {
                char charAt = str.charAt(i);
                if (charAt == '\n' || charAt == '\r') {
                    break;
                }
                i += EXCEPTION_TRY_END_SLOT;
            }
            stringBuilder.append(str.substring(length, i));
            for (CallFrame callFrame = callFrameArr[i2]; callFrame != null; callFrame = callFrame.parentFrame) {
                if (length2 == 0) {
                    Kit.codeBug();
                }
                length2--;
                InterpreterData interpreterData = callFrame.idata;
                stringBuilder.append(systemProperty);
                stringBuilder.append("\tat script");
                if (!(interpreterData.itsName == null || interpreterData.itsName.length() == 0)) {
                    stringBuilder.append('.');
                    stringBuilder.append(interpreterData.itsName);
                }
                stringBuilder.append('(');
                stringBuilder.append(interpreterData.itsSourceFile);
                int i3 = iArr[length2];
                if (i3 >= 0) {
                    stringBuilder.append(':');
                    stringBuilder.append(getIndex(interpreterData.itsICode, i3));
                }
                stringBuilder.append(')');
            }
            length = i;
            i = i2;
        }
        stringBuilder.append(str.substring(length));
        return stringBuilder.toString();
    }

    public List<String> getScriptStack(RhinoException rhinoException) {
        ScriptStackElement[][] scriptStackElements = getScriptStackElements(rhinoException);
        List<String> arrayList = new ArrayList(scriptStackElements.length);
        String systemProperty = SecurityUtilities.getSystemProperty("line.separator");
        int length = scriptStackElements.length;
        for (int i = EXCEPTION_TRY_START_SLOT; i < length; i += EXCEPTION_TRY_END_SLOT) {
            ScriptStackElement[] scriptStackElementArr = scriptStackElements[i];
            StringBuilder stringBuilder = new StringBuilder();
            int length2 = scriptStackElementArr.length;
            for (int i2 = EXCEPTION_TRY_START_SLOT; i2 < length2; i2 += EXCEPTION_TRY_END_SLOT) {
                scriptStackElementArr[i2].renderJavaStyle(stringBuilder);
                stringBuilder.append(systemProperty);
            }
            arrayList.add(stringBuilder.toString());
        }
        return arrayList;
    }

    public ScriptStackElement[][] getScriptStackElements(RhinoException rhinoException) {
        if (rhinoException.interpreterStackInfo == null) {
            return (ScriptStackElement[][]) EXCEPTION_TRY_START_SLOT;
        }
        List arrayList = new ArrayList();
        CallFrame[] callFrameArr = (CallFrame[]) rhinoException.interpreterStackInfo;
        int[] iArr = rhinoException.interpreterLineData;
        int length = callFrameArr.length;
        int length2 = iArr.length;
        while (length != 0) {
            int i = length - 1;
            CallFrame callFrame = callFrameArr[i];
            List arrayList2 = new ArrayList();
            CallFrame callFrame2 = callFrame;
            length = length2;
            while (callFrame2 != null) {
                if (length == 0) {
                    Kit.codeBug();
                }
                int i2 = length - 1;
                InterpreterData interpreterData = callFrame2.idata;
                String str = interpreterData.itsSourceFile;
                length = -1;
                int i3 = iArr[i2];
                if (i3 >= 0) {
                    length = getIndex(interpreterData.itsICode, i3);
                }
                String str2 = (interpreterData.itsName == null || interpreterData.itsName.length() == 0) ? null : interpreterData.itsName;
                callFrame2 = callFrame2.parentFrame;
                arrayList2.add(new ScriptStackElement(str, str2, length));
                length = i2;
            }
            arrayList.add(arrayList2.toArray(new ScriptStackElement[arrayList2.size()]));
            length2 = length;
            length = i;
        }
        return (ScriptStackElement[][]) arrayList.toArray(new ScriptStackElement[arrayList.size()][]);
    }

    public String getSourcePositionFromStack(Context context, int[] iArr) {
        CallFrame callFrame = (CallFrame) context.lastInterpreterFrame;
        InterpreterData interpreterData = callFrame.idata;
        if (callFrame.pcSourceLineStart >= 0) {
            iArr[EXCEPTION_TRY_START_SLOT] = getIndex(interpreterData.itsICode, callFrame.pcSourceLineStart);
        } else {
            iArr[EXCEPTION_TRY_START_SLOT] = EXCEPTION_TRY_START_SLOT;
        }
        return interpreterData.itsSourceFile;
    }

    public void setEvalScriptFlag(Script script) {
        ((InterpretedFunction) script).idata.evalScriptFlag = true;
    }
}
