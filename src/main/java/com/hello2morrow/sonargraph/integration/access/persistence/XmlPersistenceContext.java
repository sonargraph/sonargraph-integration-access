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

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class XmlPersistenceContext
{
    private final Set<String> namespaces = new LinkedHashSet<>();
    private final Set<URL> schemaUrls = new LinkedHashSet<>();

    public XmlPersistenceContext(final String namespace, final URL schemaUrl)
    {
        assert namespace != null && namespace.length() > 0 : "Parameter 'namespace' of method 'XmlPersistenceContext' must not be empty";
        assert schemaUrl != null : "Parameter 'schemaUrl' of method 'XmlPersistenceContext' must not be null";
        namespaces.add(namespace);
        schemaUrls.add(schemaUrl);
    }

    public XmlPersistenceContext(final XmlPersistenceContext persistenceContext)
    {
        assert persistenceContext != null : "Parameter 'persistenceContext' of method 'PersistenceContext' must not be null";
        namespaces.addAll(persistenceContext.getNamespaces());
        schemaUrls.addAll(persistenceContext.getSchemaUrls());
    }

    public void add(final String namespace, final URL schemaUrl)
    {
        assert namespace != null && namespace.length() > 0 : "Parameter 'namespace' of method 'add' must not be empty";
        assert schemaUrl != null : "Parameter 'schemaUrl' of method 'add' must not be null";
        namespaces.add(namespace);
        schemaUrls.add(schemaUrl);
    }

    public void add(final XmlPersistenceContext persistenceContext)
    {
        assert persistenceContext != null : "Parameter 'persistenceContext' of method 'add' must not be null";
        namespaces.addAll(persistenceContext.getNamespaces());
        schemaUrls.addAll(persistenceContext.getSchemaUrls());
    }

    public Set<String> getNamespaces()
    {
        return Collections.unmodifiableSet(namespaces);
    }

    public Set<URL> getSchemaUrls()
    {
        return Collections.unmodifiableSet(schemaUrls);
    }

    public String getNamespaceList()
    {
        if (namespaces.isEmpty())
        {
            return "";
        }

        final StringBuilder contextPath = new StringBuilder();
        for (final String nextNamespace : namespaces)
        {
            contextPath.append(nextNamespace);
            contextPath.append(":");
        }
        contextPath.deleteCharAt(contextPath.length() - 1);
        return contextPath.toString();
    }
}