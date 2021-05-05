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

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

final class XmlCDataStreamWriter implements XMLStreamWriter
{
    private static final String CDATA_END = "]]>";
    private static final String CDATA_START = "<![CDATA[";
    /**
     * The CDATA section end string "]]>" must not be part of text in a CDATA section. Split it into "]]" and ">", and put CDATA_END and CDATA_START
     * in between.
     */
    private static final String CDATA_END_ENCODED = "]]" + CDATA_END + CDATA_START + ">";
    private static final Pattern CDATA_END_PATTERN = Pattern.compile(CDATA_END);
    private static final Pattern XML_CHARS_PATTERN = Pattern.compile("[&<>]");

    private final XMLStreamWriter xmlStreamWriter;

    XmlCDataStreamWriter(final XMLStreamWriter writer)
    {
        xmlStreamWriter = writer;
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        //If there are unescaped characters, the use of a CDATA section is needed.
        final boolean useCData = XML_CHARS_PATTERN.matcher(text).find();
        if (useCData)
        {
            internWriteCData(text);
        }
        else
        {
            xmlStreamWriter.writeCharacters(text);
        }
    }

    private void internWriteCData(final String text) throws XMLStreamException
    {
        final String escapedText = CDATA_END_PATTERN.matcher(text).replaceAll(CDATA_END_ENCODED);
        //We need to replace the CDATA end ']]>' if it exists in the text.
        xmlStreamWriter.writeCData(escapedText);
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        xmlStreamWriter.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        xmlStreamWriter.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        xmlStreamWriter.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        xmlStreamWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        xmlStreamWriter.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException
    {
        xmlStreamWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        xmlStreamWriter.flush();
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        xmlStreamWriter.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException
    {
        xmlStreamWriter.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        xmlStreamWriter.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        xmlStreamWriter.writeComment(data);
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        xmlStreamWriter.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException
    {
        xmlStreamWriter.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        xmlStreamWriter.writeCData(data);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        xmlStreamWriter.writeDTD(dtd);
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        xmlStreamWriter.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        xmlStreamWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        xmlStreamWriter.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException
    {
        xmlStreamWriter.writeStartDocument(encoding, version);
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        final char[] slice = Arrays.copyOfRange(text, start, len);
        for (final char c : slice)
        {
            if (c == '&' || c == '>' || c == '<')
            {
                internWriteCData(new String(slice));
                return;
            }
        }

        xmlStreamWriter.writeCharacters(text, start, len);
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return xmlStreamWriter.getPrefix(uri);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        xmlStreamWriter.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        xmlStreamWriter.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        xmlStreamWriter.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return xmlStreamWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(final String name)
    {
        return xmlStreamWriter.getProperty(name);
    }
}