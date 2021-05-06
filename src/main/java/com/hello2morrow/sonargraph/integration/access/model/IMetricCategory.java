/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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