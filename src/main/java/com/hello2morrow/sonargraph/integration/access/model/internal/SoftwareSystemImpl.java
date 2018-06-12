/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IExternal;
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
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class SoftwareSystemImpl extends NamedElementContainerImpl implements ISoftwareSystem
{
    private static final long serialVersionUID = -4666348701032432246L;

    private final Map<String, ModuleImpl> modules = new LinkedHashMap<>();
    private final Map<String, ExternalImpl> externals = new LinkedHashMap<>();
    private final Map<String, IIssueProvider> issueProviders = new HashMap<>();
    private final Map<String, IIssueType> issueTypes = new HashMap<>();
    private final Map<IIssueType, List<IIssue>> issueMap = new HashMap<>();
    private final Map<String, IAnalyzer> analyzerMap = new HashMap<>();
    private final Map<String, IFeature> featuresMap = new HashMap<>();
    private final List<String> duplicateCodeConfigurationEntries = new ArrayList<>();
    private final List<String> scriptRunnerConfigurationEntries = new ArrayList<>();
    private final List<String> architectureCheckConfigurationEntries = new ArrayList<>();
    private final List<IMetricThreshold> thresholds = new ArrayList<>();
    private final Map<ResolutionType, ArrayList<IResolution>> resolutionMap = new EnumMap<>(ResolutionType.class);
    private final Map<IIssue, IResolution> issueToResolution = new HashMap<>();
    private final Map<NamedElementImpl, SourceFileImpl> namedElementToSourceFile = new HashMap<>();

    private final String systemId;
    private final String path;
    private final String version;
    private final String virtualModel;
    private final long timestamp;
    private final String baseDir;

    private int numberOfIssues = 0;

    public SoftwareSystemImpl(final String kind, final String presentationKind, final String systemId, final String name, final String description,
            final String path, final String version, final long timestamp, final String virtualModel)
    {
        super(kind, presentationKind, name, name, name, description, new MetaDataAccessImpl(path, systemId, version, timestamp),
                new NamedElementRegistry());

        assert systemId != null && systemId.length() > 0 : "Parameter 'systemId' of method 'SoftwareSystem' must not be empty";
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'SoftwareSystem' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'SoftwareSystem' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' of method 'SoftwareSystem' must be > 0";
        assert virtualModel != null && virtualModel.length() > 0 : "Parameter 'virtualModel' of method 'SoftwareSystemImpl' must not be empty";

        this.systemId = systemId;
        this.path = path;

        int lastIndexOf = path.lastIndexOf('/');
        if (lastIndexOf == -1)
        {
            lastIndexOf = path.lastIndexOf('\\');
        }
        assert lastIndexOf != -1 : "Invalid path for system file: " + path;
        this.baseDir = Utility.convertPathToUniversalForm(path.substring(0, lastIndexOf));
        this.version = version;
        this.timestamp = timestamp;
        this.virtualModel = virtualModel;
    }

    @Override
    public String getSystemId()
    {
        return systemId;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public String getBaseDir()
    {
        return baseDir;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public Map<String, IModule> getModules()
    {
        final Map<String, IModule> map = new LinkedHashMap<>();
        modules.values().stream().forEach((final IModule module) -> map.put(module.getName(), module));
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Optional<IModule> getModule(final String simpleName)
    {
        assert simpleName != null && simpleName.length() > 0 : "Parameter 'name' of method 'getModule' must not be empty";
        final String key = "Workspace:" + simpleName;
        if (modules.containsKey(key))
        {
            return Optional.of(modules.get(key));
        }

        return Optional.empty();
    }

    public void addModule(final ModuleImpl moduleImpl)
    {
        assert moduleImpl != null : "Parameter 'moduleImpl' of method 'addModule' must not be null";
        modules.put(moduleImpl.getFqName(), moduleImpl);
    }

    public void addExternal(final ExternalImpl externalImpl)
    {
        assert externalImpl != null : "Parameter 'externalImpl' of method 'addExternal' must not be null";
        externals.put(externalImpl.getFqName(), externalImpl);
    }

    @Override
    public Map<String, IExternal> getExternals()
    {
        final Map<String, IExternal> map = new LinkedHashMap<>();
        externals.values().stream().forEach((final IExternal external) -> map.put(external.getName(), external));
        return Collections.unmodifiableMap(map);
    }

    public void addIssueProvider(final IIssueProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addIssueProvider' must not be null";
        assert !issueProviders.containsKey(provider.getName()) : "IssueProvider '" + provider.getName() + "' has already been added";
        issueProviders.put(provider.getName(), provider);
    }

    /**
     * @return a map with the name of the provider as key and the implementation as value.
     */
    public Map<String, IIssueProvider> getIssueProviders()
    {
        return Collections.unmodifiableMap(issueProviders);
    }

    /**
     * @return a map with the name of the issue type as key and the implementation as value.
     */
    public Map<String, IIssueType> getIssueTypes()
    {
        return Collections.unmodifiableMap(issueTypes);
    }

    public void addIssueType(final IIssueType issueType)
    {
        assert issueType != null : "Parameter 'issueType' of method 'addIssueType' must not be null";
        assert !issueTypes.containsKey(issueType.getName()) : "issueType '" + issueType + "has already been added";
        issueTypes.put(issueType.getName(), issueType);
        issueMap.put(issueType, new ArrayList<>());
    }

    public void addIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'addIssue' must not be null";
        assert issueMap.containsKey(issue.getIssueType()) : "issueType '" + issue.getIssueType() + "' has not beend added";
        final List<IIssue> issues = issueMap.get(issue.getIssueType());
        assert issues != null : "'issues' of method 'addIssue' must not be null";
        issues.add(issue);
    }

    public Map<IIssueType, List<IIssue>> getIssues()
    {
        return Collections.unmodifiableMap(issueMap);
    }

    public void setNumberOfIssues(final int numberOfIssues)
    {
        assert numberOfIssues >= 0 : "Parameter 'numberOfIssues' of method 'setNumberOfIssues' must be >= 0";
        this.numberOfIssues = numberOfIssues;
    }

    public int getNumberOfIssues()
    {
        assert numberOfIssues == (issueMap.values().stream().mapToInt(i -> i.size()).sum()) : "Number of issues does not match";
        return numberOfIssues;
    }

    public void addMetricId(final IMetricId metricId)
    {
        assert metricId != null : "Parameter 'metricId' of method 'addMetricId' must not be null";
        getMetaDataAccess().addMetricId(metricId);
    }

    public Map<String, IMetricId> getMetricIds()
    {
        return getMetaDataAccess().getMetricIds();
    }

    public void addAnalyzer(final IAnalyzer analyzer)
    {
        assert analyzer != null : "Parameter 'analyzer' of method 'addAnalyzer' must not be null";
        assert !analyzerMap.containsKey(analyzer.getName()) : "Analyzer '" + analyzer.getName() + "' has already been added";
        analyzerMap.put(analyzer.getName(), analyzer);
    }

    public Map<String, IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableMap(analyzerMap);
    }

    public void addFeature(final IFeature feature)
    {
        assert feature != null : "Parameter 'feature' of method 'addFeature' must not be null";
        assert !featuresMap.containsKey(feature.getName()) : "Feature '" + feature.getName() + "' has already been added";
        featuresMap.put(feature.getName(), feature);
    }

    public Map<String, IFeature> getFeatures()
    {
        return Collections.unmodifiableMap(featuresMap);
    }

    public void addDuplicateCodeConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addDuplicateCodeConfigurationEntry' must not be empty";
        duplicateCodeConfigurationEntries.add(entry);
    }

    public List<String> getDuplicateCodeConfigurationEntries()
    {
        return Collections.unmodifiableList(duplicateCodeConfigurationEntries);
    }

    public void addScriptRunnerConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addScriptRunnerConfigurationEntry' must not be empty";
        scriptRunnerConfigurationEntries.add(entry);
    }

    public List<String> getScriptRunnerConfigurationEntries()
    {
        return Collections.unmodifiableList(scriptRunnerConfigurationEntries);
    }

    public void addArchitectureCheckConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addArchitectureCheckConfigurationEntry' must not be empty";
        architectureCheckConfigurationEntries.add(entry);
    }

    public List<String> getArchitectureCheckConfigurationEntries()
    {
        return Collections.unmodifiableList(architectureCheckConfigurationEntries);
    }

    public void addResolution(final IResolution resolution)
    {
        assert resolution != null : "Parameter 'resolution' of method 'addResolution' must not be null";

        final ArrayList<IResolution> resolutions;
        if (!resolutionMap.containsKey(resolution.getType()))
        {
            resolutions = new ArrayList<>();
            resolutionMap.put(resolution.getType(), resolutions);
        }
        else
        {
            resolutions = resolutionMap.get(resolution.getType());
        }

        resolutions.add(resolution);

        for (final IIssue nextIssue : resolution.getIssues())
        {
            issueToResolution.put(nextIssue, resolution);
        }
    }

    /**
     * @param issue the issue - must not be 'null'
     * @return the resolution or 'null' if the issue has no resolution
     */
    public IResolution getResolution(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'getResolution' must not be null";
        return issueToResolution.get(issue);
    }

    public final Optional<IMetricValue> getSystemMetricValue(final IMetricId metricId)
    {
        assert metricId != null : "Parameter 'metricId' of method 'getSystemMetricValue' must not be null";
        return super.getMetricValueForElement(metricId, getMetaDataAccess().getMetricLevels().get(IMetricLevel.SYSTEM), this.getName());
    }

    public void addMetricCategory(final IMetricCategory metricCategory)
    {
        assert metricCategory != null : "Parameter 'metricCategory' of method 'addMetricCategory' must not be null";
        getMetaDataAccess().addMetricCategory(metricCategory);
    }

    public Map<String, IMetricCategory> getMetricCategories()
    {
        return getMetaDataAccess().getMetricCategories();
    }

    @Override
    public Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> systemMetricLevel = new HashMap<>();
        systemMetricLevel.put(IMetricLevel.SYSTEM, getMetaDataAccess().getMetricLevel(IMetricLevel.SYSTEM));
        return Collections.unmodifiableMap(systemMetricLevel);
    }

    public void addMetricProvider(final IMetricProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addMetricProvider' must not be null";
        getMetaDataAccess().addMetricProvider(provider);
    }

    public Map<String, IMetricProvider> getMetricProviders()
    {
        return getMetaDataAccess().getMetricProviders();
    }

    public void addIssueCategory(final IIssueCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addIssueCategory' must not be null";
        getMetaDataAccess().addIssueCategory(category);
    }

    public Map<String, IIssueCategory> getIssueCategories()
    {
        return getMetaDataAccess().getIssueCategories();
    }

    @Override
    public String getVirtualModel()
    {
        return virtualModel;
    }

    public Map<ResolutionType, List<IResolution>> getResolutions()
    {
        return Collections.unmodifiableMap(resolutionMap);
    }

    public void addMetricThreshold(final MetricThreshold threshold)
    {
        thresholds.add(threshold);
    }

    public List<IMetricThreshold> getMetricThresholds()
    {
        return Collections.unmodifiableList(thresholds);
    }

    public void addSourceFile(final NamedElementImpl forNamedElement, final SourceFileImpl sourceFile)
    {
        assert forNamedElement != null : "Parameter 'forNamedElement' of method 'addSourceFile' must not be null";
        assert sourceFile != null : "Parameter 'sourceFile' of method 'addSourceFile' must not be null";
        final SourceFileImpl previous = namedElementToSourceFile.put(forNamedElement, sourceFile);
        assert previous == null : "'previous' of method 'addSourceFile' must be null";
    }

    @Override
    public Optional<SourceFileImpl> getSourceFile(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'getSourceFile' must not be null";
        return Optional.ofNullable(namedElementToSourceFile.get(namedElement));
    }
}