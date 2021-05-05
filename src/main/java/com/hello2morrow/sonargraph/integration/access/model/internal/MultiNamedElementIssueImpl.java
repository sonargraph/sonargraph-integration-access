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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public abstract class MultiNamedElementIssueImpl extends IssueImpl
{
    private static final long serialVersionUID = -640578050182849269L;

    public MultiNamedElementIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider)
    {
        super(name, presentationName, description, issueType, provider, -1, -1);
    }

    public abstract List<INamedElement> getNamedElements();
}