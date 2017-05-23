package com.hello2morrow.sonargraph.integration.access.model.diff;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;

public interface IMetricThresholdDelta extends IStandardDelta<IMetricThreshold>
{
    List<Pair<IMetricThreshold, IMetricThreshold>> getChanged();
}
