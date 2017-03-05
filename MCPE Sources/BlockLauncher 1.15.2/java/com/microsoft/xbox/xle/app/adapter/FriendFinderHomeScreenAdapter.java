package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderHomeScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.ui.IconFontSubTextButton;
import com.microsoft.xbox.xle.ui.ImageTitleSubtitleButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import java.util.ArrayList;
import java.util.Arrays;
import net.hockeyapp.android.BuildConfig;

public class FriendFinderHomeScreenAdapter extends AdapterBase implements OnClickListener {
    private XLEButton doneButton;
    private ImageTitleSubtitleButton inviteFacebookButton;
    private CustomTypefaceTextView inviteFriendsTextView;
    private IconFontSubTextButton invitePhoneButton;
    private ImageTitleSubtitleButton linkFacebookButton;
    private IconFontSubTextButton linkPhoneButton;
    private FrameLayout loadingFrameLayout;
    private IconFontSubTextButton searchButton;
    private EditText searchEditText;
    private FrameLayout searchIconButton;
    private final ArrayList<Integer> searchImeActions = new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(4)}));
    private RelativeLayout searchLayout;
    private FriendFinderHomeScreenViewModel viewModel;

    public FriendFinderHomeScreenAdapter(FriendFinderHomeScreenViewModel friendFinderHomeScreenViewModel) {
        super(friendFinderHomeScreenViewModel);
        this.viewModel = friendFinderHomeScreenViewModel;
        this.linkFacebookButton = (ImageTitleSubtitleButton) findViewById(R.id.friendfinder_link_facebook);
        this.linkPhoneButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_link_phone);
        this.searchButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_link_search);
        this.searchIconButton = (FrameLayout) findViewById(R.id.friendfinder_search_icon);
        this.searchLayout = (RelativeLayout) findViewById(R.id.friendfinder_search_layout);
        this.searchEditText = (EditText) findViewById(R.id.friendfinder_search_edittext);
        this.inviteFacebookButton = (ImageTitleSubtitleButton) findViewById(R.id.friendfinder_home_invite_facebook);
        this.invitePhoneButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_home_invite_phone);
        this.inviteFriendsTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_home_invite_friends_text);
        this.loadingFrameLayout = (FrameLayout) findViewById(R.id.friendfinder_home_loading);
        this.doneButton = (XLEButton) findViewById(R.id.friendfinder_home_done);
        XLEAssert.assertNotNull(this.linkFacebookButton);
        XLEAssert.assertNotNull(this.linkPhoneButton);
        XLEAssert.assertNotNull(this.searchButton);
        XLEAssert.assertNotNull(this.searchIconButton);
        XLEAssert.assertNotNull(this.searchLayout);
        XLEAssert.assertNotNull(this.searchEditText);
        XLEAssert.assertNotNull(this.inviteFacebookButton);
        XLEAssert.assertNotNull(this.invitePhoneButton);
        XLEAssert.assertNotNull(this.inviteFriendsTextView);
        XLEAssert.assertNotNull(this.loadingFrameLayout);
        XLEAssert.assertNotNull(this.doneButton);
    }

    private void setViewVisibilities() {
        int i = 8;
        this.linkFacebookButton.setVisibility(this.viewModel.facebookLinked() ? 8 : 0);
        this.linkPhoneButton.setVisibility(this.viewModel.phoneLinked() ? 8 : 0);
        this.inviteFacebookButton.setVisibility(this.viewModel.facebookLinked() ? 0 : 8);
        this.invitePhoneButton.setVisibility(this.viewModel.phoneLinked() ? 0 : 8);
        this.loadingFrameLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.doneButton.setVisibility(this.viewModel.shouldShowDone() ? 0 : 8);
        CustomTypefaceTextView customTypefaceTextView = this.inviteFriendsTextView;
        if (this.viewModel.facebookLinked() || this.viewModel.phoneLinked()) {
            i = 0;
        }
        customTypefaceTextView.setVisibility(i);
    }

    public void onClick(View view) {
        int id = view.getId();
        CharSequence name = this.viewModel.getScreen().getName();
        if (id == R.id.friendfinder_link_facebook) {
            UTCFriendFinder.trackFacebookSignup(name);
            this.viewModel.navigateToLinkFacebook();
        } else if (id == R.id.friendfinder_link_phone) {
            UTCFriendFinder.trackContactsSignUp(name);
            this.viewModel.navigateToLinkPhone();
        } else if (id == R.id.friendfinder_link_search) {
            UTCFriendFinder.trackGamertagSearch(name);
            this.searchLayout.setVisibility(0);
            this.searchButton.setVisibility(8);
            this.searchEditText.requestFocus();
        } else if (id == R.id.friendfinder_home_invite_facebook) {
            UTCFriendFinder.trackFacebookSuggestions(name);
            this.viewModel.navigateToFacebookSuggestions();
        } else if (id == R.id.friendfinder_home_invite_phone) {
            UTCFriendFinder.trackContactsSuggestions(name);
            this.viewModel.navigateToPhoneSuggestions();
        } else if (id == R.id.friendfinder_home_done) {
            UTCFriendFinder.trackDone(name);
            this.viewModel.finishFriendFinder();
        }
    }

    public void onStart() {
        super.onStart();
        this.linkFacebookButton.setOnClickListener(this);
        this.linkPhoneButton.setOnClickListener(this);
        this.searchButton.setOnClickListener(this);
        this.inviteFacebookButton.setOnClickListener(this);
        this.invitePhoneButton.setOnClickListener(this);
        this.doneButton.setOnClickListener(this);
        this.linkFacebookButton.setImageUri(BuildConfig.FLAVOR);
        this.inviteFacebookButton.setImageUri(BuildConfig.FLAVOR);
        this.searchLayout.setVisibility(8);
        final String name = this.viewModel.getScreen().getName();
        this.searchIconButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
            }
        });
        this.searchEditText.addTextChangedListener(new TextWatcher() {
            private boolean isEnterKey;

            public void afterTextChanged(Editable editable) {
                if (this.isEnterKey) {
                    editable.delete(editable.length() - 1, editable.length());
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                boolean z = charSequence.length() > 0 && charSequence.subSequence(charSequence.length() - 1, charSequence.length()).toString().equalsIgnoreCase("\n");
                this.isEnterKey = z;
                if (this.isEnterKey) {
                    UTCFriendFinder.trackGamertagSearchSubmit(name);
                    FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
                }
            }
        });
        this.searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (!FriendFinderHomeScreenAdapter.this.searchImeActions.contains(Integer.valueOf(i))) {
                    return false;
                }
                UTCFriendFinder.trackGamertagSearchSubmit(name);
                FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
                return true;
            }
        });
        this.searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                InputMethodManager inputMethodManager = (InputMethodManager) FriendFinderHomeScreenAdapter.this.searchEditText.getContext().getSystemService("input_method");
                if (z) {
                    inputMethodManager.toggleSoftInput(2, 0);
                } else {
                    inputMethodManager.toggleSoftInput(1, 0);
                }
            }
        });
    }

    protected void updateViewOverride() {
        setViewVisibilities();
        String facebookIconUri = this.viewModel.getFacebookIconUri();
        if (!JavaUtil.isNullOrEmpty(facebookIconUri)) {
            this.linkFacebookButton.setImageUri(facebookIconUri);
            this.inviteFacebookButton.setImageUri(facebookIconUri);
        }
    }
}
