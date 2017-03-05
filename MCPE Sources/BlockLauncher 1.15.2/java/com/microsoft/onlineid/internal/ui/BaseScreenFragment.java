package com.microsoft.onlineid.internal.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Resources;

public class BaseScreenFragment extends Fragment {
    private ProgressView _progress;

    public enum ArgumentsKey {
        Header,
        Body
    }

    public static <T extends BaseScreenFragment> T buildWithBaseScreen(String str, String str2, Class<T> cls) {
        Bundle bundle = new Bundle();
        try {
            BaseScreenFragment baseScreenFragment = (BaseScreenFragment) cls.newInstance();
            bundle.putString(ArgumentsKey.Header.name(), str);
            bundle.putString(ArgumentsKey.Body.name(), str2);
            baseScreenFragment.setArguments(bundle);
            return baseScreenFragment;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Resources resources = new Resources(getActivity().getApplicationContext());
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(resources.getLayout("base_screen"), viewGroup, false);
        this._progress = (ProgressView) inflate.findViewById(resources.getId("baseScreenProgressView"));
        TextView textView = (TextView) inflate.findViewById(resources.getId("baseScreenHeader"));
        TextView textView2 = (TextView) inflate.findViewById(resources.getId("baseScreenBody"));
        Bundle arguments = getArguments();
        String name = ArgumentsKey.Header.name();
        String name2 = ArgumentsKey.Body.name();
        Objects.verifyArgumentNotNull(arguments.getString(name), name);
        Objects.verifyArgumentNotNull(arguments.getString(name2), name2);
        textView.setText(arguments.getString(name));
        textView2.setText(arguments.getString(name2));
        return inflate;
    }

    protected void showProgressViewAnimation() {
        this._progress.setVisibility(0);
        this._progress.startAnimation();
    }

    protected void stopProgressAnimation() {
        this._progress.stopAnimation();
        this._progress.setVisibility(8);
    }
}
