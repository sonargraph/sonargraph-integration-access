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
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public final class IssueDeltaImpl implements IIssueDelta
{
    private static final long serialVersionUID = -3056194877234260699L;

    private final List<IIssue> added = new ArrayList<>();
    private final List<IIssue> removed = new ArrayList<>();

    private final List<IIssue> addedIgnoreResolution = new ArrayList<>();
    private final List<IIssue> addedFixResolution = new ArrayList<>();
    private final List<IIssue> removedIgnoreResolution = new ArrayList<>();
    private final List<IIssue> removedFixResolution = new ArrayList<>();

    private final List<BaselineCurrent<IThresholdViolationIssue>> improved = new ArrayList<>();
    private final List<BaselineCurrent<IThresholdViolationIssue>> worse = new ArrayList<>();

    public IssueDeltaImpl()
    {
        super();
    }

    public void added(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'added' must not be null";
        added.add(issue);
    }

    public void removed(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'removed' must not be null";
        removed.add(issue);
    }

    public void improved(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'improved' must not be null";
        improved.add(baselineCurrent);
    }

    public void worsened(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'worsened' must not be null";
        worse.add(baselineCurrent);
    }

    @Override
    public List<IIssue> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<IIssue> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getWorsened()
    {
        return Collections.unmodifiableList(worse);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getImproved()
    {
        return Collections.unmodifiableList(improved);
    }

    @Override
    public List<IIssue> getAddedIgnoreResolution()
    {
        return Collections.unmodifiableList(addedIgnoreResolution);
    }

    @Override
    public List<IIssue> getAddedFixResolution()
    {
        return Collections.unmodifiableList(addedFixResolution);
    }

    @Override
    public List<IIssue> getRemovedIgnoreResolution()
    {
        return Collections.unmodifiableList(removedIgnoreResolution);
    }

    @Override
    public List<IIssue> getRemovedFixResolution()
    {
        System.out.println("Huhu");
        return Collections.unmodifiableList(removedFixResolution);
    }

    @Override
    public boolean isEmpty()
    {
        return added.isEmpty() && removed.isEmpty() && addedFixResolution.isEmpty() && addedIgnoreResolution.isEmpty()
                && removedFixResolution.isEmpty() && removedIgnoreResolution.isEmpty() && improved.isEmpty() && worse.isEmpty();
    }

    private void addIssuesInfo(final StringBuilder builder, final List<IIssue> issues)
    {
        assert builder != null : "Parameter 'builder' of method 'addIssuesInfo' must not be null";
        assert issues != null : "Parameter 'issues' of method 'addIssuesInfo' must not be null";

        for (final IIssue nextIssue : issues)
        {
            builder.append("\n").append(Utility.INDENTATION).append(nextIssue.getKey());
            for (final INamedElement nextNamedElement : nextIssue.getAffectedNamedElements())
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(nextNamedElement.getFqName());
            }
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append("\nRemoved issues (").append(removed.size()).append(")");
        addIssuesInfo(builder, removed);
        builder.append("\nAdded issues (").append(added.size()).append(")");
        addIssuesInfo(builder, added);

        final Consumer<? super BaselineCurrent<IThresholdViolationIssue>> imporvedWorsened = i -> builder.append("\n").append(Utility.INDENTATION)
                .append(Utility.INDENTATION).append("Previous: ").append(i.getBaseline().toString()).append("; Now: ")
                .append(i.getCurrent().toString());

        builder.append("\nImproved threshold violations (").append(improved.size()).append(")");
        improved.forEach(imporvedWorsened);
        builder.append("\nWorsened threshold violations (").append(worse.size()).append(")");
        worse.forEach(imporvedWorsened);

        return builder.toString();
    }
}