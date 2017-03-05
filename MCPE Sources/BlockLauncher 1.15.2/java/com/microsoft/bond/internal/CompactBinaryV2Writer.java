package com.microsoft.bond.internal;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.CompactBinaryWriter;
import com.microsoft.bond.ProtocolVersion;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.io.BondOutputStream;
import java.io.IOException;

public class CompactBinaryV2Writer extends CompactBinaryWriter {
    private final CompactBinaryByteCounterWriter byteCounterWriter = new CompactBinaryByteCounterWriter();
    private int currentIndex = 0;

    public CompactBinaryV2Writer(BondOutputStream bondOutputStream) {
        super(ProtocolVersion.TWO, bondOutputStream);
    }

    public ProtocolWriter getFirstPassWriter() {
        return this.currentIndex == 0 ? this.byteCounterWriter : null;
    }

    public void writeContainerBegin(int i, BondDataType bondDataType) throws IOException {
        if (i < 7) {
            writeUInt8((byte) (bondDataType.getValue() | ((i + 1) << 5)));
        } else {
            super.writeContainerBegin(i, bondDataType);
        }
    }

    public void writeEnd() {
        this.currentIndex = 0;
        this.byteCounterWriter.reset();
    }

    public void writeStructBegin(BondSerializable bondSerializable, boolean z) throws IOException {
        if (!z) {
            writeUInt32(this.byteCounterWriter.getByteLength(this.currentIndex));
            this.currentIndex++;
        }
    }
}
