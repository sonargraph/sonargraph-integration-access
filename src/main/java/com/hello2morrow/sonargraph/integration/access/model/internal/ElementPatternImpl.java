/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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

import com.hello2morrow.sonargraph.integration.access.model.ElementPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;

public final class ElementPatternImpl implements IElementPattern
{
    private final ElementPatternType type;
    private final String pattern;
    private String hash = null;

    public ElementPatternImpl(final ElementPatternType type, final String pattern)
    {
        assert type != null : "Parameter 'type' of method 'ElementPatternImpl' must not be null";
        assert pattern != null && pattern.length() > 0 : "Parameter 'pattern' of method 'ElementPatternImpl' must not be empty";

        this.type = type;
        this.pattern = pattern;
    }

    public ElementPatternImpl(final ElementPatternType type, final String pattern, final String hash)
    {
        this(type, pattern);
        assert hash != null && hash.length() > 0 : "Parameter 'hash' of method 'ElementPatternImpl' must not be empty";
        this.hash = hash;
    }

    @Override
    public ElementPatternType getType()
    {
        return type;
    }

    @Override
    public String getPattern()
    {
        return pattern;
    }

    @Override
    public String getHash()
    {
        return hash;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(pattern).append(" [").append(type).append("]").toString();
    }
}