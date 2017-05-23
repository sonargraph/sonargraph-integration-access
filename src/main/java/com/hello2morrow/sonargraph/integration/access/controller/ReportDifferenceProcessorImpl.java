/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.diff.Diff;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricThresholdDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IModuleDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.CoreSystemDataDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.IssueDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.MetricDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.MetricThresholdDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.ModuleDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.StandardDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.WorkspaceDeltaImpl;

class ReportDifferenceProcessorImpl implements IReportDifferenceProcessor
{
    private final ISystemInfoProcessor baseSystem;
    //initialized on first request
    private Set<IIssue> m_issues;

    public ReportDifferenceProcessorImpl(final ISystemInfoProcessor baseSystem)
    {
        assert baseSystem != null : "Parameter 'systemInfoProcessor1' of method 'ReportDifferenceProcessorImpl' must not be null";

        this.baseSystem = baseSystem;
    }

    //TODO Check if issues with resolutions must be filtered
    @Override
    public IIssueDelta getIssueDelta(final ISystemInfoProcessor infoProcessor, final Predicate<IIssue> filter)
    {
        assert infoProcessor != null : "Parameter 'infoProcessor2' of method 'getIssueDelta' must not be null";
        //filter can be null

        //final long start = System.currentTimeMillis();
        final List<IIssue> originalIssueList = baseSystem.getIssues(filter);
        final Map<IIssue, IIssue> originalIssues = originalIssueList.stream().collect(Collectors.toMap(i -> i, i -> i));
        final List<IIssue> issues = infoProcessor.getIssues(filter);
        //System.out.println("Time needed to retrieve issues: " + (System.currentTimeMillis() - start));
        final Set<IIssue> removed = new HashSet<>(originalIssueList);
        final List<IIssue> added = new ArrayList<>();
        final List<IIssue> unchanged = new ArrayList<>();
        final List<Pair<IIssue, IIssue>> improved = new ArrayList<>();
        final List<Pair<IIssue, IIssue>> worse = new ArrayList<>();

        for (final IIssue next : issues)
        {
            final IIssue original = originalIssues.get(next);
            if (original != null)
            {
                unchanged.add(next);
            }
            else
            {
                added.add(next);
            }
            removed.remove(next);
        }

        processThresholdIssues(unchanged, added, removed, improved, worse);
        processCycleGroups(added, removed, improved, worse);
        processDuplicates(added, removed, improved, worse);

        return new IssueDeltaImpl(unchanged, added, new ArrayList<>(removed), improved, worse);
    }

    private void processThresholdIssues(final List<IIssue> unchanged, final Collection<IIssue> added, final Collection<IIssue> removed,
            final List<Pair<IIssue, IIssue>> improved, final List<Pair<IIssue, IIssue>> worse)
    {
        assert unchanged != null : "Parameter 'unchanged' of method 'processThresholdIssues' must not be null";
        assert added != null : "Parameter 'added' of method 'processThresholdIssues' must not be null";
        assert removed != null : "Parameter 'removed' of method 'processThresholdIssues' must not be null";
        assert improved != null : "Parameter 'improved' of method 'processThresholdIssues' must not be null";
        assert worse != null : "Parameter 'worse' of method 'processThresholdIssues' must not be null";

        final Predicate<? super IIssue> thresholdFilter = i -> i instanceof IThresholdViolationIssue;
        final Function<? super IIssue, ? extends IThresholdViolationIssue> mapper = i -> (IThresholdViolationIssue) i;

        final List<IThresholdViolationIssue> addedThresholdIssues = added.stream().filter(thresholdFilter).map(mapper).collect(Collectors.toList());
        final List<IThresholdViolationIssue> removeThresholdIssues = removed.stream().filter(thresholdFilter).map(mapper)
                .collect(Collectors.toList());
        if (addedThresholdIssues.isEmpty() || removeThresholdIssues.isEmpty())
        {
            return;
        }

        final List<Pair<IThresholdViolationIssue, IThresholdViolationIssue>> changed = new ArrayList<>();
        for (final IThresholdViolationIssue previous : removeThresholdIssues)
        {
            addedThresholdIssues.stream().filter(createSameThresholdPredicate(previous)).findAny()
                    .ifPresent(i -> changed.add(new Pair<>(previous, i)));
        }

        for (final Pair<IThresholdViolationIssue, IThresholdViolationIssue> next : changed)
        {
            final IThresholdViolationIssue originalTh = next.getFirst();
            final IThresholdViolationIssue th = next.getSecond();
            final Diff diff = determineThresholdDiff(originalTh, th);
            switch (diff)
            {
            case UNCHANGED:
                unchanged.add(th);
                break;
            case BETTER:
                improved.add(new Pair<IIssue, IIssue>(originalTh, th));
                break;
            case WORSE:
                worse.add(new Pair<IIssue, IIssue>(originalTh, th));
                break;
            case CHANGED:
                assert false : "Changed threshold violation issue must have been handled previously";
                break;
            case NO_MATCH_FOUND:
                assert false : "Unmatched threshold violation issue must have been handled previously";
            }
            removed.remove(originalTh);
            added.remove(th);
        }
    }

