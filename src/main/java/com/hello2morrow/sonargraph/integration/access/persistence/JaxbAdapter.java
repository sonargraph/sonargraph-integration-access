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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final AtomicBoolean JAXB_IMPLEMENTATION_VERIFIED = new AtomicBoolean(false);
    private final Unmarshaller m_reader;
    private final Marshaller m_writer;

    private static String getInfo(final String info, final String namespaceList, final AggregatingClassLoader classLoader, final Exception exception)
    {
        assert info != null && info.length() > 0 : "Parameter 'info' of method 'getInfo' must not be empty";
        assert namespaceList != null && namespaceList.length() > 0 : "Parameter 'namespaceList' of method 'getInfo' must not be empty";
        assert classLoader != null : "Parameter 'classLoader' of method 'getInfo' must not be null";
        assert exception != null : "Parameter 'exception' of method 'getInfo' must not be null";

        final StringBuilder builder = new StringBuilder();
        builder.append("[").append(info).append("] ");
        builder.append("Namepaces: ").append(namespaceList);

        builder.append(", Classloaders:");
        for (final ClassLoader nextClassLoader : classLoader.getClassLoaders())
        {
            builder.append(" <").append(nextClassLoader.getClass().getSimpleName());
            final String nextName = nextClassLoader.getName();
            if (nextName != null && !nextName.isEmpty())
            {
                builder.append(" (").append(nextName).append(")");
            }
            builder.append(">");
        }

        builder.append("\n").append(Utility.collectAll(exception));
        return builder.toString();
    }

    private void verifyJaxbImplementation(final JAXBContext jaxbContext)
    {
        assert jaxbContext != null : "Parameter 'jaxbContext' of method 'verifyJaxbImplementation' must not be null";

        if (!JAXB_IMPLEMENTATION_VERIFIED.getAndSet(true))
        {
            LOGGER.debug("Using JAXBContext implementation: {}", jaxbContext.getClass().getName());

            //Since JAXB is no longer contained from Java11 onwards, we supply a different implementation and check here that it is used.
            if (!jaxbContext.getClass().getName().equals("com.sun.xml.bind.v2.runtime.JAXBContextImpl"))
            {
                throw new RuntimeException("Current JAXBContext implementation '" + jaxbContext.getClass().getName()
                        + " does not match the expected implementation " + "com.sun.xml.bind.v2.runtime.JAXBContextImpl\n"
                        + "\tCheck if the file META-INF/services/javax.xml.bind.JAXBContext exists and contains the correct entry.\n"
                        + "\tIf used in an OSGi environment, check that the correct classloader is used.\n"
                        + "\tCheck if a jaxb.properties file is located in the packages that contain the JAXB classes.\n"
                        + "\tAlternatively set the System property 'javax.xml.bind.context.factory=com.sun.xml.bind.v2.ContextFactory'");
            }
        }
    }

    /**
     * Creates a JaxbAdapter - a writer and a reader (reader without XSD validation)
     *
     * @param namespace the namespace - must not be 'empty'
     * @param classLoader the class loader - must not be 'null'
     */
    public JaxbAdapter(final String namespace, final ClassLoader classLoader, final String defaultNamespaceRemap)
    {
        assert namespace != null && namespace.length() > 0 : "Parameter 'namespace' of method 'JaxbAdapter' must not be empty";
        assert classLoader != null : "Parameter 'classLoader' of method 'JaxbAdapter' must not be null";
        //defaultNamespaceRemap might be null.

        final AggregatingClassLoader aggregatingClassLoader;
        if (classLoader instanceof AggregatingClassLoader)
        {
            aggregatingClassLoader = (AggregatingClassLoader) classLoader;
        }
        else
        {
            // We must use this class loader here to enforce the use of a specific JaxB implementation
            aggregatingClassLoader = new AggregatingClassLoader(classLoader);
        }

        Marshaller createdWriter;
        Unmarshaller createdReader;

        try
        {
            /*
             * For properties and debugging, check com.sun.xml.bind.api.JAXBRIContext and com.sun.xml.bind.v2.runtime.JAXBContextImpl of jaxb-impl.
             */
            final Map<String, Object> properties = new HashMap<>();
            if (defaultNamespaceRemap != null)
            {
                //This property is required, so that subtypes like XsdQualityGate as XsdSystemFileElement get correctly resolved.
                //In some runtimes (e.g. SonarQube Maven Scanner) the lookup of the subtype failed in JAXBContext, where it was
                //registered with the localPart (xsdQualityGate) but looked up with namespaceURI+localPart ({http://www.hello2morrow.com/sonargraph/core/report}xsdQualityGate).
                //Using this property, all elements are registered and looked up with namespaceURI+localPart.
                properties.put("com.sun.xml.bind.defaultNamespaceRemap", defaultNamespaceRemap);
            }
            final JAXBContext jaxbContext = JAXBContext.newInstance(namespace, aggregatingClassLoader, properties);
            verifyJaxbImplementation(jaxbContext);

            createdWriter = jaxbContext.createMarshaller();
            createdWriter.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            createdWriter.setProperty(Marshaller.JAXB_ENCODING, UTF8_ENCODING);
            createdReader = jaxbContext.createUnmarshaller();
        }
        catch (final Exception ex)
        {
            createdWriter = null;
            createdReader = null;

            final String info = getInfo(INITIALIZATION_FAILED, namespace, aggregatingClassLoader, ex);
            LOGGER.error(info);
            assert false : info;

            throw new RuntimeException(INITIALIZATION_FAILED, ex);
        }

        m_writer = createdWriter;
        m_reader = createdReader;
    }

    /**
     * Creates a JaxbAdapter - a writer and a reader (reader without XSD validation)
     *
     * @param classLoader the class loader - must not be 'null'
     */
    public JaxbAdapter(final AggregatingClassLoader aggregatingClassLoader, final String namespaces)
    {
        assert aggregatingClassLoader != null : "Parameter 'aggregatingClassLoader' of method 'JaxbAdapter' must not be null";
        assert namespaces != null && namespaces.length() > 0 : "Parameter 'namespaces' of method 'JaxbAdapter' must not be empty";

        Marshaller createdWriter;
        Unmarshaller createdReader;

        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(namespaces, aggregatingClassLoader);
            verifyJaxbImplementation(jaxbContext);

            createdWriter = jaxbContext.createMarshaller();
            createdWriter.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            createdWriter.setProperty(Marshaller.JAXB_ENCODING, UTF8_ENCODING);
            createdReader = jaxbContext.createUnmarshaller();
        }
        catch (final Exception e)
        {
            createdReader = null;
            createdWriter = null;

            final String info = getInfo(INITIALIZATION_FAILED, namespaces, aggregatingClassLoader, e);
            LOGGER.error(info);
            assert false : info;

            throw new RuntimeException(INITIALIZATION_FAILED, e);
        }

        m_reader = createdReader;
        m_writer = createdWriter;
    }

    /**
     * Creates a JaxbAdapter - a writer and a reader (reader with XSD validation)
     *
     * @param persistenceContext the persistent context - must not be 'null'
     * @param classLoader the class loader - must not be 'null'
     */
    public JaxbAdapter(final XmlPersistenceContext persistenceContext, final ClassLoader classLoader)
    {
        assert persistenceContext != null : "Parameter 'persistenceContext' of method 'JaxbAdapter' must not be null";
        assert classLoader != null : "Parameter 'classLoader' of method 'JaxbAdapter' must not be null";

        final AggregatingClassLoader aggregatingClassLoader;
        if (classLoader instanceof AggregatingClassLoader)
        {
            aggregatingClassLoader = (AggregatingClassLoader) classLoader;
        }
        else
        {
            // We must use this class loader here to enforce the use of a specific JaxB implementation
            aggregatingClassLoader = new AggregatingClassLoader(classLoader);
        }

        Marshaller createdWriter;
        Unmarshaller createdReader;

        final String namespaces = persistenceContext.getNamespaceList();
        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(namespaces, aggregatingClassLoader);
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
            createdReader.setSchema(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(sources));
        }
        catch (final Exception e)
        {
            createdReader = null;
            createdWriter = null;

            final String info = getInfo(INITIALIZATION_FAILED, namespaces, aggregatingClassLoader, e);
            LOGGER.error(info);
            assert false : info;

            throw new RuntimeException(INITIALIZATION_FAILED, e);
        }

        m_reader = createdReader;
        m_writer = createdWriter;
    }

    public void setMarshalListener(final Marshaller.Listener listener)
    {
        m_writer.setListener(listener);
    }

    @SuppressWarnings("unchecked")
    public T load(final InputStream from, final ValidationEventHandler validationHandler)
    {
        assert from != null : "'from' must not be null";

        try (BufferedInputStream bufferedIn = new BufferedInputStream(from))
        {
            if (validationHandler != null)
            {
                m_reader.setEventHandler(validationHandler);
            }
            return (T) m_reader.unmarshal(bufferedIn);
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
            final XMLStreamWriter streamWriter = xmlOutputFactory.createXMLStreamWriter(bufferedOut, UTF8_ENCODING);
            //Using two decorators to correctly handle CDATA sections and output formatting.
            //The output formatting property set on the JAXB marshaller is not respected when using XMLStreamWriters.
            final XmlCDataStreamWriter cdataStreamWriter = new XmlCDataStreamWriter(streamWriter);
            final XmlPrettyPrintWriter xmlPrettyWriter = new XmlPrettyPrintWriter(cdataStreamWriter);
            m_writer.marshal(jaxbRoot, xmlPrettyWriter);
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