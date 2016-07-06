package com.hello2morrow.sonargraph.integration.access.model;

public interface IThresholdViolationIssue extends IElementIssue
{
    public Number getMetricValue();

    public IMetricThreshold getThreshold();
}
