package com.microsoft.xbox.toolkit.anim;

import android.view.animation.Interpolator;

public class XLEInterpolator
  implements Interpolator
{
  private EasingMode easingMode;
  
  public XLEInterpolator(EasingMode paramEasingMode)
  {
    this.easingMode = paramEasingMode;
  }
  
  public float getInterpolation(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("should respect 0<=normalizedTime<=1");
    }
    switch (this.easingMode)
    {
    default: 
      return paramFloat;
    case ???: 
      return getInterpolationCore(paramFloat);
    case ???: 
      return 1.0F - getInterpolationCore(1.0F - paramFloat);
    }
    if (paramFloat < 0.5D) {
      return getInterpolationCore(paramFloat * 2.0F) / 2.0F;
    }
    return (1.0F - getInterpolationCore(2.0F - paramFloat * 2.0F)) / 2.0F + 0.5F;
  }
  
  protected float getInterpolationCore(float paramFloat)
  {
    return paramFloat;
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\Minecraft PE 0.15.3.2.jar!\com\microsoft\xbox\toolkit\anim\XLEInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */