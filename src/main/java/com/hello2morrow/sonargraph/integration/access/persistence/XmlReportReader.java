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

import com.hello2morrow.sonargraph.core.persistence.report.XsdAnalyzer;
import com.hello2morrow.sonargraph.core.persistence.report.XsdCycleElement;
import com.hello2morrow.sonargraph.core.persistence.report.XsdCycleGroupContainer;
import com.hello2morrow.sonargraph.core.persistence.report.XsdCycleIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdDependencyIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdDuplicateBlockIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.core.persistence.report.XsdElement;
import com.hello2morrow.sonargraph.core.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.core.persistence.report.XsdFeature;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricFloatValue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricId;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricIntValue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricLevel;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricLevelValues;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricThreshold;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricThresholdViolationIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricThresholds;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricValue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdModule;
import com.hello2morrow.sonargraph.core.persistence.report.XsdModuleElements;
import com.hello2morrow.sonargraph.core.persistence.report.XsdModuleMetricValues;
import com.hello2morrow.sonargraph.core.persistence.report.XsdNamedElement;
import com.hello2morrow.sonargraph.core.persistence.report.XsdResolution;
import com.hello2morrow.sonargraph.core.persistence.report.XsdRootDirectory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdSimpleElementIssue;
import com.hello2morrow.sonargraph.core.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.core.persistence.report.XsdSourceFile;
import com.hello2morrow.sonargraph.core.persistence.report.XsdSystemMetricValues;
import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
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
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.internal.AbstractElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.AnalyzerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.CycleGroupImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DependencyIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockOccurrenceImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.FeatureImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueTypeImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricIdImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricLevelImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricValueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.RootDirectoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SourceFileImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SourceRootDirectoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.java.ClassRootDirectory;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl.ValidationMessageCauses;

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
    private final Map<Object, IElement> globalXmlToElementMap = new HashMap<>();
    private final Map<Object, IIssue> globalXmlIdToIssueMap = new HashMap<>();

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

    private void connectOriginals(final List<XsdSourceFile> xsdSourceFiles)
    {
        assert xsdSourceFiles != null : "Parameter 'xsdSourceFiles' of method 'connectOriginals' must not be null";

        for (final XsdSourceFile nextXsdSourceFile : xsdSourceFiles)
        {
            final SourceFileImpl nextSourceFile = getSourceFile(nextXsdSourceFile);
            if (nextSourceFile == null)
            {
                LOGGER.error("No source file impl found for xsd source file '{}'.", nextXsdSourceFile.getFqName());
                continue;
            }

            final Object nextXsdOriginal = nextXsdSourceFile.getOriginal();
            if (nextXsdOriginal != null)
            {
                if (nextXsdOriginal instanceof XsdSourceFile)
                {
                    final SourceFileImpl original = getSourceFile((XsdSourceFile) nextXsdOriginal);
                    if (original != null)
                    {
                        nextSourceFile.setOriginal(original);
                        original.setIsOriginal(true);
                    }
                    else
                    {
                        LOGGER.error("No source file impl found for original xsd source file '{}'.", ((XsdSourceFile) nextXsdOriginal).getFqName());
                    }
                }
                else
                {
                    LOGGER.error("Unexpected class '{}' as original source file.", nextXsdOriginal.getClass().getCanonicalName());
                    continue;
                }
            }
        }

        //First we need the correct isOriginal() state
        for (final XsdSourceFile nextXsdSourceFile : xsdSourceFiles)
        {
            final SourceFileImpl nextSourceFile = getSourceFile(nextXsdSourceFile);
            if (nextSourceFile != null)
            {
                final IRootDirectory nextRootDirectory = nextSourceFile.getRootDirectory();
                assert nextRootDirectory != null && nextRootDirectory instanceof RootDirectoryImpl : "Unexpected class in method 'connectOriginals': "
                        + nextRootDirectory;
                ((RootDirectoryImpl) nextRootDirectory).addSourceFile(nextSourceFile);
            }
        }
    }

    private Optional<SoftwareSystemImpl> convertXmlReportToPojo(final XsdSoftwareSystemReport report, final INamedElementAdjuster adjuster,
            final OperationResult result)
    {
        assert report != null : "Parameter 'report' of method 'convertXmlReportToPojo' must not be null";
        assert result != null : "Parameter 'result' of method 'convertXmlReportToPojo' must not be null";

        final String systemDescription = report.getSystemDescription() != null ? report.getSystemDescription().trim() : "";
        final SoftwareSystemImpl softwareSystem = new SoftwareSystemImpl("SoftwareSystem", "System", report.getSystemId(), report.getName(),
                systemDescription, report.getSystemPath(), report.getVersion(), report.getTimestamp().toGregorianCalendar().getTimeInMillis(),
                report.getCurrentVirtualModel());

        processAnalyzers(softwareSystem, report);
        processFeatures(softwareSystem, report);
        final List<XsdSourceFile> xsdSourceFiles = processWorkspace(softwareSystem, report, result, adjuster);
        if (result.isFailure())
        {
            return Optional.empty();
        }
        connectOriginals(xsdSourceFiles);

        processSystemElements(softwareSystem, report, adjuster);
        processModuleElements(softwareSystem, report, adjuster);
        addSources();

        processMetrics(softwareSystem, report);
        processMetricThresholds(softwareSystem, report);

        processIssues(softwareSystem, report, result);
        processResolutions(softwareSystem, report, result);

        globalXmlToElementMap.clear();
        globalXmlIdToIssueMap.clear();

        return Optional.of(softwareSystem);
    }

    private void addSources()
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

    private List<XsdSourceFile> processWorkspace(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report,
            final OperationResult result, final INamedElementAdjuster adjuster)
    {
        final List<XsdSourceFile> xsdSourceFiles = new ArrayList<>();

        for (final XsdModule xsdModule : report.getWorkspace().getModule())
        {
            final XsdElementKind moduleKind = (XsdElementKind) xsdModule.getKind();
            final ModuleImpl module = new ModuleImpl(moduleKind.getStandardKind(), moduleKind.getPresentationKind(), xsdModule.getName(),
                    xsdModule.getPresentationName(), xsdModule.getFqName(), xsdModule.getDescription(), xsdModule.getLanguage());
            softwareSystem.addModule(module);
            globalXmlToElementMap.put(xsdModule, module);
            for (final XsdRootDirectory nextRoot : xsdModule.getRootDirectory())
            {
                RootDirectoryImpl rootDirectory = null;

                final XsdElementKind elementKind = (XsdElementKind) nextRoot.getKind();
                final String presentationKind = elementKind.getPresentationKind();
                final String standardKind = elementKind.getStandardKind();
                final String fqName = adjuster.adjustFqName(standardKind, nextRoot.getFqName());
                final String presentationName = adjuster.adjustPresentationName(standardKind, nextRoot.getPresentationName());

                switch (standardKind)
                {
                case "JavaClassRootDirectoryPath":
                    rootDirectory = createJavaClassRootDirectory(module, standardKind, presentationKind, presentationName, fqName);
                    break;
                case "JavaSourceRootDirectoryPath":
                    //$FALL-THROUGH$
                default:
                    rootDirectory = createRootDirectory(module, standardKind, presentationKind, presentationName, fqName);
                    break;
                }

                if (rootDirectory != null)
                {
                    module.addRootDirectory(rootDirectory);
                    globalXmlToElementMap.put(nextRoot, rootDirectory);

                    for (final XsdSourceFile nextSourceFile : nextRoot.getSourceElement())
                    {
                        final XsdElementKind sourceKind = (XsdElementKind) nextSourceFile.getKind();
                        final ISourceFile sourceFile = new SourceFileImpl(rootDirectory, sourceKind.getStandardKind(),
                                sourceKind.getPresentationKind(), nextSourceFile.getName(), nextSourceFile.getPresentationName(),
                                nextSourceFile.getFqName());
                        globalXmlToElementMap.put(nextSourceFile, sourceFile);
                        xsdSourceFiles.add(nextSourceFile);
                    }
                }
            }
        }

        return xsdSourceFiles;
    }

    private SourceFileImpl getSourceFile(final XsdSourceFile nextSourceFile)
    {
        final Object source = globalXmlToElementMap.get(nextSourceFile);
        if (source == null)
        {
            LOGGER.error("Source file '{}' must have been created before.", nextSourceFile.getFqName());
            return null;
        }
        else if (!(source instanceof SourceFileImpl))
        {
            LOGGER.error("Unexpected class '{}'", source.getClass().getCanonicalName());
            return null;
        }
        return (SourceFileImpl) source;
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

            final List<IIssue> issues = new ArrayList<>();
            for (final Object nextXsdIssue : nextResolution.getIssueIds())
            {
                final XsdIssue xsdIssue = (XsdIssue) nextXsdIssue;
                assert globalXmlIdToIssueMap.containsKey(xsdIssue) : "No issue with id '" + xsdIssue.getId() + "' exists";
                issues.add(globalXmlIdToIssueMap.get(xsdIssue));
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
        if (report.getSystemElements() == null)
        {
            return;
        }
        for (final XsdNamedElement nextElement : report.getSystemElements().getElement())
        {
            final XsdElementKind elementKind = (XsdElementKind) nextElement.getKind();
            final NamedElementImpl element = createNamedElement(adjuster, nextElement, elementKind);
            softwareSystem.addElement(element);
            globalXmlToElementMap.put(nextElement, element);
        }
    }

    private void processModuleElements(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report,
            final INamedElementAdjuster adjuster)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'addModuleElements' must not be null";
        assert report != null : "Parameter 'report' of method 'addModuleElements' must not be null";

        for (final XsdModuleElements nextModuleElements : report.getModuleElements())
        {
            final IElement moduleElement = globalXmlToElementMap.get(nextModuleElements.getRef());
            assert moduleElement != null : "Module does not exist";
            final ModuleImpl module = (ModuleImpl) moduleElement;

            for (final XsdNamedElement nextElement : nextModuleElements.getElement())
            {
                final XsdElementKind elementKind = (XsdElementKind) nextElement.getKind();
                final NamedElementImpl element = createNamedElement(adjuster, nextElement, elementKind);
                module.addElement(element);
                assert !globalXmlToElementMap.containsKey(nextElement) : "element already contained: " + nextElement.getFqName();
                globalXmlToElementMap.put(nextElement, element);
            }
        }
    }

    private NamedElementImpl createNamedElement(final INamedElementAdjuster adjuster, final XsdNamedElement nextElement,
            final XsdElementKind elementKind)
    {
        final String standardKind = elementKind.getStandardKind();

        final String name = adjuster.adjustName(standardKind, nextElement.getName());
        final String presentationName = adjuster.adjustPresentationName(standardKind, nextElement.getPresentationName());
        final String fqName = adjuster.adjustFqName(standardKind, nextElement.getFqName());
        final NamedElementImpl element = new NamedElementImpl(standardKind, elementKind.getPresentationKind(), name, presentationName, fqName,
                nextElement.getLine());
        return element;
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
            final IDependencyIssue dependencyIssue = new DependencyIssueImpl(issueType, nextDependencyIssue.getDescription(), issueProvider,
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

                final CycleGroupImpl cycleGroup = new CycleGroupImpl(nextCycle.getFqName(), issueProvider.getPresentationName(),
                        nextCycle.getDescription(), issueType, issueProvider, nextCycle.isHasResolution(), analyzer);

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
            final IThresholdViolationIssue issue = new ThresholdViolationIssue(issueType, next.getDescription() != null ? next.getDescription() : "",
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

    private static RootDirectoryImpl createRootDirectory(final ModuleImpl module, final String standardKind, final String presentationKind,
            final String presentationName, final String fqName)
    {
        assert module != null : "Parameter 'module' of method 'createRootDirectory' must not be null";
        assert standardKind != null && standardKind.length() > 0 : "Parameter 'standardKind' of method 'createRootDirectory' must not be empty";
        assert presentationKind != null && presentationKind.length() > 0 : "Parameter 'presentationKind' of method 'createRootDirectory' must not be empty";
        assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'createJavaClassRootDirectory' must not be empty";
        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'createJavaClassRootDirectory' must not be empty";

        return new SourceRootDirectoryImpl(module, standardKind, presentationKind, presentationName, fqName);
    }

    private static ClassRootDirectory createJavaClassRootDirectory(final ModuleImpl module, final String standardKind, final String presentationKind,
            final String presentationName, final String fqName)
    {
        assert module != null : "Parameter 'module' of method 'createJavaClassRootDirectory' must not be null";
        assert standardKind != null && standardKind.length() > 0 : "Parameter 'standardKind' of method 'createRootDirectory' must not be empty";
        assert presentationKind != null && presentationKind.length() > 0 : "Parameter 'presentationKind' of method 'createRootDirectory' must not be empty";
        assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'createJavaClassRootDirectory' must not be empty";
        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'createJavaClassRootDirectory' must not be empty";

        return new ClassRootDirectory(module, standardKind, presentationKind, presentationName, fqName);
    }
}