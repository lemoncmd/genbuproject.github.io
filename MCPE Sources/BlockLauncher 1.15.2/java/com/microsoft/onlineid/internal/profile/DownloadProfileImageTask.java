package com.microsoft.onlineid.internal.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.KnownEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadProfileImageTask extends AsyncTask<Void, Void, Bitmap> {
    public static final String UserTileExtension = ".png";
    protected static final String UserTileUrlFormat = "https://storage.%2$s/users/0x%1$s/myprofile/expressionprofile/profilephoto:UserTileStatic";
    private final AuthenticatorUserAccount _account;
    private final Context _applicationContext;
    private final ImageView _imageView;
    private boolean _newImageDownloaded;
    private final ServerConfig _serverConfig;
    private final Transport _transport;
    private final String _userTileFileName;

    public DownloadProfileImageTask(Context context, AuthenticatorUserAccount authenticatorUserAccount, ImageView imageView) {
        this._applicationContext = context;
        this._account = authenticatorUserAccount;
        this._transport = new TransportFactory(context).createTransport();
        this._serverConfig = new ServerConfig(context);
        this._imageView = imageView;
        this._userTileFileName = this._account.getPuid() + UserTileExtension;
        this._newImageDownloaded = false;
    }

    DownloadProfileImageTask(AuthenticatorUserAccount authenticatorUserAccount, Transport transport, ServerConfig serverConfig) {
        this._applicationContext = null;
        this._account = authenticatorUserAccount;
        this._transport = transport;
        this._serverConfig = serverConfig;
        this._imageView = null;
        this._userTileFileName = null;
        this._newImageDownloaded = false;
    }

    protected Bitmap doInBackground(Void... voidArr) {
        InputStream responseStream;
        Throwable e;
        InputStream inputStream = null;
        try {
            Object[] objArr = new Object[2];
            objArr[0] = this._account.getCid();
            objArr[1] = this._serverConfig.getEnvironment().equals(KnownEnvironment.Production.getEnvironment()) ? "live.com" : "live-int.com";
            this._transport.openGetRequest(new URL(String.format(UserTileUrlFormat, objArr)));
            this._transport.setUseCaches(true);
            responseStream = this._transport.getResponseStream();
            try {
                Bitmap decodeStream = BitmapFactory.decodeStream(responseStream);
                saveUserTileImage(decodeStream);
                this._newImageDownloaded = true;
                this._transport.closeConnection();
                if (responseStream == null) {
                    return decodeStream;
                }
                try {
                    responseStream.close();
                    return decodeStream;
                } catch (Throwable e2) {
                    Logger.error("Error closing response stream.", e2);
                    return decodeStream;
                }
            } catch (MalformedURLException e3) {
                e = e3;
                try {
                    Logger.error("Error downloading image from url.", e);
                    this._transport.closeConnection();
                    if (responseStream != null) {
                        try {
                            responseStream.close();
                        } catch (Throwable e4) {
                            Logger.error("Error closing response stream.", e4);
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    e4 = th;
                    inputStream = responseStream;
                    this._transport.closeConnection();
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e22) {
                            Logger.error("Error closing response stream.", e22);
                        }
                    }
                    throw e4;
                }
            } catch (NetworkException e5) {
                e4 = e5;
                Logger.error("Error downloading image from url.", e4);
                this._transport.closeConnection();
                if (responseStream != null) {
                    try {
                        responseStream.close();
                    } catch (Throwable e42) {
                        Logger.error("Error closing response stream.", e42);
                    }
                }
                return null;
            }
        } catch (MalformedURLException e6) {
            e42 = e6;
            responseStream = null;
            Logger.error("Error downloading image from url.", e42);
            this._transport.closeConnection();
            if (responseStream != null) {
                responseStream.close();
            }
            return null;
        } catch (NetworkException e7) {
            e42 = e7;
            responseStream = null;
            Logger.error("Error downloading image from url.", e42);
            this._transport.closeConnection();
            if (responseStream != null) {
                responseStream.close();
            }
            return null;
        } catch (Throwable th2) {
            e42 = th2;
            this._transport.closeConnection();
            if (inputStream != null) {
                inputStream.close();
            }
            throw e42;
        }
    }

    Bitmap getSavedUserTileImage() {
        FileInputStream openFileInput;
        Throwable th;
        Bitmap bitmap = null;
        try {
            openFileInput = this._applicationContext.openFileInput(this._userTileFileName);
            try {
                bitmap = BitmapFactory.decodeStream(openFileInput);
                if (openFileInput != null) {
                    try {
                        openFileInput.close();
                    } catch (Throwable e) {
                        Logger.error("Error closing file input stream.", e);
                    }
                }
            } catch (FileNotFoundException e2) {
                if (openFileInput != null) {
                    try {
                        openFileInput.close();
                    } catch (Throwable e3) {
                        Logger.error("Error closing file input stream.", e3);
                    }
                }
                return bitmap;
            } catch (Throwable th2) {
                th = th2;
                if (openFileInput != null) {
                    try {
                        openFileInput.close();
                    } catch (Throwable e32) {
                        Logger.error("Error closing file input stream.", e32);
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e4) {
            openFileInput = bitmap;
            if (openFileInput != null) {
                openFileInput.close();
            }
            return bitmap;
        } catch (Throwable e322) {
            Throwable th3 = e322;
            openFileInput = bitmap;
            th = th3;
            if (openFileInput != null) {
                openFileInput.close();
            }
            throw th;
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            HttpResponseCache installed = HttpResponseCache.getInstalled();
            if (installed != null) {
                ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.LoadProfileImage, installed.getHitCount() == 0 ? "from network" : "from cache");
            }
            this._imageView.setImageBitmap(bitmap);
            return;
        }
        File fileStreamPath = this._applicationContext.getFileStreamPath(this._userTileFileName);
        if (fileStreamPath.exists() && this._newImageDownloaded) {
            ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.LoadProfileImage, "deleted");
            fileStreamPath.delete();
            this._imageView.setImageDrawable(this._applicationContext.getResources().getDrawable(this._applicationContext.getResources().getIdentifier("msa_default_user_tile", "drawable", this._applicationContext.getPackageName())));
        }
    }

    protected void onPreExecute() {
        Bitmap savedUserTileImage = getSavedUserTileImage();
        if (savedUserTileImage != null) {
            this._imageView.setImageBitmap(savedUserTileImage);
        }
    }

    void saveUserTileImage(Bitmap bitmap) {
        if (bitmap != null) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = this._applicationContext.openFileOutput(this._userTileFileName, 0);
                bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable e) {
                        Logger.error("Error closing file output stream.", e);
                    }
                }
            } catch (Throwable e2) {
                Logger.error("Error saving user tile image.", e2);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable e22) {
                        Logger.error("Error closing file output stream.", e22);
                    }
                }
            } catch (Throwable th) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable e3) {
                        Logger.error("Error closing file output stream.", e3);
                    }
                }
            }
        }
    }
}