    private Predicate<? super IThresholdViolationIssue> createSameThresholdPredicate(final IThresholdViolationIssue thresholdIssue)
    {
        return a -> a.getThreshold().equals(thresholdIssue.getThreshold()) && a.getAffectedElements().equals(thresholdIssue.getAffectedElements());
    }

    @Override
    public IMetricDelta getMetricDelta(final ISystemInfoProcessor infoProcessor, final Predicate<IMetricId> metricFilter,
            final Predicate<IElement> elementFilter)
    {
        //TODO
        //      final IMetricId metricId = original.getThreshold().getMetricId();
        //        final Double bestValue = metricId.getBestValue();
        //        if (bestValue.equals(Double.POSITIVE_INFINITY))
        //        {
        //            return originalDouble < doubleValue ? Diff.IMPROVED : Diff.WORSE;
        //        }
        //        if (bestValue.equals(Double.NEGATIVE_INFINITY))
        //        {
        //            return originalDouble > doubleValue ? Diff.IMPROVED : Diff.WORSE;
        //        }
        //
        //        if (!bestValue.equals(Double.NaN))
        //        {
        //            final double diff1 = Math.abs(bestValue.doubleValue() - originalDouble);
        //            final double diff2 = Math.abs(bestValue.doubleValue() - doubleValue);
        //            return diff1 > diff2 ? Diff.IMPROVED : Diff.WORSE;
        //        }
        return new MetricDeltaImpl();
    }

    //Check in removed if there are any cycle groups that match more or less any in added.
    private void processCycleGroups(final List<IIssue> added, final Set<IIssue> removed, final List<Pair<IIssue, IIssue>> improved,
            final List<Pair<IIssue, IIssue>> worse)
    {
        //TODO
        //1. if there is a removed cycle group where all involved elements are present in an added, then it is likely that it has been made worse
        //   (check for special case where 2 or more cycle groups are now combined into a single new one.
        //2. if there is an added cycle group where all involved elements are present in a removed, then it is likely that it has been improved
        //   (check for special case where cycle group is broken into two or more smaller cycle groups)
    }

    private void processDuplicates(final List<IIssue> added, final Set<IIssue> removed, final List<Pair<IIssue, IIssue>> improved,
            final List<Pair<IIssue, IIssue>> worse)
    {
        //TODO
        //same as for cycles: check if there are duplicates that contain exact matches and more to detect improved / worse
        //also: check if there are probable matches, e.g. where only less than a threshold of elements have been changed.
    }

