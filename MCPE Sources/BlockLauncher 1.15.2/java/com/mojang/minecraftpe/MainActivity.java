package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NativeActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.speech.tts.TextToSpeech;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.joshuahuelsman.patchtool.PTPatch;
import com.microsoft.cll.android.EventEnums;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.AddonManager;
import net.zhuoweizhang.mcpelauncher.AddonOverrideTexturePack;
import net.zhuoweizhang.mcpelauncher.Manifest.permission;
import net.zhuoweizhang.mcpelauncher.MaraudersMap;
import net.zhuoweizhang.mcpelauncher.MinecraftVersion;
import net.zhuoweizhang.mcpelauncher.PatchManager;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.RealmsRedirectInfo;
import net.zhuoweizhang.mcpelauncher.RedirectPackageManager;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.ScriptOverrideTexturePack;
import net.zhuoweizhang.mcpelauncher.ScriptTextureDownloader;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.ZipTexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.ControllerManager;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;
import net.zhuoweizhang.mcpelauncher.texture.AtlasProvider;
import net.zhuoweizhang.mcpelauncher.texture.ClientBlocksJsonProvider;
import net.zhuoweizhang.mcpelauncher.texture.TextureListProvider;
import net.zhuoweizhang.mcpelauncher.texture.TexturePackLoader;
import net.zhuoweizhang.mcpelauncher.ui.AboutAppActivity;
import net.zhuoweizhang.mcpelauncher.ui.GetSubstrateActivity;
import net.zhuoweizhang.mcpelauncher.ui.HoverCar;
import net.zhuoweizhang.mcpelauncher.ui.MainMenuOptionsActivity;
import net.zhuoweizhang.mcpelauncher.ui.ManageScriptsActivity;
import net.zhuoweizhang.mcpelauncher.ui.MinecraftNotSupportedActivity;
import net.zhuoweizhang.mcpelauncher.ui.NerdyStuffActivity;
import net.zhuoweizhang.mcpelauncher.ui.NoMinecraftActivity;
import net.zhuoweizhang.mcpelauncher.ui.TexturePacksActivity;
import org.fmod.FMOD;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Token;

