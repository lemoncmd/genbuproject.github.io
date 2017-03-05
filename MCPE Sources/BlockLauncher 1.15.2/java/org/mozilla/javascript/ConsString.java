package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayList;
import net.hockeyapp.android.BuildConfig;

public class ConsString implements Serializable, CharSequence {
    private static final long serialVersionUID = -8432806714471372570L;
    private int depth = 1;
    private final int length;
    private CharSequence s1;
    private CharSequence s2;

    public ConsString(CharSequence charSequence, CharSequence charSequence2) {
        this.s1 = charSequence;
        this.s2 = charSequence2;
        this.length = charSequence.length() + charSequence2.length();
        if (charSequence instanceof ConsString) {
            this.depth += ((ConsString) charSequence).depth;
        }
        if (charSequence2 instanceof ConsString) {
            this.depth += ((ConsString) charSequence2).depth;
        }
    }

    private synchronized String flatten() {
        if (this.depth > 0) {
            StringBuilder stringBuilder = new StringBuilder(this.length);
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.s2);
            arrayList.add(this.s1);
            while (!arrayList.isEmpty()) {
                CharSequence charSequence = (CharSequence) arrayList.remove(arrayList.size() - 1);
                if (charSequence instanceof ConsString) {
                    ConsString consString = (ConsString) charSequence;
                    arrayList.add(consString.s2);
                    arrayList.add(consString.s1);
                } else {
                    stringBuilder.append(charSequence);
                }
            }
            this.s1 = stringBuilder.toString();
            this.s2 = BuildConfig.FLAVOR;
            this.depth = 0;
        }
        return (String) this.s1;
    }

    private Object writeReplace() {
        return toString();
    }

    public char charAt(int i) {
        return (this.depth == 0 ? (String) this.s1 : flatten()).charAt(i);
    }

    public int length() {
        return this.length;
    }

    public CharSequence subSequence(int i, int i2) {
        return (this.depth == 0 ? (String) this.s1 : flatten()).substring(i, i2);
    }

    public String toString() {
        return this.depth == 0 ? (String) this.s1 : flatten();
    }
}
