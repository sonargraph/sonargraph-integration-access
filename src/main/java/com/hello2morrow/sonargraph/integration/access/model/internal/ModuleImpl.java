/*
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFileLookup;

public final class ModuleImpl extends LanguageBasedContainerImpl implements IModule
{
    private static final long serialVersionUID = -4617725409511641491L;
    private final ISourceFileLookup sourceFileLookup;
    private final String moduleId;

    public ModuleImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final String description, final String moduleId, final MetaDataAccessImpl metaDataAccessImpl,
            final NamedElementRegistry elementRegistryImpl, final String language, final ISourceFileLookup sourceFileLookup)
    {
        super(kind, presentationKind, name, presentationName, fqName, description, metaDataAccessImpl, elementRegistryImpl, language);
        assert sourceFileLookup != null : "Parameter 'sourceFileLookup' of method 'ModuleImpl' must not be null";
        this.sourceFileLookup = sourceFileLookup;
        this.moduleId = moduleId;
    }

    @Override
    public String getModuleId()
    {
        return moduleId;
    }

    @Override
    public Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> metricLevels = new LinkedHashMap<>();
        metricLevels.putAll(getAllMetricLevels());
        metricLevels.remove(IMetricLevel.SYSTEM);
        return Collections.unmodifiableMap(metricLevels);
    }

    @Override
    public Optional<ISourceFile> getSourceForElement(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'getSourceForElement' must not be null";
        final Optional<? extends ISourceFile> sourceFileOpt = sourceFileLookup.getSourceFile(namedElement);
        if (sourceFileOpt.isPresent())
        {
            final ISourceFile sourceFile = sourceFileOpt.get();
            final Optional<ISourceFile> originalSourceFileOpt = sourceFile.getOriginalLocation();
            if (originalSourceFileOpt.isPresent())
            {
                final ISourceFile originalSourceFile = originalSourceFileOpt.get();
                return getRootDirectories().stream().flatMap(r -> r.getSourceFiles().stream()).filter(s -> s == originalSourceFile).findFirst();
            }

            return getRootDirectories().stream().flatMap(r -> r.getSourceFiles().stream()).filter(s -> s == sourceFile).findFirst();
        }
        return getRootDirectories().stream().flatMap(r -> r.getSourceFiles().stream())
                .filter((final ISourceFile e) -> namedElement.getFqName().startsWith(e.getFqName())).findFirst();
    }

    @Override
    public String getImageResourceName()
    {
        return "Module";
    }
}