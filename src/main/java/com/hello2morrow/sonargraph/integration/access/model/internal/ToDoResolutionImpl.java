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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Date;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;
import com.hello2morrow.sonargraph.integration.access.model.IToDoDefinition;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;

public class ToDoResolutionImpl extends TaskResolutionImpl implements IToDoDefinition
{
    private static final long serialVersionUID = -6690045425225809050L;
    private static final String TODO = "Todo";

    public ToDoResolutionImpl(final String fqName, final Priority priority, final List<IIssue> issues, final int matchingElementsCount,
            final String description, final String information, final String assignee, final Date dateTime,
            final List<IElementPattern> elementPatterns, final List<IDependencyPattern> dependencyPatterns, final IMatching matching,
            final String descriptor)
    {
        super(fqName, ResolutionType.TODO, priority, issues, matchingElementsCount, description, information, assignee, dateTime, elementPatterns,
                dependencyPatterns, matching, descriptor);
    }

    @Override
    public String getImageResourceName()
    {
        return TODO;
    }
}