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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.diff.IModuleDelta;

public class ModuleDeltaImpl implements IModuleDelta
{
    private static final long serialVersionUID = 1127361649841021407L;
    private final IModule module;
    private final List<IRootDirectory> unchanged;
    private final List<IRootDirectory> added;
    private final List<IRootDirectory> removed;

    public ModuleDeltaImpl(final IModule module, final List<IRootDirectory> unchanged, final List<IRootDirectory> added,
            final List<IRootDirectory> removed)
    {
        assert module != null : "Parameter 'module' of method 'ModuleDeltaImpl' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'ModuleDeltaImpl' must not be null";
        assert added != null : "Parameter 'added' of method 'ModuleDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'ModuleDeltaImpl' must not be null";

        this.module = module;
        this.unchanged = unchanged;
        this.added = added;
        this.removed = removed;
    }

    @Override
    public List<IRootDirectory> getUnchangedRoots()
    {
        return Collections.unmodifiableList(unchanged);
    }

    @Override
    public List<IRootDirectory> getAddedRoots()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<IRootDirectory> getRemovedRoots()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public IModule getModule()
    {
        return module;
    }

    @Override
    public String toString()
    {
        return print(true);
    }

    public String print(final boolean includeUnchanged)
    {
        final StringBuilder builder = new StringBuilder("  Delta of module ");
        builder.append(module.getName());

        builder.append("\n").append(Utility.INDENTATION).append("Removed roots (").append(removed.size()).append("):");
        final Consumer<? super IRootDirectory> action = r -> builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(r);
        removed.forEach(action);

        builder.append("\n").append(Utility.INDENTATION).append("Added roots (").append(added.size()).append("):");
        added.forEach(action);

        if (includeUnchanged)
        {
            builder.append("\n").append(Utility.INDENTATION).append("Unchanged roots (").append(unchanged.size()).append("):");
            unchanged.forEach(action);
        }
        return builder.toString();
    }
}
