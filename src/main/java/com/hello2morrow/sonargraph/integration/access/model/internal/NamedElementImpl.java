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

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class NamedElementImpl extends ElementWithDescriptionImpl implements INamedElement
{
    private final String m_kind;
    private final String m_presentationKind;
    private final int m_line;
    private final String m_fqName;

    public NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final int line, final String description)
    {
        super(name, presentationName, description);

        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'NamedElementImpl' must not be empty";

        m_kind = kind;
        m_presentationKind = presentationKind;
        m_line = line;
        m_fqName = fqName;
    }

    public NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final int line)
    {
        this(kind, presentationKind, name, presentationName, fqName, -1, "");
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getKind()
     */
    @Override
    public final String getKind()
    {
        return m_kind;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getFqName()
     */
    @Override
    public final String getFqName()
    {
        return m_fqName;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getPresentationKind()
     */
    @Override
    public final String getPresentationKind()
    {
        return m_presentationKind;
    }

    @Override
    public final int getLineNumber()
    {
        return m_line;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + m_fqName.hashCode();
        result = prime * result + ((m_kind == null) ? 0 : m_kind.hashCode());
        result = prime * result + m_line;
        result = prime * result + ((m_presentationKind == null) ? 0 : m_presentationKind.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if(obj == null)
        {
        	return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final NamedElementImpl other = (NamedElementImpl) obj;
        if (m_fqName == null)
        {
            if (other.m_fqName != null)
            {
                return false;
            }
        }
        else if (!m_fqName.equals(other.m_fqName))
        {
            return false;
        }
        if (m_kind == null)
        {
            if (other.m_kind != null)
            {
                return false;
            }
        }
        else if (!m_kind.equals(other.m_kind))
        {
            return false;
        }
        if (m_line != other.m_line)
        {
            return false;
        }
        if (m_presentationKind == null)
        {
            if (other.m_presentationKind != null)
            {
                return false;
            }
        }
        else if (!m_presentationKind.equals(other.m_presentationKind))
        {
            return false;
        }
        return true;
    }
}