package com.microsoft.cll.android;

import com.microsoft.xbox.toolkit.MemoryMonitor;
import java.util.EnumSet;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public final class EventEnums {
    public static final double SampleRate_0_percent = 0.0d;
    public static final double SampleRate_10_percent = 10.0d;
    public static final double SampleRate_Epsilon = 1.0E-5d;
    public static final double SampleRate_NoSampling = 100.0d;
    public static final double SampleRate_Unspecified = -1.0d;

    public enum Latency {
        LatencyUnspecified(0),
        LatencyNormal(EnchantType.flintAndSteel),
        LatencyRealtime(EnchantType.axe);
        
        public final int id;

        private Latency(int i) {
            this.id = i;
        }

        static Latency FromString(String str) {
            return str == "REALTIME" ? LatencyRealtime : LatencyNormal;
        }
    }

    public enum Persistence {
        PersistenceUnspecified(0),
        PersistenceNormal(1),
        PersistenceCritical(2);
        
        public final int id;

        private Persistence(int i) {
            this.id = i;
        }

        static Persistence FromString(String str) {
            return str == "CRITICAL" ? PersistenceCritical : PersistenceNormal;
        }
    }

    public enum Sensitivity {
        SensitivityUnspecified(1),
        SensitivityNone(0),
        SensitivityMark(524288),
        SensitivityHash(MemoryMonitor.MB_TO_BYTES),
        SensitivityDrop(2097152);
        
        public final int id;

        private Sensitivity(int i) {
            this.id = i;
        }

        static EnumSet<Sensitivity> FromString(String str) {
            EnumSet<Sensitivity> noneOf = EnumSet.noneOf(Sensitivity.class);
            if (str != null) {
                if (str.contains("MARK") || str.toUpperCase().contains("USERSENSITIVE")) {
                    noneOf.add(SensitivityMark);
                }
                if (str.contains("DROP")) {
                    noneOf.add(SensitivityDrop);
                }
                if (str.contains("HASH")) {
                    noneOf.add(SensitivityHash);
                }
            }
            return noneOf;
        }
    }

    private EventEnums() {
        throw new AssertionError();
    }

    static double SampleRateFromString(String str) {
        double d = SampleRate_NoSampling;
        try {
            d = Double.parseDouble(str);
        } catch (NumberFormatException e) {
        }
        return d;
    }
}
