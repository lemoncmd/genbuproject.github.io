package com.microsoft.cll.android;

import com.microsoft.telemetry.Base;
import java.util.EnumSet;
import java.util.List;

public abstract interface ISingletonCll
{
  public abstract void SubscribeCllEvents(ICllEvents paramICllEvents);
  
  public abstract String getAppUserId();
  
  public abstract void log(Base paramBase, EventEnums.Latency paramLatency, EventEnums.Persistence paramPersistence, EnumSet<EventEnums.Sensitivity> paramEnumSet, double paramDouble, List<String> paramList);
  
  public abstract void pause();
  
  public abstract void resume();
  
  public abstract void send();
  
  public abstract void setAppUserId(String paramString);
  
  public abstract void setDebugVerbosity(Verbosity paramVerbosity);
  
  public abstract void setEndpointUrl(String paramString);
  
  public abstract void setExperimentId(String paramString);
  
  public abstract void setXuidCallback(ITicketCallback paramITicketCallback);
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void synchronize();
  
  public abstract void useLegacyCS(boolean paramBoolean);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\Minecraft PE 0.15.3.2.jar!\com\microsoft\cll\android\ISingletonCll.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */