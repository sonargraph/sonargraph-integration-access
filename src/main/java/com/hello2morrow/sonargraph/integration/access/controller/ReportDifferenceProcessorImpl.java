/**
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IModuleDelta;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.CycleGroupIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueContainer;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MultiNamedElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ReportDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SingleNamedElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.WorkspaceDeltaImpl;

final class ReportDifferenceProcessorImpl implements IReportDifferenceProcessor
{
    enum MultiNamedElementIssueType
    {
        CYCLE_GROUP,
        DUPLICATE_CODE
    }

    enum Source
    {
        BASELINE_SYSTEM,
        CURRENT_SYSTEM
    }

    private final ISystemInfoProcessor baselineSystemInfoProcessor;

    public ReportDifferenceProcessorImpl(final ISystemInfoProcessor baselineSysteminfoProcessor)
    {
        assert baselineSysteminfoProcessor != null : "Parameter 'baselineSysteminfoProcessor' of method 'ReportDifferenceProcessorImpl' must not be null";
        this.baselineSystemInfoProcessor = baselineSysteminfoProcessor;
    }

    @Override
    public ISoftwareSystem getSoftwareSystem()
    {
        return baselineSystemInfoProcessor.getSoftwareSystem();
    }

    private void processSingleElementIssue(final Source source, final SingleNamedElementIssueImpl issue,
            final Map<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> elementCollector)
    {
        assert source != null : "Parameter 'source' of method 'processSingleElementIssue' must not be null";
        assert issue != null : "Parameter 'issue' of method 'processSingleElementIssue' must not be null";
        assert elementCollector != null : "Parameter 'elementCollector' of method 'processSingleElementIssue' must not be null";

        final String namedElementFqName = issue.getNamedElement().getFqName();
        Map<String, IssueContainer<SingleNamedElementIssueImpl>> issueKeyToIssueContainer = elementCollector.get(namedElementFqName);
        if (issueKeyToIssueContainer == null)
        {
            issueKeyToIssueContainer = new HashMap<>();
            elementCollector.put(namedElementFqName, issueKeyToIssueContainer);
        }

        final String issueKey = issue.getKey();
        IssueContainer<SingleNamedElementIssueImpl> issueContainer = issueKeyToIssueContainer.get(issueKey);
        if (issueContainer == null)
        {
            issueContainer = new IssueContainer<>();
            issueKeyToIssueContainer.put(issueKey, issueContainer);
        }

        switch (source)
        {
        case BASELINE_SYSTEM:
            issueContainer.addBaselineSystemIssue(issue);
            break;
        case CURRENT_SYSTEM:
            issueContainer.addCurrentSystemIssue(issue);
            break;
        default:
            assert false : "Unhandled: " + source;
            break;
        }
    }

    private void processMultiElementIssue(final Source source, final MultiNamedElementIssueImpl issue,
            final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementCollector,
            final Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueCollector)
    {
        assert source != null : "Parameter 'source' of method 'processMultiElementIssue' must not be null";
        assert issue != null : "Parameter 'issue' of method 'processMultiElementIssue' must not be null";
        assert elementCollector != null : "Parameter 'elementCollector' of method 'processMultiElementIssue' must not be null";
        assert issueCollector != null : "Parameter 'issueCollector' of method 'processMultiElementIssue' must not be null";

        final String issueName = issue.getName();
        IssueContainer<MultiNamedElementIssueImpl> issueContainer = issueCollector.get(issueName);
        if (issueContainer == null)
        {
            issueContainer = new IssueContainer<>();
            issueCollector.put(issueName, issueContainer);
        }
        switch (source)
        {
        case BASELINE_SYSTEM:
            issueContainer.addBaselineSystemIssue(issue);
            break;
        case CURRENT_SYSTEM:
            issueContainer.addCurrentSystemIssue(issue);
            break;
        default:
            assert false : "Unhandled: " + source;
            break;
        }

        for (final INamedElement nextNamedElement : issue.getNamedElements())
        {
            final String nextFqName = nextNamedElement.getFqName();
            Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueKeyToIssueContainer = elementCollector.get(nextFqName);
            if (issueKeyToIssueContainer == null)
            {
                issueKeyToIssueContainer = new HashMap<>();
                elementCollector.put(nextFqName, issueKeyToIssueContainer);
            }

            final String issueKey = issue.getKey();
            IssueContainer<MultiNamedElementIssueImpl> nextIssueContainer = issueKeyToIssueContainer.get(issueKey);
            if (nextIssueContainer == null)
            {
                nextIssueContainer = new IssueContainer<>();
                issueKeyToIssueContainer.put(issueKey, nextIssueContainer);
            }

            switch (source)
            {
            case BASELINE_SYSTEM:
                nextIssueContainer.addBaselineSystemIssue(issue);
                break;
            case CURRENT_SYSTEM:
                nextIssueContainer.addCurrentSystemIssue(issue);
                break;
            default:
                assert false : "Unhandled: " + source;
                break;
            }
        }
    }

    private void process(final Source source, final List<IIssue> issues,
            final Map<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> elementSingleCollector,
            final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementCycleGroupCollector,
            final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementDuplicateCodeCollector,
            final Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueMultiCollector)
    {
        assert source != null : "Parameter 'source' of method 'process' must not be null";
        assert issues != null : "Parameter 'issues' of method 'process' must not be null";
        assert elementSingleCollector != null : "Parameter 'elementSingleCollector' of method 'process' must not be null";
        assert elementCycleGroupCollector != null : "Parameter 'elementCycleGroupCollector' of method 'process' must not be null";
        assert elementDuplicateCodeCollector != null : "Parameter 'elementDuplicateCodeCollector' of method 'process' must not be null";
        assert issueMultiCollector != null : "Parameter 'issueMultiCollector' of method 'process' must not be null";

        for (final IIssue nextIssue : issues)
        {
            if (nextIssue instanceof CycleGroupIssueImpl)
            {
                processMultiElementIssue(source, (MultiNamedElementIssueImpl) nextIssue, elementCycleGroupCollector, issueMultiCollector);
            }
            else if (nextIssue instanceof DuplicateCodeBlockIssueImpl)
            {
                processMultiElementIssue(source, (MultiNamedElementIssueImpl) nextIssue, elementDuplicateCodeCollector, issueMultiCollector);
            }
            else
            {
                assert nextIssue instanceof SingleNamedElementIssueImpl : "Unexpected class in method 'process': " + nextIssue;
                processSingleElementIssue(source, (SingleNamedElementIssueImpl) nextIssue, elementSingleCollector);
            }
        }
    }

    private void processThreshold(final ThresholdViolationIssue baseline, final ThresholdViolationIssue current, final IssueDeltaImpl issueDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processThreshold' must not be null";
        assert current != null : "Parameter 'current' of method 'processThreshold' must not be null";
        assert issueDeltaImpl != null : "Parameter 'issueDeltaImpl' of method 'processThreshold' must not be null";

        final Number originalValue = baseline.getMetricValue();
        final Number value = current.getMetricValue();
        if (originalValue.equals(value))
        {
            return;
        }

        final double lowerThreshold = baseline.getThreshold().getLowerThreshold().doubleValue();
        final double upperThreshold = baseline.getThreshold().getUpperThreshold().doubleValue();
        final double originalDouble = originalValue.doubleValue();
        final double doubleValue = value.doubleValue();

        if (Utility.hasChanged(originalDouble, doubleValue, 2))
        {
            if (originalDouble < lowerThreshold)
            {
                if (originalDouble < doubleValue)
                {
                    issueDeltaImpl.improved(new BaselineCurrent<IThresholdViolationIssue>(baseline, current));
                }
                else
                {
                    issueDeltaImpl.worsened(new BaselineCurrent<IThresholdViolationIssue>(baseline, current));
                }
            }
            else if (originalDouble > upperThreshold)
            {
                if (originalDouble > doubleValue)
                {
                    issueDeltaImpl.improved(new BaselineCurrent<IThresholdViolationIssue>(baseline, current));
                }
                else
                {
                    issueDeltaImpl.worsened(new BaselineCurrent<IThresholdViolationIssue>(baseline, current));
                }
            }
        }
    }

    private void processMatchingIssue(final IIssue current, final IIssue baseline, final IssueDeltaImpl issueDeltaImpl)
    {
        assert current != null : "Parameter 'current' of method 'processMatchingIssue' must not be null";
        assert baseline != null : "Parameter 'baseline' of method 'processMatchingIssue' must not be null";
        assert current != baseline : "Same intstances";
        assert issueDeltaImpl != null : "Parameter 'issueDeltaImpl' of method 'processMatchingIssue' must not be null";

        if (!current.getResolutionType().equals(baseline.getResolutionType()))
        {
            issueDeltaImpl.changedResolutionType(new BaselineCurrent<>(baseline, current));
        }

        if (current instanceof ThresholdViolationIssue)
        {
            assert baseline instanceof ThresholdViolationIssue : "Unexpected class in method 'processMatchingIssue': " + baseline;
            final ThresholdViolationIssue baselineThresholdViolationIssue = (ThresholdViolationIssue) baseline;
            final ThresholdViolationIssue currentThresholdViolationIssue = (ThresholdViolationIssue) current;
            processThreshold(baselineThresholdViolationIssue, currentThresholdViolationIssue, issueDeltaImpl);
        }
    }

    private static final class IssueComparator implements Comparator<IIssue>
    {
        IssueComparator()
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

    private void processMultiNamedElementIssueInfo(final MultiNamedElementIssueType type,
            final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementCollector,
            final Map<String, BaselineCurrent<Integer>> issueKeyToCount, final IssueDeltaImpl issueDeltaImpl)
    {
        assert type != null : "Parameter 'type' of method 'processMultiNamedElementIssueInfo' must not be null";
        assert elementCollector != null : "Parameter 'elementCollector' of method 'processMultiNamedElementIssueInfo' must not be null";
        assert issueKeyToCount != null : "Parameter 'issueKeyToCount' of method 'processMultiNamedElementIssueInfo' must not be null";
        assert issueDeltaImpl != null : "Parameter 'issueDeltaImpl' of method 'processMultiNamedElementIssueInfo' must not be null";

        for (final Entry<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> nextFqNameEntry : elementCollector.entrySet())
        {
            for (final Entry<String, IssueContainer<MultiNamedElementIssueImpl>> nextIssueKeyEntry : nextFqNameEntry.getValue().entrySet())
            {
                final IssueContainer<MultiNamedElementIssueImpl> nextIssueContainer = nextIssueKeyEntry.getValue();
                final List<MultiNamedElementIssueImpl> nextBaselineIssues = nextIssueContainer.getBaselineSystemIssues();
                final List<MultiNamedElementIssueImpl> nextCurrentIssues = nextIssueContainer.getCurrentSystemIssues();

                assert (nextBaselineIssues.isEmpty() && nextCurrentIssues.isEmpty()) == false : "No issues at all";
                final int nextBaseLineIssuesSize = nextBaselineIssues.size();
                final int nextCurrentIssuesSize = nextCurrentIssues.size();

                if (nextBaseLineIssuesSize != nextCurrentIssuesSize)
                {
                    final String nextIssueKey = nextIssueKeyEntry.getKey();
                    switch (type)
                    {
                    case CYCLE_GROUP:
                        if (nextBaselineIssues.isEmpty())
                        {
                            assert !nextCurrentIssues.isEmpty() : "'nextCurrentIssues' is not empty: " + nextCurrentIssues;
                            issueDeltaImpl.addedToCycle(nextFqNameEntry.getKey(), nextIssueKey);
                        }
                        else
                        {
                            assert nextCurrentIssues.isEmpty() : "'nextCurrentIssues' must be empty: " + nextCurrentIssues;
                            assert !nextBaselineIssues.isEmpty() : "'nextBaselineIssues' is not empty: " + nextBaselineIssues;
                            issueDeltaImpl.removedFromCycle(nextFqNameEntry.getKey(), nextIssueKey);
                        }
                        break;
                    case DUPLICATE_CODE:
                        issueDeltaImpl.changedDuplicateCodeParticipation(nextFqNameEntry.getKey(), new BaselineCurrent<Integer>(
                                nextBaseLineIssuesSize, nextCurrentIssuesSize));
                        break;
                    default:
                        assert false : "Unhandled: " + type;
                        break;
                    }

                    BaselineCurrent<Integer> count = issueKeyToCount.get(nextIssueKey);
                    if (count == null)
                    {
                        count = new BaselineCurrent<>(0, 0);
                        issueKeyToCount.put(nextIssueKey, count);
                    }

                    count.setBaseline(count.getBaseline() + nextBaseLineIssuesSize);
                    count.setCurrent(count.getCurrent() + nextCurrentIssuesSize);
                }
            }
        }
    }

    private IssueDeltaImpl createIssueDelta(final ISystemInfoProcessor infoProcessor)
    {
        assert infoProcessor != null : "Parameter 'infoProcessor' of method 'createIssueDelta' must not be null";

        final IssueDeltaImpl issueDeltaImpl = new IssueDeltaImpl();

        final Map<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> elementSingleCollector = new HashMap<>();
        final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementCycleGroupCollector = new HashMap<>();
        final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementDuplicateCodeCollector = new HashMap<>();
        final Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueMultiCollector = new HashMap<>();

        process(Source.BASELINE_SYSTEM, baselineSystemInfoProcessor.getIssues(null), elementSingleCollector, elementCycleGroupCollector,
                elementDuplicateCodeCollector, issueMultiCollector);
        process(Source.CURRENT_SYSTEM, infoProcessor.getIssues(null), elementSingleCollector, elementCycleGroupCollector,
                elementDuplicateCodeCollector, issueMultiCollector);

        final IssueComparator issueComparator = new IssueComparator();

        for (final Entry<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> nextFqNameEntry : elementSingleCollector.entrySet())
        {
            for (final Entry<String, IssueContainer<SingleNamedElementIssueImpl>> nextIssueKeyEntry : nextFqNameEntry.getValue().entrySet())
            {
                final IssueContainer<SingleNamedElementIssueImpl> nextIssueContainer = nextIssueKeyEntry.getValue();
                nextIssueContainer.sort(issueComparator);
                final List<SingleNamedElementIssueImpl> nextBaselineIssues = nextIssueContainer.getBaselineSystemIssues();
                final List<SingleNamedElementIssueImpl> nextCurrentIssues = nextIssueContainer.getCurrentSystemIssues();

                assert (nextBaselineIssues.isEmpty() && nextCurrentIssues.isEmpty()) == false : "No issues at all";

                for (final IIssue nextCurrentIssue : new ArrayList<>(nextCurrentIssues))
                {
                    for (final IIssue nextBaselineIssue : new ArrayList<>(nextBaselineIssues))
                    {
                        if (nextCurrentIssue.getLine() == nextBaselineIssue.getLine()
                                && nextCurrentIssue.getColumn() == nextBaselineIssue.getColumn())
                        {
                            processMatchingIssue(nextCurrentIssue, nextBaselineIssue, issueDeltaImpl);
                            nextCurrentIssues.remove(nextCurrentIssue);
                            nextBaselineIssues.remove(nextBaselineIssue);
                            break;
                        }
                    }
                }

                if (!nextBaselineIssues.isEmpty() && nextCurrentIssues.isEmpty())
                {
                    nextBaselineIssues.forEach(n -> issueDeltaImpl.removed(n));
                }
                else if (nextBaselineIssues.isEmpty() && !nextCurrentIssues.isEmpty())
                {
                    nextCurrentIssues.forEach(n -> issueDeltaImpl.added(n));
                }
                else if (nextBaselineIssues.size() != nextCurrentIssues.size())
                {
                    nextBaselineIssues.forEach(n -> issueDeltaImpl.removed(n));
                    nextCurrentIssues.forEach(n -> issueDeltaImpl.added(n));
                }
                else
                {
                    //If baseline/current issues have the same size we suppose that only line/column has changed
                    for (int i = 0; i < nextCurrentIssues.size(); i++)
                    {
                        final IIssue nextCurrentIssue = nextCurrentIssues.get(i);
                        final IIssue nextBaselineIssue = nextBaselineIssues.get(i);
                        processMatchingIssue(nextCurrentIssue, nextBaselineIssue, issueDeltaImpl);
                    }
                }
            }
        }

        final Map<String, BaselineCurrent<Integer>> cycleGroupsCount = new HashMap<>();
        final Map<String, BaselineCurrent<Integer>> duplicateCodeBlockCount = new HashMap<>();
        processMultiNamedElementIssueInfo(MultiNamedElementIssueType.CYCLE_GROUP, elementCycleGroupCollector, cycleGroupsCount, issueDeltaImpl);
        processMultiNamedElementIssueInfo(MultiNamedElementIssueType.DUPLICATE_CODE, elementDuplicateCodeCollector, duplicateCodeBlockCount,
                issueDeltaImpl);

        for (final Entry<String, BaselineCurrent<Integer>> nextEntry : cycleGroupsCount.entrySet())
        {
            final BaselineCurrent<Integer> nextBaselineCurrent = nextEntry.getValue();
            if (nextBaselineCurrent.getBaseline() > nextBaselineCurrent.getCurrent())
            {
                issueDeltaImpl.improvedCycleParticipation(nextEntry.getKey(), nextBaselineCurrent);
            }
            else if (nextBaselineCurrent.getBaseline() < nextBaselineCurrent.getCurrent())
            {
                issueDeltaImpl.worsenedCycleParticipation(nextEntry.getKey(), nextBaselineCurrent);
            }
        }

        assert duplicateCodeBlockCount.size() <= 1 : "Not more than 1 entry expected: " + duplicateCodeBlockCount;
        for (final Entry<String, BaselineCurrent<Integer>> nextEntry : duplicateCodeBlockCount.entrySet())
        {
            final BaselineCurrent<Integer> nextBaselineCurrent = nextEntry.getValue();
            if (nextBaselineCurrent.getBaseline() > nextBaselineCurrent.getCurrent())
            {
                issueDeltaImpl.improvedDuplicateCodeParticipation(nextBaselineCurrent);
            }
            else if (nextBaselineCurrent.getBaseline() < nextBaselineCurrent.getCurrent())
            {
                issueDeltaImpl.worsenedDuplicateCodeParticipation(nextBaselineCurrent);
            }
        }

        for (final Entry<String, IssueContainer<MultiNamedElementIssueImpl>> nextIssueNameEntry : issueMultiCollector.entrySet())
        {
            final IssueContainer<MultiNamedElementIssueImpl> nextIssueContainer = nextIssueNameEntry.getValue();
            nextIssueContainer.sort(issueComparator);

            final List<MultiNamedElementIssueImpl> nextBaselineIssues = nextIssueContainer.getBaselineSystemIssues();
            final List<MultiNamedElementIssueImpl> nextCurrentIssues = nextIssueContainer.getCurrentSystemIssues();

            assert (nextBaselineIssues.isEmpty() && nextCurrentIssues.isEmpty()) == false : "No issues at all";

            for (final IIssue nextCurrentIssue : new ArrayList<>(nextCurrentIssues))
            {
                for (final IIssue nextBaselineIssue : new ArrayList<>(nextBaselineIssues))
                {
                    if (nextCurrentIssue.getName().equals(nextBaselineIssue.getName()))
                    {
                        processMatchingIssue(nextCurrentIssue, nextBaselineIssue, issueDeltaImpl);
                        break;
                    }
                }
            }
        }

        return issueDeltaImpl;
    }

    private WorkspaceDeltaImpl createWorkspaceDelta(final ISystemInfoProcessor systemProcessor)
    {
        assert systemProcessor != null : "Parameter 'systemProcessor' of method 'createWorkspaceDelta' must not be null";

        final WorkspaceDeltaImpl workspaceDelta = new WorkspaceDeltaImpl();

        final Map<String, IModule> baselineModules = new LinkedHashMap<>(baselineSystemInfoProcessor.getModules());
        final Map<String, IModule> currentModules = new LinkedHashMap<>(systemProcessor.getModules());

        final List<String> processedModuleNames = new ArrayList<>();
        for (final Entry<String, IModule> nextEntry : baselineModules.entrySet())
        {
            final String moduleName = nextEntry.getKey();
            final IModule nextBaselineModule = nextEntry.getValue();
            final IModule nextCurrentModule = currentModules.get(moduleName);

            if (currentModules.containsKey(moduleName))
            {
                final IModuleDelta nextModuleDelta = computeModuleDelta(nextBaselineModule, nextCurrentModule);
                if (!nextModuleDelta.isEmpty())
                {
                    workspaceDelta.addChangedModule(nextModuleDelta);
                }
            }
            else
            {
                workspaceDelta.addRemovedModule(nextBaselineModule);
            }
            processedModuleNames.add(moduleName);
        }

        for (final String nextName : processedModuleNames)
        {
            currentModules.remove(nextName);
        }

        for (final Entry<String, IModule> next : currentModules.entrySet())
        {
            workspaceDelta.addAddedModule(next.getValue());
        }

        return workspaceDelta;
    }

    private IModuleDelta computeModuleDelta(final IModule baselineModule, final IModule currentModule)
    {
        assert baselineModule != null : "Parameter 'baselineModule' of method 'computeModuleDelta' must not be null";
        assert currentModule != null : "Parameter 'currentModule' of method 'computeModuleDelta' must not be null";

        final List<IRootDirectory> baselineRootDirectories = baselineModule.getRootDirectories();
        final Set<IRootDirectory> baselineRootDirectoriesAsSet = new HashSet<>(baselineRootDirectories);

        final List<IRootDirectory> currentRootDirectories = currentModule.getRootDirectories();
        final Set<IRootDirectory> currentRootDirectoriesAsSet = new HashSet<>(currentRootDirectories);

        currentRootDirectoriesAsSet.removeAll(baselineRootDirectories);
        baselineRootDirectoriesAsSet.removeAll(currentRootDirectories);

        return new ModuleDeltaImpl(baselineModule, new ArrayList<>(currentRootDirectoriesAsSet), new ArrayList<>(baselineRootDirectoriesAsSet));
    }

    private void processAnalyzers(final List<IAnalyzer> baseline, final List<IAnalyzer> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processAnalyzers' must not be null";
        assert current != null : "Parameter 'current' of method 'processAnalyzers' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processAnalyzers' must not be null";

        final Set<IAnalyzer> baselineAsSet = new HashSet<>(baseline);
        final Set<IAnalyzer> currentAsSet = new HashSet<>(current);
        currentAsSet.removeAll(baseline);
        baselineAsSet.removeAll(current);
        baselineAsSet.forEach(n -> reportDeltaImpl.removedAnalyzer(n));
        currentAsSet.forEach(n -> reportDeltaImpl.addedAnalyzer(n));
    }

    private void processFeatures(final List<IFeature> baseline, final List<IFeature> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processFeatures' must not be null";
        assert current != null : "Parameter 'current' of method 'processFeatures' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processFeatures' must not be null";

        final Set<IFeature> baselineAsSet = new HashSet<>(baseline);
        final Set<IFeature> currentAsSet = new HashSet<>(current);
        currentAsSet.removeAll(baseline);
        baselineAsSet.removeAll(current);
        baselineAsSet.forEach(n -> reportDeltaImpl.removedFeature(n));
        currentAsSet.forEach(n -> reportDeltaImpl.addedFeature(n));
    }

    private void processDuplicateCodeConfiguration(final List<String> baseline, final List<String> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processDuplicateCodeConfiguration' must not be null";
        assert current != null : "Parameter 'current' of method 'processDuplicateCodeConfiguration' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processDuplicateCodeConfiguration' must not be null";

        final Set<String> baselineAsSet = new HashSet<>(baseline);
        final Set<String> currentAsSet = new HashSet<>(current);
        currentAsSet.removeAll(baseline);
        baselineAsSet.removeAll(current);
        baselineAsSet.forEach(n -> reportDeltaImpl.removedDuplicateCodeConfigurationEntry(n));
        currentAsSet.forEach(n -> reportDeltaImpl.addedDuplicateCodeConfigurationEntry(n));
    }

    private void processScriptRunnerConfiguration(final List<String> baseline, final List<String> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processScriptRunnerConfiguration' must not be null";
        assert current != null : "Parameter 'current' of method 'processScriptRunnerConfiguration' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processScriptRunnerConfiguration' must not be null";

        final Set<String> baselineAsSet = new HashSet<>(baseline);
        final Set<String> currentAsSet = new HashSet<>(current);
        currentAsSet.removeAll(baseline);
        baselineAsSet.removeAll(current);
        baselineAsSet.forEach(n -> reportDeltaImpl.removedScriptRunnerConfigurationEntry(n));
        currentAsSet.forEach(n -> reportDeltaImpl.addedScriptRunnerConfigurationEntry(n));
    }

    private void processArchitectureCheckConfiguration(final List<String> baseline, final List<String> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processArchitectureCheckConfiguration' must not be null";
        assert current != null : "Parameter 'current' of method 'processArchitectureCheckConfiguration' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processArchitectureCheckConfiguration' must not be null";

        final Set<String> baselineAsSet = new HashSet<>(baseline);
        final Set<String> currentAsSet = new HashSet<>(current);
        currentAsSet.removeAll(baseline);
        baselineAsSet.removeAll(current);
        baselineAsSet.forEach(n -> reportDeltaImpl.removedArchitectureCheckConfigurationEntry(n));
        currentAsSet.forEach(n -> reportDeltaImpl.addedArchitectureCheckConfigurationEntry(n));
    }

    private void processThresholds(final List<IMetricThreshold> baseline, final List<IMetricThreshold> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processThresholds' must not be null";
        assert current != null : "Parameter 'current' of method 'processThresholds' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processThresholds' must not be null";

        final Map<IMetricThreshold, IMetricThreshold> currentAsMap = current.stream().collect(Collectors.toMap(next -> next, next -> next));

        for (final IMetricThreshold nextBaselineThreshold : baseline)
        {
            final IMetricThreshold nextCurrentThreshold = currentAsMap.remove(nextBaselineThreshold);
            if (nextCurrentThreshold != null)
            {
                if (!nextBaselineThreshold.getLowerThreshold().equals(nextCurrentThreshold.getLowerThreshold())
                        || !nextBaselineThreshold.getUpperThreshold().equals(nextCurrentThreshold.getUpperThreshold()))
                {
                    reportDeltaImpl.changedMetricThresholdBoundaries(new BaselineCurrent<IMetricThreshold>(nextBaselineThreshold,
                            nextCurrentThreshold));
                }
            }
            else
            {
                //Not found in current 
                reportDeltaImpl.removedMetricThreshold(nextBaselineThreshold);
            }
        }

        for (final IMetricThreshold nextCurrentThreshold : currentAsMap.values())
        {
            reportDeltaImpl.addedMetricThreshold(nextCurrentThreshold);
        }
    }

    @Override
    public IReportDelta createReportDelta(final ISystemInfoProcessor systemInfoProcessor)
    {
        assert systemInfoProcessor != null : "Parameter 'systemInfoProcessor' of method 'createReportDelta' must not be null";

        final ReportDeltaImpl reportDeltaImpl = new ReportDeltaImpl(getSoftwareSystem(), systemInfoProcessor.getSoftwareSystem());

        processFeatures(baselineSystemInfoProcessor.getFeatures(), systemInfoProcessor.getFeatures(), reportDeltaImpl);
        processAnalyzers(baselineSystemInfoProcessor.getAnalyzers(), systemInfoProcessor.getAnalyzers(), reportDeltaImpl);
        processDuplicateCodeConfiguration(baselineSystemInfoProcessor.getDuplicateCodeConfigurationEntries(),
                systemInfoProcessor.getDuplicateCodeConfigurationEntries(), reportDeltaImpl);
        processScriptRunnerConfiguration(baselineSystemInfoProcessor.getScriptRunnerConfigurationEntries(),
                systemInfoProcessor.getScriptRunnerConfigurationEntries(), reportDeltaImpl);
        processArchitectureCheckConfiguration(baselineSystemInfoProcessor.getArchitectureCheckConfigurationEntries(),
                systemInfoProcessor.getArchitectureCheckConfigurationEntries(), reportDeltaImpl);
        processThresholds(baselineSystemInfoProcessor.getMetricThresholds(), systemInfoProcessor.getMetricThresholds(), reportDeltaImpl);

        reportDeltaImpl.setWorkspaceDelta(createWorkspaceDelta(systemInfoProcessor));
        reportDeltaImpl.setIssuesDelta(createIssueDelta(systemInfoProcessor));

        return reportDeltaImpl;
    }
}