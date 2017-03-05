package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;

public final class AboutAppActivity extends Activity implements OnClickListener, OnLongClickListener {
    public static final String FORUMS_PAGE_URL = "http://www.minecraftforum.net/topic/1675581-blocklauncher-an-android-app-that-patches-minecraft-pe-without-reinstall/";
    public static final String LICENSES_URL = "https://gist.github.com/zhuowei/da4c2fec46d4d23050bf";
    public static final int SLEEP_INTERVAL = 120;
    public TextView appNameText;
    public TextView appVersionText;
    public int frame;
    public Button gotoForumsButton;
    public Button ossLicensesButton;

    protected void onCreate(Bundle savedInstanceState) {
        Utils.setLanguageOverride();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        this.appNameText = (TextView) findViewById(R.id.about_appnametext);
        this.appNameText.setOnLongClickListener(this);
        this.gotoForumsButton = (Button) findViewById(R.id.about_go_to_forums_button);
        this.gotoForumsButton.setOnClickListener(this);
        this.ossLicensesButton = (Button) findViewById(R.id.about_oss_license_info_button);
        this.ossLicensesButton.setOnClickListener(this);
        this.appVersionText = (TextView) findViewById(R.id.about_appversiontext);
        String appVersion = "Top secret alpha pre-prerelease";
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.appVersionText.setText(appVersion);
    }

    public boolean onLongClick(View v) {
        if (v != this.appNameText) {
            return false;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=6K7VaIdttkw"));
        startActivity(intent);
        return true;
    }

    public void onClick(View v) {
        if (v == this.gotoForumsButton) {
            openForumsPage();
        } else if (v == this.ossLicensesButton) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(LICENSES_URL));
            startActivity(intent);
        }
    }

    protected void openForumsPage() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(FORUMS_PAGE_URL));
        startActivity(intent);
    }
}
