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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.diff.IModuleDelta;

public class ModuleDeltaImpl implements IModuleDelta
{
    private static final String INDENTATION = "   ";
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
        final StringBuilder builder = new StringBuilder("  Delta of module ");
        builder.append(module.getName());

        builder.append("\n").append(INDENTATION).append("Removed roots [").append(removed.size()).append("]:");
        for (final IRootDirectory next : removed)
        {
            builder.append("\n").append(INDENTATION).append(INDENTATION).append(next);
        }

        builder.append("\n").append(INDENTATION).append("Added roots [").append(added.size()).append("]:");
        for (final IRootDirectory next : added)
        {
            builder.append("\n").append(INDENTATION).append(INDENTATION).append(next);
        }

        builder.append("\n").append(INDENTATION).append("Unchanged roots [").append(unchanged.size()).append("]:");
        for (final IRootDirectory next : unchanged)
        {
            builder.append("\n").append(INDENTATION).append(INDENTATION).append(next);
        }

        return builder.toString();
    }
}
