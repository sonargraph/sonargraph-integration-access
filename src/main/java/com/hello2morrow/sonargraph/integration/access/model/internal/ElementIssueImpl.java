package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class ElementIssueImpl extends AbstractElementIssueImpl
{
    private final INamedElement m_element;
    private int m_line = -1;
    private int m_column = -1;

    public ElementIssueImpl(final IIssueType issueType, final String description, final IIssueProvider issueProvider, final INamedElement element,
            final boolean hasResolution, final int line)
    {
        super(issueType.getName() + element.toString() + line, issueType.getPresentationName(), description, issueType, issueProvider, hasResolution,
                line);
        assert element != null : "Parameter 'element' of method 'ElementIssue' must not be null";
        m_element = element;
    }

    public IElement getElement()
    {
        return m_element;
    }

    public int getLine()
    {
        return m_line;
    }

    public void setLine(final int line)
    {
        assert line > 0 : "Parameter 'line' of method setLine() must be > 0";
        m_line = line;
    }

    public int getColumn()
    {
        return m_column;
    }

    public void setColumn(final int column)
    {
        assert column > 0 : "Parameter 'column' of method setColumn() must be > 0";
        m_column = column;
    }

    @Override
    public List<INamedElement> getAffectedElements()
    {
        return Collections.unmodifiableList(Arrays.asList(m_element));
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + m_column;
        result = prime * result + ((m_element == null) ? 0 : m_element.hashCode());
        result = prime * result + m_line;
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
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ElementIssueImpl other = (ElementIssueImpl) obj;
        if (m_column != other.m_column)
        {
            return false;
        }
        if (m_element == null)
        {
            if (other.m_element != null)
            {
                return false;
            }
        }
        else if (!m_element.equals(other.m_element))
        {
            return false;
        }
        if (m_line != other.m_line)
        {
            return false;
        }
        return true;
    }

}
