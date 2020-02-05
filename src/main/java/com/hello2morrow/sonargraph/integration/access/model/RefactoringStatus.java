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

import com.hello2morrow.sonargraph.integration.access.foundation.IEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;

public enum RefactoringStatus implements IEnumeration
{
    NOT_APPLICABLE(false),
    APPLICABLE(true),
    PARTIALLY_APPLICABLE(true),
    POTENTIALLY_DONE(false),
    NO_ELEMENT_MATCHED(false),
    LANGUAGE_NOT_AVAILABLE(false),
    TARGET_ROOT_DIRECTORY_NOT_FOUND(false),
    NONE(false);

    private final boolean m_isApplicable;

    private RefactoringStatus(final boolean isApplicable)
    {
        m_isApplicable = isApplicable;
    }

    public boolean isApplicable()
    {
        return m_isApplicable;
    }

    @Override
    public String getStandardName()
    {
        return Utility.convertConstantNameToStandardName(name());
    }

    @Override
    public String getPresentationName()
    {
        return Utility.convertConstantNameToPresentationName(name());
    }

    public static RefactoringStatus createFromStandardName(final String name)
    {
        assert name != null && name.trim().length() > 0 : "Parameter 'name' of method 'createFromStandardName' must not be empty";

        final String nameLowerCase = name.toLowerCase().trim();
        switch (nameLowerCase)
        {
        case "applicable":
            return RefactoringStatus.APPLICABLE;
        case "notapplicable":
            return RefactoringStatus.NOT_APPLICABLE;
        case "partiallyapplicable":
            return RefactoringStatus.PARTIALLY_APPLICABLE;
        case "potentiallydone":
            return RefactoringStatus.POTENTIALLY_DONE;
        case "noelementmatched":
            return RefactoringStatus.NO_ELEMENT_MATCHED;
        case "languagenotavailable":
            return RefactoringStatus.LANGUAGE_NOT_AVAILABLE;
        case "targetrootdirectorynotfound":
            return RefactoringStatus.TARGET_ROOT_DIRECTORY_NOT_FOUND;
        case "none":
            return RefactoringStatus.NONE;
        default:
            assert false : "Unspported refactoring status '" + name + "'";
        }

        return null;
    }
}