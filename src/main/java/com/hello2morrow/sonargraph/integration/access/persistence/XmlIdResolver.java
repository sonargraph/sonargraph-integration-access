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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * This class was necessary to work around a bug in the EclipseLink Moxy implementation which was not capable of resolving IDREF properly.
 */
final class XmlIdResolver extends org.eclipse.persistence.jaxb.IDResolver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlIdResolver.class);
    private final Map<Object, Object> m_idToObjectCache = new HashMap<>();

    public XmlIdResolver()
    {
        //no bike shedding: It is useful for debugging
        super();
    }

    @Override
    public Callable<?> resolve(final Object id, @SuppressWarnings("rawtypes") final Class type) throws SAXException
    {
        if (id == null)
        {
            return null;
        }
        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("resolving id {} of type {}", id, type);
        }
        final Object instance = m_idToObjectCache.get(id);
        if (instance == null)
        {
            throw new SAXException("Failed to resolve id " + id);
        }

        return (Callable<?>) () -> instance;
    }

    @Override
    public Callable<?> resolve(final Map<String, Object> id, @SuppressWarnings("rawtypes") final Class type) throws SAXException
    {
        throw new UnsupportedOperationException("Multiple ids not supported for type " + type);
    }

    @Override
    public void bind(final Object id, final Object obj) throws SAXException
    {
        if (id != null)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("binding id {} to obj {}", id, obj);
            }
            final Object alreadyPresent = m_idToObjectCache.put(id, obj);
            assert alreadyPresent == null : "Key " + id + " was already bound to " + alreadyPresent + " and cannot be bound to " + obj;
        }
    }

    @Override
    public void bind(final Map<String, Object> id, final Object obj) throws SAXException
    {
        throw new UnsupportedOperationException("Multiple ids not supported for object " + obj);
    }
}
