package com.microsoft.onlineid.internal.sso.client.request;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.SsoServiceError;
import com.microsoft.onlineid.internal.sso.client.ServiceBindingException;
import com.microsoft.onlineid.internal.sso.client.ServiceFinder;
import com.microsoft.onlineid.internal.sso.service.IMsaSsoService;
import com.microsoft.onlineid.internal.sso.service.IMsaSsoService.Stub;
import com.microsoft.onlineid.internal.sso.service.MsaSsoService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import java.util.Locale;
import org.mozilla.javascript.Token;

public abstract class SingleSsoRequest<T> implements ServiceConnection {
    static final String MaxTriesErrorMessage = "Max SSO tries exceeded.";
    static final int MaxWaitTimeForServiceBindingInMillis = 3000;
    protected final Context _applicationContext;
    protected final Bundle _clientState;
    protected final ServerConfig _config;
    private final Object _lock = new Object();
    protected IMsaSsoService _msaSsoService;
    protected boolean _serviceConnected;
    protected final TypedStorage _storage;

    public SingleSsoRequest(Context context, Bundle bundle) {
        this._applicationContext = context;
        this._clientState = bundle;
        this._config = new ServerConfig(context);
        this._storage = new TypedStorage(context);
        this._msaSsoService = null;
    }

    protected static void checkForErrors(Bundle bundle) throws AuthenticationException {
        checkForErrors(bundle, true);
    }

    static void checkForErrors(Bundle bundle, boolean z) throws AuthenticationException {
        if (BundleMarshaller.hasError(bundle)) {
            if (z) {
                SsoServiceError ssoServiceError = SsoServiceError.get(bundle.getInt(BundleMarshaller.ErrorCodeKey));
                String string = bundle.getString(BundleMarshaller.ErrorMessageKey);
                Logger.error(String.format(Locale.US, "%s: %s, %s", new Object[]{ClientAnalytics.SsoError, ssoServiceError.name(), string}));
                ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoError, ssoServiceError.name() + ": " + string);
            }
            throw BundleMarshaller.exceptionFromBundle(bundle);
        }
    }

    protected boolean bind(SsoService ssoService) {
        Intent intent = new Intent(SsoService.SsoServiceIntent).setPackage(ssoService.getPackageName());
        Logger.info(this._applicationContext.getPackageName() + " attempting to bind to: " + ssoService.getPackageName() + " [" + getClass().getSimpleName() + "]");
        return this._applicationContext.bindService(intent, this, 1);
    }

    public Bundle getDefaultCallingParams() {
        Bundle bundle = new Bundle();
        try {
            Bundle bundle2 = this._applicationContext.getPackageManager().getServiceInfo(new ComponentName(this._applicationContext, MsaSsoService.class.getName()), Token.RESERVED).metaData;
            bundle.putString(BundleMarshaller.ClientPackageNameKey, this._applicationContext.getPackageName());
            bundle.putInt(BundleMarshaller.ClientSsoVersionKey, bundle2.getInt(ServiceFinder.SsoVersionMetaDataName));
            bundle.putString(BundleMarshaller.ClientSdkVersionKey, bundle2.getString(ServiceFinder.SdkVersionMetaDataName));
            bundle.putString(BundleMarshaller.ClientConfigVersionKey, this._config.getString(ServerConfig.Version));
            bundle.putLong(BundleMarshaller.ClientConfigLastDownloadedTimeKey, this._storage.readConfigLastDownloadedTime());
            bundle.putBundle(BundleMarshaller.ClientStateBundleKey, this._clientState);
        } catch (Throwable e) {
            Logger.error("Could not find calling SSO service meta-data.", e);
        }
        return bundle;
    }

    boolean getIsServiceConnected() {
        return this._serviceConnected;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this._msaSsoService = Stub.asInterface(iBinder);
        synchronized (this._lock) {
            this._serviceConnected = true;
            this._lock.notify();
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this._msaSsoService = null;
        this._serviceConnected = false;
    }

    public T performRequest(SsoService ssoService) throws AuthenticationException {
        int i = this._config.getInt(Int.MaxTriesForSsoRequestToSingleService);
        if (i < 1) {
            String str = "Invalid MaxTriesForSsoRequestToSingleService: " + i;
            Logger.error(str);
            ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, str);
            i = 1;
        }
        int i2 = 1;
        Throwable th = null;
        while (i2 <= i) {
            try {
                return tryPerformRequest(ssoService);
            } catch (ServiceBindingException e) {
                th = e;
                i2++;
            }
        }
        throw new ServiceBindingException(MaxTriesErrorMessage, th);
    }

    protected abstract T performRequestTask() throws AuthenticationException, RemoteException;

    public T tryPerformRequest(SsoService ssoService) throws AuthenticationException {
        Object obj = null;
        try {
            String str;
            if (bind(ssoService)) {
                obj = 1;
                synchronized (this._lock) {
                    if (!this._serviceConnected) {
                        this._lock.wait(3000);
                    }
                }
                if (this._serviceConnected) {
                    Logger.info("Bound to: " + ssoService.getPackageName());
                    T performRequestTask = performRequestTask();
                    unbind();
                    return performRequestTask;
                }
                str = "Timed out after " + String.valueOf(MaxWaitTimeForServiceBindingInMillis) + " milliseconds when trying to bind to: " + ssoService.getPackageName() + " [" + getClass().getSimpleName() + "]";
                Logger.warning(str);
                throw new ServiceBindingException(str);
            }
            str = "Failed to bind to " + ssoService.getPackageName() + " [" + getClass().getSimpleName() + "]";
            Logger.error(str);
            throw new ServiceBindingException(str);
        } catch (AuthenticationException e) {
            try {
                throw e;
            } catch (Throwable th) {
                if (obj != null) {
                    unbind();
                }
            }
        } catch (Throwable e2) {
            Logger.error("Caught a SecurityException while trying to bind to " + ssoService.getPackageName() + ", service may not be exported correctly." + " [" + getClass().getSimpleName() + "]", e2);
            throw new ServiceBindingException(e2);
        } catch (Throwable e22) {
            Logger.error("SSO service request threw an unhandled exception.", e22);
            throw new InternalException(e22);
        }
    }

    protected void unbind() {
        this._serviceConnected = false;
        this._msaSsoService = null;
        this._applicationContext.unbindService(this);
    }
}
