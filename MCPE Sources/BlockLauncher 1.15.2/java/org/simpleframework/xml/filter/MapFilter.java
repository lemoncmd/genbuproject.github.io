package org.simpleframework.xml.filter;

import java.util.Map;

public class MapFilter implements Filter {
    private Filter filter;
    private Map map;

    public MapFilter(Map map) {
        this(map, null);
    }

    public MapFilter(Map map, Filter filter) {
        this.filter = filter;
        this.map = map;
    }

    public String replace(String str) {
        Object obj = this.map != null ? this.map.get(str) : null;
        return obj != null ? obj.toString() : this.filter != null ? this.filter.replace(str) : null;
    }
}
