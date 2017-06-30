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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

/**
 * This class is deliberately put into a different package than the {@link #SonargraphController()},
 * so there is no chance to test non-public controller functionality.
 */
public class SonargraphSystemControllerTest
{
    private static final String APPLICATION_MODULE = TestFixture.APPLICATION_MODULE;
    private static final String INVALID_REPORT = TestFixture.INVALID_TEST_REPORT;
    private static final String REPORT_PATH = TestFixture.TEST_REPORT;
    private static final String REPORT_PATH_NOT_EXISTING_BASE_PATH = TestFixture.TEST_REPORT_NOT_EXISTING_BASE_PATH;

    private ISonargraphSystemController m_controller;

    @Before
    public void before()
    {
        m_controller = new ControllerFactory().createController();
    }

    @Test
    public void validateThresholdIssues()
    {
        final OperationResult result = m_controller.loadSystemReport(new File(TestFixture.TEST_REPORT_THRESHOLD_VIOLATIONS));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final ISystemInfoProcessor processor = m_controller.createSystemInfoProcessor();
        final List<IThresholdViolationIssue> thresholdIssues = processor.getThresholdViolationIssues(null);
        assertEquals("Wrong number of issues", 3, thresholdIssues.size());

        final List<IThresholdViolationIssue> unusedTypeThresholds = processor.getThresholdViolationIssues(th -> th.getThreshold().getMetricId()
                .getName().equals("Unused Types"));
        assertEquals("Wrong number of metric threshold issue", 1, unusedTypeThresholds.size());
        assertEquals("Wrong metric value", 4, unusedTypeThresholds.get(0).getMetricValue().intValue());
    }

    @Test
    public void validateRefactorings()
    {
        final OperationResult result = m_controller.loadSystemReport(new File(TestFixture.TEST_REPORT_REFACTORINGS));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final ISystemInfoProcessor systemProcessor = m_controller.createSystemInfoProcessor();
        final List<IIssue> issues = systemProcessor.getIssues(i -> i.getIssueType().getCategory().getName().equals("Refactoring"));
        assertEquals("Wrong number of refactorings", 3, issues.size());

        final IModule alarmClock = systemProcessor.getModules().get("AlarmClock");
        final List<ISourceFile> refactoredElements = getRefactoredSourceElements(alarmClock);
        assertEquals("Wrong number of originals", 1, refactoredElements.size());
        assertEquals("Wrong refactored element", "Workspace:AlarmClock:./AlarmClock/src/main/java:com:h2m:alarm:model:AlarmClock2.java",
                refactoredElements.get(0).getFqName());
        final Optional<ISourceFile> originalSourceFileOpt = refactoredElements.get(0).getOriginal();
        assertTrue("Original source file expected '" + refactoredElements.get(0).getFqName() + "'", originalSourceFileOpt.isPresent());
        assertEquals("Wrong original", "Workspace:AlarmClock:./AlarmClock/src/main/java:com:h2m:alarm:model:AlarmClock.java", originalSourceFileOpt
                .get().getFqName());

        final IModule foundation = systemProcessor.getModules().get("Foundation");
        final List<ISourceFile> refactoredElements2 = getRefactoredSourceElements(foundation);
        assertEquals("Wrong number of originals", 1, refactoredElements2.size());
        assertEquals("Wrong refactored element", "Workspace:Foundation:./Foundation/src/main/java:com:h2m:alarm:p1:C1_2.java", refactoredElements2
                .get(0).getFqName());
        final Optional<ISourceFile> originalSourceFileOpt2 = refactoredElements2.get(0).getOriginal();
        assertTrue("Original source file expected for '" + refactoredElements2.get(0).getFqName() + "'", originalSourceFileOpt2.isPresent());
        assertEquals("Wrong original", "Workspace:AlarmClock:./AlarmClock/src/main/java:com:h2m:alarm:p1:C1.java", originalSourceFileOpt2.get()
                .getFqName());
    }

