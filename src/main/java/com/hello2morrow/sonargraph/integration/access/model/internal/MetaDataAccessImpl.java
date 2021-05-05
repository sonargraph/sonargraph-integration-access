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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

final class MetaDataAccessImpl implements Serializable
{
    private static final long serialVersionUID = 5722967552130841881L;
    private final Map<String, IMetricLevel> metricLevels = new HashMap<>();
    private final SingleExportMetaDataImpl metaData;

    public MetaDataAccessImpl(final String path, final String systemId, final String version, final long timestamp)
    {
        assert systemId != null && systemId.length() > 0 : "Parameter 'systemId' of method 'MetaDataAccessImpl' must not be empty";
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'MetaDataAccessImpl' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'MetaDataAccessImpl' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' of method 'MetaDataAccessImpl' must be > 0";
        metaData = new SingleExportMetaDataImpl(new BasicSoftwareSystemInfoImpl(path, systemId, version, timestamp), path);
    }

    void addMetricId(final IMetricId metricId)
    {
        metaData.addMetricId(metricId);
    }

    Map<String, IMetricId> getMetricIds()
    {
        return metaData.getMetricIds();
    }

    void addMetricLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'addMetricLevel' must not be null";
        assert !metricLevels.containsKey(level.getName()) : "level '" + level + "' has already been added";

        metricLevels.put(level.getName(), level);
    }

    IMetricLevel getMetricLevel(final String level)
    {
        assert level != null && level.length() > 0 : "Parameter 'level' of method 'getMetricLevel' must not be empty";
        return metricLevels.get(level);
    }

    Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> result = new LinkedHashMap<>();
        metricLevels.values().stream().sorted(new IMetricLevel.MetricLevelComparator()).forEach(c -> result.put(c.getName(), c));
        return Collections.unmodifiableMap(result);
    }

    void addMetricCategory(final IMetricCategory metricCategory)
    {
        assert metricCategory != null : "Parameter 'metricCategory' of method 'addMetricCategory' must not be null";
        metaData.addMetricCategory(metricCategory);
    }

    Map<String, IMetricCategory> getMetricCategories()
    {
        return metaData.getMetricCategories();
    }

    void addMetricProvider(final IMetricProvider provider)
    {
        metaData.addMetricProvider(provider);
    }

    public Map<String, IMetricProvider> getMetricProviders()
    {
        return metaData.getMetricProviders();
    }

    public void addIssueCategory(final IIssueCategory category)
    {
        assert category != null : "Parameter 'category' of method 'addIssueCategory' must not be null";
        metaData.addIssueCategory(category);
    }

    public Map<String, IIssueCategory> getIssueCategories()
    {
        return metaData.getIssueCategories();
    }
}