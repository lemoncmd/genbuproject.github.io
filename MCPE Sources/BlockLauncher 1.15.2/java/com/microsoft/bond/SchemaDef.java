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

public class SchemaDef implements BondMirror, BondSerializable {
    private TypeDef root;
    private ArrayList<StructDef> structs;

    public static class Schema {
        public static final Metadata metadata = new Metadata();
        private static final Metadata root_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata structs_metadata = new Metadata();

        static {
            metadata.setName("SchemaDef");
            metadata.setQualified_name("com.microsoft.bond.SchemaDef");
            structs_metadata.setName("structs");
            root_metadata.setName("root");
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
            fieldDef.setMetadata(structs_metadata);
            fieldDef.getType().setId(BondDataType.BT_LIST);
            fieldDef.getType().setElement(new TypeDef());
            fieldDef.getType().setElement(com.microsoft.bond.StructDef.Schema.getTypeDef(schemaDef));
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(root_metadata);
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

    public SchemaDef() {
        reset();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_structs(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        ReadHelper.validateType(readContainerBegin.type, BondDataType.BT_STRUCT);
        this.structs.ensureCapacity(readContainerBegin.size);
        for (int i = 0; i < readContainerBegin.size; i++) {
            StructDef structDef = new StructDef();
            structDef.readNested(protocolReader);
            this.structs.add(structDef);
        }
        protocolReader.readContainerEnd();
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return com.microsoft.bond.StructDef.Schema.metadata == structDef.getMetadata() ? new StructDef() : com.microsoft.bond.TypeDef.Schema.metadata == structDef.getMetadata() ? new TypeDef() : null;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.structs;
            case NativeRegExp.MATCH /*1*/:
                return this.root;
            default:
                return null;
        }
    }

    public final TypeDef getRoot() {
        return this.root;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final ArrayList<StructDef> getStructs() {
        return this.structs;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        SchemaDef schemaDef = (SchemaDef) obj;
        return memberwiseCompareQuick(schemaDef) && memberwiseCompareDeep(schemaDef);
    }

    protected boolean memberwiseCompareDeep(SchemaDef schemaDef) {
        boolean z;
        if (this.structs == null || this.structs.size() == 0) {
            z = true;
        } else {
            int i = 0;
            boolean z2 = true;
            while (i < this.structs.size()) {
                StructDef structDef = (StructDef) this.structs.get(i);
                StructDef structDef2 = (StructDef) schemaDef.structs.get(i);
                if (z2) {
                    if ((structDef == null) == (structDef2 == null)) {
                        z2 = true;
                        z = z2 && (structDef == null || structDef.memberwiseCompare(structDef2));
                        if (!z) {
                            break;
                        }
                        i++;
                        z2 = z;
                    }
                }
                z2 = false;
                if (!z2) {
                }
                if (!z) {
                    break;
                }
                i++;
                z2 = z;
            }
            z = z2;
        }
        return z && (this.root == null || this.root.memberwiseCompare(schemaDef.root));
    }

    protected boolean memberwiseCompareQuick(SchemaDef schemaDef) {
        return ((this.structs == null) == (schemaDef.structs == null)) && (this.structs == null || this.structs.size() == schemaDef.structs.size());
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
                    readFieldImpl_structs(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.MATCH /*1*/:
                    ReadHelper.validateType(readFieldBegin.type, BondDataType.BT_STRUCT);
                    this.root.readNested(protocolReader);
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
            readFieldImpl_structs(protocolReader, BondDataType.BT_LIST);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.root.read(protocolReader);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("SchemaDef", "com.microsoft.bond.SchemaDef");
    }

    protected void reset(String str, String str2) {
        if (this.structs == null) {
            this.structs = new ArrayList();
        } else {
            this.structs.clear();
        }
        this.root = new TypeDef();
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.structs = (ArrayList) obj;
                return;
            case NativeRegExp.MATCH /*1*/:
                this.root = (TypeDef) obj;
                return;
            default:
                return;
        }
    }

    public final void setRoot(TypeDef typeDef) {
        this.root = typeDef;
    }

    public final void setStructs(ArrayList<StructDef> arrayList) {
        this.structs = arrayList;
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
        int size = this.structs.size();
        if (hasCapability && size == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 0, Schema.structs_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 0, Schema.structs_metadata);
            protocolWriter.writeContainerBegin(size, BondDataType.BT_STRUCT);
            Iterator it = this.structs.iterator();
            while (it.hasNext()) {
                ((StructDef) it.next()).writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_STRUCT, 1, Schema.root_metadata);
        this.root.writeNested(protocolWriter, false);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
