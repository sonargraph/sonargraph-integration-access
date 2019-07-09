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

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public final class MetricIdImpl extends ElementWithDescriptionImpl implements IMetricId
{
    private static final long serialVersionUID = 6618223791961362393L;
    private final List<IMetricCategory> categories;
    private final List<IMetricLevel> levels;
    private final IMetricProvider provider;
    private final boolean isFloat;
    private final double worstValue;
    private final double bestValue;
    private final double maxValue;
    private final double minValue;

    public MetricIdImpl(final String name, final String presentationName, final String description, final List<IMetricCategory> categories,
            final List<IMetricLevel> levels, final IMetricProvider provider, final boolean isFloat, final double bestValue, final double worstValue,
            final double minValue, final double maxValue)
    {
        super(name, presentationName, description);
        assert categories != null && !categories.isEmpty() : "Parameter 'categories' of method 'setCategories' must not be empty";
        assert levels != null && !levels.isEmpty() : "Parameter 'levels' of method 'MetricIdImpl' must not be empty";
        assert provider != null : "Parameter 'provider' of method 'MetricId' must not be null";

        this.categories = categories;
        this.levels = levels;
        this.provider = provider;
        this.isFloat = isFloat;
        this.bestValue = bestValue;
        this.worstValue = worstValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public List<IMetricCategory> getCategories()
    {
        return Collections.unmodifiableList(categories);
    }

    @Override
    public IMetricProvider getProvider()
    {
        return provider;
    }

    @Override
    public boolean isFloat()
    {
        return isFloat;
    }

    @Override
    public List<IMetricLevel> getLevels()
    {
        return Collections.unmodifiableList(levels);
    }

    @Override
    public Double getBestValue()
    {
        return bestValue;
    }

    @Override
    public Double getWorstValue()
    {
        return worstValue;
    }

    @Override
    public double getBest()
    {
        return bestValue;
    }

    @Override
    public double getMin()
    {
        return minValue;
    }

    @Override
    public double getMax()
    {
        return maxValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(bestValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + (isFloat ? 1231 : 1237);
        result = prime * result + ((levels == null) ? 0 : levels.hashCode());
        temp = Double.doubleToLongBits(maxValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        temp = Double.doubleToLongBits(worstValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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

        final MetricIdImpl other = (MetricIdImpl) obj;
        if (Double.doubleToLongBits(bestValue) != Double.doubleToLongBits(other.bestValue))
        {
            return false;
        }

        if (categories == null)
        {
            if (other.categories != null)
            {
                return false;
            }
        }
        else if (!categories.equals(other.categories))
        {
            return false;
        }

        if (isFloat != other.isFloat)
        {
            return false;
        }

        if (levels == null)
        {
            if (other.levels != null)
            {
                return false;
            }
        }
        else if (!levels.equals(other.levels))
        {
            return false;
        }

        if (Double.doubleToLongBits(maxValue) != Double.doubleToLongBits(other.maxValue))
        {
            return false;
        }

        if (Double.doubleToLongBits(minValue) != Double.doubleToLongBits(other.minValue))
        {
            return false;
        }

        if (provider == null)
        {
            if (other.provider != null)
            {
                return false;
            }
        }
        else if (!provider.equals(other.provider))
        {
            return false;
        }

        if (Double.doubleToLongBits(worstValue) != Double.doubleToLongBits(other.worstValue))
        {
            return false;
        }
        return true;
    }
}