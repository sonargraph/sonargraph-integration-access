/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzerConfiguration;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IPluginConfiguration;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ISystemFile;
import com.hello2morrow.sonargraph.integration.access.model.ISystemFileElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
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
    public ISoftwareSystem getSoftwareSystem()
    {
        return softwareSystem;
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
            final List<IIssue> issueList = Collections
                    .unmodifiableList(softwareSystem.getIssues().values().stream().flatMap(list -> list.stream()).collect(toList()));
            return issueList;
        }

        return Collections
                .unmodifiableList(softwareSystem.getIssues().values().stream().flatMap(list -> list.stream()).filter(filter).collect(toList()));
    }

    @Override
    public List<IIssue> getIssues(final Set<IIssue> issuesToSelectFrom, final Predicate<IIssue> filter)
    {
        if (filter == null)
        {
            Collections.unmodifiableList(new ArrayList<>(issuesToSelectFrom));
        }

        return Collections.unmodifiableList(issuesToSelectFrom.stream().filter(filter).collect(toList()));
    }

    @Override
    public boolean hasIssue(final Predicate<IIssue> filter)
    {
        if (filter == null)
        {
            throw new IllegalArgumentException("Missing mandatory argument 'filter'");
        }
        return softwareSystem.getIssues().entrySet().stream().flatMap(entry -> entry.getValue().stream()).anyMatch(filter);
    }

    @Override
    public List<IThresholdViolationIssue> getThresholdViolationIssues(final Predicate<IThresholdViolationIssue> filter)
    {
        if (filter == null)
        {
            return softwareSystem.getIssues().entrySet().stream().filter(entry -> entry.getKey().getCategory().getName().equals("ThresholdViolation"))
                    .flatMap(entry -> entry.getValue().stream()).map(issue -> (IThresholdViolationIssue) issue).collect(toList());
        }
        return softwareSystem.getIssues().entrySet().stream().filter(entry -> entry.getKey().getCategory().getName().equals("ThresholdViolation"))
                .flatMap(entry -> entry.getValue().stream()).map(issue -> (IThresholdViolationIssue) issue).filter(filter).collect(toList());
    }

    @Override
    public List<IResolution> getResolutions(final Predicate<IResolution> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(
                    softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream()).collect(toList()));
        }

        return Collections.unmodifiableList(
                softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream()).filter(filter).collect(toList()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IResolution> List<T> getResolutions(final Predicate<T> filter, final Class<T> resolutionClass)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                    .filter(r -> resolutionClass.isAssignableFrom(r.getClass())).map(r -> (T) r).collect(toList()));
        }

        return Collections.unmodifiableList(softwareSystem.getResolutions().values().stream().flatMap(resolutions -> resolutions.stream())
                .filter(r -> resolutionClass.isAssignableFrom(r.getClass())).map(r -> (T) r).filter(filter).collect(toList()));
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
    public List<IIssueCategory> getIssueCategories()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getIssueCategories().values()));
    }

    @Override
    public List<ICycleGroupIssue> getCycleGroups(final Predicate<ICycleGroupIssue> filter)
    {
        final Predicate<ICycleGroupIssue> filter2;
        if (filter != null)
        {
            filter2 = filter;
        }
        else
        {
            filter2 = (final ICycleGroupIssue group) -> true;
        }
        return getIssues(issue -> issue instanceof ICycleGroupIssue).stream().map(issue -> (ICycleGroupIssue) issue).filter(filter2)
                .collect(toList());
    }

    @Override
    public List<IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getAnalyzers().values()));
    }

    @Override
    public List<IPlugin> getPlugins()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getPlugins().values()));
    }

    @Override
    public List<IFeature> getFeatures()
    {
        return Collections.unmodifiableList(new ArrayList<>(softwareSystem.getFeatures().values()));
    }

    @Override
    public List<String> getDuplicateCodeConfigurationEntries()
    {
        return softwareSystem.getDuplicateCodeConfigurationEntries();
    }

    @Override
    public List<String> getScriptRunnerConfigurationEntries()
    {
        return softwareSystem.getScriptRunnerConfigurationEntries();
    }

    @Override
    public List<String> getArchitectureCheckConfigurationEntries()
    {
        return softwareSystem.getArchitectureCheckConfigurationEntries();
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

    @Override
    public Map<String, IModule> getModules()
    {
        return softwareSystem.getModules();
    }

    @Override
    public List<IMetricThreshold> getMetricThresholds()
    {
        return softwareSystem.getMetricThresholds();
    }

    @Override
    public List<String> getElementKinds()
    {
        final Set<String> elementKinds = new HashSet<>(softwareSystem.getElementKinds());
        for (final IModule next : softwareSystem.getModules().values())
        {
            elementKinds.addAll(next.getElementKinds());
        }
        return new ArrayList<>(elementKinds);
    }

    @Override
    public IResolution getResolution(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'getResolution' must not be null";
        return softwareSystem.getResolution(issue);
    }

    @Override
    public IModuleInfoProcessor createModuleInfoProcessor(final IModule module)
    {
        assert module != null : "Parameter 'module' of method 'createModuleInfoProcessor' must not be null";
        assert module instanceof ModuleImpl : "Unexpected class in method 'createModuleInfoProcessor': " + module.getClass();
        return new ModuleInfoProcessorImpl(this, (ModuleImpl) module);
    }

    @Override
    public Map<String, IAnalyzerConfiguration> getAnalyzerConfigurations()
    {
        return softwareSystem.getAnalyzerConfigurations();
    }

    @Override
    public Map<String, IPluginConfiguration> getPluginConfigurations()
    {
        return softwareSystem.getPluginConfigurations();
    }

    @Override
    public List<ISystemFile> getSystemFiles()
    {
        return softwareSystem.getSystemFiles();
    }

    @Override
    public List<ISystemFileElement> getSystemFileElements()
    {
        return softwareSystem.getSystemFileElements();
    }
}