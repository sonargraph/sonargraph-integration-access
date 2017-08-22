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

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class ElementIssueImpl extends IssueImpl implements IElementIssue
{
    private static final long serialVersionUID = -3705193284431668430L;
    private final INamedElement element;

    private static String createName(final IIssueType issueType, final INamedElement element, final int line)
    {
        assert issueType != null : "Parameter 'issueType' of method 'createName' must not be null";
        assert element != null : "Parameter 'element' of method 'createName' must not be null";
        return issueType.getName() + ":" + element.getFqName() + ":" + line;
    }

    public ElementIssueImpl(final IIssueType issueType, final String description, final IIssueProvider issueProvider, final INamedElement element,
            final boolean hasResolution, final int line)
    {
        super(createName(issueType, element, line), issueType.getPresentationName(), description, issueType, issueProvider, hasResolution, line);
        assert element != null : "Parameter 'element' of method 'ElementIssue' must not be null";
        this.element = element;
    }

    public final IElement getElement()
    {
        return element;
    }

    @Override
    public final List<INamedElement> getAffectedElements()
    {
        return Collections.singletonList(element);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + element.hashCode();
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
        final ElementIssueImpl other = (ElementIssueImpl) obj;
        return element.equals(other.element);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("element:").append(element.getFqName());
        return builder.toString();
    }
}