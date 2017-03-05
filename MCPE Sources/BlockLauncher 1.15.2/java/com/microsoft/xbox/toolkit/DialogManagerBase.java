package com.microsoft.xbox.toolkit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Toast;
import com.microsoft.xbox.toolkit.IXLEManagedDialog.DialogType;
import com.microsoft.xbox.toolkit.ui.BlockingScreen;
import com.microsoft.xbox.toolkit.ui.CancellableBlockingScreen;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Stack;

public abstract class DialogManagerBase implements IProjectSpecificDialogManager {
    private BlockingScreen blockingSpinner;
    private CancellableBlockingScreen cancelableBlockingDialog;
    private Stack<IXLEManagedDialog> dialogStack = new Stack();
    private boolean isEnabled;
    private Toast visibleToast;

    protected DialogManagerBase() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    private XLEManagedAlertDialog buildDialog(String str, String str2, String str3, final Runnable runnable, String str4, final Runnable runnable2) {
        final XLEManagedAlertDialog xLEManagedAlertDialog = new XLEManagedAlertDialog(XboxTcuiSdk.getActivity());
        xLEManagedAlertDialog.setTitle(str);
        xLEManagedAlertDialog.setMessage(str2);
        xLEManagedAlertDialog.setButton(-1, str3, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ThreadManager.UIThreadPost(runnable);
            }
        });
        final Runnable anonymousClass3 = new Runnable() {
            public void run() {
                DialogManagerBase.this.dismissManagedDialog(xLEManagedAlertDialog);
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        xLEManagedAlertDialog.setButton(-2, str4, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ThreadManager.UIThreadPost(anonymousClass3);
            }
        });
        if (str4 == null || str4.length() == 0) {
            xLEManagedAlertDialog.setCancelable(false);
        } else {
            xLEManagedAlertDialog.setCancelable(true);
            xLEManagedAlertDialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialogInterface) {
                    ThreadManager.UIThreadPost(anonymousClass3);
                }
            });
        }
        return xLEManagedAlertDialog;
    }

    public void addManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (this.isEnabled) {
            this.dialogStack.push(iXLEManagedDialog);
            iXLEManagedDialog.getDialog().show();
        }
    }

    public void dismissBlocking() {
        if (this.blockingSpinner != null) {
            this.blockingSpinner.dismiss();
            this.blockingSpinner = null;
        }
        if (this.cancelableBlockingDialog != null) {
            this.cancelableBlockingDialog.dismiss();
            this.cancelableBlockingDialog = null;
        }
    }

    public void dismissManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (this.isEnabled) {
            this.dialogStack.remove(iXLEManagedDialog);
            iXLEManagedDialog.getDialog().dismiss();
        }
    }

    public void dismissToast() {
        if (this.visibleToast != null) {
            this.visibleToast.cancel();
            this.visibleToast = null;
        }
    }

    public void dismissTopNonFatalAlert() {
        if (this.dialogStack.size() > 0 && ((IXLEManagedDialog) this.dialogStack.peek()).getDialogType() != DialogType.FATAL) {
            ((IXLEManagedDialog) this.dialogStack.pop()).getDialog().dismiss();
        }
    }

    public void forceDismissAlerts() {
        while (this.dialogStack.size() > 0) {
            ((IXLEManagedDialog) this.dialogStack.pop()).quickDismiss();
        }
    }

    public void forceDismissAll() {
        dismissToast();
        forceDismissAlerts();
        dismissBlocking();
    }

    public boolean getIsBlocking() {
        return (this.blockingSpinner != null && this.blockingSpinner.isShowing()) || (this.cancelableBlockingDialog != null && this.cancelableBlockingDialog.isShowing());
    }

    public Dialog getVisibleDialog() {
        return !this.dialogStack.isEmpty() ? ((IXLEManagedDialog) this.dialogStack.peek()).getDialog() : null;
    }

    public void onDialogStopped(IXLEManagedDialog iXLEManagedDialog) {
        this.dialogStack.remove(iXLEManagedDialog);
    }

    public void setBlocking(boolean z, String str) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isEnabled) {
            return;
        }
        if (z) {
            if (this.blockingSpinner == null) {
                this.blockingSpinner = new BlockingScreen(XboxTcuiSdk.getActivity());
            }
            this.blockingSpinner.show(XboxTcuiSdk.getActivity(), str);
        } else if (this.blockingSpinner != null) {
            this.blockingSpinner.dismiss();
            this.blockingSpinner = null;
        }
    }

    public void setCancelableBlocking(boolean z, String str, final Runnable runnable) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isEnabled) {
            return;
        }
        if (z) {
            if (this.cancelableBlockingDialog == null) {
                this.cancelableBlockingDialog = new CancellableBlockingScreen(XboxTcuiSdk.getActivity());
                this.cancelableBlockingDialog.setCancelButtonAction(new View.OnClickListener() {
                    public void onClick(View view) {
                        DialogManagerBase.this.cancelableBlockingDialog.dismiss();
                        DialogManagerBase.this.cancelableBlockingDialog = null;
                        runnable.run();
                    }
                });
            }
            this.cancelableBlockingDialog.show(XboxTcuiSdk.getActivity(), str);
        } else if (this.cancelableBlockingDialog != null) {
            this.cancelableBlockingDialog.dismiss();
            this.cancelableBlockingDialog = null;
        }
    }

    public void setEnabled(boolean z) {
        if (this.isEnabled != z) {
            this.isEnabled = z;
        }
    }

    protected boolean shouldDismissAllBeforeOpeningADialog() {
        return true;
    }

    public void showFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        forceDismissAll();
        if (this.isEnabled) {
            XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, null, null);
            buildDialog.setDialogType(DialogType.FATAL);
            this.dialogStack.push(buildDialog);
            buildDialog.show();
        }
    }

    public void showManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (shouldDismissAllBeforeOpeningADialog()) {
            forceDismissAll();
        }
        if (this.isEnabled && XboxTcuiSdk.getActivity() != null && !XboxTcuiSdk.getActivity().isFinishing()) {
            this.dialogStack.push(iXLEManagedDialog);
            try {
                iXLEManagedDialog.getDialog().show();
            } catch (RuntimeException e) {
                String message = e.getMessage();
                if (message == null || !message.contains("Adding window failed")) {
                    throw e;
                }
            }
        }
    }

    public void showNonFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        if (this.isEnabled) {
            XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, null, null);
            buildDialog.setDialogType(DialogType.NON_FATAL);
            this.dialogStack.push(buildDialog);
            buildDialog.show();
        }
    }

    public void showOkCancelDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        XLEAssert.assertNotNull("You must supply cancel text if this is not a must-act dialog.", str4);
        if (this.dialogStack.size() <= 0 && this.isEnabled && XboxTcuiSdk.getActivity() != null && !XboxTcuiSdk.getActivity().isFinishing()) {
            XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, str4, runnable2);
            buildDialog.setDialogType(DialogType.NORMAL);
            this.dialogStack.push(buildDialog);
            buildDialog.show();
        }
    }

    public void showToast(int i) {
        dismissToast();
        if (this.isEnabled) {
            this.visibleToast = Toast.makeText(XboxTcuiSdk.getActivity(), i, 1);
            this.visibleToast.show();
        }
    }
}
