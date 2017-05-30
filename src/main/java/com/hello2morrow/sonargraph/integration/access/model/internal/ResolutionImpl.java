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

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class ResolutionImpl extends ElementImpl implements IResolution
{
    private static final long serialVersionUID = 6480407569513366548L;
    private final List<IIssue> issues;
    private final Priority priority;
    private final ResolutionType type;
    private final boolean isApplicable;
    private final int numberOfAffectedParserDependencies;

    //TODO: PresentationName?
    public ResolutionImpl(final String fqName, final ResolutionType type, final Priority priority, final List<IIssue> issues,
            final boolean isApplicable, final int numberOfAffectedParserDependencies)
    {
        super(fqName, type.name());

        this.type = type;
        this.priority = priority;
        this.issues = issues;
        this.isApplicable = isApplicable;
        this.numberOfAffectedParserDependencies = numberOfAffectedParserDependencies;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getIssues()
     */
    @Override
    public List<IIssue> getIssues()
    {
        return issues;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getPriority()
     */
    @Override
    public Priority getPriority()
    {
        return priority;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IResolution#getType()
     */
    @Override
    public ResolutionType getType()
    {
        return type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((issues == null) ? 0 : issues.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(type.name());
        builder.append(", priority=").append(priority);
        builder.append(", applicable=").append(isApplicable ? "yes" : "no");
        builder.append(", number of issues=").append(issues.size());
        builder.append(", number of affected parser dependencies=").append(numberOfAffectedParserDependencies);
        builder.append(", name=").append(getName());
        return builder.toString();
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
        final ResolutionImpl other = (ResolutionImpl) obj;
        if (issues == null)
        {
            if (other.issues != null)
            {
                return false;
            }
        }
        else if (!issues.equals(other.issues))
        {
            return false;
        }
        if (priority == null)
        {
            if (other.priority != null)
            {
                return false;
            }
        }
        else if (!priority.equals(other.priority))
        {
            return false;
        }
        if (type == null)
        {
            if (other.type != null)
            {
                return false;
            }
        }
        else if (!type.equals(other.type))
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean isApplicable()
    {
        return isApplicable;
    }

    @Override
    public boolean isTask()
    {
        return type != ResolutionType.IGNORE;
    }

    @Override
    public int getNumberOfAffectedParserDependencies()
    {
        return numberOfAffectedParserDependencies;
    }
}
