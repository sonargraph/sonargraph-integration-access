package com.hello2morrow.sonargraph.integration.architecture.model;

public class Filter
{
    private final String m_pattern;
    private final boolean m_isStrong;

    Filter(String pattern, boolean isStrong)
    {
        m_pattern = pattern;
        m_isStrong = isStrong;
    }

    public String getPattern()
    {
        return m_pattern;
    }

    public boolean isStrong()
    {
        return m_isStrong;
    }
}
