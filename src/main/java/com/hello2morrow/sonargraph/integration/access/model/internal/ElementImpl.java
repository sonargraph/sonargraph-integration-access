package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IElement;

public abstract class ElementImpl implements IElement
{
    private final String m_name;
    private final String m_presentationName;

    public ElementImpl(final String name, final String presentationName)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'Element' must not be empty";
        assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'Element' must not be empty";

        m_name = name;
        m_presentationName = presentationName;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.INamedElement#getName()
     */
    @Override
    public final String getName()
    {
        return m_name;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.INamedElement#getPresentationName()
     */
    @Override
    public String getPresentationName()
    {
        return m_presentationName;
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
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        result = prime * result + ((m_presentationName == null) ? 0 : m_presentationName.hashCode());
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

        if (m_name == null)
        {
            if (other.m_name != null)
            {
                return false;
            }
        }
        else if (!m_name.equals(other.m_name))
        {
            return false;
        }
        if (m_presentationName == null)
        {
            if (other.m_presentationName != null)
            {
                return false;
            }
        }
        else if (!m_presentationName.equals(other.m_presentationName))
        {
            return false;
        }
        return true;
    }
}