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
package com.hello2morrow.sonargraph.integration.access.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.INamedElementAdjuster;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.internal.AbstractElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.AnalyzerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.CycleGroupIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DependencyIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockOccurrenceImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ExternalImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.FeatureImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueTypeImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LanguageBasedContainerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LogicalElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricIdImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricLevelImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricValueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PhysicalRecursiveElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ProgrammingElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.RootDirectoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SourceFileImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl.ValidationMessageCauses;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdAnalyzer;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleGroupContainer;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDuplicateBlockIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFeature;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricFloatValue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricId;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricIntValue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricLevel;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricLevelValues;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricThresholds;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricValue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdModule;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdModuleElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdModuleMetricValues;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdNamedElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdResolution;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRootDirectory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSimpleElementIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSourceFile;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemMetricValues;

public final class XmlReportReader extends AbstractXmlReportAccess
{
    private static class NoOpAdjuster implements INamedElementAdjuster
    {
        @Override
        public String adjustFqName(final String standardKind, final String fqName)
        {
            return fqName;
        }

        @Override
        public String adjustName(final String standardKind, final String name)
        {
            return name;
        }

        @Override
        public String adjustPresentationName(final String standardKind, final String presentationName)
        {
            return presentationName;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportReader.class);
    private static final String SOFTWARE_SYSTEM_STANDARD_KIND = "SoftwareSystem";
    private static final String MODULE_STANDARD_KIND_SUFFIX = "Module";

    private final Map<Object, IElement> globalXmlToElementMap = new HashMap<>();
    private final Map<Object, IssueImpl> globalXmlIdToIssueMap = new HashMap<>();

    /**
     * Reads an XML report and allows to enable XML schema validation.
     * For big reports with thousands of elements, issues and resolutions the schema validation should be switched off.
     *
     * @param reportFile XML file that is expected to exist and be readable.
     * @param result Contains info about errors.
     * @param enableSchemaValidation flag to switch XML validation off.
     */
    public Optional<SoftwareSystemImpl> readReportFile(final File reportFile, final OperationResult result, final boolean enableSchemaValidation)
    {
        assert reportFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert reportFile.exists() : "Parameter 'reportFile' of method 'readReportFile' must be an existing file";
        assert reportFile.canRead() : "Parameter 'reportFile' of method 'readReportFile' must be a file with read access";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";

        return internReadReportFile(reportFile, result, enableSchemaValidation, new NoOpAdjuster());
    }

    /**
     * Reads an XML report without schema validation.
     *
     * @param reportFile XML file that is expected to exist and be readable.
     * @param result Contains info about errors.
     */
    public Optional<SoftwareSystemImpl> readReportFile(final File reportFile, final OperationResult result)
    {
        return internReadReportFile(reportFile, result, false, new NoOpAdjuster());
    }

    public Optional<SoftwareSystemImpl> readReportFile(final File reportFile, final OperationResult result, final INamedElementAdjuster adjuster)
    {
        return internReadReportFile(reportFile, result, false, adjuster);
    }

    private Optional<SoftwareSystemImpl> internReadReportFile(final File reportFile, final OperationResult result,
            final boolean enableSchemaValidation, final INamedElementAdjuster adjuster)
    {
        assert reportFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert reportFile.exists() : "Parameter 'reportFile' of method 'readReportFile' must be an existing file";
        assert reportFile.canRead() : "Parameter 'reportFile' of method 'readReportFile' must be a file with read access";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";
        assert adjuster != null : "Parameter 'adjuster' of method 'internReadReportFile' must not be null";

        JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter;
        try
        {
            jaxbAdapter = createJaxbAdapter(enableSchemaValidation);
        }
        catch (final Exception e)
        {
            result.addError(IOMessageCause.READ_ERROR, "Failed to initialize JAXB", e);
            return Optional.empty();
        }

        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);

