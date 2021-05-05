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

public enum ResolutionMode implements IEnumeration
{
    IGNORE,
    TASK,
    NONE;

    public static ResolutionMode fromString(final String value)
    {
        assert value != null && value.length() > 0 : "Parameter 'value' of method 'fromString' must not be empty";

        if (IGNORE.name().equalsIgnoreCase(value))
        {
            return IGNORE;
        }

        if (TASK.name().equalsIgnoreCase(value))
        {
            return TASK;
        }

        if (NONE.name().equalsIgnoreCase(value))
        {
            return NONE;
        }

        assert false : "Unsupported value: " + value;
        return null;
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
}