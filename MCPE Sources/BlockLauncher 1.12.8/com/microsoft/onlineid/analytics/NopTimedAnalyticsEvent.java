package com.microsoft.onlineid.analytics;

public class NopTimedAnalyticsEvent
  implements ITimedAnalyticsEvent
{
  public void end() {}
  
  public NopTimedAnalyticsEvent setLabel(String paramString)
  {
    return this;
  }
  
  public NopTimedAnalyticsEvent start()
  {
    return this;
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\microsoft\onlineid\analytics\NopTimedAnalyticsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */