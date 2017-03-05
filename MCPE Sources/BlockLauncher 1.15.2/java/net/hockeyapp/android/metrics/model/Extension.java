package net.hockeyapp.android.metrics.model;

import com.mojang.minecraftpe.MainActivity;
import java.io.IOException;
import java.io.Writer;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.metrics.JsonHelper;
import org.mozilla.javascript.Token;

public class Extension implements IJsonSerializable {
    private String ver = MainActivity.HALF_SUPPORT_VERSION;

    public Extension() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getVer() {
        return this.ver;
    }

    public void serialize(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer");
        }
        writer.write(Token.VAR);
        serializeContent(writer);
        writer.write(Token.CATCH);
    }

    protected String serializeContent(Writer writer) throws IOException {
        String str = BuildConfig.FLAVOR;
        if (this.ver == null) {
            return str;
        }
        writer.write(str + "\"ver\":");
        writer.write(JsonHelper.convert(this.ver));
        return ",";
    }

    public void setVer(String str) {
        this.ver = str;
    }
}