    private List<ISourceFile> getRefactoredSourceElements(final IModule module)
    {
        final Map<String, INamedElement> sourceFiles = new HashMap<>(module.getElements("JavaSourceFile"));
        sourceFiles.putAll(module.getElements("JavaInternalCompilationUnit"));
        final List<ISourceFile> refactoredElements = new ArrayList<>();
        for (final INamedElement next : sourceFiles.values())
        {
            assertTrue("Unexpected class '" + next.getClass().getCanonicalName() + "' for element: " + next.toString(), next instanceof ISourceFile);
            final ISourceFile sourceFile = (ISourceFile) next;
            final Optional<ISourceFile> originalOpt = sourceFile.getOriginal();
            if (originalOpt.isPresent())
            {
                refactoredElements.add(sourceFile);
            }
        }
        return refactoredElements;
    }

    @Test
    public void testReadValidReport()
    {
        final OperationResult result = m_controller.loadSystemReport(new File(REPORT_PATH));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = m_controller.getSoftwareSystem();

        verifySystem(
                softwareSystem,
                "D:\\00_repos\\00_e4-sgng\\com.hello2morrow.sonargraph.language.provider.java\\src\\test\\architecture\\AlarmClockWithArchitecture\\AlarmClock.sonargraph");
        verifyModule(softwareSystem);
        verifyIssues(softwareSystem);
        verifyResolutions(softwareSystem);
        verifyMetrics(softwareSystem);
    }

    @Test
    public void testReadValidReportAndOverrideSystemBaseDir()
    {
        final String baseDir = Paths.get(".").toAbsolutePath().normalize().toString();
        final OperationResult result = m_controller.loadSystemReport(new File(REPORT_PATH_NOT_EXISTING_BASE_PATH), new File(baseDir));
        assertTrue("Failed to read report: " + result.toString(), result.isSuccess());
        final ISoftwareSystem softwareSystem = m_controller.getSoftwareSystem();

        assertEquals("Wrong baseDirectory", baseDir, softwareSystem.getBaseDir());
        final Optional<IModule> module = softwareSystem.getModule(APPLICATION_MODULE);
        assertTrue("Module not found: " + APPLICATION_MODULE, module.isPresent());
        final IModuleInfoProcessor moduleProcessor = m_controller.createModuleInfoProcessor(module.get());
        assertEquals("Wrong baseDirectory", baseDir, moduleProcessor.getBaseDirectory());

        verifySystem(
                softwareSystem,
                "D:\\NOT_EXISTING\\00_e4-sgng\\com.hello2morrow.sonargraph.language.provider.java\\src\\test\\architecture\\AlarmClockWithArchitecture\\AlarmClock.sonargraph");

        verifyModule(softwareSystem);
        verifyIssues(softwareSystem);
        verifyResolutions(softwareSystem);
        verifyMetrics(softwareSystem);

    }

    private void verifySystem(final ISoftwareSystem softwareSystem, final String systemPath)
    {
        assertEquals("Wrong id", "4df288656010188b4d84a2a03bb0ecb9", softwareSystem.getSystemId());
        assertEquals("Wrong system name", "AlarmClock", softwareSystem.getPresentationName());
        assertEquals("Wrong path", systemPath, softwareSystem.getPath());
        assertEquals("Wrong version", "9.1.0.100", softwareSystem.getVersion());
        //TODO: Activate this check. De-activated now, because the test file needs to be re-generated too often.
        //assertEquals("Wrong timestamp", 1444285242759L, softwareSystem.getTimestamp());
        assertEquals("Wrong description", "", softwareSystem.getDescription());
        assertEquals("Wrong virtual model", "Modifiable.vm", softwareSystem.getVirtualModel());
        assertEquals("Wrong number of modules", 4, softwareSystem.getModules().size());

        final Set<String> expectedSystemElementKinds = new HashSet<>(Arrays.asList("GroovyScript", "JavaLogicalSystemNamespace",
                "LogicalSystemProgrammingElement", "SoftwareSystem"));
        Assert.assertThat("Wrong system element kinds", softwareSystem.getElementKinds(), is(equalTo(expectedSystemElementKinds)));
    }

