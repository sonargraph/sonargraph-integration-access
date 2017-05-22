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

import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Implementation which is able to decide to use a CDATA section for a string.
 * @see http://stackoverflow.com/questions/3136375/how-to-generate-cdata-block-using-jaxb
 */
public class XmlCDataStreamWriter implements XMLStreamWriter
{
    private static final String CDATA_END = "]]>";
    private static final String CDATA_START = "<![CDATA[";

    /**
     * The CDATA section end string "]]>" must not be part of text in a CDATA section.
     * Split it into "]]" and ">", and put CDATA_END and CDATA_START in between.
     */
    private static final String CDATA_END_ENCODED = "]]" + CDATA_END + CDATA_START + ">";

    private static final Pattern XML_CHARS = Pattern.compile("[&<>]");
    private final XMLStreamWriter m_xmlStreamWriter;

    public XmlCDataStreamWriter(final XMLStreamWriter writer)
    {
        m_xmlStreamWriter = writer;
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        final boolean useCData = XML_CHARS.matcher(text).find();
        if (useCData)
        {
            m_xmlStreamWriter.writeCData(text.replaceAll(CDATA_END, CDATA_END_ENCODED));
        }
        else
        {
            m_xmlStreamWriter.writeCharacters(text);
        }
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        m_xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        m_xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        m_xmlStreamWriter.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        m_xmlStreamWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        m_xmlStreamWriter.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException
    {
        m_xmlStreamWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        m_xmlStreamWriter.flush();
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        m_xmlStreamWriter.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        m_xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        m_xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException
    {
        m_xmlStreamWriter.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        m_xmlStreamWriter.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        m_xmlStreamWriter.writeComment(data);
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        m_xmlStreamWriter.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException
    {
        m_xmlStreamWriter.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        m_xmlStreamWriter.writeCData(data);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        m_xmlStreamWriter.writeDTD(dtd);
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        m_xmlStreamWriter.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException
    {
        m_xmlStreamWriter.writeStartDocument(encoding, version);
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        m_xmlStreamWriter.writeCharacters(text, start, len);
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return m_xmlStreamWriter.getPrefix(uri);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        m_xmlStreamWriter.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        m_xmlStreamWriter.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        m_xmlStreamWriter.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return m_xmlStreamWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(final String name) throws IllegalArgumentException
    {
        return m_xmlStreamWriter.getProperty(name);
    }
}
