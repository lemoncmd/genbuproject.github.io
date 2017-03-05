package com.microsoft.cll.android;

import Microsoft.Telemetry.Base;
import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import java.util.EnumSet;
import java.util.List;

public interface ICll {
    void SubscribeCllEvents(ICllEvents iCllEvents);

    String getAppUserId();

    void log(Base base);

    void log(Base base, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list);

    void log(Base base, List<String> list);

    void log(String str, String str2, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list);

    void pause();

    void resume();

    void send();

    void setAppUserId(String str);

    void setDebugVerbosity(Verbosity verbosity);

    void setEndpointUrl(String str);

    void setExperimentId(String str);

    void setXuidCallback(ITicketCallback iTicketCallback);

    void start();

    void stop();

    void synchronize();

    void useLegacyCS(boolean z);
}
