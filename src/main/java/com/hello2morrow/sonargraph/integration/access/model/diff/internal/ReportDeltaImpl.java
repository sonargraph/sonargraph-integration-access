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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.diff.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;

public final class ReportDeltaImpl implements IReportDelta
{
    private static final long serialVersionUID = 5654628222750372027L;

    private final List<IFeature> addedFeature = new ArrayList<>();
    private final List<IFeature> removedFeature = new ArrayList<>();

    private final List<IAnalyzer> addedAnalyzer = new ArrayList<>();
    private final List<IAnalyzer> removedAnalyzer = new ArrayList<>();

    private final List<IMetricThreshold> addedThreshold = new ArrayList<>();
    private final List<IMetricThreshold> removedThreshold = new ArrayList<>();
    private final List<BaselineCurrent<IMetricThreshold>> changedBoundariesThreshold = new ArrayList<>();

    private final ISoftwareSystem baselineSystem;
    private final ISoftwareSystem system;

    private ICoreSystemDataDelta coreDelta;
    private IWorkspaceDelta workspaceDelta;
    private IIssueDelta issueDelta;

    public ReportDeltaImpl(final ISoftwareSystem baselineSystem, final ISoftwareSystem currentSystem)
    {
        assert baselineSystem != null : "Parameter 'baselineSystem' of method 'ReportDeltaImpl' must not be null";
        assert currentSystem != null : "Parameter 'currentSystem' of method 'ReportDeltaImpl' must not be null";
        this.baselineSystem = baselineSystem;
        this.system = currentSystem;
    }

    public void removedFeature(final IFeature removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedFeature' must not be null";
        removedFeature.add(removed);
    }

    public void addedFeature(final IFeature added)
    {
        assert added != null : "Parameter 'added' of method 'addedFeature' must not be null";
        addedFeature.add(added);
    }

    public void removedAnalyzer(final IAnalyzer removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedAnalyzer' must not be null";
        removedAnalyzer.add(removed);
    }

    public void addedAnalyzer(final IAnalyzer added)
    {
        assert added != null : "Parameter 'added' of method 'addedAnalyzer' must not be null";
        addedAnalyzer.add(added);
    }

    public void changedThresholdBoundaries(final BaselineCurrent<IMetricThreshold> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'changedThresholdBoundaries' must not be null";
        changedBoundariesThreshold.add(baselineCurrent);
    }

    public void removedThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'removedThreshold' must not be null";
        removedThreshold.add(metricThreshold);
    }

    public void addedThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'addedThreshold' must not be null";
        addedThreshold.add(metricThreshold);
    }

    public ReportDeltaImpl(final ISoftwareSystem baselineSystem, final ISoftwareSystem system, final ICoreSystemDataDelta coreDelta,
            final IWorkspaceDelta workspaceDelta, final IIssueDelta issueDelta)
    {
        assert baselineSystem != null : "Parameter 'baselineSystem' of method 'ReportDeltaImpl' must not be null";
        assert system != null : "Parameter 'system' of method 'ReportDeltaImpl' must not be null";
        assert coreDelta != null : "Parameter 'coreDelta' of method 'ReportDeltaImpl' must not be null";
        assert workspaceDelta != null : "Parameter 'workspaceDelta' of method 'ReportDeltaImpl' must not be null";
        assert issueDelta != null : "Parameter 'issueDelta' of method 'ReportDeltaImpl' must not be null";

        this.baselineSystem = baselineSystem;
        this.system = system;
        this.coreDelta = coreDelta;
        this.workspaceDelta = workspaceDelta;
        this.issueDelta = issueDelta;
    }

    @Override
    public ISoftwareSystem getBaselineSystem()
    {
        return baselineSystem;
    }

    @Override
    public ISoftwareSystem getSystem()
    {
        return system;
    }

    public ICoreSystemDataDelta getCoreDelta()
    {
        return coreDelta;
    }

    public IWorkspaceDelta getWorkspaceDelta()
    {
        return workspaceDelta;
    }

    public IIssueDelta getIssueDelta()
    {
        return issueDelta;
    }

    @Override
    public boolean isEmpty()
    {
        return coreDelta.isEmpty() && workspaceDelta.isEmpty() && issueDelta.isEmpty();
    }

    private String printNameAndTimestamp(final ISoftwareSystem system)
    {
        assert system != null : "Parameter 'system' of method 'printNameAndTimestamp' must not be null";
        final StringBuilder builder = new StringBuilder();
        final String timestamp = Utility.getDateTimeStringFromLocale(new Date(system.getTimestamp()));
        builder.append(system.getName()).append(" from ").append(timestamp);
        return builder.toString();
    }

    private String printSystemInfo(final ISoftwareSystem system)
    {
        assert system != null : "Parameter 'system' of method 'printSystemInfo' must not be null";
        final StringBuilder builder = new StringBuilder();
        builder.append("\n").append(Utility.INDENTATION).append("Name: ").append(system.getName());
        builder.append("\n").append(Utility.INDENTATION).append("ID: ").append(system.getSystemId());
        builder.append("\n").append(Utility.INDENTATION).append("Path: ").append(system.getPath());
        return builder.toString();
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        if (baselineSystem.getSystemId().equals(system.getSystemId()) && baselineSystem.getPath().equals(system.getPath()))
        {
            builder.append("\n").append("System Info: ");
            builder.append(printSystemInfo(baselineSystem));
        }
        else
        {
            builder.append("\nNOTE: Delta is calculated using different Systems!\n");
            builder.append("\n").append("Baseline system: ");
            builder.append(printSystemInfo(baselineSystem));
            builder.append("\n").append("System: ");
            builder.append(printSystemInfo(system));
        }

        if (!isEmpty())
        {
            builder.append("\n\nNo delta detected between systems:");
            builder.append("\n").append(Utility.INDENTATION).append("Baseline system: ").append(printNameAndTimestamp(baselineSystem));
            builder.append("\n").append(Utility.INDENTATION).append("System         : ").append(printNameAndTimestamp(system));
            return builder.toString();
        }

        builder.append("\n\nDelta of Systems");
        builder.append("\n").append(Utility.INDENTATION).append("Baseline system: ").append(printNameAndTimestamp(baselineSystem));
        builder.append("\n").append(Utility.INDENTATION).append("System         : ").append(printNameAndTimestamp(system));
        builder.append("\n");

        final List<IDelta> deltas = Arrays.asList(getCoreDelta(), getWorkspaceDelta(), getIssueDelta());
        deltas.forEach(d ->
        {
            if (d.isEmpty())
            {
                builder.append("\n- ").append(d);
            }
        });

        return builder.toString();
    }
}