package net.zhuoweizhang.mcpelauncher;

import com.mojang.minecraftpe.MainActivity;
import java.util.HashMap;
import java.util.Map;

public class RealmsRedirectInfo {
    public static Map<String, RealmsRedirectInfo> targets = new HashMap();
    public String accountUrl;
    public String loginUrl = null;
    public String peoUrl = "NONE";

    public RealmsRedirectInfo(String peoUrl, String accountUrl, String loginUrl) {
        this.peoUrl = peoUrl;
        this.accountUrl = accountUrl;
        this.loginUrl = loginUrl;
    }

    static {
        add(new RealmsRedirectInfo("NONE", null, MainActivity.MOJANG_ACCOUNT_LOGIN_URL));
    }

    private static void add(RealmsRedirectInfo info) {
        targets.put(info.peoUrl, info);
    }
}
