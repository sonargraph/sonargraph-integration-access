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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class DependencyIssueImpl extends IssueImpl implements IDependencyIssue
{
    private final INamedElement m_from;
    private final INamedElement m_to;

    public DependencyIssueImpl(final IIssueType issueType, final String description, final IIssueProvider provider, final boolean hasResolution,
            final INamedElement from, final INamedElement to, final int line)
    {
        super(issueType.getName(), issueType.getPresentationName(), description, issueType, provider, hasResolution, line);

        assert from != null : "Parameter 'from' of method 'DependencyIssue' must not be null";
        assert to != null : "Parameter 'to' of method 'DependencyIssue' must not be null";

        m_from = from;
        m_to = to;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue#getFrom()
     */
    @Override
    public INamedElement getFrom()
    {
        return m_from;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue#getTo()
     */
    @Override
    public INamedElement getTo()
    {
        return m_to;
    }

    @Override
    public List<INamedElement> getOrigins()
    {
        return Collections.unmodifiableList(Arrays.asList(getFrom()));
    }

    //TODO: Add further info

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_from == null) ? 0 : m_from.hashCode());
        result = prime * result + ((m_to == null) ? 0 : m_to.hashCode());
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
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final DependencyIssueImpl other = (DependencyIssueImpl) obj;
        if (m_from == null)
        {
            if (other.m_from != null)
            {
                return false;
            }
        }
        else if (!m_from.equals(other.m_from))
        {
            return false;
        }
        if (m_to == null)
        {
            if (other.m_to != null)
            {
                return false;
            }
        }
        else if (!m_to.equals(other.m_to))
        {
            return false;
        }
        return true;
    }
}