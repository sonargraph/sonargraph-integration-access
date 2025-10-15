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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionMode;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.CurrentSystemConditionsImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.DiffAgainstBaselineConditionsImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateExcludeFilterImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateIssueConditionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateIssueDiffConditionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateMetricValueDiffConditionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateThresholdIssueConditionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate.QualityGateThresholdIssueDiffConditionImpl;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Check;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateExcludeFilter;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateIssueCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateIssueDiffCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateThresholdIssueCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Operator;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Status;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCheck;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdCurrentSystemConditions;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDiffAgainstBaselineConditions;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExcludeFilter;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueCondition;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssueDiffCondition;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMetricDiffCondition;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdOperator;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdQualityGate;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdQualityGateStatus;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdThresholdIssueCondition;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdThresholdIssueDiffCondition;

final class QualityGateReader
{
    public static class InvalidMetricIdException extends Exception
    {
        private static final long serialVersionUID = 736505155318129756L;

        public InvalidMetricIdException(final String message)
        {
            super(message);
        }
    }

    private static final String LIST_SEPARATOR = ",";
    private final SoftwareSystemImpl m_softwareSystem;
    private final Map<Object, IElement> m_globalXmlToElementMap;

    public QualityGateReader(final SoftwareSystemImpl softwareSystem, final Map<Object, IElement> globalXmlToElementMap)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'QualityGateReader' must not be null";
        assert globalXmlToElementMap != null : "Parameter 'globalXmlToElementMap' of method 'QualityGateReader' must not be null";

