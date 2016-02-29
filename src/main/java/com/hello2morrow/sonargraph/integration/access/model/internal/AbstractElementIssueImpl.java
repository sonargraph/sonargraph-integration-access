package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public abstract class AbstractElementIssueImpl extends IssueImpl implements IElementIssue
{
    public AbstractElementIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final boolean hasResolution, final int line)
    {
        super(name, presentationName, description, issueType, provider, hasResolution, line);
    }

    @Override
    public List<INamedElement> getOrigins()
    {
        return getAffectedElements();
    }
}
