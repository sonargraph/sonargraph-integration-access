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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class ElementIssueImpl extends AbstractElementIssueImpl
{
    private static final long serialVersionUID = -3705193284431668430L;
    private final INamedElement element;
    private int line = -1;
    private int column = -1;

    public ElementIssueImpl(final IIssueType issueType, final String description, final IIssueProvider issueProvider, final INamedElement element,
            final boolean hasResolution, final int line)
    {
        super(issueType.getName() + element.toString() + line, issueType.getPresentationName(), description, issueType, issueProvider, hasResolution,
                line);
        assert element != null : "Parameter 'element' of method 'ElementIssue' must not be null";
        this.element = element;
    }

    public IElement getElement()
    {
        return element;
    }

    public int getLine()
    {
        return line;
    }

    public void setLine(final int line)
    {
        assert line > 0 : "Parameter 'line' of method setLine() must be > 0";
        this.line = line;
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn(final int column)
    {
        assert column > 0 : "Parameter 'column' of method setColumn() must be > 0";
        this.column = column;
    }

    @Override
    public List<INamedElement> getAffectedElements()
    {
        return Collections.unmodifiableList(Arrays.asList(element));
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + column;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        result = prime * result + line;
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
        final ElementIssueImpl other = (ElementIssueImpl) obj;
        if (column != other.column)
        {
            return false;
        }
        if (element == null)
        {
            if (other.element != null)
            {
                return false;
            }
        }
        else if (!element.equals(other.element))
        {
            return false;
        }
        if (line != other.line)
        {
            return false;
        }
        return true;
    }
}