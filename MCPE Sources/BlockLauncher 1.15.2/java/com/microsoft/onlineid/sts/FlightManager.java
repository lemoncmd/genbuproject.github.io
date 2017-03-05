package com.microsoft.onlineid.sts;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.configuration.Flight;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class FlightManager {
    private static final Comparator<AuthenticatorUserAccount> PuidComparator = new Comparator<AuthenticatorUserAccount>() {
        public int compare(AuthenticatorUserAccount authenticatorUserAccount, AuthenticatorUserAccount authenticatorUserAccount2) {
            return authenticatorUserAccount.getPuid().compareTo(authenticatorUserAccount2.getPuid());
        }
    };
    private static int ResultTooManyFlights = -1;
    private AuthenticatorAccountManager _accountManager;
    private final Context _applicationContext;
    private final TypedStorage _typedStorage;

    protected FlightManager() {
        this._applicationContext = null;
        this._typedStorage = null;
    }

    public FlightManager(Context context) {
        this._applicationContext = context;
        this._accountManager = new AuthenticatorAccountManager(this._applicationContext);
        this._typedStorage = new TypedStorage(this._applicationContext);
    }

    private AuthenticatorUserAccount getPrimeAccount() {
        return this._typedStorage.hasAccounts() ? (AuthenticatorUserAccount) Collections.min(this._accountManager.getAccounts(), PuidComparator) : null;
    }

    private void unenrollAllFlights() {
    }

    public boolean canShowNgc() {
        return isInNgcFlight() || new AuthenticatorAccountManager(this._applicationContext).hasNgcSessionApprovalAccounts();
    }

    public void enrollInFlights() {
        Iterable<Integer> flights = getFlights();
        Logger.info("Enrolling in Flights" + TextUtils.join(", ", flights));
        unenrollAllFlights();
        for (Integer intValue : flights) {
            int intValue2 = intValue.intValue();
            if (intValue2 != Flight.QRCode.getFlightID()) {
                if (intValue2 == ResultTooManyFlights) {
                    Logger.error("This client is in too many flights!  They are currently enrolled in " + TextUtils.join(", ", flights));
                } else {
                    Logger.warning("Unrecognized flight number " + intValue2 + " returned");
                }
            }
        }
    }

    public Set<Integer> getFlights() {
        if (isDeviceFlightOverrideEnabled()) {
            Set<Integer> readDeviceBasedFlights = this._typedStorage.readDeviceBasedFlights();
            return readDeviceBasedFlights != null ? readDeviceBasedFlights : Collections.emptySet();
        } else {
            AuthenticatorUserAccount primeAccount = getPrimeAccount();
            return primeAccount != null ? primeAccount.getFlights() : Collections.emptySet();
        }
    }

    public boolean isDeviceFlightOverrideEnabled() {
        return this._typedStorage.readDeviceFlightOverrideEnabled();
    }

    public boolean isInNgcFlight() {
        return getFlights().contains(Integer.valueOf(Flight.QRCode.getFlightID()));
    }

    public void setDeviceFlightOverrideEnabled(boolean z) {
        this._typedStorage.writeDeviceFlightOverrideEnabled(z);
    }

    public void setFlights(Set<Integer> set) {
        AuthenticatorUserAccount primeAccount = getPrimeAccount();
        if (isDeviceFlightOverrideEnabled()) {
            this._typedStorage.writeDeviceBasedFlights(set);
        } else if (primeAccount != null) {
            primeAccount.setFlights(set);
            this._typedStorage.writeAccount(primeAccount);
        }
    }
}
