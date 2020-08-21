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

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public class IssueTypeImpl extends ElementImpl implements IIssueType
{
    private static final long serialVersionUID = 5614397098070123775L;
    private final IIssueCategory category;
    private final List<Severity> supportedSeverities;
    private final String description;
    private final IIssueProvider provider;

    public IssueTypeImpl(final String name, final String presentationName, final List<Severity> severities, final IIssueCategory category,
            final IIssueProvider provider, final String description)
    {
        super(name, presentationName);
        assert severities != null : "Parameter 'severities' of method 'IssueType' must not be null";
        assert category != null : "Parameter 'category' of method 'IssueType' must not be null";

        this.supportedSeverities = severities;
        this.category = category;
        this.description = description != null ? description : "";
        this.provider = provider;
    }

    @Override
    public IIssueCategory getCategory()
    {
        return category;
    }

    @Override
    public List<Severity> getSupportedSeverities()
    {
        return supportedSeverities;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        return result;
    }

    @Override
    public IIssueProvider getProvider()
    {
        return provider;
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
        final IssueTypeImpl other = (IssueTypeImpl) obj;
        if (provider == null)
        {
            if (other.provider != null)
            {
                return false;
            }
        }
        else if (!provider.equals(other.provider))
        {
            return false;
        }
        return true;
    }
}