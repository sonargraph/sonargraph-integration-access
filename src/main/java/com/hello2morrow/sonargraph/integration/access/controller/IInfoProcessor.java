package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;

public interface IInfoProcessor
{
    /**
     * @return the absolute base directory
     */
    public String getBaseDirectory();

    /**
     * @param predicate the predicate used to filter the existing issues. If <code>null</code>, then all issues are returned.
     * @return Unmodifiable list of issues matching the predicate.
     */
    public List<IIssue> getIssues(Predicate<IIssue> filter);

    /**
     * @param predicate the predicate used to filter the existing resolutions. If <code>null</code>, then all resolutions are returned.
     * @return Unmodifiable list of resolutions matching the predicate.
     */
    public List<IResolution> getResolutions(Predicate<IResolution> filter);

    public Optional<IMetricId> getMetricId(IMetricLevel level, String metricId);

    public Optional<IMetricValue> getMetricValueForElement(IMetricId metricId, IMetricLevel level, String fqName);

    public List<IMetricId> getMetricIdsForLevel(IMetricLevel level);

    public List<IMetricLevel> getMetricLevels();

    public Optional<IMetricLevel> getMetricLevel(String systemLevel);

    public Map<INamedElement, IMetricValue> getMetricValues(String levelName, String metricIdName);

    public Optional<IMetricValue> getMetricValue(String metricName);
}
