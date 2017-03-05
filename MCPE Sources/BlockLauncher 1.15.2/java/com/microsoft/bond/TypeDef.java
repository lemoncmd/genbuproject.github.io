package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class TypeDef implements BondMirror, BondSerializable {
    private boolean bonded_type;
    private TypeDef element;
    private BondDataType id;
    private TypeDef key;
    private short struct_def;

    public static class Schema {
        private static final Metadata bonded_type_metadata = new Metadata();
        private static final Metadata element_metadata = new Metadata();
        private static final Metadata id_metadata = new Metadata();
        private static final Metadata key_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata struct_def_metadata = new Metadata();

        static {
            metadata.setName("TypeDef");
            metadata.setQualified_name("com.microsoft.bond.TypeDef");
            id_metadata.setName(Name.MARK);
            id_metadata.getDefault_value().setInt_value((long) BondDataType.BT_STRUCT.getValue());
            struct_def_metadata.setName("struct_def");
            struct_def_metadata.getDefault_value().setUint_value(0);
            element_metadata.setName("element");
            key_metadata.setName("key");
            bonded_type_metadata.setName("bonded_type");
            bonded_type_metadata.getDefault_value().setUint_value(0);
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
            fieldDef.setMetadata(id_metadata);
            fieldDef.getType().setId(BondDataType.BT_INT32);
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(struct_def_metadata);
            fieldDef2.getType().setId(BondDataType.BT_UINT16);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(element_metadata);
            fieldDef2.getType().setId(BondDataType.BT_LIST);
            fieldDef2.getType().setElement(new TypeDef());
            fieldDef2.getType().setElement(getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 3);
            fieldDef2.setMetadata(key_metadata);
            fieldDef2.getType().setId(BondDataType.BT_LIST);
            fieldDef2.getType().setElement(new TypeDef());
            fieldDef2.getType().setElement(getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 4);
            fieldDef2.setMetadata(bonded_type_metadata);
            fieldDef2.getType().setId(BondDataType.BT_BOOL);
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

    public TypeDef() {
        reset();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_element(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        ReadHelper.validateType(readContainerBegin.type, BondDataType.BT_STRUCT);
        if (readContainerBegin.size == 1) {
            if (this.element == null) {
                this.element = new TypeDef();
            }
            this.element.readNested(protocolReader);
        } else if (readContainerBegin.size != 0) {
        }
        protocolReader.readContainerEnd();
    }

    private void readFieldImpl_key(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        ReadHelper.validateType(readContainerBegin.type, BondDataType.BT_STRUCT);
        if (readContainerBegin.size == 1) {
            if (this.key == null) {
                this.key = new TypeDef();
            }
            this.key.readNested(protocolReader);
        } else if (readContainerBegin.size != 0) {
        }
        protocolReader.readContainerEnd();
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return Schema.metadata == structDef.getMetadata() ? new TypeDef() : null;
    }

    public final boolean getBonded_type() {
        return this.bonded_type;
    }

    public final TypeDef getElement() {
        return this.element;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.id;
            case NativeRegExp.MATCH /*1*/:
                return Short.valueOf(this.struct_def);
            case NativeRegExp.PREFIX /*2*/:
                return this.element;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return this.key;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return Boolean.valueOf(this.bonded_type);
            default:
                return null;
        }
    }

    public final BondDataType getId() {
        return this.id;
    }

    public final TypeDef getKey() {
        return this.key;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final short getStruct_def() {
        return this.struct_def;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        TypeDef typeDef = (TypeDef) obj;
        return memberwiseCompareQuick(typeDef) && memberwiseCompareDeep(typeDef);
    }

    protected boolean memberwiseCompareDeep(TypeDef typeDef) {
        boolean z;
        if (this.element != null) {
            z = ((this.element == null) == (typeDef.element == null)) && (this.element == null || this.element.memberwiseCompare(typeDef.element));
        } else {
            z = true;
        }
        if (!z || this.key == null) {
            return z;
        }
        if (z) {
            if ((this.key == null) == (typeDef.key == null)) {
                z = true;
                return z && (this.key == null || this.key.memberwiseCompare(typeDef.key));
            }
        }
        z = false;
        if (!z) {
        }
    }

    protected boolean memberwiseCompareQuick(TypeDef typeDef) {
        boolean z = (this.id == typeDef.id) && this.struct_def == typeDef.struct_def;
        if (z) {
            if ((this.element == null) == (typeDef.element == null)) {
                z = true;
                if (z) {
                    if ((this.key != null) == (typeDef.key != null)) {
                        z = true;
                        return z && this.bonded_type == typeDef.bonded_type;
                    }
                }
                z = false;
                if (!z) {
                }
            }
        }
        z = false;
        if (z) {
            if (this.key != null) {
            }
            if (typeDef.key != null) {
            }
            if ((this.key != null) == (typeDef.key != null)) {
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
                    this.id = BondDataType.fromValue(ReadHelper.readInt32(protocolReader, readFieldBegin.type));
                    break;
                case NativeRegExp.MATCH /*1*/:
                    this.struct_def = ReadHelper.readUInt16(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    readFieldImpl_element(protocolReader, readFieldBegin.type);
                    break;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    readFieldImpl_key(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    this.bonded_type = ReadHelper.readBool(protocolReader, readFieldBegin.type);
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
            this.id = BondDataType.fromValue(protocolReader.readInt32());
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.struct_def = protocolReader.readUInt16();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            readFieldImpl_element(protocolReader, BondDataType.BT_LIST);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            readFieldImpl_key(protocolReader, BondDataType.BT_LIST);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.bonded_type = protocolReader.readBool();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("TypeDef", "com.microsoft.bond.TypeDef");
    }

    protected void reset(String str, String str2) {
        this.id = BondDataType.BT_STRUCT;
        this.struct_def = (short) 0;
        this.element = null;
        this.key = null;
        this.bonded_type = false;
    }

    public final void setBonded_type(boolean z) {
        this.bonded_type = z;
    }

    public final void setElement(TypeDef typeDef) {
        this.element = typeDef;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.id = (BondDataType) obj;
                return;
            case NativeRegExp.MATCH /*1*/:
                this.struct_def = ((Short) obj).shortValue();
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.element = (TypeDef) obj;
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.key = (TypeDef) obj;
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                this.bonded_type = ((Boolean) obj).booleanValue();
                return;
            default:
                return;
        }
    }

    public final void setId(BondDataType bondDataType) {
        this.id = bondDataType;
    }

    public final void setKey(TypeDef typeDef) {
        this.key = typeDef;
    }

    public final void setStruct_def(short s) {
        this.struct_def = s;
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
        boolean z2 = false;
        boolean hasCapability = protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        if (hasCapability && ((long) this.id.getValue()) == Schema.id_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT32, 0, Schema.id_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT32, 0, Schema.id_metadata);
            protocolWriter.writeInt32(this.id.getValue());
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && ((long) this.struct_def) == Schema.struct_def_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT16, 1, Schema.struct_def_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT16, 1, Schema.struct_def_metadata);
            protocolWriter.writeUInt16(this.struct_def);
            protocolWriter.writeFieldEnd();
        }
        int i = this.element != null ? 1 : 0;
        if (hasCapability && i == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 2, Schema.element_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 2, Schema.element_metadata);
            protocolWriter.writeContainerBegin(i, BondDataType.BT_STRUCT);
            if (i != 0) {
                this.element.writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        i = this.key != null ? 1 : 0;
        if (hasCapability && i == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 3, Schema.key_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 3, Schema.key_metadata);
            protocolWriter.writeContainerBegin(i, BondDataType.BT_STRUCT);
            if (i != 0) {
                this.key.writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability) {
            boolean z3 = this.bonded_type;
            if (Schema.bonded_type_metadata.getDefault_value().getUint_value() != 0) {
                z2 = true;
            }
            if (z3 == z2) {
                protocolWriter.writeFieldOmitted(BondDataType.BT_BOOL, 4, Schema.bonded_type_metadata);
                protocolWriter.writeStructEnd(z);
            }
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_BOOL, 4, Schema.bonded_type_metadata);
        protocolWriter.writeBool(this.bonded_type);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