    private void verifyModule(final ISoftwareSystem softwareSystem)
    {
        final IModule moduleApplication = softwareSystem.getModules().get(APPLICATION_MODULE);
        assertNotNull("Module does not exist", moduleApplication);
        assertEquals("Wrong module name", APPLICATION_MODULE, moduleApplication.getPresentationName());
        assertEquals("Wrong language", "Java", moduleApplication.getLanguage());
        assertEquals("Wrong description", "Main Project", moduleApplication.getDescription());
        assertEquals("Wrong number of root directories", 2, moduleApplication.getRootDirectories().size());

        final Set<String> expectedModuleElementKinds = new HashSet<>(Arrays.asList("JavaModule", "JavaLogicalModuleNamespace",
                "LogicalModuleProgrammingElement", "JavaClassRootDirectoryPath", "JavaSourceRootDirectoryPath", "JavaInternalCompilationUnit",
                "JavaType", "JavaMethod", "JavaConstructor", "JavaStaticBlock"));
        assertThat("Wrong module element kinds", moduleApplication.getElementKinds(), is(equalTo(expectedModuleElementKinds)));

        final ISystemInfoProcessor infoProcessor = m_controller.createSystemInfoProcessor();
        assertEquals("Wrong number of Analyzers", 19, infoProcessor.getAnalyzers().size());
        assertEquals("No unlicensed analyzers expected", 0, infoProcessor.getAnalyzers().stream().filter(analyzer -> !analyzer.isLicensed()).count());

        assertEquals("Wrong number of Features", 12, infoProcessor.getFeatures().size());
        assertEquals("No uunlicensed features expected", 0, infoProcessor.getFeatures().stream().filter(features -> !features.isLicensed()).count());

        final IModule moduleView = softwareSystem.getModules().get("View");
        processModuleElements(softwareSystem, moduleView);
    }

    private void processModuleElements(final ISoftwareSystem softwareSystem, final IModule module)
    {
        final Map<IMetricLevel, Map<IMetricId, Map<String, IMetricValue>>> elementMetricValueMap = new HashMap<>();
        final IModuleInfoProcessor moduleInfoProcessor = m_controller.createModuleInfoProcessor(module);
        for (final IMetricLevel nextLevel : moduleInfoProcessor.getMetricLevels())
        {
            final Map<IMetricId, Map<String, IMetricValue>> levelMetricValueMap = new HashMap<>();
            elementMetricValueMap.put(nextLevel, levelMetricValueMap);
            for (final IMetricId nextId : moduleInfoProcessor.getMetricIdsForLevel(nextLevel))
            {
                final Map<String, IMetricValue> metricValueMap;
                if (!levelMetricValueMap.containsKey(nextId))
                {
                    metricValueMap = new HashMap<>();
                    levelMetricValueMap.put(nextId, metricValueMap);
                }
                else
                {
                    metricValueMap = levelMetricValueMap.get(nextId);
                }

                for (final Entry<INamedElement, IMetricValue> nextValue : moduleInfoProcessor.getMetricValues(nextLevel.getName(), nextId.getName())
                        .entrySet())
                {
                    metricValueMap.put(nextValue.getKey().getFqName(), nextValue.getValue());
                }
            }
        }

        assertEquals("Wrong number of levels", 7, elementMetricValueMap.size());

        final ISystemInfoProcessor systemProcessor = m_controller.createSystemInfoProcessor();

        final Map<String, IMetricLevel> allMetricLevels = systemProcessor.getMetricLevels().stream()
                .collect(Collectors.toMap((final IMetricLevel level) -> level.getName(), (final IMetricLevel level) -> level));
        final Map<IMetricId, Map<String, IMetricValue>> moduleLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("Module"));

