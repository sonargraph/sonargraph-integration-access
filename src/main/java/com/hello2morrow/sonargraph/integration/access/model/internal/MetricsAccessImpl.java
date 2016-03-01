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
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

class MetricsAccessImpl
{
    private final SingleExportMetaDataImpl m_metaData;

    private final Map<String, IMetricLevel> m_metricLevels = new HashMap<>();

    public MetricsAccessImpl(final String path, final String systemId, final String version, final long timestamp)
    {
        assert version != null : "Parameter 'version' of method 'MetricsAccess' must not be null";
        m_metaData = new SingleExportMetaDataImpl(new BasicSoftwareSystemInfoImpl(path, systemId, version, timestamp), path);
    }

    void addMetricId(final IMetricId metricId)
    {
        m_metaData.addMetricId(metricId);
    }

    Map<String, IMetricId> getMetricIds()
    {
        return m_metaData.getMetricIds();
    }

    void addMetricLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'addMetricLevel' must not be null";
        assert !m_metricLevels.containsKey(level.getName()) : "level '" + level + "' has already been added";

        m_metricLevels.put(level.getName(), level);
    }

    IMetricLevel getMetricLevel(final String level)
    {
        assert level != null && level.length() > 0 : "Parameter 'level' of method 'getMetricLevel' must not be empty";
        return m_metricLevels.get(level);
    }

    Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> result = new LinkedHashMap<>();
        m_metricLevels.values().stream().sorted(new IMetricLevel.MetricLevelComparator()).forEach(c -> result.put(c.getName(), c));
        return Collections.unmodifiableMap(result);
    }

    void addMetricCategory(final IMetricCategory metricCategory)
    {
        assert metricCategory != null : "Parameter 'metricCategory' of method 'addMetricCategory' must not be null";
        m_metaData.addMetricCategory(metricCategory);
    }

    Map<String, IMetricCategory> getMetricCategories()
    {
        return m_metaData.getMetricCategories();
    }

    void addMetricProvider(final IMetricProvider provider)
    {
        m_metaData.addMetricProvider(provider);
    }

    public Map<String, IMetricProvider> getMetricProviders()
    {
        return m_metaData.getMetricProviders();
    }

    public void addIssueCategory(final IIssueCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addIssueCategory' must not be null";
        m_metaData.addIssueCategory(category);
    }

    public Map<String, IIssueCategory> getIssueCategories()
    {
        return m_metaData.getIssueCategories();
    }
}