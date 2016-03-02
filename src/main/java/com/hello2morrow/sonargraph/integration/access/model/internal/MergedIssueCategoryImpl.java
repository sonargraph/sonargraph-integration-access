/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueCategory;

public class MergedIssueCategoryImpl extends IssueCategoryImpl implements IMergedIssueCategory
{
    private final List<IBasicSoftwareSystemInfo> projects = new ArrayList<>();

    public MergedIssueCategoryImpl(final String name, final String presentationName, final IBasicSoftwareSystemInfo project)
    {
        super(name, presentationName);
        assert project != null : "Parameter 'project' of method 'MergedIssueCategoryImpl' must not be null";
        projects.add(project);
    }

    @Override
    public List<IBasicSoftwareSystemInfo> getProjects()
    {
        return Collections.unmodifiableList(projects);
    }

    @Override
    public void addSystem(final IBasicSoftwareSystemInfo systemInfo)
    {
        assert systemInfo != null : "Parameter 'systemInfo' of method 'addSystem' must not be null";
        projects.add(systemInfo);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((projects == null) ? 0 : projects.hashCode());
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
        final MergedIssueCategoryImpl other = (MergedIssueCategoryImpl) obj;
        if (projects == null)
        {
            if (other.projects != null)
            {
                return false;
            }
        }
        else if (!projects.equals(other.projects))
        {
            return false;
        }
        return true;
    }

}
