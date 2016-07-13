/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;

final class SystemInfoProcessorImpl implements ISystemInfoProcessor
{
    private final SoftwareSystemImpl softwareSystem;

    public SystemInfoProcessorImpl(final SoftwareSystemImpl system)
    {
        assert system != null : "Parameter 'system' of method 'SystemInfoProcessorImpl' must not be null";
        softwareSystem = system;
    }

    @Override
    public String getBaseDirectory()
    {
        return softwareSystem.getBaseDir();
    }

    @Override
    public List<IIssue> getIssues(final Predicate<IIssue> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(softwareSystem.getIssues().values().stream().flatMap(list -> list.stream())
                    .collect(Collectors.toList()));
        }

        return Collections.unmodifiableList(softwareSystem.getIssues().values().stream().flatMap(list -> list.stream()).filter(filter)
                .collect(Collectors.toList()));
    }

    @Override
    public List<IThresholdViolationIssue> getThresholdViolationIssues(final Predicate<IThresholdViolationIssue> filter)
    {
        if (filter == null)
        {
            return softwareSystem.getIssues().entrySet().stream()
                    .filter(entry -> entry.getKey().getCategory().getName().equals("ThresholdViolation")).flatMap(entry -> entry.getValue().stream())
                    .map(issue -> (IThresholdViolationIssue) issue).collect(Collectors.toList());
        }
        return softwareSystem.getIssues().entrySet().stream().filter(entry -> entry.getKey().getCategory().getName().equals("ThresholdViolation"))
                .flatMap(entry -> entry.getValue().stream()).map(issue -> (IThresholdViolationIssue) issue).filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public List<IResolution> getResolutions(final Predicate<IResolution> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                    .collect(Collectors.toList()));
        }

        return Collections.unmodifiableList(softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                .filter(filter).collect(Collectors.toList()));
    }

    @Override
    public Optional<IMetricId> getMetricId(final IMetricLevel level, final String metricId)
    {
        return softwareSystem.getMetricIdsForLevel(level).stream().filter(id -> id.getName().equals(metricId)).findAny();
    }

    @Override
    public Optional<IMetricValue> getMetricValueForElement(final IMetricId metricId, final IMetricLevel level, final String fqName)
    {
        return softwareSystem.getMetricValueForElement(metricId, level, fqName);
    }

    @Override
    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'getMetricIdsForLevel' must not be null";
        return softwareSystem.getMetricIdsForLevel(level);
    }

    @Override
    public List<IIssueProvider> getIssueProviders()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getIssueProviders().values()));
    }

    @Override
    public List<IIssueType> getIssueTypes()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getIssueTypes().values()));
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
        return getIssues(issue -> issue instanceof ICycleGroup).stream().map(issue -> (ICycleGroup) issue).filter(filter2)
                .collect(Collectors.toList());
    }

    @Override
    public List<IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getAnalyzers().values()));
    }

    @Override
    public List<IFeature> getFeatures()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getFeatures().values()));
    }

    @Override
    public List<IMetricCategory> getMetricCategories()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getMetricCategories().values()));
    }

    @Override
    public List<IMetricProvider> getMetricProviders()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getMetricProviders().values()));
    }

    @Override
    public Optional<IMetricId> getMetricId(final String name)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'getMetricId' must not be empty";
        return Optional.ofNullable(softwareSystem.getMetricIds().get(name));
    }

    @Override
    public List<IMetricId> getMetricIds()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getMetricIds().values()));
    }

    @Override
    public Optional<IMetricValue> getMetricValue(final String metricId)
    {
        final Optional<IMetricId> id = getMetricId(metricId);
        if (!id.isPresent())
        {
            return Optional.empty();
        }
        return softwareSystem.getSystemMetricValue(id.get());
    }

    @Override
    public Map<INamedElement, IMetricValue> getMetricValues(final String levelName, final String metricIdName)
    {
        assert levelName != null && levelName.length() > 0 : "Parameter 'levelName' of method 'getMetricValues' must not be empty";
        assert metricIdName != null && metricIdName.length() > 0 : "Parameter 'metricIdName' of method 'getMetricValues' must not be empty";

        return softwareSystem.getMetricValues(levelName, metricIdName);
    }

    @Override
    public List<IMetricLevel> getMetricLevels()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getAllMetricLevels().values()));
    }

    @Override
    public Optional<IMetricLevel> getMetricLevel(final String systemLevel)
    {
        return Optional.ofNullable(softwareSystem.getMetricLevels().get(systemLevel));
    }
}