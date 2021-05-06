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
import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateFilter;

abstract class AbstractQualityGateMatchingElementImpl extends NamedElementImpl implements IQualityGateFilter
{
    private static final long serialVersionUID = -6173446961559909501L;

    private final String m_issueType;
    private final List<ResolutionMode> m_resolutionModes;
    private final List<Severity> m_severities;

    private final String m_info;

    public AbstractQualityGateMatchingElementImpl(final String kind, final String presentationKind, final String fqName, final String name,
            final String presentationName, final String imageResourceName, final String issueType, final List<ResolutionMode> resolutionModes,
            final List<Severity> severities, final String info)
    {
        super(kind, presentationKind, name, presentationName, fqName, imageResourceName);

        assert issueType != null : "Parameter 'issueType' of method 'AbstractQualityGateMatchingElementImpl' must not be null";
        assert resolutionModes != null : "Parameter 'resolutionModes' of method 'AbstractQualityGateMatchingElementImpl' must not be null";
        assert severities != null : "Parameter 'severities' of method 'AbstractQualityGateMatchingElementImpl' must not be null";
        assert info != null : "Parameter 'info' of method 'AbstractQualityGateMatchingElementImpl' must not be null";

        m_issueType = issueType;
        m_resolutionModes = resolutionModes;
        m_severities = severities;
        m_info = info;
    }

    @Override
    public final String getIssueType()
    {
        return m_issueType;
    }

    @Override
    public final List<Severity> getSeverity()
    {
        return m_severities;
    }

    @Override
    public final List<ResolutionMode> getResolutionMode()
    {
        return m_resolutionModes;
    }

    public final String getInformation()
    {
        return m_info;
    }
}