package com.microsoft.cll.android;

import android.util.Base64;
import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolWriter;
import java.io.IOException;
import java.util.Stack;
import org.mozilla.javascript.Token;

public class JsonProtocol extends ProtocolWriter {
    private static final char ESCAPE_CHAR = '\\';
    private static final char[] HEX_CHARACTERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String NUMERIC_ESCAPE_STRING = "\\u";
    private static final Stack<Boolean> inContainerStack = new Stack();
    private static final Stack<BondDataType> keyTypes = new Stack();
    private static final Stack<BondDataType> valueTypes = new Stack();
    private final Stack<Boolean> containerIsTyped = new Stack();
    private boolean inContainer;
    private boolean isKey;
    private final StringBuilder stringBuilder;

    public JsonProtocol(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    private void actuallyWriteString(String str) {
        if (str == null) {
            appendEscaped("null");
            appendInContainer();
            return;
        }
        this.stringBuilder.append('\"');
        appendEscaped(str);
        this.stringBuilder.append('\"');
        appendInContainer();
    }

    private void appendComma() {
        if (this.stringBuilder.length() > 0 && this.stringBuilder.charAt(this.stringBuilder.length() - 1) != ',') {
            this.stringBuilder.append(',');
        }
    }

    private void appendEscaped(String str) {
        int length = this.stringBuilder.length();
        this.stringBuilder.append(str);
        int length2 = this.stringBuilder.length();
        while (length < length2) {
            char charAt = this.stringBuilder.charAt(length);
            int i;
            switch (charAt) {
                case Token.BITOR /*9*/:
                    i = length + 1;
                    this.stringBuilder.insert(length, ESCAPE_CHAR);
                    length = i + 1;
                    this.stringBuilder.setCharAt(i, 't');
                    length2++;
                    break;
                case Token.BITXOR /*10*/:
                    i = length + 1;
                    this.stringBuilder.insert(length, ESCAPE_CHAR);
                    length = i + 1;
                    this.stringBuilder.setCharAt(i, 'n');
                    length2++;
                    break;
                case Token.NE /*13*/:
                    i = length + 1;
                    this.stringBuilder.insert(length, ESCAPE_CHAR);
                    length = i + 1;
                    this.stringBuilder.setCharAt(i, 'r');
                    length2++;
                    break;
                case Token.GETPROPNOWARN /*34*/:
                    i = length + 1;
                    this.stringBuilder.insert(length, ESCAPE_CHAR);
                    length = i + 1;
                    this.stringBuilder.setCharAt(i, '\"');
                    length2++;
                    break;
                case Token.ASSIGN_BITOR /*92*/:
                    this.stringBuilder.insert(length, ESCAPE_CHAR);
                    length += 2;
                    length2++;
                    break;
                default:
                    if (!Character.isISOControl(charAt)) {
                        length++;
                        break;
                    }
                    int i2 = length + 1;
                    this.stringBuilder.insert(length, NUMERIC_ESCAPE_STRING);
                    i = i2 + 1;
                    this.stringBuilder.setCharAt(i2, HEX_CHARACTERS[(charAt >> 12) & 15]);
                    i2 = i + 1;
                    this.stringBuilder.insert(i, HEX_CHARACTERS[(charAt >> 8) & 15]);
                    i = i2 + 1;
                    this.stringBuilder.insert(i2, HEX_CHARACTERS[(charAt >> 4) & 15]);
                    length = i + 1;
                    this.stringBuilder.insert(i, HEX_CHARACTERS[charAt & 15]);
                    length2 += 5;
                    break;
            }
        }
    }

    private void appendInContainer() {
        if (this.inContainer) {
            appendComma();
        }
    }

    private void removeLastComma() {
        if (this.stringBuilder.length() > 0 && this.stringBuilder.charAt(this.stringBuilder.length() - 1) == ',') {
            this.stringBuilder.deleteCharAt(this.stringBuilder.length() - 1);
        }
    }

    private void writeJsonFieldName(String str) {
        this.stringBuilder.append("\"");
        appendEscaped(str);
        this.stringBuilder.append("\":");
    }

    public boolean hasCapability(ProtocolCapability protocolCapability) {
        return protocolCapability == ProtocolCapability.CAN_OMIT_FIELDS ? true : super.hasCapability(protocolCapability);
    }

    public String toString() {
        return this.stringBuilder.toString();
    }

    public void writeBegin() {
    }

    public void writeBlob(BondBlob bondBlob) throws IOException {
        this.stringBuilder.append(Base64.encode(bondBlob.getBuffer(), 0));
        appendInContainer();
    }

    public void writeBool(boolean z) throws IOException {
        this.stringBuilder.append(z);
        appendInContainer();
    }

    public void writeContainerBegin(int i, BondDataType bondDataType) throws IOException {
        this.stringBuilder.append('[');
        this.containerIsTyped.push(Boolean.TRUE);
        inContainerStack.push(Boolean.valueOf(true));
    }

    public void writeContainerBegin(int i, BondDataType bondDataType, BondDataType bondDataType2) throws IOException {
        this.stringBuilder.append('{');
        this.containerIsTyped.push(Boolean.FALSE);
        this.inContainer = true;
        this.isKey = true;
        keyTypes.push(bondDataType);
        valueTypes.push(bondDataType2);
        inContainerStack.push(Boolean.valueOf(true));
    }

    public void writeContainerEnd() throws IOException {
        removeLastComma();
        this.stringBuilder.append(((Boolean) this.containerIsTyped.pop()).booleanValue() ? ']' : '}');
        this.inContainer = false;
        this.isKey = false;
        keyTypes.pop();
        inContainerStack.pop();
    }

    public void writeDouble(double d) throws IOException {
        this.stringBuilder.append(d);
        appendInContainer();
    }

    public void writeEnd() {
    }

    public void writeFieldBegin(BondDataType bondDataType, int i, BondSerializable bondSerializable) throws IOException {
        Metadata metadata = bondSerializable instanceof Metadata ? (Metadata) bondSerializable : null;
        if (metadata != null) {
            writeJsonFieldName(metadata.getName());
        }
    }

    public void writeFieldEnd() {
        appendComma();
    }

    public void writeFieldOmitted(BondDataType bondDataType, int i, BondSerializable bondSerializable) throws IOException {
    }

    public void writeFloat(float f) throws IOException {
        this.stringBuilder.append(f);
        appendInContainer();
    }

    public void writeInt16(short s) throws IOException {
        this.stringBuilder.append(s);
        appendInContainer();
    }

    public void writeInt32(int i) throws IOException {
        this.stringBuilder.append(i);
        appendInContainer();
    }

    public void writeInt64(long j) throws IOException {
        this.stringBuilder.append(j);
        appendInContainer();
    }

    public void writeInt8(byte b) throws IOException {
        this.stringBuilder.append(b);
        appendInContainer();
    }

    public void writeString(String str) throws IOException {
        if (((Boolean) inContainerStack.peek()).booleanValue() && !keyTypes.empty() && keyTypes.peek() == BondDataType.BT_STRING) {
            if (this.isKey) {
                writeJsonFieldName(str);
            } else if (!this.isKey) {
                actuallyWriteString(str);
            }
            if (valueTypes.peek() == BondDataType.BT_STRING) {
                this.isKey = !this.isKey;
                return;
            }
            return;
        }
        actuallyWriteString(str);
    }

    public void writeStructBegin(BondSerializable bondSerializable, boolean z) {
        if (!z) {
            this.stringBuilder.append('{');
        }
        inContainerStack.push(Boolean.valueOf(false));
    }

    public void writeStructEnd(boolean z) {
        if (!z) {
            removeLastComma();
            this.stringBuilder.append('}');
            if (inContainerStack.size() > 1) {
                appendComma();
            }
        }
        inContainerStack.pop();
    }

    public void writeUInt16(short s) throws IOException {
        this.stringBuilder.append(s);
        appendInContainer();
    }

    public void writeUInt32(int i) throws IOException {
        this.stringBuilder.append(i);
        appendInContainer();
    }

    public void writeUInt64(long j) throws IOException {
        this.stringBuilder.append(j);
        appendInContainer();
    }

    public void writeUInt8(byte b) throws IOException {
        this.stringBuilder.append(b);
        appendInContainer();
    }

    public void writeVersion() throws IOException {
    }

    public void writeWString(String str) throws IOException {
        writeString(str);
    }
}
