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

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;

public final class MatchingImpl implements IMatching
{
    private final String info;
    private final List<IElementPattern> patterns;

    public MatchingImpl(final String info, final List<IElementPattern> patterns)
    {
        assert info != null && info.length() > 0 : "Parameter 'info' of method 'MatchingImpl' must not be empty";
        assert patterns != null && !patterns.isEmpty() : "Parameter 'patterns' of method 'MatchingImpl' must not be empty";

        this.info = info;
        this.patterns = patterns;
    }

    @Override
    public List<IElementPattern> getPatterns()
    {
        return Collections.unmodifiableList(patterns);
    }

    @Override
    public String getInfo()
    {
        return info;
    }
}