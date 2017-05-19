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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.FileUtility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue;
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
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class SoftwareSystemImpl extends NamedElementContainerImpl implements ISoftwareSystem
{
    private static final String SOFTWARE_SYSTEM = "SoftwareSystem";
    private final String systemId;
    private final String path;
    private String baseDir;
    private final String version;
    private final String virtualModel;
    private final long timestamp;
    private int numberOfIssues = 0;

    //TODO: Decide if the order of elements in the report is important for us.
    //Do we want to impose some default ordering / sorting?
    private final Map<String, IModule> modules = new LinkedHashMap<>();
    private final Map<String, IIssueProvider> issueProviders = new HashMap<>();
    private final Map<String, IIssueType> issueTypes = new HashMap<>();
    private final Map<IIssueType, List<IIssue>> issueMap = new HashMap<>();
    private final Map<String, IAnalyzer> analyzerMap = new HashMap<>();
    private final Map<String, IFeature> featuresMap = new HashMap<>();
    private final List<IMetricThreshold> m_thresholds = new ArrayList<>();
    private final Map<IAnalyzer, Map<String, ICycleGroup>> cycleGroups = new HashMap<>();
    private final Map<String, IDuplicateCodeBlockIssue> duplicateCodeBlockIssueMap = new HashMap<>();
    private final Map<ResolutionType, List<IResolution>> resolutionMap = new EnumMap<>(ResolutionType.class);

    public SoftwareSystemImpl(final String kind, final String presentationKind, final String systemId, final String name, final String description,
            final String path, final String version, final long timestamp, final String virtualModel)
    {
        super(kind, presentationKind, name, name, name, description);
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
        this.baseDir = FileUtility.convertPathToUniversalForm(path.substring(0, lastIndexOf));
        this.version = version;
        this.timestamp = timestamp;
        this.virtualModel = virtualModel;

        setMetricsAccess(new MetricsAccessImpl(path, systemId, version, timestamp));
        setElementRegistry(new ElementRegistryImpl());
        getElementRegistry().addElement(this);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getSystemId()
     */
    @Override
    public String getSystemId()
    {
        return systemId;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getPath()
     */
    @Override
    public String getPath()
    {
        return path;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getBaseDir()
     */
    @Override
    public String getBaseDir()
    {
        return baseDir;
    }

    public void setBaseDir(final String baseDirectory)
    {
        baseDir = baseDirectory;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getVersion()
     */
    @Override
    public String getVersion()
    {
        return version;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getTimestamp()
     */
    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getModules()
     */
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

    public void addModule(final ModuleImpl module)
    {
        assert module != null : "Parameter 'module' of method 'addModule' must not be null";
        modules.put(module.getFqName(), module);
        module.setMetricsAccess(getMetricsAccess());
        module.setElementRegistry(getElementRegistry());
        module.getElementRegistry().addElement(module);
    }

    @Override
    protected boolean acceptElementKind(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'acceptElementKind' must not be empty";
        return !SOFTWARE_SYSTEM.equals(elementKind);
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
        issueMap.get(issue.getIssueType()).add(issue);
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

        getMetricsAccess().addMetricId(metricId);
    }

    public Map<String, IMetricId> getMetricIds()
    {
        return getMetricsAccess().getMetricIds();
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

    public Map<String, ICycleGroup> getCycleGroups(final String analyzerId)
    {
        assert analyzerId != null && analyzerId.length() > 0 : "Parameter 'analyzerId' of method 'getCycleGroups' must not be empty";
        assert analyzerMap.containsKey(analyzerId) : "analyzerId '" + analyzerId + "' has not been added";

        final IAnalyzer analyzer = analyzerMap.get(analyzerId);

        assert cycleGroups.containsKey(analyzer) : "'" + analyzerId + "' has not been added for cycleGroups";
        return Collections.unmodifiableMap(cycleGroups.get(analyzer));
    }

    public void addCycleGroup(final ICycleGroup cycle)
    {
        assert cycle != null : "Parameter 'cycle' of method 'addCycleGroup' must not be null";

        final Map<String, ICycleGroup> cycleGroupMap;
        final IAnalyzer analyzer = cycle.getAnalyzer();
        assert analyzerMap.containsKey(analyzer.getName()) : "Analyzer '" + analyzer.getName() + "' has not been added";

        if (cycleGroups.containsKey(analyzer))
        {
            cycleGroupMap = cycleGroups.get(analyzer);
        }
        else
        {
            cycleGroupMap = new HashMap<>();
            cycleGroups.put(analyzer, cycleGroupMap);
        }

        assert !cycleGroupMap.containsKey(cycle.getName()) : "CycleGroup has already been added!";
        cycleGroupMap.put(cycle.getName(), cycle);
        addIssue(cycle);
    }

    public void addDuplicateCodeBlock(final IDuplicateCodeBlockIssue duplicate)
    {
        assert duplicate != null : "Parameter 'duplicate' of method 'addDuplicateCodeBlock' must not be null";
        assert !duplicateCodeBlockIssueMap.containsKey(duplicate.getName()) : "Duplicate has already been added";

        duplicateCodeBlockIssueMap.put(duplicate.getName(), duplicate);
        addIssue(duplicate);
    }

    public void addResolution(final IResolution resolution)
    {
        assert resolution != null : "Parameter 'resolution' of method 'addResolution' must not be null";

        final List<IResolution> resolutions;
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
    }

    public final Optional<IMetricValue> getSystemMetricValue(final IMetricId metricId)
    {
        assert metricId != null : "Parameter 'metricId' of method 'getSystemMetricValue' must not be null";
        return super.getMetricValueForElement(metricId, getMetricsAccess().getMetricLevels().get(IMetricLevel.SYSTEM), this.getName());
    }

    public void addMetricCategory(final IMetricCategory metricCategory)
    {
        assert metricCategory != null : "Parameter 'metricCategory' of method 'addMetricCategory' must not be null";
        getMetricsAccess().addMetricCategory(metricCategory);
    }

    public Map<String, IMetricCategory> getMetricCategories()
    {
        return getMetricsAccess().getMetricCategories();
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getMetricLevels()
     */
    @Override
    public Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> systemMetricLevel = new HashMap<>();
        systemMetricLevel.put(IMetricLevel.SYSTEM, getMetricsAccess().getMetricLevel(IMetricLevel.SYSTEM));
        return Collections.unmodifiableMap(systemMetricLevel);
    }

    public void addMetricProvider(final IMetricProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addMetricProvider' must not be null";
        getMetricsAccess().addMetricProvider(provider);
    }

    public Map<String, IMetricProvider> getMetricProviders()
    {
        return getMetricsAccess().getMetricProviders();
    }

    public void addIssueCategory(final IIssueCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addIssueCategory' must not be null";
        getMetricsAccess().addIssueCategory(category);
    }

    public Map<String, IIssueCategory> getIssueCategories()
    {
        return getMetricsAccess().getIssueCategories();
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
        m_thresholds.add(threshold);
    }

    public List<IMetricThreshold> getMetricThresholds()
    {
        return Collections.unmodifiableList(m_thresholds);
    }
}