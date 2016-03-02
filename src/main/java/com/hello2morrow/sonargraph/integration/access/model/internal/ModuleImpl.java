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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class ModuleImpl extends NamedElementContainerImpl implements IModule
{
    private final String language;
    private final List<IRootDirectory> rootDirectories = new ArrayList<>(2);

    public ModuleImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final String description, final String language)
    {
        super(kind, presentationKind, name, presentationName, fqName, description);
        this.language = language;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IModule#getLanguage()
     */
    @Override
    public String getLanguage()
    {
        return language;
    }

    public void addRootDirectory(final RootDirectoryImpl root)
    {
        assert root != null : "Parameter 'root' of method 'addRootDirectory' must not be null";
        assert !rootDirectories.contains(root) : "root '" + root.getName() + "' has already been added";
        rootDirectories.add(root);
        addElement(root);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IModule#getRootDirectories()
     */
    @Override
    public List<IRootDirectory> getRootDirectories()
    {
        return Collections.unmodifiableList(rootDirectories);
    }

    @Override
    protected boolean acceptElementKind(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'acceptElementKind' must not be empty";
        return !elementKind.endsWith("Module");
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
        return rootDirectories.stream().flatMap(r -> r.getSourceFiles().stream())
                .filter((final ISourceFile e) -> namedElement.getFqName().startsWith(e.getFqName())).findFirst();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ModuleImpl other = (ModuleImpl) obj;
        if (language == null)
        {
            if (other.language != null)
            {
                return false;
            }
        }
        else if (!language.equals(other.language))
        {
            return false;
        }
        return true;
    }
}
