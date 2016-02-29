package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;
import java.util.Map;

public interface IExportMetaData
{
    public Map<String, IIssueCategory> getIssueCategories();

    public Map<String, IMetricCategory> getMetricCategories();

    public Map<String, IMetricId> getMetricIds();

    public Map<String, IMetricProvider> getMetricProviders();

    public String getResourceIdentifier();

    public Map<String, IMetricLevel> getMetricLevels();

    public List<IMetricId> getMetricIdsForLevel(IMetricLevel level);
}