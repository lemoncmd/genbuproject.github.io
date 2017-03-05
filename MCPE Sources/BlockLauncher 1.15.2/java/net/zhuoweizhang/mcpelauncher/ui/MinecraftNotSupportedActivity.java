package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import net.zhuoweizhang.mcpelauncher.R;

public class MinecraftNotSupportedActivity extends Activity {
    public TextView theText;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.no_minecraft);
        this.theText = (TextView) findViewById(R.id.no_minecraft_text);
        Intent intent = getIntent();
        String minecraftVersion = intent.getStringExtra("minecraftVersion");
        this.theText.setText(getResources().getString(R.string.minecraft_version_not_supported).toString().replaceAll("MINECRAFT_VERSION", minecraftVersion).replaceAll("SUPPORTED_VERSION", intent.getStringExtra("supportedVersion")));
    }
}
