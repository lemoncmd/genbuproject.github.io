package org.simpleframework.xml.transform;

import java.util.Date;

class DateTransform<T extends Date> implements Transform<T> {
    private final DateFactory<T> factory;

    public DateTransform(Class<T> cls) throws Exception {
        this.factory = new DateFactory(cls);
    }

    public T read(String str) throws Exception {
        T instance;
        synchronized (this) {
            Long valueOf = Long.valueOf(DateType.getDate(str).getTime());
            instance = this.factory.getInstance(valueOf);
        }
        return instance;
    }

    public String write(T t) throws Exception {
        String text;
        synchronized (this) {
            text = DateType.getText(t);
        }
        return text;
    }
}
