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
package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public class ModuleInfoProcessorDuplicatesTest
{
    @Test
    public void processDuplicateIssuesInSameFile()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITH_DUPLICATES));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final Map<String, IModule> moduleMap = controller.getSoftwareSystem().getModules();

        final IModule alarmClock = moduleMap.get("AlarmClock");
        assertNotNull("Module 'AlarmClock' not found", alarmClock);

        final IModuleInfoProcessor processor = controller.createModuleInfoProcessor(alarmClock);
        final Map<ISourceFile, List<IIssue>> issueMap = processor.getIssuesForSourceFiles(issue -> issue.getIssueType().getName()
                .equals("DuplicateCodeBlock"));
        assertNotNull(issueMap);
        assertEquals(2, issueMap.size());
        List<IIssue> duplicateIssues = null;
        for (final Entry<ISourceFile, List<IIssue>> next : issueMap.entrySet())
        {
            if (next.getKey().getName().endsWith("AlarmClock.java"))
            {
                duplicateIssues = next.getValue();
            }
        }
        assertNotNull(duplicateIssues);
        assertEquals("Wrong number of issues", 2, duplicateIssues.size());
    }
}
