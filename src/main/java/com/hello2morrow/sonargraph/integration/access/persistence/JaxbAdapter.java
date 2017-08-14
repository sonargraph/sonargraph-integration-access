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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
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
import com.hello2morrow.sonargraph.integration.access.foundation.FileUtility;

final class JaxbAdapter<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbAdapter.class);
    private final Unmarshaller reader;
    private final Marshaller writer;
    private Listener marshallListener;

    private static Schema getSchema(final URL... schemaUrls) throws IOException, SAXException
    {
        assert schemaUrls != null : "Parameter 'schemaUrls' of method 'getSchema' must not be null";

        if (schemaUrls.length > 0)
        {
            final Source[] sources = new Source[schemaUrls.length];
            for (int i = 0; i < schemaUrls.length; i++)
            {
                sources[i] = new StreamSource(schemaUrls[i].openStream());
            }
            return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources);
        }
        return null;
    }

    public JaxbAdapter(final String namespace, final URL... schemaUrls)
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
            LOGGER.error("Failed to initialize", ex);
            reader = null;
            writer = null;
            assert false : "Failed to initialize: " + ExceptionUtility.collectAll(ex);
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

    private static Marshaller createWriter(final JAXBContext jaxbContext) throws JAXBException, PropertyException
    {
        final Marshaller writer = jaxbContext.createMarshaller();
        writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        writer.setProperty(Marshaller.JAXB_ENCODING, FileUtility.UTF8_ENCODING);
        return writer;
    }

    public void save(final T jaxbRoot, final File toFile) throws IOException
    {
        assert jaxbRoot != null : "'jaxbRoot' must not be null";
        assert toFile != null : "'toFile' must not be null";
        assert !toFile.exists() || toFile.isFile() : "'toFile' must be an existing file:" + toFile;

        int i = 1;
        File tmpFile = toFile;
        while (tmpFile.exists())
        {
            tmpFile = new File(toFile.getAbsolutePath() + "." + i);
            ++i;
        }

        try (OutputStream out = new FileOutputStream(tmpFile))
        {
            save(jaxbRoot, out);
        }
        finally
        {
            if (tmpFile.exists() && !tmpFile.equals(toFile))
            {
                Files.move(tmpFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public void setMarshalListener(final Marshaller.Listener listener)
    {
        assert listener != null : "Parameter 'listener' of method 'setMarshallListener' must not be null";
        marshallListener = listener;
    }

    public void save(final T jaxbRoot, final OutputStream out) throws IOException
    {
        assert jaxbRoot != null : "Parameter 'jaxbRoot' of method 'save' must not be null";
        assert out != null : "Parameter 'out' of method 'save' must not be null";
        try (OutputStream bufferedOut = new BufferedOutputStream(out))
        {
            //Using two decorators to correctly handle CDATA sections and output formatting.
            //The output formatting property set on the JAXB marshaller is not respected when using XMLStreamWriters.
            final XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(bufferedOut, FileUtility.UTF8_ENCODING);
            final XmlCDataStreamWriter cdataStreamWriter = new XmlCDataStreamWriter(streamWriter);
            final XmlPrettyPrintWriter xmlPrettyWriter = new XmlPrettyPrintWriter(cdataStreamWriter);
            writer.setListener(marshallListener);
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