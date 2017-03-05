package org.simpleframework.xml.stream;

final class ProviderFactory {
    ProviderFactory() {
    }

    public static Provider getInstance() {
        try {
            return new StreamProvider();
        } catch (Throwable th) {
            return new DocumentProvider();
        }
    }
}
