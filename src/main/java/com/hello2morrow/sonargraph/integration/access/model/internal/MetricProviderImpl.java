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

import java.util.Comparator;

import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public final class MetricProviderImpl extends ElementImpl implements IMetricProvider
{
    private static final long serialVersionUID = 615452015644502490L;
    private static final String REL_PATH_START = "./";

    public static class MetricProviderComparator implements Comparator<String>
    {
        @Override
        public int compare(final String s1, final String s2)
        {
            assert s1 != null : "Parameter 's1' of method 'compare' must not be null";
            assert s2 != null : "Parameter 's2' of method 'compare' must not be null";

            if (s1.startsWith(REL_PATH_START) && !s2.startsWith(REL_PATH_START))
            {
                return 1;
            }

            if (!s1.startsWith(REL_PATH_START) && s2.startsWith(REL_PATH_START))
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