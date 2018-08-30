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

package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Artifact extends ArchitectureElement
{
    public enum Stereotype
    {
        PUBLIC,
        HIDDEN,
        LOCAL,
        EXPOSED,
        OPTIONAL,
        DEPRECATED,
        UNRESTRICTED,
        RELAXED,
        STRICT
    }

    private final Artifact parent;
    private final EnumSet<Stereotype> stereotypes = EnumSet.noneOf(Stereotype.class);
    private final List<Artifact> children = new ArrayList<>();
    private final List<Interface> interfaces = new ArrayList<>();
    private final List<Connector> connectors = new ArrayList<>();

    /**
     * Initialize new artifact element
     * @param fqn the fully qualified name of the artifact
     */
    public Artifact(final Artifact parent, final String fqn)
    {
        super(fqn);
        this.parent = parent;
    }

    public void addStereoType(final Stereotype st)
    {
        assert st != null;
        stereotypes.add(st);
    }

    public void addChild(final Artifact child)
    {
        assert child != null;
        assert child.getParent() == this;
        children.add(child);
    }

    public void addInterface(final Interface iface)
    {
        assert iface != null;
        interfaces.add(iface);
    }

    public void addConnector(final Connector conn)
    {
        assert conn != null;
        connectors.add(conn);
    }

    public Artifact getParent()
    {
        return parent;
    }

    public Set<Stereotype> getStereotypes()
    {
        return Collections.unmodifiableSet(stereotypes);
    }

    public List<Artifact> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    public List<Interface> getInterfaces()
    {
        return Collections.unmodifiableList(interfaces);
    }

    public List<Connector> getConnectors()
    {
        return Collections.unmodifiableList(connectors);
    }
}