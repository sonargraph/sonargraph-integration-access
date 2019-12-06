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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.Comparator;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

public interface IReportDifferenceProcessor
{
    public ISoftwareSystem getSoftwareSystem();

    public IReportDelta createReportDelta(final ISystemInfoProcessor systemInfoProcessor);

    public static final class IssueComparator implements Comparator<IIssue>
    {
        public IssueComparator()
        {
            super();
        }

        @Override
        public int compare(final IIssue i1, final IIssue i2)
        {
            assert i1 != null : "Parameter 'i1' of method 'compare' must not be null";
            assert i2 != null : "Parameter 'i2' of method 'compare' must not be null";

            int compared = i1.getLine() - i2.getLine();
            if (compared == 0)
            {
                compared = i1.getColumn() - i2.getColumn();
                if (compared == 0)
                {
                    compared = i1.getName().compareToIgnoreCase(i2.getName());
                    if (compared == 0)
                    {
                        compared = 1;
                    }
                }
            }
            return compared;
        }
    }
}