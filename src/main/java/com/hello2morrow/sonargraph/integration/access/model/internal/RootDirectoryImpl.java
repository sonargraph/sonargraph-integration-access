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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public abstract class RootDirectoryImpl extends NamedElementImpl implements IRootDirectory
{
    private final Set<ISourceFile> sourceFiles = new TreeSet<>((s1, s2) -> s1.getFqName().compareTo(s2.getFqName()));

    private final NamedElementContainerImpl module;
    private final String relativePath;

    public RootDirectoryImpl(final NamedElementContainerImpl module, final String kind, final String presentationKind, final String relativePath,
            final String fqName)
    {
        super(kind, presentationKind, fqName, relativePath, fqName, -1);

        assert module != null : "Parameter 'module' of method 'RootDirectoryImpl' must not be null";
        assert relativePath != null && relativePath.length() > 0 : "Parameter 'relativePath' of method 'RootDirectoryImpl' must not be empty";

        this.module = module;
        this.relativePath = relativePath;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IRootDirectory#getAbsolutePath()
     */
    @Override
    public final String getRelativePath()
    {
        return relativePath;
    }

    public final void addSourceFile(final ISourceFile sourceFile)
    {
        assert sourceFile != null : "Parameter 'sourceFile' of method 'addSourceFile' must not be null";
        assert !sourceFiles.contains(sourceFile) : "sourceFile '" + sourceFile.getFqName() + "' has already been added";
        sourceFiles.add(sourceFile);
        module.addElement(sourceFile);
    }

    @Override
    public final Set<ISourceFile> getSourceFiles()
    {
        return Collections.unmodifiableSet(sourceFiles);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((module == null) ? 0 : module.hashCode());
        result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
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
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final RootDirectoryImpl other = (RootDirectoryImpl) obj;
        if (module == null)
        {
            if (other.module != null)
            {
                return false;
            }
        }
        else if (!module.equals(other.module))
        {
            return false;
        }
        if (relativePath == null)
        {
            if (other.relativePath != null)
            {
                return false;
            }
        }
        else if (!relativePath.equals(other.relativePath))
        {
            return false;
        }
        return true;
    }
}