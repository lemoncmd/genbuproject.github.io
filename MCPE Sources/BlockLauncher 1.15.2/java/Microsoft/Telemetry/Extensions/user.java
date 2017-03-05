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

public class user extends Extension {
    private String authId;
    private String id;
    private String localId;

    public static class Schema {
        private static final Metadata authId_metadata = new Metadata();
        private static final Metadata id_metadata = new Metadata();
        private static final Metadata localId_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("user");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.user");
            metadata.getAttributes().put("Description", "Describes the User related fields. See https://osgwiki.com/wiki/CommonSchema/user_id");
            id_metadata.setName(Name.MARK);
            id_metadata.getAttributes().put("Description", "Unique user Id. Clients aren't expected to set this; instead the service will decide the best ID to use here. Clients may set this if they believe they have the best user ID already. Format is <NamespaceIdentifier>:<Id> for example, x:12345678.");
            localId_metadata.setName("localId");
            localId_metadata.getAttributes().put("Description", "Local user identifier according to the client. Format is <NamespaceIdentifier>:<Id> for example, x:12345678.");
            authId_metadata.setName("authId");
            authId_metadata.getAttributes().put("Description", "This is the ID of the user associated with this event, deduced from a token such as an MSA ticket or Xbox xtoken.");
            authId_metadata.getAttributes().put("Name", "UserAuthId");
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
            fieldDef.setMetadata(id_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(localId_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 30);
            fieldDef.setMetadata(authId_metadata);
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

    public final String getAuthId() {
        return this.authId;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.id;
            case Token.URSH /*20*/:
                return this.localId;
            case Token.NEW /*30*/:
                return this.authId;
            default:
                return null;
        }
    }

    public final String getId() {
        return this.id;
    }

    public final String getLocalId() {
        return this.localId;
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
        user Microsoft_Telemetry_Extensions_user = (user) obj;
        return memberwiseCompareQuick(Microsoft_Telemetry_Extensions_user) && memberwiseCompareDeep(Microsoft_Telemetry_Extensions_user);
    }

    protected boolean memberwiseCompareDeep(user Microsoft_Telemetry_Extensions_user) {
        boolean z = (super.memberwiseCompareDeep(Microsoft_Telemetry_Extensions_user)) && (this.id == null || this.id.equals(Microsoft_Telemetry_Extensions_user.id));
        z = z && (this.localId == null || this.localId.equals(Microsoft_Telemetry_Extensions_user.localId));
        return z && (this.authId == null || this.authId.equals(Microsoft_Telemetry_Extensions_user.authId));
    }

    protected boolean memberwiseCompareQuick(user Microsoft_Telemetry_Extensions_user) {
        boolean z;
        if (super.memberwiseCompareQuick(Microsoft_Telemetry_Extensions_user)) {
            if ((this.id == null) == (Microsoft_Telemetry_Extensions_user.id == null)) {
                z = true;
                z = z && (this.id == null || this.id.length() == Microsoft_Telemetry_Extensions_user.id.length());
                if (z) {
                    if ((this.localId != null) == (Microsoft_Telemetry_Extensions_user.localId != null)) {
                        z = true;
                        z = z && (this.localId == null || this.localId.length() == Microsoft_Telemetry_Extensions_user.localId.length());
                        if (z) {
                            if ((this.authId != null) == (Microsoft_Telemetry_Extensions_user.authId != null)) {
                                z = true;
                                return z && (this.authId == null || this.authId.length() == Microsoft_Telemetry_Extensions_user.authId.length());
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
                    if (this.authId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_user.authId != null) {
                    }
                    if ((this.authId != null) == (Microsoft_Telemetry_Extensions_user.authId != null)) {
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
            if (this.localId != null) {
            }
            if (Microsoft_Telemetry_Extensions_user.localId != null) {
            }
            if ((this.localId != null) == (Microsoft_Telemetry_Extensions_user.localId != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.authId != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_user.authId != null) {
                    }
                    if ((this.authId != null) == (Microsoft_Telemetry_Extensions_user.authId != null)) {
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
            if (this.authId != null) {
            }
            if (Microsoft_Telemetry_Extensions_user.authId != null) {
            }
            if ((this.authId != null) == (Microsoft_Telemetry_Extensions_user.authId != null)) {
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
                            this.id = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.localId = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NEW /*30*/:
                            this.authId = ReadHelper.readString(protocolReader, readFieldBegin.type);
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
            this.id = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.localId = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.authId = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("user", "Microsoft.Telemetry.Extensions.user");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.id = BuildConfig.FLAVOR;
        this.localId = BuildConfig.FLAVOR;
        this.authId = BuildConfig.FLAVOR;
    }

    public final void setAuthId(String str) {
        this.authId = str;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.id = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.localId = (String) obj;
                return;
            case Token.NEW /*30*/:
                this.authId = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setId(String str) {
        this.id = str;
    }

    public final void setLocalId(String str) {
        this.localId = str;
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
        if (hasCapability && this.id == Schema.id_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.id_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.id_metadata);
            protocolWriter.writeString(this.id);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.localId == Schema.localId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.localId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.localId_metadata);
            protocolWriter.writeString(this.localId);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.authId == Schema.authId_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.authId_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.authId_metadata);
            protocolWriter.writeString(this.authId);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
