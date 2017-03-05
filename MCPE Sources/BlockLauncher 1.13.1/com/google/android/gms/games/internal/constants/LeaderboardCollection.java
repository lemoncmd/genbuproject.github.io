package com.google.android.gms.games.internal.constants;

public final class LeaderboardCollection
{
  public static String zzgw(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown leaderboard collection: " + paramInt);
    case 0: 
      return "PUBLIC";
    case 1: 
      return "SOCIAL";
    }
    return "SOCIAL_1P";
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.13.1.jar!\com\google\android\gms\games\internal\constants\LeaderboardCollection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */