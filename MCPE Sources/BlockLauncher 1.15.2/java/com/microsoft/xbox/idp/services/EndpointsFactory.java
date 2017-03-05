package com.microsoft.xbox.idp.services;

import com.microsoft.xbox.idp.services.Endpoints.Type;
import org.mozilla.javascript.regexp.NativeRegExp;

public class EndpointsFactory {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type[Type.PROD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type[Type.DNET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static Endpoints get() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type[Config.endpointType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return new EndpointsProd();
            case NativeRegExp.PREFIX /*2*/:
                return new EndpointsDnet();
            default:
                return null;
        }
    }
}
