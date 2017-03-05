package org.mozilla.javascript.ast;

import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class Label extends Jump {
    private String name;

    public Label() {
        this.type = Token.LABEL;
    }

    public Label(int i) {
        this(i, -1);
    }

    public Label(int i, int i2) {
        this.type = Token.LABEL;
        this.position = i;
        this.length = i2;
    }

    public Label(int i, int i2, String str) {
        this(i, i2);
        setName(str);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        String trim = str == null ? null : str.trim();
        if (trim == null || BuildConfig.FLAVOR.equals(trim)) {
            throw new IllegalArgumentException("invalid label name");
        }
        this.name = trim;
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append(this.name);
        stringBuilder.append(":\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }
}
