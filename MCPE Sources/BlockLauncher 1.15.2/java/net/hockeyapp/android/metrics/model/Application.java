package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.metrics.JsonHelper;
import org.mozilla.javascript.Token;

public class Application implements Serializable, IJsonSerializable {
    private String build;
    private String typeId;
    private String ver;

    public Application() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public void addToHashMap(Map<String, String> map) {
        if (this.ver != null) {
            map.put("ai.application.ver", this.ver);
        }
        if (this.build != null) {
            map.put("ai.application.build", this.build);
        }
        if (this.typeId != null) {
            map.put("ai.application.typeId", this.typeId);
        }
    }

    public String getBuild() {
        return this.build;
    }

    public String getTypeId() {
        return this.typeId;
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
        if (this.ver != null) {
            writer.write(str + "\"ai.application.ver\":");
            writer.write(JsonHelper.convert(this.ver));
            str = ",";
        }
        if (this.build != null) {
            writer.write(str + "\"ai.application.build\":");
            writer.write(JsonHelper.convert(this.build));
            str = ",";
        }
        if (this.typeId == null) {
            return str;
        }
        writer.write(str + "\"ai.application.typeId\":");
        writer.write(JsonHelper.convert(this.typeId));
        return ",";
    }

    public void setBuild(String str) {
        this.build = str;
    }

    public void setTypeId(String str) {
        this.typeId = str;
    }

    public void setVer(String str) {
        this.ver = str;
    }
}
