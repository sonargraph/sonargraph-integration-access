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
package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public class ModuleInfoProcessorRefactoringsTest
{
    @Test
    public void getSourceFilesAffectedByRefactorings()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_REFACTORINGS));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final Map<String, IModule> moduleMap = controller.getSoftwareSystem().getModules();

        final IModule alarmClock = moduleMap.get("AlarmClock");
        assertNotNull("Module 'AlarmClock' not found", alarmClock);

        final IModuleInfoProcessor moduleInfoProcessor = controller.createModuleInfoProcessor(alarmClock);
        final Map<ISourceFile, Map<IResolution, List<IIssue>>> resolutionsForSourceFiles = moduleInfoProcessor
                .getIssuesForResolutionsForSourceFiles(r -> r.getType() == ResolutionType.REFACTORING);
        assertEquals("Wrong number of source files with refactorings", 2, resolutionsForSourceFiles.size());

        final Optional<Entry<ISourceFile, Map<IResolution, List<IIssue>>>> c1Opt = resolutionsForSourceFiles.entrySet().stream()
                .filter(e -> e.getKey().getName().equals("C1.java")).findFirst();
        assertTrue("Refactoring for C1.java not found", c1Opt.isPresent());
        final Entry<ISourceFile, Map<IResolution, List<IIssue>>> entryC1 = c1Opt.get();
        final Map<IResolution, List<IIssue>> resolutionToIssues = entryC1.getValue();
        final List<Entry<IResolution, List<IIssue>>> resolutionToIssue = resolutionToIssues.entrySet().stream().collect(Collectors.toList());
        assertEquals("Wrong number of refactorings for C1", 1, resolutionToIssue.size());
        final IResolution resolution = resolutionToIssue.get(0).getKey();
        assertEquals("Wrong resolution text", "", resolution.getDescription());
        assertEquals("Wrong issue description", "Rename to 'C1_2'", resolutionToIssue.get(0).getValue().get(0).getDescription());

        final Optional<Entry<ISourceFile, Map<IResolution, List<IIssue>>>> alarmClockSourceOpt = resolutionsForSourceFiles.entrySet().stream()
                .filter(e -> e.getKey().getName().equals("AlarmClock.java")).findFirst();
        assertTrue("Refactoring for C1.java not found", alarmClockSourceOpt.isPresent());
        final Entry<ISourceFile, Map<IResolution, List<IIssue>>> entryAlarmClock = alarmClockSourceOpt.get();
        final Map<IResolution, List<IIssue>> resolutionToIssues2 = entryAlarmClock.getValue();
        final List<Entry<IResolution, List<IIssue>>> resolutionToIssue2 = resolutionToIssues2.entrySet().stream().collect(Collectors.toList());
        assertEquals("Wrong number of refactorings for AlarmClock", 1, resolutionToIssue2.size());
        final IResolution resolution2 = resolutionToIssue2.get(0).getKey();
        assertEquals("Wrong resolution text", "", resolution2.getDescription());
        assertEquals("Wrong issue description", "Rename to 'AlarmClock2'", resolutionToIssue2.get(0).getValue().get(0).getDescription());
    }

    @Test
    public void processReportWithDuplicateFqNames()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_REFACTORINGS_DUPLICATE_FQNAMES));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        //TODO:
        //Check for AlarmToConsole. It is present as original in rename refactoring AlarmToConsole -> AlarmToConsole2
        // It is also present as target of rename refactoring: AlarmToFile -> AlarmToConsole

    }
}
