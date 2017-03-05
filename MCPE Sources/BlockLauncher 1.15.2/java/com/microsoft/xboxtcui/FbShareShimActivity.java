package com.microsoft.xboxtcui;

import android.net.Uri;
import android.os.Bundle;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareLinkContent.Builder;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.xle.app.ImageUtil;
import java.net.URI;

public class FbShareShimActivity extends FbShimActivity {
    private final String SHARE_TO_FACEBOOK_LINK = "http://go.microsoft.com/fwlink/?LinkId=698852";
    private ShareDialog shareDialog;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.shareDialog = new ShareDialog(this);
            FacebookManager.getInstance().registerShareCallback(this.shareDialog);
            URI medium = ImageUtil.getMedium(ProfileModel.getMeProfileModel().getGamerPicImageUrl());
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                this.shareDialog.show(((Builder) new Builder().setImageUrl(Uri.parse(medium.toString())).setContentTitle(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Share_Title)).setContentDescription(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Share_Description)).setContentUrl(Uri.parse("http://go.microsoft.com/fwlink/?LinkId=698852"))).build());
            }
        }
    }
}
