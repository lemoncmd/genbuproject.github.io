package com.microsoft.xbox.xle.app.activity;

import com.microsoft.xbox.xle.telemetry.helpers.UTCReportUser;
import com.microsoft.xbox.xle.viewmodel.ReportUserScreenViewModel;
import com.microsoft.xboxtcui.R;

public class ReportUserScreen extends ActivityBase {
    private ReportUserScreenViewModel reportUserScreenViewModel;

    protected String getActivityName() {
        return "Report user";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new ReportUserScreenViewModel(this);
        this.reportUserScreenViewModel = (ReportUserScreenViewModel) this.viewModel;
        UTCReportUser.trackReportView(getName(), this.reportUserScreenViewModel.getXUID());
    }

    public void onCreateContentView() {
        setContentView(R.layout.report_user_screen);
    }

    public void onStart() {
        super.onStart();
        setBackgroundColor(this.reportUserScreenViewModel.getPreferredColor());
    }
}
