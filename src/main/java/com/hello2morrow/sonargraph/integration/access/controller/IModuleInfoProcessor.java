package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public interface IModuleInfoProcessor extends IInfoProcessor
{
    public boolean isElementContainedInModule(INamedElement element);

    public Map<ISourceFile, List<IIssue>> getIssuesForSourceFiles(Predicate<IIssue> filter);
}