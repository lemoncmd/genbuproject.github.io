package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class GUID implements BondMirror, BondSerializable {
    private int Data1;
    private short Data2;
    private short Data3;
    private long Data4;

    public static class Schema {
        private static final Metadata Data1_metadata = new Metadata();
        private static final Metadata Data2_metadata = new Metadata();
        private static final Metadata Data3_metadata = new Metadata();
        private static final Metadata Data4_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("GUID");
            metadata.setQualified_name("com.microsoft.bond.GUID");
            Data1_metadata.setName("Data1");
            Data1_metadata.getDefault_value().setUint_value(0);
            Data2_metadata.setName("Data2");
            Data2_metadata.getDefault_value().setUint_value(0);
            Data3_metadata.setName("Data3");
            Data3_metadata.getDefault_value().setUint_value(0);
            Data4_metadata.setName("Data4");
            Data4_metadata.getDefault_value().setUint_value(0);
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
            fieldDef.setMetadata(Data1_metadata);
            fieldDef.getType().setId(BondDataType.BT_UINT32);
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(Data2_metadata);
            fieldDef2.getType().setId(BondDataType.BT_UINT16);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(Data3_metadata);
            fieldDef2.getType().setId(BondDataType.BT_UINT16);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 3);
            fieldDef2.setMetadata(Data4_metadata);
            fieldDef2.getType().setId(BondDataType.BT_UINT64);
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

    public GUID() {
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

    public final int getData1() {
        return this.Data1;
    }

    public final short getData2() {
        return this.Data2;
    }

    public final short getData3() {
        return this.Data3;
    }

    public final long getData4() {
        return this.Data4;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return Integer.valueOf(this.Data1);
            case NativeRegExp.MATCH /*1*/:
                return Short.valueOf(this.Data2);
            case NativeRegExp.PREFIX /*2*/:
                return Short.valueOf(this.Data3);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return Long.valueOf(this.Data4);
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
        GUID guid = (GUID) obj;
        return memberwiseCompareQuick(guid) && memberwiseCompareDeep(guid);
    }

    protected boolean memberwiseCompareDeep(GUID guid) {
        return true;
    }

    protected boolean memberwiseCompareQuick(GUID guid) {
        boolean z = (this.Data1 == guid.Data1) && this.Data2 == guid.Data2;
        z = z && this.Data3 == guid.Data3;
        return z && this.Data4 == guid.Data4;
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
                    this.Data1 = ReadHelper.readUInt32(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.MATCH /*1*/:
                    this.Data2 = ReadHelper.readUInt16(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    this.Data3 = ReadHelper.readUInt16(protocolReader, readFieldBegin.type);
                    break;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    this.Data4 = ReadHelper.readUInt64(protocolReader, readFieldBegin.type);
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
            this.Data1 = protocolReader.readUInt32();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.Data2 = protocolReader.readUInt16();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.Data3 = protocolReader.readUInt16();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.Data4 = protocolReader.readUInt64();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("GUID", "com.microsoft.bond.GUID");
    }

    protected void reset(String str, String str2) {
        this.Data1 = 0;
        this.Data2 = (short) 0;
        this.Data3 = (short) 0;
        this.Data4 = 0;
    }

    public final void setData1(int i) {
        this.Data1 = i;
    }

    public final void setData2(short s) {
        this.Data2 = s;
    }

    public final void setData3(short s) {
        this.Data3 = s;
    }

    public final void setData4(long j) {
        this.Data4 = j;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.Data1 = ((Integer) obj).intValue();
                return;
            case NativeRegExp.MATCH /*1*/:
                this.Data2 = ((Short) obj).shortValue();
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.Data3 = ((Short) obj).shortValue();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.Data4 = ((Long) obj).longValue();
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
        boolean hasCapability = protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        if (hasCapability && ((long) this.Data1) == Schema.Data1_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT32, 0, Schema.Data1_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT32, 0, Schema.Data1_metadata);
            protocolWriter.writeUInt32(this.Data1);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && ((long) this.Data2) == Schema.Data2_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT16, 1, Schema.Data2_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT16, 1, Schema.Data2_metadata);
            protocolWriter.writeUInt16(this.Data2);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && ((long) this.Data3) == Schema.Data3_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT16, 2, Schema.Data3_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT16, 2, Schema.Data3_metadata);
            protocolWriter.writeUInt16(this.Data3);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.Data4 == Schema.Data4_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT64, 3, Schema.Data4_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT64, 3, Schema.Data4_metadata);
            protocolWriter.writeUInt64(this.Data4);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
