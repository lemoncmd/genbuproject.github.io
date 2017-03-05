package com.microsoft.cll.android;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledWorker implements Runnable {
    protected ScheduledExecutorService executor;
    protected long interval;
    protected boolean isPaused = false;
    protected ScheduledFuture nextExecution;

    public ScheduledWorker(long j) {
        this.interval = j;
    }

    private void setupExecutor(ScheduledExecutorService scheduledExecutorService) {
        this.executor = scheduledExecutorService;
        this.nextExecution = scheduledExecutorService.scheduleAtFixedRate(this, 0, this.interval, TimeUnit.SECONDS);
    }

    protected void pause() {
        this.nextExecution.cancel(false);
        this.isPaused = true;
    }

    protected void resume(ScheduledExecutorService scheduledExecutorService) {
        setupExecutor(scheduledExecutorService);
        this.isPaused = false;
    }

    public abstract void run();

    protected void start(ScheduledExecutorService scheduledExecutorService) {
        setupExecutor(scheduledExecutorService);
    }

    protected void stop() {
        this.nextExecution.cancel(true);
    }
}
