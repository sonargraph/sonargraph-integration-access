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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

abstract class AbstractExportMetaDataImpl implements IExportMetaData
{
    private static final long serialVersionUID = 1934927330602066004L;
    private final Map<String, IIssueCategory> issueCategories = new LinkedHashMap<>();
    private final Map<String, IIssueProvider> issueProviders = new LinkedHashMap<>();
    private final Map<String, IIssueType> issueTypes = new LinkedHashMap<>();
    private final Map<String, IMetricCategory> metricCategories = new HashMap<>();
    private final Map<String, IMetricProvider> metricProviders = new TreeMap<>(new MetricProviderImpl.MetricProviderComparator());
    private final Map<String, IMetricLevel> metricLevels = new LinkedHashMap<>();
    private final Map<String, IMetricId> metricIds = new HashMap<>();
    private final String identifier;

    protected AbstractExportMetaDataImpl(final String resourceIdentifier)
    {
        assert resourceIdentifier != null && resourceIdentifier.length() > 0 : "Parameter 'resourceIdentifier' of method 'AbstractExportMetaDataImpl' must not be empty";
        identifier = resourceIdentifier;
    }

    public void addIssueCategory(final IIssueCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addIssueCategory' must not be null";
        assert !issueCategories.containsKey(category.getName()) : "issueCategory '" + category.getName() + "' has already been added";

        issueCategories.put(category.getName(), category);
    }

    public void addIssueProvider(final IIssueProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addIssueProvider' must not be null";
        assert !issueProviders.containsKey(provider.getName()) : "issueProvider '" + provider.getName() + "' has already been added";

        issueProviders.put(provider.getName(), provider);
    }

    public void addIssueType(final IIssueType type)
    {
        assert type != null : "Parameter 'type' of method 'addIssueType' must not be null";
        assert !issueTypes.containsKey(type.getName()) : "issueType '" + type.getName() + "' has already been added";
        issueTypes.put(type.getName(), type);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IExportMetaData#getIssueCategories()
     */
    @Override
    public Map<String, IIssueCategory> getIssueCategories()
    {
        return Collections.unmodifiableMap(issueCategories);
    }

    @Override
    public Map<String, IIssueProvider> getIssueProviders()
    {
        return Collections.unmodifiableMap(issueProviders);
    }

    @Override
    public Map<String, IIssueType> getIssueTypes()
    {
        return Collections.unmodifiableMap(issueTypes);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IExportMetaData#getMetricCategories()
     */
    @Override
    public Map<String, IMetricCategory> getMetricCategories()
    {
        final Map<String, IMetricCategory> result = new LinkedHashMap<>();
        metricCategories.values().stream().sorted(new IMetricCategory.MetricCategoryComparator()).forEach(c -> result.put(c.getName(), c));
        return Collections.unmodifiableMap(result);
    }

    public void addMetricCategory(final IMetricCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addMetricCategory' must not be null";
        assert !metricCategories.containsKey(category.getName()) : "category '" + category.getName() + "' has already been added";

        metricCategories.put(category.getName(), category);
    }

    public void addMetricLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'addMetricLevel' must not be null";
        assert !metricLevels.containsKey(level.getName()) : "metric level '" + level.getName() + "' has already been added";

        metricLevels.put(level.getName(), level);
    }

    @Override
    public Map<String, IMetricLevel> getMetricLevels()
    {
        return Collections.unmodifiableMap(metricLevels);
    }

    @Override
    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'getMetricIdsForLevel' must not be null";
        final List<IMetricId> metrics = metricIds.values().stream().filter((final IMetricId m) -> m.getLevels().contains(level))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(metrics);
    }

    public void addMetricId(final IMetricId metricId)
    {
        assert metricId != null : "Parameter 'metricId' of method 'addMetric' must not be null";
        assert !metricIds.containsKey(metricId.getName()) : "metricId '" + metricId.getName() + "' has already been added";

        metricIds.put(metricId.getName(), metricId);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IExportMetaData#getMetricIds()
     */
    @Override
    public Map<String, IMetricId> getMetricIds()
    {
        return Collections.unmodifiableMap(metricIds);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IExportMetaData#getMetricProviders()
     */
    @Override
    public Map<String, IMetricProvider> getMetricProviders()
    {
        return Collections.unmodifiableMap(metricProviders);
    }

    public void addMetricProvider(final IMetricProvider provider)
    {
        assert provider != null : "Parameter 'provider' of method 'addMetricProvider' must not be null";
        assert !metricProviders.containsKey(provider.getName()) : "provider '" + provider.getName() + "' has already been added";

        metricProviders.put(provider.getName(), provider);
    }

    @Override
    public String getResourceIdentifier()
    {
        return identifier;
    }
}
