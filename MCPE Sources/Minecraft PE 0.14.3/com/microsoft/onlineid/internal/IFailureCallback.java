package com.microsoft.onlineid.internal;

import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;

public abstract interface IFailureCallback
{
  public abstract void onFailure(AuthenticationException paramAuthenticationException, Bundle paramBundle);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\MCPE.jar!\com\microsoft\onlineid\internal\IFailureCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */