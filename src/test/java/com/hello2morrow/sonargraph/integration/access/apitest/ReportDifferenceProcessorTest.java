package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.model.Diff;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;

public class ReportDifferenceProcessorTest
{
    private static final String PERCENTAGE_OF_DEAD_CODE = "Percentage of dead code";
    private static final String CORE_TOTAL_LINES = "CoreTotalLines";
    private static final String SYSTEM_REPORT = "./src/test/diff/AlarmClockMain_all.xml";
    private static final String SYSTEM_REPORT_1 = "./src/test/diff/AlarmClockMain_01.xml";
    private static final String SYSTEM_REPORT_2 = "./src/test/diff/AlarmClockMain_02.xml";

    private static final String LARGE_REPORT_1 = "./src/test/diff/LargeReport_1.xml";
    private static final String LARGE_REPORT_2 = "./src/test/diff/LargeReport_2.xml";

    private static final String LARGE_FULL_REPORT_1 = "./src/test/diff/LargeReport_with_Metrics_1.xml";
    private static final String LARGE_FULL_REPORT_2 = "./src/test/diff/LargeReport_with_Metrics_2.xml";

    @Test
    public void compareIssuesInSmallReports()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult load1 = controller.loadSystemReport(new File(SYSTEM_REPORT));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor1 = controller.createReportDifferenceProcessor();

        final OperationResult load2 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        final IIssueDelta delta = diffProcessor1.getIssueDelta(infoProcessor2, null);
        assertEquals("Wrong number of added issues", 0, delta.getAdded().size());
        assertEquals("Wrong number of removed issues", 6, delta.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 17, delta.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 1, delta.getWorse().size());
        final IThresholdViolationIssue worse = (IThresholdViolationIssue) delta.getWorse().get(0).getFirst();
        assertEquals("Wrong worse issue", PERCENTAGE_OF_DEAD_CODE, worse.getThreshold().getMetricId().getName());
        assertEquals("Wrong number of improved issues", 1, delta.getImproved().size());
        final IThresholdViolationIssue improved = (IThresholdViolationIssue) delta.getImproved().get(0).getFirst();
        assertEquals("Wrong improved issue", CORE_TOTAL_LINES, improved.getThreshold().getMetricId().getName());

        final OperationResult load3 = controller.loadSystemReport(new File(SYSTEM_REPORT_2));
        assertTrue(load3.toString(), load3.isSuccess());

        final ISystemInfoProcessor infoProcessor3 = controller.createSystemInfoProcessor();
        final IIssueDelta delta2 = diffProcessor1.getIssueDelta(infoProcessor3, null);
        assertEquals("Wrong number of added issues", 0, delta2.getAdded().size());
        assertEquals("Wrong number of removed issues", 13, delta2.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 11, delta2.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 1, delta2.getWorse().size());
        final IThresholdViolationIssue worse2 = (IThresholdViolationIssue) delta2.getWorse().get(0).getFirst();
        assertEquals("Wrong worse issue", CORE_TOTAL_LINES, worse2.getThreshold().getMetricId().getName());
        assertEquals("Wrong number of improved issues", 0, delta2.getImproved().size());

        final OperationResult load4 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
        assertTrue(load4.toString(), load4.isSuccess());
        final IReportDifferenceProcessor diffProcessor2 = controller.createReportDifferenceProcessor();
        final IIssueDelta delta3 = diffProcessor2.getIssueDelta(infoProcessor3, null);
        assertEquals("Wrong number of added issues", 6, delta3.getAdded().size());
        assertEquals("Wrong number of removed issues", 13, delta3.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 4, delta3.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 1, delta3.getWorse().size());
        final IThresholdViolationIssue worse3 = (IThresholdViolationIssue) delta3.getWorse().get(0).getFirst();
        assertEquals("Wrong worse issue", CORE_TOTAL_LINES, worse3.getThreshold().getMetricId().getName());
        assertEquals("Wrong number of improved issues", 1, delta3.getImproved().size());
        final IThresholdViolationIssue improved3 = (IThresholdViolationIssue) delta3.getImproved().get(0).getFirst();
        assertEquals("Wrong improved issue", PERCENTAGE_OF_DEAD_CODE, improved3.getThreshold().getMetricId().getName());
    }

    @Test
    public void compareIssuesOfLargeReports()
    {
        final String largeReport1 = LARGE_REPORT_1;
        final String largeReport2 = LARGE_REPORT_2;

        compareReports(largeReport1, largeReport2);
    }

    @Test
    public void compareMetricsOfReports()
    {
        final String largeReport1 = LARGE_FULL_REPORT_1;
        final String largeReport2 = LARGE_FULL_REPORT_2;

        compareReports(largeReport1, largeReport2);
    }

    private void compareReports(final String largeReport1, final String largeReport2)
    {
        final long start1 = System.currentTimeMillis();
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult load1 = controller.loadSystemReport(new File(largeReport1));
        System.out.println("Time needed to load first report: " + (System.currentTimeMillis() - start1));
        assertTrue(load1.toString(), load1.isSuccess());
        final ISystemInfoProcessor info1 = controller.createSystemInfoProcessor();
        System.out.println("Number of issues in report1: " + info1.getIssues(null).size());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final long start2 = System.currentTimeMillis();
        final OperationResult load2 = controller.loadSystemReport(new File(largeReport2));
        assertTrue(load2.toString(), load2.isSuccess());
        System.out.println("Time needed to load second report: " + (System.currentTimeMillis() - start2));

        final ISystemInfoProcessor info2 = controller.createSystemInfoProcessor();
        System.out.println("Number of issues in report2: " + info2.getIssues(null).size());
        final long start3 = System.currentTimeMillis();
        final IIssueDelta delta = diffProcessor.getIssueDelta(info2, null);
        System.out.println("Time needed to create delta: " + (System.currentTimeMillis() - start3));

        //TODO:
        //        assertEquals("Wrong number of unchanged", 70262, delta.getUnchangedIssues().size());
        //        assertEquals("Wrong number of removed", 10, delta.getRemovedIssues().size());
        //        assertEquals("Wrong number of added", 10, delta.getAddedIssues().size());

        final long start4 = System.currentTimeMillis();

        int newIssueCounter = 0;
        for (final IIssue next : info2.getIssues(null))
        {
            final Diff change = diffProcessor.determineChange(next);
            if (diffProcessor.isNewIssue(next))
            {
                newIssueCounter++;
            }
        }
        System.out.println("Time needed to check issues individually: " + (System.currentTimeMillis() - start4));
        //TODO
        //        assertEquals("Wrong number of new issues", 10, newIssueCounter);

        System.out.println("\nTime needed for test execution:" + (System.currentTimeMillis() - start1));
    }

    @Test
    public void checkIndividualIssues()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult load1 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final OperationResult load2 = controller.loadSystemReport(new File(SYSTEM_REPORT_2));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        int newIssueCounter = 0;
        for (final IIssue next : infoProcessor2.getIssues(null))
        {
            if (next instanceof IDependencyIssue)
            {
                assertTrue("Issue must be new: " + next, diffProcessor.isNewIssue(next));
                newIssueCounter++;
            }
            else if (next instanceof ThresholdViolationIssue || next instanceof ICycleGroup)
            {
                assertFalse("Issue must not be new: " + next, diffProcessor.isNewIssue(next));
            }
        }
        assertEquals("Wrong number of new issues", 6, newIssueCounter);
    }
}
