package com.microsoft.xbox.idp.telemetry.helpers;

import Microsoft.Telemetry.Base;
import com.microsoft.xbox.idp.telemetry.utc.PageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import java.util.ArrayList;

public class UTCPageView {
    private static final int PAGEVIEWVERSION = 1;
    private static ArrayList<String> pages = new ArrayList();

    public static void addPage(String str) {
        if (pages == null) {
            pages = new ArrayList();
        }
        if (!pages.contains(str) && str != null) {
            pages.add(str);
        }
    }

    public static String getCurrentPage() {
        int size = getSize();
        return size == 0 ? UTCTelemetry.UNKNOWNPAGE : (String) pages.get(size - 1);
    }

    public static String getPreviousPage() {
        int size = getSize();
        return size < 2 ? UTCTelemetry.UNKNOWNPAGE : (String) pages.get(size - 2);
    }

    public static int getSize() {
        if (pages == null) {
            pages = new ArrayList();
        }
        return pages.size();
    }

    public static void removePage() {
        int size = getSize();
        if (size > 0) {
            pages.remove(size - 1);
        }
    }

    public static void track(String str, CharSequence charSequence) {
        track(str, charSequence, new UTCAdditionalInfoModel());
    }

    public static void track(String str, CharSequence charSequence, UTCAdditionalInfoModel uTCAdditionalInfoModel) {
        if (charSequence != null) {
            try {
                uTCAdditionalInfoModel.addValue("activityTitle", charSequence);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageView.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        addPage(str);
        String previousPage = getPreviousPage();
        Base pageView = new PageView();
        pageView.setPageName(str);
        pageView.setFromPage(previousPage);
        UTCLog.log("pageView:%s, fromPage:%s, additionalInfo:%s", str, previousPage, uTCAdditionalInfoModel);
        pageView.setBaseData(UTCCommonDataModel.getCommonData(PAGEVIEWVERSION, uTCAdditionalInfoModel));
        UTCTelemetry.LogEvent(pageView);
    }
}
