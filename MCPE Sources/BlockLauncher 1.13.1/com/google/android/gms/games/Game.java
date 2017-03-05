package com.google.android.gms.games;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcelable;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.data.Freezable;

public abstract interface Game
  extends Parcelable, Freezable<Game>
{
  public abstract boolean areSnapshotsEnabled();
  
  public abstract int getAchievementTotalCount();
  
  public abstract String getApplicationId();
  
  public abstract String getDescription();
  
  public abstract void getDescription(CharArrayBuffer paramCharArrayBuffer);
  
  public abstract String getDeveloperName();
  
  public abstract void getDeveloperName(CharArrayBuffer paramCharArrayBuffer);
  
  public abstract String getDisplayName();
  
  public abstract void getDisplayName(CharArrayBuffer paramCharArrayBuffer);
  
  public abstract Uri getFeaturedImageUri();
  
  @Deprecated
  @KeepName
  public abstract String getFeaturedImageUrl();
  
  public abstract Uri getHiResImageUri();
  
  @Deprecated
  @KeepName
  public abstract String getHiResImageUrl();
  
  public abstract Uri getIconImageUri();
  
  @Deprecated
  @KeepName
  public abstract String getIconImageUrl();
  
  public abstract int getLeaderboardCount();
  
  public abstract String getPrimaryCategory();
  
  public abstract String getSecondaryCategory();
  
  public abstract String getThemeColor();
  
  public abstract boolean hasGamepadSupport();
  
  public abstract boolean isMuted();
  
  public abstract boolean isRealTimeMultiplayerEnabled();
  
  public abstract boolean isTurnBasedMultiplayerEnabled();
  
  public abstract String zzvA();
  
  public abstract int zzvB();
  
  public abstract boolean zzvx();
  
  public abstract boolean zzvy();
  
  public abstract boolean zzvz();
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\google\android\gms\games\Game.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */