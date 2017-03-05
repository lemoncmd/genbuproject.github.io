package net.zhuoweizhang.mcpelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.mojang.minecraftpe.MainActivity;
import net.zhuoweizhang.mcpelauncher.ui.MainMenuOptionsActivity;
import org.mozilla.javascript.Token;

public class AddonInstallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        System.out.println("MCPELauncher: " + intent.toString());
        System.out.println("Is lib loaded? " + MainActivity.libLoaded);
        String packageName = intent.getData().toString().substring(8);
        boolean isMinecraftUpdate = packageName.equals("com.mojang.minecraftpe");
        if ((!MainActivity.libLoaded && !isMinecraftUpdate) || MainMenuOptionsActivity.isManagingAddons) {
            return;
        }
        if (!intent.getAction().equals("android.intent.action.PACKAGE_REMOVED") || !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
            try {
                PackageManager pm = context.getPackageManager();
                System.out.println("MCPELauncher: " + packageName);
                if (isMinecraftUpdate) {
                    System.out.println("Detected Minecraft PE upgrade");
                    Utils.getPrefs(1).edit().putBoolean("force_prepatch", true).commit();
                } else if (!MainActivity.loadedAddons.contains(packageName)) {
                    ApplicationInfo appInfo = pm.getApplicationInfo(packageName, Token.RESERVED);
                    if (appInfo.metaData == null) {
                        return;
                    }
                    if (appInfo.metaData.getString("net.zhuoweizhang.mcpelauncher.api.nativelibname") == null) {
                        return;
                    }
                }
                System.out.println("Scheduling MCPELauncher restart");
                if (!(MainActivity.currentMainActivity == null || MainActivity.currentMainActivity.get() == null)) {
                    try {
                        ((MainActivity) MainActivity.currentMainActivity.get()).finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                        }
                        System.exit(0);
                    }
                }).start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
