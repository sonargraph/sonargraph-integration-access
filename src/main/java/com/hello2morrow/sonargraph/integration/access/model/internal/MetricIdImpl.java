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

    public MetricIdImpl(final String name, final String presentationName, final String description, final List<IMetricCategory> categories,
            final List<IMetricLevel> levels, final IMetricProvider provider, final boolean isFloat, final double bestValue, final double worstValue)
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
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#getCategories()
     */
    @Override
    public List<IMetricCategory> getCategories()
    {
        return Collections.unmodifiableList(categories);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#getProvider()
     */
    @Override
    public IMetricProvider getProvider()
    {
        return provider;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#isFloat()
     */
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
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + (isFloat ? 1231 : 1237);
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
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
        final MetricIdImpl other = (MetricIdImpl) obj;
        if (isFloat != other.isFloat)
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
        return true;
    }
}
