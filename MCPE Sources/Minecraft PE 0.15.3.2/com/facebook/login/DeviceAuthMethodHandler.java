package com.facebook.login;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.app.FragmentActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class DeviceAuthMethodHandler
  extends LoginMethodHandler
{
  public static final Parcelable.Creator<DeviceAuthMethodHandler> CREATOR = new Parcelable.Creator()
  {
    public DeviceAuthMethodHandler createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DeviceAuthMethodHandler(paramAnonymousParcel);
    }
    
    public DeviceAuthMethodHandler[] newArray(int paramAnonymousInt)
    {
      return new DeviceAuthMethodHandler[paramAnonymousInt];
    }
  };
  private static ScheduledThreadPoolExecutor backgroundExecutor;
  
  protected DeviceAuthMethodHandler(Parcel paramParcel)
  {
    super(paramParcel);
  }
  
  DeviceAuthMethodHandler(LoginClient paramLoginClient)
  {
    super(paramLoginClient);
  }
  
  public static ScheduledThreadPoolExecutor getBackgroundExecutor()
  {
    try
    {
      if (backgroundExecutor == null) {
        backgroundExecutor = new ScheduledThreadPoolExecutor(1);
      }
      ScheduledThreadPoolExecutor localScheduledThreadPoolExecutor = backgroundExecutor;
      return localScheduledThreadPoolExecutor;
    }
    finally {}
  }
  
  private void showDialog(LoginClient.Request paramRequest)
  {
    DeviceAuthDialog localDeviceAuthDialog = new DeviceAuthDialog();
    localDeviceAuthDialog.show(this.loginClient.getActivity().getSupportFragmentManager(), "login_with_facebook");
    localDeviceAuthDialog.startLogin(paramRequest);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  String getNameForLogging()
  {
    return "device_auth";
  }
  
  public void onCancel()
  {
    LoginClient.Result localResult = LoginClient.Result.createCancelResult(this.loginClient.getPendingRequest(), "User canceled log in.");
    this.loginClient.completeAndValidate(localResult);
  }
  
  public void onError(Exception paramException)
  {
    paramException = LoginClient.Result.createErrorResult(this.loginClient.getPendingRequest(), null, paramException.getMessage());
    this.loginClient.completeAndValidate(paramException);
  }
  
  public void onSuccess(String paramString1, String paramString2, String paramString3, Collection<String> paramCollection1, Collection<String> paramCollection2, AccessTokenSource paramAccessTokenSource, Date paramDate1, Date paramDate2)
  {
    paramString1 = new AccessToken(paramString1, paramString2, paramString3, paramCollection1, paramCollection2, paramAccessTokenSource, paramDate1, paramDate2);
    paramString1 = LoginClient.Result.createTokenResult(this.loginClient.getPendingRequest(), paramString1);
    this.loginClient.completeAndValidate(paramString1);
  }
  
  boolean tryAuthorize(LoginClient.Request paramRequest)
  {
    showDialog(paramRequest);
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\Minecraft PE 0.15.3.2.jar!\com\facebook\login\DeviceAuthMethodHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */