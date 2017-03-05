package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.zhuoweizhang.mcpelauncher.R;

public class NoMinecraftActivity extends Activity {
    private String learnmoreUri = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.no_minecraft);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if (message != null) {
            ((TextView) findViewById(R.id.no_minecraft_text)).setText(message);
        }
        this.learnmoreUri = intent.getStringExtra("learnmore_uri");
        if (this.learnmoreUri != null) {
            ((Button) findViewById(R.id.no_minecraft_learn_more)).setVisibility(0);
        }
    }

    public void learnMoreClicked(View v) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.learnmoreUri)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
