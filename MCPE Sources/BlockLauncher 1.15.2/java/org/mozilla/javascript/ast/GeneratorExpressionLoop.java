package org.mozilla.javascript.ast;

import net.hockeyapp.android.BuildConfig;

public class GeneratorExpressionLoop extends ForInLoop {
    public GeneratorExpressionLoop(int i) {
        super(i);
    }

    public GeneratorExpressionLoop(int i, int i2) {
        super(i, i2);
    }

    public boolean isForEach() {
        return false;
    }

    public void setIsForEach(boolean z) {
        throw new UnsupportedOperationException("this node type does not support for each");
    }

    public String toSource(int i) {
        return makeIndent(i) + " for " + (isForEach() ? "each " : BuildConfig.FLAVOR) + "(" + this.iterator.toSource(0) + (isForOf() ? " of " : " in ") + this.iteratedObject.toSource(0) + ")";
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.iterator.visit(nodeVisitor);
            this.iteratedObject.visit(nodeVisitor);
        }
    }
}
