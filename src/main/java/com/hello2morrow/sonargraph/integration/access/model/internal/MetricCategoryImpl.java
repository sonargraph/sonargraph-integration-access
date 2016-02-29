package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;

public final class MetricCategoryImpl extends ElementImpl implements IMetricCategory
{
    private final int m_orderNumber;

    public MetricCategoryImpl(final String name, final String presentationName, final int orderNumber)
    {
        super(name, presentationName);
        assert orderNumber >= 0 : "orderNumber must be >= 0";

        m_orderNumber = orderNumber;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricCategory#getOrderNumber()
     */
    @Override
    public int getOrderNumber()
    {
        return m_orderNumber;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + m_orderNumber;
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
        final MetricCategoryImpl other = (MetricCategoryImpl) obj;
        if (m_orderNumber != other.m_orderNumber)
        {
            return false;
        }
        return true;
    }
}