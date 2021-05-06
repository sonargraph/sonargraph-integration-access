/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append(" [namedElement=");
        builder.append(namedElement);
        builder.append(", severity=");
        builder.append(severity);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}