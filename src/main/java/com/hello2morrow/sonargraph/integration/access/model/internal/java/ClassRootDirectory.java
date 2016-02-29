package com.hello2morrow.sonargraph.integration.access.model.internal.java;

import com.hello2morrow.sonargraph.integration.access.model.internal.NamedElementContainerImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.RootDirectoryImpl;

public final class ClassRootDirectory extends RootDirectoryImpl
{
    public ClassRootDirectory(final NamedElementContainerImpl module, final String kind, final String presentationKind, final String absolutePath,
            final String fqName)
    {
        super(module, kind, presentationKind, absolutePath, fqName);
    }
}