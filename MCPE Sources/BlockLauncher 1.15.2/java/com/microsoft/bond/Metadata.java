package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.MapTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Metadata implements BondMirror, BondSerializable {
    private HashMap<String, String> attributes;
    private Variant default_value;
    private Modifier modifier;
    private String name;
    private String qualified_name;

    public static class Schema {
        private static final Metadata attributes_metadata = new Metadata();
        private static final Metadata default_value_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata modifier_metadata = new Metadata();
        private static final Metadata name_metadata = new Metadata();
        private static final Metadata qualified_name_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("Metadata");
            metadata.setQualified_name("com.microsoft.bond.Metadata");
            name_metadata.setName("name");
            qualified_name_metadata.setName("qualified_name");
            attributes_metadata.setName("attributes");
            modifier_metadata.setName("modifier");
            modifier_metadata.getDefault_value().setInt_value((long) Modifier.Optional.getValue());
            default_value_metadata.setName("default_value");
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
            fieldDef.setMetadata(name_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            FieldDef fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 1);
            fieldDef2.setMetadata(qualified_name_metadata);
            fieldDef2.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 2);
            fieldDef2.setMetadata(attributes_metadata);
            fieldDef2.getType().setId(BondDataType.BT_MAP);
            fieldDef2.getType().setKey(new TypeDef());
            fieldDef2.getType().setElement(new TypeDef());
            fieldDef2.getType().getKey().setId(BondDataType.BT_STRING);
            fieldDef2.getType().getElement().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 3);
            fieldDef2.setMetadata(modifier_metadata);
            fieldDef2.getType().setId(BondDataType.BT_INT32);
            structDef.getFields().add(fieldDef2);
            fieldDef2 = new FieldDef();
            fieldDef2.setId((short) 4);
            fieldDef2.setMetadata(default_value_metadata);
            fieldDef2.setType(com.microsoft.bond.Variant.Schema.getTypeDef(schemaDef));
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

    public Metadata() {
        reset();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_attributes(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_MAP);
        MapTag readMapContainerBegin = protocolReader.readMapContainerBegin();
        for (int i = 0; i < readMapContainerBegin.size; i++) {
            this.attributes.put(ReadHelper.readString(protocolReader, readMapContainerBegin.keyType), ReadHelper.readString(protocolReader, readMapContainerBegin.valueType));
        }
        protocolReader.readContainerEnd();
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return com.microsoft.bond.Variant.Schema.metadata == structDef.getMetadata() ? new Variant() : null;
    }

    public final HashMap<String, String> getAttributes() {
        return this.attributes;
    }

    public final Variant getDefault_value() {
        return this.default_value;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.name;
            case NativeRegExp.MATCH /*1*/:
                return this.qualified_name;
            case NativeRegExp.PREFIX /*2*/:
                return this.attributes;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return this.modifier;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return this.default_value;
            default:
                return null;
        }
    }

    public final Modifier getModifier() {
        return this.modifier;
    }

    public final String getName() {
        return this.name;
    }

    public final String getQualified_name() {
        return this.qualified_name;
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
        Metadata metadata = (Metadata) obj;
        return memberwiseCompareQuick(metadata) && memberwiseCompareDeep(metadata);
    }

    protected boolean memberwiseCompareDeep(Metadata metadata) {
        Object obj = (this.name == null || this.name.equals(metadata.name)) ? 1 : null;
        obj = (obj == null || !(this.qualified_name == null || this.qualified_name.equals(metadata.qualified_name))) ? null : 1;
        if (obj != null && this.attributes != null && this.attributes.size() != 0) {
            Object obj2 = obj;
            for (Entry entry : this.attributes.entrySet()) {
                String str = (String) entry.getValue();
                String str2 = (String) metadata.attributes.get(entry.getKey());
                obj = (obj2 == null || !metadata.attributes.containsKey(entry.getKey())) ? null : 1;
                if (obj != null) {
                    if (obj != null) {
                        if ((str == null ? 1 : null) == (str2 == null ? 1 : null)) {
                            obj = 1;
                            obj = (obj == null && (str == null || str.length() == str2.length())) ? 1 : null;
                            obj = (obj == null && (str == null || str.equals(str2))) ? 1 : null;
                        }
                    }
                    obj = null;
                    if (obj == null) {
                    }
                    if (obj == null) {
                    }
                }
                if (obj == null) {
                    break;
                }
                obj2 = obj;
            }
            obj = obj2;
        }
        return obj != null && (this.default_value == null || this.default_value.memberwiseCompare(metadata.default_value));
    }

    protected boolean memberwiseCompareQuick(Metadata metadata) {
        boolean z = ((this.name == null) == (metadata.name == null)) && (this.name == null || this.name.length() == metadata.name.length());
        if (z) {
            if ((this.qualified_name == null) == (metadata.qualified_name == null)) {
                z = true;
                z = z && (this.qualified_name == null || this.qualified_name.length() == metadata.qualified_name.length());
                if (z) {
                    if ((this.attributes != null) == (metadata.attributes != null)) {
                        z = true;
                        z = z && (this.attributes == null || this.attributes.size() == metadata.attributes.size());
                        return z && this.modifier == metadata.modifier;
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
            if (this.attributes != null) {
            }
            if (metadata.attributes != null) {
            }
            if ((this.attributes != null) == (metadata.attributes != null)) {
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
                    this.name = ReadHelper.readString(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.MATCH /*1*/:
                    this.qualified_name = ReadHelper.readString(protocolReader, readFieldBegin.type);
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    readFieldImpl_attributes(protocolReader, readFieldBegin.type);
                    break;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    this.modifier = Modifier.fromValue(ReadHelper.readInt32(protocolReader, readFieldBegin.type));
                    break;
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    ReadHelper.validateType(readFieldBegin.type, BondDataType.BT_STRUCT);
                    this.default_value.readNested(protocolReader);
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
            this.name = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.qualified_name = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            readFieldImpl_attributes(protocolReader, BondDataType.BT_MAP);
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.modifier = Modifier.fromValue(protocolReader.readInt32());
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.default_value.read(protocolReader);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("Metadata", "com.microsoft.bond.Metadata");
    }

    protected void reset(String str, String str2) {
        this.name = BuildConfig.FLAVOR;
        this.qualified_name = BuildConfig.FLAVOR;
        if (this.attributes == null) {
            this.attributes = new HashMap();
        } else {
            this.attributes.clear();
        }
        this.modifier = Modifier.Optional;
        this.default_value = new Variant();
    }

    public final void setAttributes(HashMap<String, String> hashMap) {
        this.attributes = hashMap;
    }

    public final void setDefault_value(Variant variant) {
        this.default_value = variant;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.name = (String) obj;
                return;
            case NativeRegExp.MATCH /*1*/:
                this.qualified_name = (String) obj;
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.attributes = (HashMap) obj;
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.modifier = (Modifier) obj;
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                this.default_value = (Variant) obj;
                return;
            default:
                return;
        }
    }

    public final void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public final void setName(String str) {
        this.name = str;
    }

    public final void setQualified_name(String str) {
        this.qualified_name = str;
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
        if (hasCapability && this.name == Schema.name_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 0, Schema.name_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 0, Schema.name_metadata);
            protocolWriter.writeString(this.name);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.qualified_name == Schema.qualified_name_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 1, Schema.qualified_name_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 1, Schema.qualified_name_metadata);
            protocolWriter.writeString(this.qualified_name);
            protocolWriter.writeFieldEnd();
        }
        int size = this.attributes.size();
        if (hasCapability && size == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_MAP, 2, Schema.attributes_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_MAP, 2, Schema.attributes_metadata);
            protocolWriter.writeContainerBegin(this.attributes.size(), BondDataType.BT_STRING, BondDataType.BT_STRING);
            for (Entry entry : this.attributes.entrySet()) {
                protocolWriter.writeString((String) entry.getKey());
                protocolWriter.writeString((String) entry.getValue());
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && ((long) this.modifier.getValue()) == Schema.modifier_metadata.getDefault_value().getInt_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_INT32, 3, Schema.modifier_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_INT32, 3, Schema.modifier_metadata);
            protocolWriter.writeInt32(this.modifier.getValue());
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeFieldBegin(BondDataType.BT_STRUCT, 4, Schema.default_value_metadata);
        this.default_value.writeNested(protocolWriter, false);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeStructEnd(z);
    }
}
