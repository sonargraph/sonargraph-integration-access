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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IMetaDataController;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.ISourceRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.internal.java.ClassRootDirectory;

public class ModuleInfoProcessorTest
{
    private static final String JAVA_INTERNAL_COMPILATION_UNIT = "JavaInternalCompilationUnit";
    private static final String JAVA_SOURCE_ROOT_DIRECTORY_PATH = "JavaSourceRootDirectoryPath";
    private static final String SRC_ROOT = "Workspace:Application:../../smallTestProject/Application/src/main/java";
    private ISonargraphSystemController m_controller;
    private IMetaDataController m_exportMetaDataController;
    private IExportMetaData m_exportMetaData;

    @Before
    public void before()
    {
        m_exportMetaDataController = new ControllerFactory().createMetaDataController();
        final OperationResultWithOutcome<IExportMetaData> loadMetaDataResult = m_exportMetaDataController.loadExportMetaData(new File(
                TestFixture.META_DATA_PATH));
        assertTrue(loadMetaDataResult.toString(), loadMetaDataResult.isSuccess());
        m_exportMetaData = loadMetaDataResult.getOutcome();
        m_controller = new ControllerFactory().createController();
        final OperationResult result = m_controller.loadSystemReport(new File(TestFixture.TEST_REPORT));
        assertTrue(result.toString(), result.isSuccess());

        assertTrue(loadMetaDataResult.toString(), loadMetaDataResult.isSuccess());
    }

    @Test
    public void testRootDirectoriesAndSourceFiles()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.APPLICATION_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();
        final List<IRootDirectory> roots = application.getRootDirectories();
        assertEquals("Wrong number of root directories", 2, roots.size());

        final Map<String, INamedElement> classRoots = application.getElements("JavaClassRootDirectoryPath");
        assertFalse("No class roots found", classRoots.isEmpty());
        final INamedElement clsRoot = classRoots.get("Workspace:Application:../../smallTestProject/Application/target/classes");
        assertNotNull("Class root not found", clsRoot);
        final ClassRootDirectory classes = (ClassRootDirectory) clsRoot;
        assertEquals("Wrong number of source files", 0, classes.getSourceFiles().size());

