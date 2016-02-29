package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;

final class SystemInfoProcessorImpl implements ISystemInfoProcessor
{
    private final SoftwareSystemImpl m_softwareSystem;

    public SystemInfoProcessorImpl(final SoftwareSystemImpl system)
    {
        assert system != null : "Parameter 'system' of method 'SystemInfoProcessorImpl' must not be null";
        m_softwareSystem = system;
    }

    @Override
    public String getBaseDirectory()
    {
        return m_softwareSystem.getBaseDir();
    }

    @Override
    public List<IIssue> getIssues(final Predicate<IIssue> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(m_softwareSystem.getIssues().values().stream().flatMap(list -> list.stream())
                    .collect(Collectors.toList()));
        }

        return Collections.unmodifiableList(m_softwareSystem.getIssues().values().stream().flatMap(list -> list.stream()).filter(filter)
                .collect(Collectors.toList()));
    }

    @Override
    public List<IResolution> getResolutions(final Predicate<IResolution> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(m_softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                    .collect(Collectors.toList()));
        }

        return Collections.unmodifiableList(m_softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                .filter(filter).collect(Collectors.toList()));
    }

    @Override
    public Optional<IMetricId> getMetricId(final IMetricLevel level, final String metricId)
    {
        return m_softwareSystem.getMetricIdsForLevel(level).stream().filter((final IMetricId id) -> id.getName().equals(metricId)).findAny();
    }

    @Override
    public Optional<IMetricValue> getMetricValueForElement(final IMetricId metricId, final IMetricLevel level, final String fqName)
    {
        return m_softwareSystem.getMetricValueForElement(metricId, level, fqName);
    }

    @Override
    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'getMetricIdsForLevel' must not be null";
        return m_softwareSystem.getMetricIdsForLevel(level);
    }

    @Override
    public List<IIssueProvider> getIssueProviders()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getIssueProviders().values()));
    }

    @Override
    public List<IIssueType> getIssueTypes()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getIssueTypes().values()));
    }

    @Override
    public List<ICycleGroup> getCycleGroups(final Predicate<ICycleGroup> filter)
    {
        final Predicate<ICycleGroup> filter2;
        if (filter != null)
        {
            filter2 = filter;
        }
        else
        {
            filter2 = (final ICycleGroup group) -> true;
        }
        return getIssues((final IIssue issue) -> issue instanceof ICycleGroup).stream().map((final IIssue issue) -> (ICycleGroup) issue)
                .filter(filter2).collect(Collectors.toList());
    }

    @Override
    public List<IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getAnalyzers().values()));
    }

    @Override
    public List<IFeature> getFeatures()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getFeatures().values()));
    }

    @Override
    public List<IMetricCategory> getMetricCategories()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getMetricCategories().values()));
    }

    @Override
    public List<IMetricProvider> getMetricProviders()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getMetricProviders().values()));
    }

    @Override
    public Optional<IMetricId> getMetricId(final String name)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'getMetricId' must not be empty";
        return Optional.ofNullable(m_softwareSystem.getMetricIds().get(name));
    }

    @Override
    public List<IMetricId> getMetricIds()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getMetricIds().values()));
    }

    @Override
    public Optional<IMetricValue> getMetricValue(final String metricId)
    {
        final Optional<IMetricId> id = getMetricId(metricId);
        if (!id.isPresent())
        {
            return Optional.empty();
        }
        return m_softwareSystem.getSystemMetricValue(id.get());
    }

    @Override
    public Map<INamedElement, IMetricValue> getMetricValues(final String levelName, final String metricIdName)
    {
        assert levelName != null && levelName.length() > 0 : "Parameter 'levelName' of method 'getMetricValues' must not be empty";
        assert metricIdName != null && metricIdName.length() > 0 : "Parameter 'metricIdName' of method 'getMetricValues' must not be empty";

        return m_softwareSystem.getMetricValues(levelName, metricIdName);
    }

    @Override
    public List<IMetricLevel> getMetricLevels()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_softwareSystem.getAllMetricLevels().values()));
    }

    @Override
    public Optional<IMetricLevel> getMetricLevel(final String systemLevel)
    {
        return Optional.ofNullable(m_softwareSystem.getMetricLevels().get(systemLevel));
    }
}
