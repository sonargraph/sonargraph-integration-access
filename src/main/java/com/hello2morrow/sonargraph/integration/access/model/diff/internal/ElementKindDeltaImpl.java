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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.diff.IElementKindDelta;

public class ElementKindDeltaImpl implements IElementKindDelta
{
    private static final long serialVersionUID = -5424079080974769404L;
    private final List<String> added;
    private final List<String> removed;
    private final List<String> unchanged;

    public ElementKindDeltaImpl(final List<String> added, final List<String> removed, final List<String> unchanged)
    {
        assert added != null : "Parameter 'added' of method 'ElementKindDelta' must not be null";
        assert removed != null : "Parameter 'removed' of method 'ElementKindDelta' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'ElementKindDelta' must not be null";

        this.added = added;
        this.removed = removed;
        this.unchanged = unchanged;
    }

    @Override
    public List<String> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<String> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<String> getUnchanged()
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
        final StringBuilder builder = new StringBuilder("Delta of ElementKind:");
        builder.append("\n").append(StringUtility.INDENTATION).append("Removed (").append(removed.size()).append("):");
        final Consumer<? super String> action = e -> builder.append("\n").append(StringUtility.INDENTATION).append(StringUtility.INDENTATION)
                .append(e);
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
