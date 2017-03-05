package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Variant implements BondMirror, BondSerializable {
    private double double_value;
    private long int_value;
    private boolean nothing;
    private String string_value;
    private long uint_value;
    private String wstring_value;

    public static class Schema {
        private static final Metadata double_value_metadata = new Metadata();
        private static final Metadata int_value_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata nothing_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata string_value_metadata = new Metadata();
        private static final Metadata uint_value_metadata = new Metadata();
        private static final Metadata wstring_value_metadata = new Metadata();

        static {
            metadata.setName("Variant");
            metadata.setQualified_name("com.microsoft.bond.Variant");
            uint_value_metadata.setName("uint_value");
            uint_value_metadata.getDefault_value().setUint_value(0);
            int_value_metadata.setName("int_value");
            int_value_metadata.getDefault_value().setInt_value(0);
            double_value_metadata.setName("double_value");
            double_value_metadata.getDefault_value().setDouble_value(0.0d);
            string_value_metadata.setName("string_value");
            wstring_value_metadata.setName("wstring_value");
            nothing_metadata.setName("nothing");
            nothing_metadata.getDefault_value().setUint_value(0);
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
            fieldDef.setMetadata(uint_value_metadata);
            fieldDef.getType().setId(BondDataType.BT_UINT64);
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(int_value_metadata);
            fieldDef2.getType().setId(BondDataType.BT_INT64);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(double_value_metadata);
            fieldDef2.getType().setId(BondDataType.BT_DOUBLE);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 3);
            fieldDef2.setMetadata(string_value_metadata);
            fieldDef2.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 4);
            fieldDef2.setMetadata(wstring_value_metadata);
            fieldDef2.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 5);
            fieldDef2.setMetadata(nothing_metadata);
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

    public Variant() {
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

    public final double getDouble_value() {
        return this.double_value;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return Long.valueOf(this.uint_value);
            case NativeRegExp.MATCH /*1*/:
                return Long.valueOf(this.int_value);
            case NativeRegExp.PREFIX /*2*/:
                return Double.valueOf(this.double_value);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return this.string_value;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return this.wstring_value;
            case Token.GOTO /*5*/:
                return Boolean.valueOf(this.nothing);
            default:
                return null;
        }
    }

    public final long getInt_value() {
        return this.int_value;
    }

    public final boolean getNothing() {
        return this.nothing;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final String getString_value() {
        return this.string_value;
    }

    public final long getUint_value() {
        return this.uint_value;
    }

    public final String getWstring_value() {
        return this.wstring_value;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Variant variant = (Variant) obj;
        return memberwiseCompareQuick(variant) && memberwiseCompareDeep(variant);
    }

    protected boolean memberwiseCompareDeep(Variant variant) {
        boolean z = this.string_value == null || this.string_value.equals(variant.string_value);
        return z && (this.wstring_value == null || this.wstring_value.equals(variant.wstring_value));
    }

    protected boolean memberwiseCompareQuick(Variant variant) {
        boolean z = ((this.uint_value > variant.uint_value ? 1 : (this.uint_value == variant.uint_value ? 0 : -1)) == 0) && this.int_value == variant.int_value;
        z = z && (Double.isNaN(this.double_value) ? Double.isNaN(variant.double_value) : this.double_value == variant.double_value);
        if (z) {
            if ((this.string_value == null) == (variant.string_value == null)) {
                z = true;
                z = z && (this.string_value == null || this.string_value.length() == variant.string_value.length());
                if (z) {
                    if ((this.wstring_value != null) == (variant.wstring_value != null)) {
                        z = true;
                        z = z && (this.wstring_value == null || this.wstring_value.length() == variant.wstring_value.length());
                        return z && this.nothing == variant.nothing;
                    }
                }
                z = false;
                if (!z) {
                }
                if (!z) {
                }
            }
        }
        z = false;
        if (!z) {
        }
        if (z) {
            if (this.wstring_value != null) {
            }
            if (variant.wstring_value != null) {
            }
            if ((this.wstring_value != null) == (variant.wstring_value != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
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
                    this.uint_value = ReadHelper.readUInt64(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.MATCH /*1*/:
                    this.int_value = ReadHelper.readInt64(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    this.double_value = ReadHelper.readDouble(protocolReader, readFieldBegin.type);
                    break;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    this.string_value = ReadHelper.readString(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    this.wstring_value = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                    break;
                case Token.GOTO /*5*/:
                    this.nothing = ReadHelper.readBool(protocolReader, readFieldBegin.type);
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
            this.uint_value = protocolReader.readUInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.int_value = protocolReader.readInt64();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.double_value = protocolReader.readDouble();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.string_value = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.wstring_value = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.nothing = protocolReader.readBool();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("Variant", "com.microsoft.bond.Variant");
    }

    protected void reset(String str, String str2) {
        this.uint_value = 0;
        this.int_value = 0;
        this.double_value = 0.0d;
        this.string_value = BuildConfig.FLAVOR;
        this.wstring_value = BuildConfig.FLAVOR;
        this.nothing = false;
    }

    public final void setDouble_value(double d) {
        this.double_value = d;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.uint_value = ((Long) obj).longValue();
                return;
            case NativeRegExp.MATCH /*1*/:
                this.int_value = ((Long) obj).longValue();
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.double_value = ((Double) obj).doubleValue();
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.string_value = (String) obj;
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                this.wstring_value = (String) obj;
                return;
            case Token.GOTO /*5*/:
                this.nothing = ((Boolean) obj).booleanValue();
                return;
            default:
                return;
        }
    }

    public final void setInt_value(long j) {
        this.int_value = j;
    }

    public final void setNothing(boolean z) {
        this.nothing = z;
    }

    public final void setString_value(String str) {
        this.string_value = str;
    }

    public final void setUint_value(long j) {
        this.uint_value = j;
    }

    public final void setWstring_value(String str) {
        this.wstring_value = str;
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
        if (hasCapability && this.uint_value == Schema.uint_value_metadata.getDefault_value().getUint_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_UINT64, 0, Schema.uint_value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_UINT64, 0, Schema.uint_value_metadata);
            protocolWriter.writeUInt64(this.uint_value);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.int_value == Schema.int_value_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT64, 1, Schema.int_value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT64, 1, Schema.int_value_metadata);
            protocolWriter.writeInt64(this.int_value);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.double_value == Schema.double_value_metadata.getDefault_value().getDouble_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_DOUBLE, 2, Schema.double_value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_DOUBLE, 2, Schema.double_value_metadata);
            protocolWriter.writeDouble(this.double_value);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.string_value == Schema.string_value_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 3, Schema.string_value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 3, Schema.string_value_metadata);
            protocolWriter.writeString(this.string_value);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.wstring_value == Schema.wstring_value_metadata.getDefault_value().getWstring_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_WSTRING, 4, Schema.wstring_value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 4, Schema.wstring_value_metadata);
            protocolWriter.writeWString(this.wstring_value);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability) {
            hasCapability = this.nothing;
            if (Schema.nothing_metadata.getDefault_value().getUint_value() != 0) {
                z2 = true;
            }
            if (hasCapability == z2) {
                protocolWriter.writeFieldOmitted(BondDataType.BT_BOOL, 5, Schema.nothing_metadata);
                protocolWriter.writeStructEnd(z);
            }
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_BOOL, 5, Schema.nothing_metadata);
        protocolWriter.writeBool(this.nothing);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
