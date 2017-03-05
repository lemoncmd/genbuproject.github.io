package com.microsoft.telemetry.cs2;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class Envelope implements IJsonSerializable {
    private String appId;
    private String appVer;
    private Base data;
    private String deviceId;
    private long flags;
    private String iKey;
    private String name;
    private String os;
    private String osVer;
    private double sampleRate = EventEnums.SampleRate_NoSampling;
    private String seq;
    private Map<String, String> tags;
    private String time;
    private String userId;
    private int ver = 1;

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

    public Base getData() {
        return this.data;
    }

    public String getDeviceId() {
        return this.deviceId;
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

    public double getSampleRate() {
        return this.sampleRate;
    }

    public String getSeq() {
        return this.seq;
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

    public String getUserId() {
        return this.userId;
    }

    public int getVer() {
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
        if (this.ver != 0) {
            writer.write(str + "\"ver\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.ver)));
            str = ",";
        }
        writer.write(str + "\"name\":");
        writer.write(JsonHelper.convert(this.name));
        writer.write("," + "\"time\":");
        writer.write(JsonHelper.convert(this.time));
        str = ",";
        if (this.sampleRate > 0.0d) {
            writer.write(str + "\"sampleRate\":");
            writer.write(JsonHelper.convert(Double.valueOf(this.sampleRate)));
            str = ",";
        }
        if (this.seq != null) {
            writer.write(str + "\"seq\":");
            writer.write(JsonHelper.convert(this.seq));
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
        if (this.deviceId != null) {
            writer.write(str + "\"deviceId\":");
            writer.write(JsonHelper.convert(this.deviceId));
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
        if (this.userId != null) {
            writer.write(str + "\"userId\":");
            writer.write(JsonHelper.convert(this.userId));
            str = ",";
        }
        if (this.tags != null) {
            writer.write(str + "\"tags\":");
            JsonHelper.writeDictionary(writer, this.tags);
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

    public void setData(Base base) {
        this.data = base;
    }

    public void setDeviceId(String str) {
        this.deviceId = str;
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

    public void setSampleRate(double d) {
        this.sampleRate = d;
    }

    public void setSeq(String str) {
        this.seq = str;
    }

    public void setTags(Map<String, String> map) {
        this.tags = map;
    }

    public void setTime(String str) {
        this.time = str;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    public void setVer(int i) {
        this.ver = i;
    }
}
