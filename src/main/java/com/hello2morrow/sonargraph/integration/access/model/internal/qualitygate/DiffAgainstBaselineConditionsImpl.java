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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IDiffAgainstBaselineConditions;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateElement;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateExcludeFilter;

public final class DiffAgainstBaselineConditionsImpl extends com.hello2morrow.sonargraph.integration.access.model.internal.ElementImpl
        implements IDiffAgainstBaselineConditions
{
    private static final long serialVersionUID = -4169763910926751792L;
    private final List<IQualityGateElement> m_issueDiffConditions = new ArrayList<>();
    private final List<IQualityGateExcludeFilter> m_excludeFilters = new ArrayList<>();

    public DiffAgainstBaselineConditionsImpl()
    {
        super("Baseline Conditions", "Baseline Conditions");
    }

    public void addQualityGateCondition(final IQualityGateElement condition)
    {
        assert condition != null : "Parameter 'condition' of method 'addQualityGateIssueDiffCondition' must not be null";
        m_issueDiffConditions.add(condition);
    }

    @Override
    public List<IQualityGateElement> getDiffConditions()
    {
        return Collections.unmodifiableList(m_issueDiffConditions);
    }

    @Override
    public List<IQualityGateExcludeFilter> getExcludeFilters()
    {
        return Collections.unmodifiableList(m_excludeFilters);
    }

    public void addExcludeFilter(final IQualityGateExcludeFilter excludeFilter)
    {
        assert excludeFilter != null : "Parameter 'excludeFilter' of method 'addExcludeFilter' must not be null";
        m_excludeFilters.add(excludeFilter);
    }
}