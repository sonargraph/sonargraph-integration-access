/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;

/**
 * Decorator of an XMLStreamWriter implementation that produces formatted XML output.
 *
 * @author Ingmar
 *
 * @see http://www.ewernli.com/web/guest/47
 */
public class XmlPrettyPrintWriter implements XMLStreamWriter
{
    public static final int INDENTATION_LENGTH = 4;

    private static final char INDENT_CHAR = ' ';

    //We don't use StringUtility.LINE_SEPARATOR, because we want the same output format on all platforms.
    private static final String LINEFEED_CHAR = "\n";

    private final XMLStreamWriter m_writer;
    private int m_depth = 0;
    private final Map<Integer, Boolean> m_hasChildElement = new HashMap<Integer, Boolean>();

    public XmlPrettyPrintWriter(final XMLStreamWriter writer)
    {
        assert writer != null : "Parameter 'writer' of method 'XmlPrettyPrintWriter' must not be null";
        m_writer = writer;
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        writeStartElementPretty();
        m_writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        writeStartElementPretty();
        m_writer.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        writeStartElementPretty();
        m_writer.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        writeEmptyElementPretty();
        m_writer.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        writeEmptyElementPretty();
        m_writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        writeEmptyElementPretty();
        m_writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        writeEndElementPretty();
        m_writer.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        m_writer.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException
    {
        m_writer.close();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        m_writer.flush();
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        m_writer.writeAttribute(localName, StringUtility.harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        m_writer.writeAttribute(prefix, namespaceURI, localName, StringUtility.harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        m_writer.writeAttribute(namespaceURI, localName, StringUtility.harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException
    {
        m_writer.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        m_writer.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        m_writer.writeComment(StringUtility.harmonizeNewLineBreaks(data));
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        m_writer.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException
    {
        m_writer.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        m_writer.writeCData(data);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        m_writer.writeDTD(dtd);
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        m_writer.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        m_writer.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        m_writer.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException
    {
        m_writer.writeStartDocument(encoding, version);
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        m_writer.writeCharacters(StringUtility.harmonizeNewLineBreaks(text));
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        m_writer.writeCharacters(StringUtility.harmonizeNewLineBreaks(new String(text)).toCharArray(), start, len);
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return m_writer.getPrefix(uri);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        m_writer.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        m_writer.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        m_writer.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return m_writer.getNamespaceContext();
    }

    @Override
    public Object getProperty(final String name) throws IllegalArgumentException
    {
        return m_writer.getProperty(name);
    }

    private String repeat(int d, final char s)
    {
        final StringBuilder builder = new StringBuilder();

        while (d-- > 0)
        {
            builder.append(s);
        }
        return builder.toString();
    }

    private void writeStartElementPretty() throws XMLStreamException
    {
        // update state of parent node
        if (m_depth > 0)
        {
            m_hasChildElement.put(m_depth - INDENTATION_LENGTH, Boolean.TRUE);
        }

        // reset state of current node
        m_hasChildElement.put(m_depth, Boolean.FALSE);

        // indent for current m_depth
        m_writer.writeCharacters(LINEFEED_CHAR);
        m_writer.writeCharacters(repeat(m_depth, INDENT_CHAR));

        m_depth += INDENTATION_LENGTH;
    }

    private void writeEndElementPretty() throws XMLStreamException
    {
        m_depth -= INDENTATION_LENGTH;

        assert m_depth >= 0 : "m_depth must not be negative!";
        assert m_hasChildElement.get(m_depth) != null : "value for m_hasChildElement must exist";

        if (m_hasChildElement.get(m_depth).equals(Boolean.TRUE))
        {
            m_writer.writeCharacters(LINEFEED_CHAR);
            m_writer.writeCharacters(repeat(m_depth, INDENT_CHAR));
        }
    }

    private void writeEmptyElementPretty() throws XMLStreamException
    {
        // update state of parent node
        if (m_depth > 0)
        {
            m_hasChildElement.put(m_depth - INDENTATION_LENGTH, Boolean.TRUE);
        }

        // indent for current m_depth
        m_writer.writeCharacters(LINEFEED_CHAR);
        m_writer.writeCharacters(repeat(m_depth, INDENT_CHAR));
    }
}
