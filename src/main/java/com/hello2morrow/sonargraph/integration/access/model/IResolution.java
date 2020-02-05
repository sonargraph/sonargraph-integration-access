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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Date;
import java.util.List;

public interface IResolution extends INamedElement
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
     * @return the information - never <code>null</code>
     */
    public String getInformation();

    /**
     * @return the formatted date of creation for this resolution.
     */
    public String getDate();

    /**
     * @return the date of creation.
     */
    public Date getCreationDate();

    /**
     * @return the issue - never <code>null</code>
     */
    public List<IIssue> getIssues();

    /**
     * @deprecated use {@link #getMatchingElementsCount()} instead
     * @return true if matching element count is &gt; 0, false otherwise
     */
    @Deprecated
    default public boolean isApplicable()
    {
        return true;
    }

    /**
     * @return the number of elements that this resolution matches.
     */
    public int getMatchingElementsCount();

    /**
     * @return true if the resolution is a task, i.e. todo, fix, or refactoring.
     */
    public boolean isTask();

    /**
     * @return the element patterns used to connect this resolution to the code.
     */
    public List<IElementPattern> getElementPatterns();

    /**
     * @return the dependency patterns used to connect this resolution to the code.
     */
    public List<IDependencyPattern> getDependencyPatterns();

    /**
     * @return the {@link IMatching} object used to connect this resolution to the code.
     */
    public IMatching getMatching();

    /**
     * @return the descriptor that uniquely identifies a resolution.
     */
    public String getDescriptor();

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    default public int getNumberOfAffectedParserDependencies()
    {
        return -1;
    }
}