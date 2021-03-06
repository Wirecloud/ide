/*
 *  Copyright (c) 2015 CoNWeT Lab., Universidad Politécnica de Madrid
 *
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MACDescription {

    public String type;
    public String vendor;
    public String name;
    public String version;
    public String description;

    private static XPath xpath;

    public MACDescription(File descriptionFile) throws FileNotFoundException, IOException, MACDescriptionParseException {
        this(new FileInputStream(descriptionFile));
    }

    public MACDescription(InputStream stream) throws IOException, MACDescriptionParseException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder; Document doc;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MACDescriptionParseException("Unexpected Error", e);
        }
        try {
            doc = dBuilder.parse(stream);
        } catch (SAXException e) {
            throw new MACDescriptionParseException("Invalid XML document", e);
        }
        String namespace = doc.getDocumentElement().getNamespaceURI();
        if (namespace == null) {
            throw new MACDescriptionParseException("Document without root namespace");
        }

        switch(namespace) {
        case "http://wirecloud.conwet.fi.upm.es/ns/template#":
            parseOldXMLDescription(doc);
            break;
        case "http://wirecloud.conwet.fi.upm.es/ns/macdescription/1":
            parseXMLDescription(doc);
            break;
        default:
            throw new MACDescriptionParseException("Unsupported description format");
        };
    }

    private static class HardcodedNamespaceResolver implements NamespaceContext {

        /**
         * This method returns the uri for all prefixes needed. Wherever possible
         * it uses XMLConstants.
         * 
         * @param prefix
         * @return uri
         */
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("wc")) {
                return "http://wirecloud.conwet.fi.upm.es/ns/macdescription/1";
            } else if (prefix.equals("oldwc")) {
                return "http://wirecloud.conwet.fi.upm.es/ns/template#";
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        }

        public String getPrefix(String namespaceURI) {
            // Not needed in this context.
            return null;
        }

        @SuppressWarnings("rawtypes")
        public Iterator getPrefixes(String namespaceURI) {
            // Not needed in this context.
            return null;
        }

    }

    static {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new HardcodedNamespaceResolver());
    }

    private String getStringField(Document doc, String query) throws MACDescriptionParseException {
        XPathExpression expr; NodeList nList;
        try {
            expr = xpath.compile(query);
            nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new MACDescriptionParseException("Unexpected error", e);
        }
        if (nList.getLength() != 0) {
            return nList.item(0).getTextContent();
        }

        return null;
    }

    private void parseOldXMLDescription(Document doc) throws MACDescriptionParseException {
        type = "widget";
        vendor = getStringField(doc, "/oldwc:Template/oldwc:Catalog.ResourceDescription/oldwc:Vendor");
        name = getStringField(doc, "/oldwc:Template/oldwc:Catalog.ResourceDescription/oldwc:Name");
        version = getStringField(doc, "/oldwc:Template/oldwc:Catalog.ResourceDescription/oldwc:Version");
        description = getStringField(doc, "/oldwc:Template/oldwc:Catalog.ResourceDescription/oldwc:Description");
    }

    private void parseXMLDescription(Document doc) throws MACDescriptionParseException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new HardcodedNamespaceResolver());

        Element root = doc.getDocumentElement();

        type = root.getTagName();

        if (root.hasAttribute("vendor")) {
            vendor = root.getAttribute("vendor");
        }

        if (root.hasAttribute("name")) {
            name = root.getAttribute("name");
        }

        if (root.hasAttribute("version")) {
            version = root.getAttribute("version");
        }

        // Description
        description = getStringField(doc, "/wc:*/wc:details/wc:description");
    }
}