package com.mojang.minecraftpe.store;

public class NativeStoreListener
  implements StoreListener
{
  private long callback;
  
  public NativeStoreListener(long paramLong)
  {
    this.callback = paramLong;
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\mojang\minecraftpe\store\NativeStoreListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */