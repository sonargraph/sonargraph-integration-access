/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.ILogicalElement;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IPhysicalRecursiveElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;

final class ModuleInfoProcessorImpl implements IModuleInfoProcessor
{
    private final ModuleImpl module;
    private final ISystemInfoProcessor systemInfoProcessor;

    public ModuleInfoProcessorImpl(final SoftwareSystemImpl softwareSystem, final ModuleImpl module)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'ModuleInfoProcessorImpl' must not be null";
        assert module != null : "Parameter 'module' of method 'ModuleInfoProcessorImpl' must not be null";

        this.systemInfoProcessor = new SystemInfoProcessorImpl(softwareSystem);
        this.module = module;
    }

    @Override
    public String getBaseDirectory()
    {
        return systemInfoProcessor.getBaseDirectory();
    }

    @Override
    public List<IIssue> getIssues(final Predicate<IIssue> filter)
    {
        final List<IIssue> systemIssues = systemInfoProcessor.getIssues(filter);
        return Collections.unmodifiableList(systemIssues.stream().filter(this::isModuleElementOriginOfIssue).collect(Collectors.toList()));
    }

    @Override
    public List<IThresholdViolationIssue> getThresholdViolationIssues(final Predicate<IThresholdViolationIssue> filter)
    {
        final List<IThresholdViolationIssue> systemIssues = systemInfoProcessor.getThresholdViolationIssues(filter);
        return Collections.unmodifiableList(systemIssues.stream().filter(this::isModuleElementOriginOfIssue).collect(Collectors.toList()));
    }

    private boolean isModuleElementOriginOfIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'isModuleElementInvolved' must not be null";

        if (issue instanceof IElementIssue)
        {
            final List<INamedElement> element = ((IElementIssue) issue).getAffectedElements();
            return element.stream().anyMatch(this::isElementContainedInModule);
        }
        if (issue instanceof IDependencyIssue)
        {
            final IDependencyIssue dependencyIssue = (IDependencyIssue) issue;
            return isElementContainedInModule(dependencyIssue.getFrom());
        }
        return true;
    }

    @Override
    public boolean isElementContainedInModule(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'isElementContainedInModule' must not be null";
        final Optional<? extends INamedElement> namedElementOpt = element.getOriginalLocation();
        if (namedElementOpt.isPresent())
        {
            return module.hasElement(namedElementOpt.get());
        }
        return module.hasElement(element);
    }

    private INamedElement getElementContainedInModule(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'getElementContainedInModule' must not be null";
        final Optional<? extends INamedElement> namedElementOpt = element.getOriginalLocation();
        if (namedElementOpt.isPresent())
        {
            if (module.hasElement(namedElementOpt.get()))
            {
                return namedElementOpt.get();
            }
        }
        if (module.hasElement(element))
        {
            return element;
        }
        return null;
    }

    @Override
    public List<IResolution> getResolutions(final Predicate<IResolution> filter)
    {
        final List<IResolution> systemResolutions = systemInfoProcessor.getResolutions(filter);

        final List<IResolution> moduleResolutions = new ArrayList<>();
        for (final IResolution next : systemResolutions)
        {
            if (next.getIssues().stream().filter(this::isModuleElementOriginOfIssue).findAny().isPresent())
            {
                moduleResolutions.add(next);
            }
        }
        return Collections.unmodifiableList(moduleResolutions);
    }

    @Override
    public IResolution getResolution(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'getResolution' must not be null";
        return systemInfoProcessor.getResolution(issue);
    }

    @Override
    public Optional<IMetricId> getMetricId(final IMetricLevel level, final String metricId)
    {
        return module.getMetricIdsForLevel(level).stream().filter(id -> id.getName().equals(metricId)).findAny();
    }

    @Override
    public Optional<IMetricValue> getMetricValueForElement(final IMetricId metricId, final IMetricLevel level, final String fqName)
    {
        return module.getMetricValueForElement(metricId, level, fqName);
    }

    @Override
    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'getMetricIdsForLevel' must not be null";
        return module.getMetricIdsForLevel(level);
    }

    @Override
    public Map<ISourceFile, Map<IResolution, List<IIssue>>> getIssuesForResolutionsForSourceFiles(final Predicate<IResolution> filter)
    {
        final Map<ISourceFile, Map<IResolution, List<IIssue>>> sourceFileToResolutionMap = new HashMap<>();
        for (final IResolution resolution : systemInfoProcessor.getResolutions(filter))
        {
            final Map<ISourceFile, List<IIssue>> issuesToSourceFiles = mapIssuesToSourceFiles(resolution.getIssues());
            for (final Map.Entry<ISourceFile, List<IIssue>> next : issuesToSourceFiles.entrySet())
            {
                final ISourceFile sourceFile = next.getKey();
                Map<IResolution, List<IIssue>> resolutionToIssues = sourceFileToResolutionMap.get(sourceFile);
                if (resolutionToIssues == null)
                {
                    resolutionToIssues = new HashMap<>();
                    sourceFileToResolutionMap.put(sourceFile, resolutionToIssues);
                }
                resolutionToIssues.put(resolution, next.getValue());
            }
        }
        return sourceFileToResolutionMap;
    }

    @Override
    public Map<ISourceFile, List<IIssue>> getIssuesForSourceFiles(final Predicate<IIssue> filter)
    {
        return mapIssuesToSourceFiles(systemInfoProcessor.getIssues(filter));
    }

    private void addSourceForIssue(final Map<ISourceFile, List<IIssue>> resultMap, final IIssue issue, final INamedElement element)
    {
        assert resultMap != null : "Parameter 'resultMap' of method 'addSourceForIssue' must not be null";
        assert issue != null : "Parameter 'issue' of method 'addSourceForIssue' must not be null";
        assert element != null : "Parameter 'element' of method 'addSourceForIssue' must not be null";

        if (isElementContainedInModule(element))
        {
            final Optional<ISourceFile> source = module.getSourceForElement(element);
            if (source.isPresent())
            {
                final ISourceFile sourceFile = source.get();
                List<IIssue> issues = resultMap.get(sourceFile);
                if (issues == null)
                {
                    issues = new ArrayList<>();
                    resultMap.put(sourceFile, issues);
                }
                issues.add(issue);
            }
        }
    }

    private Map<ISourceFile, List<IIssue>> mapIssuesToSourceFiles(final List<IIssue> systemIssues)
    {
        assert systemIssues != null : "Parameter 'systemIssues' of method 'mapIssuesToSourceFiles' must not be null";

        final Map<ISourceFile, List<IIssue>> resultMap = new HashMap<>();
        for (final IIssue issue : systemIssues)
        {
            if (issue instanceof IElementIssue)
            {
                final List<INamedElement> elements = ((IElementIssue) issue).getAffectedElements();
                for (final INamedElement next : elements)
                {
                    addSourceForIssue(resultMap, issue, next);
                }
            }
            if (issue instanceof IDependencyIssue)
            {
                final IDependencyIssue dependencyIssue = (IDependencyIssue) issue;
                addSourceForIssue(resultMap, issue, dependencyIssue.getFrom());
            }
        }

        return Collections.unmodifiableMap(resultMap);
    }

    private void addDirectoryIssue(final String directory, final IIssue issue, final Map<String, List<IIssue>> resultMap)
    {
        assert directory != null && directory.length() > 0 : "Parameter 'directory' of method 'addDirectoryIssue' must not be empty";
        assert issue != null : "Parameter 'issue' of method 'addDirectoryIssue' must not be null";
        assert resultMap != null : "Parameter 'resultMap' of method 'addDirectoryIssue' must not be null";

        List<IIssue> issues = resultMap.get(directory);
        if (issues == null)
        {
            issues = new ArrayList<>();
            resultMap.put(directory, issues);
        }
        issues.add(issue);
    }

    private String concatenate(final String relativeRootDirectory, final String relativeDirectory)
    {
        assert relativeRootDirectory != null && relativeRootDirectory.length() > 0 : "Parameter 'relativeRootDirectory' of method 'concatenate' must not be empty";
        assert relativeDirectory != null && relativeDirectory.length() > 0 : "Parameter 'relativeDirectory' of method 'concatenate' must not be empty";

        String directory = relativeRootDirectory;
        if (relativeDirectory.startsWith("./"))
        {
            directory += relativeDirectory.substring(1);
        }
        else if (relativeDirectory.startsWith("."))
        {
            directory += relativeDirectory.substring(2);
        }
        else
        {
            directory += relativeDirectory;
        }
        return directory;
    }

    private List<INamedElement> getOrigins(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'getOrigins' must not be null";

        if (namedElement instanceof ILogicalElement)
        {
            final List<INamedElement> origins = new ArrayList<>();
            for (final INamedElement nextDerivedFrom : ((ILogicalElement) namedElement).getDerivedFrom())
            {
                origins.add(nextDerivedFrom);
            }

            return origins;
        }

        return Collections.singletonList(namedElement);
    }

    private void addDirectoryIssues(final IIssue issue, final INamedElement namedElement, final Map<String, List<IIssue>> resultMap)
    {
        assert issue != null : "Parameter 'issue' of method 'addDirectoryIssues' must not be null";
        assert namedElement != null : "Parameter 'namedElement' of method 'addDirectoryIssues' must not be null";
        assert resultMap != null : "Parameter 'resultMap' of method 'addDirectoryIssues' must not be null";

        for (final INamedElement nextOrigin : getOrigins(namedElement))
        {
            final INamedElement contained = getElementContainedInModule(nextOrigin);
            if (contained != null)
            {
                if (contained instanceof IRootDirectory)
                {
                    addDirectoryIssue(((IRootDirectory) contained).getRelativePath(), issue, resultMap);
                }
                else if (contained instanceof IPhysicalRecursiveElement)
                {
                    final IPhysicalRecursiveElement nextPhysicalRecursiveElement = (IPhysicalRecursiveElement) contained;
                    final Optional<String> relRootDirOpt = nextPhysicalRecursiveElement.getRelativeRootDirectory();
                    final Optional<String> relDirOpt = nextPhysicalRecursiveElement.getRelativeDirectory();
                    if (relRootDirOpt.isPresent() && relDirOpt.isPresent())
                    {
                        final String directory = concatenate(relRootDirOpt.get(), relDirOpt.get());
                        addDirectoryIssue(directory, issue, resultMap);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, List<IIssue>> getIssuesForDirectories(final Predicate<IIssue> filter)
    {
        final Map<String, List<IIssue>> resultMap = new HashMap<>();
        for (final IIssue nextIssue : systemInfoProcessor.getIssues(filter))
        {
            if (nextIssue instanceof IElementIssue)
            {
                final List<INamedElement> elements = ((IElementIssue) nextIssue).getAffectedElements();
                for (final INamedElement next : elements)
                {
                    addDirectoryIssues(nextIssue, next, resultMap);
                }
            }
            if (nextIssue instanceof IDependencyIssue)
            {
                final INamedElement from = ((IDependencyIssue) nextIssue).getFrom();
                addDirectoryIssues(nextIssue, from, resultMap);
            }
        }
        return Collections.unmodifiableMap(resultMap);
    }

    @Override
    public Map<INamedElement, IMetricValue> getMetricValues(final String levelName, final String metricIdName)
    {
        return module.getMetricValues(levelName, metricIdName);
    }

    @Override
    public List<IMetricLevel> getMetricLevels()
    {
        return Collections.unmodifiableList(new ArrayList<>(module.getMetricLevels().values()));
    }

    @Override
    public Optional<IMetricLevel> getMetricLevel(final String level)
    {
        assert level != null && level.length() > 0 : "Parameter 'level' of method 'getMetricLevel' must not be empty";
        return Optional.ofNullable(module.getMetricLevels().get(level));
    }

    @Override
    public Optional<IMetricValue> getMetricValue(final String metricName)
    {
        return Optional.ofNullable(module.getMetricValues(IMetricLevel.MODULE, metricName).get(module));
    }
}