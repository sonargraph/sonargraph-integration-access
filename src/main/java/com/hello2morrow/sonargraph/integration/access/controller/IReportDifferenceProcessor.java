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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.diff.Diff;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IResolutionDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;

public interface IReportDifferenceProcessor
{
    /**
     * Create a delta for issues using the specified issue predicate.
     * @param filter - if null, all issues are returned.
     */
    IIssueDelta getIssueDelta(ISystemInfoProcessor infoProcessor, Predicate<IIssue> filter);

    //IMetricDelta getMetricDelta(ISystemInfoProcessor infoProcessor, Predicate<IMetricId> metricFilter, Predicate<IElement> elementFilter);

    /**
     * Fast way to determine change of an issue.
     * @param issue
     */
    Diff determineChange(IIssue issue);

    /**
     * Compares the modules and their root directories, not taking order of modules or root directories into account.
     * @param infoProcessor
     */
    IWorkspaceDelta getWorkspaceDelta(ISystemInfoProcessor infoProcessor);

    /**
     * Creates a delta for the "core" system data, e.g. features, analyzers, metric categories, etc.
     * @param infoProcessor
     */
    ICoreSystemDataDelta getCoreSystemDataDelta(ISystemInfoProcessor infoProcessor);

    /**
     * Creates a delta for the resolutions
     * @param infoProcessor
     * @param filter - if null, all resolutions are returned
     * @return
     */
    IResolutionDelta getResolutionDelta(ISystemInfoProcessor infoProcessor, final Predicate<IResolution> filter);
}
