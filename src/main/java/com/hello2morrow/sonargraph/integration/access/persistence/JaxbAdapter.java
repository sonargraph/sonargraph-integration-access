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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JaxbAdapter<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbAdapter.class);

    @FunctionalInterface
    private interface JAXBContextCreator
    {
        JAXBContext get() throws JAXBException;
    }

    private final Unmarshaller m_reader;

    private JaxbAdapter(final JAXBContextCreator creator, final URL... schemaUrl) throws Exception
    {
        assert schemaUrl != null : "Parameter 'schemaUrl' of method 'JaxbAdapter' must not be null";

        final Source[] sources = new Source[schemaUrl.length];
        for (int i = 0; i < schemaUrl.length; i++)
        {
            sources[i] = new StreamSource(schemaUrl[i].openStream());
        }

        final Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources);
        final JAXBContext jaxbContextProject = creator.get();

        m_reader = createReader(jaxbContextProject, schema);
    }

    public JaxbAdapter(final Class<T> jaxbClass, final URL... schemaUrl) throws Exception
    {
        this(() -> JAXBContext.newInstance(jaxbClass), schemaUrl);
    }

    public JaxbAdapter(final String namespace, final URL... schemaUrl) throws Exception
    {
        this(() -> JAXBContext.newInstance(namespace, JaxbAdapter.class.getClassLoader()), schemaUrl);
    }

    private Unmarshaller createReader(final JAXBContext jaxbContext, final Schema schema) throws JAXBException
    {
        final Unmarshaller reader = jaxbContext.createUnmarshaller();
        reader.setSchema(schema);
        return reader;
    }

    @SuppressWarnings("unchecked")
    public final Optional<T> load(final InputStream from, final ValidationEventHandler validationHandler) throws Exception
    {
        assert from != null : "'from' must not be null";
        assert validationHandler != null : "'validationHandler' must not be null";

        try (BufferedInputStream bufferedIn = new BufferedInputStream(from))
        {
            m_reader.setEventHandler(validationHandler);
            final T jaxbRoot = (T) m_reader.unmarshal(bufferedIn);
            return Optional.of(jaxbRoot);
        }
        catch (final Exception e)
        {
            LOGGER.error("Failed to load xml file", e);
            return Optional.empty();
        }
    }
}