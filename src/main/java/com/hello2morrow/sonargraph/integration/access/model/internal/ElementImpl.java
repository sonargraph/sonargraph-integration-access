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

import com.hello2morrow.sonargraph.integration.access.model.IElement;

public abstract class ElementImpl implements IElement
{
    private static final long serialVersionUID = 8912026050040232705L;
    private final String name;
    private final String presentationName;

    public ElementImpl(final String name, final String presentationName)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'Element' must not be empty";
        assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'Element' must not be empty";
        this.name = name;
        this.presentationName = presentationName;
    }

    @Override
    public final String getName()
    {
        return name;
    }

    @Override
    public final String getPresentationName()
    {
        return presentationName;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        final ElementImpl other = (ElementImpl) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString()
    {
        return "[" + getClass().getName() + "]\nname:" + name + "\npresentation name:" + presentationName;
    }
}