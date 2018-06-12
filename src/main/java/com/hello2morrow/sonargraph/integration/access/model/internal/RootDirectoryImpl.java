/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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

import com.hello2morrow.sonargraph.integration.access.model.IPhysicalRecursiveElement;
import com.hello2morrow.sonargraph.integration.access.model.IProgrammingElement;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class RootDirectoryImpl extends NamedElementImpl implements IProgrammingElementContainer, IRootDirectory
{
    private static final long serialVersionUID = -5510302644511647715L;
    private final Set<SourceFileImpl> sourceFileImpls = new TreeSet<>(new NamedElementComparator());
    private final Set<PhysicalRecursiveElementImpl> physicalRecursiveElementImpls = new TreeSet<>(new NamedElementComparator());
    private final Set<ProgrammingElementImpl> programmingElementImpls = new TreeSet<>(new NamedElementComparator());

    public RootDirectoryImpl(final String kind, final String presentationKind, final String relativePath, final String fqName)
    {
        super(kind, presentationKind, relativePath, relativePath, fqName);
    }

    @Override
    public String getRelativePath()
    {
        return getPresentationName();
    }

    public void addSourceFile(final SourceFileImpl sourceFileImpl)
    {
        assert sourceFileImpl != null : "Parameter 'sourceFileImpl' of method 'addSourceFile' must not be null";
        assert !sourceFileImpls.contains(sourceFileImpl) : "sourceFileImpl'" + sourceFileImpl.getFqName() + "' has already been added";
        sourceFileImpls.add(sourceFileImpl);
    }

    @Override
    public Set<ISourceFile> getSourceFiles()
    {
        return Collections.unmodifiableSet(sourceFileImpls);
    }

    @Override
    public void addPhysicalRecursiveElement(final PhysicalRecursiveElementImpl physicalRecursiveElementImpl)
    {
        assert physicalRecursiveElementImpl != null : "Parameter 'physicalRecursiveElementImpl' of method 'addPhysicalRecursiveElement' must not be null";
        assert !physicalRecursiveElementImpls.contains(physicalRecursiveElementImpl) : "Already added physical recursive element: "
                + physicalRecursiveElementImpl.getFqName();
        physicalRecursiveElementImpls.add(physicalRecursiveElementImpl);
    }

    @Override
    public Set<IPhysicalRecursiveElement> getPhysicalRecursiveElements()
    {
        return Collections.unmodifiableSet(physicalRecursiveElementImpls);
    }

    @Override
    public void addProgrammingElement(final ProgrammingElementImpl programmingElementImpl)
    {
        assert programmingElementImpl != null : "Parameter 'programmingElementImpl' of method 'addProgrammingElement' must not be null";
        assert !programmingElementImpls.contains(programmingElementImpl) : "Already added programming element: " + programmingElementImpl.getFqName();
        programmingElementImpls.add(programmingElementImpl);
    }

    @Override
    public Set<IProgrammingElement> getProgrammingElements()
    {
        return Collections.unmodifiableSet(programmingElementImpls);
    }
}