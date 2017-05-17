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

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.INamedElement#getName()
     */
    @Override
    public final String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.INamedElement#getPresentationName()
     */
    @Override
    public String getPresentationName()
    {
        return presentationName;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((presentationName == null) ? 0 : presentationName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ElementImpl other = (ElementImpl) obj;

        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (presentationName == null)
        {
            if (other.presentationName != null)
            {
                return false;
            }
        }
        else if (!presentationName.equals(other.presentationName))
        {
            return false;
        }
        return true;
    }
}