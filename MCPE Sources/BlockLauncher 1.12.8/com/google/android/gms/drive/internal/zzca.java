package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

public class zzca
  implements Parcelable.Creator<UpdateMetadataRequest>
{
  static void zza(UpdateMetadataRequest paramUpdateMetadataRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzav(paramParcel);
    zzb.zzc(paramParcel, 1, paramUpdateMetadataRequest.mVersionCode);
    zzb.zza(paramParcel, 2, paramUpdateMetadataRequest.zzaqj, paramInt, false);
    zzb.zza(paramParcel, 3, paramUpdateMetadataRequest.zzaqk, paramInt, false);
    zzb.zzI(paramParcel, i);
  }
  
  public UpdateMetadataRequest zzcb(Parcel paramParcel)
  {
    MetadataBundle localMetadataBundle = null;
    int j = zza.zzau(paramParcel);
    int i = 0;
    DriveId localDriveId = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zza.zzat(paramParcel);
      switch (zza.zzca(k))
      {
      default: 
        zza.zzb(paramParcel, k);
      }
      for (;;)
      {
        break;
        i = zza.zzg(paramParcel, k);
        continue;
        localDriveId = (DriveId)zza.zza(paramParcel, k, DriveId.CREATOR);
        continue;
        localMetadataBundle = (MetadataBundle)zza.zza(paramParcel, k, MetadataBundle.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza("Overread allowed size end=" + j, paramParcel);
    }
    return new UpdateMetadataRequest(i, localDriveId, localMetadataBundle);
  }
  
  public UpdateMetadataRequest[] zzdW(int paramInt)
  {
    return new UpdateMetadataRequest[paramInt];
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\google\android\gms\drive\internal\zzca.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */