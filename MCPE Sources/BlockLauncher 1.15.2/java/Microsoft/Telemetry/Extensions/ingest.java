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

public class ingest extends Extension {
    private long auth;
    private String clientIp;
    private long quality;
    private String time;
    private String uploadTime;
    private String userAgent;

    public static class Schema {
        private static final Metadata auth_metadata = new Metadata();
        private static final Metadata clientIp_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata quality_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata time_metadata = new Metadata();
        private static final Metadata uploadTime_metadata = new Metadata();
        private static final Metadata userAgent_metadata = new Metadata();

        static {
            metadata.setName("ingest");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.ingest");
            metadata.getAttributes().put("Description", "Describes the fields added dynamically by the service. Clients should NOT use this section since it is adding dynamically by the service.");
            time_metadata.setName("time");
            time_metadata.setModifier(Modifier.Required);
            time_metadata.getAttributes().put("Name", "IngestDateTime");
            clientIp_metadata.setName("clientIp");
            clientIp_metadata.setModifier(Modifier.Required);
            clientIp_metadata.getAttributes().put("Name", "ClientIp");
            auth_metadata.setName("auth");
            auth_metadata.getAttributes().put("Name", "DataAuthorization");
            auth_metadata.getDefault_value().setInt_value(0);
            quality_metadata.setName("quality");
            quality_metadata.getAttributes().put("Name", "DataQuality");
            quality_metadata.getDefault_value().setInt_value(0);
            uploadTime_metadata.setName("uploadTime");
            uploadTime_metadata.getAttributes().put("Name", "UploadDateTime");
            userAgent_metadata.setName("userAgent");
            userAgent_metadata.getAttributes().put("Name", "UserAgent");
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
            fieldDef.setMetadata(time_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(clientIp_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 30);
            fieldDef.setMetadata(auth_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT64);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 40);
            fieldDef.setMetadata(quality_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT64);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 50);
            fieldDef.setMetadata(uploadTime_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 60);
            fieldDef.setMetadata(userAgent_metadata);
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

    public final long getAuth() {
        return this.auth;
    }

    public final String getClientIp() {
        return this.clientIp;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.time;
            case Token.URSH /*20*/:
                return this.clientIp;
            case Token.NEW /*30*/:
                return Long.valueOf(this.auth);
            case Token.NUMBER /*40*/:
                return Long.valueOf(this.quality);
            case Token.THROW /*50*/:
                return this.uploadTime;
            case Token.ENUM_INIT_ARRAY /*60*/:
                return this.userAgent;
            default:
                return null;
        }
    }

