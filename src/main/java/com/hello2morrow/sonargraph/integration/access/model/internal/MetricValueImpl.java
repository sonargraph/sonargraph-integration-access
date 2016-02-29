package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;

public class MetricValueImpl implements IMetricValue
{
    private final IMetricId m_id;
    private final IMetricLevel m_level;
    private final Number m_value;

    public MetricValueImpl(final IMetricId metricId, final IMetricLevel level, final Number value)
    {
        assert metricId != null : "Parameter 'id' of method 'MetricValue' must not be null";
        assert level != null : "Parameter 'level' of method 'MetricValue' must not be null";
        assert value != null : "Parameter 'value' of method 'MetricValue' must not be null";

        m_id = metricId;
        m_level = level;
        m_value = value;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getId()
     */
    @Override
    public IMetricId getId()
    {
        return m_id;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getLevel()
     */
    @Override
    public IMetricLevel getLevel()
    {
        return m_level;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#getValue()
     */
    @Override
    public Number getValue()
    {
        return m_value;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IMetricValue#isFloat()
     */
    @Override
    public boolean isFloat()
    {
        return m_id.isFloat();
    }
}
