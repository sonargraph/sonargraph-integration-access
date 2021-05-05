/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlElementAttributesExtractor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlElementAttributesExtractor.class);

    private static class ElementProcessedException extends SAXException
    {
        private static final long serialVersionUID = 4957345534010873888L;
        private final Map<String, String> attributeValues;

        public ElementProcessedException(final Map<String, String> attributeValues)
        {
            super("");
            this.attributeValues = attributeValues;
        }

        public Map<String, String> getAttributeValues()
        {
            return attributeValues;
        }
    }

    private XmlElementAttributesExtractor()
    {
        super();
    }

    /**
     * Processes an XML file until an element is found and returns its attributes.
     *
     * @param file
     * @return if the element exists, returns the attributes. If it does not exist, returns an empty map. If the XML processing fails, returns null.
     */
    public static Map<String, String> process(final File file, final String elementName)
    {
        assert file != null : "Parameter 'file' of method 'process' must not be null";
        assert elementName != null && elementName.length() > 0 : "Parameter 'elementName' of method 'process' must not be empty";

        boolean error = false;
        try (InputStream is = new FileInputStream(file))
        {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final DefaultHandler processor = new DefaultHandler()
            {
                private StringBuilder m_content;
                private final Map<String, String> attributeToValues = new LinkedHashMap<>();

                @Override
                public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                        throws SAXException
                {
                    //throwing an exception is not ideal but the fastest way to terminate the parsing.
                    if (qName.equals(elementName))
                    {
                        for (int i = 0; i < attributes.getLength(); i++)
                        {
                            attributeToValues.put(attributes.getQName(i), attributes.getValue(i));
                        }
                    }
                }

                @Override
                public void endElement(final String uri, final String localName, final String qName) throws SAXException
                {
                    if (qName.equals(elementName))
                    {
                        throw new ElementProcessedException(attributeToValues);
                    }
                }

                @Override
                public void characters(final char[] ch, final int start, final int length) throws SAXException
                {
                    if (m_content != null)
                    {
                        m_content.append(ch, start, length);
                    }
                }
            };
            saxParser.parse(is, processor);
        }
        catch (final ElementProcessedException ex)
        {
            return ex.getAttributeValues();
        }
        catch (final SAXParseException ex)
        {
            //input file is not a valid XML file
            error = true;
        }
        catch (final FileNotFoundException ex)
        {
            //input file does not exist
            error = true;
        }
        catch (final IOException ex)
        {
            LOGGER.error("Failed to determine root element of " + file.getAbsolutePath(), ex);
            error = true;
        }
        catch (final ParserConfigurationException ex)
        {
            LOGGER.error("Fatal configuration exception", ex);
            error = true;
        }
        catch (final SAXException ex)
        {
            LOGGER.error("Generic SAXException while processing " + file.getAbsolutePath(), ex);
            error = true;
        }

        return error ? null : Collections.emptyMap();
    }
}