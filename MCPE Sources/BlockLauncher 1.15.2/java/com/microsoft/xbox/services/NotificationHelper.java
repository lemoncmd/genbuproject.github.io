package com.microsoft.xbox.services;

import android.content.Context;
import android.os.Bundle;

public class NotificationHelper {
    public static NotificationResult tryParseXboxLiveNotification(Bundle bundle, Context context) {
        return new NotificationResult(bundle, context);
    }
}
