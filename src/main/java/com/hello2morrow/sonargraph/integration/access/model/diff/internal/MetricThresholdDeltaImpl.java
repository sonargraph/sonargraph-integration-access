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

import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricThresholdDelta;

public class MetricThresholdDeltaImpl implements IMetricThresholdDelta
{
    private static final long serialVersionUID = 173829836484799550L;
    private final List<IMetricThreshold> added;
    private final List<IMetricThreshold> removed;
    private final List<Pair<IMetricThreshold, IMetricThreshold>> changed;
    private final List<IMetricThreshold> unchanged;

    public MetricThresholdDeltaImpl(final List<IMetricThreshold> added, final List<IMetricThreshold> removed, final List<IMetricThreshold> unchanged,
            final List<Pair<IMetricThreshold, IMetricThreshold>> changed)
    {
        assert added != null : "Parameter 'added' of method 'MetricThresholdDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'MetricThresholdDeltaImpl' must not be null";
        assert changed != null : "Parameter 'changed' of method 'MetricThresholdDeltaImpl' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'MetricThresholdDeltaImpl' must not be null";

        this.added = added;
        this.removed = removed;
        this.changed = changed;
        this.unchanged = unchanged;
    }

    @Override
    public List<IMetricThreshold> getAdded()
    {
        return added;
    }

    @Override
    public List<IMetricThreshold> getRemoved()
    {
        return removed;
    }

    @Override
    public List<IMetricThreshold> getUnchanged()
    {
        return unchanged;
    }

    @Override
    public List<Pair<IMetricThreshold, IMetricThreshold>> getChanged()
    {
        return changed;
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
        final StringBuilder builder = new StringBuilder("Delta of Metric Thresholds");
        builder.append("\n").append(StringUtility.INDENTATION).append("Removed: (").append(removed.size()).append("):");
        final Consumer<? super IMetricThreshold> action = th -> builder.append("\n").append(StringUtility.INDENTATION)
                .append(StringUtility.INDENTATION).append(th.toString());
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
