package com.microsoft.cll.android;

import Microsoft.Telemetry.Base;
import com.microsoft.bond.StructDef;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Domain;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;

public class PreSerializedEvent extends Data {
    private static final String TAG = "AndroidCll-PreSerializedEvent";
    public String serializedData;

    public PreSerializedEvent(String str, String str2, String str3, Map<String, String> map) {
        this.serializedData = str2;
        Data data = this;
        setBaseData(new Domain());
        getBaseData().QualifiedName = str3;
        this.QualifiedName = str;
        if (map != null) {
            this.Attributes.putAll(map);
        }
    }

    public static PreSerializedEvent createFromDynamicEvent(String str, String str2) {
        return new PreSerializedEvent(str, str2, BuildConfig.FLAVOR, null);
    }

    public static PreSerializedEvent createFromStaticEvent(ILogger iLogger, Base base) {
        String partCName = getPartCName(base);
        String partBName = getPartBName(iLogger, base);
        Map attributes = getAttributes(base);
        if (!partBName.isEmpty()) {
            base.setBaseType(partBName);
        }
        return new PreSerializedEvent(partCName, serializeEvent(iLogger, base), partBName, attributes);
    }

    private static Map<String, String> getAttributes(Base base) {
        Map<String, String> hashMap = new HashMap();
        hashMap.putAll(((StructDef) base.getSchema().getStructs().get(0)).getMetadata().getAttributes());
        return hashMap;
    }

    private static String getPartBName(ILogger iLogger, Base base) {
        String str = BuildConfig.FLAVOR;
        try {
            return ((StructDef) ((Microsoft.Telemetry.Domain) ((Microsoft.Telemetry.Data) base).getBaseData()).getSchema().getStructs().get(0)).getMetadata().getQualified_name();
        } catch (ClassCastException e) {
            iLogger.info(TAG, "This event doesn't extend data");
            return str;
        }
    }

    private static String getPartCName(Base base) {
        return ((StructDef) base.getSchema().getStructs().get(0)).getMetadata().getQualified_name();
    }

    private static String serializeEvent(ILogger iLogger, Base base) {
        return new BondJsonSerializer(iLogger).serialize(base);
    }

    public void serialize(Writer writer) throws IOException {
        writer.write(this.serializedData);
    }
}
