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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IModule;

public interface ISystemInfoProcessor extends IInfoProcessor
{
    public List<IIssueProvider> getIssueProviders();

    public List<IIssueType> getIssueTypes();

    public List<ICycleGroupIssue> getCycleGroups(Predicate<ICycleGroupIssue> filter);

    public Optional<IMetricId> getMetricId(String name);

    public List<IMetricId> getMetricIds();

    public List<IAnalyzer> getAnalyzers();

    public List<IMetricCategory> getMetricCategories();

    public List<IMetricProvider> getMetricProviders();

    public List<IFeature> getFeatures();

    public boolean hasIssue(Predicate<IIssue> filter);

    public Map<String, IModule> getModules();

    public List<IIssueCategory> getIssueCategories();

    public List<IMetricThreshold> getMetricThresholds();

    public List<String> getElementKinds();
}