    private Diff determineThresholdDiff(final IThresholdViolationIssue original, final IThresholdViolationIssue th)
    {
        assert original != null : "Parameter 'original' of method 'determineThresholdDiff' must not be null";
        assert th != null : "Parameter 'th' of method 'determineThresholdDiff' must not be null";

        final Number originalValue = original.getMetricValue();
        final Number value = th.getMetricValue();
        if (originalValue.equals(value))
        {
            return Diff.UNCHANGED;
        }

        final double lowerThreshold = original.getThreshold().getLowerThreshold().doubleValue();
        final double upperThreshold = original.getThreshold().getUpperThreshold().doubleValue();
        final double originalDouble = originalValue.doubleValue();
        final double doubleValue = value.doubleValue();
        if (originalDouble < lowerThreshold && doubleValue > upperThreshold || originalDouble > upperThreshold && doubleValue < lowerThreshold)
        {
            return Diff.CHANGED;
        }

        if (originalDouble < lowerThreshold)
        {
            return originalDouble < doubleValue ? Diff.BETTER : Diff.WORSE;
        }

        if (originalDouble > upperThreshold)
        {
            return originalDouble > doubleValue ? Diff.BETTER : Diff.WORSE;
        }

        assert false : "Unprocessed change of threshold violation (original|new): " + original + "|" + th;
        return null;
    }

    @Override
    public Diff determineChange(final IIssue issue)
    {
        if (m_issues == null)
        {
            //since the issue list does not change, we cache them here for later access.
            m_issues = new HashSet<>(baseSystem.getIssues(null));
        }

        if (m_issues.contains(issue))
        {
            return Diff.UNCHANGED;
        }

        if (issue instanceof IThresholdViolationIssue)
        {
            final IThresholdViolationIssue thresholdIssue = (IThresholdViolationIssue) issue;
            final Optional<IThresholdViolationIssue> previous = m_issues.stream().filter(i -> i instanceof IThresholdViolationIssue)
                    .map(i -> (IThresholdViolationIssue) i).filter(createSameThresholdPredicate(thresholdIssue)).findAny();
            if (previous.isPresent())
            {
                return determineThresholdDiff(previous.get(), thresholdIssue);
            }
            return Diff.NO_MATCH_FOUND;
        }

        //TODO: Handle duplicate and check if worse or better, based on involved files and size of individual blocks.
        //TODO: Check cycle and check if worse or better, based on number of involved elements.

        return Diff.NO_MATCH_FOUND;
    }

