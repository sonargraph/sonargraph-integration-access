package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
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
        final ISonargraphSystemController controller = new ControllerFactory().createController();
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
