package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IElementIssue extends IIssue
{
    public String getName();

    public List<INamedElement> getAffectedElements();
}