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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IElementWithDescription;

public abstract class ElementWithDescriptionImpl extends ElementImpl implements IElementWithDescription
{
    private static final long serialVersionUID = -6970888192952043004L;
    private final String description;

    public ElementWithDescriptionImpl(final String name, final String presentationName, final String description)
    {
        super(name, presentationName);
        assert description != null : "Parameter 'description' of method 'NamedElement' must not be null";
        this.description = description;
    }

    @Override
    public final String getDescription()
    {
        return description;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + description.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }

        final ElementWithDescriptionImpl other = (ElementWithDescriptionImpl) obj;
        return description.equals(other.description);
    }
}