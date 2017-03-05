package Microsoft.Telemetry;

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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.mozilla.javascript.Token;

public class Data<TDomain extends BondSerializable> extends Base {
    private TDomain baseData;
    private Class<TDomain> generic_type_TDomain = ((Class) getGenericTypeArguments()[0]);

    public static class Schema {
        private static final Metadata baseData_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("Data");
            metadata.setQualified_name("Microsoft.Telemetry.Data");
            metadata.getAttributes().put("Description", "Data struct to contain both B and C sections.");
            baseData_metadata.setName("baseData");
            baseData_metadata.setModifier(Modifier.Required);
            baseData_metadata.getAttributes().put("Name", "Item");
            baseData_metadata.getAttributes().put("Description", "Container for data item (B section).");
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
            structDef.setBase_def(Microsoft.Telemetry.Base.Schema.getTypeDef(schemaDef));
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(baseData_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRUCT);
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

    private Type[] getGenericTypeArguments() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_baseData(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        try {
            this.baseData = (BondSerializable) this.generic_type_TDomain.newInstance();
            this.baseData.readNested(protocolReader);
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e2) {
        }
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public final TDomain getBaseData() {
        return this.baseData;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.URSH /*20*/:
                return this.baseData;
            default:
                return null;
        }
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
        Data data = (Data) obj;
        return memberwiseCompareQuick(data) && memberwiseCompareDeep(data);
    }

    protected boolean memberwiseCompareDeep(Data<TDomain> data) {
        return super.memberwiseCompareDeep(data);
    }

    protected boolean memberwiseCompareQuick(Data<TDomain> data) {
        return super.memberwiseCompareQuick(data);
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
                        case Token.URSH /*20*/:
                            readFieldImpl_baseData(protocolReader, readFieldBegin.type);
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
            readFieldImpl_baseData(protocolReader, BondDataType.BT_STRUCT);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("Data", "Microsoft.Telemetry.Data");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.baseData = null;
    }

    public final void setBaseData(TDomain tDomain) {
        this.baseData = tDomain;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.URSH /*20*/:
                this.baseData = (BondSerializable) obj;
                return;
            default:
                return;
        }
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
        protocolWriter.writeFieldBegin(BondDataType.BT_STRUCT, 20, Schema.baseData_metadata);
        this.baseData.writeNested(protocolWriter, false);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
