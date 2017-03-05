package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import net.hockeyapp.android.BuildConfig;

public class SerializableExceptionBase implements BondMirror, BondSerializable {
    private String m_message;

    public static class Schema {
        private static final Metadata m_message_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("SerializableExceptionBase");
            metadata.setQualified_name("com.microsoft.bond.SerializableExceptionBase");
            m_message_metadata.setName("m_message");
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
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 8189);
            fieldDef.setMetadata(m_message_metadata);
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

    public SerializableExceptionBase() {
        reset();
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
            case (short) 8189:
                return this.m_message;
            default:
                return null;
        }
    }

    public final String getM_message() {
        return this.m_message;
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
        SerializableExceptionBase serializableExceptionBase = (SerializableExceptionBase) obj;
        return memberwiseCompareQuick(serializableExceptionBase) && memberwiseCompareDeep(serializableExceptionBase);
    }

    protected boolean memberwiseCompareDeep(SerializableExceptionBase serializableExceptionBase) {
        return this.m_message == null || this.m_message.equals(serializableExceptionBase.m_message);
    }

    protected boolean memberwiseCompareQuick(SerializableExceptionBase serializableExceptionBase) {
        return ((this.m_message == null) == (serializableExceptionBase.m_message == null)) && (this.m_message == null || this.m_message.length() == serializableExceptionBase.m_message.length());
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
        protocolReader.readStructBegin(z);
        while (true) {
            FieldTag readFieldBegin = protocolReader.readFieldBegin();
            if (readFieldBegin.type == BondDataType.BT_STOP || readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                boolean z2 = readFieldBegin.type == BondDataType.BT_STOP_BASE;
                protocolReader.readStructEnd();
                return z2;
            }
            switch (readFieldBegin.id) {
                case 8189:
                    this.m_message = ReadHelper.readString(protocolReader, readFieldBegin.type);
                    break;
                default:
                    protocolReader.skip(readFieldBegin.type);
                    break;
            }
            protocolReader.readFieldEnd();
        }
    }

    protected void readUntagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean hasCapability = protocolReader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolReader.readStructBegin(z);
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.m_message = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("SerializableExceptionBase", "com.microsoft.bond.SerializableExceptionBase");
    }

    protected void reset(String str, String str2) {
        this.m_message = BuildConfig.FLAVOR;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case (short) 8189:
                this.m_message = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setM_message(String str) {
        this.m_message = str;
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
        if (hasCapability && this.m_message == Schema.m_message_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 8189, Schema.m_message_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 8189, Schema.m_message_metadata);
            protocolWriter.writeString(this.m_message);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
