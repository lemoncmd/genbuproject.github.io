package com.microsoft.xbox.idp.telemetry.utc;

import Microsoft.Telemetry.Data;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.Modifier;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class ClientError extends Data<CommonData> {
    private String callStack;
    private String errorCode;
    private String errorName;
    private String errorText;
    private String pageName;

    public static class Schema {
        private static final Metadata callStack_metadata = new Metadata();
        private static final Metadata errorCode_metadata = new Metadata();
        private static final Metadata errorName_metadata = new Metadata();
        private static final Metadata errorText_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("ClientError");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.ClientError");
            metadata.getAttributes().put("Description", "OnlineId Client Error event");
            errorName_metadata.setName("errorName");
            errorName_metadata.setModifier(Modifier.Required);
            errorName_metadata.getAttributes().put("Description", "the name of the error-  Can be a specific name (such as UserCanceled) or Exception name (if exception handling)");
            errorText_metadata.setName("errorText");
            errorText_metadata.getAttributes().put("Description", "The text of the error message or exception, if applicable");
            errorCode_metadata.setName("errorCode");
            errorCode_metadata.getAttributes().put("Description", "The code we get back in the exception, if applicable.");
            callStack_metadata.setName("callStack");
            callStack_metadata.getAttributes().put("Description", "Call stack if we have it.");
            pageName_metadata.setName("pageName");
            pageName_metadata.getAttributes().put("Description", "Most recent page shown");
            schemaDef.setRoot(getTypeDef(schemaDef));
        }

        private static short getStructDef(SchemaDef schemaDef) {
            short s = (short) 0;
            while (s < schemaDef.getStructs().size()) {
                if (((StructDef) schemaDef.getStructs().get(s)).getMetadata() == metadata) {
                    break;
                }
                s = (short) (s + 1);
            }
            StructDef structDef = new StructDef();
            schemaDef.getStructs().add(structDef);
            structDef.setMetadata(metadata);
            structDef.setBase_def(Microsoft.Telemetry.Data.Schema.getTypeDef(schemaDef));
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 10);
            fieldDef.setMetadata(errorName_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(errorText_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 30);
            fieldDef.setMetadata(errorCode_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 40);
            fieldDef.setMetadata(callStack_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 50);
            fieldDef.setMetadata(pageName_metadata);
            fieldDef.getType().setId(BondDataType.BT_WSTRING);
            structDef.getFields().add(fieldDef);
            return s;
        }

        public static TypeDef getTypeDef(SchemaDef schemaDef) {
            TypeDef typeDef = new TypeDef();
            typeDef.setId(BondDataType.BT_STRUCT);
            typeDef.setStruct_def(getStructDef(schemaDef));
            return typeDef;
        }
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public final String getCallStack() {
        return this.callStack;
    }

    public final String getErrorCode() {
        return this.errorCode;
    }

    public final String getErrorName() {
        return this.errorName;
    }

    public final String getErrorText() {
        return this.errorText;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.errorName;
            case Token.URSH /*20*/:
                return this.errorText;
            case Token.NEW /*30*/:
                return this.errorCode;
            case Token.NUMBER /*40*/:
                return this.callStack;
            case Token.THROW /*50*/:
                return this.pageName;
            default:
                return null;
        }
    }

    public final String getPageName() {
        return this.pageName;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        ClientError clientError = (ClientError) obj;
        return memberwiseCompareQuick(clientError) && memberwiseCompareDeep(clientError);
    }

    protected boolean memberwiseCompareDeep(ClientError clientError) {
        boolean z = (super.memberwiseCompareDeep(clientError)) && (this.errorName == null || this.errorName.equals(clientError.errorName));
        z = z && (this.errorText == null || this.errorText.equals(clientError.errorText));
        z = z && (this.errorCode == null || this.errorCode.equals(clientError.errorCode));
        z = z && (this.callStack == null || this.callStack.equals(clientError.callStack));
        return z && (this.pageName == null || this.pageName.equals(clientError.pageName));
    }

    protected boolean memberwiseCompareQuick(ClientError clientError) {
        boolean z;
        if (super.memberwiseCompareQuick(clientError)) {
            if ((this.errorName == null) == (clientError.errorName == null)) {
                z = true;
                z = z && (this.errorName == null || this.errorName.length() == clientError.errorName.length());
                if (z) {
                    if ((this.errorText != null) == (clientError.errorText != null)) {
                        z = true;
                        z = z && (this.errorText == null || this.errorText.length() == clientError.errorText.length());
                        if (z) {
                            if ((this.errorCode != null) == (clientError.errorCode != null)) {
                                z = true;
                                z = z && (this.errorCode == null || this.errorCode.length() == clientError.errorCode.length());
                                if (z) {
                                    if ((this.callStack != null) == (clientError.callStack != null)) {
                                        z = true;
                                        z = z && (this.callStack == null || this.callStack.length() == clientError.callStack.length());
                                        if (z) {
                                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                                z = true;
                                                return z && (this.pageName == null || this.pageName.length() == clientError.pageName.length());
                                            }
                                        }
                                        z = false;
                                        if (!z) {
                                        }
                                    }
                                }
                                z = false;
                                if (!z) {
                                }
                                if (z) {
                                    if (this.pageName != null) {
                                    }
                                    if (clientError.pageName != null) {
                                    }
                                    if ((this.pageName != null) == (clientError.pageName != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (!z) {
                        }
                        if (z) {
                            if (this.callStack != null) {
                            }
                            if (clientError.callStack != null) {
                            }
                            if ((this.callStack != null) == (clientError.callStack != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.pageName != null) {
                                    }
                                    if (clientError.pageName != null) {
                                    }
                                    if ((this.pageName != null) == (clientError.pageName != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (!z) {
                }
                if (z) {
                    if (this.errorCode != null) {
                    }
                    if (clientError.errorCode != null) {
                    }
                    if ((this.errorCode != null) == (clientError.errorCode != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.callStack != null) {
                            }
                            if (clientError.callStack != null) {
                            }
                            if ((this.callStack != null) == (clientError.callStack != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.pageName != null) {
                                    }
                                    if (clientError.pageName != null) {
                                    }
                                    if ((this.pageName != null) == (clientError.pageName != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.callStack != null) {
                    }
                    if (clientError.callStack != null) {
                    }
                    if ((this.callStack != null) == (clientError.callStack != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.pageName != null) {
                    }
                    if (clientError.pageName != null) {
                    }
                    if ((this.pageName != null) == (clientError.pageName != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (!z) {
        }
        if (z) {
            if (this.errorText != null) {
            }
            if (clientError.errorText != null) {
            }
            if ((this.errorText != null) == (clientError.errorText != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.errorCode != null) {
                    }
                    if (clientError.errorCode != null) {
                    }
                    if ((this.errorCode != null) == (clientError.errorCode != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.callStack != null) {
                            }
                            if (clientError.callStack != null) {
                            }
                            if ((this.callStack != null) == (clientError.callStack != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.pageName != null) {
                                    }
                                    if (clientError.pageName != null) {
                                    }
                                    if ((this.pageName != null) == (clientError.pageName != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.callStack != null) {
                    }
                    if (clientError.callStack != null) {
                    }
                    if ((this.callStack != null) == (clientError.callStack != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.pageName != null) {
                    }
                    if (clientError.pageName != null) {
                    }
                    if ((this.pageName != null) == (clientError.pageName != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.errorCode != null) {
            }
            if (clientError.errorCode != null) {
            }
            if ((this.errorCode != null) == (clientError.errorCode != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.callStack != null) {
                    }
                    if (clientError.callStack != null) {
                    }
                    if ((this.callStack != null) == (clientError.callStack != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.pageName != null) {
                            }
                            if (clientError.pageName != null) {
                            }
                            if ((this.pageName != null) == (clientError.pageName != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.pageName != null) {
                    }
                    if (clientError.pageName != null) {
                    }
                    if ((this.pageName != null) == (clientError.pageName != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.callStack != null) {
            }
            if (clientError.callStack != null) {
            }
            if ((this.callStack != null) == (clientError.callStack != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.pageName != null) {
                    }
                    if (clientError.pageName != null) {
                    }
                    if ((this.pageName != null) == (clientError.pageName != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.pageName != null) {
            }
            if (clientError.pageName != null) {
            }
            if ((this.pageName != null) == (clientError.pageName != null)) {
                z = true;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
    }

    public void read(ProtocolReader protocolReader) throws IOException {
        protocolReader.readBegin();
        readNested(protocolReader);
        protocolReader.readEnd();
    }

    public void read(ProtocolReader protocolReader, BondSerializable bondSerializable) throws IOException {
    }

    public void readNested(ProtocolReader protocolReader) throws IOException {
        if (!protocolReader.hasCapability(ProtocolCapability.TAGGED)) {
            readUntagged(protocolReader, false);
        } else if (readTagged(protocolReader, false)) {
            ReadHelper.skipPartialStruct(protocolReader);
        }
    }

    protected boolean readTagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean z2 = false;
        protocolReader.readStructBegin(z);
        if (super.readTagged(protocolReader, true)) {
            while (true) {
                FieldTag readFieldBegin = protocolReader.readFieldBegin();
                if (readFieldBegin.type == BondDataType.BT_STOP || readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                    if (readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                        z2 = true;
                    }
                    protocolReader.readStructEnd();
                } else {
                    switch (readFieldBegin.id) {
                        case Token.BITXOR /*10*/:
                            this.errorName = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.errorText = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NEW /*30*/:
                            this.errorCode = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NUMBER /*40*/:
                            this.callStack = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.THROW /*50*/:
                            this.pageName = ReadHelper.readWString(protocolReader, readFieldBegin.type);
                            break;
                        default:
                            protocolReader.skip(readFieldBegin.type);
                            break;
                    }
                    protocolReader.readFieldEnd();
                }
            }
        }
        return z2;
    }

    protected void readUntagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean hasCapability = protocolReader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolReader.readStructBegin(z);
        super.readUntagged(protocolReader, true);
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.errorName = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.errorText = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.errorCode = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.callStack = protocolReader.readWString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.pageName = protocolReader.readWString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("ClientError", "com.microsoft.xbox.idp.telemetry.utc.ClientError");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.errorName = BuildConfig.FLAVOR;
        this.errorText = BuildConfig.FLAVOR;
        this.errorCode = BuildConfig.FLAVOR;
        this.callStack = BuildConfig.FLAVOR;
        this.pageName = BuildConfig.FLAVOR;
    }

    public final void setCallStack(String str) {
        this.callStack = str;
    }

    public final void setErrorCode(String str) {
        this.errorCode = str;
    }

    public final void setErrorName(String str) {
        this.errorName = str;
    }

    public final void setErrorText(String str) {
        this.errorText = str;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.errorName = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.errorText = (String) obj;
                return;
            case Token.NEW /*30*/:
                this.errorCode = (String) obj;
                return;
            case Token.NUMBER /*40*/:
                this.callStack = (String) obj;
                return;
            case Token.THROW /*50*/:
                this.pageName = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setPageName(String str) {
        this.pageName = str;
    }

    public void unmarshal(InputStream inputStream) throws IOException {
        Marshaler.unmarshal(inputStream, this);
    }

    public void unmarshal(InputStream inputStream, BondSerializable bondSerializable) throws IOException {
        Marshaler.unmarshal(inputStream, (SchemaDef) bondSerializable, this);
    }

    public void write(ProtocolWriter protocolWriter) throws IOException {
        protocolWriter.writeBegin();
        ProtocolWriter firstPassWriter = protocolWriter.getFirstPassWriter();
        if (firstPassWriter != null) {
            writeNested(firstPassWriter, false);
            writeNested(protocolWriter, false);
        } else {
            writeNested(protocolWriter, false);
        }
        protocolWriter.writeEnd();
    }

    public void writeNested(ProtocolWriter protocolWriter, boolean z) throws IOException {
        boolean hasCapability = protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        super.writeNested(protocolWriter, true);
        protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.errorName_metadata);
        protocolWriter.writeWString(this.errorName);
        protocolWriter.writeFieldEnd();
        if (hasCapability && this.errorText == Schema.errorText_metadata.getDefault_value().getWstring_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_WSTRING, 20, Schema.errorText_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.errorText_metadata);
            protocolWriter.writeWString(this.errorText);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.errorCode == Schema.errorCode_metadata.getDefault_value().getWstring_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_WSTRING, 30, Schema.errorCode_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 30, Schema.errorCode_metadata);
            protocolWriter.writeWString(this.errorCode);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.callStack == Schema.callStack_metadata.getDefault_value().getWstring_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_WSTRING, 40, Schema.callStack_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 40, Schema.callStack_metadata);
            protocolWriter.writeWString(this.callStack);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.pageName == Schema.pageName_metadata.getDefault_value().getWstring_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_WSTRING, 50, Schema.pageName_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_WSTRING, 50, Schema.pageName_metadata);
            protocolWriter.writeWString(this.pageName);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
