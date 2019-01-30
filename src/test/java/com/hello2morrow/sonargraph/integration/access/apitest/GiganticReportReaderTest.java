/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;

public final class GiganticReportReaderTest
{
    /**
     * Usable only with a gigantic report being created locally that is > 100 MB. Used to reproduce performance problem and test solution with XML
     * validation switched off.
     */

    /* Output with jaxb-impl (javax.xml.bind.context.factory=com.sun.xml.bind.v2.ContextFactory) */
    /*
    Check performance of loading D:\07_test\Sonargraph_Snapshots\Sonargraph-standard.xml, size in bytes: 416180
    2019-01-18 14:58:54,400 INFO [main|com.hello2morrow.sonargraph.integration.access.persistence.JaxbAdapter] Using JAXBContext implementation: com.sun.xml.bind.v2.runtime.JAXBContextImpl
    1. time needed to read report: 535
    2. time needed to read report: 68
    3. time needed to read report: 51
    4. time needed to read report: 44
    5. time needed to read report: 37
    6. time needed to read report: 34
    7. time needed to read report: 31
    8. time needed to read report: 32
    9. time needed to read report: 31
    10. time needed to read report: 28


    Check performance of loading D:\07_test\Sonargraph_Snapshots\Sonargraph-all-metrics.xml, size in bytes: 58120952
    1. time needed to read report: 1654
    2. time needed to read report: 1698
    3. time needed to read report: 1504
    4. time needed to read report: 1888
    5. time needed to read report: 1438
    6. time needed to read report: 1524
    7. time needed to read report: 1367
    8. time needed to read report: 1613
    9. time needed to read report: 1547
    10. time needed to read report: 1377


    Check performance of loading D:\07_test\Sonargraph_Snapshots\Sonargraph_lots-of-issues.xml, size in bytes: 77935494
    1. time needed to read report: 2064
    2. time needed to read report: 2077
    3. time needed to read report: 1848
    4. time needed to read report: 2175
    5. time needed to read report: 1864
    6. time needed to read report: 2345
    7. time needed to read report: 2181
    8. time needed to read report: 1929
    9. time needed to read report: 2115
    10. time needed to read report: 2025
    */

    @Test
    @Ignore
    public void performanceTest()
    {
        final int iterations = 10;
        perform(iterations, new File("D:/07_test/Sonargraph_Snapshots/Sonargraph-standard.xml"));
        perform(iterations, new File("D:/07_test/Sonargraph_Snapshots/Sonargraph-all-metrics.xml"));
        perform(iterations, new File("D:/07_test/Sonargraph_Snapshots/Sonargraph_lots-of-issues.xml"));
    }

    private void perform(final int times, final File reportFile)
    {
        System.out.println("Check performance of loading " + reportFile.getAbsolutePath() + ", size in bytes: " + reportFile.length());
        for (int i = 0; i < times; i++)
        {
            final long start = System.currentTimeMillis();
            final ISonargraphSystemController controller = ControllerAccess.createController();
            final Result result = controller.loadSystemReport(reportFile);
            assertTrue(result.toString(), result.isSuccess());
            System.out.println((i + 1) + ". time needed to read report: " + (System.currentTimeMillis() - start));
        }
        System.out.println("\n");
    }
}
