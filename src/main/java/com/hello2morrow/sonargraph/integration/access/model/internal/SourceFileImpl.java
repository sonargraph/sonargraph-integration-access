package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class SourceFileImpl extends NamedElementImpl implements ISourceFile
{
    private final IRootDirectory m_root;

    public SourceFileImpl(final IRootDirectory rootDirectory, final String kind, final String presentationKind, final String name,
            final String presentationName, final String fqName)
    {
        super(kind, presentationKind, name, presentationName, fqName, -1);
        assert rootDirectory != null : "Parameter 'rootDirectory' of method 'SourceFileImpl' must not be null";
        m_root = rootDirectory;
    }

    @Override
    public String getRelativeRootDirectoryPath()
    {
        return m_root.getRelativePath();
    }

    @Override
    public String getRelativePath()
    {
        //TODO [Dietmar] Strange mix up - why not rel. path? 
        return getPresentationName();
    }
}