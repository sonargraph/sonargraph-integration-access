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

import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;

public final class MetricThreshold implements IMetricThreshold
{
    private static final long serialVersionUID = -8392990712415156489L;
    private final Number upperThreshold;
    private final Number lowerThreshold;
    private final IMetricId metricId;
    private final IMetricLevel metricLevel;

    public MetricThreshold(final IMetricId metricId, final IMetricLevel metricLevel, final Number lowerThreshold, final Number upperThreshold)
    {
        assert metricId != null : "Parameter 'metricId' of method 'MetricThreshold' must not be null";
        assert metricLevel != null : "Parameter 'metricLevel' of method 'MetricThreshold' must not be null";
        assert lowerThreshold != null : "Parameter 'lowerThreshold' of method 'MetricThreshold' must not be null";
        assert upperThreshold != null : "Parameter 'upperThreshold' of method 'MetricThreshold' must not be null";

        this.metricId = metricId;
        this.metricLevel = metricLevel;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    @Override
    public Number getUpperThreshold()
    {
        return upperThreshold;
    }

    @Override
    public Number getLowerThreshold()
    {
        return lowerThreshold;
    }

    @Override
    public IMetricId getMetricId()
    {
        return metricId;
    }

    @Override
    public IMetricLevel getMetricLevel()
    {
        return metricLevel;
    }

    @Override
    public String getName()
    {
        return new StringBuilder("threshold [metricId=").append(metricId.getName()).append(", metricLevel=").append(metricLevel.getName())
                .append(", lowerThreshold=").append(lowerThreshold).append(", upperThreshold=").append(upperThreshold).append("]").toString();
    }

    @Override
    public String getPresentationName()
    {
        return new StringBuilder("threshold [metricId=").append(metricId.getPresentationName()).append(", metricLevel=")
                .append(metricLevel.getPresentationName()).append(", lowerThreshold=").append(lowerThreshold).append(", upperThreshold=")
                .append(upperThreshold).append("]").toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lowerThreshold == null) ? 0 : lowerThreshold.hashCode());
        result = prime * result + ((metricId == null) ? 0 : metricId.hashCode());
        result = prime * result + ((metricLevel == null) ? 0 : metricLevel.hashCode());
        result = prime * result + ((upperThreshold == null) ? 0 : upperThreshold.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final MetricThreshold other = (MetricThreshold) obj;
        if (metricId == null)
        {
            if (other.metricId != null)
            {
                return false;
            }
        }
        else if (!metricId.equals(other.metricId))
        {
            return false;
        }
        if (metricLevel == null)
        {
            if (other.metricLevel != null)
            {
                return false;
            }
        }
        else if (!metricLevel.equals(other.metricLevel))
        {
            return false;
        }
        if (lowerThreshold == null)
        {
            if (other.lowerThreshold != null)
            {
                return false;
            }
        }
        else if (!lowerThreshold.equals(other.lowerThreshold))
        {
            return false;
        }
        if (upperThreshold == null)
        {
            if (other.upperThreshold != null)
            {
                return false;
            }
        }
        else if (!upperThreshold.equals(other.upperThreshold))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
