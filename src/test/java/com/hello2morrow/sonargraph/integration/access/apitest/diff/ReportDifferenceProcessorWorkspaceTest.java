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
package com.hello2morrow.sonargraph.integration.access.apitest.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.diff.IModuleDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;

public class ReportDifferenceProcessorWorkspaceTest
{
    private static final String REPORT_1 = TestFixture.TEST_REPORT_WORKSPACE_1;
    private static final String REPORT_2 = TestFixture.TEST_REPORT_WORKSPACE_2;

    @Test
    public void compareWorkspaceOfReports()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final OperationResult load1 = controller.loadSystemReport(new File(REPORT_1));
        assertTrue(load1.toString(), load1.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertEquals("Wrong number of modules", 2, softwareSystem.getModules().size());

        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();
        final OperationResult load2 = controller.loadSystemReport(new File(REPORT_2));
        assertTrue(load2.toString(), load2.isSuccess());

        final ISystemInfoProcessor systemProcessor2 = controller.createSystemInfoProcessor();
        final IWorkspaceDelta workspaceDelta = diffProcessor.getWorkspaceDelta(systemProcessor2);
        final List<IModule> unchangedModules = workspaceDelta.getUnchangedModules();
        assertEquals("Wrong number of unchanged modules", 0, unchangedModules.size());

        final List<IModule> addedModules = workspaceDelta.getAddedModules();
        assertEquals("Wrong number of added modules", 0, addedModules.size());
        final List<IModule> removedModules = workspaceDelta.getRemovedModules();
        assertEquals("Wrong number of removed modules", 0, removedModules.size());
        final List<IModuleDelta> changedModules = workspaceDelta.getChangedModules();
        assertEquals("Wrong number of changed modules", 2, changedModules.size());

        final IModuleDelta alarmClockDelta = changedModules.get(0);
        assertEquals("Wrong module name", "AlarmClock", alarmClockDelta.getModule().getName());
        assertEquals("Wrong removed root", "./AlarmClock/src/test/java", alarmClockDelta.getRemovedRoots().get(0).getPresentationName());
        assertEquals("Wrong number of added roots", 0, alarmClockDelta.getAddedRoots().size());
        assertEquals("Wrong number of unchanged roots", 2, alarmClockDelta.getUnchangedRoots().size());

        final IModuleDelta foundationDelta = changedModules.get(1);
        assertEquals("Wrong module name", "Foundation", foundationDelta.getModule().getName());
        assertEquals("Wrong removed root", "./Foundation/src/main/java", foundationDelta.getRemovedRoots().get(0).getPresentationName());
        assertEquals("Wrong added root", "./Foundation/src2/main/java", foundationDelta.getAddedRoots().get(0).getPresentationName());
        assertEquals("Wrong number of unchanged roots", 1, foundationDelta.getUnchangedRoots().size());

        System.out.println("---- Delta (Architect vs Build) -----\n");
        System.out.println(workspaceDelta.toString());
    }
}