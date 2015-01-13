package com.conwet.wirecloud.ide;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.junit.Test;


public class MACDescriptionTest extends TestCase {

    private String INCOMPLETE_OLD_XML_DESCRIPTION = "<Template xmlns=\"http://wirecloud.conwet.fi.upm.es/ns/template#\"><Catalog.ResourceDescription><Name>Test</Name></Catalog.ResourceDescription></Template>";
    private String BASIC_OLD_XML_DESCRIPTION = "<Template xmlns=\"http://wirecloud.conwet.fi.upm.es/ns/template#\"><Catalog.ResourceDescription><Vendor>WireCloud</Vendor><Name>Test</Name><Version>1.0</Version></Catalog.ResourceDescription></Template>";
    private String INCOMPLETE_XML_DESCRIPTION = "<widget xmlns=\"http://wirecloud.conwet.fi.upm.es/ns/macdescription/1\" name=\"Test\"></widget>";
    private String BASIC_XML_DESCRIPTION = "<widget xmlns=\"http://wirecloud.conwet.fi.upm.es/ns/macdescription/1\" vendor=\"WireCloud\" name=\"Test\" version=\"1.0\"></widget>";

    public MACDescriptionTest(String testname) throws MalformedURLException {
        super(testname);
    }

    @Test
    public void testParseBasicOldXMLDescription() throws IOException, MACDescriptionParseException {
        InputStream source = new ByteArrayInputStream(BASIC_OLD_XML_DESCRIPTION.getBytes(Charset.forName("UTF-8")));
        MACDescription description = new MACDescription(source);
        assertEquals("WireCloud", description.vendor);
        assertEquals("Test", description.name);
        assertEquals("1.0", description.version);
    }

    @Test
    public void testParseIncompleteOldXMLDescription() throws IOException, MACDescriptionParseException {
        InputStream source = new ByteArrayInputStream(INCOMPLETE_OLD_XML_DESCRIPTION.getBytes(Charset.forName("UTF-8")));
        MACDescription description = new MACDescription(source);
        assertEquals("Test", description.name);
        assertNull(description.vendor);
        assertNull(description.version);
    }

    @Test
    public void testParseBasicXMLDescription() throws IOException, MACDescriptionParseException {
        InputStream source = new ByteArrayInputStream(BASIC_XML_DESCRIPTION.getBytes(Charset.forName("UTF-8")));
        MACDescription description = new MACDescription(source);
        assertEquals("WireCloud", description.vendor);
        assertEquals("Test", description.name);
        assertEquals("1.0", description.version);
    }

    @Test
    public void testParseIncompleteXMLDescription() throws IOException, MACDescriptionParseException {
        InputStream source = new ByteArrayInputStream(INCOMPLETE_XML_DESCRIPTION.getBytes(Charset.forName("UTF-8")));
        MACDescription description = new MACDescription(source);
        assertEquals("Test", description.name);
        assertNull(description.vendor);
        assertNull(description.version);
    }
}