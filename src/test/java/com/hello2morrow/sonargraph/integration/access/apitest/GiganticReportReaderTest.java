/**
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;

public final class GiganticReportReaderTest
{
    /**
     * Usable only with a gigantic report being created locally that is > 100 MB.
     * Used to reproduce performance problem and test solution with XML validation switched off.
     */
    @Test
    @Ignore
    public void processReport()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result result = controller.loadSystemReport(new File("D:/08_test/Sonargraph.xml"));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issues", 1061991, info.getIssues((final IIssue i) -> true).size());
    }
}