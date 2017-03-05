package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

public final class UiUtil {
    private static final String TAG = UiUtil.class.getSimpleName();

    public static boolean canScroll(ScrollView scrollView) {
        View childAt = scrollView.getChildAt(0);
        if (childAt != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childAt.getLayoutParams();
            if (scrollView.getHeight() < marginLayoutParams.bottomMargin + (childAt.getHeight() + marginLayoutParams.topMargin)) {
                return true;
            }
        }
        return false;
    }

    public static void ensureClickableSpanOnUnderlineSpan(TextView textView, int i, ClickableSpan clickableSpan) {
        CharSequence spannableStringBuilder = new SpannableStringBuilder(Html.fromHtml(textView.getResources().getString(i)));
        UnderlineSpan[] underlineSpanArr = (UnderlineSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), UnderlineSpan.class);
        if (underlineSpanArr != null && underlineSpanArr.length > 0) {
            Object obj = underlineSpanArr[0];
            spannableStringBuilder.setSpan(clickableSpan, spannableStringBuilder.getSpanStart(obj), spannableStringBuilder.getSpanEnd(obj), 33);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        textView.setText(spannableStringBuilder);
    }

    public static boolean ensureErrorButtonsFragment(BaseActivity baseActivity, ErrorScreen errorScreen) {
        if (baseActivity.hasFragment(R.id.xbid_error_buttons)) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ErrorButtonsFragment.ARG_LEFT_ERROR_BUTTON_STRING_ID, errorScreen.leftButtonTextId);
        return ensureFragment(ErrorButtonsFragment.class, baseActivity, R.id.xbid_error_buttons, bundle);
    }

    public static boolean ensureErrorFragment(BaseActivity baseActivity, ErrorScreen errorScreen) {
        return !baseActivity.hasFragment(R.id.xbid_body_fragment) ? ensureFragment(errorScreen.errorFragmentClass, baseActivity, R.id.xbid_body_fragment, baseActivity.getIntent().getExtras()) : false;
    }

    private static boolean ensureFragment(Class<? extends BaseFragment> cls, BaseActivity baseActivity, int i, Bundle bundle) {
        if (!baseActivity.hasFragment(i)) {
            try {
                BaseFragment baseFragment = (BaseFragment) cls.newInstance();
                baseFragment.setArguments(bundle);
                baseActivity.addFragment(i, baseFragment);
                return true;
            } catch (InstantiationException e) {
                Log.e(TAG, e.getMessage());
            } catch (IllegalAccessException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
        return false;
    }

    public static boolean ensureGamerTagCreationFragment(BaseActivity baseActivity, int i, Bundle bundle) {
        return ensureFragment(SignUpFragment.class, baseActivity, i, bundle);
    }

    public static boolean ensureHeaderFragment(BaseActivity baseActivity, int i, Bundle bundle) {
        return ensureFragment(HeaderFragment.class, baseActivity, i, bundle);
    }

    public static boolean ensureWelcomeFragment(BaseActivity baseActivity, int i, boolean z, Bundle bundle) {
        return z ? ensureFragment(IntroducingFragment.class, baseActivity, i, bundle) : ensureFragment(WelcomeFragment.class, baseActivity, i, bundle);
    }
}
