package com.microsoft.xbox.xle.app.activity.Profile;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;

public class ProfileScreen extends ActivityBase {
    public ProfileScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected String getActivityName() {
        return "PeopleHub Info";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ViewModelBase profileScreenViewModel = new ProfileScreenViewModel(this);
        this.viewModel = profileScreenViewModel;
        UTCPeopleHub.trackPeopleHubView(getActivityName(), profileScreenViewModel.getXuid(), profileScreenViewModel.isMeProfile());
    }

    public void onCreateContentView() {
        setContentView(R.layout.profile_screen);
    }
}
