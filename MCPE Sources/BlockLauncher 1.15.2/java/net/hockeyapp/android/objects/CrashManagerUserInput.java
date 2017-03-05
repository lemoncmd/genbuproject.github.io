package net.hockeyapp.android.objects;

public enum CrashManagerUserInput {
    CrashManagerUserInputDontSend(0),
    CrashManagerUserInputSend(1),
    CrashManagerUserInputAlwaysSend(2);
    
    private final int mValue;

    private CrashManagerUserInput(int i) {
        this.mValue = i;
    }

    public int getValue() {
        return this.mValue;
    }
}
