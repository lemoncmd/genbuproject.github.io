package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Token;

public class XmlLiteral extends AstNode {
    private List<XmlFragment> fragments;

    public XmlLiteral() {
        this.fragments = new ArrayList();
        this.type = Token.XML;
    }

    public XmlLiteral(int i) {
        super(i);
        this.fragments = new ArrayList();
        this.type = Token.XML;
    }

    public XmlLiteral(int i, int i2) {
        super(i, i2);
        this.fragments = new ArrayList();
        this.type = Token.XML;
    }

    public void addFragment(XmlFragment xmlFragment) {
        assertNotNull(xmlFragment);
        this.fragments.add(xmlFragment);
        xmlFragment.setParent(this);
    }

    public List<XmlFragment> getFragments() {
        return this.fragments;
    }

    public void setFragments(List<XmlFragment> list) {
        assertNotNull(list);
        this.fragments.clear();
        for (XmlFragment addFragment : list) {
            addFragment(addFragment);
        }
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder(250);
        for (XmlFragment toSource : this.fragments) {
            stringBuilder.append(toSource.toSource(0));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            for (XmlFragment visit : this.fragments) {
                visit.visit(nodeVisitor);
            }
        }
    }
}
