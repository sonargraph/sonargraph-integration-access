/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.AggregatingClassLoader;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;

public final class JaxbAdapter<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbAdapter.class);
    private static final String INITIALIZATION_FAILED = "Initialization failed";
    private static final String UTF8_ENCODING = "UTF-8";
    private final Unmarshaller reader;
    private final Marshaller writer;

    private static final AtomicBoolean s_hasJaxbImplementationBeenLogged = new AtomicBoolean(false);

    /**
     * Creates a JaxbAdapter - a writer and a reader (reader without XSD validation)
     *
     * @param namespace the namespace - must not be 'empty'
     * @param classLoader the class loader - must not be 'null'
     */
    public JaxbAdapter(final String namespace, ClassLoader classLoader)
    {
        assert namespace != null && namespace.length() > 0 : "Parameter 'namespace' of method 'JaxbAdapter' must not be empty";
        assert classLoader != null : "Parameter 'classLoader' of method 'JaxbAdapter' must not be null";

        if (!(classLoader instanceof AggregatingClassLoader))
        {
            // We must use this class loader here to enforce the use of a specific JaxB implementation
            classLoader = new AggregatingClassLoader(classLoader);
        }

        Marshaller createdWriter;
        Unmarshaller createdReader;

        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(namespace, classLoader);
            logJaxbImplementation(jaxbContext);
            verifyJaxbImplementation(jaxbContext);

            createdWriter = jaxbContext.createMarshaller();
            createdWriter.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            createdWriter.setProperty(Marshaller.JAXB_ENCODING, UTF8_ENCODING);
            createdReader = jaxbContext.createUnmarshaller();
        }
        catch (final Exception ex)
        {
            LOGGER.error(INITIALIZATION_FAILED, ex);
            createdWriter = null;
            createdReader = null;
            assert false : INITIALIZATION_FAILED + ": " + Utility.collectAll(ex);
        }

        this.writer = createdWriter;
        this.reader = createdReader;
    }

    /**
     * Creates a JaxbAdapter - a writer and a reader (reader with XSD validation)
     *
     * @param persistenceContext the persistent context - must not be 'null'
     * @param classLoader the class loader - must not be 'null'
     */
    public JaxbAdapter(final XmlPersistenceContext persistenceContext, ClassLoader classLoader)
    {
        assert persistenceContext != null : "Parameter 'persistenceContext' of method 'JaxbAdapter' must not be null";
        assert classLoader != null : "Parameter 'classLoader' of method 'JaxbAdapter' must not be null";

        if (!(classLoader instanceof AggregatingClassLoader))
        {
            // We must use this class loader here to enforce the use of a specific JaxB implementation
            classLoader = new AggregatingClassLoader(classLoader);
        }

        Marshaller createdWriter;
        Unmarshaller createdReader;

        try
        {
            final String namespaces = persistenceContext.getNamespaceList();
            final JAXBContext jaxbContext = JAXBContext.newInstance(namespaces, classLoader);
            logJaxbImplementation(jaxbContext);
            verifyJaxbImplementation(jaxbContext);

            createdWriter = jaxbContext.createMarshaller();
            createdWriter.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            createdWriter.setProperty(Marshaller.JAXB_ENCODING, UTF8_ENCODING);

            createdReader = jaxbContext.createUnmarshaller();
            final Set<URL> schemaUrls = persistenceContext.getSchemaUrls();
            final Source[] sources = new Source[schemaUrls.size()];
            int i = 0;
            for (final URL nextSchemaUrl : schemaUrls)
            {
                assert nextSchemaUrl != null : " 'nextSchemaUrl' of method 'getSchema' must not be null";
                sources[i] = new StreamSource(nextSchemaUrl.openStream());
                i++;
            }
            createdReader.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources));

            //createdReader.setProperty(UnmarshallerProperties.ID_RESOLVER, new XmlIdResolver());
        }
        catch (final Exception e)
        {
            LOGGER.error(INITIALIZATION_FAILED, e);
            createdReader = null;
            createdWriter = null;
            assert false : INITIALIZATION_FAILED + ": " + Utility.collectAll(e);
        }

        this.reader = createdReader;
        this.writer = createdWriter;
    }

    private void logJaxbImplementation(final JAXBContext jaxbContext)
    {
        if (!s_hasJaxbImplementationBeenLogged.getAndSet(true))
        {
            LOGGER.info("Using JAXBContext implementation: {}", jaxbContext.getClass().getName());
        }
    }

    /** Since JAXB is no longer contained from Java11 onwards, we supply a different implementation and check here that it is used. */
    private void verifyJaxbImplementation(final JAXBContext jaxbContext) throws AssertionError
    {
        if (!jaxbContext.getClass().getName().equals("com.sun.xml.bind.v2.runtime.JAXBContextImpl"))
        {
            throw new AssertionError("Current JAXBContext implementation '" + jaxbContext.getClass().getName()
                    + " does not match the expected implementation " + "com.sun.xml.bind.v2.runtime.JAXBContextImpl\n"
                    + "\tCheck if the file META-INF/services/javax.xml.bind.JAXBContext exists and contains the correct entry.\n"
                    + "\tIf used in an OSGi environment, check that the correct classloader is used.\n"
                    + "\tCheck if a jaxb.properties file is located in the packages that contain the JAXB classes.\n"
                    + "\tAlternatively set the System property 'javax.xml.bind.context.factory=com.sun.xml.bind.v2.runtime.JAXBContextImpl'");
        }
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
            final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            //Disable escaping, since we want to generate <CDATA[[...]]> sections based on the detection of special characters
            xmlOutputFactory.setProperty("escapeCharacters", false);

            //Using two decorators to correctly handle CDATA sections and output formatting.
            //The output formatting property set on the JAXB marshaller is not respected when using XMLStreamWriters.
            final XMLStreamWriter streamWriter = xmlOutputFactory.createXMLStreamWriter(bufferedOut, UTF8_ENCODING);

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