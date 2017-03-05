package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class PathParser implements Expression {
    protected boolean attribute;
    protected Cache<String> attributes = new ConcurrentCache();
    protected StringBuilder builder = new StringBuilder();
    protected String cache;
    protected int count;
    protected char[] data;
    protected Cache<String> elements = new ConcurrentCache();
    protected List<Integer> indexes = new ArrayList();
    protected String location;
    protected List<String> names = new ArrayList();
    protected int off;
    protected String path;
    protected List<String> prefixes = new ArrayList();
    protected int start;
    protected Style style;
    protected Type type;

    private class PathSection implements Expression {
        private int begin;
        private List<String> cache = new ArrayList();
        private int end;
        private String path;
        private String section;

        public PathSection(int i, int i2) {
            this.begin = i;
            this.end = i2;
        }

        private String getCanonicalPath() {
            int i = 0;
            int i2 = 0;
            while (i < this.begin) {
                i2 = PathParser.this.location.indexOf(47, i2 + 1);
                i++;
            }
            i = i2;
            for (int i3 = i; i3 <= this.end; i3++) {
                i = PathParser.this.location.indexOf(47, i + 1);
                if (i == -1) {
                    i = PathParser.this.location.length();
                }
            }
            return PathParser.this.location.substring(i2 + 1, i);
        }

        private String getFragment() {
            int i = 0;
            int i2 = 0;
            int i3 = PathParser.this.start;
            while (i <= this.end) {
                if (i3 >= PathParser.this.count) {
                    i3++;
                    break;
                }
                int i4 = i3 + 1;
                if (PathParser.this.data[i3] == '/') {
                    i++;
                    if (i == this.begin) {
                        i2 = i4;
                        i3 = i4;
                    }
                }
                i3 = i4;
            }
            return new String(PathParser.this.data, i2, (i3 - 1) - i2);
        }

        public String getAttribute(String str) {
            String path = getPath();
            return path != null ? PathParser.this.getAttributePath(path, str) : str;
        }

        public String getElement(String str) {
            String path = getPath();
            return path != null ? PathParser.this.getElementPath(path, str) : str;
        }

        public String getFirst() {
            return (String) PathParser.this.names.get(this.begin);
        }

        public int getIndex() {
            return ((Integer) PathParser.this.indexes.get(this.begin)).intValue();
        }

        public String getLast() {
            return (String) PathParser.this.names.get(this.end);
        }

        public String getPath() {
            if (this.section == null) {
                this.section = getCanonicalPath();
            }
            return this.section;
        }

        public Expression getPath(int i) {
            return getPath(i, 0);
        }

        public Expression getPath(int i, int i2) {
            return new PathSection(this.begin + i, this.end - i2);
        }

        public String getPrefix() {
            return (String) PathParser.this.prefixes.get(this.begin);
        }

        public boolean isAttribute() {
            return PathParser.this.attribute && this.end >= PathParser.this.names.size() - 1;
        }

        public boolean isEmpty() {
            return this.begin == this.end;
        }

        public boolean isPath() {
            return this.end - this.begin >= 1;
        }

        public Iterator<String> iterator() {
            if (this.cache.isEmpty()) {
                for (int i = this.begin; i <= this.end; i++) {
                    String str = (String) PathParser.this.names.get(i);
                    if (str != null) {
                        this.cache.add(str);
                    }
                }
            }
            return this.cache.iterator();
        }

        public String toString() {
            if (this.path == null) {
                this.path = getFragment();
            }
            return this.path;
        }
    }

    public PathParser(String str, Type type, Format format) throws Exception {
        this.style = format.getStyle();
        this.type = type;
        this.path = str;
        parse(str);
    }

    private void align() throws Exception {
        if (this.names.size() > this.indexes.size()) {
            this.indexes.add(Integer.valueOf(1));
        }
    }

    private void attribute() throws Exception {
        int i = this.off + 1;
        this.off = i;
        while (this.off < this.count) {
            char[] cArr = this.data;
            int i2 = this.off;
            this.off = i2 + 1;
            if (!isValid(cArr[i2])) {
                throw new PathException("Illegal character '%s' in attribute for '%s' in %s", Character.valueOf(cArr[i2]), this.path, this.type);
            }
        }
        if (this.off <= i) {
            throw new PathException("Attribute reference in '%s' for %s is empty", this.path, this.type);
        }
        this.attribute = true;
        attribute(i, this.off - i);
    }

    private void attribute(int i, int i2) {
        String str = new String(this.data, i, i2);
        if (i2 > 0) {
            attribute(str);
        }
    }

    private void attribute(String str) {
        String attribute = this.style.getAttribute(str);
        this.prefixes.add(null);
        this.names.add(attribute);
    }

    private void build() {
        int size = this.names.size();
        int i = size - 1;
        int i2 = 0;
        while (i2 < size) {
            String str = (String) this.prefixes.get(i2);
            String str2 = (String) this.names.get(i2);
            int intValue = ((Integer) this.indexes.get(i2)).intValue();
            if (i2 > 0) {
                this.builder.append('/');
            }
            if (this.attribute && i2 == i) {
                this.builder.append('@');
                this.builder.append(str2);
            } else {
                if (str != null) {
                    this.builder.append(str);
                    this.builder.append(':');
                }
                this.builder.append(str2);
                this.builder.append('[');
                this.builder.append(intValue);
                this.builder.append(']');
            }
            i2++;
        }
        this.location = this.builder.toString();
    }

    private void element() throws Exception {
        int i = this.off;
        int i2 = 0;
        while (this.off < this.count) {
            char[] cArr = this.data;
            int i3 = this.off;
            this.off = i3 + 1;
            char c = cArr[i3];
            if (isValid(c)) {
                i2++;
            } else {
                if (c == '@') {
                    this.off--;
                } else if (c == '[') {
                    index();
                } else if (c != '/') {
                    throw new PathException("Illegal character '%s' in element for '%s' in %s", Character.valueOf(c), this.path, this.type);
                }
                element(i, i2);
            }
        }
        element(i, i2);
    }

    private void element(int i, int i2) {
        String str = new String(this.data, i, i2);
        if (i2 > 0) {
            element(str);
        }
    }

    private void element(String str) {
        int indexOf = str.indexOf(58);
        Object obj = null;
        if (indexOf > 0) {
            obj = str.substring(0, indexOf);
            str = str.substring(indexOf + 1);
        }
        String element = this.style.getElement(str);
        this.prefixes.add(obj);
        this.names.add(element);
    }

    private void index() throws Exception {
        int i;
        char[] cArr;
        int i2;
        if (this.data[this.off - 1] == '[') {
            i = 0;
            while (this.off < this.count) {
                cArr = this.data;
                i2 = this.off;
                this.off = i2 + 1;
                char c = cArr[i2];
                if (!isDigit(c)) {
                    break;
                }
                i = ((i * 10) + c) - 48;
            }
        } else {
            i = 0;
        }
        cArr = this.data;
        i2 = this.off;
        this.off = i2 + 1;
        if (cArr[i2 - 1] != ']') {
            throw new PathException("Invalid index for path '%s' in %s", this.path, this.type);
        } else {
            this.indexes.add(Integer.valueOf(i));
        }
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private boolean isLetter(char c) {
        return Character.isLetterOrDigit(c);
    }

    private boolean isSpecial(char c) {
        return c == '_' || c == '-' || c == ':';
    }

    private boolean isValid(char c) {
        return isLetter(c) || isSpecial(c);
    }

    private void parse(String str) throws Exception {
        if (str != null) {
            this.count = str.length();
            this.data = new char[this.count];
            str.getChars(0, this.count, this.data, 0);
        }
        path();
    }

    private void path() throws Exception {
        if (this.data[this.off] == '/') {
            throw new PathException("Path '%s' in %s references document root", this.path, this.type);
        }
        if (this.data[this.off] == '.') {
            skip();
        }
        while (this.off < this.count) {
            if (this.attribute) {
                throw new PathException("Path '%s' in %s references an invalid attribute", this.path, this.type);
            }
            segment();
        }
        truncate();
        build();
    }

    private void segment() throws Exception {
        char c = this.data[this.off];
        if (c == '/') {
            throw new PathException("Invalid path expression '%s' in %s", this.path, this.type);
        }
        if (c == '@') {
            attribute();
        } else {
            element();
        }
        align();
    }

    private void skip() throws Exception {
        if (this.data.length > 1) {
            if (this.data[this.off + 1] != '/') {
                throw new PathException("Path '%s' in %s has an illegal syntax", this.path, this.type);
            }
            this.off++;
        }
        int i = this.off + 1;
        this.off = i;
        this.start = i;
    }

    private void truncate() throws Exception {
        if (this.off - 1 >= this.data.length) {
            this.off--;
        } else if (this.data[this.off - 1] == '/') {
            this.off--;
        }
    }

    public String getAttribute(String str) {
        if (isEmpty(this.location)) {
            return this.style.getAttribute(str);
        }
        String str2 = (String) this.attributes.fetch(str);
        if (str2 != null) {
            return str2;
        }
        str2 = getAttributePath(this.location, str);
        if (str2 == null) {
            return str2;
        }
        this.attributes.cache(str, str2);
        return str2;
    }

    protected String getAttributePath(String str, String str2) {
        String attribute = this.style.getAttribute(str2);
        return isEmpty(str) ? attribute : str + "/@" + attribute;
    }

    public String getElement(String str) {
        if (isEmpty(this.location)) {
            return this.style.getElement(str);
        }
        String str2 = (String) this.elements.fetch(str);
        if (str2 != null) {
            return str2;
        }
        str2 = getElementPath(this.location, str);
        if (str2 == null) {
            return str2;
        }
        this.elements.cache(str, str2);
        return str2;
    }

    protected String getElementPath(String str, String str2) {
        String element = this.style.getElement(str2);
        return isEmpty(element) ? str : isEmpty(str) ? element : str + "/" + element + "[1]";
    }

    public String getFirst() {
        return (String) this.names.get(0);
    }

    public int getIndex() {
        return ((Integer) this.indexes.get(0)).intValue();
    }

    public String getLast() {
        return (String) this.names.get(this.names.size() - 1);
    }

    public String getPath() {
        return this.location;
    }

    public Expression getPath(int i) {
        return getPath(i, 0);
    }

    public Expression getPath(int i, int i2) {
        int size = this.names.size() - 1;
        return size - i2 >= i ? new PathSection(i, size - i2) : new PathSection(i, i);
    }

    public String getPrefix() {
        return (String) this.prefixes.get(0);
    }

    public boolean isAttribute() {
        return this.attribute;
    }

    public boolean isEmpty() {
        return isEmpty(this.location);
    }

    public boolean isPath() {
        return this.names.size() > 1;
    }

    public Iterator<String> iterator() {
        return this.names.iterator();
    }

    public String toString() {
        int i = this.off - this.start;
        if (this.cache == null) {
            this.cache = new String(this.data, this.start, i);
        }
        return this.cache;
    }
}
