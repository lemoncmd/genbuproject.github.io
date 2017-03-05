package com.microsoft.onlineid.internal.configuration;

import com.microsoft.onlineid.internal.Objects;
import java.net.URL;

public class Environment {
    private final URL _configUrl;
    private final String _environmentName;

    public Environment(String str, URL url) {
        this._environmentName = str;
        this._configUrl = url;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Environment)) {
            return false;
        }
        Environment environment = (Environment) obj;
        return Objects.equals(getEnvironmentName(), environment.getEnvironmentName()) && Objects.equals(getConfigUrl(), environment.getConfigUrl());
    }

    public URL getConfigUrl() {
        return this._configUrl;
    }

    public String getEnvironmentName() {
        return this._environmentName;
    }

    public int hashCode() {
        return Objects.hashCode(this._environmentName) + Objects.hashCode(this._configUrl);
    }
}
