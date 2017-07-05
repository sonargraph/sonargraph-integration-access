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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;

public class IssueDeltaImpl implements IIssueDelta
{
    private static final long serialVersionUID = -3056194877234260699L;
    private static final String INDENTATION = "    ";
    private final List<IIssue> unchanged;
    private final List<IIssue> added;
    private final List<IIssue> removed;
    private final List<Pair<IIssue, IIssue>> improved;
    private final List<Pair<IIssue, IIssue>> worse;

    public IssueDeltaImpl(final List<IIssue> unchanged, final List<IIssue> added, final List<IIssue> removed,
            final List<Pair<IIssue, IIssue>> improved, final List<Pair<IIssue, IIssue>> worse)
    {
        assert unchanged != null : "Parameter 'unchanged' of method 'IssueDeltaImpl' must not be null";
        assert added != null : "Parameter 'added' of method 'IssueDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'IssueDeltaImpl' must not be null";
        assert improved != null : "Parameter 'improved' of method 'IssueDeltaImpl' must not be null";
        assert worse != null : "Parameter 'worse' of method 'IssueDeltaImpl' must not be null";

        this.unchanged = unchanged;
        this.added = added;
        this.removed = removed;
        this.improved = improved;
        this.worse = worse;
    }

    @Override
    public List<IIssue> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<IIssue> getUnchanged()
    {
        return Collections.unmodifiableList(unchanged);
    }

    @Override
    public List<IIssue> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<Pair<IIssue, IIssue>> getWorse()
    {
        return Collections.unmodifiableList(worse);
    }

    @Override
    public List<Pair<IIssue, IIssue>> getImproved()
    {
        return Collections.unmodifiableList(improved);
    }

    @Override
    public String toString()
    {
        return print(true);
    }

    @Override
    public boolean containsChanges()
    {
        return !added.isEmpty() || !removed.isEmpty() || !improved.isEmpty() || !worse.isEmpty();
    }

    @Override
    public String print(final boolean includeUnchanged)
    {
        final StringBuilder builder = new StringBuilder("Issue delta:");
        builder.append(INDENTATION).append("\n").append(INDENTATION).append("Removed (").append(removed.size()).append("):");
        final Consumer<? super IIssue> action = i -> builder.append("\n").append(INDENTATION).append(INDENTATION).append(i.toString());
        removed.forEach(action);
        builder.append(INDENTATION).append("\n").append(INDENTATION).append("Improved (").append(improved.size()).append("):");
        final Consumer<? super Pair<IIssue, IIssue>> action2 = i -> builder.append("\n").append(INDENTATION).append(INDENTATION).append("Previous: ")
                .append(i.getFirst().toString()).append("; Now: ").append(i.getSecond().toString());
        improved.forEach(action2);
        builder.append(INDENTATION).append("\n").append(INDENTATION).append("Worsened (").append(worse.size()).append("):");
        worse.forEach(action2);
        builder.append(INDENTATION).append("\n").append(INDENTATION).append("Added (").append(added.size()).append("):");
        added.forEach(action);
        if (includeUnchanged)
        {
            builder.append(INDENTATION).append("\n").append(INDENTATION).append("Unchanged (").append(unchanged.size()).append("):");
            unchanged.forEach(action);
        }
        return builder.toString();
    }
}
