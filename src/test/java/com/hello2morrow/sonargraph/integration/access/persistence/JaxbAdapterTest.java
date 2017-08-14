/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;

public final class JaxbAdapterTest
{
    private static final String PART1 = "-I./include";
    private static final String PART2 = "-DG_ANSICPP";
    private static final String CR_LF = "\r\n";
    private static final String LF = "\n";
    private static final String[] TEST_SETTINGS_LINES = new String[] { "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
            "<testXmlObject>", PART1, PART2, "</testXmlObject>" };

    private static final String ISO_8859_1 = "./src/test/encodingTest/Iso8859-1.xml";
    private static final String US_ASCII = "./src/test/encodingTest/UsAscii.xml";

    private static final String TEST_SETTINGS_XML_CR_LF = convertArrayToString(TEST_SETTINGS_LINES, CR_LF);
    private static final String TEST_SETTINGS_XML_LF = convertArrayToString(TEST_SETTINGS_LINES, LF);
    private static final String SCHEMA_URL = "com/hello2morrow/sonargraph/integration/access/persistence/test.xsd";
    private static JaxbAdapter<TestXmlObject> s_xmlAdapter;

    static String convertArrayToString(final String[] lines, final String lineSeparator)
    {
        final StringBuilder builder = new StringBuilder(lines[0]);
        for (int i = 1; i < lines.length; i++)
        {
            builder.append(lineSeparator);
            builder.append(lines[i]);
        }
        return builder.toString();
    }

    @BeforeClass
    public static void beforeClass()
    {
        s_xmlAdapter = new JaxbAdapter<TestXmlObject>(TestXmlObject.class, TestXmlObject.class.getClassLoader().getResource(SCHEMA_URL));
    }

    @Test
    public void testLoad()
    {
        final OperationResult result = new OperationResult("Convert xml");
        String optionsText = extractOptionsText(s_xmlAdapter, TEST_SETTINGS_XML_CR_LF, result);
        assertFalse("The String does not start with CR+LF", optionsText.startsWith(CR_LF));
        assertTrue("The String starts only with LF", optionsText.startsWith(LF));
        assertTrue(result.isSuccess());

        optionsText = extractOptionsText(s_xmlAdapter, TEST_SETTINGS_XML_LF, result);
        assertFalse("The String does not start with CR+LF", optionsText.startsWith(CR_LF));
        assertTrue("The String starts only with LF", optionsText.startsWith(LF));
        assertTrue(result.isSuccess());
    }

    private String extractOptionsText(final JaxbAdapter<TestXmlObject> xmlAdapter, final String xmlInputString, final OperationResult result)
    {
        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);
        final TestXmlObject xmlObject = xmlAdapter.load(new ByteArrayInputStream(xmlInputString.getBytes()), eventHandler);

        return xmlObject.getValue();
    }

    @Test
    public void testSave() throws IOException
    {
        final TestXmlObject xmlObject = new TestXmlObject();
        xmlObject.setValue(convertArrayToString(new String[] { PART1, PART2 }, CR_LF));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        s_xmlAdapter.save(xmlObject, out);
        final String xmlString = out.toString();
        assertFalse("Serialized XML object does not contain any CR+LF", xmlString.contains(CR_LF));
        assertTrue(xmlString.contains(PART1 + LF + PART2));
    }

    private void readAndTestXmlInput(final File xmlFile) throws IOException
    {
        try (FileInputStream in = new FileInputStream(xmlFile))
        {
            final OperationResult result = new OperationResult("Reading non UTF-8 formatted input from file '" + xmlFile + "'");
            final TestXmlObject xmlObject = s_xmlAdapter.load(in, new ValidationEventHandlerImpl(result));
            assertTrue(result.toString(), result.isSuccess());
            assertTrue(xmlObject.getValue().contains(PART1));
            assertTrue(xmlObject.getValue().contains(PART2));
        }
    }

    @Test
    public void testEncoding() throws IOException
    {
        readAndTestXmlInput(new File(ISO_8859_1));
        readAndTestXmlInput(new File(US_ASCII));
    }
}