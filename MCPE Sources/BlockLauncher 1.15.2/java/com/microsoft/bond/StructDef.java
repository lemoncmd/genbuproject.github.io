package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.mozilla.javascript.regexp.NativeRegExp;

public class StructDef implements BondMirror, BondSerializable {
    private TypeDef base_def;
    private ArrayList<FieldDef> fields;
    private Metadata metadata;

    public static class Schema {
        private static final Metadata base_def_metadata = new Metadata();
        private static final Metadata fields_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata metadata_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("StructDef");
            metadata.setQualified_name("com.microsoft.bond.StructDef");
            metadata_metadata.setName("metadata");
            base_def_metadata.setName("base_def");
            fields_metadata.setName("fields");
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
            fieldDef2.setMetadata(base_def_metadata);
            fieldDef2.getType().setId(BondDataType.BT_LIST);
            fieldDef2.getType().setElement(new TypeDef());
            fieldDef2.getType().setElement(com.microsoft.bond.TypeDef.Schema.getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(fields_metadata);
            fieldDef2.getType().setId(BondDataType.BT_LIST);
            fieldDef2.getType().setElement(new TypeDef());
            fieldDef2.getType().setElement(com.microsoft.bond.FieldDef.Schema.getTypeDef(schemaDef));
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

    public StructDef() {
        reset();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_base_def(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        ReadHelper.validateType(readContainerBegin.type, BondDataType.BT_STRUCT);
        if (readContainerBegin.size == 1) {
            if (this.base_def == null) {
                this.base_def = new TypeDef();
            }
            this.base_def.readNested(protocolReader);
        } else if (readContainerBegin.size != 0) {
        }
        protocolReader.readContainerEnd();
    }

    private void readFieldImpl_fields(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        ReadHelper.validateType(readContainerBegin.type, BondDataType.BT_STRUCT);
        this.fields.ensureCapacity(readContainerBegin.size);
        for (int i = 0; i < readContainerBegin.size; i++) {
            FieldDef fieldDef = new FieldDef();
            fieldDef.readNested(protocolReader);
            this.fields.add(fieldDef);
        }
        protocolReader.readContainerEnd();
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return com.microsoft.bond.Metadata.Schema.metadata == structDef.getMetadata() ? new Metadata() : com.microsoft.bond.TypeDef.Schema.metadata == structDef.getMetadata() ? new TypeDef() : com.microsoft.bond.FieldDef.Schema.metadata == structDef.getMetadata() ? new FieldDef() : null;
    }

    public final TypeDef getBase_def() {
        return this.base_def;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.metadata;
            case NativeRegExp.MATCH /*1*/:
                return this.base_def;
            case NativeRegExp.PREFIX /*2*/:
                return this.fields;
            default:
                return null;
        }
    }

    public final ArrayList<FieldDef> getFields() {
        return this.fields;
    }

    public final Metadata getMetadata() {
        return this.metadata;
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
        StructDef structDef = (StructDef) obj;
        return memberwiseCompareQuick(structDef) && memberwiseCompareDeep(structDef);
    }

    protected boolean memberwiseCompareDeep(StructDef structDef) {
        boolean z = this.metadata == null || this.metadata.memberwiseCompare(structDef.metadata);
        if (z && this.base_def != null) {
            Object obj;
            if (z) {
                if ((this.base_def == null ? 1 : null) == (structDef.base_def == null ? 1 : null)) {
                    obj = 1;
                    z = obj == null && (this.base_def == null || this.base_def.memberwiseCompare(structDef.base_def));
                }
            }
            obj = null;
            if (obj == null) {
            }
        }
        if (!z || this.fields == null || this.fields.size() == 0) {
            return z;
        }
        int i = 0;
        boolean z2 = z;
        while (i < this.fields.size()) {
            Object obj2;
            boolean z3;
            FieldDef fieldDef = (FieldDef) this.fields.get(i);
            FieldDef fieldDef2 = (FieldDef) structDef.fields.get(i);
            if (z2) {
                if ((fieldDef == null ? 1 : null) == (fieldDef2 == null ? 1 : null)) {
                    obj2 = 1;
                    z3 = obj2 == null && (fieldDef == null || fieldDef.memberwiseCompare(fieldDef2));
                    if (!z3) {
                        return z3;
                    }
                    i++;
                    z2 = z3;
                }
            }
            obj2 = null;
            if (obj2 == null) {
            }
            if (!z3) {
                return z3;
            }
            i++;
            z2 = z3;
        }
        return z2;
    }

    protected boolean memberwiseCompareQuick(StructDef structDef) {
        boolean z;
        if ((this.base_def == null) == (structDef.base_def == null)) {
            if ((this.fields == null) == (structDef.fields == null)) {
                z = true;
                return z && (this.fields == null || this.fields.size() == structDef.fields.size());
            }
        }
        z = false;
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
                    readFieldImpl_base_def(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    readFieldImpl_fields(protocolReader, readFieldBegin.type);
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
            readFieldImpl_base_def(protocolReader, BondDataType.BT_LIST);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            readFieldImpl_fields(protocolReader, BondDataType.BT_LIST);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("StructDef", "com.microsoft.bond.StructDef");
    }

    protected void reset(String str, String str2) {
        this.metadata = new Metadata();
        this.base_def = null;
        if (this.fields == null) {
            this.fields = new ArrayList();
        } else {
            this.fields.clear();
        }
    }

    public final void setBase_def(TypeDef typeDef) {
        this.base_def = typeDef;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.metadata = (Metadata) obj;
                return;
            case NativeRegExp.MATCH /*1*/:
                this.base_def = (TypeDef) obj;
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.fields = (ArrayList) obj;
                return;
            default:
                return;
        }
    }

    public final void setFields(ArrayList<FieldDef> arrayList) {
        this.fields = arrayList;
    }

    public final void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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
        int i = this.base_def != null ? 1 : 0;
        if (hasCapability && i == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 1, Schema.base_def_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 1, Schema.base_def_metadata);
            protocolWriter.writeContainerBegin(i, BondDataType.BT_STRUCT);
            if (i != 0) {
                this.base_def.writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        i = this.fields.size();
        if (hasCapability && i == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 2, Schema.fields_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 2, Schema.fields_metadata);
            protocolWriter.writeContainerBegin(i, BondDataType.BT_STRUCT);
            Iterator it = this.fields.iterator();
            while (it.hasNext()) {
                ((FieldDef) it.next()).writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
