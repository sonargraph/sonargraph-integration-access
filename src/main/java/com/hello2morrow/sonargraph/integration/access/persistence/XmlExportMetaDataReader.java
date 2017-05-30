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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.core.persistence.report.XsdExportMetaData;
import com.hello2morrow.sonargraph.core.persistence.report.XsdExportMetaDataRoot;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueCategory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueProvider;
import com.hello2morrow.sonargraph.core.persistence.report.XsdIssueType;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricCategory;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricId;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricLevel;
import com.hello2morrow.sonargraph.core.persistence.report.XsdMetricProvider;
import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.internal.BasicSoftwareSystemInfoImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueTypeImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricIdImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricLevelImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MetricProviderImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SingleExportMetaDataImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl.ValidationMessageCauses;

public final class XmlExportMetaDataReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlExportMetaDataReader.class);
    private static final String METADATA_SCHEMA = "com/hello2morrow/sonargraph/core/persistence/report/exportMetaData.xsd";
    private static final String METADATA_NAMESPACE = "com.hello2morrow.sonargraph.core.persistence.report";

    private JaxbAdapter<JAXBElement<XsdExportMetaDataRoot>> createJaxbAdapter() throws XmlProcessingException
    {
        final URL metricsXsd = getClass().getClassLoader().getResource(METADATA_SCHEMA);
        return new JaxbAdapter<>(METADATA_NAMESPACE, metricsXsd);
    }

    public Optional<SingleExportMetaDataImpl> readMetaDataFromStream(final InputStream input, final String identifier, final OperationResult result)
    {
        assert input != null : "Parameter 'input' of method 'readMetaDataFile' must not be null";
        assert identifier != null && identifier.length() > 0 : "Parameter 'identifier' of method 'readMetaDataFromStream' must not be empty";
        assert result != null : "Parameter 'result' of method 'readMetaDataFile' must not be null";

        //TODO: Check for version in xml file

        JaxbAdapter<JAXBElement<XsdExportMetaDataRoot>> jaxbAdapter;
        try
        {
            jaxbAdapter = createJaxbAdapter();
        }
        catch (final Exception e)
        {
            result.addError(IOMessageCause.READ_ERROR, "Failed to initialize JAXB", e);
            return Optional.empty();
        }

        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);

        Optional<JAXBElement<XsdExportMetaDataRoot>> xmlRoot = Optional.empty();
        try (BufferedInputStream in = new BufferedInputStream(input))
        {
            xmlRoot = jaxbAdapter.load(in, eventHandler);
            if (xmlRoot.isPresent())
            {
                final XsdExportMetaDataRoot xsdMetricsRoot = xmlRoot.get().getValue();
                return convertXmlMetaDataToPojo(xsdMetricsRoot, identifier, result);
            }
        }
        catch (final Exception ex)
        {
            result.addError(IOMessageCause.IO_EXCEPTION, ex);
        }
        finally
        {
            if (result.isFailure() || !xmlRoot.isPresent())
            {
                result.addError(IOMessageCause.WRONG_FORMAT,
                        "Report is corrupt, please ensure that versions of SonargraphBuild and Sonargraph SonarQube Plugin are compatible");
            }
        }

        return Optional.empty();

    }

    private static Optional<SingleExportMetaDataImpl> convertXmlMetaDataToPojo(final XsdExportMetaDataRoot xsdMetaData, final String identifier,
            final OperationResult result)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetrics' of method 'convertXmlMetricsToPojo' must not be null";
        assert identifier != null && identifier.length() > 0 : "Parameter 'identifier' of method 'convertXmlMetricsToPojo' must not be empty";
        assert result != null : "Parameter 'result' of method 'convertXmlMetaDataToPojo' must not be null";

        final SingleExportMetaDataImpl metaData = new SingleExportMetaDataImpl(new BasicSoftwareSystemInfoImpl(
                xsdMetaData.getSystemPathUsedForExport(), xsdMetaData.getSystemId(), xsdMetaData.getVersion(), xsdMetaData.getTimestamp()
                        .toGregorianCalendar().getTimeInMillis()), identifier);

        final Map<Object, IssueCategoryImpl> issueCategoryXsdToPojoMap = XmlExportMetaDataReader.processIssueCategories(xsdMetaData);
        for (final IssueCategoryImpl category : issueCategoryXsdToPojoMap.values())
        {
            metaData.addIssueCategory(category);
        }

        final Map<Object, IssueProviderImpl> issueProviderXsdToPojoMap = XmlExportMetaDataReader.processIssueProviders(xsdMetaData);
        for (final IssueProviderImpl provider : issueProviderXsdToPojoMap.values())
        {
            metaData.addIssueProvider(provider);
        }

        final Map<Object, IssueTypeImpl> issueTypeXsdToPojoMap = XmlExportMetaDataReader.processIssueTypes(xsdMetaData, issueCategoryXsdToPojoMap,
                issueProviderXsdToPojoMap, result);
        for (final IssueTypeImpl issueType : issueTypeXsdToPojoMap.values())
        {
            metaData.addIssueType(issueType);
        }

        final Map<Object, MetricCategoryImpl> categoryXsdToPojoMap = XmlExportMetaDataReader.processMetricCategories(xsdMetaData);
        for (final MetricCategoryImpl category : categoryXsdToPojoMap.values())
        {
            metaData.addMetricCategory(category);
        }

        final Map<Object, MetricProviderImpl> providerXsdToPojoMap = XmlExportMetaDataReader.processProviders(xsdMetaData);
        for (final MetricProviderImpl provider : providerXsdToPojoMap.values())
        {
            metaData.addMetricProvider(provider);
        }

        final Map<Object, MetricLevelImpl> metricLevelXsdToPojoMap = XmlExportMetaDataReader.processMetricLevels(xsdMetaData);
        for (final MetricLevelImpl level : metricLevelXsdToPojoMap.values())
        {
            metaData.addMetricLevel(level);
        }

        for (final MetricIdImpl id : XmlExportMetaDataReader.processMetricIds(xsdMetaData, categoryXsdToPojoMap, providerXsdToPojoMap,
                metricLevelXsdToPojoMap).values())
        {
            metaData.addMetricId(id);
        }
        return Optional.of(metaData);
    }

    static Map<Object, MetricCategoryImpl> processMetricCategories(final XsdExportMetaData xsdMetaData)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processMetricCategories' must not be null";

        final Map<Object, MetricCategoryImpl> categoryXsdToPojoMap = new LinkedHashMap<>();

        for (final XsdMetricCategory xsdCategory : xsdMetaData.getMetricCategories().getCategory())
        {
            final MetricCategoryImpl category = new MetricCategoryImpl(xsdCategory.getName(), xsdCategory.getPresentationName(),
                    xsdCategory.getOrderNumber());
            categoryXsdToPojoMap.put(xsdCategory, category);
        }
        return Collections.unmodifiableMap(categoryXsdToPojoMap);
    }

    static Map<Object, MetricProviderImpl> processProviders(final XsdExportMetaData xsdMetaData)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processProviders' must not be null";

        final Map<Object, MetricProviderImpl> providerXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdMetricProvider xsdProvider : xsdMetaData.getMetricProviders().getProvider())
        {
            final MetricProviderImpl provider = new MetricProviderImpl(xsdProvider.getName(), xsdProvider.getPresentationName());
            providerXsdToPojoMap.put(xsdProvider, provider);
        }
        return Collections.unmodifiableMap(providerXsdToPojoMap);
    }

    static Map<Object, MetricLevelImpl> processMetricLevels(final XsdExportMetaData xsdExportMetaData)
    {
        assert xsdExportMetaData != null : "Parameter 'xsdMetaData' of method 'processMetricLevels' must not be null";

        final Map<XsdMetricLevel, MetricLevelImpl> metricLevelXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdMetricLevel xsdLevel : xsdExportMetaData.getMetricLevels().getLevel())
        {
            final MetricLevelImpl level = new MetricLevelImpl(xsdLevel.getName(), xsdLevel.getPresentationName(), xsdLevel.getOrderNumber());
            metricLevelXsdToPojoMap.put(xsdLevel, level);
        }

        return Collections.unmodifiableMap(metricLevelXsdToPojoMap);
    }

    static Map<Object, MetricIdImpl> processMetricIds(final XsdExportMetaData xsdMetaData,
            final Map<Object, MetricCategoryImpl> categoryXsdToPojoMap, final Map<Object, MetricProviderImpl> providerXsdToPojoMap,
            final Map<Object, MetricLevelImpl> metricLevelXsdToPojoMap)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processMetricIds' must not be null";
        assert categoryXsdToPojoMap != null : "Parameter 'categoryXsdToPojoMap' of method 'processMetricIds' must not be null";
        assert providerXsdToPojoMap != null : "Parameter 'providerXsdToPojoMap' of method 'processMetricIds' must not be null";
        assert metricLevelXsdToPojoMap != null : "Parameter 'metricLevelXsdToPojoMap' of method 'processMetricIds' must not be null";

        final Map<Object, MetricIdImpl> metricIdXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdMetricId xsdMetricId : xsdMetaData.getMetricIds().getMetricId())
        {
            final List<IMetricCategory> categories = new ArrayList<>();
            for (final Object xsdCategory : xsdMetricId.getCategories())
            {
                final IMetricCategory category = categoryXsdToPojoMap.get(xsdCategory);
                assert category != null : "'category' for metric '" + xsdMetricId.getName() + "' must not be null";
                categories.add(category);
            }

            final List<IMetricLevel> metricLevels = new ArrayList<>();
            for (final Object xsdMetricLevel : xsdMetricId.getLevels())
            {
                final MetricLevelImpl metricLevel = metricLevelXsdToPojoMap.get(xsdMetricLevel);
                assert metricLevel != null : "Metric level for '" + xsdMetricId.getName() + "' must not be null";
                metricLevels.add(metricLevel);
            }

            final IMetricProvider provider = providerXsdToPojoMap.get(xsdMetricId.getProvider());
            assert provider != null : "'provider' for metric '" + xsdMetricId.getName() + "' must not be null";

            metricIdXsdToPojoMap.put(xsdMetricId,
                    new MetricIdImpl(xsdMetricId.getName(), xsdMetricId.getPresentationName(), xsdMetricId.getDescription(), categories,
                            metricLevels, provider, xsdMetricId.isIsFloat(), xsdMetricId.getBestValue(), xsdMetricId.getWorstValue()));
        }
        return Collections.unmodifiableMap(metricIdXsdToPojoMap);
    }

    static Map<Object, IssueCategoryImpl> processIssueCategories(final XsdExportMetaData xsdMetaData)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processIssueCategories' must not be null";

        final Map<Object, IssueCategoryImpl> issueCategoryXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdIssueCategory next : xsdMetaData.getIssueCategories().getCategory())
        {
            final IssueCategoryImpl category = new IssueCategoryImpl(next.getName(), next.getPresentationName());
            issueCategoryXsdToPojoMap.put(next, category);
        }
        return Collections.unmodifiableMap(issueCategoryXsdToPojoMap);
    }

    private static Map<Object, IssueProviderImpl> processIssueProviders(final XsdExportMetaData xsdMetaData)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processIssueProviders' must not be null";

        final Map<Object, IssueProviderImpl> issueProviderXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdIssueProvider next : xsdMetaData.getIssueProviders().getIssueProvider())
        {
            final IssueProviderImpl provider = new IssueProviderImpl(next.getName(), next.getPresentationName());
            issueProviderXsdToPojoMap.put(next, provider);
        }
        return Collections.unmodifiableMap(issueProviderXsdToPojoMap);
    }

    private static Map<Object, IssueTypeImpl> processIssueTypes(final XsdExportMetaData xsdMetaData, final Map<Object, IssueCategoryImpl> categories,
            final Map<Object, IssueProviderImpl> providers, final OperationResult result)
    {
        assert xsdMetaData != null : "Parameter 'xsdMetaData' of method 'processIssueTypes' must not be null";
        assert categories != null && !categories.isEmpty() : "Parameter 'categories' of method 'processIssueTypes' must not be empty";
        assert providers != null : "Parameter 'providers' of method 'processIssueTypes' must not be null";
        assert result != null : "Parameter 'result' of method 'processIssueTypes' must not be null";

        final Map<Object, IssueTypeImpl> issueTypeXsdToPojoMap = new LinkedHashMap<>();
        for (final XsdIssueType next : xsdMetaData.getIssueTypes().getIssueType())
        {
            final IIssueCategory category = categories.get(next.getCategory());
            assert category != null : "Category '" + next.getCategory().toString() + "' has not been processed";

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

            final IssueTypeImpl issueType = new IssueTypeImpl(next.getName(), next.getPresentationName(), severity, category, providers.get(next
                    .getProvider()), next.getDescription());
            issueTypeXsdToPojoMap.put(next, issueType);
        }
        return Collections.unmodifiableMap(issueTypeXsdToPojoMap);
    }
}
