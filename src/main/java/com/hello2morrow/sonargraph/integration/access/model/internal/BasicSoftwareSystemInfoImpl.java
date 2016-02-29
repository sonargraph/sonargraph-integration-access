package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;

public class BasicSoftwareSystemInfoImpl implements IBasicSoftwareSystemInfo
{
    private final String m_systemPath;
    private final String m_systemId;
    private final String m_version;
    private final long m_timestamp;

    public BasicSoftwareSystemInfoImpl(final String path, final String systemId, final String version, final long timestamp)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'BasicSoftwareSystemInfoImpl' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'ExportMetaDataImpl' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' must be > 0";

        m_systemPath = path;
        m_systemId = systemId;
        m_version = version;
        m_timestamp = timestamp;
    }

    @Override
    public String getPath()
    {
        return m_systemPath;
    }

    @Override
    public String getSystemId()
    {
        return m_systemId;
    }

    @Override
    public long getTimestamp()
    {
        return m_timestamp;
    }

    @Override
    public String getVersion()
    {
        return m_version;
    }
}
