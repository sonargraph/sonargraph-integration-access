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

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class SourceFileImpl extends PhysicalElementImpl implements ISourceFile
{
    private static final long serialVersionUID = -2940999235312739954L;
    private final String relativeRootDirectory;

    public SourceFileImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final boolean isLocationOnly, final String relativeRootDirectory)
    {
        super(kind, presentationKind, name, presentationName, fqName, isLocationOnly);
        assert relativeRootDirectory != null && relativeRootDirectory.length() > 0 : "Parameter 'relativeRootDirectory' of method 'SourceFileImpl' must not be empty";
        this.relativeRootDirectory = relativeRootDirectory;
    }

    @Override
    public final String getRelativeRootDirectory()
    {
        return relativeRootDirectory;
    }

    @Override
    public String getRelativePath()
    {
        return getPresentationName();
    }

    @Override
    public Optional<ISourceFile> getOriginalLocation()
    {
        final Optional<? extends INamedElement> optOriginal = super.getOriginalLocation();
        if (optOriginal.isPresent())
        {
            final INamedElement original = optOriginal.get();
            assert original != null && original instanceof ISourceFile : "Unexpected class in method 'getOriginal': " + original;
            return Optional.ofNullable((ISourceFile) original);
        }
        return Optional.empty();
    }

    @Override
    public void setOriginalLocation(final PhysicalElementImpl physicalElementImpl)
    {
        assert physicalElementImpl != null && physicalElementImpl instanceof SourceFileImpl : "Unexpected class in method 'setOriginalLOcation': "
                + physicalElementImpl;
        super.setOriginalLocation(physicalElementImpl);
    }

    @Override
    public String toString()
    {
        return super.toString() + "\nrelativeRootDirectory:" + relativeRootDirectory;
    }
}