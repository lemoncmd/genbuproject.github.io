package net.zhuoweizhang.mcpelauncher.texture;

import android.graphics.Bitmap;
import net.hockeyapp.android.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;

public final class TexturePackDescription {
    public String description = BuildConfig.FLAVOR;
    public Bitmap img = null;
    public String path;
    public String type;

    public TexturePackDescription(String type, String path) {
        this.type = type;
        this.path = path;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("t", this.type).put("p", this.path);
    }

    public String toString() {
        return this.type + ":" + this.path;
    }

    public static TexturePackDescription fromJson(JSONObject obj) throws JSONException {
        return new TexturePackDescription(obj.getString("t"), obj.getString("p"));
    }
}
