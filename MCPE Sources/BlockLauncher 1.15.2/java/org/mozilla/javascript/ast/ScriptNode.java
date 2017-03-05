package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class ScriptNode extends Scope {
    private List<FunctionNode> EMPTY_LIST;
    private Object compilerData;
    private String encodedSource;
    private int encodedSourceEnd;
    private int encodedSourceStart;
    private int endLineno;
    private List<FunctionNode> functions;
    private boolean inStrictMode;
    private boolean[] isConsts;
    private int paramCount;
    private List<RegExpLiteral> regexps;
    private String sourceName;
    private List<Symbol> symbols;
    private int tempNumber;
    private String[] variableNames;

    public ScriptNode() {
        this.encodedSourceStart = -1;
        this.encodedSourceEnd = -1;
        this.endLineno = -1;
        this.EMPTY_LIST = Collections.emptyList();
        this.symbols = new ArrayList(4);
        this.paramCount = 0;
        this.tempNumber = 0;
        this.top = this;
        this.type = Token.SCRIPT;
    }

    public ScriptNode(int i) {
        super(i);
        this.encodedSourceStart = -1;
        this.encodedSourceEnd = -1;
        this.endLineno = -1;
        this.EMPTY_LIST = Collections.emptyList();
        this.symbols = new ArrayList(4);
        this.paramCount = 0;
        this.tempNumber = 0;
        this.top = this;
        this.type = Token.SCRIPT;
    }

    public int addFunction(FunctionNode functionNode) {
        if (functionNode == null) {
            AstNode.codeBug();
        }
        if (this.functions == null) {
            this.functions = new ArrayList();
        }
        this.functions.add(functionNode);
        return this.functions.size() - 1;
    }

    public void addRegExp(RegExpLiteral regExpLiteral) {
        if (regExpLiteral == null) {
            AstNode.codeBug();
        }
        if (this.regexps == null) {
            this.regexps = new ArrayList();
        }
        this.regexps.add(regExpLiteral);
        regExpLiteral.putIntProp(4, this.regexps.size() - 1);
    }

    void addSymbol(Symbol symbol) {
        if (this.variableNames != null) {
            AstNode.codeBug();
        }
        if (symbol.getDeclType() == 88) {
            this.paramCount++;
        }
        this.symbols.add(symbol);
    }

    public void flattenSymbolTable(boolean z) {
        int i;
        Symbol symbol;
        if (!z) {
            List arrayList = new ArrayList();
            if (this.symbolTable != null) {
                for (i = 0; i < this.symbols.size(); i++) {
                    symbol = (Symbol) this.symbols.get(i);
                    if (symbol.getContainingTable() == this) {
                        arrayList.add(symbol);
                    }
                }
            }
            this.symbols = arrayList;
        }
        this.variableNames = new String[this.symbols.size()];
        this.isConsts = new boolean[this.symbols.size()];
        for (i = 0; i < this.symbols.size(); i++) {
            symbol = (Symbol) this.symbols.get(i);
            this.variableNames[i] = symbol.getName();
            this.isConsts[i] = symbol.getDeclType() == Token.CONST;
            symbol.setIndex(i);
        }
    }

    public int getBaseLineno() {
        return this.lineno;
    }

    public Object getCompilerData() {
        return this.compilerData;
    }

    public String getEncodedSource() {
        return this.encodedSource;
    }

    public int getEncodedSourceEnd() {
        return this.encodedSourceEnd;
    }

    public int getEncodedSourceStart() {
        return this.encodedSourceStart;
    }

    public int getEndLineno() {
        return this.endLineno;
    }

    public int getFunctionCount() {
        return this.functions == null ? 0 : this.functions.size();
    }

    public FunctionNode getFunctionNode(int i) {
        return (FunctionNode) this.functions.get(i);
    }

    public List<FunctionNode> getFunctions() {
        return this.functions == null ? this.EMPTY_LIST : this.functions;
    }

    public int getIndexForNameNode(Node node) {
        if (this.variableNames == null) {
            AstNode.codeBug();
        }
        Scope scope = node.getScope();
        Symbol symbol = scope == null ? null : scope.getSymbol(((Name) node).getIdentifier());
        return symbol == null ? -1 : symbol.getIndex();
    }

    public String getNextTempName() {
        StringBuilder append = new StringBuilder().append("$");
        int i = this.tempNumber;
        this.tempNumber = i + 1;
        return append.append(i).toString();
    }

    public boolean[] getParamAndVarConst() {
        if (this.variableNames == null) {
            AstNode.codeBug();
        }
        return this.isConsts;
    }

    public int getParamAndVarCount() {
        if (this.variableNames == null) {
            AstNode.codeBug();
        }
        return this.symbols.size();
    }

    public String[] getParamAndVarNames() {
        if (this.variableNames == null) {
            AstNode.codeBug();
        }
        return this.variableNames;
    }

    public int getParamCount() {
        return this.paramCount;
    }

    public String getParamOrVarName(int i) {
        if (this.variableNames == null) {
            AstNode.codeBug();
        }
        return this.variableNames[i];
    }

    public int getRegexpCount() {
        return this.regexps == null ? 0 : this.regexps.size();
    }

    public String getRegexpFlags(int i) {
        return ((RegExpLiteral) this.regexps.get(i)).getFlags();
    }

    public String getRegexpString(int i) {
        return ((RegExpLiteral) this.regexps.get(i)).getValue();
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public List<Symbol> getSymbols() {
        return this.symbols;
    }

    public boolean isInStrictMode() {
        return this.inStrictMode;
    }

    public void setBaseLineno(int i) {
        if (i < 0 || this.lineno >= 0) {
            AstNode.codeBug();
        }
        this.lineno = i;
    }

    public void setCompilerData(Object obj) {
        assertNotNull(obj);
        if (this.compilerData != null) {
            throw new IllegalStateException();
        }
        this.compilerData = obj;
    }

    public void setEncodedSource(String str) {
        this.encodedSource = str;
    }

    public void setEncodedSourceBounds(int i, int i2) {
        this.encodedSourceStart = i;
        this.encodedSourceEnd = i2;
    }

    public void setEncodedSourceEnd(int i) {
        this.encodedSourceEnd = i;
    }

    public void setEncodedSourceStart(int i) {
        this.encodedSourceStart = i;
    }

    public void setEndLineno(int i) {
        if (i < 0 || this.endLineno >= 0) {
            AstNode.codeBug();
        }
        this.endLineno = i;
    }

    public void setInStrictMode(boolean z) {
        this.inStrictMode = z;
    }

    public void setSourceName(String str) {
        this.sourceName = str;
    }

    public void setSymbols(List<Symbol> list) {
        this.symbols = list;
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            Iterator it = iterator();
            while (it.hasNext()) {
                ((AstNode) ((Node) it.next())).visit(nodeVisitor);
            }
        }
    }
}
