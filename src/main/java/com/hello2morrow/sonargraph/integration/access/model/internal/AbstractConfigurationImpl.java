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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.model.IConfiguration;

abstract class AbstractConfigurationImpl extends ElementWithDescriptionImpl implements IConfiguration
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationImpl.class);

    private static final long serialVersionUID = 2109849250086262632L;
    private final Map<String, Integer> intProperties = new HashMap<>();
    private final Map<String, String> stringProperties = new HashMap<>();
    private final Map<String, Boolean> booleanProperties = new HashMap<>();
    private final Map<String, Float> floatProperties = new HashMap<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public AbstractConfigurationImpl(final String name, final String presentationName, final String description)
    {
        super(name, presentationName, description);
    }

    public final void addIntConfigurationValue(final String property, final int value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addIntConfigurationValue' must not be empty";

        final Object prev = properties.put(property, new Integer(value));
        assert prev == null : "Integer property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value
                + "'";
        final Integer previous = intProperties.put(property, new Integer(value));
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    public final void addStringConfigurationValue(final String property, final String value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addStringConfigurationValue' must not be empty";

        final Object prev = properties.put(property, value);
        assert prev == null : "String property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value
                + "'";
        final String previous = stringProperties.put(property, value);
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    public final void addBooleanConfigurationValue(final String property, final boolean value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addBooleanConfigurationValue' must not be empty";

        final Object prev = properties.put(property, Boolean.valueOf(value));
        assert prev == null : "Boolean property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value
                + "'";
        final Boolean previous = booleanProperties.put(property, Boolean.valueOf(value));
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    public final void addFloatConfigurationValue(final String property, final float value)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'addFloatConfigurationValue' must not be empty";

        final Object prev = properties.put(property, Float.valueOf(value));
        assert prev == null : "Float property '" + property + "' was already present with value '" + prev + "', now overwritten with '" + value + "'";
        final Float previous = floatProperties.put(property, Float.valueOf(value));
        assert previous == null : "Property '" + property + "' was already present with value '" + previous + "', now overwritten with '" + value
                + "'";
    }

    @Override
    public final Map<String, Object> getConfigurationValues()
    {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public final Integer getIntConfigurationValue(final String property)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'getIntConfigurationValue' must not be empty";

        if (intProperties.containsKey(property))
        {
            return intProperties.get(property);
        }
        LOGGER.warn("Integer property '{}' does not exist, returning {}", property, null);
        return null;
    }

    @Override
    public final String getStringConfigurationValue(final String property)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'getStringConfigurationValue' must not be empty";

        if (stringProperties.containsKey(property))
        {
            return stringProperties.get(property);
        }
        LOGGER.warn("String property '{}' does not exist, returning null", property);
        return null;
    }

    @Override
    public final Boolean getBooleanConfigurationValue(final String property)
    {
        if (booleanProperties.containsKey(property))
        {
            return booleanProperties.get(property);
        }
        LOGGER.warn("Boolean property '{}' does not exist, returning null", property);
        return false;
    }

    @Override
    public Float getFloatConfigurationValue(final String property)
    {
        assert property != null && property.length() > 0 : "Parameter 'property' of method 'getFloatConfigurationValue' must not be empty";
        if (floatProperties.containsKey(property))
        {
            return floatProperties.get(property);
        }
        LOGGER.warn("Float property '{}' does not exist, returning {}", property, null);
        return null;
    }
}