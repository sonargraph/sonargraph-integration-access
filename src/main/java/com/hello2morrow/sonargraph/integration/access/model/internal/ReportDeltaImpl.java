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
import java.util.Date;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.IWorkspaceDelta;

public final class ReportDeltaImpl implements IReportDelta
{
    private static final long serialVersionUID = 5654628222750372027L;
    private final List<IFeature> addedFeatures = new ArrayList<>();
    private final List<IFeature> removedFeatures = new ArrayList<>();
    private final List<IAnalyzer> addedAnalyzers = new ArrayList<>();
    private final List<IAnalyzer> removedAnalyzers = new ArrayList<>();
    private final List<IMetricThreshold> addedMetricThresholds = new ArrayList<>();
    private final List<IMetricThreshold> removedMetricThresholds = new ArrayList<>();
    private final List<BaselineCurrent<IMetricThreshold>> changedBoundariesMetricThresholds = new ArrayList<>();
    private final ISoftwareSystem baselineSystem;
    private final ISoftwareSystem currentSystem;
    private IWorkspaceDelta workspaceDelta;
    private IIssueDelta issueDelta;

    public ReportDeltaImpl(final ISoftwareSystem baselineSystem, final ISoftwareSystem currentSystem)
    {
        assert baselineSystem != null : "Parameter 'baselineSystem' of method 'ReportDeltaImpl' must not be null";
        assert currentSystem != null : "Parameter 'currentSystem' of method 'ReportDeltaImpl' must not be null";
        this.baselineSystem = baselineSystem;
        this.currentSystem = currentSystem;
    }

    @Override
    public ISoftwareSystem getBaselineSystem()
    {
        return baselineSystem;
    }

    @Override
    public ISoftwareSystem getCurrentSystem()
    {
        return currentSystem;
    }

    public void removedFeature(final IFeature removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedFeature' must not be null";
        removedFeatures.add(removed);
    }

    public void addedFeature(final IFeature added)
    {
        assert added != null : "Parameter 'added' of method 'addedFeature' must not be null";
        addedFeatures.add(added);
    }

    public void removedAnalyzer(final IAnalyzer removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedAnalyzer' must not be null";
        removedAnalyzers.add(removed);
    }

    public void addedAnalyzer(final IAnalyzer added)
    {
        assert added != null : "Parameter 'added' of method 'addedAnalyzer' must not be null";
        addedAnalyzers.add(added);
    }

    public void changedMetricThresholdBoundaries(final BaselineCurrent<IMetricThreshold> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'changedMetricThresholdBoundaries' must not be null";
        changedBoundariesMetricThresholds.add(baselineCurrent);
    }

