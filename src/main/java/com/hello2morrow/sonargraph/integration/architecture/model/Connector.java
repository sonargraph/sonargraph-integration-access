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
package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Connector extends ArchitectureElement
{
    private final ArchitectureElement parent;
    private final boolean isOptional;
    private final List<Connector> includedConnectors = new ArrayList<>();
    private final List<Interface> connectedInterfaces = new ArrayList<>();

    public Connector(final ArchitectureElement parent, final String name, final boolean isOptional)
    {
        super(name);
        assert parent != null;
        this.parent = parent;
        this.isOptional = isOptional;
    }

    public boolean isOptional()
    {
        return isOptional;
    }

    public ArchitectureElement getParent()
    {
        return parent;
    }

    public void addIncludedConnector(final Connector included)
    {
        assert included != null;

        includedConnectors.add(included);
    }

    public List<Connector> getIncludedConnectors()
    {
        return Collections.unmodifiableList(includedConnectors);
    }

    public void addConnectedInterface(final Interface connection)
    {
        assert connection != null;

        connectedInterfaces.add(connection);
    }

    public List<Interface> getConnectedInterfaces()
    {
        return Collections.unmodifiableList(connectedInterfaces);
    }
}