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

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.diff.Diff;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;

public interface IReportDifferenceProcessor
{
    IIssueDelta getIssueDelta(ISystemInfoProcessor infoProcessor, Predicate<IIssue> filter);

    IMetricDelta getMetricDelta(ISystemInfoProcessor infoProcessor, Predicate<IMetricId> metricFilter, Predicate<IElement> elementFilter);

    Diff determineChange(IIssue next);

    /**
     * Compares the modules and their root directories
     */
    IWorkspaceDelta getWorkspaceDelta(ISystemInfoProcessor systemProcessor2);
}
