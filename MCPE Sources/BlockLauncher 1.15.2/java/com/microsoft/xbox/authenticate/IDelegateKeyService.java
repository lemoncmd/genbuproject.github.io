package com.microsoft.xbox.authenticate;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDelegateKeyService extends IInterface {

    public static abstract class Stub extends Binder implements IDelegateKeyService {
        private static final String DESCRIPTOR = "com.microsoft.xbox.authenticate.IDelegateKeyService";
        static final int TRANSACTION_requestDelegateRPSTicketSilently = 1;

        private static class Proxy implements IDelegateKeyService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public DelegateRPSTicketResult requestDelegateRPSTicketSilently() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_requestDelegateRPSTicketSilently, obtain, obtain2, 0);
                    obtain2.readException();
                    DelegateRPSTicketResult delegateRPSTicketResult = obtain2.readInt() != 0 ? (DelegateRPSTicketResult) DelegateRPSTicketResult.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return delegateRPSTicketResult;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDelegateKeyService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IDelegateKeyService)) ? new Proxy(iBinder) : (IDelegateKeyService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case TRANSACTION_requestDelegateRPSTicketSilently /*1*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    DelegateRPSTicketResult requestDelegateRPSTicketSilently = requestDelegateRPSTicketSilently();
                    parcel2.writeNoException();
                    if (requestDelegateRPSTicketSilently != null) {
                        parcel2.writeInt(TRANSACTION_requestDelegateRPSTicketSilently);
                        requestDelegateRPSTicketSilently.writeToParcel(parcel2, TRANSACTION_requestDelegateRPSTicketSilently);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    DelegateRPSTicketResult requestDelegateRPSTicketSilently() throws RemoteException;
}
