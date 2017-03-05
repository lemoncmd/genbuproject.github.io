package net.zhuoweizhang.mcpelauncher;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;

public final class AdConfiguration {
    public static final String AD_UNIT_ID = "ca-app-pub-2652482030334356/6560239824";
    public static final String DEVICE_ID_TESTER = "DF28838C26BDFAE7EB063BFEB7A241D3";
    public static final String DEVICE_ID_TESTER2 = "C0ABF0B025E43414E6EF63D720DCEFDE";

    private AdConfiguration() {
    }

    public static AdRequest buildRequest() {
        return new Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(DEVICE_ID_TESTER).addTestDevice(DEVICE_ID_TESTER2).build();
    }
}
