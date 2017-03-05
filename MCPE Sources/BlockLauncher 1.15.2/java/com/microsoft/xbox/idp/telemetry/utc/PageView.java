package com.microsoft.xbox.idp.telemetry.utc;

import Microsoft.Telemetry.Data;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.Modifier;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class PageView extends Data<CommonData> {
    private String fromPage;
    private String pageName;

    public static class Schema {
        private static final Metadata fromPage_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("PageView");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.PageView");
            metadata.getAttributes().put("Description", "OnlineId PageView event");
            pageName_metadata.setName("pageName");
            pageName_metadata.setModifier(Modifier.Required);
            pageName_metadata.getAttributes().put("Description", "The name of the currently viewed page");
            fromPage_metadata.setName("fromPage");
            fromPage_metadata.setModifier(Modifier.Required);
            fromPage_metadata.getAttributes().put("Description", "The name of the previously viewed page (aka Referer Page Uri)");
            schemaDef.setRoot(getTypeDef(schemaDef));
        }

        private static short getStructDef(SchemaDef schemaDef) {
            short s = (short) 0;
            while (s < schemaDef.getStructs().size()) {
                if (((StructDef) schemaDef.getStructs().get(s)).getMetadata() == metadata) {
                    break;
                }
                s = (short) (s + 1);
            }
            StructDef structDef = new StructDef();
            schemaDef.getStructs().add(structDef);
            structDef.setMetadata(metadata);
            structDef.setBase_def(Microsoft.Telemetry.Data.Schema.getTypeDef(schemaDef));
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 10);
            fieldDef.setMetadata(pageName_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(fromPage_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            return s;
        }

        public static TypeDef getTypeDef(SchemaDef schemaDef) {
            TypeDef typeDef = new TypeDef();
            typeDef.setId(BondDataType.BT_STRUCT);
            typeDef.setStruct_def(getStructDef(schemaDef));
            return typeDef;
        }
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.pageName;
            case Token.URSH /*20*/:
                return this.fromPage;
            default:
                return null;
        }
    }

    public final String getFromPage() {
        return this.fromPage;
    }

    public final String getPageName() {
        return this.pageName;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        PageView pageView = (PageView) obj;
        return memberwiseCompareQuick(pageView) && memberwiseCompareDeep(pageView);
    }

    protected boolean memberwiseCompareDeep(PageView pageView) {
        boolean z = (super.memberwiseCompareDeep(pageView)) && (this.pageName == null || this.pageName.equals(pageView.pageName));
        return z && (this.fromPage == null || this.fromPage.equals(pageView.fromPage));
    }

    protected boolean memberwiseCompareQuick(PageView pageView) {
        boolean z;
        if (super.memberwiseCompareQuick(pageView)) {
            if ((this.pageName == null) == (pageView.pageName == null)) {
                z = true;
                z = z && (this.pageName == null || this.pageName.length() == pageView.pageName.length());
                if (z) {
                    if ((this.fromPage != null) == (pageView.fromPage != null)) {
                        z = true;
                        return z && (this.fromPage == null || this.fromPage.length() == pageView.fromPage.length());
                    }
                }
                z = false;
                if (!z) {
                }
            }
        }
        z = false;
        if (!z) {
        }
        if (z) {
            if (this.fromPage != null) {
            }
            if (pageView.fromPage != null) {
            }
            if ((this.fromPage != null) == (pageView.fromPage != null)) {
                z = true;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
    }

    public void read(ProtocolReader protocolReader) throws IOException {
        protocolReader.readBegin();
        readNested(protocolReader);
        protocolReader.readEnd();
    }

    public void read(ProtocolReader protocolReader, BondSerializable bondSerializable) throws IOException {
    }

    public void readNested(ProtocolReader protocolReader) throws IOException {
        if (!protocolReader.hasCapability(ProtocolCapability.TAGGED)) {
            readUntagged(protocolReader, false);
        } else if (readTagged(protocolReader, false)) {
            ReadHelper.skipPartialStruct(protocolReader);
        }
    }

    protected boolean readTagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean z2 = false;
        protocolReader.readStructBegin(z);
        if (super.readTagged(protocolReader, true)) {
            while (true) {
                FieldTag readFieldBegin = protocolReader.readFieldBegin();
                if (readFieldBegin.type == BondDataType.BT_STOP || readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                    if (readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                        z2 = true;
                    }
                    protocolReader.readStructEnd();
                } else {
                    switch (readFieldBegin.id) {
                        case Token.BITXOR /*10*/:
                            this.pageName = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.fromPage = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        default:
                            protocolReader.skip(readFieldBegin.type);
                            break;
                    }
                    protocolReader.readFieldEnd();
                }
            }
        }
        return z2;
    }

    protected void readUntagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean hasCapability = protocolReader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolReader.readStructBegin(z);
        super.readUntagged(protocolReader, true);
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.pageName = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.fromPage = protocolReader.readWString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("PageView", "com.microsoft.xbox.idp.telemetry.utc.PageView");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.pageName = BuildConfig.FLAVOR;
        this.fromPage = BuildConfig.FLAVOR;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.pageName = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.fromPage = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setFromPage(String str) {
        this.fromPage = str;
    }

    public final void setPageName(String str) {
        this.pageName = str;
    }

    public void unmarshal(InputStream inputStream) throws IOException {
        Marshaler.unmarshal(inputStream, this);
    }

    public void unmarshal(InputStream inputStream, BondSerializable bondSerializable) throws IOException {
        Marshaler.unmarshal(inputStream, (SchemaDef) bondSerializable, this);
    }

    public void write(ProtocolWriter protocolWriter) throws IOException {
        protocolWriter.writeBegin();
        ProtocolWriter firstPassWriter = protocolWriter.getFirstPassWriter();
        if (firstPassWriter != null) {
            writeNested(firstPassWriter, false);
            writeNested(protocolWriter, false);
        } else {
            writeNested(protocolWriter, false);
        }
        protocolWriter.writeEnd();
    }

    public void writeNested(ProtocolWriter protocolWriter, boolean z) throws IOException {
        protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        super.writeNested(protocolWriter, true);
        protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.pageName_metadata);
        protocolWriter.writeWString(this.pageName);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.fromPage_metadata);
        protocolWriter.writeWString(this.fromPage);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
