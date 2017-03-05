package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.metrics.JsonHelper;
import org.mozilla.javascript.Token;

public class Session implements Serializable, IJsonSerializable {
    private String id;
    private String isFirst;
    private String isNew;

    public Session() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public void addToHashMap(Map<String, String> map) {
        if (this.id != null) {
            map.put("ai.session.id", this.id);
        }
        if (this.isFirst != null) {
            map.put("ai.session.isFirst", this.isFirst);
        }
        if (this.isNew != null) {
            map.put("ai.session.isNew", this.isNew);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getIsFirst() {
        return this.isFirst;
    }

    public String getIsNew() {
        return this.isNew;
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
        if (this.id != null) {
            writer.write(str + "\"ai.session.id\":");
            writer.write(JsonHelper.convert(this.id));
            str = ",";
        }
        if (this.isFirst != null) {
            writer.write(str + "\"ai.session.isFirst\":");
            writer.write(JsonHelper.convert(this.isFirst));
            str = ",";
        }
        if (this.isNew == null) {
            return str;
        }
        writer.write(str + "\"ai.session.isNew\":");
        writer.write(JsonHelper.convert(this.isNew));
        return ",";
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setIsFirst(String str) {
        this.isFirst = str;
    }

    public void setIsNew(String str) {
        this.isNew = str;
    }
}
