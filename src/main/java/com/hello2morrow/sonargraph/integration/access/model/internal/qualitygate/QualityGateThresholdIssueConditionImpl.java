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
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateThresholdIssueCondition;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Operator;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.Status;

public final class QualityGateThresholdIssueConditionImpl extends AbstractQualityGateCurrentIssueConditionImpl
        implements IQualityGateThresholdIssueCondition
{
    private static final long serialVersionUID = -4805901307661269564L;
    private final String m_metricId;

    public QualityGateThresholdIssueConditionImpl(final String kind, final String presentationKind, final String fqName, final String name,
            final String presentationName, final String imageResourceName, final String issueType, final String metricId,
            final List<ResolutionMode> resolutionModes, final List<Severity> severities, final String info, final Operator operator, final int limit,
            final Status status)
    {
        super(kind, presentationKind, fqName, name, presentationName, imageResourceName, issueType, resolutionModes, severities, info, operator,
                limit, status);
        assert metricId != null : "Parameter 'metricId' of method 'QualityGateThresholdIssueConditionImpl' must not be null";

        m_metricId = metricId;
    }

    @Override
    public String getMetricId()
    {
        return m_metricId;
    }
}