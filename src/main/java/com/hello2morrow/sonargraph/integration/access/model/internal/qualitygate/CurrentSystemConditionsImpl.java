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

import com.hello2morrow.sonargraph.integration.access.model.internal.ElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.ICurrentSystemConditions;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateExcludeFilter;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGateIssueCondition;

public final class CurrentSystemConditionsImpl extends ElementImpl implements ICurrentSystemConditions
{
    private static final long serialVersionUID = 7351205166734056150L;
    private final List<IQualityGateIssueCondition> m_conditions = new ArrayList<>();
    private final List<IQualityGateExcludeFilter> m_excludeFilters = new ArrayList<>();

    public CurrentSystemConditionsImpl()
    {
        super("Current System Conditions", "Current System Conditions");
    }

    public void addCurrentSystemCondition(final IQualityGateIssueCondition condition)
    {
        assert condition != null : "Parameter 'condition' of method 'addCurrentSystemCondition' must not be null";
        m_conditions.add(condition);
    }

    @Override
    public List<IQualityGateIssueCondition> getCurrentSystemConditions()
    {
        return Collections.unmodifiableList(m_conditions);
    }

    public void addExcludeFilter(final IQualityGateExcludeFilter filter)
    {
        assert filter != null : "Parameter 'filter' of method 'addExcludeFilter' must not be null";
        m_excludeFilters.add(filter);
    }

    @Override
    public List<IQualityGateExcludeFilter> getExcludeFilters()
    {
        return Collections.unmodifiableList(m_excludeFilters);
    }
}