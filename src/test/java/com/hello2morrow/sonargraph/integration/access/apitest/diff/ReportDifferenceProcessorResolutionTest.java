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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.function.Predicate;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.diff.IResolutionDelta;

public class ReportDifferenceProcessorResolutionTest
{
    private static final String REPORT_1 = TestFixture.TEST_REPORT_RESOLUTIONS_1;
    private static final String REPORT_2 = TestFixture.TEST_REPORT_RESOLUTIONS_2;

    @Test
    public void compareResolutions()
    {
        final IResolutionDelta delta = createDelta(REPORT_1, REPORT_2, null);
        assertNotNull("Delta missing", delta);
        assertEquals("Wrong number of added resolutions", 1, delta.getAdded().size());
        assertEquals("Wrong number of removed resolutions", 1, delta.getRemoved().size());
        assertEquals("Wrong number of changed resolutions", 1, delta.getChanged().size());
        assertEquals("Wrong number of unchanged resolutions", 1, delta.getUnchanged().size());
        //        System.out.println(delta);
    }

    @Test
    public void compareResolutionsWithFilter()
    {
        final Predicate<IResolution> filter = r -> r.getType() == ResolutionType.REFACTORING;
        final IResolutionDelta delta = createDelta(REPORT_1, REPORT_2, filter);
        assertNotNull("Delta missing", delta);
        assertEquals("Wrong number of added resolutions", 0, delta.getAdded().size());
        assertEquals("Wrong number of removed resolutions", 0, delta.getRemoved().size());
        assertEquals("Wrong number of changed resolutions", 0, delta.getChanged().size());
        assertEquals("Wrong number of unchanged resolutions", 1, delta.getUnchanged().size());
        // System.out.println(delta);
    }

    @Test
    public void compareEqualReports()
    {
        final IResolutionDelta delta = createDelta(REPORT_1, REPORT_1, null);
        assertNotNull("Delta missing", delta);
        assertEquals("Wrong number of added resolutions", 0, delta.getAdded().size());
        assertEquals("Wrong number of removed resolutions", 0, delta.getRemoved().size());
        assertEquals("Wrong number of changed resolutions", 0, delta.getChanged().size());
        assertEquals("Wrong number of unchanged resolutions", 3, delta.getUnchanged().size());
        //        System.out.println(delta);
    }

    private IResolutionDelta createDelta(final String report1, final String report2, final Predicate<IResolution> filter)
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult load1 = controller.loadSystemReport(new File(report1));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final OperationResult load2 = controller.loadSystemReport(new File(report2));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        final IResolutionDelta delta = diffProcessor.getResolutionDelta(infoProcessor2, filter);
        return delta;
    }
}
