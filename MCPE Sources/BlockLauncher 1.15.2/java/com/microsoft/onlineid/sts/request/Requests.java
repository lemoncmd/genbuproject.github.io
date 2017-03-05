package com.microsoft.onlineid.sts.request;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Requests {
    public static Element appendElement(Node node, String str) {
        Object createElement = node.getOwnerDocument().createElement(str);
        node.appendChild(createElement);
        return createElement;
    }

    public static Element appendElement(Node node, String str, String str2) {
        Element appendElement = appendElement(node, str);
        appendElement.setTextContent(str2);
        return appendElement;
    }

    public static Element xmlStringToElement(String str) throws SAXException {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(str))).getDocumentElement();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }
}
