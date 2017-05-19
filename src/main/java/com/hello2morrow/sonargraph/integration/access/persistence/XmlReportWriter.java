package com.hello2morrow.sonargraph.integration.access.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import com.hello2morrow.sonargraph.core.persistence.report.ObjectFactory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdExportMetaData;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueCategories;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueCategory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueProviders;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueTypes;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricCategories;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricCategory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricId;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricIds;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricLevel;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricLevels;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricProvider;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricProviders;
import com.hello2morrow.sonargraph.core.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;

public class XmlReportWriter extends AbstractXmlReportAccess
{
    public void writeReport(final SoftwareSystemImpl softwareSystem, final File reportFile) throws Exception
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'writeReport' must not be null";
        assert reportFile != null : "Parameter 'reportFile' of method 'writeReport' must not be null";

        final JAXBElement<XsdSoftwareSystemReport> reportXml = convertPojoToXml(softwareSystem);
        final JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> jaxbAdapter = createJaxbAdapter(false);
        jaxbAdapter.save(reportXml, reportFile);
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

        final XsdExportMetaData xmlMetaData = createXmlMetaData(softwareSystem, factory, idGenerator, elementToXmlMap);
        xmlSystem.setMetaData(xmlMetaData);

        return factory.createReport(xmlSystem);
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
        for (final IMetricLevel level : softwareSystem.getMetricLevels().values())
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
            assert xmlProvider != null : "No xml element has been created for provider " + metricId.getProvider().getName();
            xmlMetricId.setProvider(xmlProvider);
            elementToXmlMap.put(metricId, xmlMetricId);
            xmlMetricIds.getMetricId().add(xmlMetricId);
        }
        return xmlMetaData;
    }
}
