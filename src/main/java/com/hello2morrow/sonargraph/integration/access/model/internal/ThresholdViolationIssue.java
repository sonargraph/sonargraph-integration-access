package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public class ThresholdViolationIssue extends ElementIssueImpl implements IThresholdViolationIssue
{
    private final IMetricThreshold threshold;
    private final Number metricValue;

    public ThresholdViolationIssue(final IIssueType issueType, final String description, final IIssueProvider issueProvider,
            final INamedElement element, final boolean hasResolution, final int line, final Number metricValue, final IMetricThreshold threshold)
    {
        super(issueType, description, issueProvider, element, hasResolution, line);
        this.metricValue = metricValue;
        this.threshold = threshold;
    }

    @Override
    public Number getMetricValue()
    {
        return metricValue;
    }

    @Override
    public IMetricThreshold getThreshold()
    {
        return threshold;
    }
}
