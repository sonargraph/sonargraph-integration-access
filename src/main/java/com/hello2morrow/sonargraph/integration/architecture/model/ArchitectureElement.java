package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for architecture elements with a name and inclusion and exclusion filters.
 */
public abstract class ArchitectureElement
{
    private final String m_name;
    private final List<Filter> m_inclusionFilters = new ArrayList<>();
    private final List<Filter> m_exclusionFilters = new ArrayList<>();

    protected ArchitectureElement(String fqn)
    {
        assert fqn != null && fqn.length() > 0;

        m_name = fqn;
    }

    /**
     * Get the name of the element
     * @return element name
     */
    public String getName()
    {
        int dotPos = m_name.lastIndexOf('.');

        if (dotPos == -1)
        {
            return m_name;
        }
        return m_name.substring(dotPos+1);
    }

    /**
     * Get the fully qualified name of the element (e.g. Client.Controller)
     * @return fully qualified name of element
     */
    public String getFullName()
    {
        return m_name;
    }

    public void addIncludeFilter(String pattern, boolean isStromg)
    {
        m_inclusionFilters.add(new Filter(pattern, isStromg));
    }

    public void addExcludeFilter(String pattern)
    {
        m_exclusionFilters.add(new Filter(pattern, false));
    }

    public List<Filter> getIncludeFilters()
    {
        return Collections.unmodifiableList(m_inclusionFilters);
    }

    public List<Filter> getExcludeFilters()
    {
        return Collections.unmodifiableList(m_exclusionFilters);
    }
}
