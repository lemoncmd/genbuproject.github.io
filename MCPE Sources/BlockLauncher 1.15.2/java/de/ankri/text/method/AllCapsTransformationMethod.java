package de.ankri.text.method;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import java.util.Locale;

public class AllCapsTransformationMethod implements TransformationMethodCompat2 {
    private static final String TAG = "AllCapsTransformationMethod";
    private boolean mEnabled;
    private Locale mLocale;

    public AllCapsTransformationMethod(Context context) {
        this.mLocale = context.getResources().getConfiguration().locale;
    }

    public CharSequence getTransformation(CharSequence source, View view) {
        if (this.mEnabled) {
            return source != null ? source.toString().toUpperCase(this.mLocale) : null;
        } else {
            Log.w(TAG, "Caller did not enable length changes; not transforming text");
            return source;
        }
    }

    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
    }

    public void setLengthChangesAllowed(boolean allowLengthChanges) {
        this.mEnabled = allowLengthChanges;
    }
}
