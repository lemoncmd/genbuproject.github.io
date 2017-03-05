package com.microsoft.xbox.idp.util;

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.microsoft.xbox.xle.app.ImageUtil;

public class HttpUtil {

    public enum ImageSize {
        SMALL(64, 64),
        MEDIUM(208, 208),
        LARGE(ImageUtil.MEDIUM_TABLET, ImageUtil.MEDIUM_TABLET);
        
        private final int h;
        private final int w;

        private ImageSize(int i, int i2) {
            this.w = i;
            this.h = i2;
        }
    }

    public static HttpCall appendCommonParameters(HttpCall httpCall, String str) {
        httpCall.setXboxContractVersionHeaderValue(str);
        httpCall.setContentTypeHeaderValue("application/json");
        httpCall.setRetryAllowed(true);
        return httpCall;
    }

    public static String getEndpoint(Uri uri) {
        return uri.getScheme() + "://" + uri.getEncodedAuthority();
    }

    public static Builder getImageSizeUrlParams(Builder builder, ImageSize imageSize) {
        return builder.appendQueryParameter("w", Integer.toString(imageSize.w)).appendQueryParameter("h", Integer.toString(imageSize.h));
    }

    public static String getPathAndQuery(Uri uri) {
        String encodedPath = uri.getEncodedPath();
        Object encodedQuery = uri.getEncodedQuery();
        Object encodedFragment = uri.getEncodedFragment();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(encodedPath);
        if (!TextUtils.isEmpty(encodedQuery)) {
            stringBuffer.append("?").append(encodedQuery);
        }
        if (!TextUtils.isEmpty(encodedFragment)) {
            stringBuffer.append("#").append(encodedFragment);
        }
        return stringBuffer.toString();
    }
}
