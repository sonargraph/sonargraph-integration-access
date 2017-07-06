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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;

public final class StandardDeltaImpl<T extends IElement> implements IStandardDelta<T>
{
    private static final long serialVersionUID = -7912469796001338998L;
    private final String name;
    private final List<T> added;
    private final List<T> unchanged;
    private final List<T> removed;

    private StandardDeltaImpl(final String name, final List<T> added, final List<T> removed, final List<T> unchanged)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'StandardDeltaImpl' must not be empty";
        assert added != null : "Parameter 'added' of method 'StandardDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'StandardDeltaImpl' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'StandardDeltaImpl' must not be null";

        this.name = name;
        this.added = added;
        this.removed = removed;
        this.unchanged = unchanged;
    }

    public static <T extends IElement> StandardDeltaImpl<T> computeDelta(final String name, final List<T> elements1, final List<T> elements2)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'computeDelta' must not be empty";
        assert elements1 != null : "Parameter 'elements1' of method 'computeDelta' must not be null";
        assert elements2 != null : "Parameter 'elements2' of method 'computeDelta' must not be null";

        final List<T> added = new ArrayList<>(elements2);
        final List<T> unchanged = new ArrayList<>();
        final List<T> removed = new ArrayList<>();

        for (final T next : new ArrayList<>(elements1))
        {
            if (added.remove(next))
            {
                unchanged.add(next);
            }
            else
            {
                removed.add(next);
            }
        }

        return new StandardDeltaImpl<>(name, added, removed, unchanged);
    }

    @Override
    public List<T> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<T> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<T> getUnchanged()
    {
        return Collections.unmodifiableList(unchanged);
    }

    @Override
    public String toString()
    {
        return print(true);
    }

    @Override
    public boolean containsChanges()
    {
        return !added.isEmpty() || !removed.isEmpty();
    }

    @Override
    public String print(final boolean includeUnchanged)
    {
        final StringBuilder builder = new StringBuilder(name);
        builder.append(" Delta");
        builder.append("\n").append(StringUtility.INDENTATION).append("Removed (").append(removed.size()).append("):");
        final Consumer<? super T> action = r -> builder.append("\n").append(StringUtility.INDENTATION).append(StringUtility.INDENTATION)
                .append(r.getName());
        removed.forEach(action);
        builder.append("\n").append(StringUtility.INDENTATION).append("Added (").append(added.size()).append("):");
        added.forEach(action);
        if (includeUnchanged)
        {
            builder.append("\n").append(StringUtility.INDENTATION).append("Unchanged (").append(unchanged.size()).append("):");
            unchanged.forEach(action);
        }
        return builder.toString();
    }
}
