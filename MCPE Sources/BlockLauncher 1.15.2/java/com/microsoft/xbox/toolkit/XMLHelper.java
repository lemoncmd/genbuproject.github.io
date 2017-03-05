package com.microsoft.xbox.toolkit;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

public class XMLHelper {
    private static final int XML_WAIT_TIMEOUT_MS = 1000;
    private static XMLHelper instance = new XMLHelper();
    private Serializer serializer;

    private XMLHelper() {
        this.serializer = null;
        this.serializer = new Persister(new AnnotationStrategy());
    }

    public static XMLHelper instance() {
        return instance;
    }

    public <T> T load(InputStream inputStream, Class<T> cls) throws XLEException {
        ClassLoader contextClassLoader;
        if (ThreadManager.UIThread != Thread.currentThread()) {
            BackgroundThreadWaitor.getInstance().waitForReady(XML_WAIT_TIMEOUT_MS);
        }
        TimeMonitor timeMonitor = new TimeMonitor();
        try {
            contextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cls.getClassLoader());
            T read = this.serializer.read((Class) cls, inputStream, false);
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            return read;
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    public <T> String save(T t) throws XLEException {
        TimeMonitor timeMonitor = new TimeMonitor();
        Writer stringWriter = new StringWriter();
        try {
            this.serializer.write((Object) t, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }

    public <T> void save(T t, OutputStream outputStream) throws XLEException {
        TimeMonitor timeMonitor = new TimeMonitor();
        try {
            this.serializer.write((Object) t, outputStream);
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }
}