        Optional<JAXBElement<XsdSoftwareSystemReport>> xmlRoot = Optional.empty();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(reportFile)))
        {
            xmlRoot = jaxbAdapter.load(in, eventHandler);
            if (xmlRoot.isPresent())
            {
                return convertXmlReportToPojo(xmlRoot.get().getValue(), adjuster, result);
            }
        }
        catch (final Exception ex)
        {
            LOGGER.error("Failed to read report from '" + reportFile.getAbsolutePath() + "'", ex);
            result.addError(IOMessageCause.IO_EXCEPTION, ex);
        }
        finally
        {
            if (result.isFailure() || !xmlRoot.isPresent())
            {
                result.addError(IOMessageCause.WRONG_FORMAT,
                        "Report is corrupt. Ensure that the version of SonargraphBuild used to create the report is compatible with the version of this client.");
            }
        }
        return Optional.empty();
    }

    private NamedElementImpl getNamedElementImpl(final XsdNamedElement xsdNamedElement)
    {
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'getNamedElementImpl' must not be null";
        final Object namedElemenetImpl = globalXmlToElementMap.get(xsdNamedElement);
        if (namedElemenetImpl == null)
        {
            LOGGER.error("Named element '{}' must have been created before.", xsdNamedElement.getFqName());
            return null;
        }
        else if (!(namedElemenetImpl instanceof NamedElementImpl))
        {
            LOGGER.error("Unexpected class '{}'", namedElemenetImpl.getClass().getCanonicalName());
            return null;
        }
        return (NamedElementImpl) namedElemenetImpl;
    }

    private void connectNamedElementOriginal(final XsdNamedElement xsdNamedElement)
    {
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'connectNamedElementOriginal' must not be null";

        final NamedElementImpl nextNamedElementImpl = getNamedElementImpl(xsdNamedElement);
        if (nextNamedElementImpl == null)
        {
            LOGGER.error("No named element impl found for xsd named element '{}'.", xsdNamedElement.getFqName());
            return;
        }

        final Object nextXsdOriginal = xsdNamedElement.getOriginal();
        if (nextXsdOriginal != null)
        {
            if (nextXsdOriginal instanceof XsdNamedElement)
            {
                final NamedElementImpl original = getNamedElementImpl((XsdNamedElement) nextXsdOriginal);
                if (original != null)
                {
                    if (nextNamedElementImpl.getClass().equals(original.getClass()))
                    {
                        nextNamedElementImpl.setOriginal(original);
                        original.setIsOriginal(true);
                    }
                    else
                    {
                        LOGGER.error("Class info does not match for original connection '{}' vs '{}'.", nextNamedElementImpl.getClass().getName(),
                                original.getClass().getName());
                    }
                }
                else
                {
                    LOGGER.error("No named element impl found for original xsd named element '{}'.", ((XsdSourceFile) nextXsdOriginal).getFqName());
                }
            }
            else
            {
                LOGGER.error("Unexpected class '{}' as original named element impl.", nextXsdOriginal.getClass().getCanonicalName());
            }
        }
    }

    private Optional<SoftwareSystemImpl> convertXmlReportToPojo(final XsdSoftwareSystemReport report, final INamedElementAdjuster adjuster,
            final OperationResult result)
    {
        assert report != null : "Parameter 'report' of method 'convertXmlReportToPojo' must not be null";
        assert result != null : "Parameter 'result' of method 'convertXmlReportToPojo' must not be null";
        assert adjuster != null : "Parameter 'adjuster' of method 'convertXmlReportToPojo' must not be null";

        final String systemDescription = report.getSystemDescription() != null ? report.getSystemDescription().trim() : "";
        final SoftwareSystemImpl softwareSystem = new SoftwareSystemImpl("SoftwareSystem", "System", report.getSystemId(), report.getName(),
                systemDescription, report.getSystemPath(), report.getVersion(), report.getTimestamp().toGregorianCalendar().getTimeInMillis(),
                report.getCurrentVirtualModel());
        softwareSystem.addElement(softwareSystem);

        processAnalyzers(softwareSystem, report);
        processFeatures(softwareSystem, report);

        final XsdWorkspace xsdWorkspace = report.getWorkspace();
        createWorkspaceElements(softwareSystem, xsdWorkspace, result, adjuster);
        if (result.isFailure())
        {
            return Optional.empty();
        }

        finishWorkspaceElementCreation(xsdWorkspace);

        processSystemElements(softwareSystem, report, adjuster);
        createModuleElements(softwareSystem, report, adjuster);

        for (final Entry<Object, IElement> nextEntry : globalXmlToElementMap.entrySet())
        {
            final Object nextXsdElement = nextEntry.getKey();
            final IElement nextElement = nextEntry.getValue();

            if (nextXsdElement instanceof XsdLogicalElement && nextElement instanceof LogicalElementImpl)
            {
                setDerivedFrom((XsdLogicalElement) nextXsdElement, (LogicalElementImpl) nextElement);
            }
        }

        connectSourceFiles();

        processMetrics(softwareSystem, report);
        processMetricThresholds(softwareSystem, report);

        processIssues(softwareSystem, report, result);
        processResolutions(softwareSystem, report, result);

        globalXmlToElementMap.clear();
        globalXmlIdToIssueMap.clear();

        return Optional.of(softwareSystem);
    }

    private void connectSourceFiles()
    {
        for (final Map.Entry<Object, IElement> entry : globalXmlToElementMap.entrySet())
        {
            if (entry.getKey() instanceof XsdNamedElement)
            {
                final XsdNamedElement element = (XsdNamedElement) entry.getKey();
                final Object source = element.getSource();
                if (source != null)
                {
                    final IElement sourceFile = globalXmlToElementMap.get(source);
                    if (sourceFile != null)
                    {
                        assert sourceFile instanceof ISourceFile : "Unexpected class '" + sourceFile.getClass().getCanonicalName() + "'";
                        assert entry.getValue() instanceof NamedElementImpl : "Unexpected class '" + entry.getValue().getClass().getCanonicalName()
                                + "'";
                        final NamedElementImpl namedElement = (NamedElementImpl) entry.getValue();
                        namedElement.setSourceFile((ISourceFile) sourceFile);
                    }
                }
            }
        }
    }

    private void finishWorkspaceElementCreation(final XsdWorkspace xsdWorkspace)
    {
        assert xsdWorkspace != null : "Parameter 'xsdWorkspace' of method 'finishWorkspaceElementCreation' must not be null";

        for (final XsdModule nextXsdModule : xsdWorkspace.getModule())
        {
            for (final XsdRootDirectory nextXsdRootDirectory : nextXsdModule.getRootDirectory())
            {
                for (final XsdSourceFile nextXsdSourceFile : nextXsdRootDirectory.getSourceElement())
                {
                    connectNamedElementOriginal(nextXsdSourceFile);
                }

                for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdRootDirectory.getPhysicalRecursiveElement())
                {
                    connectNamedElementOriginal(nextXsdPhysicalRecursiveElement);
                }
            }
        }

        //First we need the correct isOriginal() state - so do a 2nd pass
        for (final XsdModule nextXsdModule : xsdWorkspace.getModule())
        {
            final Object nextModule = globalXmlToElementMap.get(nextXsdModule);
            assert nextModule != null && nextModule instanceof ModuleImpl : "Unexpected class in method 'finishWorkspaceCreation': " + nextModule;
            for (final XsdRootDirectory nextXsdRootDirectory : nextXsdModule.getRootDirectory())
            {
                for (final XsdSourceFile nextXsdSourceFile : nextXsdRootDirectory.getSourceElement())
                {
                    ((ModuleImpl) nextModule).addElement(getNamedElementImpl(nextXsdSourceFile));
                }
                for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdRootDirectory.getPhysicalRecursiveElement())
                {
                    ((ModuleImpl) nextModule).addElement(getNamedElementImpl(nextXsdPhysicalRecursiveElement));
                }
            }
        }
    }

    private NamedElementImpl createNamedElementImpl(final INamedElementAdjuster adjuster, final XsdNamedElement xsdNamedElement,
            final XsdElementKind xsdElementKind)
    {
        assert adjuster != null : "Parameter 'adjuster' of method 'createNamedElementImpl' must not be null";
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'createNamedElementImpl' must not be null";
        assert xsdElementKind != null : "Parameter 'xsdElementKind' of method 'createNamedElementImpl' must not be null";

        final String standardKind = xsdElementKind.getStandardKind();
        final String name = adjuster.adjustName(standardKind, xsdNamedElement.getName());
        final String presentationName = adjuster.adjustPresentationName(standardKind, xsdNamedElement.getPresentationName());
        final String fqName = adjuster.adjustFqName(standardKind, xsdNamedElement.getFqName());
        final NamedElementImpl namedElementImpl = new NamedElementImpl(standardKind, xsdElementKind.getPresentationKind(), name, presentationName,
                fqName);
        return namedElementImpl;
    }

    private RootDirectoryImpl createRootDirectoryImpl(final INamedElementAdjuster adjuster,
            final LanguageBasedContainerImpl languageBasedContainerImpl, final XsdRootDirectory xsdRootDirectory)
    {
        assert adjuster != null : "Parameter 'adjuster' of method 'createRootDirectoryImpl' must not be null";
        assert languageBasedContainerImpl != null : "Parameter 'languageBasedContainerImpl' of method 'createRootDirectoryImpl' must not be null";
        assert xsdRootDirectory != null : "Parameter 'xsdRootDirectory' of method 'createRootDirectoryImpl' must not be null";

        final XsdElementKind elementKind = (XsdElementKind) xsdRootDirectory.getKind();
        final String presentationKind = elementKind.getPresentationKind();
        final String standardKind = elementKind.getStandardKind();
        final String fqName = adjuster.adjustFqName(standardKind, xsdRootDirectory.getFqName());
        final String presentationName = adjuster.adjustPresentationName(standardKind, xsdRootDirectory.getPresentationName());
        final RootDirectoryImpl rootDirectoryImpl = new RootDirectoryImpl(standardKind, presentationKind, presentationName, fqName);
        languageBasedContainerImpl.addRootDirectory(rootDirectoryImpl);
        languageBasedContainerImpl.addElement(rootDirectoryImpl);
        globalXmlToElementMap.put(xsdRootDirectory, rootDirectoryImpl);

        return rootDirectoryImpl;
    }

    private void createSourceFileImpl(final INamedElementAdjuster adjuster, final RootDirectoryImpl rootDirectoryImpl,
            final XsdSourceFile xsdSourceFile)
    {
        assert adjuster != null : "Parameter 'adjuster' of method 'createSourceFileImpl' must not be null";
        assert rootDirectoryImpl != null : "Parameter 'rootDirectoryImpl' of method 'createSourceFileImpl' must not be null";
        assert xsdSourceFile != null : "Parameter 'xsdSourceFile' of method 'createSourceFileImpl' must not be null";

        final XsdElementKind sourceKind = (XsdElementKind) xsdSourceFile.getKind();
        final SourceFileImpl sourceFileImpl = new SourceFileImpl(sourceKind.getStandardKind(), sourceKind.getPresentationKind(),
                xsdSourceFile.getName(), xsdSourceFile.getPresentationName(), xsdSourceFile.getFqName(), rootDirectoryImpl.getRelativePath());
        rootDirectoryImpl.addSourceFile(sourceFileImpl);
        globalXmlToElementMap.put(xsdSourceFile, sourceFileImpl);
    }

    private ProgrammingElementImpl createProgrammingElementImpl(final INamedElementAdjuster adjuster,
            final XsdProgrammingElement xsdProgrammingElement, final XsdElementKind xsdElementKind)
    {
        assert adjuster != null : "Parameter 'adjuster' of method 'createNamedElementImpl' must not be null";
        assert xsdProgrammingElement != null : "Parameter 'xsdProgrammingElement' of method 'createProgrammingElementImpl' must not be null";
        assert xsdElementKind != null : "Parameter 'xsdElementKind' of method 'createNamedElementImpl' must not be null";

        final String standardKind = xsdElementKind.getStandardKind();
        final String name = adjuster.adjustName(standardKind, xsdProgrammingElement.getName());
        final String presentationName = adjuster.adjustPresentationName(standardKind, xsdProgrammingElement.getPresentationName());
        final String fqName = adjuster.adjustFqName(standardKind, xsdProgrammingElement.getFqName());
        return new ProgrammingElementImpl(standardKind, xsdElementKind.getPresentationKind(), name, presentationName, fqName,
                xsdProgrammingElement.getLine());
    }

    private void createWorkspaceElements(final SoftwareSystemImpl softwareSystem, final XsdWorkspace xsdWorkspace, final OperationResult result,
            final INamedElementAdjuster adjuster)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'createWorkspaceElements' must not be null";
        assert xsdWorkspace != null : "Parameter 'report' of method 'createWorkspaceElements' must not be null";
        assert result != null : "Parameter 'result' of method 'createWorkspaceElements' must not be null";
        assert adjuster != null : "Parameter 'adjuster' of method 'createWorkspaceElements' must not be null";

        for (final XsdModule xsdModule : xsdWorkspace.getModule())
        {
            final XsdElementKind moduleKind = (XsdElementKind) xsdModule.getKind();
            final ModuleImpl module = new ModuleImpl(moduleKind.getStandardKind(), moduleKind.getPresentationKind(), xsdModule.getName(),
                    xsdModule.getPresentationName(), xsdModule.getFqName(), xsdModule.getDescription(), softwareSystem.getMetaDataAccess(),
                    softwareSystem.getElementRegistry(), xsdModule.getLanguage());
            softwareSystem.addModule(module);
            module.addElement(module);

            globalXmlToElementMap.put(xsdModule, module);
            for (final XsdRootDirectory nextXsdRootDirectory : xsdModule.getRootDirectory())
            {
                final XsdElementKind elementKind = (XsdElementKind) nextXsdRootDirectory.getKind();
                final String presentationKind = elementKind.getPresentationKind();
                final String standardKind = elementKind.getStandardKind();
                final String fqName = adjuster.adjustFqName(standardKind, nextXsdRootDirectory.getFqName());
                final String presentationName = adjuster.adjustPresentationName(standardKind, nextXsdRootDirectory.getPresentationName());
                final RootDirectoryImpl nextRootDirectoryImpl = new RootDirectoryImpl(standardKind, presentationKind, presentationName, fqName);
                module.addRootDirectory(nextRootDirectoryImpl);
                module.addElement(nextRootDirectoryImpl);
                globalXmlToElementMap.put(nextXsdRootDirectory, nextRootDirectoryImpl);

                for (final XsdSourceFile nextXsdSourceFile : nextXsdRootDirectory.getSourceElement())
                {
                    createSourceFileImpl(adjuster, nextRootDirectoryImpl, nextXsdSourceFile);
                }

                for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdRootDirectory.getPhysicalRecursiveElement())
                {
                    final XsdElementKind nextKind = (XsdElementKind) nextXsdPhysicalRecursiveElement.getKind();
                    final PhysicalRecursiveElementImpl nextPhysicalRecursiveElement = new PhysicalRecursiveElementImpl(nextKind.getStandardKind(),
                            nextKind.getPresentationKind(), nextXsdPhysicalRecursiveElement.getName(),
                            nextXsdPhysicalRecursiveElement.getPresentationName(), nextXsdPhysicalRecursiveElement.getFqName(),
                            nextRootDirectoryImpl.getRelativePath());
                    final String nextRelativeDirectory = nextXsdPhysicalRecursiveElement.getRelativeDirectoryPath();
                    if (nextRelativeDirectory != null)
                    {
                        nextPhysicalRecursiveElement.setRelativeDirectory(nextRelativeDirectory);
                        globalXmlToElementMap.put(nextXsdPhysicalRecursiveElement, nextPhysicalRecursiveElement);
                    }
                    else
                    {
                        final Object nextSource = nextXsdPhysicalRecursiveElement.getSource();
                        if (nextSource != null)
                        {
                            final IElement element = globalXmlToElementMap.get(nextSource);
                            if (element != null)
                            {
                                assert element instanceof ISourceFile : "Unexpected element class: " + element.getClass().getName();
                                nextPhysicalRecursiveElement.setSourceFile((ISourceFile) element);
                                globalXmlToElementMap.put(nextXsdPhysicalRecursiveElement, nextPhysicalRecursiveElement);
                            }
                            else
                            {
                                //TODO warn
                            }
                        }
                    }
                    nextRootDirectoryImpl.addPhysicalRecursiveElement(nextPhysicalRecursiveElement);
                }

                for (final XsdProgrammingElement nextXsdProgrammingElement : nextXsdRootDirectory.getProgrammingElement())
                {
                    final ProgrammingElementImpl nextProgrammingElementImpl = createProgrammingElementImpl(adjuster, nextXsdProgrammingElement,
                            elementKind);
                    nextRootDirectoryImpl.addProgrammingElement(nextProgrammingElementImpl);
                    globalXmlToElementMap.put(nextXsdProgrammingElement, nextProgrammingElementImpl);
                }
            }
        }

        for (final XsdExternal nextXsdExternal : xsdWorkspace.getExternal())
        {
            final XsdElementKind nextExternalKind = (XsdElementKind) nextXsdExternal.getKind();
            final ExternalImpl nextExternalImpl = new ExternalImpl(nextExternalKind.getStandardKind(), nextExternalKind.getPresentationKind(),
                    nextXsdExternal.getName(), nextXsdExternal.getPresentationName(), nextXsdExternal.getFqName(), nextXsdExternal.getDescription(),
                    softwareSystem.getMetaDataAccess(), softwareSystem.getElementRegistry(), nextXsdExternal.getLanguage());
            softwareSystem.addExternal(nextExternalImpl);
            nextExternalImpl.addElement(nextExternalImpl);

            for (final XsdRootDirectory nextXsdRootDirectory : nextXsdExternal.getRootDirectory())
            {
                final RootDirectoryImpl nextRootDirectoryImpl = createRootDirectoryImpl(adjuster, nextExternalImpl, nextXsdRootDirectory);

                //TODO as above
                for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdRootDirectory.getPhysicalRecursiveElement())
                {
                    //TODO
                }
                for (final XsdSourceFile nextXsdSourceFile : nextXsdRootDirectory.getSourceElement())
                {
                    createSourceFileImpl(adjuster, nextRootDirectoryImpl, nextXsdSourceFile);
                }
                for (final XsdProgrammingElement nextXsdProgrammingElement : nextXsdRootDirectory.getProgrammingElement())
                {
                    //TODO
                }
            }
            for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdExternal.getPhysicalRecursiveElement())
            {
                //TODO
            }
            for (final XsdProgrammingElement nextXsdProgrammingElement : nextXsdExternal.getProgrammingElement())
            {
                //TODO
            }
        }
    }

    private void processResolutions(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report, final OperationResult result)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processResolutions' must not be null";
        assert report != null : "Parameter 'report' of method 'processResolutions' must not be null";
        assert result != null : "Parameter 'result' of method 'processResolutions' must not be null";

        if (report.getResolutions() == null)
        {
            //no resolutions to be processed
            return;
        }

        for (final XsdResolution nextResolution : report.getResolutions().getResolution())
        {
            ResolutionType type;
            if (nextResolution.isIsRefactoring())
            {
                type = ResolutionType.REFACTORING;
            }
            else
            {
                try
                {
                    type = ResolutionType.valueOf(nextResolution.getType().toUpperCase());
                }
                catch (final Exception e)
                {
                    LOGGER.error("Failed to process resolution type '" + nextResolution.getType() + "'", e);
                    result.addError(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT, "Resolution type '" + nextResolution.getType()
                            + "' is not supported and will be ignored.");
                    continue;
                }
            }

            Priority priority;
            try
            {
                priority = Priority.valueOf(StringUtility.convertStandardNameToConstantName(nextResolution.getPrio()));
            }
            catch (final Exception e)
            {
                LOGGER.error("Failed to process priority type '" + nextResolution.getPrio() + "'", e);
                result.addWarning(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT, "Priority type '" + nextResolution.getPrio()
                        + "' is not supported, setting to '" + Priority.NONE + "'");
                priority = Priority.NONE;
            }

            final boolean isIgnored = ResolutionType.IGNORE.equals(type);

            final List<IIssue> issues = new ArrayList<>();
            for (final Object nextXsdIssue : nextResolution.getIssueIds())
            {
                final XsdIssue xsdIssue = (XsdIssue) nextXsdIssue;
                final IssueImpl nextIssueImpl = globalXmlIdToIssueMap.get(xsdIssue);
                assert nextIssueImpl != null : "No issue with id '" + xsdIssue.getId() + "' exists";
                nextIssueImpl.setIsIgnored(isIgnored);
                issues.add(nextIssueImpl);
            }

            final ResolutionImpl resolution = new ResolutionImpl(nextResolution.getFqName(), type, priority, issues, nextResolution.isIsApplicable(),
                    nextResolution.getNumberOfAffectedParserDependencies(), nextResolution.getDescription(), nextResolution.getAssignee(),
                    nextResolution.getDate().toString());
            softwareSystem.addResolution(resolution);
        }
    }

    private static void processAnalyzers(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'addAnalyzers' must not be null";
        assert report != null : "Parameter 'report' of method 'addAnalyzers' must not be null";

        for (final XsdAnalyzer next : report.getAnalyzers().getAnalyzer())
        {
            softwareSystem.addAnalyzer(new AnalyzerImpl(next.getName(), next.getPresentationName(), next.getDescription(), next.isLicensed()));
        }
    }

    private static void processFeatures(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'addAnalyzers' must not be null";
        assert report != null : "Parameter 'report' of method 'addAnalyzers' must not be null";

        for (final XsdFeature next : report.getFeatures().getFeature())
        {
            softwareSystem.addFeature(new FeatureImpl(next.getName(), next.getPresentationName(), next.isLicensed()));
        }
    }

    private void processMetricThresholds(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processMetricThresholds' must not be null";
        assert report != null : "Parameter 'report' of method 'processMetricThresholds' must not be null";

        final XsdMetricThresholds metricThresholds = report.getMetricThresholds();
        if (metricThresholds == null)
        {
            return;
        }
        for (final XsdMetricThreshold next : metricThresholds.getThreshold())
        {
            final Object id = globalXmlToElementMap.get(next.getMetricId());
            assert id != null : "metric id '" + next.getMetricId() + "' of threshold '" + next.getDebugInfo() + "' must exist";
            final IMetricId metricId = (IMetricId) id;

            final Object level = globalXmlToElementMap.get(next.getMetricLevel());
            assert level != null : "metric level '" + next.getMetricLevel() + "' of threshold '" + next.getDebugInfo() + "' must exist";
            final IMetricLevel metricLevel = (IMetricLevel) level;

            final MetricThreshold threshold = new MetricThreshold(metricId, metricLevel, next.getLowerThreshold(), next.getUpperThreshold());
            softwareSystem.addMetricThreshold(threshold);
            globalXmlToElementMap.put(next, threshold);
        }
    }

    private void processSystemElements(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report,
            final INamedElementAdjuster adjuster)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'addSystemElements' must not be null";
        assert report != null : "Parameter 'report' of method 'addSystemElements' must not be null";
        final XsdSystemElements xsdSystemElements = report.getSystemElements();
        if (xsdSystemElements != null)
        {
            for (final XsdNamedElement nextElement : xsdSystemElements.getElement())
            {
                final XsdElementKind elementKind = (XsdElementKind) nextElement.getKind();
                NamedElementImpl add = null;
                if (!SOFTWARE_SYSTEM_STANDARD_KIND.equals(elementKind.getStandardKind()))
                {
                    add = createNamedElementImpl(adjuster, nextElement, elementKind);
                    softwareSystem.addElement(add);
                }
                else
                {
                    add = softwareSystem;
                }
                assert !globalXmlToElementMap.containsKey(nextElement) : "element already contained: " + nextElement.getFqName();
                globalXmlToElementMap.put(nextElement, add);
            }
            for (final XsdLogicalNamespace nextXsdLogicalNamespace : xsdSystemElements.getLogicalNamespace())
            {
                //TODO
            }
            for (final XsdLogicalProgrammingElement nextXsdLogicalProgrammingElement : xsdSystemElements.getLogicalProgrammingElement())
            {
                //TODO
            }
        }
    }

    private void createModuleElements(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report,
            final INamedElementAdjuster adjuster)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'createModuleElements' must not be null";
        assert report != null : "Parameter 'report' of method 'createModuleElements' must not be null";

        for (final XsdModuleElements nextXsdModuleElements : report.getModuleElements())
        {
            final IElement moduleElement = globalXmlToElementMap.get(nextXsdModuleElements.getRef());
            assert moduleElement != null : "Module does not exist";
            final ModuleImpl module = (ModuleImpl) moduleElement;

            for (final XsdNamedElement nextElement : nextXsdModuleElements.getElement())
            {
                final XsdElementKind elementKind = (XsdElementKind) nextElement.getKind();
                NamedElementImpl add = null;
                if (!elementKind.getStandardKind().endsWith(MODULE_STANDARD_KIND_SUFFIX))
                {
                    add = createNamedElementImpl(adjuster, nextElement, elementKind);
                    module.addElement(add);
                }
                else
                {
                    add = module;
                }
                assert !globalXmlToElementMap.containsKey(nextElement) : "element already contained: " + nextElement.getFqName();
                globalXmlToElementMap.put(nextElement, add);
            }
            for (final XsdLogicalNamespace nextXsdLogicalNamespace : nextXsdModuleElements.getLogicalNamespace())
            {
                //TODO
            }
            for (final XsdLogicalProgrammingElement nextXsdLogicalProgrammingElement : nextXsdModuleElements.getLogicalProgrammingElement())
            {
                //TODO
            }
        }
    }

    private void setDerivedFrom(final XsdLogicalElement xsLogicalElement, final LogicalElementImpl logicalElementImpl)
    {
        assert xsLogicalElement != null : "Parameter 'xsLogicalElement' of method 'setDerivedFrom' must not be null";
        assert logicalElementImpl != null : "Parameter 'logicalElementImpl' of method 'setDerivedFrom' must not be null";

        for (final Object nextDerivedFrom : xsLogicalElement.getDerivedFrom())
        {
            final IElement element = globalXmlToElementMap.get(nextDerivedFrom);
            if (element != null)
            {
                if (element instanceof NamedElementImpl)
                {
                    logicalElementImpl.addDerivedFrom((NamedElementImpl) element);
                }
                else
                {
                    LOGGER.warn("Unexpected element class '" + element.getClass().getName() + "' for 'derivedFrom':" + nextDerivedFrom);
                }
            }
            else
            {
                LOGGER.warn("No element has been created for 'derivedFrom':" + nextDerivedFrom);
            }
        }
    }

    private void processMetrics(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport xsdReport)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processMetrics' must not be null";
        assert xsdReport != null : "Parameter 'xsdReport' of method 'processMetrics' must not be null";

        final Map<Object, MetricCategoryImpl> categoryXsdToPojoMap = XmlExportMetaDataReader.processMetricCategories(xsdReport.getMetaData());
        for (final MetricCategoryImpl category : categoryXsdToPojoMap.values())
        {
            softwareSystem.addMetricCategory(category);
        }
        globalXmlToElementMap.putAll(categoryXsdToPojoMap);

        final Map<Object, MetricProviderImpl> providerXsdToPojoMap = XmlExportMetaDataReader.processProviders(xsdReport.getMetaData());
        for (final MetricProviderImpl provider : providerXsdToPojoMap.values())
        {
            softwareSystem.addMetricProvider(provider);
        }
        globalXmlToElementMap.putAll(providerXsdToPojoMap);

        final Map<Object, MetricLevelImpl> metricLevelXsdToPojoMap = XmlExportMetaDataReader.processMetricLevels(xsdReport.getMetaData());
        for (final MetricLevelImpl level : metricLevelXsdToPojoMap.values())
        {
            softwareSystem.addMetricLevel(level);
        }
        globalXmlToElementMap.putAll(metricLevelXsdToPojoMap);

        final Map<Object, MetricIdImpl> metricIdXsdToPojoMap = XmlExportMetaDataReader.processMetricIds(xsdReport.getMetaData(),
                categoryXsdToPojoMap, providerXsdToPojoMap, metricLevelXsdToPojoMap);
        for (final MetricIdImpl id : metricIdXsdToPojoMap.values())
        {
            softwareSystem.addMetricId(id);
        }
        globalXmlToElementMap.putAll(metricIdXsdToPojoMap);

        final XsdSystemMetricValues xsdSystemMetricLevel = xsdReport.getSystemMetricValues();
        if (xsdSystemMetricLevel != null)
        {
            for (final XsdMetricLevelValues nextXsdLevelValues : xsdReport.getSystemMetricValues().getLevelValues())
            {
                final MetricLevelImpl level = metricLevelXsdToPojoMap.get(nextXsdLevelValues.getLevelRef());
                assert level != null : "level has not been created for system level metric values";

                for (final XsdMetricValue nextXsdMetricValue : nextXsdLevelValues.getMetric())
                {
                    for (final XsdMetricIntValue nextIntValue : nextXsdMetricValue.getInt())
                    {
                        addMetricValue(softwareSystem, level, nextXsdMetricValue.getRef(), () -> new Integer(nextIntValue.getValue()));
                    }
                    for (final XsdMetricFloatValue nextFloatValue : nextXsdMetricValue.getFloat())
                    {
                        addMetricValue(softwareSystem, level, nextXsdMetricValue.getRef(), () -> new Float(nextFloatValue.getValue()));
                    }
                }
            }
        }

        for (final XsdModuleMetricValues nextXsdModuleMetrics : xsdReport.getModuleMetricValues())
        {
            final XsdElement xsdModule = (XsdElement) nextXsdModuleMetrics.getElementRef();
            assert xsdModule != null && xsdModule instanceof XsdModule : "Unexpected class in method 'processMetrics': " + xsdModule;
            final ModuleImpl module = (ModuleImpl) softwareSystem.getModules().get(((XsdModule) xsdModule).getName());
            assert module != null : "module '" + xsdModule.getName() + "' has not been added";
            final Map<String, IMetricLevel> metricLevels = module.getAllMetricLevels();

            for (final XsdMetricLevelValues nextXsdModuleMetricsLevelValues : nextXsdModuleMetrics.getLevelValues())
            {
                final IMetricLevel currentLevel = metricLevelXsdToPojoMap.get(nextXsdModuleMetricsLevelValues.getLevelRef());
                assert currentLevel != null : "MetricLevel not processed for "
                        + ((XsdMetricLevel) nextXsdModuleMetricsLevelValues.getLevelRef()).getId();

                if (!metricLevels.containsKey(currentLevel.getName()))
                {
                    module.addMetricLevel(currentLevel);
                }

                for (final XsdMetricValue nextValue : nextXsdModuleMetricsLevelValues.getMetric())
                {
                    for (final XsdMetricIntValue nextIntValue : nextValue.getInt())
                    {
                        addMetricValue(softwareSystem, module, currentLevel, nextValue.getRef(), nextIntValue.getRef(), () -> new Integer(
                                nextIntValue.getValue()));
                    }

                    for (final XsdMetricFloatValue nextFloatValue : nextValue.getFloat())
                    {
                        addMetricValue(softwareSystem, module, currentLevel, nextValue.getRef(), nextFloatValue.getRef(), () -> new Float(
                                nextFloatValue.getValue()));
                    }
                }
            }
        }
    }

    private void addMetricValue(final SoftwareSystemImpl softwareSystem, final ModuleImpl module, final IMetricLevel level, final Object metricIdRef,
            final Object elementRef, final Supplier<Number> supplier)
    {
        final Object metricId = globalXmlToElementMap.get(metricIdRef);
        assert metricId != null && metricId instanceof IMetricId : "Unexpected class in method 'processMetrics': " + metricId + ", "
                + ((XsdMetricId) metricIdRef).getName() + "' has not been added";

        final IElement element = globalXmlToElementMap.get(elementRef);
        assert element != null : "Element " + ((XsdElement) elementRef).getName() + " not found!";
        assert element != null && element instanceof INamedElement : "Unexpected class in method 'addMetricValue': " + element;

        final IMetricValue value = new MetricValueImpl((IMetricId) metricId, level, supplier.get());
        module.addMetricValueForElement(value, (INamedElement) element);
        softwareSystem.addMetricValueForElement(value, (INamedElement) element);
    }

    private void addMetricValue(final SoftwareSystemImpl softwareSystem, final IMetricLevel level, final Object metricIdRef,
            final Supplier<Number> supplier)
    {
        final Object metricId = globalXmlToElementMap.get(metricIdRef);
        assert metricId != null && metricId instanceof IMetricId : "Unexpected class in method 'processMetrics': " + metricId + ", "
                + ((XsdMetricId) metricIdRef).getName() + "' has not been added";

        final MetricValueImpl value = new MetricValueImpl((MetricIdImpl) metricId, level, supplier.get());
        softwareSystem.addMetricValueForElement(value, softwareSystem);
    }

    private void processIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report, final OperationResult result)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processIssues' must not be null";
        assert report != null : "Parameter 'report' of method 'processIssues' must not be null";
        assert result != null : "Parameter 'result' of method 'processIssues' must not be null";

        if (report.getIssues() == null)
        {
            softwareSystem.setNumberOfIssues(0);
            return;
        }
        softwareSystem.setNumberOfIssues(report.getIssues().getNumberOfIssues());

        for (final XsdIssueProvider next : report.getMetaData().getIssueProviders().getIssueProvider())
        {
            final IssueProviderImpl issueProvider = new IssueProviderImpl(next.getName(), next.getPresentationName());
            softwareSystem.addIssueProvider(issueProvider);
            globalXmlToElementMap.put(next, issueProvider);
        }

        final Map<Object, IssueCategoryImpl> issueCategoryXsdToPojoMap = XmlExportMetaDataReader.processIssueCategories(report.getMetaData());
        for (final IssueCategoryImpl category : issueCategoryXsdToPojoMap.values())
        {
            softwareSystem.addIssueCategory(category);
        }
        globalXmlToElementMap.putAll(issueCategoryXsdToPojoMap);

        for (final XsdIssueType next : report.getMetaData().getIssueTypes().getIssueType())
        {
            Severity severity;
            try
            {
                severity = Severity.valueOf(StringUtility.convertStandardNameToConstantName(next.getSeverity()));
            }
            catch (final Exception e)
            {
                LOGGER.error("Failed to process severity type '" + next.getSeverity() + "'", e);
                result.addWarning(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT, "Severity type '" + next.getSeverity()
                        + "' is not supported, setting to '" + Severity.ERROR + "'");
                severity = Severity.ERROR;
            }
            final IElement namedElement = globalXmlToElementMap.get(next.getCategory());
            assert namedElement != null && namedElement instanceof IIssueCategory : "Unexpected class in method 'processIssues': " + namedElement;

            final IElement provider;
            if (next.getProvider() != null)
            {
                provider = globalXmlToElementMap.get(next.getProvider());
            }
            else
            {
                provider = null;
            }

            final IssueTypeImpl issueType = new IssueTypeImpl(next.getName(), next.getPresentationName(), severity, (IIssueCategory) namedElement,
                    (IIssueProvider) provider, next.getDescription());
            softwareSystem.addIssueType(issueType);
        }

        processSimpleElementIssues(softwareSystem, report);

        if (report.getIssues().getElementIssues() != null)
        {
            processCycleGroupIssues(softwareSystem, report);
            processDuplicateIssues(softwareSystem, report);
            processThresholdIssues(softwareSystem, report);
        }

        if (report.getIssues().getDepencencyIssues() != null)
        {
            processDependencyIssues(softwareSystem, report);
        }
    }

    private void processDependencyIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        for (final XsdDependencyIssue nextDependencyIssue : report.getIssues().getDepencencyIssues().getIssue())
        {
            final IIssueType issueType = getIssueType(softwareSystem, nextDependencyIssue);
            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, nextDependencyIssue);

            final String fromId = ((XsdElement) nextDependencyIssue.getFrom()).getId();
            final IElement from = globalXmlToElementMap.get(nextDependencyIssue.getFrom());
            assert from != null : "'from' element (" + fromId + ") of dependency issue '" + nextDependencyIssue.getId() + "' not found";
            assert from != null && from instanceof INamedElement : "Unexpected class in method 'processDependencyIssues': " + from;

            final String toId = ((XsdElement) nextDependencyIssue.getTo()).getId();
            final IElement to = globalXmlToElementMap.get(nextDependencyIssue.getTo());
            assert to != null : "'to' element (" + toId + ") of dependency issue '" + nextDependencyIssue.getId() + "' not found";
            assert to != null && to instanceof INamedElement : "Unexpected class in method 'processDependencyIssues': " + to;
            final DependencyIssueImpl dependencyIssue = new DependencyIssueImpl(issueType, nextDependencyIssue.getDescription(), issueProvider,
                    nextDependencyIssue.isHasResolution(), (INamedElement) from, (INamedElement) to, nextDependencyIssue.getLine());
            softwareSystem.addIssue(dependencyIssue);
            globalXmlIdToIssueMap.put(nextDependencyIssue, dependencyIssue);
        }
    }

    private void processDuplicateIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        for (final XsdDuplicateBlockIssue nextDuplicate : report.getIssues().getElementIssues().getDuplicate())
        {
            final IIssueType issueType = getIssueType(softwareSystem, nextDuplicate);
            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, nextDuplicate);

            final List<IDuplicateCodeBlockOccurrence> occurrences = new ArrayList<>(nextDuplicate.getNumberOfOccurrences());

            for (final XsdDuplicateCodeBlockOccurrence nextOccurence : nextDuplicate.getOccurrence())
            {
                final IElement element = globalXmlToElementMap.get(nextOccurence.getSource());
                assert element instanceof ISourceFile : "Unexpected element class: " + element.getClass().getName();
                final IDuplicateCodeBlockOccurrence occurrence = new DuplicateCodeBlockOccurrenceImpl((ISourceFile) element,
                        nextOccurence.getBlockSize(), nextOccurence.getStartLine(), nextOccurence.getTolerance());
                occurrences.add(occurrence);
            }

            final DuplicateCodeBlockIssueImpl duplicate = new DuplicateCodeBlockIssueImpl(nextDuplicate.getFqName(), nextDuplicate.getName(),
                    nextDuplicate.getDescription(), issueType, issueProvider, nextDuplicate.isHasResolution(), occurrences);
            duplicate.setBlockSize(nextDuplicate.getBlockSize());
            softwareSystem.addDuplicateCodeBlock(duplicate);

            globalXmlIdToIssueMap.put(nextDuplicate, duplicate);
        }
    }

    private void processCycleGroupIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        for (final XsdCycleGroupContainer nextCycleContainer : report.getIssues().getElementIssues().getCycleGroups())
        {
            final String analyzerName = ((XsdAnalyzer) nextCycleContainer.getAnalyzerRef()).getName();
            final IAnalyzer analyzer = softwareSystem.getAnalyzers().get(analyzerName);
            assert analyzer != null : "Analyzer '" + analyzerName + "' does not exist!";

            for (final XsdCycleIssue nextCycle : nextCycleContainer.getCycleGroup())
            {
                final IIssueType issueType = getIssueType(softwareSystem, nextCycle);
                final IIssueProvider issueProvider = getIssueProvider(softwareSystem, nextCycle);

                final String name = nextCycle.getName();
                //This name might not not be set -> use the old name 'issueProvider.getPresentationName()' 
                final CycleGroupIssueImpl cycleGroup = new CycleGroupIssueImpl(nextCycle.getFqName(), name != null && !name.isEmpty() ? name
                        : issueProvider.getPresentationName(), nextCycle.getDescription(), issueType, issueProvider, nextCycle.isHasResolution(),
                        analyzer);

                final List<INamedElement> cyclicElements = new ArrayList<>();
                for (final XsdCycleElement nextElement : nextCycle.getElement())
                {
                    final IElement element = globalXmlToElementMap.get(nextElement.getRef());
                    assert element instanceof INamedElement : "Unexpected class " + element.getClass().getName();
                    cyclicElements.add((INamedElement) element);
                }
                cycleGroup.setAffectedElements(cyclicElements);

                softwareSystem.addCycleGroup(cycleGroup);
                globalXmlIdToIssueMap.put(nextCycle, cycleGroup);
            }
        }
    }

    private void processThresholdIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        for (final XsdMetricThresholdViolationIssue next : report.getIssues().getElementIssues().getThresholdViolation())
        {
            final XsdElement affectedElement = (XsdElement) next.getAffectedElement();
            final IElement affected = globalXmlToElementMap.get(affectedElement);
            assert affected != null : "Affected element of issue '" + next + "' has not been processed";
            assert affected != null && affected instanceof INamedElement : "Unexpected class in method 'processSimpleElementIssues': " + affected;

            final IIssueType issueType = getIssueType(softwareSystem, next);
            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, next);

            final IElement threshold = globalXmlToElementMap.get(next.getThresholdRef());
            assert threshold != null : "threshold has not been added to system for '" + next.getDescription() + "'";

            final IMetricThreshold metricThreshold = (IMetricThreshold) threshold;
            final ThresholdViolationIssue issue = new ThresholdViolationIssue(issueType, next.getDescription() != null ? next.getDescription() : "",
                    issueProvider, (INamedElement) affected, next.isHasResolution(), next.getLine(), next.getMetricValue(), metricThreshold);
            softwareSystem.addIssue(issue);
            globalXmlIdToIssueMap.put(next, issue);
        }
    }

    private void processSimpleElementIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert report != null : "Parameter 'report' of method 'processSimpleElementIssues' must not be null";

        if (report.getIssues() == null || report.getIssues().getElementIssues() == null)
        {
            return;
        }

        for (final XsdSimpleElementIssue next : report.getIssues().getElementIssues().getIssue())
        {
            final XsdElement affectedElement = (XsdElement) next.getAffectedElement();
            final IElement affected = globalXmlToElementMap.get(affectedElement);
            assert affected != null : "Affected element of issue '" + next + "' has not been processed";
            assert affected != null && affected instanceof INamedElement : "Unexpected class in method 'processSimpleElementIssues': " + affected;

            final IIssueType issueType = getIssueType(softwareSystem, next);
            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, next);

            final AbstractElementIssueImpl issue = new ElementIssueImpl(issueType, next.getDescription() != null ? next.getDescription() : "",
                    issueProvider, (INamedElement) affected, next.isHasResolution(), next.getLine());
            softwareSystem.addIssue(issue);
            globalXmlIdToIssueMap.put(next, issue);
        }
    }

    private static IIssueProvider getIssueProvider(final SoftwareSystemImpl softwareSystem, final XsdIssue xsdIssue)
    {
        final String issueProviderName = ((XsdIssueProvider) xsdIssue.getProvider()).getName();
        final IIssueProvider issueProvider = softwareSystem.getIssueProviders().get(issueProviderName);
        assert issueProvider != null : "issueProvider '" + issueProviderName + "' has not been added to system";
        return issueProvider;
    }

    private static IIssueType getIssueType(final SoftwareSystemImpl softwareSystem, final XsdIssue xsdIssue)
    {
        final String issueTypeName = ((XsdIssueType) xsdIssue.getType()).getName();
        final IIssueType issueType = softwareSystem.getIssueTypes().get(issueTypeName);
        assert issueType != null : "issueType '" + issueTypeName + "' has not been added to system";
        return issueType;
    }
}