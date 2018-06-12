package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an interface of an artifact
 */
public class Interface extends ArchitectureElement
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

    private final Artifact m_parent;
    private final boolean m_isOptional;
    private final EnumSet<DependencyType> m_allowedDependencyTypes; // Will be bull, if here are no restrictions
    private final List<Interface> m_exportedInterfaces = new ArrayList<>();

    public Interface(Artifact parent, String name, boolean isOptional, EnumSet<DependencyType> allowedDependencyTypes)
    {
        super(name);

        assert parent != null;
        m_parent = parent;
        m_isOptional = isOptional;
        m_allowedDependencyTypes = allowedDependencyTypes;
    }

    /**
     * Does this interface have dependency type restrictions?
     *
     * @return true, if there are dependency type restrictions.
     */
    public boolean isRestricted()
    {
        return m_allowedDependencyTypes != null;
    }

    /**
     * Get the set of allowed dependency types for this interface. If unresstricted all possible
     * dependency types will be returned.
     *
     * @return set of allowed dependency types.
     */
    public Set<DependencyType> getAllowedDependencyTypes()
    {
        if (isRestricted())
        {
            return Collections.unmodifiableSet(m_allowedDependencyTypes);
        }
        return EnumSet.allOf(DependencyType.class);
    }

    public void addExportedInterface(Interface iface)
    {
        assert iface != null;

        m_exportedInterfaces.add(iface);
    }

    public List<Interface> getExportedInterfaces()
    {
        return Collections.unmodifiableList(m_exportedInterfaces);
    }

    public boolean isOptional()
    {
        return m_isOptional;
    }

    public Artifact getParent()
    {
        return m_parent;
    }
}
