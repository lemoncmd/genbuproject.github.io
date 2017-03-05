package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.telemetry.Base;
import java.util.EnumSet;
import java.util.List;

public interface ISingletonCll {
    void SubscribeCllEvents(ICllEvents iCllEvents);

    String getAppUserId();

    void log(Base base, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list);

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
