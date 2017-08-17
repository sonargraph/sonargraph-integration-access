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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.report.ObjectFactory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdAnalyzer;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdAnalyzers;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKinds;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExportMetaData;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFeature;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFeatures;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueCategories;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueCategory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueProviders;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueTypes;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricCategories;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricCategory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricId;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricIds;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricLevel;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricLevels;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricProvider;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricProviders;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdModule;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRootDirectory;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdWorkspace;

/**
 * NOTE: Currently only writes the meta-data and workspace info of a software system to file.
 * TODO: Write further info to disk (not top-prio, but as a matter of completeness).
 */
public final class XmlReportWriter extends XmlAccess
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportWriter.class);

    public XmlReportWriter()
    {
        super();
    }

    public void writeReport(final SoftwareSystemImpl softwareSystem, final File reportFile) throws Exception
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'writeReport' must not be null";
        assert reportFile != null : "Parameter 'reportFile' of method 'writeReport' must not be null";

        final JAXBElement<XsdSoftwareSystemReport> reportXml = convertPojoToXml(softwareSystem);
        final JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter = createReportJaxbAdapter();

        int i = 1;
        File tmpFile = reportFile;
        while (tmpFile.exists())
        {
            tmpFile = new File(reportFile.getAbsolutePath() + "." + i);
            ++i;
        }
        try (OutputStream out = new FileOutputStream(tmpFile))
        {
            jaxbAdapter.save(reportXml, out);
        }
        finally
        {
            if (tmpFile.exists() && !tmpFile.equals(reportFile))
            {
                Files.move(tmpFile.toPath(), reportFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static XMLGregorianCalendar createDateTimeObject(final long timeStamp)
    {
        final GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(timeStamp);
        XMLGregorianCalendar xgcal = null;
        try
        {
            xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        }
        catch (final DatatypeConfigurationException ex)
        {
            assert false : "Failed to create new instance of XMLGregorianCalendar!";
            LOGGER.error("Failed to create XMLGregorianCalendar: " + Utility.collectAll(ex));
        }
        return xgcal;
    }

    private JAXBElement<XsdSoftwareSystemReport> convertPojoToXml(final SoftwareSystemImpl softwareSystem)
    {
        final ObjectFactory factory = new ObjectFactory();
        final XmlIdGenerator idGenerator = new XmlIdGenerator();
        final Map<Object, Object> elementToXmlMap = new HashMap<>();

        final XsdSoftwareSystemReport xmlSystem = factory.createXsdSoftwareSystemReport();
        xmlSystem.setId(idGenerator.getNextId());
        xmlSystem.setSystemId(softwareSystem.getSystemId());
        xmlSystem.setName(softwareSystem.getName());
        xmlSystem.setSystemPath(softwareSystem.getPath());
        xmlSystem.setCurrentVirtualModel(softwareSystem.getVirtualModel());
        xmlSystem.setTimestamp(createDateTimeObject(System.currentTimeMillis()));
        xmlSystem.setVersion(softwareSystem.getVersion());
        elementToXmlMap.put(softwareSystem, xmlSystem);

        final XsdExportMetaData xmlMetaData = createXmlMetaData(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setMetaData(xmlMetaData);

        final XsdFeatures xmlFeatures = createXmlFeatures(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setFeatures(xmlFeatures);

        final XsdAnalyzers xmlAnalyzers = createXmlAnalyzers(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setAnalyzers(xmlAnalyzers);

        final XsdElementKinds xmlElementKinds = createXmlElementKinds(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setElementKinds(xmlElementKinds);

        final XsdWorkspace xmlWorkspace = createXmlWorkspace(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setWorkspace(xmlWorkspace);

        return factory.createReport(xmlSystem);
    }

    private XsdElementKinds createXmlElementKinds(final SoftwareSystemImpl softwareSystem, final ObjectFactory factory,
            final XmlIdGenerator idGenerator, final Map<Object, Object> elementToXmlMap)
    {
        final XsdElementKinds xmlElementKinds = factory.createXsdElementKinds();
        final Set<String> kinds = new HashSet<>(softwareSystem.getElementKinds());
        kinds.add("SoftwareSystem");
        for (final IModule module : softwareSystem.getModules().values())
        {
            kinds.addAll(module.getElementKinds());
            kinds.add(module.getKind());
        }
        for (final String kind : kinds)
        {
            final XsdElementKind xmlKind = factory.createXsdElementKind();
            xmlKind.setId(idGenerator.getNextId());
            xmlKind.setStandardKind(kind);
            xmlKind.setPresentationKind(Utility.convertMixedCaseStringToPresentationName(kind));
            elementToXmlMap.put(kind, xmlKind);
            xmlElementKinds.getElementKind().add(xmlKind);
        }
        return xmlElementKinds;
    }

    private XsdAnalyzers createXmlAnalyzers(final SoftwareSystemImpl softwareSystem, final ObjectFactory factory, final XmlIdGenerator idGenerator,
            final Map<Object, Object> elementToXmlMap)
    {
        final XsdAnalyzers xmlAnalyzers = factory.createXsdAnalyzers();

        for (final IAnalyzer analyzer : softwareSystem.getAnalyzers().values())
        {
            final XsdAnalyzer xmlAnalyzer = factory.createXsdAnalyzer();
            xmlAnalyzer.setId(idGenerator.getNextId());
            xmlAnalyzer.setName(analyzer.getName());
            xmlAnalyzer.setPresentationName(analyzer.getPresentationName());
            xmlAnalyzer.setDescription(analyzer.getDescription());
            xmlAnalyzer.setLicensed(analyzer.isLicensed());
            elementToXmlMap.put(analyzer, xmlAnalyzer);
            xmlAnalyzers.getAnalyzer().add(xmlAnalyzer);
        }
        return xmlAnalyzers;
    }

    private XsdFeatures createXmlFeatures(final SoftwareSystemImpl softwareSystem, final ObjectFactory factory, final XmlIdGenerator idGenerator,
            final Map<Object, Object> elementToXmlMap)
    {
        final XsdFeatures xmlFeatures = factory.createXsdFeatures();

        for (final IFeature feature : softwareSystem.getFeatures().values())
        {
            final XsdFeature xmlFeature = factory.createXsdFeature();
            xmlFeature.setLicensed(feature.isLicensed());
            xmlFeature.setName(feature.getName());
            xmlFeature.setPresentationName(feature.getPresentationName());
            xmlFeatures.getFeature().add(xmlFeature);
        }
        return xmlFeatures;
    }

    private XsdWorkspace createXmlWorkspace(final SoftwareSystemImpl softwareSystem, final ObjectFactory factory, final XmlIdGenerator idGenerator,
            final Map<Object, Object> elementToXmlMap)
    {
        final XsdWorkspace xmlWorkspace = factory.createXsdWorkspace();

        //FIXME
        //        final XsdRootDirectory xsdGenericRoot = factory.createXsdRootDirectory();
        //        xsdGenericRoot.setId(idGenerator.getNextId());
        //        xsdGenericRoot.setFqName(softwareSystem.getName());
        //        xsdGenericRoot.setPresentationName(softwareSystem.getName());
        //        xsdGenericRoot.setKind(elementToXmlMap.get(softwareSystem.getKind()));
        //        xmlWorkspace.getGenericRoot().add(xsdGenericRoot); //This does not seem to be referenced by anything else in the XML reports?

        for (final IModule module : softwareSystem.getModules().values())
        {
            final XsdModule xmlModule = factory.createXsdModule();
            xmlModule.setId(idGenerator.getNextId());
            xmlModule.setName(module.getName());
            xmlModule.setPresentationName(module.getPresentationName());
            xmlModule.setFqName(module.getFqName());
            xmlModule.setDescription(module.getDescription());
            xmlModule.setKind(elementToXmlMap.get(module.getKind()));
            xmlModule.setLanguage(module.getLanguage());
            elementToXmlMap.put(module, xmlModule);
            xmlWorkspace.getModule().add(xmlModule);

            for (final IRootDirectory root : module.getRootDirectories())
            {
                final XsdRootDirectory xmlRoot = factory.createXsdRootDirectory();
                xmlRoot.setId(idGenerator.getNextId());
                xmlRoot.setName(root.getName());
                xmlRoot.setPresentationName(root.getPresentationName());
                xmlRoot.setFqName(root.getFqName());
                xmlRoot.setKind(elementToXmlMap.get(root.getKind()));
                elementToXmlMap.put(root, xmlRoot);
                xmlModule.getRootDirectory().add(xmlRoot);
            }
        }
        //TODO External
        return xmlWorkspace;
    }

    private XsdExportMetaData createXmlMetaData(final SoftwareSystemImpl softwareSystem, final ObjectFactory factory,
            final XmlIdGenerator idGenerator, final Map<Object, Object> elementToXmlMap)
    {
        final XsdExportMetaData xmlMetaData = factory.createXsdExportMetaData();

        final XsdIssueProviders xmlIssueProviders = factory.createXsdIssueProviders();
        xmlMetaData.setIssueProviders(xmlIssueProviders);
        for (final IIssueProvider provider : softwareSystem.getIssueProviders().values())
        {
            final XsdIssueProvider xmlIssueProvider = factory.createXsdIssueProvider();
            xmlIssueProvider.setId(idGenerator.getNextId());
            xmlIssueProvider.setName(provider.getName());
            xmlIssueProvider.setPresentationName(provider.getPresentationName());
            elementToXmlMap.put(provider, xmlIssueProvider);
            xmlIssueProviders.getIssueProvider().add(xmlIssueProvider);
        }

        final XsdIssueCategories xmlIssueCategories = factory.createXsdIssueCategories();
        xmlMetaData.setIssueCategories(xmlIssueCategories);
        for (final IIssueCategory category : softwareSystem.getIssueCategories().values())
        {
            final XsdIssueCategory xmlIssueCategory = factory.createXsdIssueCategory();
            xmlIssueCategory.setId(idGenerator.getNextId());
            xmlIssueCategory.setName(category.getName());
            xmlIssueCategory.setPresentationName(category.getPresentationName());
            elementToXmlMap.put(category, xmlIssueCategory);
            xmlIssueCategories.getCategory().add(xmlIssueCategory);
        }

        final XsdIssueTypes xmlIssueTypes = factory.createXsdIssueTypes();
        xmlMetaData.setIssueTypes(xmlIssueTypes);
        for (final IIssueType type : softwareSystem.getIssueTypes().values())
        {
            final XsdIssueType xmlIssueType = factory.createXsdIssueType();
            xmlIssueType.setId(idGenerator.getNextId());
            xmlIssueType.setName(type.getName());
            xmlIssueType.setPresentationName(type.getPresentationName());
            xmlIssueType.setDescription(type.getDescription());
            xmlIssueType.setSeverity(type.getSeverity().name());

            final Object xmlCategory = elementToXmlMap.get(type.getCategory());
            assert xmlCategory != null : "category must have been created before";
            xmlIssueType.setCategory(xmlCategory);
            final IIssueProvider provider = type.getProvider();
            if (provider != null)
            {
                final Object xmlIssueProvider = elementToXmlMap.get(provider);
                assert xmlIssueProvider != null : "provider " + type.getProvider().getName() + " must have been created before";
                xmlIssueType.setProvider(xmlIssueProvider);
            }
            elementToXmlMap.put(type, xmlIssueType);
            xmlIssueTypes.getIssueType().add(xmlIssueType);
        }

        final XsdMetricProviders xmlMetricProviders = factory.createXsdMetricProviders();
        xmlMetaData.setMetricProviders(xmlMetricProviders);
        for (final IMetricProvider provider : softwareSystem.getMetricProviders().values())
        {
            final XsdMetricProvider xmlMetricProvider = factory.createXsdMetricProvider();
            xmlMetricProvider.setId(idGenerator.getNextId());
            xmlMetricProvider.setName(provider.getName());
            xmlMetricProvider.setPresentationName(provider.getPresentationName());
            elementToXmlMap.put(provider, xmlMetricProvider);
            xmlMetricProviders.getProvider().add(xmlMetricProvider);
        }

        final XsdMetricCategories xmlMetricCategories = factory.createXsdMetricCategories();
        xmlMetaData.setMetricCategories(xmlMetricCategories);
        for (final IMetricCategory category : softwareSystem.getMetricCategories().values())
        {
            final XsdMetricCategory xmlMetricCategory = factory.createXsdMetricCategory();
            xmlMetricCategory.setId(idGenerator.getNextId());
            xmlMetricCategory.setName(category.getName());
            xmlMetricCategory.setPresentationName(category.getPresentationName());
            xmlMetricCategory.setOrderNumber(category.getOrderNumber());
            elementToXmlMap.put(category, xmlMetricCategory);
            xmlMetricCategories.getCategory().add(xmlMetricCategory);
        }

        final XsdMetricLevels xmlMetricLevels = factory.createXsdMetricLevels();
        xmlMetaData.setMetricLevels(xmlMetricLevels);
        for (final IMetricLevel level : softwareSystem.getAllMetricLevels().values())
        {
            final XsdMetricLevel xmlLevel = factory.createXsdMetricLevel();
            xmlLevel.setId(idGenerator.getNextId());
            xmlLevel.setName(level.getName());
            xmlLevel.setPresentationName(level.getPresentationName());
            xmlLevel.setOrderNumber(level.getOrderNumber());
            elementToXmlMap.put(level, xmlLevel);
            xmlMetricLevels.getLevel().add(xmlLevel);
        }

        final XsdMetricIds xmlMetricIds = factory.createXsdMetricIds();
        xmlMetaData.setMetricIds(xmlMetricIds);
        for (final IMetricId metricId : softwareSystem.getMetricIds().values())
        {
            final XsdMetricId xmlMetricId = factory.createXsdMetricId();
            xmlMetricId.setId(idGenerator.getNextId());
            xmlMetricId.setName(metricId.getName());
            xmlMetricId.setPresentationName(metricId.getPresentationName());
            xmlMetricId.setDescription(metricId.getDescription());
            xmlMetricId.setIsFloat(metricId.isFloat());
            xmlMetricId.setBestValue(metricId.getBestValue());
            xmlMetricId.setWorstValue(metricId.getWorstValue());
            final Object xmlProvider = elementToXmlMap.get(metricId.getProvider());
            assert xmlProvider != null : "Xml element missing for metric provider " + metricId.getProvider().getName();
            xmlMetricId.setProvider(xmlProvider);

            for (final IMetricCategory category : metricId.getCategories())
            {
                final Object xmlCategory = elementToXmlMap.get(category);
                assert xmlCategory != null : "Xml element missing for metric category " + category.getName();
                xmlMetricId.getCategories().add(xmlCategory);
            }

            for (final IMetricLevel level : metricId.getLevels())
            {
                final Object xmlLevel = elementToXmlMap.get(level);
                assert xmlLevel != null : "Xml element missing for metric level " + level.getName();
                xmlMetricId.getLevels().add(xmlLevel);
            }

            elementToXmlMap.put(metricId, xmlMetricId);
            xmlMetricIds.getMetricId().add(xmlMetricId);
        }
        return xmlMetaData;
    }
}