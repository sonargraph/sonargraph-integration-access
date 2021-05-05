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
    ResolutionType getType();

    /**
     * @return the priority - never <code>null</code>
     */
    Priority getPriority();

    /**
     * @return the assignee - never <code>null</code>
     */
    String getAssignee();

    /**
     * @return the description - never <code>null</code>
     */
    String getDescription();

    /**
     * @return the information - never <code>null</code>
     */
    String getInformation();

    /**
     * @return the formatted date of creation for this resolution.
     */
    String getDate();

    /**
     * @return the date of creation.
     */
    Date getCreationDate();

    /**
     * @return the issue - never <code>null</code>
     */
    List<IIssue> getIssues();

    /**
     * @deprecated use {@link #getMatchingElementsCount()} instead
     * @return true if matching element count is &gt; 0, false otherwise
     */
    @Deprecated
    default boolean isApplicable()
    {
        return true;
    }

    /**
     * @return the number of elements that this resolution matches.
     */
    int getMatchingElementsCount();

    /**
     * @return true if the resolution is a task, i.e. todo, fix, or refactoring.
     */
    boolean isTask();

    /**
     * @return the element patterns used to connect this resolution to the code.
     */
    List<IElementPattern> getElementPatterns();

    /**
     * @return the dependency patterns used to connect this resolution to the code.
     */
    List<IDependencyPattern> getDependencyPatterns();

    /**
     * @return the {@link IMatching} object used to connect this resolution to the code.
     */
    IMatching getMatching();

    /**
     * @return the descriptor that uniquely identifies a resolution.
     */
    String getDescriptor();

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    default int getNumberOfAffectedParserDependencies()
    {
        return -1;
    }
}