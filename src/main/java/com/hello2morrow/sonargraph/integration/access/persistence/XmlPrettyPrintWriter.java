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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

final class XmlPrettyPrintWriter implements XMLStreamWriter
{
    private static class IndentationInfo
    {
        private final int indentationLevel;
        private boolean hasChild = false;

        public IndentationInfo(final int indentationLevel)
        {
            this.indentationLevel = indentationLevel;
        }
    }

    private static final int INDENTATION_LENGTH = 1;
    private static final char INDENT_CHAR = '\t';
    private static final String LINEFEED_CHAR = System.getProperty("line.separator");

    private final Deque<IndentationInfo> indentationStack = new ArrayDeque<>();
    private final XMLStreamWriter writer;

    XmlPrettyPrintWriter(final XMLStreamWriter writer)
    {
        assert writer != null : "Parameter 'writer' of method 'XmlPrettyPrintWriter' must not be null";
        this.writer = writer;
        indentationStack.push(new IndentationInfo(0));
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        writeIndentationForStartElement();
        writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        writeIndentationForStartElement();
        writer.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        writeIndentationForStartElement();
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
        writeIndentationForEndElement();
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
        writer.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        writer.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        writer.writeAttribute(namespaceURI, localName, value);
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
        writer.writeComment(data);
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
        writer.writeCharacters(text);
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        writer.writeCharacters(text, start, len);
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
    public Object getProperty(final String name)
    {
        return writer.getProperty(name);
    }

    private char[] repeat(final int d, final char s)
    {
        final char[] padding = new char[d];
        Arrays.fill(padding, 0, d, s);
        return padding;
    }

    private void writeIndentationForStartElement() throws XMLStreamException
    {
        // update state of parent node
        final IndentationInfo current = indentationStack.peek();
        if (indentationStack.size() > 0)
        {
            current.hasChild = true;
        }

        writer.writeCharacters(LINEFEED_CHAR);
        writePadding(current);

        final int nextIndentationLevel = current.indentationLevel + INDENTATION_LENGTH;
        indentationStack.push(new IndentationInfo(nextIndentationLevel));
    }

    private void writeIndentationForEndElement() throws XMLStreamException
    {
        final IndentationInfo current = indentationStack.pop();

        //check if we need to put the end element on a new line
        if (current.hasChild)
        {
            final IndentationInfo parent = indentationStack.peek();
            writer.writeCharacters(LINEFEED_CHAR);
            writePadding(parent);
        }
    }

    private void writeEmptyElementPretty() throws XMLStreamException
    {
        // update state of parent node
        if (indentationStack.size() > 1)
        {
            final IndentationInfo current = indentationStack.pop();
            final IndentationInfo parent = indentationStack.peek();
            parent.hasChild = true;
            indentationStack.push(current);
        }

        final IndentationInfo current = indentationStack.peek();
        writer.writeCharacters(LINEFEED_CHAR);
        writePadding(current);
    }

    private void writePadding(final IndentationInfo indentationInfo) throws XMLStreamException
    {
        writer.writeCharacters(repeat(indentationInfo.indentationLevel, INDENT_CHAR), 0, indentationInfo.indentationLevel);
    }
}