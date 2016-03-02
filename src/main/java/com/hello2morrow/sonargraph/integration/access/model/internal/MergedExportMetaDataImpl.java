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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.IMergedExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public class MergedExportMetaDataImpl extends AbstractExportMetaDataImpl implements IMergedExportMetaData
{
    private final List<IBasicSoftwareSystemInfo> systems;

    public MergedExportMetaDataImpl(final Collection<IBasicSoftwareSystemInfo> systems, final Map<String, IMergedIssueCategory> issueCategories,
            final Map<String, IMetricCategory> metricCategories, final Map<String, IMetricProvider> metricProviders,
            final Map<String, IMetricId> metricIds, final Map<String, IMetricLevel> metricLevels, final String resourceIdentifier)
    {
        super(resourceIdentifier);
        assert systems != null : "Parameter 'systems' of method 'MergedExportMetaDataImpl' must not be null";
        this.systems = new ArrayList<>(systems);

        issueCategories.values().forEach(super::addIssueCategory);
        metricCategories.values().forEach(super::addMetricCategory);
        metricProviders.values().forEach(super::addMetricProvider);
        metricIds.values().forEach(super::addMetricId);
        metricLevels.values().forEach(super::addMetricLevel);
    }

    @Override
    public List<IBasicSoftwareSystemInfo> getSystems()
    {
        return Collections.unmodifiableList(systems);
    }
}