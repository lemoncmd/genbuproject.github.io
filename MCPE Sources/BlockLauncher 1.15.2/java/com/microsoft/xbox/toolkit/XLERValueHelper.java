package com.microsoft.xbox.toolkit;

import android.view.View;
import com.microsoft.xboxtcui.R.color;
import com.microsoft.xboxtcui.R.dimen;
import com.microsoft.xboxtcui.R.drawable;
import com.microsoft.xboxtcui.R.id;
import com.microsoft.xboxtcui.R.layout;
import com.microsoft.xboxtcui.R.string;
import com.microsoft.xboxtcui.R.style;
import com.microsoft.xboxtcui.R.styleable;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.lang.reflect.Field;

public class XLERValueHelper {
    public static int findDimensionIdByName(String str) {
        Field field;
        Field field2 = null;
        try {
            field = dimen.class.getField(str);
        } catch (NoSuchFieldException e) {
            field = field2;
        }
        int i = -1;
        if (field != null) {
            try {
                i = field.getInt(null);
            } catch (IllegalAccessException e2) {
            }
        }
        return i;
    }

    public static View findViewByString(String str) {
        Field field;
        Field field2 = null;
        try {
            field = id.class.getField(str);
        } catch (NoSuchFieldException e) {
            field = field2;
        }
        int i = -1;
        if (field != null) {
            try {
                i = field.getInt(null);
            } catch (IllegalAccessException e2) {
            }
        }
        return XboxTcuiSdk.getActivity().findViewById(i);
    }

    protected static Class getColorRClass() {
        return color.class;
    }

    public static int getColorRValue(String str) {
        try {
            return getColorRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getDimenRClass() {
        return dimen.class;
    }

    public static int getDimenRValue(String str) {
        try {
            return getDimenRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getDrawableRClass() {
        return drawable.class;
    }

    public static int getDrawableRValue(String str) {
        try {
            return getDrawableRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getIdRClass() {
        return id.class;
    }

    public static int getIdRValue(String str) {
        try {
            return getIdRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getLayoutRClass() {
        return layout.class;
    }

    public static int getLayoutRValue(String str) {
        try {
            return getLayoutRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStringRClass() {
        return string.class;
    }

    public static int getStringRValue(String str) {
        try {
            return getStringRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStyleRClass() {
        return style.class;
    }

    public static int getStyleRValue(String str) {
        try {
            return getStyleRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStyleableRClass() {
        return styleable.class;
    }

    public static int getStyleableRValue(String str) {
        try {
            return getStyleableRClass().getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    public static int[] getStyleableRValueArray(String str) {
        try {
            return (int[]) getStyleableRClass().getDeclaredField(str).get(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return null;
        }
    }
}
