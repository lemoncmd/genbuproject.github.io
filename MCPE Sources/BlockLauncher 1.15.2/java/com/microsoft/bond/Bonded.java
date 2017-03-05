package com.microsoft.bond;

import com.microsoft.bond.internal.Marshaler;
import java.io.IOException;
import java.io.InputStream;

public class Bonded<T extends BondSerializable> implements BondSerializable {
    private ProtocolReader Data;
    private T Value;

    public Bonded(T t) {
        this.Value = t;
    }

    public Bonded(ProtocolReader protocolReader) throws IOException {
        read(protocolReader);
    }

    public Bonded(ProtocolReader protocolReader, SchemaDef schemaDef) throws IOException {
        read(protocolReader, schemaDef);
    }

    public BondSerializable clone() {
        if (this.Data == null) {
            return new Bonded(this.Value);
        }
        try {
            return new Bonded(this.Data.cloneReader());
        } catch (IOException e) {
            return null;
        }
    }

    public void deserialize(BondSerializable bondSerializable) throws IOException {
        bondSerializable.read(this.Data);
    }

    public T getValue() {
        return this.Value;
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        return this.Value != null ? this.Value.memberwiseCompare(obj) : false;
    }

    public void read(ProtocolReader protocolReader) throws IOException {
        readNested(protocolReader);
    }

    public void read(ProtocolReader protocolReader, BondSerializable bondSerializable) throws IOException {
        readNested(protocolReader);
    }

    public void readNested(ProtocolReader protocolReader) throws IOException {
        this.Value = null;
        this.Data = protocolReader.cloneReader();
        protocolReader.skip(BondDataType.BT_STRUCT);
    }

    public void reset() {
        this.Value = null;
        this.Data = null;
    }

    public void unmarshal(InputStream inputStream) throws IOException {
        Marshaler.unmarshal(inputStream, this);
    }

    public void unmarshal(InputStream inputStream, BondSerializable bondSerializable) throws IOException {
        Marshaler.unmarshal(inputStream, (SchemaDef) bondSerializable, this);
    }

    public void write(ProtocolWriter protocolWriter) throws IOException {
        if (this.Data != null) {
            Transcoder.transcode(protocolWriter, this.Data.cloneReader());
        } else {
            this.Value.write(protocolWriter);
        }
    }

    public void writeNested(ProtocolWriter protocolWriter, boolean z) throws IOException {
        write(protocolWriter);
    }
}
