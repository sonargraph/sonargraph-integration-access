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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public abstract class RootDirectoryImpl extends NamedElementImpl implements IRootDirectory
{
    static final class SourceFileComparator implements Comparator<ISourceFile>
    {
        @Override
        public int compare(final ISourceFile s1, final ISourceFile s2)
        {
            assert s1 != null : "Parameter 's1' of method 'compare' must not be null";
            assert s2 != null : "Parameter 's2' of method 'compare' must not be null";

            if (s1 == s2)
            {
                return 0;
            }

            int compared = s1.getFqName().compareTo(s2.getFqName());
            if (compared == 0)
            {
                if (s1.getOriginal().isPresent())
                {
                    assert !s2.getOriginal().isPresent() : "No original expected for: " + s2;
                    compared = 1;
                }
                assert !s1.getOriginal().isPresent() : "No original expected for: " + s1;
                compared = -1;
            }

            return compared;
        }
    }

    private static final long serialVersionUID = -5510302644511647715L;
    private final Set<ISourceFile> sourceFiles = new TreeSet<>(new SourceFileComparator());
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
        System.out.println(sourceFile);
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
        result = prime * result + module.hashCode();
        result = prime * result + relativePath.hashCode();
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

        final RootDirectoryImpl other = (RootDirectoryImpl) obj;
        return module.equals(other.module) && relativePath.equals(other.relativePath);
    }
}