package eu.chainfire.libsuperuser;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Application extends android.app.Application {
    private static Handler mApplicationHandler = new Handler();

    public static void toast(Context context, String message) {
        if (context != null) {
            if (!(context instanceof Application)) {
                context = context.getApplicationContext();
            }
            if (context instanceof Application) {
                final Context c = context;
                final String m = message;
                ((Application) context).runInApplicationThread(new Runnable() {
                    public void run() {
                        Toast.makeText(c, m, 1).show();
                    }
                });
            }
        }
    }

    public void runInApplicationThread(Runnable r) {
        mApplicationHandler.post(r);
    }

    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
    }
}
