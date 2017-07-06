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
import java.util.Optional;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public class ModuleInfoProcessorThresholdViolationTest
{
    ISonargraphSystemController m_controller;

    @Test
    public void validateThresholdIssues()
    {
        m_controller = new ControllerFactory().createController();
        final OperationResult result = m_controller.loadSystemReport(new File(TestFixture.TEST_REPORT_THRESHOLD_VIOLATIONS));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final Map<String, IModule> moduleMap = m_controller.getSoftwareSystem().getModules();

        final IModule moduleFoundation = moduleMap.get("Foundation");
        assertNotNull("Module 'Foundation' not found", moduleFoundation);
        validateSourceFileThresholdIssuesForModule(moduleFoundation, 1, 1, 54);

        final IModule moduleModel = moduleMap.get("Model");
        assertNotNull("Module 'Model' not found", moduleModel);
        validateSourceFileThresholdIssuesForModule(moduleModel, 1, 1, 54);

        final IModule moduleView = moduleMap.get("View");
        assertNotNull("Module 'View' not found", moduleView);
        validateSourceFileThresholdIssuesForModule(moduleView, 0, 0, -1);
    }

    private void validateSourceFileThresholdIssuesForModule(final IModule module, final int numberOfAllThresholdViolations,
            final int numberOfLocSourceFileViolations, final int metricValue)
    {
        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(module);
        final List<IThresholdViolationIssue> thresholdIssues = processor.getThresholdViolationIssues(null);
        assertEquals("Wrong number of issues", numberOfAllThresholdViolations, thresholdIssues.size());

        final Optional<IMetricLevel> sourceFileLevel = processor.getMetricLevel("SourceFile");
        assertTrue("Source file metric level does not exist", sourceFileLevel.isPresent());

        final List<IThresholdViolationIssue> linesOfCodeViolations = processor.getThresholdViolationIssues(th -> th.getThreshold().getMetricId()
                .getName().equals("CoreLinesOfCode")
                && th.getThreshold().getMetricLevel().getName().equals("SourceFile"));
        assertEquals("Wrong number of metric threshold issue", numberOfLocSourceFileViolations, linesOfCodeViolations.size());
        if (numberOfLocSourceFileViolations > 0)
        {
            assertEquals("Wrong metric value", metricValue, linesOfCodeViolations.get(0).getMetricValue().intValue());
        }
    }
}
