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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.AnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IReportDelta;

public class ReportDifferenceProcessorTest
{
    private static final String BASELINE_REPORT = TestFixture.ALARM_CLOCK_BASELINE_PLUGIN_NOT_EXECUTED;
    private static final String CURRENT_REPORT = TestFixture.ALARM_CLOCK_PLUGIN_EXECUTED;

    @Test
    public void detectPluginAndAnalyzerConfigurationDelta()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result result = controller.loadSystemReport(new File(BASELINE_REPORT));
        assertTrue(result.toString(), result.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final ISonargraphSystemController controller2 = ControllerAccess.createController();
        final Result result2 = controller2.loadSystemReport(new File(CURRENT_REPORT));
        assertTrue(result2.toString(), result2.isSuccess());
        final ISystemInfoProcessor systemInfoProcessor = controller2.createSystemInfoProcessor();
        final IReportDelta delta = diffProcessor.createReportDelta(systemInfoProcessor);

        final List<IAnalyzer> addedAnalyzers = delta.getAddedAnalyzers();
        assertEquals("Wrong number of added analyzers", 0, addedAnalyzers.size());
        final BaselineCurrent<AnalyzerExecutionLevel> analyzerExecutionLevelDiff = delta.getAnalyzerExecutionLevelDiff();
        assertTrue("AnalyzerExecutionLevel must have changed", analyzerExecutionLevelDiff.hasChanged());

        //Check for differences in executed analyzers
        final List<IAnalyzer> additionallyExecutedAnalyzers = delta.getAddedAnalyzers(a -> a.isExecuted());
        assertEquals("Wrong number of additionally executed analyzers", 7, additionallyExecutedAnalyzers.size());
        final List<IAnalyzer> noLongerExecutedAnalyzers = delta.getRemovedAnalyzers(a -> a.isExecuted());
        assertEquals("Wrong number of no longer executed analyzers", 0, noLongerExecutedAnalyzers.size());

        //Check for differences in plugins
        assertEquals("No added plugins expected", 0, delta.getAddedPlugins(null).size());
        assertEquals("No removed plugins expected", 0, delta.getRemovedPlugins(null).size());

        final List<IPlugin> additionallyExecutedPlugins = delta.getAddedPlugins(p -> p.isExecuted());
        assertEquals("Wrong number of additionally executed plugins", 1, additionallyExecutedPlugins.size());
        assertEquals("Wrong additionally executed plugin", "SpotbugsPlugin", additionallyExecutedPlugins.get(0).getName());

        //Summary of delta is implemented by the toString() methods of the IDelta implementations
        //System.out.println("Delta Summary: \n" + delta);
    }
}