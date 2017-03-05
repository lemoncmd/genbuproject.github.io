package com.microsoft.onlineid.internal.configuration;

public enum Flight
{
  QRCode(11, "qr_code");
  
  private final int _flightID;
  private final String _flightName;
  
  private Flight(int paramInt, String paramString)
  {
    this._flightID = paramInt;
    this._flightName = paramString;
  }
  
  public int getFlightID()
  {
    return this._flightID;
  }
  
  public String getFlightName()
  {
    return this._flightName;
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\microsoft\onlineid\internal\configuration\Flight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */