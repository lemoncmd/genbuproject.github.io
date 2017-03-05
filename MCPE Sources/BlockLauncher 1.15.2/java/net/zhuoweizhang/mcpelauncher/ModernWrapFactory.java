package net.zhuoweizhang.mcpelauncher;

import android.app.Activity;
import android.widget.PopupWindow;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class ModernWrapFactory extends WrapFactory {
    public static final String TAG = "BlockLauncher/ModernWrapFactory";
    private static MyExceptionHandler myExceptionHandler = new MyExceptionHandler();
    public List<WeakReference<PopupWindow>> popups = new ArrayList();

    private static class MyExceptionHandler implements UncaughtExceptionHandler {
        private MyExceptionHandler() {
        }

        public void uncaughtException(Thread thread, Throwable t) {
            ScriptManager.reportScriptError(null, t);
        }
    }

    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        if (javaObject instanceof PopupWindow) {
            if (!ScriptManager.isScriptingEnabled()) {
                ((PopupWindow) javaObject).dismiss();
            }
            synchronized (this.popups) {
                this.popups.add(new WeakReference((PopupWindow) javaObject));
            }
        }
        if (javaObject instanceof Thread) {
            Thread t = (Thread) javaObject;
            UncaughtExceptionHandler exHandler = t.getUncaughtExceptionHandler();
            if (exHandler == null || (exHandler instanceof ThreadGroup)) {
                t.setUncaughtExceptionHandler(myExceptionHandler);
            }
        }
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

    protected void closePopups(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                synchronized (ModernWrapFactory.this.popups) {
                    for (WeakReference<PopupWindow> ref : ModernWrapFactory.this.popups) {
                        PopupWindow window = (PopupWindow) ref.get();
                        if (window != null) {
                            window.dismiss();
                        }
                    }
                }
            }
        });
    }
}
