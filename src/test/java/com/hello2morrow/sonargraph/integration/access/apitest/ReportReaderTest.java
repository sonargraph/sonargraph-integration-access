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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.foundation.TestUtility;
import com.hello2morrow.sonargraph.integration.access.model.AnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IComponentFilter;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDeleteRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IFilter;
import com.hello2morrow.sonargraph.integration.access.model.IIgnoreResolution;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId.SortDirection;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IMoveRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.IMoveRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.IToDoResolution;
import com.hello2morrow.sonargraph.integration.access.model.IWildcardPattern;
import com.hello2morrow.sonargraph.integration.access.model.PluginExecutionPhase;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PluginExternalImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ProgrammingElementImpl;

public final class ReportReaderTest
{
    @Test
    public void processReportWithCycleGroup()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_INTEGRATION_ACCESS_WITH_CYCLE_GROUP));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issues", 11, info.getIssues(null).size());
    }

    @Test
    public void processReportWithNoIssues()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ISSUES));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issues", 0, info.getIssues(null).size());
    }

    @Test
    public void processReportWithoutElements()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITHOUT_ELEMENTS));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 1, controller.getSoftwareSystem().getModules().size());
    }

    @Test
    public void processReportCreatedWith9_3()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_9_3));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 4, controller.getSoftwareSystem().getModules().size());
    }

    private Predicate<IIssue> createUnresolvedIssueFilter(final String categoryPresentationName)
    {
        return (final IIssue i) -> !i.hasResolution() && i.getIssueType().getCategory().getPresentationName().equals(categoryPresentationName);
    }

    @Test
    public void processCSharpReport() throws Exception
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.CSHARP_REPORT));
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
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.CPP_REPORT));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 1, controller.getSoftwareSystem().getModules().size());

        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of cycle issues", 5, info.getIssues(createUnresolvedIssueFilter("Cycle Group")).size());
        assertEquals("Wrong number of duplicates", 4, info.getIssues(createUnresolvedIssueFilter("Duplicate Code")).size());
        assertEquals("Wrong number of 'no definition found'", 22,
                info.getIssues((final IIssue i) -> i.getIssueType().getPresentationName().equals("No definition found")).size());
        assertEquals("Wrong number of workspace issues", 22, info.getIssues(createUnresolvedIssueFilter("Workspace")).size());

        final IModule generic = controller.getSoftwareSystem().getModule("Generic").orElseThrow(() -> new Exception("Module 'Generic' not found"));
        final Map<String, INamedElement> cppMemberFunctions = TestUtility.getFqNameToNamedElement(generic, "CppMemberFunction");
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
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.CPP_REPORT_HILO));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 4, controller.getSoftwareSystem().getModules().size());

        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        assertEquals("Wrong number of duplicates", 4, info.getIssues(createUnresolvedIssueFilter("Duplicate Code")).size());
        assertEquals("Wrong number of 'no definition found'", 5,
                info.getIssues((final IIssue i) -> i.getIssueType().getPresentationName().equals("No definition found")).size());
        assertEquals("Wrong number of workspace issues", 7, info.getIssues(createUnresolvedIssueFilter("Workspace")).size());

        final IModule browser = controller.getSoftwareSystem().getModule("Browser").orElseThrow(() -> new Exception("Module 'Browser' not found"));
        final Map<String, INamedElement> cppNamespaces = TestUtility.getFqNameToNamedElement(browser, "CPlusPlusLogicalModuleNamespace");
        assertTrue("no elements found", cppNamespaces.size() > 0);

        //printLogicalElements(cppMemberFunctions);

        final IModuleInfoProcessor moduleInfoProcessor = controller.createModuleInfoProcessor(browser);
        final IMetricLevel level = moduleInfoProcessor.getMetricLevel("CppNamespace").orElseThrow(() -> new Exception("Metric level not found"));
        final IMetricId metricId = moduleInfoProcessor.getMetricId(level, "CoreTypesModule").orElseThrow(() -> new Exception("Metric not found"));
        assertEquals("Wrong number of types for module namespace", 22, moduleInfoProcessor
                .getMetricValueForElement(metricId, level, "Logical module namespaces:Browser:Hilo:AsyncLoader").get().getValue().intValue());
    }

    @Test
    public void processClassFileIssuesReport()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.ALARM_CLOCK_CLASS_FILE_ISSUES_REPORT));
        assertTrue(result.toString(), result.isSuccess());
    }

    @Test
    public void testReportStandard()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_STANDARD));
        assertTrue(result.toString(), result.isSuccess());
    }

    @Test
    public void testDirectoryIssues()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITH_DERIVED));
        assertTrue(result.toString(), result.isSuccess());
        assertEquals("Wrong number of modules", 2, controller.getSoftwareSystem().getModules().size());

        final Map<String, Map<String, List<IIssue>>> moduleToIssues = new HashMap<>();

        final ISystemInfoProcessor systemInfoProcessor = controller.createSystemInfoProcessor();
        for (final IModule nextModule : systemInfoProcessor.getModules().values())
        {
            final IModuleInfoProcessor nextModuleInfoProcessor = controller.createModuleInfoProcessor(nextModule);
            final Map<String, List<IIssue>> issueMap = nextModuleInfoProcessor
                    .getIssuesForDirectories(issue -> !issue.isIgnored() && !"Workspace".equals(issue.getIssueType().getCategory().getName()));
            moduleToIssues.put(nextModule.getName(), issueMap);
        }

        final Map<String, List<IIssue>> m1IssueMap = moduleToIssues.get("M1");
        assertNotNull("Module M1 not found", m1IssueMap);
        assertTrue("3 directories with issues expected for module M1", m1IssueMap.size() == 3);
        final Map<String, List<IIssue>> m5IssueMap = moduleToIssues.get("M5");
        assertNotNull("Module M5 not found", m5IssueMap);
        assertTrue("1 directory with issue expected for module M5", m5IssueMap.size() == 1);
    }

    @Test
    public void testReportWithPackageTodo()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITH_PACKAGE_TODO));
        assertTrue(result.toString(), result.isSuccess());

        final Map<IModuleInfoProcessor, Map<String, List<IIssue>>> moduleToIssues = new HashMap<>();

        final ISystemInfoProcessor systemInfoProcessor = controller.createSystemInfoProcessor();
        for (final IModule nextModule : systemInfoProcessor.getModules().values())
        {
            final IModuleInfoProcessor nextModuleInfoProcessor = controller.createModuleInfoProcessor(nextModule);
            final Map<String, List<IIssue>> issueMap = nextModuleInfoProcessor
                    .getIssuesForDirectories(issue -> !issue.isIgnored() && !"Workspace".equals(issue.getIssueType().getCategory().getName()));
            moduleToIssues.put(nextModuleInfoProcessor, issueMap);
        }

        assertTrue("1 module expected", moduleToIssues.size() == 1);

        for (final Entry<IModuleInfoProcessor, Map<String, List<IIssue>>> nextEntry : moduleToIssues.entrySet())
        {
            final Map<String, List<IIssue>> issueMap = nextEntry.getValue();
            assertTrue("1 directory with issue expected", issueMap.size() == 1);
            for (final Entry<String, List<IIssue>> nextIssueMapEntry : issueMap.entrySet())
            {
                for (final IIssue nextIssue : nextIssueMapEntry.getValue())
                {
                    final IResolution nextResolution = nextEntry.getKey().getResolution(nextIssue);
                    assertNotNull("Resolution expected", nextResolution);
                }
            }
        }
    }

    @Test
    public void checkForCycleMetrics()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_WITH_CYCLE_METRICS));
        assertTrue(result.toString(), result.isSuccess());

        final ISystemInfoProcessor infoProcessor = controller.createSystemInfoProcessor();
        {
            final List<ICycleGroupIssue> packageCycles = getCycleGroup(infoProcessor, "NamespaceCycleGroup");
            assertEquals("Wrong number of package cycles ", 1, packageCycles.size());
            final ICycleGroupIssue packageCycle = packageCycles.get(0);
            assertEquals("Wrong number of cyclic elements", 2, packageCycle.getAffectedNamedElements().size());
            assertEquals("Wrong structural debt index", 13, packageCycle.getStructuralDebtIndex());
            assertEquals("Wrong component dependencies to remove", 1, packageCycle.getComponentDependenciesToRemove());
            assertEquals("Wrong parser dependencies to remove", 3, packageCycle.getParserDependenciesToRemove());
        }
        {
            final List<ICycleGroupIssue> componentCycles = getCycleGroup(infoProcessor, "ComponentCycleGroup");
            assertEquals("Wrong number of component cycles ", 1, componentCycles.size());
            final ICycleGroupIssue componentCycle = componentCycles.get(0);
            assertEquals("Wrong number of cyclic elements", 2, componentCycle.getAffectedNamedElements().size());
            assertEquals("Wrong structural debt index", 13, componentCycle.getStructuralDebtIndex());
            assertEquals("Wrong component dependencies to remove", 1, componentCycle.getComponentDependenciesToRemove());
            assertEquals("Wrong parser dependencies to remove", 3, componentCycle.getParserDependenciesToRemove());
        }
    }

    private List<ICycleGroupIssue> getCycleGroup(final ISystemInfoProcessor infoProcessor, final String issueTypeName)
    {
        return infoProcessor.getIssues(i -> i.getIssueType().getName().equals(issueTypeName)).stream().map(i -> (ICycleGroupIssue) i)
                .collect(Collectors.toList());
    }

    @Test
    public void processReportWithPluginInfoAndAnalyzerExecutionLevel()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_PLUGINS));
        assertTrue(result.toString(), result.isSuccess());

        final ISystemInfoProcessor systemInfoProcessor = controller.createSystemInfoProcessor();
        final ISoftwareSystem softwareSystem = systemInfoProcessor.getSoftwareSystem();
        final Map<String, IPlugin> plugins = softwareSystem.getPlugins();
        assertEquals("Wrong number of plugins", 2, plugins.size());

        final IPlugin swagger = plugins.get("SwaggerPlugin");
        assertNotNull("Swagger plugin not found", swagger);
        assertEquals("Wrong name", "SwaggerPlugin", swagger.getName());
        assertEquals("Wrong presentation name", "Swagger Plugin", swagger.getPresentationName());
        assertEquals("Wrong description", "Plugin that exposes webresources and dependencies between them.", swagger.getDescription());
        assertEquals("Wrong version", "9.9.2.533_2018-12-12", swagger.getVersion());
        assertEquals("Wrong vendor", "hello2morrow GmbH", swagger.getVendor());
        assertFalse("Must not be enabled", swagger.isEnabled());
        assertTrue("Must not be executed", swagger.getActiveExecutionPhases().isEmpty());
        assertEquals("Wrong type of available execution phase", EnumSet.of(PluginExecutionPhase.MODEL), swagger.getSupportedExecutionPhases());
        assertTrue("Must be licensed", swagger.isLicensed());

        final IPlugin spotbugsPlugin = plugins.get("SpotbugsPlugin");
        assertNotNull("Spotbugs plugin not found", spotbugsPlugin);
        assertEquals("Wrong type of available execution phases", EnumSet.of(PluginExecutionPhase.ANALYZER),
                spotbugsPlugin.getSupportedExecutionPhases());

        assertEquals("Wrong analyzer execution level", AnalyzerExecutionLevel.ADVANCED,
                systemInfoProcessor.getSoftwareSystem().getAnalyzerExecutionLevel());
        final Map<String, IAnalyzer> analyzers = softwareSystem.getAnalyzers();
        final IAnalyzer spotbugs = analyzers.get("SpotbugsPlugin");
        assertNotNull("Spotbugs analyzer must exist", spotbugs);
        assertTrue("Must be licensed", spotbugs.isLicensed());
        assertFalse("Must not be executed", spotbugs.isExecuted());
    }

    @Test
    public void processWorkspaceFilters()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.ALARM_CLOCK_WITH_WORKSPACE_FILTERS));
        assertTrue(result.toString(), result.isSuccess());

        final ISystemInfoProcessor systemInfoProcessor = controller.createSystemInfoProcessor();
        final ISoftwareSystem softwareSystem = systemInfoProcessor.getSoftwareSystem();
        {
            final Optional<IFilter> workspaceFilterOpt = softwareSystem.getWorkspaceFilter();
            assertTrue("Workspace filter must exist", workspaceFilterOpt.isPresent());
            final IFilter workspaceFilter = workspaceFilterOpt.get();
            assertEquals("Wrong description", "Exclude files from the workspace", workspaceFilter.getDescription());
            assertEquals("Wrong information", "Excluded 0 files(s)", workspaceFilter.getInformation());
            assertEquals("Wrong number of excluded elements", 0, workspaceFilter.getNumberOfExcludedElements());
            final List<IWildcardPattern> includePatterns = workspaceFilter.getIncludePatterns();
            assertEquals("Wrong number of include patterns", 1, includePatterns.size());
            final IWildcardPattern include = includePatterns.get(0);
            assertEquals("Wrong pattern", "**", include.getPattern());
            assertEquals("Wrong number of matched elements", 21, include.getNumberOfMatches());
            final List<IWildcardPattern> excludePatterns = workspaceFilter.getExcludePatterns();
            assertEquals("Wrong number of exclude patterns", 1, excludePatterns.size());
            final IWildcardPattern exclude = excludePatterns.get(0);
            assertEquals("Wrong pattern", "**/bla/**", exclude.getPattern());
            assertEquals("Wrong number of matched elements", 0, exclude.getNumberOfMatches());
        }
        {
            final Optional<IComponentFilter> productionCodeFilterOpt = softwareSystem.getProductionCodeFilter();
            assertTrue("Production code filter must exist", productionCodeFilterOpt.isPresent());
            final IComponentFilter productionCodeFilter = productionCodeFilterOpt.get();
            assertEquals("Wrong description", "Exclude internal components containing test code", productionCodeFilter.getDescription());
            assertEquals("Wrong information", "Excluded 1 internal component(s) (processed 10)", productionCodeFilter.getInformation());
            assertEquals("Wrong number of included elements", 9, productionCodeFilter.getNumberOfIncludedElements());
            assertEquals("Wrong number of excluded elements", 1, productionCodeFilter.getNumberOfExcludedElements());
            final List<IWildcardPattern> includePatterns = productionCodeFilter.getIncludePatterns();
            assertEquals("Wrong number of include patterns", 1, includePatterns.size());
            final IWildcardPattern include = includePatterns.get(0);
            assertEquals("Wrong pattern", "**", include.getPattern());
            assertEquals("Wrong number of matched elements", 10, include.getNumberOfMatches());
            final List<IWildcardPattern> excludePatterns = productionCodeFilter.getExcludePatterns();
            assertEquals("Wrong number of exclude patterns", 1, excludePatterns.size());
            final IWildcardPattern exclude = excludePatterns.get(0);
            assertEquals("Wrong number of matched elements", 1, exclude.getNumberOfMatches());
            assertEquals("Wrong pattern", "**/test/java/**1", exclude.getPattern());
        }
        {
            final Optional<IComponentFilter> issueFilterOpt = softwareSystem.getIssueFilter();
            assertTrue("Issue filter must exist", issueFilterOpt.isPresent());
            final IComponentFilter issueFilter = issueFilterOpt.get();
            assertEquals("Wrong description", "Ignore issues of internal components containing legacy/generated code", issueFilter.getDescription());
            assertEquals("Wrong information", "Ignoring issues of 2 internal component(s) (processed 9)", issueFilter.getInformation());
            assertEquals("Wrong number of included elements", 7, issueFilter.getNumberOfIncludedElements());
            assertEquals("Wrong number of excluded elements", 2, issueFilter.getNumberOfExcludedElements());
            final List<IWildcardPattern> includePatterns = issueFilter.getIncludePatterns();
            assertEquals("Wrong number of include patterns", 1, includePatterns.size());
            final IWildcardPattern include = includePatterns.get(0);
            assertEquals("Wrong pattern", "**/*", include.getPattern());
            assertEquals("Wrong number of matched elements", 9, include.getNumberOfMatches());
            final List<IWildcardPattern> excludePatterns = issueFilter.getExcludePatterns();
            assertEquals("Wrong number of exclude patterns", 1, excludePatterns.size());
            final IWildcardPattern exclude = excludePatterns.get(0);
            assertEquals("Wrong number of matched elements", 2, exclude.getNumberOfMatches());
            assertEquals("Wrong pattern", "**/test/java/**", exclude.getPattern());
        }
    }

    @Test
    public void processSystemMetaData()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_SYSTEM_METADATA));
        assertTrue(result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertNotNull("Missing softwareSystem", softwareSystem);
        final Map<String, String> metaData = softwareSystem.getMetaData();
        assertEquals("Wrong value", "hello2morrow", metaData.get("organisation"));
    }

    @Test
    public void processMetricMinMaxInformation()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_METRIC_METADATA));
        assertTrue(result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertNotNull("Missing softwareSystem", softwareSystem);
        final ISystemInfoProcessor systemProcessor = controller.createSystemInfoProcessor();
        final List<IMetricId> metricIds = systemProcessor.getMetricIds();
        assertEquals("Wrong number of metrics", 95, metricIds.size());
        validateMetricInfo(systemProcessor, "CoreAcd", 0.0, Double.POSITIVE_INFINITY, SortDirection.HIGHER_WORSE);
        validateMetricInfo(systemProcessor, "JavaCyclicityPackages", 0.0, Double.POSITIVE_INFINITY, SortDirection.HIGHER_WORSE);
        final IMetricId distance = validateMetricInfo(systemProcessor, "CoreDistanceSystem", -1.0, 1.0, SortDirection.OPTIMUM_AT_ZERO);
        assertEquals("Wrong best value", 0.0, distance.getBest(), 0.001);
        validateMetricInfo(systemProcessor, "CoreLinesOfCode", 0.0, Double.POSITIVE_INFINITY, SortDirection.INDIFFERENT);
    }

    @Test
    public void processReportWithPluginExternalIssues()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_PLUGIN_EXTERNAL));
        assertTrue(result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertNotNull("Missing softwareSystem", softwareSystem);
        final ISystemInfoProcessor systemProcessor = controller.createSystemInfoProcessor();
        final List<IResolution> todos = systemProcessor.getResolutions(r -> r.getType() == ResolutionType.TODO);
        assertEquals("Wrong number of todos", 3, todos.size());
        int index = 0;
        assertExternalTodo(todos.get(index++), "Swagger Todo", ProgrammingElementImpl.class,
                "Workspace:External [com.hello2morrow.sonargraph.plugin.swagger]:/pet/{petId}[DELETE]");
        assertExternalTodo(todos.get(index++), "Plugin External Todo", PluginExternalImpl.class,
                "Workspace:External [com.hello2morrow.sonargraph.plugin.swagger]");
        assertExternalTodo(todos.get(index), "TODO on Java External", NamedElementImpl.class, "Workspace:External [Java]:[Unknown]:java:io:File");
    }

    @Test
    public void processReportWithResolutionPatterns()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_RESOLUTION_PATTERNS));
        assertTrue(result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertNotNull("Missing softwareSystem", softwareSystem);
        final ISystemInfoProcessor systemProcessor = controller.createSystemInfoProcessor();
        final List<IResolution> ignores = systemProcessor.getResolutions(r -> r.getType() == ResolutionType.IGNORE);
        assertEquals("Wrong number of ignores", 9, ignores.size());

        {
            final IResolution ignoreArchViolations = ignores.get(0);
            assertEquals("Wrong description", "C11 -> C22", ignoreArchViolations.getDescription());
            final List<IDependencyPattern> dependencyPatterns = ignoreArchViolations.getDependencyPatterns();
            assertEquals("Wrong number of dependency patterns", 4, dependencyPatterns.size());
        }
        {
            final IResolution ignoreFixme = ignores.get(1);
            assertEquals("Wrong description", "Only present in baseline", ignoreFixme.getDescription());
            final List<IElementPattern> elementPatterns = ignoreFixme.getElementPatterns();
            assertEquals("Wrong number of element patterns", 1, elementPatterns.size());
        }
        {
            final IResolution ignoreCycle = ignores.get(5);
            assertEquals("Wrong description", "Unmodified", ignoreCycle.getDescription());
            final IMatching matching = ignoreCycle.getMatching();
            assertNotNull("Matching missing", matching);
            final List<IElementPattern> elementPatterns = matching.getPatterns();
            assertEquals("Wrong number of matching patterns", 2, elementPatterns.size());
        }
    }

    @Test
    public void processReportWithResolutionTypes()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File(TestFixture.REPORT_WITH_INDIVIDUAL_RESOLUTION_TYPES));
        assertTrue(result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        assertNotNull("Missing softwareSystem", softwareSystem);
        final ISystemInfoProcessor systemProcessor = controller.createSystemInfoProcessor();

        final List<IResolution> resolutions = systemProcessor.getResolutions(null);
        assertEquals("Wrong number of resolutions", 7, resolutions.size());

        final List<IIgnoreResolution> ignores = systemProcessor.getResolutions(null, IIgnoreResolution.class);
        assertEquals("Wrong number of ignore resolutions", 1, ignores.size());

        final List<IToDoResolution> todos = systemProcessor.getResolutions(null, IToDoResolution.class);
        assertEquals("Wrong number of todo resolutions", 1, todos.size());

        final List<IDeleteRefactoring> deletes = systemProcessor.getResolutions(null, IDeleteRefactoring.class);
        assertEquals("Wrong number of delete refactorings", 1, deletes.size());

        final List<IRenameRefactoring> renames = systemProcessor.getResolutions(null, IRenameRefactoring.class);
        assertEquals("Wrong number of rename refactorings", 1, renames.size());

        final List<IMoveRefactoring> moves = systemProcessor.getResolutions(null, IMoveRefactoring.class);
        assertEquals("Wrong number of move refactorings (move + move/rename)", 1, moves.size());

        final List<IMoveRenameRefactoring> moveRename = systemProcessor.getResolutions(null, IMoveRenameRefactoring.class);
        assertEquals("Wrong number of move/rename refactorings", 1, moveRename.size());
    }

    private void assertExternalTodo(final IResolution todo, final String description, final Class<?> clazzOfAffectedElement,
            final String fqNameOfAffectedElement)
    {
        assertEquals("Wrong description", description, todo.getDescription());
        final INamedElement affectedElement = todo.getIssues().get(0).getAffectedNamedElements().get(0);
        assertEquals("Wrong fqName", fqNameOfAffectedElement, affectedElement.getFqName());
        assertEquals("Wrong class", clazzOfAffectedElement, affectedElement.getClass());
    }

    private IMetricId validateMetricInfo(final ISystemInfoProcessor systemProcessor, final String metricId, final double min, final double max,
            final SortDirection direction)
    {
        final Optional<IMetricId> metricOptional = systemProcessor.getMetricId(metricId);
        assertTrue("Missing metric '" + metricId + "'", metricOptional.isPresent());
        final IMetricId metric = metricOptional.get();
        assertEquals("Wrong minValue", min, metric.getMin(), 0.001);
        assertEquals("Wrong maxValue", max, metric.getMax(), 0.001);
        assertEquals("Wrong direction", direction, metric.getSortDirection());
        return metric;
    }
}
