package com.microsoft.onlineid.interop.xbox.util;

import android.app.LoaderManager.LoaderCallbacks;
import com.microsoft.onlineid.interop.xbox.toolkit.BitmapLoader.Cache;

public class BitmapLoaderInfo
  implements ErrorHelper.LoaderInfo
{
  private final LoaderManager.LoaderCallbacks<?> callbacks;
  
  public BitmapLoaderInfo(LoaderManager.LoaderCallbacks<?> paramLoaderCallbacks)
  {
    this.callbacks = paramLoaderCallbacks;
  }
  
  public void clearCache(Object paramObject)
  {
    synchronized ()
    {
      ???.remove(paramObject);
      return;
    }
  }
  
  public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks()
  {
    return this.callbacks;
  }
  
  public boolean hasCachedData(Object paramObject)
  {
    for (;;)
    {
      synchronized ()
      {
        if (???.get(paramObject) != null)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\microsoft\onlineid\interop\xbox\util\BitmapLoaderInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */