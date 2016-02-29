package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;

public final class AnalyzerImpl extends ElementWithDescriptionImpl implements IAnalyzer
{
    private final boolean m_isLicensed;

    public AnalyzerImpl(final String name, final String presentationName, final String description, final boolean isLicensed)
    {
        super(name, presentationName, description);
        m_isLicensed = isLicensed;
    }

    @Override
    public boolean isLicensed()
    {
        return m_isLicensed;
    }
}
