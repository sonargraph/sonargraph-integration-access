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
import com.hello2morrow.sonargraph.integration.access.model.internal.IProgrammingElementContainer;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueTypeImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LanguageBasedContainerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LogicalElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LogicalNamespaceImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LogicalProgrammingElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricIdImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricLevelImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricValueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementContainerImpl;
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
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternal;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternalModuleScopeElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternalSystemScopeElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFeature;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalNamespace;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalProgrammingElement;
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
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPhysicalRecursiveElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdProgrammingElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdResolution;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRootDirectory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSimpleElementIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSourceFile;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemMetricValues;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdWorkspace;

public final class XmlReportReader extends XmlAccess
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportReader.class);
    private static final String SOFTWARE_SYSTEM_STANDARD_KIND = "SoftwareSystem";
    private static final String MODULE_STANDARD_KIND_SUFFIX = "Module";
    private static final String EXTERNAL_STANDARD_KIND_SUFFIX = "External";
    private final Map<Object, IElement> globalXmlToElementMap = new HashMap<>();
    private final Map<Object, IssueImpl> globalXmlIdToIssueMap = new HashMap<>();
    private File currentlyReading;

    /**
     * Reads an XML report.
     * @param reportFile XML file that is expected to exist and be readable.
     * @param result Contains info about errors.
     */
    public Optional<SoftwareSystemImpl> readReportFile(final File reportFile, final OperationResult result)
    {
        assert reportFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert reportFile.exists() : "Parameter 'reportFile' of method 'readReportFile' must be an existing file";
        assert reportFile.canRead() : "Parameter 'reportFile' of method 'readReportFile' must be a file with read access";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";
        assert currentlyReading == null : "'currentlyReading' of method 'readReportFile' must be null";

        JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter;
        try
        {
            jaxbAdapter = createReportJaxbAdapter();
        }
        catch (final Exception e)
        {
            result.addError(IOMessageCause.READ_ERROR, "Failed to initialize JAXB", e);
            return Optional.empty();
        }

        this.currentlyReading = reportFile;

        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);

        Optional<JAXBElement<XsdSoftwareSystemReport>> xmlRoot = Optional.empty();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(reportFile)))
        {
            xmlRoot = jaxbAdapter.load(in, eventHandler);
            if (xmlRoot.isPresent())
            {
                return convertXmlReportToPojo(xmlRoot.get().getValue(), result);
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
            this.currentlyReading = null;
        }

        return Optional.empty();
    }

    private XsdElementKind getXsdElementKind(final XsdNamedElement xsdNamedElement)
    {
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'getXsdElementKind' must not be null";
        assert currentlyReading != null : "'currentlyReading' of method 'getXsdElementKind' must not be null";

        final Object kind = xsdNamedElement.getKind();
        if (kind == null)
        {
            final String msg = "No associated element kind found for named element: " + xsdNamedElement.getFqName();
            LOGGER.error(msg);
            throw new NullPointerException(msg);
        }
        if (kind instanceof XsdElementKind == false)
        {
            final String msg = "Associated element kind is of wrong class '" + kind.getClass().getName() + "' for named element: "
                    + xsdNamedElement.getFqName();
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return (XsdElementKind) kind;
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

    private Optional<SoftwareSystemImpl> convertXmlReportToPojo(final XsdSoftwareSystemReport report, final OperationResult result)
    {
        assert report != null : "Parameter 'report' of method 'convertXmlReportToPojo' must not be null";
        assert result != null : "Parameter 'result' of method 'convertXmlReportToPojo' must not be null";

        final String systemDescription = report.getSystemDescription() != null ? report.getSystemDescription().trim() : "";
        final SoftwareSystemImpl softwareSystem = new SoftwareSystemImpl("SoftwareSystem", "System", report.getSystemId(), report.getName(),
                systemDescription, report.getSystemPath(), report.getVersion(), report.getTimestamp().toGregorianCalendar().getTimeInMillis(),
                report.getCurrentVirtualModel());
        softwareSystem.addElement(softwareSystem);

        processAnalyzers(softwareSystem, report);
        processFeatures(softwareSystem, report);

        final XsdWorkspace xsdWorkspace = report.getWorkspace();
        createWorkspaceElements(softwareSystem, xsdWorkspace, result);
        if (result.isFailure())
        {
            return Optional.empty();
        }

        finishWorkspaceElementCreation(xsdWorkspace);

        createSystemElements(softwareSystem, report);
        createModuleElements(report);
        createExternalScopeElements(report);

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

    private void createNamedElementImpl(final NamedElementContainerImpl namedElementContainerImpl, final XsdNamedElement xsdNamedElement,
            final XsdElementKind xsdElementKind)
    {
        assert namedElementContainerImpl != null : "Parameter 'namedElementContainerImpl' of method 'createNamedElementImpl' must not be null";
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'createNamedElementImpl' must not be null";
        assert xsdElementKind != null : "Parameter 'xsdElementKind' of method 'createNamedElementImpl' must not be null";

        final NamedElementImpl namedElementImpl = new NamedElementImpl(xsdElementKind.getStandardKind(), xsdElementKind.getPresentationKind(),
                xsdNamedElement.getName(), xsdNamedElement.getPresentationName(), xsdNamedElement.getFqName());
        namedElementContainerImpl.addElement(namedElementImpl);
        globalXmlToElementMap.put(xsdNamedElement, namedElementImpl);
    }

    private void createSourceFileImpl(final NamedElementContainerImpl namedElementContainerImpl, final RootDirectoryImpl rootDirectoryImpl,
            final XsdSourceFile xsdSourceFile)
    {
        assert rootDirectoryImpl != null : "Parameter 'rootDirectoryImpl' of method 'createSourceFileImpl' must not be null";
        assert xsdSourceFile != null : "Parameter 'xsdSourceFile' of method 'createSourceFileImpl' must not be null";

        final XsdElementKind sourceKind = (XsdElementKind) xsdSourceFile.getKind();
        final SourceFileImpl sourceFileImpl = new SourceFileImpl(sourceKind.getStandardKind(), sourceKind.getPresentationKind(),
                xsdSourceFile.getName(), xsdSourceFile.getPresentationName(), xsdSourceFile.getFqName(), rootDirectoryImpl.getRelativePath());
        rootDirectoryImpl.addSourceFile(sourceFileImpl);
        //        namedElementContainerImpl.addElement(sourceFileImpl);
        globalXmlToElementMap.put(xsdSourceFile, sourceFileImpl);
    }

    private void createProgrammingElementImpl(final NamedElementContainerImpl namedElementContainerImpl,
            final IProgrammingElementContainer programmingElementContainer, final XsdProgrammingElement xsdProgrammingElement)
    {
        assert programmingElementContainer != null : "Parameter 'programmingElementContainer' of method 'createProgrammingElementImpl' must not be null";
        assert xsdProgrammingElement != null : "Parameter 'xsdProgrammingElement' of method 'createProgrammingElementImpl' must not be null";

        final XsdElementKind xsdElementKind = getXsdElementKind(xsdProgrammingElement);
        final ProgrammingElementImpl programmingElementImpl = new ProgrammingElementImpl(xsdElementKind.getStandardKind(),
                xsdElementKind.getPresentationKind(), xsdProgrammingElement.getName(), xsdProgrammingElement.getPresentationName(),
                xsdProgrammingElement.getFqName(), xsdProgrammingElement.getLine());
        programmingElementContainer.addProgrammingElement(programmingElementImpl);
        namedElementContainerImpl.addElement(programmingElementImpl);
        globalXmlToElementMap.put(xsdProgrammingElement, programmingElementImpl);
    }

    private void createPhysicalRecursiveElenentImpl(final NamedElementContainerImpl namedElementContainerImpl,
            final IProgrammingElementContainer programmingElementContainer, final XsdPhysicalRecursiveElement xsdPhysicalRecursiveElement,
            final String relativeRootDirectory)
    {
        assert namedElementContainerImpl != null : "Parameter 'namedElementContainerImpl' of method 'createPhysicalRecursiveElenentImpl' must not be null";
        assert programmingElementContainer != null : "Parameter 'programmingElementContainer' of method 'createPhysicalRecursiveElenentImpl' must not be null";
        assert xsdPhysicalRecursiveElement != null : "Parameter 'xsdPhysicalRecursiveElement' of method 'createPhysicalRecursiveElenentImpl' must not be null";
        //'relativeRootDirectory' might be 'null';

        final XsdElementKind xsdElementKind = getXsdElementKind(xsdPhysicalRecursiveElement);
        final PhysicalRecursiveElementImpl physicalRecursiveElementImpl = new PhysicalRecursiveElementImpl(xsdElementKind.getStandardKind(),
                xsdElementKind.getPresentationKind(), xsdPhysicalRecursiveElement.getName(), xsdPhysicalRecursiveElement.getPresentationName(),
                xsdPhysicalRecursiveElement.getFqName());

        final String nextRelativeDirectory = xsdPhysicalRecursiveElement.getRelativeDirectoryPath();
        if (nextRelativeDirectory != null)
        {
            physicalRecursiveElementImpl.setRelativeDirectory(nextRelativeDirectory);
        }
        else
        {
            final Object nextSource = xsdPhysicalRecursiveElement.getSource();
            if (nextSource != null)
            {
                final IElement element = globalXmlToElementMap.get(nextSource);
                if (element != null)
                {
                    assert element instanceof ISourceFile : "Unexpected element class: " + element.getClass().getName();
                    physicalRecursiveElementImpl.setSourceFile((ISourceFile) element);
                }
                else
                {
                    //TODO warn
                }
            }
        }

        if (relativeRootDirectory != null)
        {
            physicalRecursiveElementImpl.setRelativeRootDirectory(relativeRootDirectory);
        }
        globalXmlToElementMap.put(xsdPhysicalRecursiveElement, physicalRecursiveElementImpl);
        programmingElementContainer.addPhysicalRecursiveElement(physicalRecursiveElementImpl);
        namedElementContainerImpl.addElement(physicalRecursiveElementImpl);
    }

    private void createRootDirectoryImpl(final LanguageBasedContainerImpl languageBasedContainerImpl, final XsdRootDirectory xsdRootDirectory)
    {
        assert languageBasedContainerImpl != null : "Parameter 'languageBasedContainerImpl' of method 'createRootDirectoryImpl' must not be null";
        assert xsdRootDirectory != null : "Parameter 'xsdRootDirectory' of method 'createRootDirectoryImpl' must not be null";

        final XsdElementKind xsdElementKind = (XsdElementKind) xsdRootDirectory.getKind();
        final RootDirectoryImpl rootDirectoryImpl = new RootDirectoryImpl(xsdElementKind.getStandardKind(), xsdElementKind.getPresentationKind(),
                xsdRootDirectory.getPresentationName(), xsdRootDirectory.getFqName());
        languageBasedContainerImpl.addRootDirectory(rootDirectoryImpl);
        languageBasedContainerImpl.addElement(rootDirectoryImpl);
        globalXmlToElementMap.put(xsdRootDirectory, rootDirectoryImpl);

        for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : xsdRootDirectory.getPhysicalRecursiveElement())
        {
            createPhysicalRecursiveElenentImpl(languageBasedContainerImpl, rootDirectoryImpl, nextXsdPhysicalRecursiveElement,
                    rootDirectoryImpl.getRelativePath());
        }
        for (final XsdSourceFile nextXsdSourceFile : xsdRootDirectory.getSourceElement())
        {
            createSourceFileImpl(languageBasedContainerImpl, rootDirectoryImpl, nextXsdSourceFile);
        }
        for (final XsdProgrammingElement nextXsdProgrammingElement : xsdRootDirectory.getProgrammingElement())
        {
            createProgrammingElementImpl(languageBasedContainerImpl, rootDirectoryImpl, nextXsdProgrammingElement);
        }
    }

    private void createWorkspaceElements(final SoftwareSystemImpl softwareSystem, final XsdWorkspace xsdWorkspace, final OperationResult result)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'createWorkspaceElements' must not be null";
        assert xsdWorkspace != null : "Parameter 'report' of method 'createWorkspaceElements' must not be null";
        assert result != null : "Parameter 'result' of method 'createWorkspaceElements' must not be null";

        for (final XsdModule nextXsdModule : xsdWorkspace.getModule())
        {
            final XsdElementKind moduleKind = getXsdElementKind(nextXsdModule);
            final ModuleImpl nextModuleImpl = new ModuleImpl(moduleKind.getStandardKind(), moduleKind.getPresentationKind(), nextXsdModule.getName(),
                    nextXsdModule.getPresentationName(), nextXsdModule.getFqName(), nextXsdModule.getDescription(),
                    softwareSystem.getMetaDataAccess(), softwareSystem.getElementRegistry(), nextXsdModule.getLanguage());
            softwareSystem.addModule(nextModuleImpl);
            nextModuleImpl.addElement(nextModuleImpl);
            globalXmlToElementMap.put(nextXsdModule, nextModuleImpl);

            for (final XsdRootDirectory nextXsdRootDirectory : nextXsdModule.getRootDirectory())
            {
                createRootDirectoryImpl(nextModuleImpl, nextXsdRootDirectory);
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
            globalXmlToElementMap.put(nextXsdExternal, nextExternalImpl);

            for (final XsdRootDirectory nextXsdRootDirectory : nextXsdExternal.getRootDirectory())
            {
                createRootDirectoryImpl(nextExternalImpl, nextXsdRootDirectory);
            }
            for (final XsdPhysicalRecursiveElement nextXsdPhysicalRecursiveElement : nextXsdExternal.getPhysicalRecursiveElement())
            {
                createPhysicalRecursiveElenentImpl(nextExternalImpl, nextExternalImpl, nextXsdPhysicalRecursiveElement, null);
            }
            for (final XsdProgrammingElement nextXsdProgrammingElement : nextXsdExternal.getProgrammingElement())
            {
                createProgrammingElementImpl(nextExternalImpl, nextExternalImpl, nextXsdProgrammingElement);
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

    private void createLogicalElements(final NamedElementContainerImpl namedElementContainerImpl, final XsdElements xsdElements)
    {
        assert namedElementContainerImpl != null : "Parameter 'namedElementContainerImpl' of method 'createLogicalElements' must not be null";
        assert xsdElements != null : "Parameter 'xsdElements' of method 'createLogicalElements' must not be null";

        for (final XsdLogicalNamespace nextXsdLogicalNamespace : xsdElements.getLogicalNamespace())
        {
            final XsdElementKind xsdElementKind = getXsdElementKind(nextXsdLogicalNamespace);
            final LogicalNamespaceImpl logicalNamespaceImpl = new LogicalNamespaceImpl(xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdLogicalNamespace.getName(), nextXsdLogicalNamespace.getPresentationName(),
                    nextXsdLogicalNamespace.getFqName());
            globalXmlToElementMap.put(nextXsdLogicalNamespace, logicalNamespaceImpl);
            namedElementContainerImpl.addElement(logicalNamespaceImpl);
            namedElementContainerImpl.addLogicalNamespace(logicalNamespaceImpl);
            setDerivedFrom(nextXsdLogicalNamespace, logicalNamespaceImpl);
        }
        for (final XsdLogicalProgrammingElement nextXsdLogicalProgrammingElement : xsdElements.getLogicalProgrammingElement())
        {
            final XsdElementKind xsdElementKind = getXsdElementKind(nextXsdLogicalProgrammingElement);
            final LogicalProgrammingElementImpl logicalProgrammingElementImpl = new LogicalProgrammingElementImpl(xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdLogicalProgrammingElement.getName(),
                    nextXsdLogicalProgrammingElement.getPresentationName(), nextXsdLogicalProgrammingElement.getFqName());
            globalXmlToElementMap.put(nextXsdLogicalProgrammingElement, logicalProgrammingElementImpl);
            namedElementContainerImpl.addElement(logicalProgrammingElementImpl);
            namedElementContainerImpl.addLogicalProgrammingElement(logicalProgrammingElementImpl);
            setDerivedFrom(nextXsdLogicalProgrammingElement, logicalProgrammingElementImpl);
        }
    }

    private void createSystemElements(final SoftwareSystemImpl softwareSystemImpl, final XsdSoftwareSystemReport report)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'createSystemElements' must not be null";
        assert report != null : "Parameter 'report' of method 'createSystemElements' must not be null";

        final XsdSystemElements xsdSystemElements = report.getSystemElements();
        if (xsdSystemElements != null)
        {
            for (final XsdNamedElement nextElement : xsdSystemElements.getElement())
            {
                final XsdElementKind elementKind = getXsdElementKind(nextElement);
                if (!SOFTWARE_SYSTEM_STANDARD_KIND.equals(elementKind.getStandardKind()))
                {
                    createNamedElementImpl(softwareSystemImpl, nextElement, elementKind);
                }
                else
                {
                    //We have the software system itself - register it!
                    globalXmlToElementMap.put(nextElement, softwareSystemImpl);
                }
            }
            createLogicalElements(softwareSystemImpl, xsdSystemElements);
        }
    }

    private void createModuleElements(final XsdSoftwareSystemReport report)
    {
        assert report != null : "Parameter 'report' of method 'createModuleElements' must not be null";

        for (final XsdModuleElements nextXsdModuleElements : report.getModuleElements())
        {
            final IElement nextElement = globalXmlToElementMap.get(nextXsdModuleElements.getRef());
            assert nextElement != null && nextElement instanceof ModuleImpl : "Unexpected class in method 'createModuleElements': " + nextElement;
            final ModuleImpl nextModuleImpl = (ModuleImpl) nextElement;

            for (final XsdNamedElement nextModuleElement : nextXsdModuleElements.getElement())
            {
                final XsdElementKind elementKind = getXsdElementKind(nextModuleElement);
                if (!elementKind.getStandardKind().endsWith(MODULE_STANDARD_KIND_SUFFIX))
                {
                    createNamedElementImpl(nextModuleImpl, nextModuleElement, elementKind);
                }
            }
            createLogicalElements(nextModuleImpl, nextXsdModuleElements);
        }
    }

    private void createExternalScopeElements(final XsdSoftwareSystemReport report)
    {
        assert report != null : "Parameter 'report' of method 'createModuleElements' must not be null";

        for (final XsdExternalSystemScopeElements nextXsdExternalSystemScopeElements : report.getExternalSystemScopeElements())
        {
            final IElement nextElement = globalXmlToElementMap.get(nextXsdExternalSystemScopeElements.getRef());
            assert nextElement != null && nextElement instanceof ExternalImpl : "Unexpected class in method 'createExternalScopeElements': "
                    + nextElement;
            final ExternalImpl nextExternalImpl = (ExternalImpl) nextElement;

            for (final XsdNamedElement nextNamedElement : nextXsdExternalSystemScopeElements.getElement())
            {
                final XsdElementKind elementKind = getXsdElementKind(nextNamedElement);
                if (!elementKind.getStandardKind().endsWith(EXTERNAL_STANDARD_KIND_SUFFIX))
                {
                    createNamedElementImpl(nextExternalImpl, nextNamedElement, elementKind);
                }
            }
            createLogicalElements(nextExternalImpl, nextXsdExternalSystemScopeElements);
        }
        for (final XsdExternalModuleScopeElements nextXsdExternalModuleScopeElements : report.getExternalModuleScopeElements())
        {
            final IElement nextElement = globalXmlToElementMap.get(nextXsdExternalModuleScopeElements.getRef());
            assert nextElement != null && nextElement instanceof ExternalImpl : "Unexpected class in method 'createExternalScopeElements': "
                    + nextElement;
            final ExternalImpl nextExternalImpl = (ExternalImpl) nextElement;

            for (final XsdNamedElement nextNamedElement : nextXsdExternalModuleScopeElements.getElement())
            {
                final XsdElementKind elementKind = getXsdElementKind(nextNamedElement);
                if (!elementKind.getStandardKind().endsWith(EXTERNAL_STANDARD_KIND_SUFFIX))
                {
                    createNamedElementImpl(nextExternalImpl, nextNamedElement, elementKind);
                }
            }
            createLogicalElements(nextExternalImpl, nextXsdExternalModuleScopeElements);
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

    private void processThresholdIssues(final SoftwareSystemImpl softwareSystemImpl, final XsdSoftwareSystemReport report)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'processThresholdIssues' must not be null";
        assert report != null : "Parameter 'report' of method 'processThresholdIssues' must not be null";

        for (final XsdMetricThresholdViolationIssue nextXsdMetricThresholdViolationIssue : report.getIssues().getElementIssues()
                .getThresholdViolation())
        {
            final XsdElement nextAffectedXsdElement = (XsdElement) nextXsdMetricThresholdViolationIssue.getAffectedElement();
            final IElement nextAffectedElement = globalXmlToElementMap.get(nextAffectedXsdElement);
            assert nextAffectedElement != null : "Affected element of issue '" + nextXsdMetricThresholdViolationIssue
                    + "' has not been processed - xsd element id: " + nextAffectedXsdElement.getId();
            assert nextAffectedElement != null && nextAffectedElement instanceof INamedElement : "Unexpected class in method 'processSimpleElementIssues': "
                    + nextAffectedElement;

            final IIssueType issueType = getIssueType(softwareSystemImpl, nextXsdMetricThresholdViolationIssue);
            final IIssueProvider issueProvider = getIssueProvider(softwareSystemImpl, nextXsdMetricThresholdViolationIssue);

            final IElement threshold = globalXmlToElementMap.get(nextXsdMetricThresholdViolationIssue.getThresholdRef());
            assert threshold != null : "threshold has not been added to system for '" + nextXsdMetricThresholdViolationIssue.getDescription() + "'";

            final IMetricThreshold metricThreshold = (IMetricThreshold) threshold;
            final ThresholdViolationIssue issue = new ThresholdViolationIssue(issueType,
                    nextXsdMetricThresholdViolationIssue.getDescription() != null ? nextXsdMetricThresholdViolationIssue.getDescription() : "",
                    issueProvider, (INamedElement) nextAffectedElement, nextXsdMetricThresholdViolationIssue.isHasResolution(),
                    nextXsdMetricThresholdViolationIssue.getLine(), nextXsdMetricThresholdViolationIssue.getMetricValue(), metricThreshold);
            softwareSystemImpl.addIssue(issue);
            globalXmlIdToIssueMap.put(nextXsdMetricThresholdViolationIssue, issue);
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