package com.google.gson;

import java.lang.reflect.Field;

public abstract interface FieldNamingStrategy
{
  public abstract String translateName(Field paramField);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\MCPE.jar!\com\google\gson\FieldNamingStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */