package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IIssue
{
    public IIssueProvider getIssueProvider();

    public IIssueType getIssueType();

    public boolean hasResolution();

    public String getDescription();

    public List<INamedElement> getOrigins();

    public int getLineNumber();
}