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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public class ThresholdViolationIssue extends ElementIssueImpl implements IThresholdViolationIssue
{
    private static final long serialVersionUID = -1905279510781305516L;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((metricValue == null) ? 0 : metricValue.hashCode());
        result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ThresholdViolationIssue other = (ThresholdViolationIssue) obj;
        if (metricValue == null)
        {
            if (other.metricValue != null)
            {
                return false;
            }
        }
        else if (!metricValue.equals(other.metricValue))
        {
            return false;
        }
        if (threshold == null)
        {
            if (other.threshold != null)
            {
                return false;
            }
        }
        else if (!threshold.equals(other.threshold))
        {
            return false;
        }
        return true;
    }

}
