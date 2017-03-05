package com.microsoft.onlineid.internal.profile;

import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;
import android.util.JsonToken;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.MsaService;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.KnownEnvironment;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ProfileManager {
    protected static final String AppIdRequestProperty = "PS-ApplicationId";
    protected static final String AuthTicketRequestProperty = "PS-MSAAuthTicket";
    protected static final String ClientVersion = "MSA Android";
    protected static final String ClientVersionRequestProperty = "X-ClientVersion";
    protected static final String OrderedBasicNameAttributeName = "PublicProfile.OrderedBasicName";
    protected static final String ProfileAppId = "F5EF4246-47B3-403A-885B-023BBAE0F547";
    protected static final ISecurityScope ProfileServiceScopeInt = new SecurityScope("ssl.live-int.com", "mbi_ssl");
    protected static final ISecurityScope ProfileServiceScopeProduction = new SecurityScope("ssl.live.com", "mbi_ssl");
    protected static final String ProfileServiceUrlInt = "https://directory.services.live-int.com/profile/mine/WLX.Profiles.IC.json";
    protected static final String ProfileServiceUrlProduction = "https://pf.directory.live.com/profile/mine/WLX.Profiles.IC.json";
    private final Context _applicationContext;
    private final ClockSkewManager _clockSkewManager;
    private final JsonParser _jsonParser;
    private final ServerConfig _serverConfig;
    private final TicketManager _ticketManager;
    private final TransportFactory _transportFactory;
    private final TypedStorage _typedStorage;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$util$JsonToken = new int[JsonToken.values().length];

        static {
            try {
                $SwitchMap$android$util$JsonToken[JsonToken.NULL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$util$JsonToken[JsonToken.STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    protected static class JsonParser {
        protected void findName(JsonReader jsonReader, String str) throws IOException {
            while (jsonReader.hasNext()) {
                if (!str.equals(jsonReader.nextName())) {
                    jsonReader.skipValue();
                } else {
                    return;
                }
            }
            throw new IOException("Unable to find name " + str);
        }

        protected String parseDisplayName(JsonReader jsonReader) throws IOException {
            try {
                jsonReader.beginObject();
                findName(jsonReader, "Views");
                jsonReader.beginArray();
                jsonReader.beginObject();
                findName(jsonReader, "Attributes");
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    String readDisplayNameFromEntry = readDisplayNameFromEntry(jsonReader);
                    if (readDisplayNameFromEntry != null) {
                        return readDisplayNameFromEntry;
                    }
                }
                jsonReader.close();
                return null;
            } finally {
                jsonReader.close();
            }
        }

        protected String readDisplayNameFromEntry(JsonReader jsonReader) throws IOException {
            jsonReader.beginObject();
            String str = null;
            Object obj = null;
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if ("Name".equals(nextName)) {
                    obj = jsonReader.nextString();
                } else if ("Value".equals(nextName)) {
                    switch (AnonymousClass1.$SwitchMap$android$util$JsonToken[jsonReader.peek().ordinal()]) {
                        case NativeRegExp.MATCH /*1*/:
                            jsonReader.nextNull();
                            str = null;
                            break;
                        case NativeRegExp.PREFIX /*2*/:
                            str = jsonReader.nextString();
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return ProfileManager.OrderedBasicNameAttributeName.equals(obj) ? str : null;
        }
    }

    @Deprecated
    public ProfileManager() {
        this._applicationContext = null;
        this._clockSkewManager = null;
        this._jsonParser = null;
        this._serverConfig = null;
        this._ticketManager = null;
        this._transportFactory = null;
        this._typedStorage = null;
    }

    public ProfileManager(Context context) {
        this._applicationContext = context;
        this._clockSkewManager = new ClockSkewManager(context);
        this._jsonParser = new JsonParser();
        this._serverConfig = new ServerConfig(context);
        this._ticketManager = new TicketManager(context);
        this._transportFactory = new TransportFactory(context);
        this._typedStorage = new TypedStorage(context);
    }

    public ApiRequest createUpdateProfileRequest(String str) {
        return new ApiRequest(this._applicationContext, new Intent(this._applicationContext, MsaService.class).setAction(MsaService.ActionUpdateProfile)).setAccountPuid(str);
    }

    public void updateProfile(String str, String str2) throws IOException, NetworkException, AccountNotFoundException, PromptNeededException, InvalidResponseException, StsException {
        boolean equals = this._serverConfig.getEnvironment().equals(KnownEnvironment.Production.getEnvironment());
        Ticket ticket = this._ticketManager.getTicket(str, equals ? ProfileServiceScopeProduction : ProfileServiceScopeInt, str2, true);
        Transport createTransport = this._transportFactory.createTransport();
        InputStream inputStream = null;
        try {
            createTransport.openGetRequest(new URL(equals ? ProfileServiceUrlProduction : ProfileServiceUrlInt));
            createTransport.addRequestProperty(AppIdRequestProperty, ProfileAppId);
            createTransport.addRequestProperty(AuthTicketRequestProperty, ticket.getValue());
            createTransport.addRequestProperty(ClientVersionRequestProperty, ClientVersion);
            inputStream = createTransport.getResponseStream();
            String parseDisplayName = this._jsonParser.parseDisplayName(new JsonReader(new BufferedReader(new InputStreamReader(inputStream))));
            AuthenticatorUserAccount readAccount = this._typedStorage.readAccount(str);
            if (readAccount == null) {
                throw new AccountNotFoundException("Account was deleted before operation could be completed.");
            }
            readAccount.setDisplayName(parseDisplayName);
            readAccount.setTimeOfLastProfileUpdate(this._clockSkewManager.getCurrentServerTime().getTime());
            this._typedStorage.writeAccount(readAccount);
        } finally {
            createTransport.closeConnection();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