    @Override
    public IWorkspaceDelta getWorkspaceDelta(final ISystemInfoProcessor systemProcessor2)
    {
        assert systemProcessor2 != null : "Parameter 'systemProcessor2' of method 'getWorkspaceDelta' must not be null";
        final WorkspaceDeltaImpl workspaceDelta = new WorkspaceDeltaImpl();

        final Map<String, IModule> modules1 = new LinkedHashMap<>(baseSystem.getModules());
        final Map<String, IModule> modules2 = new LinkedHashMap<>(systemProcessor2.getModules());

        final List<String> processedModuleNames = new ArrayList<>();
        for (final Entry<String, IModule> next : modules1.entrySet())
        {
            final String moduleName = next.getKey();
            final IModule module1 = next.getValue();
            final IModule module2 = modules2.get(moduleName);
            if (modules2.containsKey(moduleName))
            {
                //FIXME [IK] we currently cannot consider the ordering of the roots
                boolean unchanged = true;
                for (final IRootDirectory root1 : module1.getRootDirectories())
                {
                    if (!module2.getRootDirectories().stream().anyMatch(r2 -> r2.getFqName().equals(root1.getFqName())))
                    {
                        unchanged = false;
                        break;
                    }
                }

                if (unchanged)
                {
                    workspaceDelta.addUnchangedModule(module1);
                }
                else
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

    @Override
    public ICoreSystemDataDelta getCoreSystemDataDelta(final ISystemInfoProcessor infoProcessor)
    {
        assert infoProcessor != null : "Parameter 'infoProcessor' of method 'getMetaDataDelta' must not be null";
        final CoreSystemDataDeltaImpl delta = new CoreSystemDataDeltaImpl();

        final IStandardDelta<IIssueProvider> issueProviderDelta = StandardDeltaImpl.<IIssueProvider> computeDelta(baseSystem.getIssueProviders(),
                infoProcessor.getIssueProviders());
        delta.setIssueProviderDelta(issueProviderDelta);

        final IStandardDelta<IIssueCategory> issueCategoryDelta = StandardDeltaImpl.<IIssueCategory> computeDelta(baseSystem.getIssueCategories(),
                infoProcessor.getIssueCategories());
        delta.setIssueCategoryDelta(issueCategoryDelta);

        final IStandardDelta<IIssueType> issueTypeDelta = StandardDeltaImpl.<IIssueType> computeDelta(baseSystem.getIssueTypes(),
                infoProcessor.getIssueTypes());
        delta.setIssueTypeDelta(issueTypeDelta);

        final IStandardDelta<IMetricProvider> metricProviderDelta = StandardDeltaImpl.<IMetricProvider> computeDelta(baseSystem.getMetricProviders(),
                infoProcessor.getMetricProviders());
        delta.setMetricProviderDelta(metricProviderDelta);

        final IStandardDelta<IMetricCategory> metricCategoryDelta = StandardDeltaImpl.<IMetricCategory> computeDelta(
                baseSystem.getMetricCategories(), infoProcessor.getMetricCategories());
        delta.setMetricCategoryDelta(metricCategoryDelta);

        final IStandardDelta<IMetricLevel> metricLevelDelta = StandardDeltaImpl.<IMetricLevel> computeDelta(baseSystem.getMetricLevels(),
                infoProcessor.getMetricLevels());
        delta.setMetricLevelDelta(metricLevelDelta);

        final IStandardDelta<IMetricId> metricIdDelta = StandardDeltaImpl.<IMetricId> computeDelta(baseSystem.getMetricIds(),
                infoProcessor.getMetricIds());
        delta.setMetricIdDelta(metricIdDelta);

        final IStandardDelta<IFeature> featureDelta = StandardDeltaImpl
                .<IFeature> computeDelta(baseSystem.getFeatures(), infoProcessor.getFeatures());
        delta.setFeatureDelta(featureDelta);

        final IStandardDelta<IAnalyzer> analyzerDelta = StandardDeltaImpl.<IAnalyzer> computeDelta(baseSystem.getAnalyzers(),
                infoProcessor.getAnalyzers());
        delta.setAnalyzerDelta(analyzerDelta);

        delta.setMetricThresholdDelta(computeMetricThresholdDelta(baseSystem.getMetricThresholds(), infoProcessor.getMetricThresholds()));

        final IStandardDelta<String> elementKindDelta = StandardDeltaImpl.<String> computeDelta(baseSystem.getElementKinds(),
                infoProcessor.getElementKinds());
        delta.setElementKindDelta(elementKindDelta);

        return delta;
    }

    private IMetricThresholdDelta computeMetricThresholdDelta(final List<IMetricThreshold> thresholds1, final List<IMetricThreshold> thresholds2)
    {
        assert thresholds1 != null : "Parameter 'thresholds1' of method 'computeMetricThresholdDelta' must not be null";
        assert thresholds2 != null : "Parameter 'thresholds2' of method 'computeMetricThresholdDelta' must not be null";

        final List<IMetricThreshold> removed = new ArrayList<>();
        final List<IMetricThreshold> unchanged = new ArrayList<>();
        final List<Pair<IMetricThreshold, IMetricThreshold>> changed = new ArrayList<>();

        final List<IMetricThreshold> first = new ArrayList<>(thresholds1);
        final List<IMetricThreshold> second = new ArrayList<>(thresholds2);

        for (final IMetricThreshold next : first)
        {
            if (second.remove(next))
            {
                unchanged.add(next);
            }
            else
            {
                final Optional<IMetricThreshold> changedOpt = second.stream()
                        .filter(th -> th.getMetricId().equals(next.getMetricId()) && th.getMetricLevel().equals(next.getMetricLevel())).findFirst();
                if (changedOpt.isPresent())
                {
                    final IMetricThreshold changedTh = changedOpt.get();
                    changed.add(new Pair<IMetricThreshold, IMetricThreshold>(next, changedTh));
                    second.remove(changedTh);
                }
                else
                {
                    removed.add(next);
                }
            }
        }

        final MetricThresholdDeltaImpl delta = new MetricThresholdDeltaImpl(second /* added */, removed, unchanged, changed);

        return delta;
    }

}