    public void removedMetricThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'removedMetricThreshold' must not be null";
        removedMetricThresholds.add(metricThreshold);
    }

    public void addedMetricThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'addedMetricThreshold' must not be null";
        addedMetricThresholds.add(metricThreshold);
    }

    @Override
    public List<IFeature> getAddedFeatures()
    {
        return Collections.unmodifiableList(addedFeatures);
    }

    @Override
    public List<IFeature> getRemovedFeatures()
    {
        return Collections.unmodifiableList(removedFeatures);
    }

    @Override
    public List<IAnalyzer> getAddedAnalyzers()
    {
        return Collections.unmodifiableList(addedAnalyzers);
    }

    @Override
    public List<IAnalyzer> getRemovedAnalyzers()
    {
        return Collections.unmodifiableList(removedAnalyzers);
    }

    @Override
    public List<IMetricThreshold> getAddedMetricThresholds()
    {
        return Collections.unmodifiableList(addedMetricThresholds);
    }

    @Override
    public List<IMetricThreshold> getRemovedMetricThresholds()
    {
        return Collections.unmodifiableList(removedMetricThresholds);
    }

    @Override
    public List<BaselineCurrent<IMetricThreshold>> getChangedBoundariesMetricThresholds()
    {
        return Collections.unmodifiableList(changedBoundariesMetricThresholds);
    }

    public void setWorkspaceDelta(final IWorkspaceDelta delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setWorkspaceDelta' must not be null";
        workspaceDelta = delta;
    }

    @Override
    public IWorkspaceDelta getWorkspaceDelta()
    {
        assert workspaceDelta != null : "'workspaceDelta' of method 'getWorkspaceDelta' must not be null";
        return workspaceDelta;
    }

    public void setIssuesDelta(final IIssueDelta delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setIssuesDelta' must not be null";
        issueDelta = delta;
    }

    @Override
    public IIssueDelta getIssueDelta()
    {
        assert issueDelta != null : "'issueDelta' of method 'getIssueDelta' must not be null";
        return issueDelta;
    }

    @Override
    public boolean isEmpty()
    {
        return addedFeatures.isEmpty() && removedFeatures.isEmpty() && addedAnalyzers.isEmpty() && removedAnalyzers.isEmpty()
                && addedMetricThresholds.isEmpty() && removedMetricThresholds.isEmpty() && changedBoundariesMetricThresholds.isEmpty()
                && getWorkspaceDelta().isEmpty() && getIssueDelta().isEmpty();
    }

    private void addSystemInfo(final StringBuilder builder, final ISoftwareSystem system, final boolean includingPath,
            final boolean includingTimestamp)
    {
        assert builder != null : "Parameter 'builder' of method 'addSystemInfo' must not be null";
        assert system != null : "Parameter 'system' of method 'addSystemInfo' must not be null";

        builder.append("\n").append(Utility.INDENTATION).append("Name: ").append(system.getName());
        builder.append("\n").append(Utility.INDENTATION).append("Id: ").append(system.getSystemId());
        if (includingPath)
        {
            builder.append("\n").append(Utility.INDENTATION).append("Path: ").append(system.getPath());
        }
        if (includingTimestamp)
        {
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(system.getTimestamp())));
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        if (baselineSystem.getSystemId().equals(currentSystem.getSystemId()))
        {
            final boolean pathIsEqual = baselineSystem.getPath().equals(currentSystem.getPath());

            builder.append("System info");
            addSystemInfo(builder, baselineSystem, pathIsEqual, false);
            if (!pathIsEqual)
            {
                builder.append("\n").append(Utility.INDENTATION).append("Path baseline system: ").append(baselineSystem.getPath());
            }
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp baseline system: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(baselineSystem.getTimestamp())));
            if (!pathIsEqual)
            {
                builder.append("\n").append(Utility.INDENTATION).append("Path current system: ").append(currentSystem.getPath());
            }
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp current system: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(currentSystem.getTimestamp())));
        }
        else
        {
            builder.append("WARNING: Using systems with different ids!\n");
            builder.append("\n").append("Baseline system info");
            addSystemInfo(builder, baselineSystem, true, true);
            builder.append("\n\n").append("Current system info");
            addSystemInfo(builder, currentSystem, true, true);
        }

        builder.append("\n");

        if (isEmpty())
        {
            builder.append("\nNo delta detected.");
            return builder.toString();
        }

        builder.append("\nSystem delta");

        builder.append("\n").append(Utility.INDENTATION).append("Added features (").append(addedFeatures.size()).append(")");
        for (final IFeature next : addedFeatures)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Removed features (").append(removedFeatures.size()).append(")");
        for (final IFeature next : removedFeatures)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Added analyzers (").append(addedAnalyzers.size()).append(")");
        for (final IAnalyzer next : addedAnalyzers)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Removed analyzers (").append(removedAnalyzers.size()).append(")");
        for (final IAnalyzer next : removedAnalyzers)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Added metric thresholds (").append(addedMetricThresholds.size()).append(")");
        for (final IMetricThreshold next : addedMetricThresholds)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getMetricId().getName()).append(": ")
                    .append(next.getMetricLevel().getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Removed metric thresholds (").append(removedMetricThresholds.size()).append(")");
        for (final IMetricThreshold next : removedMetricThresholds)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getMetricId().getName()).append(": ")
                    .append(next.getMetricLevel().getName());
        }

        builder.append("\n").append(Utility.INDENTATION).append("Changed boundaries metric thresholds (")
                .append(changedBoundariesMetricThresholds.size()).append(")");
        for (final BaselineCurrent<IMetricThreshold> next : changedBoundariesMetricThresholds)
        {
            final IMetricThreshold baseline = next.getBaseline();
            final IMetricThreshold current = next.getCurrent();
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(baseline.getMetricId().getName()).append(":")
                    .append(baseline.getMetricLevel().getName()).append(" [").append(baseline.getLowerThreshold()).append("-")
                    .append(baseline.getUpperThreshold()).append("] to [").append(current.getLowerThreshold()).append("-")
                    .append(current.getUpperThreshold()).append("]");
        }

        builder.append("\n\nWorkspace delta");
        builder.append(getWorkspaceDelta());
        builder.append("\n\nIssue delta");
        builder.append(getIssueDelta());

        return builder.toString();
    }
}