package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.KamcordConstants;
import net.zhuoweizhang.mcpelauncher.PatchManager;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.Utils;

public class NerdyStuffActivity extends Activity implements OnClickListener {
    private static final String[] magicWords = new String[]{"nimubla", "muirab", "otrecnoc", "atled", "nolispe", "etagirf", "repeeketag"};
    private Button chefSpecialButton;
    private Button dumpLibMinecraftPeButton;
    private Button dumpModPEMethodsButton;
    private Button restartAppButton;
    private Button setSkinButton;

    public void onCreate(Bundle icicle) {
        Utils.setLanguageOverride();
        super.onCreate(icicle);
        setContentView(R.layout.nerdy_stuff);
        this.dumpLibMinecraftPeButton = (Button) findViewById(R.id.dump_libminecraftpe_button);
        this.dumpLibMinecraftPeButton.setOnClickListener(this);
        this.restartAppButton = (Button) findViewById(R.id.restart_app_button);
        this.restartAppButton.setOnClickListener(this);
        this.setSkinButton = (Button) findViewById(R.id.set_skin_button);
        this.setSkinButton.setOnClickListener(this);
        this.chefSpecialButton = (Button) findViewById(R.id.chef_special);
        this.chefSpecialButton.setOnClickListener(this);
        this.dumpModPEMethodsButton = (Button) findViewById(R.id.dump_modpe_methods);
        this.dumpModPEMethodsButton.setOnClickListener(this);
        printMagicWord();
    }

    public void onClick(View v) {
        if (v == this.dumpLibMinecraftPeButton) {
            dumpLib();
        } else if (v == this.restartAppButton) {
            forceRestart(this);
        } else if (v == this.setSkinButton) {
            setSkin();
        } else {
            if (v == this.chefSpecialButton) {
            }
            if (v == this.dumpModPEMethodsButton) {
                dumpModPEMethods();
            }
        }
    }

    public void dumpLib() {
        try {
            FileOutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/libminecraftpe.so.dump");
            FileChannel channel = os.getChannel();
            MainActivity.minecraftLibBuffer.position(0);
            channel.write(MainActivity.minecraftLibBuffer);
            channel.close();
            os.close();
            Toast.makeText(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "libminecraftpe.so.dump", 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forceRestart(Activity activity) {
        forceRestart(activity, 1000, true);
    }

    public static void forceRestart(Activity activity, int delay, boolean exit) {
        AlarmManager alarmMgr = (AlarmManager) activity.getSystemService("alarm");
        long timeMillis = SystemClock.elapsedRealtime() + ((long) delay);
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        intent.addFlags(335544320);
        alarmMgr.set(3, timeMillis, PendingIntent.getActivity(activity, 0, intent, 0));
        if (exit) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void setSkin() {
        Intent intent = new Intent("net.zhuoweizhang.mcpelauncher.action.SET_SKIN");
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/skin.png")), "image/png");
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scriptImport() {
        Intent intent = new Intent("net.zhuoweizhang.mcpelauncher.action.IMPORT_SCRIPT");
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/winprogress/500ise_everymethod.js")), "text/plain");
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpModPEMethods() {
        String allMethods = ScriptManager.getAllApiMethodsDescriptions();
        ((ClipboardManager) getSystemService("clipboard")).setText(allMethods);
        try {
            FileWriter w = new FileWriter(new File(Environment.getExternalStorageDirectory(), "/modpescript_dump.txt").getAbsolutePath());
            w.write(allMethods);
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Builder(this).setTitle("modpescript_dump.txt").setMessage(allMethods).setPositiveButton(17039370, null).show();
    }

    public void printMagicWord() {
        int appVersion = 0;
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
        }
        String[] lettersarr = magicWords[appVersion % magicWords.length].split(BuildConfig.FLAVOR);
        Collections.reverse(Arrays.asList(lettersarr));
        Log.i(KamcordConstants.GAME_NAME, "The magic word is " + PatchManager.join(lettersarr, BuildConfig.FLAVOR));
        Log.i(KamcordConstants.GAME_NAME, "https://groups.google.com/forum/#!forum/blocklauncher-beta");
    }
}
