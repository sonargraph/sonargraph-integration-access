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

public abstract class PhysicalElementImpl extends NamedElementImpl
{
    private static final long serialVersionUID = 3837143486613207544L;
    private final String relativeRootDirectory;

    public PhysicalElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final int line, final String relativeRootDirectory)
    {
        super(kind, presentationKind, name, presentationName, fqName, line);
        assert relativeRootDirectory != null && relativeRootDirectory.length() > 0 : "Parameter 'relativeRootDirectory' of method 'PhysicalElementImpl' must not be empty";
        this.relativeRootDirectory = relativeRootDirectory;
    }

    public final String getRelativeRootDirectory()
    {
        return relativeRootDirectory;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\nrelativeRootDirectory:" + relativeRootDirectory;
    }
}