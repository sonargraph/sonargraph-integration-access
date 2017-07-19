/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IResolution extends IElement
{
    /**
     * @return the type - never <code>null</code>
     */
    public ResolutionType getType();

    /**
     * @return the priority - never <code>null</code>
     */
    public Priority getPriority();

    /**
     * @return the assignee - never <code>null</code>
     */
    public String getAssignee();

    /**
     * @return the description - never <code>null</code>
     */
    public String getDescription();

    /**
     * @return the date - never <code>null</code>
     */
    public String getDate();

    /**
     * @return the issue - never <code>null</code>
     */
    public List<IIssue> getIssues();

    public boolean isApplicable();

    public boolean isTask();

    public int getNumberOfAffectedParserDependencies();
}