package com.microsoft.xbox.toolkit.ui.util;

import android.widget.TextView;

public final class LibCompat {
    private LibCompat() {
    }

    public static void setTextAppearance(TextView textView, int i) {
        textView.setTextAppearance(textView.getContext(), i);
    }
}
