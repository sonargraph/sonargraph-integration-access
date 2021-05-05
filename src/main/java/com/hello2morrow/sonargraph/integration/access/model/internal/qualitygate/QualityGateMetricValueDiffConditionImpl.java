/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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

import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateMetricDiffCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Operator;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Status;

public final class QualityGateMetricValueDiffConditionImpl extends NamedElementImpl implements IQualityGateMetricDiffCondition
{
    private final String m_metricId;
    private final Operator m_operator;
    private final Float m_diffThreshold;
    private final Float m_diffThresholdRelative;
    private final Status m_status;

    public QualityGateMetricValueDiffConditionImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String imageResourceName, final String metricId, final Operator operator, final Float diffThreshold,
            final Float diffThresholdRelative, final Status status)
    {
        super(kind, presentationKind, name, presentationName, fqName, imageResourceName);
        assert metricId != null : "Parameter 'metricId' of method 'QualityGateMetricValueDiffConditionImpl' must not be null";
        assert operator != null : "Parameter 'operator' of method 'QualityGateMetricValueDiffConditionImpl' must not be null";

        m_metricId = metricId;
        m_operator = operator;
        m_diffThreshold = diffThreshold;
        m_diffThresholdRelative = diffThresholdRelative;
        m_status = status;
    }

    private static final long serialVersionUID = 1990052768585667688L;

    @Override
    public String getMetricId()
    {
        return m_metricId;
    }

    @Override
    public Operator getOperator()
    {
        return m_operator;
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
    public Status getStatus()
    {
        return m_status;
    }
}