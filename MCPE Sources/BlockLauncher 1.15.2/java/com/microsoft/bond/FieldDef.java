package com.microsoft.bond;

import com.ipaulpro.afilechooser.utils.MimeTypeParser;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FieldDef implements BondMirror, BondSerializable {
    private short id;
    private Metadata metadata;
    private TypeDef type;

    public static class Schema {
        private static final Metadata id_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata metadata_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata type_metadata = new Metadata();

        static {
            metadata.setName("FieldDef");
            metadata.setQualified_name("com.microsoft.bond.FieldDef");
            metadata_metadata.setName("metadata");
            id_metadata.setName(Name.MARK);
            id_metadata.getDefault_value().setUint_value(0);
            type_metadata.setName(MimeTypeParser.TAG_TYPE);
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
            fieldDef.setId((short) 0);
            fieldDef.setMetadata(metadata_metadata);
            fieldDef.setType(com.microsoft.bond.Metadata.Schema.getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(id_metadata);
            fieldDef2.getType().setId(BondDataType.BT_UINT16);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(type_metadata);
            fieldDef2.setType(com.microsoft.bond.TypeDef.Schema.getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef2);
            return s;
        }

        public static TypeDef getTypeDef(SchemaDef schemaDef) {
            TypeDef typeDef = new TypeDef();
            typeDef.setId(BondDataType.BT_STRUCT);
            typeDef.setStruct_def(getStructDef(schemaDef));
            return typeDef;
        }
    }

    public FieldDef() {
        reset();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return com.microsoft.bond.Metadata.Schema.metadata == structDef.getMetadata() ? new Metadata() : com.microsoft.bond.TypeDef.Schema.metadata == structDef.getMetadata() ? new TypeDef() : null;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.metadata;
            case NativeRegExp.MATCH /*1*/:
                return Short.valueOf(this.id);
            case NativeRegExp.PREFIX /*2*/:
                return this.type;
            default:
                return null;
        }
    }

    public final short getId() {
        return this.id;
    }

    public final Metadata getMetadata() {
        return this.metadata;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final TypeDef getType() {
        return this.type;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        FieldDef fieldDef = (FieldDef) obj;
        return memberwiseCompareQuick(fieldDef) && memberwiseCompareDeep(fieldDef);
    }

    protected boolean memberwiseCompareDeep(FieldDef fieldDef) {
        boolean z = this.metadata == null || this.metadata.memberwiseCompare(fieldDef.metadata);
        return z && (this.type == null || this.type.memberwiseCompare(fieldDef.type));
    }

    protected boolean memberwiseCompareQuick(FieldDef fieldDef) {
        return this.id == fieldDef.id;
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
                case NativeRegExp.TEST /*0*/:
                    ReadHelper.validateType(readFieldBegin.type, BondDataType.BT_STRUCT);
                    this.metadata.readNested(protocolReader);
                    break;
                case NativeRegExp.MATCH /*1*/:
                    this.id = ReadHelper.readUInt16(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    ReadHelper.validateType(readFieldBegin.type, BondDataType.BT_STRUCT);
                    this.type.readNested(protocolReader);
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
            this.metadata.read(protocolReader);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.id = protocolReader.readUInt16();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.type.read(protocolReader);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("FieldDef", "com.microsoft.bond.FieldDef");
    }

    protected void reset(String str, String str2) {
        this.metadata = new Metadata();
        this.id = (short) 0;
        this.type = new TypeDef();
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.metadata = (Metadata) obj;
                return;
            case NativeRegExp.MATCH /*1*/:
                this.id = ((Short) obj).shortValue();
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.type = (TypeDef) obj;
                return;
            default:
                return;
        }
    }

    public final void setId(short s) {
        this.id = s;
    }

    public final void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public final void setType(TypeDef typeDef) {
        this.type = typeDef;
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
        protocolWriter.writeFieldBegin(BondDataType.BT_STRUCT, 0, Schema.metadata_metadata);
        this.metadata.writeNested(protocolWriter, false);
        protocolWriter.writeFieldEnd();
        if (hasCapability && ((long) this.id) == Schema.id_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT16, 1, Schema.id_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT16, 1, Schema.id_metadata);
            protocolWriter.writeUInt16(this.id);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_STRUCT, 2, Schema.type_metadata);
        this.type.writeNested(protocolWriter, false);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
