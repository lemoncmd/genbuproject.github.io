package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzl
  implements Parcelable.Creator<Barcode.UrlBookmark>
{
  static void zza(Barcode.UrlBookmark paramUrlBookmark, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzav(paramParcel);
    zzb.zzc(paramParcel, 1, paramUrlBookmark.versionCode);
    zzb.zza(paramParcel, 2, paramUrlBookmark.title, false);
    zzb.zza(paramParcel, 3, paramUrlBookmark.url, false);
    zzb.zzI(paramParcel, paramInt);
  }
  
  public Barcode.UrlBookmark zzhh(Parcel paramParcel)
  {
    String str2 = null;
    int j = zza.zzau(paramParcel);
    int i = 0;
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
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza("Overread allowed size end=" + j, paramParcel);
    }
    return new Barcode.UrlBookmark(i, str1, str2);
  }
  
  public Barcode.UrlBookmark[] zzkE(int paramInt)
  {
    return new Barcode.UrlBookmark[paramInt];
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\google\android\gms\vision\barcode\zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */