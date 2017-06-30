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

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public class ModuleInfoProcessorClassFileIssueTest
{
    ISonargraphSystemController m_controller;

    @Test
    public void validateIssues()
    {
        m_controller = new ControllerFactory().createController();
        final OperationResult result = m_controller.loadSystemReport(new File(TestFixture.TEST_REPORT_CLASSFILE_ISSUES));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final Map<String, IModule> moduleMap = m_controller.getSoftwareSystem().getModules();

        final IModule m1 = moduleMap.get("m1");
        assertNotNull("Module 'm1' not found", m1);

        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(m1);
        final Map<ISourceFile, List<IIssue>> issueMap = processor.getIssuesForSourceFiles(null);
        assertEquals("Wrong number of issues", 1, issueMap.size());
        assertEquals("Wrong file name", "Simple.java", issueMap.keySet().iterator().next().getName());
    }
}