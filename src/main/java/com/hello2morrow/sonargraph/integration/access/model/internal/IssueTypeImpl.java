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

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public final class IssueTypeImpl extends ElementImpl implements IIssueType
{
    private final IIssueCategory m_category;
    private final Severity m_severity;

    public IssueTypeImpl(final String name, final String presentationName, final Severity severity, final IIssueCategory category)
    {
        super(name, presentationName);
        assert severity != null : "Parameter 'severity' of method 'IssueType' must not be null";
        assert category != null : "Parameter 'category' of method 'IssueType' must not be null";

        m_severity = severity;
        m_category = category;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IIssueType#getCategory()
     */
    @Override
    public IIssueCategory getCategory()
    {
        return m_category;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IIssueType#getSeverity()
     */
    @Override
    public Severity getSeverity()
    {
        return m_severity;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_category == null) ? 0 : m_category.hashCode());
        result = prime * result + ((m_severity == null) ? 0 : m_severity.hashCode());
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
        final IssueTypeImpl other = (IssueTypeImpl) obj;
        if (m_category == null)
        {
            if (other.m_category != null)
            {
                return false;
            }
        }
        else if (!m_category.equals(other.m_category))
        {
            return false;
        }
        if (m_severity != other.m_severity)
        {
            return false;
        }
        return true;
    }

}
