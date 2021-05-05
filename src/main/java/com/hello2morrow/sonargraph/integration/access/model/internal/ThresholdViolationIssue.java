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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public final class ThresholdViolationIssue extends NamedElementIssueImpl implements IThresholdViolationIssue
{
    private static final long serialVersionUID = -1905279510781305516L;
    private final IMetricThreshold threshold;
    private final Number metricValue;

    public ThresholdViolationIssue(final String name, final String presentationName, final String description, final IIssueType issueType,
            final Severity severity, final IIssueProvider issueProvider, final int line, final int column, final INamedElement element,
            final Number metricValue, final IMetricThreshold threshold)
    {
        super(name, presentationName, description, issueType, severity, issueProvider, line, column, element);
        assert metricValue != null : "Parameter 'metricValue' of method 'ThresholdViolationIssue' must not be null";
        assert threshold != null : "Parameter 'threshold' of method 'ThresholdViolationIssue' must not be null";
        this.metricValue = metricValue;
        this.threshold = threshold;
    }

    @Override
    public String getKey()
    {
        return super.getKey() + KEY_SEPARATOR + threshold.getMetricId().getName() + KEY_SEPARATOR + threshold.getMetricLevel().getName();
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