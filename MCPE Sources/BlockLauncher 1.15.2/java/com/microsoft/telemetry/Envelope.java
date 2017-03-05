package com.microsoft.telemetry;

import com.microsoft.cll.android.EventEnums;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class Envelope implements IJsonSerializable {
    private String appId;
    private String appVer;
    private String cV;
    private Base data;
    private String epoch;
    private Map<String, Extension> ext;
    private long flags;
    private String iKey;
    private String name;
    private String os;
    private String osVer;
    private double popSample = EventEnums.SampleRate_NoSampling;
    private long seqNum;
    private Map<String, String> tags;
    private String time;
    private String ver;

    public Envelope() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getAppId() {
        return this.appId;
    }

    public String getAppVer() {
        return this.appVer;
    }

    public String getCV() {
        return this.cV;
    }

    public Base getData() {
        return this.data;
    }

    public String getEpoch() {
        return this.epoch;
    }

    public Map<String, Extension> getExt() {
        if (this.ext == null) {
            this.ext = new LinkedHashMap();
        }
        return this.ext;
    }

    public long getFlags() {
        return this.flags;
    }

    public String getIKey() {
        return this.iKey;
    }

    public String getName() {
        return this.name;
    }

    public String getOs() {
        return this.os;
    }

    public String getOsVer() {
        return this.osVer;
    }

    public double getPopSample() {
        return this.popSample;
    }

    public long getSeqNum() {
        return this.seqNum;
    }

    public Map<String, String> getTags() {
        if (this.tags == null) {
            this.tags = new LinkedHashMap();
        }
        return this.tags;
    }

    public String getTime() {
        return this.time;
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
        writer.write(BuildConfig.FLAVOR + "\"ver\":");
        writer.write(JsonHelper.convert(this.ver));
        writer.write("," + "\"name\":");
        writer.write(JsonHelper.convert(this.name));
        writer.write("," + "\"time\":");
        writer.write(JsonHelper.convert(this.time));
        String str = ",";
        if (this.popSample > 0.0d) {
            writer.write(str + "\"popSample\":");
            writer.write(JsonHelper.convert(Double.valueOf(this.popSample)));
            str = ",";
        }
        if (this.epoch != null) {
            writer.write(str + "\"epoch\":");
            writer.write(JsonHelper.convert(this.epoch));
            str = ",";
        }
        if (this.seqNum != 0) {
            writer.write(str + "\"seqNum\":");
            writer.write(JsonHelper.convert(Long.valueOf(this.seqNum)));
            str = ",";
        }
        if (this.iKey != null) {
            writer.write(str + "\"iKey\":");
            writer.write(JsonHelper.convert(this.iKey));
            str = ",";
        }
        if (this.flags != 0) {
            writer.write(str + "\"flags\":");
            writer.write(JsonHelper.convert(Long.valueOf(this.flags)));
            str = ",";
        }
        if (this.os != null) {
            writer.write(str + "\"os\":");
            writer.write(JsonHelper.convert(this.os));
            str = ",";
        }
        if (this.osVer != null) {
            writer.write(str + "\"osVer\":");
            writer.write(JsonHelper.convert(this.osVer));
            str = ",";
        }
        if (this.appId != null) {
            writer.write(str + "\"appId\":");
            writer.write(JsonHelper.convert(this.appId));
            str = ",";
        }
        if (this.appVer != null) {
            writer.write(str + "\"appVer\":");
            writer.write(JsonHelper.convert(this.appVer));
            str = ",";
        }
        if (this.cV != null) {
            writer.write(str + "\"cV\":");
            writer.write(JsonHelper.convert(this.cV));
            str = ",";
        }
        if (this.tags != null) {
            writer.write(str + "\"tags\":");
            JsonHelper.writeDictionary(writer, this.tags);
            str = ",";
        }
        if (this.ext != null) {
            writer.write(str + "\"ext\":");
            JsonHelper.writeDictionary(writer, this.ext);
            str = ",";
        }
        if (this.data == null) {
            return str;
        }
        writer.write(str + "\"data\":");
        JsonHelper.writeJsonSerializable(writer, this.data);
        return ",";
    }

    public void setAppId(String str) {
        this.appId = str;
    }

    public void setAppVer(String str) {
        this.appVer = str;
    }

    public void setCV(String str) {
        this.cV = str;
    }

    public void setData(Base base) {
        this.data = base;
    }

    public void setEpoch(String str) {
        this.epoch = str;
    }

    public void setExt(Map<String, Extension> map) {
        this.ext = map;
    }

    public void setFlags(long j) {
        this.flags = j;
    }

    public void setIKey(String str) {
        this.iKey = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setOs(String str) {
        this.os = str;
    }

    public void setOsVer(String str) {
        this.osVer = str;
    }

    public void setPopSample(double d) {
        this.popSample = d;
    }

    public void setSeqNum(long j) {
        this.seqNum = j;
    }

    public void setTags(Map<String, String> map) {
        this.tags = map;
    }

    public void setTime(String str) {
        this.time = str;
    }

    public void setVer(String str) {
        this.ver = str;
    }
}
