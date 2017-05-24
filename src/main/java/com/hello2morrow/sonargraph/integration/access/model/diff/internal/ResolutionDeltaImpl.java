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

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.diff.IResolutionDelta;

public class ResolutionDeltaImpl implements IResolutionDelta
{
    private final List<IResolution> added;
    private final List<IResolution> removed;
    private final List<IResolution> unchanged;
    private final List<Pair<IResolution, IResolution>> changed;

    public ResolutionDeltaImpl(final List<IResolution> added, final List<IResolution> removed, final List<Pair<IResolution, IResolution>> changed,
            final List<IResolution> unchanged)
    {
        assert added != null : "Parameter 'added' of method 'ResolutionDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'ResolutionDeltaImpl' must not be null";
        assert changed != null : "Parameter 'changed' of method 'ResolutionDeltaImpl' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'ResolutionDeltaImpl' must not be null";

        this.added = added;
        this.removed = removed;
        this.changed = changed;
        this.unchanged = unchanged;
    }

    @Override
    public List<IResolution> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<IResolution> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<Pair<IResolution, IResolution>> getChanged()
    {
        return Collections.unmodifiableList(changed);
    }

    @Override
    public List<IResolution> getUnchanged()
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
        return !added.isEmpty() || !removed.isEmpty() || !changed.isEmpty();
    }

    @Override
    public String print(final boolean includeUnchanged)
    {
        final StringBuilder builder = new StringBuilder("Resolution Delta:");
        builder.append("\n").append(StringUtility.INDENTATION).append("Removed (").append(removed.size()).append("):");
        final Consumer<? super IResolution> action = r -> builder.append("\n").append(StringUtility.INDENTATION).append(StringUtility.INDENTATION)
                .append(r.toString());
        removed.forEach(action);
        builder.append("\n").append(StringUtility.INDENTATION).append("Added (").append(added.size()).append("):");
        added.forEach(action);
        builder.append("\n").append(StringUtility.INDENTATION).append("Changed (").append(changed.size()).append("):");
        changed.forEach(p -> builder.append("\n").append(StringUtility.INDENTATION).append(StringUtility.INDENTATION).append("Previous: ")
                .append(p.getFirst().toString()).append("; Now: ").append(p.getSecond().toString()));
        if (includeUnchanged)
        {
            builder.append("\n").append(StringUtility.INDENTATION).append("Unchanged (").append(unchanged.size()).append("):");
            unchanged.forEach(action);
        }
        return builder.toString();
    }
}
