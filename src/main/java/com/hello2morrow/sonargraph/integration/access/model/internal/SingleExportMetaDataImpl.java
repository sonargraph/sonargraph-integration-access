package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.ISingleExportMetaData;

public final class SingleExportMetaDataImpl extends AbstractExportMetaDataImpl implements ISingleExportMetaData
{
    private final IBasicSoftwareSystemInfo m_systemInfo;

    /**
     * @param systemPath
     * @param systemId
     * @param version
     * @param timestamp
     */
    public SingleExportMetaDataImpl(final IBasicSoftwareSystemInfo systemInfo, final String resourceIdentifier)
    {
        super(resourceIdentifier);
        assert systemInfo != null : "Parameter 'systemInfo' of method 'SingleExportMetaDataImpl' must not be null";
        m_systemInfo = systemInfo;
    }

    @Override
    public IBasicSoftwareSystemInfo getSystemInfo()
    {
        return m_systemInfo;
    }
}
