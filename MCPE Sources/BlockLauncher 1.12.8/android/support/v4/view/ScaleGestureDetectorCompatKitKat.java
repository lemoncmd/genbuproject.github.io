package android.support.v4.view;

import android.view.ScaleGestureDetector;

class ScaleGestureDetectorCompatKitKat
{
  public static boolean isQuickScaleEnabled(Object paramObject)
  {
    return ((ScaleGestureDetector)paramObject).isQuickScaleEnabled();
  }
  
  public static void setQuickScaleEnabled(Object paramObject, boolean paramBoolean)
  {
    ((ScaleGestureDetector)paramObject).setQuickScaleEnabled(paramBoolean);
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\android\support\v4\view\ScaleGestureDetectorCompatKitKat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */