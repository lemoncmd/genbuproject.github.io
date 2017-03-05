package com.microsoft.xbox.toolkit;

import android.app.Dialog;

public class DialogManager implements IProjectSpecificDialogManager {
    private static DialogManager instance = new DialogManager();
    private IProjectSpecificDialogManager manager;

    private DialogManager() {
    }

    private void checkProvider() {
    }

    public static DialogManager getInstance() {
        return instance;
    }

    public void addManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.addManagedDialog(iXLEManagedDialog);
        }
    }

    public void dismissBlocking() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissBlocking();
        }
    }

    public void dismissManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissManagedDialog(iXLEManagedDialog);
        }
    }

    public void dismissToast() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissToast();
        }
    }

    public void dismissTopNonFatalAlert() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissTopNonFatalAlert();
        }
    }

    public void forceDismissAlerts() {
        checkProvider();
        if (this.manager != null) {
            this.manager.forceDismissAlerts();
        }
    }

    public void forceDismissAll() {
        checkProvider();
        if (this.manager != null) {
            this.manager.forceDismissAll();
        }
    }

    public boolean getIsBlocking() {
        checkProvider();
        return this.manager != null ? this.manager.getIsBlocking() : false;
    }

    public IProjectSpecificDialogManager getManager() {
        return this.manager;
    }

    public Dialog getVisibleDialog() {
        checkProvider();
        return this.manager != null ? this.manager.getVisibleDialog() : null;
    }

    public void onApplicationPause() {
        if (this.manager != null) {
            this.manager.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (this.manager != null) {
            this.manager.onApplicationResume();
        }
    }

    public void onDialogStopped(IXLEManagedDialog iXLEManagedDialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.onDialogStopped(iXLEManagedDialog);
        }
    }

    public void setBlocking(boolean z, String str) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setBlocking(z, str);
        }
    }

    public void setCancelableBlocking(boolean z, String str, Runnable runnable) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setCancelableBlocking(z, str, runnable);
        }
    }

    public void setEnabled(boolean z) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setEnabled(z);
        }
    }

    public void setManager(IProjectSpecificDialogManager iProjectSpecificDialogManager) {
        this.manager = iProjectSpecificDialogManager;
    }

    public void showFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showFatalAlertDialog(str, str2, str3, runnable);
        }
    }

    public void showManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showManagedDialog(iXLEManagedDialog);
        }
    }

    public void showNonFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showNonFatalAlertDialog(str, str2, str3, runnable);
        }
    }

    public void showOkCancelDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showOkCancelDialog(str, str2, str3, runnable, str4, runnable2);
        }
    }

    public void showToast(int i) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showToast(i);
        }
    }
}
