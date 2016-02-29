package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Comparator;

public interface IMetricCategory extends IElement
{
    public int getOrderNumber();

    public static class MetricCategoryComparator implements Comparator<IMetricCategory>
    {
        @Override
        public int compare(final IMetricCategory m1, final IMetricCategory m2)
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