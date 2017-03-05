package com.facebook.appevents.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.facebook.FacebookSdk;
import java.util.UUID;

class SessionInfo
{
  private static final String INTERRUPTION_COUNT_KEY = "com.facebook.appevents.SessionInfo.interruptionCount";
  private static final String LAST_SESSION_INFO_END_KEY = "com.facebook.appevents.SessionInfo.sessionEndTime";
  private static final String LAST_SESSION_INFO_START_KEY = "com.facebook.appevents.SessionInfo.sessionStartTime";
  private static final String SESSION_ID_KEY = "com.facebook.appevents.SessionInfo.sessionId";
  private Long diskRestoreTime;
  private int interruptionCount;
  private UUID sessionId;
  private Long sessionLastEventTime;
  private Long sessionStartTime;
  private SourceApplicationInfo sourceApplicationInfo;
  
  public SessionInfo(Long paramLong1, Long paramLong2)
  {
    this(paramLong1, paramLong2, UUID.randomUUID());
  }
  
  public SessionInfo(Long paramLong1, Long paramLong2, UUID paramUUID)
  {
    this.sessionStartTime = paramLong1;
    this.sessionLastEventTime = paramLong2;
    this.sessionId = paramUUID;
  }
  
  public static void clearSavedSessionFromDisk()
  {
    SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(FacebookSdk.getApplicationContext()).edit();
    localEditor.remove("com.facebook.appevents.SessionInfo.sessionStartTime");
    localEditor.remove("com.facebook.appevents.SessionInfo.sessionEndTime");
    localEditor.remove("com.facebook.appevents.SessionInfo.interruptionCount");
    localEditor.remove("com.facebook.appevents.SessionInfo.sessionId");
    localEditor.apply();
    SourceApplicationInfo.clearSavedSourceApplicationInfoFromDisk();
  }
  
  public static SessionInfo getStoredSessionInfo()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(FacebookSdk.getApplicationContext());
    long l1 = localSharedPreferences.getLong("com.facebook.appevents.SessionInfo.sessionStartTime", 0L);
    long l2 = localSharedPreferences.getLong("com.facebook.appevents.SessionInfo.sessionEndTime", 0L);
    String str = localSharedPreferences.getString("com.facebook.appevents.SessionInfo.sessionId", null);
    if ((l1 == 0L) || (l2 == 0L) || (str == null)) {
      return null;
    }
    SessionInfo localSessionInfo = new SessionInfo(Long.valueOf(l1), Long.valueOf(l2));
    localSessionInfo.interruptionCount = localSharedPreferences.getInt("com.facebook.appevents.SessionInfo.interruptionCount", 0);
    localSessionInfo.sourceApplicationInfo = SourceApplicationInfo.getStoredSourceApplicatioInfo();
    localSessionInfo.diskRestoreTime = Long.valueOf(System.currentTimeMillis());
    localSessionInfo.sessionId = UUID.fromString(str);
    return localSessionInfo;
  }
  
  public long getDiskRestoreTime()
  {
    if (this.diskRestoreTime == null) {
      return 0L;
    }
    return this.diskRestoreTime.longValue();
  }
  
  public int getInterruptionCount()
  {
    return this.interruptionCount;
  }
  
  public UUID getSessionId()
  {
    return this.sessionId;
  }
  
  public Long getSessionLastEventTime()
  {
    return this.sessionLastEventTime;
  }
  
  public long getSessionLength()
  {
    if ((this.sessionStartTime == null) || (this.sessionLastEventTime == null)) {
      return 0L;
    }
    return this.sessionLastEventTime.longValue() - this.sessionStartTime.longValue();
  }
  
  public Long getSessionStartTime()
  {
    return this.sessionStartTime;
  }
  
  public SourceApplicationInfo getSourceApplicationInfo()
  {
    return this.sourceApplicationInfo;
  }
  
  public void incrementInterruptionCount()
  {
    this.interruptionCount += 1;
  }
  
  public void setSessionLastEventTime(Long paramLong)
  {
    this.sessionLastEventTime = paramLong;
  }
  
  public void setSessionStartTime(Long paramLong)
  {
    this.sessionStartTime = paramLong;
  }
  
  public void setSourceApplicationInfo(SourceApplicationInfo paramSourceApplicationInfo)
  {
    this.sourceApplicationInfo = paramSourceApplicationInfo;
  }
  
  public void writeSessionToDisk()
  {
    SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(FacebookSdk.getApplicationContext()).edit();
    localEditor.putLong("com.facebook.appevents.SessionInfo.sessionStartTime", this.sessionStartTime.longValue());
    localEditor.putLong("com.facebook.appevents.SessionInfo.sessionEndTime", this.sessionLastEventTime.longValue());
    localEditor.putInt("com.facebook.appevents.SessionInfo.interruptionCount", this.interruptionCount);
    localEditor.putString("com.facebook.appevents.SessionInfo.sessionId", this.sessionId.toString());
    localEditor.apply();
    if (this.sourceApplicationInfo != null) {
      this.sourceApplicationInfo.writeSourceApplicationInfoToDisk();
    }
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\Minecraft PE 0.15.3.2.jar!\com\facebook\appevents\internal\SessionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */