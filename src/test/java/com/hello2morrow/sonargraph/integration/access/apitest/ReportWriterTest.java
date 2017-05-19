package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;

public class ReportWriterTest
{
    //    @Rule
    //    public TemporaryFolder m_reportDir = new TemporaryFolder(new File("./_temp"));

    private static final String MAIER_REPORT_FILE = "D:/temp/Maier_Report_Diff/sonargraph-report-941.xml";
    private static final String SMALL_REPORT = TestFixture.TEST_REPORT;
    private final File m_reportDir = new File("./_temp");

    private static final boolean IS_DEBUG = false;

    @Test
    public void writeReport()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final String reportFilePath = IS_DEBUG ? SMALL_REPORT : MAIER_REPORT_FILE;
        final OperationResult result = controller.loadSystemReport(new File(reportFilePath));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertTrue("Wrong number of issues", info.getIssues((final IIssue i) -> true).size() > 0);

        if (!IS_DEBUG)
        {
            assertEquals("Wrong number of issue types", 29, info.getIssueTypes().size());
        }
        final OperationResult writeResult = controller.writeSystemReport(new File(m_reportDir, "TestOutputReport.xml"));
        assertTrue(writeResult.toString(), writeResult.isSuccess());
    }

}
