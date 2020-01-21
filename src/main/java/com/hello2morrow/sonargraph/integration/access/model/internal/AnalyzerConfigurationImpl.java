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

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzerConfiguration;

public final class AnalyzerConfigurationImpl extends AbstractConfigurationImpl implements IAnalyzerConfiguration
{
    private static final long serialVersionUID = -5782893226459810814L;

    private final IAnalyzer analyzer;

    public AnalyzerConfigurationImpl(final IAnalyzer analyzer)
    {
        super(analyzer.getName(), analyzer.getPresentationName(), analyzer.getDescription());
        this.analyzer = analyzer;
    }

    public IAnalyzer getAnalyzer()
    {
        return analyzer;
    }
}