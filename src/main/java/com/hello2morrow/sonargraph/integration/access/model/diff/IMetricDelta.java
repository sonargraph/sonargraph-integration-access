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
package com.hello2morrow.sonargraph.integration.access.model.diff;

import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

/**
 * API to get differences in metric values.
 * Added and removed values are not considered as they indicate either a change in config or addition/removal of elements.
 */
public interface IMetricDelta
{
    Map<IMetricLevel, Map<IMetricId, Map<INamedElement, IMetricValue>>> getUnchangedValues();

    Map<IMetricLevel, Map<IMetricId, Map<INamedElement, Pair<IMetricValue, IMetricValue>>>> getWorseValues();

    Map<IMetricLevel, Map<IMetricId, Map<INamedElement, Pair<IMetricValue, IMetricValue>>>> getImprovedValues();
}
