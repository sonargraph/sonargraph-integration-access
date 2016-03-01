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
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public final class SoftwareSystemImpl extends NamedElementContainerImpl implements ISoftwareSystem
{
    private final String m_systemId;
    private final String m_path;
    private final String m_baseDir;
    private final String m_version;
    private final String m_virtualModel;
    private final long m_timestamp;
    private int m_numberOfIssues = 0;

    //TODO: Decide if the order of elements in the report is important for us.
    //Do we want to impose some default ordering / sorting?
    private final Map<String, IModule> m_modules = new LinkedHashMap<>();
    private final Map<String, IIssueProvider> m_issueProviders = new HashMap<>();
    private final Map<String, IIssueType> m_issueTypes = new HashMap<>();
    private final Map<IIssueType, List<IIssue>> m_issueMap = new HashMap<>();
    private final Map<String, IAnalyzer> m_analyzerMap = new HashMap<>();
    private final Map<String, IFeature> m_featuresMap = new HashMap<>();
    private final Map<IAnalyzer, Map<String, ICycleGroup>> m_cycleGroups = new HashMap<>();
    private final Map<String, IDuplicateCodeBlockIssue> m_duplicateCodeBlockIssueMap = new HashMap<>();
    private final Map<ResolutionType, List<IResolution>> m_resolutionMap = new HashMap<>();

    public SoftwareSystemImpl(final String kind, final String presentationKind, final String systemId, final String name, final String description,
            final String path, final String version, final long timestamp, final String virtualModel)
    {
        super(kind, presentationKind, name, name, name, description);
        assert systemId != null && systemId.length() > 0 : "Parameter 'systemId' of method 'SoftwareSystem' must not be empty";
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'SoftwareSystem' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'SoftwareSystem' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' of method 'SoftwareSystem' must be > 0";
        assert virtualModel != null && virtualModel.length() > 0 : "Parameter 'virtualModel' of method 'SoftwareSystemImpl' must not be empty";

        m_systemId = systemId;
        m_path = path;

        int lastIndexOf = m_path.lastIndexOf('/');
        if (lastIndexOf == -1)
        {
            lastIndexOf = m_path.lastIndexOf('\\');
        }
        assert lastIndexOf != -1 : "Invalid path for system file: " + m_path;
        m_baseDir = FileUtility.convertPathToUniversalForm(m_path.substring(0, lastIndexOf));
        m_version = version;
        m_timestamp = timestamp;
        m_virtualModel = virtualModel;

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
        return m_systemId;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getPath()
     */
    @Override
    public String getPath()
    {
        return m_path;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getBaseDir()
     */
    @Override
    public String getBaseDir()
    {
        return m_baseDir;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getVersion()
     */
    @Override
    public String getVersion()
    {
        return m_version;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getTimestamp()
     */
    @Override
    public long getTimestamp()
    {
        return m_timestamp;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem#getModules()
     */
    @Override
    public Map<String, IModule> getModules()
    {
        final Map<String, IModule> map = new LinkedHashMap<>();
        m_modules.values().stream().forEach((final IModule module) -> map.put(module.getName(), module));
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Optional<IModule> getModule(final String simpleName)
    {
        assert simpleName != null && simpleName.length() > 0 : "Parameter 'name' of method 'getModule' must not be empty";
        final String key = "Workspace:" + simpleName;
        if (m_modules.containsKey(key))
        {
            return Optional.of(m_modules.get(key));
        }

        return Optional.empty();
    }

    public void addModule(final ModuleImpl module)
    {
        assert module != null : "Parameter 'module' of method 'addModule' must not be null";
        m_modules.put(module.getFqName(), module);
        module.setMetricsAccess(getMetricsAccess());
        module.setElementRegistry(getElementRegistry());
        module.getElementRegistry().addElement(module);
    }

    @Override
    protected boolean acceptElementKind(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'acceptElementKind' must not be empty";
        return !elementKind.equals("SoftwareSystem");
    }

    public void addIssueProvider(final IIssueProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addIssueProvider' must not be null";
        assert !m_issueProviders.containsKey(provider.getName()) : "IssueProvider '" + provider.getName() + "' has already been added";

        m_issueProviders.put(provider.getName(), provider);
    }

    public Map<String, IIssueProvider> getIssueProviders()
    {
        return Collections.unmodifiableMap(m_issueProviders);
    }

    public Map<String, IIssueType> getIssueTypes()
    {
        return Collections.unmodifiableMap(m_issueTypes);
    }

    public void addIssueType(final IIssueType issueType)
    {
        assert issueType != null : "Parameter 'issueType' of method 'addIssueType' must not be null";
        assert !m_issueTypes.containsKey(issueType.getName()) : "issueType '" + issueType + "has already been added";

        m_issueTypes.put(issueType.getName(), issueType);
        m_issueMap.put(issueType, new ArrayList<>());
    }

    public void addIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'addIssue' must not be null";

        assert m_issueMap.containsKey(issue.getIssueType()) : "issueType '" + issue.getIssueType() + "' has not beend added";
        m_issueMap.get(issue.getIssueType()).add(issue);
    }

    public Map<IIssueType, List<IIssue>> getIssues()
    {
        return Collections.unmodifiableMap(m_issueMap);
    }

    public void setNumberOfIssues(final int numberOfIssues)
    {
        assert numberOfIssues >= 0 : "Parameter 'numberOfIssues' of method 'setNumberOfIssues' must be >= 0";
        m_numberOfIssues = numberOfIssues;
    }

    public int getNumberOfIssues()
    {
        assert m_numberOfIssues == (m_issueMap.values().stream().mapToInt(i -> i.size()).sum()) : "Number of issues does not match";
        return m_numberOfIssues;
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
        assert !m_analyzerMap.containsKey(analyzer.getName()) : "Analyzer '" + analyzer.getName() + "' has already been added";

        m_analyzerMap.put(analyzer.getName(), analyzer);
    }

    public Map<String, IAnalyzer> getAnalyzers()
    {
        return Collections.unmodifiableMap(m_analyzerMap);
    }

    public void addFeature(final IFeature feature)
    {
        assert feature != null : "Parameter 'feature' of method 'addFeature' must not be null";
        assert !m_featuresMap.containsKey(feature.getName()) : "Feature '" + feature.getName() + "' has already been added";

        m_featuresMap.put(feature.getName(), feature);
    }

    public Map<String, IFeature> getFeatures()
    {
        return Collections.unmodifiableMap(m_featuresMap);
    }

    public Map<String, ICycleGroup> getCycleGroups(final String analyzerId)
    {
        assert analyzerId != null && analyzerId.length() > 0 : "Parameter 'analyzerId' of method 'getCycleGroups' must not be empty";
        assert m_analyzerMap.containsKey(analyzerId) : "analyzerId '" + analyzerId + "' has not been added";

        final IAnalyzer analyzer = m_analyzerMap.get(analyzerId);

        assert m_cycleGroups.containsKey(analyzer) : "'" + analyzerId + "' has not been added for cycleGroups";
        return Collections.unmodifiableMap(m_cycleGroups.get(analyzer));
    }

    public void addCycleGroup(final ICycleGroup cycle)
    {
        assert cycle != null : "Parameter 'cycle' of method 'addCycleGroup' must not be null";

        final Map<String, ICycleGroup> cycleGroupMap;
        final IAnalyzer analyzer = cycle.getAnalyzer();
        assert m_analyzerMap.containsKey(analyzer.getName()) : "Analyzer '" + analyzer.getName() + "' has not been added";

        if (m_cycleGroups.containsKey(analyzer))
        {
            cycleGroupMap = m_cycleGroups.get(analyzer);
        }
        else
        {
            cycleGroupMap = new HashMap<>();
            m_cycleGroups.put(analyzer, cycleGroupMap);
        }

        assert !cycleGroupMap.containsKey(cycle.getName()) : "CycleGroup has already been added!";
        cycleGroupMap.put(cycle.getName(), cycle);
        addIssue(cycle);
    }

    public void addDuplicateCodeBlock(final IDuplicateCodeBlockIssue duplicate)
    {
        assert duplicate != null : "Parameter 'duplicate' of method 'addDuplicateCodeBlock' must not be null";
        assert !m_duplicateCodeBlockIssueMap.containsKey(duplicate.getName()) : "Duplicate has already been added";

        m_duplicateCodeBlockIssueMap.put(duplicate.getName(), duplicate);
        addIssue(duplicate);
    }

    public void addResolution(final IResolution resolution)
    {
        assert resolution != null : "Parameter 'resolution' of method 'addResolution' must not be null";

        final List<IResolution> resolutions;
        if (!m_resolutionMap.containsKey(resolution.getType()))
        {
            resolutions = new ArrayList<>();
            m_resolutionMap.put(resolution.getType(), resolutions);
        }
        else
        {
            resolutions = m_resolutionMap.get(resolution.getType());
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

    @Override
    public String getVirtualModel()
    {
        return m_virtualModel;
    }

    public Map<ResolutionType, List<IResolution>> getResolutions()
    {
        return Collections.unmodifiableMap(m_resolutionMap);
    }
}