@SuppressLint({"SdCardPath"})
public class MainActivity extends NativeActivity {
    public static final int DIALOG_COPY_WORLD = 4;
    public static final int DIALOG_CRASH_SAFE_MODE = 4096;
    public static final int DIALOG_CREATE_WORLD = 1;
    public static final int DIALOG_FIRST_LAUNCH = 4099;
    public static final int DIALOG_INSERT_TEXT = 4103;
    public static final int DIALOG_INVALID_PATCHES = 4098;
    public static final int DIALOG_MULTIPLAYER_DISABLE_SCRIPTS = 4104;
    public static final int DIALOG_NOT_SUPPORTED = 4101;
    public static final int DIALOG_RUNTIME_OPTIONS = 4097;
    public static final int DIALOG_RUNTIME_OPTIONS_WITH_INSERT_TEXT = 4105;
    public static final int DIALOG_SELINUX_BROKE_EVERYTHING = 4106;
    public static final int DIALOG_SETTINGS = 3;
    public static final int DIALOG_UPDATE_TEXTURE_PACK = 4102;
    public static final int DIALOG_VERSION_MISMATCH_SAFE_MODE = 4100;
    public static final String[] GAME_MODES;
    public static final String HALF_SUPPORT_VERSION = "1.0";
    public static final String HEY_CAN_YOU_STOP_STEALING_BLOCKLAUNCHER_CODE = "please?";
    public static final int INPUT_STATUS_CANCELLED = 0;
    public static final int INPUT_STATUS_IN_PROGRESS = -1;
    public static final int INPUT_STATUS_OK = 1;
    private static final int MAX_FAILS = 2;
    private static String MC_NATIVE_LIBRARY_DIR = "/data/data/com.mojang.minecraftpe/lib/";
    private static String MC_NATIVE_LIBRARY_LOCATION = "/data/data/com.mojang.minecraftpe/lib/libminecraftpe.so";
    public static final String MOJANG_ACCOUNT_LOGIN_URL = "https://account.mojang.com/m/login?app=mcpe";
    public static final String PT_PATCHES_DIR = "ptpatches";
    private static final int REQUEST_MANAGE_SCRIPTS = 417;
    private static final int REQUEST_MANAGE_TEXTURES = 416;
    private static final int REQUEST_PICK_IMAGE = 415;
    public static final String SCRIPT_SUPPORT_VERSION = "0.16";
    public static final String TAG = "BlockLauncher/Main";
    public static WeakReference<MainActivity> currentMainActivity = null;
    public static List<String> failedPatches = new ArrayList();
    public static boolean hasPrePatched = false;
    public static boolean libLoaded = false;
    public static Set<String> loadedAddons = new HashSet();
    public static ByteBuffer minecraftLibBuffer;
    public static boolean tempSafeMode = false;
    public AddonOverrideTexturePack addonOverrideTexturePackInstance = null;
    private int commandHistoryIndex = INPUT_STATUS_CANCELLED;
    private List<String> commandHistoryList = new ArrayList();
    private View commandHistoryView;
    private PopupWindow commandHistoryWindow;
    private boolean controllerInit = false;
    protected DisplayMetrics displayMetrics;
    protected boolean fakePackage = false;
    public boolean forceFallback = false;
    protected boolean hasRecorder = false;
    private boolean hasResetSafeModeCounter = false;
    private boolean hiddenTextDismissAfterOneLine = false;
    private TextView hiddenTextView;
    private PopupWindow hiddenTextWindow;
    private HoverCar hoverCar = null;
    protected int inputStatus = INPUT_STATUS_IN_PROGRESS;
    protected boolean isRecording = false;
    private Toast lastToast = null;
    private Dialog loginDialog;
    private WebView loginWebView;
    private final Handler mainHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            MainActivity.this.toggleRecording();
        }
    };
    public ApplicationInfo mcAppInfo;
    private PackageInfo mcPkgInfo;
    private int mcpeArch = INPUT_STATUS_CANCELLED;
    protected Context minecraftApkContext;
    public boolean minecraftApkForwardLocked = false;
    private Typeface minecraftTypeface = null;
    protected MinecraftVersion minecraftVersion;
    private Button nextButton;
    private boolean overlyZealousSELinuxSafeMode = false;
    private long pickImageCallbackAddress = 0;
    private Intent pickImageResult;
    private Button prevButton;
    public String refreshToken = BuildConfig.FLAVOR;
    private SparseArray<HurlRunner> requestMap = new SparseArray();
    public boolean requiresGuiBlocksPatch = false;
    public String session = BuildConfig.FLAVOR;
    public List<TexturePack> textureOverrides = new ArrayList();
    protected TexturePack texturePack;
    private boolean textureVerbose = false;
    private TextToSpeech tts;
    protected String[] userInputStrings = null;

    private class HurlRunner implements Runnable {
        private HttpURLConnection conn;
        private String cookies;
        private boolean isValid = true;
        private String method;
        private int requestId;
        private String strurl;
        private long timestamp;
        private URL url;

        public HurlRunner(int requestId, long timestamp, String url, String method, String cookies) {
            this.requestId = requestId;
            this.timestamp = timestamp;
            this.strurl = url;
            this.method = method;
            this.cookies = cookies;
            synchronized (MainActivity.this.requestMap) {
                MainActivity.this.requestMap.put(requestId, this);
            }
        }

        public void run() {
            InputStream is = null;
            String content = null;
            int response = MainActivity.INPUT_STATUS_CANCELLED;
            try {
                this.url = new URL(this.strurl);
                this.conn = (HttpURLConnection) this.url.openConnection();
                this.conn.setRequestMethod(this.method);
                this.conn.setRequestProperty("Cookie", this.cookies);
                this.conn.setRequestProperty("User-Agent", "MCPE/Curl");
                this.conn.setUseCaches(false);
                this.conn.setDoInput(true);
                this.conn.connect();
                try {
                    response = this.conn.getResponseCode();
                    is = this.conn.getInputStream();
                } catch (Exception e) {
                    try {
                        is = this.conn.getErrorStream();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (is != null) {
                    content = MainActivity.stringFromInputStream(is, this.conn.getContentLength() < 0 ? EnchantType.pickaxe : this.conn.getContentLength());
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e4) {
                    }
                }
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e5) {
                    }
                }
            }
            if (content != null) {
            }
            if (this.isValid) {
                MainActivity.this.nativeWebRequestCompleted(this.requestId, this.timestamp, response, content);
            }
            synchronized (MainActivity.this.requestMap) {
                MainActivity.this.requestMap.remove(MainActivity.this.requestMap.indexOfValue(this));
            }
        }
    }

    private class LoginWebViewClient extends WebViewClient {
        boolean hasFiredLaunchEvent;

        private LoginWebViewClient() {
            this.hasFiredLaunchEvent = false;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri tempUri = Uri.parse(url);
            String endHost = MainActivity.this.getRealmsRedirectInfo().accountUrl;
            if (endHost == null) {
                endHost = "account.mojang.com";
            }
            if (!tempUri.getHost().equals(endHost)) {
                return false;
            }
            if (tempUri.getPath().equals("/m/launch")) {
                MainActivity.this.loginLaunchCallback(tempUri);
                this.hasFiredLaunchEvent = true;
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (MainActivity.this.isRedirectingRealms()) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Uri tempUri = Uri.parse(url);
            String endHost = MainActivity.this.getRealmsRedirectInfo().accountUrl;
            if (endHost == null) {
                endHost = "account.mojang.com";
            }
            if (tempUri.getHost().equals(endHost) && tempUri.getPath().equals("/m/launch") && !this.hasFiredLaunchEvent) {
                MainActivity.this.loginLaunchCallback(tempUri);
                this.hasFiredLaunchEvent = true;
            }
        }
    }

    private class PopupTextWatcher implements TextWatcher, OnEditorActionListener {
        private PopupTextWatcher() {
        }

        public void afterTextChanged(Editable e) {
            MainActivity.this.nativeSetTextboxText(e.toString());
            if (MainActivity.this.isCommandHistoryEnabled() && MainActivity.this.commandHistoryIndex >= 0 && MainActivity.this.commandHistoryIndex < MainActivity.this.commandHistoryList.size()) {
                MainActivity.this.commandHistoryList.set(MainActivity.this.commandHistoryIndex, e.toString());
            }
        }

        public void beforeTextChanged(CharSequence c, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence c, int start, int count, int after) {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            MainActivity.this.nativeReturnKeyPressed();
            return true;
        }
    }

    public static native void nativeOnPickImageCanceled(long j);

    public static native void nativeOnPickImageSuccess(long j, String str);

    public native void nativeBackPressed();

    public native void nativeBackSpacePressed();

    public native void nativeKeyHandler(int i, boolean z);

    public native void nativeLoginData(String str, String str2, String str3, String str4);

    public native void nativeProcessIntentUriQuery(String str, String str2);

    public native void nativeRegisterThis();

    public native void nativeReturnKeyPressed();

    public native void nativeSetTextboxText(String str);

    public native void nativeStopThis();

    public native void nativeSuspend();

    public native void nativeTypeCharacter(String str);

    public native void nativeUnregisterThis();

    public native void nativeWebRequestCompleted(int i, long j, int i2, String str);

    static {
        String[] strArr = new String[MAX_FAILS];
        strArr[INPUT_STATUS_CANCELLED] = "creative";
        strArr[INPUT_STATUS_OK] = "survival";
        GAME_MODES = strArr;
    }

    public void onCreate(Bundle savedInstanceState) {
        currentMainActivity = new WeakReference(this);
        int safeModeCounter = Utils.getPrefs(MAX_FAILS).getInt("safe_mode_counter", INPUT_STATUS_CANCELLED);
        System.out.println("Current fails: " + safeModeCounter);
        if (safeModeCounter == MAX_FAILS) {
            Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putBoolean("zz_safe_mode", true).apply();
            safeModeCounter = INPUT_STATUS_CANCELLED;
        }
        Utils.getPrefs(MAX_FAILS).edit().putInt("safe_mode_counter", safeModeCounter + INPUT_STATUS_OK).commit();
        this.textureVerbose = new File("/sdcard/bl_textureVerbose.txt").exists();
        MinecraftVersion.context = getApplicationContext();
        boolean needsToClearOverrides = false;
        try {
            this.mcPkgInfo = getPackageManager().getPackageInfo("com.mojang.minecraftpe", INPUT_STATUS_CANCELLED);
            this.mcAppInfo = this.mcPkgInfo.applicationInfo;
            MC_NATIVE_LIBRARY_DIR = this.mcAppInfo.nativeLibraryDir;
            MC_NATIVE_LIBRARY_LOCATION = MC_NATIVE_LIBRARY_DIR + "/libminecraftpe.so";
            System.out.println("libminecraftpe.so is at " + MC_NATIVE_LIBRARY_LOCATION);
            checkArch();
            this.minecraftApkForwardLocked = !this.mcAppInfo.sourceDir.equals(this.mcAppInfo.publicSourceDir);
            int minecraftVersionCode = this.mcPkgInfo.versionCode;
            this.minecraftVersion = MinecraftVersion.getRaw(minecraftVersionCode);
            if (this.minecraftVersion == null) {
                tempSafeMode = true;
                showDialog(DIALOG_VERSION_MISMATCH_SAFE_MODE);
                this.minecraftVersion = MinecraftVersion.getDefault();
            }
            if (this.minecraftVersion.needsWarning) {
                Log.w(TAG, "OMG hipster version code found - breaking mod compat before it's cool");
            }
            PatchUtils.minecraftVersion = this.minecraftVersion;
            boolean is0140or0141 = this.mcPkgInfo.versionName.startsWith("0.14.0") || this.mcPkgInfo.versionName.equals("0.14.1");
            boolean isSupportedVersion = (this.mcPkgInfo.versionName.startsWith(SCRIPT_SUPPORT_VERSION) && !is0140or0141) || this.mcPkgInfo.versionName.startsWith(HALF_SUPPORT_VERSION);
            if (!isSupportedVersion) {
                Intent intent = new Intent(this, MinecraftNotSupportedActivity.class);
                intent.putExtra("minecraftVersion", this.mcPkgInfo.versionName);
                intent.putExtra("supportedVersion", "0.16.2, 1.0.0");
                startActivity(intent);
                finish();
                try {
                    Thread.sleep(1000);
                    Process.killProcess(Process.myPid());
                } catch (Throwable th) {
                }
            }
            checkForSubstrate();
            fixMyEpicFail();
            migrateToPatchManager();
            SharedPreferences myprefs = Utils.getPrefs(INPUT_STATUS_OK);
            if (myprefs.getInt("prepatch_version", INPUT_STATUS_IN_PROGRESS) != minecraftVersionCode) {
                System.out.println("Version updated; forcing prepatch");
                myprefs.edit().putBoolean("force_prepatch", true).apply();
                disableAllPatches();
                needsToClearOverrides = true;
            }
            if (myprefs.getInt("last_version", INPUT_STATUS_IN_PROGRESS) != minecraftVersionCode) {
                Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putBoolean("zz_texture_pack_enable", false).apply();
                myprefs.edit().putInt("last_version", minecraftVersionCode).apply();
                if (myprefs.getString("texture_packs", BuildConfig.FLAVOR).indexOf("minecraft.apk") >= 0) {
                    showDialog(DIALOG_UPDATE_TEXTURE_PACK);
                }
            }
            try {
                if (getPackageName().equals("com.mojang.minecraftpe")) {
                    this.minecraftApkContext = this;
                } else {
                    this.minecraftApkContext = createPackageContext("com.mojang.minecraftpe", MAX_FAILS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Can't create package context for the original APK", INPUT_STATUS_OK).show();
                finish();
            }
            Utils.setLanguageOverride();
            this.forceFallback = new File("/sdcard/bl_forcefallback.txt").exists();
            this.textureOverrides.clear();
            loadTexturePack();
            if (allowScriptOverrideTextures()) {
                this.textureOverrides.add(new ScriptOverrideTexturePack(this));
            }
            ScriptTextureDownloader.attachCache(this);
            this.requiresGuiBlocksPatch = doesRequireGuiBlocksPatch();
            try {
                if ((!Utils.isSafeMode() && Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_manage_patches", true)) || getMCPEVersion().startsWith(HALF_SUPPORT_VERSION)) {
                    prePatch();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                System.load(this.mcAppInfo.nativeLibraryDir + "/libfmod.so");
                System.load(MC_NATIVE_LIBRARY_LOCATION);
                FMOD.init(this);
                libLoaded = true;
                try {
                    if (!Utils.isSafeMode() || requiresPatchingInSafeMode()) {
                        initPatching();
                        if (minecraftLibBuffer != null) {
                            ScriptManager.nativePrePatch(Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_signal_handler", false), this, !hasScriptSupport());
                            if (hasScriptSupport() && Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_desktop_gui", false)) {
                                ScriptManager.nativeModPESetDesktopGui(true);
                            }
                            if (!Utils.isSafeMode()) {
                                loadNativeAddons();
                            }
                        }
                    }
                } catch (Exception e22) {
                    e22.printStackTrace();
                    reportError(e22);
                }
                try {
                    boolean shouldLoadScripts = hasScriptSupport();
                    if (!(Utils.isSafeMode() || minecraftLibBuffer == null)) {
                        applyBuiltinPatches();
                        if (shouldLoadScripts && Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_script_enable", true)) {
                            ScriptManager.init(this);
                            this.textureOverrides.add(ScriptManager.modPkgTexturePack);
                        }
                    }
                    if (Utils.isSafeMode() || !shouldLoadScripts) {
                        ScriptManager.loadEnabledScriptsNames(this);
                    }
                } catch (Exception e222) {
                    e222.printStackTrace();
                    reportError(e222);
                }
                if (needsToClearOverrides) {
                    ScriptManager.clearTextureOverrides();
                }
                initAtlasMeta();
                this.displayMetrics = new DisplayMetrics();
                setVolumeControlStream(DIALOG_SETTINGS);
                getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);
                addLibraryDirToPath(MC_NATIVE_LIBRARY_DIR);
                setFakePackage(true);
                super.onCreate(savedInstanceState);
                nativeRegisterThis();
                setFakePackage(false);
                Utils.setupTheme(this, true);
                disableTransparentSystemBar();
                CookieHandler.setDefault(new CookieManager());
                if (Utils.isSafeMode()) {
                    if (this.overlyZealousSELinuxSafeMode) {
                        showDialog(DIALOG_SELINUX_BROKE_EVERYTHING);
                    } else {
                        showDialog(DIALOG_CRASH_SAFE_MODE);
                    }
                }
                initKamcord();
                System.gc();
            } catch (Exception e2222) {
                throw new RuntimeException(e2222);
            }
        } catch (NameNotFoundException e3) {
            e3.printStackTrace();
            finish();
            startActivity(new Intent(this, NoMinecraftActivity.class));
            Thread.sleep(100);
            Process.killProcess(Process.myPid());
        } catch (Throwable th2) {
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.hasResetSafeModeCounter) {
            Utils.getPrefs(MAX_FAILS).edit().putInt("safe_mode_counter", Utils.getPrefs(MAX_FAILS).getInt("safe_mode_counter", INPUT_STATUS_CANCELLED) + INPUT_STATUS_OK).commit();
        }
        if (this.hoverCar == null) {
            getWindow().getDecorView().post(new Runnable() {
                public void run() {
                    try {
                        MainActivity.this.setupHoverCar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            this.hoverCar.setVisible(!Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_hovercar_hide", false));
        }
        setImmersiveMode(Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_immersive_mode", false));
    }

    protected void onPause() {
        nativeSuspend();
        super.onPause();
        Utils.getPrefs(MAX_FAILS).edit().putInt("safe_mode_counter", INPUT_STATUS_CANCELLED).commit();
        this.hasResetSafeModeCounter = true;
        hideKeyboardView();
    }

    public void onDestroy() {
        nativeUnregisterThis();
        super.onDestroy();
        File lockFile = new File(getFilesDir(), "running.lock");
        if (lockFile.exists()) {
            lockFile.delete();
        }
        if (this.hoverCar != null) {
            this.hoverCar.dismiss();
            this.hoverCar = null;
        }
        ScriptManager.destroy();
        System.exit(INPUT_STATUS_CANCELLED);
    }

    public void onStop() {
        nativeStopThis();
        super.onStop();
        ScriptTextureDownloader.flushCache();
        System.gc();
    }

    private void setFakePackage(boolean enable) {
        this.fakePackage = enable;
    }

    public PackageManager getPackageManager() {
        if (this.fakePackage) {
            return new RedirectPackageManager(super.getPackageManager(), MC_NATIVE_LIBRARY_DIR);
        }
        return super.getPackageManager();
    }

    private void prePatch() throws Exception {
        File patched = getDir("patched", INPUT_STATUS_CANCELLED);
        File originalLibminecraft = new File(this.mcAppInfo.nativeLibraryDir + "/libminecraftpe.so");
        File newMinecraft = new File(patched, "libminecraftpe.so");
        boolean forcePrePatch = Utils.getPrefs(INPUT_STATUS_OK).getBoolean("force_prepatch", true);
        if (hasPrePatched || Utils.getEnabledPatches().size() != 0) {
            if (!hasPrePatched && (!newMinecraft.exists() || forcePrePatch)) {
                PTPatch patch;
                System.out.println("Forcing new prepatch");
                byte[] libBytes = new byte[((int) originalLibminecraft.length())];
                ByteBuffer libBuffer = ByteBuffer.wrap(libBytes);
                InputStream is = new FileInputStream(originalLibminecraft);
                is.read(libBytes);
                is.close();
                int patchedCount = INPUT_STATUS_CANCELLED;
                int maxPatchNum = getMaxNumPatches();
                for (String patchLoc : Utils.getEnabledPatches()) {
                    if (maxPatchNum >= 0 && patchedCount >= maxPatchNum) {
                        break;
                    }
                    File patchFile = new File(patchLoc);
                    if (patchFile.exists()) {
                        try {
                            patch = new PTPatch();
                            patch.loadPatch(patchFile);
                            if (patch.checkMagic()) {
                                PatchUtils.patch(libBuffer, patch);
                                patchedCount += INPUT_STATUS_OK;
                            } else {
                                failedPatches.add(patchFile.getName());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            failedPatches.add(patchFile.getName());
                        }
                    }
                }
                if (this.requiresGuiBlocksPatch) {
                    System.out.println("Patching guiblocks");
                    patch = new PTPatch();
                    if (this.minecraftVersion.guiBlocksPatch != null) {
                        patch.loadPatch(this.minecraftVersion.guiBlocksPatch);
                        PatchUtils.patch(libBuffer, patch);
                    }
                }
                OutputStream os = new FileOutputStream(newMinecraft);
                os.write(libBytes);
                os.close();
                hasPrePatched = true;
                Utils.getPrefs(INPUT_STATUS_OK).edit().putBoolean("force_prepatch", false).putInt("prepatch_version", this.mcPkgInfo.versionCode).apply();
                if (failedPatches.size() > 0) {
                    showDialog(DIALOG_INVALID_PATCHES);
                }
            }
            MC_NATIVE_LIBRARY_DIR = patched.getCanonicalPath();
            MC_NATIVE_LIBRARY_LOCATION = newMinecraft.getCanonicalPath();
            return;
        }
        hasPrePatched = true;
        if (newMinecraft.exists()) {
            newMinecraft.delete();
        }
        if (forcePrePatch) {
            Utils.getPrefs(INPUT_STATUS_OK).edit().putBoolean("force_prepatch", false).putInt("prepatch_version", this.mcPkgInfo.versionCode).apply();
        }
    }

    public void buyGame() {
    }

    public int checkLicense() {
        return INPUT_STATUS_CANCELLED;
    }

    public void displayDialog(int dialogId) {
        System.out.println("displayDialog: " + dialogId);
        this.inputStatus = INPUT_STATUS_CANCELLED;
        switch (dialogId) {
            case INPUT_STATUS_OK /*1*/:
                System.out.println("World creation");
                this.inputStatus = INPUT_STATUS_IN_PROGRESS;
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.showDialog(MainActivity.INPUT_STATUS_OK);
                    }
                });
                return;
            case DIALOG_SETTINGS /*3*/:
                System.out.println("Settings");
                this.inputStatus = INPUT_STATUS_IN_PROGRESS;
                startActivityForResult(getOptionsActivityIntent(), 1234);
                return;
            case DIALOG_COPY_WORLD /*4*/:
                System.out.println("Copy world");
                this.inputStatus = INPUT_STATUS_IN_PROGRESS;
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.showDialog(MainActivity.DIALOG_COPY_WORLD);
                    }
                });
                return;
            default:
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1234) {
            this.inputStatus = INPUT_STATUS_OK;
            System.out.println("Settings OK");
            if (!Utils.isSafeMode()) {
                applyBuiltinPatches();
            }
        } else if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == INPUT_STATUS_IN_PROGRESS) {
                this.pickImageResult = intent;
                nativeOnPickImageSuccess(this.pickImageCallbackAddress, copyContentStoreToTempFile(intent.getData()).getAbsolutePath());
                return;
            }
            nativeOnPickImageCanceled(this.pickImageCallbackAddress);
        } else if ((requestCode == REQUEST_MANAGE_TEXTURES || requestCode == REQUEST_MANAGE_SCRIPTS) && resultCode == INPUT_STATUS_IN_PROGRESS) {
            finish();
            NerdyStuffActivity.forceRestart(this);
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case INPUT_STATUS_OK /*1*/:
                return createCreateWorldDialog();
            case DIALOG_COPY_WORLD /*4*/:
                return createCopyWorldDialog();
            case DIALOG_CRASH_SAFE_MODE /*4096*/:
                return createSafeModeDialog(R.string.manage_patches_crash_safe_mode);
            case DIALOG_RUNTIME_OPTIONS /*4097*/:
                return createRuntimeOptionsDialog(false);
            case DIALOG_INVALID_PATCHES /*4098*/:
                return createInvalidPatchesDialog();
            case DIALOG_FIRST_LAUNCH /*4099*/:
                return createFirstLaunchDialog();
            case DIALOG_VERSION_MISMATCH_SAFE_MODE /*4100*/:
                return createSafeModeDialog(R.string.version_mismatch_message);
            case DIALOG_NOT_SUPPORTED /*4101*/:
                return createNotSupportedDialog();
            case DIALOG_UPDATE_TEXTURE_PACK /*4102*/:
                return createUpdateTexturePackDialog();
            case DIALOG_INSERT_TEXT /*4103*/:
                return createInsertTextDialog();
            case DIALOG_MULTIPLAYER_DISABLE_SCRIPTS /*4104*/:
                return createMultiplayerDisableScriptsDialog();
            case DIALOG_RUNTIME_OPTIONS_WITH_INSERT_TEXT /*4105*/:
                return createRuntimeOptionsDialog(true);
            case DIALOG_SELINUX_BROKE_EVERYTHING /*4106*/:
                return createSELinuxBrokeEverythingDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    protected Dialog createCreateWorldDialog() {
        return new Builder(this).setTitle(R.string.world_create_title).setView(getLayoutInflater().inflate(R.layout.create_world_dialog, null)).setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                AlertDialog dialog = (AlertDialog) dialogI;
                String worldName = ((TextView) dialog.findViewById(R.id.world_name_entry)).getText().toString();
                String worldSeed = ((TextView) dialog.findViewById(R.id.world_seed_entry)).getText().toString();
                String worldGameMode = MainActivity.GAME_MODES[((Spinner) dialog.findViewById(R.id.world_gamemode_spinner)).getSelectedItemPosition()];
                MainActivity mainActivity = MainActivity.this;
                String[] strArr = new String[MainActivity.DIALOG_SETTINGS];
                strArr[MainActivity.INPUT_STATUS_CANCELLED] = worldName;
                strArr[MainActivity.INPUT_STATUS_OK] = worldSeed;
                strArr[MainActivity.MAX_FAILS] = worldGameMode;
                mainActivity.userInputStrings = strArr;
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_OK;
            }
        }).setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_CANCELLED;
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialogI) {
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_CANCELLED;
            }
        }).create();
    }

    protected Dialog createSafeModeDialog(int messageRes) {
        return new Builder(this).setMessage(messageRes).setPositiveButton(R.string.safe_mode_exit, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                MainActivity.this.turnOffSafeMode();
            }
        }).setNegativeButton(R.string.safe_mode_continue, null).create();
    }

    protected Dialog createRuntimeOptionsDialog(boolean hasInsertText) {
        int i;
        CharSequence livePatch = getResources().getString(R.string.pref_texture_pack);
        final CharSequence optionMenu = getResources().getString(R.string.hovercar_options);
        final CharSequence insertText = getResources().getString(R.string.hovercar_insert_text);
        CharSequence manageModPEScripts = getResources().getString(R.string.pref_zz_manage_scripts);
        CharSequence takeScreenshot = getResources().getString(R.string.take_screenshot);
        final CharSequence startRecording = getResources().getString(R.string.hovercar_start_recording);
        final CharSequence stopRecording = getResources().getString(R.string.hovercar_stop_recording);
        CharSequence[] charSequenceArr = new CharSequence[DIALOG_SETTINGS];
        charSequenceArr[INPUT_STATUS_CANCELLED] = livePatch;
        charSequenceArr[INPUT_STATUS_OK] = manageModPEScripts;
        charSequenceArr[MAX_FAILS] = takeScreenshot;
        final List<CharSequence> options = new ArrayList(Arrays.asList(charSequenceArr));
        if (this.hasRecorder) {
            Object obj;
            this.isRecording = isKamcordRecording();
            if (this.isRecording) {
                obj = stopRecording;
            } else {
                CharSequence charSequence = startRecording;
            }
            options.add(obj);
        }
        if (hasInsertText) {
            options.add(insertText);
        }
        options.add(optionMenu);
        Builder builder = new Builder(this);
        if (Utils.isSafeMode()) {
            i = R.string.pref_zz_safe_mode;
        } else {
            i = R.string.app_name;
        }
        Builder builder2 = builder.setTitle(i).setItems((CharSequence[]) options.toArray(new CharSequence[INPUT_STATUS_CANCELLED]), new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                boolean hasLoadedScripts = true;
                CharSequence buttonText = (CharSequence) options.get(button);
                if (button == 0) {
                    MainActivity.this.startActivityForResult(new Intent(MainActivity.this, TexturePacksActivity.class), MainActivity.REQUEST_MANAGE_TEXTURES);
                } else if (button == MainActivity.INPUT_STATUS_OK) {
                    if (MainActivity.this.hasScriptSupport()) {
                        MainActivity.this.startActivityForResult(new Intent(MainActivity.this, ManageScriptsActivity.class), MainActivity.REQUEST_MANAGE_SCRIPTS);
                        return;
                    }
                    new Builder(MainActivity.this).setMessage("Scripts are not supported yet in Minecraft PE " + MainActivity.this.mcPkgInfo.versionName).setPositiveButton(17039370, null).show();
                } else if (button == MainActivity.MAX_FAILS) {
                    if (!(Utils.getPrefs(MainActivity.INPUT_STATUS_CANCELLED).getBoolean("zz_script_enable", true) && !Utils.isSafeMode() && MainActivity.this.hasScriptSupport())) {
                        hasLoadedScripts = false;
                    }
                    if (hasLoadedScripts) {
                        ScriptManager.takeScreenshot("screenshot");
                    } else {
                        new Builder(MainActivity.this).setMessage(R.string.take_screenshot_requires_modpe_script).setPositiveButton(17039370, null).show();
                    }
                } else if (buttonText.equals(optionMenu)) {
                    MainActivity.this.startActivity(MainActivity.this.getOptionsActivityIntent());
                } else if (buttonText.equals(insertText)) {
                    MainActivity.this.showDialog(MainActivity.DIALOG_INSERT_TEXT);
                } else if (buttonText.equals(startRecording) || buttonText.equals(stopRecording)) {
                    MainActivity.this.mainHandler.sendEmptyMessageDelayed(327, 1000);
                }
            }
        });
        if (VERSION.SDK_INT >= 19) {
            builder2.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    MainActivity.this.touchImmersiveMode();
                }
            });
        }
        return builder2.create();
    }

    protected Dialog createInvalidPatchesDialog() {
        return new Builder(this).setMessage(getResources().getString(R.string.manage_patches_invalid_patches) + "\n" + PatchManager.join((String[]) failedPatches.toArray(PatchManager.blankArray), "\n")).setPositiveButton(17039370, null).create();
    }

    protected Dialog createFirstLaunchDialog() {
        StringBuilder dialogMsg = new StringBuilder();
        dialogMsg.append(getResources().getString(R.string.firstlaunch_generic_intro)).append("\n\n");
        if (this.minecraftApkForwardLocked) {
            dialogMsg.append(getResources().getString(R.string.firstlaunch_jelly_bean)).append("\n\n");
        }
        dialogMsg.append(getResources().getString(R.string.firstlaunch_see_options)).append("\n\n");
        return new Builder(this).setTitle(R.string.firstlaunch_title).setMessage(dialogMsg.toString()).setPositiveButton(17039370, null).setNeutralButton(R.string.firstlaunch_help, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(AboutAppActivity.FORUMS_PAGE_URL));
                MainActivity.this.startActivity(intent);
            }
        }).create();
    }

    protected Dialog createCopyWorldDialog() {
        return new Builder(this).setTitle(R.string.copy_world_title).setView(getLayoutInflater().inflate(R.layout.copy_world_dialog, null)).setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                String worldName = ((TextView) ((AlertDialog) dialogI).findViewById(R.id.world_name_entry)).getText().toString();
                MainActivity mainActivity = MainActivity.this;
                String[] strArr = new String[MainActivity.INPUT_STATUS_OK];
                strArr[MainActivity.INPUT_STATUS_CANCELLED] = worldName;
                mainActivity.userInputStrings = strArr;
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_OK;
            }
        }).setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_CANCELLED;
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialogI) {
                MainActivity.this.inputStatus = MainActivity.INPUT_STATUS_CANCELLED;
            }
        }).create();
    }

    protected Dialog createNotSupportedDialog() {
        return new Builder(this).setMessage(R.string.feature_not_supported).setPositiveButton(17039370, null).create();
    }

    protected Dialog createUpdateTexturePackDialog() {
        return new Builder(this).setMessage(R.string.extract_textures_need_update).setPositiveButton(17039370, null).create();
    }

    protected Dialog createBackupsNotSupportedDialog() {
        return new Builder(this).setMessage("Backed up versions of BlockLauncher are not supported, as BlockLauncher depends on updates from the application store.  Please reinstall BlockLauncher. If you believe you received this message in error, contact zhuowei_applications@yahoo.com").setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                MainActivity.this.finish();
            }
        }).setCancelable(false).create();
    }

    protected Dialog createInsertTextDialog() {
        final EditText editText = new EditText(this);
        editText.setSingleLine(false);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(INPUT_STATUS_CANCELLED);
        ll.addView(editText, -2, -2);
        Button back = new Button(this);
        back.setText(R.string.hovercar_insert_text_backspace);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    MainActivity.this.nativeTypeCharacter("\b");
                } catch (Exception e) {
                    MainActivity.this.showDialog(MainActivity.DIALOG_NOT_SUPPORTED);
                }
            }
        });
        ll.addView(back, -2, -2);
        return new Builder(this).setTitle(R.string.hovercar_insert_text).setView(ll).setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                try {
                    String[] lines = editText.getText().toString().split("\n");
                    for (int line = MainActivity.INPUT_STATUS_CANCELLED; line < lines.length; line += MainActivity.INPUT_STATUS_OK) {
                        if (line != 0) {
                            MainActivity.this.nativeTypeCharacter("\n");
                        }
                        MainActivity.this.nativeTypeCharacter(lines[line]);
                    }
                    editText.setText(BuildConfig.FLAVOR);
                } catch (UnsatisfiedLinkError e) {
                    MainActivity.this.showDialog(MainActivity.DIALOG_NOT_SUPPORTED);
                }
            }
        }).setNegativeButton(17039360, null).create();
    }

    protected Dialog createMultiplayerDisableScriptsDialog() {
        return new Builder(this).setMessage(R.string.script_disabled_in_multiplayer).setPositiveButton(17039370, null).create();
    }

    protected Dialog createSELinuxBrokeEverythingDialog() {
        return new Builder(this).setMessage(R.string.selinux_broke_everything).setPositiveButton(17039370, null).create();
    }

    public String getDateString(int time) {
        System.out.println("getDateString: " + time);
        return DateFormat.getDateInstance(DIALOG_SETTINGS, Locale.US).format(new Date(((long) time) * 1000));
    }

    public byte[] getFileDataBytes(String name) {
        byte[] bytes = getFileDataBytes(name, false);
        if (!name.endsWith(".meta")) {
            return bytes;
        }
        String fileStr = new String(bytes, Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        if (fileStr.contains("portal") || fileStr.contains("rabbit_foot")) {
            return bytes;
        }
        return getFileDataBytes(name, true);
    }

    public byte[] getFileDataBytes(String name, boolean forceInternal) {
        try {
            InputStream is;
            if (name.charAt(INPUT_STATUS_CANCELLED) == '/') {
                is = getRegularInputStream(name);
            } else if (name.equals("behavior_packs/vanilla/entities/villager.json") || name.equals("resourcepacks/vanilla/server/entities/villager.json")) {
                is = openFallbackAsset(name);
            } else {
                is = forceInternal ? getLocalInputStreamForAsset(name) : getInputStreamForAsset(name);
            }
            if (is == null || TAG.hashCode() != -1771687045) {
                return null;
            }
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[EnchantType.pickaxe];
            while (true) {
                int len = is.read(buffer);
                if (len < 0) {
                    return bout.toByteArray();
                }
                bout.write(buffer, INPUT_STATUS_CANCELLED, len);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public InputStream getInputStreamForAsset(String name) {
        return getInputStreamForAsset(name, null);
    }

    public InputStream getInputStreamForAsset(String name, long[] lengthOut) {
        int i = INPUT_STATUS_CANCELLED;
        while (i < this.textureOverrides.size()) {
            InputStream is;
            try {
                try {
                    is = ((TexturePack) this.textureOverrides.get(i)).getInputStream(name);
                    if (is != null) {
                        if (lengthOut != null) {
                            lengthOut[INPUT_STATUS_CANCELLED] = ((TexturePack) this.textureOverrides.get(i)).getSize(name);
                        }
                        return is;
                    }
                    i += INPUT_STATUS_OK;
                } catch (IOException e) {
                }
            } catch (Exception e2) {
                System.err.println(e2);
                return null;
            }
        }
        if (this.texturePack == null) {
            return getLocalInputStreamForAsset(name, lengthOut);
        }
        System.out.println("Trying to load  " + name + "from tp");
        is = this.texturePack.getInputStream(name);
        if (is != null) {
            return is;
        }
        System.out.println("Can't load " + name + " from tp");
        return getLocalInputStreamForAsset(name, lengthOut);
    }

    protected InputStream getLocalInputStreamForAsset(String name) {
        return getLocalInputStreamForAsset(name, null);
    }

    protected InputStream openFallbackAsset(String name) throws IOException {
        if (getMCPEVersion().startsWith(HALF_SUPPORT_VERSION)) {
            try {
                return getAssets().open("1007/" + name);
            } catch (IOException ie) {
                if (this.textureVerbose) {
                    System.err.println(ie);
                }
            }
        }
        return getAssets().open(name);
    }

    protected InputStream getLocalInputStreamForAsset(String name, long[] lengthOut) {
        try {
            if (this.forceFallback) {
                return openFallbackAsset(name);
            }
            InputStream is;
            try {
                is = this.minecraftApkContext.getAssets().open(name);
            } catch (Exception e) {
                if (this.textureVerbose) {
                    System.out.println("Attempting to load fallback");
                }
                is = openFallbackAsset(name);
            }
            if (is == null) {
                if (this.textureVerbose) {
                    System.out.println("Can't find it in the APK - attempting to load fallback");
                }
                is = openFallbackAsset(name);
            }
            if (!(is == null || lengthOut == null)) {
                lengthOut[INPUT_STATUS_CANCELLED] = (long) is.available();
            }
            return is;
        } catch (Exception e2) {
            if (this.textureVerbose) {
                System.err.println(e2);
            }
            return null;
        }
    }

    public String[] listDirForPath(String dirPath) {
        String[] origDir;
        int i$;
        System.out.println("Listing dir for " + dirPath);
        String prefix = dirPath + "/";
        Set<String> outList = new HashSet();
        for (TexturePack pack : this.textureOverrides) {
            try {
                for (String path : pack.listFiles()) {
                    if (path.startsWith(prefix) && path.indexOf("/", prefix.length()) == INPUT_STATUS_IN_PROGRESS) {
                        outList.add(path.substring(path.lastIndexOf("/")));
                    }
                }
            } catch (IOException e) {
            }
        }
        String newPath = dirPath;
        if (getMCPEVersion().startsWith(HALF_SUPPORT_VERSION)) {
            newPath = "1007/" + dirPath;
        }
        try {
            origDir = getAssets().list(newPath);
        } catch (IOException e2) {
            origDir = new String[INPUT_STATUS_CANCELLED];
        }
        String[] arr$ = origDir;
        int len$ = arr$.length;
        for (i$ = INPUT_STATUS_CANCELLED; i$ < len$; i$ += INPUT_STATUS_OK) {
            outList.add(arr$[i$]);
        }
        try {
            origDir = this.minecraftApkContext.getAssets().list(dirPath);
        } catch (IOException e3) {
            origDir = new String[INPUT_STATUS_CANCELLED];
        }
        arr$ = origDir;
        len$ = arr$.length;
        for (i$ = INPUT_STATUS_CANCELLED; i$ < len$; i$ += INPUT_STATUS_OK) {
            outList.add(arr$[i$]);
        }
        String[] retval = (String[]) outList.toArray(origDir);
        System.out.println(Arrays.toString(retval));
        return retval;
    }

    public boolean existsForPath(String path) {
        InputStream is;
        if ((!path.startsWith("resource_packs/") || path.startsWith("resource_packs/vanilla")) && (!path.startsWith("resourcepacks") || path.startsWith("resourcepacks/vanilla"))) {
            is = getInputStreamForAsset(path);
        } else {
            is = getLocalInputStreamForAsset(path);
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        if (is != null) {
            return true;
        }
        return false;
    }

    public int[] getImageData(String name) {
        return getImageData(name, true);
    }

    public int[] getImageData(String name, boolean fromAssets) {
        boolean externalData = true;
        System.out.println("Get image data: " + name + " from assets? " + fromAssets);
        if (name.length() <= 0 || name.charAt(INPUT_STATUS_CANCELLED) != '/') {
            externalData = false;
        }
        if (externalData) {
            try {
                InputStream is = getRegularInputStream(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        is = getInputStreamForAsset(name);
        if (is == null) {
            return getFakeImageData(name, fromAssets);
        }
        Bitmap bmp = BitmapFactory.decodeStream(is);
        int[] retval = new int[((bmp.getWidth() * bmp.getHeight()) + MAX_FAILS)];
        retval[INPUT_STATUS_CANCELLED] = bmp.getWidth();
        retval[INPUT_STATUS_OK] = bmp.getHeight();
        bmp.getPixels(retval, MAX_FAILS, bmp.getWidth(), INPUT_STATUS_CANCELLED, INPUT_STATUS_CANCELLED, bmp.getWidth(), bmp.getHeight());
        is.close();
        bmp.recycle();
        return retval;
    }

    public int[] getFakeImageData(String name, boolean fromAssets) {
        return new int[]{INPUT_STATUS_OK, INPUT_STATUS_OK, INPUT_STATUS_CANCELLED};
    }

    public String[] getOptionStrings() {
        System.err.println("OptionStrings");
        SharedPreferences sharedPref = Utils.getPrefs(INPUT_STATUS_CANCELLED);
        Set<Entry> prefsSet = sharedPref.getAll().entrySet();
        List<String> retval = new ArrayList();
        for (Entry<String, ?> e : prefsSet) {
            String key = (String) e.getKey();
            if (key.indexOf("zz_") != 0) {
                retval.add(key);
                if (key.equals("ctrl_sensitivity")) {
                    retval.add(Double.toString(((double) Integer.parseInt(e.getValue().toString())) / EventEnums.SampleRate_NoSampling));
                } else {
                    retval.add(e.getValue().toString());
                }
            }
        }
        retval.add("game_difficulty");
        if (sharedPref.getBoolean("game_difficultypeaceful", false)) {
            retval.add(MigrationManager.InitialSdkVersion);
        } else {
            retval.add(XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
        }
        System.out.println(retval.toString());
        return (String[]) retval.toArray(new String[INPUT_STATUS_CANCELLED]);
    }

    public float getPixelsPerMillimeter() {
        System.out.println("Pixels per mm");
        float val = ((float) this.displayMetrics.densityDpi) / 25.4f;
        String custom = Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("zz_custom_dpi", null);
        if (custom == null || custom.length() <= 0) {
            return val;
        }
        try {
            return Float.parseFloat(custom) / 25.4f;
        } catch (Exception e) {
            e.printStackTrace();
            return val;
        }
    }

    public String getPlatformStringVar(int a) {
        System.out.println("getPlatformStringVar: " + a);
        return BuildConfig.FLAVOR;
    }

    public int getScreenHeight() {
        System.out.println("height");
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        System.out.println("width");
        return this.displayMetrics.widthPixels;
    }

    public int getUserInputStatus() {
        System.out.println("User input status: " + this.inputStatus);
        return this.inputStatus;
    }

    public String[] getUserInputString() {
        System.out.println("User input string");
        return this.userInputStrings;
    }

    public boolean hasBuyButtonWhenInvalidLicense() {
        return false;
    }

    public void initiateUserInput(int a) {
        System.out.println("initiateUserInput: " + a);
    }

    public boolean isNetworkEnabled(boolean a) {
        return true;
    }

    public boolean isTouchscreen() {
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("ctrl_usetouchscreen", true);
    }

    public void postScreenshotToFacebook(String name, int firstInt, int secondInt, int[] thatArray) {
    }

    public void quit() {
        finish();
    }

    public void setIsPowerVR(boolean powerVR) {
        System.out.println("PowerVR: " + powerVR);
    }

    public void tick() {
    }

    public void vibrate(int duration) {
        if (Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_longvibration", false)) {
            duration *= 5;
        }
        ((Vibrator) getSystemService("vibrator")).vibrate((long) duration);
    }

    public int getKeyFromKeyCode(int keyCode, int metaState, int deviceId) {
        return KeyCharacterMap.load(deviceId).get(keyCode, metaState);
    }

    public static void saveScreenshot(String name, int firstInt, int secondInt, int[] thatArray) {
    }

    public int abortWebRequest(int requestId) {
        Log.i(TAG, "Abort web request: " + requestId);
        HurlRunner runner = (HurlRunner) this.requestMap.get(requestId);
        if (runner != null) {
            runner.isValid = false;
        }
        return INPUT_STATUS_CANCELLED;
    }

    public String getRefreshToken() {
        Log.i(TAG, "Get Refresh token");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("refreshToken", BuildConfig.FLAVOR);
    }

    public String getSession() {
        Log.i(TAG, "Get Session");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("sessionId", BuildConfig.FLAVOR);
    }

    public String getWebRequestContent(int requestId) {
        Log.i(TAG, "Get web request content: " + requestId);
        return "ThisIsSparta";
    }

    public int getWebRequestStatus(int requestId) {
        Log.i(TAG, "Get web request status: " + requestId);
        return INPUT_STATUS_CANCELLED;
    }

    public void openLoginWindow() {
        Log.i(TAG, "Open login window");
        runOnUiThread(new Runnable() {
            @SuppressLint({"SetJavaScriptEnabled"})
            public void run() {
                MainActivity.this.loginWebView = new WebView(MainActivity.this);
                MainActivity.this.loginWebView.setLayoutParams(new LayoutParams(MainActivity.INPUT_STATUS_IN_PROGRESS, MainActivity.INPUT_STATUS_IN_PROGRESS));
                MainActivity.this.loginWebView.setWebViewClient(new LoginWebViewClient());
                MainActivity.this.loginWebView.getSettings().setJavaScriptEnabled(true);
                MainActivity.this.loginDialog = new Dialog(MainActivity.this);
                MainActivity.this.loginDialog.setCancelable(true);
                MainActivity.this.loginDialog.requestWindowFeature(MainActivity.INPUT_STATUS_OK);
                MainActivity.this.loginDialog.setContentView(MainActivity.this.loginWebView);
                MainActivity.this.loginDialog.getWindow().setLayout(MainActivity.INPUT_STATUS_IN_PROGRESS, MainActivity.INPUT_STATUS_IN_PROGRESS);
                MainActivity.this.loginDialog.show();
                MainActivity.this.loginWebView.loadUrl(MainActivity.this.getRealmsRedirectInfo().loginUrl);
            }
        });
    }

    public void setRefreshToken(String token) {
        Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putString("refreshToken", token).apply();
    }

    public void setSession(String session) {
        Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putString("sessionId", session).apply();
    }

    public boolean supportsNonTouchscreen() {
        if (!isForcingController()) {
            boolean xperia = false;
            boolean play = false;
            String[] data = new String[DIALOG_SETTINGS];
            data[INPUT_STATUS_CANCELLED] = Build.MODEL.toLowerCase(Locale.ENGLISH);
            data[INPUT_STATUS_OK] = Build.DEVICE.toLowerCase(Locale.ENGLISH);
            data[MAX_FAILS] = Build.PRODUCT.toLowerCase(Locale.ENGLISH);
            String[] arr$ = data;
            int len$ = arr$.length;
            for (int i$ = INPUT_STATUS_CANCELLED; i$ < len$; i$ += INPUT_STATUS_OK) {
                String s = arr$[i$];
                if (s.indexOf("xperia") >= 0) {
                    xperia = true;
                }
                if (s.indexOf("play") >= 0) {
                    play = true;
                }
            }
            if (xperia && play) {
                return true;
            }
            return false;
        } else if (this.controllerInit || Utils.isSafeMode()) {
            return true;
        } else {
            ControllerManager.init();
            this.controllerInit = true;
            return true;
        }
    }

    public void webRequest(int requestId, long timestamp, String url, String method, String cookies) {
        webRequest(requestId, timestamp, url, method, cookies, BuildConfig.FLAVOR);
    }

    public void webRequest(int requestId, long timestamp, String url, String method, String cookies, String extraParam) {
        new Thread(new HurlRunner(requestId, timestamp, filterUrl(url), method, cookies)).start();
    }

    protected String filterUrl(String url) {
        return url;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == MAX_FAILS && event.getKeyCode() == 0) {
            try {
                nativeTypeCharacter(event.getCharacters());
                return true;
            } catch (UnsatisfiedLinkError e) {
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void showKeyboardView() {
        Log.i(TAG, "Show keyboard view");
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(getWindow().getDecorView(), MAX_FAILS);
    }

    public String getAccessToken() {
        Log.i(TAG, "Get access token");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("accessToken", BuildConfig.FLAVOR);
    }

    public String getClientId() {
        Log.i(TAG, "Get client ID");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("clientId", BuildConfig.FLAVOR);
    }

    public String getProfileId() {
        Log.i(TAG, "Get profile ID");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("profileUuid", BuildConfig.FLAVOR);
    }

    public String getProfileName() {
        Log.i(TAG, "Get profile name");
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("profileName", BuildConfig.FLAVOR);
    }

    public void statsTrackEvent(String firstEvent, String secondEvent) {
        Log.i(TAG, "Stats track: " + firstEvent + ":" + secondEvent);
    }

    public void statsUpdateUserData(String firstEvent, String secondEvent) {
        Log.i(TAG, "Stats update user data: " + firstEvent + ":" + secondEvent);
    }

    public boolean isDemo() {
        Log.i(TAG, "Is demo");
        return false;
    }

    public void setLoginInformation(String accessToken, String clientId, String profileUuid, String profileName) {
        Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putString("accessToken", accessToken).putString("clientId", clientId).putString("profileUuid", profileUuid).putString("profileName", profileName).apply();
    }

    public void clearLoginInformation() {
        Log.i(TAG, "Clear login info");
        Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putString("accessToken", BuildConfig.FLAVOR).putString("clientId", BuildConfig.FLAVOR).putString("profileUuid", BuildConfig.FLAVOR).putString("profileName", BuildConfig.FLAVOR).apply();
    }

    public void showKeyboard(String mystr, int maxLength, boolean mybool) {
        showKeyboard(mystr, maxLength, mybool, false);
    }

    public void showKeyboard(final String mystr, final int maxLength, final boolean mybool, boolean mybool2) {
        if (useLegacyKeyboardInput()) {
            showKeyboardView();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.showHiddenTextbox(mystr, maxLength, mybool);
                }
            });
        }
    }

    public void hideKeyboard() {
        if (useLegacyKeyboardInput()) {
            hideKeyboardView();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.dismissHiddenTextbox();
                }
            });
        }
    }

    public void updateTextboxText(final String text) {
        if (this.hiddenTextView != null) {
            this.hiddenTextView.post(new Runnable() {
                public void run() {
                    if (MainActivity.this.isCommandHistoryEnabled()) {
                        if (MainActivity.this.commandHistoryList.size() < MainActivity.INPUT_STATUS_OK || ((String) MainActivity.this.commandHistoryList.get(MainActivity.this.commandHistoryList.size() + MainActivity.INPUT_STATUS_IN_PROGRESS)).length() > 0) {
                            MainActivity.this.commandHistoryList.add(text);
                        } else {
                            MainActivity.this.commandHistoryList.set(MainActivity.this.commandHistoryList.size() + MainActivity.INPUT_STATUS_IN_PROGRESS, text);
                        }
                        MainActivity.this.setCommandHistoryIndex(MainActivity.this.commandHistoryList.size() + MainActivity.INPUT_STATUS_IN_PROGRESS);
                    }
                    MainActivity.this.hiddenTextView.setText(text);
                }
            });
        }
    }

    public void showHiddenTextbox(String text, int maxLength, boolean dismissAfterOneLine) {
        boolean commandHistory = isCommandHistoryEnabled();
        if (this.hiddenTextWindow == null) {
            if (commandHistory) {
                this.commandHistoryView = getLayoutInflater().inflate(R.layout.chat_history_popup, null);
                this.hiddenTextView = (TextView) this.commandHistoryView.findViewById(R.id.hidden_text_view);
                this.prevButton = (Button) this.commandHistoryView.findViewById(R.id.command_history_previous);
                this.nextButton = (Button) this.commandHistoryView.findViewById(R.id.command_history_next);
                View.OnClickListener listener = new View.OnClickListener() {
                    public void onClick(View v) {
                        if (v == MainActivity.this.prevButton) {
                            MainActivity.this.navigateCommandHistory(MainActivity.INPUT_STATUS_IN_PROGRESS);
                        } else if (v == MainActivity.this.nextButton) {
                            MainActivity.this.navigateCommandHistory(MainActivity.INPUT_STATUS_OK);
                        }
                    }
                };
                this.prevButton.setOnClickListener(listener);
                this.nextButton.setOnClickListener(listener);
            } else {
                this.hiddenTextView = new EditText(this);
            }
            PopupTextWatcher whoWatchesTheWatcher = new PopupTextWatcher();
            this.hiddenTextView.addTextChangedListener(whoWatchesTheWatcher);
            this.hiddenTextView.setOnEditorActionListener(whoWatchesTheWatcher);
            this.hiddenTextView.setSingleLine(true);
            this.hiddenTextView.setImeOptions(301989893);
            this.hiddenTextView.setInputType(INPUT_STATUS_OK);
            if (commandHistory) {
                this.hiddenTextWindow = new PopupWindow(this.commandHistoryView);
            } else {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.addView(this.hiddenTextView);
                this.hiddenTextWindow = new PopupWindow(linearLayout);
            }
            this.hiddenTextWindow.setWindowLayoutMode(-2, -2);
            this.hiddenTextWindow.setFocusable(true);
            this.hiddenTextWindow.setInputMethodMode(INPUT_STATUS_OK);
            this.hiddenTextWindow.setBackgroundDrawable(new ColorDrawable());
            this.hiddenTextWindow.setClippingEnabled(false);
            this.hiddenTextWindow.setTouchable(commandHistory);
            this.hiddenTextWindow.setOutsideTouchable(true);
            this.hiddenTextWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    MainActivity.this.nativeBackPressed();
                }
            });
        }
        if (commandHistory) {
            for (int i = this.commandHistoryList.size() + INPUT_STATUS_IN_PROGRESS; i >= 0; i += INPUT_STATUS_IN_PROGRESS) {
                if (((String) this.commandHistoryList.get(i)).equals(BuildConfig.FLAVOR)) {
                    this.commandHistoryList.remove(i);
                }
            }
            this.commandHistoryList.add(text);
            setCommandHistoryIndex(this.commandHistoryList.size() + INPUT_STATUS_IN_PROGRESS);
        }
        this.hiddenTextView.setText(text);
        Selection.setSelection((Spannable) this.hiddenTextView.getText(), text.length());
        this.hiddenTextDismissAfterOneLine = dismissAfterOneLine;
        this.hiddenTextWindow.showAtLocation(getWindow().getDecorView(), 51, commandHistory ? INPUT_STATUS_CANCELLED : -10000, INPUT_STATUS_CANCELLED);
        this.hiddenTextView.requestFocus();
        showKeyboardView();
    }

    public void dismissHiddenTextbox() {
        if (this.hiddenTextWindow != null) {
            this.hiddenTextWindow.dismiss();
            hideKeyboardView();
        }
    }

    public String[] getBroadcastAddresses() {
        Log.i(TAG, "get broadcast addresses");
        String[] strArr = new String[INPUT_STATUS_OK];
        strArr[INPUT_STATUS_CANCELLED] = "255.255.255.255";
        return strArr;
    }

    public long getTotalMemory() {
        try {
            long retval = Utils.parseMemInfo();
            Log.i(TAG, "Get total memory: " + retval);
            return retval;
        } catch (Exception e) {
            e.printStackTrace();
            return 17179869184L;
        }
    }

    public String getDeviceModel() {
        return HardwareInformation.getDeviceModelName();
    }

    public int getAndroidVersion() {
        return VERSION.SDK_INT;
    }

    private boolean useLegacyKeyboardInput() {
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_legacy_keyboard_input", false);
    }

    public void initPatching() throws Exception {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("mcpelauncher_tinysubstrate");
        System.out.println("MCPE Version is " + getMCPEVersion());
        if (getMCPEVersion().startsWith(HALF_SUPPORT_VERSION)) {
            System.loadLibrary("mcpelauncher_new");
        } else {
            System.loadLibrary("mcpelauncher");
        }
        if (!MaraudersMap.initPatching(this, findMinecraftLibLength())) {
            System.out.println("Well, that sucks!");
            tempSafeMode = true;
            this.overlyZealousSELinuxSafeMode = true;
        }
    }

    public static long findMinecraftLibLength() throws Exception {
        return new File(MC_NATIVE_LIBRARY_LOCATION).length();
    }

    public int getMaxNumPatches() {
        return getResources().getInteger(R.integer.max_num_patches);
    }

    public boolean doesRequireGuiBlocksPatch() {
        return false;
    }

    protected void setupHoverCar() {
        boolean z = false;
        this.hoverCar = new HoverCar(this, Utils.isSafeMode());
        this.hoverCar.show(getWindow().getDecorView());
        HoverCar hoverCar = this.hoverCar;
        if (!Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_hovercar_hide", false)) {
            z = true;
        }
        hoverCar.setVisible(z);
        this.hoverCar.mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.showDialog(Utils.getPrefs(MainActivity.INPUT_STATUS_CANCELLED).getBoolean("zz_show_insert_text", false) ? MainActivity.DIALOG_RUNTIME_OPTIONS_WITH_INSERT_TEXT : MainActivity.DIALOG_RUNTIME_OPTIONS);
                MainActivity.this.resetOrientation();
            }
        });
    }

    protected void loadNativeAddons() {
        if (Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_load_native_addons", true)) {
            PackageManager pm = getPackageManager();
            AddonManager addonManager = AddonManager.getAddonManager(this);
            List<ApplicationInfo> apps = pm.getInstalledApplications(Token.RESERVED);
            StringBuilder archFail = new StringBuilder();
            for (ApplicationInfo app : apps) {
                if (app.metaData != null) {
                    String nativeLibName = app.metaData.getString("net.zhuoweizhang.mcpelauncher.api.nativelibname");
                    String targetMCPEVersion = app.metaData.getString("net.zhuoweizhang.mcpelauncher.api.targetmcpeversion");
                    if (pm.checkPermission(permission.ADDON, app.packageName) == 0 && addonManager.isEnabled(app.packageName)) {
                        try {
                            if (!isAddonCompat(targetMCPEVersion)) {
                                throw new Exception("The addon \"" + pm.getApplicationLabel(app).toString() + "\" (" + app.packageName + ")" + " is not compatible with Minecraft PE " + this.mcPkgInfo.versionName + ".");
                            } else if (nativeLibName == null) {
                                loadedAddons.add(app.packageName);
                            } else if (checkAddonArch(new File(app.nativeLibraryDir + "/lib" + nativeLibName + ".so"))) {
                                System.load(app.nativeLibraryDir + "/lib" + nativeLibName + ".so");
                                loadedAddons.add(app.packageName);
                            } else {
                                archFail.append("\"").append(pm.getApplicationLabel(app).toString()).append("\" (").append(app.packageName).append(") ");
                            }
                        } catch (Throwable e) {
                            reportError(e);
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (archFail.length() != 0) {
                reportError(new Exception(getResources().getString(R.string.addons_wrong_arch).toString().replaceAll("ARCH", Utils.getArchName(this.mcpeArch)).replaceAll("ADDONS", archFail.toString())));
            }
            if (getMCPEVersion().startsWith(SCRIPT_SUPPORT_VERSION)) {
                this.addonOverrideTexturePackInstance = new AddonOverrideTexturePack(this, "resourcepacks/vanilla/");
            } else {
                this.addonOverrideTexturePackInstance = new AddonOverrideTexturePack(this, "resource_packs/vanilla/");
            }
            this.textureOverrides.add(this.addonOverrideTexturePackInstance);
        }
    }

    protected void migrateToPatchManager() {
        boolean enabledPatchMgr = true;
        try {
            if (Utils.getPrefs(INPUT_STATUS_OK).getInt("patchManagerVersion", INPUT_STATUS_IN_PROGRESS) <= 0) {
                enabledPatchMgr = false;
            }
            if (!enabledPatchMgr) {
                showDialog(DIALOG_FIRST_LAUNCH);
                PatchManager.getPatchManager(this).setEnabled(getDir(PT_PATCHES_DIR, INPUT_STATUS_CANCELLED).listFiles(), true);
                System.out.println(Utils.getPrefs(INPUT_STATUS_OK).getString("enabledPatches", "LOL"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void applyBuiltinPatches() {
    }

    protected void loadTexturePackOld() {
        String filePath = null;
        try {
            boolean loadTexturePack = Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_texture_pack_enable", false);
            filePath = Utils.getPrefs(INPUT_STATUS_OK).getString("texturePack", null);
            if (!loadTexturePack || filePath == null) {
                this.texturePack = null;
                return;
            }
            File file = new File(filePath);
            if (file.exists()) {
                this.texturePack = new ZipTexturePack(file);
            } else {
                this.texturePack = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            reportError(e, R.string.texture_pack_unable_to_load, filePath + ": size is " + new File(filePath).length());
        }
    }

    protected void loadTexturePack() {
        try {
            boolean loadTexturePack = Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_texture_pack_enable", false);
            this.texturePack = null;
            if (loadTexturePack) {
                List<String> incompatible = new ArrayList();
                List<TexturePack> packs = TexturePackLoader.loadTexturePacks(this, incompatible, getFileDataBytes("images/terrain.meta", true), getFileDataBytes("images/items.meta", true));
                if (incompatible.size() != 0) {
                    new Builder(this).setMessage("Some of your texture packs are not compatible with Minecraft PE " + getMCPEVersion() + ". Please update " + Utils.join(incompatible, ", ") + ".").setPositiveButton(17039370, null).show();
                }
                this.textureOverrides.addAll(packs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reportError(e, R.string.texture_pack_unable_to_load, null);
        }
    }

    private void enableSoftMenuKey() {
        getWindow().addFlags(VERSION.SDK_INT >= 19 ? 1073741824 : 134217728);
    }

    private void disableTransparentSystemBar() {
        if (VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(Integer.MIN_VALUE);
        }
    }

    private void disableAllPatches() {
        PatchManager.getPatchManager(this).disableAllPatches();
    }

    protected void loginLaunchCallback(Uri launchUri) {
        this.loginDialog.dismiss();
        if (launchUri.getQueryParameter("sessionId") != null) {
            String profileName = launchUri.getQueryParameter("profileName");
            String refreshToken = launchUri.getQueryParameter("identity");
            nativeLoginData(launchUri.getQueryParameter("accessToken"), launchUri.getQueryParameter("clientToken"), launchUri.getQueryParameter("profileUuid"), profileName);
        }
    }

    protected Intent getOptionsActivityIntent() {
        return new Intent(this, MainMenuOptionsActivity.class);
    }

    public boolean isRedirectingRealms() {
        return false;
    }

    public RealmsRedirectInfo getRealmsRedirectInfo() {
        return (RealmsRedirectInfo) RealmsRedirectInfo.targets.get("NONE");
    }

    private void turnOffSafeMode() {
        Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putBoolean("zz_safe_mode", false).commit();
        Utils.getPrefs(INPUT_STATUS_OK).edit().putBoolean("force_prepatch", true).commit();
        finish();
        NerdyStuffActivity.forceRestart(this);
    }

    public void hideKeyboardView() {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), INPUT_STATUS_CANCELLED);
        touchImmersiveMode();
    }

    public void pickImage(long callbackAddress) {
        this.pickImageCallbackAddress = callbackAddress;
        startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), REQUEST_PICK_IMAGE);
    }

    public String getDeviceId() {
        String deviceId = Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("snooperId", null);
        if (deviceId == null) {
            deviceId = createUUID();
            Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putString("snooperId", deviceId).apply();
        }
        System.out.println("Get device ID");
        return deviceId;
    }

    public String createUUID() {
        System.out.println("Create UUID");
        return UUID.randomUUID().toString().replace("-", BuildConfig.FLAVOR);
    }

    public String getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public boolean isFirstSnooperStart() {
        System.out.println("Is first snooper start");
        if (Utils.getPrefs(INPUT_STATUS_CANCELLED).getString("snooperId", null) == null) {
            return true;
        }
        return false;
    }

    public boolean hasHardwareChanged() {
        return false;
    }

    public boolean isTablet() {
        if (VERSION.SDK_INT >= 13 && getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            return true;
        }
        return false;
    }

    public float getKeyboardHeight() {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        if (r.bottom == 0) {
            return 0.0f;
        }
        return (float) (this.displayMetrics.heightPixels - r.bottom);
    }

    public void launchUri(String theUri) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(theUri)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFileDialogCallback(long pointer) {
    }

    public void updateLocalization(String a, String b) {
        System.out.println("Update localization: " + a + ":" + b);
    }

    public String[] getIPAddresses() {
        System.out.println("get IP addresses?");
        String[] strArr = new String[INPUT_STATUS_OK];
        strArr[INPUT_STATUS_CANCELLED] = "127.0.0.1";
        return strArr;
    }

    public Intent getLaunchIntent() {
        System.out.println("get launch intent");
        return getIntent();
    }

    public Intent createAndroidLaunchIntent() {
        System.out.println("create android launch intent");
        return getIntent();
    }

    public void startTextToSpeech(String a) {
        System.out.println("Text to speech: " + a);
        if (this.tts != null) {
            this.tts.speak(a, INPUT_STATUS_OK, null);
        }
    }

    public void stopTextToSpeech() {
        System.out.println("Shutting up");
        if (this.tts != null) {
            this.tts.stop();
        }
    }

    public boolean isTextToSpeechInProgress() {
        return false;
    }

    public void setTextToSpeechEnabled(boolean enabled) {
        System.out.println("Text to speech?");
        if (enabled) {
            if (this.tts == null) {
                this.tts = new TextToSpeech(this, null);
            }
        } else if (this.tts != null) {
            this.tts.shutdown();
            this.tts = null;
        }
    }

    public void setClipboard(String text) {
        ((ClipboardManager) getSystemService("clipboard")).setText(text);
    }

    public long calculateAvailableDiskFreeSpace(String disk) {
        System.out.println("Calculate disk free space: " + disk);
        return 0;
    }

    public int getCursorPosition() {
        if (this.hiddenTextView == null) {
            return INPUT_STATUS_CANCELLED;
        }
        return this.hiddenTextView.getSelectionStart();
    }

    public void onBackPressed() {
        nativeBackPressed();
    }

    private InputStream getRegularInputStream(String path) {
        try {
            return new BufferedInputStream(new FileInputStream(new File(path)));
        } catch (IOException ie) {
            ie.printStackTrace();
            return null;
        }
    }

    private File copyContentStoreToTempFile(Uri content) {
        try {
            File file = new File(getExternalFilesDir(null), "skintemp.png");
            file.getParentFile().mkdirs();
            InputStream is = getContentResolver().openInputStream(content);
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[DIALOG_CRASH_SAFE_MODE];
            while (true) {
                int count = is.read(buffer);
                if (count != INPUT_STATUS_IN_PROGRESS) {
                    os.write(buffer, INPUT_STATUS_CANCELLED, count);
                } else {
                    is.close();
                    os.close();
                    return file;
                }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
            return new File("/sdcard/totally/fake");
        }
    }

    public void setLevelCallback(boolean isRemote) {
        System.out.println("Set level callback: " + isRemote);
        if (isRemote && ScriptManager.scripts.size() > 0) {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.showDialog(MainActivity.DIALOG_MULTIPLAYER_DISABLE_SCRIPTS);
                }
            });
        }
        if (this.hasRecorder) {
            clearRuntimeOptionsDialog();
        }
    }

    public void leaveGameCallback() {
        System.out.println("Leave game");
        if (this.hasRecorder) {
            clearRuntimeOptionsDialog();
        }
    }

    public void scriptPrintCallback(final String message, final String scriptName) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Script " + scriptName + ": " + message, MainActivity.INPUT_STATUS_CANCELLED).show();
            }
        });
    }

    public void fakeTipMessageCallback(String messageRaw) {
        if (this.minecraftTypeface == null) {
            this.minecraftTypeface = Typeface.createFromAsset(getAssets(), "fonts/minecraft.ttf");
        }
        final String message = messageRaw.replaceAll("\u00a7.", BuildConfig.FLAVOR);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView toastText = new TextView(MainActivity.this);
                toastText.setText(message);
                toastText.setTypeface(MainActivity.this.minecraftTypeface);
                toastText.setTextColor(MainActivity.INPUT_STATUS_IN_PROGRESS);
                toastText.setShadowLayer(0.1f, AccountHeaderView.MarginMediumDip, AccountHeaderView.MarginMediumDip, -16777216);
                toastText.setTextSize(AccountHeaderView.TextSizeLargeSP);
                if (MainActivity.this.lastToast != null) {
                    MainActivity.this.lastToast.cancel();
                }
                Toast myToast = new Toast(MainActivity.this);
                myToast.setView(toastText);
                MainActivity.this.lastToast = myToast;
                myToast.show();
            }
        });
    }

    public void scriptOverrideTexture(String theOverridden, String url) {
        forceTextureReload();
    }

    public void scriptResetImages() {
        forceTextureReload();
    }

    public void forceTextureReload() {
        ScriptManager.nativeOnGraphicsReset();
    }

    private static String stringFromInputStream(InputStream in, int startingLength) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(startingLength);
        try {
            byte[] buffer = new byte[EnchantType.pickaxe];
            while (true) {
                int count = in.read(buffer);
                if (count == INPUT_STATUS_IN_PROGRESS) {
                    break;
                }
                bytes.write(buffer, INPUT_STATUS_CANCELLED, count);
            }
            String byteArrayOutputStream = bytes.toString(HttpURLConnectionBuilder.DEFAULT_CHARSET);
            return byteArrayOutputStream;
        } finally {
            bytes.close();
        }
    }

    public void reportError(Throwable t) {
        reportError(t, R.string.report_error_title, null);
    }

    public void reportError(final Throwable t, final int messageId, final String extraData) {
        runOnUiThread(new Runnable() {
            public void run() {
                String msg;
                StringWriter strWriter = new StringWriter();
                t.printStackTrace(new PrintWriter(strWriter));
                if (extraData != null) {
                    msg = extraData + "\n" + strWriter.toString();
                } else {
                    msg = strWriter.toString();
                }
                new Builder(MainActivity.this).setTitle(messageId).setMessage(msg).setPositiveButton(17039370, null).setNeutralButton(17039361, new OnClickListener() {
                    public void onClick(DialogInterface aDialog, int button) {
                        ((ClipboardManager) MainActivity.this.getSystemService("clipboard")).setText(msg);
                    }
                }).show();
            }
        });
    }

    public void scriptErrorCallback(final String scriptName, final Throwable t) {
        runOnUiThread(new Runnable() {
            public void run() {
                final StringWriter strWriter = new StringWriter();
                PrintWriter pWriter = new PrintWriter(strWriter);
                pWriter.println("Error occurred in script: " + scriptName);
                if (t instanceof RhinoException) {
                    String lineSource = ((RhinoException) t).lineSource();
                    if (lineSource != null) {
                        pWriter.println(lineSource);
                    }
                }
                t.printStackTrace(pWriter);
                new Builder(MainActivity.this).setTitle(R.string.script_execution_error).setMessage(strWriter.toString()).setPositiveButton(17039370, null).setNeutralButton(17039361, new OnClickListener() {
                    public void onClick(DialogInterface aDialog, int button) {
                        ((ClipboardManager) MainActivity.this.getSystemService("clipboard")).setText(strWriter.toString());
                    }
                }).show();
            }
        });
    }

    protected void resetOrientation() {
    }

    public void scriptTooManyErrorsCallback(final String scriptName) {
        runOnUiThread(new Runnable() {
            public void run() {
                new Builder(MainActivity.this).setTitle(R.string.script_execution_error).setMessage(scriptName + " " + MainActivity.this.getResources().getString(R.string.script_too_many_errors)).setPositiveButton(17039370, null).show();
            }
        });
    }

    public void screenshotCallback(final File file) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.screenshot_saved_as) + " " + file.getAbsolutePath(), MainActivity.INPUT_STATUS_OK).show();
                Context context = MainActivity.this;
                String[] strArr = new String[MainActivity.INPUT_STATUS_OK];
                strArr[MainActivity.INPUT_STATUS_CANCELLED] = file.getAbsolutePath();
                String[] strArr2 = new String[MainActivity.INPUT_STATUS_OK];
                strArr2[MainActivity.INPUT_STATUS_CANCELLED] = "image/png";
                MediaScannerConnection.scanFile(context, strArr, strArr2, null);
            }
        });
    }

    protected boolean allowScriptOverrideTextures() {
        return true;
    }

    private void addLibraryDirToPath(String path) {
        try {
            ClassLoader classLoader = getClassLoader();
            Field field = Utils.getDeclaredFieldRecursive(classLoader.getClass(), "pathList");
            field.setAccessible(true);
            Object pathListObj = field.get(classLoader);
            Class<? extends Object> pathListClass = pathListObj.getClass();
            Field natfield = Utils.getDeclaredFieldRecursive(pathListClass, "nativeLibraryDirectories");
            natfield.setAccessible(true);
            Object theFileList = natfield.get(pathListObj);
            if (theFileList instanceof File[]) {
                File[] fileList = (File[]) theFileList;
                File[] newList = addToFileList(fileList, new File(path));
                if (fileList != newList) {
                    natfield.set(pathListObj, newList);
                }
            }
            Field natElemsField = Utils.getDeclaredFieldRecursive(pathListClass, "nativeLibraryPathElements");
            if (natElemsField != null && (classLoader instanceof BaseDexClassLoader) && ((BaseDexClassLoader) classLoader).findLibrary("minecraftpe") == null) {
                natElemsField.setAccessible(true);
                Object[] theObjects = (Object[]) natElemsField.get(pathListObj);
                Class<? extends Object> elemClass = theObjects.getClass().getComponentType();
                Class[] clsArr = new Class[DIALOG_COPY_WORLD];
                clsArr[INPUT_STATUS_CANCELLED] = File.class;
                clsArr[INPUT_STATUS_OK] = Boolean.TYPE;
                clsArr[MAX_FAILS] = File.class;
                clsArr[DIALOG_SETTINGS] = DexFile.class;
                Constructor<? extends Object> elemConstructor = elemClass.getConstructor(clsArr);
                elemConstructor.setAccessible(true);
                File[] fileArr = new Object[DIALOG_COPY_WORLD];
                fileArr[INPUT_STATUS_CANCELLED] = new File(path);
                fileArr[INPUT_STATUS_OK] = Boolean.valueOf(true);
                fileArr[MAX_FAILS] = null;
                fileArr[DIALOG_SETTINGS] = null;
                Object newObject = elemConstructor.newInstance(fileArr);
                Object[] newObjects = Arrays.copyOf(theObjects, theObjects.length + INPUT_STATUS_OK);
                newObjects[newObjects.length + INPUT_STATUS_IN_PROGRESS] = newObject;
                System.out.println(newObjects);
                natElemsField.set(pathListObj, newObjects);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File[] addToFileList(File[] files, File toAdd) {
        File[] arr$ = files;
        int len$ = arr$.length;
        for (int i$ = INPUT_STATUS_CANCELLED; i$ < len$; i$ += INPUT_STATUS_OK) {
            if (arr$[i$].equals(toAdd)) {
                return files;
            }
        }
        File[] retval = new File[(files.length + INPUT_STATUS_OK)];
        System.arraycopy(files, INPUT_STATUS_CANCELLED, retval, INPUT_STATUS_OK, files.length);
        retval[INPUT_STATUS_CANCELLED] = toAdd;
        return retval;
    }

    private void navigateCommandHistory(int direction) {
        int newIndex = this.commandHistoryIndex + direction;
        if (newIndex < 0) {
            newIndex = INPUT_STATUS_CANCELLED;
        }
        if (newIndex >= this.commandHistoryList.size()) {
            newIndex = this.commandHistoryList.size() + INPUT_STATUS_IN_PROGRESS;
        }
        setCommandHistoryIndex(newIndex);
        String newCommand = (String) this.commandHistoryList.get(newIndex);
        this.hiddenTextView.setText(newCommand);
        Selection.setSelection((Spannable) this.hiddenTextView.getText(), newCommand.length());
    }

    private void setCommandHistoryIndex(int index) {
        boolean z;
        boolean z2 = true;
        this.commandHistoryIndex = index;
        Button button = this.prevButton;
        if (index != 0) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        Button button2 = this.nextButton;
        if (index == this.commandHistoryList.size() + INPUT_STATUS_IN_PROGRESS) {
            z2 = false;
        }
        button2.setEnabled(z2);
    }

    private boolean isCommandHistoryEnabled() {
        return Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_command_history", true);
    }

    @TargetApi(19)
    private void setImmersiveMode(boolean set) {
        if (VERSION.SDK_INT >= 19) {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            if (set) {
                uiOptions |= DIALOG_INVALID_PATCHES;
            } else {
                uiOptions &= -4099;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    private void touchImmersiveMode() {
        final boolean immersive = Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_immersive_mode", false);
        if (immersive) {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.setImmersiveMode(immersive);
                }
            });
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            touchImmersiveMode();
        }
    }

    protected void initKamcord() {
    }

    protected void toggleRecording() {
    }

    protected boolean isKamcordRecording() {
        return false;
    }

    private void clearRuntimeOptionsDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                MainActivity.this.removeDialog(MainActivity.DIALOG_RUNTIME_OPTIONS);
                MainActivity.this.removeDialog(MainActivity.DIALOG_RUNTIME_OPTIONS_WITH_INSERT_TEXT);
            }
        });
    }

    private void fixMyEpicFail() {
        SharedPreferences prefs = Utils.getPrefs(INPUT_STATUS_OK);
        int lastVersion = prefs.getInt("last_bl_version", INPUT_STATUS_CANCELLED);
        int myVersion = INPUT_STATUS_CANCELLED;
        try {
            myVersion = getPackageManager().getPackageInfo(getPackageName(), INPUT_STATUS_CANCELLED).versionCode;
        } catch (NameNotFoundException e) {
        }
        if (lastVersion < 69) {
            Utils.getPrefs(INPUT_STATUS_CANCELLED).edit().putBoolean("zz_load_native_addons", true).apply();
        }
        if (lastVersion != myVersion) {
            prefs.edit().putInt("last_bl_version", myVersion).apply();
        }
    }

    private void checkForSubstrate() {
        if (Build.CPU_ABI.equals("x86")) {
            PackageInfo substrateInfo = null;
            try {
                substrateInfo = getPackageManager().getPackageInfo("com.saurik.substrate", INPUT_STATUS_CANCELLED);
            } catch (NameNotFoundException e) {
            }
            if (substrateInfo == null) {
                finish();
                startActivity(new Intent(this, GetSubstrateActivity.class));
                try {
                    Thread.sleep(100);
                    Process.killProcess(Process.myPid());
                    return;
                } catch (Throwable th) {
                    return;
                }
            }
            File substrateLibFile = getFileStreamPath("libmcpelauncher_tinysubstrate.so");
            if (!substrateLibFile.exists()) {
                try {
                    PatchUtils.copy(new File(substrateInfo.applicationInfo.nativeLibraryDir, "libsubstrate.so"), substrateLibFile);
                } catch (IOException ie) {
                    throw new RuntimeException(ie);
                }
            }
            System.load(substrateLibFile.getAbsolutePath());
        }
    }

    private void checkArch() {
        try {
            this.mcpeArch = Utils.getElfArch(new File(MC_NATIVE_LIBRARY_LOCATION));
            int myArch = Utils.getElfArch(new File(getApplicationInfo().nativeLibraryDir + "/libmcpelauncher.so"));
            if (this.mcpeArch != myArch) {
                Intent intent = new Intent(this, NoMinecraftActivity.class);
                intent.putExtra("message", getResources().getString(R.string.minecraft_wrong_arch).toString().replaceAll("ARCH", Utils.getArchName(myArch)));
                intent.putExtra("learnmore_uri", "https://github.com/zhuowei/MCPELauncher/issues/495");
                startActivity(intent);
                finish();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(INPUT_STATUS_CANCELLED);
            }
        } catch (IOException e2) {
        }
    }

    private boolean checkAddonArch(File file) {
        try {
            if (Utils.getElfArch(file) == this.mcpeArch) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private boolean isAddonCompat(String version) {
        if (version == null) {
            return false;
        }
        if (version.equals(this.mcPkgInfo.versionName)) {
            return true;
        }
        if (!this.mcPkgInfo.versionName.startsWith(HALF_SUPPORT_VERSION)) {
            return false;
        }
        if (version.startsWith("1.0.0")) {
            return true;
        }
        if (version.startsWith("1.0.2")) {
            return true;
        }
        return false;
    }

    private void initAtlasMeta() {
        if (getMCPEVersion().startsWith(HALF_SUPPORT_VERSION)) {
            initAtlasMetaNew();
        } else if (!Utils.isSafeMode()) {
            try {
                AtlasProvider terrainProvider = new AtlasProvider("resourcepacks/vanilla/client/textures/terrain_texture.json", "images/terrain-atlas/", "block.bl_modpkg.");
                AtlasProvider itemsProvider = new AtlasProvider("resourcepacks/vanilla/client/textures/item_texture.json", "images/items-opaque/", "item.bl_modpkg.");
                terrainProvider.initAtlas(this);
                itemsProvider.initAtlas(this);
                TextureListProvider textureListProvider = new TextureListProvider("resourcepacks/vanilla/client/textures.list");
                textureListProvider.init(this);
                ClientBlocksJsonProvider blocksJsonProvider = new ClientBlocksJsonProvider("resourcepacks/vanilla/client/blocks.json");
                blocksJsonProvider.init(this);
                this.textureOverrides.add(INPUT_STATUS_CANCELLED, terrainProvider);
                this.textureOverrides.add(INPUT_STATUS_OK, itemsProvider);
                this.textureOverrides.add(MAX_FAILS, textureListProvider);
                this.textureOverrides.add(DIALOG_SETTINGS, blocksJsonProvider);
                ScriptManager.terrainMeta = terrainProvider;
                ScriptManager.itemsMeta = itemsProvider;
                ScriptManager.blocksJson = blocksJsonProvider;
                ScriptManager.textureList = textureListProvider;
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e);
            }
        }
    }

    private void initAtlasMetaNew() {
        if (!Utils.isSafeMode()) {
            try {
                AtlasProvider terrainProvider = new AtlasProvider("resource_packs/vanilla/textures/terrain_texture.json", "images/terrain-atlas/", "block.bl_modpkg.");
                AtlasProvider itemsProvider = new AtlasProvider("resource_packs/vanilla/textures/item_texture.json", "images/items-opaque/", "item.bl_modpkg.");
                terrainProvider.initAtlas(this);
                itemsProvider.initAtlas(this);
                TextureListProvider textureListProvider = new TextureListProvider("resource_packs/vanilla/textures.list");
                textureListProvider.init(this);
                ClientBlocksJsonProvider blocksJsonProvider = new ClientBlocksJsonProvider("resource_packs/vanilla/blocks.json");
                blocksJsonProvider.init(this);
                this.textureOverrides.add(INPUT_STATUS_CANCELLED, terrainProvider);
                this.textureOverrides.add(INPUT_STATUS_OK, itemsProvider);
                this.textureOverrides.add(MAX_FAILS, blocksJsonProvider);
                ScriptManager.terrainMeta = terrainProvider;
                ScriptManager.itemsMeta = itemsProvider;
                ScriptManager.blocksJson = blocksJsonProvider;
                ScriptManager.textureList = textureListProvider;
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e);
            }
        }
    }

    private boolean isForcingController() {
        return VERSION.SDK_INT >= 12 && Utils.hasExtrasPackage(this) && Utils.getPrefs(INPUT_STATUS_CANCELLED).getBoolean("zz_use_controller", false);
    }

    protected boolean hasScriptSupport() {
        return true;
    }

    public String getMCPEVersion() {
        return this.mcPkgInfo.versionName;
    }

    private boolean requiresPatchingInSafeMode() {
        return getMCPEVersion().startsWith(HALF_SUPPORT_VERSION);
    }

    public void reportReimported(final String scripts) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.manage_scripts_reimported_toast) + " " + scripts, MainActivity.INPUT_STATUS_CANCELLED).show();
            }
        });
    }

    public void showStoreNotWorkingDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                new Builder(MainActivity.this).setTitle(R.string.store_not_supported_title).setMessage(R.string.store_not_supported_message).setPositiveButton(17039370, null).show();
            }
        });
    }
}
