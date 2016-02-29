package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ISingleExportMetaData extends IExportMetaData
{
    public IBasicSoftwareSystemInfo getSystemInfo();

    public ISingleExportMetaData EMPTY = new ISingleExportMetaData()
    {
        @Override
        public Map<String, IMetricProvider> getMetricProviders()
        {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, IMetricId> getMetricIds()
        {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, IMetricCategory> getMetricCategories()
        {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, IIssueCategory> getIssueCategories()
        {
            return Collections.emptyMap();
        }

        @Override
        public IBasicSoftwareSystemInfo getSystemInfo()
        {
            return new IBasicSoftwareSystemInfo()
            {

                @Override
                public String getVersion()
                {
                    return "";
                }

                @Override
                public long getTimestamp()
                {
                    return 0;
                }

                @Override
                public String getSystemId()
                {
                    return "";
                }

                @Override
                public String getPath()
                {
                    return "";
                }
            };
        }

        @Override
        public String getResourceIdentifier()
        {
            return "EMPTY";
        }

        @Override
        public Map<String, IMetricLevel> getMetricLevels()
        {
            return Collections.emptyMap();
        }

        @Override
        public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
        {
            return Collections.emptyList();
        }
    };
}