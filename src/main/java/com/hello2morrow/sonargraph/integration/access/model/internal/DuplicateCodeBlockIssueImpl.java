package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class DuplicateCodeBlockIssueImpl extends AbstractElementIssueImpl implements IDuplicateCodeBlockIssue
{
    private final String m_presentationName;
    private int m_blockSize;
    private final List<IDuplicateCodeBlockOccurrence> m_occurrences;

    public DuplicateCodeBlockIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final boolean hasResolution, final List<IDuplicateCodeBlockOccurrence> occurrences)
    {
        super(name, presentationName, description, issueType, provider, hasResolution, -1);
        assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'DuplicateCodeBlockIssue' must not be empty";
        m_presentationName = presentationName;
        m_occurrences = occurrences;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue#getPresentationName()
     */
    @Override
    public String getPresentationName()
    {
        return m_presentationName;
    }

    @Override
    public List<INamedElement> getAffectedElements()
    {
        return Collections.unmodifiableList(m_occurrences.stream().map(o -> o.getSourceFile()).collect(Collectors.toList()));
    }

    public void setBlockSize(final int blockSize)
    {
        assert blockSize > 0 : "Parameter 'blockSize' of method 'setBlockSize' must be > 0";
        m_blockSize = blockSize;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue#getBlockSize()
     */
    @Override
    public int getBlockSize()
    {
        return m_blockSize;
    }

    @Override
    public List<IDuplicateCodeBlockOccurrence> getOccurrences()
    {
        return Collections.unmodifiableList(m_occurrences);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + m_blockSize;
        result = prime * result + ((m_occurrences == null) ? 0 : m_occurrences.hashCode());
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
        final DuplicateCodeBlockIssueImpl other = (DuplicateCodeBlockIssueImpl) obj;
        if (m_blockSize != other.m_blockSize)
        {
            return false;
        }
        if (m_occurrences == null)
        {
            if (other.m_occurrences != null)
            {
                return false;
            }
        }
        else if (!m_occurrences.equals(other.m_occurrences))
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