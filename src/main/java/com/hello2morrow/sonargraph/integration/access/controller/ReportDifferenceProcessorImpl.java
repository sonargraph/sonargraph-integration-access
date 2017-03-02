package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta.Diff;
import com.hello2morrow.sonargraph.integration.access.model.IMetricDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricDeltaImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;

class ReportDifferenceProcessorImpl implements IReportDifferenceProcessor
{
    private final ISystemInfoProcessor baseSystem;
    //initialized on first request
    private Set<IIssue> m_issues;

    public ReportDifferenceProcessorImpl(final ISystemInfoProcessor systemInfoProcessor1)
    {
        assert systemInfoProcessor1 != null : "Parameter 'systemInfoProcessor1' of method 'ReportDifferenceProcessorImpl' must not be null";

        baseSystem = systemInfoProcessor1;
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
                if (next instanceof ThresholdViolationIssue)
                {
                    if (System.currentTimeMillis() > 0)
                    {
                        throw new RuntimeException(
                                "Implementation needs to be changed by inspecting removed / added to belong to the same element and metric"
                                        + "\nbecause equals() / hashCode() for threshold violation is checking for exact match");
                    }
                    final ThresholdViolationIssue originalTh = (ThresholdViolationIssue) original;
                    final ThresholdViolationIssue th = (ThresholdViolationIssue) next;
                    final Diff diff = determineThresholdDiff(originalTh, th);
                    switch (diff)
                    {
                    case EQUAL:
                        unchanged.add(next);
                        removed.remove(next);
                        break;
                    case IMPROVED:
                        improved.add(new Pair<IIssue, IIssue>(originalTh, th));
                        removed.remove(next);
                        break;
                    case WORSE:
                        worse.add(new Pair<IIssue, IIssue>(originalTh, th));
                        removed.remove(next);
                        break;
                    case CHANGED:
                        added.add(next);
                        break;
                    }
                    continue;
                }
                unchanged.add(next);
            }
            else
            {

                added.add(next);
            }
            removed.remove(next);
        }

        processCycleGroups(added, removed, improved, worse);
        processDuplicates(added, removed, improved, worse);

        return new IssueDeltaImpl(unchanged, added, new ArrayList<>(removed), improved, worse);
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

    private Diff determineThresholdDiff(final ThresholdViolationIssue original, final ThresholdViolationIssue th)
    {
        assert original != null : "Parameter 'original' of method 'determineThresholdDiff' must not be null";
        assert th != null : "Parameter 'th' of method 'determineThresholdDiff' must not be null";

        final Number originalValue = original.getMetricValue();
        final Number value = th.getMetricValue();
        if (originalValue.equals(value))
        {
            return Diff.EQUAL;
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
            return originalDouble < doubleValue ? Diff.IMPROVED : Diff.WORSE;
        }

        if (originalDouble > upperThreshold)
        {
            return originalDouble > doubleValue ? Diff.IMPROVED : Diff.WORSE;
        }

        assert false : "Unprocessed change of threshold violation (original|new): " + original + "|" + th;
        return null;
    }

    //TODO Check

    @Override
    public boolean isNewIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'getDiff' must not be null";
        if (m_issues == null)
        {
            //since the issue list does not change, we cache them here for later access.
            m_issues = new HashSet<>(baseSystem.getIssues(null));
        }

        return !m_issues.contains(issue);
    }
}
