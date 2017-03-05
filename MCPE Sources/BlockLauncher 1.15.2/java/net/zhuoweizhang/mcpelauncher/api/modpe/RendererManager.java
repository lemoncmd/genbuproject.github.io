package net.zhuoweizhang.mcpelauncher.api.modpe;

import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class RendererManager {

    public static class NativeModel {
        private final int rendererId;

        private NativeModel(int rendererId) {
            this.rendererId = rendererId;
        }

        public NativeModelPart getPart(String name) {
            if (RendererManager.nativeModelPartExists(this.rendererId, name)) {
                return new NativeModelPart(this.rendererId, name);
            }
            throw new RuntimeException("The model part " + name + " does not exist.");
        }
    }

    public static class NativeModelPart {
        private String modelPartName;
        private int rendererId;
        private float textureHeight;
        private float textureWidth;
        private int textureX;
        private int textureY;
        private boolean transparent;

        private NativeModelPart(int rendererId, String modelPartName) {
            this.textureWidth = 64.0f;
            this.textureHeight = AccountHeaderView.SizeLogoDip;
            this.rendererId = rendererId;
            this.modelPartName = modelPartName;
        }

        public NativeModelPart setTextureOffset(int textureX, int textureY) {
            return setTextureOffset(textureX, textureY, false);
        }

        public NativeModelPart setTextureOffset(int textureX, int textureY, boolean transparent) {
            this.textureX = textureX;
            this.textureY = textureY;
            this.transparent = transparent;
            return this;
        }

        public void addBox(float xOffset, float yOffset, float zOffset, int width, int height, int depth) {
            addBox(xOffset, yOffset, zOffset, width, height, depth, 0.0f);
        }

        public void addBox(float xOffset, float yOffset, float zOffset, int width, int height, int depth, float scale) {
            RendererManager.nativeModelAddBox(this.rendererId, this.modelPartName, xOffset, yOffset, zOffset, width, height, depth, scale, this.textureX, this.textureY, this.transparent, this.textureWidth, this.textureHeight);
        }

        public NativeModelPart clear() {
            RendererManager.nativeModelClear(this.rendererId, this.modelPartName);
            return this;
        }

        public NativeModelPart setTextureSize(float width, float height) {
            this.textureWidth = width;
            this.textureHeight = height;
            return this;
        }

        public NativeModelPart setRotationPoint(float x, float y, float z) {
            RendererManager.nativeModelSetRotationPoint(this.rendererId, this.modelPartName, x, y, z);
            return this;
        }
    }

    public static class NativeRenderer {
        private String name = null;
        private final int rendererId;

        public NativeRenderer(int id) {
            this.rendererId = id;
        }

        public int getRenderType() {
            return this.rendererId;
        }

        public NativeModel getModel() {
            return new NativeModel(this.rendererId);
        }

        public void setName(String name) {
            if (name.indexOf(".") == -1) {
                throw new RuntimeException("Renderer name must be in format of author.modname.name; for example, coolmcpemodder.sz.SwagYolo");
            }
            this.name = name;
            NativeRendererApi.register(name, this);
        }

        public String getName() {
            return this.name;
        }
    }

    public static class NativeRendererApi extends ScriptableObject {
        public static Map<Integer, NativeRenderer> renderersById = new HashMap();
        public static Map<String, NativeRenderer> renderersByName = new HashMap();

        @JSStaticFunction
        public static NativeRenderer get(String name) {
            try {
                return new NativeRenderer(Integer.parseInt(name));
            } catch (NumberFormatException e) {
                return (NativeRenderer) renderersByName.get(name);
            }
        }

        public static NativeRenderer getById(int id) {
            return (NativeRenderer) renderersById.get(Integer.valueOf(id));
        }

        public static NativeRenderer getByName(String name) {
            return (NativeRenderer) renderersByName.get(name);
        }

        @JSStaticFunction
        public static NativeRenderer createHumanoidRenderer() {
            return new NativeRenderer(RendererManager.nativeCreateHumanoidRenderer());
        }

        public static void register(String name, NativeRenderer renderer) {
            renderersByName.put(name, renderer);
            renderersById.put(Integer.valueOf(renderer.getRenderType()), renderer);
        }

        public String getClassName() {
            return "Renderer";
        }
    }

    private static native int nativeCreateHumanoidRenderer();

    public static native int nativeCreateItemSpriteRenderer(int i);

    private static native void nativeModelAddBox(int i, String str, float f, float f2, float f3, int i2, int i3, int i4, float f4, int i5, int i6, boolean z, float f5, float f6);

    private static native void nativeModelClear(int i, String str);

    private static native boolean nativeModelPartExists(int i, String str);

    public static native void nativeModelSetRotationPoint(int i, String str, float f, float f2, float f3);

    public static void defineClasses(Scriptable scope) throws Exception {
        ScriptableObject.defineClass(scope, NativeRendererApi.class);
    }
}
