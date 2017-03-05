package com.microsoft.bond.io;

import java.io.IOException;

public interface Seekable {
    int getPosition() throws IOException;

    boolean isSeekable();

    int setPosition(int i) throws IOException;

    int setPositionRelative(int i) throws IOException;
}
