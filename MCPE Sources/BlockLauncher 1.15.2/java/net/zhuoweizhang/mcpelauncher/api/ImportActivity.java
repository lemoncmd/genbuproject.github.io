package net.zhuoweizhang.mcpelauncher.api;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import java.io.File;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;

public abstract class ImportActivity extends Activity implements OnClickListener {
    public Button cancelButton;
    public TextView installConfirmText;
    public File mFile = null;
    public Button okButton;
    public TextView patchNameText;

    protected abstract void startImport();

    public void onCreate(Bundle icicle) {
        Utils.setLanguageOverride();
        super.onCreate(icicle);
        setContentView(R.layout.import_confirm);
        this.okButton = (Button) findViewById(R.id.ok_button);
        this.cancelButton = (Button) findViewById(R.id.cancel_button);
        this.okButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.patchNameText = (TextView) findViewById(R.id.app_name);
        this.installConfirmText = (TextView) findViewById(R.id.install_confirm_question);
        if (getIntent() == null) {
            finish();
            return;
        }
        this.mFile = FileUtils.getFile(getIntent().getData());
        if (this.mFile == null || !this.mFile.canRead()) {
            finish();
            return;
        }
        this.patchNameText.setText(this.mFile.getName());
        setResult(0);
    }

    public void onClick(View v) {
        if (v.equals(this.cancelButton)) {
            finish();
        } else if (v.equals(this.okButton)) {
            this.okButton.setEnabled(false);
            this.cancelButton.setEnabled(false);
            startImport();
        }
    }
}
