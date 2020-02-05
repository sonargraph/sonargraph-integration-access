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

import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;

public class MetricValueImpl implements IMetricValue
{
    private static final long serialVersionUID = -4110580470334138407L;
    private final IMetricId id;
    private final IMetricLevel level;
    private final Number value;

    public MetricValueImpl(final IMetricId metricId, final IMetricLevel level, final Number value)
    {
        assert metricId != null : "Parameter 'id' of method 'MetricValue' must not be null";
        assert level != null : "Parameter 'level' of method 'MetricValue' must not be null";
        assert value != null : "Parameter 'value' of method 'MetricValue' must not be null";

        this.id = metricId;
        this.level = level;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getId()
     */
    @Override
    public IMetricId getId()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getLevel()
     */
    @Override
    public IMetricLevel getLevel()
    {
        return level;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getValue()
     */
    @Override
    public Number getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#isFloat()
     */
    @Override
    public boolean isFloat()
    {
        return id.isFloat();
    }

    @Override
    public String toString()
    {
        return "MetricValueImpl [id=" + id + ", level=" + level + ", value=" + value + "]";
    }
}