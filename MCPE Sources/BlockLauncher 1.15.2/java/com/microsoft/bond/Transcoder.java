package com.microsoft.bond;

import java.io.IOException;

public class Transcoder {
    public static void transcode(ProtocolWriter protocolWriter, ProtocolReader protocolReader) throws IOException {
        if (protocolReader.hasCapability(ProtocolCapability.CAN_SEEK) && protocolReader.isProtocolSame(protocolWriter)) {
            int position = protocolReader.getPosition();
            protocolReader.skip(BondDataType.BT_STRUCT);
            int position2 = protocolReader.getPosition() - position;
            protocolReader.setPosition(position);
            protocolWriter.writeBlob(protocolReader.readBlob(position2));
        }
    }
}
