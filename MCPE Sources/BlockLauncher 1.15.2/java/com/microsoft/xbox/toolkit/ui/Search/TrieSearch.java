package com.microsoft.xbox.toolkit.ui.Search;

import com.microsoft.xbox.toolkit.JavaUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import net.hockeyapp.android.BuildConfig;

public class TrieSearch {
    private static String ComponentName = TrieSearch.class.getName();
    private static int DefaultTrieDepth = 4;
    public TrieNode RootTrieNode;
    public int TrieDepth;
    public Hashtable<String, List<Object>> WordsDictionary;

    public TrieSearch() {
        this.WordsDictionary = new Hashtable();
        this.RootTrieNode = new TrieNode();
        this.TrieDepth = DefaultTrieDepth;
    }

    public TrieSearch(int i) {
        this.WordsDictionary = new Hashtable();
        this.RootTrieNode = new TrieNode();
        this.TrieDepth = i;
    }

    public static int findWordIndex(String str, String str2) {
        if (JavaUtil.isNullOrEmpty(str) || JavaUtil.isNullOrEmpty(str2)) {
            return -1;
        }
        int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
        while (indexOf != -1 && indexOf != 0 && !isNullOrWhitespace(str.substring(indexOf - 1, indexOf))) {
            indexOf = str.toLowerCase().indexOf(str2.toLowerCase(), indexOf + 1);
        }
        return indexOf;
    }

    public static List<String> getRemainingWordMatches(TrieNode trieNode, int i, String str) {
        List<String> arrayList = new ArrayList();
        if (!(trieNode == null || JavaUtil.isNullOrEmpty(str))) {
            if (trieNode.IsWord && str.length() <= i) {
                arrayList.add(str);
            }
            if (trieNode.MoreNodes != null) {
                Enumeration keys = trieNode.MoreNodes.keys();
                while (keys.hasMoreElements()) {
                    char charValue = ((Character) keys.nextElement()).charValue();
                    arrayList.addAll(getRemainingWordMatches((TrieNode) trieNode.MoreNodes.get(Character.valueOf(charValue)), i, str + charValue));
                }
            }
            if (trieNode.Words != null) {
                for (String str2 : trieNode.Words) {
                    if (str2.toLowerCase().startsWith(str.toLowerCase())) {
                        arrayList.add(str2);
                    }
                }
            }
        }
        return arrayList;
    }

    public static TrieNode getTrieNodes(Hashtable<String, List<Object>> hashtable, int i) {
        if (hashtable == null) {
            return null;
        }
        TrieNode trieNode = new TrieNode();
        Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            int i2 = 0;
            TrieNode trieNode2 = trieNode;
            while (i2 < str.length() && i2 <= i) {
                char charAt = str.charAt(i2);
                if (trieNode2.MoreNodes == null) {
                    trieNode2.MoreNodes = new Hashtable(26);
                }
                if (!trieNode2.MoreNodes.containsKey(Character.valueOf(charAt))) {
                    trieNode2.MoreNodes.put(Character.valueOf(charAt), new TrieNode());
                }
                trieNode2 = (TrieNode) trieNode2.MoreNodes.get(Character.valueOf(charAt));
                i2++;
            }
            if (i2 > i) {
                if (trieNode2.Words == null) {
                    trieNode2.Words = new ArrayList();
                }
                trieNode2.Words.add(str);
            }
            if (i2 == str.length()) {
                trieNode2.IsWord = true;
            }
        }
        return trieNode;
    }

    public static List<String> getWordMatches(TrieNode trieNode, int i, String str) {
        Object obj = null;
        List arrayList = new ArrayList();
        if (!JavaUtil.isNullOrEmpty(str)) {
            String str2 = BuildConfig.FLAVOR;
            String toUpperCase = str.toUpperCase();
            int i2 = 0;
            while (i2 < toUpperCase.length() && i2 <= i) {
                char charAt = toUpperCase.charAt(i2);
                String str3 = str2 + charAt;
                if (trieNode.MoreNodes == null || !trieNode.MoreNodes.containsKey(Character.valueOf(charAt))) {
                    str2 = str3;
                    break;
                }
                i2++;
                trieNode = (TrieNode) trieNode.MoreNodes.get(Character.valueOf(charAt));
                str2 = str3;
            }
            int i3 = 1;
            if (i2 > i) {
                if (trieNode.Words != null) {
                    for (String str22 : trieNode.Words) {
                        if (str22.toLowerCase().startsWith(str.toLowerCase())) {
                            arrayList.add(str22);
                        }
                    }
                }
            } else if (obj != null) {
                arrayList.addAll(getRemainingWordMatches(trieNode, i, str22));
            }
        }
        return arrayList;
    }

    public static Hashtable<String, List<Object>> getWordsDictionary(List<TrieInput> list) {
        Hashtable<String, List<Object>> hashtable = new Hashtable();
        if (list != null) {
            for (TrieInput trieInput : list) {
                String[] split = JavaUtil.isNullOrEmpty(trieInput.Text) ? new String[0] : trieInput.Text.split(" ");
                for (String findWordIndex : split) {
                    String findWordIndex2;
                    int findWordIndex3 = findWordIndex(trieInput.Text, findWordIndex2);
                    if (findWordIndex3 != -1) {
                        findWordIndex2 = trieInput.Text.substring(findWordIndex3).toUpperCase();
                        if (!hashtable.containsKey(findWordIndex2)) {
                            List arrayList = new ArrayList();
                            arrayList.add(trieInput.Context);
                            hashtable.put(findWordIndex2, arrayList);
                        } else if (!((List) hashtable.get(findWordIndex2)).contains(trieInput.Context)) {
                            ((List) hashtable.get(findWordIndex2)).add(trieInput.Context);
                        }
                    }
                }
            }
        }
        return hashtable;
    }

    private static boolean isNullOrWhitespace(String str) {
        return JavaUtil.isNullOrEmpty(str) || str.trim().isEmpty();
    }

    public void initialize(List<TrieInput> list) {
        this.WordsDictionary = getWordsDictionary(list);
        this.RootTrieNode = getTrieNodes(this.WordsDictionary, this.TrieDepth);
    }

    public List<String> search(String str) {
        return getWordMatches(this.RootTrieNode, this.TrieDepth, str);
    }
}
