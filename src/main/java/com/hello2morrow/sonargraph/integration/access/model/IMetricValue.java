package com.hello2morrow.sonargraph.integration.access.model;

public interface IMetricValue
{
    public IMetricId getId();

    public IMetricLevel getLevel();

    public Number getValue();

    public boolean isFloat();
}