        //Module Metrics
        assertEquals("Wrong number of metric ids on module level", 37, moduleLevelMetricValueMap.size());
        final IMetricId coreTotalLines = systemProcessor.getMetricId("CoreTotalLines").get();
        final Map<String, IMetricValue> moduleMetricValueMap = moduleLevelMetricValueMap.get(coreTotalLines);
        assertEquals("Wrong number of metric values", 1, moduleMetricValueMap.size());
        assertEquals("Wrong metric value", 102, moduleMetricValueMap.get("Workspace:View").getValue().intValue());

        //Package metrics
        final Map<IMetricId, Map<String, IMetricValue>> packageLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("Package"));
        assertEquals("Wrong number of metric ids on package level", 6, packageLevelMetricValueMap.size());

        final IMetricId coreAbstractnessModule = systemProcessor.getMetricId("CoreAbstractnessModule").get();
        final Map<String, IMetricValue> packageMetricValueMap = packageLevelMetricValueMap.get(coreAbstractnessModule);
        assertEquals("Wrong number of metric values", 3, packageMetricValueMap.size());
        assertEquals("Wrong metric value", 1.0, packageMetricValueMap.get("Logical module namespaces:View:com:h2m:alarm:presentation").getValue()
                .floatValue(), 0.01);

        //Component
        final Map<IMetricId, Map<String, IMetricValue>> componentLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("Component"));
        final IMetricId coreInstabilitySystem = systemProcessor.getMetricId("CoreInstabilitySysytem").get();
        final Map<String, IMetricValue> componentMetricValueMap = componentLevelMetricValueMap.get(coreInstabilitySystem);
        assertEquals("Wrong number of metric values", 3, componentMetricValueMap.size());
        assertEquals(
                "Wrong metric value",
                0.33,
                componentMetricValueMap
                        .get("Workspace:View:../../smallTestProject/AlarmClock/View/src/main/java:com:h2m:alarm:presentation:AlarmHandler.java")
                        .getValue().floatValue(), 0.01);

        //Source File
        final Map<IMetricId, Map<String, IMetricValue>> sourceFileLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("SourceFile"));
        final IMetricId coreSourceElementCount = systemProcessor.getMetricId("CoreSourceElementCount").get();
        final Map<String, IMetricValue> sourceFileMetricValueMap = sourceFileLevelMetricValueMap.get(coreSourceElementCount);
        assertEquals("Wrong number of metric values", 3, sourceFileMetricValueMap.size());
        assertEquals(
                "Wrong metric value",
                13,
                sourceFileMetricValueMap
                        .get("Workspace:View:../../smallTestProject/AlarmClock/View/src/main/java:com:h2m:alarm:presentation:file:AlarmToFile.java")
                        .getValue().intValue());

        //LogicalProgrammingElement
        final Map<IMetricId, Map<String, IMetricValue>> logicalProgElementLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels
                .get("LogicalProgrammingElement"));
        final IMetricId coreLogicalCouplingModule = systemProcessor.getMetricId("CoreLogicalCouplingModule").get();
        final Map<String, IMetricValue> logicalProgElementMetricValueMap = logicalProgElementLevelMetricValueMap.get(coreLogicalCouplingModule);
        assertEquals("Wrong number of metric values", 3, logicalProgElementMetricValueMap.size());
        assertEquals("Wrong metric value", 4,
                logicalProgElementMetricValueMap.get("Logical module namespaces:View:com:h2m:alarm:presentation:console:AlarmToConsole").getValue()
                        .intValue());

        //Type
        final Map<IMetricId, Map<String, IMetricValue>> typeLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("Type"));
        final IMetricId coreStatements = systemProcessor.getMetricId("CoreStatements").get();
        final Map<String, IMetricValue> typeMetricValueMap = typeLevelMetricValueMap.get(coreStatements);
        assertEquals("Wrong number of metric values", 3, typeMetricValueMap.size());
        assertEquals(
                "Wrong metric value",
                9,
                typeMetricValueMap
                        .get("Workspace:View:../../smallTestProject/AlarmClock/View/src/main/java:com:h2m:alarm:presentation:file:AlarmToFile.java:AlarmToFile")
                        .getValue().intValue());

        //Routine
        final Map<IMetricId, Map<String, IMetricValue>> routineLevelMetricValueMap = elementMetricValueMap.get(allMetricLevels.get("Routine"));
        final IMetricId coreCyclomaticComplexity = systemProcessor.getMetricId("CoreCcn").get();
        final Map<String, IMetricValue> routineMetricValueMap = routineLevelMetricValueMap.get(coreCyclomaticComplexity);
        assertEquals("Wrong number of metric values", 8, routineMetricValueMap.size());
        assertEquals(
                "Wrong metric value",
                2,
                routineMetricValueMap
                        .get("Workspace:View:../../smallTestProject/AlarmClock/View/src/main/java:com:h2m:alarm:presentation:file:AlarmToFile.java:AlarmToFile:handleAlarm()")
                        .getValue().intValue());
    }

    private void verifyIssues(final ISoftwareSystem softwareSystem)
    {
        final ISystemInfoProcessor infoProcessor = m_controller.createSystemInfoProcessor();
        assertEquals("Wrong number of issue providers", 3, infoProcessor.getIssueProviders().size());
        assertEquals("Wrong number of issue types", 10, infoProcessor.getIssueTypes().size());
        assertTrue("Script issue type not found",
                infoProcessor.getIssueTypes().stream().anyMatch((final IIssueType type) -> type.getName().equals("No incoming dependencies")));

        assertEquals("Wrong number of issues", 22, infoProcessor.getIssues(null).size());
        assertEquals("Wrong number of unresolved issues", 21, infoProcessor.getIssues((final IIssue issue) -> !issue.hasResolution()).size());

        assertEquals("Wrong number of script based issues", 4,
                infoProcessor.getIssues((final IIssue issue) -> issue.getIssueType().getName().equals("No incoming dependencies")).size());
        assertEquals("Wrong number of Script compilation issues", 1,
                infoProcessor.getIssues((final IIssue issue) -> issue.getIssueType().getName().equals("ScriptCompilationError")).size());

        assertEquals("Wrong number of cycle groups", 2, infoProcessor.getIssues((final IIssue issue) ->
        {
            final String name = issue.getIssueType().getName();
            return name.equals("NamespaceCycleGroup") || name.equals("ComponentCycleGroup");
        }).size());

        assertEquals("Wrong number of component cycle groups", 1,
                infoProcessor.getCycleGroups((final ICycleGroup group) -> group.getAnalyzer().getName().equals("ComponentCyclesModule")).size());
        assertEquals("Wrong number of package cycle groups", 1,
                infoProcessor.getCycleGroups((final ICycleGroup group) -> group.getAnalyzer().getName().equals("PackageCyclesModule")).size());

        assertEquals("Wrong number of duplicate blocks", 3,
                infoProcessor.getIssues((final IIssue issue) -> issue.getIssueType().getCategory().getName().equals("DuplicateCode")).size());

        assertEquals(
                "Wrong number of issues matching predicate filter",
                7,
                infoProcessor.getIssues(
                        (final IIssue issue) -> !issue.hasResolution() && issue.getIssueProvider().getName().equals("./createViolations.arc")).size());
    }

    private void verifyResolutions(final ISoftwareSystem softwareSystem)
    {
        final ISystemInfoProcessor infoProcessor = m_controller.createSystemInfoProcessor();
        assertEquals("Wrong number of resolutions", 1, infoProcessor.getResolutions(null).size());
        assertEquals("Wrong number of resolutions", 1, infoProcessor.getResolutions((final IResolution r) -> r.getType() == ResolutionType.TODO)
                .size());
        assertEquals("Wrong number of applicable resolutions", 1, infoProcessor.getResolutions((final IResolution r) -> r.isApplicable()).size());
    }

    private void verifyMetrics(final ISoftwareSystem softwareSystem)
    {
        final ISystemInfoProcessor infoProcessor = m_controller.createSystemInfoProcessor();
        final List<IMetricCategory> metricCategories = infoProcessor.getMetricCategories();
        assertEquals("Wrong number of metric categories", 10, metricCategories.size());
        assertEquals("Wrong first category", "Architecture", metricCategories.get(0).getName());
        final IMetricCategory metricCategory = metricCategories.stream().filter((final IMetricCategory cat) -> cat.getName().equals("CodeAnalysis"))
                .findFirst().get();
        assertEquals("Wrong presentation name", "Code Analysis", metricCategory.getPresentationName());
        assertEquals("Wrong order number of metric category", 1, metricCategory.getOrderNumber());

        final List<IMetricProvider> metricProviders = infoProcessor.getMetricProviders();
        assertEquals("Wrong number of metric providers", 3, metricProviders.size());
        assertEquals("Wrong first provider", "Core", metricProviders.get(0).getName());
        assertEquals("Wrong presentation name", "Java",
                metricProviders.stream().filter((final IMetricProvider p) -> p.getName().equals("JavaLanguageProvider")).findFirst().get()
                        .getPresentationName());

        assertEquals("Wrong number of metric ids", 76, infoProcessor.getMetricIds().size());

        final Map<String, IMetricLevel> allMetricLevels = infoProcessor.getMetricLevels().stream()
                .collect(Collectors.toMap(IMetricLevel::getName, (final IMetricLevel level) -> level));
        assertEquals("Wrong number of metric levels", 8, allMetricLevels.size());
        assertEquals("Wrong order number of metric level", 50, allMetricLevels.get("Component").getOrderNumber());

        assertEquals("Wrong system metric float value", 2.44f, infoProcessor.getMetricValue("CoreAcd").get().getValue().floatValue(), 0.001f);
        assertEquals("Wrong system metric int value", 13, infoProcessor.getMetricValue("CoreArtifactCount").get().getValue().intValue());
        assertEquals("Wrong number of unused types", 4, infoProcessor.getMetricValue("Unused Types").get().getValue().intValue());

        final IModule viewModule = softwareSystem.getModules().get("View");
        final IInfoProcessor moduleInfoProcessor = m_controller.createModuleInfoProcessor(viewModule);
        final IMetricLevel packageLevel = allMetricLevels.get("Package");
        assertEquals(
                "Wrong module metric value",
                1,
                moduleInfoProcessor
                        .getMetricValueForElement(infoProcessor.getMetricId("CoreIncomingDependenciesModule").get(), packageLevel,
                                "Logical module namespaces:View:com:h2m:alarm:presentation:file").get().getValue().intValue());
        assertEquals(
                "Wrong module metric value",
                2,
                moduleInfoProcessor
                        .getMetricValueForElement(infoProcessor.getMetricId("CoreOutgoingDependenciesModule").get(), packageLevel,
                                "Logical module namespaces:View:com:h2m:alarm:presentation:file").get().getValue().intValue());
        assertEquals(
                "Wrong module metric value",
                0.67,
                moduleInfoProcessor
                        .getMetricValueForElement(infoProcessor.getMetricId("CoreInstabilityModule").get(), packageLevel,
                                "Logical module namespaces:View:com:h2m:alarm:presentation:file").get().getValue().floatValue(), 0.01f);
    }

    @Test
    public void testReadInvalidReport()
    {
        final OperationResult result = m_controller.loadSystemReport(new File(INVALID_REPORT));
        assertTrue("Expect failure, but got success", result.isFailure());
    }

    @Test
    public void testReadNotExistingReport()
    {
        final OperationResult result = m_controller.loadSystemReport(new File("./fantasyDir/fantasyReport.xml"));
        assertTrue("Expect failure, but got success", result.isFailure());
    }
}
