package com.microsoft.bond.internal;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.CompactBinaryReader;
import com.microsoft.bond.ProtocolReader.ListTag;
import com.microsoft.bond.ProtocolVersion;
import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;
import org.mozilla.javascript.regexp.NativeRegExp;

public class CompactBinaryV2Reader extends CompactBinaryReader {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$bond$BondDataType = new int[BondDataType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$bond$BondDataType[BondDataType.BT_STRUCT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public CompactBinaryV2Reader(BondInputStream bondInputStream) {
        super(ProtocolVersion.TWO, bondInputStream);
    }

    public ListTag readContainerBegin() throws IOException {
        byte readUInt8 = readUInt8();
        BondDataType fromValue = BondDataType.fromValue(readUInt8 & 31);
        return (readUInt8 & 224) != 0 ? new ListTag(((readUInt8 >> 5) & 7) - 1, fromValue) : new ListTag(readUInt32(), fromValue);
    }

    public void readStructBegin(boolean z) throws IOException {
        if (!z) {
            readUInt32();
        }
    }

    public void skip(BondDataType bondDataType) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$bond$BondDataType[bondDataType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.stream.setPositionRelative(readUInt32());
                return;
            default:
                super.skip(bondDataType);
                return;
        }
    }
}
