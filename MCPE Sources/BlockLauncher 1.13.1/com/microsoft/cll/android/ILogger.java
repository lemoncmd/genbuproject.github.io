package com.microsoft.cll.android;

public abstract interface ILogger
{
  public abstract void error(String paramString1, String paramString2);
  
  public abstract Verbosity getVerbosity();
  
  public abstract void info(String paramString1, String paramString2);
  
  public abstract void setVerbosity(Verbosity paramVerbosity);
  
  public abstract void warn(String paramString1, String paramString2);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\microsoft\cll\android\ILogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */