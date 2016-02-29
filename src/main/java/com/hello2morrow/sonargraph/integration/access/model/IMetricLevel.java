package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Comparator;

public interface IMetricLevel extends IElement
{
    public static final String SYSTEM = "System";
    public static final String MODULE = "Module";

    public int getOrderNumber();

    public static class MetricLevelComparator implements Comparator<IMetricLevel>
    {
        @Override
        public int compare(final IMetricLevel m1, final IMetricLevel m2)
        {
            if (m1.getOrderNumber() < m2.getOrderNumber())
            {
                return -1;
            }
            if (m1.getOrderNumber() > m2.getOrderNumber())
            {
                return 1;
            }

            return m1.getName().compareTo(m2.getName());
        }

    }
}