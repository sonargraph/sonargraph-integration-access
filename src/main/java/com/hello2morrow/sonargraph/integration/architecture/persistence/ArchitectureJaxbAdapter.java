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

package com.hello2morrow.sonargraph.integration.architecture.persistence;

import com.hello2morrow.sonargraph.integration.access.persistence.JaxbAdapter;
import com.hello2morrow.sonargraph.integration.access.persistence.XmlPersistenceContext;

public final class ArchitectureJaxbAdapter
{
    private static final String ARCHITECTURE_NAMESPACE = "com.hello2morrow.sonargraph.integration.architecture.persistence";
    private static final String ARCHITECTURE_XSD = "com/hello2morrow/sonargraph/integration/architecture/persistence/architecture.xsd";

    private ArchitectureJaxbAdapter()
    {
        super();
    }

    public static JaxbAdapter<Architecture> create()
    {
        final ClassLoader classLoader = ObjectFactory.class.getClassLoader();
        return new JaxbAdapter<>(new XmlPersistenceContext(ARCHITECTURE_NAMESPACE, classLoader.getResource(ARCHITECTURE_XSD)), classLoader);
    }
}