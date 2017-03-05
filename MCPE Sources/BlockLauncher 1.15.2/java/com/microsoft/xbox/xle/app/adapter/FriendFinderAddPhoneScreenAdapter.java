package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderAddPhoneScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class FriendFinderAddPhoneScreenAdapter extends AdapterBase {
    private FrameLayout loadingLayout = ((FrameLayout) findViewById(R.id.friendfinder_add_phone_loading));
    private XLEButton nextButton = ((XLEButton) findViewById(R.id.friendfinder_info_next));
    private EditText phoneNumberEditText = ((EditText) findViewById(R.id.friendfinder_add_phone_edit_text));
    private CustomTypefaceTextView subtitleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_add_phone_subtitle));
    private FriendFinderAddPhoneScreenViewModel viewModel;

    public FriendFinderAddPhoneScreenAdapter(FriendFinderAddPhoneScreenViewModel friendFinderAddPhoneScreenViewModel) {
        super(friendFinderAddPhoneScreenViewModel);
        this.viewModel = friendFinderAddPhoneScreenViewModel;
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.phoneNumberEditText);
        XLEAssert.assertNotNull(this.nextButton);
        XLEAssert.assertNotNull(this.loadingLayout);
    }

    public void onStart() {
        super.onStart();
        this.subtitleTextView.setText(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_AddPhoneNumber_Dialog_Text_LineOne) + "\n\n" + XboxTcuiSdk.getResources().getString(R.string.FriendFinder_AddPhoneNumber_Dialog_Text_LineTwo) + "\n\n" + XboxTcuiSdk.getResources().getString(R.string.FriendFinder_AddPhoneNumber_Dialog_Text_LineThree));
        this.nextButton.setEnabled(false);
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UTCFriendFinder.trackPhoneContactsNext(FriendFinderAddPhoneScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderAddPhoneScreenAdapter.this.viewModel.addPhoneNumber(FriendFinderAddPhoneScreenAdapter.this.phoneNumberEditText.getText().toString());
            }
        });
        this.phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                FriendFinderAddPhoneScreenAdapter.this.nextButton.setEnabled(editable.length() > 0);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
    }

    protected void updateViewOverride() {
        this.loadingLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        if (!JavaUtil.isNullOrEmpty(this.viewModel.getSimPhoneNumber())) {
            CharSequence charSequence = this.viewModel.getCurrentCountryCode() + this.viewModel.getSimPhoneNumber();
            EditText editText = this.phoneNumberEditText;
            if (JavaUtil.isNullOrEmpty(charSequence)) {
                charSequence = "+";
            }
            editText.setText(charSequence);
        }
    }
}
