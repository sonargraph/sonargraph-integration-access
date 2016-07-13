package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;

public final class MetricThreshold implements IMetricThreshold
{
    private final Number upperThreshold;
    private final Number lowerThreshold;
    private final IMetricId metricId;
    private final IMetricLevel metricLevel;

    public MetricThreshold(final IMetricId metricId, final IMetricLevel metricLevel, final Number lowerThreshold, final Number upperThreshold)
    {
        assert metricId != null : "Parameter 'metricId' of method 'MetricThreshold' must not be null";
        assert metricLevel != null : "Parameter 'metricLevel' of method 'MetricThreshold' must not be null";
        assert lowerThreshold != null : "Parameter 'lowerThreshold' of method 'MetricThreshold' must not be null";
        assert upperThreshold != null : "Parameter 'upperThreshold' of method 'MetricThreshold' must not be null";

        this.metricId = metricId;
        this.metricLevel = metricLevel;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    @Override
    public Number getUpperThreshold()
    {
        return upperThreshold;
    }

    @Override
    public Number getLowerThreshold()
    {
        return lowerThreshold;
    }

    @Override
    public IMetricId getMetricId()
    {
        return metricId;
    }

    @Override
    public IMetricLevel getMetricLevel()
    {
        return metricLevel;
    }

    @Override
    public String getName()
    {
        return new StringBuilder("threshold [metricId=").append(metricId.getName()).append(", metricLevel=").append(metricLevel.getName())
                .append(", lowerThreshold=").append(lowerThreshold).append(", upperThreshold=").append(upperThreshold).append("]").toString();
    }

    @Override
    public String getPresentationName()
    {
        return new StringBuilder("threshold [metricId=").append(metricId.getPresentationName()).append(", metricLevel=")
                .append(metricLevel.getPresentationName()).append(", lowerThreshold=").append(lowerThreshold).append(", upperThreshold=")
                .append(upperThreshold).append("]").toString();
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
