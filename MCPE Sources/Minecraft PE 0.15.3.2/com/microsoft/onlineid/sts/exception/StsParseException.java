package com.microsoft.onlineid.sts.exception;

import java.util.Locale;

public class StsParseException
  extends InvalidResponseException
{
  private static final long serialVersionUID = 1L;
  
  public StsParseException() {}
  
  public StsParseException(String paramString, Throwable paramThrowable, Object... paramVarArgs)
  {
    super(String.format(Locale.US, paramString, paramVarArgs), paramThrowable);
  }
  
  public StsParseException(String paramString, Object... paramVarArgs)
  {
    super(String.format(Locale.US, paramString, paramVarArgs));
  }
  
  public StsParseException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\Minecraft PE 0.15.3.2.jar!\com\microsoft\onlineid\sts\exception\StsParseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */