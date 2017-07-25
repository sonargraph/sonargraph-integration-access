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

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;

public abstract class IssueImpl extends ElementWithDescriptionImpl implements IIssue
{
    private static final long serialVersionUID = -693212815143740221L;
    private final IIssueType issueType;
    private final IIssueProvider issueProvider;
    private final boolean hasResolution;
    private final int lineNumber;
    private boolean isIgnored;

    public IssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final boolean hasResolution, final int line)
    {
        super(name, presentationName, description != null ? description : "");
        assert provider != null : "Parameter 'provider' of method 'Issue' must not be null";

        this.issueType = issueType;
        this.issueProvider = provider;
        this.hasResolution = hasResolution;
        this.lineNumber = line;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IIssue#getIssueProvider()
     */
    @Override
    public final IIssueProvider getIssueProvider()
    {
        return issueProvider;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IIssue#getIssueType()
     */
    @Override
    public final IIssueType getIssueType()
    {
        return issueType;
    }

    public final void setIsIgnored(final boolean isIgnored)
    {
        this.isIgnored = isIgnored;
    }

    @Override
    public final boolean isIgnored()
    {
        return isIgnored;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IIssue#hasResolution()
     */
    @Override
    public final boolean hasResolution()
    {
        return hasResolution;
    }

    @Override
    public final int getLineNumber()
    {
        return lineNumber;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hasResolution ? 1231 : 1237);
        result = prime * result + ((issueProvider == null) ? 0 : issueProvider.hashCode());
        result = prime * result + ((issueType == null) ? 0 : issueType.hashCode());
        result = prime * result + lineNumber;
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
        final IssueImpl other = (IssueImpl) obj;
        if (hasResolution != other.hasResolution)
        {
            return false;
        }
        if (issueProvider == null)
        {
            if (other.issueProvider != null)
            {
                return false;
            }
        }
        else if (!issueProvider.equals(other.issueProvider))
        {
            return false;
        }
        if (issueType == null)
        {
            if (other.issueType != null)
            {
                return false;
            }
        }
        else if (!issueType.equals(other.issueType))
        {
            return false;
        }
        if (lineNumber != other.lineNumber)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("type:").append(issueType.getPresentationName());
        builder.append("\n");
        builder.append("provider:").append(issueProvider.getPresentationName());
        builder.append("\n");
        builder.append("line:").append(lineNumber);
        builder.append("\n");
        builder.append("hasResolution:").append(hasResolution);
        builder.append("\n");
        builder.append("isIgnored:").append(isIgnored);
        return builder.toString();
    }
}