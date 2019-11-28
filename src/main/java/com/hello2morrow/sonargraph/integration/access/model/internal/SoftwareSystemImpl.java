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
import com.hello2morrow.sonargraph.integration.access.model.AnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzerConfiguration;
import com.hello2morrow.sonargraph.integration.access.model.IComponentFilter;
import com.hello2morrow.sonargraph.integration.access.model.IExternal;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IFilter;
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
import com.hello2morrow.sonargraph.integration.access.model.IPluginExternal;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class SoftwareSystemImpl extends NamedElementContainerImpl implements ISoftwareSystem
{
    private static final long serialVersionUID = -4666348701032432245L;

    private final Map<String, ModuleImpl> modules = new LinkedHashMap<>();
    private final Map<String, ExternalImpl> externals = new LinkedHashMap<>();
    private final Map<String, PluginExternalImpl> pluginExternals = new LinkedHashMap<>();
    private final Map<String, IIssueProvider> issueProviders = new HashMap<>();
    private final Map<String, IIssueType> issueTypes = new HashMap<>();
    private final Map<IIssueType, List<IIssue>> issueMap = new HashMap<>();
    private final Map<String, IAnalyzer> analyzerMap = new HashMap<>();
    private final Map<String, IPlugin> pluginMap = new HashMap<>();
    private final Map<String, IFeature> featuresMap = new HashMap<>();

    //Special analyzer configuration entries
    private final List<String> duplicateCodeConfigurationEntries = new ArrayList<>();
    private final List<String> scriptRunnerConfigurationEntries = new ArrayList<>();
    private final List<String> architectureCheckConfigurationEntries = new ArrayList<>();
    private final List<IMetricThreshold> thresholds = new ArrayList<>();

    //Generic analyzer configuration entries
    private final Map<String, IAnalyzerConfiguration> analyzerIdToConfigurationMap = new HashMap<>();

    //Plugin configuration entries
    private final Map<String, IPluginConfiguration> pluginIdToConfigurationMap = new HashMap<>();

    private final Map<ResolutionType, ArrayList<IResolution>> resolutionMap = new EnumMap<>(ResolutionType.class);
    private final Map<IIssue, IResolution> issueToResolution = new HashMap<>();
    private final Map<NamedElementImpl, SourceFileImpl> namedElementToSourceFile = new HashMap<>();

    private final String systemId;
    private final String path;
    private final String version;
    private final String virtualModel;
    private final long timestamp;
    private String baseDir;
    private final AnalyzerExecutionLevel analyzerExecutionLevel;

    private IFilter workspaceFilter;
    private IComponentFilter productionCodeFilter;
    private IComponentFilter issueFilter;

    private int numberOfIssues = 0;

    private final Map<String, String> metaData = new HashMap<>();

    public SoftwareSystemImpl(final String kind, final String presentationKind, final String systemId, final String name, final String description,
            final String path, final String version, final long timestamp, final String virtualModel,
            final AnalyzerExecutionLevel analyzerExecutionLevel)
    {
        this(kind, presentationKind, systemId, name, description, path, null, version, timestamp, virtualModel, analyzerExecutionLevel);
    }

    public SoftwareSystemImpl(final String kind, final String presentationKind, final String systemId, final String name, final String description,
            final String path, final String baseDir, final String version, final long timestamp, final String virtualModel,
            final AnalyzerExecutionLevel analyzerExecutionLevel)
    {
        super(kind, presentationKind, name, name, name, description, new MetaDataAccessImpl(path, systemId, version, timestamp),
                new NamedElementRegistry());

        assert systemId != null && systemId.length() > 0 : "Parameter 'systemId' of method 'SoftwareSystem' must not be empty";
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'SoftwareSystem' must not be empty";
        //baseDir might be null
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'SoftwareSystem' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' of method 'SoftwareSystem' must be > 0";
        assert virtualModel != null && virtualModel.length() > 0 : "Parameter 'virtualModel' of method 'SoftwareSystemImpl' must not be empty";
        assert analyzerExecutionLevel != null : "Parameter 'analyzerExecutionLevel' of method 'SoftwareSystemImpl' must not be null";

        this.systemId = systemId;
        this.path = path;

        if (baseDir == null)
        {
            int lastIndexOf = path.lastIndexOf('/');
            if (lastIndexOf == -1)
            {
                lastIndexOf = path.lastIndexOf('\\');
            }
            assert lastIndexOf != -1 : "Invalid path for system file: " + path;
            this.baseDir = Utility.convertPathToUniversalForm(path.substring(0, lastIndexOf));
        }
        else
        {
            this.baseDir = baseDir;
        }

        this.version = version;
        this.timestamp = timestamp;
        this.virtualModel = virtualModel;
        this.analyzerExecutionLevel = analyzerExecutionLevel;
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

    public void setWorkspaceFilter(final IFilter filter)
    {
        assert filter != null : "Parameter 'filter' of method 'addWorkspaceFilter' must not be null";
        workspaceFilter = filter;
    }

    @Override
    public Optional<IFilter> getWorkspaceFilter()
    {
        return Optional.ofNullable(workspaceFilter);
    }

    public void setWorkspaceFileFilter(final IFilter filter)
    {
        assert filter != null : "Parameter 'filter' of method 'addWorkspaceFileFilter' must not be null";
        workspaceFilter = filter;
    }

    public void setProductionCodeFilter(final IComponentFilter filter)
    {
        assert filter != null : "Parameter 'filter' of method 'setProductionCodeFilter' must not be null";
        productionCodeFilter = filter;
    }

    @Override
    public Optional<IComponentFilter> getProductionCodeFilter()
    {
        return Optional.ofNullable(productionCodeFilter);
    }

    public void setIssueFilter(final IComponentFilter filter)
    {
        assert filter != null : "Parameter 'filter' of method 'setIssueFilter' must not be null";
        issueFilter = filter;
    }

    @Override
    public Optional<IComponentFilter> getIssueFilter()
    {
        return Optional.ofNullable(issueFilter);
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

    public void addPluginExternal(final PluginExternalImpl pluginExternalImpl)
    {
        assert pluginExternalImpl != null : "Parameter 'pluginExternalImpl' of method 'addPluginExternal' must not be null";
        pluginExternals.put(pluginExternalImpl.getFqName(), pluginExternalImpl);
    }

    @Override
    public Map<String, IPluginExternal> getPluginExternals()
    {
        final Map<String, IPluginExternal> map = new LinkedHashMap<>();
        pluginExternals.values().stream().forEach((final IPluginExternal external) -> map.put(external.getName(), external));
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

    public void addPlugin(final IPlugin plugin)
    {
        assert plugin != null : "Parameter 'plugin' of method 'addPlugin' must not be null";
        assert !pluginMap.containsKey(plugin.getName()) : "Plugin '" + plugin.getName() + "' has already been added";
        pluginMap.put(plugin.getName(), plugin);
    }

    @Override
    public AnalyzerExecutionLevel getAnalyzerExecutionLevel()
    {
        return analyzerExecutionLevel;
    }

    @Override
    public Map<String, IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableMap(analyzerMap);
    }

    @Override
    public Map<String, IPlugin> getPlugins()
    {
        return Collections.unmodifiableMap(pluginMap);
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

    public void setBaseDir(final String baseDirectory)
    {
        assert baseDirectory != null && baseDirectory.length() > 0 : "Parameter 'baseDirectory' of method 'setBaseDir' must not be empty";
        baseDir = baseDirectory;
    }

    @Override
    public Map<String, String> getMetaData()
    {
        return Collections.unmodifiableMap(metaData);
    }

    public String addMetaData(final String key, final String value)
    {
        assert key != null && key.length() > 0 : "Parameter 'key' of method 'addMetaData' must not be empty";
        assert value != null : "Parameter 'value' of method 'addMetaData' must not be null";

        return metaData.put(key, value);
    }

    public Map<String, IAnalyzerConfiguration> getAnalyzerConfigurations()
    {
        return Collections.unmodifiableMap(analyzerIdToConfigurationMap);
    }

    public void addAnalyzerConfiguration(final IAnalyzerConfiguration configuration)
    {
        assert configuration != null : "Parameter 'configuration' of method 'addAnalyzerConfiguration' must not be null";
        analyzerIdToConfigurationMap.put(configuration.getName(), configuration);
    }

    public void addPluginConfiguration(final PluginConfigurationImpl pluginConfiguration)
    {
        assert pluginConfiguration != null : "Parameter 'pluginConfiguration' of method 'addPluginConfiguration' must not be null";
        pluginIdToConfigurationMap.put(pluginConfiguration.getName(), pluginConfiguration);
    }

    public Map<String, IPluginConfiguration> getPluginConfigurations()
    {
        return Collections.unmodifiableMap(pluginIdToConfigurationMap);
    }
}