package com.google.gson;

public abstract interface ExclusionStrategy
{
  public abstract boolean shouldSkipClass(Class<?> paramClass);
  
  public abstract boolean shouldSkipField(FieldAttributes paramFieldAttributes);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\MCPE.jar!\com\google\gson\ExclusionStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */