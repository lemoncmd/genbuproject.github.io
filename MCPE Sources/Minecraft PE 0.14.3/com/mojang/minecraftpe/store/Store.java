package com.mojang.minecraftpe.store;

public abstract interface Store
{
  public abstract void destructor();
  
  public abstract String getStoreId();
  
  public abstract void purchase(String paramString);
  
  public abstract void queryProducts(String[] paramArrayOfString);
  
  public abstract void queryPurchases();
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\MCPE.jar!\com\mojang\minecraftpe\store\Store.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */