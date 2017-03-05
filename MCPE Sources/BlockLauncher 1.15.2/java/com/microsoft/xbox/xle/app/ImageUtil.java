package com.microsoft.xbox.xle.app;

import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.URI;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ImageUtil {
    public static final int LARGE_PHONE = 640;
    public static final int LARGE_TABLET = 800;
    public static final int MEDIUM_PHONE = 300;
    public static final int MEDIUM_TABLET = 424;
    public static final int SMALL = 200;
    public static final int TINY = 100;
    public static final String resizeFormatter = "&w=%d&h=%d&format=png";
    public static final String resizeFormatterSizeOnly = "&w=%d&h=%d";
    public static final String resizeFormatterWithPadding = "&mode=padding&w=%d&h=%d&format=png";

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType = new int[ImageType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.TINY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.TINY_3X4.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.TINY_4X3.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL_3X4.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL_4X3.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.MEDIUM.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.MEDIUM_3X4.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.MEDIUM_4X3.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.LARGE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.LARGE_3X4.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public enum ImageType {
        TINY,
        TINY_3X4,
        TINY_4X3,
        SMALL,
        SMALL_3X4,
        SMALL_4X3,
        MEDIUM,
        MEDIUM_3X4,
        MEDIUM_4X3,
        LARGE,
        LARGE_3X4;

        public static ImageType fromString(String str) {
            ImageType imageType = null;
            try {
                imageType = valueOf(str);
            } catch (NullPointerException e) {
            } catch (IllegalArgumentException e2) {
            }
            return imageType;
        }
    }

    private static URI createUri(String str) {
        URI uri = null;
        if (str != null) {
            try {
                uri = URI.create(str);
            } catch (IllegalArgumentException e) {
            }
        }
        return uri;
    }

    private static URI formatString(String str, int i, int i2) {
        if (str == null || !str.contains("images-eds")) {
            return null;
        }
        boolean contains = str.contains("&w=");
        boolean contains2 = str.contains("&h=");
        if (contains && contains2) {
            return createUri(str.replaceAll("w=[0-9]+", "w=" + i).replaceAll("h=[0-9]+", "h=" + i2));
        }
        if (contains) {
            return createUri(str.replaceAll("w=[0-9]+", "w=" + i) + "&h=" + i2);
        }
        if (contains2) {
            return createUri(str.replaceAll("h=[0-9]+", "h=" + i2) + "&w=" + i);
        }
        if (str.contains("format=")) {
            return createUri(str + String.format(resizeFormatterSizeOnly, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        }
        return createUri(str + String.format(resizeFormatter, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
    }

    private static URI formatURI(URI uri, int i, int i2) {
        if (uri == null) {
            return null;
        }
        URI formatString = formatString(uri.toString(), i, i2);
        return formatString != null ? formatString : uri;
    }

    public static URI getLarge(String str) {
        URI formatString = XboxTcuiSdk.getIsTablet() ? formatString(str, LARGE_TABLET, LARGE_TABLET) : formatString(str, LARGE_PHONE, LARGE_PHONE);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getLarge(URI uri) {
        return XboxTcuiSdk.getIsTablet() ? formatURI(uri, LARGE_TABLET, LARGE_TABLET) : formatURI(uri, LARGE_PHONE, LARGE_PHONE);
    }

    public static URI getLarge3X4(String str) {
        return formatString(str, 720, 1080);
    }

    public static URI getLarge3X4(URI uri) {
        return formatURI(uri, 720, 1080);
    }

    public static URI getMedium(String str) {
        URI formatString = XboxTcuiSdk.getIsTablet() ? formatString(str, MEDIUM_TABLET, MEDIUM_TABLET) : formatString(str, MEDIUM_PHONE, MEDIUM_PHONE);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getMedium(URI uri) {
        return XboxTcuiSdk.getIsTablet() ? formatURI(uri, MEDIUM_TABLET, MEDIUM_TABLET) : formatURI(uri, MEDIUM_PHONE, MEDIUM_PHONE);
    }

    public static URI getMedium2X1(String str) {
        return formatString(str, 480, 270);
    }

    public static URI getMedium2X1(URI uri) {
        return formatURI(uri, 480, 270);
    }

    public static URI getMedium3X4(String str) {
        return formatString(str, 426, LARGE_PHONE);
    }

    public static URI getMedium3X4(URI uri) {
        return formatURI(uri, 426, LARGE_PHONE);
    }

    public static URI getMedium4X3(String str) {
        return formatString(str, 562, 316);
    }

    public static URI getMedium4X3(URI uri) {
        return formatURI(uri, 562, 316);
    }

    public static URI getSmall(String str) {
        URI formatString = formatString(str, SMALL, SMALL);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getSmall(URI uri) {
        return formatURI(uri, SMALL, SMALL);
    }

    public static URI getSmall2X1(String str) {
        return formatString(str, 243, Token.SCRIPT);
    }

    public static URI getSmall2X1(URI uri) {
        return formatURI(uri, 243, Token.SCRIPT);
    }

    public static URI getSmall3X4(String str) {
        return formatString(str, 215, 294);
    }

    public static URI getSmall3X4(URI uri) {
        return formatURI(uri, 215, 294);
    }

    public static URI getSmall4X3(String str) {
        return formatString(str, 275, 216);
    }

    public static URI getSmall4X3(URI uri) {
        return formatURI(uri, 275, 216);
    }

    public static URI getTiny(String str) {
        URI formatString = formatString(str, TINY, TINY);
        return formatString == null ? createUri(str) : formatString;
    }

    public static URI getTiny(URI uri) {
        return formatURI(uri, TINY, TINY);
    }

    public static URI getTiny2X1(String str) {
        return formatString(str, Token.TO_OBJECT, 84);
    }

    public static URI getTiny2X1(URI uri) {
        return formatURI(uri, Token.TO_OBJECT, 84);
    }

    public static URI getTiny3X4(String str) {
        return formatString(str, 85, Token.FOR);
    }

    public static URI getTiny3X4(URI uri) {
        return formatURI(uri, 85, Token.FOR);
    }

    public static URI getTiny4X3(String str) {
        return formatString(str, Token.FOR, 90);
    }

    public static URI getTiny4X3(URI uri) {
        return formatURI(uri, Token.FOR, 90);
    }

    public static URI getURI(String str, int i, int i2) {
        URI formatString = formatString(str, i, i2);
        return formatString == null ? createUri(str) : formatString;
    }

    public static URI getURI(URI uri, int i, int i2) {
        return formatURI(uri, i, i2);
    }

    public static URI getUri(String str, ImageType imageType) {
        if (imageType == null) {
            return getSmall(str);
        }
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[imageType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return getTiny(str);
            case NativeRegExp.PREFIX /*2*/:
                return getTiny3X4(str);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return getTiny4X3(str);
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return getSmall(str);
            case Token.GOTO /*5*/:
                return getSmall3X4(str);
            case Token.IFEQ /*6*/:
                return getSmall4X3(str);
            case Token.IFNE /*7*/:
                return getMedium(str);
            case Token.SETNAME /*8*/:
                return getMedium3X4(str);
            case Token.BITOR /*9*/:
                return getMedium4X3(str);
            case Token.BITXOR /*10*/:
                return getLarge(str);
            case Token.BITAND /*11*/:
                return getLarge3X4(str);
            default:
                return getSmall(str);
        }
    }

    public static URI getUri(URI uri, ImageType imageType) {
        if (imageType == null) {
            return getSmall(uri);
        }
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[imageType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return getTiny(uri);
            case NativeRegExp.PREFIX /*2*/:
                return getTiny3X4(uri);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return getTiny4X3(uri);
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return getSmall(uri);
            case Token.GOTO /*5*/:
                return getSmall3X4(uri);
            case Token.IFEQ /*6*/:
                return getSmall4X3(uri);
            case Token.IFNE /*7*/:
                return getMedium(uri);
            case Token.SETNAME /*8*/:
                return getMedium3X4(uri);
            case Token.BITOR /*9*/:
                return getMedium4X3(uri);
            case Token.BITXOR /*10*/:
                return getLarge(uri);
            case Token.BITAND /*11*/:
                return getLarge3X4(uri);
            default:
                return getSmall(uri);
        }
    }
}
