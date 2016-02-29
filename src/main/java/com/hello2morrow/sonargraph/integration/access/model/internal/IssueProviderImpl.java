package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;

public final class IssueProviderImpl extends ElementImpl implements IIssueProvider
{
    public IssueProviderImpl(final String name, final String presentationName)
    {
        super(name, presentationName);
    }
}
