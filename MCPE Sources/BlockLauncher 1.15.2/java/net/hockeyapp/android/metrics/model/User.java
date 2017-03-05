package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.metrics.JsonHelper;
import org.mozilla.javascript.Token;

public class User implements Serializable, IJsonSerializable {
    private String accountAcquisitionDate;
    private String accountId;
    private String anonUserAcquisitionDate;
    private String authUserAcquisitionDate;
    private String authUserId;
    private String id;
    private String storeRegion;
    private String userAgent;

    public User() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public void addToHashMap(Map<String, String> map) {
        if (this.accountAcquisitionDate != null) {
            map.put("ai.user.accountAcquisitionDate", this.accountAcquisitionDate);
        }
        if (this.accountId != null) {
            map.put("ai.user.accountId", this.accountId);
        }
        if (this.userAgent != null) {
            map.put("ai.user.userAgent", this.userAgent);
        }
        if (this.id != null) {
            map.put("ai.user.id", this.id);
        }
        if (this.storeRegion != null) {
            map.put("ai.user.storeRegion", this.storeRegion);
        }
        if (this.authUserId != null) {
            map.put("ai.user.authUserId", this.authUserId);
        }
        if (this.anonUserAcquisitionDate != null) {
            map.put("ai.user.anonUserAcquisitionDate", this.anonUserAcquisitionDate);
        }
        if (this.authUserAcquisitionDate != null) {
            map.put("ai.user.authUserAcquisitionDate", this.authUserAcquisitionDate);
        }
    }

    public String getAccountAcquisitionDate() {
        return this.accountAcquisitionDate;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getAnonUserAcquisitionDate() {
        return this.anonUserAcquisitionDate;
    }

    public String getAuthUserAcquisitionDate() {
        return this.authUserAcquisitionDate;
    }

    public String getAuthUserId() {
        return this.authUserId;
    }

    public String getId() {
        return this.id;
    }

    public String getStoreRegion() {
        return this.storeRegion;
    }

    public String getUserAgent() {
        return this.userAgent;
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
        if (this.accountAcquisitionDate != null) {
            writer.write(str + "\"ai.user.accountAcquisitionDate\":");
            writer.write(JsonHelper.convert(this.accountAcquisitionDate));
            str = ",";
        }
        if (this.accountId != null) {
            writer.write(str + "\"ai.user.accountId\":");
            writer.write(JsonHelper.convert(this.accountId));
            str = ",";
        }
        if (this.userAgent != null) {
            writer.write(str + "\"ai.user.userAgent\":");
            writer.write(JsonHelper.convert(this.userAgent));
            str = ",";
        }
        if (this.id != null) {
            writer.write(str + "\"ai.user.id\":");
            writer.write(JsonHelper.convert(this.id));
            str = ",";
        }
        if (this.storeRegion != null) {
            writer.write(str + "\"ai.user.storeRegion\":");
            writer.write(JsonHelper.convert(this.storeRegion));
            str = ",";
        }
        if (this.authUserId != null) {
            writer.write(str + "\"ai.user.authUserId\":");
            writer.write(JsonHelper.convert(this.authUserId));
            str = ",";
        }
        if (this.anonUserAcquisitionDate != null) {
            writer.write(str + "\"ai.user.anonUserAcquisitionDate\":");
            writer.write(JsonHelper.convert(this.anonUserAcquisitionDate));
            str = ",";
        }
        if (this.authUserAcquisitionDate == null) {
            return str;
        }
        writer.write(str + "\"ai.user.authUserAcquisitionDate\":");
        writer.write(JsonHelper.convert(this.authUserAcquisitionDate));
        return ",";
    }

    public void setAccountAcquisitionDate(String str) {
        this.accountAcquisitionDate = str;
    }

    public void setAccountId(String str) {
        this.accountId = str;
    }

    public void setAnonUserAcquisitionDate(String str) {
        this.anonUserAcquisitionDate = str;
    }

    public void setAuthUserAcquisitionDate(String str) {
        this.authUserAcquisitionDate = str;
    }

    public void setAuthUserId(String str) {
        this.authUserId = str;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setStoreRegion(String str) {
        this.storeRegion = str;
    }

    public void setUserAgent(String str) {
        this.userAgent = str;
    }
}
