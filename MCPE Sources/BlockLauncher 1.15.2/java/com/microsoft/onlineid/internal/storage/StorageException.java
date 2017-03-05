package com.microsoft.onlineid.internal.storage;

public class StorageException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public StorageException(String str) {
        super(str);
    }

    public StorageException(String str, Throwable th) {
        super(str, th);
    }

    public StorageException(Throwable th) {
        super(th);
    }
}
