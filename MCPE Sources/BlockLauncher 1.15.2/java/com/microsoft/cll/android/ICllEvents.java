package com.microsoft.cll.android;

public interface ICllEvents {
    void eventDropped(String str);

    void sendComplete();

    void stopped();
}
