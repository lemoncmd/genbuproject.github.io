package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLayoutChangeListener;
import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import java.net.URI;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class XLEUniversalImageView extends XLEImageView {
    private static final int JELLY_BEAN_MR1 = 17;
    private static final String TAG = XLEUniversalImageView.class.getSimpleName();
    private boolean adjustViewBounds;
    private Params arg;
    private final OnLayoutChangeListener listener;
    private int maxHeight;
    private int maxWidth;

    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml = new int[TypefaceXml.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml[TypefaceXml.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml[TypefaceXml.SANS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml[TypefaceXml.SERIF.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml[TypefaceXml.MONOSPACE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static class Params {
        private final XLETextArg argText;
        private final XLEURIArg argUri;
        private final boolean hasSrc;

        public Params() {
            this(new XLETextArg(new com.microsoft.xbox.toolkit.ui.XLETextArg.Params()), null, false);
        }

        public Params(XLETextArg xLETextArg, XLEURIArg xLEURIArg) {
            this(xLETextArg, xLEURIArg, false);
        }

        private Params(XLETextArg xLETextArg, XLEURIArg xLEURIArg, boolean z) {
            this.argText = xLETextArg;
            this.argUri = xLEURIArg;
            this.hasSrc = z;
        }

        public Params(XLETextArg xLETextArg, boolean z) {
            this(xLETextArg, null, z);
        }

        private Params cloneWithText(String str) {
            return new Params(new XLETextArg(str, this.argText.getParams()), null, this.hasSrc);
        }

        public Params cloneEmpty() {
            return new Params(new XLETextArg(this.argText.getParams()), null, false);
        }

        public Params cloneWithSrc(boolean z) {
            return new Params(new XLETextArg(this.argText.getParams()), null, z);
        }

        public Params cloneWithUri(URI uri) {
            int i = -1;
            int loadingResourceId = this.argUri == null ? -1 : this.argUri.getLoadingResourceId();
            if (this.argUri != null) {
                i = this.argUri.getErrorResourceId();
            }
            return cloneWithUri(uri, loadingResourceId, i);
        }

        public Params cloneWithUri(URI uri, int i, int i2) {
            return new Params(new XLETextArg(this.argText.getParams()), new XLEURIArg(uri, i, i2), this.hasSrc);
        }

        public XLETextArg getArgText() {
            return this.argText;
        }

        public XLEURIArg getArgUri() {
            return this.argUri;
        }

        public boolean hasArgUri() {
            return this.argUri != null;
        }

        public boolean hasSrc() {
            return this.hasSrc;
        }

        public boolean hasText() {
            return this.argText.hasText();
        }
    }

    public enum TypefaceXml {
        NORMAL,
        SANS,
        SERIF,
        MONOSPACE;

        public static TypefaceXml fromIndex(int i) {
            TypefaceXml[] values = values();
            return (i < 0 || i >= values.length) ? null : values[i];
        }

        public static Typeface typefaceFromIndex(int i) {
            TypefaceXml fromIndex = fromIndex(i);
            if (fromIndex == null) {
                return null;
            }
            switch (AnonymousClass2.$SwitchMap$com$microsoft$xbox$toolkit$ui$XLEUniversalImageView$TypefaceXml[fromIndex.ordinal()]) {
                case NativeRegExp.PREFIX /*2*/:
                    return Typeface.SANS_SERIF;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    return Typeface.SERIF;
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    return Typeface.MONOSPACE;
                default:
                    return null;
            }
        }
    }

    public XLEUniversalImageView(Context context) {
        this(context, new Params());
    }

    public XLEUniversalImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.listener = new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                int i9 = i4 - i2;
                if (!(i3 - i == i7 - i5 && i9 == i8 - i6) && XLEUniversalImageView.this.arg.hasText()) {
                    new XLETextTask(XLEUniversalImageView.this).execute(new XLETextArg[]{XLEUniversalImageView.this.arg.getArgText()});
                }
            }
        };
        this.arg = initializeAttributes(context, attributeSet, 0);
        updateImage();
    }

    public XLEUniversalImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.listener = /* anonymous class already generated */;
        this.arg = initializeAttributes(context, attributeSet, i);
        updateImage();
    }

    public XLEUniversalImageView(Context context, Params params) {
        super(context);
        this.listener = /* anonymous class already generated */;
        setMaxWidth(Integer.MAX_VALUE);
        setMaxHeight(Integer.MAX_VALUE);
        this.arg = params;
    }

    private Params initializeAttributes(Context context, AttributeSet attributeSet, int i) {
        String string;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("XLEUniversalImageView"), i, 0);
        try {
            Params params;
            float dimension = obtainStyledAttributes.getDimension(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textSize"), context.getResources().getDisplayMetrics().scaledDensity * AccountHeaderView.MarginMediumDip);
            int color = obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textColor"), 0);
            int i2 = obtainStyledAttributes.getInt(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_typeface"), -1);
            int i3 = obtainStyledAttributes.getInt(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textStyle"), 0);
            String string2 = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_typefaceSource"));
            Typeface create = string2 == null ? Typeface.create(TypefaceXml.typefaceFromIndex(i2), i3) : FontManager.Instance().getTypeface(context, string2);
            int color2 = obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_eraseColor"), 0);
            boolean z = obtainStyledAttributes.getBoolean(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_adjustForImageSize"), false);
            boolean hasValue = obtainStyledAttributes.hasValue(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_src"));
            Float f = null;
            if (obtainStyledAttributes.hasValue(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_textAspectRatio"))) {
                f = Float.valueOf(obtainStyledAttributes.getFloat(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_textAspectRatio"), 0.0f));
            }
            com.microsoft.xbox.toolkit.ui.XLETextArg.Params params2 = new com.microsoft.xbox.toolkit.ui.XLETextArg.Params(dimension, color, create, color2, z, f);
            string = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_text"));
            if (string != null) {
                params = new Params(new XLETextArg(string, params2), false);
            } else {
                string = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_uri"));
                params = string != null ? new Params(new XLETextArg(params2), new XLEURIArg(new URI(string))) : new Params(new XLETextArg(params2), hasValue);
            }
            if (z) {
                addOnLayoutChangeListener(this.listener);
            }
            obtainStyledAttributes.recycle();
            return params;
        } catch (Throwable e) {
            throw new RuntimeException("Error parsing URI '" + string + "'", e);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
        }
    }

    private int resolveAdjustedSize(int i, int i2, int i3) {
        int mode = MeasureSpec.getMode(i3);
        int size = MeasureSpec.getSize(i3);
        switch (mode) {
            case Integer.MIN_VALUE:
                return Math.min(Math.min(i, size), i2);
            case NativeRegExp.TEST /*0*/:
                return Math.min(i, i2);
            case 1073741824:
                return size;
            default:
                return i;
        }
    }

    private void updateImage() {
        if (this.arg.hasText()) {
            new XLETextTask(this).execute(new XLETextArg[]{this.arg.getArgText()});
        } else if (this.arg.hasArgUri()) {
            TextureManager.Instance().bindToView(this.arg.getArgUri().getUri(), this, this.arg.getArgUri().getTextureBindingOption());
        } else if (!this.arg.hasSrc()) {
            setImageDrawable(null);
        }
    }

    public void clearImage() {
        this.arg = this.arg.cloneEmpty();
        updateImage();
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        float f = 0.0f;
        Object obj = null;
        Object obj2 = null;
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        Drawable drawable = getDrawable();
        if (drawable == null) {
            i3 = 0;
            i4 = 0;
        } else {
            i4 = drawable.getIntrinsicWidth();
            i3 = drawable.getIntrinsicHeight();
            if (i4 <= 0) {
                i4 = 1;
            }
            if (i3 <= 0) {
                i3 = 1;
            }
            if (this.adjustViewBounds) {
                obj = mode != 1073741824 ? 1 : null;
                obj2 = mode2 != 1073741824 ? 1 : null;
                mode = MeasureSpec.getSize(i);
                int size = MeasureSpec.getSize(i2);
                if (mode > size) {
                    i3 = (i3 * mode) / i4;
                    i4 = mode;
                } else {
                    i4 = (i4 * size) / i3;
                    i3 = size;
                }
                f = ((float) i4) / ((float) i3);
            }
        }
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        Object obj3 = getContext().getApplicationInfo().targetSdkVersion <= JELLY_BEAN_MR1 ? 1 : null;
        if (obj == null && obj2 == null) {
            i3 += paddingTop + paddingBottom;
            i4 = Math.max(i4 + (paddingLeft + paddingRight), getSuggestedMinimumWidth());
            i3 = Math.max(i3, getSuggestedMinimumHeight());
            i4 = resolveSizeAndState(i4, i, 0);
            i3 = resolveSizeAndState(i3, i2, 0);
        } else {
            mode = resolveAdjustedSize((i4 + paddingLeft) + paddingRight, this.maxWidth, i);
            i4 = resolveAdjustedSize((i3 + paddingTop) + paddingBottom, this.maxHeight, i2);
            if (f != 0.0f && ((double) Math.abs((((float) ((mode - paddingLeft) - paddingRight)) / ((float) ((i4 - paddingTop) - paddingBottom))) - f)) > 1.0E-7d) {
                Object obj4 = null;
                if (obj != null) {
                    mode2 = (((int) (((float) ((i4 - paddingTop) - paddingBottom)) * f)) + paddingLeft) + paddingRight;
                    if (obj2 == null && obj3 == null) {
                        mode = resolveAdjustedSize(mode2, this.maxWidth, i);
                    }
                    if (mode2 <= mode) {
                        obj4 = 1;
                        mode = mode2;
                    }
                }
                if (obj4 == null && obj2 != null) {
                    i3 = (((int) (((float) ((mode - paddingLeft) - paddingRight)) / f)) + paddingTop) + paddingBottom;
                    if (obj == null && obj3 == null) {
                        i4 = resolveAdjustedSize(i3, this.maxHeight, i2);
                    }
                    if (i3 <= i4) {
                        i4 = mode;
                    }
                }
            }
            i3 = i4;
            i4 = mode;
        }
        setMeasuredDimension(i4, i3);
    }

    public void setAdjustViewBounds(boolean z) {
        this.adjustViewBounds = z;
        super.setAdjustViewBounds(z);
    }

    public void setImageURI2(URI uri) {
        this.arg = this.arg.cloneWithUri(uri);
        updateImage();
    }

    public void setImageURI2(URI uri, int i, int i2) {
        this.arg = this.arg.cloneWithUri(uri, i, i2);
        updateImage();
    }

    public void setMaxHeight(int i) {
        super.setMaxHeight(i);
        this.maxHeight = i;
    }

    public void setMaxWidth(int i) {
        super.setMaxWidth(i);
        this.maxWidth = i;
    }

    public void setText(int i) {
        setText(getResources().getString(i));
    }

    public void setText(String str) {
        if (!TextUtils.equals(str, this.arg.getArgText().getText())) {
            this.arg = this.arg.cloneWithText(str);
            updateImage();
        }
    }
}
