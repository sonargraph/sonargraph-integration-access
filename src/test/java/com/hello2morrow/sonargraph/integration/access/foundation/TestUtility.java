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
package com.hello2morrow.sonargraph.integration.access.foundation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class TestUtility
{
    private TestUtility()
    {
        super();
    }

    public static Map<String, INamedElement> getFqNameToNamedElement(final IModule module, final String elementKind)
    {
        assert module != null : "Parameter 'module' of method 'getFqNameToNamedElement' must not be null";
        assert elementKind != null : "Parameter 'elementKind' of method 'getFqNameToNamedElement' must not be null";

        final Set<INamedElement> namedElements = module.getElements(elementKind);
        if (namedElements == null)
        {
            return Collections.emptyMap();
        }

        final Map<String, INamedElement> fqNameToNamedElement = new HashMap<>();
        for (final INamedElement nextNamedElement : namedElements)
        {
            final INamedElement previous = fqNameToNamedElement.put(nextNamedElement.getFqName(), nextNamedElement);
            assert previous == null : "Element with fq name already added: " + nextNamedElement.getFqName();
        }
        return fqNameToNamedElement;
    }
}