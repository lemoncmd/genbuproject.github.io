package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxAppDeepLinker;

public class ProfileScreenAdapter extends AdapterBase {
    private IconFontToggleButton blockButton = ((IconFontToggleButton) findViewById(R.id.profile_block));
    private ScrollView contentScrollView = ((ScrollView) findViewById(R.id.profile_screen_content_list));
    private IconFontToggleButton followButton = ((IconFontToggleButton) findViewById(R.id.profile_follow));
    private XLERoundedUniversalImageView gamerPicImageView = ((XLERoundedUniversalImageView) findViewById(R.id.profile_gamerpic));
    private CustomTypefaceTextView gamerscoreIconTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore_icon));
    private CustomTypefaceTextView gamerscoreTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore));
    private CustomTypefaceTextView gamertagTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamertag));
    private FastProgressBar loadingProgressBar = ((FastProgressBar) findViewById(R.id.profile_screen_loading));
    private IconFontToggleButton muteButton = ((IconFontToggleButton) findViewById(R.id.profile_mute));
    private CustomTypefaceTextView realNameTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_realname));
    private IconFontToggleButton reportButton = ((IconFontToggleButton) findViewById(R.id.profile_report));
    private XLERootView rootView = ((XLERootView) findViewById(R.id.profile_root));
    private IconFontToggleButton viewInXboxAppButton = ((IconFontToggleButton) findViewById(R.id.profile_view_in_xbox_app));
    private CustomTypefaceTextView viewInXboxAppSubTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_view_in_xbox_app_subtext));
    private ProfileScreenViewModel viewModel;

    public ProfileScreenAdapter(ProfileScreenViewModel profileScreenViewModel) {
        super(profileScreenViewModel);
        this.viewModel = profileScreenViewModel;
        this.viewInXboxAppButton.setVisibility(0);
        this.viewInXboxAppButton.setEnabled(true);
        this.viewInXboxAppButton.setChecked(true);
        if (this.viewModel.isMeProfile()) {
            this.followButton.setVisibility(8);
            this.muteButton.setVisibility(8);
            this.blockButton.setVisibility(8);
            this.reportButton.setVisibility(8);
            this.viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_MeProfile);
            return;
        }
        this.followButton.setVisibility(0);
        this.followButton.setEnabled(true);
        this.muteButton.setVisibility(0);
        this.muteButton.setEnabled(true);
        this.muteButton.setChecked(false);
        this.blockButton.setVisibility(0);
        this.blockButton.setEnabled(false);
        this.reportButton.setVisibility(0);
        this.reportButton.setEnabled(true);
        this.reportButton.setChecked(false);
        this.viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_YouProfile);
    }

    public void onStart() {
        super.onStart();
        if (this.followButton != null) {
            this.followButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ProfileScreenAdapter.this.viewModel.navigateToChangeRelationship();
                }
            });
        }
        if (this.muteButton != null) {
            this.muteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ProfileScreenAdapter.this.muteButton.toggle();
                    ProfileScreenAdapter.this.muteButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.muteButton.isChecked()) {
                        UTCPeopleHub.trackMute(true);
                        ProfileScreenAdapter.this.viewModel.muteUser();
                        return;
                    }
                    UTCPeopleHub.trackMute(false);
                    ProfileScreenAdapter.this.viewModel.unmuteUser();
                }
            });
        }
        if (this.blockButton != null) {
            this.blockButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ProfileScreenAdapter.this.blockButton.toggle();
                    ProfileScreenAdapter.this.blockButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.blockButton.isChecked()) {
                        UTCPeopleHub.trackBlock();
                        ProfileScreenAdapter.this.viewModel.blockUser();
                        return;
                    }
                    UTCPeopleHub.trackUnblock();
                    ProfileScreenAdapter.this.viewModel.unblockUser();
                }
            });
        }
        if (this.reportButton != null) {
            this.reportButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UTCPeopleHub.trackReport();
                    ProfileScreenAdapter.this.viewModel.showReportDialog();
                }
            });
        }
        if (this.viewInXboxAppButton == null) {
            return;
        }
        if (XboxAppDeepLinker.appDeeplinkingSupported()) {
            this.viewInXboxAppButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UTCPeopleHub.trackViewInXboxApp();
                    ProfileScreenAdapter.this.viewModel.launchXboxApp();
                }
            });
            return;
        }
        this.viewInXboxAppButton.setVisibility(8);
        this.viewInXboxAppSubTextView.setVisibility(8);
    }

    protected void updateViewOverride() {
        CharSequence realName;
        boolean z = false;
        if (this.rootView != null) {
            this.rootView.setBackgroundColor(this.viewModel.getPreferredColor());
        }
        this.loadingProgressBar.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.contentScrollView.setVisibility(this.viewModel.isBusy() ? 8 : 0);
        if (this.gamerPicImageView != null) {
            this.gamerPicImageView.setImageURI2(ImageUtil.getMedium(this.viewModel.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
        }
        if (this.realNameTextView != null) {
            realName = this.viewModel.getRealName();
            if (JavaUtil.isNullOrEmpty(realName)) {
                this.realNameTextView.setVisibility(8);
            } else {
                this.realNameTextView.setText(realName);
                this.realNameTextView.setVisibility(0);
            }
        }
        if (!(this.gamerscoreTextView == null || this.gamerscoreIconTextView == null)) {
            realName = this.viewModel.getGamerScore();
            if (!JavaUtil.isNullOrEmpty(realName)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamerscoreTextView, realName, 0);
                XLEUtil.updateVisibilityIfNotNull(this.gamerscoreIconTextView, 0);
            }
        }
        if (this.gamertagTextView != null) {
            realName = this.viewModel.getGamerTag();
            if (!JavaUtil.isNullOrEmpty(realName)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamertagTextView, realName, 0);
            }
        }
        if (!this.viewModel.isMeProfile()) {
            boolean z2 = this.viewModel.getIsAddingUserToBlockList() || this.viewModel.getIsRemovingUserFromBlockList();
            this.followButton.setChecked(this.viewModel.isCallerFollowingTarget());
            IconFontToggleButton iconFontToggleButton = this.followButton;
            boolean z3 = (z2 || this.viewModel.getIsBlocked()) ? false : true;
            iconFontToggleButton.setEnabled(z3);
            this.muteButton.setChecked(this.viewModel.getIsMuted());
            iconFontToggleButton = this.muteButton;
            z3 = (this.viewModel.getIsAddingUserToMutedList() || this.viewModel.getIsRemovingUserFromMutedList()) ? false : true;
            iconFontToggleButton.setEnabled(z3);
            this.blockButton.setChecked(this.viewModel.getIsBlocked());
            IconFontToggleButton iconFontToggleButton2 = this.blockButton;
            if (!z2) {
                z = true;
            }
            iconFontToggleButton2.setEnabled(z);
        }
    }
}
