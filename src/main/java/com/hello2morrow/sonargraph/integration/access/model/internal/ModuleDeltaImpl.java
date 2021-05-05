/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IModuleDelta;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;

public final class ModuleDeltaImpl implements IModuleDelta
{
    private static final long serialVersionUID = 1127361649841021407L;
    private final IModule module;
    private final List<IRootDirectory> added;
    private final List<IRootDirectory> removed;

    public ModuleDeltaImpl(final IModule module, final List<IRootDirectory> added, final List<IRootDirectory> removed)
    {
        assert module != null : "Parameter 'module' of method 'ModuleDeltaImpl' must not be null";
        assert added != null : "Parameter 'added' of method 'ModuleDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'ModuleDeltaImpl' must not be null";

        this.module = module;
        this.added = added;
        this.removed = removed;
    }

    @Override
    public List<IRootDirectory> getAddedRootDirectories()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<IRootDirectory> getRemovedRootDirectories()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public IModule getModule()
    {
        return module;
    }

    @Override
    public boolean isEmpty()
    {
        return added.isEmpty() && removed.isEmpty();
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(Utility.INDENTATION).append(Utility.INDENTATION).append(module.getName()).append(" [").append(module.getLanguage())
                .append("]");

        builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION).append("Added root directories (")
                .append(added.size()).append(")");
        for (final IRootDirectory next : added)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION)
                    .append(next.getRelativePath());
        }

        builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION).append("Removed root directories (")
                .append(removed.size()).append(")");
        for (final IRootDirectory next : removed)
        {
            builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION)
                    .append(next.getRelativePath());
        }

        return builder.toString();
    }
}