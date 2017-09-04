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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

final class XmlPrettyPrintWriter implements XMLStreamWriter
{
    private static final String DEFAULT_LINE_SEPARATOR = "\n";
    private static final int INDENTATION_LENGTH = 4;
    private static final char INDENT_CHAR = ' ';
    private static final String LINEFEED_CHAR = "\n";
    private final XMLStreamWriter writer;
    private int currentDepth = 0;
    private final Map<Integer, Boolean> m_hasChildElement = new HashMap<Integer, Boolean>();

    XmlPrettyPrintWriter(final XMLStreamWriter writer)
    {
        assert writer != null : "Parameter 'writer' of method 'XmlPrettyPrintWriter' must not be null";
        this.writer = writer;
    }

    private static String harmonizeNewLineBreaks(final String text)
    {
        assert text != null : "Parameter 'text' of method 'harmonizeNewLineBreaks' must not be null";
        //First replace Windows "\r\n" with default "\n"
        String harmonizedText = text.replace("\r\n", DEFAULT_LINE_SEPARATOR);
        //Replace MAC "\r" with default "\n"
        harmonizedText = harmonizedText.replace("\r", DEFAULT_LINE_SEPARATOR);
        return harmonizedText;
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        writeStartElementPretty();
        writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        writeStartElementPretty();
        writer.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        writeStartElementPretty();
        writer.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        writeEmptyElementPretty();
        writer.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        writeEmptyElementPretty();
        writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        writeEmptyElementPretty();
        writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        writeEndElementPretty();
        writer.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        writer.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException
    {
        writer.close();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        writer.flush();
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        writer.writeAttribute(localName, harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        writer.writeAttribute(prefix, namespaceURI, localName, harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        writer.writeAttribute(namespaceURI, localName, harmonizeNewLineBreaks(value));
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException
    {
        writer.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        writer.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        writer.writeComment(harmonizeNewLineBreaks(data));
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        writer.writeCData(data);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        writer.writeDTD(dtd);
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        writer.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        writer.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        writer.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException
    {
        writer.writeStartDocument(encoding, version);
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        writer.writeCharacters(harmonizeNewLineBreaks(text));
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        writer.writeCharacters(harmonizeNewLineBreaks(new String(text)).toCharArray(), start, len);
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return writer.getPrefix(uri);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        writer.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        writer.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        writer.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return writer.getNamespaceContext();
    }

    @Override
    public Object getProperty(final String name) throws IllegalArgumentException
    {
        return writer.getProperty(name);
    }

    private String repeat(final int d, final char s)
    {
        final StringBuilder builder = new StringBuilder();
        int count = d;
        while (count-- > 0)
        {
            builder.append(s);
        }
        return builder.toString();
    }

    private void writeStartElementPretty() throws XMLStreamException
    {
        // update state of parent node
        if (currentDepth > 0)
        {
            m_hasChildElement.put(currentDepth - INDENTATION_LENGTH, Boolean.TRUE);
        }

        // reset state of current node
        m_hasChildElement.put(currentDepth, Boolean.FALSE);

        // indent for current m_depth
        writer.writeCharacters(LINEFEED_CHAR);
        writer.writeCharacters(repeat(currentDepth, INDENT_CHAR));

        currentDepth += INDENTATION_LENGTH;
    }

    private void writeEndElementPretty() throws XMLStreamException
    {
        currentDepth -= INDENTATION_LENGTH;

        assert currentDepth >= 0 : "m_depth must not be negative!";
        assert m_hasChildElement.get(currentDepth) != null : "value for m_hasChildElement must exist";

        if (m_hasChildElement.get(currentDepth).equals(Boolean.TRUE))
        {
            writer.writeCharacters(LINEFEED_CHAR);
            writer.writeCharacters(repeat(currentDepth, INDENT_CHAR));
        }
    }

    private void writeEmptyElementPretty() throws XMLStreamException
    {
        // update state of parent node
        if (currentDepth > 0)
        {
            m_hasChildElement.put(currentDepth - INDENTATION_LENGTH, Boolean.TRUE);
        }

        // indent for current m_depth
        writer.writeCharacters(LINEFEED_CHAR);
        writer.writeCharacters(repeat(currentDepth, INDENT_CHAR));
    }
}