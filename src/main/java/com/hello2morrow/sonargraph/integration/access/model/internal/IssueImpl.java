/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public abstract class IssueImpl extends ElementWithDescriptionImpl implements IIssue
{
    private static final long serialVersionUID = -693212815143740221L;
    private final IIssueType issueType;
    private final IIssueProvider issueProvider;
    private final int line;
    private final int column;
    private ResolutionType resolutionType = ResolutionType.NONE;

    public IssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final int line, final int column)
    {
        super(name, presentationName, description != null ? description : "");
        assert issueType != null : "Parameter 'issueType' of method 'IssueImpl' must not be null";
        assert provider != null : "Parameter 'provider' of method 'Issue' must not be null";

        this.issueType = issueType;
        this.issueProvider = provider;
        this.line = line;
        this.column = column;
    }

    @Override
    public final IIssueProvider getIssueProvider()
    {
        return issueProvider;
    }

    @Override
    public final IIssueType getIssueType()
    {
        return issueType;
    }

    @Override
    public String getKey()
    {
        return issueType.getName() + KEY_SEPARATOR + issueProvider.getName();
    }

    public final void setResolutionType(final ResolutionType resolutionType)
    {
        assert resolutionType != null : "Parameter 'resolutionType' of method 'setResolutionType' must not be null";
        this.resolutionType = resolutionType;
    }

    @Override
    public ResolutionType getResolutionType()
    {
        return resolutionType;
    }

    @Override
    public final boolean isIgnored()
    {
        return ResolutionType.IGNORE.equals(resolutionType);
    }

    @Override
    public final boolean hasResolution()
    {
        return !ResolutionType.NONE.equals(resolutionType);
    }

    @Override
    public final int getLine()
    {
        return line;
    }

    @Override
    public final int getColumn()
    {
        return column;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((issueProvider == null) ? 0 : issueProvider.hashCode());
        result = prime * result + ((issueType == null) ? 0 : issueType.hashCode());
        result = prime * result + line;
        result = prime * result + column;
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
        if (line != other.line)
        {
            return false;
        }
        if (column != other.column)
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
        builder.append("line:").append(line);
        builder.append("\n");
        if (resolutionType != null)
        {
            builder.append("resolutionType:").append(resolutionType);
        }
        return builder.toString();
    }
}