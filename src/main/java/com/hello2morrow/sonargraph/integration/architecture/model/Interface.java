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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an interface of an artifact
 */
public final class Interface extends ArchitectureElement
{
    public enum DependencyType
    {
        CALL,
        EXTENDS,
        IMPLEMENTS,
        READ,
        WRITE,
        USES,
        NEW
    }

    private final ArchitectureElement parent;
    private final boolean isOptional;
    private final EnumSet<DependencyType> allowedDependencyTypes; // Will be bull, if here are no restrictions
    private final List<Interface> exportedInterfaces = new ArrayList<>();

    public Interface(final ArchitectureElement parent, final String name, final boolean isOptional,
            final EnumSet<DependencyType> allowedDependencyTypes)
    {
        super(name);

        assert parent != null;
        this.parent = parent;
        this.isOptional = isOptional;
        this.allowedDependencyTypes = allowedDependencyTypes;
    }

    /**
     * Does this interface have dependency type restrictions?
     *
     * @return true, if there are dependency type restrictions.
     */
    public boolean isRestricted()
    {
        return allowedDependencyTypes != null;
    }

    /**
     * Get the set of allowed dependency types for this interface. If unrestricted all possible
     * dependency types will be returned.
     *
     * @return set of allowed dependency types.
     */
    public Set<DependencyType> getAllowedDependencyTypes()
    {
        if (isRestricted())
        {
            return Collections.unmodifiableSet(allowedDependencyTypes);
        }
        return EnumSet.allOf(DependencyType.class);
    }

    public void addExportedInterface(final Interface iface)
    {
        assert iface != null;
        exportedInterfaces.add(iface);
    }

    public List<Interface> getExportedInterfaces()
    {
        return Collections.unmodifiableList(exportedInterfaces);
    }

    public boolean isOptional()
    {
        return isOptional;
    }

    public ArchitectureElement getParent()
    {
        return parent;
    }
}