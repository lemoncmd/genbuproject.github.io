package com.microsoft.xbox.toolkit;

public class TimeMonitor {
    private final long NSTOMSEC = 1000000;
    private long endTicks = 0;
    private long startTicks = 0;

    public long currentTime() {
        return (System.nanoTime() - this.startTicks) / 1000000;
    }

    public long getElapsedMs() {
        if (!getIsStarted()) {
            return 0;
        }
        return ((this.endTicks != 0 ? this.endTicks : System.nanoTime()) - this.startTicks) / 1000000;
    }

    public boolean getIsEnded() {
        return this.endTicks != 0;
    }

    public boolean getIsStarted() {
        return this.startTicks != 0;
    }

    public void reset() {
        this.startTicks = 0;
        this.endTicks = 0;
    }

    public void saveCurrentTime() {
        if (getIsStarted()) {
            this.endTicks = System.nanoTime();
        }
    }

    public void start() {
        this.startTicks = System.nanoTime();
        this.endTicks = 0;
    }

    public void stop() {
        if (this.startTicks != 0 && this.endTicks == 0) {
            this.endTicks = System.nanoTime();
        }
    }
}
