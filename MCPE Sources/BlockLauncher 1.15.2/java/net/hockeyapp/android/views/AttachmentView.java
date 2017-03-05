package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.io.File;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.R;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.ImageUtils;

@SuppressLint({"ViewConstructor"})
public class AttachmentView extends FrameLayout {
    private static final int IMAGES_PER_ROW_LANDSCAPE = 2;
    private static final int IMAGES_PER_ROW_PORTRAIT = 3;
    private final FeedbackAttachment mAttachment;
    private final Uri mAttachmentUri;
    private final Context mContext;
    private final String mFilename;
    private int mGap;
    private ImageView mImageView;
    private int mMaxHeightLandscape;
    private int mMaxHeightPortrait;
    private int mOrientation;
    private final ViewGroup mParent;
    private TextView mTextView;
    private int mWidthLandscape;
    private int mWidthPortrait;

    public AttachmentView(Context context, ViewGroup viewGroup, Uri uri, boolean z) {
        super(context);
        this.mContext = context;
        this.mParent = viewGroup;
        this.mAttachment = null;
        this.mAttachmentUri = uri;
        this.mFilename = uri.getLastPathSegment();
        calculateDimensions(20);
        initializeView(context, z);
        this.mTextView.setText(this.mFilename);
        new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... voidArr) {
                return AttachmentView.this.loadImageThumbnail();
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    AttachmentView.this.configureViewForThumbnail(bitmap, false);
                } else {
                    AttachmentView.this.configureViewForPlaceholder(false);
                }
            }
        }.execute(new Void[0]);
    }

    public AttachmentView(Context context, ViewGroup viewGroup, FeedbackAttachment feedbackAttachment, boolean z) {
        super(context);
        this.mContext = context;
        this.mParent = viewGroup;
        this.mAttachment = feedbackAttachment;
        this.mAttachmentUri = Uri.fromFile(new File(Constants.getHockeyAppStorageDir(), feedbackAttachment.getCacheId()));
        this.mFilename = feedbackAttachment.getFilename();
        calculateDimensions(30);
        initializeView(context, z);
        this.mOrientation = 0;
        this.mTextView.setText(R.string.hockeyapp_feedback_attachment_loading);
        configureViewForPlaceholder(false);
    }

    private void calculateDimensions(int i) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mGap = Math.round(TypedValue.applyDimension(1, 10.0f, displayMetrics));
        int round = Math.round(TypedValue.applyDimension(1, (float) i, displayMetrics));
        int i2 = displayMetrics.widthPixels;
        int i3 = (i2 - (round * IMAGES_PER_ROW_LANDSCAPE)) - (this.mGap * IMAGES_PER_ROW_LANDSCAPE);
        i2 = (i2 - (round * IMAGES_PER_ROW_LANDSCAPE)) - this.mGap;
        this.mWidthPortrait = i3 / IMAGES_PER_ROW_PORTRAIT;
        this.mWidthLandscape = i2 / IMAGES_PER_ROW_LANDSCAPE;
        this.mMaxHeightPortrait = this.mWidthPortrait * IMAGES_PER_ROW_LANDSCAPE;
        this.mMaxHeightLandscape = this.mWidthLandscape;
    }

    private void configureViewForPlaceholder(final boolean z) {
        this.mTextView.setMaxWidth(this.mWidthPortrait);
        this.mTextView.setMinWidth(this.mWidthPortrait);
        this.mImageView.setLayoutParams(new LayoutParams(-2, -2));
        this.mImageView.setAdjustViewBounds(false);
        this.mImageView.setBackgroundColor(Color.parseColor("#eeeeee"));
        this.mImageView.setMinimumHeight((int) (1.2f * ((float) this.mWidthPortrait)));
        this.mImageView.setMinimumWidth(this.mWidthPortrait);
        this.mImageView.setScaleType(ScaleType.FIT_CENTER);
        this.mImageView.setImageDrawable(getSystemIcon("ic_menu_attachment"));
        this.mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (z) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(AttachmentView.this.mAttachmentUri, FileChooserActivity.MIME_TYPE_ALL);
                    AttachmentView.this.mContext.startActivity(intent);
                }
            }
        });
    }

    private void configureViewForThumbnail(Bitmap bitmap, final boolean z) {
        int i = this.mOrientation == 1 ? this.mWidthLandscape : this.mWidthPortrait;
        int i2 = this.mOrientation == 1 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait;
        this.mTextView.setMaxWidth(i);
        this.mTextView.setMinWidth(i);
        this.mImageView.setLayoutParams(new LayoutParams(-2, -2));
        this.mImageView.setAdjustViewBounds(true);
        this.mImageView.setMinimumWidth(i);
        this.mImageView.setMaxWidth(i);
        this.mImageView.setMaxHeight(i2);
        this.mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        this.mImageView.setImageBitmap(bitmap);
        this.mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (z) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(AttachmentView.this.mAttachmentUri, FileUtils.MIME_TYPE_IMAGE);
                    AttachmentView.this.mContext.startActivity(intent);
                }
            }
        });
    }

    private Drawable getSystemIcon(String str) {
        return VERSION.SDK_INT >= 21 ? getResources().getDrawable(getResources().getIdentifier(str, "drawable", AddAccountActivity.PlatformName), this.mContext.getTheme()) : getResources().getDrawable(getResources().getIdentifier(str, "drawable", AddAccountActivity.PlatformName));
    }

    private void initializeView(Context context, boolean z) {
        setLayoutParams(new LayoutParams(-2, -2, 80));
        setPadding(0, this.mGap, 0, 0);
        this.mImageView = new ImageView(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(-1, -2, 80));
        linearLayout.setGravity(8388611);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Color.parseColor("#80262626"));
        this.mTextView = new TextView(context);
        this.mTextView.setLayoutParams(new LayoutParams(-1, -2, 17));
        this.mTextView.setGravity(17);
        this.mTextView.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_white));
        this.mTextView.setSingleLine();
        this.mTextView.setEllipsize(TruncateAt.MIDDLE);
        if (z) {
            View imageButton = new ImageButton(context);
            imageButton.setLayoutParams(new LayoutParams(-1, -2, 80));
            imageButton.setAdjustViewBounds(true);
            imageButton.setImageDrawable(getSystemIcon("ic_menu_delete"));
            imageButton.setBackgroundResource(0);
            imageButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AttachmentView.this.remove();
                }
            });
            linearLayout.addView(imageButton);
        }
        linearLayout.addView(this.mTextView);
        addView(this.mImageView);
        addView(linearLayout);
    }

    private Bitmap loadImageThumbnail() {
        try {
            this.mOrientation = ImageUtils.determineOrientation(this.mContext, this.mAttachmentUri);
            return ImageUtils.decodeSampledBitmap(this.mContext, this.mAttachmentUri, this.mOrientation == 1 ? this.mWidthLandscape : this.mWidthPortrait, this.mOrientation == 1 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait);
        } catch (Throwable th) {
            return null;
        }
    }

    public FeedbackAttachment getAttachment() {
        return this.mAttachment;
    }

    public Uri getAttachmentUri() {
        return this.mAttachmentUri;
    }

    public int getEffectiveMaxHeight() {
        return this.mOrientation == 1 ? this.mMaxHeightLandscape : this.mMaxHeightPortrait;
    }

    public int getGap() {
        return this.mGap;
    }

    public int getMaxHeightLandscape() {
        return this.mMaxHeightLandscape;
    }

    public int getMaxHeightPortrait() {
        return this.mMaxHeightPortrait;
    }

    public int getWidthLandscape() {
        return this.mWidthLandscape;
    }

    public int getWidthPortrait() {
        return this.mWidthPortrait;
    }

    public void remove() {
        this.mParent.removeView(this);
    }

    public void setImage(Bitmap bitmap, int i) {
        this.mTextView.setText(this.mFilename);
        this.mOrientation = i;
        if (bitmap == null) {
            configureViewForPlaceholder(true);
        } else {
            configureViewForThumbnail(bitmap, true);
        }
    }

    public void signalImageLoadingError() {
        this.mTextView.setText(R.string.hockeyapp_feedback_attachment_error);
    }
}
