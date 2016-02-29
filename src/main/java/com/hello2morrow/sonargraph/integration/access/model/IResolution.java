package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IResolution extends IElement
{
    public List<IIssue> getIssues();

    public Priority getPriority();

    public ResolutionType getType();

    public boolean isApplicable();

    public boolean isTask();

    public int getNumberOfAffectedParserDependencies();
}