package org.simpleframework.xml.core;

import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class ScannerFactory {
    private final Cache<Scanner> cache = new ConcurrentCache();
    private final Support support;

    public ScannerFactory(Support support) {
        this.support = support;
    }

    public Scanner getInstance(Class cls) throws Exception {
        Scanner scanner = (Scanner) this.cache.fetch(cls);
        if (scanner == null) {
            Detail detail = this.support.getDetail(cls);
            if (this.support.isPrimitive(cls)) {
                scanner = new PrimitiveScanner(detail);
            } else {
                scanner = new ObjectScanner(detail, this.support);
                if (scanner.isPrimitive() && !this.support.isContainer(cls)) {
                    scanner = new DefaultScanner(detail, this.support);
                }
            }
            this.cache.cache(cls, scanner);
        }
        return scanner;
    }
}
