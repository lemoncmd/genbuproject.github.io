package Microsoft.Telemetry.Extensions;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public enum TracingEventLevel {
    None(0),
    Critical(1),
    Error(2),
    Informational(3),
    LogAlways(4),
    Verbose(5),
    Warning(6),
    __INVALID_ENUM_VALUE(7);
    
    private final int value;

    private TracingEventLevel(int i) {
        this.value = i;
    }

    public static TracingEventLevel fromValue(int i) {
        switch (i) {
            case NativeRegExp.TEST /*0*/:
                return None;
            case NativeRegExp.MATCH /*1*/:
                return Critical;
            case NativeRegExp.PREFIX /*2*/:
                return Error;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return Informational;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return LogAlways;
            case Token.GOTO /*5*/:
                return Verbose;
            case Token.IFEQ /*6*/:
                return Warning;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }

    public int getValue() {
        return this.value;
    }
}
