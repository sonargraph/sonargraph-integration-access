package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Optional;

public interface ISourceFileLookup
{
    public Optional<? extends ISourceFile> getSourceFile(final INamedElement namedElement);
}