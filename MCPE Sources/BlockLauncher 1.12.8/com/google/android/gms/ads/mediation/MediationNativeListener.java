package com.google.android.gms.ads.mediation;

public abstract interface MediationNativeListener
{
  public abstract void onAdClicked(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdClosed(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdFailedToLoad(MediationNativeAdapter paramMediationNativeAdapter, int paramInt);
  
  public abstract void onAdLeftApplication(MediationNativeAdapter paramMediationNativeAdapter);
  
  public abstract void onAdLoaded(MediationNativeAdapter paramMediationNativeAdapter, NativeAdMapper paramNativeAdMapper);
  
  public abstract void onAdOpened(MediationNativeAdapter paramMediationNativeAdapter);
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\google\android\gms\ads\mediation\MediationNativeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */