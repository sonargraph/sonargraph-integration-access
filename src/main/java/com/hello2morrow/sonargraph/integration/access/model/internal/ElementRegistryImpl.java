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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

/**
 * Global map of elements of a Software system.
 */
final class ElementRegistryImpl implements Serializable
{
    private static final long serialVersionUID = -947650961500421692L;
    private final Map<String, INamedElement> fqNameToElementMap = new HashMap<>();

    ElementRegistryImpl()
    {
        super();
    }

    void addElement(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'addElement' must not be null";
        assert !fqNameToElementMap.containsKey(namedElement.getFqName()) : "namedElement '" + namedElement.getFqName() + "' has already been added";
        fqNameToElementMap.put(namedElement.getFqName(), namedElement);
    }

    Optional<INamedElement> getElement(final String fqName)
    {
        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'getElement' must not be empty";
        return fqNameToElementMap.containsKey(fqName) ? Optional.of(fqNameToElementMap.get(fqName)) : Optional.empty();
    }
}