        m_softwareSystem = softwareSystem;
        m_globalXmlToElementMap = globalXmlToElementMap;
    }

    public void addQualityGate(final XsdQualityGate xsdQualityGate)
    {
        assert xsdQualityGate != null : "Parameter 'xsdQualityGate' of method 'addQualityGateDetails' must not be null";

        final XsdElementKind xsdKind = XmlReportReaderUtils.getXsdElementKind(xsdQualityGate);
        final SystemFileType type = SystemFileType.fromString(xsdQualityGate.getType());
        final QualityGateImpl qualityGate = new QualityGateImpl(xsdKind.getStandardKind(), xsdKind.getPresentationKind(), xsdQualityGate.getName(),
                xsdQualityGate.getPresentationName(), xsdQualityGate.getFqName(), xsdQualityGate.getDescription(), xsdKind.getImageResourceName(),
                xsdQualityGate.getPath(), type, xsdQualityGate.getLastModified().toGregorianCalendar().getTimeInMillis());
        m_softwareSystem.addSystemFileElement(qualityGate);
        m_globalXmlToElementMap.put(xsdQualityGate, qualityGate);

        processCurrenSystemConditions(xsdQualityGate, qualityGate);
        processBaselineConditions(xsdQualityGate, qualityGate);
    }

    private void processBaselineConditions(final XsdQualityGate xsdQualityGate, final QualityGateImpl qualityGate)
    {
        final XsdDiffAgainstBaselineConditions xsdBaselineConditions = xsdQualityGate.getBaselineConditions();
        final DiffAgainstBaselineConditionsImpl baselineConditions = (DiffAgainstBaselineConditionsImpl) qualityGate
                .getDiffAgainstBaselineConditions();
        m_globalXmlToElementMap.put(xsdBaselineConditions, baselineConditions);

        for (final XsdIssueDiffCondition xsdCondition : xsdBaselineConditions.getIssueDiffCondition())
        {
            final IQualityGateIssueDiffCondition diffCondition;
            final XsdElementKind xsdKind = XmlReportReaderUtils.getXsdElementKind(xsdCondition);
            if (xsdCondition instanceof XsdThresholdIssueDiffCondition)
            {
                final XsdThresholdIssueDiffCondition xsdThresholdCondition = (XsdThresholdIssueDiffCondition) xsdCondition;
                diffCondition = new QualityGateThresholdIssueDiffConditionImpl(xsdKind.getStandardKind(), xsdKind.getPresentationKind(),
                        xsdCondition.getFqName(), xsdCondition.getName(), xsdCondition.getPresentationName(), xsdKind.getImageResourceName(),
                        xsdCondition.getIssueType(), convertResolutions(xsdCondition.getResolution()), convertSeverities(xsdCondition.getSeverity()),
                        xsdCondition.getInfo(), xsdThresholdCondition.getMetricId(), xsdThresholdCondition.getDiffThreshold(),
                        xsdThresholdCondition.getDiffThresholdRelative(), convertOperator(xsdThresholdCondition.getOperator()),
                        convertCheck(xsdThresholdCondition.getCheck()), convertStatus(xsdCondition.getStatus()));
            }
            else
            {
                diffCondition = new QualityGateIssueDiffConditionImpl(xsdKind.getStandardKind(), xsdKind.getPresentationKind(),
                        xsdCondition.getFqName(), xsdCondition.getName(), xsdCondition.getPresentationName(), xsdKind.getImageResourceName(),
                        xsdCondition.getIssueType(), convertResolutions(xsdCondition.getResolution()), convertSeverities(xsdCondition.getSeverity()),
                        xsdCondition.getInfo(), convertCheck(xsdCondition.getCheck()), convertStatus(xsdCondition.getStatus()));

            }
            baselineConditions.addQualityGateCondition(diffCondition);
            m_globalXmlToElementMap.put(xsdCondition, diffCondition);
        }
        for (final XsdMetricDiffCondition xsdMetricCondition : xsdBaselineConditions.getMetricDiffCondition())
        {
            final XsdElementKind xsdKind = XmlReportReaderUtils.getXsdElementKind(xsdMetricCondition);
            final QualityGateMetricValueDiffConditionImpl metricCondition = new QualityGateMetricValueDiffConditionImpl(xsdKind.getStandardKind(),
                    xsdKind.getPresentationKind(), xsdMetricCondition.getName(), xsdMetricCondition.getPresentationName(),
                    xsdMetricCondition.getFqName(), xsdKind.getImageResourceName(), xsdMetricCondition.getMetricId(),
                    convertOperator(xsdMetricCondition.getOperator()), xsdMetricCondition.getDiffThreshold(),
                    xsdMetricCondition.getDiffThresholdRelative(), convertStatus(xsdMetricCondition.getStatus()));
            baselineConditions.addQualityGateCondition(metricCondition);
            m_globalXmlToElementMap.put(xsdMetricCondition, metricCondition);
        }

        for (final XsdExcludeFilter nextXsdExclude : xsdBaselineConditions.getExclude())
        {
            final String issueType = nextXsdExclude.getIssueType();
            final List<ResolutionMode> resolutions = convertResolutions(nextXsdExclude.getResolution());
            final List<Severity> severities = convertSeverities(nextXsdExclude.getSeverity());
            final XsdElementKind kind = XmlReportReaderUtils.getXsdElementKind(nextXsdExclude);
            final String metricId = nextXsdExclude.getMetricId();

            final IQualityGateExcludeFilter excludeFilter = new QualityGateExcludeFilterImpl(kind.getStandardKind(), kind.getPresentationKind(),
                    nextXsdExclude.getFqName(), nextXsdExclude.getName(), nextXsdExclude.getPresentationName(), kind.getImageResourceName(),
                    issueType, resolutions, severities, metricId, nextXsdExclude.getInfo());
            baselineConditions.addExcludeFilter(excludeFilter);
            m_globalXmlToElementMap.put(nextXsdExclude, excludeFilter);
        }
    }

    private Check convertCheck(final XsdCheck check)
    {
        assert check != null : "Parameter 'check' of method 'convertCheck' must not be null";
        switch (check)
        {
        case RELAXED:
            return Check.RELAXED;
        case STRICT:
            return Check.STRICT;
        default:
            assert false : "Unknown check value: " + check.name();
        }
        return null;
    }

    private void processCurrenSystemConditions(final XsdQualityGate xsdQualityGate, final QualityGateImpl qualityGate)
    {
        final XsdCurrentSystemConditions xsdCurrentSystemConditions = xsdQualityGate.getCurrentSystemConditions();
        final CurrentSystemConditionsImpl currentSystemConditions = (CurrentSystemConditionsImpl) qualityGate.getCurrentSystemConditions();
        m_globalXmlToElementMap.put(xsdCurrentSystemConditions, currentSystemConditions);

        for (final XsdIssueCondition nextXsdCondition : xsdCurrentSystemConditions.getIssueCondition())
        {
            final String issueType = nextXsdCondition.getIssueType();
            final List<ResolutionMode> resolutions = convertResolutions(nextXsdCondition.getResolution());
            final List<Severity> severities = convertSeverities(nextXsdCondition.getSeverity());
            final XsdElementKind kind = XmlReportReaderUtils.getXsdElementKind(nextXsdCondition);

            if (nextXsdCondition instanceof XsdThresholdIssueCondition)
            {
                final XsdThresholdIssueCondition xsdThresholdCondition = (XsdThresholdIssueCondition) nextXsdCondition;
                final String metricId = xsdThresholdCondition.getMetricId();
                final IQualityGateThresholdIssueCondition condition = new QualityGateThresholdIssueConditionImpl(kind.getStandardKind(),
                        kind.getPresentationKind(), nextXsdCondition.getFqName(), nextXsdCondition.getName(), nextXsdCondition.getPresentationName(),
                        kind.getImageResourceName(), issueType, metricId, resolutions, severities, nextXsdCondition.getInfo(),
                        convertOperator(nextXsdCondition.getOperator()), nextXsdCondition.getLimit(), convertStatus(nextXsdCondition.getStatus()));
                currentSystemConditions.addCurrentSystemCondition(condition);
                m_globalXmlToElementMap.put(xsdThresholdCondition, condition);
            }
            else
            {
                final IQualityGateIssueCondition condition = new QualityGateIssueConditionImpl(kind.getStandardKind(), kind.getPresentationKind(),
                        nextXsdCondition.getFqName(), nextXsdCondition.getName(), nextXsdCondition.getPresentationName(), kind.getImageResourceName(),
                        issueType, resolutions, severities, nextXsdCondition.getInfo(), convertOperator(nextXsdCondition.getOperator()),
                        nextXsdCondition.getLimit(), convertStatus(nextXsdCondition.getStatus()));
                currentSystemConditions.addCurrentSystemCondition(condition);
                m_globalXmlToElementMap.put(nextXsdCondition, condition);
            }
        }

        for (final XsdExcludeFilter nextXsdExclude : xsdCurrentSystemConditions.getExclude())
        {
            final String issueType = nextXsdExclude.getIssueType();
            final List<ResolutionMode> resolutions = convertResolutions(nextXsdExclude.getResolution());
            final List<Severity> severities = convertSeverities(nextXsdExclude.getSeverity());
            final XsdElementKind kind = XmlReportReaderUtils.getXsdElementKind(nextXsdExclude);
            final String metricId = nextXsdExclude.getMetricId();

            final IQualityGateExcludeFilter excludeFilter = new QualityGateExcludeFilterImpl(kind.getStandardKind(), kind.getPresentationKind(),
                    nextXsdExclude.getFqName(), nextXsdExclude.getName(), nextXsdExclude.getPresentationName(), kind.getImageResourceName(),
                    issueType, resolutions, severities, metricId, nextXsdExclude.getInfo());
            currentSystemConditions.addExcludeFilter(excludeFilter);
            m_globalXmlToElementMap.put(nextXsdExclude, excludeFilter);
        }
    }

    private List<Severity> convertSeverities(final String severityString)
    {
        assert severityString != null && severityString.length() > 0 : "Parameter 'severityString' of method 'convertSeverities' must not be empty";
        final List<String> severities = convertToList(severityString);
        final List<Severity> severityList = new ArrayList<>(severities.size());
        for (final String next : severities)
        {
            final Severity converted = Severity.fromString(next);
            if (converted != null)
            {
                severityList.add(converted);
            }
        }
        return severityList;
    }

    private List<ResolutionMode> convertResolutions(final String resolutionModes)
    {
        assert resolutionModes != null
                && resolutionModes.length() > 0 : "Parameter 'resolutionTypes' of method 'convertResolutions' must not be empty";
        final List<String> resolutions = convertToList(resolutionModes);
        final List<ResolutionMode> resolutionModeList = new ArrayList<>();
        for (final String next : resolutions)
        {
            final ResolutionMode mode = ResolutionMode.fromString(next);
            if (mode != null)
            {
                resolutionModeList.add(mode);
            }
        }

        return resolutionModeList;
    }

    private Status convertStatus(final XsdQualityGateStatus xsdStatus)
    {
        assert xsdStatus != null : "Parameter 'xsdStatus' of method 'convertStatus' must not be null";
        switch (xsdStatus)
        {
        case PASSED:
            return Status.PASSED;
        case FAILED:
            return Status.FAILED;
        case NONE:
            return Status.NONE;
        default:
            assert false : "Unsupported quality gate status: " + xsdStatus.name();
            return null;
        }
    }

    public static List<String> convertToList(final String valueList)
    {
        assert valueList != null && !valueList.isEmpty() : "Parameter 'valueList' of method 'convertToList' must not be empty";
        final ArrayList<String> result = new ArrayList<>();
        for (final String next : valueList.split(LIST_SEPARATOR))
        {
            result.add(next.trim());
        }
        result.trimToSize();
        return result;
    }

    private Operator convertOperator(final XsdOperator operator)
    {
        switch (operator)
        {
        case LESS:
            return Operator.LESS;
        case LESS_OR_EQUAL:
            return Operator.LESS_OR_EQUAL;
        case EQUAL:
            return Operator.EQUAL;
        case GREATER_OR_EQUAL:
            return Operator.GREATER_OR_EQUAL;
        case GREATER:
            return Operator.GREATER;
        case N_A:
            return Operator.N_A;
        default:
            assert false : "Unsupported operator: " + operator.name();
        }
        return null;
    }
}