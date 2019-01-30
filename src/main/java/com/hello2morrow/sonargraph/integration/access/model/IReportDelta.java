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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;
import java.util.function.Predicate;

public interface IReportDelta extends IDelta
{
    public ISoftwareSystem getBaselineSystem();

    public ISoftwareSystem getCurrentSystem();

    public List<IFeature> getAddedFeatures();

    public List<IFeature> getRemovedFeatures();

    public List<IAnalyzer> getAddedAnalyzers();

    public List<IAnalyzer> getAddedAnalyzers(Predicate<IAnalyzer> filter);

    public List<IAnalyzer> getRemovedAnalyzers();

    public List<IAnalyzer> getRemovedAnalyzers(Predicate<IAnalyzer> filter);

    public List<IPlugin> getAddedPlugins(Predicate<IPlugin> filter);

    public List<IPlugin> getRemovedPlugins(Predicate<IPlugin> filter);

    public List<IMetricThreshold> getAddedMetricThresholds();

    public List<IMetricThreshold> getRemovedMetricThresholds();

    public List<BaselineCurrent<IMetricThreshold>> getChangedBoundariesMetricThresholds();

    public IWorkspaceDelta getWorkspaceDelta();

    public IIssueDelta getIssueDelta();

    public BaselineCurrent<AnalyzerExecutionLevel> getAnalyzerExecutionLevelDiff();
}