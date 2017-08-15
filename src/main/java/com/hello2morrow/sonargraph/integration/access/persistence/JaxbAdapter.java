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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.hello2morrow.sonargraph.integration.access.foundation.ExceptionUtility;

public final class JaxbAdapter<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbAdapter.class);
    private static final String INITIALIZATION_FAILED = "Initialization failed";
    private static final String UTF8_ENCODING = "UTF-8";
    private final Unmarshaller reader;
    private final Marshaller writer;

    private static Schema getSchema(final URL... schemaUrls) throws IOException, SAXException
    {
        assert schemaUrls != null : "Parameter 'schemaUrls' of method 'getSchema' must not be null";

        if (schemaUrls.length > 0)
        {
            final Source[] sources = new Source[schemaUrls.length];
            for (int i = 0; i < schemaUrls.length; i++)
            {
                final URL nextSchemaUrl = schemaUrls[i];
                assert nextSchemaUrl != null : " 'nextSchemaUrl' of method 'getSchema' must not be null";
                sources[i] = new StreamSource(nextSchemaUrl.openStream());
            }
            return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources);
        }
        return null;
    }

    JaxbAdapter(final String namespace, final URL... schemaUrls)
    {
        assert namespace != null && namespace.length() > 0 : "Parameter 'namespace' of method 'JaxbAdapter' must not be empty";
        assert schemaUrls != null : "Parameter 'schemaUrls' of method 'JaxbAdapter' must not be null";

        Marshaller writer;
        Unmarshaller reader;

        try
        {
            final JAXBContext jaxbContextProject = JAXBContext.newInstance(namespace, JaxbAdapter.class.getClassLoader());
            final Schema schema = getSchema(schemaUrls);
            reader = createReader(jaxbContextProject, schema);
            writer = createWriter(jaxbContextProject);
        }
        catch (final SAXException | IOException | JAXBException ex)
        {
            LOGGER.error(INITIALIZATION_FAILED, ex);
            reader = null;
            writer = null;
            assert false : INITIALIZATION_FAILED + ": " + ExceptionUtility.collectAll(ex);
        }

        this.reader = reader;
        this.writer = writer;
    }

    public JaxbAdapter(final Class<T> jaxbClass, final URL... schemaUrls)
    {
        assert jaxbClass != null : "Parameter 'jaxbClass' of method 'JaxbAdapter' must not be null";
        assert schemaUrls != null : "Parameter 'schemaUrls' of method 'JaxbAdapter' must not be null";

        Marshaller writer;
        Unmarshaller reader;

        try
        {
            final JAXBContext jaxbContextProject = JAXBContext.newInstance(jaxbClass);
            final Schema schema = getSchema(schemaUrls);
            reader = createReader(jaxbContextProject, schema);
            writer = createWriter(jaxbContextProject);
        }
        catch (final Throwable e)
        {
            LOGGER.error(INITIALIZATION_FAILED, e);
            reader = null;
            writer = null;
            assert false : INITIALIZATION_FAILED + ": " + ExceptionUtility.collectAll(e);
        }

        this.reader = reader;
        this.writer = writer;
    }

    public JaxbAdapter(final List<PersistenceContext> persistentContexts, final ClassLoader classLoader)
    {
        assert persistentContexts != null && !persistentContexts.isEmpty() : "Parameter 'persistentContexts' of method 'JaxbAdapter' must not be empty";

        Marshaller writer;
        Unmarshaller reader;

        try
        {
            final Set<URL> schemaUrls = new LinkedHashSet<>();
            final Set<String> namespaces = new HashSet<>();
            final StringBuilder contextPath = new StringBuilder();
            for (final PersistenceContext nextPersistenceContext : persistentContexts)
            {
                schemaUrls.addAll(nextPersistenceContext.getSchemaUrls());

                for (final String nextNamespace : nextPersistenceContext.getNamespaces())
                {
                    if (namespaces.add(nextNamespace))
                    {
                        contextPath.append(nextNamespace);
                        contextPath.append(":");
                    }
                }
            }
            contextPath.deleteCharAt(contextPath.length() - 1);

            final JAXBContext jaxbContext = JAXBContext.newInstance(contextPath.toString(), classLoader);
            final Schema schema = getSchema(schemaUrls.toArray(new URL[schemaUrls.size()]));
            reader = createReader(jaxbContext, schema);
            writer = createWriter(jaxbContext);
        }
        catch (final Throwable e)
        {
            LOGGER.error(INITIALIZATION_FAILED, e);
            reader = null;
            writer = null;
            assert false : INITIALIZATION_FAILED + ": " + ExceptionUtility.collectAll(e);
        }

        this.reader = reader;
        this.writer = writer;
    }

    private static Unmarshaller createReader(final JAXBContext jaxbContext, final Schema schema) throws JAXBException
    {
        assert jaxbContext != null : "Parameter 'jaxbContext' of method 'createReader' must not be null";
        final Unmarshaller reader = jaxbContext.createUnmarshaller();
        reader.setSchema(schema);
        return reader;
    }

    private static Marshaller createWriter(final JAXBContext jaxbContext) throws JAXBException, PropertyException
    {
        assert jaxbContext != null : "Parameter 'jaxbContext' of method 'createWriter' must not be null";
        final Marshaller writer = jaxbContext.createMarshaller();
        writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        writer.setProperty(Marshaller.JAXB_ENCODING, UTF8_ENCODING);
        return writer;
    }

    public void setMarshalListener(final Marshaller.Listener listener)
    {
        writer.setListener(listener);
    }

    @SuppressWarnings("unchecked")
    public final T load(final InputStream from, final ValidationEventHandler validationHandler)
    {
        assert from != null : "'from' must not be null";
        assert validationHandler != null : "'validationHandler' must not be null";

        try (BufferedInputStream bufferedIn = new BufferedInputStream(from))
        {
            reader.setEventHandler(validationHandler);
            return (T) reader.unmarshal(bufferedIn);
        }
        catch (final IOException | JAXBException e)
        {
            LOGGER.error("Failed to load xml file", e);
        }
        return null;
    }

    public void save(final T jaxbRoot, final OutputStream out) throws IOException
    {
        assert jaxbRoot != null : "Parameter 'jaxbRoot' of method 'save' must not be null";
        assert out != null : "Parameter 'out' of method 'save' must not be null";
        try (OutputStream bufferedOut = new BufferedOutputStream(out))
        {
            //Using two decorators to correctly handle CDATA sections and output formatting.
            //The output formatting property set on the JAXB marshaller is not respected when using XMLStreamWriters.
            final XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(bufferedOut, UTF8_ENCODING);
            final XmlCDataStreamWriter cdataStreamWriter = new XmlCDataStreamWriter(streamWriter);
            final XmlPrettyPrintWriter xmlPrettyWriter = new XmlPrettyPrintWriter(cdataStreamWriter);
            writer.marshal(jaxbRoot, xmlPrettyWriter);
        }
        catch (final JAXBException e)
        {
            LOGGER.error("Saving failed", e);
            throw new IOException(e);
        }
        catch (final XMLStreamException ex)
        {
            LOGGER.error("Saving failed", ex);
            throw new IOException(ex);
        }
    }
}