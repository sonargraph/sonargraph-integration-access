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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IFilterDelta;
import com.hello2morrow.sonargraph.integration.access.model.IWildcardPattern;

public class FilterDeltaImpl implements IFilterDelta
{
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String presentationName;

    private final List<IWildcardPattern> addedIncludePatterns = new ArrayList<>(5);
    private final List<IWildcardPattern> removedIncludePatterns = new ArrayList<>(5);
    private final List<IWildcardPattern> addedExcludePatterns = new ArrayList<>(5);
    private final List<IWildcardPattern> removedExcludePatterns = new ArrayList<>(5);

    public FilterDeltaImpl(final String name, final String presentationName)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'FilterDeltaImpl' must not be empty";
        assert presentationName != null
                && presentationName.length() > 0 : "Parameter 'presentationName' of method 'FilterDeltaImpl' must not be empty";

        this.name = name;
        this.presentationName = presentationName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getPresentationName()
    {
        return presentationName;
    }

    @Override
    public boolean isEmpty()
    {
        return addedIncludePatterns.isEmpty() && removedIncludePatterns.isEmpty() && addedExcludePatterns.isEmpty()
                && removedExcludePatterns.isEmpty();
    }

    public void removedIncludePattern(final IWildcardPattern include)
    {
        assert include != null : "Parameter 'include' of method 'addRemovedIncludePatterns' must not be null";
        removedIncludePatterns.add(include);
    }

    @Override
    public List<IWildcardPattern> getRemovedIncludePatterns()
    {
        return Collections.unmodifiableList(removedIncludePatterns);
    }

    public void removedExcludePattern(final IWildcardPattern exclude)
    {
        assert exclude != null : "Parameter 'exclude' of method 'addRemovedExcludePattern' must not be null";
        removedExcludePatterns.add(exclude);
    }

    @Override
    public List<IWildcardPattern> getRemovedExcludePatterns()
    {
        return Collections.unmodifiableList(removedExcludePatterns);
    }

    public void addedIncludePattern(final IWildcardPattern include)
    {
        assert include != null : "Parameter 'include' of method 'addedIncludePattern' must not be null";
        addedIncludePatterns.add(include);
    }

    @Override
    public List<IWildcardPattern> getAddedIncludePatterns()
    {
        return Collections.unmodifiableList(addedIncludePatterns);
    }

    public void addedExcludePattern(final IWildcardPattern exclude)
    {
        assert exclude != null : "Parameter 'exclude' of method 'addedExcludePattern' must not be null";
        addedExcludePatterns.add(exclude);
    }

    @Override
    public List<IWildcardPattern> getAddedExcludePatterns()
    {
        return Collections.unmodifiableList(addedExcludePatterns);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append(Utility.INDENTATION).append(presentationName);
        addPatternsInfo(builder, addedIncludePatterns, "Added include patterns");
        addPatternsInfo(builder, removedIncludePatterns, "Removed include patterns");
        addPatternsInfo(builder, addedExcludePatterns, "Added exclude patterns");
        addPatternsInfo(builder, removedExcludePatterns, "Removed exclude patterns");

        return builder.toString();
    }

    private void addPatternsInfo(final StringBuilder builder, final List<IWildcardPattern> patterns, final String description)
    {
        builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(description).append(" (").append(patterns.size())
                .append(")");
        for (final IWildcardPattern next : patterns)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPattern())
                    .append(" (with ").append(next.getNumberOfMatches()).append(" matches)");
        }
    }
}