package net.zhuoweizhang.mcpelauncher;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.PopupWindow;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.onlineid.sts.request.AbstractStsRequest;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.api.modpe.ArmorType;
import net.zhuoweizhang.mcpelauncher.api.modpe.BlockFace;
import net.zhuoweizhang.mcpelauncher.api.modpe.BlockRenderLayer;
import net.zhuoweizhang.mcpelauncher.api.modpe.CallbackName;
import net.zhuoweizhang.mcpelauncher.api.modpe.DimensionId;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import net.zhuoweizhang.mcpelauncher.api.modpe.Enchantment;
import net.zhuoweizhang.mcpelauncher.api.modpe.EntityRenderType;
import net.zhuoweizhang.mcpelauncher.api.modpe.EntityType;
import net.zhuoweizhang.mcpelauncher.api.modpe.MobEffect;
import net.zhuoweizhang.mcpelauncher.api.modpe.OldEntityTextureFilenameMapping;
import net.zhuoweizhang.mcpelauncher.api.modpe.ParticleType;
import net.zhuoweizhang.mcpelauncher.api.modpe.RendererManager;
import net.zhuoweizhang.mcpelauncher.api.modpe.RendererManager.NativeRenderer;
import net.zhuoweizhang.mcpelauncher.api.modpe.RendererManager.NativeRendererApi;
import net.zhuoweizhang.mcpelauncher.api.modpe.UseAnimation;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;
import net.zhuoweizhang.mcpelauncher.texture.AtlasProvider;
import net.zhuoweizhang.mcpelauncher.texture.ClientBlocksJsonProvider;
import net.zhuoweizhang.mcpelauncher.texture.ModPkgTexturePack;
import net.zhuoweizhang.mcpelauncher.texture.TextureListProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeJavaMethod;
import org.mozilla.javascript.NativeJavaMethod.MethodWatcher;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class ScriptManager {
    private static final int AMOUNT = 2;
    public static final int ARCH_ARM = 0;
    public static final int ARCH_I386 = 1;
    private static final int AXIS_X = 0;
    private static final int AXIS_Y = 1;
    private static final int AXIS_Z = 2;
    private static final int DAMAGE = 1;
    private static final String ENTITY_KEY_IMMOBILE = "zhuowei.bl.im";
    private static final String ENTITY_KEY_RENDERTYPE = "zhuowei.bl.rt";
    private static final String ENTITY_KEY_SKIN = "zhuowei.bl.s";
    private static final int ITEMID = 0;
    public static int ITEM_ID_COUNT = EnchantType.axe;
    public static final int MAX_NUM_ERRORS = 5;
    public static final String SCRIPTS_DIR = "modscripts";
    public static List<Long> allentities = new ArrayList();
    public static List<Long> allplayers = new ArrayList();
    public static Context androidContext = null;
    private static final String assetsResPackPath = "resource_packs/vanilla";
    public static ClientBlocksJsonProvider blocksJson;
    private static Class<?>[] constantsClasses = new Class[]{ChatColor.class, ItemCategory.class, ParticleType.class, EntityType.class, EntityRenderType.class, ArmorType.class, MobEffect.class, DimensionId.class, BlockFace.class, UseAnimation.class, Enchantment.class, EnchantType.class, BlockRenderLayer.class};
    private static String currentScreen;
    private static String currentScript = UTCTelemetry.UNKNOWNPAGE;
    public static Set<String> enabledScripts = new HashSet();
    private static NativeArray entityList;
    private static Map<Long, String> entityUUIDMap = new HashMap();
    public static File externalFilesDir = null;
    public static boolean hasLevel = false;
    private static Instrumentation instrumentation;
    private static ExecutorService instrumentationExecutor;
    public static boolean isRemote = false;
    public static AtlasProvider itemsMeta;
    private static float lastDestroyProgress = -1.0f;
    private static int lastDestroySide = -1;
    private static int lastDestroyX = ITEMID;
    private static int lastDestroyY = -1;
    private static int lastDestroyZ = ITEMID;
    public static ModPkgTexturePack modPkgTexturePack = new ModPkgTexturePack("resource_packs/vanilla/");
    private static final ModernWrapFactory modernWrapFactory = new ModernWrapFactory();
    public static float newPlayerPitch = 0.0f;
    public static float newPlayerYaw = 0.0f;
    private static boolean nextTickCallsSetLevel = false;
    private static JoinServerRequest requestJoinServer = null;
    private static boolean requestLeaveGame = false;
    public static int requestLeaveGameCounter = ITEMID;
    private static boolean requestReloadAllScripts = false;
    public static boolean requestScreenshot = false;
    private static SelectLevelRequest requestSelectLevel = null;
    public static boolean requestSelectLevelHasSetScreen = false;
    private static boolean requestedGraphicsReset = false;
    private static List<Runnable> runOnMainThreadList = new ArrayList();
    public static String screenshotFileName = BuildConfig.FLAVOR;
    private static AndroidPrintStream scriptErrorStream = null;
    private static boolean scriptingEnabled = true;
    private static boolean scriptingInitialized = false;
    public static List<ScriptState> scripts = new ArrayList();
    public static boolean sensorEnabled = false;
    private static String serverAddress = null;
    private static int serverPort = ITEMID;
    public static AtlasProvider terrainMeta;
    public static TextureListProvider textureList;
    private static final int[] useItemSideOffsets = new int[]{ITEMID, -1, ITEMID, ITEMID, DAMAGE, ITEMID, ITEMID, ITEMID, -1, ITEMID, ITEMID, DAMAGE, -1, ITEMID, ITEMID, DAMAGE, ITEMID, ITEMID};
    private static WorldData worldData = null;
    private static int worldDataSaveCounter = DAMAGE;
    public static String worldDir;
    public static String worldName;

    private static class AfterCapeDownloadAction implements Runnable {
        private long entityId;
        private String skinPath;

        public AfterCapeDownloadAction(long entityId, String skinPath) {
            this.entityId = entityId;
            this.skinPath = skinPath;
        }

        public void run() {
            try {
                File skinFile = ScriptManager.getTextureOverrideFile("images/" + this.skinPath);
                if (skinFile != null && skinFile.exists()) {
                    NativeEntityApi.setCape(Long.valueOf(this.entityId), this.skinPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class AfterSkinDownloadAction implements Runnable {
        private long entityId;
        private String skinPath;

        public AfterSkinDownloadAction(long entityId, String skinPath) {
            this.entityId = entityId;
            this.skinPath = skinPath;
        }

        public void run() {
            File skinFile = ScriptManager.getTextureOverrideFile("images/" + this.skinPath);
            if (skinFile != null && skinFile.exists()) {
                NativeEntityApi.setMobSkin(Long.valueOf(this.entityId), this.skinPath);
            }
        }
    }

    private static class BlockHostObject extends ImporterTopLevel {
        private long playerEnt;

        private BlockHostObject() {
            this.playerEnt = 0;
        }

        public String getClassName() {
            return "BlockHostObject";
        }

        @JSFunction
        public void print(String str) {
            ScriptManager.scriptPrint(str);
        }

        @JSFunction
        public double getPlayerX() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.ITEMID);
        }

        @JSFunction
        public double getPlayerY() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.DAMAGE);
        }

        @JSFunction
        public double getPlayerZ() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.AXIS_Z);
        }

        @JSFunction
        public long getPlayerEnt() {
            this.playerEnt = ScriptManager.nativeGetPlayerEnt();
            return this.playerEnt;
        }

        @JSFunction
        public NativePointer getLevel() {
            return new NativePointer(ScriptManager.nativeGetLevel());
        }

        @JSFunction
        public void setPosition(Object ent, double x, double y, double z) {
            ScriptManager.nativeSetPosition(ScriptManager.getEntityId(ent), (float) x, (float) y, (float) z);
        }

        @JSFunction
        public void setVelX(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.ITEMID);
        }

        @JSFunction
        public void setVelY(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.DAMAGE);
        }

        @JSFunction
        public void setVelZ(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.AXIS_Z);
        }

        @JSFunction
        public void explode(double x, double y, double z, double radius, boolean onfire) {
            ScriptManager.nativeExplode((float) x, (float) y, (float) z, (float) radius, onfire, true, Float.POSITIVE_INFINITY);
        }

        @JSFunction
        public void addItemInventory(int id, int amount, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeAddItemInventory(id, amount, damage);
                return;
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSFunction
        public void rideAnimal(Object rider, Object mount) {
            ScriptManager.nativeRideAnimal(ScriptManager.getEntityId(rider), ScriptManager.getEntityId(mount));
        }

        @JSFunction
        public long spawnChicken(double x, double y, double z, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = "mob/chicken.png";
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, 10, tex);
        }

        @JSFunction
        public long spawnCow(double x, double y, double z, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = "mob/cow.png";
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, 11, tex);
        }

        @JSFunction
        public int getCarriedItem() {
            return ScriptManager.nativeGetCarriedItem(ScriptManager.ITEMID);
        }

        @JSFunction
        public void preventDefault() {
            ScriptManager.nativePreventDefault();
        }

        @JSFunction
        public void setTile(int x, int y, int z, int id, int damage) {
            NativeLevelApi.setTile(x, y, z, id, damage);
        }

        @JSFunction
        public void clientMessage(String text) {
            ScriptManager.wordWrapClientMessage(text);
        }

        @JSFunction
        public void setNightMode(boolean isNight) {
            ScriptManager.nativeSetNightMode(isNight);
        }

        @JSFunction
        public int getTile(int x, int y, int z) {
            return ScriptManager.nativeGetTileWrap(x, y, z);
        }

        @JSFunction
        public void setPositionRelative(Object ent, double x, double y, double z) {
            ScriptManager.nativeSetPositionRelative(ScriptManager.getEntityId(ent), (float) x, (float) y, (float) z);
        }

        @JSFunction
        public void setRot(Object ent, double yaw, double pitch) {
            ScriptManager.nativeSetRot(ScriptManager.getEntityId(ent), (float) yaw, (float) pitch);
        }

        @JSFunction
        public double getPitch(Object entObj) {
            long ent;
            if (entObj == null || !(entObj instanceof Number)) {
                ent = getPlayerEnt();
            } else {
                ent = ((Number) entObj).longValue();
            }
            return (double) ScriptManager.nativeGetPitch(ent);
        }

        @JSFunction
        public double getYaw(Object entObj) {
            long ent;
            if (entObj == null || !(entObj instanceof Number)) {
                ent = getPlayerEnt();
            } else {
                ent = ((Number) entObj).longValue();
            }
            return (double) ScriptManager.nativeGetYaw(ent);
        }

        @JSFunction
        public long spawnPigZombie(double x, double y, double z, int item, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            long entityId = ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, 36, tex);
            if (item == 0 || !ScriptManager.nativeIsValidItem(item)) {
                item = 283;
            }
            ScriptManager.nativeSetCarriedItem(entityId, item, ScriptManager.DAMAGE, ScriptManager.ITEMID);
            return entityId;
        }

        @JSFunction
        public long bl_spawnMob(double x, double y, double z, int typeId, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, typeId, tex);
        }

        @JSFunction
        public void bl_setMobSkin(Object entityId, String tex) {
            NativeEntityApi.setMobSkin(Long.valueOf(ScriptManager.getEntityId(entityId)), tex);
        }
    }

    private static class EnchantmentInstance {
        public final int level;
        public final int type;

        public EnchantmentInstance(int type, int level) {
            this.type = type;
            this.level = level;
        }

        public String toString() {
            return "EnchantmentInstance[type=" + this.type + ",level=" + this.level + "]";
        }
    }

    private static class JoinServerRequest {
        public String serverAddress;
        public int serverPort;

        private JoinServerRequest() {
        }
    }

    private static final class MyMethodWatcher implements MethodWatcher {
        private MyMethodWatcher() {
        }

        private boolean testName(String name) {
            return name.equals("showAsDropDown") || name.equals("showAtLocation");
        }

        public boolean canCall(Method method, Object javaObject) {
            if ((javaObject instanceof AccessibleObject) && method.getName().equals("setAccessible")) {
                Class<?> cls = null;
                if (javaObject instanceof Member) {
                    cls = ((Member) javaObject).getDeclaringClass();
                }
                if (cls == ScriptManager.class || cls == NativeJavaMethod.class || cls == ContextFactory.class) {
                    return false;
                }
            }
            if (ScriptManager.scriptingEnabled) {
                return true;
            }
            boolean z = ((javaObject instanceof PopupWindow) && testName(method.getName())) ? false : ScriptManager.DAMAGE;
            return z;
        }
    }

    private static class NativeBlockApi extends ScriptableObject {
        @JSStaticFunction
        public static void defineBlock(int blockId, String name, Object textures, Object materialSourceIdSrc, Object opaqueSrc, Object renderTypeSrc) {
            defineBlockImpl(blockId, name, textures, materialSourceIdSrc, opaqueSrc, renderTypeSrc, ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static int defineLiquidBlock(int blockId, String name, Object textures, Object materialSourceIdSrc) {
            defineBlockImpl(blockId, name, textures, materialSourceIdSrc, Integer.valueOf(8), Integer.valueOf(8), ScriptManager.DAMAGE);
            defineBlockImpl(blockId + ScriptManager.DAMAGE, "Still " + name, textures, materialSourceIdSrc, Integer.valueOf(8), Integer.valueOf(8), ScriptManager.AXIS_Z);
            return blockId + ScriptManager.DAMAGE;
        }

        private static void defineBlockImpl(int blockId, String name, Object textures, Object materialSourceIdSrc, Object opaqueSrc, Object renderTypeSrc, int customBlockType) {
            if (blockId < 0 || blockId >= ScriptManager.ITEM_ID_COUNT) {
                throw new IllegalArgumentException("Block IDs must be >= 0 and < " + ScriptManager.ITEM_ID_COUNT);
            }
            int materialSourceId = 17;
            boolean opaque = true;
            int renderType = ScriptManager.ITEMID;
            if (materialSourceIdSrc != null && (materialSourceIdSrc instanceof Number)) {
                materialSourceId = ((Number) materialSourceIdSrc).intValue();
                Log.i(KamcordConstants.GAME_NAME, "setting material source to " + materialSourceId);
            }
            if (opaqueSrc != null && (opaqueSrc instanceof Boolean)) {
                opaque = ((Boolean) opaqueSrc).booleanValue();
                Log.i(KamcordConstants.GAME_NAME, "setting opaque to " + opaque);
            }
            if (renderTypeSrc != null && (renderTypeSrc instanceof Number)) {
                renderType = ((Number) renderTypeSrc).intValue();
                Log.i(KamcordConstants.GAME_NAME, "setting renderType to " + renderType);
            }
            TextureRequests finalTextures = ScriptManager.mapTextureNames(ScriptManager.expandTexturesArray(textures));
            ScriptManager.verifyBlockTextures(finalTextures);
            try {
                ScriptManager.blocksJson.setBlockTextures(name, blockId, finalTextures.names, finalTextures.coords);
                ScriptManager.nativeDefineBlock(blockId, name, finalTextures.names, finalTextures.coords, materialSourceId, opaque, renderType, customBlockType);
            } catch (JSONException je) {
                throw new RuntimeException(je);
            }
        }

        @JSStaticFunction
        public static void setDestroyTime(int blockId, double time) {
            if (ScriptManager.scriptingEnabled) {
                ScriptManager.nativeBlockSetDestroyTime(blockId, (float) time);
            }
        }

        @JSStaticFunction
        public static int getRenderType(int blockId) {
            return ScriptManager.nativeGetBlockRenderShape(blockId);
        }

        @JSStaticFunction
        public static void setRenderType(int blockId, int renderType) {
            ScriptManager.nativeSetBlockRenderShape(blockId, renderType);
        }

        @JSStaticFunction
        public static void setExplosionResistance(int blockId, double resist) {
            ScriptManager.nativeBlockSetExplosionResistance(blockId, (float) resist);
        }

        @JSStaticFunction
        public static void setShape(int blockId, double v1, double v2, double v3, double v4, double v5, double v6, int damage) {
            ScriptManager.nativeBlockSetShape(blockId, (float) v1, (float) v2, (float) v3, (float) v4, (float) v5, (float) v6, damage);
        }

        @JSStaticFunction
        public static void setLightLevel(int blockId, int lightLevel) {
            ScriptManager.nativeBlockSetLightLevel(blockId, lightLevel);
        }

        @JSStaticFunction
        public static void setColor(int blockId, Scriptable colorArray) {
            ScriptManager.nativeBlockSetColor(blockId, ScriptManager.expandColorsArray(colorArray));
        }

        @JSStaticFunction
        public static void setRenderLayer(int blockId, int layer) {
            if (layer == 3) {
                layer = 4;
            } else if (layer == BlockRenderLayer.alpha) {
                layer = 3;
            }
            if (layer == 4) {
                layer = 3;
            }
            ScriptManager.nativeBlockSetRenderLayer(blockId, layer);
        }

        @JSStaticFunction
        public static int getRenderLayer(int blockId) {
            return ScriptManager.nativeBlockGetRenderLayer(blockId);
        }

        @JSStaticFunction
        public static void setLightOpacity(int blockId, int lightLevel) {
            ScriptManager.nativeBlockSetLightOpacity(blockId, lightLevel);
        }

        @JSStaticFunction
        public static int[] getAllBlockIds() {
            int i;
            boolean[] validIds = new boolean[EnchantType.flintAndSteel];
            int theCount = ScriptManager.ITEMID;
            for (i = ScriptManager.ITEMID; i < EnchantType.flintAndSteel; i += ScriptManager.DAMAGE) {
                if (ScriptManager.nativeIsValidItem(i)) {
                    validIds[i] = true;
                    theCount += ScriptManager.DAMAGE;
                }
            }
            int[] retval = new int[theCount];
            i = ScriptManager.ITEMID;
            int b = ScriptManager.ITEMID;
            while (i < EnchantType.flintAndSteel) {
                int b2;
                if (validIds[i]) {
                    b2 = b + ScriptManager.DAMAGE;
                    retval[b] = i;
                } else {
                    b2 = b;
                }
                i += ScriptManager.DAMAGE;
                b = b2;
            }
            return retval;
        }

        @JSStaticFunction
        public static double getDestroyTime(int id, int damage) {
            return (double) ScriptManager.nativeBlockGetDestroyTime(id, damage);
        }

        @JSStaticFunction
        public static double getFriction(int id, int damage) {
            return (double) ScriptManager.nativeBlockGetFriction(id);
        }

        @JSStaticFunction
        public static void setFriction(int id, double friction) {
            ScriptManager.nativeBlockSetFriction(id, (float) friction);
        }

        @JSStaticFunction
        public static void setRedstoneConsumer(int id, boolean enabled) {
            ScriptManager.nativeBlockSetRedstoneConsumer(id, enabled);
        }

        @JSStaticFunction
        public static int[] getTextureCoords(int id, int damage, int side) {
            if (ScriptManager.nativeGetTextureCoordinatesForBlock(id, damage, side, new float[6])) {
                return new int[]{(int) (((double) (new float[6][ScriptManager.ITEMID] * new float[6][4])) + 0.5d), (int) (((double) (new float[6][ScriptManager.DAMAGE] * new float[6][ScriptManager.MAX_NUM_ERRORS])) + 0.5d), (int) (((double) (new float[6][ScriptManager.AXIS_Z] * new float[6][4])) + 0.5d), (int) (((double) (new float[6][3] * new float[6][ScriptManager.MAX_NUM_ERRORS])) + 0.5d), (int) (((double) new float[6][4]) + 0.5d), (int) (((double) new float[6][ScriptManager.MAX_NUM_ERRORS]) + 0.5d)};
            }
            throw new RuntimeException("Can't get texture for block " + id + ":" + damage);
        }

        public String getClassName() {
            return "Block";
        }
    }

    private static class NativeEntityApi extends ScriptableObject {
        @JSStaticFunction
        public static void setVelX(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static void setVelY(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static void setVelZ(Object ent, double amount) {
            ScriptManager.nativeSetVel(ScriptManager.getEntityId(ent), (float) amount, ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static void setRot(Object ent, double yaw, double pitch) {
            ScriptManager.nativeSetRot(ScriptManager.getEntityId(ent), (float) yaw, (float) pitch);
        }

        @JSStaticFunction
        public static void rideAnimal(Object rider, Object mount) {
            ScriptManager.nativeRideAnimal(ScriptManager.getEntityId(rider), ScriptManager.getEntityId(mount));
        }

        @JSStaticFunction
        public static void setPosition(Object ent, double x, double y, double z) {
            ScriptManager.nativeSetPosition(ScriptManager.getEntityId(ent), (float) x, (float) y, (float) z);
        }

        @JSStaticFunction
        public static void setPositionRelative(Object ent, double x, double y, double z) {
            ScriptManager.nativeSetPositionRelative(ScriptManager.getEntityId(ent), (float) x, (float) y, (float) z);
        }

        @JSStaticFunction
        public static double getPitch(Object ent) {
            return (double) ScriptManager.nativeGetPitch(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static double getYaw(Object ent) {
            return (double) ScriptManager.nativeGetYaw(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static void setFireTicks(Object ent, int howLong) {
            ScriptManager.nativeSetOnFire(ScriptManager.getEntityId(ent), howLong);
        }

        @JSStaticFunction
        public static double getX(Object ent) {
            return (double) ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static double getY(Object ent) {
            return (double) ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static double getZ(Object ent) {
            return (double) ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static void setCarriedItem(Object ent, int id, int count, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeSetCarriedItem(ScriptManager.getEntityId(ent), id, count, damage);
                return;
            }
            throw new RuntimeException("The item ID " + id + " is invalid.");
        }

        @JSStaticFunction
        public static int getEntityTypeId(Object ent) {
            return ScriptManager.nativeGetEntityTypeId(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static long spawnMob(double x, double y, double z, int typeId, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, typeId, tex);
        }

        @JSStaticFunction
        public static void setAnimalAge(Object animal, int age) {
            int type = getEntityTypeId(animal);
            ScriptManager.nativeSetAnimalAge(ScriptManager.getEntityId(animal), age);
        }

        @JSStaticFunction
        public static int getAnimalAge(Object animal) {
            int type = getEntityTypeId(animal);
            return ScriptManager.nativeGetAnimalAge(ScriptManager.getEntityId(animal));
        }

        @JSStaticFunction
        public static void setMobSkin(Object entity, String tex) {
            setMobSkinImpl(entity, tex, true);
        }

        public static void setMobSkinImpl(Object entity, String text, boolean persist) {
            String newSkinPath = (String) OldEntityTextureFilenameMapping.m.get(text);
            if (newSkinPath != null) {
                text = newSkinPath;
            }
            if (text.endsWith(DownloadProfileImageTask.UserTileExtension) || text.endsWith(".tga")) {
                text = text.substring(ScriptManager.ITEMID, text.length() - 4);
            }
            ScriptManager.nativeSetMobSkin(ScriptManager.getEntityId(entity), text);
            if (persist) {
                setExtraData(entity, ScriptManager.ENTITY_KEY_SKIN, text);
            }
        }

        @JSStaticFunction
        public static void remove(Object ent) {
            ScriptManager.nativeRemoveEntity(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static int getHealth(Object ent) {
            int entityType = getEntityTypeId(ent);
            if (entityType < 10 || entityType >= 64) {
                return ScriptManager.ITEMID;
            }
            return ScriptManager.nativeGetMobHealth(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static void setHealth(Object ent, int halfhearts) {
            int entityType = getEntityTypeId(ent);
            if (entityType >= 10 && entityType < 64) {
                ScriptManager.nativeSetMobHealth(ScriptManager.getEntityId(ent), halfhearts);
            }
        }

        @JSStaticFunction
        public static void setMaxHealth(Object ent, int halfhearts) {
            int entityType = getEntityTypeId(ent);
            if (entityType < 10 || entityType >= 64) {
                throw new RuntimeException("setMaxHealth called on non-mob: entityType=" + entityType);
            }
            ScriptManager.nativeSetMobMaxHealth(ScriptManager.getEntityId(ent), halfhearts);
        }

        @JSStaticFunction
        public static void setRenderType(Object ent, Object renderType) {
            NativeRenderer theRenderer;
            if (renderType instanceof NativeJavaObject) {
                NativeRenderer renderType2 = ((NativeJavaObject) renderType).unwrap();
            }
            boolean alreadySet = false;
            if (renderType2 instanceof Number) {
                int rendererId = ((Number) renderType2).intValue();
                setRenderTypeImpl(ent, rendererId);
                alreadySet = true;
                theRenderer = NativeRendererApi.getById(rendererId);
                if (theRenderer == null) {
                    return;
                }
            } else if (renderType2 instanceof NativeRenderer) {
                theRenderer = renderType2;
            } else {
                theRenderer = NativeRendererApi.getByName(renderType2.toString());
            }
            if (!alreadySet) {
                setRenderTypeImpl(ent, theRenderer.getRenderType());
            }
            setExtraData(ent, ScriptManager.ENTITY_KEY_RENDERTYPE, theRenderer.getName());
        }

        public static void setRenderTypeImpl(Object ent, int renderType) {
            if (renderType < EnchantType.fishingRod && !EntityRenderType.isValidRenderType(renderType)) {
                throw new RuntimeException("Render type " + renderType + " does not exist");
            } else if (renderType == 12 && getEntityTypeId(ent) != 15) {
                throw new RuntimeException("Villager render type can only be used on villagers");
            } else if (!ScriptManager.nativeSetEntityRenderType(ScriptManager.getEntityId(ent), renderType)) {
                throw new RuntimeException("Custom render type " + renderType + " does not exist");
            }
        }

        @JSStaticFunction
        public static void setSneaking(Object ent, boolean doIt) {
            ScriptManager.nativeSetSneaking(ScriptManager.getEntityId(ent), doIt);
        }

        @JSStaticFunction
        public static boolean isSneaking(Object ent) {
            return ScriptManager.nativeIsSneaking(ScriptManager.getEntityId(ent));
        }

        @JSStaticFunction
        public static double getVelX(Object ent) {
            return (double) ScriptManager.nativeGetEntityVel(ScriptManager.getEntityId(ent), ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static double getVelY(Object ent) {
            return (double) ScriptManager.nativeGetEntityVel(ScriptManager.getEntityId(ent), ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static double getVelZ(Object ent) {
            return (double) ScriptManager.nativeGetEntityVel(ScriptManager.getEntityId(ent), ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static void setNameTag(Object entity, String name) {
            if (ScriptManager.nativeGetEntityTypeId(ScriptManager.getEntityId(entity)) >= 64) {
                throw new IllegalArgumentException("setNameTag only works on mobs");
            }
            ScriptManager.nativeEntitySetNameTag(ScriptManager.getEntityId(entity), name);
        }

        @JSStaticFunction
        public static long[] getAll() {
            long[] entities = new long[ScriptManager.allentities.size()];
            for (int n = ScriptManager.ITEMID; entities.length > n; n += ScriptManager.DAMAGE) {
                entities[n] = ((Long) ScriptManager.allentities.get(n)).longValue();
            }
            return entities;
        }

        @JSStaticFunction
        public static String getNameTag(Object entity) {
            return ScriptManager.nativeEntityGetNameTag(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static int getRiding(Object entity) {
            return ScriptManager.nativeEntityGetRiding(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static int getRider(Object entity) {
            return ScriptManager.nativeEntityGetRider(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static String getMobSkin(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            int entityType = getEntityTypeId(Long.valueOf(entityId));
            if (entityType <= 0 || entityType >= 64) {
                return BuildConfig.FLAVOR;
            }
            return ScriptManager.nativeEntityGetMobSkin(entityId);
        }

        @JSStaticFunction
        public static int getRenderType(Object entity) {
            return ScriptManager.nativeEntityGetRenderType(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static String getUniqueId(Object entity) {
            return ScriptManager.getEntityUUID(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static void setCollisionSize(Object entity, double a, double b) {
            ScriptManager.nativeEntitySetSize(ScriptManager.getEntityId(entity), (float) a, (float) b);
        }

        @JSStaticFunction
        public static void setCape(Object entity, String location) {
            int typeId = ScriptManager.nativeGetEntityTypeId(ScriptManager.getEntityId(entity));
            if (typeId < 32 || typeId >= 64) {
                throw new RuntimeException("Set cape only works for humanoid mobs");
            }
            ScriptManager.nativeSetCape(ScriptManager.getEntityId(entity), location);
        }

        @JSStaticFunction
        public static void addEffect(Object entity, int potionId, int duration, int amplifier, boolean isAmbient, boolean showParticles) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("addEffect only works for mobs");
            } else if (MobEffect.effectIds.get(Integer.valueOf(potionId)) == null) {
                throw new RuntimeException("Invalid MobEffect id: " + potionId);
            } else {
                ScriptManager.nativeMobAddEffect(entityId, potionId, duration, amplifier, isAmbient, showParticles);
            }
        }

        @JSStaticFunction
        public static void removeEffect(Object entity, int potionId) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("removeEffect only works for mobs");
            } else if (MobEffect.effectIds.get(Integer.valueOf(potionId)) == null) {
                throw new RuntimeException("Invalid MobEffect id: " + potionId);
            } else {
                ScriptManager.nativeMobRemoveEffect(entityId, potionId);
            }
        }

        @JSStaticFunction
        public static void removeAllEffects(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("removeAllEffects only works for mobs");
            }
            ScriptManager.nativeMobRemoveAllEffects(entityId);
        }

        @JSStaticFunction
        public static int getItemEntityId(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId == 64) {
                return ScriptManager.nativeGetItemEntityItem(entityId, ScriptManager.ITEMID);
            }
            throw new RuntimeException("getItemEntity only works on item entities: got " + typeId);
        }

        @JSStaticFunction
        public static int getItemEntityData(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            if (ScriptManager.nativeGetEntityTypeId(entityId) == 64) {
                return ScriptManager.nativeGetItemEntityItem(entityId, ScriptManager.DAMAGE);
            }
            throw new RuntimeException("getItemEntity only works on item entities");
        }

        @JSStaticFunction
        public static int getItemEntityCount(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            if (ScriptManager.nativeGetEntityTypeId(entityId) == 64) {
                return ScriptManager.nativeGetItemEntityItem(entityId, ScriptManager.AXIS_Z);
            }
            throw new RuntimeException("getItemEntity only works on item entities");
        }

        @JSStaticFunction
        public static int getArmor(Object entity, int slot) {
            if (slot < 0 || slot >= 4) {
                throw new RuntimeException("slot " + slot + " is not a valid armor slot");
            }
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId > 0 && typeId < 64) {
                return ScriptManager.nativeMobGetArmor(entityId, slot, ScriptManager.ITEMID);
            }
            throw new RuntimeException("getArmor only works for mobs");
        }

        @JSStaticFunction
        public static int getArmorDamage(Object entity, int slot) {
            if (slot < 0 || slot >= 4) {
                throw new RuntimeException("slot " + slot + " is not a valid armor slot");
            }
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId > 0 && typeId < 64) {
                return ScriptManager.nativeMobGetArmor(entityId, slot, ScriptManager.DAMAGE);
            }
            throw new RuntimeException("getArmorDamage only works for mobs");
        }

        @JSStaticFunction
        public static void setArmor(Object entity, int slot, int id, int damage) {
            if (slot < 0 || slot >= 4) {
                throw new RuntimeException("slot " + slot + " is not a valid armor slot");
            }
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("setArmor only works for mobs");
            }
            ScriptManager.nativeMobSetArmor(entityId, slot, id, damage);
        }

        @JSStaticFunction
        public static String getArmorCustomName(Object entity, int slot) {
            if (slot < 0 || slot >= 4) {
                throw new RuntimeException("slot " + slot + " is not a valid armor slot");
            }
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId > 0 && typeId < 64) {
                return ScriptManager.nativeMobGetArmorCustomName(entityId, slot);
            }
            throw new RuntimeException("setArmor only works for mobs");
        }

        @JSStaticFunction
        public static void setArmorCustomName(Object entity, int slot, String name) {
            if (slot < 0 || slot >= 4) {
                throw new RuntimeException("slot " + slot + " is not a valid armor slot");
            }
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("setArmor only works for mobs");
            }
            ScriptManager.nativeMobSetArmorCustomName(entityId, slot, name);
        }

        @JSStaticFunction
        public static int getMaxHealth(Object entity) {
            return ScriptManager.nativeGetMobMaxHealth(ScriptManager.getEntityId(entity));
        }

        @JSStaticFunction
        public static String getExtraData(Object entity, String key) {
            if (ScriptManager.worldData == null) {
                return null;
            }
            return ScriptManager.worldData.getEntityData(ScriptManager.getEntityId(entity), key);
        }

        @JSStaticFunction
        public static boolean setExtraData(Object entity, String key, String value) {
            if (ScriptManager.worldData == null) {
                return false;
            }
            ScriptManager.worldData.setEntityData(ScriptManager.getEntityId(entity), key, value);
            return true;
        }

        @JSStaticFunction
        public static void setImmobile(Object entity, boolean immobile) {
            setImmobileImpl(entity, immobile);
            setExtraData(entity, ScriptManager.ENTITY_KEY_IMMOBILE, immobile ? XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION : MigrationManager.InitialSdkVersion);
        }

        public static void setImmobileImpl(Object entity, boolean immobile) {
            ScriptManager.nativeEntitySetImmobile(ScriptManager.getEntityId(entity), immobile);
        }

        @JSStaticFunction
        public static long getTarget(Object entity) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId > 0 && typeId < 64) {
                return ScriptManager.nativeEntityGetTarget(entityId);
            }
            throw new RuntimeException("getTarget only works on mobs");
        }

        @JSStaticFunction
        public static void setTarget(Object entity, Object target) {
            long entityId = ScriptManager.getEntityId(entity);
            int typeId = ScriptManager.nativeGetEntityTypeId(entityId);
            if (typeId <= 0 || typeId >= 64) {
                throw new RuntimeException("setTarget only works on mob entities");
            }
            long targetId = target == null ? -1 : ScriptManager.getEntityId(target);
            if (targetId != -1) {
                int targetTypeId = ScriptManager.nativeGetEntityTypeId(targetId);
                if (targetTypeId <= 0 || targetTypeId >= 64) {
                    throw new RuntimeException("setTarget only works on mob targets");
                }
            }
            ScriptManager.nativeEntitySetTarget(entityId, targetId);
        }

        public String getClassName() {
            return "Entity";
        }
    }

    private static class NativeGuiApi extends ScriptableObject {
        @JSStaticFunction
        public static int getScreenWidth() {
            return ScriptManager.ITEMID;
        }

        @JSStaticFunction
        public static int getScreenHeight() {
            return ScriptManager.ITEMID;
        }

        public String getClassName() {
            return "Gui";
        }
    }

    private static class NativeItemApi extends ScriptableObject {
        private static List<int[]> activeFurnaceRecipes = new ArrayList();
        private static List<Object[]> activeRecipes = new ArrayList();
        private static Map<Integer, Integer> itemIdToRendererId = new HashMap();
        private static Map<Integer, Integer> rendererToItemId = new HashMap();

        @JSStaticFunction
        public static String getName(int id, int damage, boolean raw) {
            if (ScriptManager.nativeIsValidItem(id)) {
                return ScriptManager.nativeGetItemName(id, damage, raw);
            }
            throw new RuntimeException("getName called with invalid item ID: " + id);
        }

        @JSStaticFunction
        public static int internalNameToId(String name) {
            return nameToIdImpl(name, true);
        }

        @JSStaticFunction
        public static int translatedNameToId(String name) {
            return nameToIdImpl(name, false);
        }

        private static int nameToIdImpl(String name, boolean internal) {
            if (name == null) {
                return -1;
            }
            int i;
            name = name.replace(" ", "_").toLowerCase();
            for (i = EnchantType.flintAndSteel; i < EnchantType.fishingRod; i += ScriptManager.DAMAGE) {
                if (idHasName(name, i, internal)) {
                    return i;
                }
            }
            for (i = ScriptManager.DAMAGE; i < EnchantType.flintAndSteel; i += ScriptManager.DAMAGE) {
                if (idHasName(name, i, internal)) {
                    return i;
                }
            }
            try {
                return Integer.parseInt(name);
            } catch (Exception e) {
                return -1;
            }
        }

        private static boolean idHasName(String targetname, int id, boolean internal) {
            int startSub = ScriptManager.ITEMID;
            String name = ScriptManager.nativeGetItemName(id, ScriptManager.ITEMID, internal);
            if (name == null) {
                return false;
            }
            if (internal) {
                int endSub = name.endsWith(".name") ? name.length() - 5 : name.length();
                if (name.startsWith("tile.") || name.startsWith("item.")) {
                    startSub = ScriptManager.MAX_NUM_ERRORS;
                }
                name = name.substring(startSub, endSub);
            }
            return targetname.equals(name.replace(" ", "_").toLowerCase());
        }

        @JSStaticFunction
        public static void addCraftRecipe(int id, int count, int damage, Scriptable ingredientsScriptable) {
            int[] expanded = ScriptManager.expandShapelessRecipe(ingredientsScriptable);
            StringBuilder temprow = new StringBuilder();
            char nextchar = 'a';
            int[] ingredients = new int[expanded.length];
            int i = ScriptManager.ITEMID;
            while (i < expanded.length) {
                int inputid = expanded[i];
                int inputcount = expanded[i + ScriptManager.DAMAGE];
                int inputdamage = expanded[i + ScriptManager.AXIS_Z];
                char nextchar2 = (char) (nextchar + ScriptManager.DAMAGE);
                char mychar = nextchar;
                for (int a = ScriptManager.ITEMID; a < inputcount; a += ScriptManager.DAMAGE) {
                    temprow.append(mychar);
                }
                ingredients[i] = mychar;
                ingredients[i + ScriptManager.DAMAGE] = inputid;
                ingredients[i + ScriptManager.AXIS_Z] = inputdamage;
                i += 3;
                nextchar = nextchar2;
            }
            int temprowLength = temprow.length();
            if (temprowLength > 9) {
                ScriptManager.scriptPrint("Too many ingredients in shapeless recipe: max of 9 slots, the extra items have been ignored");
                temprow.delete(9, temprow.length());
                temprowLength = temprow.length();
            }
            int width = temprowLength <= 4 ? ScriptManager.AXIS_Z : 3;
            String[] shape = new String[((temprowLength % width != 0 ? ScriptManager.DAMAGE : ScriptManager.ITEMID) + (temprowLength / width))];
            for (i = ScriptManager.ITEMID; i < shape.length; i += ScriptManager.DAMAGE) {
                int begin = i * width;
                int end = begin + width;
                if (end > temprowLength) {
                    end = temprowLength;
                }
                shape[i] = temprow.substring(begin, end);
            }
            verifyAndAddShapedRecipe(id, count, damage, shape, ingredients);
        }

        @JSStaticFunction
        public static void addFurnaceRecipe(int inputId, int outputId, int outputDamage) {
            if (!ScriptManager.nativeIsValidItem(inputId)) {
                throw new RuntimeException("Invalid input in furnace recipe: " + inputId + " is not a valid item. " + "You must create the item before you can add it to a recipe.");
            } else if (ScriptManager.nativeIsValidItem(outputId)) {
                for (int[] recipe : activeFurnaceRecipes) {
                    if (recipe[ScriptManager.ITEMID] == inputId && recipe[ScriptManager.DAMAGE] == outputId && recipe[ScriptManager.AXIS_Z] == outputDamage) {
                        System.out.println("Furnace recipe already exists.");
                        return;
                    }
                }
                activeFurnaceRecipes.add(new int[]{inputId, outputId, outputDamage});
                ScriptManager.nativeAddFurnaceRecipe(inputId, outputId, outputDamage);
            } else {
                throw new RuntimeException("Invalid output in furnace recipe: " + outputId + " is not a valid item. " + "You must create the item before you can add it to a recipe.");
            }
        }

        @JSStaticFunction
        public static void addShapedRecipe(int id, int count, int damage, Scriptable shape, Scriptable ingredients) {
            int i;
            int shapeArrayLength = ((Number) ScriptableObject.getProperty(shape, Name.LENGTH)).intValue();
            String[] shapeArray = new String[shapeArrayLength];
            for (i = ScriptManager.ITEMID; i < shapeArrayLength; i += ScriptManager.DAMAGE) {
                shapeArray[i] = ScriptableObject.getProperty(shape, i).toString();
            }
            int ingredientsArrayLength = ((Number) ScriptableObject.getProperty(ingredients, Name.LENGTH)).intValue();
            if (ingredientsArrayLength % 3 != 0) {
                throw new RuntimeException("Ingredients array must be [\"?\", id, damage, ...]");
            }
            int[] ingredientsArray = new int[ingredientsArrayLength];
            for (i = ScriptManager.ITEMID; i < ingredientsArrayLength; i += ScriptManager.DAMAGE) {
                Object str = ScriptableObject.getProperty(ingredients, i);
                if (i % 3 == 0) {
                    ingredientsArray[i] = str.toString().charAt(ScriptManager.ITEMID);
                } else {
                    ingredientsArray[i] = ((Number) str).intValue();
                }
            }
            verifyAndAddShapedRecipe(id, count, damage, shapeArray, ingredientsArray);
        }

        private static void verifyAndAddShapedRecipe(int id, int count, int damage, String[] shape, int[] ingredients) {
            if (id < 0 || id >= ScriptManager.ITEM_ID_COUNT) {
                throw new RuntimeException("Invalid result in recipe: " + id + ": must be between 0 and " + ScriptManager.ITEM_ID_COUNT);
            } else if (ScriptManager.nativeIsValidItem(id)) {
                int i = ScriptManager.ITEMID;
                while (i < ingredients.length) {
                    if (ScriptManager.nativeIsValidItem(ingredients[i + ScriptManager.DAMAGE])) {
                        i += 3;
                    } else {
                        throw new RuntimeException("Invalid input in recipe: " + id + " is not a valid item. " + "You must create the item before you can add it to a recipe.");
                    }
                }
                for (Object[] r : activeRecipes) {
                    if (((Integer) r[ScriptManager.ITEMID]).intValue() == id && ((Integer) r[ScriptManager.DAMAGE]).intValue() == count && ((Integer) r[ScriptManager.AXIS_Z]).intValue() == damage && Arrays.equals((String[]) r[3], shape) && Arrays.equals((int[]) r[4], ingredients)) {
                        System.out.println("Recipe already exists.");
                        return;
                    }
                }
                List list = activeRecipes;
                Object obj = new Object[ScriptManager.MAX_NUM_ERRORS];
                obj[ScriptManager.ITEMID] = Integer.valueOf(id);
                obj[ScriptManager.DAMAGE] = Integer.valueOf(count);
                obj[ScriptManager.AXIS_Z] = Integer.valueOf(damage);
                obj[3] = shape;
                obj[4] = ingredients;
                list.add(obj);
                ScriptManager.nativeAddShapedRecipe(id, count, damage, shape, ingredients);
            } else {
                throw new RuntimeException("Invalid result in recipe: " + id + " is not a valid item. " + "You must create the item before you can add it to a recipe.");
            }
        }

        public static void reregisterRecipes() {
            for (Object[] r : activeRecipes) {
                ScriptManager.nativeAddShapedRecipe(((Integer) r[ScriptManager.ITEMID]).intValue(), ((Integer) r[ScriptManager.DAMAGE]).intValue(), ((Integer) r[ScriptManager.AXIS_Z]).intValue(), (String[]) r[3], (int[]) r[4]);
            }
            for (int[] r2 : activeFurnaceRecipes) {
                ScriptManager.nativeAddFurnaceRecipe(r2[ScriptManager.ITEMID], r2[ScriptManager.DAMAGE], r2[ScriptManager.AXIS_Z]);
            }
        }

        @JSStaticFunction
        public static void setMaxDamage(int id, int maxDamage) {
            ScriptManager.nativeSetItemMaxDamage(id, maxDamage);
        }

        @JSStaticFunction
        public static int getMaxDamage(int id) {
            return ScriptManager.nativeGetItemMaxDamage(id);
        }

        @JSStaticFunction
        public static void setCategory(int id, int category) {
            if (category < 0 || category > 4) {
                throw new RuntimeException("Invalid category " + category + ": should be one of ItemCategory.MATERIAL, ItemCategory.DECORATION, " + "ItemCategory.TOOL, or ItemCategory.FOOD");
            }
            ScriptManager.nativeSetItemCategory(id, category, ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static void setHandEquipped(int id, boolean yep) {
            ScriptManager.nativeSetHandEquipped(id, yep);
        }

        @JSStaticFunction
        public static void defineArmor(int id, String iconName, int iconIndex, String name, String texture, int damageReduceAmount, int maxDamage, int armorType) {
            if (armorType < 0 || armorType > 3) {
                throw new RuntimeException("Invalid armor type: use ArmorType.helmet, ArmorType.chestplate,ArmorType.leggings, or ArmorType.boots");
            } else if (id < 0 || id >= ScriptManager.ITEM_ID_COUNT) {
                throw new IllegalArgumentException("Item IDs must be >= 0 and < " + ScriptManager.ITEM_ID_COUNT);
            } else if (ScriptManager.itemsMeta == null || ScriptManager.itemsMeta.hasIcon(iconName, iconIndex)) {
                String newSkinPath = (String) OldEntityTextureFilenameMapping.m.get(texture);
                if (newSkinPath != null) {
                    texture = newSkinPath;
                }
                ScriptManager.nativeDefineArmor(id, iconName, iconIndex, name, texture, damageReduceAmount, maxDamage, armorType);
            } else {
                throw new MissingTextureException("The item icon " + iconName + ":" + iconIndex + " does not exist");
            }
        }

        @JSStaticFunction
        public static boolean isValidItem(int id) {
            return ScriptManager.nativeIsValidItem(id);
        }

        @JSStaticFunction
        public static void setProperties(int id, Object props) {
            if (isValidItem(id)) {
                String theJson;
                if ((props instanceof CharSequence) || ScriptRuntime.typeof(props).equals("string")) {
                    theJson = props.toString();
                } else if (props instanceof Scriptable) {
                    Scriptable s = (Scriptable) props;
                    theJson = NativeJSON.stringify(org.mozilla.javascript.Context.getCurrentContext(), s.getParentScope(), s, null, BuildConfig.FLAVOR).toString();
                } else {
                    throw new RuntimeException("Invalid input to setProperties: " + props + " cannot be converted to JSON");
                }
                if (!ScriptManager.nativeItemSetProperties(id, theJson)) {
                    throw new RuntimeException("Failed to set properties for item " + id);
                }
                return;
            }
            throw new RuntimeException(id + " is not a valid item");
        }

        @JSStaticFunction
        public static int getUseAnimation(int id) {
            return ScriptManager.nativeItemGetUseAnimation(id);
        }

        @JSStaticFunction
        public static void setUseAnimation(int id, int animation) {
            ScriptManager.nativeItemSetUseAnimation(id, animation);
        }

        @JSStaticFunction
        public static void setStackedByData(int id, boolean stacked) {
            ScriptManager.nativeItemSetStackedByData(id, stacked);
        }

        @JSStaticFunction
        public static void setEnchantType(int id, int flag, int value) {
            ScriptManager.nativeSetAllowEnchantments(id, flag, value);
        }

        @JSStaticFunction
        public static int[] getTextureCoords(int id, int damage) {
            if (ScriptManager.nativeGetTextureCoordinatesForItem(id, damage, new float[6])) {
                return new int[]{(int) (((double) (new float[6][ScriptManager.ITEMID] * new float[6][4])) + 0.5d), (int) (((double) (new float[6][ScriptManager.DAMAGE] * new float[6][ScriptManager.MAX_NUM_ERRORS])) + 0.5d), (int) (((double) (new float[6][ScriptManager.AXIS_Z] * new float[6][4])) + 0.5d), (int) (((double) (new float[6][3] * new float[6][ScriptManager.MAX_NUM_ERRORS])) + 0.5d), (int) (((double) new float[6][4]) + 0.5d), (int) (((double) new float[6][ScriptManager.MAX_NUM_ERRORS]) + 0.5d)};
            }
            throw new RuntimeException("Can't get texture for item " + id + ":" + damage);
        }

        @JSStaticFunction
        public static int getMaxStackSize(int id) {
            return ScriptManager.nativeItemGetMaxStackSize(id);
        }

        @JSStaticFunction
        public static void defineThrowable(int id, String iconName, int iconSubindex, String name, int maxStackSize) {
            if (id < 0 || id >= ScriptManager.ITEM_ID_COUNT) {
                throw new IllegalArgumentException("Item IDs must be >= 0 and < ITEM_ID_COUNT");
            } else if (ScriptManager.itemsMeta == null || ScriptManager.itemsMeta.hasIcon(iconName, iconSubindex)) {
                ScriptManager.nativeDefineSnowballItem(id, iconName, iconSubindex, name, maxStackSize);
                int renderer = RendererManager.nativeCreateItemSpriteRenderer(id);
                itemIdToRendererId.put(Integer.valueOf(renderer), Integer.valueOf(id));
                rendererToItemId.put(Integer.valueOf(id), Integer.valueOf(renderer));
            } else {
                throw new MissingTextureException("The item icon " + iconName + ":" + iconSubindex + " does not exist");
            }
        }

        @JSStaticFunction
        public static int getCustomThrowableRenderType(int itemId) {
            Integer i = (Integer) rendererToItemId.get(Integer.valueOf(itemId));
            if (i != null) {
                return i.intValue();
            }
            throw new RuntimeException("Not a custom throwable item ID: " + itemId);
        }

        public String getClassName() {
            return "Item";
        }
    }

    private static class NativeLevelApi extends ScriptableObject {
        @JSStaticFunction
        public static void setNightMode(boolean isNight) {
            ScriptManager.nativeSetNightMode(isNight);
        }

        @JSStaticFunction
        public static int getTile(int x, int y, int z) {
            return ScriptManager.nativeGetTileWrap(x, y, z);
        }

        @JSStaticFunction
        public static void explode(double x, double y, double z, double radius, boolean onfire, boolean smoke, double somethingelse) {
            ScriptManager.nativeExplode((float) x, (float) y, (float) z, (float) radius, onfire, smoke, (float) somethingelse);
        }

        @JSStaticFunction
        public static void setTile(int x, int y, int z, int id, int damage) {
            if (id >= EnchantType.flintAndSteel) {
                ScriptManager.nativeSetTile(x, y, z, ScriptManager.ITEMID, ScriptManager.ITEMID);
                ScriptManager.nativeSetTile(x, y, z, 245, damage);
                ScriptManager.nativeLevelSetExtraData(x, y, z, id);
                return;
            }
            ScriptManager.nativeSetTile(x, y, z, id, damage);
        }

        @JSStaticFunction
        public static NativePointer getAddress() {
            return new NativePointer(ScriptManager.nativeGetLevel());
        }

        @JSStaticFunction
        public static long spawnChicken(double x, double y, double z, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, 10, tex);
        }

        @JSStaticFunction
        public static long spawnCow(double x, double y, double z, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, 11, tex);
        }

        @JSStaticFunction
        public static long spawnMob(double x, double y, double z, int typeId, String tex) {
            if (ScriptManager.invalidTexName(tex)) {
                tex = null;
            }
            return ScriptManager.spawnEntityImpl((float) x, (float) y, (float) z, typeId, tex);
        }

        @JSStaticFunction
        public static String getSignText(int x, int y, int z, int line) {
            if (line >= 0 && line < 4) {
                return ScriptManager.nativeGetSignText(x, y, z, line);
            }
            throw new RuntimeException("Invalid line for sign: must be in the range of 0 to 3");
        }

        @JSStaticFunction
        public static void setSignText(int x, int y, int z, int line, String newText) {
            if (line < 0 || line >= 4) {
                throw new RuntimeException("Invalid line for sign: must be in the range of 0 to 3");
            }
            ScriptManager.nativeSetSignText(x, y, z, line, newText);
        }

        @JSStaticFunction
        public static int getData(int x, int y, int z) {
            return ScriptManager.nativeGetData(x, y, z);
        }

        @JSStaticFunction
        public static String getWorldName() {
            return ScriptManager.worldName;
        }

        @JSStaticFunction
        public static String getWorldDir() {
            return ScriptManager.worldDir;
        }

        @JSStaticFunction
        public static long dropItem(double x, double y, double z, double range, int id, int count, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                return ScriptManager.nativeDropItem((float) x, (float) y, (float) z, (float) range, id, count, damage);
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSStaticFunction
        public static void setGameMode(int type) {
            if (ScriptManager.scriptingEnabled) {
                ScriptManager.nativeSetGameType(type);
            }
        }

        @JSStaticFunction
        public static int getGameMode() {
            return ScriptManager.nativeGetGameType();
        }

        @JSStaticFunction
        public static int getTime() {
            return (int) ScriptManager.nativeGetTime();
        }

        @JSStaticFunction
        public static void setTime(int time) {
            ScriptManager.nativeSetTime((long) time);
        }

        @JSStaticFunction
        public static void setSpawn(int x, int y, int z) {
            ScriptManager.nativeSetSpawn(x, y, z);
        }

        @JSStaticFunction
        public static void destroyBlock(int x, int y, int z, boolean shouldDrop) {
            if (ScriptManager.scriptingEnabled) {
                int itmId = getTile(x, y, z);
                int itmDmg = getData(x, y, z);
                ScriptManager.nativeDestroyBlock(x, y, z);
                if (shouldDrop) {
                    dropItem(((double) x) + 0.5d, (double) y, ((double) z) + 0.5d, 1.0d, itmId, ScriptManager.DAMAGE, itmDmg);
                }
            }
        }

        @JSStaticFunction
        public static void setChestSlot(int x, int y, int z, int slot, int id, int damage, int amount) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeAddItemChest(x, y, z, slot, id, damage, amount);
                return;
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSStaticFunction
        public static void setChestSlotCustomName(int x, int y, int z, int slot, String name) {
            ScriptManager.nativeSetItemNameChest(x, y, z, slot, name);
        }

        @JSStaticFunction
        public static int getChestSlot(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemChest(x, y, z, slot);
        }

        @JSStaticFunction
        public static int getChestSlotData(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemDataChest(x, y, z, slot);
        }

        @JSStaticFunction
        public static int getChestSlotCount(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemCountChest(x, y, z, slot);
        }

        @JSStaticFunction
        public static String getChestSlotCustomName(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemNameChest(x, y, z, slot);
        }

        @JSStaticFunction
        public static void playSound(double x, double y, double z, String sound, double volume, double pitch) {
            float f = (float) x;
            float f2 = (float) y;
            float f3 = (float) z;
            float f4 = (volume <= 0.0d || volume != volume) ? 1.0f : (float) volume;
            float f5 = (pitch <= 0.0d || pitch != pitch) ? 1.0f : (float) pitch;
            ScriptManager.nativePlaySound(f, f2, f3, sound, f4, f5);
        }

        @JSStaticFunction
        public static void playSoundEnt(Object ent, String sound, double volume, double pitch) {
            float f = 1.0f;
            float x = ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.ITEMID);
            float y = ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.DAMAGE);
            float z = ScriptManager.nativeGetEntityLoc(ScriptManager.getEntityId(ent), ScriptManager.AXIS_Z);
            float f2 = (volume <= 0.0d || volume != volume) ? 1.0f : (float) volume;
            if (pitch > 0.0d && pitch == pitch) {
                f = (float) pitch;
            }
            ScriptManager.nativePlaySound(x, y, z, sound, f2, f);
        }

        @JSStaticFunction
        public static int getBrightness(int x, int y, int z) {
            return ScriptManager.nativeGetBrightness(x, y, z);
        }

        @JSStaticFunction
        public static void setFurnaceSlot(int x, int y, int z, int slot, int id, int damage, int amount) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeAddItemFurnace(x, y, z, slot, id, damage, amount);
                return;
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSStaticFunction
        public static int getFurnaceSlot(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemFurnace(x, y, z, slot);
        }

        @JSStaticFunction
        public static int getFurnaceSlotData(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemDataFurnace(x, y, z, slot);
        }

        @JSStaticFunction
        public static int getFurnaceSlotCount(int x, int y, int z, int slot) {
            return ScriptManager.nativeGetItemCountFurnace(x, y, z, slot);
        }

        @JSStaticFunction
        public static void addParticle(Object typeRaw, double x, double y, double z, double xVel, double yVel, double zVel, int size) {
            String type = ParticleType.getTypeFromRaw(typeRaw);
            if (ParticleType.checkValid(type, size)) {
                ScriptManager.nativeLevelAddParticle(type, (float) x, (float) y, (float) z, (float) xVel, (float) yVel, (float) zVel, size);
            }
        }

        @JSStaticFunction
        public static int getBiome(int x, int z) {
            return ScriptManager.nativeLevelGetBiome(x, z);
        }

        @JSStaticFunction
        public static String getBiomeName(int x, int z) {
            return ScriptManager.nativeLevelGetBiomeName(x, z);
        }

        @JSStaticFunction
        public static String biomeIdToName(int id) {
            return ScriptManager.nativeBiomeIdToName(id);
        }

        @JSStaticFunction
        public static int getGrassColor(int x, int z) {
            return ScriptManager.nativeLevelGetGrassColor(x, z);
        }

        @JSStaticFunction
        public static void setGrassColor(int x, int z, int color) {
            ScriptManager.nativeLevelSetGrassColor(x, z, color);
        }

        @JSStaticFunction
        public static void setSpawnerEntityType(int x, int y, int z, int type) {
            if (getTile(x, y, z) != 52) {
                throw new RuntimeException("Block at " + x + ":" + y + ":" + z + " is not a mob spawner!");
            }
            ScriptManager.nativeSpawnerSetEntityType(x, y, z, type);
        }

        @JSStaticFunction
        public static int getSpawnerEntityType(int x, int y, int z) {
            if (getTile(x, y, z) == 52) {
                return ScriptManager.nativeSpawnerGetEntityType(x, y, z);
            }
            throw new RuntimeException("Block at " + x + ":" + y + ":" + z + " is not a mob spawner!");
        }

        @JSStaticFunction
        public static double getLightningLevel() {
            return (double) ScriptManager.nativeLevelGetLightningLevel();
        }

        @JSStaticFunction
        public static void setLightningLevel(double val) {
            ScriptManager.nativeLevelSetLightningLevel((float) val);
        }

        @JSStaticFunction
        public static double getRainLevel() {
            return (double) ScriptManager.nativeLevelGetRainLevel();
        }

        @JSStaticFunction
        public static void setRainLevel(double val) {
            ScriptManager.nativeLevelSetRainLevel((float) val);
        }

        @JSStaticFunction
        public static boolean canSeeSky(int x, int y, int z) {
            return ScriptManager.nativeLevelCanSeeSky(x, y, z);
        }

        @JSStaticFunction
        public static int getDifficulty() {
            return ScriptManager.nativeLevelGetDifficulty();
        }

        @JSStaticFunction
        public static void setDifficulty(int difficulty) {
            ScriptManager.nativeLevelSetDifficulty(difficulty);
        }

        @JSStaticFunction
        public static void setBlockExtraData(int x, int y, int z, int data) {
            ScriptManager.nativeLevelSetExtraData(x, y, z, data);
        }

        public String getClassName() {
            return "Level";
        }
    }

    private static class NativeModPEApi extends ScriptableObject {
        @JSStaticFunction
        public static void log(String str) {
            Log.i("MCPELauncherLog", str);
        }

        @JSStaticFunction
        public static void setTerrain(String url) {
            overrideTexture("images/terrain-atlas.tga", url);
        }

        @JSStaticFunction
        public static void setItems(String url) {
            overrideTexture("images/items-opaque.png", url);
        }

        @JSStaticFunction
        public static void setGuiBlocks(String url) {
            overrideTexture("gui/gui_blocks.png", url);
        }

        @JSStaticFunction
        public static void overrideTexture(String theOverridden, String url) {
            ScriptManager.overrideTexture(url, theOverridden);
        }

        @JSStaticFunction
        public static void resetImages() {
            ScriptManager.clearTextureOverrides();
        }

        @JSStaticFunction
        public static void setItem(int id, String iconName, int iconSubindex, String name, int maxStackSize) {
            try {
                Integer.parseInt(iconName);
                throw new IllegalArgumentException("The item icon for " + name.trim() + " is not updated for 0.8.0. Please ask the script author to update");
            } catch (NumberFormatException e) {
                if (id < 0 || id >= ScriptManager.ITEM_ID_COUNT) {
                    throw new IllegalArgumentException("Item IDs must be >= 0 and < ITEM_ID_COUNT");
                } else if (ScriptManager.itemsMeta == null || ScriptManager.itemsMeta.hasIcon(iconName, iconSubindex)) {
                    ScriptManager.nativeDefineItem(id, iconName, iconSubindex, name, maxStackSize);
                } else {
                    throw new MissingTextureException("The item icon " + iconName + ":" + iconSubindex + " does not exist");
                }
            }
        }

        @JSStaticFunction
        public static void setFoodItem(int id, String iconName, int iconSubindex, int halfhearts, String name, int maxStackSize) {
            setItem(id, iconName, iconSubindex, name, maxStackSize);
            NativeItemApi.setProperties(id, "{\"use_animation\":\"eat\",\"use_duration\": 32,\"food\":{\"nutrition\":" + halfhearts + ",\"saturation_modifier\": \"normal\"," + "\"is_meat\": false}}");
        }

        private static String getLevelName(File worldDir) throws IOException {
            File nameFile = new File(worldDir, "levelname.txt");
            if (!nameFile.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(nameFile);
            byte[] buf = new byte[((int) nameFile.length())];
            fis.read(buf);
            fis.close();
            return new String(buf, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        }

        @JSStaticFunction
        public static void selectLevel(String levelDir) {
            String levelDirName = levelDir;
            File worldsDir = new File("/sdcard/games/com.mojang/minecraftWorlds");
            File theDir = new File(worldsDir, levelDirName);
            if (!theDir.exists()) {
                File[] arr$ = worldsDir.listFiles();
                int len$ = arr$.length;
                for (int i$ = ScriptManager.ITEMID; i$ < len$; i$ += ScriptManager.DAMAGE) {
                    File worldDir = arr$[i$];
                    try {
                        String worldName = getLevelName(worldDir);
                        if (worldName != null && worldName.equals(levelDir)) {
                            levelDirName = worldDir.getName();
                            theDir = worldDir;
                            break;
                        }
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
            }
            if (!theDir.exists()) {
                throw new RuntimeException("The selected world " + levelDir + " does not exist.");
            } else if (levelDirName.equals(ScriptManager.worldDir)) {
                System.err.println("Attempted to load level that is already loaded - ignore");
            } else {
                String levelFullName = null;
                try {
                    levelFullName = getLevelName(theDir);
                } catch (IOException ie2) {
                    ie2.printStackTrace();
                }
                ScriptManager.setRequestLeaveGame();
                ScriptManager.requestSelectLevel = new SelectLevelRequest();
                ScriptManager.requestSelectLevel.dir = levelDirName;
                SelectLevelRequest access$1800 = ScriptManager.requestSelectLevel;
                if (levelFullName != null) {
                    levelDirName = levelFullName;
                }
                access$1800.name = levelDirName;
            }
        }

        @JSStaticFunction
        public static String readData(String prefName) {
            return ScriptManager.androidContext.getSharedPreferences("BlockLauncherModPEScript" + ScriptManager.currentScript, ScriptManager.ITEMID).getString(prefName, BuildConfig.FLAVOR);
        }

        @JSStaticFunction
        public static void saveData(String prefName, String prefValue) {
            Editor prefsEditor = ScriptManager.androidContext.getSharedPreferences("BlockLauncherModPEScript" + ScriptManager.currentScript, ScriptManager.ITEMID).edit();
            prefsEditor.putString(prefName, prefValue);
            prefsEditor.commit();
        }

        @JSStaticFunction
        public static void removeData(String prefName) {
            Editor prefsEditor = ScriptManager.androidContext.getSharedPreferences("BlockLauncherModPEScript" + ScriptManager.currentScript, ScriptManager.ITEMID).edit();
            prefsEditor.remove(prefName);
            prefsEditor.commit();
        }

        @JSStaticFunction
        public static void leaveGame() {
            ScriptManager.setRequestLeaveGame();
        }

        @JSStaticFunction
        public static void setGameSpeed(double ticksPerSecond) {
            ScriptManager.nativeSetGameSpeed((float) ticksPerSecond);
        }

        @JSStaticFunction
        public static void takeScreenshot(String fileName) {
            ScriptManager.screenshotFileName = fileName.replace("/", BuildConfig.FLAVOR).replace("\\", BuildConfig.FLAVOR);
            ScriptManager.nativeRequestFrameCallback();
        }

        @JSStaticFunction
        public static void langEdit(String key, String value) {
            ScriptManager.nativeSetI18NString(key, value);
        }

        @JSStaticFunction
        public static void showTipMessage(String msg) {
            ScriptManager.nativeShowTipMessage(msg);
        }

        @JSStaticFunction
        public static void setCamera(Object entityId) {
            ScriptManager.nativeSetCameraEntity(ScriptManager.getEntityId(entityId));
        }

        @JSStaticFunction
        public static void setFov(double fov) {
            ScriptManager.nativeSetFov((float) fov, true);
        }

        @JSStaticFunction
        public static void resetFov() {
            ScriptManager.nativeSetFov(0.0f, false);
        }

        @JSStaticFunction
        public static String getMinecraftVersion() {
            try {
                return ScriptManager.androidContext.getPackageManager().getPackageInfo("com.mojang.minecraftpe", ScriptManager.ITEMID).versionName;
            } catch (Exception e) {
                e.printStackTrace();
                return UTCTelemetry.UNKNOWNPAGE;
            }
        }

        @JSStaticFunction
        public static byte[] getBytesFromTexturePack(String name) {
            if (MainActivity.currentMainActivity != null) {
                MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                if (main != null) {
                    byte[] bytes = main.getFileDataBytes(name);
                    if (bytes != null) {
                        return bytes;
                    }
                    StringBuilder append = new StringBuilder().append(main.getMCPEVersion().startsWith(MainActivity.SCRIPT_SUPPORT_VERSION) ? "resourcepacks/vanilla/client/" : "resource_packs/vanilla/");
                    if (name.startsWith("images/")) {
                        name = "textures/" + name.substring("images/".length());
                    }
                    return main.getFileDataBytes(append.append(name).toString());
                }
            }
            return null;
        }

        @JSStaticFunction
        public static InputStream openInputStreamFromTexturePack(String name) {
            if (MainActivity.currentMainActivity != null) {
                MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                if (main != null) {
                    InputStream is = main.getInputStreamForAsset(name);
                    if (is != null) {
                        return is;
                    }
                    StringBuilder append = new StringBuilder().append(main.getMCPEVersion().startsWith(MainActivity.SCRIPT_SUPPORT_VERSION) ? "resourcepacks/vanilla/client/" : "resource_packs/vanilla/");
                    if (name.startsWith("images/")) {
                        name = "textures/" + name.substring("images/".length());
                    }
                    return main.getInputStreamForAsset(append.append(name).toString());
                }
            }
            return null;
        }

        @JSStaticFunction
        public static void dumpVtable(String className, int size) {
            ScriptManager.nativeDumpVtable("_ZTV" + className.length() + className, size);
        }

        @JSStaticFunction
        public static String getI18n(String key) {
            return ScriptManager.nativeGetI18NString(key);
        }

        @JSStaticFunction
        public static String getLanguage() {
            return ScriptManager.nativeGetLanguageName();
        }

        @JSStaticFunction
        public static void setUiRenderDebug(boolean render) {
            ScriptManager.nativeModPESetRenderDebug(render);
        }

        @JSStaticFunction
        public static String getOS() {
            return AbstractStsRequest.DeviceType;
        }

        public String getClassName() {
            return "ModPE";
        }
    }

    private static class NativePlayerApi extends ScriptableObject {
        private static long playerEnt = 0;

        @JSStaticFunction
        public static double getX() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static double getY() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static double getZ() {
            return (double) ScriptManager.nativeGetPlayerLoc(ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static long getEntity() {
            playerEnt = ScriptManager.nativeGetPlayerEnt();
            return playerEnt;
        }

        @JSStaticFunction
        public static int getCarriedItem() {
            return ScriptManager.nativeGetCarriedItem(ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static void addItemInventory(int id, int amount, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeAddItemInventory(id, amount, damage);
                return;
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSStaticFunction
        public static void setHealth(int value) {
            ScriptManager.nativeSetMobHealth(ScriptManager.nativeGetPlayerEnt(), value);
        }

        @JSStaticFunction
        public static int getSelectedSlotId() {
            return ScriptManager.nativeGetSelectedSlotId();
        }

        @JSStaticFunction
        public static void setSelectedSlotId(int slot) {
            ScriptManager.nativeSetSelectedSlotId(slot);
        }

        @JSStaticFunction
        public static void clearInventorySlot(int slot) {
            ScriptManager.nativeClearSlotInventory(slot);
        }

        @JSStaticFunction
        public static int getInventorySlot(int slot) {
            return ScriptManager.nativeGetSlotInventory(slot, ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static int getInventorySlotData(int slot) {
            return ScriptManager.nativeGetSlotInventory(slot, ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static int getInventorySlotCount(int slot) {
            return ScriptManager.nativeGetSlotInventory(slot, ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static int getCarriedItemData() {
            return ScriptManager.nativeGetCarriedItem(ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static int getCarriedItemCount() {
            return ScriptManager.nativeGetCarriedItem(ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static void addItemCreativeInv(int id, int count, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                ScriptManager.nativeAddItemCreativeInv(id, count, damage);
                return;
            }
            throw new RuntimeException("You must make an item with id " + id + " before you can add it to the creative inventory.");
        }

        @JSStaticFunction
        public static int getArmorSlot(int slot) {
            return NativeEntityApi.getArmor(Long.valueOf(getEntity()), slot);
        }

        @JSStaticFunction
        public static int getArmorSlotDamage(int slot) {
            return NativeEntityApi.getArmorDamage(Long.valueOf(getEntity()), slot);
        }

        @JSStaticFunction
        public static void setArmorSlot(int slot, int id, int damage) {
            if (ScriptManager.nativeIsValidItem(id)) {
                NativeEntityApi.setArmor(Long.valueOf(getEntity()), slot, id, damage);
                return;
            }
            throw new RuntimeException("invalid item id " + id);
        }

        @JSStaticFunction
        public static String getName(Object ent) {
            if (isPlayer(ent)) {
                return ScriptManager.nativeGetPlayerName(ScriptManager.getEntityId(ent));
            }
            if (ent == null || ScriptManager.getEntityId(ent) == getEntity()) {
                return ScriptManager.getPlayerNameFromConfs();
            }
            return "Not a player";
        }

        @JSStaticFunction
        public static boolean isPlayer(Object ent) {
            return NativeEntityApi.getEntityTypeId(Long.valueOf(ScriptManager.getEntityId(ent))) == 63;
        }

        @JSStaticFunction
        public static long getPointedEntity() {
            return ScriptManager.nativePlayerGetPointedEntity();
        }

        @JSStaticFunction
        public static int getPointedBlockX() {
            return ScriptManager.nativePlayerGetPointedBlock(ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static int getPointedBlockY() {
            return ScriptManager.nativePlayerGetPointedBlock(ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static int getPointedBlockZ() {
            return ScriptManager.nativePlayerGetPointedBlock(ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static int getPointedBlockId() {
            return ScriptManager.nativePlayerGetPointedBlock(16);
        }

        @JSStaticFunction
        public static int getPointedBlockData() {
            return ScriptManager.nativePlayerGetPointedBlock(17);
        }

        @JSStaticFunction
        public static int getPointedBlockSide() {
            return ScriptManager.nativePlayerGetPointedBlock(18);
        }

        @JSStaticFunction
        public static double getPointedVecX() {
            return (double) ScriptManager.nativePlayerGetPointedVec(ScriptManager.ITEMID);
        }

        @JSStaticFunction
        public static double getPointedVecY() {
            return (double) ScriptManager.nativePlayerGetPointedVec(ScriptManager.DAMAGE);
        }

        @JSStaticFunction
        public static double getPointedVecZ() {
            return (double) ScriptManager.nativePlayerGetPointedVec(ScriptManager.AXIS_Z);
        }

        @JSStaticFunction
        public static void setInventorySlot(int slot, int itemId, int count, int damage) {
            ScriptManager.nativeSetInventorySlot(slot, itemId, count, damage);
        }

        @JSStaticFunction
        public static boolean isFlying() {
            return ScriptManager.nativePlayerIsFlying();
        }

        @JSStaticFunction
        public static void setFlying(boolean val) {
            ScriptManager.nativePlayerSetFlying(val);
        }

        @JSStaticFunction
        public static boolean canFly() {
            return ScriptManager.nativePlayerCanFly();
        }

        @JSStaticFunction
        public static void setCanFly(boolean val) {
            ScriptManager.nativePlayerSetCanFly(val);
        }

        @JSStaticFunction
        public static int getDimension() {
            return ScriptManager.nativePlayerGetDimension();
        }

        @JSStaticFunction
        public static double getHunger() {
            return (double) ScriptManager.nativePlayerGetHunger(getEntity());
        }

        @JSStaticFunction
        public static void setHunger(double value) {
            ScriptManager.nativePlayerSetHunger(getEntity(), (float) value);
        }

        @JSStaticFunction
        public static double getExhaustion() {
            return (double) ScriptManager.nativePlayerGetExhaustion();
        }

        @JSStaticFunction
        public static void setExhaustion(double value) {
            ScriptManager.nativePlayerSetExhaustion((float) value);
        }

        @JSStaticFunction
        public static double getSaturation() {
            return (double) ScriptManager.nativePlayerGetSaturation();
        }

        @JSStaticFunction
        public static void setSaturation(double value) {
            ScriptManager.nativePlayerSetSaturation((float) value);
        }

        @JSStaticFunction
        public static int getLevel() {
            return ScriptManager.nativePlayerGetLevel();
        }

        @JSStaticFunction
        public static void setLevel(int value) {
            ScriptManager.nativePlayerSetLevel(value);
        }

        @JSStaticFunction
        public static void addExp(int value) {
            ScriptManager.nativePlayerAddExperience(value);
        }

        @JSStaticFunction
        public static double getExp() {
            return (double) ScriptManager.nativePlayerGetExperience();
        }

        @JSStaticFunction
        public static void setExp(double value) {
            ScriptManager.nativePlayerSetExperience((float) value);
        }

        @JSStaticFunction
        public static boolean enchant(int slot, int enchantment, int level) {
            if (enchantment >= 0 && enchantment <= 24) {
                return ScriptManager.nativePlayerEnchant(slot, enchantment, level);
            }
            throw new RuntimeException("Invalid enchantment: " + enchantment);
        }

        @JSStaticFunction
        public static EnchantmentInstance[] getEnchantments(int slot) {
            int[] ret = ScriptManager.nativePlayerGetEnchantments(slot);
            if (ret == null) {
                return null;
            }
            EnchantmentInstance[] en = new EnchantmentInstance[(ret.length / ScriptManager.AXIS_Z)];
            for (int i = ScriptManager.ITEMID; i < en.length; i += ScriptManager.DAMAGE) {
                en[i] = new EnchantmentInstance(ret[i * ScriptManager.AXIS_Z], ret[(i * ScriptManager.AXIS_Z) + ScriptManager.DAMAGE]);
            }
            return en;
        }

        @JSStaticFunction
        public static String getItemCustomName(int slot) {
            return ScriptManager.nativePlayerGetItemCustomName(slot);
        }

        @JSStaticFunction
        public static void setItemCustomName(int slot, String name) {
            ScriptManager.nativePlayerSetItemCustomName(slot, name);
        }

        @JSStaticFunction
        public static int getScore() {
            return ScriptManager.nativePlayerGetScore();
        }

        public String getClassName() {
            return "Player";
        }
    }

    private static class NativePointer extends ScriptableObject {
        public long value;

        public NativePointer(long value) {
            this.value = value;
        }

        public String getClassName() {
            return "NativePointer";
        }
    }

    private static class NativeServerApi extends ScriptableObject {
        @JSStaticFunction
        public static void joinServer(String serverAddress, int port) {
            throw new RuntimeException("FIXME 0.11");
        }

        @JSStaticFunction
        public static void sendChat(String message) {
            ScriptManager.nativeSendChat(message);
        }

        @JSStaticFunction
        public static String getAddress() {
            return ScriptManager.serverAddress;
        }

        @JSStaticFunction
        public static int getPort() {
            return ScriptManager.serverPort;
        }

        @JSStaticFunction
        public static long[] getAllPlayers() {
            long[] players = new long[ScriptManager.allplayers.size()];
            for (int n = ScriptManager.ITEMID; players.length > n; n += ScriptManager.DAMAGE) {
                players[n] = ((Long) ScriptManager.allplayers.get(n)).longValue();
            }
            return players;
        }

        @JSStaticFunction
        public static String[] getAllPlayerNames() {
            String[] players = new String[ScriptManager.allplayers.size()];
            for (int n = ScriptManager.ITEMID; players.length > n; n += ScriptManager.DAMAGE) {
                players[n] = ScriptManager.nativeGetPlayerName(((Long) ScriptManager.allplayers.get(n)).longValue());
            }
            return players;
        }

        public String getClassName() {
            return "Server";
        }
    }

    private static class ParseThread implements Runnable {
        public Exception error = null;
        private Reader in;
        private String sourceName;

        public ParseThread(Reader in, String sourceName) {
            this.in = in;
            this.sourceName = sourceName;
        }

        public void run() {
            try {
                org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter();
                ScriptManager.setupContext(ctx);
                ScriptManager.initJustLoadedScript(ctx, ctx.compileReader(this.in, this.sourceName, ScriptManager.ITEMID, null), this.sourceName);
                org.mozilla.javascript.Context.exit();
            } catch (Exception e) {
                e.printStackTrace();
                this.error = e;
            }
        }
    }

    public static class ScriptState {
        public int errors = ScriptManager.ITEMID;
        public String name;
        public Scriptable scope;
        public Script script;

        protected ScriptState(Script script, Scriptable scope, String name) {
            this.script = script;
            this.scope = scope;
            this.name = name;
        }
    }

    private static class SelectLevelRequest {
        public String dir;
        public int gameMode;
        public String name;
        public String seed;

        private SelectLevelRequest() {
            this.gameMode = ScriptManager.ITEMID;
        }
    }

    private static class SkinLoader implements Runnable {
        private long entityId;

        public SkinLoader(long entityId) {
            this.entityId = entityId;
        }

        public void run() {
            try {
                String playerName = ScriptManager.nativeGetPlayerName(this.entityId);
                System.out.println("Player name: " + playerName + " entity ID: " + this.entityId);
                if (playerName != null) {
                    if (ScriptManager.isSkinNameNormalized()) {
                        playerName = playerName.toLowerCase();
                    }
                    if (playerName.length() > 0) {
                        String skinName = "mob/" + playerName + DownloadProfileImageTask.UserTileExtension;
                        File skinFile = ScriptManager.getTextureOverrideFile("images/" + skinName);
                        if (skinFile != null) {
                            new Thread(new ScriptTextureDownloader(new URL(ScriptManager.getSkinURL(playerName)), skinFile, new AfterSkinDownloadAction(this.entityId, skinName), false)).start();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class TextureRequests {
        public int[] coords;
        public String[] names;

        private TextureRequests() {
        }
    }

    public static native void nativeAddFurnaceRecipe(int i, int i2, int i3);

    public static native void nativeAddItemChest(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public static native void nativeAddItemCreativeInv(int i, int i2, int i3);

    public static native void nativeAddItemFurnace(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public static native void nativeAddItemInventory(int i, int i2, int i3);

    public static native void nativeAddShapedRecipe(int i, int i2, int i3, String[] strArr, int[] iArr);

    public static native void nativeArmorAddQueuedTextures();

    public static native String nativeBiomeIdToName(int i);

    public static native float nativeBlockGetDestroyTime(int i, int i2);

    public static native float nativeBlockGetFriction(int i);

    public static native int nativeBlockGetRenderLayer(int i);

    public static native int nativeBlockGetSecondPart(int i, int i2, int i3, int i4);

    public static native void nativeBlockSetCollisionEnabled(int i, boolean z);

    public static native void nativeBlockSetColor(int i, int[] iArr);

    public static native void nativeBlockSetDestroyTime(int i, float f);

    public static native void nativeBlockSetExplosionResistance(int i, float f);

    public static native void nativeBlockSetFriction(int i, float f);

    public static native void nativeBlockSetLightLevel(int i, int i2);

    public static native void nativeBlockSetLightOpacity(int i, int i2);

    public static native void nativeBlockSetRedstoneConsumer(int i, boolean z);

    public static native void nativeBlockSetRenderLayer(int i, int i2);

    public static native void nativeBlockSetShape(int i, float f, float f2, float f3, float f4, float f5, float f6, int i2);

    public static native void nativeBlockSetStepSound(int i, int i2);

    public static native void nativeClearCapes();

    public static native void nativeClearSlotInventory(int i);

    public static native void nativeClientMessage(String str);

    public static native void nativeCloseScreen();

    public static native void nativeDefineArmor(int i, String str, int i2, String str2, String str3, int i3, int i4, int i5);

    public static native void nativeDefineBlock(int i, String str, String[] strArr, int[] iArr, int i2, boolean z, int i3, int i4);

    public static native void nativeDefineItem(int i, String str, int i2, String str2, int i3);

    public static native void nativeDefinePlaceholderBlocks();

    public static native void nativeDefineSnowballItem(int i, String str, int i2, String str2, int i3);

    public static native void nativeDestroyBlock(int i, int i2, int i3);

    public static native long nativeDropItem(float f, float f2, float f3, float f4, int i, int i2, int i3);

    public static native void nativeDumpVtable(String str, int i);

    public static native int nativeEntityGetCarriedItem(long j, int i);

    public static native String nativeEntityGetMobSkin(long j);

    public static native String nativeEntityGetNameTag(long j);

    public static native int nativeEntityGetRenderType(long j);

    public static native int nativeEntityGetRider(long j);

    public static native int nativeEntityGetRiding(long j);

    public static native long nativeEntityGetTarget(long j);

    public static native long[] nativeEntityGetUUID(long j);

    public static native boolean nativeEntityHasCustomSkin(long j);

    public static native void nativeEntitySetImmobile(long j, boolean z);

    public static native void nativeEntitySetNameTag(long j, String str);

    public static native void nativeEntitySetSize(long j, float f, float f2);

    public static native void nativeEntitySetTarget(long j, long j2);

    public static native void nativeExplode(float f, float f2, float f3, float f4, boolean z, boolean z2, float f5);

    public static native void nativeExtinguishFire(int i, int i2, int i3, int i4);

    public static native void nativeForceCrash();

    public static native void nativeGetAllEntities();

    public static native int nativeGetAnimalAge(long j);

    public static native int nativeGetArch();

    public static native int nativeGetBlockRenderShape(int i);

    public static native int nativeGetBrightness(int i, int i2, int i3);

    public static native int nativeGetCarriedItem(int i);

    public static native int nativeGetData(int i, int i2, int i3);

    public static native float nativeGetEntityLoc(long j, int i);

    public static native int nativeGetEntityTypeId(long j);

    public static native float nativeGetEntityVel(long j, int i);

    public static native int nativeGetGameType();

    public static native String nativeGetI18NString(String str);

    public static native int nativeGetItemChest(int i, int i2, int i3, int i4);

    public static native int nativeGetItemCountChest(int i, int i2, int i3, int i4);

    public static native int nativeGetItemCountFurnace(int i, int i2, int i3, int i4);

    public static native int nativeGetItemDataChest(int i, int i2, int i3, int i4);

    public static native int nativeGetItemDataFurnace(int i, int i2, int i3, int i4);

    public static native int nativeGetItemEntityItem(long j, int i);

    public static native int nativeGetItemFurnace(int i, int i2, int i3, int i4);

    public static native int nativeGetItemIdCount();

    public static native int nativeGetItemMaxDamage(int i);

    public static native String nativeGetItemName(int i, int i2, boolean z);

    public static native String nativeGetItemNameChest(int i, int i2, int i3, int i4);

    public static native String nativeGetLanguageName();

    public static native long nativeGetLevel();

    public static native int nativeGetMobHealth(long j);

    public static native int nativeGetMobMaxHealth(long j);

    public static native float nativeGetPitch(long j);

    public static native long nativeGetPlayerEnt();

    public static native float nativeGetPlayerLoc(int i);

    public static native String nativeGetPlayerName(long j);

    public static native int nativeGetSelectedSlotId();

    public static native String nativeGetSignText(int i, int i2, int i3, int i4);

    public static native int nativeGetSlotInventory(int i, int i2);

    public static native boolean nativeGetTextureCoordinatesForBlock(int i, int i2, int i3, float[] fArr);

    public static native boolean nativeGetTextureCoordinatesForItem(int i, int i2, float[] fArr);

    public static native int nativeGetTile(int i, int i2, int i3);

    public static native long nativeGetTime();

    public static native float nativeGetYaw(long j);

    public static native boolean nativeHasPreventedDefault();

    public static native void nativeHurtTo(int i);

    public static native boolean nativeIsBlockTextureAtlasLoaded();

    public static native boolean nativeIsSneaking(long j);

    public static native boolean nativeIsValidCommand(String str);

    public static native boolean nativeIsValidItem(int i);

    public static native int nativeItemGetMaxStackSize(int i);

    public static native int nativeItemGetUseAnimation(int i);

    public static native boolean nativeItemIsExtendedBlock(int i);

    public static native boolean nativeItemSetProperties(int i, String str);

    public static native void nativeItemSetStackedByData(int i, boolean z);

    public static native void nativeItemSetUseAnimation(int i, int i2);

    public static native void nativeJoinServer(String str, int i);

    public static native void nativeLeaveGame(boolean z);

    public static native void nativeLevelAddParticle(String str, float f, float f2, float f3, float f4, float f5, float f6, int i);

    public static native boolean nativeLevelCanSeeSky(int i, int i2, int i3);

    public static native int nativeLevelGetBiome(int i, int i2);

    public static native String nativeLevelGetBiomeName(int i, int i2);

    public static native int nativeLevelGetDifficulty();

    public static native int nativeLevelGetExtraData(int i, int i2, int i3);

    public static native int nativeLevelGetGrassColor(int i, int i2);

    public static native float nativeLevelGetLightningLevel();

    public static native float nativeLevelGetRainLevel();

    public static native boolean nativeLevelIsRemote();

    public static native void nativeLevelSetBiome(int i, int i2, int i3);

    public static native void nativeLevelSetDifficulty(int i);

    public static native void nativeLevelSetExtraData(int i, int i2, int i3, int i4);

    public static native void nativeLevelSetGrassColor(int i, int i2, int i3);

    public static native void nativeLevelSetLightningLevel(float f);

    public static native void nativeLevelSetRainLevel(float f);

    public static native void nativeMobAddEffect(long j, int i, int i2, int i3, boolean z, boolean z2);

    public static native int nativeMobGetArmor(long j, int i, int i2);

    public static native String nativeMobGetArmorCustomName(long j, int i);

    public static native void nativeMobRemoveAllEffects(long j);

    public static native void nativeMobRemoveEffect(long j, int i);

    public static native void nativeMobSetArmor(long j, int i, int i2, int i3);

    public static native void nativeMobSetArmorCustomName(long j, int i, String str);

    public static native void nativeModPESetDesktopGui(boolean z);

    public static native void nativeModPESetRenderDebug(boolean z);

    public static native void nativeNewLevelCallbackEnded();

    public static native void nativeNewLevelCallbackStarted();

    public static native void nativeOnGraphicsReset();

    public static native void nativePlaySound(float f, float f2, float f3, String str, float f4, float f5);

    public static native void nativePlayerAddExperience(int i);

    public static native boolean nativePlayerCanFly();

    public static native boolean nativePlayerEnchant(int i, int i2, int i3);

    public static native int nativePlayerGetDimension();

    public static native int[] nativePlayerGetEnchantments(int i);

    public static native float nativePlayerGetExhaustion();

    public static native float nativePlayerGetExperience();

    public static native float nativePlayerGetHunger(long j);

    public static native String nativePlayerGetItemCustomName(int i);

    public static native int nativePlayerGetLevel();

    public static native int nativePlayerGetPointedBlock(int i);

    public static native long nativePlayerGetPointedEntity();

    public static native float nativePlayerGetPointedVec(int i);

    public static native float nativePlayerGetSaturation();

    public static native int nativePlayerGetScore();

    public static native boolean nativePlayerIsFlying();

    public static native void nativePlayerSetCanFly(boolean z);

    public static native void nativePlayerSetExhaustion(float f);

    public static native void nativePlayerSetExperience(float f);

    public static native void nativePlayerSetFlying(boolean z);

    public static native void nativePlayerSetHunger(long j, float f);

    public static native void nativePlayerSetItemCustomName(int i, String str);

    public static native void nativePlayerSetLevel(int i);

    public static native void nativePlayerSetSaturation(float f);

    public static native void nativePrePatch(boolean z, MainActivity mainActivity, boolean z2);

    public static native void nativePreventDefault();

    public static native void nativeRecipeSetAnyAuxValue(int i, boolean z);

    public static native void nativeRemoveEntity(long j);

    public static native void nativeRemoveItemBackground();

    public static native void nativeRequestFrameCallback();

    public static native void nativeRideAnimal(long j, long j2);

    public static native void nativeScreenChooserSetScreen(int i);

    public static native void nativeSelectLevel(String str, String str2);

    public static native void nativeSendChat(String str);

    public static native void nativeSetAllowEnchantments(int i, int i2, int i3);

    public static native void nativeSetAnimalAge(long j, int i);

    public static native void nativeSetBlockRenderShape(int i, int i2);

    public static native void nativeSetCameraEntity(long j);

    public static native void nativeSetCape(long j, String str);

    public static native void nativeSetCarriedItem(long j, int i, int i2, int i3);

    public static native boolean nativeSetEntityRenderType(long j, int i);

    public static native void nativeSetExitEnabled(boolean z);

    public static native void nativeSetFov(float f, boolean z);

    public static native void nativeSetGameSpeed(float f);

    public static native void nativeSetGameType(int i);

    public static native void nativeSetHandEquipped(int i, boolean z);

    public static native void nativeSetI18NString(String str, String str2);

    public static native void nativeSetInventorySlot(int i, int i2, int i3, int i4);

    public static native void nativeSetIsRecording(boolean z);

    public static native void nativeSetItemCategory(int i, int i2, int i3);

    public static native void nativeSetItemMaxDamage(int i, int i2);

    public static native void nativeSetItemNameChest(int i, int i2, int i3, int i4, String str);

    public static native void nativeSetMobHealth(long j, int i);

    public static native void nativeSetMobMaxHealth(long j, int i);

    public static native void nativeSetMobSkin(long j, String str);

    public static native void nativeSetNightMode(boolean z);

    public static native void nativeSetOnFire(long j, int i);

    public static native void nativeSetPosition(long j, float f, float f2, float f3);

    public static native void nativeSetPositionRelative(long j, float f, float f2, float f3);

    public static native void nativeSetRot(long j, float f, float f2);

    public static native void nativeSetSelectedSlotId(int i);

    public static native void nativeSetSignText(int i, int i2, int i3, int i4, String str);

    public static native void nativeSetSneaking(long j, boolean z);

    public static native void nativeSetSpawn(int i, int i2, int i3);

    public static native void nativeSetStonecutterItem(int i, int i2);

    public static native void nativeSetTextParseColorCodes(boolean z);

    public static native void nativeSetTile(int i, int i2, int i3, int i4, int i5);

    public static native void nativeSetTime(long j);

    public static native void nativeSetUseController(boolean z);

    public static native void nativeSetVel(long j, float f, int i);

    public static native void nativeSetupHooks(int i);

    public static native void nativeShowProgressScreen();

    public static native void nativeShowTipMessage(String str);

    public static native long nativeSpawnEntity(float f, float f2, float f3, int i, String str);

    public static native int nativeSpawnerGetEntityType(int i, int i2, int i3);

    public static native void nativeSpawnerSetEntityType(int i, int i2, int i3, int i4);

    public static void loadScript(Reader in, String sourceName) throws IOException {
        if (!scriptingInitialized) {
            return;
        }
        if (scriptingEnabled) {
            ParseThread parseRunner = new ParseThread(in, sourceName);
            Thread t = new Thread(Thread.currentThread().getThreadGroup(), parseRunner, "BlockLauncher parse thread", 262144);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
            }
            if (parseRunner.error != null) {
                RuntimeException back;
                if (parseRunner.error instanceof RuntimeException) {
                    back = (RuntimeException) parseRunner.error;
                } else {
                    back = new RuntimeException(parseRunner.error);
                }
                throw back;
            }
            return;
        }
        throw new RuntimeException("Not available in multiplayer");
    }

    public static void loadScript(File file, boolean firstLoad) throws IOException {
        Throwable th;
        if (isClassGenMode()) {
            if (!scriptingInitialized) {
                return;
            }
            if (scriptingEnabled) {
                loadScriptFromInstance(ScriptTranslationCache.get(androidContext, file), file.getName());
                return;
            }
            throw new RuntimeException("Not available in multiplayer");
        } else if (isPackagedScript(file)) {
            loadPackagedScript(file, firstLoad);
        } else {
            Reader in = null;
            try {
                Reader in2 = new FileReader(file);
                try {
                    loadScript(in2, file.getName());
                    if (in2 != null) {
                        in2.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    in = in2;
                    if (in != null) {
                        in.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (in != null) {
                    in.close();
                }
                throw th;
            }
        }
    }

    public static void loadScriptFromInstance(Script script, String sourceName) {
        org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter();
        setupContext(ctx);
        initJustLoadedScript(ctx, script, sourceName);
        org.mozilla.javascript.Context.exit();
    }

    public static void initJustLoadedScript(org.mozilla.javascript.Context ctx, Script script, String sourceName) {
        Scriptable scope = ctx.initStandardObjects(new BlockHostObject(), false);
        ScriptState state = new ScriptState(script, scope, sourceName);
        ((ScriptableObject) scope).defineFunctionProperties(getAllJsFunctions(BlockHostObject.class), BlockHostObject.class, AXIS_Z);
        try {
            ScriptableObject.defineClass(scope, NativePlayerApi.class);
            ScriptableObject.defineClass(scope, NativeLevelApi.class);
            ScriptableObject.defineClass(scope, NativeEntityApi.class);
            ScriptableObject.defineClass(scope, NativeModPEApi.class);
            ScriptableObject.defineClass(scope, NativeItemApi.class);
            ScriptableObject.defineClass(scope, NativeBlockApi.class);
            ScriptableObject.defineClass(scope, NativeServerApi.class);
            RendererManager.defineClasses(scope);
            Class[] arr$ = constantsClasses;
            int len$ = arr$.length;
            for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
                Class<?> clazz = arr$[i$];
                ScriptableObject.putProperty(scope, clazz.getSimpleName(), classConstantsToJSObject(clazz));
            }
        } catch (Exception e) {
            dumpScriptError(e);
            reportScriptError(state, e);
        }
        script.exec(ctx, scope);
        scripts.add(state);
    }

    public static void callScriptMethod(String functionName, Object... args) {
        if (scriptingEnabled) {
            org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter();
            setupContext(ctx);
            for (ScriptState state : scripts) {
                if (state.errors < MAX_NUM_ERRORS) {
                    currentScript = state.name;
                    Scriptable scope = state.scope;
                    Object obj = scope.get(functionName, scope);
                    if (obj != null && (obj instanceof Function)) {
                        try {
                            ((Function) obj).call(ctx, scope, scope, args);
                        } catch (Exception e) {
                            dumpScriptError(e);
                            reportScriptError(state, e);
                        }
                    }
                }
            }
        }
    }

    @CallbackName(args = {"x", "y", "z", "itemid", "blockid", "side", "itemDamage", "blockDamage"}, name = "useItem", prevent = true)
    public static void useItemOnCallback(int x, int y, int z, int itemid, int blockid, int side, int itemDamage, int blockDamage) {
        callScriptMethod("useItem", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(itemid), Integer.valueOf(blockid), Integer.valueOf(side), Integer.valueOf(itemDamage), Integer.valueOf(blockDamage));
        if (itemid >= EnchantType.flintAndSteel && nativeItemIsExtendedBlock(itemid) && !nativeHasPreventedDefault()) {
            nativePreventDefault();
            doUseItemOurselves(x, y, z, itemid, blockid, side, itemDamage, blockDamage);
        }
    }

    @CallbackName(args = {"x", "y", "z", "side"}, name = "destroyBlock", prevent = true)
    public static void destroyBlockCallback(int x, int y, int z, int side) {
        int blockId = nativeGetTileWrap(x, y, z);
        callScriptMethod("destroyBlock", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(side));
        if (blockId >= EnchantType.flintAndSteel && !nativeHasPreventedDefault()) {
            nativePreventDefault();
            NativeLevelApi.destroyBlock(x, y, z, true);
        }
    }

    @CallbackName(args = {"x", "y", "z", "side"}, name = "startDestroyBlock", prevent = true)
    public static void startDestroyBlockCallback(int x, int y, int z, int side) {
        callScriptMethod("startDestroyBlock", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(side));
    }

    @CallbackName(args = {"x", "y", "z", "side", "progress"}, name = "continueDestroyBlock", prevent = true)
    public static void continueDestroyBlockCallback(int x, int y, int z, int side, float progress) {
        boolean samePlace;
        if (x == lastDestroyX && y == lastDestroyY && z == lastDestroyZ && side == lastDestroySide) {
            samePlace = true;
        } else {
            samePlace = false;
        }
        if (progress == 0.0f && !(progress == lastDestroyProgress && samePlace)) {
            callScriptMethod("startDestroyBlock", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(side));
        }
        lastDestroyProgress = progress;
        lastDestroyX = x;
        lastDestroyY = y;
        lastDestroyZ = z;
        lastDestroySide = side;
        Object[] objArr = new Object[MAX_NUM_ERRORS];
        objArr[ITEMID] = Integer.valueOf(x);
        objArr[DAMAGE] = Integer.valueOf(y);
        objArr[AXIS_Z] = Integer.valueOf(z);
        objArr[3] = Integer.valueOf(side);
        objArr[4] = Float.valueOf(progress);
        callScriptMethod("continueDestroyBlock", objArr);
    }

    public static void setLevelCallback(boolean hasLevel, boolean isRemoteAAAAAA) {
    }

    @CallbackName(name = "newLevel")
    public static void setLevelFakeCallback(boolean hasLevel, boolean isRemote) {
        boolean z = true;
        isRemote = nativeLevelIsRemote();
        nextTickCallsSetLevel = false;
        System.out.println("Level: " + hasLevel);
        if (!isRemote) {
            scriptingEnabled = true;
        }
        nativeSetGameSpeed(20.0f);
        allentities.clear();
        allplayers.clear();
        entityUUIDMap.clear();
        nativeClearCapes();
        hasLevel = true;
        entityAddedCallback(nativeGetPlayerEnt());
        if (!isRemote) {
            runOnMainThreadList.add(new Runnable() {
                public void run() {
                    NativeItemApi.reregisterRecipes();
                }
            });
        }
        nativeNewLevelCallbackStarted();
        Object[] objArr = new Object[DAMAGE];
        objArr[ITEMID] = Boolean.valueOf(hasLevel);
        callScriptMethod("newLevel", objArr);
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                if (!scriptingEnabled) {
                    modernWrapFactory.closePopups(main);
                }
                if (scriptingEnabled) {
                    z = false;
                }
                main.setLevelCallback(z);
            }
        }
        nativeNewLevelCallbackEnded();
    }

    @CallbackName(name = "selectLevelHook")
    private static void selectLevelCallback(String wName, String wDir) {
        System.out.println("World name: " + wName);
        System.out.println("World dir: " + wDir);
        worldName = wName;
        worldDir = wDir;
        scriptingEnabled = true;
        isRemote = false;
        if (worldData != null) {
            try {
                worldData.save();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            worldData = null;
        }
        try {
            worldData = new WorldData(new File(new File("/sdcard/games/com.mojang/minecraftWorlds"), worldDir));
        } catch (IOException ie2) {
            ie2.printStackTrace();
        }
        callScriptMethod("selectLevelHook", new Object[ITEMID]);
        nativeArmorAddQueuedTextures();
        nextTickCallsSetLevel = true;
    }

    @CallbackName(name = "leaveGame")
    private static void leaveGameCallback(boolean thatboolean) {
        isRemote = false;
        scriptingEnabled = true;
        hasLevel = false;
        if (scriptingInitialized) {
            callScriptMethod("leaveGame", new Object[ITEMID]);
        }
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                main.leaveGameCallback();
            }
        }
        if (worldData != null) {
            try {
                worldData.save();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            worldData = null;
        }
        serverAddress = null;
        serverPort = ITEMID;
    }

    @CallbackName(args = {"attacker", "victim"}, name = "attackHook", prevent = true)
    public static void attackCallback(long attacker, long victim) {
        Object[] objArr = new Object[AXIS_Z];
        objArr[ITEMID] = Long.valueOf(attacker);
        objArr[DAMAGE] = Long.valueOf(victim);
        callScriptMethod("attackHook", objArr);
    }

    @CallbackName(args = {"attacker", "victim", "halfhearts"}, name = "entityHurtHook", prevent = true)
    public static void entityHurtCallback(long attacker, long victim, int halfhearts) {
        callScriptMethod("entityHurtHook", Long.valueOf(attacker), Long.valueOf(victim), Integer.valueOf(halfhearts));
    }

    @CallbackName(name = "modTick")
    public static void tickCallback() {
        int i;
        if (nextTickCallsSetLevel) {
            setLevelFakeCallback(true, nativeLevelIsRemote());
        }
        callScriptMethod("modTick", new Object[ITEMID]);
        if (requestedGraphicsReset) {
            nativeOnGraphicsReset();
            requestedGraphicsReset = false;
        }
        if (sensorEnabled) {
            updatePlayerOrientation();
        }
        if (requestLeaveGame) {
            i = requestLeaveGameCounter;
            requestLeaveGameCounter = i - 1;
            if (i <= 0) {
                nativeScreenChooserSetScreen(DAMAGE);
                nativeLeaveGame(false);
                requestLeaveGame = false;
                if (MainActivity.currentMainActivity != null) {
                    final MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                    if (main != null) {
                        main.runOnUiThread(new Runnable() {
                            public void run() {
                                main.dismissHiddenTextbox();
                                main.hideKeyboardView();
                                System.out.println("Closed keyboard, I hope");
                            }
                        });
                    }
                }
                nativeRequestFrameCallback();
            }
        }
        if (!(requestJoinServer == null || requestLeaveGame)) {
            nativeJoinServer(requestJoinServer.serverAddress, requestJoinServer.serverPort);
            requestJoinServer = null;
        }
        if (runOnMainThreadList.size() > 0) {
            synchronized (runOnMainThreadList) {
                for (Runnable r : runOnMainThreadList) {
                    r.run();
                }
                runOnMainThreadList.clear();
            }
        }
        if (worldData != null) {
            i = worldDataSaveCounter - 1;
            worldDataSaveCounter = i;
            if (i <= 0) {
                try {
                    worldData.save();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                worldDataSaveCounter = org.mozilla.javascript.Context.VERSION_ES6;
            }
        }
    }

    private static void updatePlayerOrientation() {
        nativeSetRot(nativeGetPlayerEnt(), newPlayerYaw, newPlayerPitch);
    }

    @CallbackName(args = {"str"}, name = "chatHook", prevent = true)
    public static void chatCallback(String str) {
        boolean validNativeCommand = true;
        if (isRemote) {
            nameAndShame(str);
        }
        if (str != null && str.length() >= DAMAGE) {
            Object[] objArr = new Object[DAMAGE];
            objArr[ITEMID] = str;
            callScriptMethod("chatHook", objArr);
            if (str.charAt(ITEMID) == '/') {
                objArr = new Object[DAMAGE];
                objArr[ITEMID] = str.substring(DAMAGE);
                callScriptMethod("procCmd", objArr);
                String[] splitted = str.substring(DAMAGE).split(" ");
                if (splitted.length <= 0 || !nativeIsValidCommand(splitted[ITEMID])) {
                    validNativeCommand = false;
                }
                if (!isRemote && !validNativeCommand) {
                    nativePreventDefault();
                    if (MainActivity.currentMainActivity != null) {
                        MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                        if (main != null) {
                            main.updateTextboxText(BuildConfig.FLAVOR);
                        }
                    }
                }
            }
        }
    }

    @CallbackName(args = {"attacker", "victim"}, name = "deathHook", prevent = true)
    public static void mobDieCallback(long attacker, long victim) {
        String str = "deathHook";
        Object[] objArr = new Object[AXIS_Z];
        if (attacker == -1) {
            attacker = -1;
        }
        objArr[ITEMID] = Long.valueOf(attacker);
        objArr[DAMAGE] = Long.valueOf(victim);
        callScriptMethod(str, objArr);
        if (worldData != null) {
            worldData.clearEntityData(victim);
        }
    }

    @CallbackName(args = {"entity"}, name = "entityRemovedHook")
    public static void entityRemovedCallback(long entity) {
        if (NativePlayerApi.isPlayer(Long.valueOf(entity))) {
            playerRemovedHandler(entity);
        }
        int entityIndex = allentities.indexOf(Long.valueOf(entity));
        if (entityIndex >= 0) {
            allentities.remove(entityIndex);
        }
        Object[] objArr = new Object[DAMAGE];
        objArr[ITEMID] = Long.valueOf(entity);
        callScriptMethod("entityRemovedHook", objArr);
    }

    @CallbackName(args = {"entity"}, name = "entityAddedHook")
    public static void entityAddedCallback(long entity) {
        System.out.println("Entity added: " + entity + " entity type: " + NativeEntityApi.getEntityTypeId(Long.valueOf(entity)));
        String renderType = NativeEntityApi.getExtraData(Long.valueOf(entity), ENTITY_KEY_RENDERTYPE);
        if (!(renderType == null || renderType.length() == 0)) {
            NativeRenderer renderer = NativeRendererApi.getByName(renderType);
            if (renderer != null) {
                NativeEntityApi.setRenderTypeImpl(Long.valueOf(entity), renderer.getRenderType());
            }
        }
        String customSkin = NativeEntityApi.getExtraData(Long.valueOf(entity), ENTITY_KEY_SKIN);
        if (!(customSkin == null || customSkin.length() == 0)) {
            System.out.println("Custom skin: " + customSkin);
            NativeEntityApi.setMobSkinImpl(Long.valueOf(entity), customSkin, false);
        }
        String immobile = NativeEntityApi.getExtraData(Long.valueOf(entity), ENTITY_KEY_IMMOBILE);
        if (!(immobile == null || immobile.length() == 0)) {
            System.out.println("Immobile: " + customSkin);
            NativeEntityApi.setImmobileImpl(Long.valueOf(entity), immobile.equals(XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION));
        }
        if (NativePlayerApi.isPlayer(Long.valueOf(entity))) {
            playerAddedHandler(entity);
        }
        allentities.add(Long.valueOf(entity));
        Object[] objArr = new Object[DAMAGE];
        objArr[ITEMID] = Long.valueOf(entity);
        callScriptMethod("entityAddedHook", objArr);
    }

    public static void levelEventCallback(int player, int eventType, int x, int y, int z, int data) {
        callScriptMethod("levelEventHook", Integer.valueOf(player), Integer.valueOf(eventType), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(data));
    }

    public static void blockEventCallback(int x, int y, int z, int type, int data) {
        Object[] objArr = new Object[MAX_NUM_ERRORS];
        objArr[ITEMID] = Integer.valueOf(x);
        objArr[DAMAGE] = Integer.valueOf(y);
        objArr[AXIS_Z] = Integer.valueOf(z);
        objArr[3] = Integer.valueOf(type);
        objArr[4] = Integer.valueOf(data);
        callScriptMethod("blockEventHook", objArr);
    }

    private static void rakNetConnectCallback(String hostname, int port) {
        Log.i(KamcordConstants.GAME_NAME, "Connecting to " + hostname + ":" + port);
        scriptingEnabled = isLocalAddress(hostname);
        Log.i(KamcordConstants.GAME_NAME, "Scripting is now " + (scriptingEnabled ? "enabled" : "disabled"));
        serverAddress = hostname;
        serverPort = port;
        isRemote = true;
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                if (!scriptingEnabled) {
                    modernWrapFactory.closePopups(main);
                }
                main.setLevelCallback(!scriptingEnabled);
            }
        }
    }

    public static void frameCallback() {
        if (requestReloadAllScripts) {
            if (nativeIsValidItem(EnchantType.flintAndSteel)) {
                requestReloadAllScripts = false;
                System.out.println("BlockLauncher is loading scripts");
                try {
                    if (!new File("/sdcard/mcpelauncher_do_not_create_placeholder_blocks").exists()) {
                        nativeDefinePlaceholderBlocks();
                    }
                    MobEffect.initIds();
                    loadEnabledScripts();
                } catch (Exception e) {
                    dumpScriptError(e);
                    reportScriptError(null, e);
                }
            } else {
                nativeRequestFrameCallback();
                return;
            }
        }
        if (!(requestSelectLevel == null || requestLeaveGame)) {
            if (requestSelectLevelHasSetScreen) {
                nativeSelectLevel(requestSelectLevel.dir, requestSelectLevel.name);
                requestSelectLevel = null;
                requestSelectLevelHasSetScreen = false;
            } else {
                nativeShowProgressScreen();
                requestSelectLevelHasSetScreen = true;
                nativeRequestFrameCallback();
            }
        }
        if (requestScreenshot) {
            ScreenshotHelper.takeScreenshot(screenshotFileName);
            requestScreenshot = false;
        }
    }

    @CallbackName(args = {"str"}, name = "serverMessageReceiveHook", prevent = true)
    public static void handleChatPacketCallback(String str) {
        if (str != null && str.length() >= DAMAGE) {
            Object[] objArr = new Object[DAMAGE];
            objArr[ITEMID] = str;
            callScriptMethod("serverMessageReceiveHook", objArr);
        }
    }

    @CallbackName(args = {"str", "sender"}, name = "chatReceiveHook", prevent = true)
    private static void handleMessagePacketCallback(String sender, String str) {
        if (str != null && str.length() >= DAMAGE) {
            if (sender.length() == 0 && str.equals("\u00a70BlockLauncher, enable scripts")) {
                scriptingEnabled = true;
                nativePreventDefault();
                if (MainActivity.currentMainActivity != null) {
                    MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                    if (main != null) {
                        main.scriptPrintCallback("Scripts have been re-enabled", BuildConfig.FLAVOR);
                    }
                }
            }
            Object[] objArr = new Object[AXIS_Z];
            objArr[ITEMID] = str;
            objArr[DAMAGE] = sender;
            callScriptMethod("chatReceiveHook", objArr);
        }
    }

    @CallbackName(args = {"entity", "x", "y", "z", "power", "onFire"}, name = "explodeHook", prevent = true)
    public static void explodeCallback(long entity, float x, float y, float z, float power, boolean onFire) {
        callScriptMethod("explodeHook", Long.valueOf(entity), Float.valueOf(x), Float.valueOf(y), Float.valueOf(z), Float.valueOf(power), Boolean.valueOf(onFire));
    }

    @CallbackName(args = {"hearts", "saturationRatio"}, name = "eatHook")
    public static void eatCallback(int hearts, float notHearts) {
        Object[] objArr = new Object[AXIS_Z];
        objArr[ITEMID] = Integer.valueOf(hearts);
        objArr[DAMAGE] = Float.valueOf(notHearts);
        callScriptMethod("eatHook", objArr);
    }

    @CallbackName(args = {"x", "y", "z", "newCurrent", "someBooleanIDontKnow", "blockId", "blockData"}, name = "redstoneUpdateHook")
    public static void redstoneUpdateCallback(int x, int y, int z, int newCurrent, boolean something, int blockId, int blockData) {
        callScriptMethod("redstoneUpdateHook", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(newCurrent), Boolean.valueOf(something), Integer.valueOf(blockId), Integer.valueOf(blockData));
    }

    @CallbackName(args = {"projectile", "blockX", "blockY", "blockZ", "side"}, name = "projectileHitBlockHook")
    public static void throwableHitCallback(long projectile, int type, int side, int x, int y, int z, float hX, float hY, float hZ, long targetEntity) {
        Integer customProjectileId = nativeGetEntityTypeId(projectile) == 81 ? (Integer) NativeItemApi.itemIdToRendererId.get(Integer.valueOf(nativeEntityGetRenderType(projectile))) : null;
        Object[] objArr;
        if (type == 0) {
            if (customProjectileId != null) {
                callScriptMethod("customThrowableHitBlockHook", Long.valueOf(projectile), Integer.valueOf(customProjectileId.intValue()), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), Integer.valueOf(side));
            }
            objArr = new Object[MAX_NUM_ERRORS];
            objArr[ITEMID] = Long.valueOf(projectile);
            objArr[DAMAGE] = Integer.valueOf(x);
            objArr[AXIS_Z] = Integer.valueOf(y);
            objArr[3] = Integer.valueOf(z);
            objArr[4] = Integer.valueOf(side);
            callScriptMethod("projectileHitBlockHook", objArr);
        } else if (type == DAMAGE) {
            if (customProjectileId != null) {
                callScriptMethod("customThrowableHitEntityHook", Long.valueOf(projectile), Integer.valueOf(customProjectileId.intValue()), Long.valueOf(targetEntity));
            }
            objArr = new Object[AXIS_Z];
            objArr[ITEMID] = Long.valueOf(projectile);
            objArr[DAMAGE] = Long.valueOf(targetEntity);
            callScriptMethod("projectileHitEntityHook", objArr);
        }
    }

    @CallbackName(args = {"projectile", "targetEntity"}, name = "projectileHitEntityHook")
    public static void dummyThrowableHitEntityCallback() {
    }

    @CallbackName(args = {"player", "experienceAdded"}, name = "playerAddExpHook", prevent = true)
    public static void playerAddExperienceCallback(long player, int experienceAdded) {
        Object[] objArr = new Object[AXIS_Z];
        objArr[ITEMID] = Long.valueOf(player);
        objArr[DAMAGE] = Integer.valueOf(experienceAdded);
        callScriptMethod("playerAddExpHook", objArr);
    }

    @CallbackName(args = {"player", "levelsAdded"}, name = "playerExpLevelChangeHook", prevent = true)
    public static void playerAddLevelsCallback(long player, int experienceAdded) {
        Object[] objArr = new Object[AXIS_Z];
        objArr[ITEMID] = Long.valueOf(player);
        objArr[DAMAGE] = Integer.valueOf(experienceAdded);
        callScriptMethod("playerExpLevelChangeHook", objArr);
    }

    @CallbackName(args = {"screenName"}, name = "screenChangeHook")
    public static void screenChangeCallback(String s1, String s2, String s3) {
        if ("options_screen".equals(s1) && "resource_packs_screen".equals(currentScreen)) {
            loadResourcePackScripts();
        }
        if ("store_screen".equals(s1)) {
            MainActivity activity = (MainActivity) MainActivity.currentMainActivity.get();
            if (activity != null) {
                activity.showStoreNotWorkingDialog();
            }
        }
        currentScreen = s1;
        Object[] objArr = new Object[DAMAGE];
        objArr[ITEMID] = s1;
        callScriptMethod("screenChangeHook", objArr);
    }

    public static InputStream getSoundInputStream(String name, long[] lengthout) {
        System.out.println("Get sound input stream");
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                return main.getInputStreamForAsset(name.substring("file:///android_asset/".length()), lengthout);
            }
        }
        return null;
    }

    public static byte[] getSoundBytes(String name) {
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                return main.getFileDataBytes(name.substring("file:///android_asset/".length()));
            }
        }
        return null;
    }

    public static void init(Context cxt) throws IOException {
        scriptingInitialized = true;
        MainActivity mainActivity = (MainActivity) MainActivity.currentMainActivity.get();
        if (mainActivity != null && mainActivity.getMCPEVersion().startsWith(MainActivity.SCRIPT_SUPPORT_VERSION)) {
            modPkgTexturePack = new ModPkgTexturePack("resourcepacks/vanilla/");
        }
        int versionCode = ITEMID;
        try {
            versionCode = cxt.getPackageManager().getPackageInfo("com.mojang.minecraftpe", ITEMID).versionCode;
        } catch (NameNotFoundException e) {
        }
        if (MinecraftVersion.isAmazon()) {
            versionCode = 43690;
        }
        nativeSetupHooks(versionCode);
        ITEM_ID_COUNT = nativeGetItemIdCount();
        scripts.clear();
        entityList = new NativeArray(0);
        androidContext = cxt.getApplicationContext();
        ContextFactory.initGlobal(new BlockContextFactory());
        NativeJavaMethod.setMethodWatcher(new MyMethodWatcher());
        requestReloadAllScripts = true;
        nativeRequestFrameCallback();
        prepareEnabledScripts();
    }

    public static void destroy() {
        scriptingInitialized = false;
        androidContext = null;
        scripts.clear();
        runOnMainThreadList.clear();
    }

    public static void removeScript(String scriptId) {
        for (int i = scripts.size() - 1; i >= 0; i--) {
            if (((ScriptState) scripts.get(i)).name.equals(scriptId)) {
                scripts.remove(i);
                break;
            }
        }
        if (isPackagedScript(scriptId)) {
            try {
                modPkgTexturePack.removePackage(scriptId);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void reloadScript(File file) throws IOException {
        removeScript(file.getName());
        loadScript(file, false);
    }

    public static void reportScriptError(ScriptState state, Throwable t) {
        if (state != null) {
            state.errors += DAMAGE;
        }
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                main.scriptErrorCallback(state == null ? "Unknown script" : state.name, t);
                if (state != null && state.errors >= MAX_NUM_ERRORS) {
                    main.scriptTooManyErrorsCallback(state.name);
                }
            }
        }
    }

    private static void scriptPrint(String str) {
        System.out.println(str);
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                main.scriptPrintCallback(str, currentScript);
            }
        }
    }

    private static void scriptFakeTipMessage(String str) {
        if (MainActivity.currentMainActivity != null) {
            MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
            if (main != null) {
                main.fakeTipMessageCallback(str);
            }
        }
    }

    public static void requestGraphicsReset() {
        requestedGraphicsReset = true;
    }

    public static Set<String> getEnabledScripts() {
        return enabledScripts;
    }

    private static void setEnabled(String name, boolean state) throws IOException {
        if (state) {
            reloadScript(getScriptFile(name));
            enabledScripts.add(name);
        } else {
            enabledScripts.remove(name);
            removeScript(name);
        }
        saveEnabledScripts();
    }

    private static void setEnabledWithoutLoad(String name, boolean state) throws IOException {
        if (state) {
            enabledScripts.add(name);
        } else {
            enabledScripts.remove(name);
        }
        saveEnabledScripts();
    }

    public static void setEnabled(File[] files, boolean state) throws IOException {
        File[] arr$ = files;
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            String name = arr$[i$].getAbsolutePath();
            if (name != null && name.length() > 0) {
                if (state) {
                    reloadScript(getScriptFile(name));
                    enabledScripts.add(name);
                } else {
                    enabledScripts.remove(name);
                    removeScript(name);
                }
            }
        }
        saveEnabledScripts();
    }

    public static void setEnabled(File file, boolean state) throws IOException {
        setEnabled(file.getName(), state);
    }

    public static void setEnabledWithoutLoad(File file, boolean state) throws IOException {
        setEnabledWithoutLoad(file.getName(), state);
    }

    private static boolean isEnabled(String name) {
        return enabledScripts.contains(name);
    }

    public static boolean isEnabled(File file) {
        return isEnabled(file.getName());
    }

    public static void removeDeadEntries(Collection<String> allPossibleFiles) {
        enabledScripts.retainAll(allPossibleFiles);
        saveEnabledScripts();
    }

    public static void loadEnabledScriptsNames(Context androidContext) {
        enabledScripts = Utils.getEnabledScripts();
    }

    protected static void loadEnabledScripts() throws IOException {
        for (String name : enabledScripts) {
            File file = getScriptFile(name);
            if (file.exists() && file.isFile()) {
                try {
                    loadScript(file, true);
                } catch (Exception e) {
                    dumpScriptError(e);
                    ((MainActivity) MainActivity.currentMainActivity.get()).reportError(e);
                }
            } else {
                Log.i(KamcordConstants.GAME_NAME, "ModPE script " + file.toString() + " doesn't exist");
            }
        }
        loadResourcePackScripts();
    }

    protected static void loadAddonScripts() {
        Reader theReader;
        Exception e;
        Throwable th;
        MainActivity mainActivity = (MainActivity) MainActivity.currentMainActivity.get();
        if (mainActivity != null && mainActivity.addonOverrideTexturePackInstance != null) {
            for (Entry<String, ZipFile> s : mainActivity.addonOverrideTexturePackInstance.getZipsByPackage().entrySet()) {
                theReader = null;
                try {
                    ZipFile zipFile = (ZipFile) s.getValue();
                    ZipEntry entry = zipFile.getEntry("assets/script/main.js");
                    if (entry != null) {
                        Reader theReader2 = new InputStreamReader(zipFile.getInputStream(entry));
                        try {
                            loadScript(theReader2, "Addon " + ((String) s.getKey()) + ":main.js");
                            if (theReader2 != null) {
                                try {
                                    theReader2.close();
                                    theReader = theReader2;
                                } catch (IOException ie) {
                                    ie.printStackTrace();
                                    theReader = theReader2;
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            theReader = theReader2;
                            try {
                                dumpScriptError(e);
                                mainActivity.reportError(e);
                                if (theReader != null) {
                                    try {
                                        theReader.close();
                                    } catch (IOException ie2) {
                                        ie2.printStackTrace();
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            theReader = theReader2;
                        }
                    } else if (theReader != null) {
                        try {
                            theReader.close();
                        } catch (IOException ie22) {
                            ie22.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    dumpScriptError(e);
                    mainActivity.reportError(e);
                    if (theReader != null) {
                        theReader.close();
                    }
                }
            }
            return;
        }
        return;
        if (theReader != null) {
            try {
                theReader.close();
            } catch (IOException ie222) {
                ie222.printStackTrace();
            }
        }
        throw th;
        throw th;
    }

    protected static void loadResourcePackScripts() {
        Reader theReader;
        Exception e;
        Throwable th;
        MainActivity mainActivity = (MainActivity) MainActivity.currentMainActivity.get();
        if (mainActivity != null) {
            List<ResourcePack> resourcePacks = ResourcePack.getAllResourcePacks();
            System.out.println(resourcePacks);
            for (int i = scripts.size() - 1; i >= 0; i--) {
                if (((ScriptState) scripts.get(i)).name.startsWith("__bl_ResourcePack_")) {
                    scripts.remove(i);
                    break;
                }
            }
            for (ResourcePack pack : resourcePacks) {
                theReader = null;
                try {
                    InputStream is = pack.getInputStream("main.js");
                    if (is != null) {
                        Reader theReader2 = new InputStreamReader(is);
                        try {
                            loadScript(theReader2, "__bl_ResourcePack_" + pack.getName() + "_main.js");
                            if (theReader2 != null) {
                                try {
                                    theReader2.close();
                                    theReader = theReader2;
                                } catch (IOException ie) {
                                    try {
                                        ie.printStackTrace();
                                        theReader = theReader2;
                                    } catch (IOException ie2) {
                                        ie2.printStackTrace();
                                        return;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            theReader = theReader2;
                            try {
                                dumpScriptError(e);
                                mainActivity.reportError(e);
                                if (theReader == null) {
                                    try {
                                        theReader.close();
                                    } catch (IOException ie22) {
                                        ie22.printStackTrace();
                                    }
                                } else {
                                    continue;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            theReader = theReader2;
                        }
                    } else if (theReader != null) {
                        try {
                            theReader.close();
                        } catch (IOException ie222) {
                            ie222.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                } catch (Exception e3) {
                    e = e3;
                    dumpScriptError(e);
                    mainActivity.reportError(e);
                    if (theReader == null) {
                        continue;
                    } else {
                        theReader.close();
                    }
                }
            }
            return;
        }
        return;
        throw th;
        if (theReader != null) {
            try {
                theReader.close();
            } catch (IOException ie2222) {
                ie2222.printStackTrace();
            }
        }
        throw th;
    }

    protected static void prepareEnabledScripts() throws IOException {
        loadEnabledScriptsNames(androidContext);
        boolean reimportEnabled = Utils.getPrefs(ITEMID).getBoolean("zz_reimport_scripts", false);
        StringBuilder reimportedString = new StringBuilder();
        for (String name : enabledScripts) {
            File file = getScriptFile(name);
            if (file.exists() && file.isFile()) {
                if (reimportEnabled) {
                    try {
                        if (reimportIfPossible(file)) {
                            reimportedString.append(file.getName()).append(' ');
                        }
                    } catch (Exception e) {
                        dumpScriptError(e);
                        ((MainActivity) MainActivity.currentMainActivity.get()).reportError(e);
                    }
                }
                prepareScript(file);
            } else {
                Log.i(KamcordConstants.GAME_NAME, "ModPE script " + file.toString() + " doesn't exist");
            }
        }
        if (reimportedString.length() != 0) {
            ((MainActivity) MainActivity.currentMainActivity.get()).reportReimported(reimportedString.toString());
        }
    }

    private static void prepareScript(File file) throws Exception {
        if (isPackagedScript(file)) {
            modPkgTexturePack.addPackage(file);
        }
    }

    protected static void saveEnabledScripts() {
        Editor edit = Utils.getPrefs(DAMAGE).edit();
        edit.putString("enabledScripts", PatchManager.join((String[]) enabledScripts.toArray(PatchManager.blankArray), ";"));
        edit.putInt("scriptManagerVersion", DAMAGE);
        edit.apply();
    }

    public static File getScriptFile(String scriptId) {
        return new File(androidContext.getDir(SCRIPTS_DIR, ITEMID), scriptId);
    }

    public static void setOriginalLocation(File source, File target) throws IOException {
        Editor edit = Utils.getPrefs(DAMAGE).edit();
        JSONObject originalLocations = getOriginalLocations();
        try {
            originalLocations.put(target.getName(), source.getAbsolutePath());
            edit.putString("scriptOriginalLocations", originalLocations.toString());
            edit.apply();
        } catch (JSONException jsonException) {
            throw new RuntimeException("Setting original location failed", jsonException);
        }
    }

    public static JSONObject getOriginalLocations() {
        try {
            return new JSONObject(Utils.getPrefs(DAMAGE).getString("scriptOriginalLocations", "{}"));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static File getOriginalFile(File curFile) {
        String originalLoc = getOriginalLocations().optString(curFile.getName(), null);
        if (originalLoc == null) {
            return null;
        }
        File originalFile = new File(originalLoc);
        if (originalFile.exists()) {
            return originalFile;
        }
        return null;
    }

    public static boolean reimportIfPossible(File curFile) throws IOException {
        File originalFile = getOriginalFile(curFile);
        if (originalFile == null || originalFile.lastModified() <= curFile.lastModified()) {
            return false;
        }
        PatchUtils.copy(originalFile, curFile);
        return true;
    }

    private static String[] getAllJsFunctions(Class<? extends ScriptableObject> clazz) {
        List<String> allList = new ArrayList();
        Method[] arr$ = clazz.getMethods();
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            Method met = arr$[i$];
            if (met.getAnnotation(JSFunction.class) != null) {
                allList.add(met.getName());
            }
        }
        return (String[]) allList.toArray(PatchManager.blankArray);
    }

    private static boolean invalidTexName(String tex) {
        return tex == null || tex.equals("undefined") || tex.equals("null");
    }

    private static boolean isValidStringParameter(String tex) {
        return !invalidTexName(tex);
    }

    private static void wordWrapClientMessage(String msg) {
        String[] portions = msg.split("\n");
        for (int i = ITEMID; i < portions.length; i += DAMAGE) {
            String line = portions[i];
            if (msg.indexOf(ChatColor.BEGIN) >= 0) {
                nativeClientMessage(line);
            } else {
                while (line.length() > 40) {
                    String newStr = line.substring(ITEMID, 40);
                    nativeClientMessage(newStr);
                    line = line.substring(newStr.length());
                }
                if (line.length() > 0) {
                    nativeClientMessage(line);
                }
            }
        }
    }

    public static String getAllApiMethodsDescriptions() {
        StringBuilder builder = new StringBuilder();
        appendApiMethods(builder, BlockHostObject.class, null);
        appendApiMethods(builder, NativeModPEApi.class, "ModPE");
        appendApiMethods(builder, NativeLevelApi.class, "Level");
        appendApiMethods(builder, NativePlayerApi.class, "Player");
        appendApiMethods(builder, NativeEntityApi.class, "Entity");
        appendApiMethods(builder, NativeItemApi.class, "Item");
        appendApiMethods(builder, NativeBlockApi.class, "Block");
        appendApiMethods(builder, NativeServerApi.class, "Server");
        appendCallbacks(builder);
        Class[] arr$ = constantsClasses;
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            appendApiClassConstants(builder, arr$[i$]);
        }
        return builder.toString();
    }

    private static void appendApiMethods(StringBuilder builder, Class<?> clazz, String namespace) {
        Method[] arr$ = clazz.getMethods();
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            Method met = arr$[i$];
            if (met.getAnnotation(JSFunction.class) != null || met.getAnnotation(JSStaticFunction.class) != null) {
                appendApiMethodDescription(builder, met, namespace);
            }
        }
        builder.append("\n");
    }

    private static void appendApiMethodDescription(StringBuilder builder, Method met, String namespace) {
        if (namespace != null) {
            builder.append(namespace);
            builder.append('.');
        }
        builder.append(met.getName());
        builder.append('(');
        Class<?>[] params = met.getParameterTypes();
        for (int i = ITEMID; i < params.length; i += DAMAGE) {
            builder.append("par");
            builder.append(i + DAMAGE);
            builder.append(params[i].getSimpleName().replaceAll("Native", BuildConfig.FLAVOR));
            if (i < params.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(");\n");
    }

    private static void appendApiClassConstants(StringBuilder builder, Class<?> clazz) {
        String className = clazz.getSimpleName();
        Field[] arr$ = clazz.getFields();
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            Field field = arr$[i$];
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers) && Modifier.isPublic(fieldModifiers)) {
                builder.append(className).append(".").append(field.getName()).append(";\n");
            }
        }
        builder.append("\n");
    }

    private static void appendCallbacks(StringBuilder builder) {
        Method[] arr$ = ScriptManager.class.getMethods();
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            CallbackName name = (CallbackName) arr$[i$].getAnnotation(CallbackName.class);
            if (name != null) {
                if (name.prevent()) {
                    builder.append("// can use preventDefault()\n");
                }
                builder.append("function ").append(name.name()).append("(").append(Utils.joinArray(name.args(), ", ")).append(")\n");
            }
        }
        builder.append("\n");
    }

    private static boolean isLocalAddress(String str) {
        try {
            InetAddress address = InetAddress.getByName(str);
            Log.i(KamcordConstants.GAME_NAME, str);
            if (address.isLoopbackAddress() || address.isLinkLocalAddress() || address.isSiteLocalAddress()) {
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void takeScreenshot(String fileName) {
        screenshotFileName = fileName.replace("/", BuildConfig.FLAVOR).replace("\\", BuildConfig.FLAVOR);
        requestScreenshot = true;
        nativeRequestFrameCallback();
    }

    private static void overrideTexture(String urlString, String textureName) {
        if (androidContext != null) {
            if (textureName.contains("terrain-atlas.tga") || textureName.contains("items-opaque.png")) {
                scriptPrint("cannot override " + textureName);
            } else if (urlString == BuildConfig.FLAVOR) {
                clearTextureOverride(textureName);
            } else {
                try {
                    new Thread(new ScriptTextureDownloader(new URL(urlString), getTextureOverrideFile(textureName))).start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static File getTextureOverrideFile(String textureName) {
        if (androidContext == null) {
            return null;
        }
        if (externalFilesDir == null) {
            externalFilesDir = androidContext.getExternalFilesDir(null);
        }
        return new File(new File(externalFilesDir, "textures"), textureName.replace("..", BuildConfig.FLAVOR));
    }

    public static void clearTextureOverrides() {
        if (androidContext != null) {
            Utils.clearDirectory(new File(androidContext.getExternalFilesDir(null), "textures"));
            requestedGraphicsReset = true;
        }
    }

    private static void clearTextureOverride(String texture) {
        File file = getTextureOverrideFile(texture);
        if (file != null && file.exists()) {
            file.delete();
        }
        requestedGraphicsReset = true;
    }

    public static ScriptableObject classConstantsToJSObject(Class<?> clazz) {
        ScriptableObject obj = new NativeObject();
        Field[] arr$ = clazz.getFields();
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            Field field = arr$[i$];
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers) && Modifier.isPublic(fieldModifiers)) {
                try {
                    obj.putConst(field.getName(), obj, field.get(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    public static void setupContext(org.mozilla.javascript.Context ctx) {
        ctx.setOptimizationLevel(-1);
        ctx.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
    }

    public static TextureRequests expandTexturesArray(Object inArrayObj) {
        int[] endArray = new int[96];
        String[] stringArray = new String[96];
        TextureRequests retval = new TextureRequests();
        retval.coords = endArray;
        retval.names = stringArray;
        if (inArrayObj instanceof String) {
            Arrays.fill(stringArray, (String) inArrayObj);
        } else {
            Scriptable inArrayScriptable = (Scriptable) inArrayObj;
            int inArrayLength = ((Number) ScriptableObject.getProperty(inArrayScriptable, Name.LENGTH)).intValue();
            int wrap = inArrayLength % 6 == 0 ? 6 : DAMAGE;
            Object firstObj = ScriptableObject.getProperty(inArrayScriptable, ITEMID);
            if ((inArrayLength == DAMAGE || inArrayLength == AXIS_Z) && (firstObj instanceof String)) {
                Arrays.fill(stringArray, (String) firstObj);
                if (inArrayLength == AXIS_Z) {
                    Arrays.fill(endArray, ((Number) ScriptableObject.getProperty(inArrayScriptable, DAMAGE)).intValue());
                }
            } else {
                for (int i = ITEMID; i < endArray.length; i += DAMAGE) {
                    Scriptable myObj;
                    if (i < inArrayLength) {
                        myObj = ScriptableObject.getProperty(inArrayScriptable, i);
                    } else {
                        myObj = ScriptableObject.getProperty(inArrayScriptable, i % wrap);
                    }
                    Scriptable myScriptable = myObj;
                    String texName = (String) ScriptableObject.getProperty(myScriptable, ITEMID);
                    int texCoord = ITEMID;
                    if (((Number) ScriptableObject.getProperty(myScriptable, Name.LENGTH)).intValue() > DAMAGE) {
                        texCoord = ((Number) ScriptableObject.getProperty(myScriptable, DAMAGE)).intValue();
                    }
                    endArray[i] = texCoord;
                    stringArray[i] = texName;
                }
            }
        }
        return retval;
    }

    public static TextureRequests mapTextureNames(TextureRequests requests) {
        for (int i = ITEMID; i < requests.coords.length; i += DAMAGE) {
            String name = requests.names[i];
            if (name.equals("stonecutter")) {
                requests.names[i] = new String[]{"stonecutter_side", "stonecutter_other_side", "stonecutter_top", "stonecutter_bottom"}[requests.coords[i] % 4];
                requests.coords[i] = ITEMID;
            } else if (name.equals("piston_inner")) {
                requests.names[i] = "piston_top";
            }
        }
        return requests;
    }

    public static int[] expandColorsArray(Scriptable inArrayScriptable) {
        int inArrayLength = ((Number) ScriptableObject.getProperty(inArrayScriptable, Name.LENGTH)).intValue();
        int[] endArray = new int[16];
        for (int i = ITEMID; i < endArray.length; i += DAMAGE) {
            if (i < inArrayLength) {
                endArray[i] = (int) ((Number) ScriptableObject.getProperty(inArrayScriptable, i)).longValue();
            } else {
                endArray[i] = (int) ((Number) ScriptableObject.getProperty(inArrayScriptable, (int) ITEMID)).longValue();
            }
        }
        Log.i(KamcordConstants.GAME_NAME, Arrays.toString(endArray));
        return endArray;
    }

    public static void processDebugCommand(String cmd) {
        try {
            if (cmd.equals("dumpitems")) {
                debugDumpItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void debugDumpItems() throws IOException {
        PrintWriter out = new PrintWriter(new File(Environment.getExternalStorageDirectory(), "/items.csv"));
        textureUVbuf = new float[6];
        int[][] bonuses = new int[][]{new int[]{DAMAGE, DAMAGE, 6}, new int[]{12, DAMAGE, DAMAGE}, new int[]{38, ITEMID, 8}, new int[]{Token.LETEXPR, ITEMID, 15}, new int[]{171, ITEMID, 15}, new int[]{175, ITEMID, MAX_NUM_ERRORS}, new int[]{349, DAMAGE, 3}, new int[]{350, DAMAGE, DAMAGE}, new int[]{383, 10, 63}};
        for (int i = ITEMID; i < ITEM_ID_COUNT; i += DAMAGE) {
            String itemName = nativeGetItemName(i, ITEMID, true);
            if (itemName != null) {
                boolean success = nativeGetTextureCoordinatesForItem(i, ITEMID, textureUVbuf);
                out.println(i + "," + itemName + "," + Arrays.toString(textureUVbuf).replace("[", BuildConfig.FLAVOR).replace("]", BuildConfig.FLAVOR).replace(",", "|"));
            }
        }
        int[][] arr$ = bonuses;
        int len$ = arr$.length;
        for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
            int[] bonus = arr$[i$];
            int id = bonus[ITEMID];
            for (int dmg = bonus[DAMAGE]; dmg <= bonus[AXIS_Z]; dmg += DAMAGE) {
                itemName = nativeGetItemName(id, dmg, true);
                if (itemName != null) {
                    success = nativeGetTextureCoordinatesForItem(id, dmg, textureUVbuf);
                    out.println(id + ":" + dmg + "," + itemName + "," + Arrays.toString(textureUVbuf).replace("[", BuildConfig.FLAVOR).replace("]", BuildConfig.FLAVOR).replace(",", "|"));
                }
            }
        }
        out.close();
    }

    private static void playerAddedHandler(long entityId) {
        allplayers.add(Long.valueOf(entityId));
        if (shouldLoadSkin()) {
            runOnMainThread(new SkinLoader(entityId));
        }
    }

    private static String getSkinURL(String name) {
        return "http://blskins.ablecuboid.com/blskins/" + name + DownloadProfileImageTask.UserTileExtension;
    }

    private static String getCapeURL(String name) {
        return "http://blskins.ablecuboid.com/blskins/capes/" + name + DownloadProfileImageTask.UserTileExtension;
    }

    private static boolean isSkinNameNormalized() {
        return true;
    }

    private static void playerRemovedHandler(long entityId) {
        int entityIndex = allplayers.indexOf(Long.valueOf(entityId));
        if (entityIndex >= 0) {
            allplayers.remove(entityIndex);
        }
    }

    public static void runOnMainThread(Runnable run) {
        synchronized (runOnMainThreadList) {
            runOnMainThreadList.add(run);
        }
    }

    private static boolean shouldLoadSkin() {
        return false;
    }

    private static boolean isClassGenMode() {
        return false;
    }

    private static int[] expandShapelessRecipe(Scriptable inArrayScriptable) {
        int inArrayLength = ((Number) ScriptableObject.getProperty(inArrayScriptable, Name.LENGTH)).intValue();
        if (!(ScriptableObject.getProperty(inArrayScriptable, (int) ITEMID) instanceof Number)) {
            throw new IllegalArgumentException("Method takes in an array of [itemid, itemCount, itemdamage, ...]");
        } else if (inArrayLength % 3 != 0) {
            throw new IllegalArgumentException("Array length must be multiple of 3 (this was changed in 1.6.8): [itemid, itemCount, itemdamage, ...]");
        } else {
            int[] endArray = new int[inArrayLength];
            for (int i = ITEMID; i < endArray.length; i += DAMAGE) {
                endArray[i] = ((Number) ScriptableObject.getProperty(inArrayScriptable, i)).intValue();
            }
            return endArray;
        }
    }

    private static void nameAndShame(String str) {
    }

    private static String getEntityUUID(long entityId) {
        return Long.toString(entityId);
    }

    private static boolean isPackagedScript(File file) {
        return isPackagedScript(file.getName());
    }

    private static boolean isPackagedScript(String str) {
        return str.toLowerCase().endsWith(".modpkg");
    }

    private static void loadPackagedScript(File file, boolean firstLoad) throws IOException {
        Reader reader;
        Throwable th;
        if (!firstLoad) {
            modPkgTexturePack.addPackage(file);
        }
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(file);
            MpepInfo info = null;
            boolean scrambled = false;
            try {
                info = MpepInfo.fromZip(zipFile2);
                scrambled = info != null && info.scrambleCode.length() > 0;
            } catch (JSONException e) {
            }
            try {
                Enumeration<? extends ZipEntry> entries = zipFile2.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    reader = null;
                    String name = entry.getName();
                    if (name.startsWith("script/") && name.toLowerCase().endsWith(".js")) {
                        if (scrambled) {
                            InputStream is = zipFile2.getInputStream(entry);
                            byte[] scrambleBytes = new byte[((int) entry.getSize())];
                            is.read(scrambleBytes);
                            is.close();
                            reader = Scrambler.scramble(scrambleBytes, info);
                        } else {
                            reader = new InputStreamReader(zipFile2.getInputStream(entry));
                        }
                        loadScript(reader, file.getName());
                        if (reader != null) {
                            reader.close();
                        }
                        if (zipFile2 != null) {
                            zipFile2.close();
                        }
                        if (!firstLoad && !true) {
                            modPkgTexturePack.removePackage(file.getName());
                            return;
                        }
                    }
                }
                if (zipFile2 != null) {
                    zipFile2.close();
                }
                if (!firstLoad) {
                }
            } catch (Throwable th2) {
                th = th2;
                zipFile = zipFile2;
                if (zipFile != null) {
                    zipFile.close();
                }
                modPkgTexturePack.removePackage(file.getName());
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (zipFile != null) {
                zipFile.close();
            }
            if (!(firstLoad || false)) {
                modPkgTexturePack.removePackage(file.getName());
            }
            throw th;
        }
    }

    private static void verifyBlockTextures(TextureRequests requests) {
        if (terrainMeta != null) {
            int i = ITEMID;
            while (i < requests.names.length) {
                if (terrainMeta.hasIcon(requests.names[i], requests.coords[i])) {
                    i += DAMAGE;
                } else {
                    throw new MissingTextureException("The requested block texture " + requests.names[i] + ":" + requests.coords[i] + " does not exist");
                }
            }
        }
    }

    private static void setRequestLeaveGame() {
        nativeCloseScreen();
        requestLeaveGame = true;
        requestLeaveGameCounter = 10;
    }

    private static long getEntityId(Object entityId) {
        if (entityId == null) {
            return -1;
        }
        if (entityId instanceof NativeJavaObject) {
            return ((Long) ((NativeJavaObject) entityId).unwrap()).longValue();
        }
        if (entityId instanceof Number) {
            return ((Number) entityId).longValue();
        }
        if (entityId instanceof Undefined) {
            return 0;
        }
        throw new RuntimeException("Not an entity: " + entityId + " (" + entityId.getClass().toString() + ")");
    }

    private static void injectKeyEvent(final int key, final int pressed) {
        if (instrumentation == null) {
            instrumentation = new Instrumentation();
            instrumentationExecutor = Executors.newSingleThreadExecutor();
        }
        instrumentationExecutor.execute(new Runnable() {
            public void run() {
                ScriptManager.instrumentation.sendKeySync(new KeyEvent(pressed != 0 ? ScriptManager.ITEMID : ScriptManager.DAMAGE, key));
            }
        });
    }

    private static long spawnEntityImpl(float x, float y, float z, int entityType, String skinPath) {
        if (entityType <= 0) {
            throw new RuntimeException("Invalid entity type: " + entityType);
        }
        if (skinPath != null) {
            String newSkinPath = (String) OldEntityTextureFilenameMapping.m.get(skinPath);
            if (newSkinPath != null) {
                skinPath = newSkinPath;
            }
            if (skinPath.endsWith(DownloadProfileImageTask.UserTileExtension) || skinPath.endsWith(".tga")) {
                skinPath = skinPath.substring(ITEMID, skinPath.length() - 4);
            }
        }
        long retval = nativeSpawnEntity(x, y, z, entityType, skinPath);
        if (nativeEntityHasCustomSkin(retval)) {
            NativeEntityApi.setExtraData(Long.valueOf(retval), ENTITY_KEY_SKIN, skinPath);
        }
        return retval;
    }

    protected static ModernWrapFactory getWrapFactory() {
        return modernWrapFactory;
    }

    protected static boolean isScriptingEnabled() {
        return scriptingEnabled;
    }

    private static String getPlayerNameFromConfs() {
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt");
            if (!f.exists()) {
                return "Steve";
            }
            byte[] fileBytes = new byte[((int) f.length())];
            FileInputStream fis = new FileInputStream(f);
            fis.read(fileBytes);
            fis.close();
            String[] arr$ = new String(fileBytes, HttpURLConnectionBuilder.DEFAULT_CHARSET).split("\n");
            int len$ = arr$.length;
            for (int i$ = ITEMID; i$ < len$; i$ += DAMAGE) {
                String s = arr$[i$];
                if (s.startsWith("mp_username:")) {
                    return s.substring("mp_username:".length());
                }
            }
            return "Steve";
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    private static void dumpScriptError(Throwable t) {
        if (scriptErrorStream == null) {
            scriptErrorStream = new AndroidPrintStream(6, "ScriptError");
        }
        t.printStackTrace(scriptErrorStream);
    }

    private static boolean assetFileExists(String path) {
        MainActivity activity = (MainActivity) MainActivity.currentMainActivity.get();
        if (activity == null) {
            return false;
        }
        return activity.existsForPath(path);
    }

    private static String[] assetListDir(String path) {
        MainActivity activity = (MainActivity) MainActivity.currentMainActivity.get();
        return activity == null ? null : activity.listDirForPath(path);
    }

    public static int nativeGetTileWrap(int x, int y, int z) {
        int tile = nativeGetTile(x, y, z);
        if (tile == 245) {
            int extraData = nativeLevelGetExtraData(x, y, z);
            if (extraData != 0) {
                return extraData;
            }
        }
        return tile;
    }

    public static void doUseItemOurselves(int x, int y, int z, int itemId, int blockId, int side, int itemDamage, int blockDamage) {
        int blockX = x + useItemSideOffsets[side * 3];
        int blockY = y + useItemSideOffsets[(side * 3) + DAMAGE];
        int blockZ = z + useItemSideOffsets[(side * 3) + AXIS_Z];
        if (NativeLevelApi.getTile(blockX, blockY, blockZ) == 0) {
            NativeLevelApi.setTile(blockX, blockY, blockZ, itemId, itemDamage);
        }
    }
}
