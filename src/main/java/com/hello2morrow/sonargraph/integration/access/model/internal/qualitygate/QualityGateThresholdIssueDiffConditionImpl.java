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
package com.hello2morrow.sonargraph.integration.access.model.internal.qualitygate;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.ResolutionMode;
import com.hello2morrow.sonargraph.integration.access.model.Severity;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Check;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateThresholdIssueDiffCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Operator;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Status;

public final class QualityGateThresholdIssueDiffConditionImpl extends QualityGateIssueDiffConditionImpl
        implements IQualityGateThresholdIssueDiffCondition
{
    private static final long serialVersionUID = 3210885895029740958L;
    private final String m_metricId;
    private final Float m_diffThreshold;
    private final Float m_diffThresholdRelative;
    private final Operator m_operator;

    public QualityGateThresholdIssueDiffConditionImpl(final String kind, final String presentationKind, final String fqName, final String name,
            final String presentationName, final String imageResourceName, final String issueType, final List<ResolutionMode> resolutionModes,
            final List<Severity> severities, final String info, final String metricId, final Float diffThreshold, final Float diffThresholdRelative,
            final Operator operator, final Check check, final Status status)
    {
        super(kind, presentationKind, fqName, name, presentationName, imageResourceName, issueType, resolutionModes, severities, info, check, status);
        assert metricId != null
                && metricId.length() > 0 : "Parameter 'metricId' of method 'QualityGateThresholdIssueDiffConditionImpl' must not be empty";
        assert operator != null : "Parameter 'operator' of method 'QualityGateThresholdIssueDiffCondition' must not be null";

        m_metricId = metricId;
        m_diffThreshold = diffThreshold;
        m_diffThresholdRelative = diffThresholdRelative;
        m_operator = operator;
    }

    @Override
    public String getMetricId()
    {
        return m_metricId;
    }

    @Override
    public Float getDiffThreshold()
    {
        return m_diffThreshold;
    }

    @Override
    public Float getDiffThresholdRelative()
    {
        return m_diffThresholdRelative;
    }

    @Override
    public Operator getOperator()
    {
        return m_operator;
    }
}