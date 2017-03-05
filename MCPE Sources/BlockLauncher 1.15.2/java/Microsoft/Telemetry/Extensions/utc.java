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

public class utc extends Extension {
    private String aId;
    private long cat;
    private long flags;
    private String op;
    private String raId;
    private String sqmId;
    private String stId;

    public static class Schema {
        private static final Metadata aId_metadata = new Metadata();
        private static final Metadata cat_metadata = new Metadata();
        private static final Metadata flags_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata op_metadata = new Metadata();
        private static final Metadata raId_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata sqmId_metadata = new Metadata();
        private static final Metadata stId_metadata = new Metadata();

        static {
            metadata.setName("utc");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.utc");
            metadata.getAttributes().put("Description", "Describes the properties that might be populated by a logging library on Windows.");
            stId_metadata.setName("stId");
            stId_metadata.getAttributes().put("Description", "Used for UTC scenarios.");
            aId_metadata.setName("aId");
            aId_metadata.getAttributes().put("Description", "Activity Id in ETW (event tracing for windows).");
            raId_metadata.setName("raId");
            raId_metadata.getAttributes().put("Description", "Related Activity Id in ETW.");
            op_metadata.setName("op");
            op_metadata.getAttributes().put("Description", "Op Code in ETW.");
            cat_metadata.setName("cat");
            cat_metadata.getAttributes().put("Description", "Categories.");
            cat_metadata.getDefault_value().setInt_value(0);
            flags_metadata.setName("flags");
            flags_metadata.getAttributes().put("Description", "This captures the characteristics of the traffic. Examples: isTest, isInternal.");
            flags_metadata.getDefault_value().setInt_value(0);
            sqmId_metadata.setName("sqmId");
            sqmId_metadata.getAttributes().put("Description", "The Windows SQM device ID.");
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
            fieldDef.setMetadata(stId_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(aId_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 30);
            fieldDef.setMetadata(raId_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 40);
            fieldDef.setMetadata(op_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 50);
            fieldDef.setMetadata(cat_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT64);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 60);
            fieldDef.setMetadata(flags_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT64);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 70);
            fieldDef.setMetadata(sqmId_metadata);
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

    public final String getAId() {
        return this.aId;
    }

    public final long getCat() {
        return this.cat;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.stId;
            case Token.URSH /*20*/:
                return this.aId;
            case Token.NEW /*30*/:
                return this.raId;
            case Token.NUMBER /*40*/:
                return this.op;
            case Token.THROW /*50*/:
                return Long.valueOf(this.cat);
            case Token.ENUM_INIT_ARRAY /*60*/:
                return Long.valueOf(this.flags);
            case Token.DEL_REF /*70*/:
                return this.sqmId;
            default:
                return null;
        }
    }

    public final long getFlags() {
        return this.flags;
    }

    public final String getOp() {
        return this.op;
    }

