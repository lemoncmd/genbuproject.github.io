package com.microsoft.cll.android;

import com.microsoft.telemetry.IJsonSerializable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class EventSerializer {
    private final String TAG = "EventSerializer";
    private final ILogger logger;

    public EventSerializer(ILogger iLogger) {
        this.logger = iLogger;
    }

    public String serialize(IJsonSerializable iJsonSerializable) {
        Writer stringWriter = new StringWriter();
        try {
            iJsonSerializable.serialize(stringWriter);
        } catch (IOException e) {
            this.logger.error("EventSerializer", "IOException when serializing");
        }
        String str = stringWriter.toString() + "\r\n";
        this.logger.info("EventSerializer", str);
        return str;
    }
}
