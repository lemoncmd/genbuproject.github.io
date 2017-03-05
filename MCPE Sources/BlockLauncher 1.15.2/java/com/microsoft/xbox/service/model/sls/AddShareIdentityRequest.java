package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;
import java.util.ArrayList;

public class AddShareIdentityRequest {
    public ArrayList<String> xuids;

    public AddShareIdentityRequest(ArrayList<String> arrayList) {
        this.xuids = arrayList;
    }

    public static String getAddShareIdentityRequestBody(AddShareIdentityRequest addShareIdentityRequest) {
        return GsonUtil.toJsonString(addShareIdentityRequest);
    }
}
