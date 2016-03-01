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
    private final Set<ISourceFile> m_sourceFiles = new TreeSet<>((s1, s2) -> s1.getFqName().compareTo(s2.getFqName()));

    private final NamedElementContainerImpl m_module;
    private final String m_relativePath;

    public RootDirectoryImpl(final NamedElementContainerImpl module, final String kind, final String presentationKind, final String relativePath,
            final String fqName)
    {
        super(kind, presentationKind, fqName, relativePath, fqName, -1);

        assert module != null : "Parameter 'module' of method 'RootDirectoryImpl' must not be null";
        assert relativePath != null && relativePath.length() > 0 : "Parameter 'relativePath' of method 'RootDirectoryImpl' must not be empty";

        m_module = module;
        m_relativePath = relativePath;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IRootDirectory#getAbsolutePath()
     */
    @Override
    public final String getRelativePath()
    {
        return m_relativePath;
    }

    public final void addSourceFile(final ISourceFile sourceFile)
    {
        assert sourceFile != null : "Parameter 'sourceFile' of method 'addSourceFile' must not be null";
        assert !m_sourceFiles.contains(sourceFile) : "sourceFile '" + sourceFile.getFqName() + "' has already been added";
        m_sourceFiles.add(sourceFile);
        m_module.addElement(sourceFile);
    }

    @Override
    public final Set<ISourceFile> getSourceFiles()
    {
        return Collections.unmodifiableSet(m_sourceFiles);
    }
}