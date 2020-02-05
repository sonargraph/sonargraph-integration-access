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

import com.hello2morrow.sonargraph.integration.access.model.DependencyPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;

public final class DependencyPatternImpl implements IDependencyPattern
{
    private final DependencyPatternType type;
    private final String fromPattern;
    private final String toPattern;

    public DependencyPatternImpl(final DependencyPatternType type, final String fromPattern, final String toPattern)
    {
        assert type != null : "Parameter 'type' of method 'DependencyPatternImpl' must not be null";
        assert fromPattern != null && fromPattern.length() > 0 : "Parameter 'fromPattern' of method 'DependencyPatternImpl' must not be empty";
        assert toPattern != null && toPattern.length() > 0 : "Parameter 'toPattern' of method 'DependencyPatternImpl' must not be empty";

        this.type = type;
        this.fromPattern = fromPattern;
        this.toPattern = toPattern;
    }

    @Override
    public DependencyPatternType getType()
    {
        return type;
    }

    @Override
    public String getFromPattern()
    {
        return fromPattern;
    }

    @Override
    public String getToPattern()
    {
        return toPattern;
    }
}