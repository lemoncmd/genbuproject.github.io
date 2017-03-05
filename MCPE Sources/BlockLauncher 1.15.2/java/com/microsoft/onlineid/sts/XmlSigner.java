package com.microsoft.onlineid.sts;

import android.util.Base64;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sts.SharedKeyGenerator.KeyPurpose;
import com.microsoft.onlineid.sts.request.ISignableRequest;
import com.microsoft.onlineid.sts.request.Requests;
import java.io.CharArrayWriter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlSigner {
    public static final String SignatureNamespace = "http://www.w3.org/2000/09/xmldsig#";
    private final MessageDigest _elementDigester = Cryptography.getSha256Digester();
    private final List<Element> _elementsToDigest = new ArrayList();
    private byte[] _nonce = null;

    private String getId(Element element) {
        return element.getAttribute(element.getNodeName().equals("wsu:Timestamp") ? "wsu:Id" : "Id");
    }

    private byte[] getOrCreateNonce() {
        if (this._nonce == null) {
            this._nonce = new byte[32];
            new SecureRandom().nextBytes(this._nonce);
        }
        return this._nonce;
    }

    private Transformer getTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public void addElementToSign(Element element) {
        this._elementsToDigest.add(element);
    }

    String buildSignedInfoTag() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<SignedInfo xmlns=\"").append(SignatureNamespace).append("\">").append("<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">").append("</CanonicalizationMethod>").append("<SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#hmac-sha256\">").append("</SignatureMethod>");
        for (Element element : this._elementsToDigest) {
            stringBuilder.append("<Reference URI=\"#").append(getId(element)).append("\">").append("<Transforms>").append("<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform>").append("</Transforms>").append("<DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"></DigestMethod>").append("<DigestValue>").append(computeDigest(elementToCanonicalizedString(element))).append("</DigestValue>").append("</Reference>");
        }
        stringBuilder.append("</SignedInfo>");
        return stringBuilder.toString();
    }

    public String computeDigest(String str) {
        return Base64.encodeToString(this._elementDigester.digest(str.getBytes(Strings.Utf8Charset)), 2);
    }

    String computeSignatureForRequest(byte[] bArr, String str) {
        return computeSignatureImplementation(bArr, getOrCreateNonce(), str);
    }

    public String computeSignatureForResponse(byte[] bArr, byte[] bArr2, String str) {
        return computeSignatureImplementation(bArr, bArr2, str.replace("<SignedInfo>", "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\">"));
    }

    String computeSignatureImplementation(byte[] bArr, byte[] bArr2, String str) {
        return Base64.encodeToString(Cryptography.getInitializedHmacSha256Digester(new SecretKeySpec(new SharedKeyGenerator(bArr).generateKey(KeyPurpose.STSDigest, bArr2), Cryptography.HmacSha256Algorithm)).doFinal(str.getBytes(Strings.Utf8Charset)), 2);
    }

    String elementToCanonicalizedString(Element element) {
        Source dOMSource = new DOMSource(element);
        Object streamResult = new StreamResult(new CharArrayWriter());
        Transformer transformer = getTransformer();
        transformer.setOutputProperty("method", "html");
        transformer.setOutputProperty("indent", "no");
        try {
            transformer.transform(dOMSource, streamResult);
            return streamResult.getWriter().toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getEncodedNonce() {
        return Base64.encodeToString(getOrCreateNonce(), 2);
    }

    public void sign(ISignableRequest iSignableRequest) {
        Element parentOfSignatureNode = iSignableRequest.getParentOfSignatureNode();
        Document ownerDocument = parentOfSignatureNode.getOwnerDocument();
        byte[] signingSessionKey = iSignableRequest.getSigningSessionKey();
        String buildSignedInfoTag = buildSignedInfoTag();
        try {
            parentOfSignatureNode.appendChild(ownerDocument.importNode(Requests.xmlStringToElement("<Signature xmlns=\"" + SignatureNamespace + "\">" + buildSignedInfoTag + "<SignatureValue>" + computeSignatureForRequest(signingSessionKey, buildSignedInfoTag) + "</SignatureValue>" + "<KeyInfo>" + "<wsse:SecurityTokenReference><wsse:Reference URI=\"#SignKey\"/></wsse:SecurityTokenReference>" + "</KeyInfo>" + "</Signature>"), true));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
