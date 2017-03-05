package com.microsoft.xbox.idp.interop;

import android.content.Context;
import android.util.Log;
import com.microsoft.cll.android.ITicketCallback;
import com.microsoft.cll.android.TicketObject;
import com.microsoft.xbox.idp.jobs.JobSilentSignIn;
import net.hockeyapp.android.BuildConfig;

public class CLLCallback implements ITicketCallback {
    private static final String POLICY = "mbi_ssl";
    private static final String VORTEX_SCOPE = "vortex.data.microsoft.com";
    private String m_activityTitle;
    private Context m_context = null;
    private String m_vortexTicket = new String(BuildConfig.FLAVOR);

    public CLLCallback(Context context, String str) {
        this.m_context = context;
        this.m_activityTitle = str;
    }

    public String getAuthXToken(boolean z) {
        return Interop.GetLiveXTokenCallback(z);
    }

    public String getMsaDeviceTicket(boolean z) {
        if (this.m_vortexTicket.length() > 0 && !z) {
            return this.m_vortexTicket;
        }
        Object mSATicketCallbacks = new MSATicketCallbacks();
        JobSilentSignIn jobSilentSignIn = new JobSilentSignIn(this.m_context, this.m_activityTitle, mSATicketCallbacks, VORTEX_SCOPE, POLICY, new LocalConfig().getCid());
        synchronized (jobSilentSignIn) {
            try {
                jobSilentSignIn.start();
                jobSilentSignIn.wait();
            } catch (Exception e) {
                Log.i("XSAPI.Android", "exception on votex MSA Ticket");
            }
        }
        this.m_vortexTicket = mSATicketCallbacks.getTicket();
        return this.m_vortexTicket;
    }

    public TicketObject getXTicketForXuid(String str) {
        return new TicketObject("x:" + Interop.GetXTokenCallback(str), false);
    }
}