        final Map<String, INamedElement> srcRoots = application.getElements(JAVA_SOURCE_ROOT_DIRECTORY_PATH);
        assertFalse("No src roots found", srcRoots.isEmpty());
        final INamedElement srcRoot = srcRoots.get(SRC_ROOT);
        assertNotNull("Src root not found", srcRoot);
        final ISourceRootDirectory src = (ISourceRootDirectory) srcRoot;
        assertEquals("Wrong number of source files", 2, src.getSourceFiles().size());
    }

    @Test
    public void testIsContainedInModule()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.APPLICATION_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();
        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(application);

        final INamedElement srcRoot = application.getElements(JAVA_SOURCE_ROOT_DIRECTORY_PATH).get(SRC_ROOT);
        assertNotNull("src root not found", srcRoot);
        assertTrue("Element must be contained in module!", processor.isElementContainedInModule(srcRoot));

        final INamedElement srcFile = application.getElements(JAVA_INTERNAL_COMPILATION_UNIT).get(
                "Workspace:Application:../../smallTestProject/Application/src/main/java:com:h2m:alarm:application:Main.java");
        assertNotNull("src file not found", srcFile);
        assertTrue("Element must be contained in module!", processor.isElementContainedInModule(srcFile));

        final Optional<IModule> foundationOptional = m_controller.getSoftwareSystem().getModule("Foundation");
        assertTrue("Module not found", foundationOptional.isPresent());
        final IModule foundation = foundationOptional.get();
        final INamedElement srcRoot2 = foundation.getElements(JAVA_SOURCE_ROOT_DIRECTORY_PATH).get(
                "Workspace:Foundation:../../smallTestProject/AlarmClock/Foundation/src/main/java");
        final INamedElement srcFile2 = foundation.getElements(JAVA_INTERNAL_COMPILATION_UNIT).get(
                "Workspace:Foundation:../../smallTestProject/AlarmClock/Foundation/src/main/java:com:h2m:common:observer:DuplicateInFoundation.java");
        assertFalse("Element must not be contained in module", processor.isElementContainedInModule(srcRoot2));
        assertFalse("Element must not be contained in module", processor.isElementContainedInModule(srcFile2));
    }

    @Test
    public void testGetSourceFileForArchitectureViolation()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.APPLICATION_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();

        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(application);
        final List<IIssue> unresolvedIssues = processor.getIssues((final IIssue issue) -> !issue.hasResolution());
        assertEquals("Wrong number of unresolved issues", 9, unresolvedIssues.size());

        final IIssueCategory architectureViolation = m_exportMetaData.getIssueCategories().get("ArchitectureViolation");

        final List<IIssue> architectureViolations = processor.getIssues((final IIssue issue) ->
        {
            final boolean matchesCategory = issue.getIssueType().getCategory().equals(architectureViolation);
            final boolean matchesDescription = issue.getDescription().startsWith("[New]");
            return matchesCategory && matchesDescription;
        });
        assertEquals("Wrong number of architecture violations", 1, architectureViolations.size());

        final Optional<ISourceFile> sourceOptional = application.getSourceForElement(architectureViolations.get(0).getOrigins().get(0));
        assertTrue("No source for element found", sourceOptional.isPresent());
        final ISourceFile source = sourceOptional.get();
        assertEquals("Wrong source of architecture violation", "./com/h2m/alarm/application/Main.java", source.getPresentationName());
    }

    @Test
    public void testGetResolutionsForModule()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.APPLICATION_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();

        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(application);
        assertEquals("Wrong number of resolutions for module '" + application.getName() + "'", 1, processor.getResolutions(null).size());
        assertEquals("Wrong number of resolutions of type 'TODO'", 1,
                processor.getResolutions((final IResolution r) -> r.getType().equals(ResolutionType.TODO)).size());
        assertEquals("Wrong number of resolutions of type 'Refactoring'", 0,
                processor.getResolutions((final IResolution r) -> r.getType().equals(ResolutionType.REFACTORING)).size());

        assertEquals("Wrong number of unresolved resolutions", 0, processor.getResolutions((final IResolution r) -> !r.isApplicable()).size());
    }

    @Test
    public void testGetIssuesForSourceFiles()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.APPLICATION_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();

        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(application);
        final Map<ISourceFile, List<IIssue>> sourceFilesWithIssueMap = processor.getIssuesForSourceFiles((final IIssue issue) -> !issue
                .hasResolution());
        assertFalse("Map must not be empty", sourceFilesWithIssueMap.isEmpty());

        final List<ISourceFile> files = sourceFilesWithIssueMap.keySet().stream()
                .filter((final ISourceFile file) -> file.getFqName().endsWith("Main.java")).collect(Collectors.toList());
        assertEquals("Wrong number of files with issues (without resolution)", 1, files.size());

        final ISourceFile main = files.get(0);
        assertEquals("Wrong relatve source file path", "./com/h2m/alarm/application/Main.java", main.getRelativePath());
        assertEquals("Wrong relative root directory", "../../smallTestProject/Application/src/main/java", main.getRelativeRootDirectoryPath());
        assertEquals("Wrong absolute system directory",
                "D:/00_repos/00_e4-sgng/com.hello2morrow.sonargraph.language.provider.java/src/test/architecture/AlarmClockWithArchitecture",
                m_controller.getSoftwareSystem().getBaseDir());
        final List<IIssue> issuesForMain = sourceFilesWithIssueMap.get(main);
        assertEquals("Wrong number of unresolved issues for source file", 8, issuesForMain.size());
    }

    @Test
    public void testGetDuplicates()
    {
        final Optional<IModule> applicationOptional = m_controller.getSoftwareSystem().getModule(TestFixture.MODEL_MODULE);
        assertTrue("Module not found", applicationOptional.isPresent());
        final IModule application = applicationOptional.get();

        final IModuleInfoProcessor processor = m_controller.createModuleInfoProcessor(application);
        final Map<ISourceFile, List<IIssue>> sourceFilesWithIssueMap = processor.getIssuesForSourceFiles((final IIssue issue) -> !issue
                .hasResolution());
        assertFalse("Map must not be empty", sourceFilesWithIssueMap.isEmpty());

        final Optional<ISourceFile> duplicateInModel = sourceFilesWithIssueMap.keySet().stream()
                .filter(s -> s.getFqName().endsWith("DuplicateInModel.java")).findFirst();
        assertTrue("Source file with issues not present", duplicateInModel.isPresent());

        final List<IIssue> duplicatesInModelIssues = sourceFilesWithIssueMap.get(duplicateInModel.get());
        final List<IIssue> duplicates = duplicatesInModelIssues.stream().filter(i -> (i instanceof IDuplicateCodeBlockIssue))
                .map(i -> ((IDuplicateCodeBlockIssue) i)).collect(Collectors.toList());
        assertEquals("Wrong number of duplicates", 2, duplicates.size());
    }
}