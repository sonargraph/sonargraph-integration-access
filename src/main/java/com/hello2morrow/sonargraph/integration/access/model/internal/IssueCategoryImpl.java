package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;

public class IssueCategoryImpl extends ElementImpl implements IIssueCategory
{
    public IssueCategoryImpl(final String name, final String presentationName)
    {
        super(name, presentationName);
    }
}