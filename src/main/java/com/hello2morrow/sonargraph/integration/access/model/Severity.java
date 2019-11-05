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

public enum Severity implements IEnumeration
{
    //DO NOT CHANGE THE ORDER OF THESE CONSTANTS!
    ERROR,
    WARNING,
    INFO,
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

    public static Severity fromString(final String name)
    {
        assert name != null && name.trim().length() > 0 : "Parameter 'name' of method 'fromName' must not be empty";

        final String nameLowerCase = name.toLowerCase().trim();
        switch (nameLowerCase)
        {
        case "error":
            return Severity.ERROR;
        case "warning":
            return Severity.WARNING;
        case "info":
            return Severity.INFO;
        case "none":
            return Severity.NONE;
        default:
            assert false : "Unspported severity '" + name + "'";
        }

        return null;
    }
}