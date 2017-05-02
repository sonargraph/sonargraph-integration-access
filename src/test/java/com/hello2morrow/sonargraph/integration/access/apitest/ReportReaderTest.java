/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public class ReportReaderTest
{
    @Test
    public void processReportWithNoIssues()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ISSUES));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issues", 0, info.getIssues(null).size());
    }

    @Test
    public void processReportWithoutElements()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ELEMENTS));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 1, controller.getSoftwareSystem().getModules().size());
    }

    @Test
    public void processReportCreatedWith9_3()
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_9_3));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 4, controller.getSoftwareSystem().getModules().size());
    }

    @Test
    public void processCSharpReport() throws Exception
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.CSHARP_REPORT));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 4, controller.getSoftwareSystem().getModules().size());

        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of cycle issues", 86, info.getIssues(createUnresolvedIssueFilter("Cycle Group")).size());
        assertEquals("Wrong number of duplicates", 200, info.getIssues(createUnresolvedIssueFilter("Duplicate Code")).size());
        assertEquals("Wrong number of script based issues", 109, info.getIssues(createUnresolvedIssueFilter("Script Based")).size());
        assertEquals("Wrong number of threshold violations", 47, info.getIssues(createUnresolvedIssueFilter("Threshold Violation")).size());
        assertEquals("Wrong number of workspace issues", 1, info.getIssues(createUnresolvedIssueFilter("Workspace")).size());

        assertEquals("Wrong number of resolutions", 1, info.getResolutions((final IResolution r) -> r.getType() == ResolutionType.TODO).size());

        final IModule nhibernate = controller.getSoftwareSystem().getModule("NHibernate")
                .orElseThrow(() -> new Exception("Module NHibernate not found"));
        final IModuleInfoProcessor moduleProcessor = controller.createModuleInfoProcessor(nhibernate);
        assertEquals("Wrong number of namespace cycle groups", 2,
                moduleProcessor.getIssues((final IIssue i) -> i.getIssueType().getPresentationName().equals("Namespace Cycle Group")).size());
    }

    @Test
    public void processCppReport() throws Exception
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.CPP_REPORT));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 1, controller.getSoftwareSystem().getModules().size());

        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of cycle issues", 5, info.getIssues(createUnresolvedIssueFilter("Cycle Group")).size());
        assertEquals("Wrong number of duplicates", 4, info.getIssues(createUnresolvedIssueFilter("Duplicate Code")).size());
        assertEquals("Wrong number of 'no definition found'", 22,
                info.getIssues((final IIssue i) -> i.getIssueType().getPresentationName().equals("No definition found")).size());
        assertEquals("Wrong number of workspace issues", 22, info.getIssues(createUnresolvedIssueFilter("Workspace")).size());

        final IModule generic = controller.getSoftwareSystem().getModule("Generic").orElseThrow(() -> new Exception("Module 'Generic' not found"));
        final Map<String, INamedElement> cppMemberFunctions = generic.getElements("CppMemberFunction");
        assertTrue("no elements found", cppMemberFunctions.size() > 0);

        //printLogicalElements(cppMemberFunctions);

        final String functionFqName = "Workspace:Generic:./src:kernel:gsingltn:(header)./src/../include/gsingltn.h:class G_Singleton<T1>:G_Singleton(const char*, const char*)";
        assertNotNull("Logical element not found", cppMemberFunctions.get(functionFqName));
        final IModuleInfoProcessor moduleInfoProcessor = controller.createModuleInfoProcessor(generic);
        final IMetricLevel level = moduleInfoProcessor.getMetricLevel("Routine").get();
        final IMetricId metricId = moduleInfoProcessor.getMetricId(level, "CoreParameters").get();
        final IMetricValue value = moduleInfoProcessor.getMetricValueForElement(metricId, level, functionFqName).get();

        assertEquals("Wrong metric value", 2, value.getValue().intValue());
    }

    @Test
    public void processCppReportWithLogicalNamespaces() throws Exception
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult result = controller.loadSystemReport(new File(TestFixture.CPP_REPORT_HILO));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 4, controller.getSoftwareSystem().getModules().size());

        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of duplicates", 4, info.getIssues(createUnresolvedIssueFilter("Duplicate Code")).size());
        assertEquals("Wrong number of 'no definition found'", 5,
                info.getIssues((final IIssue i) -> i.getIssueType().getPresentationName().equals("No definition found")).size());
        assertEquals("Wrong number of workspace issues", 7, info.getIssues(createUnresolvedIssueFilter("Workspace")).size());

        final IModule browser = controller.getSoftwareSystem().getModule("Browser").orElseThrow(() -> new Exception("Module 'Browser' not found"));
        final Map<String, INamedElement> cppNamespaces = browser.getElements("CPlusPlusLogicalModuleNamespace");
        assertTrue("no elements found", cppNamespaces.size() > 0);

        //printLogicalElements(cppMemberFunctions);

        final IModuleInfoProcessor moduleInfoProcessor = controller.createModuleInfoProcessor(browser);
        final IMetricLevel level = moduleInfoProcessor.getMetricLevel("CppNamespace").orElseThrow(() -> new Exception("Metric level not found"));
        final IMetricId metricId = moduleInfoProcessor.getMetricId(level, "CoreTypesModule").orElseThrow(() -> new Exception("Metric not found"));
        assertEquals("Wrong number of types for module namespace", 22,
                moduleInfoProcessor.getMetricValueForElement(metricId, level, "Logical module namespaces:Browser:Hilo:AsyncLoader").get().getValue()
                        .intValue());
    }

    //    private void printLogicalElements(final Map<String, INamedElement> logicalModuleProgrammingElements)
    //    {
    //        for (final Map.Entry<String, INamedElement> next : logicalModuleProgrammingElements.entrySet())
    //        {
    //            System.out.println("fqName (key): " + next.getKey() + ", presentation name: " + next.getValue().getPresentationName());
    //        }
    //    }

    private Predicate<IIssue> createUnresolvedIssueFilter(final String categoryPresentationName)
    {
        return (final IIssue i) -> !i.hasResolution() && i.getIssueType().getCategory().getPresentationName().equals(categoryPresentationName);
    }
}