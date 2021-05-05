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

import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;
import com.hello2morrow.sonargraph.integration.access.model.internal.SystemFileElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.ICurrentSystemConditions;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IDiffAgainstBaselineConditions;
import com.hello2morrow.sonargraph.integration.access.model.qualitygate.IQualityGate;

public final class QualityGateImpl extends SystemFileElementImpl implements IQualityGate
{
    private static final long serialVersionUID = 1723895144779266776L;

    private final CurrentSystemConditionsImpl m_currentSystemConditionsImpl = new CurrentSystemConditionsImpl();
    private final IDiffAgainstBaselineConditions m_diffAgainstBaselineConditions = new DiffAgainstBaselineConditionsImpl();

    public QualityGateImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final String description, final String imageResourceName, final String path, final SystemFileType type, final long lastModified)
    {
        super(kind, presentationKind, name, presentationName, fqName, description, imageResourceName, path, type, lastModified);
    }

    @Override
    public ICurrentSystemConditions getCurrentSystemConditions()
    {
        return m_currentSystemConditionsImpl;
    }

    @Override
    public IDiffAgainstBaselineConditions getDiffAgainstBaselineConditions()
    {
        return m_diffAgainstBaselineConditions;
    }
}