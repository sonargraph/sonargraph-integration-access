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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;

public final class IssueContainer<T extends IIssue>
{
    private final List<T> baselineSystemIssues = new ArrayList<>(2);
    private final List<T> currentSystemIssues = new ArrayList<>(2);

    public IssueContainer()
    {
        super();
    }

    public void addBaselineSystemIssue(final T issue)
    {
        assert issue != null : "Parameter 'issue' of method 'addBaselineSystemIssue' must not be null";
        baselineSystemIssues.add(issue);
    }

    public void addCurrentSystemIssue(final T issue)
    {
        assert issue != null : "Parameter 'issue' of method 'addCurrentSystemIssue' must not be null";
        currentSystemIssues.add(issue);
    }

    public void sort(final Comparator<IIssue> comparator)
    {
        assert comparator != null : "Parameter 'comparator' of method 'sort' must not be null";
        Collections.sort(baselineSystemIssues, comparator);
        Collections.sort(currentSystemIssues, comparator);
    }

    public List<T> getBaselineSystemIssues()
    {
        return baselineSystemIssues;
    }

    public List<T> getCurrentSystemIssues()
    {
        return currentSystemIssues;
    }
}