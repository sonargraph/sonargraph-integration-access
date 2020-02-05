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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

abstract class AbstractResolutionImpl extends ElementImpl implements IResolution
{
    private static final long serialVersionUID = 6480407569513366548L;
    private final List<IIssue> issues;
    private final Priority priority;
    private final ResolutionType type;
    private final Date dateTime;
    private final String assignee;
    private final String description;
    private final String information;
    private final int matchingElementsCount;
    private final List<IElementPattern> elementPatterns;
    private final List<IDependencyPattern> dependencyPatterns;
    private final IMatching matching;
    private final String descriptor;

    public AbstractResolutionImpl(final String fqName, final ResolutionType type, final Priority priority, final List<IIssue> issues,
            final int matchingElementsCount, final String description, final String information, final String assignee, final Date dateTime,
            final List<IElementPattern> elementPatterns, final List<IDependencyPattern> dependencyPatterns, final IMatching matching,
            final String descriptor)
    {
        super(fqName, type != null ? type.name() : "");

        assert type != null : "Parameter 'type' of method 'ResolutionImpl' must not be null";
        assert priority != null : "Parameter 'priority' of method 'ResolutionImpl' must not be null";
        assert issues != null : "Parameter 'issues' of method 'ResolutionImpl' must not be null";
        assert dateTime != null : "Parameter 'dateTime' of method 'ResolutionImpl' must not be null";

        this.type = type;
        this.priority = priority;
        this.issues = issues;

        this.matchingElementsCount = matchingElementsCount;

        this.description = description != null ? description : "";
        this.information = information != null ? information : "";
        this.assignee = assignee != null ? assignee : "";
        this.dateTime = dateTime;

        this.elementPatterns = elementPatterns != null ? elementPatterns : Collections.emptyList();
        this.dependencyPatterns = dependencyPatterns != null ? dependencyPatterns : Collections.emptyList();
        this.matching = matching;
        this.descriptor = descriptor != null ? descriptor : "";
    }

    @Override
    public final String getFqName()
    {
        return getName();
    }

    @Override
    public final Optional<String> getOriginalFqName()
    {
        return Optional.of(getFqName());
    }

    @Override
    public final boolean isLocationOnly()
    {
        return false;
    }

    @Override
    public Optional<? extends INamedElement> getOriginalLocation()
    {
        return Optional.empty();
    }

    @Override
    public final String getKind()
    {
        return getType().getStandardName();
    }

    @Override
    public final String getPresentationKind()
    {
        return getType().getPresentationName();
    }

    @Override
    public abstract String getImageResourceName();

    @Override
    public List<IIssue> getIssues()
    {
        return Collections.unmodifiableList(issues);
    }

    @Override
    public Priority getPriority()
    {
        return priority;
    }

    @Override
    public ResolutionType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(type.name());
        builder.append(", priority=").append(priority);
        builder.append(", description=").append(description);
        builder.append(", assignee=").append(assignee);
        builder.append(", date=").append(dateTime);
        builder.append(", number of issues=").append(issues.size());
        builder.append(", name=").append(getName());
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((assignee == null) ? 0 : assignee.hashCode());
        result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((issues == null) ? 0 : issues.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        final AbstractResolutionImpl other = (AbstractResolutionImpl) obj;
        if (assignee == null)
        {
            if (other.assignee != null)
            {
                return false;
            }
        }
        else if (!assignee.equals(other.assignee))
        {
            return false;
        }
        if (dateTime == null)
        {
            if (other.dateTime != null)
            {
                return false;
            }
        }
        else if (!dateTime.equals(other.dateTime))
        {
            return false;
        }
        if (description == null)
        {
            if (other.description != null)
            {
                return false;
            }
        }
        else if (!description.equals(other.description))
        {
            return false;
        }

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

        if (priority != other.priority)
        {
            return false;
        }
        if (type != other.type)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean isTask()
    {
        return type != ResolutionType.IGNORE;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getInformation()
    {
        return information;
    }

    @Override
    public String getAssignee()
    {
        return assignee;
    }

    @Override
    public String getDate()
    {
        return Utility.getDateTimeStringFromLocale(dateTime);
    }

    @Override
    public Date getCreationDate()
    {
        return dateTime;
    }

    @Override
    public int getMatchingElementsCount()
    {
        return matchingElementsCount;
    }

    @Override
    public List<IElementPattern> getElementPatterns()
    {
        return Collections.unmodifiableList(elementPatterns);
    }

    @Override
    public List<IDependencyPattern> getDependencyPatterns()
    {
        return Collections.unmodifiableList(dependencyPatterns);
    }

    @Override
    public IMatching getMatching()
    {
        return matching;
    }

    @Override
    public String getDescriptor()
    {
        return descriptor;
    }

}