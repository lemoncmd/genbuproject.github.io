package com.microsoft.cll.android;

import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.ProtocolWriter;
import java.io.IOException;

public class BondJsonSerializer {
    private final String TAG = "AndroidCll-EventSerializer";
    private final ILogger logger;
    private final StringBuilder resultString = new StringBuilder();
    private final ProtocolWriter writer = new JsonProtocol(this.resultString);

    public BondJsonSerializer(ILogger iLogger) {
        this.logger = iLogger;
    }

    public String serialize(BondSerializable bondSerializable) {
        String obj;
        synchronized (this) {
            try {
                bondSerializable.write(this.writer);
            } catch (IOException e) {
                this.logger.error("AndroidCll-EventSerializer", "IOException when serializing");
            }
            obj = this.writer.toString();
            this.resultString.setLength(0);
        }
        return obj;
    }
}
