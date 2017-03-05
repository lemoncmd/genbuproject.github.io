package com.microsoft.xbox.xle.app;

import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.DialogManagerBase;
import com.microsoft.xbox.toolkit.IProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class SGProjectSpecificDialogManager extends DialogManagerBase {
    private static IProjectSpecificDialogManager instance = new SGProjectSpecificDialogManager();
    private ChangeFriendshipDialog changeFriendshipDialog;

    private SGProjectSpecificDialogManager() {
    }

    public static IProjectSpecificDialogManager getInstance() {
        return instance;
    }

    public static SGProjectSpecificDialogManager getProjectSpecificInstance() {
        return (SGProjectSpecificDialogManager) DialogManager.getInstance().getManager();
    }

    public void dismissChangeFriendshipDialog() {
        if (this.changeFriendshipDialog != null) {
            dismissManagedDialog(this.changeFriendshipDialog);
            this.changeFriendshipDialog = null;
        }
    }

    public void forceDismissAll() {
        super.forceDismissAll();
        dismissChangeFriendshipDialog();
    }

    public void notifyChangeFriendshipDialogAsyncTaskCompleted() {
        if (this.changeFriendshipDialog != null) {
            this.changeFriendshipDialog.reportAsyncTaskCompleted();
        }
    }

    public void notifyChangeFriendshipDialogAsyncTaskFailed(String str) {
        if (this.changeFriendshipDialog != null) {
            this.changeFriendshipDialog.reportAsyncTaskFailed(str);
        }
    }

    public void notifyChangeFriendshipDialogUpdateView() {
        if (this.changeFriendshipDialog != null) {
            this.changeFriendshipDialog.updateView();
        }
    }

    public void onApplicationPause() {
        forceDismissAll();
    }

    public void onApplicationResume() {
    }

    protected boolean shouldDismissAllBeforeOpeningADialog() {
        return false;
    }

    public void showChangeFriendshipDialog(ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel, ViewModelBase viewModelBase) {
        if (this.changeFriendshipDialog != null) {
            this.changeFriendshipDialog.setVm(changeFriendshipDialogViewModel);
            this.changeFriendshipDialog.getDialog().show();
            return;
        }
        this.changeFriendshipDialog = new ChangeFriendshipDialog(XboxTcuiSdk.getActivity(), changeFriendshipDialogViewModel, viewModelBase);
        addManagedDialog(this.changeFriendshipDialog);
    }
}
