package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.ProtocolReader.MapTag;
import com.microsoft.bond.internal.CompactBinaryV2Reader;
import com.microsoft.bond.internal.DecimalHelper;
import com.microsoft.bond.internal.IntegerHelper;
import com.microsoft.bond.internal.SkipHelper;
import com.microsoft.bond.internal.StringHelper;
import com.microsoft.bond.io.BondInputStream;
import com.microsoft.bond.io.MemoryBondInputStream;
import java.io.IOException;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage.Header;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class CompactBinaryReader extends ProtocolReader {
    private byte[] readBuffer = new byte[64];
    protected final BondInputStream stream;
    private final ProtocolVersion version;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$BondDataType = new int[BondDataType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$ProtocolCapability = new int[ProtocolCapability.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.CLONEABLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.CAN_OMIT_FIELDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.TAGGED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.CAN_SEEK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_WSTRING.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_LIST.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_SET.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_STRUCT.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    protected CompactBinaryReader(ProtocolVersion protocolVersion, BondInputStream bondInputStream) {
        this.version = protocolVersion;
        this.stream = bondInputStream;
    }

    private void SkipContainer() throws IOException {
        ListTag readContainerBegin = readContainerBegin();
        if (readContainerBegin.type == BondDataType.BT_UINT8 || readContainerBegin.type == BondDataType.BT_INT8) {
            this.stream.setPositionRelative(readContainerBegin.size);
        } else {
            for (int i = 0; i < readContainerBegin.size; i++) {
                skip(readContainerBegin.type);
            }
        }
        readContainerEnd();
    }

    public static CompactBinaryReader createV1(BondInputStream bondInputStream) {
        return new CompactBinaryReader(ProtocolVersion.ONE, bondInputStream);
    }

    public static CompactBinaryReader createV1(byte[] bArr) {
        return createV1(bArr, 0, bArr.length);
    }

    public static CompactBinaryReader createV1(byte[] bArr, int i, int i2) {
        return createV1(new MemoryBondInputStream(bArr, i, i2));
    }

    public static CompactBinaryReader createV2(BondInputStream bondInputStream) {
        return new CompactBinaryV2Reader(bondInputStream);
    }

    public static CompactBinaryReader createV2(byte[] bArr) {
        return createV2(bArr, 0, bArr.length);
    }

    public static CompactBinaryReader createV2(byte[] bArr, int i, int i2) {
        return createV2(new MemoryBondInputStream(bArr, i, i2));
    }

    private void ensureReadBufferCapacity(int i) {
        if (this.readBuffer.length < i) {
            this.readBuffer = new byte[i];
        }
    }

    public ProtocolReader cloneReader() throws IOException {
        BondInputStream clone = this.stream.clone(true);
        return this.version == ProtocolVersion.ONE ? createV1(clone) : this.version == ProtocolVersion.TWO ? createV2(clone) : null;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public int getPosition() throws IOException {
        return this.stream.getPosition();
    }

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$ProtocolCapability[protocolCapability.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return this.stream.isCloneable();
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return true;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return this.stream.isSeekable();
            default:
                return super.hasCapability(protocolCapability);
        }
    }

    public boolean isProtocolSame(ProtocolWriter protocolWriter) {
        if (!(protocolWriter instanceof CompactBinaryWriter)) {
            return false;
        }
        return this.version == ((CompactBinaryWriter) protocolWriter).getVersion();
    }

    public BondBlob readBlob(int i) throws IOException {
        return this.stream.readBlob(i);
    }

    public boolean readBool() throws IOException {
        return readUInt8() != (byte) 0;
    }

    public ListTag readContainerBegin() throws IOException {
        return new ListTag(readUInt32(), BondDataType.fromValue(readUInt8()));
    }

    public void readContainerEnd() {
    }

    public double readDouble() throws IOException {
        ensureReadBufferCapacity(8);
        this.stream.read(this.readBuffer, 0, 8);
        return DecimalHelper.decodeDouble(this.readBuffer);
    }

    public FieldTag readFieldBegin() throws IOException {
        BondDataType bondDataType = BondDataType.BT_STOP;
        byte read = this.stream.read();
        BondDataType fromValue = BondDataType.fromValue(read & 31);
        int i = read & 224;
        i = i == 224 ? (this.stream.read() & 255) | ((this.stream.read() & 255) << 8) : i == Header.ID_INTERLEAVE ? this.stream.read() : i >> 5;
        return new FieldTag(fromValue, i);
    }

    public float readFloat() throws IOException {
        ensureReadBufferCapacity(4);
        this.stream.read(this.readBuffer, 0, 4);
        return DecimalHelper.decodeFloat(this.readBuffer);
    }

    public short readInt16() throws IOException {
        return IntegerHelper.decodeZigzag16(IntegerHelper.decodeVarInt16(this.stream));
    }

    public int readInt32() throws IOException {
        return IntegerHelper.decodeZigzag32(IntegerHelper.decodeVarInt32(this.stream));
    }

    public long readInt64() throws IOException {
        return IntegerHelper.decodeZigzag64(IntegerHelper.decodeVarInt64(this.stream));
    }

    public byte readInt8() throws IOException {
        return this.stream.read();
    }

    public MapTag readMapContainerBegin() throws IOException {
        return new MapTag(readUInt32(), BondDataType.fromValue(readUInt8()), BondDataType.fromValue(readUInt8()));
    }

    public String readString() throws IOException {
        int decodeVarInt32 = IntegerHelper.decodeVarInt32(this.stream);
        if (decodeVarInt32 == 0) {
            return BuildConfig.FLAVOR;
        }
        ensureReadBufferCapacity(decodeVarInt32);
        this.stream.read(this.readBuffer, 0, decodeVarInt32);
        return StringHelper.decodeFromUtf8(this.readBuffer, 0, decodeVarInt32);
    }

    public short readUInt16() throws IOException {
        return IntegerHelper.decodeVarInt16(this.stream);
    }

    public int readUInt32() throws IOException {
        return IntegerHelper.decodeVarInt32(this.stream);
    }

    public long readUInt64() throws IOException {
        return IntegerHelper.decodeVarInt64(this.stream);
    }

    public byte readUInt8() throws IOException {
        return this.stream.read();
    }

    public String readWString() throws IOException {
        int decodeVarInt32 = IntegerHelper.decodeVarInt32(this.stream) << 1;
        if (decodeVarInt32 == 0) {
            return BuildConfig.FLAVOR;
        }
        ensureReadBufferCapacity(decodeVarInt32);
        this.stream.read(this.readBuffer, 0, decodeVarInt32);
        return StringHelper.decodeFromUtf16(this.readBuffer, 0, decodeVarInt32);
    }

    public void setPosition(int i) throws IOException {
        this.stream.setPosition(i);
    }

    public void skip(BondDataType bondDataType) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$BondDataType[bondDataType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.stream.setPositionRelative(readUInt32());
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.stream.setPositionRelative(readUInt32() << 1);
                return;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                SkipContainer();
                return;
            case Token.GOTO /*5*/:
                break;
            default:
                SkipHelper.skip(this, bondDataType);
                return;
        }
        FieldTag readFieldBegin;
        do {
            readFieldBegin = readFieldBegin();
            while (readFieldBegin.type != BondDataType.BT_STOP && readFieldBegin.type != BondDataType.BT_STOP_BASE) {
                skip(readFieldBegin.type);
                readFieldEnd();
                readFieldBegin = readFieldBegin();
            }
        } while (readFieldBegin.type != BondDataType.BT_STOP);
    }

    public String toString() {
        return String.format("[%s version=%d]", new Object[]{getClass().getName(), Short.valueOf(this.version.getValue())});
    }
}
