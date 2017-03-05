package com.microsoft.onlineid.internal.sso.service;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMsaSsoService extends IInterface {

    public static abstract class Stub extends Binder implements IMsaSsoService {
        private static final String DESCRIPTOR = "com.microsoft.onlineid.internal.sso.service.IMsaSsoService";
        static final int TRANSACTION_getAccount = 1;
        static final int TRANSACTION_getAccountById = 2;
        static final int TRANSACTION_getAccountPickerIntent = 4;
        static final int TRANSACTION_getAllAccounts = 3;
        static final int TRANSACTION_getSignInIntent = 5;
        static final int TRANSACTION_getSignOutIntent = 7;
        static final int TRANSACTION_getSignUpIntent = 6;
        static final int TRANSACTION_getTicket = 8;
        static final int TRANSACTION_retrieveBackup = 10;
        static final int TRANSACTION_storeBackup = 9;

        private static class Proxy implements IMsaSsoService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public Bundle getAccount(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccount, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getAccountById(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccountById, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getAccountPickerIntent(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccountPickerIntent, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getAllAccounts(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAllAccounts, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public Bundle getSignInIntent(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignInIntent, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getSignOutIntent(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignOutIntent, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getSignUpIntent(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignUpIntent, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getTicket(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getTicket, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle retrieveBackup(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_retrieveBackup, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle storeBackup(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(Stub.TRANSACTION_getAccount);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_storeBackup, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle2 = obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return bundle2;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMsaSsoService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMsaSsoService)) ? new Proxy(iBinder) : (IMsaSsoService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Bundle bundle = null;
            switch (i) {
                case TRANSACTION_getAccount /*1*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getAccount(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getAccountById /*2*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getAccountById(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getAllAccounts /*3*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getAllAccounts(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getAccountPickerIntent /*4*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getAccountPickerIntent(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getSignInIntent /*5*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getSignInIntent(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getSignUpIntent /*6*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getSignUpIntent(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getSignOutIntent /*7*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getSignOutIntent(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_getTicket /*8*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = getTicket(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_storeBackup /*9*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = storeBackup(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case TRANSACTION_retrieveBackup /*10*/:
                    parcel.enforceInterface(DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    }
                    bundle = retrieveBackup(bundle);
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(TRANSACTION_getAccount);
                        bundle.writeToParcel(parcel2, TRANSACTION_getAccount);
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

    Bundle getAccount(Bundle bundle) throws RemoteException;

    Bundle getAccountById(Bundle bundle) throws RemoteException;

    Bundle getAccountPickerIntent(Bundle bundle) throws RemoteException;

    Bundle getAllAccounts(Bundle bundle) throws RemoteException;

    Bundle getSignInIntent(Bundle bundle) throws RemoteException;

    Bundle getSignOutIntent(Bundle bundle) throws RemoteException;

    Bundle getSignUpIntent(Bundle bundle) throws RemoteException;

    Bundle getTicket(Bundle bundle) throws RemoteException;

    Bundle retrieveBackup(Bundle bundle) throws RemoteException;

    Bundle storeBackup(Bundle bundle) throws RemoteException;
}
