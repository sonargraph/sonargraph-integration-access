package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzerConfiguration;

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
public final class AnalyzerConfigurationImpl extends ElementWithDescriptionImpl implements IAnalyzerConfiguration
{
    private static final long serialVersionUID = -5782893226459810814L;

    private final Map<String, Integer> intProperties = new HashMap<>();
    private final Map<String, String> stringProperties = new HashMap<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    private final IAnalyzer analyzer;

    public AnalyzerConfigurationImpl(final IAnalyzer analyzer)
    {
        super(analyzer.getName(), analyzer.getPresentationName(), analyzer.getDescription());
        this.analyzer = analyzer;
    }

    public void addIntConfigurationValue(final String property, final int value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addIntConfigurationValue' must not be empty";

        final Object prev = properties.put(property, new Integer(value));
        assert prev == null : "Property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value + "'";

        final Integer previous = intProperties.put(property, new Integer(value));
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    public void addStringConfigurationValue(final String property, final String value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addStringConfigurationValue' must not be empty";

        final Object prev = properties.put(property, value);
        assert prev == null : "Property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value + "'";

        final String previous = stringProperties.put(property, value);
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    @Override
    public Map<String, Object> getConfigurationValues()
    {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public int getIntConfigurationValue(final String property)
    {
        if (intProperties.containsKey(property))
        {
            return intProperties.get(property).intValue();
        }

        return -1;
    }

    @Override
    public String getStringConfigurationValue(final String property)
    {
        if (stringProperties.containsKey(property))
        {
            return stringProperties.get(property);
        }

        return null;
    }

    public IAnalyzer getAnalyzer()
    {
        return analyzer;
    }
}