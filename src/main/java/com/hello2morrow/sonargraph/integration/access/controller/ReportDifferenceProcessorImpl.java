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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
    enum Source
    {
        BASELINE_SYSTEM,
        CURRENT_SYSTEM
    }

    private final ISystemInfoProcessor baseSystem;

    public ReportDifferenceProcessorImpl(final ISystemInfoProcessor baseSystem)
    {
        assert baseSystem != null : "Parameter 'baseSystem' of method 'ReportDifferenceProcessorImpl' must not be null";
        this.baseSystem = baseSystem;
    }

    @Override
    public ISoftwareSystem getSoftwareSystem()
    {
        return baseSystem.getSoftwareSystem();
    }

    private void processSingleElementIssue(final Source source, final SingleNamedElementIssueImpl issue,
            final Map<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> elementCollector)
    {
        assert source != null : "Parameter 'source' of method 'processSingleElementIssue' must not be null";
        assert issue != null : "Parameter 'issue' of method 'processSingleElementIssue' must not be null";
        assert elementCollector != null : "Parameter 'elementCollector' of method 'processSingleElementIssue' must not be null";

        final String fqName = issue.getNamedElement().getFqName();
        Map<String, IssueContainer<SingleNamedElementIssueImpl>> issueKeyToIssueContainer = elementCollector.get(fqName);
        if (issueKeyToIssueContainer == null)
        {
            issueKeyToIssueContainer = new HashMap<>();
            elementCollector.put(fqName, issueKeyToIssueContainer);
        }

        final String issueKey = issue.getKey();
        IssueContainer<SingleNamedElementIssueImpl> issueContainer = issueKeyToIssueContainer.get(issueKey);
        if (issueContainer == null)
        {
            issueContainer = new IssueContainer<SingleNamedElementIssueImpl>();
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
            issueContainer = new IssueContainer<MultiNamedElementIssueImpl>();
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
            final String fqName = nextNamedElement.getFqName();
            Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueKeyToIssueContainer = elementCollector.get(fqName);
            if (issueKeyToIssueContainer == null)
            {
                issueKeyToIssueContainer = new HashMap<>();
                elementCollector.put(fqName, issueKeyToIssueContainer);
            }

            final String issueKey = issue.getKey();
            IssueContainer<MultiNamedElementIssueImpl> nextIssueContainer = issueKeyToIssueContainer.get(issueKey);
            if (nextIssueContainer == null)
            {
                nextIssueContainer = new IssueContainer<MultiNamedElementIssueImpl>();
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
            final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementMultiCollector,
            final Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueMultiCollector)
    {
        assert source != null : "Parameter 'source' of method 'process' must not be null";
        assert issues != null : "Parameter 'issues' of method 'process' must not be null";
        assert elementSingleCollector != null : "Parameter 'elementSingleCollector' of method 'process' must not be null";
        assert elementMultiCollector != null : "Parameter 'elementMultiCollector' of method 'process' must not be null";
        assert issueMultiCollector != null : "Parameter 'issueMultiCollector' of method 'process' must not be null";
        for (final IIssue nextIssue : issues)
        {
            if (nextIssue instanceof MultiNamedElementIssueImpl)
            {
                processMultiElementIssue(source, (MultiNamedElementIssueImpl) nextIssue, elementMultiCollector, issueMultiCollector);
            }
            else
            {
                assert nextIssue instanceof SingleNamedElementIssueImpl : "Unexpected class in method 'process': " + nextIssue;
                processSingleElementIssue(source, (SingleNamedElementIssueImpl) nextIssue, elementSingleCollector);
            }
        }
    }

    //TODO
    private int matchedIssues;

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

    private void processMatchingIssue(final String namedElementFqName, final IIssue current, final IIssue baseline,
            final IssueDeltaImpl issueDeltaImpl)
    {
        assert namedElementFqName != null && namedElementFqName.length() > 0 : "Parameter 'namedElementFqName' of method 'processMatchingIssue' must not be empty";
        assert current != null : "Parameter 'current' of method 'processMatchingIssue' must not be null";
        assert baseline != null : "Parameter 'baseline' of method 'processMatchingIssue' must not be null";
        assert current != baseline : "Same intstances";
        assert issueDeltaImpl != null : "Parameter 'issueDeltaImpl' of method 'processMatchingIssue' must not be null";

        matchedIssues++;

        //        System.out.println("ISSUE MATCHED FOR NAMED ELEMENT: " + namedElementFqName + "\n BASELINE: " + baseline.getName() + " ("
        //                + baseline.getDescription() + ") [" + baseline.getLine() + "," + baseline.getColumn() + "]\n CURRENT: " + current.getName() + " ("
        //                + current.getDescription() + ") [" + current.getLine() + "," + current.getColumn() + "]");
        if (!current.getResolutionType().equals(baseline.getResolutionType()))
        {
            //TODO  
        }

        if (current instanceof ThresholdViolationIssue)
        {
            assert baseline instanceof ThresholdViolationIssue : "Unexpected class in method 'processMatchingIssue': " + baseline;
            final ThresholdViolationIssue baselineThresholdViolationIssue = (ThresholdViolationIssue) baseline;
            final ThresholdViolationIssue currentThresholdViolationIssue = (ThresholdViolationIssue) current;
            processThreshold(baselineThresholdViolationIssue, currentThresholdViolationIssue, issueDeltaImpl);
        }
    }

    private IssueDeltaImpl createIssueDelta(final ISystemInfoProcessor infoProcessor)
    {
        assert infoProcessor != null : "Parameter 'infoProcessor' of method 'createIssueDelta' must not be null";

        final IssueDeltaImpl issueDeltaImpl = new IssueDeltaImpl();

        final Map<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> elementSingleCollector = new HashMap<>();
        final Map<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> elementMultiCollector = new HashMap<>();
        final Map<String, IssueContainer<MultiNamedElementIssueImpl>> issueMultiCollector = new HashMap<>();
        process(Source.BASELINE_SYSTEM, baseSystem.getIssues(null), elementSingleCollector, elementMultiCollector, issueMultiCollector);
        process(Source.CURRENT_SYSTEM, infoProcessor.getIssues(null), elementSingleCollector, elementMultiCollector, issueMultiCollector);

        for (final Entry<String, Map<String, IssueContainer<SingleNamedElementIssueImpl>>> nextFqNameEntry : elementSingleCollector.entrySet())
        {
            final String nextNamedElementFqName = nextFqNameEntry.getKey();

            for (final Entry<String, IssueContainer<SingleNamedElementIssueImpl>> nextIssueKeyEntry : nextFqNameEntry.getValue().entrySet())
            {
                final IssueContainer<SingleNamedElementIssueImpl> nextIssueContainer = nextIssueKeyEntry.getValue();
                nextIssueContainer.sort();
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
                            processMatchingIssue(nextNamedElementFqName, nextCurrentIssue, nextBaselineIssue, issueDeltaImpl);
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
                        processMatchingIssue(nextNamedElementFqName, nextCurrentIssue, nextBaselineIssue, issueDeltaImpl);
                    }
                }
            }
        }

        for (final Entry<String, Map<String, IssueContainer<MultiNamedElementIssueImpl>>> nextFqNameEntry : elementMultiCollector.entrySet())
        {
            final String nextNamedElementFqName = nextFqNameEntry.getKey();

            for (final Entry<String, IssueContainer<MultiNamedElementIssueImpl>> nextIssueKeyEntry : nextFqNameEntry.getValue().entrySet())
            {
                final IssueContainer<MultiNamedElementIssueImpl> nextIssueContainer = nextIssueKeyEntry.getValue();
                nextIssueContainer.sort();
                final List<MultiNamedElementIssueImpl> nextBaselineIssues = nextIssueContainer.getBaselineSystemIssues();
                final List<MultiNamedElementIssueImpl> nextCurrentIssues = nextIssueContainer.getCurrentSystemIssues();

                assert (nextBaselineIssues.isEmpty() && nextCurrentIssues.isEmpty()) == false : "No issues at all";
                //TODO
            }
        }

        for (final Entry<String, IssueContainer<MultiNamedElementIssueImpl>> nextIssueNameEntry : issueMultiCollector.entrySet())
        {
            //TODO
        }

        //        System.out.println("Matched issues: " + matchedIssues);
        return issueDeltaImpl;
    }

    private WorkspaceDeltaImpl createWorkspaceDelta(final ISystemInfoProcessor systemProcessor)
    {
        assert systemProcessor != null : "Parameter 'systemProcessor' of method 'createWorkspaceDelta' must not be null";

        final WorkspaceDeltaImpl workspaceDelta = new WorkspaceDeltaImpl();

        final Map<String, IModule> modules1 = new LinkedHashMap<>(baseSystem.getModules());
        final Map<String, IModule> modules2 = new LinkedHashMap<>(systemProcessor.getModules());

        final List<String> processedModuleNames = new ArrayList<>();
        for (final Entry<String, IModule> next : modules1.entrySet())
        {
            final String moduleName = next.getKey();
            final IModule module1 = next.getValue();
            final IModule module2 = modules2.get(moduleName);
            if (modules2.containsKey(moduleName))
            {
                boolean unchanged = true;
                for (final IRootDirectory root1 : module1.getRootDirectories())
                {
                    if (!module2.getRootDirectories().stream().anyMatch(r2 -> r2.getFqName().equals(root1.getFqName())))
                    {
                        unchanged = false;
                        break;
                    }
                }

                if (!unchanged)
                {
                    workspaceDelta.addChangedModule(computeModuleDelta(module1, module2));
                }
            }
            else
            {
                workspaceDelta.addRemovedModule(module1);
            }
            processedModuleNames.add(moduleName);
        }

        for (final String nextName : processedModuleNames)
        {
            modules2.remove(nextName);
        }

        for (final Entry<String, IModule> next : modules2.entrySet())
        {
            workspaceDelta.addAddedModule(next.getValue());
        }

        return workspaceDelta;
    }

    private IModuleDelta computeModuleDelta(final IModule module1, final IModule module2)
    {
        assert module1 != null : "Parameter 'module1' of method 'computeModuleDelta' must not be null";
        assert module2 != null : "Parameter 'module2' of method 'computeModuleDelta' must not be null";

        final Map<String, IRootDirectory> rootPaths1 = new LinkedHashMap<>();
        final Map<String, IRootDirectory> rootPaths2 = new LinkedHashMap<>();
        module1.getRootDirectories().stream().forEach(r -> rootPaths1.put(r.getPresentationName(), r));
        module2.getRootDirectories().stream().forEach(r -> rootPaths2.put(r.getPresentationName(), r));

        final List<IRootDirectory> added = new ArrayList<>();
        final List<IRootDirectory> unchanged = new ArrayList<>();
        final List<IRootDirectory> removed = new ArrayList<>();
        for (final Map.Entry<String, IRootDirectory> next : rootPaths1.entrySet())
        {
            if (rootPaths2.containsKey(next.getKey()))
            {
                unchanged.add(next.getValue());
            }
            else
            {
                removed.add(next.getValue());
            }
        }
        for (final IRootDirectory next : unchanged)
        {
            rootPaths2.remove(next.getPresentationName());
        }

        added.addAll(rootPaths2.values());

        return new ModuleDeltaImpl(module1, unchanged, added, removed);
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

    private void processThreshols(final List<IMetricThreshold> baseline, final List<IMetricThreshold> current, final ReportDeltaImpl reportDeltaImpl)
    {
        assert baseline != null : "Parameter 'baseline' of method 'processThreshols' must not be null";
        assert current != null : "Parameter 'current' of method 'processThreshols' must not be null";
        assert baseline != current : "Same instances";
        assert reportDeltaImpl != null : "Parameter 'reportDeltaImpl' of method 'processThreshols' must not be null";

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

        processFeatures(baseSystem.getFeatures(), systemInfoProcessor.getFeatures(), reportDeltaImpl);
        processAnalyzers(baseSystem.getAnalyzers(), systemInfoProcessor.getAnalyzers(), reportDeltaImpl);
        processThreshols(baseSystem.getMetricThresholds(), systemInfoProcessor.getMetricThresholds(), reportDeltaImpl);

        reportDeltaImpl.setWorkspaceDelta(createWorkspaceDelta(systemInfoProcessor));
        reportDeltaImpl.setIssuesDelta(createIssueDelta(systemInfoProcessor));

        return reportDeltaImpl;
    }
}