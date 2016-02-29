package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ISourceRootDirectory;

public final class SourceRootDirectoryImpl extends RootDirectoryImpl implements ISourceRootDirectory
{
    public SourceRootDirectoryImpl(final NamedElementContainerImpl module, final String kind, final String presentationKind,
            final String absolutePath, final String fqName)
    {
        super(module, kind, presentationKind, absolutePath, fqName);
    }
}