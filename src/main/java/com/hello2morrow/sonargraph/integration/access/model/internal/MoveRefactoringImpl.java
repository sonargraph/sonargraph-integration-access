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
import com.hello2morrow.sonargraph.integration.access.model.IMoveRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.RefactoringStatus;

public class MoveRefactoringImpl extends AbstractRefactoringImpl implements IMoveRefactoring
{
    private static final long serialVersionUID = 446521166917502168L;
    private final String targetRootDirectoryFqName;
    private final String moveToParentName;
    private final String elementKind;

    public MoveRefactoringImpl(final String fqName, final Priority priority, final List<IIssue> issues, final int matchingElementsCount,
            final String description, final String information, final String assignee, final Date dateTime,
            final List<IElementPattern> elementPatterns, final List<IDependencyPattern> dependencyPatterns, final IMatching matching,
            final String descriptor, final RefactoringStatus status, final int numberOfPotentiallyDoneElements,
            final String targetRootDirectoryFqName, final String moveToParentName, final String elementKind)
    {
        super(fqName, priority, issues, matchingElementsCount, description, information, assignee, dateTime, elementPatterns, dependencyPatterns,
                matching, descriptor, status, numberOfPotentiallyDoneElements);
        assert targetRootDirectoryFqName != null
                && targetRootDirectoryFqName.length() > 0 : "Parameter 'targetRootDirectoryFqName' of method 'MoveRefactoringImpl' must not be empty";
        assert moveToParentName != null
                && moveToParentName.length() > 0 : "Parameter 'moveToParentName' of method 'MoveRefactoringImpl' must not be empty";
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'MoveRefactoringImpl' must not be empty";

        this.targetRootDirectoryFqName = targetRootDirectoryFqName;
        this.moveToParentName = moveToParentName;
        this.elementKind = elementKind;
    }

    @Override
    public String getTargetRootDirectoryFqName()
    {
        return targetRootDirectoryFqName;
    }

    @Override
    public String getMoveToParentName()
    {
        return moveToParentName;
    }

    @Override
    public String getElementKind()
    {
        return elementKind;
    }

    @Override
    public String getImageResourceName()
    {
        return "MoveRefactoring";
    }
}