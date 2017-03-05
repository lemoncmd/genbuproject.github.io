package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.views.PaintView;
import org.mozilla.javascript.Token;

public class PaintActivity extends Activity {
    public static final String EXTRA_IMAGE_URI = "imageUri";
    private static final int MENU_CLEAR_ID = 3;
    private static final int MENU_SAVE_ID = 1;
    private static final int MENU_UNDO_ID = 2;
    private String mImageName;
    private PaintView mPaintView;

    private String determineFilename(Uri uri, String str) {
        String str2 = null;
        String[] strArr = new String[MENU_SAVE_ID];
        strArr[0] = "_data";
        Cursor query = getApplicationContext().getContentResolver().query(uri, strArr, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    str2 = query.getString(0);
                }
                query.close();
            } catch (Throwable th) {
                query.close();
            }
        }
        return str2 == null ? str : new File(str2).getName();
    }

    private void makeResult() {
        File file = new File(getCacheDir(), Util.LOG_IDENTIFIER);
        file.mkdir();
        File file2 = new File(file, this.mImageName + ".jpg");
        int i = MENU_SAVE_ID;
        while (file2.exists()) {
            file2 = new File(file, this.mImageName + "_" + i + ".jpg");
            i += MENU_SAVE_ID;
        }
        this.mPaintView.setDrawingCacheEnabled(true);
        final Bitmap drawingCache = this.mPaintView.getDrawingCache();
        AnonymousClass2 anonymousClass2 = new AsyncTask<File, Void, Void>() {
            protected Void doInBackground(File... fileArr) {
                try {
                    OutputStream fileOutputStream = new FileOutputStream(fileArr[0]);
                    drawingCache.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                    HockeyLog.error("Could not save image.", e);
                }
                return null;
            }
        };
        File[] fileArr = new File[MENU_SAVE_ID];
        fileArr[0] = file2;
        anonymousClass2.execute(fileArr);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_URI, Uri.fromFile(file2));
        if (getParent() == null) {
            setResult(-1, intent);
        } else {
            getParent().setResult(-1, intent);
        }
        finish();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null || extras.getParcelable(EXTRA_IMAGE_URI) == null) {
            HockeyLog.error("Can't set up PaintActivity as image extra was not provided!");
            return;
        }
        Uri uri = (Uri) extras.getParcelable(EXTRA_IMAGE_URI);
        this.mImageName = determineFilename(uri, uri.getLastPathSegment());
        int i = getResources().getDisplayMetrics().widthPixels;
        int i2 = getResources().getDisplayMetrics().heightPixels;
        int i3 = i > i2 ? 0 : MENU_SAVE_ID;
        int determineOrientation = PaintView.determineOrientation(getContentResolver(), uri);
        setRequestedOrientation(determineOrientation);
        if (i3 != determineOrientation) {
            HockeyLog.debug("Image loading skipped because activity will be destroyed for orientation change.");
            return;
        }
        this.mPaintView = new PaintView(this, uri, i, i2);
        View linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LayoutParams(-1, -1));
        linearLayout.setGravity(17);
        linearLayout.setOrientation(MENU_SAVE_ID);
        View linearLayout2 = new LinearLayout(this);
        linearLayout2.setLayoutParams(new LayoutParams(-1, -1));
        linearLayout2.setGravity(17);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2);
        linearLayout2.addView(this.mPaintView);
        setContentView(linearLayout);
        Toast.makeText(this, getString(R.string.hockeyapp_paint_indicator_toast), MENU_SAVE_ID).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SAVE_ID, 0, getString(R.string.hockeyapp_paint_menu_save));
        menu.add(0, MENU_UNDO_ID, 0, getString(R.string.hockeyapp_paint_menu_undo));
        menu.add(0, MENU_CLEAR_ID, 0, getString(R.string.hockeyapp_paint_menu_clear));
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || this.mPaintView.isClear()) {
            return super.onKeyDown(i, keyEvent);
        }
        OnClickListener anonymousClass1 = new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case -2:
                        PaintActivity.this.finish();
                        return;
                    case Token.ERROR /*-1*/:
                        PaintActivity.this.makeResult();
                        return;
                    default:
                        return;
                }
            }
        };
        new Builder(this).setMessage(R.string.hockeyapp_paint_dialog_message).setPositiveButton(R.string.hockeyapp_paint_dialog_positive_button, anonymousClass1).setNegativeButton(R.string.hockeyapp_paint_dialog_negative_button, anonymousClass1).setNeutralButton(R.string.hockeyapp_paint_dialog_neutral_button, anonymousClass1).show();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_SAVE_ID /*1*/:
                makeResult();
                return true;
            case MENU_UNDO_ID /*2*/:
                this.mPaintView.undo();
                return true;
            case MENU_CLEAR_ID /*3*/:
                this.mPaintView.clearImage();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
