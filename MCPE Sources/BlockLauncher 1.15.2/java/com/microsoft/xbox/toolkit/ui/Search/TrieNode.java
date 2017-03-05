package com.microsoft.xbox.toolkit.ui.Search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class TrieNode {
    public boolean IsWord;
    public Hashtable<Character, TrieNode> MoreNodes = new Hashtable(26);
    public List<String> Words = new ArrayList();

    public void accept(ITrieNodeVisitor iTrieNodeVisitor) {
        if (iTrieNodeVisitor != null) {
            iTrieNodeVisitor.visit(this);
        }
        if (this.MoreNodes != null) {
            Enumeration keys = this.MoreNodes.keys();
            while (keys.hasMoreElements()) {
                ((TrieNode) this.MoreNodes.get(Character.valueOf(((Character) keys.nextElement()).charValue()))).accept(iTrieNodeVisitor);
            }
        }
    }
}
