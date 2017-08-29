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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

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
    private final List<BaselineCurrent<IIssue>> changedResolutionType = new ArrayList<>();
    private final List<BaselineCurrent<IThresholdViolationIssue>> improved = new ArrayList<>();
    private final List<BaselineCurrent<IThresholdViolationIssue>> worsened = new ArrayList<>();
    private final Map<String, String> addedToCycle = new TreeMap<>();
    private final Map<String, String> removedFromCycle = new TreeMap<>();
    private final Map<String, BaselineCurrent<Integer>> improvedCycleParticipation = new TreeMap<>();
    private final Map<String, BaselineCurrent<Integer>> worsenedCycleParticipation = new TreeMap<>();
    private final Map<String, BaselineCurrent<Integer>> changedDuplicateCodeBlockParticipation = new HashMap<>();
    private BaselineCurrent<Integer> improvedDuplicateCodeParticipation;
    private BaselineCurrent<Integer> worsenedDuplicateCodeParticipation;

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

    public void changedResolutionType(final BaselineCurrent<IIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'changedResolutionType' must not be null";
        changedResolutionType.add(baselineCurrent);
    }

    public void improved(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'improved' must not be null";
        improved.add(baselineCurrent);
    }

    public void worsened(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'worsened' must not be null";
        worsened.add(baselineCurrent);
    }

    public void addedToCycle(final String namedElementFqName, final String cycleInfo)
    {
        assert namedElementFqName != null && namedElementFqName.length() > 0 : "Parameter 'namedElementFqName' of method 'addedToCycle' must not be empty";
        assert cycleInfo != null && cycleInfo.length() > 0 : "Parameter 'cycleInfo' of method 'addedToCycle' must not be empty";
        addedToCycle.put(namedElementFqName, cycleInfo);
    }

    public void removedFromCycle(final String namedElementFqName, final String cycleInfo)
    {
        assert namedElementFqName != null && namedElementFqName.length() > 0 : "Parameter 'namedElementFqName' of method 'removedFromCycle' must not be empty";
        assert cycleInfo != null && cycleInfo.length() > 0 : "Parameter 'cycleInfo' of method 'removedFromCycle' must not be empty";
        removedFromCycle.put(namedElementFqName, cycleInfo);
    }

    public void improvedCycleParticipation(final String cycleInfo, final BaselineCurrent<Integer> baselineCurrent)
    {
        assert cycleInfo != null && cycleInfo.length() > 0 : "Parameter 'cycleInfo' of method 'improvedCycleParticipation' must not be empty";
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'improvedCycleParticipation' must not be null";
        improvedCycleParticipation.put(cycleInfo, baselineCurrent);
    }

    public void worsenedCycleParticipation(final String cycleInfo, final BaselineCurrent<Integer> baselineCurrent)
    {
        assert cycleInfo != null && cycleInfo.length() > 0 : "Parameter 'cycleInfo' of method 'worsenedCycleParticipation' must not be empty";
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'worsenedCycleParticipation' must not be null";
        worsenedCycleParticipation.put(cycleInfo, baselineCurrent);
    }

    public void changedDuplicateCodeParticipation(final String namedElementFqName, final BaselineCurrent<Integer> baselineCurrent)
    {
        assert namedElementFqName != null : "Parameter 'namedElementFqName' of method 'changedDuplicateCodeParticipation' must not be null";
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'changedDuplicateCodeParticipation' must not be null";
        changedDuplicateCodeBlockParticipation.put(namedElementFqName, baselineCurrent);
    }

    public void improvedDuplicateCodeParticipation(final BaselineCurrent<Integer> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'improvedDuplicateCodeParticipation' must not be null";
        improvedDuplicateCodeParticipation = baselineCurrent;
    }

    public void worsenedDuplicateCodeParticipation(final BaselineCurrent<Integer> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'worsenedDuplicateCodeParticipation' must not be null";
        worsenedDuplicateCodeParticipation = baselineCurrent;
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
    public List<BaselineCurrent<IIssue>> getChangedResolutionType()
    {
        return Collections.unmodifiableList(changedResolutionType);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getWorsenedThresholdViolation()
    {
        return Collections.unmodifiableList(worsened);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getImprovedThresholdViolation()
    {
        return Collections.unmodifiableList(improved);
    }

    @Override
    public Map<String, String> getAddedToCycle()
    {
        return Collections.unmodifiableMap(addedToCycle);
    }

    @Override
    public Map<String, String> getRemovedFromCycle()
    {
        return Collections.unmodifiableMap(removedFromCycle);
    }

    @Override
    public Map<String, BaselineCurrent<Integer>> getImprovedCycleParticipation()
    {
        return Collections.unmodifiableMap(improvedCycleParticipation);
    }

    @Override
    public Map<String, BaselineCurrent<Integer>> getWorsenedCycleParticipation()
    {
        return Collections.unmodifiableMap(worsenedCycleParticipation);
    }

    @Override
    public Optional<BaselineCurrent<Integer>> getImprovedDuplicateCodeParticipation()
    {
        return Optional.ofNullable(improvedDuplicateCodeParticipation);
    }

    @Override
    public Optional<BaselineCurrent<Integer>> getWorsenedDuplicateCodeParticipation()
    {
        return Optional.ofNullable(worsenedDuplicateCodeParticipation);
    }

    @Override
    public Map<String, BaselineCurrent<Integer>> getChangedDuplicateCodeBlockParticipation()
    {
        return Collections.unmodifiableMap(changedDuplicateCodeBlockParticipation);
    }

    @Override
    public boolean isEmpty()
    {
        return added.isEmpty() && removed.isEmpty() && changedResolutionType.isEmpty() && improved.isEmpty() && worsened.isEmpty()
                && addedToCycle.isEmpty() && removedFromCycle.isEmpty() && improvedCycleParticipation.isEmpty()
                && worsenedCycleParticipation.isEmpty() && changedDuplicateCodeBlockParticipation.isEmpty()
                && improvedDuplicateCodeParticipation == null && worsenedDuplicateCodeParticipation == null;
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

        builder.append("\nIssues with changed resolution type (").append(changedResolutionType.size()).append(")");
        for (final BaselineCurrent<IIssue> next : changedResolutionType)
        {
            final IIssue nextBaseline = next.getBaseline();
            builder.append("\n").append(Utility.INDENTATION).append("Changed resolution of issue '").append(nextBaseline.getKey())
                    .append("' (baseline/current): ").append(nextBaseline.getResolutionType()).append("/")
                    .append(next.getCurrent().getResolutionType());
        }

        builder.append("\nImproved metric values of threshold violations (").append(improved.size()).append(")");
        for (final BaselineCurrent<IThresholdViolationIssue> next : improved)
        {
            final IThresholdViolationIssue nextBaseline = next.getBaseline();
            builder.append("\n").append(Utility.INDENTATION).append("Improved metric value of threshold violation '").append(nextBaseline.getKey())
                    .append("' (baseline/current): ").append(Utility.getRoundedValueAsString(nextBaseline.getMetricValue(), 2)).append("/")
                    .append(Utility.getRoundedValueAsString(next.getCurrent().getMetricValue(), 2));
        }

        builder.append("\nWorsened metric values of threshold violations (").append(worsened.size()).append(")");
        for (final BaselineCurrent<IThresholdViolationIssue> next : worsened)
        {
            final IThresholdViolationIssue nextBaseline = next.getBaseline();
            builder.append("\n").append(Utility.INDENTATION).append("Worsened metric value of threshold violation '").append(nextBaseline.getKey())
                    .append("' (baseline/current): ").append(Utility.getRoundedValueAsString(nextBaseline.getMetricValue(), 2)).append("/")
                    .append(Utility.getRoundedValueAsString(next.getCurrent().getMetricValue(), 2));
        }

        builder.append("\nElements added to cycles (").append(addedToCycle.size()).append(")");
        for (final Entry<String, String> nextEntry : addedToCycle.entrySet())
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(nextEntry.getKey()).append(" [")
                    .append(nextEntry.getValue()).append("]");
        }
        builder.append("\nElements removed from cycles (").append(removedFromCycle.size()).append(")");
        for (final Entry<String, String> nextEntry : removedFromCycle.entrySet())
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(nextEntry.getKey()).append(" [")
                    .append(nextEntry.getValue()).append("]");
        }
        for (final Entry<String, BaselineCurrent<Integer>> nextEntry : improvedCycleParticipation.entrySet())
        {
            final BaselineCurrent<Integer> nextBaselineCurrent = nextEntry.getValue();
            builder.append("\nOverall cycle participation improved for '").append(nextEntry.getKey())
                    .append("'. Number of cyclic elements (baseline/current): ").append(nextBaselineCurrent.getBaseline()).append("/")
                    .append(nextBaselineCurrent.getCurrent());
        }
        for (final Entry<String, BaselineCurrent<Integer>> nextEntry : worsenedCycleParticipation.entrySet())
        {
            final BaselineCurrent<Integer> nextBaselineCurrent = nextEntry.getValue();
            builder.append("\nOverall cycle participation worsened for '").append(nextEntry.getKey())
                    .append("'. Number of cyclic elements (baseline/current): ").append(nextBaselineCurrent.getBaseline()).append("/")
                    .append(nextBaselineCurrent.getCurrent());
        }

        builder.append("\nElements with changed duplicate code block participation (").append(changedDuplicateCodeBlockParticipation.size())
                .append(")");
        for (final Entry<String, BaselineCurrent<Integer>> nextEntry : changedDuplicateCodeBlockParticipation.entrySet())
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(nextEntry.getKey());
        }
        if (improvedDuplicateCodeParticipation != null)
        {
            builder.append("\nOverall duplicate code participation improved. Number of source files containing duplicate code (baseline/current): ")
                    .append(improvedDuplicateCodeParticipation.getBaseline()).append("/").append(improvedDuplicateCodeParticipation.getCurrent());
        }
        if (worsenedDuplicateCodeParticipation != null)
        {
            builder.append("\nOverall duplicate code participation worsened. Number of source files containing duplicate code (baseline/current): ")
                    .append(worsenedDuplicateCodeParticipation.getBaseline()).append("/").append(worsenedDuplicateCodeParticipation.getCurrent());
        }

        return builder.toString();
    }
}