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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.diff.Diff;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;

public class ReportDifferenceProcessorIssuesTest
{
    private static final String PERCENTAGE_OF_DEAD_CODE = "Percentage of dead code";
    private static final String CORE_TOTAL_LINES = "CoreTotalLines";
    private static final String SYSTEM_REPORT = "./src/test/diff/AlarmClockMain_all.xml";
    private static final String SYSTEM_REPORT_1 = "./src/test/diff/AlarmClockMain_01.xml";
    private static final String SYSTEM_REPORT_2 = "./src/test/diff/AlarmClockMain_02.xml";

    private static final String LARGE_REPORT_1 = "./src/test/diff/LargeReport_1.xml";
    private static final String LARGE_REPORT_2 = "./src/test/diff/LargeReport_2.xml";

    @Test
    public void compareEqualReports()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(SYSTEM_REPORT));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor1 = controller.createReportDifferenceProcessor();

        final Result load2 = controller.loadSystemReport(new File(SYSTEM_REPORT));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        final IIssueDelta delta = diffProcessor1.getIssueDelta(infoProcessor2, null);
        assertEquals("Wrong number of added issues", 0, delta.getAdded().size());
        assertEquals("Wrong number of removed issues", 0, delta.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 25, delta.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 0, delta.getWorse().size());
        assertEquals("Wrong number of improved issues", 0, delta.getImproved().size());
    }

    @Test
    public void compareIssuesInSmallReports()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(SYSTEM_REPORT));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor1 = controller.createReportDifferenceProcessor();

        final Result load2 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
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

        final Result load3 = controller.loadSystemReport(new File(SYSTEM_REPORT_2));
        assertTrue(load3.toString(), load3.isSuccess());

        final ISystemInfoProcessor infoProcessor3 = controller.createSystemInfoProcessor();
        final IIssueDelta delta2 = diffProcessor1.getIssueDelta(infoProcessor3, null);
        assertEquals("Wrong number of added issues", 0, delta2.getAdded().size());
        assertEquals("Wrong number of removed issues", 14, delta2.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 10, delta2.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 1, delta2.getWorse().size());
        final IThresholdViolationIssue worse2 = (IThresholdViolationIssue) delta2.getWorse().get(0).getFirst();
        assertEquals("Wrong worse issue", CORE_TOTAL_LINES, worse2.getThreshold().getMetricId().getName());
        assertEquals("Wrong number of improved issues", 0, delta2.getImproved().size());

        final Result load4 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
        assertTrue(load4.toString(), load4.isSuccess());
        final IReportDifferenceProcessor diffProcessor2 = controller.createReportDifferenceProcessor();
        final IIssueDelta delta3 = diffProcessor2.getIssueDelta(infoProcessor3, null);
        assertEquals("Wrong number of added issues", 6, delta3.getAdded().size());
        assertEquals("Wrong number of removed issues", 14, delta3.getRemoved().size());
        assertEquals("Wrong number of unchanged issues", 3, delta3.getUnchanged().size());
        assertEquals("Wrong number of worsened issues", 1, delta3.getWorse().size());
        final IThresholdViolationIssue worse3 = (IThresholdViolationIssue) delta3.getWorse().get(0).getFirst();
        assertEquals("Wrong worse issue", CORE_TOTAL_LINES, worse3.getThreshold().getMetricId().getName());
        assertEquals("Wrong number of improved issues", 1, delta3.getImproved().size());
        final IThresholdViolationIssue improved3 = (IThresholdViolationIssue) delta3.getImproved().get(0).getFirst();
        assertEquals("Wrong improved issue", PERCENTAGE_OF_DEAD_CODE, improved3.getThreshold().getMetricId().getName());
    }

    //This test can be used to test the performance as it compares reports of ~50 MB size.
    @Ignore
    @Test
    public void compareIssuesOfLargeReports()
    {
        final String largeReport1 = LARGE_REPORT_1;
        final String largeReport2 = LARGE_REPORT_2;

        final long start1 = System.currentTimeMillis();
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(largeReport1));
        System.out.println("Time needed to load first report: " + (System.currentTimeMillis() - start1));
        assertTrue(load1.toString(), load1.isSuccess());
        final ISystemInfoProcessor info1 = controller.createSystemInfoProcessor();
        System.out.println("Number of issues in report1: " + info1.getIssues(null).size());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final long start2 = System.currentTimeMillis();
        final Result load2 = controller.loadSystemReport(new File(largeReport2));
        assertTrue(load2.toString(), load2.isSuccess());
        System.out.println("Time needed to load second report: " + (System.currentTimeMillis() - start2));

        final ISystemInfoProcessor info2 = controller.createSystemInfoProcessor();
        System.out.println("Number of issues in report2: " + info2.getIssues(null).size());
        final long start3 = System.currentTimeMillis();
        final IIssueDelta delta = diffProcessor.getIssueDelta(info2, null);
        System.out.println("Time needed to create delta: " + (System.currentTimeMillis() - start3));

        assertEquals("Wrong number of unchanged", 70262, delta.getUnchanged().size());
        assertEquals("Wrong number of removed", 10, delta.getRemoved().size());
        assertEquals("Wrong number of added", 10, delta.getAdded().size());

        final long start4 = System.currentTimeMillis();

        int newIssueCounter = 0;
        for (final IIssue next : info2.getIssues(null))
        {
            final Diff change = diffProcessor.determineChange(next);
            if (change == Diff.NO_MATCH_FOUND)
            {
                newIssueCounter++;
            }
        }
        System.out.println("Time needed to check issues individually: " + (System.currentTimeMillis() - start4));
        assertEquals("Wrong number of new issues", 10, newIssueCounter);

        System.out.println("\nTime needed for test execution:" + (System.currentTimeMillis() - start1));
    }

    @Test
    public void checkIndividualIssues()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(SYSTEM_REPORT_1));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final Result load2 = controller.loadSystemReport(new File(SYSTEM_REPORT_2));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        int newIssueCounter = 0;
        for (final IIssue next : infoProcessor2.getIssues(null))
        {
            if (next instanceof IDependencyIssue)
            {
                assertEquals("Issue must be new: " + next, Diff.NO_MATCH_FOUND, diffProcessor.determineChange(next));
                newIssueCounter++;
            }
            else if (next instanceof ThresholdViolationIssue || next instanceof ICycleGroupIssue)
            {
                assertFalse("Issue must not be new: " + next, Diff.NO_MATCH_FOUND == diffProcessor.determineChange(next));
            }
        }
        assertEquals("Wrong number of new issues", 6, newIssueCounter);
    }
}
