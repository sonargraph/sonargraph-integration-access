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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.ILanguageBasedContainer;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;

public abstract class LanguageBasedContainerImpl extends NamedElementContainerImpl implements ILanguageBasedContainer
{
    private static final long serialVersionUID = -418718806795385173L;
    private final List<RootDirectoryImpl> rootDirectories = new ArrayList<>(2);
    private final String language;

    public LanguageBasedContainerImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description, final MetaDataAccessImpl metaDataAccessImpl,
            final ElementRegistryImpl elementRegistryImpl, final String language)
    {
        super(kind, presentationKind, name, presentationName, fqName, description, metaDataAccessImpl, elementRegistryImpl);
        assert language != null : "Parameter 'language' of method 'LanguageBasedContainerImpl' must not be null";
        this.language = language;
    }

    @Override
    public final String getLanguage()
    {
        return language;
    }

    public final void addRootDirectory(final RootDirectoryImpl rootDirectory)
    {
        assert rootDirectory != null : "Parameter 'rootDirectory' of method 'addRootDirectory' must not be null";
        assert !rootDirectories.contains(rootDirectory) : "Root directory'" + rootDirectory.getFqName() + "' has already been added";
        rootDirectories.add(rootDirectory);
    }

    @Override
    public final List<IRootDirectory> getRootDirectories()
    {
        return Collections.unmodifiableList(rootDirectories);
    }

}