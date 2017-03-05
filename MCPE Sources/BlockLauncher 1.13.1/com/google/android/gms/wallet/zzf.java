package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<FullWalletRequest>
{
  static void zza(FullWalletRequest paramFullWalletRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzav(paramParcel);
    zzb.zzc(paramParcel, 1, paramFullWalletRequest.getVersionCode());
    zzb.zza(paramParcel, 2, paramFullWalletRequest.zzboo, false);
    zzb.zza(paramParcel, 3, paramFullWalletRequest.zzbop, false);
    zzb.zza(paramParcel, 4, paramFullWalletRequest.zzboz, paramInt, false);
    zzb.zzI(paramParcel, i);
  }
  
  public FullWalletRequest zzht(Parcel paramParcel)
  {
    Cart localCart = null;
    int j = zza.zzau(paramParcel);
    int i = 0;
    String str2 = null;
    String str1 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzat(paramParcel);
      switch (zza.zzca(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        str1 = zza.zzp(paramParcel, k);
        break;
      case 3: 
        str2 = zza.zzp(paramParcel, k);
        break;
      case 4: 
        localCart = (Cart)zza.zza(paramParcel, k, Cart.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza("Overread allowed size end=" + j, paramParcel);
    }
    return new FullWalletRequest(i, str1, str2, localCart);
  }
  
  public FullWalletRequest[] zzkR(int paramInt)
  {
    return new FullWalletRequest[paramInt];
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\google\android\gms\wallet\zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */