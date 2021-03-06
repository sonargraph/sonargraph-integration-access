/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.MigrationCheck;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultCause;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.AnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.model.ElementPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IProgrammingElementContainer;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.ISystemFile;
import com.hello2morrow.sonargraph.integration.access.model.PluginExecutionPhase;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;
import com.hello2morrow.sonargraph.integration.access.model.internal.AbstractFilterImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.AnalyzerConfigurationImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.AnalyzerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.CycleGroupIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DependencyIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DuplicateCodeBlockOccurrenceImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ElementPatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ExcludePatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ExternalImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.FeatureImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IncludePatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueFilterImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueTypeImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LanguageBasedContainerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.LineBasedIssueImpl;
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
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementIssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PhysicalElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PhysicalRecursiveElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PluginConfigurationImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PluginExternalImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.PluginImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ProductionCodeFilterImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ProgrammingElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.RootDirectoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SourceFileImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SystemFileImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.WorkspaceFileFilterImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.QualityGateReader.InvalidMetricIdException;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl.ValidationMessageCauses;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdAnalyzer;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdAnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdArchitectureCheckConfiguration;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdBooleanEntry;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleGroupAnalyzerConfiguration;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleGroupContainer;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCycleIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDuplicateBlockIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDuplicateCodeConfiguration;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementPattern;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExecutionPhase;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternal;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternalModuleScopeElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExternalSystemScopeElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFilter;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFloatEntry;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIntEntry;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLineBasedIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalNamespace;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdLogicalProgrammingElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMap;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMapEntry;
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
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPhysicalElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPhysicalRecursiveElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPlugin;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPluginConfiguration;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPluginExternal;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdPlugins;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdProgrammingElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdQualityGate;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRootDirectory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdScriptRunnerConfiguration;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSimpleElementIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSourceFile;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdStringEntry;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemElements;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemFile;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemFileElement;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSystemMetricValues;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdWildcardPattern;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdWorkspace;

public final class XmlReportReader extends XmlAccess
{
    static final Logger LOGGER = LoggerFactory.getLogger(XmlReportReader.class);
    private static final String SOFTWARE_SYSTEM_STANDARD_KIND = "SoftwareSystem";
    private static final String MODULE_STANDARD_KIND_SUFFIX = "Module";
    private static final String EXTERNAL_STANDARD_KIND_SUFFIX = "External";
    private final Map<Object, IElement> globalXmlToElementMap = new HashMap<>();
    private final Map<Object, IssueImpl> globalXmlIdToIssueMap = new HashMap<>();

    //For old reports, we need to track the usage of deprecatedIssueTypes and map them to the new ones.
    private final Map<String, IssueTypeImpl> deprecatedIssueTypeNameToIssueType = new HashMap<>();
    private final Map<String, IssueTypeImpl> deprecatedIssueTypeNameToNewIssueType = new HashMap<>();

    /**
     * Reads an XML report.
     *
     * @param reportStream Stream of the XML file that is expected to exist and be readable.
     * @param baseDir
     * @param result Contains info about errors.
     */
    public Optional<SoftwareSystemImpl> readReportFile(final InputStream reportStream, final File baseDir, final Result result)
    {
        assert reportStream != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";

        reset();

        JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter;
        try
        {
            jaxbAdapter = createReportJaxbAdapter();
        }
        catch (final Exception e)
        {
            result.addError(ResultCause.READ_ERROR, "Failed to initialize JAXB", e);
            return Optional.empty();
        }

        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);
        JAXBElement<XsdSoftwareSystemReport> xmlRoot = null;
        try (BufferedInputStream in = new BufferedInputStream(reportStream))
        {
            xmlRoot = jaxbAdapter.load(in, eventHandler);
            if (xmlRoot != null)
            {
                final String baseDirectoryPath = baseDir != null ? Utility.convertPathToUniversalForm(baseDir.getCanonicalPath()) : null;
                return convertXmlReportToPojo(xmlRoot.getValue(), baseDirectoryPath, result);
            }
        }
        catch (final Throwable ex)
        {
            LOGGER.error("Failed to read report from stream", ex);
            result.addError(ResultCause.READ_ERROR, ex);
        }
        finally
        {
            if (result.isFailure() || xmlRoot == null)
            {
                result.addError(ResultCause.WRONG_FORMAT,
                        "Report is corrupt. Ensure that the version of Sonargraph/SonargraphBuild used to create the report is compatible with the version of this client.");
            }
        }

        return Optional.empty();
    }

    private void reset()
    {
        globalXmlIdToIssueMap.clear();
        globalXmlToElementMap.clear();
        deprecatedIssueTypeNameToIssueType.clear();
        deprecatedIssueTypeNameToNewIssueType.clear();
    }

    /**
     * Reads an XML report.
     *
     * @param reportFile XML file that is expected to exist and be readable.
     * @param baseDir
     * @param result Contains info about errors.
     */
    public Optional<SoftwareSystemImpl> readReportFile(final File reportFile, final File baseDir, final Result result)
    {
        assert reportFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert reportFile.exists() : "Parameter 'reportFile' of method 'readReportFile' must be an existing file";
        assert reportFile.canRead() : "Parameter 'reportFile' of method 'readReportFile' must be a file with read access";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";

        reset();

        JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter;
        try
        {
            jaxbAdapter = createReportJaxbAdapter();
        }
        catch (final Exception e)
        {
            result.addError(ResultCause.READ_ERROR, "Failed to initialize JAXB", e);
            return Optional.empty();
        }

        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);
        JAXBElement<XsdSoftwareSystemReport> xmlRoot = null;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(reportFile)))
        {
            xmlRoot = jaxbAdapter.load(in, eventHandler);
            if (xmlRoot != null)
            {
                final String baseDirectoryPath = baseDir != null ? Utility.convertPathToUniversalForm(baseDir.getCanonicalPath()) : null;
                return convertXmlReportToPojo(xmlRoot.getValue(), baseDirectoryPath, result);
            }
        }
        catch (final Throwable ex)
        {
            LOGGER.error("Failed to read report from '" + reportFile.getAbsolutePath() + "'", ex);
            result.addError(ResultCause.READ_ERROR, ex);
        }
        finally
        {
            if (result.isFailure() || xmlRoot == null)
            {
                result.addError(ResultCause.WRONG_FORMAT,
                        "Report is corrupt. Ensure that the version of Sonargraph/SonargraphBuild used to create the report is compatible with the version of this client.");
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T extends NamedElementImpl> T getNamedElementImpl(final XsdNamedElement xsdNamedElement, final Class<T> clazz)
    {
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'getNamedElementImpl' must not be null";
        assert clazz != null : "Parameter 'clazz' of method 'getNamedElementImpl' must not be null";

        final Object namedElementImpl = globalXmlToElementMap.get(xsdNamedElement);
        if (namedElementImpl == null)
        {
            LOGGER.error("Named element '{}' must have been created before.", xsdNamedElement.getFqName());
            return null;
        }
        else if (!clazz.isAssignableFrom(namedElementImpl.getClass()))
        {
            LOGGER.error("Expected class '{}' but was '{}'", clazz.getName(), namedElementImpl.getClass().getName());
            return null;
        }
        return (T) namedElementImpl;
    }

    private void connectPhysicalElementOriginal(final XsdPhysicalElement xsdPhysicalElement)
    {
        assert xsdPhysicalElement != null : "Parameter 'xsdPhysicalElement' of method 'connectPhysicalElementOriginal' must not be null";
        final PhysicalElementImpl nextPhysicalElementImpl = getNamedElementImpl(xsdPhysicalElement, PhysicalElementImpl.class);
        if (nextPhysicalElementImpl == null)
        {
            LOGGER.error("No named element impl found for xsd named element '{}'.", xsdPhysicalElement.getFqName());
            return;
        }

        final Object nextXsdOriginal = xsdPhysicalElement.getOriginalLocation();
        if (nextXsdOriginal != null)
        {
            if (nextXsdOriginal instanceof XsdPhysicalElement)
            {
                final PhysicalElementImpl original = getNamedElementImpl((XsdNamedElement) nextXsdOriginal, PhysicalElementImpl.class);
                if (original != null)
                {
                    if (nextPhysicalElementImpl.getClass().equals(original.getClass()))
                    {
                        nextPhysicalElementImpl.setOriginalLocation(original);
                    }
                    else
                    {
                        LOGGER.error("Class info does not match for original connection '{}' vs '{}'.", nextPhysicalElementImpl.getClass().getName(),
                                original.getClass().getName());
                    }
                }
                else
                {
                    LOGGER.error("No named element impl found for original xsd named element '{}'.",
                            ((XsdPhysicalElement) nextXsdOriginal).getFqName());
                }
            }
            else
            {
                LOGGER.error("Unexpected class '{}' as original named element impl.", nextXsdOriginal.getClass().getCanonicalName());
            }
        }
    }

    private Optional<SoftwareSystemImpl> convertXmlReportToPojo(final XsdSoftwareSystemReport xsdReport, final String basePath, final Result result)
    {
        assert xsdReport != null : "Parameter 'xsdReport' of method 'convertXmlReportToPojo' must not be null";
        assert result != null : "Parameter 'result' of method 'convertXmlReportToPojo' must not be null";

        String description = xsdReport.getDescription();
        if (description == null)
        {
            description = xsdReport.getSystemDescription() != null ? xsdReport.getSystemDescription().trim() : "";
        }

        final SoftwareSystemImpl softwareSystemImpl;
        if (basePath == null)
        {
            softwareSystemImpl = new SoftwareSystemImpl("SoftwareSystem", "System", xsdReport.getSystemId(), xsdReport.getName(), description,
                    xsdReport.getSystemPath(), xsdReport.getVersion(), xsdReport.getTimestamp().toGregorianCalendar().getTimeInMillis(),
                    xsdReport.getCurrentVirtualModel(), convertXmlExecutionLevel(xsdReport.getAnalyzerExecutionLevel()));
        }
        else
        {
            softwareSystemImpl = new SoftwareSystemImpl("SoftwareSystem", "System", xsdReport.getSystemId(), xsdReport.getName(), description,
                    xsdReport.getSystemPath(), basePath, xsdReport.getVersion(), xsdReport.getTimestamp().toGregorianCalendar().getTimeInMillis(),
                    xsdReport.getCurrentVirtualModel(), convertXmlExecutionLevel(xsdReport.getAnalyzerExecutionLevel()));
        }
        softwareSystemImpl.addElement(softwareSystemImpl);
        softwareSystemImpl.setContextInfo(xsdReport.getReportContextInfo() != null ? xsdReport.getReportContextInfo() : "");
        processSystemMetaData(softwareSystemImpl, xsdReport);
        processAnalyzers(softwareSystemImpl, xsdReport);
        processPlugins(softwareSystemImpl, xsdReport);
        processFeatures(softwareSystemImpl, xsdReport);
        processDuplicateCodeConfiguration(softwareSystemImpl, xsdReport);
        processScriptRunnerConfiguration(softwareSystemImpl, xsdReport);
        processArchitectureCheckConfiguration(softwareSystemImpl, xsdReport);
        processCycleGroupAnalyzerConfigurations(softwareSystemImpl, xsdReport);
        processPluginConfigurations(softwareSystemImpl, xsdReport);
        processCheckedArchitectureFiles(softwareSystemImpl, xsdReport);
        processActiveQualityGates(softwareSystemImpl, xsdReport);

        final XsdWorkspace xsdWorkspace = xsdReport.getWorkspace();
        createWorkspaceElements(softwareSystemImpl, xsdWorkspace, result);
        if (result.isFailure())
        {
            return Optional.empty();
        }

        finishWorkspaceElementCreation(xsdWorkspace);

        createSystemElements(softwareSystemImpl, xsdReport);
        createModuleElements(xsdReport);
        createExternalScopeElements(xsdReport);

        connectSourceFiles(softwareSystemImpl);

        processMetrics(softwareSystemImpl, xsdReport);
        processMetricThresholds(softwareSystemImpl, xsdReport);

        processIssues(softwareSystemImpl, xsdReport, result);
        final ResolutionConverter resolutionConverter = new ResolutionConverter(softwareSystemImpl, xsdReport, globalXmlIdToIssueMap);
        resolutionConverter.convert(result);

        globalXmlToElementMap.clear();
        globalXmlIdToIssueMap.clear();

        return Optional.of(softwareSystemImpl);
    }

    private void processSystemMetaData(final SoftwareSystemImpl softwareSystemImpl, final XsdSoftwareSystemReport xsdReport)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'processSystemMetaData' must not be null";
        assert xsdReport != null : "Parameter 'xsdReport' of method 'processSystemMetaData' must not be null";

        final XsdMap xsdMetaData = xsdReport.getSystemMetaData();
        if (xsdMetaData == null)
        {
            return;
        }

        for (final XsdMapEntry next : xsdMetaData.getEntry())
        {
            final String previous = softwareSystemImpl.addMetaData(next.getKey(), next.getValue());
            assert previous == null : "Unexpected duplicate entry for metadata key '" + next.getKey() + "'. previous value: " + previous
                    + ", current value: " + next.getValue();
        }
    }

    private void connectSourceFiles(final SoftwareSystemImpl softwareSystemImpl)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'connectSourceFiles' must not be null";

        for (final Map.Entry<Object, IElement> nextEntry : globalXmlToElementMap.entrySet())
        {
            final Object nextKey = nextEntry.getKey();
            if (nextKey instanceof XsdNamedElement)
            {
                final XsdNamedElement nextXsdNamedElement = (XsdNamedElement) nextKey;
                final Object nextSource = nextXsdNamedElement.getSource();
                if (nextSource != null)
                {
                    final IElement nextSourceFile = globalXmlToElementMap.get(nextSource);
                    if (nextSourceFile != null)
                    {
                        assert nextSourceFile instanceof SourceFileImpl : "Unexpected class '" + nextSourceFile.getClass().getName() + "'";
                        assert nextEntry.getValue() instanceof NamedElementImpl : "Unexpected class '" + nextEntry.getValue().getClass().getName()
                                + "'";
                        softwareSystemImpl.addSourceFile((NamedElementImpl) nextEntry.getValue(), (SourceFileImpl) nextSourceFile);
                    }
                    else
                    {
                        LOGGER.warn("No element created for 'XsdNamedElement.getSource()' reference: " + nextSource);
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
                nextXsdRootDirectory.getSourceElement().forEach(s -> connectPhysicalElementOriginal(s));
                nextXsdRootDirectory.getPhysicalRecursiveElement().forEach(p -> connectPhysicalElementOriginal(p));
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
                xsdNamedElement.getName(), xsdNamedElement.getPresentationName(), xsdNamedElement.getFqName(),
                extractImageResourceName(xsdElementKind));
        namedElementContainerImpl.addElement(namedElementImpl);
        if (xsdNamedElement.getOriginalFqName() != null)
        {
            namedElementImpl.setOriginalFqName(xsdNamedElement.getOriginalFqName());
        }
        globalXmlToElementMap.put(xsdNamedElement, namedElementImpl);
    }

    private void createSourceFileImpl(final SoftwareSystemImpl softwareSystemImpl, final NamedElementContainerImpl namedElementContainerImpl,
            final RootDirectoryImpl rootDirectoryImpl, final XsdSourceFile xsdSourceFile)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'createSourceFileImpl' must not be null";
        assert rootDirectoryImpl != null : "Parameter 'rootDirectoryImpl' of method 'createSourceFileImpl' must not be null";
        assert xsdSourceFile != null : "Parameter 'xsdSourceFile' of method 'createSourceFileImpl' must not be null";

        final XsdElementKind sourceKind = (XsdElementKind) xsdSourceFile.getKind();
        final SourceFileImpl sourceFileImpl = new SourceFileImpl(sourceKind.getStandardKind(), sourceKind.getPresentationKind(),
                xsdSourceFile.getName(), xsdSourceFile.getPresentationName(), xsdSourceFile.getFqName(), xsdSourceFile.isLocationOnly(),
                rootDirectoryImpl.getRelativePath(), extractImageResourceName(sourceKind));
        if (xsdSourceFile.getOriginalFqName() != null)
        {
            sourceFileImpl.setOriginalFqName(xsdSourceFile.getOriginalFqName());
        }
        rootDirectoryImpl.addSourceFile(sourceFileImpl);
        softwareSystemImpl.addSourceFile(sourceFileImpl, sourceFileImpl);
        namedElementContainerImpl.addElement(sourceFileImpl);
        globalXmlToElementMap.put(xsdSourceFile, sourceFileImpl);
    }

    private void createProgrammingElementImpl(final NamedElementContainerImpl namedElementContainerImpl,
            final IProgrammingElementContainer programmingElementContainer, final XsdProgrammingElement xsdProgrammingElement)
    {
        assert programmingElementContainer != null : "Parameter 'programmingElementContainer' of method 'createProgrammingElementImpl' must not be null";
        assert xsdProgrammingElement != null : "Parameter 'xsdProgrammingElement' of method 'createProgrammingElementImpl' must not be null";

        final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(xsdProgrammingElement);
        final ProgrammingElementImpl programmingElementImpl = new ProgrammingElementImpl(xsdElementKind.getStandardKind(),
                xsdElementKind.getPresentationKind(), xsdProgrammingElement.getName(), xsdProgrammingElement.getPresentationName(),
                xsdProgrammingElement.getFqName(), xsdProgrammingElement.getLine(), extractImageResourceName(xsdElementKind));
        programmingElementContainer.addProgrammingElement(programmingElementImpl);
        namedElementContainerImpl.addElement(programmingElementImpl);

        if (xsdProgrammingElement.getOriginalFqName() != null)
        {
            programmingElementImpl.setOriginalFqName(xsdProgrammingElement.getOriginalFqName());
        }
        globalXmlToElementMap.put(xsdProgrammingElement, programmingElementImpl);
    }

    private void createPhysicalRecursiveElementImpl(final SoftwareSystemImpl softwareSystemImpl,
            final NamedElementContainerImpl namedElementContainerImpl, final IProgrammingElementContainer programmingElementContainer,
            final XsdPhysicalRecursiveElement xsdPhysicalRecursiveElement, final String relativeRootDirectory)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'createPhysicalRecursiveElementImpl' must not be null";
        assert namedElementContainerImpl != null : "Parameter 'namedElementContainerImpl' of method 'createPhysicalRecursiveElementImpl' must not be null";
        assert programmingElementContainer != null : "Parameter 'programmingElementContainer' of method 'createPhysicalRecursiveElementImpl' must not be null";
        assert xsdPhysicalRecursiveElement != null : "Parameter 'xsdPhysicalRecursiveElement' of method 'createPhysicalRecursiveElementImpl' must not be null";
        //'relativeRootDirectory' might be 'null';

        final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(xsdPhysicalRecursiveElement);
        final PhysicalRecursiveElementImpl physicalRecursiveElementImpl = new PhysicalRecursiveElementImpl(xsdElementKind.getStandardKind(),
                xsdElementKind.getPresentationKind(), xsdPhysicalRecursiveElement.getName(), xsdPhysicalRecursiveElement.getPresentationName(),
                xsdPhysicalRecursiveElement.getFqName(), xsdPhysicalRecursiveElement.isLocationOnly(), extractImageResourceName(xsdElementKind));

        final String nextRelativeDirectory = xsdPhysicalRecursiveElement.getRelativeDirectoryPath();
        if (nextRelativeDirectory != null)
        {
            physicalRecursiveElementImpl.setRelativeDirectory(nextRelativeDirectory);
        }
        if (relativeRootDirectory != null)
        {
            physicalRecursiveElementImpl.setRelativeRootDirectory(relativeRootDirectory);
        }
        globalXmlToElementMap.put(xsdPhysicalRecursiveElement, physicalRecursiveElementImpl);
        programmingElementContainer.addPhysicalRecursiveElement(physicalRecursiveElementImpl);
        namedElementContainerImpl.addElement(physicalRecursiveElementImpl);
    }

    private void createRootDirectoryImpl(final SoftwareSystemImpl softwareSystemImpl, final LanguageBasedContainerImpl languageBasedContainerImpl,
            final XsdRootDirectory xsdRootDirectory)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'createRootDirectoryImpl' must not be null";
        assert languageBasedContainerImpl != null : "Parameter 'languageBasedContainerImpl' of method 'createRootDirectoryImpl' must not be null";
        assert xsdRootDirectory != null : "Parameter 'xsdRootDirectory' of method 'createRootDirectoryImpl' must not be null";

        final XsdElementKind xsdElementKind = (XsdElementKind) xsdRootDirectory.getKind();
        final RootDirectoryImpl rootDirectoryImpl = new RootDirectoryImpl(xsdElementKind.getStandardKind(), xsdElementKind.getPresentationKind(),
                xsdRootDirectory.getPresentationName(), xsdRootDirectory.getFqName(), extractImageResourceName(xsdElementKind));
        languageBasedContainerImpl.addRootDirectory(rootDirectoryImpl);
        languageBasedContainerImpl.addElement(rootDirectoryImpl);
        globalXmlToElementMap.put(xsdRootDirectory, rootDirectoryImpl);

        xsdRootDirectory.getPhysicalRecursiveElement().forEach(e -> createPhysicalRecursiveElementImpl(softwareSystemImpl, languageBasedContainerImpl,
                rootDirectoryImpl, e, rootDirectoryImpl.getRelativePath()));
        xsdRootDirectory.getSourceElement().forEach(s -> createSourceFileImpl(softwareSystemImpl, languageBasedContainerImpl, rootDirectoryImpl, s));
        xsdRootDirectory.getProgrammingElement().forEach(p -> createProgrammingElementImpl(languageBasedContainerImpl, rootDirectoryImpl, p));
    }

    private void createWorkspaceElements(final SoftwareSystemImpl softwareSystemImpl, final XsdWorkspace xsdWorkspace, final Result result)
    {
        assert softwareSystemImpl != null : "Parameter 'softwareSystemImpl' of method 'createWorkspaceElements' must not be null";
        assert xsdWorkspace != null : "Parameter 'report' of method 'createWorkspaceElements' must not be null";
        assert result != null : "Parameter 'result' of method 'createWorkspaceElements' must not be null";

        final XsdFilter xsdWorkspaceFilter = xsdWorkspace.getWorkspaceFilter();
        if (xsdWorkspaceFilter != null)
        {
            final WorkspaceFileFilterImpl fileFilter = new WorkspaceFileFilterImpl(xsdWorkspaceFilter.getDescription(),
                    xsdWorkspaceFilter.getInformation(), xsdWorkspaceFilter.getNumberOfExcludedElements());
            addPatternsToFilter(xsdWorkspaceFilter, fileFilter);
            softwareSystemImpl.setWorkspaceFileFilter(fileFilter);
        }

        final XsdFilter xsdProductionCodeFilter = xsdWorkspace.getProductionCodeFilter();
        if (xsdProductionCodeFilter != null)
        {
            final ProductionCodeFilterImpl productionCodeFilter = new ProductionCodeFilterImpl(xsdProductionCodeFilter.getDescription(),
                    xsdProductionCodeFilter.getInformation(), xsdProductionCodeFilter.getNumberOfIncludedElements(),
                    xsdProductionCodeFilter.getNumberOfExcludedElements());
            addPatternsToFilter(xsdProductionCodeFilter, productionCodeFilter);
            softwareSystemImpl.setProductionCodeFilter(productionCodeFilter);
        }

        final XsdFilter xsdIssueFilter = xsdWorkspace.getIssueFilter();
        if (xsdIssueFilter != null)
        {
            final IssueFilterImpl issueFilter = new IssueFilterImpl(xsdIssueFilter.getDescription(), xsdIssueFilter.getInformation(),
                    xsdIssueFilter.getNumberOfIncludedElements(), xsdIssueFilter.getNumberOfExcludedElements());
            addPatternsToFilter(xsdIssueFilter, issueFilter);
            softwareSystemImpl.setIssueFilter(issueFilter);
        }

        for (final XsdModule nextXsdModule : xsdWorkspace.getModule())
        {
            final XsdElementKind moduleKind = XmlReportReaderUtils.getXsdElementKind(nextXsdModule);
            final ModuleImpl nextModuleImpl = new ModuleImpl(moduleKind.getStandardKind(), moduleKind.getPresentationKind(), nextXsdModule.getName(),
                    nextXsdModule.getPresentationName(), nextXsdModule.getFqName(), nextXsdModule.getDescription(), nextXsdModule.getModuleId(),
                    softwareSystemImpl.getMetaDataAccess(), softwareSystemImpl.getElementRegistry(), nextXsdModule.getLanguage(), softwareSystemImpl);
            softwareSystemImpl.addModule(nextModuleImpl);
            nextModuleImpl.addElement(nextModuleImpl);
            globalXmlToElementMap.put(nextXsdModule, nextModuleImpl);

            nextXsdModule.getRootDirectory().forEach(r -> createRootDirectoryImpl(softwareSystemImpl, nextModuleImpl, r));
        }

        for (final XsdExternal nextXsdExternal : xsdWorkspace.getExternal())
        {
            final XsdElementKind nextExternalKind = (XsdElementKind) nextXsdExternal.getKind();
            final ExternalImpl nextExternalImpl = new ExternalImpl(nextExternalKind.getStandardKind(), nextExternalKind.getPresentationKind(),
                    nextXsdExternal.getName(), nextXsdExternal.getPresentationName(), nextXsdExternal.getFqName(), nextXsdExternal.getDescription(),
                    softwareSystemImpl.getMetaDataAccess(), softwareSystemImpl.getElementRegistry(), nextXsdExternal.getLanguage());
            softwareSystemImpl.addExternal(nextExternalImpl);
            nextExternalImpl.addElement(nextExternalImpl);
            globalXmlToElementMap.put(nextXsdExternal, nextExternalImpl);

            nextXsdExternal.getRootDirectory().forEach(r -> createRootDirectoryImpl(softwareSystemImpl, nextExternalImpl, r));
            nextXsdExternal.getPhysicalRecursiveElement()
                    .forEach(e -> createPhysicalRecursiveElementImpl(softwareSystemImpl, nextExternalImpl, nextExternalImpl, e, null));
            nextXsdExternal.getProgrammingElement().forEach(e -> createProgrammingElementImpl(nextExternalImpl, nextExternalImpl, e));
        }

        for (final XsdPluginExternal nextXsdPluginExternal : xsdWorkspace.getPluginExternal())
        {
            final XsdElementKind xsdPluginKind = (XsdElementKind) nextXsdPluginExternal.getKind();
            final PluginExternalImpl nextPluginExternalImpl = new PluginExternalImpl(xsdPluginKind.getStandardKind(),
                    xsdPluginKind.getPresentationKind(), nextXsdPluginExternal.getName(), nextXsdPluginExternal.getPresentationName(),
                    nextXsdPluginExternal.getFqName(), nextXsdPluginExternal.getDescription(), softwareSystemImpl.getMetaDataAccess(),
                    softwareSystemImpl.getElementRegistry());
            softwareSystemImpl.addPluginExternal(nextPluginExternalImpl);
            nextPluginExternalImpl.addElement(nextPluginExternalImpl);
            globalXmlToElementMap.put(nextXsdPluginExternal, nextPluginExternalImpl);

            nextXsdPluginExternal.getPhysicalRecursiveElement()
                    .forEach(e -> createPhysicalRecursiveElementImpl(softwareSystemImpl, nextPluginExternalImpl, nextPluginExternalImpl, e, null));
            nextXsdPluginExternal.getProgrammingElement()
                    .forEach(e -> createProgrammingElementImpl(nextPluginExternalImpl, nextPluginExternalImpl, e));
        }
    }

    private void addPatternsToFilter(final XsdFilter xsdFilter, final AbstractFilterImpl filter)
    {
        assert xsdFilter != null : "Parameter 'xsdFilter' of method 'addPatternsToFilter' must not be null";
        assert filter != null : "Parameter 'filter' of method 'addPatternsToFilter' must not be null";

        for (final XsdWildcardPattern nextXsdPattern : xsdFilter.getExclude())
        {
            final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(nextXsdPattern);
            final ExcludePatternImpl pattern = new ExcludePatternImpl(nextXsdPattern.getFqName(), xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdPattern.getName(), nextXsdPattern.getPresentationName(),
                    nextXsdPattern.getNumberOfMatches());
            filter.addExcludePattern(pattern);
        }
        for (final XsdWildcardPattern nextXsdPattern : xsdFilter.getInclude())
        {
            final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(nextXsdPattern);
            final IncludePatternImpl pattern = new IncludePatternImpl(nextXsdPattern.getFqName(), xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdPattern.getName(), nextXsdPattern.getPresentationName(),
                    nextXsdPattern.getNumberOfMatches());
            filter.addIncludePattern(pattern);
        }
    }

    private void processAnalyzers(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processAnalyzers' must not be null";
        assert report != null : "Parameter 'report' of method 'processAnalyzers' must not be null";

        for (final XsdAnalyzer nextXsdAnalyzer : report.getAnalyzers().getAnalyzer())
        {
            final IAnalyzer analyzer = new AnalyzerImpl(nextXsdAnalyzer.getName(), nextXsdAnalyzer.getPresentationName(),
                    nextXsdAnalyzer.getDescription(), nextXsdAnalyzer.isLicensed(), convertXmlExecutionLevel(nextXsdAnalyzer.getExecutionLevel()),
                    nextXsdAnalyzer.isExecuted());
            softwareSystem.addAnalyzer(analyzer);
            globalXmlToElementMap.put(nextXsdAnalyzer, analyzer);
        }
    }

    private static AnalyzerExecutionLevel convertXmlExecutionLevel(final XsdAnalyzerExecutionLevel xsdExecutionLevel)
    {
        assert xsdExecutionLevel != null : "Parameter 'executionLevel' of method 'convertXmlExecutionLevel' must not be null";

        switch (xsdExecutionLevel)
        {
        case FULL:
            return AnalyzerExecutionLevel.FULL;
        case ADVANCED:
            return AnalyzerExecutionLevel.ADVANCED;
        case BASIC:
            return AnalyzerExecutionLevel.BASIC;
        case MINIMAL:
            return AnalyzerExecutionLevel.MINIMAL;
        default:
            assert false : "Unsupported analyzer execution level: " + xsdExecutionLevel.name();
        }
        return null;
    }

    private void processPlugins(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processPlugins' must not be null";
        assert report != null : "Parameter 'report' of method 'processPlugins' must not be null";
        final XsdPlugins xsdPlugins = report.getPlugins();
        if (xsdPlugins != null)
        {
            for (final XsdPlugin next : xsdPlugins.getPlugin())
            {
                final Set<PluginExecutionPhase> supportedExecutionPhases = convertXmlPluginExecutionPhases(next.getSupportedExecutionPhase());
                final Set<PluginExecutionPhase> activeExecutionPhases = convertXmlPluginExecutionPhases(next.getActiveExecutionPhase());
                final IPlugin plugin = new PluginImpl(next.getName(), next.getPresentationName(), next.getDescription(), next.getVendor(),
                        next.getVersion(), next.isLicensed(), next.isEnabled(), supportedExecutionPhases, activeExecutionPhases);
                softwareSystem.addPlugin(plugin);
                globalXmlToElementMap.put(next, plugin);
            }
        }
    }

    private static Set<PluginExecutionPhase> convertXmlPluginExecutionPhases(final List<XsdExecutionPhase> xsdExecutionPhases)
    {
        return xsdExecutionPhases.stream().map(xsd -> convertXmlPluginExecutionPhase(xsd)).collect(Collectors.toSet());
    }

    private static PluginExecutionPhase convertXmlPluginExecutionPhase(final XsdExecutionPhase xsdExecutionPhase)
    {
        switch (xsdExecutionPhase)
        {
        case ANALYZER:
            return PluginExecutionPhase.ANALYZER;
        case MODEL:
            return PluginExecutionPhase.MODEL;
        default:
            assert false : "Unsupported execution phase: " + xsdExecutionPhase.name();
            return null;
        }
    }

    private void processCheckedArchitectureFiles(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processCheckedArchitectureFiles' must not be null";
        assert report != null : "Parameter 'report' of method 'processCheckedArchitectureFiles' must not be null";

        for (final XsdSystemFile next : report.getSystemFile())
        {
            final SystemFileType type = SystemFileType.fromString(next.getType());
            if (type == SystemFileType.ARCHITECTURE)
            {
                final ISystemFile systemFile = new SystemFileImpl(next.getPath(), type,
                        next.getLastModified().toGregorianCalendar().getTimeInMillis(), next.getHash());
                softwareSystem.addSystemFile(systemFile);
            }
        }
    }

    private void processActiveQualityGates(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processActiveQualityGates' must not be null";
        assert report != null : "Parameter 'report' of method 'processActiveQualityGates' must not be null";

        final QualityGateReader reader = new QualityGateReader(softwareSystem, globalXmlToElementMap);
        for (final XsdSystemFileElement next : report.getSystemFileElement())
        {
            final SystemFileType type = SystemFileType.fromString(next.getType());
            if (next instanceof XsdQualityGate && type == SystemFileType.QUALITY_GATE)
            {
                assert next instanceof XsdQualityGate : "Unexpected class " + next.getClass().getCanonicalName();

                try
                {
                    reader.addQualityGate((XsdQualityGate) next);
                }
                catch (final InvalidMetricIdException e)
                {
                    throw new IllegalArgumentException("Failed to process quality gate '" + next.getPath() + "': " + e.getMessage());
                }
            }
        }
    }

    private static void processFeatures(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processFeatures' must not be null";
        assert report != null : "Parameter 'report' of method 'processFeatures' must not be null";
        report.getFeatures().getFeature()
                .forEach(f -> softwareSystem.addFeature(new FeatureImpl(f.getName(), f.getPresentationName(), f.isLicensed())));
    }

    private static void processDuplicateCodeConfiguration(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processDuplicateCodeConfiguration' must not be null";
        assert report != null : "Parameter 'report' of method 'processDuplicateCodeConfiguration' must not be null";

        final XsdDuplicateCodeConfiguration duplicateCodeConfiguration = report.getDuplicateCodeConfiguration();
        if (duplicateCodeConfiguration != null)
        {
            duplicateCodeConfiguration.getEntry().forEach(e -> softwareSystem.addDuplicateCodeConfigurationEntry(e));
        }
    }

    private static void processScriptRunnerConfiguration(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processScriptRunnerConfiguration' must not be null";
        assert report != null : "Parameter 'report' of method 'processScriptRunnerConfiguration' must not be null";

        final XsdScriptRunnerConfiguration scriptRunnerConfiguration = report.getScriptRunnerConfiguration();
        if (scriptRunnerConfiguration != null)
        {
            scriptRunnerConfiguration.getEntry().forEach(e -> softwareSystem.addScriptRunnerConfigurationEntry(e));
        }
    }

    private static void processArchitectureCheckConfiguration(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processArchitectureCheckConfiguration' must not be null";
        assert report != null : "Parameter 'report' of method 'processArchitectureCheckConfiguration' must not be null";

        final XsdArchitectureCheckConfiguration architectureCheckConfiguration = report.getArchitectureCheckConfiguration();
        if (architectureCheckConfiguration != null)
        {
            architectureCheckConfiguration.getEntry().forEach(e -> softwareSystem.addArchitectureCheckConfigurationEntry(e));
        }
    }

    private void processCycleGroupAnalyzerConfigurations(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processCycleAnalyzerConfigurations' must not be null";
        assert report != null : "Parameter 'report' of method 'processCycleAnalyzerConfigurations' must not be null";

        for (final XsdCycleGroupAnalyzerConfiguration nextXsdCycleGroupConfiguration : report.getCycleGroupAnalyzerConfiguration())
        {
            final Object xsdAnalyzer = nextXsdCycleGroupConfiguration.getAnalyzer();
            final IElement analyzer = globalXmlToElementMap.get(xsdAnalyzer);
            assert analyzer != null : "Missing analyzer for configuration '" + nextXsdCycleGroupConfiguration.getName() + "'";
            assert analyzer instanceof IAnalyzer : "Wrong class " + analyzer.getClass().getCanonicalName();

            final AnalyzerConfigurationImpl configuration = new AnalyzerConfigurationImpl((IAnalyzer) analyzer);
            globalXmlToElementMap.put(nextXsdCycleGroupConfiguration, configuration);
            softwareSystem.addAnalyzerConfiguration(configuration);

            for (final XsdIntEntry nextXsdIntEntry : nextXsdCycleGroupConfiguration.getIntEntry())
            {
                configuration.addIntConfigurationValue(nextXsdIntEntry.getName(), nextXsdIntEntry.getValue().intValue());
            }
            for (final XsdStringEntry nextXsdStringEntry : nextXsdCycleGroupConfiguration.getStringEntry())
            {
                configuration.addStringConfigurationValue(nextXsdStringEntry.getName(), nextXsdStringEntry.getValue());
            }
        }
    }

    private void processPluginConfigurations(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processPluginConfigurations' must not be null";
        assert report != null : "Parameter 'report' of method 'processPluginConfigurations' must not be null";

        for (final XsdPluginConfiguration nextXsdPluginConfiguration : report.getPluginConfiguration())
        {
            final Object xsdPluginObject = nextXsdPluginConfiguration.getPlugin();
            final Object pluginObject = globalXmlToElementMap.get(xsdPluginObject);
            assert pluginObject != null : "Missing plugin for configuration: " + nextXsdPluginConfiguration.getId();
            final IPlugin plugin = (IPlugin) pluginObject;
            final PluginConfigurationImpl pluginConfiguration = new PluginConfigurationImpl(nextXsdPluginConfiguration.getName(),
                    nextXsdPluginConfiguration.getPresentationName(), plugin);
            softwareSystem.addPluginConfiguration(pluginConfiguration);

            for (final XsdStringEntry nextXsdStringEntry : nextXsdPluginConfiguration.getStringEntry())
            {
                pluginConfiguration.addStringConfigurationValue(nextXsdStringEntry.getName(), nextXsdStringEntry.getValue());
            }

            for (final XsdIntEntry nextXsdIntEntry : nextXsdPluginConfiguration.getIntEntry())
            {
                pluginConfiguration.addIntConfigurationValue(nextXsdIntEntry.getName(), nextXsdIntEntry.getValue().intValue());
            }

            for (final XsdFloatEntry nextXsdFloatEntry : nextXsdPluginConfiguration.getFloatEntry())
            {
                pluginConfiguration.addFloatConfigurationValue(nextXsdFloatEntry.getName(), nextXsdFloatEntry.getValue().floatValue());
            }

            for (final XsdBooleanEntry nextXsdBooleanEntry : nextXsdPluginConfiguration.getBooleanEntry())
            {
                pluginConfiguration.addBooleanConfigurationValue(nextXsdBooleanEntry.getName(), nextXsdBooleanEntry.isValue().booleanValue());
            }
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

            final String xsdSeverity = next.getSeverity();
            Severity severity;
            if (xsdSeverity != null)
            {
                severity = Severity.fromString(xsdSeverity);
            }
            else
            {
                severity = Severity.WARNING;
            }

            final MetricThreshold threshold = new MetricThreshold(metricId, metricLevel, next.getLowerThreshold(), next.getUpperThreshold(),
                    severity);
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
            final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(nextXsdLogicalNamespace);
            final LogicalNamespaceImpl logicalNamespaceImpl = new LogicalNamespaceImpl(xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdLogicalNamespace.getName(), nextXsdLogicalNamespace.getPresentationName(),
                    nextXsdLogicalNamespace.getFqName(), extractImageResourceName(xsdElementKind));
            globalXmlToElementMap.put(nextXsdLogicalNamespace, logicalNamespaceImpl);
            namedElementContainerImpl.addElement(logicalNamespaceImpl);
            namedElementContainerImpl.addLogicalNamespace(logicalNamespaceImpl);
            setDerivedFrom(nextXsdLogicalNamespace, logicalNamespaceImpl);
        }
        for (final XsdLogicalProgrammingElement nextXsdLogicalProgrammingElement : xsdElements.getLogicalProgrammingElement())
        {
            final XsdElementKind xsdElementKind = XmlReportReaderUtils.getXsdElementKind(nextXsdLogicalProgrammingElement);
            final LogicalProgrammingElementImpl logicalProgrammingElementImpl = new LogicalProgrammingElementImpl(xsdElementKind.getStandardKind(),
                    xsdElementKind.getPresentationKind(), nextXsdLogicalProgrammingElement.getName(),
                    nextXsdLogicalProgrammingElement.getPresentationName(), nextXsdLogicalProgrammingElement.getFqName(),
                    extractImageResourceName(xsdElementKind));
            globalXmlToElementMap.put(nextXsdLogicalProgrammingElement, logicalProgrammingElementImpl);
            namedElementContainerImpl.addElement(logicalProgrammingElementImpl);
            namedElementContainerImpl.addLogicalProgrammingElement(logicalProgrammingElementImpl);
            setDerivedFrom(nextXsdLogicalProgrammingElement, logicalProgrammingElementImpl);
        }
    }

    private String extractImageResourceName(final XsdElementKind xsdElementKind)
    {
        return xsdElementKind.getStandardKind().equals(xsdElementKind.getImageResourceName()) ? null : xsdElementKind.getImageResourceName();
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
                final XsdElementKind elementKind = XmlReportReaderUtils.getXsdElementKind(nextElement);
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
                final XsdElementKind elementKind = XmlReportReaderUtils.getXsdElementKind(nextModuleElement);
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
            assert nextElement != null
                    && nextElement instanceof NamedElementContainerImpl : "Unexpected class in method 'createExternalScopeElements': " + nextElement;
            final NamedElementContainerImpl nextExternalContainerImpl = (NamedElementContainerImpl) nextElement;
            for (final XsdNamedElement nextNamedElement : nextXsdExternalSystemScopeElements.getElement())
            {
                final XsdElementKind elementKind = XmlReportReaderUtils.getXsdElementKind(nextNamedElement);
                if (!elementKind.getStandardKind().endsWith(EXTERNAL_STANDARD_KIND_SUFFIX))
                {
                    createNamedElementImpl(nextExternalContainerImpl, nextNamedElement, elementKind);
                }
            }
            createLogicalElements(nextExternalContainerImpl, nextXsdExternalSystemScopeElements);
        }
        for (final XsdExternalModuleScopeElements nextXsdExternalModuleScopeElements : report.getExternalModuleScopeElements())
        {
            final IElement nextElement = globalXmlToElementMap.get(nextXsdExternalModuleScopeElements.getRef());
            assert nextElement != null
                    && nextElement instanceof NamedElementContainerImpl : "Unexpected class in method 'createExternalScopeElements': " + nextElement;
            final NamedElementContainerImpl nextExternalContainerImpl = (NamedElementContainerImpl) nextElement;

            for (final XsdNamedElement nextNamedElement : nextXsdExternalModuleScopeElements.getElement())
            {
                final XsdElementKind elementKind = XmlReportReaderUtils.getXsdElementKind(nextNamedElement);
                if (!elementKind.getStandardKind().endsWith(EXTERNAL_STANDARD_KIND_SUFFIX))
                {
                    createNamedElementImpl(nextExternalContainerImpl, nextNamedElement, elementKind);
                }
            }
            createLogicalElements(nextExternalContainerImpl, nextXsdExternalModuleScopeElements);
        }
    }

    private void processMetrics(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport xsdReport)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'processMetrics' must not be null";
        assert xsdReport != null : "Parameter 'xsdReport' of method 'processMetrics' must not be null";

        final Map<Object, MetricCategoryImpl> categoryXsdToPojoMap = XmlExportMetaDataReader.processMetricCategories(xsdReport.getMetaData());
        categoryXsdToPojoMap.values().forEach(c -> softwareSystem.addMetricCategory(c));
        globalXmlToElementMap.putAll(categoryXsdToPojoMap);

        final Map<Object, MetricProviderImpl> providerXsdToPojoMap = XmlExportMetaDataReader.processProviders(xsdReport.getMetaData());
        providerXsdToPojoMap.values().forEach(p -> softwareSystem.addMetricProvider(p));
        globalXmlToElementMap.putAll(providerXsdToPojoMap);

        final Map<Object, MetricLevelImpl> metricLevelXsdToPojoMap = XmlExportMetaDataReader.processMetricLevels(xsdReport.getMetaData());
        metricLevelXsdToPojoMap.values().forEach(l -> softwareSystem.addMetricLevel(l));
        globalXmlToElementMap.putAll(metricLevelXsdToPojoMap);

        final Map<Object, MetricIdImpl> metricIdXsdToPojoMap = XmlExportMetaDataReader.processMetricIds(xsdReport.getMetaData(), categoryXsdToPojoMap,
                providerXsdToPojoMap, metricLevelXsdToPojoMap);
        metricIdXsdToPojoMap.values().forEach(i -> softwareSystem.addMetricId(i));
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
                        addMetricValue(softwareSystem, module, currentLevel, nextValue.getRef(), nextIntValue.getRef(),
                                () -> new Integer(nextIntValue.getValue()));
                    }
                    for (final XsdMetricFloatValue nextFloatValue : nextValue.getFloat())
                    {
                        addMetricValue(softwareSystem, module, currentLevel, nextValue.getRef(), nextFloatValue.getRef(),
                                () -> new Float(nextFloatValue.getValue()));
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
        assert element instanceof INamedElement : "Unexpected class in method 'addMetricValue': " + element;

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

    private void processIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report, final Result result)
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
        issueCategoryXsdToPojoMap.values().forEach(c -> softwareSystem.addIssueCategory(c));
        globalXmlToElementMap.putAll(issueCategoryXsdToPojoMap);

        processIssueTypes(softwareSystem, report, result);

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

    private void processIssueTypes(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report, final Result result)
    {
        final boolean migrationNeeded = MigrationCheck.isPreUnificationOfIssueIds(softwareSystem.getVersion());

        for (final XsdIssueType next : report.getMetaData().getIssueTypes().getIssueType())
        {
            final List<Severity> severities = new ArrayList<>();
            try
            {
                for (final String nextSeverityString : next.getSeverity().split(", "))
                {
                    severities.add(Severity.fromString(nextSeverityString));
                }
            }
            catch (final Exception e)
            {
                LOGGER.error("Failed to process severity type '" + next.getSeverity() + "'", e);
                result.addWarning(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT,
                        "Severity type '" + next.getSeverity() + "' is not supported, setting to '" + Severity.ERROR + "'");
                severities.add(Severity.ERROR);
            }

            if (migrationNeeded)
            {
                final IssueTypeImpl convertedIssueType = handledDeprecatedIssueTypes(next, severities);
                if (convertedIssueType != null)
                {
                    if (!softwareSystem.getIssueTypes().containsKey(convertedIssueType.getName()))
                    {
                        softwareSystem.addIssueType(convertedIssueType);
                    }
                    continue;
                }
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

            final IssueTypeImpl issueType = new IssueTypeImpl(next.getName(), next.getPresentationName(), severities, (IIssueCategory) namedElement,
                    (IIssueProvider) provider, next.getDescription());
            softwareSystem.addIssueType(issueType);
        }
    }

    private IssueTypeImpl handledDeprecatedIssueTypes(final XsdIssueType xsdIssueType, final List<Severity> severities)
    {
        assert xsdIssueType != null : "Parameter 'xsdIssueType' of method 'handledDeprecatedIssueTypes' must not be null";
        assert severities != null && !severities.isEmpty() : "Parameter 'severities' of method 'handledDeprecatedIssueTypes' must not be empty";

        if (severities.size() > 1)
        {
            //more than one severity indicates that this cannot be a deprecated issue type.
            return null;
        }

        final String thresholdErrorStandardName = "ThresholdViolationError";
        final String thresholdWarningStandardName = "ThresholdViolation";
        if (xsdIssueType.getName().equals(thresholdErrorStandardName) || xsdIssueType.getName().equals(thresholdWarningStandardName))
        {
            return createDeprecatedIssueType(xsdIssueType, thresholdWarningStandardName, "Threshold Violation", thresholdErrorStandardName,
                    "Threshold Violation (Error)");
        }

        final String moduleCycleWarningStandardName = "ModuleCycleGroup";
        final String moduleCycleErrorStandardName = "CriticalModuleCycleGroup";
        if (xsdIssueType.getName().equals(moduleCycleWarningStandardName) || xsdIssueType.getName().equals(moduleCycleErrorStandardName))
        {
            return createDeprecatedIssueType(xsdIssueType, moduleCycleWarningStandardName, "Module Cycle Group", moduleCycleErrorStandardName,
                    "Critical Module Cycle Group");
        }

        final String directoryCycleWarningStandardName = "DirectoryCycleGroup";
        final String directoryCycleErrorStandardName = "CriticalDirectoryCycleGroup";
        if (xsdIssueType.getName().equals(directoryCycleWarningStandardName) || xsdIssueType.getName().equals(directoryCycleErrorStandardName))
        {
            return createDeprecatedIssueType(xsdIssueType, directoryCycleWarningStandardName, "Directory Cycle Group",
                    directoryCycleErrorStandardName, "Critical Directory Cycle Group");
        }

        final String namespaceCycleWarningStandardName = "NamespaceCycleGroup";
        final String namespaceCycleErrorStandardName = "CriticalNamespaceCycleGroup";
        if (xsdIssueType.getName().equals(namespaceCycleWarningStandardName) || xsdIssueType.getName().equals(namespaceCycleErrorStandardName))
        {
            return createDeprecatedIssueType(xsdIssueType, namespaceCycleWarningStandardName, "Namespace Cycle Group",
                    namespaceCycleErrorStandardName, "Critical Namespace Cycle Group");
        }

        final String componentCycleWarningStandardName = "ComponentCycleGroup";
        final String componentCycleErrorStandardName = "CriticalComponentCycleGroup";
        if (xsdIssueType.getName().equals(componentCycleWarningStandardName) || xsdIssueType.getName().equals(componentCycleErrorStandardName))
        {
            return createDeprecatedIssueType(xsdIssueType, componentCycleWarningStandardName, "Component Cycle Group",
                    componentCycleErrorStandardName, "Critical Component Cycle Group");
        }

        return null;
    }

    private IssueTypeImpl createDeprecatedIssueType(final XsdIssueType xsdIssueType, final String warningStandardName,
            final String warningPresentationName, final String errorStandardName, final String errorPresentationName)
    {
        final IElement provider;
        if (xsdIssueType.getProvider() != null)
        {
            provider = globalXmlToElementMap.get(xsdIssueType.getProvider());
        }
        else
        {
            provider = null;
        }

        final IElement issueCategory = globalXmlToElementMap.get(xsdIssueType.getCategory());
        assert issueCategory != null && issueCategory instanceof IIssueCategory : "Unexpected class in method 'processIssues': " + issueCategory;

        if (!deprecatedIssueTypeNameToIssueType.containsKey(warningStandardName))
        {
            final IssueTypeImpl deprecatedIssueTypeWarning = new IssueTypeImpl(warningStandardName, warningPresentationName,
                    Collections.singletonList(Severity.WARNING), (IIssueCategory) issueCategory, (IIssueProvider) provider,
                    xsdIssueType.getDescription());
            deprecatedIssueTypeNameToIssueType.put(warningStandardName, deprecatedIssueTypeWarning);
        }

        if (!deprecatedIssueTypeNameToIssueType.containsKey(errorStandardName))
        {
            final IssueTypeImpl deprecatedIssueTypeError = new IssueTypeImpl(errorStandardName, errorPresentationName,
                    Collections.singletonList(Severity.ERROR), (IIssueCategory) issueCategory, (IIssueProvider) provider,
                    xsdIssueType.getDescription());
            deprecatedIssueTypeNameToIssueType.put(errorStandardName, deprecatedIssueTypeError);
        }

        IssueTypeImpl newIssueType = deprecatedIssueTypeNameToNewIssueType.get(warningStandardName);
        if (newIssueType == null)
        {
            newIssueType = deprecatedIssueTypeNameToNewIssueType.get(errorStandardName);
        }

        if (newIssueType == null)
        {
            newIssueType = new IssueTypeImpl(warningStandardName, warningPresentationName, Arrays.asList(Severity.ERROR, Severity.WARNING),
                    (IIssueCategory) issueCategory, (IIssueProvider) provider, xsdIssueType.getDescription());

            deprecatedIssueTypeNameToNewIssueType.put(warningStandardName, newIssueType);
            deprecatedIssueTypeNameToNewIssueType.put(errorStandardName, newIssueType);
        }
        return newIssueType;
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
            assert from instanceof INamedElement : "Unexpected class in method 'processDependencyIssues': " + from;

            final String toId = ((XsdElement) nextDependencyIssue.getTo()).getId();
            final IElement to = globalXmlToElementMap.get(nextDependencyIssue.getTo());
            assert to != null : "'to' element (" + toId + ") of dependency issue '" + nextDependencyIssue.getId() + "' not found";
            assert to instanceof INamedElement : "Unexpected class in method 'processDependencyIssues': " + to;

            final String nextName = issueType.getName();
            final String nextPresentationName = issueType.getPresentationName();
            final DependencyIssueImpl dependencyIssue = new DependencyIssueImpl(nextName, nextPresentationName, nextDependencyIssue.getDescription(),
                    issueType, issueProvider, nextDependencyIssue.getLine(), nextDependencyIssue.getColumn(), (INamedElement) from,
                    (INamedElement) to);
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
            for (final XsdDuplicateCodeBlockOccurrence nextOccurrence : nextDuplicate.getOccurrence())
            {
                final IElement element = globalXmlToElementMap.get(nextOccurrence.getSource());
                assert element instanceof ISourceFile : "Unexpected element class: " + element.getClass().getName();
                final Integer endLine = nextOccurrence.getEndLine();

                final int[] hash = HashExtractor.decodeHash(nextOccurrence.getHash());
                final IDuplicateCodeBlockOccurrence occurrence = new DuplicateCodeBlockOccurrenceImpl((ISourceFile) element,
                        nextOccurrence.getBlockSize(), nextOccurrence.getStartLine(), endLine.intValue(), nextOccurrence.getTolerance(), hash);
                occurrences.add(occurrence);
            }

            final DuplicateCodeBlockIssueImpl duplicate = new DuplicateCodeBlockIssueImpl(nextDuplicate.getFqName(), nextDuplicate.getName(),
                    nextDuplicate.getDescription(), issueType, issueProvider, occurrences, nextDuplicate.getDuplicateLineCount());
            duplicate.setBlockSize(nextDuplicate.getBlockSize());
            softwareSystem.addIssue(duplicate);

            globalXmlIdToIssueMap.put(nextDuplicate, duplicate);
        }
    }

    private void processCycleGroupIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        final boolean handleDeprecatedIssues = MigrationCheck.isPreUnificationOfIssueIds(softwareSystem.getVersion());

        for (final XsdCycleGroupContainer nextXsdCycleContainer : report.getIssues().getElementIssues().getCycleGroups())
        {
            final String analyzerName = ((XsdAnalyzer) nextXsdCycleContainer.getAnalyzerRef()).getName();
            final IAnalyzer analyzer = softwareSystem.getAnalyzers().get(analyzerName);
            assert analyzer != null : "Analyzer '" + analyzerName + "' does not exist!";

            for (final XsdCycleIssue nextXsdCycle : nextXsdCycleContainer.getCycleGroup())
            {
                final Severity severity;
                final IIssueType issueType;
                if (handleDeprecatedIssues)
                {
                    final IIssueType deprecatedIssueType = getDeprecatedIssueType(nextXsdCycle);
                    if (deprecatedIssueType != null)
                    {
                        severity = deprecatedIssueType.getSupportedSeverities().get(0);
                        issueType = getConvertedIssueType(nextXsdCycle);
                    }
                    else
                    {
                        issueType = getIssueType(softwareSystem, nextXsdCycle);
                        final String severityString = nextXsdCycle.getSeverity();
                        assert severityString != null : "Missing severity for cycle group issue: " + nextXsdCycle.getId();
                        severity = Severity.fromString(severityString);
                    }
                }
                else
                {
                    issueType = getIssueType(softwareSystem, nextXsdCycle);
                    final String severityString = nextXsdCycle.getSeverity();
                    assert severityString != null : "Missing severity for cycle group issue: " + nextXsdCycle.getId();
                    severity = Severity.fromString(severityString);
                }

                final IIssueProvider issueProvider = getIssueProvider(softwareSystem, nextXsdCycle);

                final List<INamedElement> cyclicElements = new ArrayList<>();
                for (final XsdCycleElement nextElement : nextXsdCycle.getElement())
                {
                    final IElement element = globalXmlToElementMap.get(nextElement.getRef());
                    assert element instanceof INamedElement : "Unexpected class " + element.getClass().getName();
                    cyclicElements.add((INamedElement) element);
                }

                final Object scopeObject = nextXsdCycle.getScope();
                final INamedElement scope;
                if (scopeObject != null)
                {
                    final IElement scopeElement = globalXmlToElementMap.get(scopeObject);
                    assert scopeElement != null && scopeElement instanceof INamedElement : "Unexpected class in method 'processCycleGroupIssues': "
                            + scopeElement;
                    scope = (INamedElement) scopeElement;
                }
                else
                {
                    scope = null;
                }

                final String name = nextXsdCycle.getName();
                //This name might not not be set -> use the old name 'issueProvider.getPresentationName()'
                final CycleGroupIssueImpl cycleGroup = new CycleGroupIssueImpl(nextXsdCycle.getFqName(),
                        name != null && !name.isEmpty() ? name : issueProvider.getPresentationName(), nextXsdCycle.getDescription(), issueType,
                        severity, issueProvider, analyzer, cyclicElements, nextXsdCycle.getStructuralDebtIndex(),
                        nextXsdCycle.getComponentDependenciesToRemove(), nextXsdCycle.getParserDependenciesToRemove(), scope);

                softwareSystem.addIssue(cycleGroup);
                globalXmlIdToIssueMap.put(nextXsdCycle, cycleGroup);
            }
        }
    }

    private void processThresholdIssues(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report)
    {
        assert softwareSystem != null : "Parameter 'softwareSystemImpl' of method 'processThresholdIssues' must not be null";
        assert report != null : "Parameter 'report' of method 'processThresholdIssues' must not be null";

        final boolean handleDeprecatedIssues = MigrationCheck.isPreUnificationOfIssueIds(softwareSystem.getVersion());

        for (final XsdMetricThresholdViolationIssue nextXsdThresholdViolation : report.getIssues().getElementIssues().getThresholdViolation())
        {
            final XsdElement nextAffectedXsdElement = (XsdElement) nextXsdThresholdViolation.getAffectedElement();
            final IElement nextAffectedElement = globalXmlToElementMap.get(nextAffectedXsdElement);
            assert nextAffectedElement != null : "Affected element of issue '" + nextXsdThresholdViolation
                    + "' has not been processed - xsd element id: " + nextAffectedXsdElement.getId();
            assert nextAffectedElement instanceof INamedElement : "Unexpected class in method 'processSimpleElementIssues': " + nextAffectedElement;

            final Severity severity;
            final IIssueType issueType;
            if (handleDeprecatedIssues)
            {
                final IIssueType deprecatedIssueType = getDeprecatedIssueType(nextXsdThresholdViolation);
                if (deprecatedIssueType != null)
                {
                    severity = deprecatedIssueType.getSupportedSeverities().get(0);
                    issueType = getConvertedIssueType(nextXsdThresholdViolation);
                }
                else
                {
                    issueType = getIssueType(softwareSystem, nextXsdThresholdViolation);
                    final String severityString = nextXsdThresholdViolation.getSeverity();
                    assert severityString != null : "Missing severity for threshold issue: " + nextXsdThresholdViolation.getId();
                    severity = Severity.fromString(severityString);
                }
            }
            else
            {
                issueType = getIssueType(softwareSystem, nextXsdThresholdViolation);
                final String severityString = nextXsdThresholdViolation.getSeverity();
                assert severityString != null : "Missing severity for threshold issue: " + nextXsdThresholdViolation.getId();
                severity = Severity.fromString(severityString);
            }

            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, nextXsdThresholdViolation);

            final IElement threshold = globalXmlToElementMap.get(nextXsdThresholdViolation.getThresholdRef());
            assert threshold != null : "threshold has not been added to system for '" + nextXsdThresholdViolation.getDescription() + "'";

            final IMetricThreshold metricThreshold = (IMetricThreshold) threshold;

            final ThresholdViolationIssue issue = new ThresholdViolationIssue(issueType.getName(), issueType.getPresentationName(),
                    nextXsdThresholdViolation.getDescription() != null ? nextXsdThresholdViolation.getDescription() : "", issueType, severity,
                    issueProvider, nextXsdThresholdViolation.getLine(), nextXsdThresholdViolation.getColumn(), (INamedElement) nextAffectedElement,
                    nextXsdThresholdViolation.getMetricValue(), metricThreshold);
            softwareSystem.addIssue(issue);
            globalXmlIdToIssueMap.put(nextXsdThresholdViolation, issue);
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
            assert affected instanceof INamedElement : "Unexpected class in method 'processSimpleElementIssues': " + affected;

            final IIssueType issueType = getIssueType(softwareSystem, next);
            Severity severity;
            if (issueType.getSupportedSeverities().size() == 1)
            {
                severity = issueType.getSupportedSeverities().get(0);
            }
            else
            {
                final String severityString = next.getSeverity();
                assert severityString != null : "Missing severity for " + next;
                severity = Severity.fromString(severityString);
            }

            final IIssueProvider issueProvider = getIssueProvider(softwareSystem, next);
            final String nextName = issueType.getName();
            final String nextPresentationName = issueType.getPresentationName();
            final NamedElementIssueImpl issue;
            final String description = next.getDescription() != null ? next.getDescription() : "";
            if (next instanceof XsdLineBasedIssue)
            {
                final XsdElementPattern xsdPattern = ((XsdLineBasedIssue) next).getPattern();
                final String hash = xsdPattern.getHash();
                final IElementPattern pattern = new ElementPatternImpl(ElementPatternType.FULLY_QUALIFIED_NAME, xsdPattern.getValue(), hash);
                final LineBasedIssueImpl.PatternInfo info = HashExtractor.extractLineBasedIssuePatternInfo(hash);
                if (info == null)
                {
                    LOGGER.error("Failed to extract info from hash '{}'", hash);
                    issue = null;
                }
                else
                {
                    issue = new LineBasedIssueImpl(nextName, nextPresentationName, description, issueType, severity, issueProvider, next.getLine(),
                            next.getColumn(), (INamedElement) affected, pattern, info.getLineText(), info.getPrefixHashs(), info.getPostfixHashs());
                }
            }
            else
            {
                issue = new NamedElementIssueImpl(nextName, nextPresentationName, description, issueType, severity, issueProvider, next.getLine(),
                        next.getColumn(), (INamedElement) affected);
            }
            if (issue != null)
            {
                softwareSystem.addIssue(issue);
                globalXmlIdToIssueMap.put(next, issue);
            }
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
        return issueType;
    }

    private IIssueType getDeprecatedIssueType(final XsdIssue xsdIssue)
    {
        final String issueTypeName = ((XsdIssueType) xsdIssue.getType()).getName();
        final IIssueType issueType = deprecatedIssueTypeNameToIssueType.get(issueTypeName);
        return issueType;
    }

    private IIssueType getConvertedIssueType(final XsdIssue xsdIssue)
    {
        final String issueTypeName = ((XsdIssueType) xsdIssue.getType()).getName();
        final IIssueType issueType = deprecatedIssueTypeNameToNewIssueType.get(issueTypeName);
        return issueType;
    }
}