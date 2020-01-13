package com.hello2morrow.sonargraph.integration.access.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlElementContentExtractor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlElementContentExtractor.class);

    private static SAXParserFactory s_factory;
    private static SAXParser s_saxParser;
    private static DefaultHandler s_processor;

    private static class ElementProcessedException extends SAXException
    {
        private static final long serialVersionUID = 2126363921505871938L;
        private final String attributeValue;

        public ElementProcessedException(final String attributeValue)
        {
            this.attributeValue = attributeValue;
        }

        public String getAttributeValue()
        {
            return attributeValue;
        }
    }

    private XmlElementContentExtractor()
    {
        super();
    }

    /**
     * Returns the root element of an XML file or null.
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String process(final File file, final String elementName)
    {
        assert file != null : "Parameter 'file' of method 'process' must not be null";
        assert elementName != null && elementName.length() > 0 : "Parameter 'elementName' of method 'process' must not be empty";

        try (InputStream is = new FileInputStream(file))
        {
            if (s_factory == null)
            {
                s_factory = SAXParserFactory.newInstance();
            }
            if (s_saxParser == null)
            {
                s_saxParser = s_factory.newSAXParser();
            }
            if (s_processor == null)
            {
                s_processor = new DefaultHandler()
                {
                    private StringBuilder m_content;

                    @Override
                    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                            throws SAXException
                    {
                        //throwing an exception is not ideal but the fastest way to terminate the parsing.
                        if (qName.equals(elementName))
                        {
                            m_content = new StringBuilder();
                        }
                    }

                    @Override
                    public void endElement(final String uri, final String localName, final String qName) throws SAXException
                    {
                        if (qName.equals(elementName))
                        {
                            throw new ElementProcessedException(m_content.toString());
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
            }
            s_saxParser.parse(is, s_processor);
        }
        catch (final ElementProcessedException ex)
        {
            return ex.getAttributeValue();
        }
        catch (final SAXParseException ex)
        {
            //input file is not a valid XML file
        }
        catch (final FileNotFoundException ex)
        {
            //input file does not exist
        }
        catch (final IOException ex)
        {
            LOGGER.error("Failed to determine root element of " + file.getAbsolutePath(), ex);
        }
        catch (final ParserConfigurationException ex)
        {
            LOGGER.error("Fatal configuration exception", ex);
        }
        catch (final SAXException ex)
        {
            LOGGER.error("Generic SAXException while processing " + file.getAbsolutePath(), ex);
        }
        return null;
    }
}