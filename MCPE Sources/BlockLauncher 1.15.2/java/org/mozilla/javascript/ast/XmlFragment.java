package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public abstract class XmlFragment extends AstNode {
    public XmlFragment() {
        this.type = Token.XML;
    }

    public XmlFragment(int i) {
        super(i);
        this.type = Token.XML;
    }

    public XmlFragment(int i, int i2) {
        super(i, i2);
        this.type = Token.XML;
    }
}
