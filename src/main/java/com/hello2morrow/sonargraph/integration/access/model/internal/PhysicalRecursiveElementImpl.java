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

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IPhysicalRecursiveElement;

public final class PhysicalRecursiveElementImpl extends PhysicalElementImpl implements IPhysicalRecursiveElement
{
    private static final long serialVersionUID = 84185773903958748L;
    private String relativeRootDirectory;
    private String relativeDirectory;

    public PhysicalRecursiveElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final boolean isLocationOnly)
    {
        super(kind, presentationKind, name, presentationName, fqName, isLocationOnly);
    }

    public void setRelativeRootDirectory(final String relativeRootDirectory)
    {
        assert relativeRootDirectory != null && relativeRootDirectory.length() > 0 : "Parameter 'relativeRootDirectory' of method 'setRelativeRootDirectory' must not be empty";
        this.relativeRootDirectory = relativeRootDirectory;
    }

    @Override
    public Optional<String> getRelativeRootDirectory()
    {
        return Optional.ofNullable(relativeRootDirectory);
    }

    public void setRelativeDirectory(final String relativeDirectory)
    {
        assert relativeDirectory != null && relativeDirectory.length() > 0 : "Parameter 'relativeDirectory' of method 'setRelativeDirectory' must not be empty";
        this.relativeDirectory = relativeDirectory;
    }

    @Override
    public Optional<String> getRelativeDirectory()
    {
        return Optional.ofNullable(relativeDirectory);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        if (relativeRootDirectory != null)
        {
            builder.append("\nrelativeRootDirectory:").append(relativeRootDirectory);
        }
        if (relativeDirectory != null)
        {
            builder.append("\nrelativeDirectory:").append(relativeDirectory);
        }

        return builder.toString();
    }
}