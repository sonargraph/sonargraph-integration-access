/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IPhysicalRecursiveElement;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public abstract class RootDirectoryImpl extends NamedElementImpl implements IRootDirectory
{
    static final class NamedElementComparator implements Comparator<INamedElement>
    {
        @Override
        public int compare(final INamedElement e1, final INamedElement e2)
        {
            assert e1 != null : "Parameter 'e1' of method 'compare' must not be null";
            assert e2 != null : "Parameter 'e2' of method 'compare' must not be null";

            if (e1 == e2)
            {
                return 0;
            }

            int compared = e1.getFqName().compareTo(e2.getFqName());
            if (compared == 0)
            {
                if (e1.getOriginal().isPresent())
                {
                    assert !e2.getOriginal().isPresent() : "No original expected for: " + e2;
                    compared = 1;
                }
                assert !e1.getOriginal().isPresent() : "No original expected for: " + e1;
                compared = -1;
            }

            return compared;
        }
    }

    private static final long serialVersionUID = -5510302644511647715L;
    private final Set<SourceFileImpl> sourceFiles = new TreeSet<>(new NamedElementComparator());
    private final Set<PhysicalRecursiveElementImpl> physicalRecursiveElements = new TreeSet<>(new NamedElementComparator());

    public RootDirectoryImpl(final String kind, final String presentationKind, final String relativePath, final String fqName)
    {
        super(kind, presentationKind, relativePath, relativePath, fqName, -1);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IRootDirectory#getAbsolutePath()
     */
    @Override
    public final String getRelativePath()
    {
        return getPresentationName();
    }

    public final void addSourceFile(final SourceFileImpl sourceFile)
    {
        assert sourceFile != null : "Parameter 'sourceFile' of method 'addSourceFile' must not be null";
        assert !sourceFiles.contains(sourceFile) : "sourceFile '" + sourceFile.getFqName() + "' has already been added";
        sourceFiles.add(sourceFile);
    }

    @Override
    public final Set<ISourceFile> getSourceFiles()
    {
        return Collections.unmodifiableSet(sourceFiles);
    }

    public final void addPhysicalRecursiveElement(final PhysicalRecursiveElementImpl physicalRecursiveElement)
    {
        assert physicalRecursiveElement != null : "Parameter 'physicalRecursiveElement' of method 'addPhysicalRecursiveElement' must not be null";
        assert !physicalRecursiveElements.contains(physicalRecursiveElement) : "Already added physical recursive element: "
                + physicalRecursiveElement.getFqName();
        physicalRecursiveElements.add(physicalRecursiveElement);
    }

    @Override
    public final Set<IPhysicalRecursiveElement> getPhysicalRecursiveElements()
    {
        return Collections.unmodifiableSet(physicalRecursiveElements);
    }
}