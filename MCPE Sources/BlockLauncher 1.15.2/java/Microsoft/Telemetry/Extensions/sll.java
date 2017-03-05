package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
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

public class sll extends Extension {
    private TracingEventLevel level;
    private String libVer;

    public static class Schema {
        private static final Metadata level_metadata = new Metadata();
        private static final Metadata libVer_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("sll");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.sll");
            metadata.getAttributes().put("Description", "Describes the fields related to a service logging library implementation.");
            libVer_metadata.setName("libVer");
            libVer_metadata.getAttributes().put("Description", "Service Logging Library version");
            level_metadata.setName("level");
            level_metadata.setModifier(Modifier.Required);
            level_metadata.getAttributes().put("Description", "Severity level for service event");
            level_metadata.getDefault_value().setInt_value((long) TracingEventLevel.None.getValue());
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
            fieldDef.setMetadata(libVer_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(level_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT32);
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
                return this.libVer;
            case Token.URSH /*20*/:
                return this.level;
            default:
                return null;
        }
    }

    public final TracingEventLevel getLevel() {
        return this.level;
    }

    public final String getLibVer() {
        return this.libVer;
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
        sll Microsoft_Telemetry_Extensions_sll = (sll) obj;
        return memberwiseCompareQuick(Microsoft_Telemetry_Extensions_sll) && memberwiseCompareDeep(Microsoft_Telemetry_Extensions_sll);
    }

    protected boolean memberwiseCompareDeep(sll Microsoft_Telemetry_Extensions_sll) {
        return (super.memberwiseCompareDeep(Microsoft_Telemetry_Extensions_sll)) && (this.libVer == null || this.libVer.equals(Microsoft_Telemetry_Extensions_sll.libVer));
    }

    protected boolean memberwiseCompareQuick(sll Microsoft_Telemetry_Extensions_sll) {
        boolean z;
        if (super.memberwiseCompareQuick(Microsoft_Telemetry_Extensions_sll)) {
            if ((this.libVer == null) == (Microsoft_Telemetry_Extensions_sll.libVer == null)) {
                z = true;
                z = z && (this.libVer == null || this.libVer.length() == Microsoft_Telemetry_Extensions_sll.libVer.length());
                return z && this.level == Microsoft_Telemetry_Extensions_sll.level;
            }
        }
        z = false;
        if (!z) {
        }
        if (!z) {
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
                            this.libVer = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.level = TracingEventLevel.fromValue(ReadHelper.readInt32(protocolReader, readFieldBegin.type));
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
            this.libVer = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.level = TracingEventLevel.fromValue(protocolReader.readInt32());
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("sll", "Microsoft.Telemetry.Extensions.sll");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.libVer = BuildConfig.FLAVOR;
        this.level = TracingEventLevel.None;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.libVer = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.level = (TracingEventLevel) obj;
                return;
            default:
                return;
        }
    }

    public final void setLevel(TracingEventLevel tracingEventLevel) {
        this.level = tracingEventLevel;
    }

    public final void setLibVer(String str) {
        this.libVer = str;
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
        if (hasCapability && this.libVer == Schema.libVer_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.libVer_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.libVer_metadata);
            protocolWriter.writeString(this.libVer);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_INT32, 20, Schema.level_metadata);
        protocolWriter.writeInt32(this.level.getValue());
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