    public final long getQuality() {
        return this.quality;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final String getTime() {
        return this.time;
    }

    public final String getUploadTime() {
        return this.uploadTime;
    }

    public final String getUserAgent() {
        return this.userAgent;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        ingest Microsoft_Telemetry_Extensions_ingest = (ingest) obj;
        return memberwiseCompareQuick(Microsoft_Telemetry_Extensions_ingest) && memberwiseCompareDeep(Microsoft_Telemetry_Extensions_ingest);
    }

    protected boolean memberwiseCompareDeep(ingest Microsoft_Telemetry_Extensions_ingest) {
        boolean z = (super.memberwiseCompareDeep(Microsoft_Telemetry_Extensions_ingest)) && (this.time == null || this.time.equals(Microsoft_Telemetry_Extensions_ingest.time));
        z = z && (this.clientIp == null || this.clientIp.equals(Microsoft_Telemetry_Extensions_ingest.clientIp));
        z = z && (this.uploadTime == null || this.uploadTime.equals(Microsoft_Telemetry_Extensions_ingest.uploadTime));
        return z && (this.userAgent == null || this.userAgent.equals(Microsoft_Telemetry_Extensions_ingest.userAgent));
    }

    protected boolean memberwiseCompareQuick(ingest Microsoft_Telemetry_Extensions_ingest) {
        boolean z;
        if (super.memberwiseCompareQuick(Microsoft_Telemetry_Extensions_ingest)) {
            if ((this.time == null) == (Microsoft_Telemetry_Extensions_ingest.time == null)) {
                z = true;
                z = z && (this.time == null || this.time.length() == Microsoft_Telemetry_Extensions_ingest.time.length());
                if (z) {
                    if ((this.clientIp != null) == (Microsoft_Telemetry_Extensions_ingest.clientIp != null)) {
                        z = true;
                        z = z && (this.clientIp == null || this.clientIp.length() == Microsoft_Telemetry_Extensions_ingest.clientIp.length());
                        z = z && this.auth == Microsoft_Telemetry_Extensions_ingest.auth;
                        z = z && this.quality == Microsoft_Telemetry_Extensions_ingest.quality;
                        if (z) {
                            if ((this.uploadTime != null) == (Microsoft_Telemetry_Extensions_ingest.uploadTime != null)) {
                                z = true;
                                z = z && (this.uploadTime == null || this.uploadTime.length() == Microsoft_Telemetry_Extensions_ingest.uploadTime.length());
                                if (z) {
                                    if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
                                        z = true;
                                        return z && (this.userAgent == null || this.userAgent.length() == Microsoft_Telemetry_Extensions_ingest.userAgent.length());
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
                            if (this.userAgent != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                            }
                            if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
                if (!z) {
                }
                if (!z) {
                }
                if (z) {
                    if (this.uploadTime != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_ingest.uploadTime != null) {
                    }
                    if ((this.uploadTime != null) == (Microsoft_Telemetry_Extensions_ingest.uploadTime != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.userAgent != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                            }
                            if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
                    if (this.userAgent != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                    }
                    if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
            if (this.clientIp != null) {
            }
            if (Microsoft_Telemetry_Extensions_ingest.clientIp != null) {
            }
            if ((this.clientIp != null) == (Microsoft_Telemetry_Extensions_ingest.clientIp != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                }
                if (z) {
                }
                if (z) {
                    if (this.uploadTime != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_ingest.uploadTime != null) {
                    }
                    if ((this.uploadTime != null) == (Microsoft_Telemetry_Extensions_ingest.uploadTime != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.userAgent != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                            }
                            if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
                    if (this.userAgent != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                    }
                    if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
            if (this.uploadTime != null) {
            }
            if (Microsoft_Telemetry_Extensions_ingest.uploadTime != null) {
            }
            if ((this.uploadTime != null) == (Microsoft_Telemetry_Extensions_ingest.uploadTime != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.userAgent != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
                    }
                    if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
            if (this.userAgent != null) {
            }
            if (Microsoft_Telemetry_Extensions_ingest.userAgent != null) {
            }
            if ((this.userAgent != null) == (Microsoft_Telemetry_Extensions_ingest.userAgent != null)) {
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
                            this.time = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.clientIp = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NEW /*30*/:
                            this.auth = ReadHelper.readInt64(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NUMBER /*40*/:
                            this.quality = ReadHelper.readInt64(protocolReader, readFieldBegin.type);
                            break;
                        case Token.THROW /*50*/:
                            this.uploadTime = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.ENUM_INIT_ARRAY /*60*/:
                            this.userAgent = ReadHelper.readString(protocolReader, readFieldBegin.type);
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
            this.time = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.clientIp = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.auth = protocolReader.readInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.quality = protocolReader.readInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.uploadTime = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.userAgent = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("ingest", "Microsoft.Telemetry.Extensions.ingest");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.time = BuildConfig.FLAVOR;
        this.clientIp = BuildConfig.FLAVOR;
        this.auth = 0;
        this.quality = 0;
        this.uploadTime = BuildConfig.FLAVOR;
        this.userAgent = BuildConfig.FLAVOR;
    }

    public final void setAuth(long j) {
        this.auth = j;
    }

    public final void setClientIp(String str) {
        this.clientIp = str;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.time = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.clientIp = (String) obj;
                return;
            case Token.NEW /*30*/:
                this.auth = ((Long) obj).longValue();
                return;
            case Token.NUMBER /*40*/:
                this.quality = ((Long) obj).longValue();
                return;
            case Token.THROW /*50*/:
                this.uploadTime = (String) obj;
                return;
            case Token.ENUM_INIT_ARRAY /*60*/:
                this.userAgent = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setQuality(long j) {
        this.quality = j;
    }

    public final void setTime(String str) {
        this.time = str;
    }

    public final void setUploadTime(String str) {
        this.uploadTime = str;
    }

    public final void setUserAgent(String str) {
        this.userAgent = str;
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
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.time_metadata);
        protocolWriter.writeString(this.time);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.clientIp_metadata);
        protocolWriter.writeString(this.clientIp);
        protocolWriter.writeFieldEnd();
        if (hasCapability && this.auth == Schema.auth_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT64, 30, Schema.auth_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT64, 30, Schema.auth_metadata);
            protocolWriter.writeInt64(this.auth);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.quality == Schema.quality_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT64, 40, Schema.quality_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT64, 40, Schema.quality_metadata);
            protocolWriter.writeInt64(this.quality);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.uploadTime == Schema.uploadTime_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.uploadTime_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.uploadTime_metadata);
            protocolWriter.writeString(this.uploadTime);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.userAgent == Schema.userAgent_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.userAgent_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.userAgent_metadata);
            protocolWriter.writeString(this.userAgent);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
