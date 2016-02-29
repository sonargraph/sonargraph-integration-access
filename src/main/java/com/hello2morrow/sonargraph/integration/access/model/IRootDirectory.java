package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Set;

public interface IRootDirectory extends IFilePathElement
{
    public Set<ISourceFile> getSourceFiles();
}