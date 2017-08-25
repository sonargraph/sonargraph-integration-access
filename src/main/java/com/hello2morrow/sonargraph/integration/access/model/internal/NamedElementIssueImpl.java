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

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class NamedElementIssueImpl extends SingleNamedElementIssueImpl implements IElementIssue
{
    private static final long serialVersionUID = -3705193284431668430L;
    private final INamedElement namedElement;

    public NamedElementIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider issueProvider, final int line, final int column, final INamedElement namedElement)
    {
        super(name, presentationName, description, issueType, issueProvider, line, column);
        assert namedElement != null : "Parameter 'namedElement' of method 'NamedElementIssueImpl' must not be null";
        this.namedElement = namedElement;
    }

    @Override
    public final INamedElement getNamedElement()
    {
        return namedElement;
    }

    @Override
    public final List<INamedElement> getAffectedElements()
    {
        return Collections.singletonList(namedElement);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + namedElement.hashCode();
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
        final NamedElementIssueImpl other = (NamedElementIssueImpl) obj;
        return namedElement.equals(other.namedElement);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("namedElement:").append(namedElement.getFqName());
        return builder.toString();
    }
}