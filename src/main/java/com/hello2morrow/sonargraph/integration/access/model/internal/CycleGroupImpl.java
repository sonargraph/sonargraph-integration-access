package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class CycleGroupImpl extends AbstractElementIssueImpl implements ICycleGroup
{
    private final IAnalyzer m_analyzer;
    private final List<INamedElement> m_cyclicElements = new ArrayList<>();

    public CycleGroupImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final boolean hasResolution, final IAnalyzer analyzer)
    {
        super(name, presentationName, description, issueType, provider, hasResolution, -1);
        assert analyzer != null : "Parameter 'analyzer' of method 'CycleGroup' must not be null";

        m_analyzer = analyzer;
    }

    public void setAffectedElements(final List<INamedElement> elements)
    {
        assert elements != null : "Parameter 'elements' of method 'setCyclicElements' must not be null";
        assert !elements.isEmpty() : "Parameter 'elements' of method 'setCyclicElements' must not be empty";
        m_cyclicElements.addAll(elements);
    }

    @Override
    public List<INamedElement> getAffectedElements()
    {
        return Collections.unmodifiableList(m_cyclicElements);
    }

    @Override
    public List<INamedElement> getOrigins()
    {
        return getAffectedElements();
    }

    @Override
    public IAnalyzer getAnalyzer()
    {
        return m_analyzer;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_analyzer == null) ? 0 : m_analyzer.hashCode());
        result = prime * result + ((m_cyclicElements == null) ? 0 : m_cyclicElements.hashCode());
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
        final CycleGroupImpl other = (CycleGroupImpl) obj;
        if (m_analyzer == null)
        {
            if (other.m_analyzer != null)
            {
                return false;
            }
        }
        else if (!m_analyzer.equals(other.m_analyzer))
        {
            return false;
        }
        if (m_cyclicElements == null)
        {
            if (other.m_cyclicElements != null)
            {
                return false;
            }
        }
        else if (!m_cyclicElements.equals(other.m_cyclicElements))
        {
            return false;
        }
        return true;
    }
}
