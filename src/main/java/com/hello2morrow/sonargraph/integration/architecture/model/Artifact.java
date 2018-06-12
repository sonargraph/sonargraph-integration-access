package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Artifact extends ArchitectureElement
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

    private final Artifact m_parent;
    private EnumSet<Stereotype> m_stereotypes = EnumSet.noneOf(Stereotype.class);
    private final List<Artifact> m_children = new ArrayList<>();
    private final List<Interface> m_interfaces = new ArrayList<>();
    private final List<Connector> m_connectors = new ArrayList<>();

    /**
     * Initialize new artifact element
     * @param fqn the fully quaified name of the artifact
     */
    public Artifact(Artifact parent, String fqn)
    {
        super(fqn);
        m_parent = parent;
    }

    public void addStereoType(Stereotype st)
    {
        assert st != null;

        m_stereotypes.add(st);
    }

    public void addChild(Artifact child)
    {
        assert child != null;
        assert child.getParent() == this;

        m_children.add(child);
    }

    public void addInterface(Interface iface)
    {
        assert iface != null;

        m_interfaces.add(iface);
    }

    public void addConnector(Connector conn)
    {
        assert conn != null;

        m_connectors.add(conn);
    }

    public Artifact getParent()
    {
        return m_parent;
    }

    public Set<Stereotype> getStereotypes()
    {
        return Collections.unmodifiableSet(m_stereotypes);
    }

    public List<Artifact> getChildren()
    {
        return Collections.unmodifiableList(m_children);
    }

    public List<Interface> getInterfaces()
    {
        return Collections.unmodifiableList(m_interfaces);
    }

    public List<Connector> getConnectors()
    {
        return Collections.unmodifiableList(m_connectors);
    }
}
