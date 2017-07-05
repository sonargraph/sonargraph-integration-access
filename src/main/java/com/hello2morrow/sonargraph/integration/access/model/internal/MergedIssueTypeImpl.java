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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public class MergedIssueTypeImpl extends IssueTypeImpl implements IMergedIssueType
{
    private static final long serialVersionUID = -1042056968763573015L;
    private final List<IBasicSoftwareSystemInfo> systems = new ArrayList<>();

    public MergedIssueTypeImpl(final String name, final String presentationName, final Severity severity, final IIssueCategory category,
            final IIssueProvider provider, final String description, final IBasicSoftwareSystemInfo system)
    {
        super(name, presentationName, severity, category, provider, description);
        assert system != null : "Parameter 'system' of method 'MergedIssueCategoryImpl' must not be null";
        systems.add(system);
    }

    @Override
    public List<IBasicSoftwareSystemInfo> getSystems()
    {
        return Collections.unmodifiableList(systems);
    }

    @Override
    public void addSystem(final IBasicSoftwareSystemInfo systemInfo)
    {
        assert systemInfo != null : "Parameter 'systemInfo' of method 'addSystem' must not be null";
        systems.add(systemInfo);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((systems == null) ? 0 : systems.hashCode());
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
        final MergedIssueTypeImpl other = (MergedIssueTypeImpl) obj;
        if (systems == null)
        {
            if (other.systems != null)
            {
                return false;
            }
        }
        else if (!systems.equals(other.systems))
        {
            return false;
        }
        return true;
    }
}
