package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;

public class EventBatcher {
    private StringBuilder eventString;
    private final String newLine;
    private int numberOfEvents;
    private int size;

    public EventBatcher() {
        this.newLine = "\r\n";
        this.size = SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES);
        this.eventString = new StringBuilder(this.size);
        this.numberOfEvents = 0;
    }

    public EventBatcher(int i) {
        this.newLine = "\r\n";
        this.size = i;
        this.eventString = new StringBuilder(i);
        this.numberOfEvents = 0;
    }

    protected boolean canAddToBatch(String str) {
        return (this.eventString.length() + "\r\n".length()) + str.length() <= this.size && this.numberOfEvents < SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSPERPOST);
    }

    public String getBatchedEvents() {
        String stringBuilder = this.eventString.toString();
        this.eventString.setLength(0);
        this.numberOfEvents = 0;
        return stringBuilder;
    }

    public boolean tryAddingEventToBatch(String str) {
        if (!canAddToBatch(str)) {
            return false;
        }
        this.eventString.append(str).append("\r\n");
        this.numberOfEvents++;
        return true;
    }
}
