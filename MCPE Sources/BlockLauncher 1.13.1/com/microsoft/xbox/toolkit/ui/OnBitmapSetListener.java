package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.widget.ImageView;

public abstract interface OnBitmapSetListener
{
  public abstract void onAfterImageSet(ImageView paramImageView, Bitmap paramBitmap);
  
  public abstract void onBeforeImageSet(ImageView paramImageView, Bitmap paramBitmap);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\microsoft\xbox\toolkit\ui\OnBitmapSetListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */