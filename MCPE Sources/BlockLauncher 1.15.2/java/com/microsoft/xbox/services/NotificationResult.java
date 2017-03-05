package com.microsoft.xbox.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.ipaulpro.afilechooser.utils.MimeTypeParser;
import com.microsoft.xbox.idp.R;
import net.hockeyapp.android.BuildConfig;

public class NotificationResult {
    public String body;
    public String data;
    public NotificationType notificationType;
    public String title;

    public enum NotificationType {
        Achievement,
        Invite,
        Unknown
    }

    public NotificationResult(Bundle bundle, Context context) {
        String string = bundle.getString(MimeTypeParser.TAG_TYPE);
        if (string == null) {
            this.notificationType = NotificationType.Unknown;
        } else if (string.equals("xbox_live_game_invite")) {
            this.title = context.getString(R.string.xbox_live_game_invite_title);
            string = context.getString(R.string.xbox_live_game_invite_body);
            Bundle bundle2 = bundle.getBundle("notification");
            if (bundle2 != null) {
                String string2 = bundle2.getString("body_loc_args");
                if (string2 != null) {
                    String[] split = string2.replace("[", BuildConfig.FLAVOR).replace("]", BuildConfig.FLAVOR).split(",");
                    this.body = String.format(string, new Object[]{split[0], split[1]});
                }
            } else {
                Log.i("XSAPI.Android", "could not parse notification");
            }
            this.notificationType = NotificationType.Invite;
        } else if (string.equals("xbox_live_achievement_unlock")) {
            this.notificationType = NotificationType.Achievement;
            Bundle bundle3 = bundle.getBundle("notification");
            if (bundle3 != null) {
                this.title = bundle3.getString("title");
                this.body = bundle3.getString("body");
            }
        } else {
            this.notificationType = NotificationType.Unknown;
        }
        this.data = bundle.getString("xbl");
    }
}
