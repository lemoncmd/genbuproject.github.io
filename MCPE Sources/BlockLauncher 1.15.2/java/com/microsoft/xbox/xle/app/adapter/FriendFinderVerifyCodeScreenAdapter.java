package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderVerifyCodeScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;

public class FriendFinderVerifyCodeScreenAdapter extends AdapterBase {
    private IconFontToggleButton callMeButton = ((IconFontToggleButton) findViewById(R.id.friendfinder_verify_call_me));
    private EditText codeEditText = ((EditText) findViewById(R.id.friendfinder_verify_code_edit_text));
    private FrameLayout loadingLayout = ((FrameLayout) findViewById(R.id.friendfinder_verify_loading));
    private IconFontToggleButton resendCodeButton = ((IconFontToggleButton) findViewById(R.id.friendfinder_verify_resend));
    private XLEButton verifyButton = ((XLEButton) findViewById(R.id.friendfinder_verify_verify_code));
    private FriendFinderVerifyCodeScreenViewModel viewModel;

    public FriendFinderVerifyCodeScreenAdapter(FriendFinderVerifyCodeScreenViewModel friendFinderVerifyCodeScreenViewModel) {
        super(friendFinderVerifyCodeScreenViewModel);
        this.viewModel = friendFinderVerifyCodeScreenViewModel;
        XLEAssert.assertNotNull(this.codeEditText);
        XLEAssert.assertNotNull(this.resendCodeButton);
        XLEAssert.assertNotNull(this.callMeButton);
        XLEAssert.assertNotNull(this.verifyButton);
        XLEAssert.assertNotNull(this.loadingLayout);
        this.resendCodeButton.setChecked(false);
        this.resendCodeButton.setEnabled(true);
        this.callMeButton.setChecked(false);
        this.callMeButton.setEnabled(true);
        this.verifyButton.setEnabled(false);
    }

    public void onStart() {
        super.onStart();
        this.codeEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                FriendFinderVerifyCodeScreenAdapter.this.verifyButton.setEnabled(editable.length() > 0);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        this.resendCodeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UTCFriendFinder.trackPhoneContactsResendCode(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.resendCode();
            }
        });
        this.callMeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UTCFriendFinder.trackPhoneContactsCallMe(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.callMe();
            }
        });
        this.verifyButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UTCFriendFinder.trackPhoneContactsNext(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.verifyCode(FriendFinderVerifyCodeScreenAdapter.this.codeEditText.getText().toString());
            }
        });
    }

    protected void updateViewOverride() {
        boolean z = false;
        this.loadingLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.resendCodeButton.setEnabled(!this.viewModel.isSendingCode());
        IconFontToggleButton iconFontToggleButton = this.callMeButton;
        if (!this.viewModel.isSendingCode()) {
            z = true;
        }
        iconFontToggleButton.setEnabled(z);
    }
}
