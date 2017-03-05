package net.hockeyapp.android.metrics;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.metrics.model.Base;
import net.hockeyapp.android.metrics.model.Data;
import net.hockeyapp.android.metrics.model.Domain;
import net.hockeyapp.android.metrics.model.Envelope;
import net.hockeyapp.android.metrics.model.TelemetryData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

class Channel {
    private static final Object LOCK = new Object();
    private static final String TAG = "HockeyApp-Metrics";
    protected static int mMaxBatchCount = 1;
    private final Persistence mPersistence;
    protected final List<String> mQueue = new LinkedList();
    protected final TelemetryContext mTelemetryContext;

    public Channel(TelemetryContext telemetryContext, Persistence persistence) {
        this.mTelemetryContext = telemetryContext;
        this.mPersistence = persistence;
    }

    protected Envelope createEnvelope(Data<Domain> data) {
        Envelope envelope = new Envelope();
        envelope.setData(data);
        Domain baseData = data.getBaseData();
        if (baseData instanceof TelemetryData) {
            envelope.setName(((TelemetryData) baseData).getEnvelopeName());
        }
        this.mTelemetryContext.updateScreenResolution();
        envelope.setTime(Util.dateToISO8601(new Date()));
        envelope.setIKey(this.mTelemetryContext.getInstrumentationKey());
        Map contextTags = this.mTelemetryContext.getContextTags();
        if (contextTags != null) {
            envelope.setTags(contextTags);
        }
        return envelope;
    }

    protected void enqueue(String str) {
        if (str != null) {
            synchronized (LOCK) {
                if (!this.mQueue.add(str)) {
                    HockeyLog.verbose(TAG, "Unable to add item to queue");
                } else if (this.mQueue.size() >= mMaxBatchCount) {
                    synchronize();
                }
            }
        }
    }

    public void enqueueData(Base base) {
        if (base instanceof Data) {
            Envelope envelope = null;
            try {
                envelope = createEnvelope((Data) base);
            } catch (ClassCastException e) {
                HockeyLog.debug(TAG, "Telemetry not enqueued, could not create Envelope, must be of type ITelemetry");
            }
            if (envelope != null) {
                enqueue(serializeEnvelope(envelope));
                HockeyLog.debug(TAG, "enqueued telemetry: " + envelope.getName());
                return;
            }
            return;
        }
        HockeyLog.debug(TAG, "Telemetry not enqueued, must be of type ITelemetry");
    }

    protected String serializeEnvelope(Envelope envelope) {
        if (envelope != null) {
            try {
                Writer stringWriter = new StringWriter();
                envelope.serialize(stringWriter);
                return stringWriter.toString();
            } catch (IOException e) {
                HockeyLog.debug(TAG, "Failed to save data with exception: " + e.toString());
                return null;
            }
        }
        HockeyLog.debug(TAG, "Envelope wasn't empty but failed to serialize anything, returning null");
        return null;
    }

    protected void synchronize() {
        if (!this.mQueue.isEmpty()) {
            String[] strArr = new String[this.mQueue.size()];
            this.mQueue.toArray(strArr);
            this.mQueue.clear();
            if (this.mPersistence != null) {
                this.mPersistence.persist(strArr);
            }
        }
    }
}
