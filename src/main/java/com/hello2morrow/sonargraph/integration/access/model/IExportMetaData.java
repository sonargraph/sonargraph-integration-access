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
package com.hello2morrow.sonargraph.integration.access.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IExportMetaData extends Serializable
{
    public Map<String, IIssueCategory> getIssueCategories();

    public Map<String, IIssueProvider> getIssueProviders();

    public Map<String, IIssueType> getIssueTypes();

    public Map<String, IMetricCategory> getMetricCategories();

    public Map<String, IMetricId> getMetricIds();

    public Map<String, IMetricProvider> getMetricProviders();

    public String getResourceIdentifier();

    public Map<String, IMetricLevel> getMetricLevels();

    public List<IMetricId> getMetricIdsForLevel(IMetricLevel level);
}