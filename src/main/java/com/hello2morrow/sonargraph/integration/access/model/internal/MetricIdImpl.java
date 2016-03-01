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

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public final class MetricIdImpl extends ElementWithDescriptionImpl implements IMetricId
{
    private final List<IMetricCategory> m_categories;
    private final List<IMetricLevel> m_levels;
    private final IMetricProvider m_provider;
    private final boolean m_isFloat;

    public MetricIdImpl(final String name, final String presentationName, final String description, final List<IMetricCategory> categories,
            final List<IMetricLevel> levels, final IMetricProvider provider, final boolean isFloat)
    {
        super(name, presentationName, description);
        assert categories != null && !categories.isEmpty() : "Parameter 'categories' of method 'setCategories' must not be empty";
        assert levels != null && !levels.isEmpty() : "Parameter 'levels' of method 'MetricIdImpl' must not be empty";
        assert provider != null : "Parameter 'provider' of method 'MetricId' must not be null";

        m_categories = categories;
        m_levels = levels;
        m_provider = provider;
        m_isFloat = isFloat;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#getCategories()
     */
    @Override
    public List<IMetricCategory> getCategories()
    {
        return Collections.unmodifiableList(m_categories);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#getProvider()
     */
    @Override
    public IMetricProvider getProvider()
    {
        return m_provider;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricId#isFloat()
     */
    @Override
    public boolean isFloat()
    {
        return m_isFloat;
    }

    @Override
    public List<IMetricLevel> getLevels()
    {
        return Collections.unmodifiableList(m_levels);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_categories == null) ? 0 : m_categories.hashCode());
        result = prime * result + (m_isFloat ? 1231 : 1237);
        result = prime * result + ((m_provider == null) ? 0 : m_provider.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if(obj == null)
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
        if (m_isFloat != other.m_isFloat)
        {
            return false;
        }
        if (m_provider == null)
        {
            if (other.m_provider != null)
            {
                return false;
            }
        }
        else if (!m_provider.equals(other.m_provider))
        {
            return false;
        }
        if (m_categories == null)
        {
            if (other.m_categories != null)
            {
                return false;
            }
        }
        else if (!m_categories.equals(other.m_categories))
        {
            return false;
        }
        return true;
    }

}
