package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Comparator;

import com.hello2morrow.sonargraph.integration.access.foundation.FileUtility;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public final class MetricProviderImpl extends ElementImpl implements IMetricProvider
{
    public static class MetricProviderComparator implements Comparator<String>
    {
        @Override
        public int compare(final String s1, final String s2)
        {
            if (s1.startsWith(FileUtility.REL_PATH_START) && !s2.startsWith(FileUtility.REL_PATH_START))
            {
                return 1;
            }

            if (!s1.startsWith(FileUtility.REL_PATH_START) && s2.startsWith(FileUtility.REL_PATH_START))
            {
                return -1;
            }

            return s1.compareTo(s2);
        }
    }

    public MetricProviderImpl(final String name, final String presentationName)
    {
        super(name, presentationName);
    }
}