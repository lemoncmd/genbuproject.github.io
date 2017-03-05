package com.microsoft.xbox.service.model.friendfinder;

import com.microsoft.xbox.toolkit.GsonUtil;

public class UpdateThirdPartyTokenRequest {
    public String accessToken;

    public UpdateThirdPartyTokenRequest(String str) {
        this.accessToken = str;
    }

    public static String getUpdateThirdPartyTokenRequestBody(UpdateThirdPartyTokenRequest updateThirdPartyTokenRequest) {
        try {
            return GsonUtil.toJsonString(updateThirdPartyTokenRequest);
        } catch (Exception e) {
            return null;
        }
    }
}
