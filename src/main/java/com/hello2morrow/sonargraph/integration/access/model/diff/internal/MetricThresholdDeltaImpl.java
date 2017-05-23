package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricThresholdDelta;

public class MetricThresholdDeltaImpl implements IMetricThresholdDelta
{
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
}
