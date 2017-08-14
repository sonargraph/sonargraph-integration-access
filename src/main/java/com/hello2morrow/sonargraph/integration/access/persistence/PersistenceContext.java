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

import java.net.URL;
import java.util.Collections;
import java.util.List;

public final class PersistenceContext
{
    private final List<URL> m_schemaUrls;
    private final List<String> m_namespaces;

    public PersistenceContext(final List<URL> urls, final List<String> namespaces)
    {
        assert urls != null && !urls.isEmpty() : "Parameter 'urls' of method 'PersistenceContext' must not be empty";
        assert namespaces != null && !namespaces.isEmpty() : "Parameter 'namespaces' of method 'PersistenceContext' must not be empty";
        m_schemaUrls = urls;
        m_namespaces = namespaces;
    }

    public List<URL> getSchemaUrls()
    {
        return Collections.unmodifiableList(m_schemaUrls);
    }

    public List<String> getNamespaces()
    {
        return Collections.unmodifiableList(m_namespaces);
    }
}