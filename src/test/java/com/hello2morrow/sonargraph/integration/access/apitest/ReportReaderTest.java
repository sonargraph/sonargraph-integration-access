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

public class ReportReaderTest
{

    @Test
    public void processReportWithNoIssues()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ISSUES));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issues", 0, info.getIssues((final IIssue i) -> true).size());
    }

    @Test
    public void processReportWithoutElements()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ELEMENTS));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 1, controller.getSoftwareSystem().getModules().size());
    }
}