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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IResolutionDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;

public final class ReportDeltaImpl implements IReportDelta
{
    private static final long serialVersionUID = 5654628222750372027L;
    private final ISoftwareSystem system1;
    private final ISoftwareSystem system2;
    private final ICoreSystemDataDelta coreDelta;
    private final IWorkspaceDelta workspaceDelta;
    private final IIssueDelta issueDelta;
    private final IResolutionDelta resolutionDelta;

    public ReportDeltaImpl(final ISoftwareSystem system1, final ISoftwareSystem system2, final ICoreSystemDataDelta coreDelta,
            final IWorkspaceDelta workspaceDelta, final IIssueDelta issueDelta, final IResolutionDelta resolutionDelta)
    {
        assert system1 != null : "Parameter 'system1' of method 'ReportDeltaImpl' must not be null";
        assert system2 != null : "Parameter 'system2' of method 'ReportDeltaImpl' must not be null";
        assert coreDelta != null : "Parameter 'coreDelta' of method 'ReportDeltaImpl' must not be null";
        assert workspaceDelta != null : "Parameter 'workspaceDelta' of method 'ReportDeltaImpl' must not be null";
        assert issueDelta != null : "Parameter 'issueDelta' of method 'ReportDeltaImpl' must not be null";
        assert resolutionDelta != null : "Parameter 'resolutionDelta' of method 'ReportDeltaImpl' must not be null";

        this.system1 = system1;
        this.system2 = system2;

        this.coreDelta = coreDelta;
        this.workspaceDelta = workspaceDelta;
        this.issueDelta = issueDelta;
        this.resolutionDelta = resolutionDelta;
    }

    @Override
    public ISoftwareSystem getSystem1()
    {
        return system1;
    }

    @Override
    public ISoftwareSystem getSystem2()
    {
        return system2;
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

    public IResolutionDelta getResolutionDelta()
    {
        return resolutionDelta;
    }

    @Override
    public String toString()
    {
        return print(true);
    }

    @Override
    public boolean containsChanges()
    {
        return coreDelta.containsChanges() || workspaceDelta.containsChanges() || issueDelta.containsChanges() || resolutionDelta.containsChanges();
    }

    @Override
    public String print(final boolean includeUnresolved)
    {
        final StringBuilder builder = new StringBuilder();
        if (system1.getSystemId().equals(system2.getSystemId()) && system1.getPath().equals(system2.getPath()))
        {
            builder.append("\n").append("System Info: ");
            builder.append(printSystemInfo(system1));
        }
        else
        {
            builder.append("\nNOTE: Delta is calculated using different Systems!!!\n");
            builder.append("\n").append("System Info 1: ");
            builder.append(printSystemInfo(system1));
            builder.append("\n").append("System Info 2: ");
            builder.append(printSystemInfo(system2));
        }

        if (!containsChanges())
        {
            builder.append("\n\nNo delta detected between systems:");
            builder.append("\n").append(Utility.INDENTATION).append("System 1 (Baseline): ").append(printNameAndTimestamp(system1));
            builder.append("\n").append(Utility.INDENTATION).append("System 2           : ").append(printNameAndTimestamp(system2));
            return builder.toString();
        }

        builder.append("\n\nDelta of Systems");
        builder.append("\n").append(Utility.INDENTATION).append("System 1 (Baseline): ").append(printNameAndTimestamp(system1));
        builder.append("\n").append(Utility.INDENTATION).append("System 2           : ").append(printNameAndTimestamp(system2));
        builder.append("\n");

        final List<IDelta> deltas = Arrays.asList(getCoreDelta(), getWorkspaceDelta(), getIssueDelta(), getResolutionDelta());
        deltas.forEach(d ->
        {
            if (d.containsChanges() || includeUnresolved)
            {
                builder.append("\n- ").append(d.print(includeUnresolved));
            }
        });

        return builder.toString();
    }

    private String printNameAndTimestamp(final ISoftwareSystem system)
    {
        final StringBuilder builder = new StringBuilder();
        final String timestamp = Utility.getDateTimeStringFromLocale(new Date(system.getTimestamp()));
        builder.append(system.getName()).append(" from ").append(timestamp);
        return builder.toString();
    }

    private String printSystemInfo(final ISoftwareSystem system)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n").append(Utility.INDENTATION).append("Name: ").append(system.getName());
        builder.append("\n").append(Utility.INDENTATION).append("ID: ").append(system.getSystemId());
        builder.append("\n").append(Utility.INDENTATION).append("Path: ").append(system.getPath());
        return builder.toString();
    }
}