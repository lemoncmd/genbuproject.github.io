package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Vector<T extends BondSerializable, U extends BondSerializable> implements BondSerializable, BondMirror {
    private Class<T> generic_type_T;
    private Class<U> generic_type_U;
    private ArrayList<T> value;

    public static class Schema {
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        private static final Metadata value_metadata = new Metadata();

        static {
            metadata.setName("Vector");
            metadata.setQualified_name("com.microsoft.bond.Vector");
            value_metadata.setName(ES6Iterator.VALUE_PROPERTY);
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
            fieldDef.setMetadata(value_metadata);
            fieldDef.getType().setId(BondDataType.BT_LIST);
            fieldDef.getType().setElement(new TypeDef());
            fieldDef.getType().getElement().setId(BondDataType.BT_STRUCT);
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

    public Vector() {
        Type[] genericTypeArguments = getGenericTypeArguments();
        this.generic_type_T = (Class) genericTypeArguments[0];
        this.generic_type_U = (Class) genericTypeArguments[1];
        reset();
    }

    private Type[] getGenericTypeArguments() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    private void readFieldImpl_value(ProtocolReader protocolReader, BondDataType bondDataType) throws IOException {
        ReadHelper.validateType(bondDataType, BondDataType.BT_LIST);
        ListTag readContainerBegin = protocolReader.readContainerBegin();
        this.value.ensureCapacity(readContainerBegin.size);
        for (int i = 0; i < readContainerBegin.size; i++) {
            Object obj;
            try {
                obj = (BondSerializable) this.generic_type_T.newInstance();
                try {
                    obj.readNested(protocolReader);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e2) {
                }
            } catch (InstantiationException e3) {
                obj = null;
            } catch (IllegalAccessException e4) {
                obj = null;
            }
            this.value.add(obj);
        }
        protocolReader.readContainerEnd();
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                return this.value;
            default:
                return null;
        }
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public final ArrayList<T> getValue() {
        return this.value;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Vector vector = (Vector) obj;
        return memberwiseCompareQuick(vector) && memberwiseCompareDeep(vector);
    }

    protected boolean memberwiseCompareDeep(Vector<T, U> vector) {
        if (!(this.value == null || this.value.size() == 0)) {
            for (int i = 0; i < this.value.size(); i++) {
                BondSerializable bondSerializable = (BondSerializable) this.value.get(i);
                bondSerializable = (BondSerializable) vector.value.get(i);
            }
        }
        return true;
    }

    protected boolean memberwiseCompareQuick(Vector<T, U> vector) {
        return ((this.value == null) == (vector.value == null)) && (this.value == null || this.value.size() == vector.value.size());
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
                    readFieldImpl_value(protocolReader, readFieldBegin.type);
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
            readFieldImpl_value(protocolReader, BondDataType.BT_LIST);
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("Vector", "com.microsoft.bond.Vector");
    }

    protected void reset(String str, String str2) {
        if (this.value == null) {
            this.value = new ArrayList();
        } else {
            this.value.clear();
        }
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case NativeRegExp.TEST /*0*/:
                this.value = (ArrayList) obj;
                return;
            default:
                return;
        }
    }

    public final void setValue(ArrayList<T> arrayList) {
        this.value = arrayList;
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
        int size = this.value.size();
        if (hasCapability && size == 0) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_LIST, 0, Schema.value_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_LIST, 0, Schema.value_metadata);
            protocolWriter.writeContainerBegin(size, BondDataType.BT_STRUCT);
            Iterator it = this.value.iterator();
            while (it.hasNext()) {
                ((BondSerializable) it.next()).writeNested(protocolWriter, false);
            }
            protocolWriter.writeContainerEnd();
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
