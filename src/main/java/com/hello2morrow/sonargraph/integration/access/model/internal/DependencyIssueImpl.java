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

import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class DependencyIssueImpl extends SingleNamedElementIssueImpl implements IDependencyIssue
{
    private static final long serialVersionUID = 2039911860451849412L;
    private final INamedElement from;
    private final INamedElement to;

    public DependencyIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final int line, final int column, final INamedElement from, final INamedElement to)
    {
        super(name, presentationName, description, issueType, provider, line, column);

        assert from != null : "Parameter 'from' of method 'DependencyIssue' must not be null";
        assert to != null : "Parameter 'to' of method 'DependencyIssue' must not be null";

        this.from = from;
        this.to = to;
    }

    @Override
    public INamedElement getNamedElement()
    {
        return from;
    }

    @Override
    public INamedElement getFrom()
    {
        return from;
    }

    @Override
    public INamedElement getTo()
    {
        return to;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("from:").append(from.getFqName());
        builder.append("\n");
        builder.append("to:").append(to.getFqName());
        return builder.toString();
    }
}