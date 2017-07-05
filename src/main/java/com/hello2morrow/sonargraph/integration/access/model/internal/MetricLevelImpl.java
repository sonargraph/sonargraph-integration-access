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

import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;

public final class MetricLevelImpl extends ElementImpl implements IMetricLevel
{
    private static final long serialVersionUID = -1495880029918751555L;
    private final int orderNumber;

    public MetricLevelImpl(final String name, final String presentationName, final int orderNumber)
    {
        super(name, presentationName);
        assert orderNumber >= 0 : "orderNumber must a >= 0";
        this.orderNumber = orderNumber;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricLevel#getOrderNumber()
     */
    @Override
    public int getOrderNumber()
    {
        return orderNumber;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + orderNumber;
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
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final MetricLevelImpl other = (MetricLevelImpl) obj;
        if (orderNumber != other.orderNumber)
        {
            return false;
        }
        return true;
    }
}
