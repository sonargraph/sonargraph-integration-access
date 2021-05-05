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

public enum ResolutionType implements IEnumeration
{
    TODO,
    IGNORE,
    REFACTORING,
    FIX,
    NONE;

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

    public static ResolutionType fromString(final String name)
    {
        assert name != null && name.trim().length() > 0 : "Parameter 'name' of method 'fromName' must not be empty";

        final String nameLowerCase = name.toLowerCase().trim();
        switch (nameLowerCase)
        {
        case "todo":
            return ResolutionType.TODO;
        case "ignore":
            return ResolutionType.IGNORE;
        case "refactoring":
            return ResolutionType.REFACTORING;
        case "fix":
            return ResolutionType.FIX;
        case "none":
            return ResolutionType.NONE;
        default:
            assert false : "Unspported resolution '" + name + "'";
        }

        return null;
    }

    @Override
    public String toString()
    {
        return getPresentationName();
    }
}