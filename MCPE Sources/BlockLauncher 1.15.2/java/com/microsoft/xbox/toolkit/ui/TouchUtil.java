package com.microsoft.xbox.toolkit.ui;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.xbox.toolkit.XLEAssert;

public class TouchUtil {
    public static OnClickListener createOnClickListener(OnClickListener onClickListener) {
        if (onClickListener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", onClickListener);
        return onClickListener;
    }

    public static OnItemClickListener createOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", onItemClickListener);
        return onItemClickListener;
    }

    public static OnLongClickListener createOnLongClickListener(OnLongClickListener onLongClickListener) {
        if (onLongClickListener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", onLongClickListener);
        return onLongClickListener;
    }
}
