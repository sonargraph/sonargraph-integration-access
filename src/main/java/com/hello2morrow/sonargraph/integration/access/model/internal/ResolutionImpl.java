package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class ResolutionImpl extends ElementImpl implements IResolution
{
    private final List<IIssue> m_issues;
    private final Priority m_priority;
    private final ResolutionType m_type;
    private final boolean m_isApplicable;
    private final int m_numberOfAffectedParserDependencies;

    //TODO: PresentationName?
    public ResolutionImpl(final String fqName, final ResolutionType type, final Priority priority, final List<IIssue> issues,
            final boolean isApplicable, final int numberOfAffectedParserDependencies)
    {
        super(fqName, type.name());

        m_type = type;
        m_priority = priority;
        m_issues = issues;
        m_isApplicable = isApplicable;
        m_numberOfAffectedParserDependencies = numberOfAffectedParserDependencies;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getIssues()
     */
    @Override
    public List<IIssue> getIssues()
    {
        return m_issues;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getPriority()
     */
    @Override
    public Priority getPriority()
    {
        return m_priority;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getType()
     */
    @Override
    public ResolutionType getType()
    {
        return m_type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_issues == null) ? 0 : m_issues.hashCode());
        result = prime * result + ((m_priority == null) ? 0 : m_priority.hashCode());
        result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
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
        final ResolutionImpl other = (ResolutionImpl) obj;
        if (m_issues == null)
        {
            if (other.m_issues != null)
            {
                return false;
            }
        }
        else if (!m_issues.equals(other.m_issues))
        {
            return false;
        }
        if (m_priority == null)
        {
            if (other.m_priority != null)
            {
                return false;
            }
        }
        else if (!m_priority.equals(other.m_priority))
        {
            return false;
        }
        if (m_type == null)
        {
            if (other.m_type != null)
            {
                return false;
            }
        }
        else if (!m_type.equals(other.m_type))
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean isApplicable()
    {
        return m_isApplicable;
    }

    @Override
    public boolean isTask()
    {
        return m_type != ResolutionType.IGNORE;
    }

    @Override
    public int getNumberOfAffectedParserDependencies()
    {
        return m_numberOfAffectedParserDependencies;
    }
}
