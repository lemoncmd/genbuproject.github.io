package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
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

public class os extends Extension {
    private String expId;
    private String locale;

    public static class Schema {
        private static final Metadata expId_metadata = new Metadata();
        private static final Metadata locale_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("os");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.os");
            metadata.getAttributes().put("Description", "Describes the OS properties that would be populated by the client.");
            locale_metadata.setName("locale");
            locale_metadata.getAttributes().put("Description", "OS locale, set by the user, in the Windows locale format. Example, en-US for US English. Refer RFC 4646 for the format.");
            expId_metadata.setName("expId");
            expId_metadata.getAttributes().put("Description", "Comma delimited list of experiment ids for experiments installed on the OS. Format is <NamespaceIdentifier>:<ExperimentId> for example, m:12345.");
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
            structDef.setBase_def(Microsoft.Telemetry.Extension.Schema.getTypeDef(schemaDef));
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 10);
            fieldDef.setMetadata(locale_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(expId_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
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

    public final String getExpId() {
        return this.expId;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.locale;
            case Token.URSH /*20*/:
                return this.expId;
            default:
                return null;
        }
    }

    public final String getLocale() {
        return this.locale;
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
        os osVar = (os) obj;
        return memberwiseCompareQuick(osVar) && memberwiseCompareDeep(osVar);
    }

    protected boolean memberwiseCompareDeep(os osVar) {
        boolean z = (super.memberwiseCompareDeep(osVar)) && (this.locale == null || this.locale.equals(osVar.locale));
        return z && (this.expId == null || this.expId.equals(osVar.expId));
    }

    protected boolean memberwiseCompareQuick(os osVar) {
        boolean z;
        if (super.memberwiseCompareQuick(osVar)) {
            if ((this.locale == null) == (osVar.locale == null)) {
                z = true;
                z = z && (this.locale == null || this.locale.length() == osVar.locale.length());
                if (z) {
                    if ((this.expId != null) == (osVar.expId != null)) {
                        z = true;
                        return z && (this.expId == null || this.expId.length() == osVar.expId.length());
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
            if (this.expId != null) {
            }
            if (osVar.expId != null) {
            }
            if ((this.expId != null) == (osVar.expId != null)) {
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
                            this.locale = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.expId = ReadHelper.readString(protocolReader, readFieldBegin.type);
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
            this.locale = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.expId = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("os", "Microsoft.Telemetry.Extensions.os");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.locale = BuildConfig.FLAVOR;
        this.expId = BuildConfig.FLAVOR;
    }

    public final void setExpId(String str) {
        this.expId = str;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.locale = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.expId = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setLocale(String str) {
        this.locale = str;
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
        boolean hasCapability = protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        super.writeNested(protocolWriter, true);
        if (hasCapability && this.locale == Schema.locale_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.locale_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.locale_metadata);
            protocolWriter.writeString(this.locale);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.expId == Schema.expId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.expId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.expId_metadata);
            protocolWriter.writeString(this.expId);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
