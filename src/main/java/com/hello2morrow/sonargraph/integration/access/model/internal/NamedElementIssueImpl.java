/*
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

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.INamedElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public class NamedElementIssueImpl extends SingleNamedElementIssueImpl implements INamedElementIssue
{
    private static final long serialVersionUID = -9139600683023358335L;
    private final INamedElement namedElement;
    private final Severity severity;

    public NamedElementIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final Severity severity, final IIssueProvider issueProvider, final int line, final int column, final INamedElement namedElement)
    {
        super(name, presentationName, description, issueType, issueProvider, line, column);
        assert namedElement != null : "Parameter 'namedElement' of method 'NamedElementIssueImpl' must not be null";
        assert severity != null : "Parameter 'severity' of method 'NamedElementIssueImpl' must not be null";

        this.namedElement = namedElement;
        this.severity = severity;
    }

    @Override
    public final INamedElement getNamedElement()
    {
        return namedElement;
    }

    @Override
    public Severity getSeverity()
    {
        return severity;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((namedElement == null) ? 0 : namedElement.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
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
        final NamedElementIssueImpl other = (NamedElementIssueImpl) obj;
        if (namedElement == null)
        {
            if (other.namedElement != null)
            {
                return false;
            }
        }
        else if (!namedElement.equals(other.namedElement))
        {
            return false;
        }
        if (severity != other.severity)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("NamedElementIssueImpl [namedElement=");
        builder.append(namedElement);
        builder.append(", severity=");
        builder.append(severity);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}