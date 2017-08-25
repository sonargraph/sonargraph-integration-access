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

import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IModuleDelta;
import com.hello2morrow.sonargraph.integration.access.model.IWorkspaceDelta;

public final class WorkspaceDeltaImpl implements IWorkspaceDelta
{
    private static final long serialVersionUID = 412462604753552482L;
    private final List<IModuleDelta> changedModules = new ArrayList<>();
    private final List<IModule> removedModules = new ArrayList<>();
    private final List<IModule> addedModules = new ArrayList<>();
    private final String INDENTATION = "    ";

    public void addRemovedModule(final IModule module)
    {
        assert module != null : "Parameter 'module' of method 'addRemovedModule' must not be null";
        removedModules.add(module);
    }

    public void addAddedModule(final IModule module)
    {
        assert module != null : "Parameter 'module' of method 'addAddedModule' must not be null";
        addedModules.add(module);
    }

    @Override
    public List<IModule> getAddedModules()
    {
        return Collections.unmodifiableList(addedModules);
    }

    @Override
    public List<IModule> getRemovedModules()
    {
        return Collections.unmodifiableList(removedModules);
    }

    @Override
    public List<IModuleDelta> getChangedModules()
    {
        return Collections.unmodifiableList(changedModules);
    }

    public void addChangedModule(final IModuleDelta changedModule)
    {
        assert changedModule != null : "Parameter 'changedModule' of method 'addChangedModule' must not be null";
        changedModules.add(changedModule);
    }

    @Override
    public boolean isEmpty()
    {
        return removedModules.isEmpty() && addedModules.isEmpty() && changedModules.isEmpty();
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder("WorkspaceDelta:");
        builder.append("\n").append(INDENTATION).append("Changed Modules (").append(changedModules.size()).append("):");
        for (final IModuleDelta delta : changedModules)
        {
            builder.append("\n--").append(delta.toString());
        }

        builder.append("\n").append(INDENTATION).append("Removed Modules (").append(removedModules.size()).append("):");
        builder.append(printModuleList(removedModules));

        builder.append("\n").append(INDENTATION).append("Added Modules (").append(addedModules.size()).append("):");
        builder.append(printModuleList(addedModules));

        return builder.toString();
    }

    private String printModuleList(final List<IModule> modules)
    {
        final StringBuilder builder = new StringBuilder();
        for (final IModule nextModule : modules)
        {
            builder.append("\n").append(INDENTATION).append(INDENTATION).append(nextModule.getName());
        }
        return builder.toString();
    }
}