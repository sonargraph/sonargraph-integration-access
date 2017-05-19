package com.hello2morrow.sonargraph.integration.access.persistence;

class XmlIdGenerator
{
    private static final String xmlElementIdFormat = "_%1x";
    private int m_idCounter;

    public XmlIdGenerator()
    {
        m_idCounter = 0;
    }

    public String getNextId()
    {
        return String.format(xmlElementIdFormat, m_idCounter++);
    }
}
