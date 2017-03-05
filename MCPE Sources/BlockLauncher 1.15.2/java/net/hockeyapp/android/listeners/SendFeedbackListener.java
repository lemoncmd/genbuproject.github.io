package net.hockeyapp.android.listeners;

import net.hockeyapp.android.tasks.SendFeedbackTask;

public abstract class SendFeedbackListener {
    public void feedbackFailed(SendFeedbackTask sendFeedbackTask, Boolean bool) {
    }

    public void feedbackSuccessful(SendFeedbackTask sendFeedbackTask) {
    }
}
