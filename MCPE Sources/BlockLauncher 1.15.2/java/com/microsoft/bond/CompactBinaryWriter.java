package com.microsoft.bond;

import com.microsoft.bond.internal.CompactBinaryV2Writer;
import com.microsoft.bond.internal.DecimalHelper;
import com.microsoft.bond.internal.IntegerHelper;
import com.microsoft.bond.internal.StringHelper;
import com.microsoft.bond.io.BondOutputStream;
import java.io.IOException;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage.Header;
import org.mozilla.javascript.regexp.NativeRegExp;

public class CompactBinaryWriter extends ProtocolWriter {
    public static final short MAGIC = ((short) ProtocolType.COMPACT_PROTOCOL.getValue());
    private final BondOutputStream stream;
    private final ProtocolVersion version;
    private final byte[] writeBuffer = new byte[10];

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$ProtocolCapability = new int[ProtocolCapability.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.CAN_OMIT_FIELDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.TAGGED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    protected CompactBinaryWriter(ProtocolVersion protocolVersion, BondOutputStream bondOutputStream) {
        this.version = protocolVersion;
        this.stream = bondOutputStream;
    }

    public static CompactBinaryWriter createV1(BondOutputStream bondOutputStream) {
        return new CompactBinaryWriter(ProtocolVersion.ONE, bondOutputStream);
    }

    public static CompactBinaryWriter createV2(BondOutputStream bondOutputStream) {
        return new CompactBinaryV2Writer(bondOutputStream);
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$ProtocolCapability[protocolCapability.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return true;
            default:
                return super.hasCapability(protocolCapability);
        }
    }

    public String toString() {
        return String.format("[%s version=%d]", new Object[]{getClass().getName(), Short.valueOf(this.version.getValue())});
    }

    public void writeBlob(BondBlob bondBlob) throws IOException {
        this.stream.write(bondBlob.getBuffer(), bondBlob.getOffset(), bondBlob.size());
    }

    public void writeBool(boolean z) throws IOException {
        writeUInt8((byte) (z ? 1 : 0));
    }

    public void writeContainerBegin(int i, BondDataType bondDataType) throws IOException {
        writeUInt8((byte) bondDataType.getValue());
        writeUInt32(i);
    }

    public void writeContainerBegin(int i, BondDataType bondDataType, BondDataType bondDataType2) throws IOException {
        writeUInt8((byte) bondDataType.getValue());
        writeUInt8((byte) bondDataType2.getValue());
        writeUInt32(i);
    }

    public void writeContainerEnd() {
    }

    public void writeDouble(double d) throws IOException {
        DecimalHelper.encodeDouble(d, this.writeBuffer);
        this.stream.write(this.writeBuffer, 0, 8);
    }

    public void writeFieldBegin(BondDataType bondDataType, int i, BondSerializable bondSerializable) throws IOException {
        byte value = (byte) bondDataType.getValue();
        if (i <= 5) {
            this.stream.write(value | (i << 5));
        } else if (i <= 255) {
            this.stream.write(value | Header.ID_INTERLEAVE);
            this.stream.write((byte) i);
        } else {
            this.stream.write(value | 224);
            this.stream.write((byte) i);
            this.stream.write(i >>> 8);
        }
    }

    public void writeFloat(float f) throws IOException {
        DecimalHelper.encodeFloat(f, this.writeBuffer);
        this.stream.write(this.writeBuffer, 0, 4);
    }

    public void writeInt16(short s) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt16(IntegerHelper.encodeZigzag16(s), this.writeBuffer, 0));
    }

    public void writeInt32(int i) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt32(IntegerHelper.encodeZigzag32(i), this.writeBuffer, 0));
    }

    public void writeInt64(long j) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt64(IntegerHelper.encodeZigzag64(j), this.writeBuffer, 0));
    }

    public void writeInt8(byte b) throws IOException {
        this.stream.write(b);
    }

    public void writeString(String str) throws IOException {
        if (str.isEmpty()) {
            writeUInt32(0);
            return;
        }
        byte[] encodeToUtf8 = StringHelper.encodeToUtf8(str);
        writeUInt32(encodeToUtf8.length);
        this.stream.write(encodeToUtf8);
    }

    public void writeStructEnd(boolean z) throws IOException {
        writeUInt8((byte) (z ? BondDataType.BT_STOP_BASE.getValue() : BondDataType.BT_STOP.getValue()));
    }

    public void writeUInt16(short s) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt16(s, this.writeBuffer, 0));
    }

    public void writeUInt32(int i) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt32(i, this.writeBuffer, 0));
    }

    public void writeUInt64(long j) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt64(j, this.writeBuffer, 0));
    }

    public void writeUInt8(byte b) throws IOException {
        this.stream.write(b);
    }

    public void writeVersion() throws IOException {
        writeUInt16(MAGIC);
        writeUInt16(this.version.getValue());
    }

    public void writeWString(String str) throws IOException {
        if (str.isEmpty()) {
            writeUInt32(0);
            return;
        }
        writeUInt32(str.length());
        byte[] encodeToUtf16 = StringHelper.encodeToUtf16(str);
        this.stream.write(encodeToUtf16, 0, encodeToUtf16.length);
    }
}
