package com.microsoft.bond;

import java.io.IOException;

public class BondException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public BondException(IOException iOException) {
        super(iOException);
    }

    public BondException(String str) {
        super(str);
    }
}
