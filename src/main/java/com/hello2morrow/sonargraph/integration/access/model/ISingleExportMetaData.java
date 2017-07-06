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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ISingleExportMetaData extends IExportMetaData
{
    public static final class Empty implements ISingleExportMetaData
    {
        private static final long serialVersionUID = -8405764963352910497L;

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
        public Map<String, IIssueProvider> getIssueProviders()
        {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, IIssueType> getIssueTypes()
        {
            return Collections.emptyMap();
        }

        @Override
        public IBasicSoftwareSystemInfo getSystemInfo()
        {
            return new IBasicSoftwareSystemInfo()
            {
                private static final long serialVersionUID = 1599596216021058066L;

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
    }

    public static final ISingleExportMetaData EMPTY = new Empty();

    public IBasicSoftwareSystemInfo getSystemInfo();
}