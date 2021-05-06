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
import java.util.Set;

import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.PluginExecutionPhase;

public class PluginImpl extends ElementWithDescriptionImpl implements IPlugin
{
    private static final long serialVersionUID = 2844288755616364951L;

    private final String vendor;
    private final String version;
    private final boolean isLicensed;
    private final boolean isEnabled;
    private final Set<PluginExecutionPhase> supportedExecutionPhases;
    private final Set<PluginExecutionPhase> activeExecutionPhases;

    public PluginImpl(final String name, final String presentationName, final String description, final String vendor, final String version,
            final boolean isLicensed, final boolean isEnabled, final Set<PluginExecutionPhase> supportedExecutionPhases,
            final Set<PluginExecutionPhase> activeExecutionPhases)
    {
        super(name, presentationName, description);
        assert vendor != null && vendor.length() > 0 : "Parameter 'vendor' of method 'PluginImpl' must not be empty";
        assert version != null : "Parameter 'version' of method 'PluginImpl' must not be null";
        assert supportedExecutionPhases != null
                && !supportedExecutionPhases.isEmpty() : "Parameter 'supportedExecutionPhases' of method 'PluginImpl' must not be empty";
        assert activeExecutionPhases != null : "Parameter 'activeExecutionPhases' of method 'PluginImpl' must not be null";

        this.vendor = vendor;
        this.version = version;
        this.isLicensed = isLicensed;
        this.isEnabled = isEnabled;
        this.supportedExecutionPhases = supportedExecutionPhases;
        this.activeExecutionPhases = activeExecutionPhases;
    }

    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }

    @Override
    public String getVendor()
    {
        return vendor;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public boolean isLicensed()
    {
        return isLicensed;
    }

    @Override
    public Set<PluginExecutionPhase> getActiveExecutionPhases()
    {
        return Collections.unmodifiableSet(activeExecutionPhases);
    }

    @Override
    public Set<PluginExecutionPhase> getSupportedExecutionPhases()
    {
        return Collections.unmodifiableSet(supportedExecutionPhases);
    }
}