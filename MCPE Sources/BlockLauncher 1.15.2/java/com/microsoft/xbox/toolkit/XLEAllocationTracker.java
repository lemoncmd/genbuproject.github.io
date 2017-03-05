package com.microsoft.xbox.toolkit;

import java.util.HashMap;

public class XLEAllocationTracker {
    private static XLEAllocationTracker instance = new XLEAllocationTracker();
    private HashMap<String, HashMap<String, Integer>> adapterCounter = new HashMap();

    public static XLEAllocationTracker getInstance() {
        return instance;
    }

    private HashMap<String, Integer> getTagHash(String str) {
        if (!this.adapterCounter.containsKey(str)) {
            this.adapterCounter.put(str, new HashMap());
        }
        return (HashMap) this.adapterCounter.get(str);
    }

    public void debugDecrement(String str, String str2) {
    }

    public int debugGetOverallocatedCount(String str) {
        return 0;
    }

    public int debugGetTotalCount(String str) {
        return 0;
    }

    public void debugIncrement(String str, String str2) {
    }

    public void debugPrintOverallocated(String str) {
    }
}
