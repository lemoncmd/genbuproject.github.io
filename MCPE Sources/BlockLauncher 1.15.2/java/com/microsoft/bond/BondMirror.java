package com.microsoft.bond;

public interface BondMirror {
    BondMirror createInstance(StructDef structDef);

    Object getField(FieldDef fieldDef);

    SchemaDef getSchema();

    void setField(FieldDef fieldDef, Object obj);
}
