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
package com.hello2morrow.sonargraph.integration.access.model.diff;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public interface ICoreSystemDataDelta extends IDelta
{
    IStandardDelta<IIssueProvider> getIssueProviderDelta();

    IStandardDelta<IIssueCategory> getIssueCategoryDelta();

    IStandardDelta<IIssueType> getIssueTypeDelta();

    IStandardDelta<IMetricProvider> getMetricProviderDelta();

    IStandardDelta<IMetricCategory> getMetricCategoryDelta();

    IStandardDelta<IMetricLevel> getMetricLevelDelta();

    IStandardDelta<IMetricId> getMetricIdDelta();

    IStandardDelta<IFeature> getFeatureDelta();

    IStandardDelta<IAnalyzer> getAnalyzerDelta();

    IMetricThresholdDelta getMetricThresholdDelta();

    IElementKindDelta getElementKindDelta();
}
