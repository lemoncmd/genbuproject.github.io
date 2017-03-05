package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.Iterator;
import java.util.LinkedList;

public class XLEAnimationPackage
{
  private LinkedList<XLEAnimationEntry> animations = new LinkedList();
  private Runnable onAnimationEndRunnable;
  private boolean running = false;
  
  private int getRemainingAnimations()
  {
    Iterator localIterator = this.animations.iterator();
    int i = 0;
    while (localIterator.hasNext()) {
      if (!((XLEAnimationEntry)localIterator.next()).done) {
        i += 1;
      }
    }
    return i;
  }
  
  private void tryFinishAll()
  {
    if (getRemainingAnimations() == 0)
    {
      XLEAssert.assertTrue(this.running);
      this.running = false;
      this.onAnimationEndRunnable.run();
    }
  }
  
  public XLEAnimationPackage add(XLEAnimationPackage paramXLEAnimationPackage)
  {
    if (paramXLEAnimationPackage != null)
    {
      paramXLEAnimationPackage = paramXLEAnimationPackage.animations.iterator();
      while (paramXLEAnimationPackage.hasNext()) {
        add(((XLEAnimationEntry)paramXLEAnimationPackage.next()).animation);
      }
    }
    return this;
  }
  
  public void add(XLEAnimation paramXLEAnimation)
  {
    this.animations.add(new XLEAnimationEntry(paramXLEAnimation));
  }
  
  public void clearAnimation()
  {
    Iterator localIterator = this.animations.iterator();
    while (localIterator.hasNext()) {
      ((XLEAnimationEntry)localIterator.next()).clearAnimation();
    }
  }
  
  public void setOnAnimationEndRunnable(Runnable paramRunnable)
  {
    this.onAnimationEndRunnable = paramRunnable;
  }
  
  public void startAnimation()
  {
    if (!this.running) {}
    for (boolean bool = true;; bool = false)
    {
      XLEAssert.assertTrue(bool);
      this.running = true;
      Iterator localIterator = this.animations.iterator();
      while (localIterator.hasNext()) {
        ((XLEAnimationEntry)localIterator.next()).startAnimation();
      }
    }
  }
  
  private class XLEAnimationEntry
  {
    public XLEAnimation animation;
    public boolean done;
    public int iterationID;
    
    public XLEAnimationEntry(XLEAnimation paramXLEAnimation)
    {
      this.animation = paramXLEAnimation;
      this.iterationID = 0;
      this.done = false;
      paramXLEAnimation.setOnAnimationEnd(new Runnable()
      {
        public void run()
        {
          XLEAnimationPackage.XLEAnimationEntry.this.onAnimationEnded();
        }
      });
    }
    
    private void finish()
    {
      this.done = true;
      XLEAnimationPackage.this.tryFinishAll();
    }
    
    private void onAnimationEnded()
    {
      boolean bool2 = false;
      if (Thread.currentThread() == ThreadManager.UIThread) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        XLEAssert.assertTrue(bool1);
        bool1 = bool2;
        if (XLEAnimationPackage.this.onAnimationEndRunnable != null) {
          bool1 = true;
        }
        XLEAssert.assertTrue(bool1);
        ThreadManager.UIThreadPost(new Runnable()
        {
          public void run()
          {
            if (this.val$finishIterationID == XLEAnimationPackage.XLEAnimationEntry.this.iterationID) {
              XLEAnimationPackage.XLEAnimationEntry.this.finish();
            }
          }
        });
        return;
      }
    }
    
    public void clearAnimation()
    {
      this.iterationID += 1;
      this.animation.clear();
    }
    
    public void startAnimation()
    {
      this.animation.start();
    }
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\microsoft\xbox\toolkit\anim\XLEAnimationPackage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */