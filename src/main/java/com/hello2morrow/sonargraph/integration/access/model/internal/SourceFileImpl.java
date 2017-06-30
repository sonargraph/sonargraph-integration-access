/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class SourceFileImpl extends NamedElementImpl implements ISourceFile
{
    private static final long serialVersionUID = -2940999235312739954L;
    private final IRootDirectory rootDirectory;
    private boolean isOriginal;
    private ISourceFile original = null;

    public SourceFileImpl(final IRootDirectory rootDirectory, final String kind, final String presentationKind, final String name,
            final String presentationName, final String fqName)
    {
        super(kind, presentationKind, name, presentationName, fqName, -1);
        assert rootDirectory != null : "Parameter 'rootDirectory' of method 'SourceFileImpl' must not be null";
        this.rootDirectory = rootDirectory;
        setSourceFile(this);
    }

    @Override
    public String getRelativeRootDirectoryPath()
    {
        return rootDirectory.getRelativePath();
    }

    public IRootDirectory getRootDirectory()
    {
        return rootDirectory;
    }

    @Override
    public String getRelativePath()
    {
        return getPresentationName();
    }

    @Override
    public Optional<ISourceFile> getOriginal()
    {
        return Optional.ofNullable(original);
    }

    public void setOriginal(final ISourceFile original)
    {
        this.original = original;
    }

    public void setIsOriginal(final boolean isOriginal)
    {
        this.isOriginal = isOriginal;
    }

    @Override
    public boolean isOriginal()
    {
        return isOriginal;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((original == null) ? 0 : original.hashCode());
        result = prime * result + rootDirectory.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }

        final SourceFileImpl other = (SourceFileImpl) obj;
        if (original == null)
        {
            if (other.original != null)
            {
                return false;
            }
        }
        else if (!original.equals(other.original))
        {
            return false;
        }

        return rootDirectory.equals(other.rootDirectory);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("In root directory: ").append(rootDirectory.getRelativePath());
        if (original != null)
        {
            builder.append("\n");
            builder.append("Has original with fq name: ").append(original.getFqName());
        }
        if (isOriginal)
        {
            builder.append("\n");
            builder.append("Is Original");
        }
        return builder.toString();
    }
}