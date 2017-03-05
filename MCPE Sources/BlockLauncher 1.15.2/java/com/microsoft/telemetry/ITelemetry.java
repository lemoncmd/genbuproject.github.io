package com.microsoft.telemetry;

import java.util.Map;

public abstract class ITelemetry extends Domain {
    public abstract String getBaseType();

    public abstract String getEnvelopeName();

    public abstract Map<String, String> getProperties();

    public abstract void setProperties(Map<String, String> map);

    public abstract void setVer(int i);
}
