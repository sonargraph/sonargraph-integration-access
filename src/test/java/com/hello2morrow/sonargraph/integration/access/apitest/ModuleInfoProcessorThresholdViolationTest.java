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
