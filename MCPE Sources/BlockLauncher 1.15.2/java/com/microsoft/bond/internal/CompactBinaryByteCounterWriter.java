package com.microsoft.bond.internal;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolWriter;
import java.io.IOException;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class CompactBinaryByteCounterWriter extends ProtocolWriter {
    private IntArrayStack byteLengths = new IntArrayStack(32);
    private IntArrayStack byteLengthsIndexes = new IntArrayStack(8);
    private int positionBytes;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$ProtocolCapability = new int[ProtocolCapability.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.TAGGED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.PASS_THROUGH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$bond$ProtocolCapability[ProtocolCapability.CAN_OMIT_FIELDS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public int getByteLength(int i) {
        return this.byteLengths.get(i);
    }

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$ProtocolCapability[protocolCapability.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return true;
            default:
                return super.hasCapability(protocolCapability);
        }
    }

    public void reset() {
        this.positionBytes = 0;
        this.byteLengths.clear();
        this.byteLengthsIndexes.clear();
    }

    public void writeBlob(BondBlob bondBlob) throws IOException {
        this.positionBytes += bondBlob.size();
    }

    public void writeBool(boolean z) throws IOException {
        this.positionBytes++;
    }

    public void writeContainerBegin(int i, BondDataType bondDataType) throws IOException {
        this.positionBytes = ((i < 7 ? 0 : IntegerHelper.getVarUInt32Size(i)) + 1) + this.positionBytes;
    }

    public void writeContainerBegin(int i, BondDataType bondDataType, BondDataType bondDataType2) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt32Size(i) + 2;
    }

    public void writeContainerEnd() throws IOException {
    }

    public void writeDouble(double d) throws IOException {
        this.positionBytes += 8;
    }

    public void writeFieldBegin(BondDataType bondDataType, int i, BondSerializable bondSerializable) throws IOException {
        if (i <= 5) {
            this.positionBytes++;
        } else if (i <= 255) {
            this.positionBytes += 2;
        } else {
            this.positionBytes += 3;
        }
    }

    public void writeFieldEnd() throws IOException {
    }

    public void writeFieldOmitted(BondDataType bondDataType, int i, BondSerializable bondSerializable) throws IOException {
    }

    public void writeFloat(float f) throws IOException {
        this.positionBytes += 4;
    }

    public void writeInt16(short s) throws IOException {
        writeUInt16(IntegerHelper.encodeZigzag16(s));
    }

    public void writeInt32(int i) throws IOException {
        writeUInt32(IntegerHelper.encodeZigzag32(i));
    }

    public void writeInt64(long j) throws IOException {
        writeUInt64(IntegerHelper.encodeZigzag64(j));
    }

    public void writeInt8(byte b) throws IOException {
        this.positionBytes++;
    }

    public void writeString(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            this.positionBytes++;
            return;
        }
        int length = StringHelper.encodeToUtf8(str).length;
        this.positionBytes = (length + IntegerHelper.getVarUInt32Size(length)) + this.positionBytes;
    }

    public void writeStructBegin(BondSerializable bondSerializable, boolean z) throws IOException {
        if (!z) {
            this.byteLengthsIndexes.push(this.byteLengths.getSize());
            this.byteLengths.push(this.positionBytes);
        }
    }

    public void writeStructEnd(boolean z) throws IOException {
        this.positionBytes++;
        if (!z) {
            int pop = this.byteLengthsIndexes.pop();
            int i = this.positionBytes - this.byteLengths.get(pop);
            this.byteLengths.set(pop, i);
            this.positionBytes += IntegerHelper.getVarUInt32Size(i);
        }
    }

    public void writeUInt16(short s) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt16Size(s);
    }

    public void writeUInt32(int i) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt32Size(i);
    }

    public void writeUInt64(long j) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt64Size(j);
    }

    public void writeUInt8(byte b) throws IOException {
        this.positionBytes++;
    }

    public void writeVersion() throws IOException {
        this.positionBytes += 4;
    }

    public void writeWString(String str) throws IOException {
        if (str.isEmpty()) {
            this.positionBytes++;
            return;
        }
        int length = StringHelper.encodeToUtf16(str).length;
        this.positionBytes = (length + IntegerHelper.getVarUInt32Size(str.length())) + this.positionBytes;
    }
}
