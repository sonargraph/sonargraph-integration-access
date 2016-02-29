package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;
import java.util.Optional;

public interface IModule extends IElementContainer
{
    public String getLanguage();

    public List<IRootDirectory> getRootDirectories();

    public Optional<ISourceFile> getSourceForElement(INamedElement namedElement);
}