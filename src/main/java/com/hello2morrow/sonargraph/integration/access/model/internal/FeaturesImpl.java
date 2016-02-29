package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IFeature;

public class FeaturesImpl extends ElementImpl implements IFeature
{
    private final boolean m_licensed;

    public FeaturesImpl(final String name, final String presentationName, final boolean licensed)
    {
        super(name, presentationName);
        m_licensed = licensed;
    }

    @Override
    public boolean isLicensed()
    {
        return m_licensed;
    }
}
