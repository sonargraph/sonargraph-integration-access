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

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public class ThresholdViolationIssue extends ElementIssueImpl implements IThresholdViolationIssue
{
    private final IMetricThreshold threshold;
    private final Number metricValue;

    public ThresholdViolationIssue(final IIssueType issueType, final String description, final IIssueProvider issueProvider,
            final INamedElement element, final boolean hasResolution, final int line, final Number metricValue, final IMetricThreshold threshold)
    {
        super(issueType, description, issueProvider, element, hasResolution, line);
        this.metricValue = metricValue;
        this.threshold = threshold;
    }

    @Override
    public Number getMetricValue()
    {
        return metricValue;
    }

    @Override
    public IMetricThreshold getThreshold()
    {
        return threshold;
    }
}
