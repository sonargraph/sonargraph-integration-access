package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.Diff;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricDeltaImpl;

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
    public boolean isNewIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'getDiff' must not be null";
        if (m_issues == null)
        {
            //since the issue list does not change, we cache them here for later access.
            m_issues = new HashSet<>(baseSystem.getIssues(null));
        }

        if (m_issues.contains(issue))
        {
            return false;
        }

        if (issue instanceof IThresholdViolationIssue)
        {
            return !m_issues.stream().filter(i -> i instanceof IThresholdViolationIssue).map(i -> (IThresholdViolationIssue) i)
                    .anyMatch(createSameThresholdPredicate((IThresholdViolationIssue) issue));
        }
        return true;
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

        //TODO: Check duplicate
        //TODO: Check cycle

        return Diff.NO_MATCH_FOUND;
    }
}