    public final String getRaId() {
        return this.raId;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final String getSqmId() {
        return this.sqmId;
    }

    public final String getStId() {
        return this.stId;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        utc Microsoft_Telemetry_Extensions_utc = (utc) obj;
        return memberwiseCompareQuick(Microsoft_Telemetry_Extensions_utc) && memberwiseCompareDeep(Microsoft_Telemetry_Extensions_utc);
    }

    protected boolean memberwiseCompareDeep(utc Microsoft_Telemetry_Extensions_utc) {
        boolean z = (super.memberwiseCompareDeep(Microsoft_Telemetry_Extensions_utc)) && (this.stId == null || this.stId.equals(Microsoft_Telemetry_Extensions_utc.stId));
        z = z && (this.aId == null || this.aId.equals(Microsoft_Telemetry_Extensions_utc.aId));
        z = z && (this.raId == null || this.raId.equals(Microsoft_Telemetry_Extensions_utc.raId));
        z = z && (this.op == null || this.op.equals(Microsoft_Telemetry_Extensions_utc.op));
        return z && (this.sqmId == null || this.sqmId.equals(Microsoft_Telemetry_Extensions_utc.sqmId));
    }

    protected boolean memberwiseCompareQuick(utc Microsoft_Telemetry_Extensions_utc) {
        boolean z;
        if (super.memberwiseCompareQuick(Microsoft_Telemetry_Extensions_utc)) {
            if ((this.stId == null) == (Microsoft_Telemetry_Extensions_utc.stId == null)) {
                z = true;
                z = z && (this.stId == null || this.stId.length() == Microsoft_Telemetry_Extensions_utc.stId.length());
                if (z) {
                    if ((this.aId != null) == (Microsoft_Telemetry_Extensions_utc.aId != null)) {
                        z = true;
                        z = z && (this.aId == null || this.aId.length() == Microsoft_Telemetry_Extensions_utc.aId.length());
                        if (z) {
                            if ((this.raId != null) == (Microsoft_Telemetry_Extensions_utc.raId != null)) {
                                z = true;
                                z = z && (this.raId == null || this.raId.length() == Microsoft_Telemetry_Extensions_utc.raId.length());
                                if (z) {
                                    if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                                        z = true;
                                        z = z && (this.op == null || this.op.length() == Microsoft_Telemetry_Extensions_utc.op.length());
                                        z = z && this.cat == Microsoft_Telemetry_Extensions_utc.cat;
                                        z = z && this.flags == Microsoft_Telemetry_Extensions_utc.flags;
                                        if (z) {
                                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                                z = true;
                                                return z && (this.sqmId == null || this.sqmId.length() == Microsoft_Telemetry_Extensions_utc.sqmId.length());
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
                                if (!z) {
                                }
                                if (!z) {
                                }
                                if (z) {
                                    if (this.sqmId != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                                    }
                                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (!z) {
                        }
                        if (z) {
                            if (this.op != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.op != null) {
                            }
                            if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                    if (this.sqmId != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                                    }
                                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (!z) {
                }
                if (z) {
                    if (this.raId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.raId != null) {
                    }
                    if ((this.raId != null) == (Microsoft_Telemetry_Extensions_utc.raId != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.op != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.op != null) {
                            }
                            if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                    if (this.sqmId != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                                    }
                                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.op != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.op != null) {
                    }
                    if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                }
                if (z) {
                }
                if (z) {
                    if (this.sqmId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                    }
                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (!z) {
        }
        if (z) {
            if (this.aId != null) {
            }
            if (Microsoft_Telemetry_Extensions_utc.aId != null) {
            }
            if ((this.aId != null) == (Microsoft_Telemetry_Extensions_utc.aId != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.raId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.raId != null) {
                    }
                    if ((this.raId != null) == (Microsoft_Telemetry_Extensions_utc.raId != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.op != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.op != null) {
                            }
                            if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                }
                                if (z) {
                                    if (this.sqmId != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                                    }
                                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.op != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.op != null) {
                    }
                    if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                }
                if (z) {
                }
                if (z) {
                    if (this.sqmId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                    }
                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.raId != null) {
            }
            if (Microsoft_Telemetry_Extensions_utc.raId != null) {
            }
            if ((this.raId != null) == (Microsoft_Telemetry_Extensions_utc.raId != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.op != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.op != null) {
                    }
                    if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                        }
                        if (z) {
                            if (this.sqmId != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                            }
                            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                }
                if (z) {
                }
                if (z) {
                    if (this.sqmId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                    }
                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.op != null) {
            }
            if (Microsoft_Telemetry_Extensions_utc.op != null) {
            }
            if ((this.op != null) == (Microsoft_Telemetry_Extensions_utc.op != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                }
                if (z) {
                }
                if (z) {
                    if (this.sqmId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
                    }
                    if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
        }
        if (z) {
        }
        if (z) {
            if (this.sqmId != null) {
            }
            if (Microsoft_Telemetry_Extensions_utc.sqmId != null) {
            }
            if ((this.sqmId != null) == (Microsoft_Telemetry_Extensions_utc.sqmId != null)) {
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
                            this.stId = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.aId = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NEW /*30*/:
                            this.raId = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NUMBER /*40*/:
                            this.op = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.THROW /*50*/:
                            this.cat = ReadHelper.readInt64(protocolReader, readFieldBegin.type);
                            break;
                        case Token.ENUM_INIT_ARRAY /*60*/:
                            this.flags = ReadHelper.readInt64(protocolReader, readFieldBegin.type);
                            break;
                        case Token.DEL_REF /*70*/:
                            this.sqmId = ReadHelper.readString(protocolReader, readFieldBegin.type);
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
            this.stId = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.aId = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.raId = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.op = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.cat = protocolReader.readInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.flags = protocolReader.readInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.sqmId = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("utc", "Microsoft.Telemetry.Extensions.utc");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.stId = BuildConfig.FLAVOR;
        this.aId = BuildConfig.FLAVOR;
        this.raId = BuildConfig.FLAVOR;
        this.op = BuildConfig.FLAVOR;
        this.cat = 0;
        this.flags = 0;
        this.sqmId = BuildConfig.FLAVOR;
    }

    public final void setAId(String str) {
        this.aId = str;
    }

    public final void setCat(long j) {
        this.cat = j;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.stId = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.aId = (String) obj;
                return;
            case Token.NEW /*30*/:
                this.raId = (String) obj;
                return;
            case Token.NUMBER /*40*/:
                this.op = (String) obj;
                return;
            case Token.THROW /*50*/:
                this.cat = ((Long) obj).longValue();
                return;
            case Token.ENUM_INIT_ARRAY /*60*/:
                this.flags = ((Long) obj).longValue();
                return;
            case Token.DEL_REF /*70*/:
                this.sqmId = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setFlags(long j) {
        this.flags = j;
    }

    public final void setOp(String str) {
        this.op = str;
    }

    public final void setRaId(String str) {
        this.raId = str;
    }

    public final void setSqmId(String str) {
        this.sqmId = str;
    }

    public final void setStId(String str) {
        this.stId = str;
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
        if (hasCapability && this.stId == Schema.stId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.stId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.stId_metadata);
            protocolWriter.writeString(this.stId);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.aId == Schema.aId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.aId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.aId_metadata);
            protocolWriter.writeString(this.aId);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.raId == Schema.raId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.raId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.raId_metadata);
            protocolWriter.writeString(this.raId);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.op == Schema.op_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 40, Schema.op_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.op_metadata);
            protocolWriter.writeString(this.op);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.cat == Schema.cat_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT64, 50, Schema.cat_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT64, 50, Schema.cat_metadata);
            protocolWriter.writeInt64(this.cat);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.flags == Schema.flags_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT64, 60, Schema.flags_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT64, 60, Schema.flags_metadata);
            protocolWriter.writeInt64(this.flags);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.sqmId == Schema.sqmId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.sqmId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.sqmId_metadata);
            protocolWriter.writeString(this.sqmId);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
