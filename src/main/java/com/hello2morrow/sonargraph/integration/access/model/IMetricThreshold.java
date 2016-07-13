package com.hello2morrow.sonargraph.integration.access.model;

public interface IMetricThreshold extends IElement
{
    public Number getUpperThreshold();

    public Number getLowerThreshold();

    public IMetricId getMetricId();

    public IMetricLevel getMetricLevel();
}
