package org.mozilla.javascript;

class DefaultErrorReporter implements ErrorReporter {
    static final DefaultErrorReporter instance = new DefaultErrorReporter();
    private ErrorReporter chainedReporter;
    private boolean forEval;

    private DefaultErrorReporter() {
    }

    static ErrorReporter forEval(ErrorReporter errorReporter) {
        ErrorReporter defaultErrorReporter = new DefaultErrorReporter();
        defaultErrorReporter.forEval = true;
        defaultErrorReporter.chainedReporter = errorReporter;
        return defaultErrorReporter;
    }

    public void error(String str, String str2, int i, String str3, int i2) {
        if (this.forEval) {
            String substring;
            String str4 = "SyntaxError";
            if (str.startsWith("TypeError: ")) {
                str4 = "TypeError";
                substring = str.substring("TypeError: ".length());
            } else {
                substring = str;
            }
            throw ScriptRuntime.constructError(str4, substring, str2, i, str3, i2);
        } else if (this.chainedReporter != null) {
            this.chainedReporter.error(str, str2, i, str3, i2);
        } else {
            throw runtimeError(str, str2, i, str3, i2);
        }
    }

    public EvaluatorException runtimeError(String str, String str2, int i, String str3, int i2) {
        return this.chainedReporter != null ? this.chainedReporter.runtimeError(str, str2, i, str3, i2) : new EvaluatorException(str, str2, i, str3, i2);
    }

    public void warning(String str, String str2, int i, String str3, int i2) {
        if (this.chainedReporter != null) {
            this.chainedReporter.warning(str, str2, i, str3, i2);
        }
    }
}
